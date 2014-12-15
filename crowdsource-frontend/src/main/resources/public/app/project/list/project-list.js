angular.module('crowdsource')

    .controller('ProjectListController', function($scope, Project) {

        $scope.projects = Project.getAll();
    });
