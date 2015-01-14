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
            fullEndDate.setUTCHours(23);
            fullEndDate.setUTCMinutes(59);
            fullEndDate.setUTCSeconds(59);

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
    });
