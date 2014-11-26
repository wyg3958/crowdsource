angular.module('crowdsource')

    .controller('SignupController', function ($scope, User, FormUtils, $location) {

        var ctrl = this;

        $scope.$watch(function() { return ctrl.user; }, function() {
            ctrl.form.$setPristine();
        }, true);

        this.signUp = function () {
            if (!this.form.$valid) {
                return;
            }

            ctrl.generalErrorOcurred = false;
            ctrl.loading = true;

            var promise = User.register(ctrl.user);
            promise.then(function() {
                $location.path('/signup/' + ctrl.user.email + '/success');
            });
            promise.catch(function(response) {
                if (!FormUtils.applyServerErrorResponse(ctrl.form, response)) {
                    ctrl.generalErrorOcurred = true;
                }
            });
            promise.finally(function() {
                ctrl.loading = false;
            });
        };

    });