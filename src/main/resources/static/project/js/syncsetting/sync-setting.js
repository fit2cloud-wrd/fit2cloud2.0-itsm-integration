ProjectApp.controller('SyncOrgController', function ($scope, HttpUtils, $state, Notification, FilterSearch, AuthService, eyeService, $filter) {
    $scope.conditions = [
        {
            key: "syncOrgName",
            name: $filter('translator')('i18n_name', "名称"),
            directive: "filter-contains",
        },
        {
            key: "syncOrgCode",
            name: $filter('translator')('i18n_code', "编码"),
            directive: "filter-contains",
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
        {value: $filter('translator')('i18n_name', "名称"), key: "sync_org_name", sort: false},
        {value: $filter('translator')('i18n_code', "编码"), key: "sync_org_code", sort: false},
        {value: $filter('translator')('i18n_remark', "描述"), key: "remark", sort: false},
        {value: $filter('translator')('i18n_status', "状态"), key: "is_sync", sort: false},
        {value: $filter('translator')('i18n_create_time', "创建时间"), key: "create_time", sort: true},
        {value: $filter('translator')('i18n_update_time', "更新时间"), key: "update_time", sort: true},
    ];

    $scope.wizardDeleteColumns = [
        {value: $filter('translator')('i18n_name', "名称"), key: "sync_org_name", sort: false},
        {value: $filter('translator')('i18n_code', "编码"), key: "sync_org_code", sort: false},
        {value: $filter('translator')('i18n_remark', "描述"), key: "remark", sort: false},
    ];

    if (AuthService.hasPermissions("SYNC_ORG_USER:READ+EDIT")) {
        $scope.columns.push({value: "", default: true, sort: false});
        $scope.columns.unshift($scope.first);
    }

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
        HttpUtils.paging($scope, "sync/setting/org/list", condition, function (res) {
        });
    };

    $scope.list();

    $scope.add = function () {
        $scope.syncOrg = {};
        $scope.edit = false;
        $scope.formUrl = "project/html/syncsetting/sync-org-edit.html";
        $scope.toggleForm($scope.formUrl);
        $scope.show = true;
    };

    $scope.editSyncOrg = function (item) {
        $scope.syncOrg = angular.copy(item);
        $scope.edit = true;
        $scope.formUrl = "project/html/syncsetting/sync-org-edit.html";
        $scope.toggleForm($scope.formUrl, {
            edit: true,
            show: true
        });
        $scope.show = true;
    };

    // 批量删除功能
    $scope.batchDelete = function () {
        let deleteItems = $scope.items.filter(item => item.enable);
        if (deleteItems.length < 1) {
            Notification.warn($filter('translator')('i18n_please_choose_data', '请选择数据'));
            return;
        }
        $scope.deleteItems = deleteItems;
        $scope.formUrl = "project/html/syncsetting/sync-org-delete.html";
        $scope.toggleForm($scope.formUrl, {
            deleteItems: $scope.deleteItems
        });
    };

    $scope.delete = function (item) {
        Notification.confirm($filter('translator')('i18n_will_delete_sync_org', '确认删除同步机构') + '，' + item.syncOrgName + '?', function () {
            $scope.loadingLayer = HttpUtils.get('sync/setting/org/delete/' + item.id, function (response) {
                if (response.success) {
                    $scope.list();
                    Notification.success($filter('translator')('i18n_delete_success', '删除成功'));
                }
            });
        });
    };

    $scope.enableSyncOrg = function (item) {
        $scope.loadingLayer = HttpUtils.get('sync/setting/org/enable/' + item.id, function (response) {
            if (response.success) {
                $scope.list();
                Notification.success($filter('translator')('i18n_operation_success', '操作成功'));
            }
        });
    };

    $scope.closeToggleForm = function () {
        $scope.syncOrg = {};
        $scope.toggleForm();
    };

    $scope.closeDeleteDialog = function () {
        $scope.deleteItems = [];
        $scope.toggleForm();
    };

    $scope.deleteSyncOrg = function () {
        let ids = $scope.deleteItems.map(item => item.id);
        $scope.apiAccountDeleteLoadingLayer = HttpUtils.post('sync/setting/org/batchDelete', ids, function (response) {
            if (response.success) {
                Notification.success($filter('translator')('i18n_delete_success', '删除成功'));
                $scope.list();
                $scope.closeToggleForm();
                $scope.show = false;
            }
        });
    };

    $scope.closeSyncOrgEdit = function () {
        $scope.closeToggleForm();
        $scope.show = false;
    };

    $scope.submitSyncOrgEdit = function (){
        let url = 'sync/setting/org/add';
        if ($scope.syncOrg.id) {
            url = 'sync/setting/org/update'
        }

        let syncOrg = {};
        angular.copy($scope.syncOrg, syncOrg);
        //处理备注是否必填
        $scope.syncOrgEditLoadingLayer = HttpUtils.post(url, syncOrg, function () {
            Notification.success($filter('translator')('i18n_save_success', '保存成功'));
            $scope.closeToggleForm();
            $scope.list();
            $scope.show = false;
        });
    };
});


