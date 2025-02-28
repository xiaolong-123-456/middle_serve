package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SysRobat;
import com.ruoyi.system.mapper.SysRobatMapper;
import com.ruoyi.system.service.ISysRobatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 机器人设置Service业务层处理
 * 
 * @author maser123
 * @date 2024-11-18
 */
@Service
public class SysRobatServiceImpl implements ISysRobatService 
{
    @Autowired
    private SysRobatMapper sysRobatMapper;

    /**
     * 查询机器人设置
     * 
     * @param id 机器人设置主键
     * @return 机器人设置
     */
    @Override
    public SysRobat selectSysRobatById(Long id)
    {
        return sysRobatMapper.selectSysRobatById(id);
    }

    @Override
    public SysRobat selectSysRobatByUserId(Long userId) {
        return sysRobatMapper.selectSysRobatByUserId(userId);
    }

    @Override
    public List<SysRobat> selectSysRobatAll() {
        return sysRobatMapper.selectSysRobatAll();
    }

    /**
     * 查询机器人设置列表
     * 
     * @param sysRobat 机器人设置
     * @return 机器人设置
     */
    @Override
    public List<SysRobat> selectSysRobatList(SysRobat sysRobat)
    {
        return sysRobatMapper.selectSysRobatList(sysRobat);
    }

    /**
     * 新增机器人设置
     * 
     * @param sysRobat 机器人设置
     * @return 结果
     */
    @Override
    public int insertSysRobat(SysRobat sysRobat)
    {
        sysRobat.setCreateTime(DateUtils.getNowDate());
        return sysRobatMapper.insertSysRobat(sysRobat);
    }

    /**
     * 修改机器人设置
     * 
     * @param sysRobat 机器人设置
     * @return 结果
     */
    @Override
    public int updateSysRobat(SysRobat sysRobat)
    {
        sysRobat.setUpdateTime(DateUtils.getNowDate());
        return sysRobatMapper.updateSysRobat(sysRobat);
    }

    /**
     * 批量删除机器人设置
     * 
     * @param ids 需要删除的机器人设置主键
     * @return 结果
     */
    @Override
    public int deleteSysRobatByIds(Long[] ids)
    {
        return sysRobatMapper.deleteSysRobatByIds(ids);
    }

    /**
     * 删除机器人设置信息
     * 
     * @param id 机器人设置主键
     * @return 结果
     */
    @Override
    public int deleteSysRobatById(Long id)
    {
        return sysRobatMapper.deleteSysRobatById(id);
    }
}
