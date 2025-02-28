package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysRobat;

import java.util.List;

/**
 * 机器人设置Mapper接口
 * 
 * @author maser123
 * @date 2024-11-18
 */
public interface SysRobatMapper 
{
    /**
     * 查询机器人设置
     * 
     * @param id 机器人设置主键
     * @return 机器人设置
     */
    public SysRobat selectSysRobatById(Long id);

    /**
     * 查询机器人设置
     *
     * @param userId 用户id
     * @return 机器人设置
     */
    public SysRobat selectSysRobatByUserId(Long userId);

    /**
     * 查询所有用户的机器人设置
     *
     * @return 机器人设置
     */
    public List<SysRobat> selectSysRobatAll();

    /**
     * 查询机器人设置列表
     * 
     * @param sysRobat 机器人设置
     * @return 机器人设置集合
     */
    public List<SysRobat> selectSysRobatList(SysRobat sysRobat);

    /**
     * 新增机器人设置
     * 
     * @param sysRobat 机器人设置
     * @return 结果
     */
    public int insertSysRobat(SysRobat sysRobat);

    /**
     * 修改机器人设置
     * 
     * @param sysRobat 机器人设置
     * @return 结果
     */
    public int updateSysRobat(SysRobat sysRobat);

    /**
     * 删除机器人设置
     * 
     * @param id 机器人设置主键
     * @return 结果
     */
    public int deleteSysRobatById(Long id);

    /**
     * 批量删除机器人设置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysRobatByIds(Long[] ids);
}
