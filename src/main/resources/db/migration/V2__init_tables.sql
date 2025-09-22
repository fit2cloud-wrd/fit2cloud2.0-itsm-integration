CREATE TABLE `pci_process`  (
    `process_id` varchar(36) NULL,
    `module` varchar(50) NULL,
    `business_key` varchar(100) NULL,
    `resource_type` varchar(100) NULL,
    `external_process_id` varchar(255) NULL,
    INDEX `IDX_PCI_PROCESS_ID`(`process_id`) USING HASH,
    INDEX `IDX_PCI_PROCESS_BUSINESS_KEY`(`business_key`) USING HASH,
    INDEX `IDX_PCI_EXTERNAL_PROCESS_ID`(`external_process_id`) USING HASH,
    INDEX `IDX_PCI_PROCESS_MODULE`(`module`) USING HASH,
    INDEX `IDX_PCI_PROCESS_RESOURCE_TYPE`(`resource_type`) USING HASH
);
CREATE TABLE `pci_api_account`  (
    `id` varchar(32) NOT NULL COMMENT '主键索引',
    `name` varchar(255) NOT NULL COMMENT '账号名称',
    `system_type` varchar(10) NOT NULL COMMENT '对接系统类型(ITSM/CMDB)',
    `provider_factory_id` varchar(64) NOT NULL COMMENT '厂商 ID',
    `status` varchar(1) NOT NULL COMMENT '状态(0:待验证,1:可用,2:不可用)',
    `sync_status` varchar(50) DEFAULT NULL COMMENT '账号状态',
    `msg` varchar(255) NULL COMMENT '异常原因',
    `api_endpoint` varchar(255) NOT NULL COMMENT 'API 地址前缀',
    `test_api_endpoint` varchar(255) NOT NULL COMMENT '测试 API 地址',
    `version` varchar(50) NOT NULL COMMENT '版本号',
    `credential` text NOT NULL COMMENT '认证信息',
    `enable` varchar(1) NOT NULL COMMENT '启用1/禁用0',
    `custom_content` text NULL comment '自定义配置内容',
    `auto_sync` tinyint(1) DEFAULT '0' COMMENT '是否自动同步',
    PRIMARY KEY (`id`)
);
CREATE TABLE `pci_api_endpoint`  (
    `id` varchar(32) NOT NULL COMMENT '主键索引',
    `api_type` varchar(50) NOT NULL COMMENT 'API 功能',
    `api_account` varchar(32) NOT NULL COMMENT 'API 账号 ID',
    `endpoint` varchar(255) NOT NULL COMMENT 'API 地址',
    `method` varchar(10) NOT NULL COMMENT '请求方法(GET/POST)',
    `custom_content` text NULL comment '请求参数模版',
    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
    PRIMARY KEY (`id`)
);
CREATE TABLE `pci_api_parameter_mapping`  (
    `id` varchar(32) NOT NULL COMMENT '主键索引',
    `api_id` varchar(32) NOT NULL COMMENT 'API 地址 ID',
    `origin_field` varchar(200) NOT NULL COMMENT '源字段',
    `origin_field_name` varchar(200) NULL COMMENT '源字段名',
    `origin_field_type` varchar(20) NOT NULL COMMENT '源字段类型',
    `origin_field_source` varchar(32) NOT NULL COMMENT '源字段来源',
    `origin_field_table` varchar(32) NULL COMMENT '源表',
    `target_field` varchar(50) NOT NULL COMMENT '目标字段名',
    `target_field_type` varchar(20) NOT NULL COMMENT '目标字段类型',
    `field_tag` varchar(255) NULL comment '参数标识',
    `description` text NULL COMMENT '描述',
    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
    PRIMARY KEY (`id`)
);
CREATE TABLE `pci_api_log`  (
    `id` varchar(32) NOT NULL COMMENT '主键索引',
    `api_id` varchar(32) NOT NULL COMMENT 'API 地址 ID',
    `method` varchar(10) NOT NULL COMMENT '请求方法(GET/POST等)',
    `workspace` varchar(50) NOT NULL COMMENT '工作空间 ID',
    `resource_type` varchar(20) NOT NULL COMMENT '资源类型',
    `resource_id` varchar(50) NOT NULL COMMENT '资源 ID',
    `resource_name` varchar(255) NOT NULL COMMENT '资源名称',
    `module` varchar(50) NOT NULL COMMENT '模块 ID',
    `code` int(4) NOT NULL COMMENT '返回码 Response Code',
    `execute_time` bigint(13) NOT NULL COMMENT '开始执行时间',
    `expended_time` int(8) NOT NULL COMMENT '耗时(毫秒)',
    `request_body` text COMMENT '请求体信息',
    `message` text NOT NULL COMMENT '日志信息',
    PRIMARY KEY (`id`),
    INDEX `IDX_PCI_API_LOG_API_ID`(`api_id`) USING BTREE,
    INDEX `IDX_PCI_API_LOG_WORKSPACE`(`workspace`) USING BTREE,
    INDEX `IDX_PCI_API_LOG_RESOURCE_TYPE`(`resource_type`) USING BTREE,
    INDEX `IDX_PCI_API_LOG_RESOURCE_ID`(`resource_id`) USING BTREE,
    INDEX `IDX_PCI_API_LOG_RESOURCE_NAME`(`resource_name`) USING BTREE,
    INDEX `IDX_PCI_API_LOG_MODULE`(`module`) USING BTREE,
    INDEX `IDX_PCI_API_LOG_CODE`(`code`) USING BTREE,
    INDEX `IDX_PCI_API_LOG_EXECUTE_TIME`(`execute_time`) USING BTREE
);
CREATE TABLE `pci_api_parameter_mapping_dictionary`  (
    `id` varchar(32) NOT NULL COMMENT '主键索引',
    `parameter_mapping_id` varchar(32) NOT NULL COMMENT 'api字段映射表ID',
    `source_field_value` varchar(255) NOT NULL COMMENT '源字段值',
    `target_field_value` varchar(255) NOT NULL COMMENT '映射字段值',
    `is_default` tinyint(1) NULL COMMENT '默认映射标识',
    PRIMARY KEY (`id`)
);
CREATE TABLE `pci_sync_org_setting`  (
    `id` varchar(64) NOT NULL,
    `sync_org_id` varchar(64) NOT NULL COMMENT '同步机构ID',
    `sync_org_name` varchar(255) NOT NULL COMMENT '同步机构名称',
    `sync_org_code` varchar(255) NULL COMMENT '同步机构编码',
    `remark` varchar(255) NULL COMMENT '同步机构描述',
    `is_sync` tinyint(1) NOT NULL DEFAULT 1 COMMENT '同步标识（1同步/0不同步）',
    `create_time` bigint(0) NULL COMMENT '创建时间',
    `update_time` bigint(0) NULL COMMENT '最后更新时间',
    `create_user` varchar(64) NULL,
    `update_user` varchar(64) NULL,
    PRIMARY KEY (`id`)
);
CREATE TABLE `pci_sync_user_setting`  (
     `id` varchar(64) NOT NULL,
     `sync_user_id` varchar(64) NOT NULL COMMENT '同步用户ID',
     `sync_user_name` varchar(255) NOT NULL COMMENT '同步用户名称',
     `sync_user_code` varchar(255)  NULL DEFAULT NULL COMMENT '同步用户编码',
     `remark` varchar(255) NULL COMMENT '同步用户描述',
     `org_id` varchar(10) NULL COMMENT '用户所属机构ID',
     `org_name` varchar(30) NULL COMMENT '用户所属机构名称',
     `is_sync` tinyint(1) NOT NULL DEFAULT 1 COMMENT '同步标识（1同步/0不同步）',
     `email` varchar(70) NULL COMMENT '邮箱',
     `create_time` bigint(0) NULL COMMENT '创建时间',
     `update_time` bigint(0) NULL COMMENT '最后更新时间',
     `create_user` varchar(64) NULL,
     `update_user` varchar(64) NULL,
     PRIMARY KEY (`id`)
);
CREATE TABLE `pci_external_system_org` (
     `id` varchar(64) NOT NULL COMMENT '主编码',
     `parent_id` varchar(64) DEFAULT NULL COMMENT '父节点编码',
     `nc_org_id` varchar(64) DEFAULT NULL COMMENT 'NC组织编码',
     `name` varchar(100) NOT NULL COMMENT '组织名称',
     `short_name` varchar(100) DEFAULT NULL COMMENT '简称',
     `type` varchar(10) DEFAULT NULL COMMENT '组织类型：dept（部门）/corp(公司)',
     `business_corporation` varchar(30) DEFAULT '' COMMENT '企业法人',
     `org_admin_no` varchar(64) DEFAULT NULL COMMENT '部门负责人工号',
     `org_admin_name` varchar(50) DEFAULT NULL COMMENT '部门负责人姓名',
     `business_unit_code` varchar(255) DEFAULT NULL COMMENT '经营单位编码',
     `org_admin_post_name` varchar(30) DEFAULT NULL COMMENT '部门负责人岗位名称',
     `nc_org_parent_id` varchar(64) DEFAULT NULL COMMENT 'NC上级组织编码',
     `parent_name` varchar(100) DEFAULT NULL COMMENT '上级组织名称',
     `org_admin_post_id` varchar(30) DEFAULT NULL COMMENT '部门负责人岗位编码',
     `business_unit_name` varchar(30) DEFAULT NULL COMMENT '经营单位名称',
     `revoked` varchar(1) DEFAULT NULL COMMENT '是否撤销',
     `company_code` varchar(3) DEFAULT NULL COMMENT '公司编码',
     `company_cdescription` varchar(100) DEFAULT NULL COMMENT '公司描述',
     PRIMARY KEY (`id`)
);
CREATE TABLE `pci_external_system_user` (
      `id` varchar(10) NOT NULL COMMENT '主编码',
      `work_no` varchar(11) DEFAULT NULL COMMENT '员工工号',
      `name` varchar(50) DEFAULT NULL COMMENT '姓名',
      `position_serial_code` varchar(10) DEFAULT NULL COMMENT '岗位序列编码',
      `sex` varchar(1) DEFAULT NULL COMMENT '性别',
      `company_code` varchar(3) DEFAULT NULL COMMENT '所属公司编码',
      `company_name` varchar(30) DEFAULT NULL COMMENT '所属公司名称',
      `business_unit_code` varchar(4) DEFAULT NULL COMMENT '经营单位编码',
      `org_id` varchar(10) DEFAULT NULL COMMENT '所属部门编码',
      `org_name` varchar(30) DEFAULT NULL COMMENT '所属部门名称',
      `idcard_no` varchar(20) DEFAULT NULL COMMENT '证件号码',
      `phone_number` varchar(24) DEFAULT NULL COMMENT '手机',
      `email` varchar(70) DEFAULT NULL COMMENT '电子邮箱',
      `position_code` varchar(8) DEFAULT NULL COMMENT '岗位编码',
      `position_name` varchar(30) DEFAULT NULL COMMENT '岗位名称',
      `position_serial` varchar(30) DEFAULT NULL COMMENT '岗位序列',
      `on_the_job` varchar(1) DEFAULT NULL COMMENT '是否在岗',
      `type_code` varchar(3) DEFAULT NULL COMMENT '员工类别编码',
      `type` varchar(30) DEFAULT NULL COMMENT '员工类别',
      `business_unit_name` varchar(30) DEFAULT NULL COMMENT '经营单位名称',
      PRIMARY KEY (`id`)
);
CREATE TABLE `pci_external_system_org_mapping` (
   `org_id` varchar(50) NOT NULL COMMENT '组织 id',
   `external_system_org_id` varchar(10) NOT NULL COMMENT '主编码',
   PRIMARY KEY (`org_id`,`external_system_org_id`)
);
CREATE TABLE `pci_external_system_sync_detail_log`  (
    `id` varchar(32) NOT NULL,
    `pci_api_log_id` varchar(32) NOT NULL COMMENT '日志主表ID',
    `soa_id` varchar(32) NOT NULL COMMENT '同步机构或用户的主编码',
    `name` varchar(30) NULL COMMENT '机构名称或用户名称',
    `type` tinyint(1) NOT NULL COMMENT '类型：机构/用户',
    `err_msg` text NULL COMMENT '同步异常信息',
    `create_time` bigint(0) NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
);