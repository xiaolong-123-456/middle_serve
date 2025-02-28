package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperatePayProductInfo;
import com.ruoyi.operate.dto.OperatePayProjectInfoDTO;
import com.ruoyi.operate.dto.RelevanceDTO;
import com.ruoyi.operate.mapper.OperatePayProductInfoMapper;
import com.ruoyi.operate.service.IOperatePayProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 支付产品详情信息Service业务层处理
 * 
 * @author master123
 * @date 2024-09-09
 */
@Service
public class OperatePayProductInfoServiceImpl implements IOperatePayProductInfoService 
{
    @Autowired
    private OperatePayProductInfoMapper operatePayProductInfoMapper;

    /**
     * 查询支付产品详情信息
     * 
     * @param id 支付产品详情信息主键
     * @return 支付产品详情信息
     */
    @Override
    public OperatePayProductInfo selectOperatePayProductInfoById(Long id)
    {
        return operatePayProductInfoMapper.selectOperatePayProductInfoById(id);
    }

    /**
     * 查询支付产品详情信息列表
     * 
     * @param operatePayProductInfo 支付产品详情信息
     * @return 支付产品详情信息
     */
    @Override
    public List<OperatePayProductInfo> selectOperatePayProductInfoList(OperatePayProductInfo operatePayProductInfo)
    {
        return operatePayProductInfoMapper.selectOperatePayProductInfoList(operatePayProductInfo);
    }

    /**
     * 新增支付产品详情信息
     * 
     * @param operatePayProductInfo 支付产品详情信息
     * @return 结果
     */
    @Override
    public int insertOperatePayProductInfo(OperatePayProductInfo operatePayProductInfo)
    {
        operatePayProductInfo.setCreateTime(DateUtils.getNowDate());
        return operatePayProductInfoMapper.insertOperatePayProductInfo(operatePayProductInfo);
    }

    /**
     * 修改支付产品详情信息
     * 
     * @param operatePayProductInfo 支付产品详情信息
     * @return 结果
     */
    @Override
    public int updateOperatePayProductInfo(OperatePayProductInfo operatePayProductInfo)
    {
        operatePayProductInfo.setUpdateTime(DateUtils.getNowDate());
        return operatePayProductInfoMapper.updateOperatePayProductInfo(operatePayProductInfo);
    }

    /**
     * 批量删除支付产品详情信息
     * 
     * @param ids 需要删除的支付产品详情信息主键
     * @return 结果
     */
    @Override
    public int deleteOperatePayProductInfoByIds(Long[] ids)
    {
        return operatePayProductInfoMapper.deleteOperatePayProductInfoByIds(ids);
    }

    /**
     * 删除支付产品详情信息信息
     * 
     * @param id 支付产品详情信息主键
     * @return 结果
     */
    @Override
    public int deleteOperatePayProductInfoById(Long id)
    {
        return operatePayProductInfoMapper.deleteOperatePayProductInfoById(id);
    }

    @Override
    public List<OperatePayProjectInfoDTO> queryListByPayProductId(Long payProductId) {
        return operatePayProductInfoMapper.selectListByPayProductId(payProductId);
    }

    @Override
    public int updateByPayChaId(Long riskControlId, Long payChaId) {
        return operatePayProductInfoMapper.updateByPayChaId(riskControlId,payChaId);
    }

    @Override
    public int updateByIds(Map<String, Object> params) {
        return operatePayProductInfoMapper.updateByIds(params);
    }

    @Override
    public int deleteByParams(Map<String, Object> params) {
        return operatePayProductInfoMapper.deleteByParams(params);
    }

    @Override
    public List<RelevanceDTO> queryChaByProId(Long payProductId) {
        return operatePayProductInfoMapper.selectChaByProId(payProductId);
    }
}
