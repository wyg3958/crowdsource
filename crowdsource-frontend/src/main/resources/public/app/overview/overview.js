angular.module('crowdsource')

    .controller('OverviewController', function($scope, $resource) {
        var Hello = $resource('/hello');

        $scope.hello = Hello.get();
    });
