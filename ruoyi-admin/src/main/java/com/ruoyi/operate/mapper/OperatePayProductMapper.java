package com.ruoyi.operate.mapper;

import com.ruoyi.operate.domain.OperatePayProduct;
import com.ruoyi.operate.dto.OperatePayProTableDTO;
import com.ruoyi.operate.dto.OperatePayProjectDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 支付产品信息Mapper接口
 * 
 * @author master123
 * @date 2024-09-05
 */
@Mapper
public interface OperatePayProductMapper 
{
    /**
     * 查询支付产品信息
     * 
     * @param id 支付产品信息主键
     * @return 支付产品信息
     */
    public OperatePayProduct selectOperatePayProductById(Long id);

    /**
     * 查询支付产品信息列表
     * 
     * @param operatePayProduct 支付产品信息
     * @return 支付产品信息集合
     */
    public List<OperatePayProduct> selectOperatePayProductList(OperatePayProduct operatePayProduct);

    List<OperatePayProduct> selectPayProductList(OperatePayProjectDTO operatePayProjectDTO);

    /**
     * 新增支付产品信息
     * 
     * @param operatePayProduct 支付产品信息
     * @return 结果
     */
    public int insertOperatePayProduct(OperatePayProduct operatePayProduct);

    /**
     * 修改支付产品信息
     * 
     * @param operatePayProduct 支付产品信息
     * @return 结果
     */
    public int updateOperatePayProduct(OperatePayProduct operatePayProduct);

    /**
     * 删除支付产品信息
     * 
     * @param id 支付产品信息主键
     * @return 结果
     */
    public int deleteOperatePayProductById(Long id);

    /**
     * 批量删除支付产品信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOperatePayProductByIds(Long[] ids);


    /**
     * 获取所有产品关联商户
     *
     * @param params 参数
     * @return 结果
     */
    List<OperatePayProTableDTO> selectPayProAllByMchId(Map<String, Object> params);

    /**
     * 获取所有产品关联代理商
     *
     * @param params 参数
     * @return 结果
     */
    List<OperatePayProTableDTO> selectPayProAllByAgentId(Map<String, Object> params);

    /**
     * 获取所有产品
     *
     * @return 结果
     */
    List<OperatePayProTableDTO> selectPayProAll();

    /**
     * 启用禁用
     */
    int forbidOrEnableByIds(Map<String, Object> params);
}
