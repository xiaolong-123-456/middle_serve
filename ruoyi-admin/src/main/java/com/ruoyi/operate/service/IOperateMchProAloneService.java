package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperateMchProAlone;
import com.ruoyi.operate.dto.OperatePayProjectInfoDTO;
import com.ruoyi.operate.dto.RelevanceDTO;

import java.util.List;

/**
 * 商户和产品单独配置Service接口
 * 
 * @author master123
 * @date 2024-09-13
 */
public interface IOperateMchProAloneService 
{
    /**
     * 查询商户和产品单独配置
     * 
     * @param id 商户和产品单独配置主键
     * @return 商户和产品单独配置
     */
    public OperateMchProAlone selectOperateMchProAloneById(Long id);

    /**
     * 查询商户和产品单独配置列表
     * 
     * @param operateMchProAlone 商户和产品单独配置
     * @return 商户和产品单独配置集合
     */
    public List<OperateMchProAlone> selectOperateMchProAloneList(OperateMchProAlone operateMchProAlone);

    /**
     * 新增商户和产品单独配置
     * 
     * @param operateMchProAlone 商户和产品单独配置
     * @return 结果
     */
    public int insertOperateMchProAlone(OperateMchProAlone operateMchProAlone);

    /**
     * 修改商户和产品单独配置
     * 
     * @param operateMchProAlone 商户和产品单独配置
     * @return 结果
     */
    public int updateOperateMchProAlone(OperateMchProAlone operateMchProAlone);

    /**
     * 批量删除商户和产品单独配置
     * 
     * @param ids 需要删除的商户和产品单独配置主键集合
     * @return 结果
     */
    public int deleteOperateMchProAloneByIds(Long[] ids);

    /**
     * 删除商户和产品单独配置信息
     * 
     * @param id 商户和产品单独配置主键
     * @return 结果
     */
    public int deleteOperateMchProAloneById(Long id);

    /**
     * 根据商户和产品获取单独的配置
     *
     * @param mchProId 商户和产品配置详情id
     * @return 结果
     */
    public List<OperatePayProjectInfoDTO> queryListByMchProId(Long mchProId);

    /**
     * 根据支付通道id补充风控id
     *@param riskControlId 风控id
     * @param payChaId 支付通道id
     * @return 结果
     */
    public int updateByPayChaId(Long riskControlId, Long payChaId);

    /**
     * 下单时单独配置，查询关联的通道
     *
     * @param mchProId 商户和产品配置详情id
     * @return 结果
     */
    public List<RelevanceDTO> queryChaByMchProId(Long mchProId);
}
