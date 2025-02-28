package com.ruoyi.operate.controller;

import com.github.pagehelper.PageInfo;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.GoogleAuthenticator;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.operate.domain.OperateAgent;
import com.ruoyi.operate.domain.OperateMerchant;
import com.ruoyi.operate.dto.MchAccountDTO;
import com.ruoyi.operate.dto.OperateBillDTO;
import com.ruoyi.operate.dto.OperateMerchantDTO;
import com.ruoyi.operate.service.IOperateAgentService;
import com.ruoyi.operate.service.IOperateMerchantService;
import com.ruoyi.operate.service.IOperateOrderService;
import com.ruoyi.operate.utils.ExecBot;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商户信息Controller
 *
 * @author master123
 * @date 2024-08-19
 */
@RestController
@RequestMapping("/operate/merchant")
public class OperateMerchantController extends BaseController
{
    @Autowired
    private IOperateMerchantService operateMerchantService;
    @Autowired
    private IOperateAgentService operateAgentService;
    @Autowired
    private ISysUserService userService;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private ExecBot execBot;
    @Autowired
    private IOperateOrderService operateOrderService;
    @Autowired
    private SysUserMapper userMapper;

    /**
     * 查询商户信息列表
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperateMerchantDTO operateMerchantDTO)
    {
        startPage();
        List<OperateMerchantDTO> list = operateMerchantService.selectOperateMerchantList(operateMerchantDTO);
        return getDataTable(list);
    }


    @GetMapping("/dataList")
    @PreAuthorize("@ss.hasPermi('operate:merchant:list')")
    public AjaxResult dataList(OperateMerchantDTO operateMerchantDTO)
    {
        List<OperateMerchantDTO> list = operateMerchantService.selectOperateMerchantList(operateMerchantDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("dataList", list);
        return ajax;
    }

//    /**
//     * 导出商户信息列表
//     */
//    @PreAuthorize("@ss.hasPermi('operate:merchant:export')")
//    @Log(title = "商户信息", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, OperateMerchant operateMerchant)
//    {
//        List<OperateMerchant> list = operateMerchantService.selectOperateMerchantList(operateMerchant);
//        ExcelUtil<OperateMerchant> util = new ExcelUtil<OperateMerchant>(OperateMerchant.class);
//        util.exportExcel(response, list, "商户信息数据");
//    }

    /**
     * 获取商户信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:query')")
    @GetMapping(value ={"/","/{id}"})
    public AjaxResult getInfo(@PathVariable(value = "id", required = false) Long id)
    {
        //查询代理商
        AjaxResult ajax = AjaxResult.success();
        List<OperateAgent> operateAgents = operateAgentService.queryOperateAgentAll();
        ajax.put("agentList", operateAgents);
        if (StringUtils.isNotNull(id))
        {
            ajax.put(AjaxResult.DATA_TAG, operateMerchantService.selectOperateMerchantById(id));
        }
        return ajax;
    }

    /**
     * 新增商户信息
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:add')")
    @Log(title = "商户信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperateMerchant operateMerchant)
    {
        operateMerchant.setCreateBy(getUsername());
        //创建商户的mchKey
        String secretKey = GoogleAuthenticator.getSecretKey();
        operateMerchant.setMchKey(secretKey);
//        operateMerchant.setLoginPwd(SecurityUtils.encryptPassword(operateMerchant.getLoginPwd()));

        //创建用户
        SysUser sysUser = new SysUser();
        sysUser.setNickName(operateMerchant.getMchName());
        sysUser.setUserType("11");
        sysUser.setUserName(operateMerchant.getLoginAct());
        sysUser.setPassword(operateMerchant.getLoginPwd());
        sysUser.setUserKey(secretKey);

        //用户校验
        if (!userService.checkUserNameUnique(sysUser))
        {
            return error("新增商户'" + sysUser.getUserName() + "'失败，登录账号已存在");
        }
        sysUser.setCreateBy(getUsername());
        sysUser.setPassword(SecurityUtils.encryptPassword(sysUser.getPassword()));
        userService.insertUser(sysUser);

        // 新增用户与角色管理
        List<SysUserRole> list = new ArrayList();
        SysUserRole ur = new SysUserRole();
        ur.setUserId(sysUser.getUserId());
        ur.setRoleId(Long.valueOf(2));
        list.add(ur);
        sysUserRoleMapper.batchUserRole(list);

        operateMerchant.setUserId(sysUser.getUserId());
        return toAjax(operateMerchantService.insertOperateMerchant(operateMerchant));
    }

    /**
     * 修改商户信息
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:edit')")
    @Log(title = "修改商户信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperateMerchant operateMerchant)
    {

        //修改用户
        SysUser sysUser = userService.selectUserByName(operateMerchant.getLoginAct());
        if(StringUtils.isNotEmpty(operateMerchant.getMchName())){
            sysUser.setNickName(operateMerchant.getMchName());
        }
        if(StringUtils.isNotEmpty(operateMerchant.getLoginPwd())){
            sysUser.setPassword(SecurityUtils.encryptPassword(operateMerchant.getLoginPwd()));
        }
        if(StringUtils.isNotEmpty(operateMerchant.getMchKey())){
            sysUser.setUserKey(operateMerchant.getMchKey());
        }
        if(operateMerchant.getStatus().equals("1")){
            sysUser.setDelFlag("2");
        }else{
            sysUser.setDelFlag("0");
        }
        sysUser.setUpdateBy(getUsername());
        userService.updateUser(sysUser);

        operateMerchant.setUpdateBy(getUsername());
        return toAjax(operateMerchantService.updateOperateMerchant(operateMerchant));
    }

    /**
     * 修改余额
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:edit')")
    @Log(title = "商户余额变更", businessType = BusinessType.UPDATE)
    @GetMapping("/updateBlance")
    public AjaxResult updateBlance(OperateMerchant operateMerchant)
    {
        operateMerchant.setUpdateBy(getUsername());
        return toAjax(operateMerchantService.updateOperateMerchant(operateMerchant));
    }

    /**
     * 删除商户信息
     */
    @PreAuthorize("@ss.hasPermi('operate:merchant:remove')")
    @Log(title = "商户信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        for(Long id:ids){
            OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(id);
            userMapper.deleteUserById(operateMerchant.getUserId());
        }

