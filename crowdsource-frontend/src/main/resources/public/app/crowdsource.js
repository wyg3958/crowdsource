/**
 * global application configuration
 */
angular.module('crowdsource', ['ngRoute', 'ngResource', 'ngMessages', 'dibari.angular-ellipsis'])

    .config(function ($routeProvider, $locationProvider, $httpProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'app/overview/overview.html',
                controller: 'OverviewController',
                requireLogin: true
            })
            .when('/projects', {
                templateUrl: 'app/project/list/project-list.html',
                controller: 'ProjectListController'
            })
            .when('/project/new', {
                templateUrl: 'app/project/form/project-form.html',
                controller: 'ProjectFormController',
                requireLogin: true
            })
            .when('/project/:projectId', {
                templateUrl: 'app/project/details/project-details.html',
                controller: 'ProjectDetailsController'
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
                controller: 'UserSignupController as signup'
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

    .run(function (Authentication, Route) {
        Authentication.init();
        Route.init();

        // initialize foundation widgets
        $(document).foundation({
            equalizer: {
                // required to work with block grids
                equalize_on_stack: true
            }
        });
    });
