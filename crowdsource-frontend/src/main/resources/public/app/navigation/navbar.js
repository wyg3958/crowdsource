angular.module('crowdsource')

    .controller('NavigationController', function ($location, Authentication) {

        var vm = this;

        vm.getClassForMenuItem = function (location) {
            if ($location.path() == location) {
                return 'active';
            }

            return '';
        };

        vm.currentUser = Authentication.currentUser;
    });