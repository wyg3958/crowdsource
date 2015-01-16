angular.module('crowdsource')

    .directive('datepicker', function () {
        return {
            require: 'ngModel',
            link: function (scope, elem, attrs, ngModel) {

                //allow only dates starting from tomorrow
                var nowTemp = new Date();
                var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate() + 1, 0, 0, 0, 0);

                $(elem).fdatepicker({
                    format: "dd.mm.yyyy",

                    onRender: function (date) {
                        return date.valueOf() < now.valueOf() ? 'disabled' : '';
                    }
                });

                ngModel.$parsers.push(function (stringValue) {
                    if (stringValue) {
                        var parts = stringValue.split('.');
                        console.log(parts);

                        var date = new Date(Date.UTC(parseInt(parts[2]), parseInt(parts[1]) - 1, parseInt(parts[0]), 0, 0, 0));
                        console.log(date);
                        return date;
                    }
                    return null;
                });
                ngModel.$formatters.push(function (date) {
                    if (date) {
                        return '' + date.getDate() + '.' + (date.getMonth() + 1) + '.' + date.getFullYear();
                    }
                    return '';
                });
            }
        }
    });