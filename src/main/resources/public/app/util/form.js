angular.module('crowdsource')

    .factory('FormUtils', function () {
        var REMOTE_RULE_PREFIX = 'remote_';

        return {
            /**
             * The server response is expected to be
             * {
             *   "fieldViolations":
             *   {
             *     "email": "inactivated",
             *     "name": "unique",
             *     ...
             *   }
             * }
             *
             * For every key-value pair in "fieldViolations", the
             * validity of the form field is set to false:
             *
             *   form.email.$setValidity("inactivated", false);
             *   form.name.$setValidity("unique", false);
             *
             *
             * @param form The form to apply the field validities to
             * @param response The server's response
             * @returns {boolean} if at least one field's validity was set to false
             */
            applyServerErrorResponse: function (form, response) {
                var appliedErrors = false;

                if (response.data && response.data.fieldViolations) {
                    angular.forEach(response.data.fieldViolations, function (rule, field) {
                        if (form[field]) {
                            form[field].$setValidity(REMOTE_RULE_PREFIX + rule, false);

                            appliedErrors = true;
                        }
                    });
                }

                return appliedErrors;
            },
            isRemoteRule: function(rule) {
                return rule.indexOf(REMOTE_RULE_PREFIX) === 0;
            }
        };
    })

    .directive('resetRemoteValidation', function (FormUtils) {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, elem, attrs) {
                var form = $(elem).closest('form');
                if (!form) {
                    return;
                }
                var formName = form.attr('name');
                var elemName = attrs.name;

                scope.$watch(attrs.ngModel, function () {
                    var elemScope = scope[formName][elemName];

                    // iterate over ever error of the field
                    angular.forEach(elemScope.$error, function (valid, rule) {
                        if (FormUtils.isRemoteRule(rule)) {
                            // set the validity to true if it is a remote validation error when the value changes
                            elemScope.$setValidity(rule, true);
                        }
                    });
                });
            }
        }
    });