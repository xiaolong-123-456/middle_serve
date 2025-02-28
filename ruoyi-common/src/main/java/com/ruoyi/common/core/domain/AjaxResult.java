package com.ruoyi.common.core.domain;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 操作消息提醒
 * 
 * @author ruoyi
 */
public class AjaxResult extends HashMap<String, Object>
{
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    public static final String CODE_TAG = "code";

    /** 返回内容 */
    public static final String MSG_TAG = "msg";

    /** 数据对象 */
    public static final String DATA_TAG = "data";


//    /** 对接系统状态码 */
//    public static final String CODE_TAG = "code";
//
//    /** 对接系统返回消息 */
//    public static final String MSG_TAG = "msg";

    /** 对接系统支付url */
    public static final String PAYURL_TAG = "payUrl";

    /** 对接系统支付url */
    public static final String PAYPARAM_TAG = "payParam";

    /** 对接系统本渠道订单号 */
    public static final String PAYORDERNO_TAG = "payOrderNo";

    /** 对接系统sign */
    public static final String SIGN_TAG = "sign";

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public AjaxResult()
    {
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     * 
     * @param code 状态码
     * @param msg 返回内容
     */
    public AjaxResult(int code, String msg)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     * 
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public AjaxResult(int code, String msg, Object data)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(data))
        {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 用于系统数据对接 AjaxResult 对象（下单返回给商户）
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param payUrl URL
     * @param payOrderNo 本系统订单id
     * @param sign 验签
     */
    public AjaxResult(String code, String msg, String payUrl, String payOrderNo, String sign)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(payUrl) && payUrl != null)
        {
            super.put(PAYURL_TAG, payUrl);
        }
        if (StringUtils.isNotNull(payOrderNo) && payOrderNo != null)
        {
            super.put(PAYORDERNO_TAG, payOrderNo);
        }
        if (StringUtils.isNotNull(sign) && sign != null)
        {
            super.put(SIGN_TAG, sign);
        }
    }

    /**
     * 用于系统数据对接 AjaxResult 对象（查询返回给商户）
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param amount 金额
     * @param mchId 商户id
     * @param productId 产品id
     * @param payOrderNo 本系统订单
     * @param mchOrderNo 商户订单号
     * @param status 订单状态
     */
    public AjaxResult(String code, String msg, Integer amount, String mchId,
                      String productId,String payOrderNo,String mchOrderNo,Integer status)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(amount) && amount != null)
        {
            super.put("amount", amount);
        }
        if (StringUtils.isNotNull(mchId) && mchId != null)
        {
            super.put("mchId", mchId);
        }
        if (StringUtils.isNotNull(productId) && productId != null)
        {
            super.put("productId", productId);
        }
        if (StringUtils.isNotNull(mchOrderNo) && mchOrderNo != null)
        {
            super.put("mchOrderNo", mchOrderNo);
        }
        if (StringUtils.isNotNull(payOrderNo) && payOrderNo != null)
        {
            super.put(PAYORDERNO_TAG, payOrderNo);
        }
        if (StringUtils.isNotNull(status) && status != null)
        {
            super.put("status", status);
        }
    }

    /**
     * 用于系统数据对接 AjaxResult 对象（下单返回给商户）
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param payOrderNo 本系统订单
     * @param sign 验签
     */
    public AjaxResult(String code, String msg, Map<String, Object> payParam, String payOrderNo, String sign,String t)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(payParam) && payParam != null)
        {
            super.put(PAYPARAM_TAG, payParam);
        }
        if (StringUtils.isNotNull(payOrderNo) && payOrderNo != null)
        {
            super.put(PAYORDERNO_TAG, payOrderNo);
        }
        if (StringUtils.isNotNull(sign) && sign != null)
        {
            super.put(SIGN_TAG, sign);
        }
    }

    /**
     * 返回成功消息
     * 
     * @return 成功消息
     */
    public static AjaxResult success()
    {
        return AjaxResult.success("操作成功");
    }

    /**
     * 返回成功数据
     * 
     * @return 成功消息
     */
    public static AjaxResult success(Object data)
    {
        return AjaxResult.success("操作成功", data);
    }

    /**
     * 返回成功消息
     * 
     * @param msg 返回内容
     * @return 成功消息
     */
    public static AjaxResult success(String msg)
    {
        return AjaxResult.success(msg, null);
    }

    /**
     * 返回成功消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static AjaxResult success(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static AjaxResult warn(String msg)
    {
        return AjaxResult.warn(msg, null);
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static AjaxResult warn(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.WARN, msg, data);
    }

    /**
     * 返回错误消息
     * 
     * @return 错误消息
     */
    public static AjaxResult error()
    {
        return AjaxResult.error("操作失败");
    }

    /**
     * 返回错误消息
     * 
     * @param msg 返回内容
     * @return 错误消息
     */
    public static AjaxResult error(String msg)
    {
        return AjaxResult.error(msg, null);
    }

    /**
     * 返回错误消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 错误消息
     */
    public static AjaxResult error(String msg, Object data)
    {
        return new AjaxResult(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     * 
     * @param code 状态码
     * @param msg 返回内容
     * @return 错误消息
     */
    public static AjaxResult error(int code, String msg)
    {
        return new AjaxResult(code, msg, null);
    }

    /**
     * 是否为成功消息
     *
     * @return 结果
     */
    public boolean isSuccess()
    {
        return Objects.equals(HttpStatus.SUCCESS, this.get(CODE_TAG));
    }

    /**
     * 是否为警告消息
     *
     * @return 结果
     */
    public boolean isWarn()
    {
        return Objects.equals(HttpStatus.WARN, this.get(CODE_TAG));
    }

    /**
     * 是否为错误消息
     *
     * @return 结果
     */
    public boolean isError()
    {
        return Objects.equals(HttpStatus.ERROR, this.get(CODE_TAG));
    }

    /**
     * 方便链式调用
     *
     * @param key 键
     * @param value 值
     * @return 数据对象
     */
    @Override
    public AjaxResult put(String key, Object value)
    {
        super.put(key, value);
        return this;
    }
}
