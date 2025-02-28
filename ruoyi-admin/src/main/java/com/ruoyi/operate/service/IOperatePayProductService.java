package com.ruoyi.operate.service;

import com.ruoyi.operate.domain.OperatePayProduct;
import com.ruoyi.operate.dto.OperatePayProTableDTO;
import com.ruoyi.operate.dto.OperatePayProjectDTO;

import java.util.List;
import java.util.Map;

/**
 * 支付产品信息Service接口
 * 
 * @author master123
 * @date 2024-09-05
 */
public interface IOperatePayProductService 
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

    public List<OperatePayProduct> selectpPayProductList(OperatePayProjectDTO operatePayProjectDTO);
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
     * 批量删除支付产品信息
     * 
     * @param ids 需要删除的支付产品信息主键集合
     * @return 结果
     */
    public int deleteOperatePayProductByIds(Long[] ids);

    /**
     * 删除支付产品信息信息
     * 
     * @param id 支付产品信息主键
     * @return 结果
     */
    public int deleteOperatePayProductById(Long id);

    /**
     * 获取所有产品关联商户
     *
     * @param params 参数
     * @return 支付产品信息集合
     */
    public List<OperatePayProTableDTO> getPayProAllByMchId(Map<String, Object> params);

    /**
     * 获取所有产品关联代理商
     *
     * @param params 参数
     * @return 支付产品信息集合
     */
    public List<OperatePayProTableDTO> getPayProAllByAgentId(Map<String, Object> params);

    /**
     * 获取所有产品
     *
     * @return 支付产品信息集合
     */
    public List<OperatePayProTableDTO> getPayProAll();

    /**
     * 启用禁用
     *
     */
    public int forbidOrEnableByIds(Map<String, Object> params);
}
