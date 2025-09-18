
ALTER TABLE `pci_api_account`
    MODIFY COLUMN `status` varchar(1) NOT NULL COMMENT '状态(0:待验证,1:可用,2:不可用)';

ALTER TABLE `pci_api_parameter_mapping`
    ADD COLUMN `description` text NULL COMMENT '描述';

ALTER TABLE `pci_api_account`
    ADD COLUMN `enable` varchar(1) NOT NULL COMMENT '启用1/禁用0';