package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.OperateChannelLabel;
import com.ruoyi.operate.service.IOperateChannelLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 通道标识信息Controller
 * 
 * @author master123
 * @date 2024-09-03
 */
@RestController
@RequestMapping("/operate/channelLabel")
public class OperateChannelLabelController extends BaseController
{
    @Autowired
    private IOperateChannelLabelService operateChannelLabelService;

    /**
     * 查询通道标识信息列表
     */
    @PreAuthorize("@ss.hasPermi('operate:channelLabel:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperateChannelLabel operateChannelLabel)
    {
        startPage();
        List<OperateChannelLabel> list = operateChannelLabelService.selectOperateChannelLabelList(operateChannelLabel);
        return getDataTable(list);
    }

    @GetMapping("/dataList")
    @PreAuthorize("@ss.hasPermi('operate:channelLabel:list')")
    public AjaxResult dataList(OperateChannelLabel operateChannelLabel)
    {
        List<OperateChannelLabel> list = operateChannelLabelService.selectOperateChannelLabelList(operateChannelLabel);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("dataList", list);
        return ajax;
    }

    /**
     * 导出通道标识信息列表
     */
    @PreAuthorize("@ss.hasPermi('operate:channelLabel:export')")
    @Log(title = "通道标识信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperateChannelLabel operateChannelLabel)
    {
        List<OperateChannelLabel> list = operateChannelLabelService.selectOperateChannelLabelList(operateChannelLabel);
        ExcelUtil<OperateChannelLabel> util = new ExcelUtil<OperateChannelLabel>(OperateChannelLabel.class);
        util.exportExcel(response, list, "通道标识信息数据");
    }

    /**
     * 获取通道标识信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:channelLabel:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operateChannelLabelService.selectOperateChannelLabelById(id));
    }

    /**
     * 新增通道标识信息
     */
    @PreAuthorize("@ss.hasPermi('operate:channelLabel:add')")
    @Log(title = "通道标识信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateChannelLabel operateChannelLabel)
    {
        operateChannelLabel.setCreateBy(getUsername());
        return toAjax(operateChannelLabelService.insertOperateChannelLabel(operateChannelLabel));
    }

    /**
     * 修改通道标识信息
     */
    @PreAuthorize("@ss.hasPermi('operate:channelLabel:edit')")
    @Log(title = "通道标识信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateChannelLabel operateChannelLabel)
    {
        operateChannelLabel.setUpdateBy(getUsername());
        return toAjax(operateChannelLabelService.updateOperateChannelLabel(operateChannelLabel));
    }

    /**
     * 删除通道标识信息
     */
    @PreAuthorize("@ss.hasPermi('operate:channelLabel:remove')")
    @Log(title = "通道标识信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operateChannelLabelService.deleteOperateChannelLabelByIds(ids));
    }
}
