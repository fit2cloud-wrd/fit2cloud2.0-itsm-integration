ProjectApp.controller('ApiParameterMappingController', function ($scope, HttpUtils, Notification, FilterSearch, AuthService, eyeService, $filter) {

    $scope.conditions = [
        {
            key: "originField",
            name: $filter('translator')('i18n_origin_field', "源字段"),
            directive: "filter-contains",
        },
        {
            key: "description",
            name: $filter('translator')('i18n_oirigin_field_descreption', "描述"),
            directive: "filter-contains",
        },
        {
            key: "targetField",
            name: $filter('translator')('i18n_target_field', "映射字段"),
            directive: "filter-contains",
        },
        {
            key: "apiId",
            name: $filter('translator')('i18n_api_address', "API 地址"),
            directive: "filter-select-ajax",
            url: "api/account/getApiEndpointAll",
            convert: {value: "id", label: "endpoint"}
        },
     {
            key: "fieldTag",
            name: $filter('translator')('i18n_field_tag', "字段标签"),
            directive: "filter-contains",
        },
    ];

    // 全选按钮，添加到$scope.columns
    $scope.first = {
        default: true,
        sort: false,
        type: "checkbox",
        checkValue: false,
        change: function (checked) {
            $scope.items.forEach(function (item) {
                item.enable = checked;
            });
        },
        width: "40px"
    };

    $scope.columns = [
        {value: $filter('translator')('i18n_origin_field', "源字段"), key: "originField", sort: false},
        {value: $filter('translator')('i18n_oirigin_field_descreption', "描述"), key: "description", sort: false},
        {value: $filter('translator')('i18n_oirigin_field_type', "源字段类型"), key: "oiriginFieldType", sort: false},
        {value: $filter('translator')('i18n_target_field', "映射字段"), key: "targetField", sort: false},
        {value: $filter('translator')('i18n_target_field_type', "映射字段类型"), key: "targetFieldType", sort: false},
        {value: $filter('translator')('i18n_api_address', "API 地址"), key: "apiEndpoint", sort: false},
        {
            value: $filter('translator')('i18n_update_time', "修改时间"),
            key: "updateTime",
            checked: true,
            sort: true
        },
        {value: $filter('translator')('i18n_field_tag', "字段标签"), key: "fieldTag", sort: false},
    ];

    $scope.wizardDeleteColumns = [
        {value: $filter('translator')('i18n_origin_field', "源字段"), key: "originField", sort: false},
        {value: $filter('translator')('i18n_oirigin_field_descreption', "描述"), key: "description", sort: false},
        {value: $filter('translator')('i18n_target_field', "映射字段"), key: "targetField", sort: false}
    ];

    $scope.originFieldSourceOptions = [
        {"value": "table", "lable": $filter('translator')('i18n_origin_source_table', "资源表")},
        {"value": "tag", "lable": $filter('translator')('i18n_oirigin_source_tag', "标签")},
        {"value": "constant", "lable": $filter('translator')('i18n_oirigin_source_constant', "常量")},
        {"value": "resourceParam", "lable": $filter('translator')('i18n_oirigin_source_resource_param', "资源参数")},
        {"value": "splicing", "lable": $filter('translator')('i18n_oirigin_source_splicing', "拼接")},
        {"value": "orderField", "lable": $filter('translator')('i18n_oirigin_source_order_field', "订单属性")},
        {"value": "resourcePoolTag", "lable": $filter('translator')('i18n_oirigin_source_resource_pool_tag', "资源池标签")}
    ];

    $scope.endpointParam = angular.fromJson(sessionStorage.getItem("endpointParam"));
    sessionStorage.removeItem("endpointParam");

    if (AuthService.hasPermissions("API_PARAMETER_MAPPING:READ+EDIT")) {
        $scope.columns.push({value: "", default: true, sort: false});
        $scope.columns.unshift($scope.first);
    }

    // 获取API 地址
    HttpUtils.get("api/account/getApiEndpointAll", function (response) {
        $scope.apiEndpointOptions = response.data;
    });
    // 获取API参数映射源字段类型
    HttpUtils.get("api/account/originFieldType/list", function (response) {
        $scope.originFieldTypeOptions = response.data;
    });
    // 获取API参数映射映射字段类型
    HttpUtils.get("api/account/targetFieldType/list", function (response) {
        $scope.targetFieldTypeOptions = response.data;
    });

    //用于传入后台的数据
    $scope.filters = [];

    if ($scope.endpointParam) {
        $scope.filters = [{
            key: "apiId",
            name: $filter('translator')('i18n_api_address', "API 地址"),
            operator: "=",
            label: $scope.endpointParam.label,
            value: $scope.endpointParam.value
        }];
    }

    $scope.list = function (sortObj) {
        const condition = FilterSearch.convert($scope.filters);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        // 保留排序条件，用于分页
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }

        HttpUtils.paging($scope, "api/account/parameterMapping/list", condition);
    };

    $scope.list();

    $scope.add = function () {
        if ($scope.endpointParam && $scope.endpointParam.value) {
            $scope.currentParameterMapping = {
                apiId: $scope.endpointParam.value
            };
        } else {
            $scope.currentParameterMapping = {};
        }

        // 新增时默认 源字段来源 为资源表
        // $scope.currentParameterMapping.originFieldSource = "table";
        $scope.getOriginTableOptions($scope.currentParameterMapping.originFieldSource);
        $scope.showSelectFiled = true;
        $scope.originFieldOptions = [];

        $scope.edit = false;
        $scope.formUrl = "project/html/api/api-parameter-mapping-edit.html";
        $scope.toggleForm($scope.formUrl);
        $scope.show = true;
    };

    $scope.originFieldSourceChange = function () {
        if (!$scope.currentParameterMapping.originFieldSource) {
            return;
        }
        // 当选择常量时，不展示源表、源字段类型，切源字段变为输入框
        if ($scope.currentParameterMapping.originFieldSource === 'constant') {
            $scope.showSelectFiled = false;
            // 源自段类型置为固定值
            $scope.currentParameterMapping.originFieldType = "常量";
        } else if ($scope.currentParameterMapping.originFieldSource === 'resourceParam') {
            $scope.showSelectFiled = false;
            $scope.currentParameterMapping.originFieldType = "资源参数";
        } else if ($scope.currentParameterMapping.originFieldSource === 'table') {
            // 源字段类型置空
            $scope.currentParameterMapping.originFieldType = "";
            $scope.showSelectFiled = true;
            $scope.getOriginTableOptions($scope.currentParameterMapping.originFieldSource, $scope.currentParameterMapping.apiId);
        } else if ($scope.currentParameterMapping.originFieldSource === 'splicing') {
            $scope.showSelectFiled = false;
            // 源自段类型置为固定值
            $scope.currentParameterMapping.originFieldType = "String";
        } else if ($scope.currentParameterMapping.originFieldSource === 'orderField') {
            $scope.showSelectFiled = false;
            // 源自段类型置为固定值
            $scope.currentParameterMapping.originFieldType = "String";
            $scope.getOriginOrderFieldOptions($scope.currentParameterMapping.apiId);
        }else if ($scope.currentParameterMapping.originFieldSource === 'resourcePoolTag') {
            $scope.showSelectFiled = true;
            // 源自段类型置为固定值
            $scope.currentParameterMapping.originFieldType = "String";
            $scope.getOriginFieldResourcePoolTagOptions();
        } else {
            // 源字段类型置空
            $scope.currentParameterMapping.originFieldType = "String";
            $scope.showSelectFiled = true;
            $scope.getOriginFieldTagOptions();
        }
        $scope.currentParameterMapping.originField = "";
    };

    $scope.getOriginOrderFieldOptions = function (apiId) {
        if (!apiId) {
            return;
        }
        // 根据 流程api类型 获取可选的订单属性
        $scope.parameterMappingEditLoadingLayer = HttpUtils.get("api/account/orderField/list/" + apiId, function (response) {
            $scope.originFieldTypeOptions = response.data;
            //$scope.originTableChange();
        });
    };
    $scope.getOriginFieldTagOptions = function () {
        if ($scope.showSelectFiled) {
            $scope.parameterMappingEditLoadingLayer = HttpUtils.get("api/account/getOriginFieldTagOptions", function (response) {
                $scope.originFieldOptions = response.data;
            });
        }
    };

    $scope.getOriginFieldResourcePoolTagOptions = function () {
        if ($scope.showSelectFiled) {
            $scope.parameterMappingEditLoadingLayer = HttpUtils.get("api/account/getOriginFieldResourcePoolTagOptions", function (response) {
                $scope.originFieldOptions = response.data;
            });
        }
    };

    $scope.getOriginTableOptions = function (originFieldSource, apiId) {
        if (!originFieldSource || !apiId) {
            return;
        }
        // 根据 源字段来源 获取可选的表
        $scope.parameterMappingEditLoadingLayer = HttpUtils.get("api/account/originTable/list/" + apiId, function (response) {
            $scope.originTableOptions = response.data;
            $scope.originTableChange();
        });
    };

    $scope.originTableChange = function () {
        // 根据 源表 获取可选的表字段
        let originFieldTable = $scope.currentParameterMapping.originFieldTable;
        if (originFieldTable && $scope.showSelectFiled) {
            $scope.parameterMappingEditLoadingLayer = HttpUtils.get("api/account/getApiOriginTableAttr/" + originFieldTable, function (response) {
                $scope.originFieldOptions = response.data;
            });
        }
    };

    // API 地址变更
    $scope.apiEndpointChange = function () {
        $scope.originTableOptions = [];
        $scope.originFieldOptions = [];
        $scope.currentParameterMapping.originFieldSource = "";
        $scope.changeParameterMappingValue();
        $scope.originFieldSourceChange();
    };

    $scope.changeParameterMappingValue = function () {
        if (!$scope.currentParameterMapping.originFieldSource) {
            $scope.currentParameterMapping.originFieldTable = "";
        }
        if (!$scope.currentParameterMapping.originFieldTable) {
            $scope.currentParameterMapping.originField = "";
        }
    };

    $scope.editMapping = function (item) {
        console.log('item', item);
        $scope.currentParameterMapping = angular.copy(item);

        // 当选择常量时，不展示源表、源字段类型，切源字段变为输入框
        if ($scope.currentParameterMapping.originFieldSource === 'constant'
            || $scope.currentParameterMapping.originFieldSource === 'resourceParam'
            || $scope.currentParameterMapping.originFieldSource === 'splicing') {
            $scope.showSelectFiled = false;
        } else {
            $scope.showSelectFiled = true;
            $scope.getOriginTableOptions($scope.currentParameterMapping.originFieldSource, $scope.currentParameterMapping.apiId);
            $scope.originTableChange();
        }

        $scope.edit = true;
        $scope.formUrl = "project/html/api/api-parameter-mapping-edit.html";
        $scope.toggleForm($scope.formUrl, {
            currentParameterMapping: $scope.currentParameterMapping,
            edit: true,
            show: true
        });
        $scope.show = true;
    };

    $scope.delete = function (item) {
        Notification.confirm($filter('translator')('i18n_will_delete_api_parameter_mapping', '确认删除API参数映射') + '，' + item.originField + '?', function () {
            $scope.loadingLayer = HttpUtils.get('api/account/parameterMapping/delete/' + item.id, function (response) {
                Notification.success($filter('translator')('i18n_delete_success', '删除成功'));
                $scope.list();
            });
        });
    };

    $scope.closeDeleteDialog = function () {
        $scope.deleteItems = [];
        $scope.toggleForm();
    };

    $scope.deleteApiAccount = function () {
        let ids = $scope.deleteItems.map(item => item.id);
        $scope.apiEndpointDeleteLoadingLayer = HttpUtils.post('api/account/parameterMapping/batchDelete', ids, function (response) {
            if (response.success) {
                Notification.success($filter('translator')('i18n_delete_success', '删除成功'));
                $scope.list();
                $scope.closeToggleForm();
                $scope.show = false;
            }
        });
    };

    // 批量删除功能
    $scope.batchDelete = function () {
        let deleteItems = $scope.items.filter(item => item.enable);
        if (deleteItems.length < 1) {
            Notification.warn($filter('translator')('i18n_please_choose_data', '请选择数据'));
            return;
        }
        $scope.deleteItems = deleteItems;
        console.log('$scope.deleteItems', $scope.deleteItems);
        $scope.formUrl = "project/html/api/api-parameter-mapping-delete.html";
        $scope.toggleForm($scope.formUrl, {
            deleteItems: $scope.deleteItems
        });
    };

    $scope.closeToggleForm = function () {
        $scope.toggleForm();
        $scope.currentParameterMapping = {};
    };

    $scope.saveApiParameterMapping = function () {
        let url = 'api/account/parameterMapping/add';
        if ($scope.currentParameterMapping.id) {
            url = 'api/account/parameterMapping/update'
        }

        let parameterMapping = {};
        angular.copy($scope.currentParameterMapping, parameterMapping);

        // 组织参数映射数据
        let dictionaryMappingList = [];
        if ($scope.currentParameterMapping.originFieldType === 'Dictionary') {
            dictionaryMappingList = angular.copy($scope.dictionaryMappingList);
        }
        parameterMapping.mappingDictionaryList = dictionaryMappingList;

        //处理备注是否必填
        $scope.parameterMappingEditLoadingLayer = HttpUtils.post(url, parameterMapping, function () {
            Notification.success($filter('translator')('i18n_save_success', '保存成功'));
            $scope.closeToggleForm();
            $scope.list();
            $scope.show = false;
        });
    };

    $scope.getDictionaryMappingList = function (callback) {
        if ($scope.edit) {
            $scope.apiAccountEditLoadingLayer = HttpUtils.get('api/account/dictionaryMappingList/' + $scope.currentParameterMapping.id, function (response) {
                $scope.dictionaryMappingList = response.data;
                if ($scope.dictionaryMappingList.length < 1) {
                    $scope.dictionaryMappingList = [{
                        sourceFieldValue: '其他',
                        targetFieldValue: '',
                        isDefault: true
                    }];
                }
                callback(true);
            }, function (response) {
                Notification.danger($filter('translator')('i18n_obtain_dictionary_mapping_list_error', '获取API字典映射信息失败, 错误: ') + response.message);
                callback(false);
            });
        } else {
            $scope.dictionaryMappingList = [{
                sourceFieldValue: '其他',
                targetFieldValue: '',
                isDefault: true
            }];

            callback(true);
        }
    };

    $scope.initWizard = function () {
        $scope.wizard = {
            setting: {
                title: $filter('translator')('i18n_title', "标题"),
                subtitle: $filter('translator')('i18n_sub_title', "子标题"),
                closeText: $filter('translator')('i18n_cancel', "取消"),
                submitText: $filter('translator')('i18n_save', "保存"),
                nextText: $filter('translator')('i18n_next', "下一步"),
                prevText: $filter('translator')('i18n_previous', "上一步")
            },
            // 按顺序显示,id必须唯一并需要与页面中的id一致，select为分步初始化方法，next为下一步方法(最后一步时作为提交方法)
            steps: [
                {
                    id: "1",
                    name: $filter('translator')('i18n_basic_info', "基本信息"),
                    select: function () {
                    },
                    next: function () {
                        if ($scope.wizard.nextStep) {
                            $scope.wizard.nextStep = false;
                            return true;
                        }
                        if (!$scope.currentParameterMapping.apiId) {
                            Notification.info($filter('translator')('i18n_api_endpoint_not_null', "API地址不能为空"));
                            return false;
                        }
                        if (!$scope.currentParameterMapping.originField) {
                            Notification.info($filter('translator')('i18n_api_account_not_null', "源字段不能为空"));
                            return false;
                        }
                        if (!$scope.currentParameterMapping.originFieldType) {
                            Notification.info($filter('translator')('i18n_api_origin_field_type_not_null', "源字段类型不能为空"));
                            return false;
                        }
                        if (!$scope.currentParameterMapping.targetField) {
                            Notification.info($filter('translator')('i18n_api_method_not_null', "映射字段不能为空"));
                            return false;
                        }
                        if (!$scope.currentParameterMapping.targetFieldType) {
                            Notification.info($filter('translator')('i18n_api_method_not_null', "映射字段类型不能为空"));
                            return false;
                        }
                        // 标签名称翻译
                        if ($scope.currentParameterMapping.originFieldSource === 'tag') {
                            $scope.originFieldOptions.forEach(option => {
                                if (option.value === $scope.currentParameterMapping.originField) {
                                    $scope.currentParameterMapping.originFieldName = option.name;
                                }
                            });
                        }
                        console.log('$scope.currentParameterMapping', $scope.currentParameterMapping);

                        // 获取字典映射列表
                        if ($scope.currentParameterMapping.originFieldType === 'Dictionary') {
                            $scope.getDictionaryMappingList(function (result) {
                                if (result) {
                                    $scope.wizard.nextStep = true;
                                    $scope.wizard.next();
                                } else {
                                    return false;
                                }
                            });
                        } else {
                            $scope.wizard.nextStep = true;
                            $scope.wizard.next();
                        }
                    }
                },
                {
                    id: "2",
                    name: $filter('translator')('i18n_api_param_mapping', "API 参数映射"),
                    select: function () {
                    },
                    next: function () {
                        if ($scope.currentParameterMapping.originFieldType === 'Dictionary') {
                            if ($scope.dictionaryMappingList.length > 0
                                && $scope.dictionaryMappingList.filter(bo => !bo.sourceFieldValue || !bo.targetFieldValue).length > 0) {
                                Notification.info($filter('translator')('i18n_dictionary_mapping_not_null', "字典映射字段值不能为空，请检查"));
                                return false;
                            }
                        }

                        $scope.saveApiParameterMapping();
                    }
                }
            ],
            // 嵌入页面需要指定关闭方法
            close: function () {
                $scope.closeToggleForm();
                $scope.show = false;
            }
        };
    };

    $scope.initWizard();

    $scope.addDictionaryMapping = function () {
        $scope.dictionaryMappingList.push({
            sourceFieldValue: '',
            targetFieldValue: '',
            isDefault: false
        });
    };

    $scope.removeDictionaryMapping = function (index) {
        $scope.dictionaryMappingList.splice(index, 1);
    };
});
