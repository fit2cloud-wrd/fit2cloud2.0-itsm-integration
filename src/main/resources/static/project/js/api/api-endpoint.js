ProjectApp.controller('ApiAddressController', function ($scope, HttpUtils, Notification, $state, FilterSearch, AuthService, eyeService, $filter) {

    $scope.conditions = [
        {
            key: "systemType",
            name: $filter('translator')('i18n_account_business_type', "业务类型"),
            directive: "filter-select-ajax",
            url: "api/account/type/list",
            convert: {value: "value", label: "lable"}
        },
        {
            key: "apiType",
            name: $filter('translator')('i18n_api_type', "API 功能"),
            directive: "filter-select-ajax",
            url: "api/account/apiType/list",
            convert: {value: "value", label: "lable"}
        },
        {
            key: "apiAccount",
            name: $filter('translator')('i18n_api_endpoint_belong_account', "所属账号"),
            directive: "filter-select-ajax",
            url: "api/account/getApiAccountAll",
            convert: {value: "id", label: "name"}
        },
        {
            key: "endpoint",
            name: $filter('translator')('i18n_api_address', "API 地址"),
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
        {value: $filter('translator')('i18n_account_business_type', "业务类型"), key: "systemType", sort: false},
        {value: $filter('translator')('i18n_api_type', "API 功能"), key: "apiType", sort: false},
        {value: $filter('translator')('i18n_api_endpoint_belong_account', "所属账号"), key: "apiAccount", sort: false},
        {value: $filter('translator')('i18n_api_parameter_mapping', "参数映射"), key: "parameterMappingCount", sort: false},
        {value: $filter('translator')('i18n_api_address', "API 地址"), key: "endpoint", sort: false},
        {
            value: $filter('translator')('i18n_update_time', "修改时间"),
            key: "updateTime",
            checked: true,
            sort: true
        },
    ];

    $scope.apiMethodOptions = [
        {
            value: "POST",
            lable: "POST"
        },
        {
            value: "PUT",
            lable: "PUT"
        },
        {
            value: "GET",
            lable: "GET"
        },
        {
            value: "SOAP",
            lable: "SOAP"
        }
    ];

    $scope.wizardDeleteColumns = [
        {value: $filter('translator')('i18n_account_business_type', "业务类型"), key: "systemType", sort: false},
        {value: $filter('translator')('i18n_api_type', "API 功能"), key: "apiType", sort: false},
        {value: $filter('translator')('i18n_api_endpoint_belong_account', "所属账号"), key: "apiAccount", sort: false},
        {value: $filter('translator')('i18n_api_address', "API 地址"), key: "endpoint", sort: false}
    ];

    $scope.apiAccountParam = angular.fromJson(sessionStorage.getItem("apiAccountParam"));
    sessionStorage.removeItem("apiAccountParam");

    if (AuthService.hasPermissions("API_ACCOUNT:READ+EDIT")) {
        $scope.columns.push({value: "", default: true, sort: false});
        $scope.columns.unshift($scope.first);
    }

    HttpUtils.get("api/account/getApiAccountAll", function (response) {
        $scope.apiAccountOptions = response.data;
    });
    HttpUtils.get("api/account/apiType/list", function (response) {
        $scope.apiTypeOptions = response.data;
    });

    //用于传入后台的数据
    $scope.filters = [];

    if ($scope.apiAccountParam) {
        $scope.filters = [{
            key: "apiAccount",
            name: $filter('translator')('i18n_api_endpoint_belong_account', "所属账号"),
            operator: "=",
            label: $scope.apiAccountParam.label,
            value: $scope.apiAccountParam.value
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

        HttpUtils.paging($scope, "api/account/endpoint/list", condition, function (res) {
        });
    };

    $scope.list();

    $scope.add = function () {
        $scope.currentApiEndPoint = {};
        $scope.edit = false;
        $scope.formUrl = "project/html/api/api-endpoint-edit.html";
        $scope.toggleForm($scope.formUrl);
        $scope.show = true;
    };

    $scope.editEndpoint = function (item) {
        console.log('item', item);
        $scope.currentApiEndPoint = angular.copy(item);

        if ($scope.currentApiEndPoint.customContent) {
            $scope.currentApiEndPoint.customContent = angular.fromJson($scope.currentApiEndPoint.customContent);
            $scope.onSyncHostAssociationChange();
            $scope.onCustomContentObjIdChange();
        }

        $scope.edit = true;
        $scope.formUrl = "project/html/api/api-endpoint-edit.html";
        $scope.toggleForm($scope.formUrl, {
            currentApiEndPoint: $scope.currentApiEndPoint,
            edit: true,
            show: true
        });
        $scope.show = true;
    };

    // API账号变更，API功能可选值变更
    $scope.apiAccountChange = function () {
        let apiAccount = $scope.currentApiEndPoint.apiAccount;
        HttpUtils.get("api/account/apiType/list/" + apiAccount, function (response) {
            $scope.apiTypeOptions = response.data;
        });
    };

    $scope.view = function () {
        eyeService.view('#password', '#eye');
    };

    $scope.changeAndDeleteTemplate = function () {
        HttpUtils.post('template/changeAndDeleteTemplate' + '/' + $scope.nowTemplate, $scope.productList, function (res) {
            if (res.data) {
                $scope.list();
                Notification.success($filter('translator')('i18n_delete_ip_pool_re2', '删除成功'));
                $scope.closeToggleForm();
                $scope.show = false;
            }
        });
    };

    $scope.addToChange = function (productId, templateId) {
        for (let i = 0; i < $scope.productList.length; i++) {
            if ($scope.productList[i].id == productId) {
                $scope.productList[i].description = templateId;
            }
        }
    };

    $scope.delete = function (item) {
        Notification.confirm($filter('translator')('i18n_will_delete_api_endpoint', '确认删除API地址') + '，' + item.endpoint + '?', function () {
            $scope.loadingLayer = HttpUtils.get('api/account/endpoint/delete/' + item.id, function (response) {
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
        $scope.apiEndpointDeleteLoadingLayer = HttpUtils.post('api/account/endpoint/batchDelete', ids, function (response) {
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
        $scope.formUrl = "project/html/api/api-endpoint-delete.html";
        $scope.toggleForm($scope.formUrl, {
            deleteItems: $scope.deleteItems
        });
    };

    $scope.closeToggleForm = function () {
        $scope.toggleForm();
        $scope.currentApiAccount = {};
    };

    $scope.saveApiEndPoint = function () {
        let url = 'api/account/endpoint/add';
        if ($scope.currentApiEndPoint.id) {
            url = 'api/account/endpoint/update'
        }

        let apiEndPoint = {};
        angular.copy($scope.currentApiEndPoint, apiEndPoint);
        if (apiEndPoint.customContent) {
           apiEndPoint.customContent = angular.toJson(apiEndPoint.customContent)
        }
        //处理备注是否必填
        $scope.apiEndPointEditLoadingLayer = HttpUtils.post(url, apiEndPoint, function () {
            Notification.success($filter('translator')('i18n_save_success', '保存成功'));
            $scope.closeToggleForm();
            $scope.list();
            $scope.show = false;
        });
    };

    $scope.goParameterMapping = function (item) {
        sessionStorage.setItem("endpointParam", angular.toJson({
                label: item.endpoint,
                value: item.id
            }
        ));
        $state.go("apiParameterMapping");
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
                        if (!$scope.currentApiEndPoint.apiAccount) {
                            Notification.info($filter('translator')('i18n_api_account_not_null', "API账号不能为空"));
                            return false;
                        }
                        if (!$scope.currentApiEndPoint.apiType) {
                            Notification.info($filter('translator')('i18n_api_type_not_null', "API功能不能为空"));
                            return false;
                        }
                        if (!$scope.currentApiEndPoint.endpoint) {
                            Notification.info($filter('translator')('i18n_api_endpoint_not_null', "API地址不能为空"));
                            return false;
                        }
                        if (!$scope.currentApiEndPoint.method) {
                            Notification.info($filter('translator')('i18n_api_method_not_null', "请求方法不能为空"));
                            return false;
                        }

                        $scope.saveApiEndPoint();
                    }
                },
            ],
            // 嵌入页面需要指定关闭方法
            close: function () {
                $scope.closeToggleForm();
                $scope.show = false;
            }
        };
    };




    $scope.onSyncHostAssociationChange = function () {
        let apiAccountId = $scope.currentApiEndPoint.apiAccount;
        if (!apiAccountId && !$scope.currentApiEndPoint.customContent.syncHostAssociation ) {
            return false;
        }

        HttpUtils.get("bkcmdb/search_objects/" + apiAccountId , function (response) {
            $scope.bkCmdbObject = response.data;
        });

    }

    $scope.onCustomContentObjIdChange = function () {
        let objId = $scope.currentApiEndPoint.customContent.objId;
        if (!objId) {
            return false;
        }
        let apiAccountId = $scope.currentApiEndPoint.apiAccount;
        if (!apiAccountId) {
            return false;
        }

        HttpUtils.get("bkcmdb/find_object_association/" + apiAccountId +"/"+objId , function (response) {
                response.data.filter(item => item.isonly);
            $scope.bkCmdbObjectAssociation = response.data;
       });

    }


    $scope.showBkCoCmdbConfig = function () {

        let apiType = $scope.currentApiEndPoint.apiType;

        let apiTypeFilter = [
            "CMDB_CREATE_HOST",
            "CMDB_UPDATE_HOST",
            "CMDB_DELETE_HOST",
            "CMDB_QUERY_HOST"
        ];

        if (apiTypeFilter.includes(apiType)) {
            return false;
        }


        let apiAccountId = $scope.currentApiEndPoint.apiAccount;
        if (!apiAccountId) {
            return false;
        }

        let apiAccount = $scope.apiAccountOptions.find(e => e.id === apiAccountId);
        if (apiAccount && apiAccount.systemType === "CMDB" && apiAccount.providerFactoryId === "LANJINGCOMPANY" && apiAccount.version === "V2-Custom" ) {
            return true;
        }


    }


    $scope.initWizard();
});
