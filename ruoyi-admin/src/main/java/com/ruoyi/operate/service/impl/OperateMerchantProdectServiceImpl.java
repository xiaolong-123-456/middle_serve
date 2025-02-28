package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateMerchantProdect;
import com.ruoyi.operate.mapper.OperateMerchantProdectMapper;
import com.ruoyi.operate.service.IOperateMerchantProdectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 商户和产品配置详情Service业务层处理
 * 
 * @author master123
 * @date 2024-09-12
 */
@Service
public class OperateMerchantProdectServiceImpl implements IOperateMerchantProdectService 
{
    @Autowired
    private OperateMerchantProdectMapper operateMerchantProdectMapper;

    /**
     * 查询商户和产品配置详情
     * 
     * @param id 商户和产品配置详情主键
     * @return 商户和产品配置详情
     */
    @Override
    public OperateMerchantProdect selectOperateMerchantProdectById(Long id)
    {
        return operateMerchantProdectMapper.selectOperateMerchantProdectById(id);
    }

    /**
     * 查询商户和产品配置详情列表
     * 
     * @param operateMerchantProdect 商户和产品配置详情
     * @return 商户和产品配置详情
     */
    @Override
    public List<OperateMerchantProdect> selectOperateMerchantProdectList(OperateMerchantProdect operateMerchantProdect)
    {
        return operateMerchantProdectMapper.selectOperateMerchantProdectList(operateMerchantProdect);
    }

    /**
     * 新增商户和产品配置详情
     * 
     * @param operateMerchantProdect 商户和产品配置详情
     * @return 结果
     */
    @Override
    public int insertOperateMerchantProdect(OperateMerchantProdect operateMerchantProdect)
    {
        operateMerchantProdect.setCreateTime(DateUtils.getNowDate());
        return operateMerchantProdectMapper.insertOperateMerchantProdect(operateMerchantProdect);
    }

    /**
     * 修改商户和产品配置详情
     * 
     * @param operateMerchantProdect 商户和产品配置详情
     * @return 结果
     */
    @Override
    public int updateOperateMerchantProdect(OperateMerchantProdect operateMerchantProdect)
    {
        operateMerchantProdect.setUpdateTime(DateUtils.getNowDate());
        return operateMerchantProdectMapper.updateOperateMerchantProdect(operateMerchantProdect);
    }

    /**
     * 批量删除商户和产品配置详情
     * 
     * @param ids 需要删除的商户和产品配置详情主键
     * @return 结果
     */
    @Override
    public int deleteOperateMerchantProdectByIds(Long[] ids)
    {
        return operateMerchantProdectMapper.deleteOperateMerchantProdectByIds(ids);
    }

    /**
     * 删除商户和产品配置详情信息
     * 
     * @param id 商户和产品配置详情主键
     * @return 结果
     */
    @Override
    public int deleteOperateMerchantProdectById(Long id)
    {
        return operateMerchantProdectMapper.deleteOperateMerchantProdectById(id);
    }

    @Override
    public List<OperateMerchantProdect> queryMchProList(Map<String, Object> params) {
        return operateMerchantProdectMapper.selectMchProList(params);
    }
}
