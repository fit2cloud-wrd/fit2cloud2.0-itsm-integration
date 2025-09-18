package com.fit2cloud.itsm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.utils.ResultHolder;
import com.fit2cloud.itsm.common.constants.InternalConstants;
import org.springframework.stereotype.Service;

/**
 * 此类实现调用管理中心接口维护用户数据
 */
@Service
public class InternalUserService extends InternalService {

    /**
     * 创建用户
     * */
    public User createUser(String jsonParam){
        ResultHolder resultHolder = invoke(InternalConstants.MC_SERVICE_ID, InternalConstants.CREATE_USER_URL, jsonParam);
        return JSONObject.parseObject(JSONObject.toJSONString(resultHolder.getData()), User.class);
    }

    /**
     * 更新用户
     * */
    public void updateUser(String jsonParam){
        invoke(InternalConstants.MC_SERVICE_ID, InternalConstants.UPDATE_USER_URL, jsonParam);
    }
}
