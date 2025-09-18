package com.fit2cloud.itsm.service.impl;

import com.fit2cloud.itsm.dao.PciProcessMapper;
import com.fit2cloud.itsm.model.PciProcess;
import com.fit2cloud.itsm.model.PciProcessExample;
import com.fit2cloud.itsm.model.request.PciProcessRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PciProcessService {
    @Resource
    private PciProcessMapper pciProcessMapper;

    public List<PciProcess> list(PciProcessRequest pciProcessRequest){
        PciProcessExample example = new PciProcessExample();
        PciProcessExample.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(pciProcessRequest.getBusinessKey())) {
            criteria.andBusinessKeyEqualTo(pciProcessRequest.getBusinessKey());
        }
        if (StringUtils.isNotBlank(pciProcessRequest.getExternalProcessId())) {
            criteria.andExternalProcessIdEqualTo(pciProcessRequest.getExternalProcessId());
        }
        if (StringUtils.isNotBlank(pciProcessRequest.getModule())) {
            criteria.andModuleEqualTo(pciProcessRequest.getModule());
        }
        if (StringUtils.isNotBlank(pciProcessRequest.getProcessId())) {
            criteria.andProcessIdEqualTo(pciProcessRequest.getProcessId());
        }
        if (StringUtils.isNotBlank(pciProcessRequest.getResourceType())) {
            criteria.andResourceTypeEqualTo(pciProcessRequest.getResourceType());
        }
        if (StringUtils.isNotBlank(pciProcessRequest.getSort())) {
            example.setOrderByClause(pciProcessRequest.getSort());
        }
        return pciProcessMapper.selectByExample(example);
    }

    public String getProcessId (String externalProcessId){
        PciProcessExample example = new PciProcessExample();
        example.createCriteria().andExternalProcessIdEqualTo(externalProcessId);

        List<PciProcess> pciProcesses = pciProcessMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(pciProcesses)) {
            throw new IllegalStateException("无法找到相关流程！externalProcessId: " + externalProcessId);
        }
        if (pciProcesses.size() > 1) {
            throw new IllegalStateException(String.format("找到[%s]条数据，请保证一个 externalProcessId 只有一条云管流程！", pciProcesses.size()));
        }
        return pciProcesses.get(0).getProcessId();
    }

    public String getExternalProcessIdByProcessId(String processId){
        PciProcessExample example = new PciProcessExample();
        example.createCriteria().andProcessIdEqualTo(processId);

        List<PciProcess> pciProcesses = pciProcessMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(pciProcesses)) {
            throw new IllegalStateException("无法找到相关流程！processId: " + processId);
        }
        if (pciProcesses.size() > 1) {
            throw new IllegalStateException(String.format("找到[%s]条数据，请保证一个 processId 只有与一个外部流程 ID （externalProcessId）对应！", pciProcesses.size()));
        }
        return pciProcesses.get(0).getExternalProcessId();
    }

    public String getExternalProcessIdByBusinessKey (String businessKey){
        PciProcessExample example = new PciProcessExample();
        example.createCriteria().andBusinessKeyEqualTo(businessKey);

        List<PciProcess> pciProcesses = pciProcessMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(pciProcesses)) {
            throw new IllegalStateException("无法找到相关流程！businessKey: " + businessKey);
        }
        if (pciProcesses.size() > 1) {
            throw new IllegalStateException(String.format("找到[%s]条数据，请保证一个 businessKey 只有与一个外部流程 ID （externalProcessId）对应！", pciProcesses.size()));
        }
        return pciProcesses.get(0).getExternalProcessId();
    }

    public void newPciProcess (PciProcess pciProcess){
        pciProcessMapper.insert(pciProcess);
    }
}
