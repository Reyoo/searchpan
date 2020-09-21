package com.libbytian.pan.system.aop;



import com.libbytian.pan.system.aop.constant.RequestLimitConstant;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.util.PanHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * @author  sun7127
 * @describtion : 限制频繁调用
 */

@Slf4j
@Aspect
@Component
public class RequestLimitContract {


    @Autowired
    private RedisTemplate redisTemplate;


    @Around("@annotation(limit)")
    @Transactional(rollbackFor = Exception.class)
    public Object requestLimit(ProceedingJoinPoint process, RequestLimit limit)  throws Exception{

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime = System.currentTimeMillis();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        MethodSignature methodSignature = (MethodSignature) process.getSignature();
        Method method = methodSignature.getMethod();
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
        // 根据 IP + API 限流
        String requestURI = request.getRequestURI();
        String requestIp = request.getRemoteHost();
        String className = method.getDeclaringClass().getName();


        Object[] args = process.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof HttpServletRequest) {
                request = (HttpServletRequest) args[i];
                break;
            }
        }

        if (request == null) {
            throw new Exception("未知IP");
        }

//        log.info("请求时间:{}, clientIp:{}, 请求方法:{}, 请求参数{}", simpleDateFormat.format(startTime), "", methodName);
        Object result = null;
        try {
            //  查询ip是否被锁定 -是 直接返回 -否 继续流程
            String ipAddress = PanHttpUtil.getIpAddress(request);
            //判断黑名单
            Boolean ipIsEmpty = redisTemplate.boundHashOps(RequestLimitConstant.BLACKLIST).hasKey(ipAddress);
            if(!ipIsEmpty){
                //判断白名单
                Boolean whiteIpIsEmpty = redisTemplate.boundHashOps(RequestLimitConstant.WHITELIST).hasKey(ipAddress);
                //如果白名单也为空 则保存 否则则修改次数放行，如果次数大于3 则停用
                if(!whiteIpIsEmpty){
                    //同时更新value 次数为1;
                    //调用执行目标方法
                    result = process.proceed();
                    RequestLimitModel initrequestLimitModel = new RequestLimitModel();
                    initrequestLimitModel.setIpAddress(ipAddress);
                    initrequestLimitModel.setTimes(0);
                    HashOperations hashOps = redisTemplate.opsForHash();

                    hashOps.putIfAbsent(RequestLimitConstant.WHITELIST, ipAddress, initrequestLimitModel);
                    redisTemplate.boundValueOps(RequestLimitConstant.WHITELIST).expire(limit.frameTime(),TimeUnit.SECONDS);
                    System.out.println("----------------------------------------------");
                    System.out.println(redisTemplate.boundHashOps(RequestLimitConstant.WHITELIST).getExpire());
                    System.out.println("----------------------------------------------");
                    return result;
                }else{
                    RequestLimitModel hasDatarequestLimitModel = (RequestLimitModel) redisTemplate.boundHashOps(RequestLimitConstant.WHITELIST).get(ipAddress);
                    System.out.println(redisTemplate.boundHashOps(RequestLimitConstant.WHITELIST).getExpire().intValue());
                    if(hasDatarequestLimitModel.getTimes()<limit.count()&&redisTemplate.boundHashOps(RequestLimitConstant.WHITELIST).getExpire().intValue()>0 && redisTemplate.boundHashOps(RequestLimitConstant.WHITELIST).getExpire().intValue() < limit.frameTime()){
                        result = process.proceed();
                        hasDatarequestLimitModel.setTimes(hasDatarequestLimitModel.getTimes()+1);
                        redisTemplate.boundHashOps(RequestLimitConstant.WHITELIST).put(ipAddress, hasDatarequestLimitModel);
                        redisTemplate.boundValueOps(RequestLimitConstant.WHITELIST).expire(limit.frameTime(),TimeUnit.SECONDS);
                        System.out.println("=========================");
                        System.out.println(redisTemplate.boundHashOps(RequestLimitConstant.WHITELIST).getExpire().intValue());
                        System.out.println("执行这一步");
                    } else {
                        System.out.println(redisTemplate.boundHashOps(RequestLimitConstant.BLACKLIST).getExpire());
//                       //放入黑名单
                        redisTemplate.boundHashOps(RequestLimitConstant.BLACKLIST).put(ipAddress, hasDatarequestLimitModel);
                        redisTemplate.boundValueOps(RequestLimitConstant.BLACKLIST).expire(limit.lockTime(),TimeUnit.SECONDS);
                        //删除白名单
                        redisTemplate.boundHashOps(RequestLimitConstant.WHITELIST).delete(ipAddress);
                        return AjaxResult.error("操作过于频繁,已经放入小黑屋，封锁一个小时");
                    }
                }
            }else {
                //如果在黑名单 则判断是否满足过期时间，如果已满足过期时间 则放行
                int deadtime = redisTemplate.boundHashOps(RequestLimitConstant.BLACKLIST).getExpire().intValue();
                System.out.println("================" + redisTemplate.boundHashOps(RequestLimitConstant.BLACKLIST).getExpire());
                if(deadtime< 0){
                    redisTemplate.boundHashOps(RequestLimitConstant.BLACKLIST).delete(ipAddress);
                    redisTemplate.boundHashOps(RequestLimitConstant.WHITELIST).delete(ipAddress);
                    result = process.proceed();
                    return result;
                }

                return AjaxResult.error("操作过于频繁,已经放入小黑屋，封锁一个小时");
            }


        } catch (Throwable throwable) {
            throwable.printStackTrace();
            String exception = throwable.getClass() + ":" + throwable.getMessage();
            long costTime = System.currentTimeMillis() - startTime;
            log.error("请求时间:{}, 请求耗时:{}, 请求类名:{}, 请求方法:{}, 请求参数:{}, 请求结果:{}", startTime, costTime, className, methodName, "", exception);
            return AjaxResult.error("服务器异常:",exception) ;
        }
//        long costTime = System.currentTimeMillis() - startTime;
//        log.info("请求时间:{}, 请求耗时:{}, 请求类名:{}, 请求方法：{}, 请求参数:{}, 请求结果：{}", simpleDateFormat.format(startTime), costTime, className, methodName, "", new Gson().toJson(result));
        return result;
    }

}