package com.fit2cloud.itsm.service;

import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;

public interface OAClient {
    /**
     * 通过调用 OA 接口给已有工单发送流程对应任务的详细描述，如 一个 JSON 字符串或一个 PDF 文件等
     */
    void submitProcess(String externalProcessId, BusinessEventContextDTO businessEventContextDTO);

    /**
     * 通过调用 OA 接口创建工单
     */
    String createProcess(BusinessEventContextDTO businessEventContextDTO);

    /**
     * 通过调用 OA 接口给已有工单发送流程对应任务的执行结果，此方法在流程执行结束后被调用
     */
    void completeProcess(String externalProcessId, BusinessEventContextDTO businessEventContextDTO);

}
