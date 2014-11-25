angular.module('crowdsource')

    .controller('SignupController', function (User) {

        var ctrl = this;

        this.signUp = function () {
            if (!this.form.$valid) {
                return;
            }

            User.register(ctrl.user);
        };

    });