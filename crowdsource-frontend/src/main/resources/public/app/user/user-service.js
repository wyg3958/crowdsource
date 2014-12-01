angular.module('crowdsource')

    .factory('User', function ($resource) {
        var userResource = $resource('/user');

        return {
            register: function (user) {
                return userResource.save(user).$promise;
            },
            activate: function (user, activationToken) {
                console.log('MOCK! User activation', user, activationToken);
            }
        };
    });