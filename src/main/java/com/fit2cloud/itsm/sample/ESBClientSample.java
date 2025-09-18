package com.fit2cloud.itsm.sample;

import com.fit2cloud.commons.server.base.domain.FlowTask;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.itsm.service.EsbClient;

import java.util.List;

public class ESBClientSample implements EsbClient {

    @Override
    public void sendPendingTasks(List<FlowTask> pendingTaskList) {
        for (FlowTask task : pendingTaskList) {
            doSendPendingTask(task);
        }
    }

    @Override
    public void completeTasks(List<FlowTask> completedTaskList) {
        for (FlowTask task : completedTaskList) {
            doCompleteTask(task);
        }
    }

    @Override
    public void businessFinished(BusinessEventContextDTO businessEventContextDTO) {
        // 这里可以调用 ESB 平台通知流程对应任务已执行结束
    }

    private void doSendPendingTask(FlowTask flowTask) {
        System.out.printf("====== Sending pending task for user: %s%n", flowTask.getTaskAssignee());
    }

    private void doCompleteTask(FlowTask flowTask) {
        System.out.printf("====== Sending completed task for user: %s%n", flowTask.getTaskAssignee());
    }
}
