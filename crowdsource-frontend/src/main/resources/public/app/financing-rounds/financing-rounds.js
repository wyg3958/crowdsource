angular.module('crowdsource')

    .controller('FinancingRoundController', function ($routeParams, $location, FinancingRound) {

        var vm = this;

        vm.alreadyActive = true;

        vm.allFinancingRounds = FinancingRound.getAll();


        vm.allFinancingRounds.$promise
            .then(function (allRounds) {

                var activeRoundFound = false;

                if (allRounds.length == 0) {
                    vm.alreadyActive = false;
                }

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

            //financing round should end at end of day
            var fullEndDate = new Date(+vm.endDate);
            fullEndDate.setHours(23);
            fullEndDate.setMinutes(59);
            fullEndDate.setSeconds(59);

            vm.financingRound = {
                end: fullEndDate.valueOf(),
                value: vm.budget
            };

            vm.saving = true;

            FinancingRound.start(vm.financingRound)
                .then(function () {
                    vm.allFinancingRounds = FinancingRound.getAll();
                    vm.alreadyActive = true;
                    alert("Finanzierungsrunde gestartet.");
                })
                .catch(function (response) {
                    console.log(response);
                    alert("Fehler beim Starten der Finanzierungsrunde!");
                })
                .finally(function () {
                    vm.saving = false;
                });
        };


        vm.stop = function (financingRound) {

            vm.stopping = true;

            FinancingRound.stop(financingRound)
                .then(function () {
                    vm.allFinancingRounds = FinancingRound.getAll();
                    vm.alreadyActive = false;
                    alert("Finanzierungsrunde gestoppt.");
                })
                .catch(function (response) {
                    console.log(response);
                    alert("Fehler beim Stoppen der Finanzierungsrunde!");
                })
                .finally(function () {
                    vm.stopping = false;
                });
        };
    });
