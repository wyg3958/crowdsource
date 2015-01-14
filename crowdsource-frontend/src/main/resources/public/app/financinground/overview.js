angular.module('crowdsource')

    .controller('FinancingRoundController', function ($routeParams, $location, FinancingRound) {

        var vm = this;

        vm.allFinancingRounds = FinancingRound.getAll();

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
