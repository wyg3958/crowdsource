/**
 * global application configuration
 */
angular.module('crowdsource', ['ngRoute', 'ngResource', 'ngMessages'])

    .config(function ($routeProvider, $locationProvider, $httpProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'app/overview/overview.html',
                controller: 'OverviewController',
                requireLogin: true
            })
            .when('/project/new', {
                templateUrl: 'app/project/form/project-form.html',
                controller: 'ProjectFormController',
                requireLogin: true
            })
            .when('/project/new/success', {
                templateUrl: 'app/project/form/project-form-success.html',
                requireLogin: true
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

        $httpProvider.interceptors.push('UnauthorizedInterceptor');
    })
    .run(function ($rootScope, $location, Authentication) {
        Authentication.init();

        $rootScope.$on('$routeChangeStart', function (event, next) {
            // if the route was configured with requireLogin: true and the user is not logged in, redirect to login
            if (next.requireLogin && !Authentication.isLoggedIn()) {
                event.preventDefault(); // cancel the requested route change
                $location.path('/login');
            }
        });

        // initialize foundation widgets
        $(document).foundation();
    });
