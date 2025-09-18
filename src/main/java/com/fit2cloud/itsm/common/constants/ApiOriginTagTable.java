package com.fit2cloud.itsm.common.constants;

import com.fit2cloud.commons.server.base.domain.CloudServer;
import com.fit2cloud.commons.server.base.domain.Workspace;
import org.apache.poi.ss.formula.functions.T;

import java.util.Objects;

public class ApiOriginTagTable {
    
    public enum API_ORIGIN_TAG {
        single_tag("单级标签"),
        multiple_tag("多级标签"),
        ;

        API_ORIGIN_TAG(String tagTypeName) {
            this.tagTypeName = tagTypeName;
        }

        private final String tagTypeName;

        public String getTagTypeName() {
            return tagTypeName;
        }

        public static String getTagTypeByName(String name) {
            String result = "";
            for (API_ORIGIN_TAG tag : API_ORIGIN_TAG.values()) {
                if (Objects.equals(tag.name(), name)) {
                    result = tag.tagTypeName;
                }
            }
            return result;
        }
    }

}
