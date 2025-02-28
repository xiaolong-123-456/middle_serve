package com.ruoyi.operate.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.operate.domain.OperateMchProAlone;
import com.ruoyi.operate.dto.OperatePayProjectInfoDTO;
import com.ruoyi.operate.dto.RelevanceDTO;
import com.ruoyi.operate.mapper.OperateMchProAloneMapper;
import com.ruoyi.operate.service.IOperateMchProAloneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商户和产品单独配置Service业务层处理
 * 
 * @author master123
 * @date 2024-09-13
 */
@Service
public class OperateMchProAloneServiceImpl implements IOperateMchProAloneService 
{
    @Autowired
    private OperateMchProAloneMapper operateMchProAloneMapper;

    /**
     * 查询商户和产品单独配置
     * 
     * @param id 商户和产品单独配置主键
     * @return 商户和产品单独配置
     */
    @Override
    public OperateMchProAlone selectOperateMchProAloneById(Long id)
    {
        return operateMchProAloneMapper.selectOperateMchProAloneById(id);
    }

    /**
     * 查询商户和产品单独配置列表
     * 
     * @param operateMchProAlone 商户和产品单独配置
     * @return 商户和产品单独配置
     */
    @Override
    public List<OperateMchProAlone> selectOperateMchProAloneList(OperateMchProAlone operateMchProAlone)
    {
        return operateMchProAloneMapper.selectOperateMchProAloneList(operateMchProAlone);
    }

    /**
     * 新增商户和产品单独配置
     * 
     * @param operateMchProAlone 商户和产品单独配置
     * @return 结果
     */
    @Override
    public int insertOperateMchProAlone(OperateMchProAlone operateMchProAlone)
    {
        operateMchProAlone.setCreateTime(DateUtils.getNowDate());
        return operateMchProAloneMapper.insertOperateMchProAlone(operateMchProAlone);
    }

    /**
     * 修改商户和产品单独配置
     * 
     * @param operateMchProAlone 商户和产品单独配置
     * @return 结果
     */
    @Override
    public int updateOperateMchProAlone(OperateMchProAlone operateMchProAlone)
    {
        operateMchProAlone.setUpdateTime(DateUtils.getNowDate());
        return operateMchProAloneMapper.updateOperateMchProAlone(operateMchProAlone);
    }

    /**
     * 批量删除商户和产品单独配置
     * 
     * @param ids 需要删除的商户和产品单独配置主键
     * @return 结果
     */
    @Override
    public int deleteOperateMchProAloneByIds(Long[] ids)
    {
        return operateMchProAloneMapper.deleteOperateMchProAloneByIds(ids);
    }

    /**
     * 删除商户和产品单独配置信息
     * 
     * @param id 商户和产品单独配置主键
     * @return 结果
     */
    @Override
    public int deleteOperateMchProAloneById(Long id)
    {
        return operateMchProAloneMapper.deleteOperateMchProAloneById(id);
    }

    @Override
    public List<OperatePayProjectInfoDTO> queryListByMchProId(Long mchProId) {
        return operateMchProAloneMapper.selectListByMchProId(mchProId);
    }

    @Override
    public int updateByPayChaId(Long riskControlId, Long payChaId) {
        return operateMchProAloneMapper.updateByPayChaId(riskControlId,payChaId);
    }

    @Override
    public List<RelevanceDTO> queryChaByMchProId(Long mchProId) {
        return operateMchProAloneMapper.selectChaByMchProId(mchProId);
    }
}
