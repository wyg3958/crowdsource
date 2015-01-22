angular.module('crowdsource')


    .controller('ProjectAdministrationController', function ($routeParams, $location, Project) {

        var vm = this;

        vm.allProjects = Project.getAll();
        vm.allProjects.$promise
            .catch(function () {
                vm.info = "Fehler beim Abruf der Projektdaten";
            });

        vm.allStatuses = ["PROPOSED", "SAVED", "PUBLISHED", "FULLY_PLEDGED"];
    }
);