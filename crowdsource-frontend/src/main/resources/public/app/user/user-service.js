angular.module('crowdsource')

    .factory('User', function ($resource) {

        var UserResource = $resource('/user/:id', {}, {
            current: {
                method: 'GET',
                params: { id: 'current' },
                transformResponse: function(responseString) {
                    var data = angular.fromJson(responseString);
                    data.loggedIn = true;
                    return data;
                }
            }
        });

        var UserActivationResource = $resource('/user/:email/activation', { email: '@email' });

        return {
            register: function (user) {
                return UserResource.save(user).$promise;
            },
            activate: function (user) {
                return UserActivationResource.save(user);
            },
            authenticated: function() {
                var user = UserResource.current();
                // already set the user as logged in, even if the server did not respond yet
                // we expect this call to be only called if there is an auth token available in the app
                user.loggedIn = true;
                return  user;
            },
            anonymous: function() {
                return new UserResource({
                    $resolved: true,
                    budget: 0,
                    loggedIn: false
                });
            }
        };
    });