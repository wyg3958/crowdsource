angular.module('crowdsource')

    .directive('navBar', function ($location, Authentication) {
        var directive = {};

        directive.controllerAs = 'nav';
        directive.bindToController = true;
        directive.templateUrl = 'app/components/layout/nav-bar/nav-bar.html';

        directive.controller = function () {
            var vm = this;

            vm.auth = Authentication;

            vm.getClassForMenuItem = function (location) {
                if ($location.path() == location) {
                    return 'current';
                }

                return '';
            };
        };

        return directive;
    });
