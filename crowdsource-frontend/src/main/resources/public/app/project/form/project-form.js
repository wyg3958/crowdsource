angular.module('crowdsource')

    .controller('ProjectFormController', function($location, Project, RemoteFormValidation) {

        var vm = this;

        vm.submitProjectIdea = function() {
            if (!vm.form.$valid) {
                return;
            }

            RemoteFormValidation.clearRemoteErrors(vm);
            vm.loading = true;

            Project.add(vm.project)
                .then(function(savedProject) {
                    $location.path('/project/new/' + savedProject.id);
                })
                .catch(function(response) {
                    RemoteFormValidation.applyServerErrorResponse(vm, vm.form, response);
                })
                .finally(function() {
                    vm.loading = false;
                });
        };

    });
