package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.OperateChaIncome;
import com.ruoyi.operate.domain.OperateMchChaRemit;
import com.ruoyi.operate.domain.OperateMchExpend;
import com.ruoyi.operate.service.IOperateChaIncomeService;
import com.ruoyi.operate.service.IOperateMchChaRemitService;
import com.ruoyi.operate.service.IOperateMchExpendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 商户和渠道打款Controller
 * 
 * @author master123
 * @date 2024-09-30
 */
@RestController
@RequestMapping("/operate/remit")
public class OperateMchChaRemitController extends BaseController
{
    @Autowired
    private IOperateMchChaRemitService operateMchChaRemitService;
    @Autowired
    private IOperateMchExpendService operateMchExpendService;
    @Autowired
    private IOperateChaIncomeService operateChaIncomeService;

    /**
     * 查询商户和渠道打款列表
     */
    @PreAuthorize("@ss.hasPermi('operate:remit:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperateMchChaRemit operateMchChaRemit)
    {
        startPage();
        List<OperateMchChaRemit> list = operateMchChaRemitService.selectOperateMchChaRemitList(operateMchChaRemit);
        return getDataTable(list);
    }

    /**
     * 导出商户和渠道打款列表
     */
    @PreAuthorize("@ss.hasPermi('operate:remit:export')")
    @Log(title = "商户和渠道打款", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperateMchChaRemit operateMchChaRemit)
    {
        List<OperateMchChaRemit> list = operateMchChaRemitService.selectOperateMchChaRemitList(operateMchChaRemit);
        ExcelUtil<OperateMchChaRemit> util = new ExcelUtil<OperateMchChaRemit>(OperateMchChaRemit.class);
        util.exportExcel(response, list, "商户和渠道打款数据");
    }

    /**
     * 获取商户和渠道打款详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:remit:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operateMchChaRemitService.selectOperateMchChaRemitById(id));
    }

    /**
     * 新增商户和渠道打款
     */
    @PreAuthorize("@ss.hasPermi('operate:remit:add')")
    @Log(title = "商户和渠道打款", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateMchChaRemit operateMchChaRemit)
    {
        operateMchChaRemit.setCreateBy(getUsername());
        //商户下发
        OperateMchExpend operateMchExpend = new OperateMchExpend();
        operateMchExpend.setExpendAmount(operateMchChaRemit.getRemitAmount());
        operateMchExpend.setMchId(operateMchChaRemit.getMchId());
        operateMchExpend.setMchName(operateMchChaRemit.getMchName());
        operateMchExpend.setExpendDate(operateMchChaRemit.getRemitDate());
        operateMchExpend.setRemark("打款管理操作");
        operateMchExpend.setCreateBy(getUsername());
        operateMchExpendService.insertOperateMchExpend(operateMchExpend);
        //渠道下发
        OperateChaIncome operateChaIncome = new OperateChaIncome();
        operateChaIncome.setChaLableId(operateMchChaRemit.getChaLableId());
        operateChaIncome.setChaLableName(operateMchChaRemit.getChaLableName());
        operateChaIncome.setIncomeAmount(operateMchChaRemit.getRemitAmount());
        operateChaIncome.setIncomeDate(operateMchChaRemit.getRemitDate());
        operateChaIncome.setRemark("打款管理操作");
        operateChaIncome.setCreateBy(getUsername());
        operateChaIncomeService.insertOperateChaIncome(operateChaIncome);

        return toAjax(operateMchChaRemitService.insertOperateMchChaRemit(operateMchChaRemit));
    }

    /**
     * 修改商户和渠道打款
     */
    @PreAuthorize("@ss.hasPermi('operate:remit:edit')")
    @Log(title = "商户和渠道打款", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateMchChaRemit operateMchChaRemit)
    {
        return toAjax(operateMchChaRemitService.updateOperateMchChaRemit(operateMchChaRemit));
    }

    /**
     * 删除商户和渠道打款
     */
    @PreAuthorize("@ss.hasPermi('operate:remit:remove')")
    @Log(title = "商户和渠道打款", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operateMchChaRemitService.deleteOperateMchChaRemitByIds(ids));
    }
}
