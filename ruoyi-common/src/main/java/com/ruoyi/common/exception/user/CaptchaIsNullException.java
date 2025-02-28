package com.ruoyi.common.exception.user;

/**
 * 验证码为空异常类
 *
 */
public class CaptchaIsNullException extends UserException
{
    private static final long serialVersionUID = 1L;

    public CaptchaIsNullException()
    {
        super("user.jcaptcha.isNull.error", null);
    }
}
