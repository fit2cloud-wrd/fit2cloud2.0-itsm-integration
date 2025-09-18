package com.fit2cloud.itsm.model.loadbalancer;

import lombok.Data;

@Data
public class Persist {
    private String name;
    private String partition;
    private String tmDefault;
    private Reference nameReference;
}
