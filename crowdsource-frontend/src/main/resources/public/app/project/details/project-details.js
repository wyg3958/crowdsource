angular.module('crowdsource')

    .controller('ProjectDetailsController', function($scope, $routeParams, Project) {

        $scope.project = Project.get($routeParams.projectId);

    });
