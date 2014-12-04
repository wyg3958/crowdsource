angular.module('crowdsource')

    .controller('UserLoginController', function ($scope, $location, Authentication, RemoteFormValidation) {

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

            var promise = Authentication.login(userCopy.email, userCopy.password);
            promise.then(function () {
                $location.path('/');
            });
            promise.catch(function (response) {
                RemoteFormValidation.setGeneralError($scope, 'bad_credentials');
                //RemoteFormValidation.applyServerErrorResponse($scope, $scope.loginForm, response);
            });
            promise.finally(function () {
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