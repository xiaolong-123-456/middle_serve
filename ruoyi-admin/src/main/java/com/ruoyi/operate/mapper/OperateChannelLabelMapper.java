package com.ruoyi.operate.mapper;

import com.ruoyi.operate.domain.OperateChannelLabel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 通道标识信息Mapper接口
 * 
 * @author master123
 * @date 2024-09-03
 */
@Mapper
public interface OperateChannelLabelMapper 
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
     * 删除通道标识信息
     * 
     * @param id 通道标识信息主键
     * @return 结果
     */
    public int deleteOperateChannelLabelById(Long id);

    /**
     * 批量删除通道标识信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOperateChannelLabelByIds(Long[] ids);

    /**
     * 查询所有通道标识
     */
    public List<OperateChannelLabel> selectChannelLabelAll();

    /**
     * 根据通道标识代码 查询通道标识信息
     *
     * @param code 通道标识代码
     * @return 通道标识信息
     */
    OperateChannelLabel selectOperateChannelLabelByCode(String code);

    /**
     * 根据飞机群id 查询商户通道标识
     *
     * @param tgmGroup 飞机群id
     * @return 结果
     */
    public  List<OperateChannelLabel> selectChaLabByGroupId(Long tgmGroup);
}
