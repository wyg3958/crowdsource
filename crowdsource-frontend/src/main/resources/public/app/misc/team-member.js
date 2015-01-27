angular.module('crowdsource')

    .directive('teamMember', function () {
        return {
            controllerAs: 'member',
            bindToController: true,
            templateUrl: 'app/misc/team-member.html',
            scope: {
                image: '@',
                email: '@',
                name: '@',
                role: '@'
            }
        };
    });
