package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.OperateChannelLabel;
import com.ruoyi.operate.domain.OperateMerchantProdect;
import com.ruoyi.operate.domain.OperatePayChannel;
import com.ruoyi.operate.dto.OperatePayProjectInfoDTO;
import com.ruoyi.operate.service.IOperateChannelLabelService;
import com.ruoyi.operate.service.IOperateMerchantProdectService;
import com.ruoyi.operate.service.IOperatePayChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 商户和产品配置详情Controller
 * 
 * @author master123
 * @date 2024-09-12
 */
@RestController
@RequestMapping("/operate/merchantProdect")
public class OperateMerchantProdectController extends BaseController
{
    @Autowired
    private IOperateMerchantProdectService operateMerchantProdectService;
    @Autowired
    private IOperatePayChannelService operatePayChannelService;
    @Autowired
    private IOperateChannelLabelService operateChannelLabelService;

    /**
     * 查询商户和产品配置详情列表
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperateMerchantProdect operateMerchantProdect)
    {
        startPage();
        List<OperateMerchantProdect> list = operateMerchantProdectService.selectOperateMerchantProdectList(operateMerchantProdect);
        return getDataTable(list);
    }

    /**
     * 导出商户和产品配置详情列表
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:export')")
    @Log(title = "商户和产品配置详情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperateMerchantProdect operateMerchantProdect)
    {
        List<OperateMerchantProdect> list = operateMerchantProdectService.selectOperateMerchantProdectList(operateMerchantProdect);
        ExcelUtil<OperateMerchantProdect> util = new ExcelUtil<OperateMerchantProdect>(OperateMerchantProdect.class);
        util.exportExcel(response, list, "商户和产品配置详情数据");
    }

    /**
     * 获取商户和产品配置详情详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operateMerchantProdectService.selectOperateMerchantProdectById(id));
    }

    /**
     * 新增商户和产品配置详情
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:add')")
    @Log(title = "商户和产品配置详情", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateMerchantProdect operateMerchantProdect)
    {
        operateMerchantProdect.setCreateBy(getUsername());
        return toAjax(operateMerchantProdectService.insertOperateMerchantProdect(operateMerchantProdect));
    }

    /**
     * 修改商户和产品配置详情
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:edit')")
    @Log(title = "商户和产品配置详情", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateMerchantProdect operateMerchantProdect)
    {
        return toAjax(operateMerchantProdectService.updateOperateMerchantProdect(operateMerchantProdect));
    }

    /**
     * 删除商户和产品配置详情
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:remove')")
    @Log(title = "商户和产品配置详情", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operateMerchantProdectService.deleteOperateMerchantProdectByIds(ids));
    }

    /**
     * 获取所有支付通道
     */
    @GetMapping("/getPayChaAll")
//    @Log(title = "商户和产品配置详情", businessType = BusinessType.OTHER)
    public AjaxResult getPayChaAll(@RequestParam Map<String, Object> params)
    {
        AjaxResult ajax = AjaxResult.success();
        //查询通道标识
        List<OperateChannelLabel> operateChannelLabels = operateChannelLabelService.selectChannelLabelAll();
        ajax.put("chaLabList", operateChannelLabels);
        //新增时查询所有支付通道
        List<OperatePayProjectInfoDTO> operatePayChannels = operatePayChannelService.selectPayChannel(new OperatePayChannel());
        ajax.put("payChaList", operatePayChannels);
        return ajax;
    }


}
