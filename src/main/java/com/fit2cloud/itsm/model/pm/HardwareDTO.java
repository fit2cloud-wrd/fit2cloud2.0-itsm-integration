package com.fit2cloud.itsm.model.pm;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data

public class HardwareDTO {
    private Integer fansNumber;
    private List<String> macAddress;
    private Integer cpuCores;
    private Double cpuGhz;
    private String machineName;
    private String machineType;
    private String machineModle;
    private String machineSerialNumber;
    private Integer powerNumber;
    private Integer powerWatt;
    private Map<String, Integer> memoryInfo;
    private List<String> immInfo;
    private List<Double> diskSize;
}
