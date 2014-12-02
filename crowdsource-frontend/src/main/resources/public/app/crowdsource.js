/**
 * global application configuration
 */
angular.module('crowdsource', ['ngRoute', 'ngResource', 'ngMessages'])

    .config(function ($routeProvider, $locationProvider) {
        $routeProvider
            .when('/login', {
                templateUrl: 'app/user/login/user-login.html',
                controller: 'UserLoginController'
            })
            .when('/signup', {
                templateUrl: 'app/user/signup/user-signup.html',
                controller: 'UserSignupController'
            })
            .when('/signup/:email/success', {
                templateUrl: 'app/user/signup/user-signup-success.html',
                controller: 'UserSignupSuccessController'
            })
            .when('/signup/:email/activation/:activationToken', {
                templateUrl: 'app/user/activation/user-activation.html',
                controller: 'UserActivationController'
            })
            .otherwise({redirectTo: '/signup'});

        // we can only activate this, once you can reload the page with the path not being /
        $locationProvider.html5Mode(false);
    });
