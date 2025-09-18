ProjectApp.controller('ProcessDockingLogController', function ($scope, HttpUtils, $state, Notification, FilterSearch, AuthService, eyeService, $filter) {
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
            key: "resourceId",
            name: $filter('translator')('i18n_resource_id', "订单ID"),
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
        {value: $filter('translator')('i18n_api_account', "API 账号"), key: "apiId", sort: false},
        {value: $filter('translator')('i18n_api_type', "API 功能"), key: "apiType", sort: false},
        {value: $filter('translator')('i18n_workspace', "工作空间"), key: "workspace", sort: false},
        {value: $filter('translator')('i18n_resource_id', "订单ID"), key: "resourceId", sort: false},
        {value: $filter('translator')('i18n_api_address', "API 地址"), key: "endpoint", sort: false},
        {value: $filter('translator')('i18n_status', "状态"), key: "code", checked: true, sort: true},
        {value: $filter('translator')('i18n_execute_time', "调用时间"), key: "executeTime", checked: true, sort: true},
        {value: $filter('translator')('i18n_expended_time', "耗时(ms)"), key: "expendedTime", checked: true, sort: true},
    ];

    //用于传入后台的数据
    $scope.filters = [];

    if (AuthService.hasPermissions("DOCKING_LOG:PUSH")) {
        $scope.columns.push({value: "", default: true, sort: false});
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
        condition.systemType = "PROCESS";
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

    $scope.rePush = function (item) {
        $scope.loadingLayer = HttpUtils.post("process/repush/" + item.id, {}, function (response) {
            if (response.data) {
                Notification.success($filter('translator')('i18n_repush_success', '推送成功'));
                $scope.list();
            } else {
                Notification.danger($filter('translator')('i18n_repush_failed', '推送失败'));
                $scope.list();
            }
        }, function (response) {
            Notification.danger($filter('translator')('i18n_repush_failed', '推送失败') + "：" + response.message);
            $scope.list();
        });
    };
});
