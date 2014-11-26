angular.module('crowdsource')

    .controller('SignupController', function ($scope, User, FormUtils, $location) {

        $scope.EMAIL_HOST = '@axelspringer.de';

        $scope.signUp = function () {
            if (!$scope.signupForm.$valid) {
                return;
            }

            $scope.generalErrorOcurred = false;
            $scope.loading = true;

            var userCopy = angular.copy($scope.user);
            userCopy.email = userCopy.email + $scope.EMAIL_HOST;

            var promise = User.register(userCopy);
            promise.then(function() {
                $location.path('/signup/' + userCopy.email + '/success');
            });
            promise.catch(function(response) {
                if (!FormUtils.applyServerErrorResponse($scope.signupForm, response)) {
                    $scope.generalErrorOcurred = true;
                }
            });
            promise.finally(function() {
                $scope.loading = false;
            });
        };

        $scope.shouldShowValidationError = function(field) {
            if (!$scope.signupForm[field]) {
                return false;
            }

            var userInteracted = $scope.signupForm.$submitted || $scope.signupForm[field].$dirty;
            return (userInteracted && $scope.signupForm[field].$invalid);
        };

        $scope.shouldNotShowValidationError = function(field) {
            return !$scope.shouldShowValidationError(field);
        };

    });