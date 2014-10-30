angular.module('crowdsource').factory('helloservice', function ($resource) {
    return $resource('hello', {}, {})
});
