package com.fit2cloud.itsm.common.utils;

import com.fit2cloud.commons.server.base.domain.FlowProcess;
import com.fit2cloud.commons.server.base.domain.FlowTask;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.itsm.model.PciProcess;
import org.apache.commons.collections.CollectionUtils;

import java.util.Objects;

public class ConvertUtil {

    public static PciProcess convert(String externalProcessId,BusinessEventContextDTO BusinessEventContextDTO) {
        PciProcess pciProcess = new PciProcess();
        pciProcess.setExternalProcessId(externalProcessId);
        pciProcess.setResourceType(BusinessEventContextDTO.getResourceType().name());

        FlowTask task = null;
        FlowProcess process = null;
        if (Objects.nonNull(BusinessEventContextDTO.getProcessEventContext().getTask())) {
            task = BusinessEventContextDTO.getProcessEventContext().getTask();
        } else if (CollectionUtils.isNotEmpty(BusinessEventContextDTO.getProcessEventContext().getTasks())) {
            task = BusinessEventContextDTO.getProcessEventContext().getTasks().get(0);
        } else if (Objects.nonNull(BusinessEventContextDTO.getProcessEventContext().getProcess())) {
            process = BusinessEventContextDTO.getProcessEventContext().getProcess();
        }
        if (Objects.nonNull(task)) {
            pciProcess.setProcessId(task.getProcessId());
            pciProcess.setModule(task.getModule());
            pciProcess.setBusinessKey(task.getBusinessKey());
        } else if (Objects.nonNull(process)) {
            pciProcess.setProcessId(process.getProcessId());
            pciProcess.setModule(process.getModule());
            pciProcess.setBusinessKey(process.getBusinessKey());
        } else {
            throw new IllegalArgumentException("task and process cannot be null at the same time");
        }
        return pciProcess;
    }
}
