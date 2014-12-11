angular.module('crowdsource')

    .value('emailDomain', '@axelspringer.de')

    .directive('emailFormGroup', function(emailDomain) {
        return {
            restrict: 'E',
            require: '^form',
            scope: {
                model: '=',
                fieldName: '@'
            },
            templateUrl: 'app/components/form-group/email/email-form-group.html',
            link: function(scope, element, attributes, form) {
                scope.form = form;
                scope.EMAIL_DOMAIN = emailDomain;
            }
        };
    })

    .directive('localEmailPartInput', function(emailDomain) {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attributes, ngModel) {
                ngModel.$parsers.push(function(value) {
                    if (value) {
                        return value + emailDomain;
                    }
                    return value;
                });
                ngModel.$formatters.push(function(value) {
                    if (value) {
                        return value.substr(0, value.indexOf(emailDomain));
                    }
                    return value;
                });
            }
        };
    })

    .directive('nonExternalEmail', function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.non_external_email = function (modelValue) {
                    return !modelValue || modelValue.indexOf('_extern') < 0;
                }
            }
        };
    });