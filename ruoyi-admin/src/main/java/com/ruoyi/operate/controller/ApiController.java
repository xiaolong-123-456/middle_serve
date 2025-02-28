package com.ruoyi.operate.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SignConfig;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.operate.domain.*;
import com.ruoyi.operate.dto.*;
import com.ruoyi.operate.mapper.OperateAgentProductMapper;
import com.ruoyi.operate.mapper.OperateOrderMapper;
import com.ruoyi.operate.service.*;
import com.ruoyi.operate.utils.ExecBot;
import com.ruoyi.system.domain.SysRobat;
import com.ruoyi.system.service.ISysRobatService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * 对外开放的接口
 * @author master123
 */
@RestController
@RequestMapping("/api/main")
public class ApiController extends BaseController
{

    private static Logger log = LoggerFactory.getLogger(SignConfig.class);

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 定义允许的字符集
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private IOperateMerchantService operateMerchantService;
    @Autowired
    private IOperateMerchantProdectService operateMerchantProdectService;
    @Autowired
    private IOperateMchProAloneService operateMchProAloneService;
    @Autowired
    private IOperateRiskControlService operateRiskControlService;
    @Autowired
    private IOperateOrderService operateOrderService;
    @Autowired
    private OperateOrderMapper operateOrderMapper;
    @Autowired
    private IOperateChannelLabelService operateChannelLabelService;
    @Autowired
    private IOperatePayProductInfoService operatePayProductInfoService;
    @Autowired
    private IOperatePayProductService operatePayProductService;
    @Autowired
    private OperateAgentProductMapper operateAgentProductMapper;
    @Autowired
    private IOperateAgentService operateAgentService;
    @Autowired
    private IOperatePayChannelService operatePayChannelService;
    @Autowired
    private ExecBot execBot;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysRobatService sysRobatService;
    @Autowired
    private IOperateNotifyService operateNotifyService;
    @Autowired
    private IOperateReplenishmentService operateReplenishmentService;
    @Autowired
    private RedisCache redisCache;

    //回调接口、包括post和get(@requestParam 适用于url拼接和表单提交)
    private static final String NOTIFU_URL = "http://center.cft.today/api/main/pay/notify";

    //回调接口、包括post和get(@requestBody 适用于json格式)
    private static final String NOTIFU_BODY_URL = "http://center.cft.today/api/main/pay/body/notify";

    //回调接口测试
    private static final String NOTIFU_TEST_URL = "http://center.cft.today/api/main/pay/test/notify";

    //回调接口测试
    private static final String NOTIFU_TEST_BODY_URL = "http://center.cft.today/api/main/pay/body/test/notify";

    //测试的支付页面
    private static final String PAY_TEST_URL = "http://p.cft.today/payTest.html";

    //防拉空的支付页面
    private static final String PAY_ABLOWDOWN_URL = "http://p.cft.today/antiBlowdown.html";

    //ip
    private static final String ip = "139.180.144.47";

    /**
     * 下单接口
     */
    @PostMapping("/pay/createOrder")
//    @Log(title = "商户下单")
    public AjaxResult createOrder(@RequestBody CreatOrderDTO creatOrderDTO)
    {
        if (null == creatOrderDTO.getMchId()) {
            return new AjaxResult("FAIL", "下单失败 商户id不能为空！", null,null,null);
        }
        if (null == creatOrderDTO.getProductId()) {
            return new AjaxResult("FAIL", "下单失败 产品id不能为空！", null,null,null);
        }
        if (null == creatOrderDTO.getMchOrderNo() || creatOrderDTO.getMchOrderNo().trim().equals("")) {
            return new AjaxResult("FAIL", "下单失败，商户订单号不能为空！", null,null,null);
        }
        if (null == creatOrderDTO.getAmount()) {
            return new AjaxResult("FAIL", "下单失败 支付金额不能为空！", null,null,null);
        }
        if (null == creatOrderDTO.getNotifyUrl() || creatOrderDTO.getNotifyUrl().trim().equals("")) {
            return new AjaxResult("FAIL", "下单失败 支付结果后台回调URL不能为空！", null,null,null);
        }
        if (null == creatOrderDTO.getSign() || creatOrderDTO.getSign().trim().equals("")) {
            return new AjaxResult("FAIL", "下单失败 sign验签不能为空！", null,null,null);
        }

        long start = System.currentTimeMillis();
        log.error("========商户下单数据信息++++++++++++++++=== ：" + creatOrderDTO);

        //获取key
        OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(creatOrderDTO.getMchId());
        String mchkey = operateMerchant.getMchKey();
        //sign验证
        boolean checkResult;
        try {
            checkResult = SignConfig.checkIsSignValidFromResyponseStringObject(creatOrderDTO, mchkey,"0","0","0","key");
        } catch (Exception e) {
            log.error("===========验签过程异常===================");
            return new AjaxResult("FAIL", "sign验签过程异常！", null,null,null);
        }

        if(checkResult == false){
            log.error("===========商户id="+creatOrderDTO.getMchId()+"商户下单验签失败!可能数据被篡改！===================");
            return new AjaxResult("FAIL", "sign验签失败！", null,null,null);
        }else{
            //先找到
            Map<String, Object> params = new HashMap<>();
            params.put("productId",creatOrderDTO.getProductId());
            params.put("mchId",creatOrderDTO.getMchId());
            List<OperateMerchantProdect> operateMerchantProdects = operateMerchantProdectService.queryMchProList(params);
            //金额单位换算
            BigDecimal amount = creatOrderDTO.getAmount().divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP);
            if(operateMerchantProdects.size() < 0){

                //飞机发送通知
                SysRobat robat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                if(robat.getTgmError() != null && !robat.getTgmError().equals("")){
                    String message = "商户订单：" + creatOrderDTO.getMchOrderNo() + ",金额:" + amount + "\n" +
                            "产品编码：" + creatOrderDTO.getProductId() + ",商户号:" + creatOrderDTO.getMchId() + "\n" +
                            "产品和商户没有配置=======交易关闭";
                    //发送
                    execBot.sendMessageToGroup(robat.getTgmError().toString(),message);
                }


                log.error("======产品id="+creatOrderDTO.getProductId()+"==商户id="+creatOrderDTO.getMchId()+"=======商户和产品没有配置=======交易关闭======");
                return new AjaxResult("FAIL", "下单失败(商户和产品配置异常)，请联系管理员！", null,null,null);
            }else{

                Long mchProId = operateMerchantProdects.get(0).getId();
                //查询到的数据就是配置，过滤掉删除的（包括通道标识和支付通道）
                List<RelevanceDTO> relevanceDTOS;
                if(operateMerchantProdects.get(0).getPriority().equals("1")){
                    //商户和产品单独配置
                    relevanceDTOS = operateMchProAloneService.queryChaByMchProId(mchProId);
                }else{
                    //商户和产品统一配置
                    relevanceDTOS = operatePayProductInfoService.queryChaByProId(creatOrderDTO.getProductId());
                }
                if(relevanceDTOS.size() <= 0){

                    //飞机发送通知
                    SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                    if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                        String message = "产品编码：" + creatOrderDTO.getProductId() + "\n" +
                                "产品没有配置通道=======交易关闭";
                        //发送
                        execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
                    }


                    log.error("======产品id="+creatOrderDTO.getProductId()+"=======产品没有配置通道=======交易关闭======");
                    return new AjaxResult("FAIL", "下单失败(产品配置异常)，请联系管理员！", null,null,null);
                }else{
                    //找到符合条件的上游通道
                    int check = 0;
                    for(RelevanceDTO dto : relevanceDTOS){
                        check++;
                        //风控匹配
                        OperateRiskControl operateRiskControl = operateRiskControlService.selectOperateRiskControlById(dto.getRiskControlId());
                        if(ObjectUtils.isNotEmpty(operateRiskControl) && operateRiskControl != null){
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

                            //查询该通道当天的交易总金额
                            OperateOrderDTO operateOrderDTO = new OperateOrderDTO();
                            operateOrderDTO.setPayChannelId(dto.getPayChaId());
                            operateOrderDTO.setToDayDate(beijingTimeTodayString);
                            List<OperateOrder> dataList = operateOrderService.selectOperateOrderList(operateOrderDTO);
                            //订单完成和支付成功的状态下
                            List<OperateOrder> havePaid = dataList.stream().filter(item -> item.getPaymentStatus().equals("2") ||
                                    item.getPaymentStatus().equals("4")).collect(Collectors.toList());
                            BigDecimal totalAmount = havePaid.stream().map(OperateOrder::getPaymentAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            //风控配置的判断
                            if(operateRiskControl.getTotalAmount().compareTo(totalAmount.add(amount)) >= 0){
                                //金额区间
                                if(operateRiskControl.getMaxAmount().compareTo(amount) >= 0
                                        && operateRiskControl.getMinAmount().compareTo(amount) <= 0){

                                    List<String> fixedAmountList = new ArrayList<>();
                                    if(operateRiskControl.getFixedAmount() != null && StringUtils.isNotEmpty(operateRiskControl.getFixedAmount())) {
                                        //根据类型过滤
                                        String COMMA = ",|，";
                                        // 将逗号替换为常量表示的逗号
                                        String replaced = operateRiskControl.getFixedAmount().replaceAll(COMMA, ",");
                                        String[] fixedAmount = replaced.split(COMMA);

                                        for (String fruit : fixedAmount) {
                                            // 使用trim去除可能存在的空格
                                            fixedAmountList.add(fruit.trim());
                                        }
                                    }

                                    if(operateRiskControl.getAmountType().equals("0")){
                                        //任意连续（排除金额）
                                        String strAmount = String.valueOf(amount);
                                        if(fixedAmountList.size() > 0 && fixedAmountList.stream().anyMatch(item -> new BigDecimal(item).compareTo(amount) == 0)){

                                            //飞机发送通知
//                                            SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
//                                            if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
//                                                String message = "金额：" + amount + "\n" +
//                                                        "上游：" + dto.getChaLabName() + "  " + "通道编码：" + dto.getPayChaCode() + "\n" +
//                                                        "支付金额为排除金额=======交易关闭";
//                                                //发送
//                                                execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
//                                            }

                                            log.error("=============支付通道id="+dto.getPayChaId()+"=====符合风控的排除金额======跳过=========");
                                            continue;
                                        }else{

                                            //生成订单
                                            OperatePayProduct operatePayProduct = operatePayProductService.selectOperatePayProductById(creatOrderDTO.getProductId());
                                            OperateOrder operateOrder = new OperateOrder();
                                            operateOrder.setNotifyUrl(creatOrderDTO.getNotifyUrl());
                                            operateOrder.setReturnUrl(creatOrderDTO.getReturnUrl());
                                            operateOrder.setMchId(operateMerchant.getId());
                                            operateOrder.setMchName(operateMerchant.getMchName());
                                            operateOrder.setProductId(operatePayProduct.getId());
                                            operateOrder.setProductName(operatePayProduct.getProName());
                                            operateOrder.setChaLabId(dto.getChaLabId());
                                            operateOrder.setChaLabName(dto.getChaLabName());
                                            operateOrder.setPayChannelId(dto.getPayChaId());
                                            operateOrder.setPayChannelName(dto.getPayChaName());
                                            operateOrder.setPayChannelCode(dto.getPayChaCode());
                                            operateOrder.setMchOrderNo(creatOrderDTO.getMchOrderNo());
                                            if(dto.getChaType().equals("1")){
                                                operateOrder.setPayOrderNo(generateBillNoNumber("test"));
                                            }else{
                                                operateOrder.setPayOrderNo(generateBillNoNumber("CFT"));
                                            }

                                            operateOrder.setLabelRate(dto.getChaRate());
                                            operateOrder.setPaymentStatus("0");
                                            operateOrder.setPaymentAmount(amount);
                                            //平台收入
                                            BigDecimal platRate = dto.getChaRate().divide(new BigDecimal(100));
                                            BigDecimal platDeduct = amount.multiply(platRate);
                                            BigDecimal platActReceipt = amount.subtract(platDeduct);
                                            operateOrder.setPlatActReceipt(platActReceipt.setScale(2,BigDecimal.ROUND_HALF_UP));
                                            //渠道成本
                                            operateOrder.setPayChaCost(platDeduct.setScale(2,BigDecimal.ROUND_HALF_UP));

                                            //商户收益
                                            BigDecimal mchDeduct;
                                            if(operateMerchantProdects.get(0).getPriority().equals("1")){
                                                operateOrder.setMchRate(operateMerchantProdects.get(0).getRate());
                                                //商户实际收入
                                                BigDecimal mchRate = operateMerchantProdects.get(0).getRate().divide(new BigDecimal(100));
                                                mchDeduct = amount.multiply(mchRate);
                                                BigDecimal mchActReceipt = amount.subtract(mchDeduct);
                                                operateOrder.setMchActReceipt(mchActReceipt.setScale(2,BigDecimal.ROUND_HALF_UP));
                                            }else{
                                                operateOrder.setMchRate(operatePayProduct.getMchRate());
                                                //商户实际收入
                                                BigDecimal mchRate = operatePayProduct.getMchRate().divide(new BigDecimal(100));
                                                mchDeduct = amount.multiply(mchRate);
                                                BigDecimal mchActReceipt = amount.subtract(mchDeduct);
                                                operateOrder.setMchActReceipt(mchActReceipt.setScale(2,BigDecimal.ROUND_HALF_UP));
                                            }
                                            if(operateMerchant.getAgentId() != null){
                                                //根据产品/商户查询关联
                                                Map<String, Object> agentParams = new HashMap<>();
                                                agentParams.put("productId",creatOrderDTO.getProductId());
                                                agentParams.put("agentId",operateMerchant.getAgentId());
                                                List<OperateAgentProduct> agentProduct = operateAgentProductMapper.selectAgePro(agentParams);
                                                if(agentProduct.size() > 0){
                                                    operateOrder.setAgentId(operateMerchant.getAgentId());
                                                    operateOrder.setAgentRate(agentProduct.get(0).getRate());
                                                    //代理收入
                                                    BigDecimal agentRate = agentProduct.get(0).getRate().divide(new BigDecimal(100));
                                                    BigDecimal agentDeduct = amount.multiply(agentRate);
                                                    operateOrder.setAgentActReceipt(agentDeduct.setScale(2,BigDecimal.ROUND_HALF_UP));
                                                    //利润
                                                    BigDecimal profit = mchDeduct.subtract(operateOrder.getPayChaCost().add(operateOrder.getAgentActReceipt()));
                                                    operateOrder.setProfit(profit.setScale(2,BigDecimal.ROUND_HALF_UP));
                                                }

                                            }else{
                                                //利润
                                                BigDecimal profit = mchDeduct.subtract(operateOrder.getPayChaCost());
                                                operateOrder.setProfit(profit.setScale(2,BigDecimal.ROUND_HALF_UP));
                                            }


                                            try {
                                                // 插入订单数据
                                                operateOrderService.insertOperateOrder(operateOrder);
                                            } catch (DuplicateKeyException e) {
                                                return new AjaxResult("FAIL", "下单失败,订单号重复！", null,null,null);
                                            }



                                            //测试通道
                                            if(dto.getChaType().equals("1")){

                                                //订单状态改变
                                                operateOrder.setPaymentStatus("1");
                                                operateOrderService.updateOperateOrder(operateOrder);

                                                //生成sign验签
                                                Map<String, Object> signParams = new HashMap<>();
                                                signParams.put("code","SUCCESS");
                                                signParams.put("msg","操作成功");
                                                signParams.put("payUrl",PAY_TEST_URL);
                                                signParams.put("payOrderNo",operateOrder.getPayOrderNo());
                                                String sign = null;
                                                try {
                                                    sign = SignConfig.getSignNew(signParams,mchkey,"0","0","key");
                                                } catch (Exception e) {
//                                                        throw new RuntimeException(e);
                                                }

                                                return new AjaxResult("SUCCESS", "操作成功", PAY_TEST_URL,operateOrder.getPayOrderNo(),sign);


                                            //防拉空
                                            }else if(dto.getChaType().equals("2")){

                                                //订单状态改变
                                                operateOrder.setPaymentStatus("1");
                                                operateOrderService.updateOperateOrder(operateOrder);

                                                //生成sign验签
                                                Map<String, Object> signParams = new HashMap<>();
                                                signParams.put("code","SUCCESS");
                                                signParams.put("msg","操作成功");
                                                signParams.put("payUrl",PAY_ABLOWDOWN_URL);
                                                signParams.put("payOrderNo",operateOrder.getPayOrderNo());
                                                String sign = null;
                                                try {
                                                    sign = SignConfig.getSignNew(signParams,mchkey,"0","0","key");
                                                } catch (Exception e) {
//                                                        throw new RuntimeException(e);
                                                }

                                                return new AjaxResult("SUCCESS", "操作成功", PAY_ABLOWDOWN_URL,operateOrder.getPayOrderNo(),sign);

                                            }else{

                                                //上游下单
                                                AjaxResult ajaxResult = sendChaLable(dto,creatOrderDTO,operateOrder,mchkey);
                                                if(ajaxResult.get("code").toString().equals("SUCCESS")){

                                                    long end = System.currentTimeMillis();
                                                    float processTime = (end - start) / 1000F;
                                                    log.error("=====商户订单号为"+creatOrderDTO.getMchOrderNo()+"=====下单流程消耗的时间======:" + processTime + "秒====");
                                                    return ajaxResult;

                                                }else{
                                                    //下单失败

                                                    //飞机发送通知(下单上游返回失败)
                                                    SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                                                    if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                                                        String message = "产品["+creatOrderDTO.getProductId()+"]  " +dto.getChaLabName() + "【"+dto.getPayChaCode()+"】" + "取码失败\n" +
                                                                "商户订单：" + creatOrderDTO.getMchOrderNo() + ",金额:" + amount + "\n" +
                                                                "原因：" + ajaxResult.get("msg").toString();
                                                        //发送
                                                        execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);

                                                    }


                                                    //这时要删除订单(最后一条订单不删除，作为蓝单展示)
                                                    if(check != relevanceDTOS.size()){
                                                        operateOrderService.deleteOperateOrderById(operateOrder.getId());
                                                        continue;
                                                    }else{
                                                        //所有通道都失败，这个时候是需要通知商户
                                                        //订单状态改变(这个时候蓝单，因为没拉到肯定支付不了)
//                                                        operateOrder.setPaymentStatus("3");
//                                                        operateOrderService.updateOperateOrder(operateOrder);

                                                        return new AjaxResult("FAIL", "下单失败！", null,null,null);
                                                    }

                                                }

                                            }



                                        }

                                    }else if(operateRiskControl.getAmountType().equals("1")){

                                        //固定金额（固定金额）
                                        String strAmount = String.valueOf(amount);
                                        if(fixedAmountList.size() > 0 && fixedAmountList.stream().anyMatch(item -> new BigDecimal(item).compareTo(amount) == 0)){

                                            //生成订单
                                            OperatePayProduct operatePayProduct = operatePayProductService.selectOperatePayProductById(creatOrderDTO.getProductId());
                                            OperateOrder operateOrder = new OperateOrder();
                                            operateOrder.setNotifyUrl(creatOrderDTO.getNotifyUrl());
                                            operateOrder.setReturnUrl(creatOrderDTO.getReturnUrl());
                                            operateOrder.setMchId(operateMerchant.getId());
                                            operateOrder.setMchName(operateMerchant.getMchName());
                                            operateOrder.setProductId(operatePayProduct.getId());
                                            operateOrder.setProductName(operatePayProduct.getProName());
                                            operateOrder.setChaLabId(dto.getChaLabId());
                                            operateOrder.setChaLabName(dto.getChaLabName());
                                            operateOrder.setPayChannelId(dto.getPayChaId());
                                            operateOrder.setPayChannelName(dto.getPayChaName());
                                            operateOrder.setPayChannelCode(dto.getPayChaCode());
                                            operateOrder.setMchOrderNo(creatOrderDTO.getMchOrderNo());
                                            if(dto.getChaType().equals("1")){
                                                operateOrder.setPayOrderNo(generateBillNoNumber("test"));
                                            }else{
                                                operateOrder.setPayOrderNo(generateBillNoNumber("CFT"));
                                            }
                                            operateOrder.setLabelRate(dto.getChaRate());
                                            operateOrder.setPaymentStatus("0");
                                            operateOrder.setPaymentAmount(amount);
                                            //平台收入
                                            BigDecimal platRate = dto.getChaRate().divide(new BigDecimal(100));
                                            BigDecimal platDeduct = amount.multiply(platRate);
                                            BigDecimal platActReceipt = amount.subtract(platDeduct);
                                            operateOrder.setPlatActReceipt(platActReceipt.setScale(2,BigDecimal.ROUND_HALF_UP));
                                            //渠道成本
                                            operateOrder.setPayChaCost(platDeduct.setScale(2,BigDecimal.ROUND_HALF_UP));

                                            //商户收益
                                            BigDecimal mchDeduct;
                                            if(operateMerchantProdects.get(0).getPriority().equals("1")){
                                                operateOrder.setMchRate(operateMerchantProdects.get(0).getRate());
                                                //商户实际收入
                                                BigDecimal mchRate = operateMerchantProdects.get(0).getRate().divide(new BigDecimal(100));
                                                mchDeduct = amount.multiply(mchRate);
                                                BigDecimal mchActReceipt = amount.subtract(mchDeduct);
                                                operateOrder.setMchActReceipt(mchActReceipt.setScale(2,BigDecimal.ROUND_HALF_UP));
                                            }else{
                                                operateOrder.setMchRate(operatePayProduct.getMchRate());
                                                //商户实际收入
                                                BigDecimal mchRate = operatePayProduct.getMchRate().divide(new BigDecimal(100));
                                                mchDeduct = amount.multiply(mchRate);
                                                BigDecimal mchActReceipt = amount.subtract(mchDeduct);
                                                operateOrder.setMchActReceipt(mchActReceipt.setScale(2,BigDecimal.ROUND_HALF_UP));
                                            }
                                            if(operateMerchant.getAgentId() != null){
                                                //根据产品/商户查询关联
                                                Map<String, Object> agentParams = new HashMap<>();
                                                agentParams.put("productId",creatOrderDTO.getProductId());
                                                agentParams.put("agentId",operateMerchant.getAgentId());
                                                List<OperateAgentProduct> agentProduct = operateAgentProductMapper.selectAgePro(agentParams);
                                                if(agentProduct.size() > 0){
                                                    operateOrder.setAgentId(operateMerchant.getAgentId());
                                                    operateOrder.setAgentRate(agentProduct.get(0).getRate());
                                                    //代理收入
                                                    BigDecimal agentRate = agentProduct.get(0).getRate().divide(new BigDecimal(100));
                                                    BigDecimal agentDeduct = amount.multiply(agentRate);
                                                    operateOrder.setAgentActReceipt(agentDeduct.setScale(2,BigDecimal.ROUND_HALF_UP));
                                                    //利润
                                                    BigDecimal profit = mchDeduct.subtract(operateOrder.getPayChaCost().add(operateOrder.getAgentActReceipt()));
                                                    operateOrder.setProfit(profit.setScale(2,BigDecimal.ROUND_HALF_UP));
                                                }

                                            }else{
                                                //利润
                                                BigDecimal profit = mchDeduct.subtract(operateOrder.getPayChaCost());
                                                operateOrder.setProfit(profit.setScale(2,BigDecimal.ROUND_HALF_UP));
                                            }


                                            try {
                                                // 插入订单数据
                                                operateOrderService.insertOperateOrder(operateOrder);
                                            } catch (DuplicateKeyException e) {
                                                return new AjaxResult("FAIL", "下单失败,订单号重复！", null,null,null);
                                            }


                                            //测试通道
                                            if(dto.getChaType().equals("1")){

                                                //订单状态改变
                                                operateOrder.setPaymentStatus("1");
                                                operateOrderService.updateOperateOrder(operateOrder);

                                                //生成sign验签
                                                Map<String, Object> signParams = new HashMap<>();
                                                signParams.put("code","SUCCESS");
                                                signParams.put("msg","操作成功");
                                                signParams.put("payUrl",PAY_TEST_URL);
                                                signParams.put("payOrderNo",operateOrder.getPayOrderNo());
                                                String sign = null;
                                                try {
                                                    sign = SignConfig.getSignNew(signParams,mchkey,"0","0","key");
                                                } catch (Exception e) {
//                                                        throw new RuntimeException(e);
                                                }


                                                return new AjaxResult("SUCCESS", "操作成功", PAY_TEST_URL,operateOrder.getPayOrderNo(),sign);


                                            //防拉空
                                            }else if(dto.getChaType().equals("2")){

                                                //订单状态改变
                                                operateOrder.setPaymentStatus("1");
                                                operateOrderService.updateOperateOrder(operateOrder);

                                                //生成sign验签
                                                Map<String, Object> signParams = new HashMap<>();
                                                signParams.put("code","SUCCESS");
                                                signParams.put("msg","操作成功");
                                                signParams.put("payUrl",PAY_ABLOWDOWN_URL);
                                                signParams.put("payOrderNo",operateOrder.getPayOrderNo());
                                                String sign = null;
                                                try {
                                                    sign = SignConfig.getSignNew(signParams,mchkey,"0","0","key");
                                                } catch (Exception e) {
    //                                                        throw new RuntimeException(e);
                                                }

                                                return new AjaxResult("SUCCESS", "操作成功", PAY_ABLOWDOWN_URL,operateOrder.getPayOrderNo(),sign);

                                            }else{

                                                //上游下单
                                                AjaxResult ajaxResult = sendChaLable(dto, creatOrderDTO, operateOrder, mchkey);

                                                //如果失败就找下一个通道
                                                if(ajaxResult.get("code").toString().equals("SUCCESS")){

                                                    long end = System.currentTimeMillis();
                                                    float processTime = (end - start) / 1000F;
                                                    log.error("=====商户订单号为"+creatOrderDTO.getMchOrderNo()+"=====下单流程消耗的时间======:" + processTime + "秒====");
                                                    return ajaxResult;

                                                }else{

                                                    //飞机发送通知(下单上游返回失败)
                                                    SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                                                    if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                                                        String message = "产品["+creatOrderDTO.getProductId()+"]  " + dto.getChaLabName() + "【"+dto.getPayChaCode()+"】" + "取码失败\n" +
                                                                "商户订单：" + creatOrderDTO.getMchOrderNo() + ",金额:" + amount + "\n" +
                                                                "原因：" + ajaxResult.get("msg").toString();
                                                        //发送
                                                        execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
                                                    }


                                                    //这时要删除订单(最后一条订单不删除，作为蓝单展示)
                                                    if(check != relevanceDTOS.size()){
                                                        operateOrderService.deleteOperateOrderById(operateOrder.getId());
                                                        continue;
                                                    }else{
                                                        //所有通道都失败，这个时候是需要通知商户
                                                        //订单状态改变（这时候订单就是蓝单 ）
//                                                        operateOrder.setPaymentStatus("3");
//                                                        operateOrderService.updateOperateOrder(operateOrder);

                                                        return new AjaxResult("FAIL", "下单失败！", null,null,null);
                                                    }
                                                }

                                            }


                                        }else{

//                                            //飞机发送通知
//                                            SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
//                                            if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
//                                                String message = "金额：" + amount + "\n" +
//                                                        "上游：" + dto.getChaLabName() + "  " + "通道编码：" + dto.getPayChaCode() + "\n" +
//                                                        "不符合风控的固定金额=======交易关闭";
//                                                //发送
//                                                execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
//                                            }

                                            log.error("=========支付通道id="+dto.getPayChaId()+"====不符合风控的固定金额======跳过=========");
                                            continue;
                                        }

                                    }

                                }else{

//                                    //飞机发送通知
//                                    SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
//
//                                    if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
//                                        String message = "金额：" + amount + "\n" +
//                                                "上游：" + dto.getChaLabName() + "  " + "通道编码：" + dto.getPayChaCode() + "\n" +
//                                                "支付金额不在风控的金额范围区间=======交易关闭";
//                                        //发送
//                                        execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
//                                    }


                                    log.error("=========支付通道id="+dto.getPayChaId()+"====支付金额不在风控的金额范围区间======跳过=========");
                                    continue;
                                }

                            }else{

                                //飞机发送通知
                                SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                                if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                                    String message = "产品编码：" + creatOrderDTO.getProductId() + "\n" +
                                            "上游：" + dto.getChaLabName() + "  " + "通道编码：" + dto.getPayChaCode() + "\n" +
                                            "超过今天最大交易量=======交易关闭";
                                    //发送
                                    execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
                                }


                                log.error("===========支付通道id="+dto.getPayChaId()+"===超过该支付通道当天最大交易量======跳过=========");
                                continue;
                            }
                        }else{
                            //飞机发送通知
                            SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                            if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                                String message =  "上游：" + dto.getChaLabName() + "  " + "通道编码：" + dto.getPayChaCode() + "\n" +
                                        "风控没有配置=======交易关闭";
                                //发送
                                execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
                            }


