angular.module('crowdsource')

    .directive('confirmed', function ($interval, $timeout) {
        return {
            // compile the directive before the ng-click directive to be able to prevent the click event
            priority: -1,
            scope: {
                confirmText: '@?',
                confirmTimeout: '@?' // seconds
            },
            link: function(scope, elem) {
                var confirming = false;
                var textBeforeConfirming;

                scope.confirmTimeout = scope.confirmTimeout || 3;
                scope.confirmText = scope.confirmText || 'Wirklich?';

                elem.bind('click', function(e) {
                    if (!confirming) {
                        showConfirmation(e);

                        e.stopImmediatePropagation();
                        e.preventDefault();
                    }
                    else {
                        confirming = false;
                    }
                });

                function showConfirmation(e) {
                    textBeforeConfirming = elem.text();
                    elem.text(scope.confirmText + ' ' + scope.confirmTimeout);
                    confirming = true;

                    startCountdown();
                }

                function startCountdown() {
                    var repetitions = scope.confirmTimeout - 1;

                    $interval(function(counter) {
                        elem.text(scope.confirmText + ' ' + (repetitions - counter));
                    }, 1000, repetitions);

                    $timeout(function() {
                        elem.text(textBeforeConfirming);
                        confirming = false;
                    }, scope.confirmTimeout * 1000);
                }
            }
        };
    });
