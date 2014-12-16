angular.module('crowdsource')

    .controller('ProjectListController', function($scope, $location, Project) {

        $scope.projects = Project.getAll();

        $scope.showProjectDetails = function(project) {
            $location.path('/project/' + project.id);
        };
    });
