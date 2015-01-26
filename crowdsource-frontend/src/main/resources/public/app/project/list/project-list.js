angular.module('crowdsource')

    .controller('ProjectListController', function ($location, Project) {

        var vm = this;

        vm.projects = Project.getAll();

        vm.showProjectDetails = function (project) {
            $location.path('/project/' + project.id);
        };

        vm.projectOrder = function (project) {

            var projectState = project['status'];

            // scores for states
            var score = 1;
            var projectScores = {
                'PROPOSED': score++,
                'PUBLISHED': score++,
                'FULLY_PLEDGED': score++,
                'REJECTED': score++
            };

            // get score for state
            var projectScore = projectScores[projectState];

            // return score or max score
            return projectScore || score;
        }
    });
