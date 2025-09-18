DROP TABLE IF EXISTS `pci_api_request_log`;
CREATE TABLE `pci_api_request_log`
(
    `pci_api_log_id`  varchar(32) NOT NULL COMMENT '主键索引',
    `request_body` text COMMENT '请求体信息',
    PRIMARY KEY (`pci_api_log_id`)
);