package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.OperateMchProAlone;
import com.ruoyi.operate.service.IOperateMchProAloneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 商户和产品单独配置Controller
 * 
 * @author master123
 * @date 2024-09-13
 */
@RestController
@RequestMapping("/operate/mchProAlone")
public class OperateMchProAloneController extends BaseController
{
    @Autowired
    private IOperateMchProAloneService operateMchProAloneService;

    /**
     * 查询商户和产品单独配置列表
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperateMchProAlone operateMchProAlone)
    {
        startPage();
        List<OperateMchProAlone> list = operateMchProAloneService.selectOperateMchProAloneList(operateMchProAlone);
        return getDataTable(list);
    }

    /**
     * 导出商户和产品单独配置列表
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:export')")
    @Log(title = "商户和产品单独配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperateMchProAlone operateMchProAlone)
    {
        List<OperateMchProAlone> list = operateMchProAloneService.selectOperateMchProAloneList(operateMchProAlone);
        ExcelUtil<OperateMchProAlone> util = new ExcelUtil<OperateMchProAlone>(OperateMchProAlone.class);
        util.exportExcel(response, list, "商户和产品单独配置数据");
    }

    /**
     * 获取商户和产品单独配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operateMchProAloneService.selectOperateMchProAloneById(id));
    }

    /**
     * 新增商户和产品单独配置
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:add')")
    @Log(title = "商户和产品单独配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateMchProAlone operateMchProAlone)
    {
        operateMchProAlone.setCreateBy(getUsername());
        return toAjax(operateMchProAloneService.insertOperateMchProAlone(operateMchProAlone));
    }

    /**
     * 修改商户和产品单独配置
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:edit')")
    @Log(title = "商户和产品单独配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateMchProAlone operateMchProAlone)
    {
        return toAjax(operateMchProAloneService.updateOperateMchProAlone(operateMchProAlone));
    }

    /**
     * 删除商户和产品单独配置
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:remove')")
    @Log(title = "商户和产品单独配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operateMchProAloneService.deleteOperateMchProAloneByIds(ids));
    }


}
