package com.ruoyi.operate.mapper;

import com.ruoyi.operate.domain.OperateAgent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 所有代理商Mapper接口
 * 
 * @author master123
 * @date 2024-08-26
 */
@Mapper
public interface OperateAgentMapper 
{
    /**
     * 查询所有代理商
     * 
     * @param id 所有代理商主键
     * @return 所有代理商
     */
    public OperateAgent selectOperateAgentById(Long id);

    /**
     * 查询所有代理商列表
     * 
     * @param operateAgent 所有代理商
     * @return 所有代理商集合
     */
    public List<OperateAgent> selectOperateAgentList(OperateAgent operateAgent);

    /**
     * 新增所有代理商
     * 
     * @param operateAgent 所有代理商
     * @return 结果
     */
    public int insertOperateAgent(OperateAgent operateAgent);

    /**
     * 修改所有代理商
     * 
     * @param operateAgent 所有代理商
     * @return 结果
     */
    public int updateOperateAgent(OperateAgent operateAgent);

    /**
     * 删除所有代理商
     * 
     * @param id 所有代理商主键
     * @return 结果
     */
    public int deleteOperateAgentById(Long id);

    /**
     * 批量删除所有代理商
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOperateAgentByIds(Long[] ids);

    List<OperateAgent> selectOperateAgentAll();

    /**
     * 查询代理商
     * @param userId 用户id
     * @return 所有代理商
     */
    public OperateAgent selectOperateAgentByUserId(Long userId);
}
