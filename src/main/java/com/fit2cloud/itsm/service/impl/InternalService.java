package com.fit2cloud.itsm.service.impl;

import com.fit2cloud.commons.server.model.SessionUser;
import com.fit2cloud.commons.server.service.MicroService;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.server.utils.HttpHeaderUtils;
import com.fit2cloud.commons.server.utils.SessionUtils;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.commons.utils.ResultHolder;

import javax.annotation.Resource;

public abstract class InternalService {

    @Resource
    protected MicroService microService;

    @Resource
    protected UserCommonService userCommonService;

    protected ResultHolder invoke(String service, String url, String jsonParam){
        //用于后台定时调用该方法时，用户未登录的情况
        SessionUser user = SessionUtils.getUser();
        if (user == null) {
            LogUtil.debug("[定时任务调用]：模拟用户登陆状态");
            HttpHeaderUtils.runAsUser(userCommonService.getUserById("admin"));
        }
        ResultHolder resultHolder = microService.postForResultHolder(service, url, jsonParam);
        if (resultHolder.isSuccess()) {
            return resultHolder;
        } else {
            throw new RuntimeException("调用远程服务失败！msg：" + resultHolder.getMessage());
        }
    }
}
