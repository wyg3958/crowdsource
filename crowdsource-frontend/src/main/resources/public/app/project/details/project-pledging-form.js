angular.module('crowdsource')

    .directive('projectPledgingForm', function() {
        return {
            templateUrl: 'app/project/details/project-pledging-form.html',
            controllerAs: 'pledgingForm',
            bindToController: true,
            scope: {
                project: '='
            },
            controller: function(Project, User) {
                var vm = this;

                vm.user = User.current();

                vm.pledgeProject = function() {
                    vm.success = false;

                    Project.pledge(vm.project.id, vm.pledge).$promise
                        .then(function() {
                            // load the project again to update the project to the new state
                            // (mainly to update the "backers" property, as this cannot be
                            // easily identified on the client
                            return Project.get(vm.project.id).$promise;
                        })
                        .then(function(updatedProject) {
                            angular.copy(updatedProject, vm.project);

                            vm.success = true;
                            vm.user.budget -= vm.pledge.amount;
                            vm.pledge.amount = 0;

                            vm.form.$setPristine();
                        })
                        .catch(function() {
                            alert('Zu hoher Finanzierungsbetrag oder nicht eingeloggt');
                        });
                };

                vm.getPledgableAmount = function() {
                    if (!vm.project.$resolved || !vm.user.$resolved) {
                        return 0;
                    }

                    var remainingProjectGoal = vm.project.pledgeGoal - vm.project.pledgedAmount;
                    return Math.min(remainingProjectGoal, vm.user.budget);
                };
            }
        };
    });
