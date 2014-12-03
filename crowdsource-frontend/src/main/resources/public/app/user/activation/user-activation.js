angular.module('crowdsource')

    .controller('UserActivationController', function ($scope, $routeParams, User) {

        $scope.user = {
            email: $routeParams.email
        };

        $scope.activate = function() {
            if ($scope.user.password != $scope.user.repeatedPassword) {
                $scope.activationForm.repeatedPassword.$setValidity('remote_equal', false);
            }

            if (!$scope.activationForm.$valid) {
                return;
            }

            $scope.loading = true;

            var userCopy = angular.copy($scope.user);
            // remove this helper property from the copied object to not send this to the server
            delete userCopy['repeatedPassword'];

            var promise = User.activate(userCopy, $routeParams.activationToken).$promise;
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