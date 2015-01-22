angular.module('crowdsource')

    .factory('Sorting', function () {
        var service = {};

        // Takes an array of objects
        // and returns an array of unique valued in the object array for a given key.
        service.unique = function (data, key) {
            var result = [];
            for (var i = 0; i < data.length; i++) {
                var value = data[i][key];
                if (result.indexOf(value) == -1) {
                    result.push(value);
                }
            }
            return result;
        };
        return service;
    })

    .controller('ProjectAdministrationController', function ($routeParams, $location, Project, Sorting) {

        var vm = this;

        vm.allProjects = Project.getAll();
        vm.allProjects.$promise
            .then(function () {
                vm.allStatuses = Sorting.unique(vm.allProjects, 'status');
            })
            .catch(function () {
                vm.info = "Fehler beim Abruf der Projektdaten";
            });

        vm.allStatuses = Sorting.unique(vm.allProjects, 'status');
    }
);