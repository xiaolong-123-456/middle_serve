package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.OperateMchExpend;
import com.ruoyi.operate.service.IOperateMchExpendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 商户下发Controller
 * 
 * @author master123
 * @date 2024-09-27
 */
@RestController
@RequestMapping("/operate/mchExpend")
public class OperateMchExpendController extends BaseController
{
    @Autowired
    private IOperateMchExpendService operateMchExpendService;

    /**
     * 查询商户下发列表
     */
    @PreAuthorize("@ss.hasPermi('operate:mchExpend:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperateMchExpend operateMchExpend)
    {
        startPage();
        List<OperateMchExpend> list = operateMchExpendService.selectOperateMchExpendList(operateMchExpend);
        return getDataTable(list);
    }

    /**
     * 导出商户下发列表
     */
    @PreAuthorize("@ss.hasPermi('operate:mchExpend:export')")
    @Log(title = "商户下发", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperateMchExpend operateMchExpend)
    {
        List<OperateMchExpend> list = operateMchExpendService.selectOperateMchExpendList(operateMchExpend);
        ExcelUtil<OperateMchExpend> util = new ExcelUtil<OperateMchExpend>(OperateMchExpend.class);
        util.exportExcel(response, list, "商户下发数据");
    }

    /**
     * 获取商户下发详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:mchExpend:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operateMchExpendService.selectOperateMchExpendById(id));
    }

    /**
     * 新增商户下发
     */
    @PreAuthorize("@ss.hasPermi('operate:mchExpend:add')")
    @Log(title = "商户下发", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateMchExpend operateMchExpend)
    {
        operateMchExpend.setCreateBy(getUsername());
        return toAjax(operateMchExpendService.insertOperateMchExpend(operateMchExpend));
    }

    /**
     * 修改商户下发
     */
    @PreAuthorize("@ss.hasPermi('operate:mchExpend:edit')")
    @Log(title = "商户下发", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateMchExpend operateMchExpend)
    {
        return toAjax(operateMchExpendService.updateOperateMchExpend(operateMchExpend));
    }

    /**
     * 删除商户下发
     */
    @PreAuthorize("@ss.hasPermi('operate:mchExpend:remove')")
    @Log(title = "商户下发", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operateMchExpendService.deleteOperateMchExpendByIds(ids));
    }
}
