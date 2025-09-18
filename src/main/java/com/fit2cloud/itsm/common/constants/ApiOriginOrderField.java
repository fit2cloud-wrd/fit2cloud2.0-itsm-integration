package com.fit2cloud.itsm.common.constants;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApiOriginOrderField {
    public enum ORIGIN_FIELD_TYPE {
        vm_create_vm_name("PROCESS_CREATE", "vmName", "机器名"),
        vm_create_instance_type("PROCESS_CREATE", "instanceType", "实例类型"),
        vm_create_ip_array("PROCESS_CREATE", "ipArray", "IP地址"),
        vm_create_system_disk_size("PROCESS_CREATE", "systemDiskSize", "系统盘大小"),
        vm_create_disk_size("PROCESS_CREATE", "diskSize", "数据盘大小"),
        vm_create_mount_dir("PROCESS_CREATE", "mountDir", "数据盘挂载点"),
        vm_create_network("PROCESS_CREATE", "network", "网络"),
        vm_create_hostname("PROCESS_CREATE", "hostname", "主机名"),
        vm_create_expired_time("PROCESS_CREATE", "expiredTime", "到期时间"),
        vm_create_remark("PROCESS_CREATE", "remark", "用途"),
        vm_create_vm_os("PROCESS_CREATE", "vm_os", "操作系统"),
        vm_create_vm_os_version("PROCESS_CREATE", "vm_os_version", "操作系统版本"),
        vm_create_order_id("PROCESS_CREATE", "orderId", "订单号"),
        vm_create_apply_user_id("PROCESS_CREATE", "applyUserId", "申请人ID"),
        vm_create_apply_user_name("PROCESS_CREATE", "applyUserName", "申请人姓名"),
        vm_create_order_description("PROCESS_CREATE", "orderDescription", "订单描述"),
        vm_create_custom_system("PROCESS_CREATE", "custom_system", "应用系统"),
        vm_create_application("PROCESS_CREATE", "application", "应用"),
        vm_create_cpu_count("PROCESS_CREATE", "cpuCount", "CPU核数"),
        vm_create_memory("PROCESS_CREATE", "memory", "内存大小"),
        vm_create_count("PROCESS_CREATE", "count", "申请数量"),
        vm_create_business_key("PROCESS_CREATE", "businessKey", "申请单号"),
        vm_create_applicant("PROCESS_CREATE", "applicant", "申请人（id）"),
        vm_create_description("PROCESS_CREATE", "description", "申请原因"),
        vm_create_orgAdmins("PROCESS_CREATE", "orgAdmins", "工作空间下的组织管理员"),
        vm_create_admins("PROCESS_CREATE", "admins", "系统管理员"),
        vm_create_disk_type("PROCESS_CREATE", "diskType", "磁盘类型"),
        vm_create_add_domain("PROCESS_CREATE", "addDomain", "是否加域"),
        vm_create_platform("PROCESS_CREATE", "platform", "云平台"),
        vm_create_owner("PROCESS_CREATE", "owner", "归属人"),
        vm_create_expired_time_str("PROCESS_CREATE", "expiredTimeStr", "租期"),

        vm_delete_hostname("PROCESS_DELETE", "hostname", "主机名"),
        vm_delete_host("PROCESS_DELETE", "host", "宿主机"),
        vm_delete_organization_name("PROCESS_DELETE", "organizationName", "组织"),
        vm_delete_apply_user_name("PROCESS_DELETE", "applyUserName", "申请人姓名"),
        vm_delete_apply_disk("PROCESS_DELETE", "disk", "磁盘大小(GB)"),
        vm_delete_ip("PROCESS_DELETE", "managementIp", "IP地址"),
        vm_delete_instance_name("PROCESS_DELETE", "instanceName", "虚拟机名称"),
        vm_delete_region_name("PROCESS_DELETE", "regionName", "区域"),
        vm_delete_remark("PROCESS_DELETE", "remark", "备注"),
        vm_delete_info_os("PROCESS_DELETE", "osInfo", "操作系统"),
        vm_delete_owner("PROCESS_DELETE", "ownerName", "归属人"),
        vm_delete_workspace_name("PROCESS_DELETE", "workspaceName", "工作空间"),
        vm_delete_zone_name("PROCESS_DELETE", "zoneName", "可用区"),
        vm_delete_instance_type("PROCESS_DELETE", "instanceType", "实例类型"),
        vm_delete_create_time("PROCESS_DELETE", "createTime", "创建时间"),
        vm_delete_expired_time("PROCESS_DELETE", "expiredTime", "到期时间"),
        vm_delete_business_key("PROCESS_DELETE", "businessKey", "申请单号"),
        vm_delete_applicant("PROCESS_DELETE", "applicant", "申请人（id）"),
        vm_delete_description("PROCESS_DELETE", "description", "申请原因"),
        vm_delete_orgAdmins("PROCESS_DELETE", "orgAdmins", "工作空间下的组织管理员"),
        vm_delete_admins("PROCESS_DELETE", "admins", "系统管理员"),
        vm_delete_account_name("PROCESS_DELETE", "accountName", "云账号名称"),

        vm_update_hostname("PROCESS_UPDATE", "hostname", "主机名"),
        vm_update_host("PROCESS_UPDATE", "host", "宿主机"),
        vm_update_organization_name("PROCESS_UPDATE", "organizationName", "组织"),
        vm_update_apply_user_name("PROCESS_UPDATE", "applyUserName", "申请人姓名"),
        vm_update_apply_disk("PROCESS_UPDATE", "disk", "磁盘大小(GB)"),
        vm_update_ip("PROCESS_UPDATE", "managementIp", "IP地址"),
        vm_update_instance_name("PROCESS_UPDATE", "instanceName", "虚拟机名称"),
        vm_update_region_name("PROCESS_UPDATE", "regionName", "区域"),
        vm_update_remark("PROCESS_UPDATE", "remark", "备注"),
        vm_update_info_os("PROCESS_UPDATE", "osInfo", "操作系统"),
        vm_update_owner("PROCESS_UPDATE", "ownerName", "归属人"),
        vm_update_workspace_name("PROCESS_UPDATE", "workspaceName", "工作空间"),
        vm_update_zone_name("PROCESS_UPDATE", "zoneName", "可用区"),
        vm_update_create_time("PROCESS_UPDATE", "createTime", "创建时间"),
        vm_update_expired_time("PROCESS_UPDATE", "expiredTime", "到期时间"),
        vm_update_business_key("PROCESS_UPDATE", "businessKey", "申请单号"),
        vm_update_applicant("PROCESS_UPDATE", "applicant", "申请人（id）"),
        vm_update_description("PROCESS_UPDATE", "description", "申请原因"),
        vm_update_orgAdmins("PROCESS_UPDATE", "orgAdmins", "工作空间下的组织管理员"),
        vm_update_admins("PROCESS_UPDATE", "admins", "系统管理员"),
        vm_update_account_name("PROCESS_UPDATE", "accountName", "云账号名称"),
        vm_update_instance_type("PROCESS_UPDATE","instanceType","变更后大小"),
        vm_update_instance_type_before("PROCESS_UPDATE","instanceTypeBefore","变更前大小"),

        vm_disk_update_hostname("PROCESS_DISK_UPDATE", "hostname", "主机名"),
        vm_disk_update_host("PROCESS_DISK_UPDATE", "host", "宿主机"),
        vm_disk_update_organization_name("PROCESS_DISK_UPDATE", "organizationName", "组织"),
        vm_disk_update_apply_user_name("PROCESS_DISK_UPDATE", "applyUserName", "申请人姓名"),
        vm_disk_update_ip("PROCESS_DISK_UPDATE", "managementIp", "IP地址"),
        vm_disk_update_instance_name("PROCESS_DISK_UPDATE", "instanceName", "虚拟机名称"),
        vm_disk_update_region_name("PROCESS_DISK_UPDATE", "regionName", "区域"),
        vm_disk_update_remark("PROCESS_DISK_UPDATE", "remark", "备注"),
        vm_disk_update_info_os("PROCESS_DISK_UPDATE", "osInfo", "操作系统"),
        vm_disk_update_owner("PROCESS_DISK_UPDATE", "ownerName", "归属人"),
        vm_disk_update_workspace_name("PROCESS_DISK_UPDATE", "workspaceName", "工作空间"),
        vm_disk_update_zone_name("PROCESS_DISK_UPDATE", "zoneName", "可用区"),
        vm_disk_update_create_time("PROCESS_DISK_UPDATE", "createTime", "创建时间"),
        vm_disk_update_expired_time("PROCESS_DISK_UPDATE", "expiredTime", "到期时间"),
        vm_disk_update_business_key("PROCESS_DISK_UPDATE", "businessKey", "申请单号"),
        vm_disk_update_applicant("PROCESS_DISK_UPDATE", "applicant", "申请人（id）"),
        vm_disk_update_description("PROCESS_DISK_UPDATE", "description", "申请原因"),
        vm_disk_update_orgAdmins("PROCESS_DISK_UPDATE", "orgAdmins", "工作空间下的组织管理员"),
        vm_disk_update_admins("PROCESS_DISK_UPDATE", "admins", "系统管理员"),
        vm_disk_update_account_name("PROCESS_DISK_UPDATE", "accountName", "云账号名称"),
        vm_disk_update_apply_disk_before("PROCESS_DISK_UPDATE", "diskSize", "变更前(磁盘总大小(GB))"),
        vm_disk_update_apply_disk("PROCESS_DISK_UPDATE", "newDiskSize", "变更后(磁盘总大小(GB))"),
        vm_disk("PROCESS_DISK_UPDATE", "disk", "磁盘"),
        vm_disk_name("PROCESS_DISK_UPDATE", "diskName", "磁盘名称（多磁盘拼接）"),
        vm_disk_update_config_name("PROCESS_DISK_UPDATE","diskConfig_diskName","磁盘名称（单块磁盘）"),
        vm_disk_update_config_disk_before("PROCESS_DISK_UPDATE","diskConfig_diskSizeBefore","变更前磁盘大小(GB)（单块磁盘）"),
        vm_disk_update_config_disk("PROCESS_DISK_UPDATE","diskConfig_diskSize","变更后磁盘大小(GB)（单块磁盘）"),
        vm_disk_update_config_update("PROCESS_DISK_UPDATE","diskConfig_update","是否扩容或新增（单块磁盘）"),
        vm_disk_update_config_datastore("PROCESS_DISK_UPDATE","diskConfig_datastoreName","存储器（单块磁盘）"),
        vm_disk_update_config_type("PROCESS_DISK_UPDATE","diskConfig_diskType","磁盘置备（单块磁盘）"),
        vm_disk_update_config_mode("PROCESS_DISK_UPDATE","diskConfig_diskMode","磁盘模式（单块磁盘）"),
        vm_disk_update_config_mountDir("PROCESS_DISK_UPDATE","diskConfig_mountDir","挂载点（单块磁盘）"),
        vm_datastore_Name("PROCESS_DISK_UPDATE","datastoreName","在与虚拟机相同目录中的存储器"),
        vm_disk_type("PROCESS_DISK_UPDATE","diskType","磁盘置备"),
        vm_disk_mode("PROCESS_DISK_UPDATE","diskMode","磁盘模式"),
        vm_red_text("PROCESS_DISK_UPDATE","redText","redText"),
        vm_mountDir("PROCESS_DISK_UPDATE", "mountDir", "装载目录");






                ;

        ORIGIN_FIELD_TYPE(String apiType, String key, String label) {
            this.apiType = apiType;
            this.key = key;
            this.label = label;
        }

        private final String apiType;

        private final String key;

        private final String label;

        public static List<JSONObject> getOrderFieldByApiType (String apiType) {
            List<JSONObject> resultList = new ArrayList<>();
            for (ApiOriginOrderField.ORIGIN_FIELD_TYPE field : ApiOriginOrderField.ORIGIN_FIELD_TYPE.values()) {
                if (Objects.equals(field.apiType, apiType)) {
                    JSONObject object = new JSONObject();
                    object.put("key", field.key);
                    object.put("label", field.label);
                    resultList.add(object);
                }
            }
            return resultList;
        }

        public String getApiType() {
            return apiType;
        }

        public String getKey() {
            return key;
        }

        public String getLabel() {
            return label;
        }
    }
}
