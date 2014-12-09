angular.module('crowdsource')

    .controller('ProjectFormController', function($scope, $location, Project, RemoteFormValidation) {

        $scope.submitProjectIdea = function() {
            if (!$scope.projectForm.$valid) {
                return;
            }

            RemoteFormValidation.clearRemoteErrors($scope);
            $scope.loading = true;

            Project.add($scope.project)
                .then(function() {
                    $location.path('/project/new/success');
                })
                .catch(function(response) {
                    RemoteFormValidation.applyServerErrorResponse($scope, $scope.projectForm, response);
                })
                .finally(function() {
                    $scope.loading = false;
                });
        };

    });
