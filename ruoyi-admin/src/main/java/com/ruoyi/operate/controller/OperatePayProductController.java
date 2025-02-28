package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.ConvertUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.*;
import com.ruoyi.operate.dto.OperatePayProTableDTO;
import com.ruoyi.operate.dto.OperatePayProjectDTO;
import com.ruoyi.operate.dto.OperatePayProjectInfoDTO;
import com.ruoyi.operate.mapper.OperateAgentProductMapper;
import com.ruoyi.operate.mapper.OperateMchProAloneMapper;
import com.ruoyi.operate.mapper.OperateMerchantProdectMapper;
import com.ruoyi.operate.mapper.OperatePayProductInfoMapper;
import com.ruoyi.operate.service.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 支付产品信息Controller
 * 
 * @author master123
 * @date 2024-09-05
 */
@RestController
@RequestMapping("/operate/payProduct")
public class OperatePayProductController extends BaseController
{
    @Autowired
    private IOperatePayProductService operatePayProductService;
    @Autowired
    private IOperateChannelLabelService operateChannelLabelService;
    @Autowired
    private IOperatePayChannelService operatePayChannelService;
    @Autowired
    private IOperatePayProductInfoService operatePayProductInfoService;
    @Autowired
    private IOperateMerchantProdectService operateMerchantProdectService;
    @Autowired
    private OperateMerchantProdectMapper operateMerchantProdectMapper;
    @Autowired
    private IOperateMchProAloneService operateMchProAloneService;
    @Autowired
    private OperateMchProAloneMapper operateMchProAloneMapper;
    @Autowired
    private OperateAgentProductMapper operateAgentProductMapper;
    @Autowired
    private IOperateAgentProductService operateMerchantProdect;
    @Autowired
    private OperatePayProductInfoMapper operatePayProductInfoMapper;
    @Autowired
    private IOperateMerchantService operateMerchantService;
    @Autowired
    private IOperateAgentService operateAgentService;

    /**
     * 查询支付产品信息列表
     */
//    @PreAuthorize("@ss.hasPermi('operate:payProduct:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperatePayProduct operatePayProduct)
    {
        startPage();
        List<OperatePayProduct> list = operatePayProductService.selectOperatePayProductList(operatePayProduct);
        return getDataTable(list);
    }

