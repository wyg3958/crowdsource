angular.module('crowdsource')

    .controller('UserActivationController', function ($scope, $routeParams, User, Authentication) {

        $scope.user = {
            email: $routeParams.email,
            activationToken: $routeParams.activationToken
        };

        $scope.activate = function() {
            if ($scope.user.password != $scope.user.repeatedPassword) {
                $scope.activationForm.repeatedPassword.$setValidity('remote_equal', false);
            }

            if (!$scope.activationForm.$valid) {
                return;
            }

            $scope.loading = true;

            var activationPromise = User.activate($scope.user).$promise;
            activationPromise.then(function () {
                return Authentication.login($scope.user.email, $scope.user.password);
            });
            activationPromise.catch(function () {
                $scope.generalErrorOcurred = true;
            });
            activationPromise.finally(function () {
                $scope.loading = false;
            });
        };
    });