CREATE TABLE `pci_api_log_content`  (
    `pci_api_log_id` varchar(32) NOT NULL COMMENT '主键索引',
    `message` text NOT NULL COMMENT '日志信息',
    PRIMARY KEY (`pci_api_log_id`)
);