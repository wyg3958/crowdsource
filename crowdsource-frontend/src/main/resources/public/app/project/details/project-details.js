angular.module('crowdsource')

    .controller('ProjectDetailsController', function($routeParams, Project) {

        this.project = Project.get($routeParams.projectId);

    });
