package com.ruoyi.operate.mapper;

import com.ruoyi.operate.domain.OperatePayChannel;
import com.ruoyi.operate.dto.OperatePayChannelDTO;
import com.ruoyi.operate.dto.OperatePayProjectInfoDTO;
import com.ruoyi.operate.dto.OperateRiskControlDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 支付通道信息Mapper接口
 * 
 * @author master123
 * @date 2024-09-03
 */
@Mapper
public interface OperatePayChannelMapper 
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
     * 删除支付通道信息
     * 
     * @param id 支付通道信息主键
     * @return 结果
     */
    public int deleteOperatePayChannelById(Long id);

    /**
     * 批量删除支付通道信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOperatePayChannelByIds(Long[] ids);

    /**
     * 查询所有通道标识
     */
    public List<OperatePayProjectInfoDTO> selectPayChannel(OperatePayChannel operatePayChannel);

    /**
     * 查询风控配置
     */
    public OperateRiskControlDTO selectRiskByPayChaId(Long id);

    /**
     * 启用禁用
     */
    int forbidOrEnableByIds(Map<String, Object> params);
}
