/**
 * global application configuration
 */
angular.module('crowdsource', ['ngRoute', 'ngResource', 'ngMessages'])

.config(function ($routeProvider, $locationProvider) {
    $routeProvider
        .when('/login', {
            templateUrl: 'app/user/login.html',
            controller: 'LoginController as login'
        })
        .when('/signup', {
            templateUrl: 'app/user/signup.html',
            controller: 'SignupController as signup'
        })
        .when('/signup/:email/success', {
            templateUrl: 'app/user/signup-success.html',
            controller: 'SignupSuccessController as signupSuccess'
        });

    // we can only activate this, once you can reload the page with the path not being /
    $locationProvider.html5Mode(false);
});
