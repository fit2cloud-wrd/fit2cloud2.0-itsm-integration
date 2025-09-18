package com.fit2cloud.itsm.model.bkcmdb;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ApiEndpointCustomContent {
    private String objId;
    private String objectAssociationId;
    private boolean syncHostAssociation;

}
