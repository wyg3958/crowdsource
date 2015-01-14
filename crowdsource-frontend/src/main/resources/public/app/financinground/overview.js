angular.module('crowdsource')

    .controller('FinancingRoundController', function ($routeParams, $location, FinancingRound) {

        var vm = this;

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
                end: new Date(fullEndDate),
                value: vm.budget
            };

            vm.saving = true;

            FinancingRound.start(vm.financingRound)
                .then(function () {
                    alert("Finanzierungsrunde gestartet.");
                })
                .catch(function (response) {
                    alert(response);
                })
                .finally(function () {
                    vm.saving = false;
                });
        };
    });
