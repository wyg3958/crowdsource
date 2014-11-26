angular.module('crowdsource')

    .controller('SignupController', function (User, $location) {

        var ctrl = this;

        this.signUp = function () {
            if (!this.form.$valid) {
                return;
            }

            ctrl.loading = true;

            var promise = User.register(ctrl.user);
            promise.catch(function() {
                $location.path('/signup/' + ctrl.user.email + '/success');
            });
            promise.finally(function() {
                ctrl.loading = false;
            });
        };

    });