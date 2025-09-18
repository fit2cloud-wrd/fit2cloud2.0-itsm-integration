package com.fit2cloud.itsm.model.request;

import com.fit2cloud.itsm.model.PciApiParameterMappingDictionary;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class PciApiParameterMappingRequest {
    private String id;

    private String apiId;

    private String originField;

    private String originFieldName;

    private String originFieldType;

    private String targetField;

    private String targetFieldType;

    private String description;

    private String originFieldSource;

    private String originFieldTable;

    private String fieldTag;

    private List<PciApiParameterMappingDictionary> mappingDictionaryList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getOriginField() {
        return originField;
    }

    public void setOriginField(String originField) {
        this.originField = originField;
    }

    public String getOriginFieldType() {
        return originFieldType;
    }

    public void setOriginFieldType(String originFieldType) {
        this.originFieldType = originFieldType;
    }

    public String getTargetField() {
        return targetField;
    }

    public void setTargetField(String targetField) {
        this.targetField = targetField;
    }

    public String getTargetFieldType() {
        return targetFieldType;
    }

    public void setTargetFieldType(String targetFieldType) {
        this.targetFieldType = targetFieldType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginFieldSource() {
        return originFieldSource;
    }

    public void setOriginFieldSource(String originFieldSource) {
        this.originFieldSource = originFieldSource;
    }

    public String getOriginFieldTable() {
        return originFieldTable;
    }

    public void setOriginFieldTable(String originFieldTable) {
        this.originFieldTable = originFieldTable;
    }

    public List<PciApiParameterMappingDictionary> getMappingDictionaryList() {
        return mappingDictionaryList;
    }

    public void setMappingDictionaryList(List<PciApiParameterMappingDictionary> mappingDictionaryList) {
        this.mappingDictionaryList = mappingDictionaryList;
    }

    public String getOriginFieldName() {
        return originFieldName;
    }

    public void setOriginFieldName(String originFieldName) {
        this.originFieldName = originFieldName;
    }

    public String getFieldTag() {
        return fieldTag;
    }

    public void setFieldTag(String fieldTag) {
        this.fieldTag = fieldTag;
    }
}