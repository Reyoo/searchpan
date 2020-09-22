package com.libbytian.pan.system.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemUserModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liugh123
 * @since 2018-05-03
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemUserService extends IService<SystemUserModel> {

//    /**
//     * 根据用户名查询用户
//     * @param username 用户名
//     * @return 用户
//     */
    SystemUserModel getUserByUserName(String username) ;
//
//    User getUserByMobile(String mobile);

    /**
     * 注册用户
     * @param user
     * @return
     */
    SystemUserModel register(SystemUserModel user ) throws Exception;


    /**
     * 查询用户是否存在
     * @param username
     * @return
     */
    int selectByName(String username) throws Exception;

//    Map<String, Object> getLoginUserAndMenuInfo(User user);
//
//    void deleteByUserNo(String userNo)throws Exception;
//
//    Page<User> selectPageByConditionUser(Page<User> userPage, String info, Integer[] status, String startTime, String endTime);
//
//    Map<String,Object> checkMobileAndPasswd(JSONObject requestJson)throws Exception;
//
//    Map<String,Object> checkMobileAndCatcha(JSONObject requestJson)throws Exception;
//
//    User checkAndRegisterUser(JSONObject requestJson)throws Exception;
//
//    User updateForgetPasswd(JSONObject requestJson)throws Exception;
//
//    void resetMobile(User currentUser, JSONObject requestJson)throws Exception;
//
//    void resetPassWord(User currentUser, JSONObject requestJson)throws Exception;
//
//    User insertUserByAdmin(JSONObject requestJson)throws Exception;
}
