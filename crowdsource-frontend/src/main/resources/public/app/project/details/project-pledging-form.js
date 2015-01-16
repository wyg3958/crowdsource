angular.module('crowdsource')

    .directive('projectPledgingForm', function() {
        return {
            templateUrl: 'app/project/details/project-pledging-form.html',
            controllerAs: 'pledgingForm',
            bindToController: true,
            scope: {
                project: '='
            },
            controller: function(Project, Authentication, RemoteFormValidation, $q) {
                var vm = this;

                // to get the current user's budget
                vm.user = Authentication.reloadUser();

                vm.pledgeProject = function() {
                    vm.success = false;
                    vm.saving = true;
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
                        })
                        .finally(function() {
                            vm.saving = false;
                        });
                };

                vm.getPledgableAmount = function() {
                    if (!vm.project.$resolved || !vm.user.$resolved) {
                        return 0;
                    }

                    var remainingProjectGoal = vm.project.pledgeGoal - vm.project.pledgedAmount;
                    return Math.min(remainingProjectGoal, vm.user.budget);
                };

                vm.getNotification = function() {
                    if (!vm.user.$resolved || !vm.project.$resolved) {
                        return null;
                    }

                    if (!vm.user.loggedIn) {
                        return { type: 'info', message: 'Bitte logge dich ein, um Projekte finanziell zu unterstützen.' };
                    }

                    if (vm.success) {
                        return { type: 'success', message: 'Deine Finanzierung war erfolgreich.' };
                    }
                    else if (vm.project.status == 'FULLY_PLEDGED') {
                        return { type: 'info', message: 'Das Project ist zu 100% finanziert. Eine weitere Finanzierung ist nicht mehr möglich.' };
                    }
                    else if (vm.user.budget == 0) {
                        return { type: 'info', message: 'Dein Budget ist leider aufgebraucht. Du kannst dieses Projekt nicht weiter finanzieren. Bitte warte ab, bis die nächste Finanzierungsrunde startet, dann wird der Finanzierungstopf erneut auf alle Benutzer aufgeteilt.' };
                    }

                    return null;
                };


                function reloadUserAndProject() {
                    // parallel execution of backend calls
                    var promises = $q.all({
                        project: Project.get(vm.project.id).$promise,
                        user: Authentication.reloadUser().$promise
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
