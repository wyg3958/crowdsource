angular.module('crowdsource')

    .factory('FinancingRound', function ($resource) {

        var financingRoundResource = $resource('/financinground');

        var financingRoundsResource = $resource('/financingrounds');

        return {
            start: function (financingRound) {
                return financingRoundResource.save(financingRound).$promise;
            },
            get: function () {
                return financingRoundsResource.get();
            }
        };
    });