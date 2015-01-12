angular.module('crowdsource')

    .factory('User', function ($resource) {

        var userResource = $resource('/user/:id');

        var userActivationResource = $resource('/user/:email/activation', { email: '@email' });

        return {
            register: function (user) {
                return userResource.save(user).$promise;
            },
            activate: function (user) {
                return userActivationResource.save(user);
            },
            current: function() {
                return userResource.get({ id: 'current' });
            }
        };
    });