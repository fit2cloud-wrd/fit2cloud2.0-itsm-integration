package com.fit2cloud.itsm.sample;

import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.itsm.service.OAClient;

public class OAClientSample implements OAClient {
    @Override
    public void submitProcess(String externalProcessId, BusinessEventContextDTO businessEventContextDTO) {
        // 调用外部接口发送流程详情
    }

    @Override
    public String createProcess(BusinessEventContextDTO businessEventContextDTO) {
        // 调用外部接口创建流程，返回外部系统流程 ID
        return null;
    }

    @Override
    public void completeProcess(String externalProcessId, BusinessEventContextDTO businessEventContextDTO) {
        // 调用外部系统反馈执行结果
    }
}
