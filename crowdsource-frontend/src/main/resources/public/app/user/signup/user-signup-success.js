angular.module('crowdsource')

    .controller('UserSignupSuccessController', function ($routeParams) {

        this.email = $routeParams.email;
    });
