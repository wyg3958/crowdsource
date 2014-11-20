var app = angular.module('crowdsource', ['ngRoute']);

app.controller('MainController', function ($scope, $route, $routeParams, $location) {
        $scope.$route = $route;
        $scope.$location = $location;
        $scope.$routeParams = $routeParams;
    }
);

/**
 * template routing
 */
app.config(function ($routeProvider, $locationProvider) {
    $routeProvider
        .when('/login', {
            templateUrl: 'app/templates/login.html',
            controller: 'MainController'
        })
        .when('/signup', {
            templateUrl: 'app/templates/signup.html',
            controller: 'MainController'
        });

    // configure html5 to get links working on jsfiddle
    $locationProvider.html5Mode(true);
});

