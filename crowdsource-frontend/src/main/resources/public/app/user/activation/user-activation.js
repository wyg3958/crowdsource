angular.module('crowdsource')

    .controller('UserActivationController', function ($scope, $routeParams, $location, User, Authentication, RemoteFormValidation) {

        $scope.user = {
            email: $routeParams.email,
            activationToken: $routeParams.activationToken
        };

        $scope.activate = function() {
            if (!$scope.activationForm.$valid) {
                return;
            }

            if ($scope.user.password != $scope.user.repeatedPassword) {
                $scope.activationForm.repeatedPassword.$setValidity('remote_equal', false);
                return;
            }

            RemoteFormValidation.clearRemoteErrors($scope);
            $scope.loading = true;

            User.activate($scope.user).$promise
                .then(function () {
                    return Authentication.login($scope.user.email, $scope.user.password);
                })
                .then(function() {
                    $location.path('/');
                })
                .catch(function (response) {
                    RemoteFormValidation.applyServerErrorResponse($scope, $scope.activationForm, response);
                })
                .finally(function() {
                    $scope.loading = false;
                });
        };
    });