        return toAjax(operateMerchantService.deleteOperateMerchantByIds(ids));
    }

    /**
     * 商户账户列表
     */
    @GetMapping("/getMchAccount")
    @PreAuthorize("@ss.hasPermi('operate:merchant:list')")
    public AjaxResult getMchAccount(MchAccountDTO mchAccountDTO){
        startPage();
        List<MchAccountDTO> dataList = operateMerchantService.queryMchAccountList(mchAccountDTO);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("rows", dataList);
        ajax.put("total", new PageInfo(dataList).getTotal());
        BigDecimal todayBlanceTotal = dataList.stream().map(MchAccountDTO::getTodayBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("todayBlanceTotal", todayBlanceTotal);
        BigDecimal blanceTotal = dataList.stream().map(MchAccountDTO::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ajax.put("blanceTotal", blanceTotal);

        return success(ajax);
    }

    /**
     * userId获取商户数据
     */
    @GetMapping("/getMchData")
    public AjaxResult getMchData(@RequestParam("userId") String userId){
        OperateMerchant operateMerchant = operateMerchantService.queryOperateMerchantByUserId(Long.valueOf(userId));
        //查询代理商
        AjaxResult ajax = AjaxResult.success();
        ajax.put(AjaxResult.DATA_TAG, operateMerchant);
        return ajax;
    }

    /**
     * 通过userId改商户key
     */
    @GetMapping("/updateMchData")
    public AjaxResult updateMchData(@RequestParam Map<String, Object> params){
        String key = (String) params.get("key");
        String userId = (String) params.get("userId");
        //商户
        OperateMerchant operateMerchant = operateMerchantService.queryOperateMerchantByUserId(Long.valueOf(userId));
        operateMerchant.setMchKey(key);
        operateMerchant.setUpdateBy(getUsername());
        operateMerchantService.updateOperateMerchant(operateMerchant);
        //用户
        SysUser sysUser = userService.selectUserById(Long.valueOf(userId));
        sysUser.setCreateBy(getUsername());
        sysUser.setUserKey(key);
        sysUser.setLoginSet("0");
        sysUser.setIsGoogle("0");

        return toAjax(userService.updateUser(sysUser));
    }


    /**
     * 商户对账--发起一键对账
     */
    @Log(title = "一键对账", businessType = BusinessType.OTHER)
    @GetMapping("/reconciliation")
    public AjaxResult reconciliation(@RequestParam Map<String, Object> params)
    {
        String mchList = (String) params.get("mchList");
        if(StringUtils.isNotEmpty(mchList)){

            String[] payProIdList = mchList.split(",");
            List<Long> selectmchList = Arrays.stream(payProIdList).map(Long::parseLong)
                    .collect(Collectors.toList());

            // 获取默认时区的当前时间
            LocalDateTime now = LocalDateTime.now();
            // 设置为北京时区
            ZoneId beijingZoneId = ZoneId.of("Asia/Shanghai");
            // 获取北京当前时间精确到秒
            ZonedDateTime beijingTime = now.atZone(beijingZoneId);
            // 格式化时间为字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //今天
            String beijingTimeTodayString = beijingTime.format(formatter);

            //飞机发送对账信息
            for(Long mchId : selectmchList){
                OperateMerchant operateMerchant = operateMerchantService.selectOperateMerchantById(mchId);

                if(operateMerchant.getTgmGroup() != null){
                    OperateBillDTO operateBillDTO = new OperateBillDTO();
                    operateBillDTO.setMchId(operateMerchant.getId());
                    operateBillDTO.setCheckDate(beijingTimeTodayString);
                    List<OperateBillDTO> dataList = operateOrderService.queryMchBill(operateBillDTO);
                    //发送的消息
                    String message = "商户对账日期：" + beijingTimeTodayString + "\n" +
                            "商户名称：" + operateMerchant.getMchName() + "\n" +
                            "交易量：" + dataList.get(0).getBillTotalAmount() + "\n" +
                            "入账：" + dataList.get(0).getIncomeAmount() + "\n" +
                            "下发：" + dataList.get(0).getExpendAmount() + "\n" +
                            "差额：" + dataList.get(0).getBalance() + "\n" ;
                    //发送
                    execBot.sendMessageToGroup(operateMerchant.getTgmGroup().toString(),message);
                }

            }



        }else{

            return error("没有筛选商户 对账失败！");
        }

        return toAjax(1);
    }
}
