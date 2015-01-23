angular.module('crowdsource')

    .directive('teaser', function ($interval, $timeout, Route, TeaserMetrics, User, FinancingRound) {
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

            Route.onRouteChangeSuccessAndInit(function (event, current, previous) {
                var wasPageLoad = previous === undefined;
                render(current, wasPageLoad);
            });

            $interval(applyRemainingTime, 1000);

            function render(currentRoute, wasPageLoad) {
                vm.show = currentRoute.showTeaser;

                if (currentRoute.showTeaser) {
                    // refresh the data every time a route is changed and
                    // the teaser should be shown
                    loadData(wasPageLoad);
                }
            }

            function loadData(wasPageLoad) {
                User.getMetrics().$promise.then(function(metrics) {
                    vm.userMetrics = metrics;
                });

                // The FinancingRound service already calls reloadCurrentRound on initalization.
                // No need to do it twice.
                if (wasPageLoad) {
                    FinancingRound.current.$promise.then(function () {
                        applyRemainingTime();
                    });
                }
                else {
                    FinancingRound.reloadCurrentRound().then(function () {
                        // recalculate the remaining time right when the data is available,
                        // else it is first updated when the next $interval kicks in
                        applyRemainingTime();
                    });
                }
            }

            function applyRemainingTime() {
                if (!FinancingRound.current.$resolved) {
                    return;
                }

                vm.remainingTime = TeaserMetrics.formatRemainingTime(FinancingRound.current.endDate);

                // financing round time is now over
                if (FinancingRound.current.active && !vm.remainingTime) {
                    // reload the data 500ms later (because the time of the browser could be out of sync with the server time)
                    $timeout(function() {
                        loadData(false);
                    }, 500);
                }
            }
        };

        return directive;
    })

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
    });
