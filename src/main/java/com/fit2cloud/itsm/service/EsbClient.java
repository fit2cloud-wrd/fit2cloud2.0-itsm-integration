package com.fit2cloud.itsm.service;

import com.fit2cloud.commons.server.base.domain.FlowTask;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;

import java.util.List;

public interface EsbClient {

    /**
     * 发送待办到第三方系统
     * @param pendingTaskList
     */
    void sendPendingTasks(List<FlowTask> pendingTaskList);

    /**
     * 将第三方系统对应的待办置为已办
     * @param completedTaskList
     */
    void completeTasks(List<FlowTask> completedTaskList);

    /**
     * 将第三方系统对应的已办置为办结
     * @param businessEventContextDTO
     */
    void businessFinished(BusinessEventContextDTO businessEventContextDTO);

}
