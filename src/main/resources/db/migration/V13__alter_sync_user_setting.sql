ALTER TABLE `pci_sync_user_setting`
    ADD COLUMN `email` varchar(70) NULL COMMENT '邮箱';

ALTER TABLE `pci_sync_user_setting`
    MODIFY COLUMN `sync_user_id` varchar(64)  NOT NULL COMMENT '同步用户ID',
    MODIFY COLUMN `sync_user_name` varchar(255)  NOT NULL COMMENT '同步用户名称',
    MODIFY COLUMN `sync_user_code` varchar(255)  NULL DEFAULT NULL COMMENT '同步用户编码',
    MODIFY COLUMN `remark` varchar(255)  NULL DEFAULT NULL COMMENT '同步用户描述';

ALTER TABLE `pci_sync_user_setting`
    ADD COLUMN `org_id` varchar(10) NULL COMMENT '用户所属机构ID',
    ADD COLUMN `org_name` varchar(30) NULL COMMENT '用户所属机构名称';