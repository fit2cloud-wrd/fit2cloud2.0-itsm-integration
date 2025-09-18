
CREATE TABLE `pci_api_parameter_mapping_dictionary`  (
    `id` varchar(32) NOT NULL COMMENT '主键索引',
    `parameter_mapping_id` varchar(32) NOT NULL COMMENT 'api字段映射表ID',
    `source_field_value` varchar(255) NOT NULL COMMENT '源字段值',
    `target_field_value` varchar(255) NOT NULL COMMENT '映射字段值',
    `is_default` tinyint(1) NULL COMMENT '默认映射标识',
    PRIMARY KEY (`id`)
);

ALTER TABLE `pci_api_parameter_mapping`
    ADD COLUMN `origin_field_source` varchar(32) NOT NULL COMMENT '源字段来源',
    ADD COLUMN `origin_field_table` varchar(32) NULL COMMENT '源表';


ALTER TABLE `pci_api_account`
    CHANGE COLUMN `enable` `enable_flag` varchar(1) NOT NULL COMMENT '启用1/禁用0';

ALTER TABLE `pci_api_parameter_mapping`
    ADD COLUMN `origin_field_name` varchar(200) NULL COMMENT '源字段名';