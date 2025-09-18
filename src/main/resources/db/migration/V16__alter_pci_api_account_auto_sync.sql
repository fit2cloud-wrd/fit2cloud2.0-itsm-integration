ALTER TABLE pci_api_account ADD `sync_status` varchar(50) DEFAULT NULL COMMENT '账号状态';
ALTER TABLE pci_api_account ADD `auto_sync`  tinyint(1) DEFAULT '0' COMMENT '是否自动同步';
