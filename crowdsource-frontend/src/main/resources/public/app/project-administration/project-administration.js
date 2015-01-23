angular.module('crowdsource')


    .controller('ProjectAdministrationController', function ($routeParams, $location, Project) {

        var vm = this;

        vm.allProjects = Project.getAll();
        vm.allProjects.$promise
            .catch(function () {
                vm.info = "Fehler beim Abruf der Projektdaten";
            });

        vm.allStatuses = ["PROPOSED", "SAVED", "PUBLISHED", "FULLY_PLEDGED"];

//        vm.publish = function () {
//
//            if (!vm.form.$valid) {
//                return;
//            }
//
//            vm.publishing = true;
//
//            FinancingRound.start(vm.newRound)
//                .then(function () {
//                    vm.allFinancingRounds = FinancingRound.getAll();
//                    vm.info = "Finanzierungsrunde gestartet.";
//                })
//                .catch(function () {
//                    vm.info = "Fehler beim Starten der Finanzierungsrunde!";
//                })
//                .finally(function () {
//                    vm.saving = false;
//                });
//        };
    }
);