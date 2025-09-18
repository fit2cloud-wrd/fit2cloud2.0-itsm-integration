CREATE TABLE `pci_api_account`  (
    `id` varchar(32) NOT NULL COMMENT '主键索引',
    `name` varchar(255) NOT NULL COMMENT '账号名称',
    `system_type` varchar(10) NOT NULL COMMENT '对接系统类型(ITSM/CMDB)',
    `provider_factory_id` varchar(64) NOT NULL COMMENT '厂商 ID',
    `status` tinyint(1) NOT NULL COMMENT '状态',
    `msg` varchar(255) NULL COMMENT '异常原因',
    `api_endpoint` varchar(255) NOT NULL COMMENT 'API 地址前缀',
    `test_api_endpoint` varchar(255) NOT NULL COMMENT '测试 API 地址',
    `version` varchar(50) NOT NULL COMMENT '版本号',
    `credential` text NOT NULL COMMENT '认证信息',
    PRIMARY KEY (`id`)
);
CREATE TABLE `pci_api_endpoint`  (
    `id` varchar(32) NOT NULL COMMENT '主键索引',
    `api_type` varchar(20) NOT NULL COMMENT 'API 功能',
    `api_account` varchar(32) NOT NULL COMMENT 'API 账号 ID',
    `endpoint` varchar(255) NOT NULL COMMENT 'API 地址',
    `method` varchar(10) NOT NULL COMMENT '请求方法(GET/POST)',
    `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
    PRIMARY KEY (`id`)
);
CREATE TABLE `pci_api_parameter_mapping`  (
    `id` varchar(32) NOT NULL COMMENT '主键索引',
    `api_id` varchar(32) NOT NULL COMMENT 'API 地址 ID',
    `origin_field` varchar(50) NOT NULL COMMENT '源字段名',
    `origin_field_type` varchar(20) NOT NULL COMMENT '源字段类型',
    `target_field` varchar(50) NOT NULL COMMENT '目标字段名',
    `target_field_type` varchar(20) NOT NULL COMMENT '目标字段类型',
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