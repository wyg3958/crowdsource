angular.module('crowdsource')

    .factory('ValidationUtils', function() {

        var shouldShowValidationError = function (form, fieldName) {
            if (!form[fieldName]) {
                return false;
            }

            var userInteracted = form.$submitted || form[fieldName].$dirty;
            return (userInteracted && form[fieldName].$invalid);
        };

        return {
            onShowError: function(elem, scope, fieldName, action) {
                // find the form where this elem belongs to
                var form = $(elem).closest('form');
                if (!form) {
                    return null;
                }
                var formName = form.attr('name');

                scope.$watch(function () {
                    var showError = shouldShowValidationError(scope[formName], fieldName);
                    action(showError);
                });
            }
        };
    })

    .directive('formGroup', function (ValidationUtils) {
        return {
            restrict: 'A',
            link: function(scope, elem, attrs) {

                ValidationUtils.onShowError(elem, scope, attrs.formGroup, function(showError) {
                    elem.toggleClass('error', showError);
                });
            }
        };
    })

    .directive('formLabelValid', function(ValidationUtils) {
        return {
            restrict: 'A',
            link: function(scope, elem, attrs) {

                elem.addClass('valid-label');
                ValidationUtils.onShowError(elem, scope, attrs.formLabelValid, function(showError) {
                    elem.toggleClass('ng-hide', showError);
                });
            }
        }
    })

    .directive('formLabelInvalid', function(ValidationUtils) {
        return {
            restrict: 'A',
            link: function(scope, elem, attrs) {

                elem.addClass('invalid-label');
                ValidationUtils.onShowError(elem, scope, attrs.formLabelInvalid, function(showError) {
                    elem.toggleClass('ng-hide', !showError);
                });
            }
        }
    });