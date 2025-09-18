package com.fit2cloud.itsm.model.loadbalancer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Profiles extends ModuleDto  {

    @JsonProperty("items")
    private List<ItemsDTO> items;

    @Data
    public static class ItemsDTO {
        @JsonProperty("kind")
        private String kind;
        @JsonProperty("name")
        private String name;
        @JsonProperty("partition")
        private String partition;
        @JsonProperty("fullPath")
        private String fullPath;
        @JsonProperty("generation")
        private Integer generation;
        @JsonProperty("selfLink")
        private String selfLink;
        @JsonProperty("context")
        private String context;
    }
}
