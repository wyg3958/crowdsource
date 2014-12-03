angular.module('crowdsource')

    .controller('UserActivationController', function ($scope, $routeParams, User) {

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

            var promise = User.activate($scope.user).$promise;
            promise.then(function () {
                // TODO: implement me
            });
            promise.catch(function () {
                $scope.generalErrorOcurred = true;
            });
            promise.finally(function () {
                $scope.loading = false;
            });
        };
    });