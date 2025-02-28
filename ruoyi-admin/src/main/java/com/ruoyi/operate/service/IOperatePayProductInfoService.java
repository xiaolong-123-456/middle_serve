package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperatePayProductInfo;
import com.ruoyi.operate.dto.OperatePayProjectInfoDTO;
import com.ruoyi.operate.dto.RelevanceDTO;

import java.util.List;
import java.util.Map;

/**
 * 支付产品详情信息Service接口
 * 
 * @author master123
 * @date 2024-09-09
 */
public interface IOperatePayProductInfoService 
{
    /**
     * 查询支付产品详情信息
     * 
     * @param id 支付产品详情信息主键
     * @return 支付产品详情信息
     */
    public OperatePayProductInfo selectOperatePayProductInfoById(Long id);

    /**
     * 查询支付产品详情信息列表
     * 
     * @param operatePayProductInfo 支付产品详情信息
     * @return 支付产品详情信息集合
     */
    public List<OperatePayProductInfo> selectOperatePayProductInfoList(OperatePayProductInfo operatePayProductInfo);

    /**
     * 新增支付产品详情信息
     * 
     * @param operatePayProductInfo 支付产品详情信息
     * @return 结果
     */
    public int insertOperatePayProductInfo(OperatePayProductInfo operatePayProductInfo);

    /**
     * 修改支付产品详情信息
     * 
     * @param operatePayProductInfo 支付产品详情信息
     * @return 结果
     */
    public int updateOperatePayProductInfo(OperatePayProductInfo operatePayProductInfo);

    /**
     * 批量删除支付产品详情信息
     * 
     * @param ids 需要删除的支付产品详情信息主键集合
     * @return 结果
     */
    public int deleteOperatePayProductInfoByIds(Long[] ids);

    /**
     * 删除支付产品详情信息信息
     * 
     * @param id 支付产品详情信息主键
     * @return 结果
     */
    public int deleteOperatePayProductInfoById(Long id);

    /**
     * 根据支付产品id查询详情
     *
     * @param payProductId 支付产品id
     * @return 结果
     */
    public List<OperatePayProjectInfoDTO> queryListByPayProductId(Long payProductId);


    /**
     * 根据支付通道id补充风控id
     *@param riskControlId 风控id
     * @param payChaId 支付通道id
     * @return 结果
     */
    public int updateByPayChaId(Long riskControlId, Long payChaId);

    /**
     * 批量修改
     * @param params
     * @return 结果
     */
    public int updateByIds(Map<String, Object> params);

    /**
     * 根据产品id or 通道id 删除
     * @params 参数
     * @return 结果
     */
    public int deleteByParams(Map<String, Object> params);

    /**
     * 下单时统一配置，查询关联的通道
     *
     * @param payProductId 商户和产品配置详情id
     * @return 结果
     */
    public List<RelevanceDTO> queryChaByProId(Long payProductId);
}
