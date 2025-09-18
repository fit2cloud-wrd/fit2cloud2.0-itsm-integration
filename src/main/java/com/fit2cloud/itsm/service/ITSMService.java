package com.fit2cloud.itsm.service;

import com.fit2cloud.commons.server.base.domain.User;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;

import java.util.List;

/**
 * 流程对接接口
 */
public interface ITSMService {

    /**
     * 缓存请求中的 OA 工单 ID
     * 适用场景：OA 中先建工单然后跳转 CMP
     */
    void stashExternalProcessId();

    /**
     * 获取缓存的 OA 工单 ID
     * 适用场景：OA 中先建工单然后跳转 CMP
     */
    String getExternalProcessId();

    /**
     * 获取跳转的页面
     * 1、可以固定跳转至首页
     * 2、也可以根据 request 中的参数来实现不同请求跳转至不同页面
     */
    String getFirstPageUrl();

    /**
     * CMP 绑定CMP资源订单和ITSM流程
     */
    boolean bindingCmpProcess(BusinessEventContextDTO BusinessEventContextDTO);

    /**
     * CMP 流程提交时调用
     */
    void onCmpProcessSubmit(BusinessEventContextDTO BusinessEventContextDTO);

    /**
     * CMP 流程结束时调用
     */
    void onCmpProcessComplete(BusinessEventContextDTO BusinessEventContextDTO);

    /**
     * CMP 流程对应业务结束时调用
     */
    void onCmpBusinessComplete(BusinessEventContextDTO BusinessEventContextDTO);

    /**
     * 第三方系统审批完成时调用，默认审批对应CMP流程
     */
    void onOAProcessComplete(String externalProcessId, String remark, String assign);

    /**
     * 第三方系统审批驳回时调用，默认驳回对应CMP流程
     */
    void onOAProcessReject(String externalProcessId, String remark, String assign);

}
