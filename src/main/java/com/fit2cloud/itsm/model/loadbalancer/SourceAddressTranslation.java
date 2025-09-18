package com.fit2cloud.itsm.model.loadbalancer;

import lombok.Data;

@Data
public class SourceAddressTranslation {
    private String type;
    private String pool;
    private Reference poolReference;
}
