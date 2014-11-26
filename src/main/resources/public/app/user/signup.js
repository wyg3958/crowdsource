angular.module('crowdsource')

    .controller('SignupController', function (User) {

        var ctrl = this;

        this.signUp = function () {
            if (!this.form.$valid) {
                return;
            }

            ctrl.loading = true;

            var promise = User.register(ctrl.user);
            promise.finally(function() {
                ctrl.loading = false;
            });
        };

    });