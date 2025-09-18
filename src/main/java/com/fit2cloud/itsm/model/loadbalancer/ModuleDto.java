package com.fit2cloud.itsm.model.loadbalancer;

import lombok.Data;

@Data
public abstract class ModuleDto {
    private String kind;
    private String name;
    private String partition;
    private String fullPath;
    private Integer generation;
    private String selfLink;
}
