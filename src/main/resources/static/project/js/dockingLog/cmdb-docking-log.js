ProjectApp.controller('CmdbDockingLogController', function ($scope, HttpUtils, $state, Notification, FilterSearch, AuthService, eyeService, $filter, $interval) {
    $scope.conditions = [
        {
            key: "apiAccount",
            name: $filter('translator')('i18n_api_account', "API 账号"),
            directive: "filter-select-ajax",
            url: "api/account/getApiAccountAll",
            convert: {value: "id", label: "name"}
        },
        {
            key: "apiType",
            name: $filter('translator')('i18n_api_type', "API 功能"),
            directive: "filter-select-ajax",
            url: "api/account/apiType/list",
            convert: {value: "value", label: "lable"}
        },
        {
            key: "workspace",
            name: $filter('translator')('i18n_workspace', "工作空间"),
            directive: "filter-select-virtual",
            search: true,
            url: "workspace/listAll",
            convert: {value: "id", label: "name"}
        },
        {
            key: "resourceName",
            name: $filter('translator')('i18n_resource_name', "资源名称"),
            directive: "filter-contains"
        },
        {
            key: "endpoint",
            name: $filter('translator')('i18n_api_address', "API 地址"),
            directive: "filter-contains"
        },
        {
            key: "code",
            name: $filter('translator')('i18n_status', '状态'),
            directive: "filter-select",
            selects: [
                {value: '0', label: $filter('translator')('i18n_success', "成功")},
                {value: '1', label: $filter('translator')('i18n_error', "异常")},
                {value: '2', label: $filter('translator')('i18n_in_excution', "执行中")}
            ]
        },
    ];

    $scope.columns = [
        {value: $filter('translator')('i18n_api_account', "API 账号"), key: "api_id", sort: false},
        {value: $filter('translator')('i18n_api_type', "API 功能"), key: "api_type", sort: false},
        {value: $filter('translator')('i18n_workspace', "工作空间"), key: "workspace", sort: false},
        {value: $filter('translator')('i18n_resource_name', "资源名称"), key: "resource_name", sort: false},
        {value: $filter('translator')('i18n_api_address', "API 地址"), key: "endpoint", sort: false},
        {value: $filter('translator')('i18n_status', "状态"), key: "code", checked: true, sort: true},
        {value: $filter('translator')('i18n_execute_time', "调用时间"), key: "execute_time", checked: true, sort: true},
        {value: $filter('translator')('i18n_expended_time', "耗时(ms)"), key: "expended_time", checked: true, sort: true},
    ];

    //用于传入后台的数据
    $scope.filters = [];

    $scope.countInPush = function () {
        $scope.loadingLayer = HttpUtils.get("api/log/count/inPush/CMDB", function (response) {
            if (response.success) {
                $scope.$emit('countInPush', response.data);
            }
        });
    };

    // todo：超链接资源跳转
    $scope.goResource = function () {
        sessionStorage.setItem("orderServerParam", angular.toJson({
                label: item.resourceName,
                value: item.resourceId
            }
        ));
        $state.go("server");
    };

    //接收子级controller的值
    $scope.$on('countInPush', function (event, data) {
        $scope.inPushCount = data;
    });

    $scope.getStatus = function () {
        HttpUtils.get("api/log/pushAll/status", function (resp) {
            if (resp && resp.data === 'inPush') {
                $scope.isSync = true;
            } else {
                $scope.isSync = false;
            }
        });
    };

    $scope.getSyncHostStatus = function () {
        HttpUtils.get("api/log/syncHost/status", function (resp) {
            if (resp && resp.data === 'inPush') {
                $scope.isSyncHost = true;
            } else {
                $scope.isSyncHost = false;
            }
        });
    };

    $scope.checkStatus = function () {
        if ($scope.timer == undefined) {
            $scope.timer = $interval(function () {
                $scope.getStatus();
            }, 5000);
        } else {
            if (!$scope.isSync) {
                $interval.cancel($scope.timer);
            }
        }
    };

    $scope.checkSyncHostStatus = function () {
        if ($scope.syncHostTimer == undefined) {
            $scope.syncHostTimer = $interval(function () {
                $scope.getSyncHostStatus();
            }, 5000);
        } else {
            if (!$scope.isSyncHost) {
                $interval.cancel($scope.syncHostTimer);
            }
        }
    };

    // 全量推送
    $scope.pushAll = function (item) {
        let url = "api/log/pushAll";
        if (item) {
            url += item;
        }
        HttpUtils.get(url, function (resp) {
            if (resp.success) {
                Notification.success($filter('translator')('i18n_push_finished', '推送完成'));
            } else {
                Notification.info(result.data.message);
            }
            $scope.isSync = false;
            $scope.list();
        }, function (resp) {
            Notification.danger($filter('translator')('i18n_push_failed', '推送失败') +":"+resp.message);
            $scope.isSync = false;
            $scope.list();
        });
        $scope.isSync = true;
        $scope.checkStatus();
    };

    // 宿主机同步
    $scope.syncHost = function (item) {
        let url = "api/log/syncHost";
        if (item) {
            url += item;
        }
        HttpUtils.get(url, function (resp) {
            if (resp.success) {
                Notification.success($filter('translator')('i18n_sync_finished', '同步完成'));
            } else {
                Notification.info(result.data.message);
            }
            $scope.isSyncHost = false;
            $scope.list();
        }, function (resp) {
            Notification.danger($filter('translator')('i18n_sync_failed', '同步失败') +":"+resp.message);
            $scope.isSyncHost = false;
            $scope.list();
        });
        $scope.isSyncHost = true;
        $scope.checkSyncHostStatus();
    };

    $scope.list = function (sortObj) {
        const condition = FilterSearch.convert($scope.filters);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        // 保留排序条件，用于分页
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }
        condition.systemType = "CMDB";
        $scope.countInPush();
        $scope.getStatus();
        $scope.checkStatus();
        $scope.checkSyncHostStatus();
        HttpUtils.paging($scope, "api/log/list", condition);
    };

    $scope.list();

    $scope.showErrorDetail = function (item) {
        // 获取异常信息
        $scope.loadingLayer = HttpUtils.get("api/log/detail/" + item.id, function (response) {
            if (response.success) {
                $scope.message = response.data;
            }
        });
        $scope.infoUrl = 'project/html/dockinglog/error-log-detail.html';
        $scope.toggleInfoForm(true);
    };

    $scope.closeInformation = function () {
        $scope.toggleInfoForm(false);
    };
});
