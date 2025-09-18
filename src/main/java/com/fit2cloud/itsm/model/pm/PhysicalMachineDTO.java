package com.fit2cloud.itsm.model.pm;

import lombok.Data;

import java.util.Map;

@Data

public class PhysicalMachineDTO extends PhysicalMachine {
    private String providerName;
    private String outbandStatus;
    private HardwareDTO hardwareEntity;
    /**
     * key 数量，value 大小
     */
    private Map<Integer, Integer> memoryInfo;
    private Map<Double, Integer> diskInfo;

    private String organizationId;
    private String workspaceName;
    private String organizationName;
    private String resourcePoolName;
    private String userName;
    private String applyUserName;

}