ProjectApp.controller('SyncUserController', function ($scope, HttpUtils, $state, Notification, FilterSearch, AuthService, eyeService, $filter) {
    $scope.conditions = [
        {
            key: "syncUserName",
            name: $filter('translator')('i18n_name', "名称"),
            directive: "filter-contains",
        },
        {
            key: "syncUserCode",
            name: $filter('translator')('i18n_code', "编码"),
            directive: "filter-contains",
        },
        {
            key: "email",
            name: $filter('translator')('i18n_email', "邮箱"),
            directive: "filter-contains",
        },
        {
            key: "orgId",
            name: $filter('translator')('i18n_org', "机构"),
            directive: "filter-select-virtual",
            search: true,
            url: "sync/setting/org/list",
            convert: {value: "value", label: "lable"}
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
        {value: $filter('translator')('i18n_name', "名称"), key: "sync_user_name", sort: false},
        {value: $filter('translator')('i18n_code', "编码"), key: "sync_user_code", sort: false},
        {value: $filter('translator')('i18n_email', "邮箱"), key: "email", sort: false},
        {value: $filter('translator')('i18n_org', "机构"), key: "organization", sort: false},
        {value: $filter('translator')('i18n_remark', "描述"), key: "remark", sort: false},
        {value: $filter('translator')('i18n_status', "状态"), key: "is_sync", sort: false},
        {value: $filter('translator')('i18n_create_time', "创建时间"), key: "create_time", sort: true},
        {value: $filter('translator')('i18n_update_time', "更新时间"), key: "update_time", sort: true},
    ];

    $scope.wizardDeleteColumns = [
        {value: $filter('translator')('i18n_name', "名称"), key: "sync_user_name", sort: false},
        {value: $filter('translator')('i18n_code', "编码"), key: "sync_user_code", sort: false},
        {value: $filter('translator')('i18n_remark', "描述"), key: "remark", sort: false},
    ];

    if (AuthService.hasPermissions("SYNC_ORG_USER:READ+EDIT")) {
        $scope.columns.push({value: "", default: true, sort: false});
        $scope.columns.unshift($scope.first);
    }

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
        HttpUtils.paging($scope, "sync/setting/user/list", condition, function (res) {
        });
    };

    $scope.list();

    $scope.add = function () {
        $scope.syncUser = {};
        $scope.edit = false;
        $scope.formUrl = "project/html/syncsetting/sync-user-edit.html";
        $scope.toggleForm($scope.formUrl);
        $scope.show = true;
    };

    $scope.editSyncUser = function (item) {
        $scope.syncUser = angular.copy(item);
        $scope.edit = true;
        $scope.formUrl = "project/html/syncsetting/sync-user-edit.html";
        $scope.toggleForm($scope.formUrl, {
            edit: true,
            show: true
        });
        $scope.show = true;
    };

    // 批量删除功能
    $scope.batchDelete = function () {
        let deleteItems = $scope.items.filter(item => item.enable);
        if (deleteItems.length < 1) {
            Notification.warn($filter('translator')('i18n_please_choose_data', '请选择数据'));
            return;
        }
        $scope.deleteItems = deleteItems;
        $scope.formUrl = "project/html/syncsetting/sync-user-delete.html";
        $scope.toggleForm($scope.formUrl, {
            deleteItems: $scope.deleteItems
        });
    };

    $scope.delete = function (item) {
        Notification.confirm($filter('translator')('i18n_will_delete_sync_user', '确认删除同步用户') + '，' + item.syncUserName + '?', function () {
            $scope.loadingLayer = HttpUtils.get('sync/setting/user/delete/' + item.id, function (response) {
                if (response.success) {
                    $scope.list();
                    Notification.success($filter('translator')('i18n_delete_success', '删除成功'));
                }
            });
        });
    };

    $scope.enableSyncUser = function (item) {
        $scope.loadingLayer = HttpUtils.get('sync/setting/user/enable/' + item.id, function (response) {
            if (response.success) {
                $scope.list();
                Notification.success($filter('translator')('i18n_operation_success', '操作成功'));
            }
        });
    };

    $scope.closeToggleForm = function () {
        $scope.syncUser = {};
        $scope.toggleForm();
    };

    $scope.closeDeleteDialog = function () {
        $scope.deleteItems = [];
        $scope.toggleForm();
    };

    $scope.deleteSyncUser = function () {
        let ids = $scope.deleteItems.map(item => item.id);
        $scope.apiAccountDeleteLoadingLayer = HttpUtils.post('sync/setting/user/batchDelete', ids, function (response) {
            if (response.success) {
                Notification.success($filter('translator')('i18n_delete_success', '删除成功'));
                $scope.list();
                $scope.closeToggleForm();
                $scope.show = false;
            }
        });
    };

    $scope.closeSyncUserEdit = function () {
        $scope.closeToggleForm();
        $scope.show = false;
    };

    $scope.submitSyncUserEdit = function (){
        let url = 'sync/setting/user/add';
        if ($scope.syncUser.id) {
            url = 'sync/setting/user/update'
        }

        let syncUser = {};
        angular.copy($scope.syncUser, syncUser);
        //处理备注是否必填
        $scope.syncUserEditLoadingLayer = HttpUtils.post(url, syncUser, function () {
            Notification.success($filter('translator')('i18n_save_success', '保存成功'));
            $scope.closeToggleForm();
            $scope.list();
            $scope.show = false;
        });
    };
});
