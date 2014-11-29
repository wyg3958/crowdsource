angular.module('crowdsource')

    .controller('NavigationController', function ($scope, $location) {

        $scope.getClassForMenuItem = function (location) {
            if ($location.path() == location) {
                return 'active';
            }

            return '';
        };

    });