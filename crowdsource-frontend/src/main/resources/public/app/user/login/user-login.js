angular.module('crowdsource')

    .controller('UserLoginController', function ($scope, Authentication, RemoteFormValidation, Route) {

        $scope.login = function () {
            if (!$scope.loginForm.$valid) {
                return;
            }

            RemoteFormValidation.clearRemoteErrors($scope);
            $scope.loading = true;

            Authentication.login($scope.user.email, $scope.user.password)
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
    });