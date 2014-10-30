angular.module('crowdsource').directive('hello', function () {
    return {
        // restrict to element (modern browsers only)
        restrict: 'E',
        // map template
        templateUrl: 'app/directives/hello/hello.html',
        // directive controller
        controller: function ($scope, helloservice) {
            $scope.hello = helloservice.get();
        }
    };
});
