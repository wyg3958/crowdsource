angular.module('crowdsource')

    .directive('statusBar', function(Authentication) {
        return {
            controllerAs: 'status',
            bindToController: true,
            templateUrl: 'app/components/layout/status-bar/status-bar.html',
            controller: function() {
                var vm = this;

                vm.auth = Authentication;
            }
        };
    });
