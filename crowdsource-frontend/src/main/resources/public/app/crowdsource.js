/**
 * global application configuration
 */
angular.module('crowdsource', ['ngRoute', 'ngResource', 'ngMessages'])

    .config(function ($routeProvider, $locationProvider, $httpProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'app/overview/overview.html',
                controller: 'OverviewController'
            })
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
            .when('/logout', {
                templateUrl: 'app/user/logout/user-logout.html',
                controller: 'UserLogoutController'
            })
            .otherwise({redirectTo: '/'});

        $httpProvider.interceptors.push(function ($q, $location) {
            return {
                responseError: function (response) {
                    if (response.status == 401) {

                        // redirect to login
                        $location.path('/login');
                    }

                    return $q.reject(response);
                }
            }
        });
    }).run(function (Authentication) {
        Authentication.init();
    });
