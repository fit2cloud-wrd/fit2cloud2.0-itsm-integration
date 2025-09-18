package com.fit2cloud.itsm.service.impl;

import com.fit2cloud.commons.server.base.domain.FlowProcess;
import com.fit2cloud.commons.server.base.domain.FlowTask;
import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.commons.server.service.UserCommonService;
import com.fit2cloud.commons.server.utils.ServletUtils;
import com.fit2cloud.commons.utils.LogUtil;
import com.fit2cloud.itsm.service.ITSMService;
import com.fit2cloud.itsm.integration.IntegrationManager;
import com.fit2cloud.itsm.common.constants.OAIntegrationType;
import com.fit2cloud.itsm.common.constants.WikiConstants;
import com.fit2cloud.itsm.common.utils.ConvertUtil;
import com.fit2cloud.itsm.model.PciProcess;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

import static com.fit2cloud.commons.server.utils.ServletUtils.getRequest;

@Service("itsmService")
public class ITSMServiceImp implements ITSMService {

    @Lazy
    @Resource
    IntegrationManager integrationManager;
    @Resource
    private UserCommonService userCommonService;
    @Resource
    private PciProcessService pciProcessService;
    @Resource
    private InternalProcessService internalProcessService;

    /**
     * 将外部性系统流程单号缓存至 cookie
     */
    @Override
    public void stashExternalProcessId() {
        if (getRequest().getParameterMap().containsKey(integrationManager.getEXTERNAL_PROCESS_ID_PARAM_KEY())) {
            String processId = getExternalRequestProcessId();
            if (StringUtils.isNotBlank(processId)) {
                ServletUtils.addCookie(integrationManager.getExternalProcessIdCookieKey(), processId);
            } else {
                LogUtil.warn(String.format("没有在请求中找到 %s ，如果不需要在跳转连接中获取第三方系统流程单号，可忽略此问题。参考地址：%s", integrationManager.getEXTERNAL_PROCESS_ID_PARAM_KEY(), WikiConstants.EXTERNAL_PROCESS_GUIDE));
            }
        } else {
            // TODO 写好 WIKI 后贴地址
            LogUtil.debug(String.format("没有在请求中找到 %s ，如果不需要在跳转连接中获取第三方系统流程单号，可忽略此问题。参考地址：%s", integrationManager.getEXTERNAL_PROCESS_ID_PARAM_KEY(), WikiConstants.EXTERNAL_PROCESS_GUIDE));
        }
    }

    /**
     * 获取外部系统流程单号ID
     * @return
     */
    @Override
    public String getExternalProcessId() {
        return ServletUtils.getCookie(integrationManager.getExternalProcessIdCookieKey());
    }

    /**
     * 获取跳转链接的第一个页面，默认跳转根地址
     * 可通过在 request 中传递参数来决定跳转到哪个页面
     * @return
     */
    @Override
    public String getFirstPageUrl() {
        // ServletUtils.getRequest();
        return "redirect:/";
    }

    @Override
    public boolean bindingCmpProcess(BusinessEventContextDTO BusinessEventContextDTO) {
        return false;
    }

    @Override
    public void onCmpProcessSubmit(BusinessEventContextDTO businessEventContextDTO) {
        integrationManager = IntegrationManager.getManager().get();
        if (integrationManager.getManagerOaIntegrationType() == OAIntegrationType.ExternalOrderReadyBeforeSubmit) {
            String externalProcessId = getExternalProcessId();
//            externalProcessId = "test1";
            if (StringUtils.isBlank(externalProcessId)) {
                throw new IllegalStateException("Cannot find external process id in cookie!");
            }
            PciProcess pciProcess = ConvertUtil.convert(externalProcessId, businessEventContextDTO);
            integrationManager.getITSMProvider().putOneItsm(externalProcessId, businessEventContextDTO);
            pciProcessService.newPciProcess(pciProcess);
        } else if (integrationManager.getManagerOaIntegrationType() == OAIntegrationType.CreateExternalOrderWhenSubmit) {
            LogUtil.info("--------------------------- 》》》CreateExternalOrderWhenSubmit 》》》---------------------------");

            LogUtil.info("===「 来自 ITSM 订单 」===");
            String externalProcessId = integrationManager.getITSMProvider().takeOneItsm(businessEventContextDTO);
            LogUtil.info("= 1.创建ITSM流程单：" + externalProcessId);
            PciProcess pciProcess = buildPciProcess(externalProcessId, businessEventContextDTO);
            LogUtil.info("= 2.绑定：ITSM流程单<" + externalProcessId + "> === <" + pciProcess.getBusinessKey() + ">云管资源订单");
            pciProcessService.newPciProcess(pciProcess);
        } else {
            if (Objects.isNull(integrationManager.getManagerOaIntegrationType())) {
                throw new IllegalStateException("请配置 OA 对接类型，integrationManager.oaIntegrationType");
            } else {
                throw new IllegalStateException("无法处理的对接类型 " + integrationManager.getManagerOaIntegrationType());
            }
        }
    }

    @Override
    public void onCmpProcessComplete(BusinessEventContextDTO businessEventContextDTO) {
        String externalProcessId = pciProcessService.getExternalProcessIdByProcessId(businessEventContextDTO.getBusinessKey());
        integrationManager.getITSMProvider().makeOneItsm(externalProcessId, businessEventContextDTO);
    }

    @Override
    public void onCmpBusinessComplete(BusinessEventContextDTO businessEventContextDTO) {
        String externalProcessId = pciProcessService.getExternalProcessIdByProcessId(businessEventContextDTO.getProcessEventContext().getProcessId());
        integrationManager.getITSMProvider().makeOneItsm(externalProcessId, businessEventContextDTO);
    }

    @Override
    public void onOAProcessComplete(String externalProcessId, String remark, String assign) {
        String processId = pciProcessService.getProcessId(externalProcessId);
        internalProcessService.complete(processId, Optional.ofNullable(remark).orElse("通过 OA API 审批"), assign);
    }

    @Override
    public void onOAProcessReject(String externalProcessId, String remark, String assign) {
        String processId = pciProcessService.getProcessId(externalProcessId);
        internalProcessService.reject(processId, Optional.ofNullable(remark).orElse("通过 OA API 驳回"), assign);
    }

    private String getExternalRequestProcessId(){
        HttpServletRequest request = ServletUtils.getRequest();
        return request.getParameter(integrationManager.getEXTERNAL_PROCESS_ID_PARAM_KEY());
    }

    public static PciProcess buildPciProcess(String externalProcessId, BusinessEventContextDTO businessEventContext) {
        PciProcess pciProcess = new PciProcess();
        pciProcess.setExternalProcessId(externalProcessId);
        pciProcess.setResourceType(businessEventContext.getResourceType().name());

        FlowTask task = null;
        FlowProcess process = null;
        if (Objects.nonNull(businessEventContext.getProcessEventContext().getTask())) {
            task = businessEventContext.getProcessEventContext().getTask();
        } else if (CollectionUtils.isNotEmpty(businessEventContext.getProcessEventContext().getTasks())) {
            task = businessEventContext.getProcessEventContext().getTasks().get(0);
        } else if (Objects.nonNull(businessEventContext.getProcessEventContext().getProcess())) {
            process = businessEventContext.getProcessEventContext().getProcess();
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
