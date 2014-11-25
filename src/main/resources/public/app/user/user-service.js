angular.module('crowdsource')

    .factory('User', function ($resource) {
        var userResource = $resource('/user');

        return {
            register: function(user) {
                console.log('Fake registering', user);
            }
        };
    });