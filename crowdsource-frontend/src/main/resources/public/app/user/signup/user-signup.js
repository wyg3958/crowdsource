angular.module('crowdsource')

    .controller('UserSignupController', function ($scope, $location, User, RemoteFormValidation) {

        $scope.EMAIL_HOST = '@axelspringer.de';

        $scope.signUp = function () {
            if (!$scope.signupForm.$valid) {
                return;
            }

            $scope.generalErrorOcurred = false;
            $scope.loading = true;

            var userCopy = angular.copy($scope.user);
            // make the email an actual email on a copied user object (to not update the UI)
            userCopy.email = userCopy.email + $scope.EMAIL_HOST;

            var promise = User.register(userCopy);
            promise.then(function () {
                $location.path('/signup/' + userCopy.email + '/success');
            });
            promise.catch(function (response) {
                if (!RemoteFormValidation.applyServerErrorResponse($scope.signupForm, response)) {
                    $scope.generalErrorOcurred = true;
                }
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