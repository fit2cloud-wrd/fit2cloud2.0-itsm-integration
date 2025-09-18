ProjectApp.service('operationArr', function () {
    this.removeByValue = function (arr, val) {
        for (let i = 0; i < arr.length; i++) {
            if (arr[i] === val) {
                arr.splice(i, 1);
                break;
            }
        }
    };
});

ProjectApp.service('ProhibitPrompts', function () {
    this.changeType = function (elementId) {
        let elementIdSelect = "#" + elementId;
        let element = angular.element(elementIdSelect);
        if (element[0].type === 'text') {
            element[0].type = SERVICE_RESOURCE_KEY["SECRET_KEY"];
        }
    };
});