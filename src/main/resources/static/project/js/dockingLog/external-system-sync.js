ProjectApp.controller('ExternalSystemSyncDetailLogController', function ($scope, HttpUtils, $state, Notification, FilterSearch, AuthService, eyeService, $filter) {
    $scope.conditions = [
        {
            key: "type",
            name: $filter('translator')('i18n_sync_type', '同步类型'),
            directive: "filter-select",
            selects: [
                {value: true, label: $filter('translator')('i18n_sync_org', "组织机构同步")},
                {value: false, label: $filter('translator')('i18n_sync_user', "用户同步")}
            ]
        },
        {
            key: "name",
            name: $filter('translator')('i18n_name', '名称'),
            directive: "filter-contains",
        },
    ];

    $scope.logDetailColumns = [
        {value: $filter('translator')('i18n_sync_type', "同步类型"), key: "type", checked: true, sort: true},
        {value: $filter('translator')('i18n_name', "名称"), key: "name", checked: true, sort: true},
        {value: $filter('translator')('i18n_create_time', "创建时间"), key: "createTime", checked: true, sort: true},
        {value: $filter('translator')('i18n_err_msg', "异常信息"), key: "errMsg", sort: false},
    ];

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
        $scope.loadingLayer = HttpUtils.paging($scope, "api/log/syncDetail", condition, function (response) {
            $scope.logDetailItems = response.data["listObject"];
        });
    };

    $scope.list();

    // 查看异常信息
    $scope.showErrMessage = function (item) {
        $scope.message = item.errMsg;
        $scope.infoUrl = 'project/html/dockinglog/error-log-detail.html';
        $scope.toggleInfoForm(true);
    };

    $scope.closeInformation = function () {
        $scope.toggleInfoForm(false);
    };
});
