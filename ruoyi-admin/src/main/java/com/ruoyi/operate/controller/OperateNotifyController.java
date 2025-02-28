package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.OperateNotify;
import com.ruoyi.operate.service.IOperateNotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 回调记录Controller
 * 
 * @author master123
 * @date 2024-12-25
 */
@RestController
@RequestMapping("/operate/notify")
public class OperateNotifyController extends BaseController
{
    @Autowired
    private IOperateNotifyService operateNotifyService;

    /**
     * 查询回调记录列表
     */
    @PreAuthorize("@ss.hasPermi('operate:notify:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperateNotify operateNotify)
    {
        startPage();
        List<OperateNotify> list = operateNotifyService.selectOperateNotifyList(operateNotify);
        return getDataTable(list);
    }

    /**
     * 导出回调记录列表
     */
    @PreAuthorize("@ss.hasPermi('operate:notify:export')")
    @Log(title = "回调记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperateNotify operateNotify)
    {
        List<OperateNotify> list = operateNotifyService.selectOperateNotifyList(operateNotify);
        ExcelUtil<OperateNotify> util = new ExcelUtil<OperateNotify>(OperateNotify.class);
        util.exportExcel(response, list, "回调记录数据");
    }

    /**
     * 获取回调记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:notify:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operateNotifyService.selectOperateNotifyById(id));
    }

    /**
     * 新增回调记录
     */
    @PreAuthorize("@ss.hasPermi('operate:notify:add')")
    @Log(title = "回调记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateNotify operateNotify)
    {
        return toAjax(operateNotifyService.insertOperateNotify(operateNotify));
    }

    /**
     * 修改回调记录
     */
    @PreAuthorize("@ss.hasPermi('operate:notify:edit')")
    @Log(title = "回调记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateNotify operateNotify)
    {
        return toAjax(operateNotifyService.updateOperateNotify(operateNotify));
    }

    /**
     * 删除回调记录
     */
    @PreAuthorize("@ss.hasPermi('operate:notify:remove')")
    @Log(title = "回调记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operateNotifyService.deleteOperateNotifyByIds(ids));
    }
}
