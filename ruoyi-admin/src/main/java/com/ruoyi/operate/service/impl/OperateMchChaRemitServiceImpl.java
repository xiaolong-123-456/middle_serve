package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateMchChaRemit;
import com.ruoyi.operate.mapper.OperateMchChaRemitMapper;
import com.ruoyi.operate.service.IOperateMchChaRemitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商户和渠道打款Service业务层处理
 * 
 * @author master123
 * @date 2024-09-30
 */
@Service
public class OperateMchChaRemitServiceImpl implements IOperateMchChaRemitService 
{
    @Autowired
    private OperateMchChaRemitMapper operateMchChaRemitMapper;

    /**
     * 查询商户和渠道打款
     * 
     * @param id 商户和渠道打款主键
     * @return 商户和渠道打款
     */
    @Override
    public OperateMchChaRemit selectOperateMchChaRemitById(Long id)
    {
        return operateMchChaRemitMapper.selectOperateMchChaRemitById(id);
    }

    /**
     * 查询商户和渠道打款列表
     * 
     * @param operateMchChaRemit 商户和渠道打款
     * @return 商户和渠道打款
     */
    @Override
    public List<OperateMchChaRemit> selectOperateMchChaRemitList(OperateMchChaRemit operateMchChaRemit)
    {
        return operateMchChaRemitMapper.selectOperateMchChaRemitList(operateMchChaRemit);
    }

    /**
     * 新增商户和渠道打款
     * 
     * @param operateMchChaRemit 商户和渠道打款
     * @return 结果
     */
    @Override
    public int insertOperateMchChaRemit(OperateMchChaRemit operateMchChaRemit)
    {
        operateMchChaRemit.setCreateTime(DateUtils.getNowDate());
        return operateMchChaRemitMapper.insertOperateMchChaRemit(operateMchChaRemit);
    }

    /**
     * 修改商户和渠道打款
     * 
     * @param operateMchChaRemit 商户和渠道打款
     * @return 结果
     */
    @Override
    public int updateOperateMchChaRemit(OperateMchChaRemit operateMchChaRemit)
    {
        operateMchChaRemit.setUpdateTime(DateUtils.getNowDate());
        return operateMchChaRemitMapper.updateOperateMchChaRemit(operateMchChaRemit);
    }

    /**
     * 批量删除商户和渠道打款
     * 
     * @param ids 需要删除的商户和渠道打款主键
     * @return 结果
     */
    @Override
    public int deleteOperateMchChaRemitByIds(Long[] ids)
    {
        return operateMchChaRemitMapper.deleteOperateMchChaRemitByIds(ids);
    }

    /**
     * 删除商户和渠道打款信息
     * 
     * @param id 商户和渠道打款主键
     * @return 结果
     */
    @Override
    public int deleteOperateMchChaRemitById(Long id)
    {
        return operateMchChaRemitMapper.deleteOperateMchChaRemitById(id);
    }
}
