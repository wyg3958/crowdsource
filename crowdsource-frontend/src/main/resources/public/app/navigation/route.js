angular.module('crowdsource')

    .factory('Route', function($rootScope, $location, Authentication) {
        var service = {};

        var pathBeforeRedirectToLogin;

        service.init = function() {
            $rootScope.$on('$routeChangeStart', function (event, next) {
                // if the route was configured with requireLogin: true and the user is not logged in, redirect to login
                if (next.requireLogin && !Authentication.isLoggedIn()) {

                    // remember the path where the user would have been redirected to
                    pathBeforeRedirectToLogin = next.originalPath;

                    // cancel the requested route change
                    event.preventDefault();

                    // force redirect to login
                    $location.path('/login');
                }
            });

        };

        service.redirectToOriginallyRequestedPageOr = function(fallbackPath) {
            if (pathBeforeRedirectToLogin) {
                $location.path(pathBeforeRedirectToLogin);
            }
            else {
                $location.path(fallbackPath);
            }

            pathBeforeRedirectToLogin = undefined;
        };

        return service;
    });