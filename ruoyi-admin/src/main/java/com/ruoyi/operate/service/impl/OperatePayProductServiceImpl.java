package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperatePayProduct;
import com.ruoyi.operate.dto.OperatePayProTableDTO;
import com.ruoyi.operate.dto.OperatePayProjectDTO;
import com.ruoyi.operate.mapper.OperatePayProductMapper;
import com.ruoyi.operate.service.IOperatePayProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 支付产品信息Service业务层处理
 * 
 * @author master123
 * @date 2024-09-05
 */
@Service
public class OperatePayProductServiceImpl implements IOperatePayProductService 
{
    @Autowired
    private OperatePayProductMapper operatePayProductMapper;

    /**
     * 查询支付产品信息
     * 
     * @param id 支付产品信息主键
     * @return 支付产品信息
     */
    @Override
    public OperatePayProduct selectOperatePayProductById(Long id)
    {
        return operatePayProductMapper.selectOperatePayProductById(id);
    }

    /**
     * 查询支付产品信息列表
     * 
     * @param operatePayProduct 支付产品信息
     * @return 支付产品信息
     */
    @Override
    public List<OperatePayProduct> selectOperatePayProductList(OperatePayProduct operatePayProduct)
    {
        return operatePayProductMapper.selectOperatePayProductList(operatePayProduct);
    }

    @Override
    public List<OperatePayProduct> selectpPayProductList(OperatePayProjectDTO operatePayProjectDTO) {
        return operatePayProductMapper.selectPayProductList(operatePayProjectDTO);
    }

    /**
     * 新增支付产品信息
     * 
     * @param operatePayProduct 支付产品信息
     * @return 结果
     */
    @Override
    public int insertOperatePayProduct(OperatePayProduct operatePayProduct)
    {
        operatePayProduct.setCreateTime(DateUtils.getNowDate());
        return operatePayProductMapper.insertOperatePayProduct(operatePayProduct);
    }

    /**
     * 修改支付产品信息
     * 
     * @param operatePayProduct 支付产品信息
     * @return 结果
     */
    @Override
    public int updateOperatePayProduct(OperatePayProduct operatePayProduct)
    {
        operatePayProduct.setUpdateTime(DateUtils.getNowDate());
        return operatePayProductMapper.updateOperatePayProduct(operatePayProduct);
    }

    /**
     * 批量删除支付产品信息
     * 
     * @param ids 需要删除的支付产品信息主键
     * @return 结果
     */
    @Override
    public int deleteOperatePayProductByIds(Long[] ids)
    {
        return operatePayProductMapper.deleteOperatePayProductByIds(ids);
    }

    /**
     * 删除支付产品信息信息
     * 
     * @param id 支付产品信息主键
     * @return 结果
     */
    @Override
    public int deleteOperatePayProductById(Long id)
    {
        return operatePayProductMapper.deleteOperatePayProductById(id);
    }

    @Override
    public List<OperatePayProTableDTO> getPayProAllByMchId(Map<String, Object> params) {
        return operatePayProductMapper.selectPayProAllByMchId(params);
    }

    @Override
    public List<OperatePayProTableDTO> getPayProAllByAgentId(Map<String, Object> params) {
        return operatePayProductMapper.selectPayProAllByAgentId(params);
    }

    @Override
    public List<OperatePayProTableDTO> getPayProAll() {
        return operatePayProductMapper.selectPayProAll();
    }

    @Override
    public int forbidOrEnableByIds(Map<String, Object> params) {
        return operatePayProductMapper.forbidOrEnableByIds(params);
    }
}
