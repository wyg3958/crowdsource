angular.module('crowdsource')

    .controller('ProjectDetailsController', function ($routeParams, $location, Project, Comment) {

        var vm = this;

        vm.project = Project.get($routeParams.projectId);

        vm.project.$promise.catch(function (response) {
            if (response.status == 404) {
                $location.path('/error/notfound');
            }
            else {
                $location.path('/error/unknown');
            }
        });

        vm.comments = Comment.getAll(vm.project.id);

        vm.storeComment = function (comment) {

            Comment.add(vm.project.id, comment).$promise.catch(function (response) {
                if (response.status == 404) {
                    $location.path('/error/notfound');
                }
                // TODO: validation errors
                else {
                    $location.path('/error/unknown');
                }
            });
        }
    });
