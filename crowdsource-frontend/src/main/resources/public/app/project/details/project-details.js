angular.module('crowdsource')

    .controller('ProjectDetailsController', function ($routeParams, $location, Project, Authentication) {

        var vm = this;

        vm.auth = Authentication;

        vm.project = Project.get($routeParams.projectId);

        // set the project id beforehand to allow the project-comments directive
        // to already load the comments for this project, else it must wait until
        // the GET /project/:id response is finished
        vm.project.id = $routeParams.projectId;

        vm.publish = function () {
            Project.publish(vm.project.id).$promise
                .then(function (project) {
                    vm.project = project;
                })
                .catch(function () {
                    $location.path('/error/unknown');
                });
        };

        vm.reject = function () {
            Project.reject(vm.project.id).$promise
                .then(function (project) {
                    vm.project = project;
                })
                .catch(function () {
                    $location.path('/error/unknown');
                });
        };

        vm.project.$promise.catch(function (response) {
            if (response.status == 404) {
                $location.path('/error/notfound');
            }
            else {
                $location.path('/error/unknown');
            }
        });
    });
