angular.module('crowdsource')

    .controller('UserSignupSuccessController', function ($scope, $routeParams) {

        $scope.email = $routeParams.email;

    });