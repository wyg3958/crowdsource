angular.module('crowdsource')

    .factory('ProjectAdministration', function ($resource) {

        var service = {};

        var publishRessource = $resource('/project/:id/comments');

        service.add = function (projectId, comment) {
            return commentResource.save({id: projectId}, comment);
        };

        service.getAll = function (projectId) {
            return commentsResource.query({id: projectId});
        };

        return service;
    });