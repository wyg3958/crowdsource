/**
 * global application configuration
 */
angular.module('crowdsource', ['ngRoute', 'ngResource', 'ngMessages'])

.config(function ($routeProvider, $locationProvider) {
    $routeProvider
        .when('/login', {
            templateUrl: 'app/user/login.html',
            controller: 'LoginController'
        })
        .when('/signup', {
            templateUrl: 'app/user/signup.html',
            controller: 'SignupController'
        })
        .when('/signup/:email/success', {
            templateUrl: 'app/user/signup-success.html',
            controller: 'SignupSuccessController'
        })
        .otherwise({ redirectTo: '/signup' });

    // we can only activate this, once you can reload the page with the path not being /
    $locationProvider.html5Mode(false);
});
