package com.fit2cloud.itsm.service.impl;

import com.fit2cloud.commons.server.base.domain.FlowTask;
import com.fit2cloud.commons.server.process.TaskService;
import com.fit2cloud.commons.server.process.request.ApiCompleteTaskRequest;
import com.fit2cloud.commons.server.process.request.ApiRejectTaskRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class InternalProcessService {

    @Resource
    private TaskService taskService;

    public void complete(String processId, String remark, String assign) {
        ApiCompleteTaskRequest request = new ApiCompleteTaskRequest();
        request.setProcessId(processId);
        request.setRemark(remark);
        request.setAssign(assign);

        FlowTask flowTask = taskService.getFirstPendingTask(request.getProcessId());
        if (!taskService.dispatcher(flowTask, "/flow/runtime/process/complete", request)) {
            flowTask.setTaskRemarks(request.getRemark());
            taskService.completeByApi(flowTask, request.getAssign(), request.getRemark());
        }
    }

    public void reject(String processId, String remark, String assign) {
        ApiRejectTaskRequest request = new ApiRejectTaskRequest();
        request.setProcessId(processId);
        request.setRemark(remark);
        request.setAssign(assign);

        FlowTask flowTask = taskService.getFirstPendingTask(request.getProcessId());
        if (!taskService.dispatcher(flowTask, "/flow/runtime/process/reject", request)) {
            flowTask.setTaskRemarks(request.getRemark());
            taskService.rejectByApi(flowTask, request.getAssign(), request.getRemark());
        }
    }
}
