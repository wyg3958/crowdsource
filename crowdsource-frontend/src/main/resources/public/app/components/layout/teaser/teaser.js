angular.module('crowdsource')

    .factory('Metrics', function() {
        var service = {};

        service.formatRemainingTime = function(untilDate) {
            var now = moment();
            var end = moment(untilDate);

            var diff = moment.duration(end.diff(now));
            if (diff.asSeconds() < 1) {
                return 'beendet';
            }

            var remainingTime = '';
            if (diff.days() > 0) {
                var days = Math.floor(diff.asDays());
                if (days > 6) {
                    return days + ' Tage';
                }
                remainingTime += days + 'd ';
            }
            if (remainingTime || diff.hours() > 0) {
                remainingTime += diff.hours() + 'h ';
            }
            if (remainingTime || diff.minutes() > 0) {
                remainingTime += diff.minutes() + 'm ';
            }
            remainingTime += diff.seconds() + 's ';

            return remainingTime;
        };

        return service;
    })

    .directive('teaser', function ($rootScope, $interval, Metrics, User, FinancingRound) {
        var directive = {};

        directive.controllerAs = 'teaser';
        directive.bindToController = true;
        directive.templateUrl = 'app/components/layout/teaser/teaser.html';

        directive.controller = function () {
            var vm = this;

            vm.show = false;

            $rootScope.$on('$routeChangeSuccess', function (event, current) {
                vm.show = current.showTeaser;
            });

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

                vm.remainingTime = Metrics.formatRemainingTime(vm.activeRound.endDate);
            }
        };

        return directive;
    });
