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
                .then(function() {
                    $location.path('/project/new/success');
                })
                .catch(function(response) {
                    RemoteFormValidation.applyServerErrorResponse(vm, vm.form, response);
                })
                .finally(function() {
                    vm.loading = false;
                });
        };

    });
