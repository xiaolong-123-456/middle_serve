package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateChannelLabel;
import com.ruoyi.operate.mapper.OperateChannelLabelMapper;
import com.ruoyi.operate.service.IOperateChannelLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通道标识信息Service业务层处理
 * 
 * @author master123
 * @date 2024-09-03
 */
@Service
public class OperateChannelLabelServiceImpl implements IOperateChannelLabelService 
{
    @Autowired
    private OperateChannelLabelMapper operateChannelLabelMapper;

    /**
     * 查询通道标识信息
     * 
     * @param id 通道标识信息主键
     * @return 通道标识信息
     */
    @Override
    public OperateChannelLabel selectOperateChannelLabelById(Long id)
    {
        return operateChannelLabelMapper.selectOperateChannelLabelById(id);
    }

    /**
     * 查询通道标识信息列表
     * 
     * @param operateChannelLabel 通道标识信息
     * @return 通道标识信息
     */
    @Override
    public List<OperateChannelLabel> selectOperateChannelLabelList(OperateChannelLabel operateChannelLabel)
    {
        return operateChannelLabelMapper.selectOperateChannelLabelList(operateChannelLabel);
    }

    /**
     * 新增通道标识信息
     * 
     * @param operateChannelLabel 通道标识信息
     * @return 结果
     */
    @Override
    public int insertOperateChannelLabel(OperateChannelLabel operateChannelLabel)
    {
        operateChannelLabel.setCreateTime(DateUtils.getNowDate());
        return operateChannelLabelMapper.insertOperateChannelLabel(operateChannelLabel);
    }

    /**
     * 修改通道标识信息
     * 
     * @param operateChannelLabel 通道标识信息
     * @return 结果
     */
    @Override
    public int updateOperateChannelLabel(OperateChannelLabel operateChannelLabel)
    {
        operateChannelLabel.setUpdateTime(DateUtils.getNowDate());
        return operateChannelLabelMapper.updateOperateChannelLabel(operateChannelLabel);
    }

    /**
     * 批量删除通道标识信息
     * 
     * @param ids 需要删除的通道标识信息主键
     * @return 结果
     */
    @Override
    public int deleteOperateChannelLabelByIds(Long[] ids)
    {
        return operateChannelLabelMapper.deleteOperateChannelLabelByIds(ids);
    }

    /**
     * 删除通道标识信息信息
     * 
     * @param id 通道标识信息主键
     * @return 结果
     */
    @Override
    public int deleteOperateChannelLabelById(Long id)
    {
        return operateChannelLabelMapper.deleteOperateChannelLabelById(id);
    }

    @Override
    public List<OperateChannelLabel> selectChannelLabelAll()
    {
        return operateChannelLabelMapper.selectChannelLabelAll();
    }

    @Override
    public OperateChannelLabel selectOperateChannelLabelByCode(String code) {
        return operateChannelLabelMapper.selectOperateChannelLabelByCode(code);
    }

    @Override
    public List<OperateChannelLabel> queryChaLabByGroupId(Long groupId) {
        return operateChannelLabelMapper.selectChaLabByGroupId(groupId);
    }
}
