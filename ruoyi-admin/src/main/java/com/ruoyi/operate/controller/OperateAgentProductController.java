package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.OperateAgentProduct;
import com.ruoyi.operate.service.IOperateAgentProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 代理商和产品配置Controller
 * 
 * @author master123
 * @date 2024-09-20
 */
@RestController
@RequestMapping("/operate/agentProduct")
public class OperateAgentProductController extends BaseController
{
    @Autowired
    private IOperateAgentProductService operateAgentProductService;

    /**
     * 查询代理商和产品配置列表
     */
    @PreAuthorize("@ss.hasPermi('operate:agentProduct:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperateAgentProduct operateAgentProduct)
    {
        startPage();
        List<OperateAgentProduct> list = operateAgentProductService.selectOperateAgentProductList(operateAgentProduct);
        return getDataTable(list);
    }

    /**
     * 导出代理商和产品配置列表
     */
    @PreAuthorize("@ss.hasPermi('operate:agent:export')")
    @Log(title = "代理商和产品配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperateAgentProduct operateAgentProduct)
    {
        List<OperateAgentProduct> list = operateAgentProductService.selectOperateAgentProductList(operateAgentProduct);
        ExcelUtil<OperateAgentProduct> util = new ExcelUtil<OperateAgentProduct>(OperateAgentProduct.class);
        util.exportExcel(response, list, "代理商和产品配置数据");
    }

    /**
     * 获取代理商和产品配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:agent:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operateAgentProductService.selectOperateAgentProductById(id));
    }

    /**
     * 新增代理商和产品配置
     */
    @PreAuthorize("@ss.hasPermi('operate:agent:add')")
    @Log(title = "代理商和产品配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateAgentProduct operateAgentProduct)
    {
        operateAgentProduct.setCreateBy(getUsername());
        return toAjax(operateAgentProductService.insertOperateAgentProduct(operateAgentProduct));
    }

    /**
     * 修改代理商和产品配置
     */
    @PreAuthorize("@ss.hasPermi('operate:agent:edit')")
    @Log(title = "代理商和产品配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateAgentProduct operateAgentProduct)
    {
        return toAjax(operateAgentProductService.updateOperateAgentProduct(operateAgentProduct));
    }

    /**
     * 删除代理商和产品配置
     */
    @PreAuthorize("@ss.hasPermi('operate:agent:remove')")
    @Log(title = "代理商和产品配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operateAgentProductService.deleteOperateAgentProductByIds(ids));
    }
}
