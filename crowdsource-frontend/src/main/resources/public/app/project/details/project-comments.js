angular.module('crowdsource')

    .directive('projectComments', function(Comment, Authentication, RemoteFormValidation) {
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
                    if (!vm.form.$valid) {
                        return;
                    }

                    vm.loading = true;

                    RemoteFormValidation.clearRemoteErrors(vm);

                    // work on a copy, or the message in the comment-list
                    // will be cleared in the .then() callback
                    var commentCopy = angular.copy(comment);
                    commentCopy.created = new Date();
                    commentCopy.userName = Authentication.currentUser.name;

                    Comment.add(vm.project.id, commentCopy).$promise
                        .then(function() {
                            vm.comments.push(commentCopy);

                            // clear the text area
                            vm.newComment.comment = '';

                            vm.form.$setPristine();

                            reloadComments();
                        })
                        .catch(function(response) {
                            RemoteFormValidation.applyServerErrorResponse(vm, vm.form, response);
                        })
                        .finally(function() {
                            vm.loading = false;
                        });
                };

                function reloadComments() {
                    // reload comments, do not assign the return value of Comment.getAll
                    // directly to vm.comments, instead wait for the call to finish
                    // and then assign it to prevent the list being cleared during loading
                    Comment.getAll(vm.project.id).$promise
                        .then(function (reloadedComments) {
                            vm.comments = reloadedComments;
                        });
                }
            }
        };
    });
