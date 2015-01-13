angular.module('crowdsource')

    .controller('FinancingRoundController', function ($routeParams, $location, FinancingRound) {

        var vm = this;

        vm.start = function () {

            if (!vm.form.$valid) {
                return;
            }
            console.log(vm.endDate);
            if (vm.endDate == undefined) {
                vm.form.endDate.$setValidity('valid_date', false);
                return;
            }

//            vm.saving = true;
//
//            FinancingRound.start().$promise
//                .then(function() {
//                    alert("Then");
//                })
//                .catch(function (response) {
//                    alert(response);
//                })
//                .finally(function() {
//                    vm.saving = false;
//                });
        };
    });
