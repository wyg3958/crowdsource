angular.module('crowdsource')

    .directive('projectPledgingForm', function() {
        return {
            templateUrl: 'app/project/details/project-pledging-form.html',
            controllerAs: 'pledgingForm',
            bindToController: true,
            scope: {
                project: '='
            },
            controller: function(Project, Authentication, RemoteFormValidation, FinancingRound, $q) {
                var vm = this;

                vm.activeFinancingRound = FinancingRound.getActive();

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
                        })
                        .finally(function() {
                            vm.pledge.amount = 0;
                            vm.saving = false;
                            vm.form.$setPristine();
                        });
                };

                vm.getPledgableAmount = function() {
                    if (isLoading()) {
                        return 0;
                    }

                    if (!vm.activeFinancingRound.active) {
                        return 0;
                    }

                    var remainingProjectGoal = vm.project.pledgeGoal - vm.project.pledgedAmount;
                    return Math.min(remainingProjectGoal, vm.user.budget);
                };

                vm.getNotification = function() {
                    if (isLoading()) {
                        return null;
                    }

                    if (vm.success) {
                        return { type: 'success', message: 'Deine Finanzierung war erfolgreich.' };
                    }
                    if (vm.project.status == 'FULLY_PLEDGED') {
                        return { type: 'info', message: 'Das Project ist zu 100% finanziert. Eine weitere Finanzierung ist nicht mehr möglich.' };
                    }
                    if (!vm.activeFinancingRound.active) {
                        return { type: 'info', message: 'Momentan läuft keine Finanzierungsrunde. Bitte versuche es nochmal, wenn die Finanzierungsrunde gestartet worden ist.' };
                    }
                    if (!vm.user.loggedIn) {
                        return { type: 'info', message: 'Bitte logge dich ein, um Projekte finanziell zu unterstützen.' };
                    }
                    if (vm.user.budget == 0) {
                        return { type: 'info', message: 'Dein Budget ist leider aufgebraucht. Du kannst dieses Projekt nicht weiter finanzieren. Bitte warte ab, bis die nächste Finanzierungsrunde startet, dann wird der Finanzierungstopf erneut auf alle Benutzer aufgeteilt.' };
                    }

                    return null;
                };


                function isLoading() {
                    return !vm.project.$resolved || !vm.user.$resolved || !vm.activeFinancingRound.$resolved;
                }

                function reloadUserAndProject() {
                    // parallel execution of backend calls
                    var promises = $q.all({
                        project: Project.get(vm.project.id).$promise,
                        user: Authentication.reloadUser().$promise,
                        financingRound: FinancingRound.getActive().$promise
                    });

                    // will be resolved when all calls are completed
                    promises.then(function(resolvedPromises) {
                        angular.copy(resolvedPromises.project, vm.project);
                        angular.copy(resolvedPromises.user, vm.user);
                        angular.copy(resolvedPromises.financingRound, vm.activeFinancingRound);
                    });

                    return promises;
                }
            }
        };
    });
