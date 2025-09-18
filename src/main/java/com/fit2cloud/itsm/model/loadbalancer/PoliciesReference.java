package com.fit2cloud.itsm.model.loadbalancer;

import lombok.Data;

@Data
@SuppressWarnings("WeakerAccess")
public class PoliciesReference {
    private String link;
    private Boolean isSubcollection;
}
