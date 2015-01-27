angular.module('crowdsource')

    .factory('Project', function ($resource) {

        var service = {};

        var projectResource = $resource('/project/:id', {}, {
            put: {
                method: 'PUT'
            }
        });
        var projectsResource = $resource('/projects');
        var projectPledgeResource = $resource('/project/:id/pledge');

        service.add = function (project) {
            return projectResource.save(project).$promise;
        };

        service.getAll = function () {
            return projectsResource.query();
        };

        service.get = function (projectId) {
            return projectResource.get({id: projectId});
        };

        service.pledge = function (projectId, pledge) {
            return projectPledgeResource.save({id: projectId}, pledge);
        };

        service.publish = function (projectId) {
            return projectResource.put({id: projectId}, {status: 'PUBLISHED'});
        };

        service.reject = function (projectId) {
            return projectResource.put({id: projectId}, {status: 'REJECTED'});
        };

        return service;
    });