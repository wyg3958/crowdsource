angular.module('crowdsource')

    .factory('User', function (Authentication, $resource) {

        var UserResource = $resource('/user/:id');

        var UserActivationResource = $resource('/user/:email/activation', { email: '@email' });

        return {
            register: function (user) {
                return UserResource.save(user).$promise;
            },
            activate: function (user) {
                return UserActivationResource.save(user);
            },
            current: function() {
                if (Authentication.isLoggedIn()) {
                    var user = UserResource.get({ id: 'current' });

                    user.$promise.then(function(data) {
                        data.loggedIn = true;
                    });

                    return user;
                }
                else {
                    return new UserResource({
                        $resolved: true,
                        budget: 0,
                        loggedIn: false
                    });
                }
            }
        };
    });