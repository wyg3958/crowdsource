angular.module('crowdsource')

    .controller('ProjectDetailsController', function ($window, $routeParams, $location, Project, Authentication) {

        var vm = this;

        vm.auth = Authentication;

        vm.project = Project.get($routeParams.projectId);

        // set the project id beforehand to allow the project-comments directive
        // to already load the comments for this project, else it must wait until
        // the GET /project/:id response is finished
        vm.project.id = $routeParams.projectId;

        vm.publish = function () {
            if (!$window.confirm('Willst Du das Projekt wirklich veröffentlichen?')) {
                return;
            }

            vm.publishing = true;
            Project.publish(vm.project.id).$promise
                .then(function (project) {
                    vm.project = project;
                })
                .catch(function () {
                    $location.path('/error/unknown');
                }).finally(function () {
                    vm.publishing = false;
                });
        };

        vm.reject = function () {
            if (!$window.confirm('Willst Du das Projekt wirklich ablehnen?')) {
                return;
            }

            vm.rejecting = true;
            Project.reject(vm.project.id).$promise
                .then(function (project) {
                    vm.project = project;
                })
                .catch(function () {
                    $location.path('/error/unknown');
                }).finally(function () {
                    vm.rejecting = false;
                });
        };

        vm.project.$promise.catch(function (response) {
            if (response.status == 404) {
                $location.path('/error/notfound');
            }
            else if (response.status == 403) {
                $location.path('/error/forbidden');
            }
            else {
                $location.path('/error/unknown');
            }
        });
    });
