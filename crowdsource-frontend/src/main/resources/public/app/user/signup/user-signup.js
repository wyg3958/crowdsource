angular.module('crowdsource')

    .controller('UserSignupController', function ($scope, $location, User, RemoteFormValidation) {

        $scope.signUp = function () {
            if (!$scope.signupForm.$valid) {
                return;
            }

            RemoteFormValidation.clearRemoteErrors($scope);
            $scope.loading = true;

            var promise = User.register($scope.user);
            promise.then(function () {
                $location.path('/signup/' + $scope.user.email + '/success');
            });
            promise.catch(function (response) {
                RemoteFormValidation.applyServerErrorResponse($scope, $scope.signupForm, response);
            });
            promise.finally(function () {
                $scope.loading = false;
            });
        };
    });
