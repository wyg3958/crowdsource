angular.module('crowdsource')

    .directive('teaser', function ($interval, User, FinancingRound) {
        var directive = {};

        directive.controllerAs = 'teaser';
        directive.bindToController = true;
        directive.templateUrl = 'app/components/layout/teaser/teaser.html';

        directive.controller = function () {
            var vm = this;

            vm.userMetrics = User.getMetrics();

            vm.activeRound = FinancingRound.getActive();
            vm.activeRound.$promise.then(function() {
                applyRemainingTime();
                $interval(applyRemainingTime, 1000);
            });


            function applyRemainingTime() {
                if (!vm.activeRound.$resolved) {
                    return;
                }

                vm.remainingTime = getRemainingTime(vm.activeRound.endDate);
            }

            function getRemainingTime(untilDate) {
                var now = moment();
                var end = moment(untilDate);

                var diff = moment.duration(end.diff(now));
                return diff;
            }
        };

        return directive;
    });
