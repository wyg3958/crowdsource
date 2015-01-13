angular.module('crowdsource')

    .directive('projectPledgingForm', function() {
        return {
            templateUrl: 'app/project/details/project-pledging-form.html',
            controllerAs: 'pledgingForm',
            bindToController: true,
            scope: {
                project: '='
            },
            controller: function(Project, User, RemoteFormValidation, $q) {
                var vm = this;

                vm.user = User.current();

                vm.pledgeProject = function() {
                    vm.success = false;
                    RemoteFormValidation.clearRemoteErrors(vm);

                    Project.pledge(vm.project.id, vm.pledge).$promise
                        .then(function() {
                            // load the project and user again to update the project details view to the new state
                            return reloadUserAndProject();
                        },
                        function(response) {
                            RemoteFormValidation.applyServerErrorResponse(vm, vm.form, response);

                            // most probably the project details or displayed user budget was outdated and the user "over-pledged"
                            reloadUserAndProject();

                            // return a rejected promise, or the .then() callback below will be called
                            return $q.reject(response);
                        })
                        .then(function() {
                            vm.success = true;
                            vm.pledge.amount = 0;

                            vm.form.$setPristine();
                        });
                };

                vm.getPledgableAmount = function() {
                    if (!vm.project.$resolved || !vm.user.$resolved) {
                        return 0;
                    }

                    var remainingProjectGoal = vm.project.pledgeGoal - vm.project.pledgedAmount;
                    return Math.min(remainingProjectGoal, vm.user.budget);
                };


                function reloadUserAndProject() {
                    // parallel execution of backend calls
                    var promises = $q.all({
                        project: Project.get(vm.project.id).$promise,
                        user: User.current().$promise
                    });

                    // will be resolved when both calls are completed
                    promises.then(function(resolvedPromises) {
                        angular.copy(resolvedPromises.project, vm.project);
                        angular.copy(resolvedPromises.user, vm.user);
                    });

                    return promises;
                }
            }
        };
    });
