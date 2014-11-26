angular.module('crowdsource')

    .controller('SignupSuccessController', function ($routeParams) {

        this.email = $routeParams.email;

    });