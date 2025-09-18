package com.fit2cloud.itsm.model.pm;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PhysicalMachine implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String hostname;
    private String machineType;
    private Integer cpu;
    private String cpuType;
    private Integer core;
    private Integer thread;
    private Integer memory;
    private String memoryType;
    private String diskType;
    private Integer disk;
    private String managementIp;
    private String bmcMac;
    private String ipArray;
    private String osType;
    private String osVersion;
    private String osDetail;
    private String machineBrand;
    private String machineModel;
    private String serverId;
    private String machineSn;
    private Long maintenanceTimestamp;
    private String status;
    private String power;
    private String workspaceId;
    private Long recycledTime;
    private String sshUser;
    private String sshPwd;
    private Integer sshPort;
    private Long lastUpdated;
    private Long expiresTime;
    private String orderItemId;
    private String instanceUuid;
    private String providerId;
    private String ruleId;
    private String cpuFre;
    private String remark;
    private Byte autoDeploy;
    private String applyUser;
    private String owner;
    private String assetId;
    private String machineRoom;
    private String machineRack;
    private String uNumber;
    private Byte needInstallOs;
    private Integer syncSuccess;
    private String purpose;
    private String envSign;
    private Integer optimisticLockVersion;
    private String upperStatus;
    private String classify;
    private String maintainEndTime;
    private String dataCenter;
    private String isManage;
    private String resourcePoolId;
    private String projectName;
    private String ipType;
    private BigDecimal price;
    private String equipmentStatus;
    private String extendInfo;

}
