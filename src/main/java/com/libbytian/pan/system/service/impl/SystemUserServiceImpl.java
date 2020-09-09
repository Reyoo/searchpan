package com.libbytian.pan.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemUserMapper;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToRole;
import com.libbytian.pan.system.service.ISystemUserService;
import com.libbytian.pan.system.service.ISystemUserToRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liugh123
 * @since 2018-05-03
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemUserServiceImpl extends ServiceImpl<SystemUserMapper, SystemUserModel> implements ISystemUserService {


    private final ISystemUserToRoleService userToRoleService;

    private final SystemUserMapper systemUserMapper ;


    @Override
    public SystemUserModel getUserByUserName(String username) {
        return systemUserMapper.selectUserByUsername(username);
    }

    /**
     * 设置所有通过注册的用户均为普通用户，用户权限变更需要在管理端进行配置
     * @param user
     * @return
     */

    @Override
    public SystemUserModel register(SystemUserModel user) {
        user.setCreateTime(LocalDateTime.now());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(user.getPassword());
        user.setPassword(encode);
        boolean result = this.save(user);
        if (result) {
            SystemUserToRole userToRole  = SystemUserToRole.builder().userId(user.getUserId()).roleId("ROLE_NORMAL").build();
            userToRoleService.save(userToRole);
        }
        return user;
    }
//
//    @Override
//    public Map<String, Object> getLoginUserAndMenuInfo(User user) {
//        Map<String, Object> result = new HashMap<>();
//        UserToRole userToRole = userToRoleService.selectByUserNo(user.getUserNo());
//        user.setToken(JWTUtil.sign(user.getUserNo(), user.getPassword()));
//        result.put("user",user);
//        List<Menu> buttonList = new ArrayList<Menu>();
//        //根据角色主键查询启用的菜单权限
//        List<Menu> menuList = menuService.findMenuByRoleCode(userToRole.getRoleCode());
//        List<Menu> retMenuList = menuService.treeMenuList(Constant.ROOT_MENU, menuList);
//        for (Menu buttonMenu : menuList) {
//            if(buttonMenu.getMenuType() == Constant.TYPE_BUTTON){
//                buttonList.add(buttonMenu);
//            }
//        }
//        result.put("menuList",retMenuList);
//        result.put("buttonList",buttonList);
//        return result;
//    }
//
//    @Override
//    public void deleteByUserNo(String userNo) throws Exception{
//        User user = this.selectById(userNo);
//        if (ComUtil.isEmpty(user)) {
//            throw new BusinessException(CodeEnum.INVALID_USER.getMsg(),CodeEnum.INVALID_USER.getCode());
//        }
//        EntityWrapper<UserToRole> ew = new EntityWrapper<>();
//        ew.eq("user_no", userNo);
//        userToRoleService.delete(ew);
//        this.deleteById(userNo);
//    }
//
//    @Override
//    public Page<User> selectPageByConditionUser(Page<User> userPage, String info, Integer[] status, String startTime, String endTime) {
//        //注意！！ 分页 total 是经过插件自动 回写 到传入 page 对象
//        return userPage.setRecords(mapper.selectPageByConditionUser(userPage, info,status,startTime,endTime));
//    }
//
//    @Override
//    public Map<String, Object> checkMobileAndPasswd(JSONObject requestJson) throws Exception{
//        //由于 @ValidationParam注解已经验证过mobile和passWord参数，所以可以直接get使用没毛病。
//        String identity = requestJson.getString("identity");
//        InfoToUser infoToUser = infoToUserService.selectOne(new EntityWrapper<InfoToUser>().eq("identity_info ", identity));
//        if(ComUtil.isEmpty(infoToUser)){
//            throw new BusinessException(CodeEnum.INVALID_USER.getMsg(),CodeEnum.INVALID_USER.getCode());
//        }
//        User user = this.selectOne(new EntityWrapper<User>().where("user_no = {0} and status = 1",infoToUser.getUserNo()));
//        if (ComUtil.isEmpty(user) || !BCrypt.checkpw(requestJson.getString("password"), user.getPassword())) {
//            throw new BusinessException(CodeEnum.INVALID_USERNAME_PASSWORD.getMsg(),CodeEnum.INVALID_USERNAME_PASSWORD.getCode());
//        }
//        //测试websocket用户登录给管理员发送消息的例子  前端代码参考父目录下WebSocketDemo.html
////        noticeService.insertByThemeNo("themeNo-cwr3fsxf233edasdfcf2s3","13888888888");
////        MyWebSocketService.sendMessageTo(JSONObject.toJSONString(user),"13888888888");
//        return this.getLoginUserAndMenuInfo(user);
//    }
//
//
//
//    @Override
//    public Map<String, Object> checkMobileAndCatcha(JSONObject requestJson) throws Exception {
//        String mobile = requestJson.getString("mobile");
//        if(!StringUtil.checkMobileNumber(mobile)){
//            throw new BusinessException(CodeEnum.MOBILE_ERROR.getMsg(),CodeEnum.MOBILE_ERROR.getCode());
//        }
//        User user = this.getUserByMobile(mobile);
//        //如果不是启用的状态
//        if(!ComUtil.isEmpty(user) && user.getStatus() != Constant.ENABLE){
//            throw new BusinessException("该用户状态不是启用的!");
//        }
//        List<SmsVerify> smsVerifies = smsVerifyService.getByMobileAndCaptchaAndType(mobile,
//                requestJson.getString("captcha"), SmsSendUtil.SMSType.getType(SmsSendUtil.SMSType.AUTH.name()));
//        if(ComUtil.isEmpty(smsVerifies)){
//            throw new BusinessException(CodeEnum.VERIFY_PARAM_ERROR.getMsg(),CodeEnum.VERIFY_PARAM_ERROR.getCode());
//        }
//        if(SmsSendUtil.isCaptchaPassTime(smsVerifies.get(0).getCreateTime())){
//            throw new BusinessException(CodeEnum.VERIFY_PARAM_PASS.getMsg(),CodeEnum.VERIFY_PARAM_PASS.getCode());
//        }
//        if (ComUtil.isEmpty(user)) {
//            //设置默认密码
//            User userRegister = User.builder().password(BCrypt.hashpw("123456", BCrypt.gensalt()))
//                    .mobile(mobile).username(mobile).build();
//            user =this.register(userRegister, Constant.RoleType.USER);
//        }
//        return this.getLoginUserAndMenuInfo(user);
//    }
//
//    @Override
//    public User checkAndRegisterUser(JSONObject requestJson) throws Exception {
//        //可直接转为java对象,简化操作,不用再set一个个属性
//        User userRegister = requestJson.toJavaObject(User.class);
//        if(!StringUtil.checkMobileNumber(userRegister.getMobile())){
//            throw new BusinessException(CodeEnum.MOBILE_ERROR.getMsg(),CodeEnum.MOBILE_ERROR.getCode());
//        }
//        if (!userRegister.getPassword().equals(requestJson.getString("rePassword"))) {
//            throw new BusinessException(CodeEnum.INVALID_RE_PASSWORD.getMsg(),CodeEnum.INVALID_RE_PASSWORD.getCode());
//        }
//        List<SmsVerify> smsVerifies = smsVerifyService.getByMobileAndCaptchaAndType(userRegister.getMobile(),
//                requestJson.getString("captcha"), SmsSendUtil.SMSType.getType(SmsSendUtil.SMSType.REG.name()));
//        if(ComUtil.isEmpty(smsVerifies)){
//            throw new BusinessException(CodeEnum.VERIFY_PARAM_ERROR.getMsg(),CodeEnum.VERIFY_PARAM_ERROR.getCode());
//        }
//        //验证码是否过期
//        if(SmsSendUtil.isCaptchaPassTime(smsVerifies.get(0).getCreateTime())){
//            throw new BusinessException(CodeEnum.VERIFY_PARAM_PASS.getMsg(),CodeEnum.VERIFY_PARAM_PASS.getCode());
//        }
//        userRegister.setPassword(BCrypt.hashpw(requestJson.getString("password"), BCrypt.gensalt()));
//        User registerUser = this.register(userRegister, Constant.RoleType.USER);
//        infoToUserService.insert(InfoToUser.builder().userNo(registerUser.getUserNo())
//                .identityInfo(userRegister.getMobile()).identityType(Constant.LOGIN_MOBILE).build());
//        //默认注册普通用户
//        return registerUser;
//    }
//
//    @Override
//    public User updateForgetPasswd(JSONObject requestJson) throws Exception {
//        String mobile = requestJson.getString("mobile");
//        if(!StringUtil.checkMobileNumber(mobile)){
//            throw new BusinessException(CodeEnum.MOBILE_ERROR.getMsg(),CodeEnum.MOBILE_ERROR.getCode());
//        }
//        if (!requestJson.getString("password").equals(requestJson.getString("rePassword"))) {
//            throw new BusinessException(CodeEnum.INVALID_RE_PASSWORD.getMsg(),CodeEnum.INVALID_RE_PASSWORD.getCode());
//        }
//        User user = this.getUserByMobile(mobile);
//        roleService.getRoleIsAdminByUserNo(user.getUserNo());
//        if(ComUtil.isEmpty(user)){
//            throw new BusinessException(CodeEnum.INVALID_USER.getMsg(),CodeEnum.INVALID_USER.getCode());
//        }
//        List<SmsVerify> smsVerifies = smsVerifyService.getByMobileAndCaptchaAndType(mobile,
//                requestJson.getString("captcha"), SmsSendUtil.SMSType.getType(SmsSendUtil.SMSType.FINDPASSWORD.name()));
//        if(ComUtil.isEmpty(smsVerifies)){
//            throw new BusinessException(CodeEnum.VERIFY_PARAM_ERROR.getMsg(),CodeEnum.VERIFY_PARAM_ERROR.getCode());
//        }
//        if(SmsSendUtil.isCaptchaPassTime(smsVerifies.get(0).getCreateTime())){
//            throw new BusinessException(CodeEnum.VERIFY_PARAM_PASS.getMsg(),CodeEnum.VERIFY_PARAM_PASS.getCode());
//        }
//        user.setPassword(BCrypt.hashpw(requestJson.getString("password"),BCrypt.gensalt()));
//        this.updateById(user);
//        return user;
//    }
//
//    @Override
//    public void resetMobile(User currentUser, JSONObject requestJson) throws Exception {
//        String newMobile = requestJson.getString("newMobile");
//        if(!StringUtil.checkMobileNumber(newMobile)){
//          throw  new BusinessException(CodeEnum.MOBILE_ERROR.getMsg(),CodeEnum.MOBILE_ERROR.getCode());
//        }
//        List<SmsVerify> smsVerifies = smsVerifyService.getByMobileAndCaptchaAndType(newMobile,
//                requestJson.getString("captcha"), SmsSendUtil.SMSType.getType(SmsSendUtil.SMSType.MODIFYINFO.name()));
//        if(ComUtil.isEmpty(smsVerifies)){
//            throw  new BusinessException(CodeEnum.VERIFY_PARAM_ERROR.getMsg(),CodeEnum.VERIFY_PARAM_ERROR.getCode());
//        }
//        if(SmsSendUtil.isCaptchaPassTime(smsVerifies.get(0).getCreateTime())){
//            throw  new BusinessException(CodeEnum.VERIFY_PARAM_PASS.getMsg(),CodeEnum.VERIFY_PARAM_PASS.getCode());
//        }
//        currentUser.setMobile(newMobile);
//        this.updateById(currentUser);
//    }
//
//    @Override
//    public void resetPassWord(User currentUser, JSONObject requestJson) throws Exception {
//        if (!requestJson.getString("password").equals(requestJson.getString("rePassword"))) {
//            throw  new BusinessException(CodeEnum.INVALID_RE_PASSWORD.getMsg(),CodeEnum.INVALID_RE_PASSWORD.getCode());
//        }
//        if(!BCrypt.checkpw(requestJson.getString("oldPassword"),currentUser.getPassword())){
//            throw  new BusinessException(CodeEnum.INVALID_USERNAME_PASSWORD.getMsg(),CodeEnum.INVALID_USERNAME_PASSWORD.getCode());
//        }
//        currentUser.setPassword(BCrypt.hashpw(requestJson.getString("password"),BCrypt.gensalt()));
//        this.updateById(currentUser);
//    }
//
//    @Override
//    public User insertUserByAdmin(JSONObject requestJson) throws Exception {
//        User user = requestJson.toJavaObject(User.class);
//        if(!ComUtil.isEmpty(this.selectOne(new EntityWrapper<User>().eq("user_name", user.getUsername())))){
//            throw new BusinessException(CodeEnum.INVALID_USER_EXIST.getMsg(),CodeEnum.INVALID_USER_EXIST.getCode());
//        }
//        Role role = roleService.selectOne(
//                new EntityWrapper<Role>().eq("role_name", requestJson.getString("roleName")));
//        if(ComUtil.isEmpty(role)){
//            throw new BusinessException(CodeEnum.INVALID_ROLE.getMsg(),CodeEnum.INVALID_ROLE.getCode());
//        }
//        String userNo = GenerationSequenceUtil.generateUUID("user");
//        if(!ComUtil.isEmpty(user.getMobile())){
//            if(!StringUtil.checkMobileNumber(user.getMobile())){
//                throw new BusinessException(CodeEnum.MOBILE_ERROR.getMsg(),CodeEnum.MOBILE_ERROR.getCode());
//            }
//            infoToUserService.insert(InfoToUser.builder().identityInfo(user.getMobile())
//                    .identityType(Constant.LOGIN_MOBILE).userNo(userNo)
//                    .identityInfo(user.getMobile()).build());
//        }
//        if(!ComUtil.isEmpty(user.getEmail())){
//            if(!StringUtil.checkEmail(user.getEmail())){
//                throw new BusinessException(CodeEnum.EMAIL_ERROR.getMsg());
//            }
//            infoToUserService.insert(InfoToUser.builder().userNo(userNo)
//                    .identityInfo(user.getEmail()).identityType(Constant.LOGIN_EMAIL).build());
//        }
//        user.setPassword(BCrypt.hashpw("123456", BCrypt.gensalt()));
//        user.setUserNo(userNo);
//        user.setCreateTime(System.currentTimeMillis());
//        user.setStatus(Constant.ENABLE);
//        this.insert(user);
//        infoToUserService.insert(InfoToUser.builder().userNo(userNo)
//                .identityInfo(user.getUsername()).identityType(Constant.LOGIN_USERNAME).build());
//        UserToRole userToRole  = UserToRole.builder().userNo(user.getUserNo()).roleCode(role.getRoleCode()).build();
//        userToRoleService.insert(userToRole);
//        return user;
//    }


}
