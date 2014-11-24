var app = angular.module('crowdsource', ['ngRoute']);

app.controller('MainController', function ($scope, $http) {
        $scope.saveUser = function () {
            var request = $http.post('/saveUser', {msg: 'hello word!'}).
                success(function (data, status, headers, config) {
                    // this callback will be called asynchronously
                    // when the response is available
                }).
                error(function (data, status, headers, config) {
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });
        }
    }
);

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

    $locationProvider.html5Mode(true);
});
