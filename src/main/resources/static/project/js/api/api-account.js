ProjectApp.controller('ApiAccountController', function ($scope, HttpUtils, $state, Notification, FilterSearch, AuthService, eyeService, $filter) {
    $scope.conditions = [
        {
            key: "name",
            name: $filter('translator')('i18n_name', "名称"),
            directive: "filter-contains",
        },
        {
            key: "providerFactoryId",
            name: $filter('translator')('i18n_vendor', "厂商"),
            directive: "filter-select-ajax",
            url: "api/account/getVendorList",
            convert: {value: "id", label: "name"}
        },
        {
            key: "systemType",
            name: $filter('translator')('i18n_account_type', "账号类型"),
            directive: "filter-select-ajax",
            url: "api/account/type/list",
            convert: {value: "value", label: "lable"}
        },
        {
            key: "status",
            name: $filter('translator')('i18n_status', '状态'),
            directive: "filter-select",
            selects: [
                {value: '0', label: $filter('translator')('i18n_un_validate', "待验证")},
                {value: '1', label: $filter('translator')('i18n_can_use', "可用")},
                {value: '2', label: $filter('translator')('i18n_can_not_use', "不可用")}
            ]
        },
        {
            key: "enableFlag",
            name: $filter('translator')('i18n_enable_or_disable', '启用/禁用'),
            directive: "filter-select",
            selects: [
                {value: '1', label: $filter('translator')('i18n_enable', "启用")},
                {value: '0', label: $filter('translator')('i18n_disable', "禁用")}
            ]
        },
        {
            key: "syncStatus",
            name: $filter('translator')('i18n_sync_status', '同步状态'),
            directive: "filter-select",
            selects: [
                {value: "WAIT", label: $filter('translator')('i18n_sync_wait', "等待同步")},
                {value: "PROCESSING", label: $filter('translator')('i18n_sync_processing', "正在同步")},
                {value: "END", label: $filter('translator')('i18n_sync_end', "同步结束")}
            ]
        },
        {
            key: "autoSync",
            name: $filter('translator')('i18n_auto_sync', '自动同步'),
            directive: "filter-select",
            selects: [
                {value: true, label: $filter('translator')('i18n_true', "是")},
                {value: false,label: $filter('translator')('i18n_false', "否")},
            ]
        }
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
        {value: $filter('translator')('i18n_name', "名称"), key: "name", sort: false},
        {value: $filter('translator')('i18n_vendor', "厂商"), key: "providerFactoryId", sort: false},
        {value: $filter('translator')('i18n_account_type', "账号类型"), key: "systemType", sort: false},
        {value: $filter('translator')('i18n_status', "状态"), key: "status", sort: false},
        {value: $filter('translator')('i18n_sync_status', "同步状态"), key: "sync_status"},
        {value: $filter('translator')('i18n_auto_sync', "自动同步"), key: "auto_sync", sort: false},
        {value: $filter('translator')('i18n_api_address', "API 地址"), key: "testApiEndpoint", sort: false},
        {
            value: $filter('translator')('i18n_api_endpoint', "API Endpoint"),
            key: "apiEndpoint",
            checked: true,
            sort: true
        },
        {value: $filter('translator')('i18n_version_number', "版本号"), key: "version", checked: true, sort: true},
        {value: $filter('translator')('i18n_enable_or_disable', "启用/禁用"), key: "enable_flag", sort: false},
    ];

    $scope.wizardDeleteColumns = [
        {value: $filter('translator')('i18n_name', "名称"), key: "name", sort: false},
        {value: $filter('translator')('i18n_vendor', "厂商"), key: "providerFactoryId", sort: false},
        {value: $filter('translator')('i18n_account_type', "账号类型"), key: "systemType", sort: false}
    ];

    if (AuthService.hasPermissions("API_ACCOUNT:READ+EDIT")) {
        $scope.columns.push({value: "", default: true, sort: false});
        $scope.columns.unshift($scope.first);
    }

    // 初始化厂商
    HttpUtils.get("api/account/getVendorList", function (response) {
        $scope.providerFactoryOptions = response.data;
    });
    HttpUtils.get("api/account/type/list", function (response) {
        $scope.systemTypeOptions = response.data;
    });
    HttpUtils.get("api/account/apiType/list", function (response) {
        $scope.apiTypeOptions = response.data;
    });
    HttpUtils.get("api/account/bkCompanyApiType/list", function (response) {
        $scope.bkApiTypeOptions = response.data;
    });

    $scope.isChangeAutoSync = AuthService.hasPermissions("API_ACCOUNT:READ");
    /**
     * 是否开启自动同步
     * @param item
     */
    $scope.changeAutoSync = function (item) {
        if (undefined === item.autoSync) {
            return;
        }
        $scope.loadingLayer = HttpUtils.post("api/account/autoSync/" + item.id, !item.autoSync, function (resp) {
            Notification.success(Translator.get("i18n_update_success"));
            $scope.list();
        }, function (resp) {
            Notification.danger(Translator.get("i18n_update_failed") + resp.message);
        })
    };
    //用于传入后台的数据
    $scope.filters = [];

    $scope.list = function (sortObj) {
        const condition = FilterSearch.convert($scope.filters);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        // 保留排序条件，用于分页
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }
        HttpUtils.paging($scope, "api/account/list", condition, function (res) {
        });
    };

    $scope.list();

    $scope.add = function () {
        $scope.currentApiAccount = {};
        $scope.edit = false;
        $scope.formUrl = "project/html/api/api-account-edit.html";
        $scope.toggleForm($scope.formUrl);
        $scope.show = true;
    };

    $scope.editApiAccount = function (item) {
        // credential认真信息处理
        $scope.currentApiAccount = angular.copy(item);
        let credential = JSON.parse(item.credential);
        $scope.editTmpList = credential;
        $scope.patchCredentialElement();
        $scope.edit = true;
        $scope.formUrl = "project/html/api/api-account-edit.html";
        $scope.currentApiAccountName = $scope.currentApiAccount.name;
        $scope.toggleForm($scope.formUrl, {
            currentApiAccount: $scope.currentApiAccount,
            edit: true,
            show: true
        });
        $scope.getTemplate();
        $scope.show = true;
    };

    $scope.patchCredentialElement = function () {
        if ($scope.editTmpList.length > 0) {
            $scope.editTmpList.forEach(editTmp => {
                if (editTmp.defaultValue) {
                    $scope.currentApiAccount[editTmp.name] = editTmp.defaultValue;
                }
            });
        }
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
        $scope.formUrl = "project/html/api/api-account-delete.html";
        $scope.toggleForm($scope.formUrl, {
            deleteItems: $scope.deleteItems
        });
    };

    $scope.view = function () {
        eyeService.view('#password', '#eye');
    };

    $scope.addToChange = function (productId, templateId) {
        for (let i = 0; i < $scope.productList.length; i++) {
            if ($scope.productList[i].id == productId) {
                $scope.productList[i].description = templateId;
            }
        }
    };

    $scope.delete = function (item) {
        Notification.confirm($filter('translator')('i18n_will_delete_api_account', '确认删除API账号') + '，' + item.name + '?', function () {
            $scope.loadingLayer = HttpUtils.get('api/account/delete/' + item.id, function (response) {
                if (response.success) {
                    $scope.list();
                    Notification.success($filter('translator')('i18n_delete_success', '删除成功'));
                }
            });
        });
    };

    $scope.enableApiAccount = function (item) {
        $scope.loadingLayer = HttpUtils.get('api/account/enable/' + item.id, function (response) {
            if (response.success) {
                $scope.list();
                Notification.success($filter('translator')('i18n_operation_success', '操作成功'));
            }
        });
    };

    $scope.validate = function (item) {
        $scope.loadingLayer = HttpUtils.post("api/account/validate/" + item.id, {}, function (response) {
            if (response.data) {
                Notification.success($filter('translator')('i18n_account_cloud_valid_msg', '账号有效'));
                $scope.list();
            } else {
                Notification.danger($filter('translator')('i18n_account_cloud_invalid_msg', '账号无效'));
                $scope.list();
            }
        }, function (response) {
            Notification.danger($filter('translator')('i18n_account_cloud_invalid_msg', '账号无效') + "," + response.message);
            $scope.list();
        });
    };

    $scope.closeToggleForm = function () {
        $scope.currentApiAccount = {};
        $scope.toggleForm();
    };

    $scope.closeDeleteDialog = function () {
        $scope.deleteItems = [];
        $scope.toggleForm();
    };

    $scope.checkApiAccountName = function (name) {
        let check = name.length > 1 && name.length < 255;
        if (check && (!$scope.currentApiAccountName || $scope.currentApiAccountName !== name)) {
            HttpUtils.get("api/account/check/accountName/" + name, function (resp) {
                if (!resp.data) {
                    Notification.warn($filter('translator')('i18n_api_account_name_exist_already', '账号名称已存在'));
                }
            });
        }
    };

    $scope.saveApiAccount = function () {
        let url = 'api/account/add';
        if ($scope.currentApiAccount.id) {
            url = 'api/account/update'
        }

        let apiAccount = {};
        angular.copy($scope.currentApiAccount, apiAccount);
        apiAccount.credential = angular.toJson($scope.tmpList);
        apiAccount.defaultApiList = angular.toJson(apiAccount.defaultApiList.filter(api => api.enable));
        //处理备注是否必填
        $scope.apiAccountEditLoadingLayer = HttpUtils.post(url, apiAccount, function () {
            Notification.success($filter('translator')('i18n_save_success', '保存成功'));
            $scope.closeToggleForm();
            $scope.list();
            $scope.show = false;
        });
    };

    $scope.deleteApiAccount = function () {
        let ids = $scope.deleteItems.map(item => item.id);
        $scope.apiAccountDeleteLoadingLayer = HttpUtils.post('api/account/batchDelete', ids, function (response) {
            if (response.success) {
                Notification.success($filter('translator')('i18n_delete_success', '删除成功'));
                $scope.list();
                $scope.closeToggleForm();
                $scope.show = false;
            }
        });
    };

    $scope.getTemplate = function () {
        this.getApiVersion();
        this.getCreditialTemplate();
        this.getCustomContent();
    };

    $scope.getCustomContent = function () {
        $scope.apiAccountEditLoadingLayer = HttpUtils.get('api/account/customContent/' + $scope.currentApiAccount.id, function (response) {
            $scope.currentApiAccount.customContent = response.data
            $scope.initApiAccountBkCmdbV2cCustomContent($scope.currentApiAccount)
        }, function (response) {
            Notification.danger($filter('translator')('i18n_failed_obtain_template_error', '获取API认证模板信息失败, 错误: ') + response.message);
        });
    }

    $scope.getCreditialTemplate = function () {
        $scope.apiAccountEditLoadingLayer = HttpUtils.get('api/account/creditial/' + $scope.currentApiAccount.providerFactoryId, function (response) {
            let data = JSON.parse(response.data);
            console.log('data', data);
            $scope.tmpList = data.data;
            if ($scope.editTmpList) {
                $scope.editTmpList.forEach(editTmp => {
                    $scope.tmpList.map(tmp => {
                        if (editTmp.name === tmp.name) {
                            tmp.defaultValue = editTmp.defaultValue;
                        }
                    });
                });
            }
        }, function (response) {
            Notification.danger($filter('translator')('i18n_failed_obtain_template_error', '获取API认证模板信息失败, 错误: ') + response.message);
        });
    };

    $scope.getApiVersion = function () {
        $scope.apiAccountEditLoadingLayer = HttpUtils.get("api/account/version/" + $scope.currentApiAccount.providerFactoryId, function (response) {
            $scope.versionOptions = response.data;
        });
    };

    $scope.getDefaultApiList = function (callback) {
        if ($scope.edit) {
            $scope.apiAccountEditLoadingLayer = HttpUtils.get('api/account/apiList/' + $scope.currentApiAccount.id, function (response) {
                let data = response.data;
                console.log('data', data);
                $scope.currentApiAccount.defaultApiList = [];

                // 展示数据翻译
                data.forEach(defaultApi => {
                    $scope.currentApiAccount.defaultApiList.push(defaultApi);
                });
                console.log('defaultApiList', $scope.currentApiAccount.defaultApiList);
                callback(true);
            }, function (response) {
                Notification.danger($filter('translator')('i18n_failed_obtain_api_list_error', '获取API地址信息失败, 错误: ') + response.message);
                callback(false);
            });
        } else {
            $scope.apiAccountEditLoadingLayer = HttpUtils.get('api/account/default/apiList/' + $scope.currentApiAccount.providerFactoryId, function (response) {
                let data = angular.fromJson(response.data);
                console.log('data', data);
                console.log('$scope.systemTypeOptions', $scope.systemTypeOptions);
                $scope.currentApiAccount.defaultApiList = [];

                // 展示数据翻译
                data.data.forEach(defaultApi => {
                    let systemTypeOptions = $scope.systemTypeOptions.filter(option => defaultApi.systemType === option.value);
                    if (systemTypeOptions.length > 0) {
                        defaultApi.systemTypeName = systemTypeOptions[0].lable;
                    }

                    let apiTypeOptions = $scope.apiTypeOptions.filter(option => defaultApi.apiType === option.value);
                    if (defaultApi.company && "LANJINGCOMPANY" === defaultApi.company) {
                        // 进入判断中的代码
                        apiTypeOptions = $scope.bkApiTypeOptions.filter(option => defaultApi.apiType === option.value);
                    }

                    if (apiTypeOptions.length > 0) {
                        defaultApi.apiTypeName = apiTypeOptions[0].lable;
                    }

                    $scope.currentApiAccount.defaultApiList.push(defaultApi);
                });
                console.log('defaultApiList', $scope.currentApiAccount.defaultApiList);
                callback(true);
            }, function (response) {
                Notification.danger($filter('translator')('i18n_failed_obtain_default_api_list_error', '获取厂商的默认API地址信息失败, 错误: ') + response.message);
                callback(false);
            });
        }
    };

    $scope.goApiEndpoint = function (item) {
        console.log('item', "");
        sessionStorage.setItem("apiAccountParam", angular.toJson({
                label: item.name,
                value: item.id
            }
        ));
        $state.go("apiAddress");
    };

    $scope.execFunction = function (action, parameter) {
        return new Function('currentProduct', action)(parameter);
    };

    $scope.ifShow = function (tmp, currentProduct) {
        if (!tmp) return;

        if ($scope.execFunction(tmp.deleted, currentProduct)) {
            return false;
        }

        return true;
    };

    $scope.changeTmpValue = function (tmp) {
        $scope.currentApiAccount[tmp.name] = tmp.defaultValue;
    };

    $scope.changeAllSelected = function (enableAll) {
        console.log(enableAll);
        if ($scope.currentApiAccount.defaultApiList) {
            $scope.currentApiAccount.defaultApiList.forEach(defaultApi => {
                defaultApi.enable = enableAll;
            });
        }
    };

    $scope.getCmdbLevelConfigList = function (apiAccount) {
        return [
            {
                "label" : $filter('translator')( "i18n_cmdb_business_name", "业务名称（业务拓扑根目录）"),
                "defaultValue" : "",
                "description" : ""
            },
            {
                "label" : $filter('translator')( "i18n_cmdb_business_second_level_dir", "业务拓扑 二级目录"),
                "defaultValue" : "",
                "description" : ""
            },
            {
                "label" : $filter('translator')( "i18n_cmdb_business_third_level_dir","业务拓扑 三级目录"),
                "defaultValue" : "",
                "description" : ""
            },
            {
                "label" : $filter('translator')( "i18n_cmdb_business_four_level_dir", "业务拓扑 四级目录"),
                "defaultValue" : "",
                "description" : ""
            }
        ]
    }

    $scope.initApiAccountBkCmdbV2cCustomContent = function (apiAccount) {
        if (!(apiAccount.providerFactoryId ==='LANJINGCOMPANY'  && apiAccount.version  === 'V2-Custom'  && apiAccount.systemType  === 'CMDB') ) {
            return
        }
        if(apiAccount.customContent)  {
            let customContent = angular.fromJson(apiAccount.customContent);
            apiAccount.customContentInst = customContent;
            let levelConfigList =  $scope.getCmdbLevelConfigList();
            let levels =  customContent.levels;
            apiAccount.customContentInst.levelConfigList = levelConfigList
            for (let i = 0; i < levelConfigList.length; i++) {
                let configItem =  levelConfigList[i];
                configItem.defaultValue =  levels[i];
            }
        } else {
            apiAccount.customContentInst = {
                syncEnable : false,
                levels : [],
                levelConfigList : $scope.getCmdbLevelConfigList()
            }
        }
    }

    $scope.changeWizardStepsBkCmdbV2c = function (apiAccount) {

        if(!apiAccount){
          return;
        }

        let step = {
            id: "bkCmdbV2c",
            name: $filter('translator')('i18n_cmdb_business_sync_config', "业务同步配置"),
            select: function () {},
            next: function () {

                let customContentInst = angular.copy($scope.currentApiAccount.customContentInst);
                if(!customContentInst){
                        customContentInst   = {
                        syncEnable : false,
                        levels : [],
                        levelConfigList : $scope.getCmdbLevelConfigList()
                    }
                }

                let levels = customContentInst.levelConfigList.map(e => e.defaultValue)
                delete customContentInst.levelConfigList;
                customContentInst.levels = levels;
                $scope.currentApiAccount.customContent = angular.toJson(customContentInst);

                if ($scope.wizard.isLast()) {
                    $scope.saveApiAccount();
                } else {
                    return true;
                }
            }
        };

        let steps = $scope.wizard.steps;
        let stepIndex =  steps.findIndex(e => e.id === step.id);

        if (apiAccount.providerFactoryId ==='LANJINGCOMPANY'  && apiAccount.version  === 'V2-Custom'  && apiAccount.systemType  === 'CMDB' ) {
            if( stepIndex == -1 ){
                steps.push(step);
            }
        } else {
            if( stepIndex > -1 ){
                steps.splice(stepIndex, 1);
            }
        }
    }

    $scope.$watch("currentApiAccount", function (curr,old) {
        $scope. changeWizardStepsBkCmdbV2c(curr)
    },true)


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
                        //获取流程角色
                    },
                    next: function () {
                        if ($scope.wizard.nextStep) {
                            $scope.wizard.nextStep = false;
                            return true;
                        }
                        if (!$scope.currentApiAccount.name) {
                            Notification.info($filter('translator')('i18n_api_account_name_not_null', "账号名称不能为空"));
                            return false;
                        }
                        if (!$scope.currentApiAccount.providerFactoryId) {
                            Notification.info($filter('translator')('i18n_api_provider_not_null', "厂商不能为空"));
                            return false;
                        }
                        if (!$scope.currentApiAccount.systemType) {
                            Notification.info($filter('translator')('i18n_system_type_not_null', "账号类型不能为空"));
                            return false;
                        }
                        if (!$scope.currentApiAccount.version) {
                            Notification.info($filter('translator')('i18n_version_not_null', "API版本不能为空"));
                            return false;
                        }
                        if (!$scope.currentApiAccount.apiEndpoint) {
                            Notification.info($filter('translator')('i18n_api_endpoint_not_null', "账号API Endpoint不能为空"));
                            return false;
                        }
                        if ($scope.tmpList) {
                            let flag = true;
                            $scope.tmpList.map(tmp => {
                                if (tmp.required && !tmp.defaultValue && $scope.ifShow(tmp, $scope.currentApiAccount)) {
                                    Notification.info(tmp.notifyMsg);
                                    flag = false;
                                }
                            });
                            if (!flag) {
                                return false;
                            }
                        }

                        // 获取厂商的默认API地址列表
                        $scope.getDefaultApiList(function (result) {
                            console.log('result', result);
                            console.log('defaultApiList', $scope.currentApiAccount.defaultApiList);
                            if (result) {
                                $scope.wizard.nextStep = true;
                                $scope.wizard.next();
                            } else {
                                return false;
                            }
                        });
                    }
                },
                {
                    id: "2",
                    name: $filter('translator')('i18n_template_config', "API地址"),
                    select: function () {
                    },
                    next: function () {
                        if ($scope.wizard.isLast()) {
                           $scope.saveApiAccount();
                        } else {
                            $scope.initApiAccountBkCmdbV2cCustomContent($scope.currentApiAccount)
                            return true;
                        }
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
});
