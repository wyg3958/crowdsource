angular.module('crowdsource')

    .directive('projectPledgingForm', function () {
        return {
            templateUrl: 'app/project/details/project-pledging-form.html',
            controllerAs: 'pledgingForm',
            bindToController: true,
            scope: {
                project: '='
            },
            controller: function (Project, Authentication, RemoteFormValidation, FinancingRound, $q) {
                var vm = this;

                vm.pledge = {
                    amount: getPledgedAmountByCurrentUser()
                };

                FinancingRound.reloadCurrentRound();

                // to get the current user's budget
                vm.user = Authentication.reloadUser();

                vm.pledgeProject = function () {
                    vm.success = false;
                    vm.saving = true;
                    RemoteFormValidation.clearRemoteErrors(vm);

                    Project.pledge(vm.project.id, vm.pledge).$promise
                        .then(function () {
                            // load the project and user again to update the project details view to the new state
                            return reloadUserAndProject();
                        },
                        function (response) {
                            RemoteFormValidation.applyServerErrorResponse(vm, vm.form, response);

                            // most probably the project details or displayed user budget was outdated and the user "over-pledged"
                            reloadUserAndProject();

                            // return a rejected promise, or the .then() callback below will be called
                            return $q.reject(response);
                        })
                        .then(function () {
                            vm.success = true;
                        })
                        .finally(function () {
                            vm.pledge.amount = getPledgedAmountByCurrentUser();
                            vm.saving = false;
                            vm.form.$setPristine();
                        });
                };

                vm.getPledgableAmount = function () {
                    var remainingProjectGoal,
                        pledgedByCurrentUserSum = getPledgedAmountByCurrentUser();

                    if (isLoading()) {
                        return 0;
                    }

                    if (!FinancingRound.current.active || vm.project.status != 'PUBLISHED') {
                        return 0;
                    }

                    remainingProjectGoal = vm.project.pledgeGoal - vm.project.pledgedAmount;

                    return pledgedByCurrentUserSum + Math.min(remainingProjectGoal, vm.user.budget);
                };


                vm.getNotification = function () {
                    if (isLoading()) {
                        return null;
                    }

                    if (vm.success && vm.project.status == 'FULLY_PLEDGED') {
                        return {
                            type: 'success',
                            message: 'Deine Finanzierung war erfolgreich. Das Projekt ist jetzt zu 100% finanziert. Eine weitere Finanzierung ist nicht mehr möglich.'
                        };
                    }
                    if (vm.success) {
                        return {type: 'success', message: 'Deine Finanzierung war erfolgreich.'};
                    }
                    if (vm.project.status == 'FULLY_PLEDGED') {
                        return {type: 'info', message: 'Das Projekt ist zu 100% finanziert. Eine weitere Finanzierung ist nicht mehr möglich.'};
                    }
                    if (vm.project.status != 'PUBLISHED') {
                        return {type: 'info', message: 'Eine Finanzierung ist erst möglich, wenn das Projekt von einem Administrator veröffentlicht wurde.'};
                    }
                    if (!FinancingRound.current.active) {
                        return {type: 'info', message: 'Momentan läuft keine Finanzierungsrunde. Bitte versuche es nochmal, wenn die Finanzierungsrunde gestartet worden ist.'};
                    }
                    if (!vm.user.loggedIn) {
                        return {type: 'info', message: 'Bitte logge dich ein, um Projekte finanziell zu unterstützen.'};
                    }
                    if (vm.user.budget == 0) {
                        return {
                            type: 'info',
                            message: 'Dein Budget ist leider aufgebraucht. Du kannst dieses Projekt nicht weiter finanzieren. Bitte warte ab, bis die nächste Finanzierungsrunde startet, dann wird der Finanzierungstopf erneut auf alle Benutzer aufgeteilt.'
                        };
                    }

                    return null;
                };

                function getPledgedAmountByCurrentUser () {
                    return vm.project.pledgedAmountByRequestingUser || 0;
                }

                function isLoading() {
                    return !vm.project.$resolved || !vm.user.$resolved || !FinancingRound.current.$resolved;
                }

                function reloadUserAndProject() {
                    // parallel execution of backend calls
                    var promises = $q.all({
                        project: Project.get(vm.project.id).$promise,
                        user: Authentication.reloadUser().$promise,
                        financingRound: FinancingRound.reloadCurrentRound()
                    });

                    // will be resolved when all calls are completed
                    promises.then(function (resolvedPromises) {
                        angular.copy(resolvedPromises.project, vm.project);
                        // the user and financing round are already copied over in their services
                    });

                    return promises;
                }
            }
        };
    });
