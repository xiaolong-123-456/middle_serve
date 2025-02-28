package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateOrder;
import com.ruoyi.operate.dto.EChartsDTO;
import com.ruoyi.operate.dto.OperateBillDTO;
import com.ruoyi.operate.dto.OperateOrderDTO;
import com.ruoyi.operate.mapper.OperateOrderMapper;
import com.ruoyi.operate.service.IOperateOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 订单数据Service业务层处理
 * 
 * @author master123
 * @date 2024-09-25
 */
@Service
public class OperateOrderServiceImpl implements IOperateOrderService 
{
    @Autowired
    private OperateOrderMapper operateOrderMapper;

    /**
     * 查询订单数据
     * 
     * @param id 订单数据主键
     * @return 订单数据
     */
    @Override
    public OperateOrder selectOperateOrderById(Long id)
    {
        return operateOrderMapper.selectOperateOrderById(id);
    }

    /**
     * 查询订单数据列表
     * 
     * @param operateOrderDTO 订单数据
     * @return 订单数据
     */
    @Override
    public List<OperateOrder> selectOperateOrderList(OperateOrderDTO operateOrderDTO)
    {
        return operateOrderMapper.selectOperateOrderList(operateOrderDTO);
    }

    @Override
    public List<OperateOrder> selectOperateOrderYesterdayList(OperateOrderDTO operateOrderDTO) {
        return operateOrderMapper.selectOperateOrderYesterdayList(operateOrderDTO);
    }

    /**
     * 新增订单数据
     * 
     * @param operateOrder 订单数据
     * @return 结果
     */
    @Override
    public int insertOperateOrder(OperateOrder operateOrder)
    {
        operateOrder.setCreateTime(DateUtils.getNowDate());
        return operateOrderMapper.insertOperateOrder(operateOrder);
    }

    /**
     * 修改订单数据
     * 
     * @param operateOrder 订单数据
     * @return 结果
     */
    @Override
    public int updateOperateOrder(OperateOrder operateOrder)
    {
        operateOrder.setUpdateTime(DateUtils.getNowDate());
        return operateOrderMapper.updateOperateOrder(operateOrder);
    }

    /**
     * 批量删除订单数据
     * 
     * @param ids 需要删除的订单数据主键
     * @return 结果
     */
    @Override
    public int deleteOperateOrderByIds(Long[] ids)
    {
        return operateOrderMapper.deleteOperateOrderByIds(ids);
    }

    /**
     * 删除订单数据信息
     * 
     * @param id 订单数据主键
     * @return 结果
     */
    @Override
    public int deleteOperateOrderById(Long id)
    {
        return operateOrderMapper.deleteOperateOrderById(id);
    }

    @Override
    public List<OperateBillDTO> queryChaLabBill(OperateBillDTO operateBillDTO) {
        return operateOrderMapper.selectChaLabBill(operateBillDTO);
    }

    @Override
    public List<OperateBillDTO> queryMchBill(OperateBillDTO operateBillDTO) {
        return operateOrderMapper.selectMchBill(operateBillDTO);
    }

    @Override
    public List<OperateBillDTO> queryMchBillInfo(OperateBillDTO operateBillDTO) {
        return operateOrderMapper.selectMchBillInfo(operateBillDTO);
    }

    @Override
    public List<OperateBillDTO> queryChaLabBillInfo(OperateBillDTO operateBillDTO) {
        return operateOrderMapper.selectChaLabBillInfo(operateBillDTO);
    }

    @Override
    public List<OperateBillDTO> queryMchStats(OperateBillDTO operateBillDTO) {
        return operateOrderMapper.selectMchStats(operateBillDTO);
    }

    @Override
    public List<OperateBillDTO> queryMchStatsInfo(OperateBillDTO operateBillDTO) {
        return operateOrderMapper.selectMchStatsInfo(operateBillDTO);
    }

    @Override
    public List<OperateBillDTO> queryProStats(OperateBillDTO operateBillDTO) {
        return operateOrderMapper.selectProStats(operateBillDTO);
    }

    @Override
    public List<OperateBillDTO> queryProStatsInfo(OperateBillDTO operateBillDTO) {
        return operateOrderMapper.selectProStatsInfo(operateBillDTO);
    }

    @Override
    public List<OperateBillDTO> queryChaLabStats(OperateBillDTO operateBillDTO) {
        return operateOrderMapper.selectChaLabStats(operateBillDTO);
    }

    @Override
    public List<OperateBillDTO> queryChaLabStatsInfo(OperateBillDTO operateBillDTO) {
        return operateOrderMapper.selectChaLabStatsInfo(operateBillDTO);
    }

    @Override
    public OperateOrder queryOperateOrderByOrderNo(Map<String, Object> params) {
        return operateOrderMapper.selectOperateOrderByOrderNo(params);
    }

    @Override
    public List<EChartsDTO> queryOrderByDate() {
        return operateOrderMapper.selectOrderByDate();
    }
}
