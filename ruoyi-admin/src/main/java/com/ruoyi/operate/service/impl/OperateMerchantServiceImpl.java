package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateMerchant;
import com.ruoyi.operate.dto.MchAccountDTO;
import com.ruoyi.operate.dto.OperateMerchantDTO;
import com.ruoyi.operate.mapper.OperateMerchantMapper;
import com.ruoyi.operate.service.IOperateMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商户信息Service业务层处理
 * 
 * @author zjj
 * @date 2024-08-19
 */
@Service
public class OperateMerchantServiceImpl implements IOperateMerchantService 
{
    @Autowired
    private OperateMerchantMapper operateMerchantMapper;

    /**
     * 查询商户信息
     * 
     * @param id 商户信息主键
     * @return 商户信息
     */
    @Override
    public OperateMerchant selectOperateMerchantById(Long id)
    {
        return operateMerchantMapper.selectOperateMerchantById(id);
    }

    /**
     * 查询商户信息列表
     * 
     * @param operateMerchantDTO 商户信息
     * @return 商户信息
     */
    @Override
    public List<OperateMerchantDTO> selectOperateMerchantList(OperateMerchantDTO operateMerchantDTO)
    {
        return operateMerchantMapper.selectOperateMerchantList(operateMerchantDTO);
    }

    /**
     * 新增商户信息
     * 
     * @param operateMerchant 商户信息
     * @return 结果
     */
    @Override
    public int insertOperateMerchant(OperateMerchant operateMerchant)
    {
        operateMerchant.setCreateTime(DateUtils.getNowDate());
        return operateMerchantMapper.insertOperateMerchant(operateMerchant);
    }

    /**
     * 修改商户信息
     * 
     * @param operateMerchant 商户信息
     * @return 结果
     */
    @Override
    public int updateOperateMerchant(OperateMerchant operateMerchant)
    {
        operateMerchant.setUpdateTime(DateUtils.getNowDate());
        return operateMerchantMapper.updateOperateMerchant(operateMerchant);
    }

    /**
     * 批量删除商户信息
     * 
     * @param ids 需要删除的商户信息主键
     * @return 结果
     */
    @Override
    public int deleteOperateMerchantByIds(Long[] ids)
    {
        return operateMerchantMapper.deleteOperateMerchantByIds(ids);
    }

    /**
     * 删除商户信息信息
     * 
     * @param id 商户信息主键
     * @return 结果
     */
    @Override
    public int deleteOperateMerchantById(Long id)
    {
        return operateMerchantMapper.deleteOperateMerchantById(id);
    }

    @Override
    public List<MchAccountDTO> queryMchAccountList(MchAccountDTO mchAccountDTO) {
        return operateMerchantMapper.selectMchAccountList(mchAccountDTO);
    }

    @Override
    public  List<OperateMerchant> queryMchByGroupId(Long tgmGroup) {
        return operateMerchantMapper.selectMchByGroupId(tgmGroup);
    }

    @Override
    public OperateMerchant queryOperateMerchantByUserId(Long userId) {
        return operateMerchantMapper.selectOperateMerchantByUserId(userId);
    }
}
