package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperateOrder;
import com.ruoyi.operate.dto.EChartsDTO;
import com.ruoyi.operate.dto.OperateBillDTO;
import com.ruoyi.operate.dto.OperateOrderDTO;

import java.util.List;
import java.util.Map;

/**
 * 订单数据Service接口
 * 
 * @author master123
 * @date 2024-09-25
 */
public interface IOperateOrderService 
{
    /**
     * 查询订单数据
     * 
     * @param id 订单数据主键
     * @return 订单数据
     */
    public OperateOrder selectOperateOrderById(Long id);

    /**
     * 查询订单数据列表
     * 
     * @param operateOrderDTO 订单数据
     * @return 订单数据集合
     */
    public List<OperateOrder> selectOperateOrderList(OperateOrderDTO operateOrderDTO);

    /**
     * 查询隔日回调订单数据列表
     *
     * @param operateOrderDTO 订单数据
     * @return 订单数据集合
     */
    public List<OperateOrder> selectOperateOrderYesterdayList(OperateOrderDTO operateOrderDTO);

    /**
     * 新增订单数据
     * 
     * @param operateOrder 订单数据
     * @return 结果
     */
    public int insertOperateOrder(OperateOrder operateOrder);

    /**
     * 修改订单数据
     * 
     * @param operateOrder 订单数据
     * @return 结果
     */
    public int updateOperateOrder(OperateOrder operateOrder);

    /**
     * 批量删除订单数据
     * 
     * @param ids 需要删除的订单数据主键集合
     * @return 结果
     */
    public int deleteOperateOrderByIds(Long[] ids);

    /**
     * 删除订单数据信息
     * 
     * @param id 订单数据主键
     * @return 结果
     */
    public int deleteOperateOrderById(Long id);

    /**
     * 查询商户对账数据
     *
     * @param operateBillDTO
     * @return 订单数据集合
     */
    public List<OperateBillDTO> queryMchBill(OperateBillDTO operateBillDTO);

    /**
     * 查询渠道对账数据
     *
     * @param operateBillDTO
     * @return 订单数据集合
     */
    public List<OperateBillDTO> queryChaLabBill(OperateBillDTO operateBillDTO);

    /**
     * 查询商户对账详情
     *
     * @param operateBillDTO
     * @return 订单数据集合
     */
    public List<OperateBillDTO> queryMchBillInfo(OperateBillDTO operateBillDTO);

    /**
     * 查询渠道对账详情
     *
     * @param operateBillDTO
     * @return 订单数据集合
     */
    public List<OperateBillDTO> queryChaLabBillInfo(OperateBillDTO operateBillDTO);

    /**
     * 查询商户统计数据
     *
     * @param operateBillDTO
     * @return 订单数据集合
     */
    public List<OperateBillDTO> queryMchStats(OperateBillDTO operateBillDTO);

    /**
     * 查询商户统计查看详情
     *
     * @param operateBillDTO
     * @return 订单数据集合
     */
    public List<OperateBillDTO> queryMchStatsInfo(OperateBillDTO operateBillDTO);

    /**
     * 查询产品统计列表
     *
     * @param operateBillDTO
     * @return 订单数据集合
     */
    public List<OperateBillDTO> queryProStats(OperateBillDTO operateBillDTO);

    /**
     * 查询产品统计查看详情
     *
     * @param operateBillDTO
     * @return 订单数据集合
     */
    public List<OperateBillDTO> queryProStatsInfo(OperateBillDTO operateBillDTO);

    /**
     * 查询渠道统计列表
     *
     * @param operateBillDTO
     * @return 订单数据集合
     */
    public List<OperateBillDTO> queryChaLabStats(OperateBillDTO operateBillDTO);

    /**
     * 查询渠道统计查看详情
     *
     * @param operateBillDTO
     * @return 订单数据集合
     */
    public List<OperateBillDTO> queryChaLabStatsInfo(OperateBillDTO operateBillDTO);

    /**
     * 根据商户订单号或者本渠道订单 查询订单数据
     *
     * @param params 订单数据主键
     * @return 订单数据
     */
    public OperateOrder queryOperateOrderByOrderNo(Map<String, Object> params);

    /**
     * 首页折线图表统计
     */
    List<EChartsDTO> queryOrderByDate();
}
