package com.ruoyi.operate.mapper;

import com.ruoyi.operate.domain.OperateMerchant;
import com.ruoyi.operate.dto.MchAccountDTO;
import com.ruoyi.operate.dto.OperateMerchantDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商户信息Mapper接口
 * 
 * @author master123
 * @date 2024-08-19
 */
@Mapper
public interface OperateMerchantMapper 
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
     * 删除商户信息
     * 
     * @param id 商户信息主键
     * @return 结果
     */
    public int deleteOperateMerchantById(Long id);

    /**
     * 批量删除商户信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOperateMerchantByIds(Long[] ids);

    /**
     * 查询商户账
     * @return 结果
     */
    public List<MchAccountDTO> selectMchAccountList(MchAccountDTO mchAccountDTO);


    /**
     * 根据飞机群id 查询商户
     *
     * @param tgmGroup 飞机群id
     * @return 结果
     */
    public  List<OperateMerchant> selectMchByGroupId(Long tgmGroup);

    /**
     * 查询商户
     * @param userId 用户id
     */
    public OperateMerchant selectOperateMerchantByUserId(Long userId);

}
