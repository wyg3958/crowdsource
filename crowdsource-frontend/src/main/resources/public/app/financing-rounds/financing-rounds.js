angular.module('crowdsource')

    .controller('FinancingRoundController', function ($routeParams, $location, FinancingRound) {

        var vm = this;

        vm.allFinancingRounds = FinancingRound.getAll();
        vm.allFinancingRounds.$promise.catch(function () {
            vm.info = "Fehler beim Abrufen der Finanzierungsrunden";
        });

        vm.start = function () {

            if (!vm.form.$valid) {
                return;
            }

            vm.saving = true;

            FinancingRound.start(vm.newRound)
                .then(function () {
                    vm.allFinancingRounds = FinancingRound.getAll();
                    vm.info = "Finanzierungsrunde gestartet.";
                })
                .catch(function () {
                    vm.info = "Fehler beim Starten der Finanzierungsrunde!";
                })
                .finally(function () {
                    vm.saving = false;
                });
        };


        vm.stop = function (financingRound) {

            // TODO: confirm dialog (if really needed. this is an admin tool)
            //if (!confirm("Wilst Du diese Runde wirklich vorzeitig beenden?")) {
            //    return;
            //}

            vm.stopping = true;

            FinancingRound.stop(financingRound)
                .then(function () {
                    vm.allFinancingRounds = FinancingRound.getAll();
                    vm.info = "Finanzierungsrunde gestoppt.";
                })
                .catch(function () {
                    vm.info = "Fehler beim Stoppen der Finanzierungsrunde!";
                })
                .finally(function () {
                    vm.stopping = false;
                });
        };

        vm.canStartNewRound = function () {
            if (!vm.allFinancingRounds.$resolved) {
                return false;
            }

            var activeRoundFound = false;

            angular.forEach(vm.allFinancingRounds, function (round) {
                if (round.active == true) {
                    activeRoundFound = true;
                    return false;
                }
            });

            return !activeRoundFound;
        };
    });
