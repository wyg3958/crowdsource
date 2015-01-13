angular.module('crowdsource')

    .factory('FinancingRound', function ($resource) {

        var financingRoundResource = $resource('/financinground');

        var financingRoundsResource = $resource('/financingrounds');

        return {
            start: function () {
                return financingRoundResource.save().$promise;
            },
            get: function () {
                return financingRoundsResource.get();
            }
        };
    });