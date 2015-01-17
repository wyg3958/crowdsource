angular.module('crowdsource')

    .directive('projectComments', function(Comment) {
        return {
            templateUrl: 'app/project/details/project-comments.html',
            controllerAs: 'projectComments',
            bindToController: true,
            scope: {
                project: '='
            },
            controller: function () {
                var vm = this;

                vm.comments = Comment.getAll(vm.project.id);

                vm.storeComment = function (comment) {
                    vm.loading = true;

                    Comment.add(vm.project.id, comment).$promise
                        .then(function() {
                            vm.comments = Comment.getAll(vm.project.id);
                        })
                        .finally(function() {
                            vm.loading = false;
                        });
                }
            }
        };
    });
