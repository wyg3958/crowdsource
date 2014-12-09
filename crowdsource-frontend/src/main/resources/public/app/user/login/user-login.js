angular.module('crowdsource')

    .controller('UserLoginController', function ($scope, Authentication, RemoteFormValidation, Route) {

        $scope.EMAIL_HOST = '@axelspringer.de';

        $scope.login = function () {
            if (!$scope.loginForm.$valid) {
                return;
            }

            RemoteFormValidation.clearRemoteErrors($scope);
            $scope.loading = true;

            var userCopy = angular.copy($scope.user);
            // make the email an actual email on a copied user object (to not update the UI)
            userCopy.email = userCopy.email + $scope.EMAIL_HOST;

            Authentication.login(userCopy.email, userCopy.password)
                .then(function () {
                    Route.redirectToOriginallyRequestedPageOr('/');
                })
                .catch(function (errorCode) {
                    RemoteFormValidation.setGeneralError($scope, errorCode);
                })
                .finally(function () {
                    $scope.loading = false;
                });
        };
    })

    .directive("nonExternalEmail", function () {
        return {
            restrict: "A",
            require: "ngModel",
            link: function (scope, element, attributes, ngModel) {
                ngModel.$validators.non_external_email = function (modelValue) {
                    return !modelValue || modelValue.indexOf('_extern') < 0;
                }
            }
        };
    });