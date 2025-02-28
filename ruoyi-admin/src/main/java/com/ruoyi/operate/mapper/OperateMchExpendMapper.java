package com.ruoyi.operate.mapper;

import com.ruoyi.operate.domain.OperateMchExpend;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商户下发Mapper接口
 * 
 * @author master123
 * @date 2024-09-27
 */
@Mapper
public interface OperateMchExpendMapper 
{
    /**
     * 查询商户下发
     * 
     * @param id 商户下发主键
     * @return 商户下发
     */
    public OperateMchExpend selectOperateMchExpendById(Long id);

    /**
     * 查询商户下发列表
     * 
     * @param operateMchExpend 商户下发
     * @return 商户下发集合
     */
    public List<OperateMchExpend> selectOperateMchExpendList(OperateMchExpend operateMchExpend);

    /**
     * 新增商户下发
     * 
     * @param operateMchExpend 商户下发
     * @return 结果
     */
    public int insertOperateMchExpend(OperateMchExpend operateMchExpend);

    /**
     * 修改商户下发
     * 
     * @param operateMchExpend 商户下发
     * @return 结果
     */
    public int updateOperateMchExpend(OperateMchExpend operateMchExpend);

    /**
     * 删除商户下发
     * 
     * @param id 商户下发主键
     * @return 结果
     */
    public int deleteOperateMchExpendById(Long id);

    /**
     * 批量删除商户下发
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOperateMchExpendByIds(Long[] ids);
}
