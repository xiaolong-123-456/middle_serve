package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysRobat;
import com.ruoyi.system.service.ISysRobatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 机器人设置Controller
 * 
 * @author maser123
 * @date 2024-11-18
 */
@RestController
@RequestMapping("/system/robat")
public class SysRobatController extends BaseController
{
    @Autowired
    private ISysRobatService sysRobatService;

    /**
     * 查询机器人设置列表
     */
    @PreAuthorize("@ss.hasPermi('system:robat:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysRobat sysRobat)
    {
        startPage();
        List<SysRobat> list = sysRobatService.selectSysRobatList(sysRobat);
        return getDataTable(list);
    }

    /**
     * 导出机器人设置列表
     */
    @PreAuthorize("@ss.hasPermi('system:robat:export')")
    @Log(title = "机器人设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRobat sysRobat)
    {
        List<SysRobat> list = sysRobatService.selectSysRobatList(sysRobat);
        ExcelUtil<SysRobat> util = new ExcelUtil<SysRobat>(SysRobat.class);
        util.exportExcel(response, list, "机器人设置数据");
    }

    /**
     * 获取机器人设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:robat:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(sysRobatService.selectSysRobatById(id));
    }

    /**
     * 获取机器人设置详细信息
     */
    @GetMapping("/getInfo")
    public AjaxResult getInfo()
    {
        return success(sysRobatService.selectSysRobatById(Long.valueOf("1")));
    }

    /**
     * 新增机器人设置
     */
    @PreAuthorize("@ss.hasPermi('system:robat:add')")
    @Log(title = "机器人设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysRobat sysRobat)
    {

        return toAjax(sysRobatService.insertSysRobat(sysRobat));
    }

    /**
     * 修改机器人设置
     */
    @PreAuthorize("@ss.hasPermi('system:robat:edit')")
    @Log(title = "机器人设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysRobat sysRobat)
    {
        sysRobat.setUpdateBy(getUsername());
        return toAjax(sysRobatService.updateSysRobat(sysRobat));
    }

    /**
     * 删除机器人设置
     */
    @PreAuthorize("@ss.hasPermi('system:robat:remove')")
    @Log(title = "机器人设置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(sysRobatService.deleteSysRobatByIds(ids));
    }
}
