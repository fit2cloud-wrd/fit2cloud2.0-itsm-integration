CREATE TABLE `pci_process`  (
    `process_id` varchar(36) NULL,
    `module` varchar(50) NULL,
    `business_key` varchar(100) NULL,
    `resource_type` varchar(100) NULL,
    `external_process_id` varchar(255) NULL,
    INDEX `IDX_PCI_PROCESS_ID`(`process_id`) USING HASH,
    INDEX `IDX_PCI_PROCESS_BUSINESS_KEY`(`business_key`) USING HASH,
    INDEX `IDX_PCI_EXTERNAL_PROCESS_ID`(`external_process_id`) USING HASH,
    INDEX `IDX_PCI_PROCESS_MODULE`(`module`) USING HASH,
    INDEX `IDX_PCI_PROCESS_RESOURCE_TYPE`(`resource_type`) USING HASH
);