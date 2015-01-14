angular.module('crowdsource')

    .directive('mydatepicker', function () {
        return {
            require: 'ngModel',
            link: function (scope, elem, attrs, ngModel) {

                var nowTemp = new Date();
                var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

                $(elem).fdatepicker({
                    format: "dd.mm.yyyy",

                    onRender: function (date) {
                        return date.valueOf() < now.valueOf() ? 'disabled' : '';
                    }

                }).on('changeDate', function (ev) {
                    ngModel.$setViewValue(ev.date.valueOf());
                    ngModel.$render();
                });
            }
        }
    });