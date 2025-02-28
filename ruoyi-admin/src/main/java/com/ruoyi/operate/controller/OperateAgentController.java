package com.ruoyi.operate.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.GoogleAuthenticator;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.operate.domain.OperateAgent;
import com.ruoyi.operate.service.IOperateAgentService;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 所有代理商Controller
 * 
 * @author master123
 * @date 2024-08-26
 */
@RestController
@RequestMapping("/operate/agent")
public class OperateAgentController extends BaseController
{
    @Autowired
    private IOperateAgentService operateAgentService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysUserMapper userMapper;

    /**
     * 查询所有代理商列表
     */
    @PreAuthorize("@ss.hasPermi('operate:agent:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperateAgent operateAgent)
    {
        startPage();
        List<OperateAgent> list = operateAgentService.selectOperateAgentList(operateAgent);
        return getDataTable(list);
    }

    /**
     * 导出所有代理商列表
     */
    @PreAuthorize("@ss.hasPermi('operate:agent:export')")
    @Log(title = "所有代理商", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperateAgent operateAgent)
    {
        List<OperateAgent> list = operateAgentService.selectOperateAgentList(operateAgent);
        ExcelUtil<OperateAgent> util = new ExcelUtil<OperateAgent>(OperateAgent.class);
        util.exportExcel(response, list, "所有代理商数据");
    }

    /**
     * 获取所有代理商详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:agent:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(operateAgentService.selectOperateAgentById(id));
    }

    /**
     * 新增所有代理商
     */
    @PreAuthorize("@ss.hasPermi('operate:agent:add')")
    @Log(title = "所有代理商", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateAgent operateAgent)
    {
        operateAgent.setCreateBy(getUsername());
        //创建商户的mchKey
        String secretKey = GoogleAuthenticator.getSecretKey();
        operateAgent.setAgentKey(secretKey);
//        operateAgent.setLoginPwd(SecurityUtils.encryptPassword(operateAgent.getLoginPwd()));

        //创建用户
        SysUser sysUser = new SysUser();
        sysUser.setNickName(operateAgent.getAgentName());
        sysUser.setUserType("22");
        sysUser.setUserName(operateAgent.getLoginAct());
        sysUser.setPassword(operateAgent.getLoginPwd());
        sysUser.setUserKey(secretKey);

        //用户校验
        if (!userService.checkUserNameUnique(sysUser))
        {
            return error("新增代理商'" + sysUser.getUserName() + "'失败，登录账号已存在");
        }
        sysUser.setCreateBy(getUsername());
        sysUser.setPassword(SecurityUtils.encryptPassword(sysUser.getPassword()));
        userService.insertUser(sysUser);

        // 新增用户与角色管理
        List<SysUserRole> list = new ArrayList();
        SysUserRole ur = new SysUserRole();
        ur.setUserId(sysUser.getUserId());
        ur.setRoleId(Long.valueOf(3));
        list.add(ur);
        sysUserRoleMapper.batchUserRole(list);

        operateAgent.setUserId(sysUser.getUserId());
        return toAjax(operateAgentService.insertOperateAgent(operateAgent));
    }

    /**
     * 修改所有代理商
     */
    @PreAuthorize("@ss.hasPermi('operate:agent:edit')")
    @Log(title = "所有代理商", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateAgent operateAgent)
    {

        //修改用户
        SysUser sysUser = userService.selectUserByName(operateAgent.getLoginAct());
        if(StringUtils.isNotEmpty(operateAgent.getAgentName())){
            sysUser.setNickName(operateAgent.getAgentName());
        }
        if(StringUtils.isNotEmpty(operateAgent.getLoginPwd())){
            sysUser.setPassword(SecurityUtils.encryptPassword(operateAgent.getLoginPwd()));
        }
        if(StringUtils.isNotEmpty(operateAgent.getAgentKey())){
            sysUser.setUserKey(operateAgent.getAgentKey());
        }
        if(operateAgent.getStatus().equals("1")){
            sysUser.setDelFlag("2");
        }else{
            sysUser.setDelFlag("0");
        }
        sysUser.setUpdateBy(getUsername());
        userService.updateUser(sysUser);

        operateAgent.setUpdateBy(getUsername());
        return toAjax(operateAgentService.updateOperateAgent(operateAgent));
    }

    /**
     * 修改余额
     */
    @PreAuthorize("@ss.hasPermi('operate:agent:edit')")
    @Log(title = "代理商余额变更", businessType = BusinessType.UPDATE)
    @GetMapping("/updateBlance")
    public AjaxResult updateBlance(OperateAgent operateAgent)
    {
        operateAgent.setUpdateBy(getUsername());
        return toAjax(operateAgentService.updateOperateAgent(operateAgent));
    }

    /**
     * 删除所有代理商
     */
    @PreAuthorize("@ss.hasPermi('operate:agent:remove')")
    @Log(title = "所有代理商", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        for(Long id:ids){
            OperateAgent operateAgent = operateAgentService.selectOperateAgentById(id);
            userMapper.deleteUserById(operateAgent.getUserId());
        }
        return toAjax(operateAgentService.deleteOperateAgentByIds(ids));
    }

    /**
     * userId获取代理商数据
     */
    @GetMapping("/getAgentData")
    public AjaxResult getAgentData(@RequestParam("userId") String userId){
        OperateAgent operateAgent = operateAgentService.queryOperateAgentByUserId(Long.valueOf(userId));
        //查询代理商
        AjaxResult ajax = AjaxResult.success();
        ajax.put(AjaxResult.DATA_TAG, operateAgent);
        return ajax;
    }

    /**
     * 通过userId改代理商key
     */
    @GetMapping("/updateAgentData")
    public AjaxResult updateAgentData(@RequestParam Map<String, Object> params){
        String key = (String) params.get("key");
        String userId = (String) params.get("userId");
        //代理商
        OperateAgent operateAgent = operateAgentService.queryOperateAgentByUserId(Long.valueOf(userId));
        operateAgent.setAgentKey(key);
        operateAgent.setUpdateBy(getUsername());
        operateAgentService.updateOperateAgent(operateAgent);
        //用户
        SysUser sysUser = userService.selectUserById(Long.valueOf(userId));
        sysUser.setCreateBy(getUsername());
        sysUser.setUserKey(key);
        sysUser.setLoginSet("0");
        sysUser.setIsGoogle("0");

        return toAjax(userService.updateUser(sysUser));
    }
}
