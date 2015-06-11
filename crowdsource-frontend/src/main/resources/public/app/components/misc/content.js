angular.module('crowdsource')

    .factory('Content', function ($http) {
        var service = {};

        // TODO: optimize performance ?
        service.getAll = function () {
            return $http.get('/content', {cache: true});
        };

        return service;
    })

    .directive('content', function (Content) {
        return {
            restrict: 'E',
            scope: {
                key: '@'
            },
            template: '<span>{{ content }}</span>',
            controller: function ($scope) {
                Content.getAll().then(function(response) {
                    $scope.content = response.data[$scope.key];
                });
            }
        }
    });
