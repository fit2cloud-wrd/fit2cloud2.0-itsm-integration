package com.fit2cloud.itsm.integration;

import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.itsm.model.dto.APICredentialItem;
import com.fit2cloud.itsm.model.dto.PciApiEndpointDTO;

import java.util.List;

public interface ITSMIntegration {

    String getId();

    String getName();

    /** 版本号 */
    String getVersion();

    /** 支持版本号 */
    List<String> getSupportVersion();

    /** 默认API地址 */
    String getDefaultAPIEndpoint();

    /** 验证默认API地址 */
    String testDefaultAPIEndpoint();

    /** 默认API列表 */
    List<PciApiEndpointDTO> getDefaultApiList();

    /** 验证默认API列表 */
    String testDefaultApiUrl();

    /** 默认API列表Json */
    String getDefaultApiListJson();

    /** 认证信息填写模板 */
    List<APICredentialItem> getCredentialTemplate();

    /** 认证信息填写模板Json */
    String getCreditialJson();

    <T extends ITSMIntegration> T getProvider(Class<T> clazz, String version);

    /** 调用OA接口，创建一个新的工单 */
    String takeOneItsm(BusinessEventContextDTO businessEventContextDTO);

    /** 调用OA接口，给已有工单，推送（补充）流程对应任务的详细内容（描述）。如 一个 JSON 字符串或一个 PDF 文件等 */
    void putOneItsm(String externalProcessId, BusinessEventContextDTO businessEventContextDTO);

    /** 调用OA接口，给已有工单，发送流程对应（关联）任务的执行结果。此方法在流程执行结束后被调用 */
    void makeOneItsm(String externalProcessId, BusinessEventContextDTO businessEventContextDTO);

}
