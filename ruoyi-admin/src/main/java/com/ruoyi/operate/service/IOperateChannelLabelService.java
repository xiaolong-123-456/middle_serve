package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperateChannelLabel;

import java.util.List;

/**
 * 通道标识信息Service接口
 * 
 * @author master123
 * @date 2024-09-03
 */
public interface IOperateChannelLabelService 
{
    /**
     * 查询通道标识信息
     * 
     * @param id 通道标识信息主键
     * @return 通道标识信息
     */
    public OperateChannelLabel selectOperateChannelLabelById(Long id);

    /**
     * 查询通道标识信息列表
     * 
     * @param operateChannelLabel 通道标识信息
     * @return 通道标识信息集合
     */
    public List<OperateChannelLabel> selectOperateChannelLabelList(OperateChannelLabel operateChannelLabel);

    /**
     * 新增通道标识信息
     * 
     * @param operateChannelLabel 通道标识信息
     * @return 结果
     */
    public int insertOperateChannelLabel(OperateChannelLabel operateChannelLabel);

    /**
     * 修改通道标识信息
     * 
     * @param operateChannelLabel 通道标识信息
     * @return 结果
     */
    public int updateOperateChannelLabel(OperateChannelLabel operateChannelLabel);

    /**
     * 批量删除通道标识信息
     * 
     * @param ids 需要删除的通道标识信息主键集合
     * @return 结果
     */
    public int deleteOperateChannelLabelByIds(Long[] ids);

    /**
     * 删除通道标识信息信息
     * 
     * @param id 通道标识信息主键
     * @return 结果
     */
    public int deleteOperateChannelLabelById(Long id);

    /**
     * 查询所有通道标识
     *
     * @return 通道标识列表
     */
    public List<OperateChannelLabel> selectChannelLabelAll();

    /**
     * 根据通道标识代码 查询通道标识信息
     *
     * @param code 通道标识代码
     * @return 通道标识信息
     */
    public OperateChannelLabel selectOperateChannelLabelByCode(String code);

    /**
     * 根据群id找到上游
     *
     * @param code 通道标识代码
     * @return 通道标识信息
     */
    public List<OperateChannelLabel> queryChaLabByGroupId(Long code);


}
