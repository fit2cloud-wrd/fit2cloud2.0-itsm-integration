package com.fit2cloud.itsm.model.loadbalancer;


import lombok.Data;

import java.io.Serializable;

@Data
public class LbLtmPool implements Serializable {
    private String id;
    private String name;
    private String part;
    private String status;
    private String fullPath;
    private String monitor;
    private String loadBalancingMode;
    private String accountId;
    private Long syncTime;
    private Long deletedTime;
}
