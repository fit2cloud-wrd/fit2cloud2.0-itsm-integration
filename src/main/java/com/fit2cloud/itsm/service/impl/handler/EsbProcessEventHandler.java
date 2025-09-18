package com.fit2cloud.itsm.service.impl.handler;

import com.fit2cloud.commons.server.base.domain.FlowTask;
import com.fit2cloud.commons.server.constants.ProcessEventArgumentsConstants;
import com.fit2cloud.commons.server.process.TaskService;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.itsm.integration.IntegrationManager;
import com.fit2cloud.itsm.service.IProcessEventHandler;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.fit2cloud.commons.server.constants.ProcessEventArgumentsConstants.valueOf;

@Component
public class EsbProcessEventHandler implements IProcessEventHandler {

    @Lazy
    @Resource
    private IntegrationManager integrationManager;
    @Resource
    private TaskService taskService;

    @Override
    public void onProcessEvent(BusinessEventContextDTO businessEventContextDTO) {
        ProcessEventArgumentsConstants arg = valueOf(businessEventContextDTO.getProcessEventContext().getArguments());
        switch (arg) {
            case PENDING:
            case APPROVED:
            case REJECTED:
            case CANCEL:
                handleTasks(businessEventContextDTO);
                // 此处可调用 流程平台 接口将所有用户的待办置为已办
                // 此处可调用 流程平台 接口发送待办消息，或发送更新数据
                break;
            case BUSINESS_FINISHED:
                if (esbClientReady()) {
                    integrationManager.getEsbClient().businessFinished(businessEventContextDTO);
                }
                break;
            case SUBMIT:
            case COMPLETE:
            case BUSINESS_WARN:
            case BUSINESS_ERROR:
            default:
                throw new IllegalStateException("请修改事件配置，ESB 对接不支持此事件参数: " + arg);
        }
    }

    private void handleTasks(BusinessEventContextDTO businessEventContextDTO){
        if (! esbClientReady()) {
            return;
        }
        List<FlowTask> taskList;
        if (Objects.nonNull(businessEventContextDTO.getProcessEventContext().getTask())) {
            taskList = taskService.getSameActivityTask(businessEventContextDTO.getProcessEventContext().getTask());
            taskList.add(businessEventContextDTO.getProcessEventContext().getTask());
            integrationManager.getEsbClient().completeTasks(taskList);
        } else if (Objects.nonNull(businessEventContextDTO.getProcessEventContext().getTasks())) {
            taskList = businessEventContextDTO.getProcessEventContext().getTasks();
            integrationManager.getEsbClient().sendPendingTasks(taskList);
        } else if (Objects.nonNull(businessEventContextDTO.getProcessEventContext().getProcess())){
            taskList = taskService.listCurrentTask(businessEventContextDTO.getProcessEventContext().getProcessId());
            integrationManager.getEsbClient().completeTasks(taskList);
        }else {
            LogUtil.debug("task 和 taskList 均为空，不做处理！");
        }
    }

    private boolean esbClientReady (){
        if (Objects.isNull(integrationManager.getEsbClient())) {
            LogUtil.debug("Esb Client not Ready!");
            return false;
        }
        return true;
    }
}
