angular.module('crowdsource')

    .controller('FinancingRoundController', function ($routeParams, $location, FinancingRound) {

        var vm = this;

        refreshView(true);

        vm.allFinancingRounds.$promise
            .then(function (allRounds) {

                var activeRoundFound = false;

                angular.forEach(allRounds, function (round) {
                    if (round.active == true) {
                        activeRoundFound = true;
                    }
                });
                vm.alreadyActive = activeRoundFound;
            });


        vm.start = function () {

            if (!vm.form.$valid) {
                return;
            }

            vm.saving = true;

            FinancingRound.start(vm.newRound)
                .then(function () {
                    refreshView(true);
                    alert("Finanzierungsrunde gestartet.");
                })
                .catch(function () {
                    alert("Fehler beim Starten der Finanzierungsrunde!");
                })
                .finally(function () {
                    vm.saving = false;
                });
        };


        vm.stop = function (financingRound) {

            if (!confirm("Wilst Du diese Runde wirklich vorzeitig beenden?")) {
                return;
            }

            vm.stopping = true;

            FinancingRound.stop(financingRound)
                .then(function () {
                    refreshView(false);
                    alert("Finanzierungsrunde gestoppt.");
                })
                .catch(function () {
                    alert("Fehler beim Stoppen der Finanzierungsrunde!");
                })
                .finally(function () {
                    vm.stopping = false;
                });
        };

        function refreshView(isFinancingRoundActive) {
            vm.alreadyActive = isFinancingRoundActive;
            vm.allFinancingRounds = FinancingRound.getAll();
        }

    });
