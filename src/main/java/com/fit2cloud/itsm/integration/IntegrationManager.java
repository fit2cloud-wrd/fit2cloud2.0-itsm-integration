package com.fit2cloud.itsm.integration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fit2cloud.itsm.service.EsbClient;
import com.fit2cloud.itsm.service.impl.handler.EsbProcessEventHandler;
import com.fit2cloud.itsm.service.IProcessEventHandler;
import com.fit2cloud.itsm.service.impl.handler.OAProcessEventHandler;
import com.fit2cloud.itsm.service.impl.ITSMServiceImp;
import com.fit2cloud.itsm.service.ITSMService;
import com.fit2cloud.itsm.common.constants.OAIntegrationType;
import com.fit2cloud.itsm.common.utils.ProviderFactoryUtil;
import com.fit2cloud.itsm.common.utils.SpringContextUtils;
import com.fit2cloud.itsm.model.dto.APICredentialItem;
import com.fit2cloud.itsm.model.dto.PciApiAccountDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IntegrationManager {

    public static ThreadLocal<IntegrationManager> manager = new ThreadLocal<>();
    private PciApiAccountDTO pciApiAccountDTO;
    private EsbClient esbClient;
    private OAIntegrationType oaIntegrationType;
    private String USER_PARAM_KEY = "userId";
    private String EXTERNAL_PROCESS_ID_PARAM_KEY = "processId";

    public ITSMService getITSMService() {
        return SpringContextUtils.getBean("itsmService", ITSMServiceImp.class);
    }

    public IProcessEventHandler getProcessEventHandler() {
        List<APICredentialItem> credentialItemList = getCredentialTemplate();
        String itsmType = "";
        String integrationType = "";
        for (APICredentialItem item : credentialItemList) {
            if (Objects.equals(item.getName(), "itsmType")) {
                itsmType = item.getDefaultValue();
            }
            if (Objects.equals(item.getName(), "integrationType")) {
                integrationType = item.getDefaultValue();
            }
        }

        if (Objects.equals(integrationType, "CreateExternalOrderWhenSubmit")) {
            manager.get().setOaIntegrationType(OAIntegrationType.CreateExternalOrderWhenSubmit);
        } else {
            manager.get().setOaIntegrationType(OAIntegrationType.ExternalOrderReadyBeforeSubmit);
        }

        if (StringUtils.equals(itsmType, "OA")) {
            return new OAProcessEventHandler();
        } else if (StringUtils.equals(itsmType, "ESB")) {
            return new EsbProcessEventHandler();
        }

        return null;
    }

    public List<APICredentialItem> getCredentialTemplate() {
        List<APICredentialItem> credentialItems = new ArrayList<>();

        // 获取默认启用的API账号
        ThreadLocal threadLocal = new ThreadLocal();
        PciApiAccountDTO pciApiAccountDTO = manager.get().getPciApiAccountDTO();

        if (pciApiAccountDTO != null) {
            String credential = pciApiAccountDTO.getCredential();
            JSONArray jsonArray = JSONArray.parseArray(credential);

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                APICredentialItem item = new APICredentialItem();
                item.setName(jsonObject.getString("name"));
                item.setLabel(jsonObject.getString("label"));
                item.setDefaultValue(jsonObject.getString("defaultValue"));
                credentialItems.add(item);
            }
        }

        return credentialItems;
    }

    public ITSMIntegration getITSMProvider() {
        // 根据 账号类型 获取当前启用的流程API账号
        Map<String, ITSMIntegration> providerMap = ProviderFactoryUtil.getProviderMap();

        // 获取默认启用的API账号
//        ThreadLocal threadLocal = new ThreadLocal();
        PciApiAccountDTO pciApiAccountDTO = manager.get().getPciApiAccountDTO();

        // 要执行的厂商
        ITSMIntegration providerFactory = providerMap.get(pciApiAccountDTO.getProviderFactoryId());

        // 要执行厂商启用版本的Provider实现类
        ITSMIntegration provider = (ITSMIntegration) providerFactory.getProvider(ITSMIntegration.class, pciApiAccountDTO.getVersion());
        return provider;
    }

    public EsbClient getEsbClient() {
        return esbClient;
    }

    public void setEsbClient(EsbClient esbClient) {
        this.esbClient = esbClient;
    }

    public OAIntegrationType getOaIntegrationType() {
        return this.oaIntegrationType;
    }

    public OAIntegrationType getManagerOaIntegrationType() {
        return manager.get().getOaIntegrationType();
    }

    public void setOaIntegrationType(OAIntegrationType oaIntegrationType) {
        this.oaIntegrationType = oaIntegrationType;
    }

    public String getUSER_PARAM_KEY() {
        return USER_PARAM_KEY;
    }

    public void setUSER_PARAM_KEY(String USER_PARAM_KEY) {
        this.USER_PARAM_KEY = USER_PARAM_KEY;
    }

    public String getEXTERNAL_PROCESS_ID_PARAM_KEY() {
        return EXTERNAL_PROCESS_ID_PARAM_KEY;
    }

    public void setEXTERNAL_PROCESS_ID_PARAM_KEY(String EXTERNAL_PROCESS_ID_PARAM_KEY) {
        this.EXTERNAL_PROCESS_ID_PARAM_KEY = EXTERNAL_PROCESS_ID_PARAM_KEY;
    }

    public String getExternalProcessIdCookieKey() {
        return "EXTERNAL_PROCESS_ID";
    }

    public static ThreadLocal<IntegrationManager> getManager() {
        return manager;
    }

    public static void setManager(ThreadLocal<IntegrationManager> manager) {
        IntegrationManager.manager = manager;
    }

    public PciApiAccountDTO getPciApiAccountDTO() {
        return pciApiAccountDTO;
    }

    public void setPciApiAccountDTO(PciApiAccountDTO pciApiAccountDTO) {
        this.pciApiAccountDTO = pciApiAccountDTO;
    }


}
