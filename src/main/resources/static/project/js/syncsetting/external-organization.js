ProjectApp.controller('OrganizationController', function ($scope, HttpUtils, FilterSearch, $http, Notification, operationArr, $state, eyeService, Translator, $filter, $mdPanel, $interval) {
    /** 公共参数、方法 **/
    // 外部系统顶级组织ID
    const ROOT_ORG_ID = "16f7ecc0-48ca-4e46-894b-ed0d8f998dfe";
    $scope.org = ROOT_ORG_ID;//左侧树选中的组织id

    $scope.orgParent = "0";//左侧树选中的组织的父组织ID
    $scope.orgObj = {};//左侧树选中的组织
    $scope.orgObjOld = {};//左侧树选中的组织备份
    $scope.orgAdminIds = [];//左侧树选中的组织备的组织管理员ids
    $scope.orgAdminIdsOld = [];//左侧树选中的组织备的组织管理员ids
    $scope.batchAddToSyncOrgItems = [];

    $scope.editLoadingLayer;//右侧弹出框loading
    $scope.$on('editLoadingLayer', function (e, data) {
        $scope.editLoadingLayer = data;
    });
    $scope._loadingLayer = [];
    $scope._loadingLayer.add = function (item) {
        $scope._loadingLayer.push(item);
        $scope.allLoadingLayer = $scope._loadingLayer.concat([]);
    };

    $scope.$on('loadingLayer', function (e, data) {
        $scope._loadingLayer.add(data);
    });

    $scope.$on('sourceType', function (e, data) {
        $scope.sourceType = data;
    });

    $scope.$on('formUrl', function (e, data) {
        $scope.formUrl = data;
    });
    $scope.$on('infoUrl', function (e, data) {
        $scope.infoUrl = data;
    });

    $scope.$on('list', function (e, data) {
        $scope.allList();
    });

    $scope.getStatus = function () {
        HttpUtils.get("api/log/syncAll/status", function (resp) {
            if (resp && resp.data === 'inPush') {
                $scope.isSync = true;
            } else {
                $scope.isSync = false;
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

    $scope.tab = 0;
    $scope.changeTab = function (tab) {
        $scope.tab = tab;
    }
    $scope.$watch("orgParent", function () {
        //当工作空间不显示，且tab在租户tab上，则往前移动一个tab
        if ($scope.orgParent == 0 && $scope.tab === 3) {
            $scope.tab = 2;
        }
    })

    $scope.allList = function () {
        $scope.$broadcast('orgList', {});
        $scope.$broadcast('orgUserList', {});
    }

    $scope.closeToggleForm = function () {
        $scope.toggleForm();
        $scope.item = {};
        $scope.resetItem = {};
    };
    $scope.closeInformation = function () {
        $scope.item = {};
        $scope.toggleInfoForm(false);
    };

    $scope.onOrgNameChange = function () {
        if ($scope.item.name) {
            $scope.item.workspaceName = $scope.item.name + Translator.get("i18n_workspace_list");
        } else {
            $scope.item.workspaceName = '';
        }
    };

    $scope.getStatus();
    /** 左侧组织树 **/
    $scope.treeData = {};
    $scope.noroot = {};
    $scope.onChangeTree = function (node) {
        $scope.org = node.id;
        $scope._loadingLayer.add(
            HttpUtils.get("external/system/orgTree/" + $scope.org, function (rep) {
                // $scope.setTree(rep.data, "collapsed", false);
                $scope.treeData = rep.data;
                $scope.noroot.selected = $scope.org;
            })
        );
        $scope._loadingLayer.add(
            HttpUtils.get("external/system/organization/" + $scope.org, function (rep) {
                $scope.orgParent = rep.data.parentId;
                $scope.orgObj = rep.data;
                $scope.orgObjOld = rep.data;
                $scope.allList();
            })
        );
    };
    $scope.$on('onChangeTree', function (e, data) {
        $scope.onChangeTree(data);
    });
    $scope.noroot = {
        onChange: function (node) {
            $scope.onChangeTree(node);
        }
    };
    $scope.hasChildren = function (node) {
        return angular.isArray(node.children) && node.children.length > 0;
    };
    $scope.setTree = function (tree, key, value) {
        for (let i = 0; i < tree.length; i++) {
            let node = tree[i];
            node[key] = value;
            if ($scope.hasChildren(node)) {
                $scope.setTree(node.children, key, value);
            }
        }
    }

    $scope.getTopOrg = function (callback) {
        HttpUtils.get("external/system/getTopOrg", function (rep) {
            if (rep.data) {
                $scope.org = rep.data;
            }
            callback(true);
        })
    };

    $scope.initOrg = function () {
        $scope.allList();
        $scope.getTopOrg(function (res) {
            if (res) {
                $scope._loadingLayer.add(
                    HttpUtils.get("external/system/orgTree/" + $scope.org, function (rep) {
                        $scope.treeData = rep.data;
                        $scope.noroot.selected = $scope.org;
                    })
                );
                $scope._loadingLayer.add(
                    HttpUtils.get("external/system/organization/" + $scope.org, function (rep) {
                        if (rep.data) {
                            $scope.orgParent = rep.data.parentId;
                            $scope.orgObj = rep.data;
                            $scope.orgObjOld = rep.data;

                            let roleList = $scope.orgObj.orgAdminList;
                            $scope.orgAdminList = {};
                            if (roleList && roleList.length > 0) {
                                roleList.forEach(function (orgAdmin) {
                                    if ($scope.orgAdminList[orgAdmin.roleName]) {
                                        $scope.orgAdminList[orgAdmin.roleName].orgAdminList.push(orgAdmin);
                                        $scope.orgAdminList[orgAdmin.roleName].orgAdminIds.push(orgAdmin.id);
                                        $scope.orgAdminList[orgAdmin.roleName].orgAdminNames += (($scope.orgAdminList[orgAdmin.roleName].orgAdminNames != "" ? "," : "") + orgAdmin.name + "(" + orgAdmin.id + ")");
                                    } else {
                                        if (orgAdmin.id) {
                                            $scope.orgAdminList[orgAdmin.roleName] = {
                                                orgAdminList: [orgAdmin],
                                                orgAdminIds: [orgAdmin.id],
                                                orgAdminNames: orgAdmin.name + "(" + orgAdmin.id + ")"
                                            };
                                        } else {
                                            $scope.orgAdminList[orgAdmin.roleName] = {
                                                orgAdminList: [],
                                                orgAdminIds: [],
                                                orgAdminNames: Translator.get("i18n_none")
                                            };
                                        }
                                    }
                                    $scope.orgAdminList[orgAdmin.roleName].orgAdminIdsOld = angular.copy($scope.orgAdminList[orgAdmin.roleName].orgAdminIds);
                                });
                            }
                        }
                    })
                );
            }
        });
    }
    $scope.initOrg();
    $scope.$on('initOrg', function (e, data) {
        $scope.initOrg();
    });

    /** 基本信息 **/
    $scope.userList;
    $scope.allUserList;

    $scope.initUserList = function () {
        $scope._loadingLayer.add(
            HttpUtils.post('external/system/allUsers', {}, function (rep) {
                $scope.allUserList = rep.data;
            }, function (rep) {
                Notification.danger(rep.data.message);
            })
        );
    };
    $scope.initUserList();

    $scope.optionChange = function (data) {
        $scope.orgAdminIds = data;
    };

    /** 下级组织 **/
    $scope.item;
    $scope.orgs;
    $scope.orgList;
    $scope.orgIdNameMap;
    $scope.$on('item', function (e, data) {
        $scope.item = data;
    });
    $scope.$on('orgList', function (e, data) {
        $scope.orgs = data.orgs;
        $scope.orgList = data.orgList;
        $scope.orgIdNameMap = data.orgIdNameMap;
    });


    $scope.closeAddSyncOrgDialog = function () {
        console.log('111');
        $scope.$broadcast('closeAddSyncOrgDialog', {});
    };

    $scope.submitAddSyncOrg = function () {
        console.log('222');
        $scope.$broadcast('submitAddSyncOrg', {});
    };

    /** 用户 **/
    $scope.resetItem;
    $scope.$on('resetItem', function (e, data) {
        $scope.resetItem = data;
    });
    $scope.ids;
    $scope.$on('ids', function (e, data) {
        $scope.ids = data;
    });
    $scope.select;
    $scope.$on('select', function (e, data) {
        $scope.select = data;
    });
    $scope.roles;
    $scope.$on('roles', function (e, data) {
        $scope.roles = data;
    });
    $scope.workspaces;
    $scope.workspaceList;
    $scope.$on('workspaceList', function (e, data) {
        $scope.workspaces = data.workspaces;
        $scope.workspaceList = data.workspaceList;
    });

    // 全量同步
    $scope.syncAll = function () {
        let url = "api/log/syncAll";
        HttpUtils.get(url, function (resp) {
            if (resp.success) {
                Notification.success($filter('translator')('i18n_sync_finished', '同步完成'));
            } else {
                Notification.info(result.data.message);
            }
            $scope.isSync = false;
            HttpUtils.get("external/system/getTopOrg", function (rep) {
                if (rep.data) {
                    $scope.org = rep.data;
                }
                $scope.initOrg();
            });
        }, function (resp) {
            Notification.danger($filter('translator')('i18n_sync_failed', '同步失败'));
            $scope.isSync = false;
            HttpUtils.get("external/system/getTopOrg", function (rep) {
                if (rep.data) {
                    $scope.org = rep.data;
                }
                $scope.initOrg();
            });
        });

        $scope.isSync = true;
        $scope.checkStatus();
    };

    $scope.canEditOrg = function (user, orgList) {
        if ($scope.orgIdNameMap[user.orgId]) return true;
        if (user.orgName === "-") return true;
        return false;
    }
    $scope.view = function (password, eye) {
        eyeService.view("#" + password, "#" + eye);
    };

    $scope.addToSyncOrg = function () {
        Notification.confirm($filter('translator')('i18n_will_add_to_sync_setting', '确认将机构添加到同步设置') + '?', function () {
            $scope.loadingLayer = HttpUtils.get('external/system/org/addToSyncSetting/' + $scope.org, function (response) {
                if (response.success) {
                    Notification.success($filter('translator')('i18n_operation_success', '操作成功'));
                }
            });
        });
    };
});

ProjectApp.controller('OrganizationListController', function ($scope, $rootScope, $mdDialog, HttpUtils, FilterSearch, AuthService, $http, Notification, operationArr, $state, Translator, $filter) {
    $scope.$on('orgList', function (e, data) {
        $scope.list();
    });

    $scope.conditions = [
        {
            key: "name",
            name: $filter('translator')('i18n_org_name', "机构名称"),
            directive: "filter-contains",
        },
        {
            key: "id",
            name: $filter('translator')('i18n_org_code', "机构编码"),
            directive: "filter-contains",
        },
        {
            key: "sync",
            name: $filter('translator')('i18n_is_sync', '是否同步'),
            directive: "filter-select",
            selects: [
                {value: true, label: $filter('translator')('i18n_yes', "是")},
                {value: false, label: $filter('translator')('i18n_no', "否")}
            ]
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
                $scope.singleClick(checked, item, true);
            });
        },
        width: "40px"
    };

    $scope.columns = [
        $scope.first,
        {value: $filter('translator')('i18n_org_code', "机构编码"), key: "name", sort: false},
        {value: $filter('translator')('i18n_org_name', "机构名称"), key: "name", sort: false},
        {value: $filter('translator')('i18n_is_sync', "是否同步"), key: "is_sync", sort: false},
        {value: $filter('translator')('i18n_organization_desc', "描述"), key: "description"},
        {value: $filter('translator')('i18n_organization_child_organ', "下级组织"), key: "countOrg"},// 不想排序的列，用sort: false
        {value: $filter('translator')('i18n_organization_member', "成员"), key: "countUser"},
    ];

    $scope.wizardDeleteColumns = [
        {value: $filter('translator')('i18n_org_name', "机构名称"), key: "name", sort: false},
        {value: $filter('translator')('i18n_org_code', "机构编码"), key: "id", sort: false},
        {value: $filter('translator')('i18n_parent_org_name', "上级机构名称"), key: "parent_name", sort: false},
        {value: $filter('translator')('i18n_parent_org_code', "上级机构编码"), key: "parent_id", sort: false},
    ];

    if (AuthService.hasPermissions("EXTERNAL_ORGANIZATION:READ+EDIT")) {
        $scope.columns.push({value: "", default: true, sort: false});
    }

    // 用于传入后台的参数
    $scope.filters = [];
    $scope.ids = [];


    $scope.clickChecked = function (checked, item, isSelectAll) {
        $scope.singleClick(checked, item, isSelectAll);
    };

    $scope.singleClick = function (checked, item, isSelectAll) {
        if (checked === true) {
            $scope.ids.push(item.id);
        } else {
            if (isSelectAll) {
                $scope.ids = [];
            } else {
                operationArr.removeByValue($scope.ids, item.id);
                if ($scope.ids.length === 0) {
                    $scope.first.checkValue = false;
                }
            }
        }
    };

    $scope.list = function (sortObj) {
        var condition = FilterSearch.convert($scope.filters);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        condition.parentId = $scope.org;
        // 保留排序条件，用于分r页
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }
        HttpUtils.paging($scope, "external/system/org/list", condition, function () {
            angular.forEach($scope.items, function (item) {
                item.enable = false;
            });
        });
        $scope.$emit('loadingLayer', $scope.loadingLayer);
    };

    $scope.acquisitionConditions = function () {
        HttpUtils.get("external/system/orgTreeList", function (rep) {
            $scope.orgs = rep.data;
            if (!$scope.orgs || $scope.orgs.length < 1) {
                Notification.confirm(Translator.get("i18n_organ_init_error_info"), function () {
                    $state.go("organization", {});
                })
            }
            let list = [];
            for (let k = 0; k < rep.data.length; k++) {
                list.push({
                    id: rep.data[k].id,
                    name: rep.data[k].name,
                    showText: rep.data[k].showText
                });
            }
            $scope.orgList = list;
            $scope.$emit('orgList', {"orgList": $scope.orgList, "orgs": $scope.orgs});
        });
    };

    $scope.closeToggleForm = function () {
        $scope.toggleForm();
        $scope.item = {};
        $scope.$emit('item', $scope.item);
    };
    $scope.linkOrg = function (item) {
        $scope.$emit('onChangeTree', item);
    };

    $scope.linkUser = function (item) {
        if ($scope.selected === item.$$hashKey) {
            $scope.closeInformation();
        }
        $scope.selected = item.$$hashKey;
        $scope.showOrorgId = item.id;
        $scope.sourceType = "org";
        $scope.infoUrl = 'project/html/organization/organization-authorize.html' + '?_t=' + moment().valueOf();
        $scope.$emit('infoUrl', $scope.infoUrl);
        $scope.$emit('showOrorgId', $scope.showOrorgId);
        $scope.$emit('sourceType', $scope.sourceType);
        $scope.toggleInfoForm(true);
    };

    $scope.closeInformation = function () {
        $scope.item = {};
        $scope.$emit('item', $scope.item);
        $scope.selected = "";
        $scope.toggleInfoForm(false);
    };


    /** 将用户批量分配到当前组织下 **/
    $scope.batchAddToSync = function () {
        let batchAddToSyncItems = $scope.items.filter(item => item.enable);
        if (batchAddToSyncItems.length < 1) {
            Notification.warn($filter('translator')('i18n_please_choose_data', '请选择数据'));
            return;
        }
        $scope.batchAddToSyncOrgItems = batchAddToSyncItems;
        $scope.formUrl = 'project/html/syncsetting/external-org-add-to-sync.html' + '?_t=' + moment().valueOf();
        $scope.$emit('formUrl', $scope.formUrl);
        $scope.toggleForm();
    };

    $scope.$on('closeAddSyncOrgDialog', function (e, data) {
        $scope.closeAddSyncOrgDialog();
    });
    $scope.$on('submitAddSyncOrg', function (e, data) {
        $scope.submitAddSyncOrg();
    });

    $scope.batchAddToSyncOrg = function () {
        let batchAddToSyncItems = $scope.items.filter(item => item.enable);
        if (batchAddToSyncItems.length < 1) {
            Notification.warn($filter('translator')('i18n_please_choose_data', '请选择数据'));
            return;
        }
        $scope.batchAddToSyncOrgItems = batchAddToSyncItems;
        Notification.confirm($filter('translator')('i18n_will_add_to_sync_setting', '确认将机构添加到同步设置') + '?', function () {
            $scope.submitAddSyncOrg();
        });

        // $scope.formUrl = "project/html/syncsetting/external-org-add-to-sync.html";
        // $scope.$emit('formUrl', $scope.formUrl);
        // $scope.toggleForm();
        // $scope.toggleForm($scope.formUrl, {
        //     batchAddToSyncOrgItems: $scope.batchAddToSyncOrgItems
        // });
    };

    $scope.addToSync = function (item) {
        if (item.sync && item.sync == true) {
            Notification.warn($filter('translator')('i18n_cur_org_already_exist_in_org_setting', '当前机构已添加同步设置'));
            return;
        }
        Notification.confirm($filter('translator')('i18n_will_add_to_sync_setting', '确认将机构添加到同步设置') + '，' + item.name + '?', function () {
            $scope.loadingLayer = HttpUtils.get('external/system/org/addToSyncSetting/' + item.id, function (response) {
                if (response.success) {
                    $scope.list();
                    Notification.success($filter('translator')('i18n_operation_success', '操作成功'));
                }
            });
        });
    };

    $scope.closeAddSyncOrgDialog = function () {
        $scope.batchAddToSyncOrgItems = [];
        $scope.toggleForm();
    };

    $scope.submitAddSyncOrg = function () {
        let ids = $scope.batchAddToSyncOrgItems.map(item => item.id);
        $scope.apiAccountDeleteLoadingLayer = HttpUtils.post('external/system/org/batchAddToSyncSetting', ids, function (response) {
            if (response.success) {
                Notification.success($filter('translator')('i18n_operation_success', '操作成功'));
                $scope.list();
                // $scope.closeAddSyncOrgDialog();
            }
        });
    };
});

ProjectApp.controller('OrgUserController', function ($scope, HttpUtils, FilterSearch, $http, Notification, operationArr, eyeService, $state, $filter, $stateParams, ProhibitPrompts, UserService, AuthService, Loading, Translator, $mdDialog) {

    $scope.$on('orgUserList', function (e, data) {
        $scope.list();
    });

    $scope.conditions = [
        {
            key: "name",
            name: $filter('translator')('i18n_name', "名称"),
            directive: "filter-contains",
        },
        {
            key: "id",
            name: $filter('translator')('i18n_code', "编码"),
            directive: "filter-contains",
        },
        {
            key: "sync",
            name: $filter('translator')('i18n_is_sync', '是否同步'),
            directive: "filter-select",
            selects: [
                {value: true, label: $filter('translator')('i18n_yes', "是")},
                {value: false, label: $filter('translator')('i18n_no', "否")}
            ]
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
        {value: $filter('translator')('i18n_name', "名称"), key: "name", sort: false},
        {value: $filter('translator')('i18n_code', "编码"), key: "id", sort: false},
        {value: $filter('translator')('i18n_org_name', "机构名称"), key: "org_name", sort: false},
        {value: $filter('translator')('i18n_org_code', "机构编码"), key: "org_id", sort: false},
        {value: $filter('translator')('i18n_sex', "性别"), key: "sex", sort: false},
        {value: $filter('translator')('i18n_phone_number', "手机号"), key: "phone_number", sort: false},
        {value: $filter('translator')('i18n_email', "电子邮箱"), key: "email", sort: false},
        {value: $filter('translator')('i18n_is_sync', "是否同步"), key: "is_sync", sort: false},
    ];

    $scope.wizardDeleteColumns = [
        {value: $filter('translator')('i18n_name', "名称"), key: "sync_user_name", sort: false},
        {value: $filter('translator')('i18n_code', "编码"), key: "sync_user_code", sort: false},
    ];

    if (AuthService.hasPermissions("EXTERNAL_ORGANIZATION:READ+EDIT")) {
        $scope.columns.push({value: "", default: true});
        $scope.columns.unshift($scope.first);
    }

    // 用于传入后台的参数
    $scope.filters = [];

    $scope.list = function (sortObj) {
        const condition = FilterSearch.convert($scope.filters);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        if ($scope.org) {
            condition.orgId = $scope.org;
        }
        // 保留排序条件，用于分页
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }
        HttpUtils.paging($scope, "external/system/user/list", condition, function () {
        });
        $scope.$emit('loadingLayer', $scope.loadingLayer);
    };

    /** 编辑 **/
    $scope.getParentId = function (roleId) {
        let parentId = null;
        angular.forEach($scope.roles, function (role) {
            if (role.id === roleId) {
                if (role.type !== 'System') {
                    parentId = role.parentId;
                } else {
                    parentId = roleId;
                }
            }
        });
        return parentId;
    };

    $scope.batchAddToSync = function () {
        let batchAddToSyncItems = $scope.items.filter(item => item.enable);
        if (batchAddToSyncItems.length < 1) {
            Notification.warn($filter('translator')('i18n_please_choose_data', '请选择数据'));
            return;
        }
        $scope.batchAddToSyncItems = batchAddToSyncItems;
        Notification.confirm($filter('translator')('i18n_will_add_to_sync_user_setting', '确认将用户添加到同步设置') + '?', function () {
            $scope.submitAddSync();
        });
        // $scope.formUrl = "project/html/syncsetting/external-user-add-to-sync.html";
        // $scope.toggleForm($scope.formUrl, {
        //     batchAddToSyncItems: $scope.batchAddToSyncItems
        // });
    };

    $scope.addToSync = function (item) {
        if (item.sync && item.sync == true) {
            Notification.warn($filter('translator')('i18n_cur_user_already_exist_in_org_setting', '当前用户已添加同步设置'));
            return;
        }
        Notification.confirm($filter('translator')('i18n_will_add_to_sync_user_setting', '确认将用户添加到同步设置') + '，' + item.name + '?', function () {
            $scope.loadingLayer = HttpUtils.get('external/system/user/addToSyncSetting/' + item.id, function (response) {
                if (response.success) {
                    $scope.list();
                    Notification.success($filter('translator')('i18n_operation_success', '操作成功'));
                }
            });
        });
    };

    $scope.closeAddSyncDialog = function () {
        $scope.batchAddToSyncItems = [];
        $scope.toggleForm();
    };

    $scope.submitAddSync = function () {
        let ids = $scope.batchAddToSyncItems.map(item => item.id);
        $scope.apiAccountDeleteLoadingLayer = HttpUtils.post('external/system/user/batchAddToSyncSetting', ids, function (response) {
            if (response.success) {
                Notification.success($filter('translator')('i18n_operation_success', '操作成功'));
                $scope.list();
                // $scope.closeAddSyncDialog();
            }
        });
    };

});
