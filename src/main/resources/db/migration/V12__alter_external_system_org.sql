ALTER TABLE `pci_external_system_org`
    MODIFY COLUMN `id` varchar(64)  NOT NULL COMMENT '主编码' FIRST,
    MODIFY COLUMN `parent_id` varchar(64)  NULL DEFAULT NULL COMMENT '父节点编码',
    MODIFY COLUMN `nc_org_id` varchar(64)  NULL DEFAULT NULL COMMENT 'NC组织编码',
    MODIFY COLUMN `name` varchar(100)  NOT NULL COMMENT '组织名称',
    MODIFY COLUMN `short_name` varchar(100)  NULL DEFAULT NULL COMMENT '简称',
    MODIFY COLUMN `org_admin_no` varchar(64)  NULL DEFAULT NULL COMMENT '部门负责人工号',
    MODIFY COLUMN `org_admin_name` varchar(30)  NULL DEFAULT NULL COMMENT '部门负责人姓名',
    MODIFY COLUMN `nc_org_parent_id` varchar(64)  NULL DEFAULT NULL COMMENT 'NC上级组织编码',
    MODIFY COLUMN `parent_name` varchar(100)  NULL DEFAULT NULL COMMENT '上级组织名称',
    MODIFY COLUMN `org_admin_post_id` varchar(30)  NULL DEFAULT NULL COMMENT '部门负责人岗位编码',
    MODIFY COLUMN `company_cdescription` varchar(100)  NULL DEFAULT NULL COMMENT '公司描述';


INSERT INTO `pci_external_system_org` (`id`, `parent_id`, `nc_org_id`, `name`, `short_name`, `type`, `business_corporation`, `org_admin_no`, `org_admin_name`, `business_unit_code`, `org_admin_post_name`, `nc_org_parent_id`, `parent_name`, `org_admin_post_id`, `business_unit_name`, `revoked`, `company_code`, `company_cdescription`)
VALUES ('16f7ecc0-48ca-4e46-894b-ed0d8f998dfe', '', '', '总部', '总部', 'corp', '', '', '', '', '', '', '', '', '总部', 'N', '001', '总部');