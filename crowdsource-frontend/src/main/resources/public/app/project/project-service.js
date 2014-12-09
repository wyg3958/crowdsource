angular.module('crowdsource')

    .factory('Project', function ($resource) {

        var projectResource = $resource('/project');

        return {
            add: function (project) {
                return projectResource.save(project).$promise;
            }
        };
    });