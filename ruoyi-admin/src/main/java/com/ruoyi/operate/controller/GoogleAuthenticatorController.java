package com.ruoyi.operate.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.GoogleAuthenticator;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.operate.service.IOperateMerchantService;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.system.service.ISysUserService;
import org.iherus.codegen.qrcode.SimpleQrcodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 商户和产品配置详情Controller
 * 
 * @author master123
 * @date 2024-09-12
 */
@RestController
@RequestMapping("/operate/GoogleAuthenticator")
public class GoogleAuthenticatorController extends BaseController
{

    @Autowired
    private IOperateMerchantService operateMerchantService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    private static String SECRET_KEY = "";


    /**
     * 生成 Google 密钥，两种方式任选一种
     */
    @GetMapping("getSecretKey")
//    @Log(title = "生成 Google 密钥", businessType = BusinessType.OTHER)
    public String getSecretKey() {
        String secretKey = GoogleAuthenticator.getSecretKey();
        SECRET_KEY = secretKey;
        return secretKey;
    }

    /**
     * 生成二维码，APP直接扫描绑定，两种方式任选一种
     */
    @GetMapping("getQrcode")
//    @Log(title = "生成二维码，APP直接扫描绑定", businessType = BusinessType.OTHER)
    public void getQrcode(@RequestParam("userId") String userId, HttpServletResponse response) throws Exception {
        response.setContentType("image/png");
        SysUser sysUser = sysUserService.selectUserById(Long.valueOf(userId));
        SECRET_KEY = sysUser.getUserKey();
        // 生成二维码内容
        String qrCodeText = GoogleAuthenticator.getQrCodeText(sysUser.getUserKey(), sysUser.getUserName(), "");
        // 生成二维码输出
        new SimpleQrcodeGenerator().generate(qrCodeText).toStream(response.getOutputStream());
    }

    /**
     * 获取code
     */
    @GetMapping("getCode")
//    @Log(title = "谷歌验证获取code", businessType = BusinessType.OTHER)
    public String getCode() {
        return GoogleAuthenticator.getCode(SECRET_KEY);
    }

    /**
     * 安全中心设置
     */
    @GetMapping("checkCode")
//    @Log(title = "安全中心设置", businessType = BusinessType.OTHER)
    public Boolean checkCode(@RequestParam Map<String, Object> params) {
        String code = (String) params.get("code");
        String userId = (String) params.get("userId");
        String loginSet = (String) params.get("loginSet");
        SysUser sysUser = sysUserService.selectUserById(Long.valueOf(userId));
        boolean result = GoogleAuthenticator.checkCode(sysUser.getUserKey(), Long.parseLong(code), System.currentTimeMillis());
        if(result){
            sysUser.setIsGoogle("1");
            sysUser.setLoginSet("1");
            if(StringUtils.isNotEmpty(loginSet)){
                sysUser.setLoginSet(loginSet);
            }
            sysUserService.updateUser2(sysUser);

        }
        return result;

    }

    /**
     * 验证code是否正确
     */
    @GetMapping("validateCode")
    public Boolean validateCode(@RequestParam Map<String, Object> params) {
        String code = (String) params.get("code");
        String userId = (String) params.get("userId");
        SysUser sysUser = sysUserService.selectUserById(Long.valueOf(userId));
        boolean result = GoogleAuthenticator.checkCode(sysUser.getUserKey(), Long.parseLong(code), System.currentTimeMillis());
        return result;
    }

}
