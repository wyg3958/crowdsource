/**
 * global application configuration
 */
angular.module('crowdsource', ['ngRoute', 'ngResource', 'ngMessages', 'dibari.angular-ellipsis', 'ngScrollTo'])

    .config(function ($routeProvider, $locationProvider, $httpProvider) {
        $routeProvider
            .when('/projects', {
                templateUrl: 'app/project/list/project-list.html',
                controller: 'ProjectListController as projectList'
            })
            .when('/project/new', {
                templateUrl: 'app/project/form/project-form.html',
                controller: 'ProjectFormController as projectForm',
                requireLogin: true
            })
            .when('/project/:projectId', {
                templateUrl: 'app/project/details/project-details.html',
                controller: 'ProjectDetailsController as projectDetails'
            })
            .when('/project/new/success', {
                templateUrl: 'app/project/form/project-form-success.html',
                requireLogin: true
            })
            .when('/login', {
                templateUrl: 'app/user/login/user-login.html',
                controller: 'UserLoginController as login'
            })
            .when('/signup', {
                templateUrl: 'app/user/signup/user-signup.html',
                controller: 'UserSignupController as signup'
            })
            .when('/signup/:email/success', {
                templateUrl: 'app/user/signup/user-signup-success.html',
                controller: 'UserSignupSuccessController as signupSuccess'
            })
            .when('/signup/:email/activation/:activationToken', {
                templateUrl: 'app/user/activation/user-activation.html',
                controller: 'UserActivationController as activation'
            })
            .when('/login/password-recovery', {
                templateUrl: 'app/user/password-recovery/password-recovery.html',
                controller: 'PasswordRecoveryController as passwordRecovery'
            })
            .when('/login/password-recovery/:email/success', {
                templateUrl: 'app/user/password-recovery/password-recovery-success.html',
                controller: 'PasswordRecoverySuccessController as passwordRecoverySuccess'
            })
            .when('/financingrounds', {
                templateUrl: 'app/financing-rounds/financing-rounds.html',
                controller: 'FinancingRoundController as financingRound',
                requireLogin: true
            })
            .when('/logout', {
                templateUrl: 'app/user/logout/user-logout.html',
                controller: 'UserLogoutController as logout'
            })
            .when('/error/notfound', {
                templateUrl: 'app/error/error-notfound.html'
            })
            .when('/error/unknown', {
                templateUrl: 'app/error/error-unknown.html'
            })
            .otherwise({redirectTo: '/projects'});

        $httpProvider.interceptors.push('UnauthorizedInterceptor');
        $httpProvider.interceptors.push('LoggingInterceptor');
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
