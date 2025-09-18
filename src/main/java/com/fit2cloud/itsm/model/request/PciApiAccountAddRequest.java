package com.fit2cloud.itsm.model.request;

public class PciApiAccountAddRequest {
    private String id;

    private String name;

    private String providerFactoryId;

    private String systemType;

    private String version;

    private String apiEndpoint;

    private String testApiEndpoint;

    private String credential;

    private String defaultApiList;

    private String customContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getProviderFactoryId() {
        return providerFactoryId;
    }

    public void setProviderFactoryId(String providerFactoryId) {
        this.providerFactoryId = providerFactoryId;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getTestApiEndpoint() {
        return testApiEndpoint;
    }

    public void setTestApiEndpoint(String testApiEndpoint) {
        this.testApiEndpoint = testApiEndpoint;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getDefaultApiList() {
        return defaultApiList;
    }

    public void setDefaultApiList(String defaultApiList) {
        this.defaultApiList = defaultApiList;
    }

    public String getCustomContent() {
        return customContent;
    }

    public void setCustomContent(String customContent) {
        this.customContent = customContent;
    }
}
