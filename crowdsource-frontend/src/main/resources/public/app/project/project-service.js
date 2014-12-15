angular.module('crowdsource')

    .factory('Project', function ($resource) {

        var projectsResource = $resource('/projects');
        var projectResource = $resource('/project');

        return {
            add: function (project) {
                return projectResource.save(project).$promise;
            },
            getAll: function () {
                return projectsResource.query();
            }
        };
    });