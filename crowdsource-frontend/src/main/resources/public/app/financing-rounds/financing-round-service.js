angular.module('crowdsource')

    .factory('FinancingRound', function ($resource, $q) {

        var financingRoundResource = $resource('/financinground/:id');

        var stopFinancingRoundResource = $resource('/financinground/:id/cancel', {}, {
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
                return stopFinancingRoundResource.update({id: financingRound.id}, {}).$promise;
            },
            getAll: function () {
                return financingRoundsResource.query();
            },
            getActive: function () {
                var deferred = $q.defer();

                var activeRound = financingRoundResource.get({ id: 'active' });
                activeRound.$promise
                    .then(function () {
                        deferred.resolve(activeRound);
                    })
                    .catch(function (response) {
                        // also resolve the deferred when a 404 is returned
                        // (this means that there is no active financing round atm)
                        if (response.status == 404) {
                            activeRound.active = false;
                            deferred.resolve(activeRound);
                        }
                        else {
                            deferred.reject(response);
                        }
                    });

                activeRound.$promise = deferred.promise;
                return activeRound;
            }
        };
    });