package com.fit2cloud.itsm.integration;

import com.fit2cloud.commons.server.process.dto.BusinessEventContextDTO;
import com.fit2cloud.itsm.common.constants.ApiSupportVersion;
import com.fit2cloud.itsm.common.constants.ApiType;
import com.fit2cloud.itsm.common.utils.ReadConfigFileUtil;
import com.fit2cloud.itsm.common.utils.SpringContextUtils;
import com.fit2cloud.itsm.model.dto.APICredentialItem;
import com.fit2cloud.itsm.model.dto.PciApiAccountDTO;
import com.fit2cloud.itsm.model.dto.PciApiEndpointDTO;
import com.fit2cloud.itsm.service.impl.ApiAccountService;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Component
public class CloudwiseIntegration implements ITSMIntegration {

    @Resource
    private ApiAccountService apiAccountService;



    @Override
    public String getId() {
        return "CLOUDWISE";
    }

    @Override
    public String getName() {
        return "云智慧";
    }

    @Override
    public String getVersion() {
        return  ApiSupportVersion.CLOUDWISE.V2.getVersion();
    }

    @Override
    public List<String> getSupportVersion() {
        List<String> versionList = new ArrayList<>();
        for (ApiSupportVersion.CLOUDWISE value : ApiSupportVersion.CLOUDWISE.values()) {
            versionList.add(value.getVersion());
        }
        return versionList;
    }

    @Override
    public String getDefaultAPIEndpoint() {
        // 获取默认启用的API账号
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.PROCESS.name());
        if (pciApiAccountDTO != null) {
            return pciApiAccountDTO.getApiEndpoint();
        }
        return null;
    }

    @Override
    public String testDefaultAPIEndpoint() {
        // 获取默认启用的API账号
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.PROCESS.name());
        if (pciApiAccountDTO != null) {
            return pciApiAccountDTO.getTestApiEndpoint();
        }
        return null;
    }

    @Override
    public List<PciApiEndpointDTO> getDefaultApiList() {
        // 获取默认启用的API账号
        PciApiAccountDTO pciApiAccountDTO = apiAccountService.getDefaultApiAccount(ApiType.ACCOUNT_TYPE.PROCESS.name());
        if (pciApiAccountDTO != null) {
            return apiAccountService.getApiListByAccountId(pciApiAccountDTO.getId());
        }
        return null;
    }

    @Override
    public String testDefaultApiUrl() {
        return "/cmdb/model/list";
    }

    @Override
    public String getDefaultApiListJson() {
        return ReadConfigFileUtil.readConfigFile("CloudwiseDefaultApiList.json");
    }

    @Override
    public List<APICredentialItem> getCredentialTemplate() {
        return null;
    }

    @Override
    public String getCreditialJson() {
        return ReadConfigFileUtil.readConfigFile("CloudwiseCredential.json");
    }

    @Override
    public <T extends ITSMIntegration> T getProvider(Class<T> clazz, String version) {

        try {
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .forPackages(ITSMIntegration.class.getPackage().getName())
                    .addScanners(new SubTypesScanner()));

            Set<Class<? extends ITSMIntegration>> implClass = reflections.getSubTypesOf(ITSMIntegration.class);
            for (Class<? extends ITSMIntegration> subClass : implClass) {
                Class<?> cls = Class.forName(subClass.getName());

                Method getVersionMethod = subClass.getDeclaredMethod("getVersion");
                String clsVersion = (String) getVersionMethod.invoke(cls.newInstance());
                if (StringUtils.equals(clsVersion, version)) {
                    return (T) SpringContextUtils.getBean(subClass);
                }
            }
        } catch (Exception exception) {
        }

        return null;
    }

    @Override
    public void putOneItsm(String externalProcessId, BusinessEventContextDTO businessEventContextDTO) {

    }

    @Override
    public String takeOneItsm(BusinessEventContextDTO businessEventContextDTO) {
        return null;
    }

    @Override
    public void makeOneItsm(String externalProcessId, BusinessEventContextDTO businessEventContextDTO) {

    }


}
