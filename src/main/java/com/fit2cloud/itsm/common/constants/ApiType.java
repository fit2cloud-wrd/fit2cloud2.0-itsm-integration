package com.fit2cloud.itsm.common.constants;

import com.fit2cloud.commons.server.constants.ResourceOperation;

import java.util.Objects;

public class ApiType {

    public enum API_TYPE {
        CMDB_CREATE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.CREATE, "新增虚拟机"),
        CMDB_UPDATE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.UPDATE, "更新虚拟机"),
        CMDB_DELETE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.DELETE, "删除虚拟机"),
        CMDB_QUERY(ACCOUNT_TYPE.CMDB.name, ResourceOperation.QUERY, "查询虚拟机"),

        CMDB_CREATE_HOST(ACCOUNT_TYPE.CMDB.name, ResourceOperation.CREATE_HOST, "新增虚拟机(主机模型）"),
        CMDB_UPDATE_HOST(ACCOUNT_TYPE.CMDB.name, ResourceOperation.UPDATE_HOST, "更新虚拟机(主机模型)"),
        CMDB_DELETE_HOST(ACCOUNT_TYPE.CMDB.name, ResourceOperation.DELETE_HOST, "删除虚拟机(主机模型）"),
        CMDB_QUERY_HOST(ACCOUNT_TYPE.CMDB.name, ResourceOperation.QUERY_HOST, "查询虚拟机(主机模型）"),

        CMDB_F5_CREATE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.CREATE, "新增F5虚拟服务器"),
        CMDB_F5_UPDATE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.UPDATE, "更新F5虚拟服务器"),
        CMDB_F5_DELETE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.DELETE, "删除F5虚拟服务器"),
        CMDB_F5_QUERY(ACCOUNT_TYPE.CMDB.name, ResourceOperation.QUERY, "查询F5虚拟服务器"),

        CMDB_CLOUD_DESKTOP_CREATE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.CREATE, "新增云桌面"),
        CMDB_CLOUD_DESKTOP_UPDATE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.UPDATE, "更新云桌面"),
        CMDB_CLOUD_DESKTOP_DELETE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.DELETE, "删除云桌面"),
        CMDB_CLOUD_DESKTOP_QUERY(ACCOUNT_TYPE.CMDB.name, ResourceOperation.QUERY, "查询云桌面"),

        PHYSICAL_MACHINE_CREATE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.CREATE, "新增物理机"),
        PHYSICAL_MACHINE_UPDATE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.UPDATE, "更新物理机"),
        PHYSICAL_MACHINE_DELETE(ACCOUNT_TYPE.CMDB.name, ResourceOperation.DELETE, "删除物理机"),
        PHYSICAL_MACHINE_QUERY(ACCOUNT_TYPE.CMDB.name, ResourceOperation.QUERY, "查询物理机"),

        //宿主机推送
        CMDB_HOST_QUERY(ACCOUNT_TYPE.CMDB.name, "", "查询宿主机"),
        CMDB_SYNC_HOST(ACCOUNT_TYPE.CMDB.name, "", "宿主机同步"),

        NOTICE_TODO(ACCOUNT_TYPE.NOTICE.name, "", "待办通知"),
        NOTICE_DONE(ACCOUNT_TYPE.NOTICE.name, "","已办通知"),

        PROCESS_CREATE(ACCOUNT_TYPE.PROCESS.name, "CREATE","虚拟机申请"),
        PROCESS_UPDATE(ACCOUNT_TYPE.PROCESS.name, "UPDATE","虚拟机配置变更"),
        PROCESS_DEPLOY_MIDDLEWARE(ACCOUNT_TYPE.PROCESS.name, "DEPLOY_MIDDLEWARE","虚拟机部署中间件"),
        PROCESS_DISK_UPDATE(ACCOUNT_TYPE.PROCESS.name, "DISK_UPDATE","磁盘变更"),
        PROCESS_SERVER_SNAPSHOT_CREATE(ACCOUNT_TYPE.PROCESS.name, "SERVER_SNAPSHOT_CREATE","创建快照申请"),
        PROCESS_VM_CLONE(ACCOUNT_TYPE.PROCESS.name, "VM_CLONE","虚拟机克隆"),
        PROCESS_DELETE(ACCOUNT_TYPE.PROCESS.name, "DELETE", "回收虚拟机"),
        PROCESS_UPDATE_OWNER(ACCOUNT_TYPE.PROCESS.name, "UPDATE_OWNER", "归属人变更"),
        //F5 apiType 要与业务模块的订单类型保持一致
        PROCESS_APPLY_VIRTUAL(ACCOUNT_TYPE.PROCESS.name, "APPLY_VIRTUAL","F5申请"),
        PROCESS_VDI_CREATE(ACCOUNT_TYPE.PROCESS.name, "VDI_CREATE", "云桌面申请"),
        PROCESS_VDI_UPDATE(ACCOUNT_TYPE.PROCESS.name, "VDI_UPDATE", "云桌面配置变更"),
        PROCESS_VDI_DELETE(ACCOUNT_TYPE.PROCESS.name, "VDI_DELETE", "云桌面删除"),
        PROCESS_VDI_UPDATE_OWNER(ACCOUNT_TYPE.PROCESS.name, "VDI_UPDATE_OWNER", "云桌面变更归属人"),

        PROCESS_DELETE_VIRTUAL(ACCOUNT_TYPE.PROCESS.name, "DELETE_VIRTUAL","F5回收"),
        PROCESS_UPDATE_VIRTUAL(ACCOUNT_TYPE.PROCESS.name, "UPDATE_VIRTUAL","F5修改"),
        PROCESS_UPDATE_OWNER_F5(ACCOUNT_TYPE.PROCESS.name, "UPDATE_OWNER_F5","F5归属人变更"),
        //回调ITSM接口修改工单状态
        PROCESS_GET_TICKET_INFO(ACCOUNT_TYPE.PROCESS.name, "GET_TICKET_INFO","获取工单详情"),
        PROCESS_OPERATE_NODE(ACCOUNT_TYPE.PROCESS.name, "OPERATE_NODE","更新工单节点（更新已完成状态）"),
        PROCESS_OPERATE_TICKET(ACCOUNT_TYPE.PROCESS.name, "OPERATE_TICKET","更新工单（更新已作废状态）"),

        ORG_OBTAIN_ORG(ACCOUNT_TYPE.ORG.name, "","获取组织"),
        ORG_OBTAIN_WORKSPACE(ACCOUNT_TYPE.ORG.name, "","获取工作空间"),
        ORG_OBTAIN_USER(ACCOUNT_TYPE.ORG.name, "","获取用户"),;

        API_TYPE(String accountType, String apiType, String name) {
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
            for (API_TYPE type : API_TYPE.values()) {
                if (Objects.equals(type.name(), typeName)) {
                    result = type.getName();
                }
            }
            return result;
        }

        public static String getAccountTypeByApiType(String apiType) {
            String result = "";
            for (API_TYPE type : API_TYPE.values()) {
                if (Objects.equals(type.apiType, apiType)) {
                    result = type.getAccountType();
                }
            }
            return result;
        }

        public static String getApiTypeNameByApiType(String apiType) {
            String result = "";
            for (API_TYPE type : API_TYPE.values()) {
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