                            log.error("===========支付通道id="+dto.getPayChaId()+"===风控没有配置======跳过=========");
                            continue;
                        }
                    }
                }

            }

        }

        //没有找到匹配的通道
        long end = System.currentTimeMillis();
        float processTime = (end - start) / 1000F;
        log.error("=====商户订单号为"+creatOrderDTO.getMchOrderNo()+"==没有找到匹配的通道===下单流程消耗的时间======:" + processTime + "秒====");

        return new AjaxResult("FAIL", "下单失败！", null,null,null);
    }



    /**
     * 调用上游接口---下单
     */
    public AjaxResult sendChaLable(RelevanceDTO relevanceDTO, CreatOrderDTO creatOrderDTO,OperateOrder operateOrder,String mchkey){
        //获取上游
        OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(relevanceDTO.getChaLabId());
        //上游请求参数
        Map<String, Object> params = new HashMap<>();
        List<Map<String, Object>> configListObject = JSONArray.parseObject(operateChannelLabel.getConfigDescribe(), List.class);

        //基本配置
        //商户id(required为0是必填)
        if(configListObject.get(0).get("required").toString().equals("0")){

            //数据判断和转换
            String dataType = configListObject.get(0).get("dataType").toString();
            Object data = SignConfig.dataTypeConvert(operateChannelLabel.getMchId(), dataType);

            params.put(configListObject.get(0).get("name").toString(),data);
        }
        //产品id,或者是上游编码(required为0是必填)
        if(configListObject.get(1).get("required").toString().equals("0")){

            //梦想点卷的通道不参与签名 不参与签名
            if(ObjectUtils.isNotEmpty(configListObject.get(1).get("isSign"))){

            }else{

                //数据判断和转换
                String dataType = configListObject.get(1).get("dataType").toString();
                Object data = SignConfig.dataTypeConvert(relevanceDTO.getPayChaCode(), dataType);

                params.put(configListObject.get(1).get("name").toString(),data);
            }

        }
        //订单号(required为0是必填)
        if(configListObject.get(2).get("required").toString().equals("0")){

            //数据判断和转换
            String dataType = configListObject.get(2).get("dataType").toString();
            Object data = SignConfig.dataTypeConvert(operateOrder.getPayOrderNo(), dataType);

            params.put(configListObject.get(2).get("name").toString(),data);
        }
        //支付金额(required为0是必填,value为0单位要换算成分)
        if(configListObject.get(3).get("required").toString().equals("0")){
            if(!configListObject.get(3).get("value").toString().equals("0")){

                BigDecimal amount = creatOrderDTO.getAmount().divide(new BigDecimal(100));
                //数据判断和转换
                String dataType = configListObject.get(3).get("dataType").toString();
                //化为整数
                BigDecimal newAmount = amount.setScale(0, RoundingMode.HALF_UP);
                Object data = SignConfig.dataTypeConvert(newAmount, dataType);

                params.put(configListObject.get(3).get("name").toString(),data);
            }else{

                //数据判断和转换
                String dataType = configListObject.get(3).get("dataType").toString();
                //化为整数
                BigDecimal newAmount = creatOrderDTO.getAmount().setScale(0, RoundingMode.HALF_UP);
                Object data = SignConfig.dataTypeConvert(newAmount, dataType);

                params.put(configListObject.get(3).get("name").toString(),data);
            }

        }
        //notifyUrl(required为0是必填)、sign加密前url不需要URLEncoder
        if(configListObject.get(4).get("required").toString().equals("0")){
            //上游回调的请求方式确认
            String paramType = configListObject.get(7).get("paramType").toString();
            if(paramType.equals("param")){

                params.put(configListObject.get(4).get("name").toString(),NOTIFU_URL);

            }else if(paramType.equals("json")){

                params.put(configListObject.get(4).get("name").toString(),NOTIFU_BODY_URL);

            }

        }

        //returnUrl(required为0是必填)、sign加密前url不需要URLEncoder
        if(configListObject.get(5).get("required").toString().equals("0")){

            //玖忆的returnurl 不参与签名
            if(ObjectUtils.isNotEmpty(configListObject.get(5).get("isSign"))){

            }else{
                if(StringUtils.isNotEmpty(creatOrderDTO.getReturnUrl())){
                    params.put(configListObject.get(5).get("name").toString(),creatOrderDTO.getReturnUrl());
                }else{
                    //fufu 必填
                    if(ObjectUtils.isNotEmpty(configListObject.get(5).get("value"))){
                        params.put(configListObject.get(5).get("name").toString(),configListObject.get(5).get("value").toString());
                    }

                }
            }


        }

        //随机字符串（确保请求的时效性、防止重放攻击）
        if(configListObject.get(9).get("required").toString().equals("0")){
            String length = configListObject.get(9).get("length").toString();
            //肯定是字符串
            params.put(configListObject.get(9).get("name").toString(),generateNonce(Integer.valueOf(length)));
        }

        //毫秒级时间戳（确保请求的时效性、防止重放攻击）
        if(configListObject.get(10).get("required").toString().equals("0")){
            long timestamp = System.currentTimeMillis();

            if(ObjectUtils.isNotEmpty(configListObject.get(10).get("length"))){
                if(configListObject.get(10).get("length").equals("10")){
                    // 转换为秒级时间戳（10位）
                    timestamp = timestamp / 1000;
                }
            }
            String dataType = configListObject.get(10).get("dataType").toString();
            Object data = SignConfig.dataTypeConvert(timestamp, dataType);

            params.put(configListObject.get(10).get("name").toString(),data);
        }

        //时间：格式（2016-12-26 18:18:18）
        if(configListObject.get(11).get("required").toString().equals("0")){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 格式化当前时间
            String formattedDate = formatter.format(DateUtils.getNowDate());
            params.put(configListObject.get(11).get("name").toString(),formattedDate);
        }

        //时间：格式（20161226181818）
        if(configListObject.size() > 12){
            if(configListObject.get(12).get("required").toString().equals("0")){
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                // 格式化当前时间
                String formattedDate = formatter.format(DateUtils.getNowDate());
                params.put(configListObject.get(12).get("name").toString(),formattedDate);
            }
        }


        //上游不一样，有些参数需要额外的配置
        if(StringUtils.isNotEmpty(operateChannelLabel.getBuyParams()) && operateChannelLabel.getBuyParams() != null){
            List<Map<String, Object>> buyListObject = JSONArray.parseObject(operateChannelLabel.getBuyParams(), List.class);
            for (Map<String, Object> mapList : buyListObject) {
                if (mapList.get("required").toString().equals("0")) {

                    if(ObjectUtils.isNotEmpty(mapList.get("isSign"))){

                    }else{
                        //数据判断和转换
                        String dataType = mapList.get("dataType").toString();
                        Object data = SignConfig.dataTypeConvert(mapList.get("value"), dataType);

                        params.put(mapList.get("name").toString(),data);
                    }

                }

                //ip
                if(mapList.get("required").toString().equals("2")){
                    if(ObjectUtils.isNotEmpty(mapList.get("isSign"))){

                    }else{
                        params.put(mapList.get("name").toString(),ip);
                    }

                }

                //产品名称
                if(mapList.get("required").toString().equals("3")){
                    if(ObjectUtils.isNotEmpty(mapList.get("isSign"))){

                    }else{
                        params.put(mapList.get("name").toString(),operateOrder.getPayChannelName());
                    }

                }
            }
        }

        //sign验签,value值为0字段空不验证，1字段空也要参与签名
        try {

            if(ObjectUtils.isNotEmpty(configListObject.get(6).get("signType"))){

                String sign = "";
                if(configListObject.get(6).get("signType").equals("1")){
                    //玖忆
                    sign = SignConfig.getSignNew_JY1(params,operateChannelLabel.getMchKey());

                }else if(configListObject.get(6).get("signType").equals("2")){
                    //锦鲤
                    sign = SignConfig.getSignNew_JL1(params,operateChannelLabel.getMchKey());

                }else if(configListObject.get(6).get("signType").equals("3")){
                    //盈通
                    sign = SignConfig.getSignNew_YT1(params,operateChannelLabel.getMchKey());

                }else if(configListObject.get(6).get("signType").equals("4")){
                    //万嘉
                    sign = SignConfig.getSignNew_WJ1(params,operateChannelLabel.getMchKey());

                }else if(configListObject.get(6).get("signType").equals("5")){
                    //pag
                    sign = SignConfig.getSignNew_PGA(params,operateChannelLabel.getMchKey());

                }

                params.put(configListObject.get(6).get("name").toString(),sign);

            }else{

                String keyName = "key";
                if(ObjectUtils.isNotEmpty(configListObject.get(8).get("keyName"))){
                    keyName = configListObject.get(8).get("keyName").toString();
                }

                //蓝猫、新mw不在末尾添加key，直接加入集合按顺序生成验签
                if(configListObject.get(8).get("isKey").toString().equals("3")){
                    params.put(configListObject.get(8).get("keyName").toString(),operateChannelLabel.getMchKey());
                }

                String sign = SignConfig.getSignNew(params, operateChannelLabel.getMchKey(),
                        configListObject.get(6).get("value").toString(),configListObject.get(8).get("isKey").toString(),keyName);
                //有些上游sign是小写的
                if(configListObject.get(8).get("value").toString().equals("1")){
                    sign = sign.toLowerCase();
                }

                //蓝猫、新mw除掉key
                if(configListObject.get(8).get("isKey").toString().equals("3")){
                    params.remove(configListObject.get(8).get("keyName").toString());
                }

                params.put(configListObject.get(6).get("name").toString(),sign);
            }


        } catch (Exception e) {
            log.error("===========验签过程异常===================");
            return new AjaxResult("FAIL", "下单失败,验签过程异常！", null,null,null);
        }


        //然后再重新编码
        //支付结果后台回调URL(required为0是必填,URLEncoder为0表示url需要编码)
        if(configListObject.get(4).get("required").toString().equals("0")){
            if(configListObject.get(4).get("URLEncoder").toString().equals("0")){
                try {
                    String notifyUrl = URLEncoder.encode(NOTIFU_URL, "UTF-8");
                    params.put(configListObject.get(4).get("name").toString(),notifyUrl);
                } catch (UnsupportedEncodingException e) {
//                    throw new RuntimeException(e);
                }
            }
        }
        //支付结果前端跳转URL(required为0是必填)
        if(configListObject.get(5).get("required").toString().equals("0")){
            if(configListObject.get(5).get("URLEncoder").toString().equals("0")){
                try {
                    String returnUrl = URLEncoder.encode(creatOrderDTO.getReturnUrl(), "UTF-8");
                    params.put(configListObject.get(5).get("name").toString(),returnUrl);
                } catch (UnsupportedEncodingException e) {
//                    throw new RuntimeException(e);
                }
            }
        }

        //玖忆的returnurl 不参与签名，签名结束后补上
        if(ObjectUtils.isNotEmpty(configListObject.get(5).get("isSign"))){
            if(StringUtils.isNotEmpty(creatOrderDTO.getReturnUrl())){
                params.put(configListObject.get(5).get("name").toString(),creatOrderDTO.getReturnUrl());
            }else{
                //fufu 必填
                if(ObjectUtils.isNotEmpty(configListObject.get(5).get("value"))){
                    params.put(configListObject.get(5).get("name").toString(),configListObject.get(5).get("value").toString());
                }

            }
        }

        //梦想点卷的通道编码 不参与签名，签名结束后补上
        if(ObjectUtils.isNotEmpty(configListObject.get(1).get("isSign"))){
            //数据判断和转换
            String dataType = configListObject.get(1).get("dataType").toString();
            Object data = SignConfig.dataTypeConvert(relevanceDTO.getPayChaCode(), dataType);

            params.put(configListObject.get(1).get("name").toString(),data);
        }


        //梦想点卷的ip不参与签名、冷锋等
        if(StringUtils.isNotEmpty(operateChannelLabel.getBuyParams()) && operateChannelLabel.getBuyParams() != null){
            List<Map<String, Object>> buyListObject = JSONArray.parseObject(operateChannelLabel.getBuyParams(), List.class);
            for (Map<String, Object> mapList : buyListObject) {
                if (mapList.get("required").toString().equals("0")) {

                    if(ObjectUtils.isNotEmpty(mapList.get("isSign"))){
                        //数据判断和转换
                        String dataType = mapList.get("dataType").toString();
                        Object data = SignConfig.dataTypeConvert(mapList.get("value"), dataType);

                        params.put(mapList.get("name").toString(),data);
                    }

                }

                //ip
                if(mapList.get("required").toString().equals("2")){
                    if(ObjectUtils.isNotEmpty(mapList.get("isSign"))){
                        params.put(mapList.get("name").toString(),ip);
                    }
                }

                //产品名称
                if(mapList.get("required").toString().equals("3")){
                    if(ObjectUtils.isNotEmpty(mapList.get("isSign"))){
                        params.put(mapList.get("name").toString(),operateOrder.getPayChannelName());
                    }
                }
            }
        }




        //下单接口
        String url = operateChannelLabel.getGateway() + operateChannelLabel.getBuyPort();
        //请求类型
        String httpType = configListObject.get(0).get("httpType").toString();
        String paramType = configListObject.get(0).get("paramType").toString();
        try {
            String resJson = "";
            if(httpType.equals("GET")){

                String httpGetParam = SignConfig.getHttpGetParam(params);
                resJson = HttpUtils.sendGet(url,httpGetParam);

            }else if(httpType.equals("POST")){

                if(paramType.equals("param")){
                    //表单
                    resJson = HttpUtils.sendHttpPostParam(url,params);

                }else{
                    //json
                    resJson = HttpUtils.sendHttpPost(url,params);
                }
            }
            log.info("===========下单上游返回数据"+ resJson +"===================");
            //数据返回的结果
            Map<String,Object> resJsonMap =  JSONObject.parseObject(resJson);

            log.info("===========下单上游返回数据"+ resJsonMap +"===================");
            //根据返回的参数配置，拆分获取到的数据
            List<Map<String, Object>> returnParams = JSONArray.parseObject(operateChannelLabel.getReturnParams(), List.class);

            //code状态参数名
            String code = returnParams.get(0).get("name").toString();
            //code成功状态的值
            String codeSuccess = returnParams.get(0).get("value").toString();
            //msg消息参数名
            String msg = returnParams.get(1).get("name").toString();
            //url层级判断
            String checkTier = returnParams.get(2).get("value").toString();
            //第一层参数参数名
            String LayerOwn = returnParams.get(3).get("name").toString();
            //第二层参数参数名
            String LayerTwo = returnParams.get(4).get("name").toString();
            //第三层参数参数名
            String LayerThree = returnParams.get(6).get("name").toString();
            //下单成功
            if(resJsonMap.get(code).toString().equals(codeSuccess)){

                //先判断有没有验签(有些上游返回没有验签,0有1无)
                String signCheck = returnParams.get(5).get("isCheck").toString();
                boolean checkResult = true;
                if(signCheck.equals("0")){
                    //先验签
                    String chaLablekey = operateChannelLabel.getMchKey();
                    try {

                        String keyName = "key";
                        if(ObjectUtils.isNotEmpty(returnParams.get(5).get("keyName"))){
                            keyName = returnParams.get(5).get("keyName").toString();
                        }

                        if(ObjectUtils.isNotEmpty(returnParams.get(5).get("signParam"))){

                            String signParam = returnParams.get(5).get("signParam").toString();
                            String data = JSON.toJSONString(resJsonMap.get(signParam));
                            Map<String,Object> dataMap =  JSONObject.parseObject(data);

                            //目前“控股”，sign在外面，所以要加进去
                            //目前“安心”，sign在里面，不用加进去,signLoc为0表示sign在data里面
                            if(ObjectUtils.isNotEmpty(returnParams.get(5).get("signLoc"))){
                                if(returnParams.get(5).get("signLoc").equals("1")){
                                    dataMap.put(returnParams.get(5).get("name").toString(),resJsonMap.get(returnParams.get(5).get("name")));
                                }
                            }

                            checkResult = SignConfig.checkIsSignValidFromResponse(dataMap, chaLablekey,returnParams.get(5).get("value").toString()
                                    ,returnParams.get(5).get("lowerOrUpper").toString(),returnParams.get(5).get("isKey").toString(),keyName);

                        }else{

                            checkResult = SignConfig.checkIsSignValidFromResponse(resJsonMap, chaLablekey,returnParams.get(5).get("value").toString()
                                    ,returnParams.get(5).get("lowerOrUpper").toString(),returnParams.get(5).get("isKey").toString(),keyName);
                        }


                    } catch (Exception e) {
                        log.error("===========验签过程异常===================");
                        return new AjaxResult("FAIL", "下单失败,验签过程异常！", null,null,null);
                    }
                }

                if(checkResult == false){
                    log.error("===========支付通道id="+relevanceDTO.getPayChaId()+"上游返回数据验签失败!可能数据被篡改！===================");
                    return new AjaxResult("FAIL", "下单失败,上游返回数据验签失败！", null,null,null);
                }else{

                    //订单状态改变
                    operateOrder.setPaymentStatus("1");
                    operateOrderService.updateOperateOrder(operateOrder);

                    String payUrl = "";
                    if(checkTier.equals("1")){
                        payUrl =  resJsonMap.get(LayerOwn).toString();
                    }else if(checkTier.equals("2")){
                        String data = JSON.toJSONString(resJsonMap.get(LayerOwn));
                        Map<String,Object> dataMap =  JSONObject.parseObject(data);
                        payUrl =  dataMap.get(LayerTwo).toString();
                    }else if(checkTier.equals("3")){
                        String data = JSON.toJSONString(resJsonMap.get(LayerOwn));
                        Map<String,Object> dataMap =  JSONObject.parseObject(data);
                        String urlParam = JSON.toJSONString(dataMap.get(LayerTwo));
                        Map<String,Object> urlParamMap =  JSONObject.parseObject(urlParam);
                        payUrl =  urlParamMap.get(LayerThree).toString();
                    }

                    //生成sign验签
                    Map<String, Object> signParams = new HashMap<>();
                    signParams.put("code","SUCCESS");
                    signParams.put("msg","操作成功");
                    signParams.put("payUrl",payUrl);
                    signParams.put("payOrderNo",operateOrder.getPayOrderNo());
                    String sign = SignConfig.getSignNew(signParams,mchkey,"0","0","key");


                    return new AjaxResult("SUCCESS", "操作成功", payUrl,operateOrder.getPayOrderNo(),sign);

                }


            }else{

                log.error("===========支付通道id="+relevanceDTO.getPayChaId()+"下单失败原因======="+resJsonMap.get(msg)+"============");
                return new AjaxResult("FAIL", "上游返回失败：" + resJsonMap.get(msg).toString(), null,null,null);
            }



        } catch (Exception e) {
            log.error("===========支付通道id="+relevanceDTO.getPayChaId()+"访问上游请求http异常===================");
            return new AjaxResult("FAIL", "下单失败,访问上游请求http异常！", null,null,null);
        }
    }

    /**
     * 上游回调接口
     */
    @RequestMapping(value = "/pay/notify", method = {RequestMethod.GET, RequestMethod.POST})
    public Object notify(HttpServletRequest request,@RequestParam Map<String, Object> params)
    {
        //获取请求用户的ip地址
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_FORWARDED");
        }

        log.info("=================上游回调参数" + params + "============================");
        log.info("=================上游回调ip" + ipAddress + "============================");
        //根据通知的ip找到对应的渠道
        if(StringUtils.isNotEmpty(ipAddress) || ipAddress != null){
            List<OperateChannelLabel> operateChannelLabels = operateChannelLabelService.selectOperateChannelLabelList(new OperateChannelLabel());
            for(OperateChannelLabel channelLabel : operateChannelLabels){

                // 如果商户没有固定IP（比如梦想点卷等），则直接通过商户ID进行判断
                String apiId = "";
                if(channelLabel.getInformIp().equals("all")){
                    apiId = params.get("api_id").toString();
                }

                if(channelLabel.getInformIp().contains(ipAddress) ||
                        (apiId != null && apiId != "" && apiId.equals(channelLabel.getMchId()))) {

                    //根据回调的参数配置，拆分获取到的数据
                    List<Map<String, Object>> notifyParams = JSONArray.parseObject(channelLabel.getNotifyParams(), List.class);

                    //大总统和evo的服务器是同一个ip，除了校验ip还要校验商户id
                    if(ObjectUtils.isNotEmpty(notifyParams.get(1).get("check"))){
                        String mchId = notifyParams.get(1).get("name").toString();
                        if(!params.get(mchId).toString().equals(channelLabel.getMchId())){
                            continue;
                        }
                    }

                    String signCheck = notifyParams.get(8).get("isCheck").toString();
                    boolean checkResult = true;
                    if(signCheck.equals("0")){
                        //验签
                        try {
                            if(ObjectUtils.isNotEmpty(notifyParams.get(8).get("signType"))){

                                if(notifyParams.get(8).get("signType").equals("1")){
                                    //玖忆
                                    checkResult = SignConfig.checkIsSignValidFromResponse_JY(params, channelLabel.getMchKey());
                                }else if(notifyParams.get(8).get("signType").equals("2")){
                                    //锦鲤
                                    checkResult = SignConfig.checkIsSignValidFromResponse_JL(params, channelLabel.getMchKey());
                                }else if(notifyParams.get(8).get("signType").equals("3")){
                                    //盈通
                                    checkResult = SignConfig.checkIsSignValidFromResponse_YT(params, channelLabel.getMchKey());
                                }else if(notifyParams.get(8).get("signType").equals("4")){
                                    //万嘉
                                    checkResult = SignConfig.checkIsSignValidFromResponse_WJ(params, channelLabel.getMchKey());

                                }else if(notifyParams.get(8).get("signType").equals("5")){
                                    //pag
                                    checkResult = SignConfig.checkIsSignValidFromResponse_PGA(params, channelLabel.getMchKey());

                                }

                            }else{
                                String keyName = "key";
                                if(ObjectUtils.isNotEmpty(notifyParams.get(8).get("keyName"))){
                                    keyName = notifyParams.get(8).get("keyName").toString();
                                }

                                //蓝猫、新mw不在末尾添加key，直接加入集合按顺序生成验签
                                if(notifyParams.get(8).get("isKey").toString().equals("3")){
                                    params.put(notifyParams.get(8).get("keyName").toString(),channelLabel.getMchKey());
                                }

                                checkResult = SignConfig.checkIsSignValidFromResponse(params, channelLabel.getMchKey(),notifyParams.get(8).get("value").toString(),
                                        notifyParams.get(8).get("lowerOrUpper").toString(),notifyParams.get(8).get("isKey").toString(),keyName);
                            }

                        } catch (Exception e) {
                            log.error("===========上游回调数据验签过程异常===================");
                            return "验签失败";
                        }
                    }

                    if(checkResult){

                        //本系统订单号
                        String payOrderNoName = notifyParams.get(3).get("name").toString();
                        String payOrderNo = params.get(payOrderNoName).toString();
                        if (null == payOrderNo || StringUtils.isEmpty(payOrderNo)) {
                            return "fail,缺少商户订单号";
                        }else{
                            Map<String, Object> orderParams = new HashMap<>();
                            orderParams.put("payOrderNo",payOrderNo);
                            OperateOrder operateOrder = operateOrderMapper.selectOrderByParams(orderParams);

                            //如果订单是完成的状态，上游再次回调就忽略
                            if(operateOrder.getPaymentStatus().equals("4")){
                                return "fail";
                            }

                            //状态参数和值(支付成功的值)
                            String statusParam = notifyParams.get(5).get("name").toString();
                            String statusValue = notifyParams.get(5).get("value").toString();
                            //成功返回值
                            String success = notifyParams.get(7).get("value").toString();

                            boolean check = false;
                            //有些上游是支付成功才回调 没有回调的状态(梦想点卷、万嘉)
                            if(ObjectUtils.isNotEmpty(notifyParams.get(5).get("isCheck"))){
                                check = true;
                            }else{
                                //从回调的参数中获取值
                                String status = params.get(statusParam).toString();
                                check = status.equals(statusValue);
                            }

                            if(check){
                                //支付成功
                                operateOrder.setPaymentStatus("2");
                                operateOrder.setChaCallbackDate(DateUtils.getNowDate());
                                operateOrderService.updateOperateOrder(operateOrder);

                                //商户和代理商余额计算
                                Long agentId = operateOrder.getAgentId();
                                if(agentId != null){
                                    OperateAgent operateAgent = operateAgentService.selectOperateAgentById(agentId);
                                    if(operateAgent.getBalance() != null){
                                        operateAgent.setBalance(operateAgent.getBalance().add(operateOrder.getAgentActReceipt()));
                                    }else{
                                        operateAgent.setBalance(operateOrder.getAgentActReceipt());
                                    }
                                    operateAgentService.updateOperateAgent(operateAgent);
                                }

                                OperateMerchant operateMch = operateMerchantService.selectOperateMerchantById(operateOrder.getMchId());
                                if(operateMch.getBalance() != null){
                                    operateMch.setBalance(operateMch.getBalance().add(operateOrder.getMchActReceipt()));
                                }else{
                                    operateMch.setBalance(operateOrder.getMchActReceipt());
                                }
                                operateMerchantService.updateOperateMerchant(operateMch);


                                //查询这个上游的差额

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
                                OperateBillDTO operateBillDTO = new OperateBillDTO();
                                operateBillDTO.setCheckDate(beijingTimeTodayString);
                                operateBillDTO.setChaLabId(channelLabel.getId());
                                List<OperateBillDTO> dataList = operateOrderService.queryChaLabBill(operateBillDTO);
                                //拿到差额配置
                                //飞机发送消息(通道额度警告  小于等于配置的数值就要发送通知)
                                SysRobat robat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                                if(robat.getChaQuotaWarn() != null && !robat.getChaQuotaWarn().equals("")){
                                    if(dataList.get(0).getBalance().compareTo(new BigDecimal(robat.getChaQuotaWarn())) <= 0){
                                        if(robat.getTgmError() != null && !robat.getTgmError().equals("") && redisCache.getCacheObject(channelLabel.getId().toString()) == null){
                                            //24小时内提醒一次
                                            redisCache.setCacheObject(channelLabel.getId().toString(), "通道额度警告", 1440, TimeUnit.MINUTES);

                                            String message =  "注意：通道" + "  " + channelLabel.getChaLabName() + "  " + "差额低于：" + robat.getChaQuotaWarn() + "\n"
                                                    + robat.getWarnAppend();
                                            //发送
                                            execBot.sendMessageToGroup(robat.getTgmError().toString(),message);
                                        }
                                    }

                                }



                                //通知次数最多5次就要关闭线程
                                final int[] counter = {0};
                                int TOTAL_EXECUTIONS = 6;
                                //异步回调通知商户
                                Callable<Boolean> task = () -> {
                                    counter[0]++;
                                    if (counter[0] < TOTAL_EXECUTIONS) {
                                        //生成sign验签
                                        Map<String, Object> signParams = new HashMap<>();
                                        signParams.put("payOrderNo",operateOrder.getPayOrderNo());
                                        signParams.put("mchId",operateOrder.getMchId());
                                        signParams.put("productId",operateOrder.getProductId());
                                        signParams.put("mchOrderNo",operateOrder.getMchOrderNo());
                                        signParams.put("amount",operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue());
                                        signParams.put("status",2);
                                        OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(operateOrder.getMchId());
                                        String sign = SignConfig.getSignNew(signParams,operateMerchant.getMchKey(),"0","0","key");
                                        signParams.put("sign",sign);

                                        log.error("===========回调给商户===发送的参数："+signParams+"===================");

                                        //通知
                                        //请求类型
                                        OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operateOrder.getChaLabId());
                                        List<Map<String, Object>> notifyListObject = JSONArray.parseObject(operateChannelLabel.getNotifyParams(), List.class);
                                        String httpType = notifyListObject.get(9).get("httpType").toString();
                                        String required = notifyListObject.get(9).get("required").toString();
                                        String resJson = "";
                                        if(required.equals("0")){

                                            if(httpType.equals("GET")){

                                                String httpGetParam = SignConfig.getHttpGetParam(signParams);
                                                resJson = HttpUtils.sendGet(operateOrder.getNotifyUrl(),httpGetParam);

                                            }else if(httpType.equals("POST")){

                                                resJson = HttpUtils.sendHttpPost(operateOrder.getNotifyUrl(),signParams);
                                            }
                                        }else{

                                            resJson = HttpUtils.sendHttpPost(operateOrder.getNotifyUrl(),signParams);
                                        }

                                        log.info("===========回调给商户！商户返回的结果"+resJson+"===================");


                                        //生成回调记录
                                        OperateNotify operateNotify = operateNotifyService.selectOperateNotifyByOrderId(operateOrder.getId());
                                        if(operateNotify == null){
                                            operateNotify = new OperateNotify();
                                            operateNotify.setOrderId(operateOrder.getId());
                                            operateNotify.setMchId(operateOrder.getMchId());
                                            operateNotify.setMchName(operateOrder.getMchName());
                                            operateNotify.setMchOrderNo(operateOrder.getMchOrderNo());
                                            operateNotify.setPayOrderNo(operateOrder.getPayOrderNo());
                                            operateNotify.setOrderType("0");
                                            operateNotify.setNotifyUrl(operateOrder.getNotifyUrl());
                                            operateNotify.setNotifyDate(DateUtils.getNowDate());
                                            operateNotify.setNotifyTime(0);
                                            operateNotify.setNotifyStatus("2");
                                            operateNotifyService.insertOperateNotify(operateNotify);
                                        }


                                        //数据返回的结果
                                        if(resJson.equals("success")){

                                            Date NewDate = new Date();
                                            // 计算时间差(耗时)
                                            long diffInMilliseconds = NewDate.getTime() - operateOrder.getCreateTime().getTime();
                                            long diffInSeconds = diffInMilliseconds / 1000;
                                            operateOrder.setPaymentStatus("4");
                                            operateOrder.setElapsedTime(String.valueOf(diffInSeconds));
                                            operateOrderService.updateOperateOrder(operateOrder);

                                            //回调状态修改
                                            Integer notifyTime = operateNotify.getNotifyTime();
                                            operateNotify.setNotifyTime(notifyTime + 1);
                                            operateNotify.setNotifyDate(DateUtils.getNowDate());
                                            operateNotify.setMchMsg(resJson);
                                            operateNotify.setNotifyStatus("0");
                                            operateNotifyService.updateOperateNotify(operateNotify);

                                            // 返回true表示需要再次执行，false表示不再执行
                                            return false;

                                        }else{

                                            //回调失败
                                            if(!operateNotify.getNotifyStatus().equals("0")){
                                                Integer notifyTime = operateNotify.getNotifyTime();
                                                operateNotify.setNotifyTime(notifyTime + 1);
                                                operateNotify.setNotifyDate(DateUtils.getNowDate());
                                                operateNotify.setMchMsg(resJson);
                                                operateNotify.setNotifyStatus("1");
                                                operateNotifyService.updateOperateNotify(operateNotify);

                                                //最后一次发原因发出来
                                                if(counter[0] == 5){
                                                    //飞机发送通知(下单上游返回失败)
                                                    SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                                                    if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                                                        String message = "商户号："+ operateOrder.getMchId() + "\n"
                                                                + "商户订单号："+ operateOrder.getMchOrderNo() + "\n" +
                                                                "回调通知商户失败，原因： " + resJson;
                                                        //发送
                                                        execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
                                                    }

                                                }
                                            }

                                            return true;
                                        }

                                    } else {

                                        return false;
                                    }

                                };
                                // 初始延迟1秒，然后每60秒执行一次
                                startAsyncTaskWithResult(task, 60, 60);

                                //这个return是返回给上游
                                return success;

                            }else{
                                //支付失败
                                operateOrder.setPaymentStatus("3");
                                operateOrder.setChaCallbackDate(DateUtils.getNowDate());
                                operateOrderService.updateOperateOrder(operateOrder);

                                //异步回调通知商户
                                //通知次数最多5次就要关闭线程
                                final int[] counter = {0};
                                int TOTAL_EXECUTIONS = 6;
                                Callable<Boolean> task = () -> {
                                    //生成sign验签
                                    counter[0]++;
                                    if (counter[0] < TOTAL_EXECUTIONS) {
                                        Map<String, Object> signParams = new HashMap<>();
                                        signParams.put("payOrderNo",operateOrder.getPayOrderNo());
                                        signParams.put("mchId",operateOrder.getMchId());
                                        signParams.put("productId",operateOrder.getProductId());
                                        signParams.put("mchOrderNo",operateOrder.getMchOrderNo());
                                        signParams.put("amount",operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue());
                                        signParams.put("status",2);
                                        OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(operateOrder.getMchId());
                                        String sign = SignConfig.getSignNew(signParams,operateMerchant.getMchKey(),"0","0","key");
                                        signParams.put("sign",sign);

                                        log.error("===========回调给商户===发送的参数："+signParams+"===================");

                                        //通知
                                        //请求类型
                                        OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operateOrder.getChaLabId());
                                        List<Map<String, Object>> notifyListObject = JSONArray.parseObject(operateChannelLabel.getNotifyParams(), List.class);
                                        String httpType = notifyListObject.get(9).get("httpType").toString();
                                        String required = notifyListObject.get(9).get("required").toString();
                                        String resJson = "";
                                        if(required.equals("0")){

                                            if(httpType.equals("GET")){

                                                String httpGetParam = SignConfig.getHttpGetParam(signParams);
                                                resJson = HttpUtils.sendGet(operateOrder.getNotifyUrl(),httpGetParam);

                                            }else if(httpType.equals("POST")){

                                                resJson = HttpUtils.sendHttpPost(operateOrder.getNotifyUrl(),signParams);
                                            }
                                        }else{

                                            resJson = HttpUtils.sendHttpPost(operateOrder.getNotifyUrl(),signParams);
                                        }

                                        log.info("===========回调给商户！商户返回的结果"+resJson+"===================");


                                        //生成回调记录
                                        OperateNotify operateNotify = operateNotifyService.selectOperateNotifyByOrderId(operateOrder.getId());
                                        if(operateNotify == null){
                                            operateNotify = new OperateNotify();
                                            operateNotify.setOrderId(operateOrder.getId());
                                            operateNotify.setMchId(operateOrder.getMchId());
                                            operateNotify.setMchName(operateOrder.getMchName());
                                            operateNotify.setMchOrderNo(operateOrder.getMchOrderNo());
                                            operateNotify.setPayOrderNo(operateOrder.getPayOrderNo());
                                            operateNotify.setOrderType("0");
                                            operateNotify.setNotifyUrl(operateOrder.getNotifyUrl());
                                            operateNotify.setNotifyDate(DateUtils.getNowDate());
                                            operateNotify.setNotifyTime(0);
                                            operateNotify.setNotifyStatus("2");
                                            operateNotifyService.insertOperateNotify(operateNotify);
                                        }


                                        //数据返回的结果
                                        if(resJson.equals("success")){

                                            //回调状态修改
                                            Integer notifyTime = operateNotify.getNotifyTime();
                                            operateNotify.setNotifyTime(notifyTime + 1);
                                            operateNotify.setNotifyDate(DateUtils.getNowDate());
                                            operateNotify.setMchMsg(resJson);
                                            operateNotify.setNotifyStatus("0");
                                            operateNotifyService.updateOperateNotify(operateNotify);

                                            // 返回true表示需要再次执行，false表示不再执行
                                            return false;

                                        }else{

                                            //回调失败
                                            if(!operateNotify.getNotifyStatus().equals("0")){
                                                Integer notifyTime = operateNotify.getNotifyTime();
                                                operateNotify.setNotifyTime(notifyTime + 1);
                                                operateNotify.setNotifyDate(DateUtils.getNowDate());
                                                operateNotify.setMchMsg(resJson);
                                                operateNotify.setNotifyStatus("1");
                                                operateNotifyService.updateOperateNotify(operateNotify);

                                                //最后一次发原因发出来
                                                if(counter[0] == 5){
                                                    //飞机发送通知(下单上游返回失败)
                                                    SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                                                    if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                                                        String message = "商户号："+ operateOrder.getMchId() + "\n"
                                                                + "商户订单号："+ operateOrder.getMchOrderNo() + "\n" +
                                                                "回调通知商户失败，原因： " + resJson;
                                                        //发送
                                                        execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
                                                    }

                                                }
                                            }



                                            return true;
                                        }
                                    }else{

                                        return false;
                                    }

                                };
                                // 初始延迟1秒，然后每60秒执行一次
                                startAsyncTaskWithResult(task, 60, 60);

                                //这个return是返回给上游
                                return success;
                            }

                        }

                    }else{
                        log.error("===========上游回调验签失败，可能数据被篡改!===================");
                        return "验签失败";
                    }
                }
            }

        }else{
            log.error("===========获取回调请求的IP地址错误请联系管理员====================");
            return "fail";
        }
        return "fail";
    }

    /**
     * 上游回调接口
     */
    @RequestMapping(value = "/pay/body/notify", method = {RequestMethod.GET, RequestMethod.POST})
    public Object bodyNotify(HttpServletRequest request,@RequestBody Map<String, Object> params)
    {
        //获取请求用户的ip地址
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_FORWARDED");
        }

        log.info("=================上游回调参数" + params + "============================");
        log.info("=================上游回调ip" + ipAddress + "============================");
        //根据通知的ip找到对应的渠道
        if(StringUtils.isNotEmpty(ipAddress) || ipAddress != null){
            List<OperateChannelLabel> operateChannelLabels = operateChannelLabelService.selectOperateChannelLabelList(new OperateChannelLabel());
            for(OperateChannelLabel channelLabel : operateChannelLabels){

                // 如果商户没有固定IP（比如梦想点卷等），则直接通过商户ID进行判断
                String apiId = "";
                if(channelLabel.getInformIp().equals("all")){
                    apiId = params.get("api_id").toString();
                }

                if(channelLabel.getInformIp().contains(ipAddress) ||
                        (apiId != null && apiId != "" && apiId.equals(channelLabel.getMchId()))) {

                    //根据回调的参数配置，拆分获取到的数据
                    List<Map<String, Object>> notifyParams = JSONArray.parseObject(channelLabel.getNotifyParams(), List.class);


                    //大总统和evo的服务器是同一个ip，除了校验ip还要校验商户id
                    if(ObjectUtils.isNotEmpty(notifyParams.get(1).get("check"))){
                        String mchId = notifyParams.get(1).get("name").toString();
                        if(!params.get(mchId).toString().equals(channelLabel.getMchId())){
                            continue;
                        }
                    }

                    String signCheck = notifyParams.get(8).get("isCheck").toString();
                    boolean checkResult = true;
                    if(signCheck.equals("0")){
                        //验签
                        try {

                            if(ObjectUtils.isNotEmpty(notifyParams.get(8).get("signType"))){

                                if(notifyParams.get(8).get("signType").equals("1")){
                                    //玖忆
                                    checkResult = SignConfig.checkIsSignValidFromResponse_JY(params, channelLabel.getMchKey());
                                }else if(notifyParams.get(8).get("signType").equals("2")){
                                    //锦鲤
                                    checkResult = SignConfig.checkIsSignValidFromResponse_JL(params, channelLabel.getMchKey());
                                }else if(notifyParams.get(8).get("signType").equals("3")){
                                    //盈通
                                    checkResult = SignConfig.checkIsSignValidFromResponse_YT(params, channelLabel.getMchKey());
                                }else if(notifyParams.get(8).get("signType").equals("4")){
                                    //万嘉
                                    checkResult = SignConfig.checkIsSignValidFromResponse_WJ(params, channelLabel.getMchKey());
                                }else if(notifyParams.get(8).get("signType").equals("5")){
                                    //pag
                                    checkResult = SignConfig.checkIsSignValidFromResponse_PGA(params, channelLabel.getMchKey());
                                }

                            }else{

                                String keyName = "key";
                                if(ObjectUtils.isNotEmpty(notifyParams.get(8).get("keyName"))){
                                    keyName = notifyParams.get(8).get("keyName").toString();
                                }

                                //蓝猫、新mw不在末尾添加key，直接加入集合按顺序生成验签
                                if(notifyParams.get(8).get("isKey").toString().equals("3")){
                                    params.put(notifyParams.get(8).get("keyName").toString(),channelLabel.getMchKey());
                                }

                                checkResult = SignConfig.checkIsSignValidFromResponse(params, channelLabel.getMchKey(),notifyParams.get(8).get("value").toString(),
                                        notifyParams.get(8).get("lowerOrUpper").toString(),notifyParams.get(8).get("isKey").toString(),keyName);
                            }

                        } catch (Exception e) {
                            log.error("===========上游回调数据验签过程异常===================");
                            return "验签失败";
                        }
                    }

                    if(checkResult){

                        //本系统订单号
                        String payOrderNoName = notifyParams.get(3).get("name").toString();
                        String payOrderNo = params.get(payOrderNoName).toString();
                        if (null == payOrderNo || StringUtils.isEmpty(payOrderNo)) {
                            return "fail,缺少商户订单号";
                        }else{
                            Map<String, Object> orderParams = new HashMap<>();
                            orderParams.put("payOrderNo",payOrderNo);
                            OperateOrder operateOrder = operateOrderMapper.selectOrderByParams(orderParams);

                            //如果订单是完成的状态，上游再次回调就忽略
                            if(operateOrder.getPaymentStatus().equals("4")){
                                return "fail";
                            }

                            //状态参数和值(支付成功的值)
                            String statusParam = notifyParams.get(5).get("name").toString();
                            String statusValue = notifyParams.get(5).get("value").toString();
                            //成功返回值
                            String success = notifyParams.get(7).get("value").toString();

                            boolean check = false;
                            //有些上游是支付成功才回调 没有回调的状态(梦想点卷、万嘉)
                            if(ObjectUtils.isNotEmpty(notifyParams.get(5).get("isCheck"))){
                                check = true;
                            }else{
                                //从回调的参数中获取值
                                String status = params.get(statusParam).toString();
                                check = status.equals(statusValue);
                            }

                            if(check){
                                //支付成功
                                operateOrder.setPaymentStatus("2");
                                operateOrder.setChaCallbackDate(DateUtils.getNowDate());
                                operateOrderService.updateOperateOrder(operateOrder);

                                //商户和代理商余额计算
                                Long agentId = operateOrder.getAgentId();
                                if(agentId != null){
                                    OperateAgent operateAgent = operateAgentService.selectOperateAgentById(agentId);
                                    if(operateAgent.getBalance() != null){
                                        operateAgent.setBalance(operateAgent.getBalance().add(operateOrder.getAgentActReceipt()));
                                    }else{
                                        operateAgent.setBalance(operateOrder.getAgentActReceipt());
                                    }
                                    operateAgentService.updateOperateAgent(operateAgent);
                                }

                                OperateMerchant operateMch = operateMerchantService.selectOperateMerchantById(operateOrder.getMchId());
                                if(operateMch.getBalance() != null){
                                    operateMch.setBalance(operateMch.getBalance().add(operateOrder.getMchActReceipt()));
                                }else{
                                    operateMch.setBalance(operateOrder.getMchActReceipt());
                                }
                                operateMerchantService.updateOperateMerchant(operateMch);


                                //通道额度通知

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
                                OperateBillDTO operateBillDTO = new OperateBillDTO();
                                operateBillDTO.setCheckDate(beijingTimeTodayString);
                                operateBillDTO.setChaLabId(channelLabel.getId());
                                List<OperateBillDTO> dataList = operateOrderService.queryChaLabBill(operateBillDTO);
                                //拿到差额配置
                                //飞机发送消息(通道额度警告  小于等于配置的数值就要发送通知)
                                SysRobat robat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                                if(robat.getChaQuotaWarn() != null && !robat.getChaQuotaWarn().equals("")){
                                    if(dataList.get(0).getBalance().compareTo(new BigDecimal(robat.getChaQuotaWarn())) <= 0){
                                        if(robat.getTgmError() != null && !robat.getTgmError().equals("") && redisCache.getCacheObject(channelLabel.getId().toString()) == null){
                                            //24小时内提醒一次
                                            redisCache.setCacheObject(channelLabel.getId().toString(), "通道额度警告", 1440, TimeUnit.MINUTES);

                                            String message =  "注意：通道" + "  " + channelLabel.getChaLabName() + "  " + "差额低于：" + robat.getChaQuotaWarn() + "\n"
                                                    + robat.getWarnAppend();
                                            //发送
                                            execBot.sendMessageToGroup(robat.getTgmError().toString(),message);
                                        }
                                    }

                                }


                                //通知次数最多5次就要关闭线程
                                final int[] counter = {0};
                                int TOTAL_EXECUTIONS = 6;
                                //异步回调通知商户
                                Callable<Boolean> task = () -> {
                                    counter[0]++;
                                    if (counter[0] < TOTAL_EXECUTIONS) {
                                        //生成sign验签
                                        Map<String, Object> signParams = new HashMap<>();
                                        signParams.put("payOrderNo",operateOrder.getPayOrderNo());
                                        signParams.put("mchId",operateOrder.getMchId());
                                        signParams.put("productId",operateOrder.getProductId());
                                        signParams.put("mchOrderNo",operateOrder.getMchOrderNo());
                                        signParams.put("amount",operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue());
                                        signParams.put("status",2);
                                        OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(operateOrder.getMchId());
                                        String sign = SignConfig.getSignNew(signParams,operateMerchant.getMchKey(),"0","0","key");
                                        signParams.put("sign",sign);

                                        log.error("===========回调给商户===发送的参数："+signParams+"===================");

                                        //通知
                                        //请求类型
                                        OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operateOrder.getChaLabId());
                                        List<Map<String, Object>> notifyListObject = JSONArray.parseObject(operateChannelLabel.getNotifyParams(), List.class);
                                        String httpType = notifyListObject.get(9).get("httpType").toString();
                                        String required = notifyListObject.get(9).get("required").toString();
                                        String resJson = "";
                                        if(required.equals("0")){

                                            if(httpType.equals("GET")){

                                                String httpGetParam = SignConfig.getHttpGetParam(signParams);
                                                resJson = HttpUtils.sendGet(operateOrder.getNotifyUrl(),httpGetParam);

                                            }else if(httpType.equals("POST")){

                                                resJson = HttpUtils.sendHttpPost(operateOrder.getNotifyUrl(),signParams);
                                            }
                                        }else{

                                            resJson = HttpUtils.sendHttpPost(operateOrder.getNotifyUrl(),signParams);
                                        }

                                        log.info("===========回调给商户！商户返回的结果"+resJson+"===================");


                                        //生成回调记录
                                        OperateNotify operateNotify = operateNotifyService.selectOperateNotifyByOrderId(operateOrder.getId());
                                        if(operateNotify == null){
                                            operateNotify = new OperateNotify();
                                            operateNotify.setOrderId(operateOrder.getId());
                                            operateNotify.setMchId(operateOrder.getMchId());
                                            operateNotify.setMchName(operateOrder.getMchName());
                                            operateNotify.setMchOrderNo(operateOrder.getMchOrderNo());
                                            operateNotify.setPayOrderNo(operateOrder.getPayOrderNo());
                                            operateNotify.setOrderType("0");
                                            operateNotify.setNotifyUrl(operateOrder.getNotifyUrl());
                                            operateNotify.setNotifyDate(DateUtils.getNowDate());
                                            operateNotify.setNotifyTime(0);
                                            operateNotify.setNotifyStatus("2");
                                            operateNotifyService.insertOperateNotify(operateNotify);
                                        }

                                        //数据返回的结果
                                        if(resJson.equals("success")){

                                            Date NewDate = new Date();
                                            // 计算时间差(耗时)
                                            long diffInMilliseconds = NewDate.getTime() - operateOrder.getCreateTime().getTime();
                                            long diffInSeconds = diffInMilliseconds / 1000;
                                            operateOrder.setPaymentStatus("4");
                                            operateOrder.setElapsedTime(String.valueOf(diffInSeconds));
                                            operateOrderService.updateOperateOrder(operateOrder);

                                            //回调状态修改
                                            Integer notifyTime = operateNotify.getNotifyTime();
                                            operateNotify.setNotifyTime(notifyTime + 1);
                                            operateNotify.setNotifyDate(DateUtils.getNowDate());
                                            operateNotify.setMchMsg(resJson);
                                            operateNotify.setNotifyStatus("0");
                                            operateNotifyService.updateOperateNotify(operateNotify);

                                            // 返回true表示需要再次执行，false表示不再执行
                                            return false;
                                        }else{

                                            //回调失败
                                            if(!operateNotify.getNotifyStatus().equals("0")){
                                                //回调状态修改
                                                Integer notifyTime = operateNotify.getNotifyTime();
                                                operateNotify.setNotifyTime(notifyTime + 1);
                                                operateNotify.setNotifyDate(DateUtils.getNowDate());
                                                operateNotify.setMchMsg(resJson);
                                                operateNotify.setNotifyStatus("1");
                                                operateNotifyService.updateOperateNotify(operateNotify);

                                                //最后一次发原因发出来
                                                if(counter[0] == 5){
                                                    //飞机发送通知(下单上游返回失败)
                                                    SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                                                    if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                                                        String message = "商户号："+ operateOrder.getMchId() + "\n"
                                                                + "商户订单号："+ operateOrder.getMchOrderNo() + "\n" +
                                                                "回调通知商户失败，原因： " + resJson;
                                                        //发送
                                                        execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
                                                    }

                                                }
                                            }



                                            return true;
                                        }

                                    } else {

                                        return false;
                                    }

                                };
                                // 初始延迟1秒，然后每60秒执行一次
                                startAsyncTaskWithResult(task, 60, 60);

                                //这个return是返回给上游
                                return success;

                            }else{
                                //支付失败
                                operateOrder.setPaymentStatus("3");
                                operateOrder.setChaCallbackDate(DateUtils.getNowDate());
                                operateOrderService.updateOperateOrder(operateOrder);

                                //异步回调通知商户
                                //通知次数最多5次就要关闭线程
                                final int[] counter = {0};
                                int TOTAL_EXECUTIONS = 6;
                                Callable<Boolean> task = () -> {
                                    //生成sign验签
                                    counter[0]++;
                                    if (counter[0] < TOTAL_EXECUTIONS) {
                                        Map<String, Object> signParams = new HashMap<>();
                                        signParams.put("payOrderNo",operateOrder.getPayOrderNo());
                                        signParams.put("mchId",operateOrder.getMchId());
                                        signParams.put("productId",operateOrder.getProductId());
                                        signParams.put("mchOrderNo",operateOrder.getMchOrderNo());
                                        signParams.put("amount",operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue());
                                        signParams.put("status",2);
                                        OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(operateOrder.getMchId());
                                        String sign = SignConfig.getSignNew(signParams,operateMerchant.getMchKey(),"0","0","key");
                                        signParams.put("sign",sign);

                                        log.error("===========回调给商户===发送的参数："+signParams+"===================");

                                        //通知
                                        //请求类型
                                        OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operateOrder.getChaLabId());
                                        List<Map<String, Object>> notifyListObject = JSONArray.parseObject(operateChannelLabel.getNotifyParams(), List.class);
                                        String httpType = notifyListObject.get(9).get("httpType").toString();
                                        String required = notifyListObject.get(9).get("required").toString();
                                        String resJson = "";
                                        if(required.equals("0")){

                                            if(httpType.equals("GET")){

                                                String httpGetParam = SignConfig.getHttpGetParam(signParams);
                                                resJson = HttpUtils.sendGet(operateOrder.getNotifyUrl(),httpGetParam);

                                            }else if(httpType.equals("POST")){

                                                resJson = HttpUtils.sendHttpPost(operateOrder.getNotifyUrl(),signParams);
                                            }
                                        }else{

                                            resJson = HttpUtils.sendHttpPost(operateOrder.getNotifyUrl(),signParams);
                                        }

                                        log.info("===========回调给商户！商户返回的结果"+resJson+"===================");


                                        //生成回调记录
                                        OperateNotify operateNotify = operateNotifyService.selectOperateNotifyByOrderId(operateOrder.getId());
                                        if(operateNotify == null){
                                            operateNotify = new OperateNotify();
                                            operateNotify.setOrderId(operateOrder.getId());
                                            operateNotify.setMchId(operateOrder.getMchId());
                                            operateNotify.setMchName(operateOrder.getMchName());
                                            operateNotify.setMchOrderNo(operateOrder.getMchOrderNo());
                                            operateNotify.setPayOrderNo(operateOrder.getPayOrderNo());
                                            operateNotify.setOrderType("0");
                                            operateNotify.setNotifyUrl(operateOrder.getNotifyUrl());
                                            operateNotify.setNotifyDate(DateUtils.getNowDate());
                                            operateNotify.setNotifyTime(0);
                                            operateNotify.setNotifyStatus("2");
                                            operateNotifyService.insertOperateNotify(operateNotify);
                                        }

                                        //数据返回的结果
                                        if(resJson.equals("success")){

                                            //回调状态修改
                                            Integer notifyTime = operateNotify.getNotifyTime();
                                            operateNotify.setNotifyTime(notifyTime + 1);
                                            operateNotify.setNotifyDate(DateUtils.getNowDate());
                                            operateNotify.setMchMsg(resJson);
                                            operateNotify.setNotifyStatus("0");
                                            operateNotifyService.updateOperateNotify(operateNotify);

                                            // 返回true表示需要再次执行，false表示不再执行
                                            return false;


                                        }else{

                                            //回调失败
                                            if(!operateNotify.getNotifyStatus().equals("0")){
                                                //回调状态修改
                                                Integer notifyTime = operateNotify.getNotifyTime();
                                                operateNotify.setNotifyTime(notifyTime + 1);
                                                operateNotify.setNotifyDate(DateUtils.getNowDate());
                                                operateNotify.setMchMsg(resJson);
                                                operateNotify.setNotifyStatus("1");
                                                operateNotifyService.updateOperateNotify(operateNotify);

                                                //最后一次发原因发出来
                                                if(counter[0] == 5){
                                                    //飞机发送通知(下单上游返回失败)
                                                    SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                                                    if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                                                        String message = "商户号："+ operateOrder.getMchId() + "\n"
                                                                + "商户订单号："+ operateOrder.getMchOrderNo() + "\n" +
                                                                "回调通知商户失败，原因： " + resJson;
                                                        //发送
                                                        execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
                                                    }

                                                }
                                            }

                                            return true;

                                        }
                                    }else{

                                        return false;
                                    }

                                };
                                // 初始延迟1秒，然后每60秒执行一次
                                startAsyncTaskWithResult(task, 60, 60);

                                //这个return是返回给上游
                                return success;
                            }

                        }

                    }else{
                        log.error("===========上游回调验签失败，可能数据被篡改!===================");
                        return "验签失败";
                    }
                }
            }

        }else{
            log.error("===========获取回调请求的IP地址错误请联系管理员====================");
            return "fail";
        }
        return "fail";
    }


    /**
     * 查询订单接口
     */
    @PostMapping("/pay/queryOrder")
