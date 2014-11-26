angular.module('crowdsource')

    .controller('SignupSuccessController', function ($scope, $routeParams) {

        $scope.email = $routeParams.email;

    });