package com.fit2cloud.itsm.model.fusionaccess;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@Data
public class DesktopServer implements Serializable {
    private String id;
    private String vmId;
    private String sid;
    private String computerName;
    private String description;
    private Integer vCpu;
    private Integer memory;
    private Integer systemDisk;
    private String systemvolumeId;
    private String resourceGroupName;
    private String resGroupType;
    private String vmType;
    private String instanceState;
    private String useState;
    private String attachState;
    private String attachType;
    private String dgName;
    private String siteId;
    private String siteName;
    private String clusterId;
    private String clusterName;
    private String templateId;
    private String templateType;
    private String serviceType;
    private Long createTime;
    private Long lastSyncTimestamp;
    private Long detachTime;
    private String domain;
    private String regionId;
    private String logUser;
    private String ip;
    private Integer isMacIpBind;
    private String ipMod;
    private String createCtatus;
    private String agentVersion;
    private String osType;
    private String desktopType;
    private String farmId;
    private Integer platformKind;
    private String dgType;
    private String appGroupName;
    private String platformName;
    private String vmCreator;
    private String vipVm;
    private String sessionCount;
    private String sk;
    private String osPlatform;
    private String gpuType;
    private String domianName;
    private Boolean isHpet;
    private String accountId;
    private String workspaceId;
    private String instanceType;
    private String instanceTypeDescription;

}