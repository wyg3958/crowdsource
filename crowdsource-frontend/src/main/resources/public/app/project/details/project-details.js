angular.module('crowdsource')

    .controller('ProjectDetailsController', function($routeParams, $location, Project) {

        var vm = this;

        vm.project = Project.get($routeParams.projectId);

//        vm.project.$promise.then(function() {
//            vm.project.pledgedAmount = 20000;
//        });

        vm.project.$promise.catch(function(response) {
            if (response.status == 404) {
                $location.path('/error/notfound');
            }
            else {
                $location.path('/error/unknown');
            }
        });

    });
