angular.module('crowdsource')

    .directive('projectPledgingForm', function() {
        return {
            templateUrl: 'app/project/details/project-pledging-form.html',
            controllerAs: 'pledgingForm',
            bindToController: true,
            scope: {
                project: '='
            },
            controller: function(Project, $route) {
                var vm = this;

                vm.pledgeProject = function() {
                    Project.pledge(vm.project.id, vm.pledge).$promise
                        .then(function() {
                            alert('Finanzierung erfolgreich');
                            $route.reload();
                        })
                        .catch(function() {
                            alert('Zu viel');
                        });
                };
            }
        };
    });