    @GetMapping("/dataList")
    public AjaxResult dataList(OperatePayProjectDTO operatePayProjectDTO)
    {
        List<OperatePayProduct> list = operatePayProductService.selectpPayProductList(operatePayProjectDTO);

        if(StringUtils.isNotEmpty(operatePayProjectDTO.getUserId())){
            //获取商户
            OperateMerchant operateMerchant = operateMerchantService.queryOperateMerchantByUserId(Long.valueOf(operatePayProjectDTO.getUserId()));
            Map<String, Object> mchProParams = new HashMap<>();
            mchProParams.put("mchId",operateMerchant.getId());

            List<Long> operateMerchantProdects = operateMerchantProdectService.queryMchProList(mchProParams)
                    .stream().map(OperateMerchantProdect::getProductId).collect(Collectors.toList());

            Iterator<OperatePayProduct> iterator = list.iterator();
            while (iterator.hasNext()) {
                OperatePayProduct next = iterator.next();
                if (!operateMerchantProdects.stream().anyMatch(item -> next.getId().equals(item))) {
                    iterator.remove();
                }
            }
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("dataList", list);
        return ajax;
    }

    /**
     * 导出支付产品信息列表
     */
    @PreAuthorize("@ss.hasPermi('operate:payProduct:export')")
    @Log(title = "支付产品信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperatePayProduct operatePayProduct)
    {
        List<OperatePayProduct> list = operatePayProductService.selectOperatePayProductList(operatePayProduct);
        ExcelUtil<OperatePayProduct> util = new ExcelUtil<OperatePayProduct>(OperatePayProduct.class);
        util.exportExcel(response, list, "支付产品信息数据");
    }

    /**
     * 获取支付产品信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payProduct:query')")
    @GetMapping(value ={"/","/{id}"})
    public AjaxResult getInfo(@PathVariable(value = "id", required = false) Long id)
    {

        AjaxResult ajax = AjaxResult.success();

        //查询通道标识
        List<OperateChannelLabel> operateChannelLabels = operateChannelLabelService.selectChannelLabelAll();
        ajax.put("chaLabList", operateChannelLabels);

        //新增时查询所有支付通道
        List<OperatePayProjectInfoDTO> operatePayChannels = operatePayChannelService.selectPayChannel(new OperatePayChannel());
        ajax.put("payChaList", operatePayChannels);

        if (StringUtils.isNotNull(id))
        {
            //修改时查询勾选的数据
            List<OperatePayProjectInfoDTO> operatePayProjectInfoDTOS = operatePayProductInfoService.queryListByPayProductId(id);
            ajax.put("selectPayChaList", operatePayProjectInfoDTOS);
            OperatePayProduct operatePayProduct = operatePayProductService.selectOperatePayProductById(id);
            ajax.put(AjaxResult.DATA_TAG, operatePayProduct);
        }
        return ajax;
    }

    /**
     * 新增支付产品信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payProduct:add')")
    @Log(title = "支付产品信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperatePayProjectDTO operatePayProjectDTO)
    {
        //保存支付通道
        OperatePayProduct operatePayProduct = ConvertUtils.sourceToTarget(operatePayProjectDTO, OperatePayProduct.class);
        operatePayProduct.setCreateBy(getUsername());
        operatePayProductService.insertOperatePayProduct(operatePayProduct);
        List<OperatePayProjectInfoDTO> payChaList = operatePayProjectDTO.getSelectPayChaList();
        for(OperatePayProjectInfoDTO dto : payChaList){
            OperatePayProductInfo operatePayProductInfo = new OperatePayProductInfo();
            operatePayProductInfo.setPayChannelId(dto.getId());
            operatePayProductInfo.setPayProductId(operatePayProduct.getId());
            operatePayProductInfo.setSort(dto.getSort());
            operatePayProductInfo.setRiskControlId(dto.getRiskControlId());
            operatePayProductInfo.setCreateBy(getUsername());
            operatePayProductInfoService.insertOperatePayProductInfo(operatePayProductInfo);
        }

        return toAjax(1);
    }

    /**
     * 修改支付产品信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payProduct:edit')")
    @Log(title = "支付产品信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperatePayProjectDTO operatePayProjectDTO)
    {
        //修改支付通道
        OperatePayProduct operatePayProduct = ConvertUtils.sourceToTarget(operatePayProjectDTO, OperatePayProduct.class);
        operatePayProduct.setUpdateBy(getUsername());

        //查出已经选择的支付通道
        List<OperatePayProjectInfoDTO> payProjectInfoDTOS = operatePayProductInfoService.queryListByPayProductId(operatePayProjectDTO.getId());
        List<OperatePayProjectInfoDTO> selectPayChaList = operatePayProjectDTO.getSelectPayChaList();
        List<Long> payProjectInfoIds = payProjectInfoDTOS.stream().map(OperatePayProjectInfoDTO::getId).collect(Collectors.toList());
        List<Long> selectPayChaIdList = selectPayChaList.stream().map(OperatePayProjectInfoDTO::getId).collect(Collectors.toList());
        //获取交集
        List<Long> ids = payProjectInfoIds.stream().filter(selectPayChaIdList::contains).collect(Collectors.toList());
        //删除
        if(ids.size() > 0){
            Map<String, Object> params = new HashMap<>();
            params.put("ids",ids);
            params.put("payProductId",operatePayProjectDTO.getId());
            operatePayProductInfoService.updateByIds(params);

        }else{
            //之前的数据全部删除
            Map<String, Object> deleteParams = new HashMap<>();
            deleteParams.put("payProductId",operatePayProjectDTO.getId());
            operatePayProductInfoService.deleteByParams(deleteParams);
        }

        if(selectPayChaList.size() > 0){
            for(OperatePayProjectInfoDTO dto : selectPayChaList){
                OperatePayProductInfo operatePayProductInfo = ConvertUtils.sourceToTarget(dto, OperatePayProductInfo.class);
                if(ids.stream().anyMatch(item -> dto.getId().equals(item))){
                    operatePayProductInfo.setPayChannelId(dto.getId());
                    operatePayProductInfo.setPayProductId(operatePayProjectDTO.getId());
                    operatePayProductInfo.setUpdateBy(getUsername());
                    operatePayProductInfoService.updateOperatePayProductInfo(operatePayProductInfo);
                }else{
                    operatePayProductInfo.setPayChannelId(dto.getId());
                    operatePayProductInfo.setPayProductId(operatePayProjectDTO.getId());
                    operatePayProductInfo.setCreateBy(getUsername());
                    operatePayProductInfoService.insertOperatePayProductInfo(operatePayProductInfo);
                }
            }

        }

        //商户关联(全配)
        if(StringUtils.isNotEmpty(operatePayProjectDTO.getConfigType()) && operatePayProjectDTO.getConfigType().equals("0")){
            //根据产品查询关联
            Map<String, Object> params = new HashMap<>();
            params.put("productId",operatePayProjectDTO.getId());
            List<OperateMerchantProdect> operateMerchantProdects = operateMerchantProdectService.queryMchProList(params);


            //逻辑删除(全配 、单独)
            if(operateMerchantProdects.size() > 0){
                List<Long> mchIds = operateMerchantProdects.stream().map(OperateMerchantProdect::getMchId).collect(Collectors.toList());
                Map<String, Object> updateParams = new HashMap<>();
                updateParams.put("productId",operatePayProjectDTO.getId());
                updateParams.put("mchIds",mchIds);
                operateMerchantProdectMapper.updateByMchPro(updateParams);

                List<Long> mchProIds = operateMerchantProdects.stream().map(OperateMerchantProdect::getId).collect(Collectors.toList());
                Map<String, Object> updateAloneParams = new HashMap<>();
                updateAloneParams.put("mchProIds",mchProIds);
                operateMchProAloneMapper.updateByMchProIds(updateAloneParams);
            }


            for(Long mchId : operatePayProjectDTO.getMchList()){
                OperateMerchantProdect operateMerchantProdect = new OperateMerchantProdect();
                operateMerchantProdect.setCreateBy(getUsername());
                operateMerchantProdect.setProductId(operatePayProjectDTO.getId());
                operateMerchantProdect.setMchId(mchId);
                operateMerchantProdect.setPriority("0");
                operateMerchantProdectService.insertOperateMerchantProdect(operateMerchantProdect);
            }



        }

        operatePayProductService.updateOperatePayProduct(operatePayProduct);

        return toAjax(1);
    }



    /**
     * 删除支付产品信息
     */
    @PreAuthorize("@ss.hasPermi('operate:payProduct:remove')")
    @Log(title = "支付产品信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(operatePayProductService.deleteOperatePayProductByIds(ids));
    }

    /**
     * 获取所有产品关联商户
     */
    @GetMapping("/getPayProAllByMchId")
    public AjaxResult getPayProAllByMchId(@RequestParam Map<String, Object> params)
    {
        //因为接口可能从商户、代理商系统进来
        String userId = (String) params.get("userId");
        OperateMerchant operateMerchant = new OperateMerchant();
        if(StringUtils.isNotEmpty(userId)){
            //获取商户
            operateMerchant = operateMerchantService.queryOperateMerchantByUserId(Long.valueOf(userId));
            params.put("mchId",operateMerchant.getId());
        }else{
            String mchId = (String) params.get("mchId");
            operateMerchant = operateMerchantService.selectOperateMerchantById(Long.valueOf(mchId));
            params.put("agentId",operateMerchant.getAgentId());
        }


        AjaxResult ajax = AjaxResult.success();
        List<OperatePayProTableDTO> operatePayProducts = operatePayProductService.getPayProAllByMchId(params);

        //因为接口可能从商户、代理商系统进来
        if(StringUtils.isNotEmpty(userId)){
            // 根据产品/商户查询关联
            Map<String, Object> mchProParams = new HashMap<>();
            mchProParams.put("mchId",operateMerchant.getId());

            List<Long> operateMerchantProdects = operateMerchantProdectService.queryMchProList(mchProParams)
                    .stream().map(OperateMerchantProdect::getProductId).collect(Collectors.toList());

            Iterator<OperatePayProTableDTO> iterator = operatePayProducts.iterator();
            while (iterator.hasNext()) {
                OperatePayProTableDTO next = iterator.next();
                if (!operateMerchantProdects.stream().anyMatch(item -> next.getId().equals(item))) {
                    iterator.remove();
                }
            }

            ajax.put("payProductList", operatePayProducts);
            ajax.put("total", operatePayProducts.size());
            return ajax;

        }else{

            ajax.put("payProductList", operatePayProducts);
            ajax.put("total", operatePayProducts.size());
            return ajax;
        }


    }

    /**
     * 获取所有产品关联代理商
     */
    @GetMapping("/getPayProAllByAgentId")
    public AjaxResult getPayProAllByAgentId(@RequestParam Map<String, Object> params)
    {
        //因为接口可能从商户、代理商系统进来
        String userId = (String) params.get("userId");
        OperateAgent operateAgent = new OperateAgent();
        if(StringUtils.isNotEmpty(userId)){
            //获取代理
            operateAgent = operateAgentService.queryOperateAgentByUserId(Long.valueOf(userId));
            params.put("agentId",operateAgent.getId());
        }

        AjaxResult ajax = AjaxResult.success();
        List<OperatePayProTableDTO> operatePayProducts = operatePayProductService.getPayProAllByAgentId(params);

        //因为接口可能从商户、代理商系统进来
        if(StringUtils.isNotEmpty(userId)){
            // 根据产品/商户查询关联
            Map<String, Object> agentParams = new HashMap<>();
            agentParams.put("agentId",operateAgent.getId());
            
            List<Long> agentProducts = operateAgentProductMapper.selectAgePro(agentParams)
                    .stream().map(OperateAgentProduct::getProductId).collect(Collectors.toList());
                    

            Iterator<OperatePayProTableDTO> iterator = operatePayProducts.iterator();
            while (iterator.hasNext()) {
                OperatePayProTableDTO next = iterator.next();
                if (!agentProducts.stream().anyMatch(item -> next.getId().equals(item))) {
                    iterator.remove();
                }
            }

            ajax.put("payProductList", operatePayProducts);
            ajax.put("total", operatePayProducts.size());
            return ajax;

        }else{

            ajax.put("payProductList", operatePayProducts);
            ajax.put("total", operatePayProducts.size());
            return ajax;
        }

    }

    /**
     * 获取所有产品
     */
    @GetMapping("/getPayProAll")
//    @Log(title = "获取所有产品", businessType = BusinessType.OTHER)
    public AjaxResult getPayProAll(@RequestParam("payChaId") Long payChaId)
    {
        AjaxResult ajax = AjaxResult.success();
        List<OperatePayProTableDTO> operatePayProducts = operatePayProductService.getPayProAll();
        ajax.put("payProductList", operatePayProducts);
        ajax.put("total", operatePayProducts.size());

        List<Long> payProIds = operatePayProductInfoMapper.selectDataListByPayChaId(payChaId);
        ajax.put("payProIdList", payProIds);
        return ajax;
    }

    /**
     * 单独配置时查询产品和商户的配置信息
     */
    @GetMapping("/getMchProConfigAlone")
//    @Log(title = "单独配置时查询产品和商户的配置信息", businessType = BusinessType.OTHER)
    public AjaxResult getMchProConfigAlone(@RequestParam Map<String, Object> params)
    {

        AjaxResult ajax = AjaxResult.success();

        //查询通道标识
        List<OperateChannelLabel> operateChannelLabels = operateChannelLabelService.selectChannelLabelAll();
        ajax.put("chaLabList", operateChannelLabels);

        //新增时查询所有支付通道
        List<OperatePayProjectInfoDTO> operatePayChannels = operatePayChannelService.selectPayChannel(new OperatePayChannel());
        ajax.put("payChaList", operatePayChannels);

        //根据产品\商户查询关联
        List<OperateMerchantProdect> operateMerchantProdects = operateMerchantProdectService.queryMchProList(params);
        if(operateMerchantProdects.size() > 0 ){
            if(operateMerchantProdects.get(0).getPriority().equals("1")){

                //查询产品关联的通道数据(单独配置)
                List<OperatePayProjectInfoDTO> operatePayProjectInfoDTOS = operateMchProAloneService.queryListByMchProId(operateMerchantProdects.get(0).getId());
                ajax.put("selectPayChaList", operatePayProjectInfoDTOS);

            }else{

                //查询产品关联的通道数据
                Long productId = Long.valueOf(params.get("productId").toString());
                List<OperatePayProjectInfoDTO> operatePayProjectInfoDTOS = operatePayProductInfoService.queryListByPayProductId(productId);
                ajax.put("selectPayChaList", operatePayProjectInfoDTOS);

            }

        }else {

            //查询产品关联的通道数据
            Long productId = Long.valueOf(params.get("productId").toString());
            List<OperatePayProjectInfoDTO> operatePayProjectInfoDTOS = operatePayProductInfoService.queryListByPayProductId(productId);
            ajax.put("selectPayChaList", operatePayProjectInfoDTOS);
        }

        return ajax;
    }


    /**
     * 产品和商户单独配置
     */
    @Log(title = "产品和商户单独配置")
    @PutMapping("/mchProConfigAloneEdit")
    public AjaxResult mchProConfigAloneEdit(@RequestBody OperatePayProjectDTO operatePayProjectDTO)
    {
        // 根据产品/商户查询关联
        Map<String, Object> params = new HashMap<>();
        params.put("productId",operatePayProjectDTO.getId());
        params.put("mchId",operatePayProjectDTO.getMchId());
        List<OperateMerchantProdect> operateMerchantProdects = operateMerchantProdectService.queryMchProList(params);

        //关闭
        if(operatePayProjectDTO.getStatus().equals("1")){

            if(operateMerchantProdects.size() > 0){

                operateMerchantProdectMapper.updateByMchIdPro(params);
                operateMchProAloneMapper.updateByMchProId(operateMerchantProdects.get(0).getId());

            }

        //开启
        }else{

            //代理商费率修改
            OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(operatePayProjectDTO.getMchId());
            if(operateMerchant.getAgentId() != null && operatePayProjectDTO.getAgentRate() != null){
                //根据产品/商户查询关联
                Map<String, Object> agentProParams = new HashMap<>();
                agentProParams.put("agentId",operateMerchant.getAgentId());
                agentProParams.put("productId",operatePayProjectDTO.getId());
                List<OperateAgentProduct> agentProducts = operateAgentProductMapper.selectAgePro(agentProParams);
                if(agentProducts.size() > 0){
                    Map<String, Object> agentParams = new HashMap<>();
                    agentParams.put("productId",operatePayProjectDTO.getId());
                    agentParams.put("agentId",operateMerchant.getAgentId());
                    agentParams.put("agentRate",operatePayProjectDTO.getAgentRate());
                    operateAgentProductMapper.updateRateByAgentIdPro(agentParams);
                }else{
                    OperateAgentProduct operateAgentProduct = new OperateAgentProduct();
                    operateAgentProduct.setAgentId(operateMerchant.getAgentId());
                    operateAgentProduct.setProductId(operatePayProjectDTO.getId());
                    operateAgentProduct.setRate(operatePayProjectDTO.getAgentRate());
                    operateAgentProduct.setCreateBy(getUsername());
                    operateAgentProduct.setCreateTime(DateUtils.getNowDate());
                    operateMerchantProdect.insertOperateAgentProduct(operateAgentProduct);
                }

            }

            if(operateMerchantProdects.size() > 0){

                OperateMerchantProdect operateMerchantProdect = operateMerchantProdects.get(0);
                operateMerchantProdect.setUpdateTime(DateUtils.getNowDate());
                operateMerchantProdect.setUpdateBy(getUsername());
                operateMerchantProdect.setPriority("1");
                operateMerchantProdect.setRate(operatePayProjectDTO.getMchRate());
                operateMerchantProdectService.updateOperateMerchantProdect(operateMerchantProdect);

                //先删除再新增
                operateMchProAloneMapper.updateByMchProId(operateMerchantProdect.getId());
                if(operatePayProjectDTO.getSelectPayChaList().size() > 0){
                    for(OperatePayProjectInfoDTO dto : operatePayProjectDTO.getSelectPayChaList()){
                        OperateMchProAlone operateMchProAlone = new OperateMchProAlone();
                        operateMchProAlone.setMchProId(operateMerchantProdect.getId());
                        operateMchProAlone.setPayChannelId(dto.getId());
                        operateMchProAlone.setSort(dto.getSort());
                        operateMchProAlone.setRiskControlId(dto.getRiskControlId());
                        operateMchProAlone.setCreateBy(getUsername());
                        operateMchProAloneService.insertOperateMchProAlone(operateMchProAlone);
                    }

                }

            }else{

                OperateMerchantProdect operateMerchantProdect = new OperateMerchantProdect();
                operateMerchantProdect.setProductId(operatePayProjectDTO.getId());
                operateMerchantProdect.setMchId(operatePayProjectDTO.getMchId());
                operateMerchantProdect.setCreateBy(getUsername());
                operateMerchantProdect.setCreateTime(DateUtils.getNowDate());
                operateMerchantProdect.setPriority("1");
                operateMerchantProdect.setRate(operatePayProjectDTO.getMchRate());
                operateMerchantProdectService.insertOperateMerchantProdect(operateMerchantProdect);

                if(operatePayProjectDTO.getSelectPayChaList().size() > 0){
                    for(OperatePayProjectInfoDTO dto : operatePayProjectDTO.getSelectPayChaList()){
                        OperateMchProAlone operateMchProAlone = new OperateMchProAlone();
                        operateMchProAlone.setMchProId(operateMerchantProdect.getId());
                        operateMchProAlone.setPayChannelId(dto.getId());
                        operateMchProAlone.setSort(dto.getSort());
                        operateMchProAlone.setRiskControlId(dto.getRiskControlId());
                        operateMchProAlone.setCreateBy(getUsername());
                        operateMchProAlone.setCreateTime(DateUtils.getNowDate());
                        operateMchProAloneService.insertOperateMchProAlone(operateMchProAlone);
                    }

                }

            }

        }

        return toAjax(1);
    }


    /**
     * 产品和代理商单独配置
     */
    @Log(title = "产品和代理商单独配置")
    @PutMapping("/agentProConfigAloneEdit")
    public AjaxResult agentProConfigAloneEdit(@RequestBody OperateAgentProduct operateAgentProduct)
    {
        //根据产品/商户查询关联
        Map<String, Object> params = new HashMap<>();
        params.put("productId",operateAgentProduct.getProductId());
        params.put("agentId",operateAgentProduct.getAgentId());
        List<OperateAgentProduct> agentProducts = operateAgentProductMapper.selectAgePro(params);

        //关闭
        if(operateAgentProduct.getStatus().equals("1")){

            if(ObjectUtils.isNotEmpty(agentProducts)){

                operateAgentProductMapper.updateByAgentIdPro(params);

            }

        //开启
        }else{

            if(agentProducts.size() > 0){
                OperateAgentProduct agentProduct = agentProducts.get(0);
                agentProduct.setUpdateBy(getUsername());
                agentProduct.setUpdateTime(DateUtils.getNowDate());
                agentProduct.setRate(operateAgentProduct.getRate());
                agentProduct.setStatus("0");
                operateMerchantProdect.updateOperateAgentProduct(agentProduct);

            }else{
                operateAgentProduct.setCreateBy(getUsername());
                operateAgentProduct.setCreateTime(DateUtils.getNowDate());
                operateMerchantProdect.insertOperateAgentProduct(operateAgentProduct);

            }

        }

        return toAjax(1);
    }

    /**
     * 代理商统一点位
     */
    @Log(title = "代理商统一点位")
    @PutMapping("/agentProConfigEdit")
    public AjaxResult agentProConfigEdit(@RequestBody OperateAgentProduct operateAgentProduct)
    {

        //先删除该代理商之前关联的产品
        Map<String, Object> params = new HashMap<>();
        params.put("agentId",operateAgentProduct.getAgentId());
        operateAgentProductMapper.updateByAgentIdPro(params);
        //根据产品/商户查询关联
        List<OperatePayProduct> operatePayProducts = operatePayProductService.selectOperatePayProductList(new OperatePayProduct());
        for(OperatePayProduct operatePayProduct : operatePayProducts){
            operateAgentProduct.setRate(operateAgentProduct.getRate());
            operateAgentProduct.setAgentId(operateAgentProduct.getAgentId());
            operateAgentProduct.setProductId(operatePayProduct.getId());
            operateAgentProduct.setCreateBy(getUsername());
            operateAgentProduct.setCreateTime(DateUtils.getNowDate());
            operateMerchantProdect.insertOperateAgentProduct(operateAgentProduct);
        }

        return toAjax(1);
    }

    /**
     * 支付通道：产品保存配置
     */
    @Log(title = "支付通道：产品保存配置", businessType = BusinessType.OTHER)
    @GetMapping("/payChaSave")
    public AjaxResult payChaSave(@RequestParam Map<String, Object> params)
    {
        String payProList = (String) params.get("payProList");
        if(StringUtils.isNotEmpty(payProList)){

            String[] payProIdList = payProList.split(",");
            List<Long> selectPayProList = Arrays.stream(payProIdList).map(Long::parseLong)
                    .collect(Collectors.toList());

            //查出已经选择的支付通道
            List<Long> payProIds = operatePayProductInfoMapper.selectDataListByPayChaId(Long.valueOf(params.get("payChaId").toString()));
            //获取交集
            List<Long> ids = payProIds.stream().filter(selectPayProList::contains).collect(Collectors.toList());
            //删除
            if(ids.size() > 0){
                Map<String, Object> updateParams = new HashMap<>();
                updateParams.put("ids",ids);
                updateParams.put("payChaId",Long.valueOf(params.get("payChaId").toString()));
                operatePayProductInfoMapper.updateByProIds(updateParams);

            }else{
                //之前的数据全部删除
                Map<String, Object> deleteParams = new HashMap<>();
                deleteParams.put("payChaId",Long.valueOf(params.get("payChaId").toString()));
                operatePayProductInfoService.deleteByParams(deleteParams);
            }

            if(selectPayProList.size() > 0){
                OperatePayChannel operatePayChannel = operatePayChannelService.selectOperatePayChannelById(Long.valueOf(params.get("payChaId").toString()));
                for(Long payProId : selectPayProList){
                    if(!ids.stream().anyMatch(item -> payProId.equals(item))){
                        OperatePayProductInfo operatePayProductInfo = new OperatePayProductInfo();
                        operatePayProductInfo.setPayChannelId(operatePayChannel.getId());
                        operatePayProductInfo.setPayProductId(payProId);
                        operatePayProductInfo.setSort(1);
                        operatePayProductInfo.setRiskControlId(operatePayChannel.getRiskControlId());
                        operatePayProductInfo.setCreateBy(getUsername());
                        operatePayProductInfoService.insertOperatePayProductInfo(operatePayProductInfo);
                    }
                }

            }

        }else{

            //之前的数据全部删除
            Map<String, Object> deleteParams = new HashMap<>();
            deleteParams.put("payChaId",Long.valueOf(params.get("payChaId").toString()));
            operatePayProductInfoService.deleteByParams(deleteParams);
        }

        return toAjax(1);
    }


    /**
     * 启用禁用产品
     */
    @PreAuthorize("@ss.hasPermi('operate:payProduct:edit')")
    @Log(title = "启用禁用产品")
    @GetMapping("/forbidOrEnable")
    public AjaxResult forbidOrEnable(@RequestParam Map<String, Object> params)
    {
        String ids = (String) params.get("ids");
        params.put("ids",ids.split(","));
        return toAjax(operatePayProductService.forbidOrEnableByIds(params));
    }





}
