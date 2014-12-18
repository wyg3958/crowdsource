angular.module('crowdsource')

    .controller('ProjectListController', function($location, Project) {

        var vm = this;

        vm.projects = Project.getAll();

        vm.showProjectDetails = function(project) {
            $location.path('/project/' + project.id);
        };
    });
