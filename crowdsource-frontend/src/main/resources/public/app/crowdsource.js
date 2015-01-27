/**
 * global application configuration
 */
angular.module('crowdsource', ['ngRoute', 'ngResource', 'ngMessages', 'dibari.angular-ellipsis', 'ngScrollTo'])

    .config(function ($routeProvider, $locationProvider, $httpProvider) {
        $routeProvider
            .when('/projects', {
                templateUrl: 'app/project/list/project-list.html',
                controller: 'ProjectListController as projectList',
                title: 'Projekte',
                showTeaser: true
            })
            .when('/project/new', {
                templateUrl: 'app/project/form/project-form.html',
                controller: 'ProjectFormController as projectForm',
                title: 'Neues Projekt',
                requireLogin: true
            })
            .when('/project/:projectId', {
                templateUrl: 'app/project/details/project-details.html',
                controller: 'ProjectDetailsController as projectDetails',
                title: 'Projektdetails'
            })
            .when('/project/new/success', {
                templateUrl: 'app/project/form/project-form-success.html',
                requireLogin: true,
                title: 'Neues Projekt angelegt'
            })
            .when('/login', {
                templateUrl: 'app/user/login/user-login.html',
                controller: 'UserLoginController as login',
                title: 'Login'
            })
            .when('/signup', {
                templateUrl: 'app/user/signup/user-signup.html',
                controller: 'UserSignupController as signup',
                title: 'Registrierung'
            })
            .when('/signup/:email/success', {
                templateUrl: 'app/user/signup/user-signup-success.html',
                controller: 'UserSignupSuccessController as signupSuccess',
                title: 'Registrierung erfolgreich'
            })
            .when('/signup/:email/activation/:activationToken', {
                templateUrl: 'app/user/activation/user-activation.html',
                controller: 'UserActivationController as activation',
                title: 'Aktivierung'
            })
            .when('/login/password-recovery', {
                templateUrl: 'app/user/password-recovery/password-recovery.html',
                controller: 'PasswordRecoveryController as passwordRecovery',
                title: 'Passwort vergessen'
            })
            .when('/login/password-recovery/:email/success', {
                templateUrl: 'app/user/password-recovery/password-recovery-success.html',
                controller: 'PasswordRecoverySuccessController as passwordRecoverySuccess',
                title: 'Passwort vergessen erfolgreich'
            })
            .when('/login/password-recovery/:email/activation/:activationToken', {
                templateUrl: 'app/user/activation/user-activation.html',
                controller: 'UserActivationController as activation',
                title: 'Passwort setzen'
            })
            .when('/financingrounds', {
                templateUrl: 'app/financing-rounds/financing-rounds.html',
                controller: 'FinancingRoundsController as financingRounds',
                title: 'Finanzierungsrunden',
                requireLogin: true
            })
            .when('/about', {
                templateUrl: 'app/misc/about.html',
                title: 'Über Uns',
                showTeaser: true
            })
            .when('/logout', {
                templateUrl: 'app/user/logout/user-logout.html',
                controller: 'UserLogoutController as logout',
                title: 'Logout'
            })
            .when('/error/notfound', {
                templateUrl: 'app/error/error-notfound.html',
                title: 'Seite nicht gefunden'
            })
            .when('/error/forbidden', {
                templateUrl: 'app/error/error-forbidden.html',
                title: 'Zugriff verweigert'
            })
            .when('/error/unknown', {
                templateUrl: 'app/error/error-unknown.html',
                title: 'Technischer Fehler'
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
