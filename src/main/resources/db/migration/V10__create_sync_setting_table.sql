
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
     `sync_user_id` varchar(64) NOT NULL COMMENT '同步机构ID',
     `sync_user_name` varchar(255) NOT NULL COMMENT '同步机构名称',
     `sync_user_code` varchar(255) NULL COMMENT '同步机构编码',
     `remark` varchar(255) NULL COMMENT '同步机构描述',
     `is_sync` tinyint(1) NOT NULL DEFAULT 1 COMMENT '同步标识（1同步/0不同步）',
     `create_time` bigint(0) NULL COMMENT '创建时间',
     `update_time` bigint(0) NULL COMMENT '最后更新时间',
     `create_user` varchar(64) NULL,
     `update_user` varchar(64) NULL,
     PRIMARY KEY (`id`)
);