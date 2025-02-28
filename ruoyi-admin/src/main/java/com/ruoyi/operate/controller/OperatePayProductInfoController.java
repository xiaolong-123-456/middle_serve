package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.OperatePayProductInfo;
import com.ruoyi.operate.service.IOperatePayProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 支付产品详情信息Controller
 * 
 * @author master123
 * @date 2024-09-09
 */
@RestController
@RequestMapping("/operate/payProductInfo")
public class OperatePayProductInfoController extends BaseController
{
    @Autowired
    private IOperatePayProductInfoService operatePayProductInfoService;

    /**
     * 查询支付产品详情信息列表
     */
    @PreAuthorize("@ss.hasPermi('operate:payProduct:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperatePayProductInfo operatePayProductInfo)
    {
        startPage();
        List<OperatePayProductInfo> list = operatePayProductInfoService.selectOperatePayProductInfoList(operatePayProductInfo);
        return getDataTable(list);
    }

    /**
     * 导出支付产品详情信息列表
     */
    @PreAuthorize("@ss.hasPermi('operate:payProduct:export')")
    @Log(title = "支付产品详情信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperatePayProductInfo operatePayProductInfo)
    {
        List<OperatePayProductInfo> list = operatePayProductInfoService.selectOperatePayProductInfoList(operatePayProductInfo);
        ExcelUtil<OperatePayProductInfo> util = new ExcelUtil<OperatePayProductInfo>(OperatePayProductInfo.class);
        util.exportExcel(response, list, "支付产品详情信息数据");
    }

    /**
     * 获取支付产品详情信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payProduct:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operatePayProductInfoService.selectOperatePayProductInfoById(id));
    }

    /**
     * 新增支付产品详情信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payProduct:add')")
    @Log(title = "支付产品详情信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperatePayProductInfo operatePayProductInfo)
    {
        operatePayProductInfo.setCreateBy(getUsername());
        return toAjax(operatePayProductInfoService.insertOperatePayProductInfo(operatePayProductInfo));
    }

    /**
     * 修改支付产品详情信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payProduct:edit')")
    @Log(title = "支付产品详情信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperatePayProductInfo operatePayProductInfo)
    {
        return toAjax(operatePayProductInfoService.updateOperatePayProductInfo(operatePayProductInfo));
    }

    /**
     * 删除支付产品详情信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payProduct:remove')")
    @Log(title = "支付产品详情信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operatePayProductInfoService.deleteOperatePayProductInfoByIds(ids));
    }

}
