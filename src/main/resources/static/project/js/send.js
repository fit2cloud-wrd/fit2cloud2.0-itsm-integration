ProjectApp.controller('SendController', function($scope){

    $scope.columns = [
        {key: "process_id", value: 'CMP 流程 ID', checked: true},
        {key: "module", value: '模块 ID', checked: true},
        {key: "business_key", value: '订单号', checked: true},
        {key: "resource_type", value: '资源类型', checked: true},
        {key: "external_process_id", value: '外部系统流程 ID', checked: true}
    ];
    $scope.conditions = [
        {
            key: "businessKey",
            name: '订单号',
            directive: "filter-contains"
        },
        {key: "processId", name: '流程ID', directive: "filter-input"},
        {
            key: "externalProcessId",
            name: '外部系统流程ID',
            directive: "filter-contains"
        },
        {
            key: "resourceType",
            name: '资源类型',
            directive: "filter-contains"
        },
        {
            key: "module",
            name: '模块',
            directive: "filter-select"
        }
    ];
    $scope.filters = [];

    $scope.list = function (sortObj) {
        let condition = FilterSearch.convert($scope.filters);
        if (sortObj) {
            $scope.sort = sortObj;
        }
        if ($scope.sort) {
            condition.sort = $scope.sort.sql;
        }
        condition = condition ? condition : {};
        HttpUtils.paging($scope, 'pci/process/list', condition)
    };
});