//    @Log(title = "查询订单")
    public AjaxResult queryOrder( @RequestBody QueryOrderDTO queryOrderDTO)
    {
        if (null == queryOrderDTO.getMchId()) {
            return new AjaxResult("FAIL", "查询失败 商户id不能为空！", null,null,null);
        }
        if (null == queryOrderDTO.getMchOrderNo() || queryOrderDTO.getMchOrderNo().trim().equals("")) {
            return new AjaxResult("FAIL", "查询失败，商户订单号不能为空！", null,null,null);
        }
        if (null == queryOrderDTO.getSign() || queryOrderDTO.getSign().trim().equals("")) {
            return new AjaxResult("FAIL", "查询失败 sign验签不能为空！", null,null,null);
        }

        log.info("=================商户查询订单参数" + queryOrderDTO + "============================");

        //获取key
        OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(queryOrderDTO.getMchId());
        String mchkey = operateMerchant.getMchKey();
        //sign验证
        boolean checkResult;
        try {
            checkResult = SignConfig.checkIsSignValidFromResyponseStringObject(queryOrderDTO, mchkey,"0","0","0","key");
        } catch (Exception e) {
            log.error("===========验签过程异常===================");
            return new AjaxResult("FAIL", "查询失败！", null,null,null);
        }

        if(checkResult == false){
            log.error("===========商户id="+queryOrderDTO.getMchId()+"查询订单验签失败!可能数据被篡改！===================");
            return new AjaxResult("FAIL", "sign验签失败！", null,null,null);
        }else{

            //查询订单
            Map<String, Object> orderParams = new HashMap<>();
            orderParams.put("mchOrderNo",queryOrderDTO.getMchOrderNo());
            OperateOrder operateOrder = operateOrderMapper.selectOrderByParams(orderParams);
            if(operateOrder != null && ObjectUtils.isNotEmpty(operateOrder)){

                if(operateOrder.getPaymentStatus().equals("0")){

                    return new AjaxResult("SUCCESS", "操作成功", operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue(),
                            String.valueOf(operateOrder.getMchId()),String.valueOf(operateOrder.getProductId()),operateOrder.getPayOrderNo(),
                            operateOrder.getMchOrderNo(),0);

                }else if(operateOrder.getPaymentStatus().equals("1")){

                    //支付中的状态比较特殊，有可能上游通道那边会员支付过去了，但是没有回调
                    OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operateOrder.getChaLabId());
                    //从配置信息获取参数
                    Map<String, Object> params = new HashMap<>();

                    if(ObjectUtils.isNotEmpty(operateChannelLabel.getCheckParams())){


                        List<Map<String, Object>> checkParams = JSONArray.parseObject(operateChannelLabel.getCheckParams(), List.class);

                        //商户id(required为0是必填)
                        if(checkParams.get(0).get("required").toString().equals("0")){

                            //数据判断和转换
                            String dataType = checkParams.get(0).get("dataType").toString();
                            Object data = SignConfig.dataTypeConvert(operateChannelLabel.getMchId(), dataType);

                            params.put(checkParams.get(0).get("name").toString(),data);
                        }
                        //商户订单号(required为0是必填)
                        if(checkParams.get(1).get("required").toString().equals("0")){

                            //数据判断和转换
                            String dataType = checkParams.get(1).get("dataType").toString();
                            Object data = SignConfig.dataTypeConvert(operateOrder.getPayOrderNo(), dataType);

                            params.put(checkParams.get(1).get("name").toString(),data);
                        }

                        //随机字符串（确保请求的时效性、防止重放攻击）
                        if(checkParams.get(3).get("required").toString().equals("0")){
                            String length = checkParams.get(3).get("length").toString();
                            //肯定是字符串
                            params.put(checkParams.get(3).get("name").toString(),generateNonce(Integer.valueOf(length)));

                        }

                        //毫秒级时间戳（确保请求的时效性、防止重放攻击）
                        if(checkParams.get(4).get("required").toString().equals("0")){
                            long timestamp = System.currentTimeMillis();

                            if(ObjectUtils.isNotEmpty(checkParams.get(4).get("length"))){
                                if(checkParams.get(4).get("length").equals("10")){
                                    // 转换为秒级时间戳（10位）
                                    timestamp = timestamp / 1000;
                                }
                            }

                            String dataType = checkParams.get(4).get("dataType").toString();
                            Object data = SignConfig.dataTypeConvert(timestamp, dataType);
                            params.put(checkParams.get(4).get("name").toString(),data);
                        }

                        //时间：格式（2016-12-26 18:18:18）
                        if(checkParams.get(5).get("required").toString().equals("0")){
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            // 格式化当前时间
                            String formattedDate = formatter.format(DateUtils.getNowDate());
                            params.put(checkParams.get(5).get("name").toString(),formattedDate);
                        }

                        //接口版本号
                        if(checkParams.size() > 6){

                            if(checkParams.get(6).get("required").toString().equals("0")){
                                params.put(checkParams.get(6).get("name").toString(),checkParams.get(6).get("value").toString());
                            }
                        }

                        //标识参数
                        if(checkParams.size() > 7){

                            if(checkParams.get(7).get("required").toString().equals("0")){
                                params.put(checkParams.get(7).get("name").toString(),checkParams.get(7).get("value").toString());
                            }
                        }

                        //时间：格式（20161226181818）
                        if(checkParams.size() > 8){

                            if(checkParams.get(8).get("required").toString().equals("0")){
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                                // 格式化当前时间
                                String formattedDate = formatter.format(DateUtils.getNowDate());
                                params.put(checkParams.get(8).get("name").toString(),formattedDate);
                            }

                        }


                        //sign验签,value值为0字段空不验证，1字段空也要参与签名
                        try {

                            if(ObjectUtils.isNotEmpty(checkParams.get(2).get("signType"))){

                                String sign = "";
                                if(checkParams.get(2).get("signType").equals("1")){
                                    //玖忆
                                    sign = SignConfig.getSignNew_JY2(params,operateChannelLabel.getMchKey());

                                }else if(checkParams.get(2).get("signType").equals("2")){
                                    //锦鲤
                                    sign = SignConfig.getSignNew_JL2(params,operateChannelLabel.getMchKey());

                                }else if(checkParams.get(2).get("signType").equals("3")){
                                    //盈通
                                    sign = SignConfig.getSignNew_YT2(params,operateChannelLabel.getMchKey());
                                }else if(checkParams.get(2).get("signType").equals("4")){
                                    //万嘉
                                    sign = SignConfig.getSignNew_WJ2(params,operateChannelLabel.getMchKey());
                                }else if(checkParams.get(2).get("signType").equals("5")){
                                    //pag
                                    sign = SignConfig.getSignNew_PGA(params,operateChannelLabel.getMchKey());
                                }

                                params.put(checkParams.get(2).get("name").toString(),sign);

                            }else{
                                String keyName = "key";
                                if(ObjectUtils.isNotEmpty(checkParams.get(2).get("keyName"))){
                                    keyName = checkParams.get(2).get("keyName").toString();
                                }

                                //蓝猫、新mw不在末尾添加key，直接加入集合按顺序生成验签
                                if(checkParams.get(2).get("isKey").toString().equals("3")){
                                    params.put(checkParams.get(2).get("keyName").toString(),operateChannelLabel.getMchKey());
                                }

                                String sign = SignConfig.getSignNew(params, operateChannelLabel.getMchKey(),
                                        checkParams.get(2).get("value").toString(),checkParams.get(2).get("isKey").toString(),keyName);
                                //有些上游sign是小写的
                                if(checkParams.get(2).get("lowerOrUpper").toString().equals("1")){
                                    sign = sign.toLowerCase();
                                }

                                //蓝猫、新mw除掉key
                                if(checkParams.get(2).get("isKey").toString().equals("3")){
                                    params.remove(checkParams.get(2).get("keyName").toString());
                                }

                                params.put(checkParams.get(2).get("name").toString(),sign);

                            }



                        } catch (Exception e) {
                            log.error("===========查询订单验签过程异常===================");
                            return new AjaxResult("FAIL", "查询失败！", null,null,null);
                        }

                        String url = operateChannelLabel.getGateway() + operateChannelLabel.getQueryPort();
                        //请求类型
                        String httpType = checkParams.get(0).get("httpType").toString();
                        String paramType = checkParams.get(0).get("paramType").toString();
                        try {
                            String resJson = "";
                            if(httpType.equals("GET")){

                                String httpGetParam = SignConfig.getHttpGetParam(params);
                                resJson = HttpUtils.sendGet(url,httpGetParam);

                            }else if(httpType.equals("POST")){

                                if(paramType.equals("param")){
                                    //表单
                                    resJson = HttpUtils.sendHttpPostParam(url,params);

                                }else{
                                    //json
                                    resJson = HttpUtils.sendHttpPost(url,params);
                                }


                            }
                            //数据返回的结果
                            Map<String,Object> resJsonMap =  JSONObject.parseObject(resJson);
                            //根据返回的参数配置，拆分获取到的数据
                            List<Map<String, Object>> queryRenParams = JSONArray.parseObject(operateChannelLabel.getQueryRenParams(), List.class);

                            //根据配置的查询返回参数找到数据
                            //code状态参数名
                            String code = queryRenParams.get(0).get("name").toString();
                            //code成功状态的值
                            String codeSuccess = queryRenParams.get(0).get("value").toString();
                            //msg消息参数名
                            String msg = queryRenParams.get(1).get("name").toString();
                            //状态层级判断
                            String checkTier = queryRenParams.get(2).get("value").toString();
                            //第一层参数参数名
                            String LayerOwn = queryRenParams.get(3).get("name").toString();
                            //第二层参数参数名
                            String LayerTwo = queryRenParams.get(4).get("name").toString();
                            //支付成功状态
                            String status1 = queryRenParams.get(7).get("value").toString();
                            //订单完成\补单成功的状态
                            String status2 = queryRenParams.get(8).get("value").toString();
                            //支付中
                            String status3 = queryRenParams.get(9).get("value").toString();

                            //梦想点卷 没有查询结果的状态
                            boolean start = true;
                            if(!ObjectUtils.isNotEmpty(queryRenParams.get(0).get("isCheck"))){
                                start = resJsonMap.get(code).toString().equals(codeSuccess);
                            }

                            //查询成功
                            if(start){

                                //先判断有没有验签(有些上游返回没有验签,0有1无)
                                String signValue = queryRenParams.get(5).get("value").toString();
                                boolean signCheck = true;
                                if(signValue.equals("0")){
                                    //先验签
                                    String chaLablekey = operateChannelLabel.getMchKey();
                                    try {

                                        String keyName = "key";
                                        if(ObjectUtils.isNotEmpty(queryRenParams.get(6).get("keyName"))){
                                            keyName = queryRenParams.get(6).get("keyName").toString();
                                        }

                                        if(ObjectUtils.isNotEmpty(queryRenParams.get(6).get("signParam"))){

                                            String signParam = queryRenParams.get(6).get("signParam").toString();
                                            String data = JSON.toJSONString(resJsonMap.get(signParam));
                                            Map<String,Object> dataMap =  JSONObject.parseObject(data);

                                            //目前“控股”，sign在外面，所以要加进去
                                            //目前“安心”，sign在里面，不用加进去,signLoc为0表示sign在data里面
                                            if(ObjectUtils.isNotEmpty(queryRenParams.get(6).get("signLoc"))){
                                                if(queryRenParams.get(6).get("signLoc").equals("1")){
                                                    dataMap.put(queryRenParams.get(6).get("name").toString(),resJsonMap.get(queryRenParams.get(6).get("name")));
                                                }
                                            }

                                            signCheck = SignConfig.checkIsSignValidFromResponse(dataMap, chaLablekey,queryRenParams.get(6).get("value").toString(),
                                                    queryRenParams.get(6).get("lowerOrUpper").toString(),queryRenParams.get(6).get("isKey").toString(),keyName);

                                        }else{


                                            signCheck = SignConfig.checkIsSignValidFromResponse(resJsonMap, chaLablekey,queryRenParams.get(6).get("value").toString(),
                                                    queryRenParams.get(6).get("lowerOrUpper").toString(),queryRenParams.get(6).get("isKey").toString(),keyName);
                                        }


                                    } catch (Exception e) {
                                        log.error("===========查询订单==上游返回的参数验签过程异常===================");
                                        return new AjaxResult("FAIL", "查询失败！", null,null,null);
                                    }
                                }

                                if(signCheck == false){
                                    log.error("===========查询订单上游返回数据验签失败!可能数据被篡改！===================");
                                    return new AjaxResult("FAIL", "查询失败！", null,null,null);
                                }else{


                                    String status = "";
                                    if(checkTier.equals("1")){
                                        status = resJsonMap.get(LayerOwn).toString();
                                    }else if(checkTier.equals("2")){
                                        String data = JSON.toJSONString(resJsonMap.get(LayerOwn));
                                        Map<String,Object> dataMap =  JSONObject.parseObject(data);
                                        status = dataMap.get(LayerTwo).toString();
                                    }

                                    //支付成功（可能上游回调失败，或者没有回调通知）
                                    if(status.equals(status1) || status.equals(status2)){

//                                        operateOrder.setPaymentStatus("2");
//                                        operateOrder.setChaCallbackDate(DateUtils.getNowDate());
//                                        operateOrderService.updateOperateOrder(operateOrder);

                                        return new AjaxResult("SUCCESS", "操作成功", operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue(),
                                                String.valueOf(operateOrder.getMchId()),String.valueOf(operateOrder.getProductId()),operateOrder.getPayOrderNo(),
                                                operateOrder.getMchOrderNo(),2);

                                        //支付中
                                    }else if(status.equals(status3)){

//                                        operateOrder.setPaymentStatus("1");
//                                        operateOrderService.updateOperateOrder(operateOrder);

                                        return new AjaxResult("SUCCESS", "操作成功", operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue(),
                                                String.valueOf(operateOrder.getMchId()),String.valueOf(operateOrder.getProductId()),operateOrder.getPayOrderNo(),
                                                operateOrder.getMchOrderNo(),1);


                                        //支付失败
                                    }else{

//                                        operateOrder.setPaymentStatus("3");
//                                        operateOrder.setChaCallbackDate(DateUtils.getNowDate());
//                                        operateOrderService.updateOperateOrder(operateOrder);

                                        return new AjaxResult("SUCCESS", "操作成功", operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue(),
                                                String.valueOf(operateOrder.getMchId()),String.valueOf(operateOrder.getProductId()),operateOrder.getPayOrderNo(),
                                                operateOrder.getMchOrderNo(),3);

                                    }

                                }


                            }else{

                                return new AjaxResult("FAIL", "查询失败！", null,null,null);

                            }


                        } catch (Exception e) {

                            log.error("===========查询订单id="+ operateOrder.getId()+"访问上游请求http异常===================");
                            return new AjaxResult("FAIL", "查询失败！", null,null,null);

                        }


                    }else{

                        return new AjaxResult("FAIL", "查询失败！", null,null,null);

                    }



                }else if(operateOrder.getPaymentStatus().equals("2")){

                    return new AjaxResult("SUCCESS", "操作成功", operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue(),
                            String.valueOf(operateOrder.getMchId()),String.valueOf(operateOrder.getProductId()),operateOrder.getPayOrderNo(),
                            operateOrder.getMchOrderNo(),2);

                }else if(operateOrder.getPaymentStatus().equals("3")){

                    return new AjaxResult("SUCCESS", "操作成功", operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue(),
                            String.valueOf(operateOrder.getMchId()),String.valueOf(operateOrder.getProductId()),operateOrder.getPayOrderNo(),
                            operateOrder.getMchOrderNo(),3);


                }else if(operateOrder.getPaymentStatus().equals("4")){

                    return new AjaxResult("SUCCESS", "操作成功", operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue(),
                            String.valueOf(operateOrder.getMchId()),String.valueOf(operateOrder.getProductId()),operateOrder.getPayOrderNo(),
                            operateOrder.getMchOrderNo(),4);
                }
            }

            return new AjaxResult("FAIL", "该订单不存在", null,null,null);

        }

    }


    /**
     * 重发回调通知
     */
    @Log(title = "重发回调通知")
    @GetMapping("/order/notifyAgain")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult notifyAgain(@RequestParam Map<String, Object> params)
    {
        String ids = (String) params.get("ids");
        String[] split = ids.split(",");
        for (String notifyId : split) {
            OperateNotify operateNotify = operateNotifyService.selectOperateNotifyById(Long.valueOf(notifyId));
            OperateOrder operateOrder = operateOrderService.selectOperateOrderById(operateNotify.getOrderId());

            //这边只通知一次
            //异步回调通知商户
            Callable<Boolean> task = () -> {
                //生成sign验签
                Map<String, Object> signParams = new HashMap<>();
                signParams.put("payOrderNo",operateOrder.getPayOrderNo());
                signParams.put("mchId",operateOrder.getMchId());
                signParams.put("productId",operateOrder.getProductId());
                signParams.put("mchOrderNo",operateOrder.getMchOrderNo());
                signParams.put("amount",operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue());
                signParams.put("status",2);
                OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(operateOrder.getMchId());
                String sign = SignConfig.getSignNew(signParams,operateMerchant.getMchKey(),"0","0","key");
                signParams.put("sign",sign);

                log.error("===========重新回调通知商户===发送的参数："+signParams+"===================");

                //通知
                //请求类型
                OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operateOrder.getChaLabId());
                List<Map<String, Object>> notifyListObject = JSONArray.parseObject(operateChannelLabel.getNotifyParams(), List.class);
                String httpType = notifyListObject.get(9).get("httpType").toString();
                String required = notifyListObject.get(9).get("required").toString();
                String resJson = "";
                if(required.equals("0")){

                    if(httpType.equals("GET")){

                        String httpGetParam = SignConfig.getHttpGetParam(signParams);
                        resJson = HttpUtils.sendGet(operateOrder.getNotifyUrl(),httpGetParam);

                    }else if(httpType.equals("POST")){

                        resJson = HttpUtils.sendHttpPost(operateOrder.getNotifyUrl(),signParams);
                    }
                }else{

                    resJson = HttpUtils.sendHttpPost(operateOrder.getNotifyUrl(),signParams);
                }



                log.error("===========重新回调通知商户===商户返回的数据："+resJson+"===================");

                //数据返回的结果
                if(resJson.equals("success")){

                    if(operateOrder.getPaymentStatus().equals("2")){
                        operateOrder.setPaymentStatus("4");
                        operateOrder.setElapsedTime("回调记录：重新回调的订单");
                        operateOrderService.updateOperateOrder(operateOrder);
                    }

                    //回调状态修改
                    Integer notifyTime = operateNotify.getNotifyTime();
                    operateNotify.setNotifyTime(notifyTime + 1);
                    operateNotify.setNotifyDate(DateUtils.getNowDate());
                    operateNotify.setMchMsg(resJson);
                    operateNotify.setNotifyStatus("0");
                    operateNotifyService.updateOperateNotify(operateNotify);

                    OperateMerchant operateMch = operateMerchantService.selectOperateMerchantById(operateOrder.getMchId());
                    if(operateMch.getBalance() != null){
                        operateMch.setBalance(operateMch.getBalance().add(operateOrder.getMchActReceipt()));
                    }else{
                        operateMch.setBalance(operateOrder.getMchActReceipt());
                    }
                    operateMerchantService.updateOperateMerchant(operateMch);


                    // 返回true表示需要再次执行，false表示不再执行
                    return false;

                }else{

                    //回调状态修改
                    Integer notifyTime = operateNotify.getNotifyTime();
                    operateNotify.setNotifyTime(notifyTime + 1);
                    operateNotify.setNotifyDate(DateUtils.getNowDate());
                    operateNotify.setMchMsg(resJson);
                    operateNotify.setNotifyStatus("1");
                    operateNotifyService.updateOperateNotify(operateNotify);


                    //飞机发送通知(重新回调通知失败)
                    SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                    if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                        String message = "商户号："+ operateOrder.getMchId() + "\n"
                                + "商户订单号："+ operateOrder.getMchOrderNo() + "\n" +
                                "手动补单通知商户失败！原因： " + resJson;
                        //发送
                        execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
                    }


                    return false;

                }
            };

            // 每60秒执行一次
            startAsyncTaskWithResult(task, 60, 60);
        }

        return toAjax(1);
    }


    /**
     * 手动补单
     */
    @Log(title = "手动补单")
    @GetMapping("/order/replenishment")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult replenishment(@RequestParam("orderId") Long orderId)
    {
        //支付成功
        OperateOrder operateOrder = operateOrderService.selectOperateOrderById(orderId);
        operateOrder.setPaymentStatus("2");
        operateOrder.setChaCallbackDate(DateUtils.getNowDate());
        operateOrder.setUpdateBy(getUsername());
        operateOrderService.updateOperateOrder(operateOrder);

        //商户和代理商余额计算
        Long agentId = operateOrder.getAgentId();
        if(agentId != null){
            OperateAgent operateAgent = operateAgentService.selectOperateAgentById(agentId);
            if(operateAgent.getBalance() != null){
                operateAgent.setBalance(operateAgent.getBalance().add(operateOrder.getAgentActReceipt()));
            }else{
                operateAgent.setBalance(operateOrder.getAgentActReceipt());
            }
            operateAgentService.updateOperateAgent(operateAgent);
        }

        OperateMerchant operateMch = operateMerchantService.selectOperateMerchantById(operateOrder.getMchId());
        if(operateMch.getBalance() != null){
            operateMch.setBalance(operateMch.getBalance().add(operateOrder.getMchActReceipt()));
        }else{
            operateMch.setBalance(operateOrder.getMchActReceipt());
        }
        operateMerchantService.updateOperateMerchant(operateMch);

        //通知次数最多5次就要关闭线程
        final int[] counter = {0};
        int TOTAL_EXECUTIONS = 6;
        //异步回调通知商户
        Callable<Boolean> task = () -> {
            counter[0]++;
            if (counter[0] < TOTAL_EXECUTIONS) {
                //生成sign验签
                Map<String, Object> signParams = new HashMap<>();
                signParams.put("payOrderNo",operateOrder.getPayOrderNo());
                signParams.put("mchId",operateOrder.getMchId());
                signParams.put("productId",operateOrder.getProductId());
                signParams.put("mchOrderNo",operateOrder.getMchOrderNo());
                signParams.put("amount",operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue());
                signParams.put("status",2);
                OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(operateOrder.getMchId());
                String sign = SignConfig.getSignNew(signParams,operateMerchant.getMchKey(),"0","0","key");
                signParams.put("sign",sign);

                log.error("===========手动补单回调通知商户===发送的参数："+signParams+"===================");
                //通知

                //请求类型
                OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operateOrder.getChaLabId());
                List<Map<String, Object>> notifyListObject = JSONArray.parseObject(operateChannelLabel.getNotifyParams(), List.class);
                String httpType = notifyListObject.get(9).get("httpType").toString();
                String required = notifyListObject.get(9).get("required").toString();
                String resJson = "";
                if(required.equals("0")){

                    if(httpType.equals("GET")){

                        String httpGetParam = SignConfig.getHttpGetParam(signParams);
                        resJson = HttpUtils.sendGet(operateOrder.getNotifyUrl(),httpGetParam);

                    }else if(httpType.equals("POST")){

                        resJson = HttpUtils.sendHttpPost(operateOrder.getNotifyUrl(),signParams);
                    }
                }else{

                     resJson = HttpUtils.sendHttpPost(operateOrder.getNotifyUrl(),signParams);
                }



                log.error("===========手动补单回调通知商户===商户返回的数据："+resJson+"===================");


                //生成回调记录
                OperateNotify operateNotify = operateNotifyService.selectOperateNotifyByOrderId(operateOrder.getId());
                if(operateNotify == null){
                    operateNotify = new OperateNotify();
                    operateNotify.setOrderId(operateOrder.getId());
                    operateNotify.setMchId(operateOrder.getMchId());
                    operateNotify.setMchName(operateOrder.getMchName());
                    operateNotify.setMchOrderNo(operateOrder.getMchOrderNo());
                    operateNotify.setPayOrderNo(operateOrder.getPayOrderNo());
                    operateNotify.setOrderType("0");
                    operateNotify.setNotifyUrl(operateOrder.getNotifyUrl());
                    operateNotify.setNotifyDate(DateUtils.getNowDate());
                    operateNotify.setNotifyTime(0);
                    operateNotify.setNotifyStatus("2");
                    operateNotifyService.insertOperateNotify(operateNotify);
                }

                //数据返回的结果
                if(resJson.equals("success")){

                    operateOrder.setPaymentStatus("4");
                    operateOrder.setElapsedTime("补单成功");
                    operateOrderService.updateOperateOrder(operateOrder);

                    //回调状态修改
                    Integer notifyTime = operateNotify.getNotifyTime();
                    operateNotify.setNotifyTime(notifyTime + 1);
                    operateNotify.setNotifyDate(DateUtils.getNowDate());
                    operateNotify.setMchMsg(resJson);
                    operateNotify.setNotifyStatus("0");
                    operateNotifyService.updateOperateNotify(operateNotify);


                    //补单记录
                    OperateReplenishment operateReplenishment = new OperateReplenishment();
                    operateReplenishment.setOrderId(operateOrder.getId());
                    operateReplenishment.setMchId(operateOrder.getMchId());
                    operateReplenishment.setMchName(operateOrder.getMchName());
                    operateReplenishment.setMchOrderNo(operateOrder.getMchOrderNo());
                    operateReplenishment.setPayOrderNo(operateOrder.getPayOrderNo());
                    operateReplenishment.setChaLabId(operateOrder.getChaLabId());
                    operateReplenishment.setChaLabName(operateOrder.getChaLabName());
                    operateReplenishment.setPayChannelId(operateOrder.getPayChannelId());
                    operateReplenishment.setPayChannelName(operateOrder.getPayChannelName());
                    operateReplenishment.setPayChannelCode(operateOrder.getPayChannelCode());
                    operateReplenishment.setPaymentAmount(operateOrder.getPaymentAmount());
                    operateReplenishmentService.insertOperateReplenishment(operateReplenishment);


                    // 返回true表示需要再次执行，false表示不再执行
                    return false;

                }else{

                    //回调状态修改
                    Integer notifyTime = operateNotify.getNotifyTime();
                    operateNotify.setNotifyDate(DateUtils.getNowDate());
                    operateNotify.setMchMsg(resJson);
                    operateNotify.setNotifyStatus("1");
                    operateNotifyService.updateOperateNotify(operateNotify);

                    //最后一次发原因发出来
                    if(counter[0] == 5){
                        //飞机发送通知(下单上游返回失败)
                        SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                        if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                            String message = "商户号："+ operateOrder.getMchId() + "\n"
                                    + "商户订单号："+ operateOrder.getMchOrderNo() + "\n" +
                                    "手动补单通知商户失败！原因： " + resJson;
                            //发送
                            execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
                        }

                    }


                    return true;

                }

            } else {
//                //回调通知失败
//                //飞机发送通知
//                List<SysRobat> sysRobats = sysRobatService.selectSysRobatAll();
//                for(SysRobat sysRobat : sysRobats){
//                    if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
//                        String message = "商户号："+ operateOrder.getMchId() + "\n"
//                                + "商户订单号："+ operateOrder.getMchOrderNo() + "\n" +
//                                "手动补单通知商户失败！";
//                        //发送
//                        execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
//                        break;
//                    }
//                }

                return false;
            }

        };

        // 每60秒执行一次
        startAsyncTaskWithResult(task, 60, 60);

        return toAjax(1);
    }


    /**
     * 上游回调测试接口
     */
    @RequestMapping(value = "/pay/test/notify", method = {RequestMethod.GET, RequestMethod.POST})
    public Object testNotify(HttpServletRequest request,@RequestParam Map<String, Object> params)
    {
        //获取请求用户的ip地址
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_FORWARDED");
        }

        log.info("=================上游测试回调参数" + params + "============================");
        log.info("=================上游测试回调ip" + ipAddress + "============================");
        //根据通知的ip找到对应的渠道
        if(StringUtils.isNotEmpty(ipAddress) || ipAddress != null){
            List<OperateChannelLabel> operateChannelLabels = operateChannelLabelService.selectOperateChannelLabelList(new OperateChannelLabel());
            for(OperateChannelLabel channelLabel : operateChannelLabels){

                // 如果商户没有固定IP（比如梦想点卷等），则直接通过商户ID进行判断
                String apiId = "";
                if(channelLabel.getInformIp().equals("all")){
                    apiId = params.get("api_id").toString();
                }

                if(channelLabel.getInformIp().contains(ipAddress) ||
                        (apiId != null && apiId != "" && apiId.equals(channelLabel.getMchId()))) {


                    //根据回调的参数配置，拆分获取到的数据
                    List<Map<String, Object>> notifyParams = JSONArray.parseObject(channelLabel.getNotifyParams(), List.class);


                    //大总统和evo的服务器是同一个ip，除了校验ip还要校验商户id
                    if(ObjectUtils.isNotEmpty(notifyParams.get(1).get("check"))){
                        String mchId = notifyParams.get(1).get("name").toString();
                        if(!params.get(mchId).toString().equals(channelLabel.getMchId())){
                            continue;
                        }
                    }


                    String signCheck = notifyParams.get(8).get("isCheck").toString();
                    boolean checkResult = true;
                    if (signCheck.equals("0")) {
                        //验签
                        try {

                            if (ObjectUtils.isNotEmpty(notifyParams.get(8).get("signType"))) {

                                if (notifyParams.get(8).get("signType").equals("1")) {
                                    //玖忆
                                    checkResult = SignConfig.checkIsSignValidFromResponse_JY(params, channelLabel.getMchKey());
                                }else if(notifyParams.get(8).get("signType").equals("2")){
                                    //锦鲤
                                    checkResult = SignConfig.checkIsSignValidFromResponse_JL(params, channelLabel.getMchKey());
                                } else if (notifyParams.get(8).get("signType").equals("3")) {
                                    //盈通
                                    checkResult = SignConfig.checkIsSignValidFromResponse_YT(params, channelLabel.getMchKey());
                                }else if (notifyParams.get(8).get("signType").equals("4")) {
                                    //万嘉
                                    checkResult = SignConfig.checkIsSignValidFromResponse_WJ(params, channelLabel.getMchKey());
                                }else if (notifyParams.get(8).get("signType").equals("5")) {
                                    //pag
                                    checkResult = SignConfig.checkIsSignValidFromResponse_PGA(params, channelLabel.getMchKey());
                                }

                            } else {

                                String keyName = "key";
                                if (ObjectUtils.isNotEmpty(notifyParams.get(8).get("keyName"))) {
                                    keyName = notifyParams.get(8).get("keyName").toString();
                                }

                                //蓝猫、新mw不在末尾添加key，直接加入集合按顺序生成验签
                                if (notifyParams.get(8).get("isKey").toString().equals("3")) {
                                    params.put(notifyParams.get(8).get("keyName").toString(), channelLabel.getMchKey());
                                }

                                checkResult = SignConfig.checkIsSignValidFromResponse(params, channelLabel.getMchKey(), notifyParams.get(8).get("value").toString(),
                                        notifyParams.get(8).get("lowerOrUpper").toString(), notifyParams.get(8).get("isKey").toString(), keyName);
                            }

                        } catch (Exception e) {
                            log.error("===========上游回调数据验签过程异常===================");
                            return "验签失败";
                        }
                    }

                    if (checkResult) {

                        //本系统订单号
                        String payOrderNoName = notifyParams.get(3).get("name").toString();
                        String payOrderNo = params.get(payOrderNoName).toString();
                        if (null == payOrderNo || StringUtils.isEmpty(payOrderNo)) {
                            return "fail,缺少商户订单号";
                        } else {
                            Map<String, Object> orderParams = new HashMap<>();
                            orderParams.put("payOrderNo", payOrderNo);
                            OperateOrder operateOrder = operateOrderMapper.selectOrderByParams(orderParams);
                            //状态参数和值(支付成功的值)
                            String statusParam = notifyParams.get(5).get("name").toString();
                            String statusValue = notifyParams.get(5).get("value").toString();
                            //成功返回值
                            String success = notifyParams.get(7).get("value").toString();

                            boolean check = false;
                            //有些上游是支付成功才回调 没有回调的状态(梦想点卷、万嘉)
                            if(ObjectUtils.isNotEmpty(notifyParams.get(5).get("isCheck"))){
                                check = true;
                            }else{
                                //从回调的参数中获取值
                                String status = params.get(statusParam).toString();
                                check = status.equals(statusValue);
                            }

                            if(check){
                                //支付成功
                                operateOrder.setPaymentStatus("2");
                                operateOrder.setChaCallbackDate(DateUtils.getNowDate());
                                operateOrderService.updateOperateOrder(operateOrder);

                                return success;

                            } else {
                                //支付失败
                                operateOrder.setPaymentStatus("3");
                                operateOrder.setChaCallbackDate(DateUtils.getNowDate());
                                operateOrderService.updateOperateOrder(operateOrder);

                                return success;
                            }

                        }


                    } else {
                        log.error("===========上游测试回调验签失败，可能数据被篡改!===================");
                        return "验签失败";
                    }
                }
            }

        }else{
            log.error("===========上游测试回调获取请求的IP地址错误请联系管理员====================");
            return "fail";
        }
        return "fail";
    }

    /**
     * 上游回调测试接口
     */
    @RequestMapping(value = "/pay/body/test/notify", method = {RequestMethod.GET, RequestMethod.POST})
    public Object bodyTestNotify(HttpServletRequest request,@RequestBody Map<String, Object> params)
    {
        //获取请求用户的ip地址
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_FORWARDED");
        }

        log.info("=================上游测试回调参数" + params + "============================");
        log.info("=================上游测试回调ip" + ipAddress + "============================");
        //根据通知的ip找到对应的渠道
        if(StringUtils.isNotEmpty(ipAddress) || ipAddress != null){
            List<OperateChannelLabel> operateChannelLabels = operateChannelLabelService.selectOperateChannelLabelList(new OperateChannelLabel());
            for(OperateChannelLabel channelLabel : operateChannelLabels){

                // 如果商户没有固定IP（比如梦想点卷等），则直接通过商户ID进行判断
                String apiId = "";
                if(channelLabel.getInformIp().equals("all")){
                    apiId = params.get("api_id").toString();
                }

                if(channelLabel.getInformIp().contains(ipAddress) ||
                        (apiId != null && apiId != "" && apiId.equals(channelLabel.getMchId()))) {

                    //根据回调的参数配置，拆分获取到的数据
                    List<Map<String, Object>> notifyParams = JSONArray.parseObject(channelLabel.getNotifyParams(), List.class);

                    //大总统和evo的服务器是同一个ip，除了校验ip还要校验商户id
                    if(ObjectUtils.isNotEmpty(notifyParams.get(1).get("check"))){
                        String mchId = notifyParams.get(1).get("name").toString();
                        if(!params.get(mchId).toString().equals(channelLabel.getMchId())){
                            continue;
                        }
                    }

                    String signCheck = notifyParams.get(8).get("isCheck").toString();
                    boolean checkResult = true;
                    if (signCheck.equals("0")) {
                        //验签
                        try {

                            if (ObjectUtils.isNotEmpty(notifyParams.get(8).get("signType"))) {

                                if (notifyParams.get(8).get("signType").equals("1")) {
                                    //玖忆
                                    checkResult = SignConfig.checkIsSignValidFromResponse_JY(params, channelLabel.getMchKey());
                                } else if(notifyParams.get(8).get("signType").equals("2")){
                                    //锦鲤
                                    checkResult = SignConfig.checkIsSignValidFromResponse_JL(params, channelLabel.getMchKey());
                                } else if (notifyParams.get(8).get("signType").equals("3")) {
                                    //盈通
                                    checkResult = SignConfig.checkIsSignValidFromResponse_YT(params, channelLabel.getMchKey());
                                }else if (notifyParams.get(8).get("signType").equals("4")) {
                                    //万嘉
                                    checkResult = SignConfig.checkIsSignValidFromResponse_WJ(params, channelLabel.getMchKey());
                                }else if (notifyParams.get(8).get("signType").equals("5")) {
                                    //pag
                                    checkResult = SignConfig.checkIsSignValidFromResponse_PGA(params, channelLabel.getMchKey());
                                }

                            } else {

                                String keyName = "key";
                                if (ObjectUtils.isNotEmpty(notifyParams.get(8).get("keyName"))) {
                                    keyName = notifyParams.get(8).get("keyName").toString();
                                }

                                //蓝猫、新mw不在末尾添加key，直接加入集合按顺序生成验签
                                if (notifyParams.get(8).get("isKey").toString().equals("3")) {
                                    params.put(notifyParams.get(8).get("keyName").toString(), channelLabel.getMchKey());
                                }

                                checkResult = SignConfig.checkIsSignValidFromResponse(params, channelLabel.getMchKey(), notifyParams.get(8).get("value").toString(),
                                        notifyParams.get(8).get("lowerOrUpper").toString(), notifyParams.get(8).get("isKey").toString(), keyName);
                            }

                        } catch (Exception e) {
                            log.error("===========上游回调数据验签过程异常===================");
                            return "验签失败";
                        }
                    }

                    if (checkResult) {

                        //本系统订单号
                        String payOrderNoName = notifyParams.get(3).get("name").toString();
                        String payOrderNo = params.get(payOrderNoName).toString();
                        if (null == payOrderNo || StringUtils.isEmpty(payOrderNo)) {
                            return "fail,缺少商户订单号";
                        } else {
                            Map<String, Object> orderParams = new HashMap<>();
                            orderParams.put("payOrderNo", payOrderNo);
                            OperateOrder operateOrder = operateOrderMapper.selectOrderByParams(orderParams);
                            //状态参数和值(支付成功的值)
                            String statusParam = notifyParams.get(5).get("name").toString();
                            String statusValue = notifyParams.get(5).get("value").toString();
                            //成功返回值
                            String success = notifyParams.get(7).get("value").toString();

                            boolean check = false;
                            //有些上游是支付成功才回调 没有回调的状态(梦想点卷、万嘉)
                            if(ObjectUtils.isNotEmpty(notifyParams.get(5).get("isCheck"))){
                                check = true;
                            }else{
                                //从回调的参数中获取值
                                String status = params.get(statusParam).toString();
                                check = status.equals(statusValue);
                            }

                            if(check){
                                //支付成功
                                operateOrder.setPaymentStatus("2");
                                operateOrder.setChaCallbackDate(DateUtils.getNowDate());
                                operateOrderService.updateOperateOrder(operateOrder);

                                //这个return是返回给上游
                                return success;

                            } else {
                                //支付失败
                                operateOrder.setPaymentStatus("3");
                                operateOrder.setChaCallbackDate(DateUtils.getNowDate());
                                operateOrderService.updateOperateOrder(operateOrder);
                                //这个return是返回给上游
                                return success;
                            }

                        }


                    } else {
                        log.error("===========上游测试回调验签失败，可能数据被篡改!===================");
                        return "验签失败";
                    }
                }
            }

        }else{
            log.error("===========上游测试回调获取请求的IP地址错误请联系管理员====================");
            return "fail";
        }
        return "fail";
    }

    /**
     * 上游下单测试接口
     */
    @GetMapping("/pay/testCreatOrder")
    @Log(title = "上游通道测试", businessType = BusinessType.OTHER)
    public AjaxResult testCreatOrder(@RequestParam Map<String, Object> dataParams){

        String payChaId = dataParams.get("payChaId").toString();
        String money = dataParams.get("testAmount").toString();
        //获取上游和通道
        OperatePayChannel operatePayChannel = operatePayChannelService.selectOperatePayChannelById(Long.valueOf(payChaId));
        OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operatePayChannel.getChaLabId());

        AjaxResult ajax = AjaxResult.success();

        //风控匹配
        OperateRiskControl operateRiskControl = operateRiskControlService.selectOperateRiskControlById(operatePayChannel.getRiskControlId());
        if(ObjectUtils.isNotEmpty(operateRiskControl) && operateRiskControl != null){
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

            //查询该通道当天的交易总金额
            OperateOrderDTO operateOrderDTO = new OperateOrderDTO();
            operateOrderDTO.setPayChannelId(operatePayChannel.getId());
            operateOrderDTO.setToDayDate(beijingTimeTodayString);
            List<OperateOrder> dataList = operateOrderService.selectOperateOrderList(operateOrderDTO);
            //订单完成和支付成功的状态下
            List<OperateOrder> havePaid = dataList.stream().filter(item -> item.getPaymentStatus().equals("2") ||
                    item.getPaymentStatus().equals("4")).collect(Collectors.toList());
            BigDecimal totalAmount = havePaid.stream().map(OperateOrder::getPaymentAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal amount = new BigDecimal(money);

            //风控配置的判断
            if(operateRiskControl.getTotalAmount().compareTo(totalAmount.add(amount)) >= 0){
                //金额区间
                if(operateRiskControl.getMaxAmount().compareTo(amount) >= 0
                        && operateRiskControl.getMinAmount().compareTo(amount) <= 0){

                    List<String> fixedAmountList = new ArrayList<>();
                    if(operateRiskControl.getFixedAmount() != null && StringUtils.isNotEmpty(operateRiskControl.getFixedAmount())) {
                        //根据类型过滤
                        String COMMA = ",|，";
                        // 将逗号替换为常量表示的逗号
                        String replaced = operateRiskControl.getFixedAmount().replaceAll(COMMA, ",");
                        String[] fixedAmount = replaced.split(COMMA);

                        for (String fruit : fixedAmount) {
                            // 使用trim去除可能存在的空格
                            fixedAmountList.add(fruit.trim());
                        }
                    }

                    if(operateRiskControl.getAmountType().equals("0")){
                        //任意连续（排除金额）
                        if(fixedAmountList.size() > 0 && fixedAmountList.stream().anyMatch(item -> new BigDecimal(item).compareTo(amount) == 0)){

                            //飞机发送通知
//                            SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
//                            if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
//                                String message = "金额：" + amount + "\n" +
//                                        "上游：" + operateChannelLabel.getChaLabName() + "  " + "通道编码：" + operatePayChannel.getChaCode() + "\n" +
//                                        "支付金额为排除金额=======交易关闭";
//                                //发送
//                                execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
//                            }


                            log.error("=============支付通道id="+operatePayChannel.getId()+"=====测试符合风控的排除金额======跳过=========");
                            ajax.put("result", "排除金额!");
                            return success(ajax);

                        }else {

                            AjaxResult result = testPay(operatePayChannel,operateChannelLabel,amount);
                            return result;
                        }

                    }else if(operateRiskControl.getAmountType().equals("1")){

                        //固定金额（固定金额）
                        String strAmount = String.valueOf(amount);
                        if(fixedAmountList.size() > 0 && fixedAmountList.stream().anyMatch(item -> new BigDecimal(item).compareTo(amount) == 0)){

                            AjaxResult result = testPay(operatePayChannel,operateChannelLabel,amount);
                            return result;

                        }else{

                            //飞机发送通知
//                            SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
//                            if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
//                                String message = "金额：" + amount + "\n" +
//                                        "上游：" + operateChannelLabel.getChaLabName() + "  " + "通道编码：" + operatePayChannel.getChaCode() + "\n" +
//                                        "不符合风控的固定金额=======交易关闭";
//                                //发送
//                                execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
//                            }

                            log.error("=========支付通道id="+operatePayChannel.getId()+"====测试不符合风控的固定金额======跳过=========");

                            ajax.put("result", "不符合风控的固定金额!");
                            return success(ajax);

                        }
                    }

                }else{

                    //飞机发送通知
//                    SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
//                    if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
//                        String message = "金额：" + amount + "\n" +
//                                "上游：" + operateChannelLabel.getChaLabName() + "  " + "通道编码：" + operatePayChannel.getChaCode() + "\n" +
//                                "支付金额不在风控的金额范围区间=======交易关闭";
//                        //发送
//                        execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
//                    }


                    log.error("=========支付通道id="+operatePayChannel.getId()+"====测试支付金额不在风控的金额范围区间======跳过=========");

                    ajax.put("result", "支付金额不在风控的金额范围区间!");
                    return success(ajax);

                }
            }else{

                //飞机发送通知
                SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                    String message =  "上游：" + operateChannelLabel.getChaLabName() + "  " + "通道编码：" + operatePayChannel.getChaCode() + "\n" +
                            "支付金额超过通道当天最大交易量=======交易关闭";
                    //发送
                    execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
                }


                log.error("===========支付通道id="+operatePayChannel.getId()+"===测试超过该支付通道当天最大交易量======跳过=========");

                ajax.put("result", "支付金额超过通道当天最大交易量!");
                return success(ajax);

            }
        }else{

            //飞机发送通知
            SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
            if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                String message =  "上游：" + operateChannelLabel.getChaLabName() + "  " + "通道编码：" + operatePayChannel.getChaCode() + "\n" +
                        "风控没有配置=======交易关闭";
                //发送
                execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
            }


            log.error("===========支付通道id="+operatePayChannel.getId()+"===测试风控没有配置======跳过=========");

            ajax.put("result", "风控没有配置!");
            return success(ajax);

        }

        ajax.put("result", "测试下单异常");
        return success(ajax);
    }

    public AjaxResult testPay(OperatePayChannel operatePayChannel,OperateChannelLabel operateChannelLabel,BigDecimal amount){

        AjaxResult ajax = AjaxResult.success();
        //生成测试订单
        SysUser sysUser = sysUserService.selectUserByName("mchTest");
        OperateMerchant operateMerchant = new OperateMerchant();
        if(sysUser != null){
            operateMerchant = operateMerchantService.queryOperateMerchantByUserId(sysUser.getUserId());
        }
        //固定产品为1000
        OperatePayProduct operatePayProduct = operatePayProductService.selectOperatePayProductById(Long.valueOf("1000"));
        OperateOrder operateOrder = new OperateOrder();
        operateOrder.setNotifyUrl("https://www.baidu.com/");
        operateOrder.setReturnUrl(null);
        operateOrder.setMchId(operateMerchant.getId());
        operateOrder.setMchName(operateMerchant.getMchName());
        operateOrder.setMchRate(new BigDecimal(0));
        operateOrder.setProductId(Long.valueOf("1000"));
        operateOrder.setProductName(operatePayProduct.getProName());
        operateOrder.setChaLabId(operateChannelLabel.getId());
        operateOrder.setChaLabName(operateChannelLabel.getChaLabName());
        operateOrder.setPayChannelId(operatePayChannel.getId());
        operateOrder.setPayChannelName(operatePayChannel.getChaName());
        operateOrder.setPayChannelCode(operatePayChannel.getChaCode());
        operateOrder.setMchOrderNo(generateBillNoNumber("test"));
        operateOrder.setPayOrderNo(generateBillNoNumber("CFT"));
        operateOrder.setLabelRate(operatePayChannel.getChaRate());
        operateOrder.setPaymentStatus("0");
        operateOrder.setPaymentAmount(amount);
        //平台收入
        BigDecimal platRate = operatePayChannel.getChaRate().divide(new BigDecimal(100));
        BigDecimal platDeduct = amount.multiply(platRate);
        BigDecimal platActReceipt = amount.subtract(platDeduct);
        operateOrder.setPlatActReceipt(platActReceipt.setScale(2,BigDecimal.ROUND_HALF_UP));
        //渠道成本
        operateOrder.setPayChaCost(platDeduct.setScale(2,BigDecimal.ROUND_HALF_UP));

        //先找到
        Map<String, Object> mchProParams = new HashMap<>();
        mchProParams.put("productId",Long.valueOf("1000"));
        mchProParams.put("mchId",operateMerchant.getId());
        List<OperateMerchantProdect> operateMerchantProdects = operateMerchantProdectService.queryMchProList(mchProParams);
        //商户收益
        BigDecimal mchDeduct;
        if(operateMerchantProdects.get(0).getPriority().equals("1")){
            operateOrder.setMchRate(operateMerchantProdects.get(0).getRate());
            //商户实际收入
            BigDecimal mchRate = operateMerchantProdects.get(0).getRate().divide(new BigDecimal(100));
            mchDeduct = amount.multiply(mchRate);
            BigDecimal mchActReceipt = amount.subtract(mchDeduct);
            operateOrder.setMchActReceipt(mchActReceipt.setScale(2,BigDecimal.ROUND_HALF_UP));
        }else{
            operateOrder.setMchRate(operatePayProduct.getMchRate());
            //商户实际收入
            BigDecimal mchRate = operatePayProduct.getMchRate().divide(new BigDecimal(100));
            mchDeduct = amount.multiply(mchRate);
            BigDecimal mchActReceipt = amount.subtract(mchDeduct);
            operateOrder.setMchActReceipt(mchActReceipt.setScale(2,BigDecimal.ROUND_HALF_UP));
        }
        if(operateMerchant.getAgentId() != null){
            //根据产品/商户查询关联
            Map<String, Object> agentParams = new HashMap<>();
            agentParams.put("productId",Long.valueOf("1000"));
            agentParams.put("agentId",operateMerchant.getAgentId());
            List<OperateAgentProduct> agentProduct = operateAgentProductMapper.selectAgePro(agentParams);
            if(agentProduct.size() > 0){
                operateOrder.setAgentId(operateMerchant.getAgentId());
                operateOrder.setAgentRate(agentProduct.get(0).getRate());
                //代理收入
                BigDecimal agentRate = agentProduct.get(0).getRate().divide(new BigDecimal(100));
                BigDecimal agentDeduct = amount.multiply(agentRate);
                operateOrder.setAgentActReceipt(agentDeduct.setScale(2,BigDecimal.ROUND_HALF_UP));
                //利润
                BigDecimal profit = mchDeduct.subtract(operateOrder.getPayChaCost().add(operateOrder.getAgentActReceipt()));
                operateOrder.setProfit(profit.setScale(2,BigDecimal.ROUND_HALF_UP));
            }

        }else{
            //利润
            BigDecimal profit = mchDeduct.subtract(operateOrder.getPayChaCost());
            operateOrder.setProfit(profit.setScale(2,BigDecimal.ROUND_HALF_UP));
        }


        //自己的测试通道
        if(operateChannelLabel.getChaLabCode().equals("testLabel")){
            operateOrder.setPaymentStatus("1");
            operateOrderService.insertOperateOrder(operateOrder);
            if(operatePayChannel.getChaType().equals("1")){
                ajax.put("payUrl", PAY_TEST_URL);
            }else if(operatePayChannel.getChaType().equals("2")){
                ajax.put("payUrl", PAY_ABLOWDOWN_URL);
            }

            ajax.put("result", "");
            return success(ajax);
        }


        //上游请求参数
        Map<String, Object> params = new HashMap<>();
        List<Map<String, Object>> configListObject = JSONArray.parseObject(operateChannelLabel.getConfigDescribe(), List.class);

        //基本配置
        //商户id(required为0是必填)
        if(configListObject.get(0).get("required").toString().equals("0")){

            //数据判断和转换
            String dataType = configListObject.get(0).get("dataType").toString();
            Object data = SignConfig.dataTypeConvert(operateChannelLabel.getMchId(), dataType);

            params.put(configListObject.get(0).get("name").toString(),data);
        }

        //产品id,或者是上游编码(required为0是必填)
        if(configListObject.get(1).get("required").toString().equals("0")){

            //梦想点卷的通道不参与签名 不参与签名
            if(ObjectUtils.isNotEmpty(configListObject.get(1).get("isSign"))){

            }else{

                //数据判断和转换
                String dataType = configListObject.get(1).get("dataType").toString();
                Object data = SignConfig.dataTypeConvert(operatePayChannel.getChaCode(), dataType);

                params.put(configListObject.get(1).get("name").toString(),data);
            }


        }

        //订单号(required为0是必填)
        if(configListObject.get(2).get("required").toString().equals("0")){

            //数据判断和转换
            String dataType = configListObject.get(2).get("dataType").toString();
            Object data = SignConfig.dataTypeConvert(operateOrder.getPayOrderNo(), dataType);

            params.put(configListObject.get(2).get("name").toString(),data);
        }

        //支付金额(required为0是必填,value为0单位要换算成分)
        if(configListObject.get(3).get("required").toString().equals("0")){
            if(!configListObject.get(3).get("value").toString().equals("0")){

                //数据判断和转换
                String dataType = configListObject.get(3).get("dataType").toString();
                //化为整数
                BigDecimal newAmount = amount.setScale(0, RoundingMode.HALF_UP);
                Object data = SignConfig.dataTypeConvert(newAmount, dataType);

                params.put(configListObject.get(3).get("name").toString(),data);
            }else{

                //数据判断和转换
                String dataType = configListObject.get(3).get("dataType").toString();
                //化为整数
                BigDecimal newAmount = amount.multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP);
                Object data = SignConfig.dataTypeConvert(newAmount, dataType);

                params.put(configListObject.get(3).get("name").toString(),data);
            }

        }

        //notifyUrl(required为0是必填)、sign加密前url不需要URLEncoder
        if(configListObject.get(4).get("required").toString().equals("0")){
            //上游回调的请求方式确认
            String paramType = configListObject.get(7).get("paramType").toString();
            if(paramType.equals("param")){

                params.put(configListObject.get(4).get("name").toString(),NOTIFU_TEST_URL);

            }else if(paramType.equals("json")){

                params.put(configListObject.get(4).get("name").toString(),NOTIFU_TEST_BODY_URL);

            }

        }

        //returnUrl(required为0是必填)、sign加密前url不需要URLEncoder
        if(configListObject.get(5).get("required").toString().equals("0")){

            //玖忆的returnurl 不参与签名
            if(ObjectUtils.isNotEmpty(configListObject.get(5).get("isSign"))){

            }else{
                //fufu 必填（百度的地址）
                if(ObjectUtils.isNotEmpty(configListObject.get(5).get("value"))){
                    params.put(configListObject.get(5).get("name").toString(),configListObject.get(5).get("value").toString());
                }else{
                    params.put(configListObject.get(5).get("name").toString(),"");

                }
            }


        }

        //随机字符串（确保请求的时效性、防止重放攻击）
        if(configListObject.get(9).get("required").toString().equals("0")){
            String length = configListObject.get(9).get("length").toString();
            //肯定是字符串
            params.put(configListObject.get(9).get("name").toString(),generateNonce(Integer.valueOf(length)));
        }

        //毫秒级时间戳（确保请求的时效性、防止重放攻击）
        if(configListObject.get(10).get("required").toString().equals("0")){
            long timestamp = System.currentTimeMillis();

            if(ObjectUtils.isNotEmpty(configListObject.get(10).get("length"))){
                if(configListObject.get(10).get("length").equals("10")){
                    // 转换为秒级时间戳（10位）
                    timestamp = timestamp / 1000;
                }
            }

            String dataType = configListObject.get(10).get("dataType").toString();
            Object data = SignConfig.dataTypeConvert(timestamp, dataType);
            params.put(configListObject.get(10).get("name").toString(),data);
        }

        //时间：格式（2016-12-26 18:18:18）
        if(configListObject.get(11).get("required").toString().equals("0")){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 格式化当前时间
            String formattedDate = formatter.format(DateUtils.getNowDate());
            params.put(configListObject.get(11).get("name").toString(),formattedDate);
        }

        //时间：格式（20161226181818）
        if(configListObject.size() > 12){

            if(configListObject.get(12).get("required").toString().equals("0")){
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                // 格式化当前时间
                String formattedDate = formatter.format(DateUtils.getNowDate());
                params.put(configListObject.get(12).get("name").toString(),formattedDate);
            }

        }


        //上游不一样，有些参数需要额外的配置
        if(StringUtils.isNotEmpty(operateChannelLabel.getBuyParams()) && operateChannelLabel.getBuyParams() != null){
            List<Map<String, Object>> buyListObject = JSONArray.parseObject(operateChannelLabel.getBuyParams(), List.class);
            for (Map<String, Object> mapList : buyListObject) {
                if (mapList.get("required").toString().equals("0")) {

                    if(ObjectUtils.isNotEmpty(mapList.get("isSign"))){

                    }else{
                        //数据判断和转换
                        String dataType = mapList.get("dataType").toString();
                        Object data = SignConfig.dataTypeConvert(mapList.get("value"), dataType);

                        params.put(mapList.get("name").toString(),data);
                    }

                }

                //ip
                if(mapList.get("required").toString().equals("2")){
                    if(ObjectUtils.isNotEmpty(mapList.get("isSign"))){

                    }else{
                        params.put(mapList.get("name").toString(),ip);
                    }

                }

                //产品名称
                if(mapList.get("required").toString().equals("3")){
                    if(ObjectUtils.isNotEmpty(mapList.get("isSign"))){

                    }else{
                        params.put(mapList.get("name").toString(),operateOrder.getPayChannelName());
                    }

                }
            }
        }

        //sign验签,value值为0字段空不验证，1字段空也要参与签名
        try {
            if(ObjectUtils.isNotEmpty(configListObject.get(6).get("signType"))){
                String sign = "";

                if(configListObject.get(6).get("signType").equals("1")){
                    //玖忆
                    sign = SignConfig.getSignNew_JY1(params,operateChannelLabel.getMchKey());
                }else if(configListObject.get(6).get("signType").equals("2")){
                    //锦鲤
                    sign = SignConfig.getSignNew_JL1(params,operateChannelLabel.getMchKey());
                }else if(configListObject.get(6).get("signType").equals("3")){
                    //盈通
                    sign = SignConfig.getSignNew_YT1(params,operateChannelLabel.getMchKey());

                }else if(configListObject.get(6).get("signType").equals("4")){
                    //万嘉
                    sign = SignConfig.getSignNew_WJ1(params,operateChannelLabel.getMchKey());

                }else if(configListObject.get(6).get("signType").equals("5")){
                    //pag
                    sign = SignConfig.getSignNew_PGA(params,operateChannelLabel.getMchKey());

                }


                params.put(configListObject.get(6).get("name").toString(),sign);

            }else{
                String keyName = "key";
                if(ObjectUtils.isNotEmpty(configListObject.get(8).get("keyName"))){
                    keyName = configListObject.get(8).get("keyName").toString();
                }

                //蓝猫、新mw不在末尾添加key，直接加入集合按顺序生成验签
                if(configListObject.get(8).get("isKey").toString().equals("3")){
                    params.put(configListObject.get(8).get("keyName").toString(),operateChannelLabel.getMchKey());
                }

                String sign = SignConfig.getSignNew(params, operateChannelLabel.getMchKey(),
                        configListObject.get(6).get("value").toString(),configListObject.get(8).get("isKey").toString(),keyName);
                //有些上游sign是小写的
                if(configListObject.get(8).get("value").toString().equals("1")){
                    sign = sign.toLowerCase();
                }

                //蓝猫、新mw除掉key
                if(configListObject.get(8).get("isKey").toString().equals("3")){
                    params.remove(configListObject.get(8).get("keyName").toString());
                }

                params.put(configListObject.get(6).get("name").toString(),sign);
            }

        } catch (Exception e) {
            log.error("===========验签过程异常===================");
            ajax.put("result", "验签过程异常");
            return success(ajax);
        }


        //然后再重新编码
        //支付结果后台回调URL(required为0是必填,URLEncoder为0表示url需要编码)
        if(configListObject.get(4).get("required").toString().equals("0")){
            if(configListObject.get(4).get("URLEncoder").toString().equals("0")){
                try {
                    String notifyUrl = URLEncoder.encode(NOTIFU_TEST_URL, "UTF-8");
                    params.put(configListObject.get(4).get("name").toString(),notifyUrl);
                } catch (UnsupportedEncodingException e) {
//                    throw new RuntimeException(e);
                }
            }
        }

        //玖忆的returnurl 不参与签名，签名结束后补上
        if(ObjectUtils.isNotEmpty(configListObject.get(5).get("isSign"))){
            //fufu 必填（百度的地址）
            if(ObjectUtils.isNotEmpty(configListObject.get(5).get("value"))){
                params.put(configListObject.get(5).get("name").toString(),configListObject.get(5).get("value").toString());
            }else{
                params.put(configListObject.get(5).get("name").toString(),"");

            }
        }

        //梦想点卷的通道编码 不参与签名，签名结束后补上
        if(ObjectUtils.isNotEmpty(configListObject.get(1).get("isSign"))){

            //数据判断和转换
            String dataType = configListObject.get(1).get("dataType").toString();
            Object data = SignConfig.dataTypeConvert(operatePayChannel.getChaCode(), dataType);

            params.put(configListObject.get(1).get("name").toString(),data);
        }

        //梦想点卷的ip不参与签名、冷锋等
        if(StringUtils.isNotEmpty(operateChannelLabel.getBuyParams()) && operateChannelLabel.getBuyParams() != null){
            List<Map<String, Object>> buyListObject = JSONArray.parseObject(operateChannelLabel.getBuyParams(), List.class);
            for (Map<String, Object> mapList : buyListObject) {
                if (mapList.get("required").toString().equals("0")) {

                    if(ObjectUtils.isNotEmpty(mapList.get("isSign"))){
                        //数据判断和转换
                        String dataType = mapList.get("dataType").toString();
                        Object data = SignConfig.dataTypeConvert(mapList.get("value"), dataType);

                        params.put(mapList.get("name").toString(),data);
                    }

                }

                //ip
                if(mapList.get("required").toString().equals("2")){
                    if(ObjectUtils.isNotEmpty(mapList.get("isSign"))){
                        params.put(mapList.get("name").toString(),ip);
                    }
                }

                //产品名称
                if(mapList.get("required").toString().equals("3")){
                    if(ObjectUtils.isNotEmpty(mapList.get("isSign"))){
                        params.put(mapList.get("name").toString(),operateOrder.getPayChannelName());
                    }
                }
            }
        }

        log.info("===========测试下单参数"+ params +"===================");

        //下单接口
        String url = operateChannelLabel.getGateway() + operateChannelLabel.getBuyPort();
        //请求类型
        String httpType = configListObject.get(0).get("httpType").toString();
        String paramType = configListObject.get(0).get("paramType").toString();
        try {
            String resJson = "";
            if(httpType.equals("GET")){

                String httpGetParam = SignConfig.getHttpGetParam(params);
                resJson = HttpUtils.sendGet(url,httpGetParam);

            }else if(httpType.equals("POST")){

                if(paramType.equals("param")){
                    //表单
                    resJson = HttpUtils.sendHttpPostParam(url,params);

                }else{
                    //json
                    resJson = HttpUtils.sendHttpPost(url,params);
                }

            }

            log.info("===========测试下单上游返回数据"+ resJson +"===================");
            //数据返回的结果
            Map<String, Object> resJsonMap =  JSONObject.parseObject(resJson);
            log.info("===========测试下单上游返回数据"+ resJsonMap +"===================");
            //根据返回的参数配置，拆分获取到的数据
            List<Map<String, Object>> returnParams = JSONArray.parseObject(operateChannelLabel.getReturnParams(), List.class);

            //code状态参数名
            String code = returnParams.get(0).get("name").toString();
            //code成功状态的值
            String codeSuccess = returnParams.get(0).get("value").toString();
            //msg消息参数名
            String msg = returnParams.get(1).get("name").toString();
            //url层级判断
            String checkTier = returnParams.get(2).get("value").toString();
            //第一层参数参数名
            String LayerOwn = returnParams.get(3).get("name").toString();
            //第二层参数参数名
            String LayerTwo = returnParams.get(4).get("name").toString();
            //第三层参数参数名
            String LayerThree = returnParams.get(6).get("name").toString();

            try {
                // 插入订单数据
                operateOrderService.insertOperateOrder(operateOrder);
            } catch (DuplicateKeyException e) {
                return new AjaxResult("FAIL", "下单失败,订单号重复！", null,null,null);
            }


            //下单成功
            if(resJsonMap.get(code).toString().equals(codeSuccess)){

                //先判断有没有验签(有些上游返回没有验签,0有1无)
                String signCheck = returnParams.get(5).get("isCheck").toString();
                boolean checkResult = true;
                if(signCheck.equals("0")){
                    //先验签
                    String chaLablekey = operateChannelLabel.getMchKey();
                    try {

                        String keyName = "key";
                        if(ObjectUtils.isNotEmpty(returnParams.get(5).get("keyName"))){
                            keyName = returnParams.get(5).get("keyName").toString();
                        }

                        if(ObjectUtils.isNotEmpty(returnParams.get(5).get("signParam"))){

                            String signParam = returnParams.get(5).get("signParam").toString();
                            String data = JSON.toJSONString(resJsonMap.get(signParam));
                            Map<String,Object> dataMap =  JSONObject.parseObject(data);

                            //目前“控股”，sign在外面，所以要加进去
                            //目前“安心”，sign在里面，不用加进去,signLoc为0表示sign在data里面
                            if(ObjectUtils.isNotEmpty(returnParams.get(5).get("signLoc"))){
                                if(returnParams.get(5).get("signLoc").equals("1")){
                                    dataMap.put(returnParams.get(5).get("name").toString(),resJsonMap.get(returnParams.get(5).get("name")));
                                }
                            }

                            checkResult = SignConfig.checkIsSignValidFromResponse(dataMap, chaLablekey,returnParams.get(5).get("value").toString(),
                                    returnParams.get(5).get("lowerOrUpper").toString(),returnParams.get(5).get("isKey").toString(),keyName);

                        }else{


                            checkResult = SignConfig.checkIsSignValidFromResponse(resJsonMap, chaLablekey,returnParams.get(5).get("value").toString(),
                                    returnParams.get(5).get("lowerOrUpper").toString(),returnParams.get(5).get("isKey").toString(),keyName);
                        }

                    } catch (Exception e) {
                        log.error("===========验签过程异常===================");
                        ajax.put("result", "验签过程异常");
                        return success(ajax);
                    }
                }

                if(checkResult == false){
                    log.error("===========(测试拉数据)支付通道id="+operatePayChannel.getId()+"上游返回数据验签失败!可能数据被篡改！===================");
                    ajax.put("result", "上游返回数据验签失败!");
                    return success(ajax);
                }else{

                    //订单状态改变
                    operateOrder.setPaymentStatus("1");
                    operateOrderService.updateOperateOrder(operateOrder);

                    String payUrl = "";
                    if(checkTier.equals("1")){
                        payUrl =  resJsonMap.get(LayerOwn).toString();
                    }else if(checkTier.equals("2")){
                        String data = JSON.toJSONString(resJsonMap.get(LayerOwn));
                        Map<String,Object> dataMap =  JSONObject.parseObject(data);
                        payUrl =  dataMap.get(LayerTwo).toString();
                    }else if(checkTier.equals("3")){
                        String data = JSON.toJSONString(resJsonMap.get(LayerOwn));
                        Map<String,Object> dataMap =  JSONObject.parseObject(data);
                        String urlParam = JSON.toJSONString(dataMap.get(LayerTwo));
                        Map<String,Object> urlParamMap =  JSONObject.parseObject(urlParam);
                        payUrl =  urlParamMap.get(LayerThree).toString();
                    }

                    ajax.put("payUrl", payUrl);
                    ajax.put("result", "");
                    ajax.put("payOrderNo", operateOrder.getPayOrderNo());
                    return success(ajax);

                }


            }else{

                //飞机发送通知(下单上游返回失败)
                SysRobat sysRobat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
                if(sysRobat.getTgmError() != null && !sysRobat.getTgmError().equals("")){
                    String message = "产品[1000]  " + operateChannelLabel.getChaLabName() + "【"+operatePayChannel.getChaCode()+"】" + "取码失败\n" +
                            "商户订单：" + operateOrder.getMchOrderNo() + ",金额:" + amount + "\n" +
                            "原因：" + resJsonMap.get(msg).toString();
                    //发送
                    execBot.sendMessageToGroup(sysRobat.getTgmError().toString(),message);
                }

                log.error("===========(测试拉数据)支付通道id="+operatePayChannel.getId()+"上游通道返回下单失败======="+resJsonMap.get(msg)+"============");
                ajax.put("result", "上游通道返回下单失败,原因："+resJsonMap.get(msg).toString());
                return success(ajax);
            }




        } catch (Exception e) {
            log.error("===========(测试拉数据)支付通道id="+operatePayChannel.getId()+"访问上游请求http异常===================");
            ajax.put("result", "访问上游请求http异常");
            return success(ajax);
        }
    }

    /**
     * 手动测试查询订单
     */
    @PostMapping("/pay/testQueryOrder")
    @PreAuthorize("@ss.hasPermi('operate:order:export')")
    public AjaxResult testQueryOrder( @RequestBody OperateOrder operateOrder)
    {

        if(operateOrder != null && ObjectUtils.isNotEmpty(operateOrder)){

            OperateChannelLabel operateChannelLabel = operateChannelLabelService.selectOperateChannelLabelById(operateOrder.getChaLabId());
            //从配置信息获取参数
            Map<String, Object> params = new HashMap<>();

            if(ObjectUtils.isNotEmpty(operateChannelLabel.getCheckParams())){


                List<Map<String, Object>> checkParams = JSONArray.parseObject(operateChannelLabel.getCheckParams(), List.class);

                //商户id(required为0是必填)
                if(checkParams.get(0).get("required").toString().equals("0")){

                    //数据判断和转换
                    String dataType = checkParams.get(0).get("dataType").toString();
                    Object data = SignConfig.dataTypeConvert(operateChannelLabel.getMchId(), dataType);

                    params.put(checkParams.get(0).get("name").toString(),data);
                }
                //商户订单号(required为0是必填)
                if(checkParams.get(1).get("required").toString().equals("0")){

                    //数据判断和转换
                    String dataType = checkParams.get(1).get("dataType").toString();
                    Object data = SignConfig.dataTypeConvert(operateOrder.getPayOrderNo(), dataType);

                    params.put(checkParams.get(1).get("name").toString(),data);
                }

                //随机字符串（确保请求的时效性、防止重放攻击）
                if(checkParams.get(3).get("required").toString().equals("0")){
                    String length = checkParams.get(3).get("length").toString();
                    //肯定是字符串
                    params.put(checkParams.get(3).get("name").toString(),generateNonce(Integer.valueOf(length)));

                }

                //毫秒级时间戳（确保请求的时效性、防止重放攻击）
                if(checkParams.get(4).get("required").toString().equals("0")){
                    long timestamp = System.currentTimeMillis();

                    if(ObjectUtils.isNotEmpty(checkParams.get(4).get("length"))){
                        if(checkParams.get(4).get("length").equals("10")){
                            // 转换为秒级时间戳（10位）
                            timestamp = timestamp / 1000;
                        }
                    }

                    String dataType = checkParams.get(4).get("dataType").toString();
                    Object data = SignConfig.dataTypeConvert(timestamp, dataType);
                    params.put(checkParams.get(4).get("name").toString(),data);
                }

                //时间：格式（2016-12-26 18:18:18）
                if(checkParams.get(5).get("required").toString().equals("0")){
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    // 格式化当前时间
                    String formattedDate = formatter.format(DateUtils.getNowDate());
                    params.put(checkParams.get(5).get("name").toString(),formattedDate);
                }

                //接口版本号
                if(checkParams.size() > 6){

                    if(checkParams.get(6).get("required").toString().equals("0")){
                        params.put(checkParams.get(6).get("name").toString(),checkParams.get(6).get("value").toString());
                    }
                }

                //标识参数
                if(checkParams.size() > 7){

                    if(checkParams.get(7).get("required").toString().equals("0")){
                        params.put(checkParams.get(7).get("name").toString(),checkParams.get(7).get("value").toString());
                    }
                }

                //时间：格式（20161226181818）
                if(checkParams.size() > 8){

                    if(checkParams.get(8).get("required").toString().equals("0")){
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                        // 格式化当前时间
                        String formattedDate = formatter.format(DateUtils.getNowDate());
                        params.put(checkParams.get(8).get("name").toString(),formattedDate);
                    }

                }


                //sign验签,value值为0字段空不验证，1字段空也要参与签名
                try {

                    if(ObjectUtils.isNotEmpty(checkParams.get(2).get("signType"))){

                        String sign = "";

                        if(checkParams.get(2).get("signType").equals("1")){
                            //玖忆
                            sign = SignConfig.getSignNew_JY2(params,operateChannelLabel.getMchKey());

                        }else if(checkParams.get(2).get("signType").equals("2")){
                            //锦鲤
                            sign = SignConfig.getSignNew_JL2(params,operateChannelLabel.getMchKey());

                        }else if(checkParams.get(2).get("signType").equals("3")){
                            //盈通
                            sign = SignConfig.getSignNew_YT2(params,operateChannelLabel.getMchKey());
                        }else if(checkParams.get(2).get("signType").equals("4")){
                            //万嘉
                            sign = SignConfig.getSignNew_WJ2(params,operateChannelLabel.getMchKey());
                        }else if(checkParams.get(2).get("signType").equals("5")){
                            //pag
                            sign = SignConfig.getSignNew_PGA(params,operateChannelLabel.getMchKey());
                        }

                        params.put(checkParams.get(2).get("name").toString(),sign);

                    }else{
                        String keyName = "key";
                        if(ObjectUtils.isNotEmpty(checkParams.get(2).get("keyName"))){
                            keyName = checkParams.get(2).get("keyName").toString();
                        }

                        //蓝猫、新mw不在末尾添加key，直接加入集合按顺序生成验签
                        if(checkParams.get(2).get("isKey").toString().equals("3")){
                            params.put(checkParams.get(2).get("keyName").toString(),operateChannelLabel.getMchKey());
                        }

                        String sign = SignConfig.getSignNew(params, operateChannelLabel.getMchKey(),
                                checkParams.get(2).get("value").toString(),checkParams.get(2).get("isKey").toString(),keyName);
                        //有些上游sign是小写的
                        if(checkParams.get(2).get("lowerOrUpper").toString().equals("1")){
                            sign = sign.toLowerCase();
                        }

                        //蓝猫、新mw除掉key
                        if(checkParams.get(2).get("isKey").toString().equals("3")){
                            params.remove(checkParams.get(2).get("keyName").toString());
                        }

                        params.put(checkParams.get(2).get("name").toString(),sign);

                    }



                } catch (Exception e) {
                    log.error("===========查询订单验签过程异常===================");
                    return new AjaxResult("FAIL", "查询订单验签过程异常！", null,null,null);
                }

                String url = operateChannelLabel.getGateway() + operateChannelLabel.getQueryPort();
                //请求类型
                String httpType = checkParams.get(0).get("httpType").toString();
                String paramType = checkParams.get(0).get("paramType").toString();
                try {
                    String resJson = "";
                    if(httpType.equals("GET")){

                        String httpGetParam = SignConfig.getHttpGetParam(params);
                        resJson = HttpUtils.sendGet(url,httpGetParam);

                    }else if(httpType.equals("POST")){

                        if(paramType.equals("param")){
                            //表单
                            resJson = HttpUtils.sendHttpPostParam(url,params);

                        }else{
                            //json
                            resJson = HttpUtils.sendHttpPost(url,params);
                        }


                    }
                    //数据返回的结果
                    Map<String,Object> resJsonMap =  JSONObject.parseObject(resJson);

                    log.info("=================手动查单返回参数" + resJsonMap + "============================");

                    //根据返回的参数配置，拆分获取到的数据
                    List<Map<String, Object>> queryRenParams = JSONArray.parseObject(operateChannelLabel.getQueryRenParams(), List.class);

                    //根据配置的查询返回参数找到数据
                    //code状态参数名
                    String code = queryRenParams.get(0).get("name").toString();
                    //code成功状态的值
                    String codeSuccess = queryRenParams.get(0).get("value").toString();
                    //msg消息参数名
                    String msg = queryRenParams.get(1).get("name").toString();
                    //状态层级判断
                    String checkTier = queryRenParams.get(2).get("value").toString();
                    //第一层参数参数名
                    String LayerOwn = queryRenParams.get(3).get("name").toString();
                    //第二层参数参数名
                    String LayerTwo = queryRenParams.get(4).get("name").toString();
                    //支付成功状态
                    String status1 = queryRenParams.get(7).get("value").toString();
                    //订单完成\补单成功的状态
                    String status2 = queryRenParams.get(8).get("value").toString();
                    //支付中
                    String status3 = queryRenParams.get(9).get("value").toString();

                    //梦想点卷 没有查询结果的状态
                    boolean start = true;
                    if(!ObjectUtils.isNotEmpty(queryRenParams.get(0).get("isCheck"))){
                        start = resJsonMap.get(code).toString().equals(codeSuccess);
                    }

                    //查询成功
                    if(start){

                        //先判断有没有验签(有些上游返回没有验签,0有1无)
                        String signValue = queryRenParams.get(5).get("value").toString();
                        boolean signCheck = true;
                        if(signValue.equals("0")){
                            //先验签
                            String chaLablekey = operateChannelLabel.getMchKey();
                            try {

                                String keyName = "key";
                                if(ObjectUtils.isNotEmpty(queryRenParams.get(6).get("keyName"))){
                                    keyName = queryRenParams.get(6).get("keyName").toString();
                                }

                                if(ObjectUtils.isNotEmpty(queryRenParams.get(6).get("signParam"))){

                                    String signParam = queryRenParams.get(6).get("signParam").toString();
                                    String data = JSON.toJSONString(resJsonMap.get(signParam));
                                    Map<String,Object> dataMap =  JSONObject.parseObject(data);

                                    //目前“控股”，sign在外面，所以要加进去
                                    //目前“安心”，sign在里面，不用加进去,signLoc为0表示sign在data里面
                                    if(ObjectUtils.isNotEmpty(queryRenParams.get(6).get("signLoc"))){
                                        if(queryRenParams.get(6).get("signLoc").equals("1")){
                                            dataMap.put(queryRenParams.get(6).get("name").toString(),resJsonMap.get(queryRenParams.get(6).get("name")));
                                        }
                                    }

                                    signCheck = SignConfig.checkIsSignValidFromResponse(dataMap, chaLablekey,queryRenParams.get(6).get("value").toString(),
                                            queryRenParams.get(6).get("lowerOrUpper").toString(),queryRenParams.get(6).get("isKey").toString(),keyName);

                                }else{


                                    signCheck = SignConfig.checkIsSignValidFromResponse(resJsonMap, chaLablekey,queryRenParams.get(6).get("value").toString(),
                                            queryRenParams.get(6).get("lowerOrUpper").toString(),queryRenParams.get(6).get("isKey").toString(),keyName);
                                }


                            } catch (Exception e) {
                                log.error("===========查询订单==上游返回的参数验签过程异常===================");
                                return new AjaxResult("FAIL", "查询订单==上游返回的参数验签过程异常！", null,null,null);
                            }
                        }

                        if(signCheck == false){
                            log.error("===========查询订单上游返回数据验签失败!可能数据被篡改！===================");
                            return new AjaxResult("FAIL", "查询订单上游返回数据验签失败！", null,null,null);
                        }else{


                            String status = "";
                            if(checkTier.equals("1")){
                                status = resJsonMap.get(LayerOwn).toString();
                            }else if(checkTier.equals("2")){
                                String data = JSON.toJSONString(resJsonMap.get(LayerOwn));
                                Map<String,Object> dataMap =  JSONObject.parseObject(data);
                                status = dataMap.get(LayerTwo).toString();
                            }

                            //支付成功（可能上游回调失败，或者没有回调通知）
                            if(status.equals(status1) || status.equals(status2)){

//                                operateOrder.setPaymentStatus("2");
//                                operateOrder.setChaCallbackDate(DateUtils.getNowDate());
//                                operateOrderService.updateOperateOrder(operateOrder);

                                return new AjaxResult("SUCCESS", "支付成功", operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue(),
                                        String.valueOf(operateOrder.getMchId()),String.valueOf(operateOrder.getProductId()),operateOrder.getPayOrderNo(),
                                        operateOrder.getMchOrderNo(),2);

                                //支付中
                            }else if(status.equals(status3)){

//                                operateOrder.setPaymentStatus("1");
//                                operateOrderService.updateOperateOrder(operateOrder);

                                return new AjaxResult("SUCCESS", "支付中", operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue(),
                                        String.valueOf(operateOrder.getMchId()),String.valueOf(operateOrder.getProductId()),operateOrder.getPayOrderNo(),
                                        operateOrder.getMchOrderNo(),1);


                                //支付失败
                            }else{

//                                operateOrder.setPaymentStatus("3");
//                                operateOrder.setChaCallbackDate(DateUtils.getNowDate());
//                                operateOrderService.updateOperateOrder(operateOrder);

                                return new AjaxResult("SUCCESS", "支付失败", operateOrder.getPaymentAmount().multiply(new BigDecimal(100)).intValue(),
                                        String.valueOf(operateOrder.getMchId()),String.valueOf(operateOrder.getProductId()),operateOrder.getPayOrderNo(),
                                        operateOrder.getMchOrderNo(),3);

                            }

                        }


                    }else{

                        return new AjaxResult("FAIL", "查询失败！", null,null,null);

                    }


                } catch (Exception e) {

                    log.error("===========查询订单id="+ operateOrder.getId()+"访问上游请求http异常===================");
                    return new AjaxResult("FAIL", "查询失败！", null,null,null);

                }


            }else{

                return new AjaxResult("FAIL", "没有配置查单！查询失败！", null,null,null);

            }

        }

        return success("订单异常！");

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
                } else {
                    // 不需要再次调用，终止调度
                    scheduler.shutdown();
                }
            } catch (InterruptedException | ExecutionException e) {
                // 处理异常
                Thread.currentThread().interrupt();
                scheduler.shutdown();
            }
        }, initialDelay, period, TimeUnit.SECONDS);

    }

    /**
     * 根据随机数、时间戳生成订单号
     */
    public static String generateBillNoNumber(String prefix) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        int RANDOM_NUM_BOUND = 1000; // 定义随机数范围
        // 生成时间戳部分
        String timestamp = dateFormat.format(new Date());
        // 生成随机数部分
        int randomNumber = ThreadLocalRandom.current().nextInt(RANDOM_NUM_BOUND);
        // 组合成业务流水号
        return prefix + timestamp + String.format("%03d", randomNumber);
    }

    /**
     * 生成随机字符串 (nonce)
     *
     * @param length 指定随机字符串的长度 (8-32)
     * @return 生成的随机字符串
     */
    public static String generateNonce(int length) {
        StringBuilder nonce = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            nonce.append(CHARACTERS.charAt(randomIndex));
        }
        return nonce.toString();
    }
    

    public static void main(String[] args) {


//        {msg=订单成功, sign=C8593EBF52F9E4174FBD938E63CF3C3A, paySerialNo=AH20250222203617074934, amount=500.0, code=200, endTime=2025-02-22 20:36:50}
//        {Comment=null, Attachments=null, AccessKey=45OnrbQEMVizlz7V7aMghR73, Timestamp=1740230589, Amount=400, Sign=c75a8c9d4c557c1b46bbf22b1eebdf66, OrderNo=AH20250222212031034870, Status=4, Ext=null, OrderInfoExt=null}
//        {Comment=null, Attachments=null, AccessKey=45OnrbQEMVizlz7V7aMghR73, Timestamp=1740230589, Amount=400, Sign=, OrderNo=AH20250222212031034870, Status=4, Ext=null, OrderInfoExt=null, sign=}
//        AccessKey=45OnrbQEMVizlz7V7aMghR73&Amount=400&OrderNo=AH20250222212031034870&Status=4&Timestamp=1740230589&SecretKey=gbo87AzeKqtEv4RbW4VKiRE9grlaylu944ELoyrQ

//        try {
//            String result = "amount=10000&currency=CNY&fee=1050&mch_no=1722072303&nonce=6d9bd7dc686d4ed8adb49c3354475e02&out_order_no=AH20250224223145856031&pay_type=92&status=SUCCESS&time_stamp=1740407645&trade_no=PGA20250224047534P";
//            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secret_key = new SecretKeySpec("001cccf540a6423a80e0312f08e12d7e".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
//            sha256_HMAC.init(secret_key);
//            byte[] array = sha256_HMAC.doFinal(result.toString().getBytes(StandardCharsets.UTF_8));
//            StringBuilder sb = new StringBuilder();
//            for (byte item: array) {
//                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
//            }
//            System.out.println(sb.toString());
//        } catch (InvalidKeyException e) {
//            throw new RuntimeException(e);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }

//        System.out.println(MD5Utils.MD5Encode(result));

//                System.out.println("0".equals(0));

    }

    /**
     * test
     */
