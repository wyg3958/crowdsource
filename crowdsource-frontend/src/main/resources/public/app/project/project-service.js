angular.module('crowdsource')

    .factory('Project', function ($resource) {

        var service = {};

        var projectsResource = $resource('/projects');
        var projectResource = $resource('/project/:id');

        service.add = function (project) {
            return projectResource.save(project).$promise;
        };

        service.getAll = function () {
            return projectsResource.query();
        };

        service.get = function (projectId) {
            return projectResource.get({ id: projectId });
        };

        return service;
    });