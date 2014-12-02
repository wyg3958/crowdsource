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

            var copiedUser = angular.copy($scope.user);
            // remove this helper property from the copied object to not send this to the server
            delete copiedUser['repeatedPassword'];

            User.activate(copiedUser, $routeParams.activationToken);
        };

        // TODO: same code as in user-signup.js. A directive would be better
        $scope.shouldShowValidationError = function (field) {
            if (!$scope.activationForm[field]) {
                return false;
            }

            var userInteracted = $scope.activationForm.$submitted || $scope.activationForm[field].$dirty;
            return (userInteracted && $scope.activationForm[field].$invalid);
        };

        // TODO: same code as in user-signup.js. A directive would be better
        $scope.shouldNotShowValidationError = function (field) {
            return !$scope.shouldShowValidationError(field);
        };
    });