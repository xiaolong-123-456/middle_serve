package com.ruoyi.operate.mapper;

import com.ruoyi.operate.domain.OperateMchChaRemit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商户和渠道打款Mapper接口
 * 
 * @author master123
 * @date 2024-09-30
 */
@Mapper
public interface OperateMchChaRemitMapper 
{
    /**
     * 查询商户和渠道打款
     * 
     * @param id 商户和渠道打款主键
     * @return 商户和渠道打款
     */
    public OperateMchChaRemit selectOperateMchChaRemitById(Long id);

    /**
     * 查询商户和渠道打款列表
     * 
     * @param operateMchChaRemit 商户和渠道打款
     * @return 商户和渠道打款集合
     */
    public List<OperateMchChaRemit> selectOperateMchChaRemitList(OperateMchChaRemit operateMchChaRemit);

    /**
     * 新增商户和渠道打款
     * 
     * @param operateMchChaRemit 商户和渠道打款
     * @return 结果
     */
    public int insertOperateMchChaRemit(OperateMchChaRemit operateMchChaRemit);

    /**
     * 修改商户和渠道打款
     * 
     * @param operateMchChaRemit 商户和渠道打款
     * @return 结果
     */
    public int updateOperateMchChaRemit(OperateMchChaRemit operateMchChaRemit);

    /**
     * 删除商户和渠道打款
     * 
     * @param id 商户和渠道打款主键
     * @return 结果
     */
    public int deleteOperateMchChaRemitById(Long id);

    /**
     * 批量删除商户和渠道打款
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOperateMchChaRemitByIds(Long[] ids);
}
