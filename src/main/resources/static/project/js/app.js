/**
 * 启动app，加载菜单
 */

// 流程管理使用方法：1、加载process-design.css和process-design.js，加载f2c.process，配置module.json
var ProjectApp = angular.module('ProjectApp', ['f2c.common', 'f2c.process']);

ProjectApp.controller('IndexCtrl', function(){
    //      $scope.menus = [];
});


// trust as html for table content html
ProjectApp.filter("toTrusted", ['$sce', function ($sce) {
    return function (text) {
        if (text) {
            return $sce.trustAs($sce.HTML, text);
        } else {
            return null;
        }
    }
}]).filter("UnreadCountFilter", function () {
    return function (count) {
        if (count > 99) {
            return "···";
        } else {
            return count;
        }
    }
}).filter("DateFormatBetween", ['$filter',function ($filter) {
    let oneDayMills = 24 * 3600 * 1000;
    let oneHourMills =  3600 * 1000;
    return function (mills) {
        if (mills && mills > 0) {
            let duration = moment().valueOf() - mills;
            let day = Math.floor(duration / oneDayMills);
            let hour = Math.ceil((duration % oneDayMills) / oneHourMills);
            return day + " " + $filter("translator")("i18n_day") + " " + hour + " " + $filter("translator")("i18n_hour");
        }
    };
}]);