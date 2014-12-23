angular.module('crowdsource')

    .controller('NavigationController', function ($scope, $location, Authentication) {

        $scope.getClassForMenuItem = function (location) {
            if ($location.path() == location) {
                return 'active';
            }

            return '';
        };

        /**
         * Will influence behaviour (eg login/logout-links, create project, ...)
         */
        $scope.loggedIn = function () {
            return Authentication.isLoggedIn();
        }
    });