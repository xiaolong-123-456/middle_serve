package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperateMerchantProdect;

import java.util.List;
import java.util.Map;

/**
 * 商户和产品配置详情Service接口
 * 
 * @author master123
 * @date 2024-09-12
 */
public interface IOperateMerchantProdectService 
{
    /**
     * 查询商户和产品配置详情
     * 
     * @param id 商户和产品配置详情主键
     * @return 商户和产品配置详情
     */
    public OperateMerchantProdect selectOperateMerchantProdectById(Long id);

    /**
     * 查询商户和产品配置详情列表
     * 
     * @param operateMerchantProdect 商户和产品配置详情
     * @return 商户和产品配置详情集合
     */
    public List<OperateMerchantProdect> selectOperateMerchantProdectList(OperateMerchantProdect operateMerchantProdect);

    /**
     * 新增商户和产品配置详情
     * 
     * @param operateMerchantProdect 商户和产品配置详情
     * @return 结果
     */
    public int insertOperateMerchantProdect(OperateMerchantProdect operateMerchantProdect);

    /**
     * 修改商户和产品配置详情
     * 
     * @param operateMerchantProdect 商户和产品配置详情
     * @return 结果
     */
    public int updateOperateMerchantProdect(OperateMerchantProdect operateMerchantProdect);

    /**
     * 批量删除商户和产品配置详情
     * 
     * @param ids 需要删除的商户和产品配置详情主键集合
     * @return 结果
     */
    public int deleteOperateMerchantProdectByIds(Long[] ids);

    /**
     * 删除商户和产品配置详情信息
     * 
     * @param id 商户和产品配置详情主键
     * @return 结果
     */
    public int deleteOperateMerchantProdectById(Long id);

    /**
     * 查询商户和盘品关联集合
     *
     * @param params 参数
     * @return 结果
     */
    public List<OperateMerchantProdect> queryMchProList(Map<String, Object> params);
}
