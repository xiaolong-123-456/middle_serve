package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperatePayChannel;
import com.ruoyi.operate.dto.OperatePayChannelDTO;
import com.ruoyi.operate.dto.OperatePayProjectInfoDTO;
import com.ruoyi.operate.dto.OperateRiskControlDTO;

import java.util.List;
import java.util.Map;

/**
 * 支付通道信息Service接口
 * 
 * @author master123
 * @date 2024-09-03
 */
public interface IOperatePayChannelService 
{
    /**
     * 查询支付通道信息
     * 
     * @param id 支付通道信息主键
     * @return 支付通道信息
     */
    public OperatePayChannel selectOperatePayChannelById(Long id);

    /**
     * 查询支付通道信息列表
     * 
     * @param operatePayChannel 支付通道信息
     * @return 支付通道信息集合
     */
    public List<OperatePayChannelDTO> selectOperatePayChannelList(OperatePayChannel operatePayChannel);

    /**
     * 新增支付通道信息
     * 
     * @param operatePayChannel 支付通道信息
     * @return 结果
     */
    public int insertOperatePayChannel(OperatePayChannel operatePayChannel);

    /**
     * 修改支付通道信息
     * 
     * @param operatePayChannel 支付通道信息
     * @return 结果
     */
    public int updateOperatePayChannel(OperatePayChannel operatePayChannel);

    /**
     * 批量删除支付通道信息
     * 
     * @param ids 需要删除的支付通道信息主键集合
     * @return 结果
     */
    public int deleteOperatePayChannelByIds(Long[] ids);

    /**
     * 删除支付通道信息信息
     * 
     * @param id 支付通道信息主键
     * @return 结果
     */
    public int deleteOperatePayChannelById(Long id);

    /**
     * 查询所有支付通道
     *
     * @return 支付通道列表
     */
    public List<OperatePayProjectInfoDTO> selectPayChannel(OperatePayChannel operatePayChannel);

    /**
     * 根据通道ID获取风控配置
     *
     * @return 风控配置
     */
    public OperateRiskControlDTO selectRiskByPayChaId(Long id);

    /**
     * 启用禁用
     *
     * @param params 参数
     * @return 结果
     */
    public int forbidOrEnableByIds(Map<String, Object> params);
}
