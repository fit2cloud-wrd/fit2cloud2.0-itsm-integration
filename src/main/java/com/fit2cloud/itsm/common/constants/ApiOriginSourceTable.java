package com.fit2cloud.itsm.common.constants;

import com.fit2cloud.commons.server.base.domain.*;
import com.fit2cloud.itsm.model.VmCloudServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description: 字段映射源表
 * apiType：ApiType.API_TYPE
 * tableName:数据库表名
 * isDefault：当前apiType对应的默认的资源表
 * sqlWhere: 表查询关联sql
 * tableClass：表对应的实体类
 * @author zhaoqian
 * @date 2022/7/4 10:06 上午 
*/
public class ApiOriginSourceTable {

    public enum API_ORIGIN_TABLE {
        cloud_server("CMDB", "cloud_server", true, "cloud_server.id=${1}", CloudServer.class,"1"),
        workspace("CMDB", "workspace", false, "left join workspace on cloud_server.workspace_id = workspace.id", Workspace.class, "1"),
        vm_cloud_server("CMDB", "vm_cloud_server", false, "left join vm_cloud_server on cloud_server.id = vm_cloud_server.cloud_server_id", VmCloudServer.class, "1"),
        organization("CMDB", "organization", false, "left join workspace on cloud_server.workspace_id = workspace.id left join organization on workspace.organization_id = organization.id", Organization.class, "1"),
        mc_system("CMDB", "mc_system", false,
                "left join mc_system_mapping on cloud_server.id = mc_system_mapping.resource_id " +
                        "left join mc_system on mc_system_mapping.system_id = mc_system.id and mc_system.type = 'CUSTOM_SYSTEM'", McSystem.class, "1"),
        mc_system2("CMDB", "mc_system2", false,
                "left join mc_system_mapping on cloud_server.id = mc_system_mapping.resource_id " +
                        "left join mc_system on mc_system_mapping.system_id = mc_system.id and mc_system.type = 'APPLICATION'", McSystem.class, "1"),
        cloud_host("CMDB", "cloud_host", false, "left join cloud_host on cloud_server.host = cloud_host.host_id", CloudHost.class, "1"),
        cloud_host2("CMDB", "cloud_host2", true, "cloud_host.id=${1}", CloudHost.class, "2"),
        cloud_account("CMDB", "cloud_account", true, "cloud_account.id=${1}", CloudAccount.class, "3");

        API_ORIGIN_TABLE(String systemType, String tableName, Boolean isDefault,
                         String sqlWhere, Class<?> tableClass, String linkTag) {
            this.systemType = systemType;
            this.tableName = tableName;
            this.isDefault = isDefault;
            this.sqlWhere = sqlWhere;
            this.tableClass = tableClass;
            this.linkTag = linkTag;
        }

        private final String systemType;

        private final String tableName;

        private final Boolean isDefault;

        private final String sqlWhere;

        private final Class<?> tableClass;

        private final String linkTag;


        public static List<String> getTableList(String systemType) {
            List<String> tableList = new ArrayList<>();
            for (API_ORIGIN_TABLE table : API_ORIGIN_TABLE.values()) {
                if (Objects.equals(table.systemType, systemType)) {
                    tableList.add(table.tableName);
                }
            }
            return tableList;
        }

        public static List<String> getTableList(String systemType, String linkTag) {
            List<String> tableList = new ArrayList<>();
            for (API_ORIGIN_TABLE table : API_ORIGIN_TABLE.values()) {
                if (Objects.equals(table.systemType, systemType)
                        && Objects.equals(table.linkTag, linkTag)) {
                    tableList.add(table.tableName);
                }
            }
            return tableList;
        }

        public static String getTableName(String name) {
            String result = "";
            for (API_ORIGIN_TABLE table : API_ORIGIN_TABLE.values()) {
                if (Objects.equals(table.name(), name)) {
                    result = table.tableName;
                }
            }
            return result;
        }

        public static String getTableSqlWhere(String name) {
            String result = "";
            for (API_ORIGIN_TABLE table : API_ORIGIN_TABLE.values()) {
                if (Objects.equals(table.name(), name)) {
                    result = table.sqlWhere;
                }
            }
            return result;
        }

        public static Class<?> getTableClass(String name) {
            Class<?> result = null;
            for (API_ORIGIN_TABLE table : API_ORIGIN_TABLE.values()) {
                if (Objects.equals(table.tableName, name)) {
                    result = table.tableClass;
                }
            }
            return result;
        }

        public static List<String> getDefaultTable(String systemType) {
            List<String> defaultTableList = new ArrayList<>();
            for (API_ORIGIN_TABLE table : API_ORIGIN_TABLE.values()) {
                if (Objects.equals(table.systemType, systemType) && table.isDefault) {
                    defaultTableList.add(table.tableName);
                }
            }
            return defaultTableList;
        }

        public static String getTableLinktag(String name) {
            String result = "";
            for (API_ORIGIN_TABLE table : API_ORIGIN_TABLE.values()) {
                if (Objects.equals(table.name(), name)) {
                    result = table.linkTag;
                }
            }
            return result;
        }
    }

}
