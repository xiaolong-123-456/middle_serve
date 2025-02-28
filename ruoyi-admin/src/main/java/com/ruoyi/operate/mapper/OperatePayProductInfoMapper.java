package com.ruoyi.operate.mapper;

import com.ruoyi.operate.domain.OperatePayProductInfo;
import com.ruoyi.operate.dto.OperatePayProjectInfoDTO;
import com.ruoyi.operate.dto.RelevanceDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 支付产品详情信息Mapper接口
 * 
 * @author master123
 * @date 2024-09-09
 */
@Mapper
public interface OperatePayProductInfoMapper 
{
    /**
     * 查询支付产品详情信息
     * 
     * @param id 支付产品详情信息主键
     * @return 支付产品详情信息
     */
    public OperatePayProductInfo selectOperatePayProductInfoById(Long id);

    /**
     * 查询支付产品详情信息列表
     * 
     * @param operatePayProductInfo 支付产品详情信息
     * @return 支付产品详情信息集合
     */
    public List<OperatePayProductInfo> selectOperatePayProductInfoList(OperatePayProductInfo operatePayProductInfo);

    /**
     * 新增支付产品详情信息
     * 
     * @param operatePayProductInfo 支付产品详情信息
     * @return 结果
     */
    public int insertOperatePayProductInfo(OperatePayProductInfo operatePayProductInfo);

    /**
     * 修改支付产品详情信息
     * 
     * @param operatePayProductInfo 支付产品详情信息
     * @return 结果
     */
    public int updateOperatePayProductInfo(OperatePayProductInfo operatePayProductInfo);

    /**
     * 删除支付产品详情信息
     * 
     * @param id 支付产品详情信息主键
     * @return 结果
     */
    public int deleteOperatePayProductInfoById(Long id);

    /**
     * 批量删除支付产品详情信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOperatePayProductInfoByIds(Long[] ids);

    List<OperatePayProjectInfoDTO> selectListByPayProductId(Long payProductId);

    int updateByPayChaId(@Param("riskControlId") Long riskControlId, @Param("payChaId") Long payChaId);

    int updateByIds(Map<String, Object> params);

    int updateByProIds(Map<String, Object> params);

    int deleteByParams(Map<String, Object> params);

    List<Long> selectDataListByPayChaId(Long payChaId);

    /**
     * 下单时根据统一配置，查询关联的通道
     *
     * @param payProductId 产品id
     * @return 结果
     */
    List<RelevanceDTO> selectChaByProId(Long payProductId);
}