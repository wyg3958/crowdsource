angular.module('crowdsource')

    .factory('Content', function () {
        var service;

        // TODO: optimize performance ?
        service.getAll = function () {
            return $http.get('/content', {cache: true});
        };

        return service;
    })

    .directive('content', function () {
        return {
            restrict: 'A',
            scope: {
                key: '='
            },
            template: '<span>{{ content }}</span>',
            controller: function ($scope) {
                Content.getAll().then(function(content) {
                    $scope.content = content[$scope.key];
                });
            }
        }
    });
