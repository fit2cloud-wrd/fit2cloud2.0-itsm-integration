package com.fit2cloud.itsm.model.loadbalancer;

import lombok.Data;

import java.io.Serializable;
@Data
public class LbLtmMember implements Serializable {
    private String id;
    private String name;
    private String part;
    private String status;
    private String fullPath;
    private String address;
    private String poolId;
    private String monitor;
    private String session;
    private String state;
    private String port;
    private String accountId;
    private String fqdnName;
    private String fqdnAutopopulate;
    private Long syncTime;
    private Long deletedTime;
    private Integer ratio;
}