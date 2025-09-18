package com.fit2cloud.itsm.common.constants;

import com.fit2cloud.commons.server.constants.ResourceOperation;

import java.util.Objects;

public class BKCompanyApiType {

    public enum BK_COMPANY_API_TYPE {
        CMDB_CREATE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.CREATE, "新增(自定义模型)"),
        CMDB_UPDATE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.UPDATE, "更新(自定义模型)"),
        CMDB_DELETE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.DELETE, "删除(自定义模型)"),

        CMDB_QUERY(ACCOUNT_TYPE.CMDB.name, ResourceOperation.QUERY, "查询(自定义模型)"),

        CMDB_CREATE_HOST(ACCOUNT_TYPE.CMDB.name, ResourceOperation.CREATE_HOST, "新增(主机模型）"),

        CMDB_UPDATE_HOST(ACCOUNT_TYPE.CMDB.name, ResourceOperation.UPDATE_HOST, "更新(主机模型)"),

        CMDB_DELETE_HOST(ACCOUNT_TYPE.CMDB.name, ResourceOperation.DELETE_HOST, "删除(主机模型）"),

        CMDB_QUERY_HOST(ACCOUNT_TYPE.CMDB.name, ResourceOperation.QUERY_HOST, "查询(主机模型）"),
        //宿主机推送
        CMDB_HOST_QUERY(ACCOUNT_TYPE.CMDB.name, "", "查询宿主机"),
        CMDB_SYNC_HOST(ACCOUNT_TYPE.CMDB.name, "", "宿主机同步"),;

        BK_COMPANY_API_TYPE(String accountType, String apiType, String name) {
            this.accountType = accountType;
            this.apiType = apiType;
            this.name = name;
        }

        private final String accountType;
        private final String apiType;
        private final String name;

        public String getName() {
            return name;
        }

        public String getAccountType() {
            return accountType;
        }

        public String getApiType() {
            return apiType;
        }

        public static String getNameByType(String typeName) {
            String result = "";
            for (BK_COMPANY_API_TYPE type : BK_COMPANY_API_TYPE.values()) {
                if (Objects.equals(type.name(), typeName)) {
                    result = type.getName();
                }
            }
            return result;
        }

        public static String getApiTypeNameByApiType(String apiType) {
            String result = "";
            for (BKCompanyApiType.BK_COMPANY_API_TYPE type : BKCompanyApiType.BK_COMPANY_API_TYPE.values()) {
                if (Objects.equals(type.apiType, apiType)) {
                    result = type.name();
                }
            }
            return result;
        }
    }

    public enum ACCOUNT_TYPE {
        CMDB("CMDB推送"), NOTICE("代办通知"), PROCESS("流程对接"), ORG("组织架构同步"), ;

        ACCOUNT_TYPE(String name) {
            this.name = name;
        }

        private final String name;

        public String getName() {
            return name;
        }

        public static String getNameByType(String typeName) {
            String result = "";
            for (ACCOUNT_TYPE type : ACCOUNT_TYPE.values()) {
                if (Objects.equals(type.name(), typeName)) {
                    result = type.getName();
                }
            }
            return result;
        }
    }

    public enum ACCOUNT_STATUS {
        UN_VALIDATE("待验证", "0"), CAN_USE("可用","1"), CAN_NOT_USE("不可用", "2");

        private final String name;
        private final String value;

        ACCOUNT_STATUS(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    public enum CMDB {
        GET_VM("查询虚拟机"),INSERT_VM("新增虚拟机"), UPDATE_VM("更新虚拟机"), DELETE_VM("删除虚拟机");

        CMDB(String name) {
            this.name = name;
        }

        private final String name;

        public String getName() {
            return name;
        }

    }

    public enum PROCESS {
        CREATE_PROCESS("创建流程"), COMPLETE_PROCESS("流程完成"), CREATE_TASK("待办通知"), COMPLETE_TASK("已办通知");

        PROCESS(String name) {
            this.name = name;
        }

        private final String name;

        public String getName() {
            return name;
        }
    }

    public enum ORG {
        GET_ORG("获取组织"), GET_WORKSPACE("获取工作空间");

        ORG(String name) {
            this.name = name;
        }

        private final String name;

        public String getName() {
            return name;
        }
    }

    public enum USER {
        GET_USER("获取用户");

        USER(String name) {
            this.name = name;
        }

        private final String name;

        public String getName() {
            return name;
        }
    }

    public enum API_LOG_CODE {
        SUCESS(0, "成功"),
        ERROR(1, "异常"),
        EXCUTION(2, "执行中"),;

        API_LOG_CODE(Integer code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }

        private final Integer code;
        private final String displayName;

        public Integer getCode() {
            return code;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ENABLE_STATUS {
        ENABLE("1", "启用"),
        DISABLE("0", "禁用");

        ENABLE_STATUS(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }

        private final String code;
        private final String displayName;

        public String getCode() {
            return code;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

}
