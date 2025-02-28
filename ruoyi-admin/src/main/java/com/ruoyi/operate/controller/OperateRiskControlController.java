package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.ConvertUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.OperatePayChannel;
import com.ruoyi.operate.domain.OperateRiskControl;
import com.ruoyi.operate.dto.OperateRiskControlDTO;
import com.ruoyi.operate.service.IOperateMchProAloneService;
import com.ruoyi.operate.service.IOperatePayChannelService;
import com.ruoyi.operate.service.IOperatePayProductInfoService;
import com.ruoyi.operate.service.IOperateRiskControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 风控设置Controller
 * 
 * @author master123
 * @date 2024-09-09
 */
@RestController
@RequestMapping("/operate/riskControl")
public class OperateRiskControlController extends BaseController
{
    @Autowired
    private IOperateRiskControlService operateRiskControlService;
    @Autowired
    private IOperatePayChannelService operatePayChannelService;
    @Autowired
    private IOperatePayProductInfoService operatePayProductInfoService;
    @Autowired
    private IOperateMchProAloneService operateMchProAloneService;

    /**
     * 查询风控设置列表
     */
    @PreAuthorize("@ss.hasPermi('operate:payChannel:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperateRiskControl operateRiskControl)
    {
        startPage();
        List<OperateRiskControl> list = operateRiskControlService.selectOperateRiskControlList(operateRiskControl);
        return getDataTable(list);
    }

    /**
     * 导出风控设置列表
     */
    @PreAuthorize("@ss.hasPermi('operate:payChannel:export')")
    @Log(title = "风控设置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperateRiskControl operateRiskControl)
    {
        List<OperateRiskControl> list = operateRiskControlService.selectOperateRiskControlList(operateRiskControl);
        ExcelUtil<OperateRiskControl> util = new ExcelUtil<OperateRiskControl>(OperateRiskControl.class);
        util.exportExcel(response, list, "风控设置数据");
    }

    /**
     * 获取风控设置详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payChannel:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operateRiskControlService.selectOperateRiskControlById(id));
    }

    /**
     * 新增风控设置
     */
    @PreAuthorize("@ss.hasPermi('operate:payChannel:add')")
    @Log(title = "风控设置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateRiskControlDTO operateRiskControlDTO)
    {

        OperateRiskControl operateRiskControl = ConvertUtils.sourceToTarget(operateRiskControlDTO, OperateRiskControl.class);
        operateRiskControl.setCreateBy(getUsername());
        operateRiskControlService.insertOperateRiskControl(operateRiskControl);
        //风控新增时关联支付通道、支付产品详情
        OperatePayChannel operatePayChannel = operatePayChannelService.selectOperatePayChannelById(operateRiskControlDTO.getPayChaId());
        operatePayChannel.setRiskControlId(operateRiskControl.getId());
        operatePayChannelService.updateOperatePayChannel(operatePayChannel);
        operatePayProductInfoService.updateByPayChaId(operateRiskControl.getId(),operateRiskControlDTO.getPayChaId());
        operateMchProAloneService.updateByPayChaId(operateRiskControl.getId(),operateRiskControlDTO.getPayChaId());
        return success(operateRiskControl);
    }

    /**
     * 修改风控设置
     */
    @PreAuthorize("@ss.hasPermi('operate:payChannel:edit')")
    @Log(title = "风控设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateRiskControl operateRiskControl)
    {
        operateRiskControl.setUpdateBy(getUsername());
        return toAjax(operateRiskControlService.updateOperateRiskControl(operateRiskControl));
    }

    /**
     * 删除风控设置
     */
    @PreAuthorize("@ss.hasPermi('operate:payChannel:remove')")
    @Log(title = "风控设置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operateRiskControlService.deleteOperateRiskControlByIds(ids));
    }
}
