package com.ruoyi.operate.controller;

import com.github.pagehelper.PageInfo;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.OperateReplenishment;
import com.ruoyi.operate.service.IOperateReplenishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

/**
 * 补单记录Controller
 * 
 * @author master123
 * @date 2024-12-25
 */
@RestController
@RequestMapping("/operate/replenishment")
public class OperateReplenishmentController extends BaseController
{
    @Autowired
    private IOperateReplenishmentService operateReplenishmentService;

    /**
     * 查询补单记录列表
     */
    @PreAuthorize("@ss.hasPermi('operate:replenishment:list')")
    @GetMapping("/list")
    public AjaxResult list(OperateReplenishment operateReplenishment)
    {
        //没有分页的数据
        List<OperateReplenishment> dataList = operateReplenishmentService.selectOperateReplenishmentList(operateReplenishment);

        startPage();
        List<OperateReplenishment> pageList = operateReplenishmentService.selectOperateReplenishmentList(operateReplenishment);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", pageList);
        ajax.put("total", new PageInfo(pageList).getTotal());
        //补单总金额
        BigDecimal orderTotalAmount = dataList.stream()
                .map(val -> val.getPaymentAmount() != null ? val.getPaymentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("orderTotalAmount", orderTotalAmount);

        return success(ajax);
    }

    /**
     * 导出补单记录列表
     */
    @PreAuthorize("@ss.hasPermi('operate:replenishment:export')")
    @Log(title = "补单记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperateReplenishment operateReplenishment)
    {
        List<OperateReplenishment> list = operateReplenishmentService.selectOperateReplenishmentList(operateReplenishment);
        ExcelUtil<OperateReplenishment> util = new ExcelUtil<OperateReplenishment>(OperateReplenishment.class);
        util.exportExcel(response, list, "补单记录数据");
    }

    /**
     * 获取补单记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:replenishment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operateReplenishmentService.selectOperateReplenishmentById(id));
    }

    /**
     * 新增补单记录
     */
    @PreAuthorize("@ss.hasPermi('operate:replenishment:add')")
    @Log(title = "补单记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateReplenishment operateReplenishment)
    {
        return toAjax(operateReplenishmentService.insertOperateReplenishment(operateReplenishment));
    }

    /**
     * 修改补单记录
     */
    @PreAuthorize("@ss.hasPermi('operate:replenishment:edit')")
    @Log(title = "补单记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateReplenishment operateReplenishment)
    {
        return toAjax(operateReplenishmentService.updateOperateReplenishment(operateReplenishment));
    }

    /**
     * 删除补单记录
     */
    @PreAuthorize("@ss.hasPermi('operate:replenishment:remove')")
    @Log(title = "补单记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operateReplenishmentService.deleteOperateReplenishmentByIds(ids));
    }
}
