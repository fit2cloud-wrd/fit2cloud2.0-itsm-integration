package com.fit2cloud.itsm.model.loadbalancer;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VirtualProfile extends ModuleDto {
    private String context;

}
