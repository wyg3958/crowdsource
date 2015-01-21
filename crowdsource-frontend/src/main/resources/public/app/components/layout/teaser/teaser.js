angular.module('crowdsource')

    .factory('TeaserMetrics', function() {
        var service = {};

        service.formatRemainingTime = function(untilDate) {
            var now = moment();
            var end = moment(untilDate);

            var diff = moment.duration(end.diff(now));
            if (diff.asSeconds() < 1) {
                return null;
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
            remainingTime += diff.seconds() + 's';

            return remainingTime;
        };

        return service;
    })

    .directive('teaser', function ($rootScope, $interval, TeaserMetrics, User, FinancingRound) {
        var directive = {};

        directive.controllerAs = 'teaser';
        directive.bindToController = true;
        directive.templateUrl = 'app/components/layout/teaser/teaser.html';

        directive.controller = function () {
            var vm = this;

            vm.show = false;

            // initialize to a string that evaluates to true to show the
            // 3 metrics items while loading instead of only two
            vm.remainingTime = " ";

            var activeRound = { $resolved: false };

            $rootScope.$on('$routeChangeSuccess', function (event, current) {
                vm.show = current.showTeaser;

                if (current.showTeaser) {
                    // refresh the data every time a route is changed and
                    // the teaser should be shown
                    loadData();
                }
            });

            $interval(applyRemainingTime, 1000);

            function loadData() {
                User.getMetrics().$promise.then(function(metrics) {
                    vm.userMetrics = metrics;
                });

                activeRound = FinancingRound.getActive();
                activeRound.$promise.then(function() {
                    // recalculate the remaining time right when the data is available,
                    // else it is first updated when the next $interval kicks in
                    applyRemainingTime();
                });
            }

            function applyRemainingTime() {
                if (!activeRound.$resolved) {
                    return;
                }

                vm.remainingTime = TeaserMetrics.formatRemainingTime(activeRound.endDate);
            }
        };

        return directive;
    });
