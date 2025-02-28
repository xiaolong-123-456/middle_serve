package com.ruoyi.operate.mapper;

import com.ruoyi.operate.domain.OperateOrder;
import com.ruoyi.operate.dto.EChartsDTO;
import com.ruoyi.operate.dto.OperateBillDTO;
import com.ruoyi.operate.dto.OperateOrderDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 订单数据Mapper接口
 * 
 * @author master123
 * @date 2024-09-25
 */
@Mapper
public interface OperateOrderMapper 
{
    /**
     * 查询订单数据
     * 
     * @param id 订单数据主键
     * @return 订单数据
     */
    public OperateOrder selectOperateOrderById(Long id);

    public OperateOrder selectOrderByParams(Map<String, Object> params);

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
    List<OperateOrder> selectOperateOrderYesterdayList(OperateOrderDTO operateOrderDTO);

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
     * 删除订单数据
     * 
     * @param id 订单数据主键
     * @return 结果
     */
    public int deleteOperateOrderById(Long id);

    /**
     * 批量删除订单数据
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOperateOrderByIds(Long[] ids);

    /**
     * 商户对账列表
     *
     * @param operateBillDTO
     * @return 结果
     */
    public List<OperateBillDTO> selectMchBill(OperateBillDTO operateBillDTO);

    /**
     * 渠道对账列表
     *
     * @param operateBillDTO
     * @return 结果
     */
    public List<OperateBillDTO> selectChaLabBill(OperateBillDTO operateBillDTO);

    /**
     * 商户对账查看详情
     *
     * @param operateBillDTO 参数
     * @return 结果
     */
    List<OperateBillDTO> selectMchBillInfo(OperateBillDTO operateBillDTO);

    /**
     * 渠道对账查看详情
     *
     * @param operateBillDTO 参数
     * @return 结果
     */
    List<OperateBillDTO> selectChaLabBillInfo(OperateBillDTO operateBillDTO);

    /**
     * 商户统计列表
     *
     * @param operateBillDTO 参数
     * @return 结果
     */
    List<OperateBillDTO> selectMchStats(OperateBillDTO operateBillDTO);

    /**
     * 商户统计查看详情
     *
     * @param operateBillDTO 参数
     * @return 结果
     */
    List<OperateBillDTO> selectMchStatsInfo(OperateBillDTO operateBillDTO);

    /**
     * 产品统计列表
     *
     * @param operateBillDTO 参数
     * @return 结果
     */
    List<OperateBillDTO> selectProStats(OperateBillDTO operateBillDTO);

    /**
     * 产品统计查看详情
     *
     * @param operateBillDTO 参数
     * @return 结果
     */
    List<OperateBillDTO> selectProStatsInfo(OperateBillDTO operateBillDTO);


    /**
     * 渠道统计列表
     *
     * @param operateBillDTO 参数
     * @return 结果
     */
    List<OperateBillDTO> selectChaLabStats(OperateBillDTO operateBillDTO);

    /**
     * 渠道统计查看详情
     *
     * @param operateBillDTO 参数
     * @return 结果
     */
    List<OperateBillDTO> selectChaLabStatsInfo(OperateBillDTO operateBillDTO);

    /**
     * 根据商户订单号或者本渠道订单 查询订单数据
     *
     * @param params 订单数据主键
     * @return 订单数据
     */
    public OperateOrder selectOperateOrderByOrderNo(Map<String, Object> params);

    /**
     * 首页折线图表统计
     */
    public List<EChartsDTO> selectOrderByDate();
}
