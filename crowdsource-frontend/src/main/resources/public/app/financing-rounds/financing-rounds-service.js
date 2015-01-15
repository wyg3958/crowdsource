angular.module('crowdsource')

    .factory('FinancingRound', function ($resource) {

        var financingRoundResource = $resource('/financinground');

        var stopFinancingRoundRessource = $resource('/financinground/:id/cancel', {}, {
            'update': {
                method: 'PUT'
            }
        });

        var financingRoundsResource = $resource('/financingrounds', {}, {
            query: {
                method: 'GET',
                isArray: true,
                transformResponse: function (data) {
                    var response = angular.fromJson(data);

                    angular.forEach(response, function (round) {
                        round.start = new Date(round.start);
                        round.end = new Date(round.end);
                    });
                    return response;
                }
            }
        });

        return {
            start: function (financingRound) {
                return financingRoundResource.save(financingRound).$promise;
            },
            stop: function (financingRound) {
                return stopFinancingRoundRessource.update({id: financingRound.id}, financingRound).$promise;
            },
            getAll: function () {
                return financingRoundsResource.query();
            }
        };
    });