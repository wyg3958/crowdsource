angular.module('crowdsource')

    .factory('User', function ($resource) {
        var userResource = $resource('/user');
        var userActivationResource = $resource('/user/:email/activation', { email: '@email' });

        return {
            register: function (user) {
                return userResource.save(user).$promise;
            },
            activate: function (user) {
                return userActivationResource.save(user);
            }
        };
    });