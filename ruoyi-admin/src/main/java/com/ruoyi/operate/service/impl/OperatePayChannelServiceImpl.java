package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperatePayChannel;
import com.ruoyi.operate.dto.OperatePayChannelDTO;
import com.ruoyi.operate.dto.OperatePayProjectInfoDTO;
import com.ruoyi.operate.dto.OperateRiskControlDTO;
import com.ruoyi.operate.mapper.OperatePayChannelMapper;
import com.ruoyi.operate.service.IOperatePayChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 支付通道信息Service业务层处理
 * 
 * @author master123
 * @date 2024-09-03
 */
@Service
public class OperatePayChannelServiceImpl implements IOperatePayChannelService 
{
    @Autowired
    private OperatePayChannelMapper operatePayChannelMapper;

    /**
     * 查询支付通道信息
     * 
     * @param id 支付通道信息主键
     * @return 支付通道信息
     */
    @Override
    public OperatePayChannel selectOperatePayChannelById(Long id)
    {
        return operatePayChannelMapper.selectOperatePayChannelById(id);
    }

    /**
     * 查询支付通道信息列表
     * 
     * @param operatePayChannel 支付通道信息
     * @return 支付通道信息
     */
    @Override
    public List<OperatePayChannelDTO> selectOperatePayChannelList(OperatePayChannel operatePayChannel)
    {
        return operatePayChannelMapper.selectOperatePayChannelList(operatePayChannel);
    }

    /**
     * 新增支付通道信息
     * 
     * @param operatePayChannel 支付通道信息
     * @return 结果
     */
    @Override
    public int insertOperatePayChannel(OperatePayChannel operatePayChannel)
    {
        //保存风控信息

        operatePayChannel.setCreateTime(DateUtils.getNowDate());
        return operatePayChannelMapper.insertOperatePayChannel(operatePayChannel);
    }

    /**
     * 修改支付通道信息
     * 
     * @param operatePayChannel 支付通道信息
     * @return 结果
     */
    @Override
    public int updateOperatePayChannel(OperatePayChannel operatePayChannel)
    {
        operatePayChannel.setUpdateTime(DateUtils.getNowDate());
        return operatePayChannelMapper.updateOperatePayChannel(operatePayChannel);
    }

    /**
     * 批量删除支付通道信息
     * 
     * @param ids 需要删除的支付通道信息主键
     * @return 结果
     */
    @Override
    public int deleteOperatePayChannelByIds(Long[] ids)
    {
        return operatePayChannelMapper.deleteOperatePayChannelByIds(ids);
    }

    /**
     * 删除支付通道信息信息
     * 
     * @param id 支付通道信息主键
     * @return 结果
     */
    @Override
    public int deleteOperatePayChannelById(Long id)
    {
        return operatePayChannelMapper.deleteOperatePayChannelById(id);
    }

    @Override
    public List<OperatePayProjectInfoDTO> selectPayChannel(OperatePayChannel operatePayChannel) {
        return operatePayChannelMapper.selectPayChannel(operatePayChannel);
    }

    @Override
    public OperateRiskControlDTO selectRiskByPayChaId(Long id) {
        return operatePayChannelMapper.selectRiskByPayChaId(id);
    }

    @Override
    public int forbidOrEnableByIds(Map<String, Object> params) {
        return operatePayChannelMapper.forbidOrEnableByIds(params);
    }
}
