package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperateMerchant;
import com.ruoyi.operate.dto.MchAccountDTO;
import com.ruoyi.operate.dto.OperateMerchantDTO;

import java.util.List;

/**
 * 商户信息Service接口
 * 
 * @author zjj
 * @date 2024-08-19
 */
public interface IOperateMerchantService 
{
    /**
     * 查询商户信息
     * 
     * @param id 商户信息主键
     * @return 商户信息
     */
    public OperateMerchant selectOperateMerchantById(Long id);

    /**
     * 查询商户信息列表
     * 
     * @param operateMerchantDTO 商户信息
     * @return 商户信息集合
     */
    public List<OperateMerchantDTO> selectOperateMerchantList(OperateMerchantDTO operateMerchantDTO);

    /**
     * 新增商户信息
     * 
     * @param operateMerchant 商户信息
     * @return 结果
     */
    public int insertOperateMerchant(OperateMerchant operateMerchant);

    /**
     * 修改商户信息
     * 
     * @param operateMerchant 商户信息
     * @return 结果
     */
    public int updateOperateMerchant(OperateMerchant operateMerchant);

    /**
     * 批量删除商户信息
     * 
     * @param ids 需要删除的商户信息主键集合
     * @return 结果
     */
    public int deleteOperateMerchantByIds(Long[] ids);

    /**
     * 删除商户信息信息
     * 
     * @param id 商户信息主键
     * @return 结果
     */
    public int deleteOperateMerchantById(Long id);

    /**
     * 查询商户账户列表
     *
     * @param mchAccountDTO 商户信息
     * @return 结果
     */
    List<MchAccountDTO> queryMchAccountList(MchAccountDTO mchAccountDTO);

    /**
     * 根据飞机群id 查询商户
     *
     * @param tgmGroup 飞机群id
     * @return 结果
     */
    List<OperateMerchant> queryMchByGroupId(Long tgmGroup);

    /**
     * 查询商户
     *
     * @param userId
     */
    public OperateMerchant queryOperateMerchantByUserId(Long userId);
}
