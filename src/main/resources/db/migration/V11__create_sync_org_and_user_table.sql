
DROP TABLE IF EXISTS `pci_external_system_org`;
CREATE TABLE `pci_external_system_org` (
     `id` varchar(10) NOT NULL COMMENT '主编码',
     `parent_id` varchar(10) DEFAULT NULL COMMENT '父节点编码',
     `nc_org_id` varchar(30) DEFAULT NULL COMMENT 'NC组织编码',
     `name` varchar(30) NOT NULL COMMENT '组织名称',
     `short_name` varchar(10) DEFAULT NULL COMMENT '简称',
     `type` varchar(10) DEFAULT NULL COMMENT '组织类型：dept（部门）/corp(公司)',
     `business_corporation` varchar(30) DEFAULT '' COMMENT '企业法人',
     `org_admin_no` varchar(11) DEFAULT NULL COMMENT '部门负责人工号',
     `org_admin_name` varchar(50) DEFAULT NULL COMMENT '部门负责人姓名',
     `business_unit_code` varchar(255) DEFAULT NULL COMMENT '经营单位编码',
     `org_admin_post_name` varchar(30) DEFAULT NULL COMMENT '部门负责人岗位名称',
     `nc_org_parent_id` varchar(30) DEFAULT NULL COMMENT 'NC上级组织编码',
     `parent_name` varchar(30) DEFAULT NULL COMMENT '上级组织名称',
     `org_admin_post_id` varchar(8) DEFAULT NULL COMMENT '部门负责人岗位编码',
     `business_unit_name` varchar(30) DEFAULT NULL COMMENT '经营单位名称',
     `revoked` varchar(1) DEFAULT NULL COMMENT '是否撤销',
     `company_code` varchar(3) DEFAULT NULL COMMENT '公司编码',
     `company_cdescription` varchar(30) DEFAULT NULL COMMENT '公司描述',
     PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `pci_external_system_user`;
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

DROP TABLE IF EXISTS `pci_external_system_org_mapping`;
CREATE TABLE `pci_external_system_org_mapping` (
   `org_id` varchar(50) NOT NULL COMMENT '组织 id',
   `external_system_org_id` varchar(10) NOT NULL COMMENT '主编码',
   PRIMARY KEY (`org_id`,`external_system_org_id`)
);

DROP TABLE IF EXISTS `pci_external_system_sync_detail_log`;
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