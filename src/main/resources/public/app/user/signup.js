angular.module('crowdsource')

    .controller('SignupController', function (User) {

        var ctrl = this;

        this.signUp = function () {
            User.register(ctrl.user);
        };

    });