angular.module('crowdsource')

    .directive('statusBar', function($rootScope, $window, Authentication, FinancingRound) {
        return {
            controllerAs: 'status',
            bindToController: true,
            templateUrl: 'app/components/layout/status-bar/status-bar.html',
            controller: function() {
                var vm = this;

                $rootScope.$on('$routeChangeSuccess', function (event, current) {
                    vm.breadcrump = current.title;
                    $window.document.title = "AS CrowdSource - " + current.title;
                });

                vm.auth = Authentication;

                vm.financingRound = FinancingRound;
            }
        };
    });
