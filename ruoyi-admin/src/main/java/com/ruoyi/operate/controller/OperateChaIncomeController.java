package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.OperateChaIncome;
import com.ruoyi.operate.service.IOperateChaIncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 渠道下发Controller
 * 
 * @author master123
 * @date 2024-09-27
 */
@RestController
@RequestMapping("/operate/chaIncome")
public class OperateChaIncomeController extends BaseController
{
    @Autowired
    private IOperateChaIncomeService operateChaIncomeService;

    /**
     * 查询渠道下发列表
     */
    @PreAuthorize("@ss.hasPermi('operate:chaIncome:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperateChaIncome operateChaIncome)
    {
        startPage();
        List<OperateChaIncome> list = operateChaIncomeService.selectOperateChaIncomeList(operateChaIncome);
        return getDataTable(list);
    }

    /**
     * 导出渠道下发列表
     */
    @PreAuthorize("@ss.hasPermi('operate:chaIncome:export')")
    @Log(title = "渠道下发", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperateChaIncome operateChaIncome)
    {
        List<OperateChaIncome> list = operateChaIncomeService.selectOperateChaIncomeList(operateChaIncome);
        ExcelUtil<OperateChaIncome> util = new ExcelUtil<OperateChaIncome>(OperateChaIncome.class);
        util.exportExcel(response, list, "渠道下发数据");
    }

    /**
     * 获取渠道下发详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:chaIncome:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operateChaIncomeService.selectOperateChaIncomeById(id));
    }

    /**
     * 新增渠道下发
     */
    @PreAuthorize("@ss.hasPermi('operate:chaIncome:add')")
    @Log(title = "渠道下发", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateChaIncome operateChaIncome)
    {
        operateChaIncome.setCreateBy(getUsername());
        return toAjax(operateChaIncomeService.insertOperateChaIncome(operateChaIncome));
    }

    /**
     * 修改渠道下发
     */
    @PreAuthorize("@ss.hasPermi('operate:chaIncome:edit')")
    @Log(title = "渠道下发", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateChaIncome operateChaIncome)
    {
        return toAjax(operateChaIncomeService.updateOperateChaIncome(operateChaIncome));
    }

    /**
     * 删除渠道下发
     */
    @PreAuthorize("@ss.hasPermi('operate:chaIncome:remove')")
    @Log(title = "渠道下发", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operateChaIncomeService.deleteOperateChaIncomeByIds(ids));
    }
}
