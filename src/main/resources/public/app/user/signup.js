angular.module('crowdsource')

    .controller('SignupController', function () {

        this.signUp = function () {
            console.log('signup', this.user);
        };

    });