//    @GetMapping("/test")
//    public AjaxResult test(@RequestParam Map<String, Object> dataParams){
//        // 获取默认时区的当前时间
//        LocalDateTime now = LocalDateTime.now();
//        // 设置为北京时区
//        ZoneId beijingZoneId = ZoneId.of("Asia/Shanghai");
//        // 获取北京当前时间精确到秒
//        ZonedDateTime beijingTime = now.atZone(beijingZoneId);
//        // 格式化时间为字符串
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        //今天
//        String beijingTimeTodayString = beijingTime.format(formatter);
//        OperateBillDTO operateBillDTO = new OperateBillDTO();
//        operateBillDTO.setCheckDate(beijingTimeTodayString);
//        operateBillDTO.setChaLabId(Long.valueOf("2000"));
//        List<OperateBillDTO> dataList = operateOrderService.queryChaLabBill(operateBillDTO);
//        //拿到差额配置
//        //飞机发送消息(通道额度警告  小于等于配置的数值就要发送通知)
//        SysRobat robat = sysRobatService.selectSysRobatById(Long.valueOf("1"));
//        if(robat.getChaQuotaWarn() != null && !robat.getChaQuotaWarn().equals("")){
//            if(dataList.get(0).getBalance().compareTo(new BigDecimal(robat.getChaQuotaWarn())) <= 0){
//                if(robat.getTgmError() != null && !robat.getTgmError().equals("")){
//                    String message =  "注意：通道" + "  " + "测试" + "  " + "差额低于：" + robat.getChaQuotaWarn() + "\n"
//                            + robat.getWarnAppend();
//                    //发送
//                    execBot.sendMessageToGroup(robat.getTgmError().toString(),message);
//                }
//            }
//
//        }
//
//        return success();
//    }

}
