package com.ruoyi.operate.utils;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.operate.domain.*;
import com.ruoyi.operate.dto.OperateBillDTO;
import com.ruoyi.operate.dto.OperateMerchantDTO;
import com.ruoyi.operate.service.*;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ExecBot extends TelegramLongPollingBot {
    //飞机机器人的token和username
    private String username ="NiuXiaoNiu_Bot";
    private String token ="7830501103:AAGeyuutmziwseezjaDzKexk1RfPa7WhGlA";

    //正式测试财付通
//    private String username ="cftzs_bot";
//    private String token ="7800520436:AAFomD8HxOteCtms1PXqa1ZNehCa3HHDRdI";

//    private String username ="NiuXiaoNiu2_Bot";
//    private String token ="7832272066:AAEECgMOsHynUKuzWhc-3G-Krj2YHvIfets";


    private static Logger log = LoggerFactory.getLogger(ExecBot.class);

    @Autowired
    private IOperateMerchantService operateMerchantService;
    @Autowired
    private IOperateChannelLabelService operateChannelLabelService;
    @Autowired
    private IOperatePayProductService operatePayProductService;
    @Autowired
    private IOperatePayChannelService operatePayChannelService;
    @Autowired
    private IOperateMerchantProdectService operateMerchantProdectService;
    @Autowired
    private IOperateOrderService operateOrderService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private ISysDictDataService sysDictDataService;
    @Autowired
    private RedisCache redisCache;

    public ExecBot() {
        this( new DefaultBotOptions());
    }

    public ExecBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public String getBotUsername() {
        return this.username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // 检查消息是否来自群组
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            String receivedText = message.getText();
            //消息发送
            SendMessage response = new SendMessage();

            //商户绑定（绑定只有本系统的用户才能绑定）
            if (StringUtils.isNotEmpty(receivedText) && receivedText.contains("/商户绑定")) {

                List<SysUser> list = sysUserMapper.selectUserList2(new SysUser());
                List<SysUser> collect = list.stream().filter(item -> item.getStatus().equals("0") && item.getTgmId() != null).collect(Collectors.toList());
                // 获取发送这条消息的用户信息
                User user = message.getFrom();
                long userId = user.getId();
                if (collect.stream().anyMatch(item -> userId == item.getTgmId())) {

                    //发送
                    response.setChatId(chatId);
                    String[] splits = receivedText.split(" ");
                    if (splits.length == 2) {
                        //对应商户绑定飞机群id
                        OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(Long.valueOf(splits[1]));
                        if (operateMerchant != null) {
                            operateMerchant.setTgmGroup(Long.valueOf(chatId));
                            operateMerchantService.updateOperateMerchant(operateMerchant);
                            response.setText("商户绑定完成!");
                        } else {
                            response.setText("找不到指定商户!");
                        }

                    } else {
//                        response.setText("格式错误!");
                    }
                }


                //通道绑定（绑定只有本系统的用户才能绑定）
            } else if (StringUtils.isNotEmpty(receivedText) && receivedText.contains("/通道绑定")) {

                List<SysUser> list = sysUserMapper.selectUserList2(new SysUser());
                List<SysUser> collect = list.stream().filter(item -> item.getStatus().equals("0") && item.getTgmId() != null).collect(Collectors.toList());
                // 获取发送这条消息的用户信息
                User user = message.getFrom();
                long userId = user.getId();
                if (collect.stream().anyMatch(item -> userId == item.getTgmId())) {

                    String[] splits = receivedText.split(" ");
                    response.setChatId(chatId);
                    if (splits.length == 2) {
                        //对应通道绑定飞机群id
                        OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelByCode(splits[1]);
                        if (operateChannelLabel != null) {
                            operateChannelLabel.setTgmGroup(Long.valueOf(chatId));
                            operateChannelLabelService.updateOperateChannelLabel(operateChannelLabel);
                            response.setText("通道绑定完成!");
                        } else {
                            response.setText("找不到指定通道!");
                        }

                    } else {
//                        response.setText("格式错误!");
                    }


                }


                //机器人和用户的群 查询用户本身的id
            } else if (StringUtils.isNotEmpty(receivedText) && receivedText.equals("/查询")) {

                // 获取发送这条消息的用户信息
                User user = message.getFrom();
                long userId = user.getId();

                response.setChatId(chatId);
                String result = "用户id: " + userId + "\n" +
                        "群组id: " + chatId;
                response.setText(result);


                //今日对账
            } else if (StringUtils.isNotEmpty(receivedText) && receivedText.equals("/今日对账")) {

                // 获取默认时区的当前时间
                LocalDateTime now = LocalDateTime.now();
                // 设置为北京时区
                ZoneId beijingZoneId = ZoneId.of("Asia/Shanghai");
                // 获取北京当前时间精确到秒
                ZonedDateTime beijingTime = now.atZone(beijingZoneId);
                // 格式化时间为字符串
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                //今天
                String beijingTimeTodayString = beijingTime.format(formatter);

                //查找对应的商户
                List<OperateMerchant> operateMerchants = operateMerchantService.queryMchByGroupId(Long.valueOf(chatId));

                if (operateMerchants.size() <= 0) {
                    //上游渠道
                    List<OperateChannelLabel> operateChannelLabels = operateChannelLabelService.queryChaLabByGroupId(Long.valueOf(chatId));
                    for (OperateChannelLabel operateChannelLabel : operateChannelLabels) {
                        OperateBillDTO operateBillDTO = new OperateBillDTO();
                        operateBillDTO.setChaLabId(operateChannelLabel.getId());
                        operateBillDTO.setCheckDate(beijingTimeTodayString);
                        List<OperateBillDTO> dataList = operateOrderService.queryChaLabBill(operateBillDTO);
                        //发送的消息
                        String result = "通道对账日期：" + beijingTimeTodayString + "\n" +
                                "通道名称：" + operateChannelLabel.getChaLabName() + "\n" +
                                "交易量：" + dataList.get(0).getBillTotalAmount() + "\n" +
                                "入账：" + dataList.get(0).getIncomeAmount() + "\n" +
                                "下发：" + dataList.get(0).getExpendAmount() + "\n" +
                                "差额：" + dataList.get(0).getBalance() + "\n";

                        // 发送
                        sendMessageToGroup(chatId, result);
                    }

                } else {
                    //商户
                    for (OperateMerchant operateMerchant : operateMerchants) {
                        OperateBillDTO operateBillDTO = new OperateBillDTO();
                        operateBillDTO.setMchId(operateMerchant.getId());
                        operateBillDTO.setCheckDate(beijingTimeTodayString);
                        List<OperateBillDTO> dataList = operateOrderService.queryMchBill(operateBillDTO);
                        //发送的消息
                        String result = "商户对账日期：" + beijingTimeTodayString + "\n" +
                                "商户名称：" + operateMerchant.getMchName() + "\n" +
                                "交易量：" + dataList.get(0).getBillTotalAmount() + "\n" +
                                "入账：" + dataList.get(0).getIncomeAmount() + "\n" +
                                "下发：" + dataList.get(0).getExpendAmount() + "\n" +
                                "差额：" + dataList.get(0).getBalance() + "\n";

                        // 发送
                        sendMessageToGroup(chatId, result);
                    }


                }


                //昨日对账
            } else if (StringUtils.isNotEmpty(receivedText) && receivedText.equals("/昨日对账")) {

                // 获取默认时区的当前时间
                LocalDateTime now = LocalDateTime.now();
                // 设置为北京时区
                ZoneId beijingZoneId = ZoneId.of("Asia/Shanghai");
                // 获取北京当前时间精确到秒
                ZonedDateTime beijingTime = now.atZone(beijingZoneId);
                // 格式化时间为字符串
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                //今天
                String beijingTimeTodayString = beijingTime.format(formatter);
                //昨天
                String beijingTimeyesterdayString = beijingTime.minusDays(1).format(formatter);

                //查找对应的商户
                List<OperateMerchant> operateMerchants = operateMerchantService.queryMchByGroupId(Long.valueOf(chatId));

                if (operateMerchants.size() <= 0) {
                    //上游渠道
                    List<OperateChannelLabel> operateChannelLabels = operateChannelLabelService.queryChaLabByGroupId(Long.valueOf(chatId));
                    for (OperateChannelLabel operateChannelLabel : operateChannelLabels) {
                        OperateBillDTO operateBillDTO = new OperateBillDTO();
                        operateBillDTO.setChaLabId(operateChannelLabel.getId());
                        operateBillDTO.setCheckDate(beijingTimeyesterdayString);
                        List<OperateBillDTO> dataList = operateOrderService.queryChaLabBill(operateBillDTO);
                        //发送的消息
                        String result = "通道对账日期：" + beijingTimeyesterdayString + "\n" +
                                "通道名称：" + operateChannelLabel.getChaLabName() + "\n" +
                                "交易量：" + dataList.get(0).getBillTotalAmount() + "\n" +
                                "入账：" + dataList.get(0).getIncomeAmount() + "\n" +
                                "下发：" + dataList.get(0).getExpendAmount() + "\n" +
                                "差额：" + dataList.get(0).getBalance() + "\n";

                        // 发送
                        sendMessageToGroup(chatId, result);
                    }

                } else {
                    //商户
                    for (OperateMerchant operateMerchant : operateMerchants) {
                        OperateBillDTO operateBillDTO = new OperateBillDTO();
                        operateBillDTO.setMchId(operateMerchant.getId());
                        operateBillDTO.setCheckDate(beijingTimeyesterdayString);
                        List<OperateBillDTO> dataList = operateOrderService.queryMchBill(operateBillDTO);
                        //发送的消息
                        String result = "商户对账日期：" + beijingTimeyesterdayString + "\n" +
                                "商户名称：" + operateMerchant.getMchName() + "\n" +
                                "交易量：" + dataList.get(0).getBillTotalAmount() + "\n" +
                                "入账：" + dataList.get(0).getIncomeAmount() + "\n" +
                                "下发：" + dataList.get(0).getExpendAmount() + "\n" +
                                "差额：" + dataList.get(0).getBalance() + "\n";

                        // 发送
                        sendMessageToGroup(chatId, result);
                    }


                }


                //商户公告（用于回复某条消息 然后发送到对应的商户）
            } else if (StringUtils.isNotEmpty(receivedText) && receivedText.contains("/商户公告")) {
                //被回复的消息
                Message repliedToMessage = message.getReplyToMessage();

                // 被回复消息的文本(图片下的说明)
                String originalText = repliedToMessage.getCaption();

                List<OperateMerchantDTO> dataList = operateMerchantService.selectOperateMerchantList(new OperateMerchantDTO());

                if (receivedText.equals("/商户公告")) {

                    //排除掉状态停止的商户
                    List<OperateMerchantDTO> data = dataList.stream().filter(item -> item.getStatus().equals("0")).collect(Collectors.toList());
                    if (data.size() > 0) {
                        for (OperateMerchantDTO dto : data) {

                            if (dto.getTgmGroup() != null) {

                                if (repliedToMessage.hasPhoto()) {
                                    //包含图片
                                    //转发图片和文字说明
                                    List<PhotoSize> photos = repliedToMessage.getPhoto();
                                    // 获取最佳分辨率的图片
                                    PhotoSize largestPhoto = photos.stream()
                                            .max(Comparator.comparing(PhotoSize::getFileSize))
                                            .orElse(null);

                                    if (largestPhoto != null) {
                                        String photoFileId = largestPhoto.getFileId();

                                        // 转发图片和内容
                                        SendPhoto sendPhoto = new SendPhoto();
                                        sendPhoto.setChatId(dto.getTgmGroup().toString());
                                        sendPhoto.setPhoto(new InputFile(photoFileId));
                                        sendPhoto.setCaption(originalText);

                                        try {
                                            execute(sendPhoto);

                                        } catch (TelegramApiException e) {
                                            e.printStackTrace();
                                        }
                                    }


                                } else {

                                    // 发送
                                    sendMessageToGroup(dto.getTgmGroup().toString(), repliedToMessage.getText());

                                }

                            }

                        }
                    } else {

//                        response.setChatId(chatId);
//                        response.setText("没有商户！或者商户没有绑定！");

                    }

                } else {
                    //获取所有商户类型
                    SysDictData sysDictData = new SysDictData();
                    sysDictData.setDictType("mch_type");
                    List<SysDictData> dictList = sysDictDataService.selectDictDataList(sysDictData);

                    //只能一次空格 然后接上对应的商户类型(拿到用户输入的商户类型)
                    String[] splits = receivedText.split(" ");
                    List<String> list = Arrays.asList(splits);

                    for (SysDictData data : dictList) {
                        if (list.stream().anyMatch(item -> data.getDictLabel().equals(item))) {

                            //获取对应商户类型
                            List<OperateMerchantDTO> mchList = dataList.stream().filter(item ->
                                    item.getMchType().equals(data.getDictValue()) && item.getStatus().equals("0")).collect(Collectors.toList());
                            if (mchList.size() > 0) {

                                for (OperateMerchantDTO dto : mchList) {

                                    if (dto.getTgmGroup() != null) {

                                        if (repliedToMessage.hasPhoto()) {
                                            //包含图片
                                            //转发图片和文字说明
                                            List<PhotoSize> photos = repliedToMessage.getPhoto();
                                            // 获取最佳分辨率的图片
                                            PhotoSize largestPhoto = photos.stream()
                                                    .max(Comparator.comparing(PhotoSize::getFileSize))
                                                    .orElse(null);

                                            if (largestPhoto != null) {
                                                String photoFileId = largestPhoto.getFileId();

                                                // 转发图片和内容
                                                SendPhoto sendPhoto = new SendPhoto();
                                                sendPhoto.setChatId(dto.getTgmGroup().toString());
                                                sendPhoto.setPhoto(new InputFile(photoFileId));
                                                sendPhoto.setCaption(originalText);

                                                try {
                                                    execute(sendPhoto);

                                                } catch (TelegramApiException e) {
                                                    e.printStackTrace();
                                                }
                                            }


                                        } else {

                                            // 发送
                                            sendMessageToGroup(dto.getTgmGroup().toString(), repliedToMessage.getText());

                                        }


                                    }
                                }

                            } else {
//
//                                response.setChatId(chatId);
//                                response.setText("指定的商户类型没有用户！或者商户没有绑定！");
                            }

                        }
                    }

                }


                //商户查询(即使这个群有多个商户，查通道的时候发送一次就好)
            } else if (StringUtils.isNotEmpty(receivedText) && receivedText.contains("/查询通道")) {
                response.setChatId(chatId);
                String[] splits = receivedText.split(" ");
                String productId = splits[1];
                String msg = "";
                if (splits.length == 2) {
                    List<OperateMerchant> operateMerchants = operateMerchantService.queryMchByGroupId(Long.valueOf(chatId));
                    if (operateMerchants.size() <= 0) {
                        response.setText("商户还未绑定 无法查询!");
                    } else {
                        String status = operateMerchants.get(0).getStatus().equals("0") ? "开启" : "关闭";
                        msg = "商户 : " + operateMerchants.get(0).getMchName() + "  (" + operateMerchants.get(0).getId() + ")\n" +
                                "***目前已" + status + "***\n";
                        //先找到是否有单独配置
                        Map<String, Object> params = new HashMap<>();
                        params.put("productId", productId);
                        params.put("mchId", operateMerchants.get(0).getId());
                        List<OperateMerchantProdect> operateMerchantProdects = operateMerchantProdectService.queryMchProList(params);
                        if (operateMerchantProdects.size() > 0) {

                            OperatePayProduct operatePayProduct = operatePayProductService.selectOperatePayProductById(Long.valueOf(productId));

                            if (operateMerchantProdects.get(0).getPriority().equals("1")) {
                                msg = msg + productId + "  " + operatePayProduct.getProName() + "  " + operateMerchantProdects.get(0).getRate() + "%";
                                response.setText(msg);
                            } else {
                                if (operatePayProduct == null) {
                                    response.setText("找不到该通道!");
                                } else {
                                    msg = msg + productId + "  " + operatePayProduct.getProName() + "  " + operatePayProduct.getMchRate() + "%";
                                    response.setText(msg);
                                }
                            }
                        } else {
//                            response.setText("商户和通道还未配置!");
                        }

                    }
                } else {
//                    response.setText("格式错误!");
                }


                //商户查单 转发 ...
            } else if (message.getForwardFrom() != null || message.getForwardFromChat() != null) {

                // 判断消息发送者是否是机器人
                boolean isBot = message.getFrom().getIsBot();

                if (!isBot) {
                    //图片的说明文字
                    String caption = message.getCaption();
                    if (StringUtils.isEmpty(caption) || caption.equals("")) {
                        // 设置引用的消息 ID
//                        response.setReplyToMessageId(message.getMessageId());
//                        response.setChatId(chatId);
//                        response.setText("没有识别到订单号!");
                    } else {
                        if (message.hasPhoto()) {

                            Map<String, Object> params = new HashMap<>();
                            params.put("mchOrderNo", caption);
                            OperateOrder operateOrder = operateOrderService.queryOperateOrderByOrderNo(params);
                            if (operateOrder != null) {

                                //redis存飞机消息id，key为单号(存一条)
//                                redisCache.setCacheObject(caption,message.getMessageId());
                                redisCache.setCacheObject(caption, message.getMessageId(), 1440, TimeUnit.MINUTES);

                                SendMessage response2 = new SendMessage();
                                // 设置引用的消息 ID
                                response2.setReplyToMessageId(message.getMessageId());
                                response2.setChatId(chatId);
                                response2.setText("我们收到了您订单号  [ " + caption + " ]  的支付凭证,火速查单中...");
                                try {
                                    execute(response2);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }


                                // 设置引用的消息 ID
                                response.setReplyToMessageId(message.getMessageId());
                                response.setChatId(chatId);

                                if (operateOrder.getPaymentStatus().equals("2")) {

                                    response.setText(operateOrder.getMchOrderNo() + "  订单支付成功");

                                } else if (operateOrder.getPaymentStatus().equals("3")) {

                                    response.setText(operateOrder.getMchOrderNo() + "  订单支付失败");

                                } else if (operateOrder.getPaymentStatus().equals("4")) {

                                    response.setText(operateOrder.getMchOrderNo() + "  订单处理完成");

                                } else {
                                    //一般是支付中的状态
                                    //找到订单对应的商户
                                    OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operateOrder.getChaLabId());

                                    if (operateChannelLabel.getTgmGroup() != null) {
                                        //转发图片和文字说明
                                        List<PhotoSize> photos = message.getPhoto();
                                        // 获取最佳分辨率的图片
                                        PhotoSize largestPhoto = photos.stream()
                                                .max(Comparator.comparing(PhotoSize::getFileSize))
                                                .orElse(null);

                                        if (largestPhoto != null) {
                                            String photoFileId = largestPhoto.getFileId();

                                            // 转发图片
                                            SendPhoto sendPhoto = new SendPhoto();
                                            sendPhoto.setChatId(operateChannelLabel.getTgmGroup());
                                            sendPhoto.setPhoto(new InputFile(photoFileId));
                                            sendPhoto.setCaption(operateOrder.getPayOrderNo());

                                            try {
                                                Message execute = execute(sendPhoto);

                                                //redis存飞机消息id，key为单号(存一条)
//                                              redisCache.setCacheObject(operateOrder.getPayOrderNo(),execute.getMessageId());
                                                redisCache.setCacheObject(operateOrder.getPayOrderNo(), message.getMessageId(), 1440, TimeUnit.MINUTES);

                                            } catch (TelegramApiException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        //异步回调通知商户
                                        Callable<Boolean> task = () -> {
                                            Map<String, Object> checkParams = new HashMap<>();
                                            checkParams.put("mchOrderNo", caption);
                                            OperateOrder checkOperateOrder = operateOrderService.queryOperateOrderByOrderNo(checkParams);
                                            //数据返回的结果
                                            if (!checkOperateOrder.getPaymentStatus().equals("1")) {

                                                SendMessage response3 = new SendMessage();
                                                response3.setChatId(chatId);
                                                // 设置引用的消息 ID
                                                response3.setReplyToMessageId(message.getMessageId());

                                                if (checkOperateOrder.getPaymentStatus().equals("2")) {
                                                    response3.setText(checkOperateOrder.getMchOrderNo() + "  订单已回调,支付成功");

                                                } else if (checkOperateOrder.getPaymentStatus().equals("3")) {
                                                    response3.setText(checkOperateOrder.getMchOrderNo() + "  订单已回调,订单支付失败");

                                                } else if (checkOperateOrder.getPaymentStatus().equals("4")) {
                                                    response3.setText(checkOperateOrder.getMchOrderNo() + "  订单已回调,订单处理完成");
                                                }

                                                try {
                                                    execute(response3);
                                                } catch (TelegramApiException e) {
                                                    e.printStackTrace();
                                                }

                                                // 返回true表示需要再次执行，false表示不再执行
                                                return false;
                                            } else {
                                                return true;
                                            }

                                        };
                                        // 每60秒执行一次
                                        startAsyncTaskWithResult(task, 60, 60);


                                    } else {
                                        //上游通道没有绑定
                                        // 设置引用的消息 ID
                                        response.setText("通道没有绑定!");
                                    }
                                }

                            } else {
//                                response.setText("订单号出错,识别异常!");
                            }
                        } else {

//                            response.setText("缺少图片!");
                        }
                    }
                }


                //商户群(不是回复的状态下) 如果是真实用户 发送图片和带图片说明的 就是要检验是否查单(这种情况下也要查单 转发到上游)
            } else if (!message.isReply() && message.hasPhoto() && !message.getCaption().isEmpty() && message.getCaption() != null) {

                // 判断消息发送者是否是机器人
                boolean isBot = message.getFrom().getIsBot();
                String caption = message.getCaption();

                if (!isBot) {

                    Map<String, Object> params = new HashMap<>();
                    params.put("mchOrderNo", caption);
                    OperateOrder operateOrder = operateOrderService.queryOperateOrderByOrderNo(params);
                    if (operateOrder != null) {

                        //redis存飞机消息id，key为单号(存一条)
                        //redisCache.setCacheObject(caption,message.getMessageId());
                        redisCache.setCacheObject(caption, message.getMessageId(), 1440, TimeUnit.MINUTES);

                        SendMessage response2 = new SendMessage();
                        // 设置引用的消息 ID
                        response2.setReplyToMessageId(message.getMessageId());
                        response2.setChatId(chatId);
                        response2.setText("我们收到了您订单号  [ " + caption + " ]  的支付凭证,火速查单中...");
                        try {
                            execute(response2);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }


                        // 设置引用的消息 ID
                        response.setReplyToMessageId(message.getMessageId());
                        response.setChatId(chatId);

                        if (operateOrder.getPaymentStatus().equals("2")) {

                            response.setText(operateOrder.getMchOrderNo() + "  订单支付成功");

                        } else if (operateOrder.getPaymentStatus().equals("3")) {

                            response.setText(operateOrder.getMchOrderNo() + "  订单支付失败");

                        } else if (operateOrder.getPaymentStatus().equals("4")) {

                            response.setText(operateOrder.getMchOrderNo() + "  订单处理完成");

                        } else {
                            //一般是支付中的状态
                            //找到订单对应的商户
                            OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operateOrder.getChaLabId());

                            if (operateChannelLabel.getTgmGroup() != null) {
                                //转发图片和文字说明
                                List<PhotoSize> photos = message.getPhoto();
                                // 获取最佳分辨率的图片
                                PhotoSize largestPhoto = photos.stream()
                                        .max(Comparator.comparing(PhotoSize::getFileSize))
                                        .orElse(null);

                                if (largestPhoto != null) {
                                    String photoFileId = largestPhoto.getFileId();

                                    // 转发图片
                                    SendPhoto sendPhoto = new SendPhoto();
                                    sendPhoto.setChatId(operateChannelLabel.getTgmGroup());
                                    sendPhoto.setPhoto(new InputFile(photoFileId));
                                    sendPhoto.setCaption(operateOrder.getPayOrderNo());

                                    try {
                                        Message execute = execute(sendPhoto);

                                        //redis存飞机消息id，key为单号
//                                        redisCache.setCacheObject(operateOrder.getPayOrderNo(),execute.getMessageId());
                                        redisCache.setCacheObject(operateOrder.getPayOrderNo(), execute.getMessageId(), 1440, TimeUnit.MINUTES);
                                    } catch (TelegramApiException e) {
                                        e.printStackTrace();
                                    }
                                }

                                //异步回调通知商户
                                Callable<Boolean> task = () -> {
                                    Map<String, Object> checkParams = new HashMap<>();
                                    checkParams.put("mchOrderNo", caption);
                                    OperateOrder checkOperateOrder = operateOrderService.queryOperateOrderByOrderNo(checkParams);
                                    //数据返回的结果
                                    if (!checkOperateOrder.getPaymentStatus().equals("1")) {

                                        SendMessage response3 = new SendMessage();
                                        response3.setChatId(chatId);
                                        // 设置引用的消息 ID
                                        response3.setReplyToMessageId(message.getMessageId());

                                        if (checkOperateOrder.getPaymentStatus().equals("2")) {
                                            response3.setText(checkOperateOrder.getMchOrderNo() + "  订单已回调,支付成功");

                                        } else if (checkOperateOrder.getPaymentStatus().equals("3")) {
                                            response3.setText(checkOperateOrder.getMchOrderNo() + "  订单已回调,订单支付失败");

                                        } else if (checkOperateOrder.getPaymentStatus().equals("4")) {
                                            response3.setText(checkOperateOrder.getMchOrderNo() + "  订单已回调,订单处理完成");
                                        }

                                        try {
                                            execute(response3);
                                        } catch (TelegramApiException e) {
                                            e.printStackTrace();
                                        }

                                        // 返回true表示需要再次执行，false表示不再执行
                                        return false;
                                    } else {
                                        return true;
                                    }

                                };
                                // 每60秒执行一次
                                startAsyncTaskWithResult(task, 60, 60);


                            } else {
                                //上游通道没有绑定
                                // 设置引用的消息 ID
                                response.setText("通道没有绑定!");
                            }
                        }

                    }
                }


                //消息回复...(商户、上游都可以回复 关键是订单号)
            } else if (message.isReply()) {

                // 获取被回复的消息
                Message repliedToMessage = message.getReplyToMessage();
                // 被回复消息的文本(订单号)
                String originalText = repliedToMessage.getCaption();
                // 判断消息发送者是否是机器人
                boolean isBot = repliedToMessage.getFrom().getIsBot();

                //查询订单号对应的商户
                if (StringUtils.isNotEmpty(originalText) && originalText != null) {

                    //回复加急的情况下是属于查单
                    if (StringUtils.isNotEmpty(receivedText) && receivedText.contains("加急")) {

                        Map<String, Object> params = new HashMap<>();
                        params.put("mchOrderNo", originalText);
                        OperateOrder operateOrder = operateOrderService.queryOperateOrderByOrderNo(params);
                        if (operateOrder != null) {

                            SendMessage response2 = new SendMessage();
                            response2.setReplyToMessageId(message.getMessageId());
                            response2.setChatId(chatId);
                            response2.setText("查单加急已送出，请稍等...");

                            try {
                                execute(response2);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }

                            response.setChatId(chatId);
                            response.setReplyToMessageId(message.getMessageId());

                            if (operateOrder.getPaymentStatus().equals("2")) {

                                response.setText(operateOrder.getMchOrderNo() + "  订单支付成功");

                            } else if (operateOrder.getPaymentStatus().equals("3")) {

                                response.setText(operateOrder.getMchOrderNo() + "  订单支付失败");

                            } else if (operateOrder.getPaymentStatus().equals("4")) {

                                response.setText(operateOrder.getMchOrderNo() + "  订单处理完成");

                            } else {
                                //一般是支付中的状态
                                //找到订单对应的上游
                                OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operateOrder.getChaLabId());

                                if (operateChannelLabel.getTgmGroup() != null) {

                                    //如果订单和图片之前发送过了，就直接引用
                                    SendMessage response3 = new SendMessage();
                                    response3.setChatId(operateChannelLabel.getTgmGroup());
                                    //如果订单和图片之前发送过了，就直接引用
                                    if (redisCache.getCacheObject(operateOrder.getPayOrderNo()) != null) {
                                        response3.setReplyToMessageId(redisCache.getCacheObject(operateOrder.getPayOrderNo()));
                                    }

                                    response3.setText(message.getText());

                                    try {
                                        execute(response3);
                                    } catch (TelegramApiException e) {
                                        e.printStackTrace();
                                    }

//                                    redisCache.deleteObject(operateOrder.getPayOrderNo());

                                } else {
                                    //上游通道没有绑定
                                    response.setChatId(chatId);
                                    response.setReplyToMessageId(message.getMessageId());
                                    response.setText("通道没有绑定!");
                                }
                            }

                        } else {
//                            response.setChatId(chatId);
//                            response.setReplyToMessageId(message.getMessageId());
//                            response.setText("订单号出错,识别异常!");
                        }


                    } else {

                        //普通的回复 回复给商户(这条消息是从上游那边过来的)
                        Map<String, Object> params = new HashMap<>();
                        params.put("payOrderNo", originalText);
                        OperateOrder operateOrder = operateOrderService.queryOperateOrderByOrderNo(params);
                        if (operateOrder != null) {
                            //找到订单对应的商户
                            OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(operateOrder.getMchId());
                            if (message.hasPhoto()) {
                                //包含图片
                                if (operateMerchant.getTgmGroup() != null) {
                                    //转发图片和文字说明
                                    List<PhotoSize> photos = message.getPhoto();
                                    // 获取最佳分辨率的图片
                                    PhotoSize largestPhoto = photos.stream()
                                            .max(Comparator.comparing(PhotoSize::getFileSize))
                                            .orElse(null);

                                    if (largestPhoto != null) {
                                        String photoFileId = largestPhoto.getFileId();

                                        // 转发图片和内容
                                        SendPhoto sendPhoto = new SendPhoto();
                                        //根据redis缓存获取到引用的消息id
                                        if (redisCache.getCacheObject(operateOrder.getMchOrderNo()) != null) {
                                            sendPhoto.setReplyToMessageId(redisCache.getCacheObject(operateOrder.getMchOrderNo()));
                                        }
                                        sendPhoto.setChatId(operateMerchant.getTgmGroup());
                                        sendPhoto.setPhoto(new InputFile(photoFileId));
                                        sendPhoto.setCaption(operateOrder.getMchOrderNo() + "   " + message.getCaption());

                                        //redis删除引用(用过之后就要请掉缓存)
//                                        redisCache.deleteObject(operateOrder.getMchOrderNo());

                                        try {
                                            execute(sendPhoto);


                                        } catch (TelegramApiException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }


                            } else {

                                //根据redis缓存获取到引用的消息id
                                if (redisCache.getCacheObject(operateOrder.getMchOrderNo()) != null) {
                                    response.setReplyToMessageId(redisCache.getCacheObject(operateOrder.getMchOrderNo()));
                                }
                                response.setChatId(operateMerchant.getTgmGroup());
                                String msg = operateOrder.getMchOrderNo();
                                if (StringUtils.isNotEmpty(receivedText) && !receivedText.equals("")) {
                                    msg = msg + "  " + receivedText;
                                }
                                response.setText(msg);


                                //redis删除引用(用过之后就要请掉缓存)
//                                redisCache.deleteObject(operateOrder.getMchOrderNo());

                            }


                        } else {

//                          //普通的回复 回复给上游(这条消息是从上游那边过来的)
                            Map<String, Object> params2 = new HashMap<>();
                            params2.put("mchOrderNo", originalText);
                            OperateOrder operateOrder2 = operateOrderService.queryOperateOrderByOrderNo(params2);
                            //找到订单对应的上游
                            OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operateOrder2.getChaLabId());
                            if (message.hasPhoto()) {
                                //包含图片
                                if (operateChannelLabel.getTgmGroup() != null) {
                                    //转发图片和文字说明
                                    List<PhotoSize> photos = message.getPhoto();
                                    // 获取最佳分辨率的图片
                                    PhotoSize largestPhoto = photos.stream()
                                            .max(Comparator.comparing(PhotoSize::getFileSize))
                                            .orElse(null);

                                    if (largestPhoto != null) {
                                        String photoFileId = largestPhoto.getFileId();

                                        // 转发图片和内容
                                        SendPhoto sendPhoto = new SendPhoto();
                                        //根据redis缓存获取到引用的消息id
                                        if (redisCache.getCacheObject(operateOrder2.getPayOrderNo()) != null) {
                                            sendPhoto.setReplyToMessageId(redisCache.getCacheObject(operateOrder2.getPayOrderNo()));
                                        }
                                        sendPhoto.setChatId(operateChannelLabel.getTgmGroup());
                                        sendPhoto.setPhoto(new InputFile(photoFileId));
                                        sendPhoto.setCaption(operateOrder2.getPayOrderNo() + "   " + message.getCaption());

                                        //redis删除引用(用过之后就要请掉缓存)
//                                        redisCache.deleteObject(operateOrder.getMchOrderNo());

                                        try {
                                            execute(sendPhoto);


                                        } catch (TelegramApiException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }


                            } else {

                                //根据redis缓存获取到引用的消息id
                                if (redisCache.getCacheObject(operateOrder2.getPayOrderNo()) != null) {
                                    response.setReplyToMessageId(redisCache.getCacheObject(operateOrder2.getPayOrderNo()));
                                }
                                response.setChatId(operateChannelLabel.getTgmGroup());
                                String msg = operateOrder2.getPayOrderNo();
                                if (StringUtils.isNotEmpty(receivedText) && !receivedText.equals("")) {
                                    msg = msg + "  " + receivedText;
                                }
                                response.setText(msg);


                                //redis删除引用(用过之后就要请掉缓存)
//                                redisCache.deleteObject(operateOrder.getMchOrderNo());

                            }

                        }
                    }

                }


            // 简单的算数功能
            }else if(receivedText.matches("^[\\-+]?\\d+(\\.\\d+)?(\\s*[\\+\\-\\*/^]\\s*\\d+(\\.\\d+)?)+$")){
                String responseText = "";

                try {
                    // 计算表达式
                    String result = evaluateExpression(receivedText);
                    responseText = String.format(
                            "`%s` = `%s`",
                            receivedText, result
                    );

                    response.setParseMode("Markdown");
                    response.setChatId(chatId);
                    response.setText(responseText);

                } catch (Exception e) {

                }

            }

            try {
                execute(response); // 发送消息
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


//        }else{
//
//            //消息是否有文本(和机器的对应聊天)
//            // 设置变量
//            String message_text = update.getMessage().getText();
//            long chat_id = update.getMessage().getChatId();
//            //创建消息对象
//            SendMessage message = new SendMessage();
//            message.setChatId(String.valueOf(chat_id));
//
//            //用户查询自己的飞机id
//            if(StringUtils.isNotEmpty(message_text) && message_text.equals("/查询")){
//                message.setText(String.valueOf(chat_id));
//            }
//
//            try {
//                execute(message); // 发送我们的消息对象给用户
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//
//        }

    }


    // 向指定群聊发送消息的方法(用于通知商户)
    public void sendMessageToGroup(String groupChatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(groupChatId);  // 群聊的 chatId
        sendMessage.setText(message);  // 设置消息内容

        try {
            execute(sendMessage);  // 执行发送
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // 计算表达式
    private String evaluateExpression(String expression) throws Exception {
        // 创建一个 JavaScript 引擎
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        Object result = engine.eval(expression);  // 直接计算表达式

        // 使用 BigDecimal 处理结果，避免精度丢失
        BigDecimal decimalResult = new BigDecimal(result.toString());
        decimalResult = decimalResult.setScale(2, RoundingMode.HALF_UP); // 保留两位小数

        return decimalResult.toString();
    }

    /**
     * 异步请求调用
     */
    public static void startAsyncTaskWithResult(Callable<Boolean> task, long initialDelay, long period){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ExecutorService executor = Executors.newCachedThreadPool();
        // 首次调用
        final Future<Boolean>[] future = new Future[]{executor.submit(task)};

        scheduler.scheduleWithFixedDelay(() -> {
            try {
                Boolean result = future[0].get(); // 获取结果
                if (result != null && result) {
                    // 如果需要再次调用，重新提交任务
                    future[0] = executor.submit(task);
                    log.error("===========查单线程启动,查询订单通知商户!===================");
                } else {
                    // 不需要再次调用，终止调度
                    log.error("===========查单线程关闭!===================");
                    scheduler.shutdown();
                }
            } catch (InterruptedException | ExecutionException e) {
                // 处理异常
                Thread.currentThread().interrupt();
                scheduler.shutdown();
            }
        }, initialDelay, period, TimeUnit.SECONDS);

    }

}
