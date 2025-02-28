package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.operate.domain.OperateChannelLabel;
import com.ruoyi.operate.domain.OperatePayChannel;
import com.ruoyi.operate.dto.OperatePayChannelDTO;
import com.ruoyi.operate.dto.OperatePayProjectInfoDTO;
import com.ruoyi.operate.service.IOperateChannelLabelService;
import com.ruoyi.operate.service.IOperatePayChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 支付通道信息Controller
 * 
 * @author master123
 * @date 2024-09-03
 */
@RestController
@RequestMapping("/operate/payChannel")
public class OperatePayChannelController extends BaseController
{
    @Autowired
    private IOperatePayChannelService operatePayChannelService;
    @Autowired
    private IOperateChannelLabelService operateChannelLabelService;

    /**
     * 查询支付通道信息列表
     */
//    @PreAuthorize("@ss.hasPermi('operate:payChannel:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperatePayChannel operatePayChannel)
    {
        startPage();
        List<OperatePayChannelDTO> list = operatePayChannelService.selectOperatePayChannelList(operatePayChannel);
        return getDataTable(list);
    }

    @GetMapping("/dataList")
    @PreAuthorize("@ss.hasPermi('operate:payChannel:list')")
    public AjaxResult dataList(OperatePayChannel operatePayChannel)
    {
        List<OperatePayChannelDTO> list = operatePayChannelService.selectOperatePayChannelList(operatePayChannel);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("dataList", list);
        return ajax;
    }


    /**
     * 获取支付通道信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payChannel:query')")
    @GetMapping(value ={"/","/{id}"})
    public AjaxResult getInfo(@PathVariable(value = "id", required = false) Long id)
    {
        //查询通道标识
        AjaxResult ajax = AjaxResult.success();
        List<OperateChannelLabel> operateChannelLabels = operateChannelLabelService.selectChannelLabelAll();
        ajax.put("chaLabList", operateChannelLabels);
        if (StringUtils.isNotNull(id))
        {
            OperatePayChannel operatePayChannel = operatePayChannelService.selectOperatePayChannelById(id);
            ajax.put(AjaxResult.DATA_TAG, operatePayChannel);
        }
        return ajax;
    }

    /**
     * 新增支付通道信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payChannel:add')")
    @Log(title = "支付通道信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperatePayChannel operatePayChannel)
    {
        operatePayChannel.setCreateBy(getUsername());
        return toAjax(operatePayChannelService.insertOperatePayChannel(operatePayChannel));
    }

    /**
     * 修改支付通道信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payChannel:edit')")
    @Log(title = "支付通道信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperatePayChannel operatePayChannel)
    {
        operatePayChannel.setUpdateBy(getUsername());
        return toAjax(operatePayChannelService.updateOperatePayChannel(operatePayChannel));
    }

    /**
     * 删除支付通道信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payChannel:remove')")
    @Log(title = "支付通道信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operatePayChannelService.deleteOperatePayChannelByIds(ids));
    }

    /**
     * 根据通道标识获取支付通道
     */
    @PreAuthorize("@ss.hasPermi('operate:payChannel:query')")
    @GetMapping("/getPayChaByChaLabId/{chaLabId}")
    public AjaxResult getPayChaByChaLabId(@PathVariable("chaLabId") Long chaLabId)
    {
        AjaxResult ajax = AjaxResult.success();
        OperatePayChannel operatePayChannel = new OperatePayChannel();
        //0的时候查询全部
        if(chaLabId.equals(0)){
            List<OperatePayProjectInfoDTO> operatePayChannels = operatePayChannelService.selectPayChannel(new OperatePayChannel());
            ajax.put("payChaList", operatePayChannels);
        }else{
            operatePayChannel.setChaLabId(chaLabId);
            List<OperatePayProjectInfoDTO> operatePayChannels = operatePayChannelService.selectPayChannel(operatePayChannel);
            ajax.put("payChaList", operatePayChannels);
        }

        return ajax;
    }

    /**
     * 启用禁用支付通道信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payChannel:edit')")
    @Log(title = "启用禁用支付通道信息")
    @GetMapping("/forbidOrEnable")
    public AjaxResult forbidOrEnable(@RequestParam Map<String, Object> params)
    {
        String ids = (String) params.get("ids");
        params.put("ids",ids.split(","));
        return toAjax(operatePayChannelService.forbidOrEnableByIds(params));
    }


}
