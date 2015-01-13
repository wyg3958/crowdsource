angular.module('crowdsource')

    .directive('projectPledgingForm', function() {
        return {
            templateUrl: 'app/project/details/project-pledging-form.html',
            controllerAs: 'pledgingForm',
            bindToController: true,
            scope: {
                project: '='
            },
            controller: function(Project, User, $route) {
                var vm = this;

                vm.user = User.current();

                vm.pledgeProject = function() {
                    Project.pledge(vm.project.id, vm.pledge).$promise
                        .then(function() {
                            alert('Finanzierung erfolgreich');
                            $route.reload();
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
