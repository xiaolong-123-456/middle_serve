package com.ruoyi.operate.controller;

import com.github.pagehelper.PageInfo;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.operate.domain.OperateAgent;
import com.ruoyi.operate.domain.OperateMerchant;
import com.ruoyi.operate.domain.OperateOrder;
import com.ruoyi.operate.dto.EChartsDTO;
import com.ruoyi.operate.dto.OperateBillDTO;
import com.ruoyi.operate.dto.OperateMerchantDTO;
import com.ruoyi.operate.dto.OperateOrderDTO;
import com.ruoyi.operate.service.IOperateAgentService;
import com.ruoyi.operate.service.IOperateMerchantService;
import com.ruoyi.operate.service.IOperateOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单数据Controller
 * 
 * @author master123
 * @date 2024-09-25
 */
@RestController
@RequestMapping("/operate/order")
public class OperateOrderController extends BaseController
{
    SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private IOperateOrderService operateOrderService;
    @Autowired
    private IOperateAgentService operateAgentService;
    @Autowired
    private IOperateMerchantService operateMerchantService;

    /**
     * 查询订单数据列表
     */
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    @GetMapping("/list")
    public AjaxResult list(OperateOrderDTO operateOrderDTO)
    {

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

        AjaxResult ajax = AjaxResult.success();

        //创建时间是今天的数据
        List<OperateOrder> dataList = operateOrderService.selectOperateOrderList(operateOrderDTO);

        Map<String, Object> params = operateOrderDTO.getParams();
        String beginTime = null;
        String endTime = null;
        try {
            beginTime = fm.format(fm.parse(params.get("beginTime").toString()));
            endTime = fm.format(fm.parse(params.get("endTime").toString()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if(beijingTimeTodayString.equals(beginTime) && beijingTimeTodayString.equals(endTime)){
            //隔日回调的数据
            operateOrderDTO.setYesterDayDate(beijingTimeyesterdayString);
            List<OperateOrder> yesterdayDataList = operateOrderService.selectOperateOrderYesterdayList(operateOrderDTO);
            dataList.addAll(yesterdayDataList);

            //隔日订单数
            ajax.put("yesOrderTotalNumber", yesterdayDataList.size());
            //隔日订单总金额
            BigDecimal yesOrderTotalAmount = yesterdayDataList.stream()
                    .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            ajax.put("yesOrderTotalAmount", yesOrderTotalAmount);
            //商户总收入
            List<OperateOrder> yesHavePaid = yesterdayDataList.stream().filter(item -> item.getPaymentStatus().equals("2") ||
                    item.getPaymentStatus().equals("4")).collect(Collectors.toList());
            BigDecimal yesMchActReceipt = yesHavePaid.stream()
                    .map(val -> val.getMchActReceipt() != null ? val.getMchActReceipt() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            ajax.put("yesMchTotalAmount", yesMchActReceipt);
            //代理商收入
            BigDecimal yesAgentActReceipt = yesHavePaid.stream()
                    .map(val -> val.getAgentActReceipt() != null ? val.getAgentActReceipt() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            ajax.put("yesAgentTotalAmount", yesAgentActReceipt);
            //平台收入
            BigDecimal yesProfit = yesHavePaid.stream()
                    .map(val -> val.getProfit() != null ? val.getProfit() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            ajax.put("yesPlatTotalAmount", yesProfit);
        }

        startPage();
        //创建时间是今天的数据
        List<OperateOrder> pageDataList = operateOrderService.selectOperateOrderList(operateOrderDTO);
        ajax.put("rows", pageDataList);
        ajax.put("total", new PageInfo(pageDataList).getTotal());
        //当天：订单展示的各种数据
        //提交订单数
        ajax.put("orderTotalNumber", dataList.size());
        //订单总金额
        BigDecimal orderTotalAmount = dataList.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("orderTotalAmount", orderTotalAmount);
        //已付订单数
        List<OperateOrder> HavePaid = dataList.stream().filter(item -> item.getPaymentStatus().equals("2") ||
                item.getPaymentStatus().equals("4")).collect(Collectors.toList());
        ajax.put("orderHavePaidNumber", HavePaid.size());
        //已付订单总金额
        BigDecimal orderHavePaidAmount = HavePaid.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("orderHavePaidAmount", orderHavePaidAmount);
        //商户总收入
        BigDecimal mchActReceipt = HavePaid.stream()
                .map(val -> val.getMchActReceipt() != null ? val.getMchActReceipt() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("mchTotalAmount", mchActReceipt);
        //代理商收入
        BigDecimal agentActReceipt = HavePaid.stream()
                .map(val -> val.getAgentActReceipt() != null ? val.getAgentActReceipt() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("agentTotalAmount", agentActReceipt);
        //平台收入
        BigDecimal profit = HavePaid.stream()
                .map(val -> val.getProfit() != null ? val.getProfit() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("platTotalAmount", profit);
        //未付款订单数
        List<OperateOrder> nonPayment = dataList.stream().filter(item -> item.getPaymentStatus().equals("0") ||
                item.getPaymentStatus().equals("1") || item.getPaymentStatus().equals("3")).collect(Collectors.toList());
        ajax.put("orderNonPaymentNumber", nonPayment.size());
        //未付款总金额
        BigDecimal orderNonPaymentAmount = nonPayment.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("orderNonPaymentAmount",orderNonPaymentAmount);


        return success(ajax);
    }

    /**
     * 查询订单数据列表(mch)
     */
    @GetMapping("/mchOrderList")
    public AjaxResult mchOrderList(OperateOrderDTO operateOrderDTO)
    {

        OperateMerchant operateMerchant = operateMerchantService.queryOperateMerchantByUserId(Long.valueOf(operateOrderDTO.getUserId()));
        operateOrderDTO.setMchId(operateMerchant.getId());

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

        AjaxResult ajax = AjaxResult.success();

        //创建时间是今天的数据
        List<OperateOrder> dataList = operateOrderService.selectOperateOrderList(operateOrderDTO);

        Map<String, Object> params = operateOrderDTO.getParams();
        String beginTime = null;
        String endTime = null;
        try {
            beginTime = fm.format(fm.parse(params.get("beginTime").toString()));
            endTime = fm.format(fm.parse(params.get("endTime").toString()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if(beijingTimeTodayString.equals(beginTime) && beijingTimeTodayString.equals(endTime)){
            //隔日回调的数据
            operateOrderDTO.setYesterDayDate(beijingTimeyesterdayString);
            List<OperateOrder> yesterdayDataList = operateOrderService.selectOperateOrderYesterdayList(operateOrderDTO);
            dataList.addAll(yesterdayDataList);

            //隔日订单数
            ajax.put("yesOrderTotalNumber", yesterdayDataList.size());
            //隔日订单总金额
            BigDecimal yesOrderTotalAmount = yesterdayDataList.stream()
                    .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            ajax.put("yesOrderTotalAmount", yesOrderTotalAmount);
            //商户总收入
            List<OperateOrder> yesHavePaid = yesterdayDataList.stream().filter(item -> item.getPaymentStatus().equals("2") ||
                    item.getPaymentStatus().equals("4")).collect(Collectors.toList());
            BigDecimal yesMchActReceipt = yesHavePaid.stream()
                    .map(val -> val.getMchActReceipt() != null ? val.getMchActReceipt() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            ajax.put("yesMchTotalAmount", yesMchActReceipt);
        }

        startPage();
        //创建时间是今天的数据
        List<OperateOrder> pageDataList = operateOrderService.selectOperateOrderList(operateOrderDTO);
        ajax.put("rows", pageDataList);
        ajax.put("total", new PageInfo(pageDataList).getTotal());
        //当天：订单展示的各种数据
        //提交订单数
        ajax.put("orderTotalNumber", dataList.size());
        //订单总金额
        BigDecimal orderTotalAmount = dataList.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("orderTotalAmount", orderTotalAmount);
        //已付订单数
        List<OperateOrder> HavePaid = dataList.stream().filter(item -> item.getPaymentStatus().equals("2") ||
                item.getPaymentStatus().equals("4")).collect(Collectors.toList());
        ajax.put("orderHavePaidNumber", HavePaid.size());
        //已付订单总金额
        BigDecimal orderHavePaidAmount = HavePaid.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("orderHavePaidAmount", orderHavePaidAmount);
        //商户总收入
        BigDecimal mchActReceipt = HavePaid.stream()
                .map(val -> val.getMchActReceipt() != null ? val.getMchActReceipt() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("mchTotalAmount", mchActReceipt);
        //未付款订单数
        List<OperateOrder> nonPayment = dataList.stream().filter(item -> item.getPaymentStatus().equals("0") ||
                item.getPaymentStatus().equals("1") || item.getPaymentStatus().equals("3")).collect(Collectors.toList());
        ajax.put("orderNonPaymentNumber", nonPayment.size());
        //未付款总金额
        BigDecimal orderNonPaymentAmount = nonPayment.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("orderNonPaymentAmount",orderNonPaymentAmount);

        //成功率
        if(dataList.size() > 0){
            double result = (double) HavePaid.size() / dataList.size();
            BigDecimal success = new BigDecimal(result);
            ajax.put("success", success.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP) + "%");
        }else{
            ajax.put("success", "0.00%");
        }

        return success(ajax);
    }


    /**
     * 查询订单数据列表(agent)
     */
    @GetMapping("/agentOrderList")
    public AjaxResult agentOrderList(OperateOrderDTO operateOrderDTO)
    {

        OperateAgent operateAgent = operateAgentService.queryOperateAgentByUserId(Long.valueOf(operateOrderDTO.getUserId()));
        operateOrderDTO.setAgentId(operateAgent.getId());

        AjaxResult ajax = AjaxResult.success();

        //创建时间是今天的数据
        List<OperateOrder> dataList = operateOrderService.selectOperateOrderList(operateOrderDTO);

        startPage();
        //创建时间是今天的数据
        List<OperateOrder> pageDataList = operateOrderService.selectOperateOrderList(operateOrderDTO);
        ajax.put("rows", pageDataList);
        ajax.put("total", new PageInfo(pageDataList).getTotal());
        //当天：订单展示的各种数据
        //提交订单数
        ajax.put("orderTotalNumber", dataList.size());
        //订单总金额
        BigDecimal orderTotalAmount = dataList.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("orderTotalAmount", orderTotalAmount);
        //已付订单数
        List<OperateOrder> HavePaid = dataList.stream().filter(item -> item.getPaymentStatus().equals("2") ||
                item.getPaymentStatus().equals("4")).collect(Collectors.toList());
        ajax.put("orderHavePaidNumber", HavePaid.size());
        //已付订单总金额
        BigDecimal orderHavePaidAmount = HavePaid.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("orderHavePaidAmount", orderHavePaidAmount);
        //未付款订单数
        List<OperateOrder> nonPayment = dataList.stream().filter(item -> item.getPaymentStatus().equals("0") ||
                item.getPaymentStatus().equals("1") || item.getPaymentStatus().equals("3")).collect(Collectors.toList());
        ajax.put("orderNonPaymentNumber", nonPayment.size());
        //未付款总金额
        BigDecimal orderNonPaymentAmount = nonPayment.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("orderNonPaymentAmount",orderNonPaymentAmount);

        return success(ajax);
    }

    /**
     * 查询隔日回调订单数据列表
     */
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    @GetMapping("/yesterdayList")
    public AjaxResult yesterdayList(OperateOrderDTO operateOrderDTO)
    {

        //没有分页数据
        List<OperateOrder> yesterdayDataList = operateOrderService.selectOperateOrderYesterdayList(operateOrderDTO);

        startPage();
        List<OperateOrder> pageYesterdayDataList = operateOrderService.selectOperateOrderYesterdayList(operateOrderDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", pageYesterdayDataList);
        ajax.put("total", new PageInfo(pageYesterdayDataList).getTotal());

        //隔日订单数
        ajax.put("yesOrderTotalNumber", yesterdayDataList.size());
        //隔日订单总金额
        BigDecimal yesOrderTotalAmount = yesterdayDataList.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("yesOrderTotalAmount", yesOrderTotalAmount);
        //商户总收入
        List<OperateOrder> yesHavePaid = yesterdayDataList.stream().filter(item -> item.getPaymentStatus().equals("2") ||
                item.getPaymentStatus().equals("4")).collect(Collectors.toList());
        BigDecimal yesMchActReceipt = yesHavePaid.stream()
                .map(val -> val.getMchActReceipt() != null ? val.getMchActReceipt() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("yesMchTotalAmount", yesMchActReceipt);
        //代理商收入
        BigDecimal yesAgentActReceipt = yesHavePaid.stream()
                .map(val -> val.getAgentActReceipt() != null ? val.getAgentActReceipt() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("yesAgentTotalAmount", yesAgentActReceipt);
        //平台收入
        BigDecimal yesProfit = yesHavePaid.stream()
                .map(val -> val.getProfit() != null ? val.getProfit() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("yesPlatTotalAmount", yesProfit);

        return success(ajax);
    }

    /**
     * 查询隔日回调订单数据列表(mch)
     */
    @GetMapping("/yesterdayMchList")
    public AjaxResult yesterdayMchList(OperateOrderDTO operateOrderDTO)
    {

        OperateMerchant operateMerchant = operateMerchantService.queryOperateMerchantByUserId(Long.valueOf(operateOrderDTO.getUserId()));
        operateOrderDTO.setMchId(operateMerchant.getId());

        List<OperateOrder> yesterdayDataList = operateOrderService.selectOperateOrderYesterdayList(operateOrderDTO);

        startPage();
        List<OperateOrder> pageYesterdayDataList = operateOrderService.selectOperateOrderYesterdayList(operateOrderDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", pageYesterdayDataList);
        ajax.put("total", new PageInfo(pageYesterdayDataList).getTotal());

        //隔日订单数
        ajax.put("yesOrderTotalNumber", yesterdayDataList.size());
        //隔日订单总金额
        BigDecimal yesOrderTotalAmount = yesterdayDataList.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("yesOrderTotalAmount", yesOrderTotalAmount);
        //商户总收入
        List<OperateOrder> yesHavePaid = yesterdayDataList.stream().filter(item -> item.getPaymentStatus().equals("2") ||
                item.getPaymentStatus().equals("4")).collect(Collectors.toList());
        BigDecimal yesMchActReceipt = yesHavePaid.stream()
                .map(val -> val.getMchActReceipt() != null ? val.getMchActReceipt() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("yesMchTotalAmount", yesMchActReceipt);

        return success(ajax);
    }


    /**
     * 获取订单数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:order:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operateOrderService.selectOperateOrderById(id));
    }

    /**
     * 新增订单数据
     */
    @PreAuthorize("@ss.hasPermi('operate:order:add')")
    @Log(title = "订单数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateOrder operateOrder)
    {
        operateOrder.setCreateBy(getUsername());
        return toAjax(operateOrderService.insertOperateOrder(operateOrder));
    }

    /**
     * 修改订单数据
     */
    @PreAuthorize("@ss.hasPermi('operate:order:edit')")
    @Log(title = "订单数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateOrder operateOrder)
    {
        return toAjax(operateOrderService.updateOperateOrder(operateOrder));
    }


    /**
     * 删除订单数据
     */
    @PreAuthorize("@ss.hasPermi('operate:order:remove')")
    @Log(title = "订单数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operateOrderService.deleteOperateOrderByIds(ids));
    }

    /**
     * 商户对账模块
     */
    @GetMapping("/getMchBill")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult getMchBill(OperateBillDTO operateBillDTO){
//        startPage();
        List<OperateBillDTO> dataList = operateOrderService.queryMchBill(operateBillDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", dataList);
        ajax.put("total", new PageInfo(dataList).getTotal());
        return success(ajax);
    }

    /**
     * 渠道对账模块
     */
    @GetMapping("/getChaLabBill")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult getChaLabBill(OperateBillDTO operateBillDTO){
//        startPage();
        List<OperateBillDTO> dataList = operateOrderService.queryChaLabBill(operateBillDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", dataList);
        ajax.put("total", new PageInfo(dataList).getTotal());
        return success(ajax);
    }

    /**
     * 商户对账查看详情
     */
    @GetMapping("/getMchBillInfo")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult getMchBillInfo(OperateBillDTO operateBillDTO){
        List<OperateBillDTO> dataList = operateOrderService.queryMchBillInfo(operateBillDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("dataList", dataList);
        ajax.put("total", new PageInfo(dataList).getTotal());
        return success(ajax);
    }

    /**
     * 渠道对账查看详情
     */
    @GetMapping("/getChaLabBillInfo")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult getChaLabBillInfo(OperateBillDTO operateBillDTO){
        List<OperateBillDTO> dataList = operateOrderService.queryChaLabBillInfo(operateBillDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("dataList", dataList);
        ajax.put("total", new PageInfo(dataList).getTotal());
        return success(ajax);
    }

    /**
     * 商户统计模块
     */
    @GetMapping("/getMchStats")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult getMchStats(OperateBillDTO operateBillDTO){
//        startPage();
        List<OperateBillDTO> dataList = operateOrderService.queryMchStats(operateBillDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", dataList);
        ajax.put("total", new PageInfo(dataList).getTotal());
        return success(ajax);
    }

    /**
     * 商户统计查看
     */
    @GetMapping("/getMchStatsInfo")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult getMchStatsInfo(OperateBillDTO operateBillDTO){
        List<OperateBillDTO> dataList = operateOrderService.queryMchStatsInfo(operateBillDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("dataList", dataList);
        ajax.put("total", new PageInfo(dataList).getTotal());
        return success(ajax);
    }

    /**
     * 产品统计列表
     */
    @GetMapping("/getProStats")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult getProStats(OperateBillDTO operateBillDTO){
//        startPage();
        List<OperateBillDTO> dataList = operateOrderService.queryProStats(operateBillDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", dataList);
        ajax.put("total", new PageInfo(dataList).getTotal());
        return success(ajax);
    }

    /**
     * 产品统计查看详情
     */
    @GetMapping("/getProStatsInfo")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult getProStatsInfo(OperateBillDTO operateBillDTO){
        List<OperateBillDTO> dataList = operateOrderService.queryProStatsInfo(operateBillDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("dataList", dataList);
        ajax.put("total", new PageInfo(dataList).getTotal());
        return success(ajax);
    }

    /**
     * 渠道统计列表
     */
    @GetMapping("/getChaLabStats")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult getChaLabStats(OperateBillDTO operateBillDTO){
//        startPage();
        List<OperateBillDTO> dataList = operateOrderService.queryChaLabStats(operateBillDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", dataList);
        ajax.put("total", new PageInfo(dataList).getTotal());
        return success(ajax);
    }

    /**
     * 渠道统计查看详情
     */
    @GetMapping("/getChaLabStatsInfo")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult getChaLabStatsInfo(OperateBillDTO operateBillDTO){
        List<OperateBillDTO> dataList = operateOrderService.queryChaLabStatsInfo(operateBillDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("dataList", dataList);
        ajax.put("total", new PageInfo(dataList).getTotal());
        return success(ajax);
    }

    /**
     * 首页获取数据(me)
     */
    @GetMapping("/getHomePageData")
    @PreAuthorize("@ss.hasPermi('operate:order:list')")
    public AjaxResult getHomePageData(){

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

        AjaxResult ajax = AjaxResult.success();

        //今天 / 昨天订单
        //创建时间是今天的数据
        OperateOrderDTO operateOrderDTO = new OperateOrderDTO();
        operateOrderDTO.setToDayDate(beijingTimeTodayString);
        List<OperateOrder> dataList = operateOrderService.selectOperateOrderList(operateOrderDTO);
        //今日收款订单
        List<OperateOrder> HavePaid = dataList.stream().filter(item -> item.getPaymentStatus().equals("4")).collect(Collectors.toList());
        ajax.put("todayOrder", HavePaid.size());
        //今日收款总额
        BigDecimal HavePaidAmount = HavePaid.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("todayOrderAmount", HavePaidAmount);
        //今日成功率
        if(dataList.size() > 0){
            double result = (double) HavePaid.size() / dataList.size();
            BigDecimal todaySuccess = new BigDecimal(result);
            ajax.put("todaySuccess", todaySuccess.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP) + "%");
        }else{
            ajax.put("todaySuccess", "0.00%");
        }


        //创建时间是昨天的数据
        OperateOrderDTO yesOperateOrderDTO = new OperateOrderDTO();
        yesOperateOrderDTO.setToDayDate(beijingTimeyesterdayString);
        List<OperateOrder> yesDataList = operateOrderService.selectOperateOrderList(yesOperateOrderDTO);
        //昨日收款订单
        List<OperateOrder> yesHavePaid = yesDataList.stream().filter(item -> item.getPaymentStatus().equals("4")).collect(Collectors.toList());
        ajax.put("yesOrder", yesHavePaid.size());
        //昨日收款总额
        BigDecimal yesHavePaidAmount = yesHavePaid.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("yesOrderAmount", yesHavePaidAmount);
        //昨日成功率
        if(yesDataList.size() > 0){
            double yesResult = (double) yesHavePaid.size() / yesDataList.size();
            BigDecimal yesSuccess = new BigDecimal(yesResult);
            ajax.put("yesSuccess", yesSuccess.multiply(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP) + "%");
        }else{
            ajax.put("yesSuccess", "0.00%");
        }

        //代理商总数
        List<OperateAgent> operateAgents = operateAgentService.selectOperateAgentList(new OperateAgent());
        ajax.put("agentTotal", operateAgents.size());
        //代理商总数
        List<OperateMerchantDTO> operateMerchantDTOS = operateMerchantService.selectOperateMerchantList(new OperateMerchantDTO());
        ajax.put("mchTotal", operateMerchantDTOS.size());
        //收款订单
        List<OperateOrder> operateOrders = operateOrderService.selectOperateOrderList(new OperateOrderDTO());
        List<OperateOrder> orderTotal = operateOrders.stream().filter(item -> item.getPaymentStatus().equals("4")).collect(Collectors.toList());
        ajax.put("orderTotal", orderTotal.size());
        //收款总额
        BigDecimal amountTotal = orderTotal.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("amountTotal", amountTotal);
        //总利润
        BigDecimal profitTotal = orderTotal.stream()
                .map(val -> val.getProfit() != null ? val.getProfit() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("profitTotal", profitTotal);

        //折线图表统计
        List<EChartsDTO> eChartsDTOs = operateOrderService.queryOrderByDate();

        ajax.put("dataList", eChartsDTOs);
        return success(ajax);
    }

    /**
     * 首页获取数据(mch)
     */
    @GetMapping("/getMchHomePageData")
    public AjaxResult getMchHomePageData(@RequestParam("userId") String userId){

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

        AjaxResult ajax = AjaxResult.success();

        //余额
        OperateMerchant operateMerchant = operateMerchantService.queryOperateMerchantByUserId(Long.valueOf(userId));
        if(operateMerchant.getBalance() != null){
            ajax.put("balance", operateMerchant.getBalance());
        }else{
            ajax.put("balance", 0);
        }

        //今天 / 昨天订单
        //创建时间是今天的数据
        OperateOrderDTO operateOrderDTO = new OperateOrderDTO();
        operateOrderDTO.setToDayDate(beijingTimeTodayString);
        operateOrderDTO.setMchId(operateMerchant.getId());
        List<OperateOrder> dataList = operateOrderService.selectOperateOrderList(operateOrderDTO);
        //今日收款订单
        List<OperateOrder> HavePaid = dataList.stream().filter(item -> item.getPaymentStatus().equals("4")).collect(Collectors.toList());
        ajax.put("todayOrder", HavePaid.size());
        //今日收款总额
        BigDecimal HavePaidAmount = HavePaid.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("todayOrderAmount", HavePaidAmount);
        //今日收款净额
        BigDecimal todayNetAmount = HavePaid.stream()
                .map(val -> val.getMchActReceipt() != null ? val.getMchActReceipt() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("todayNetAmount", todayNetAmount);
        //今日收款手续费
        ajax.put("todayServiceAmount", HavePaidAmount.subtract(todayNetAmount));


        //创建时间是昨天的数据
        OperateOrderDTO yesOperateOrderDTO = new OperateOrderDTO();
        yesOperateOrderDTO.setToDayDate(beijingTimeyesterdayString);
        yesOperateOrderDTO.setMchId(operateMerchant.getId());
        List<OperateOrder> yesDataList = operateOrderService.selectOperateOrderList(yesOperateOrderDTO);
        //昨日收款订单
        List<OperateOrder> yesHavePaid = yesDataList.stream().filter(item -> item.getPaymentStatus().equals("4")).collect(Collectors.toList());
        ajax.put("yesOrder", yesHavePaid.size());
        //昨日收款总额
        BigDecimal yesHavePaidAmount = yesHavePaid.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("yesOrderAmount", yesHavePaidAmount);
        //今日收款净额
        BigDecimal yesNetAmount = yesHavePaid.stream()
                .map(val -> val.getMchActReceipt() != null ? val.getMchActReceipt() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("yesNetAmount", yesNetAmount);
        //今日收款手续费
        ajax.put("yesServiceAmount", yesHavePaidAmount.subtract(yesNetAmount));

        return success(ajax);
    }


    /**
     * 首页获取数据(agent)
     */
    @GetMapping("/getAgentHomePageData")
    public AjaxResult getAgentHomePageData(@RequestParam("userId") String userId){

        // 获取默认时区的当前时间
        LocalDateTime now = LocalDateTime.now();
        // 设置为北京时区
        ZoneId beijingZoneId = ZoneId.of("Asia/Shanghai");
        // 获取北京当前时间精确到秒
        ZonedDateTime beijingTime = now.atZone(beijingZoneId);
        // 格式化时间为字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        AjaxResult ajax = AjaxResult.success();

        //余额
        OperateAgent operateAgent = operateAgentService.queryOperateAgentByUserId(Long.valueOf(userId));
        if(operateAgent.getBalance() != null){
            ajax.put("balance", operateAgent.getBalance());
        }else{
            ajax.put("balance", 0);
        }
        OperateMerchantDTO operateMerchant = new OperateMerchantDTO();
        operateMerchant.setAgentId(operateAgent.getId());
        operateMerchant.setStatus("0");
        List<OperateMerchantDTO> operateMerchantDTOS = operateMerchantService.selectOperateMerchantList(operateMerchant);
        //商户数量
        if(operateMerchantDTOS.size() > 0){
            ajax.put("mchNumber", operateMerchantDTOS.size());
        }else{
            ajax.put("mchNumber", 0);
        }

        return success(ajax);
    }

}
