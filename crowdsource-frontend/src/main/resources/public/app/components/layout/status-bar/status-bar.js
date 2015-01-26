angular.module('crowdsource')

    .directive('statusBar', function($rootScope, $window, Route, Authentication, FinancingRound) {
        return {
            controllerAs: 'status',
            bindToController: true,
            templateUrl: 'app/components/layout/status-bar/status-bar.html',
            controller: function() {
                var vm = this;

                vm.auth = Authentication;

                vm.financingRound = FinancingRound;

                Route.onRouteChangeSuccessAndInit(function (event, current) {
                    updateView(current);
                });

                function updateView (currentRoute) {
                    var title = "AS CrowdSource";
                    if (currentRoute.title) {
                        title += " - " + currentRoute.title;
                    }

                    $window.document.title = title;
                    vm.breadcrump = currentRoute.title;
                }
            }
        };
    });
