package com.ruoyi.operate.mapper;

import com.ruoyi.operate.domain.OperateAgentProduct;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 代理商和产品配置Mapper接口
 * 
 * @author master123
 * @date 2024-09-20
 */
@Mapper
public interface OperateAgentProductMapper 
{
    /**
     * 查询代理商和产品配置
     * 
     * @param id 代理商和产品配置主键
     * @return 代理商和产品配置
     */
    public OperateAgentProduct selectOperateAgentProductById(Long id);

    /**
     * 查询代理商和产品配置列表
     * 
     * @param operateAgentProduct 代理商和产品配置
     * @return 代理商和产品配置集合
     */
    public List<OperateAgentProduct> selectOperateAgentProductList(OperateAgentProduct operateAgentProduct);

    /**
     * 新增代理商和产品配置
     * 
     * @param operateAgentProduct 代理商和产品配置
     * @return 结果
     */
    public int insertOperateAgentProduct(OperateAgentProduct operateAgentProduct);

    /**
     * 修改代理商和产品配置
     * 
     * @param operateAgentProduct 代理商和产品配置
     * @return 结果
     */
    public int updateOperateAgentProduct(OperateAgentProduct operateAgentProduct);

    /**
     * 删除代理商和产品配置
     * 
     * @param id 代理商和产品配置主键
     * @return 结果
     */
    public int deleteOperateAgentProductById(Long id);

    /**
     * 批量删除代理商和产品配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOperateAgentProductByIds(Long[] ids);

    /**
     * 根据产品id、代理商id查询关联
     *
     * @param params 参数
     * @return 结果
     */
    List<OperateAgentProduct> selectAgePro(Map<String, Object> params);


    /**
     * 根据产品和代理商id逻辑删除
     *
     * @param params 参数
     * @return 结果
     */
    int updateByAgentIdPro(Map<String, Object> params);

    /**
     * 根据产品和代理商id修改费率
     *
     * @param params 参数
     * @return 结果
     */
    int updateRateByAgentIdPro(Map<String, Object> params);
}
