angular.module('crowdsource')

    .factory('FinancingRound', function ($resource, $q) {

        var service = {};

        var financingRoundResource = $resource('/financingrounds/:id');

        var stopFinancingRoundRessource = $resource('/financingrounds/:id/cancel', {}, {
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

        service.start = function (financingRound) {
            return financingRoundResource.save(financingRound).$promise;
        };

        service.stop = function (financingRound) {
            return stopFinancingRoundRessource.update({id: financingRound.id}, {}).$promise;
        };

        service.getAll = function () {
            return financingRoundsResource.query();
        };

        service.reloadCurrentRound = function () {
            var promise = getCurrent();
            service.current.$promise = promise;

            promise.then(function (currentRound) {
                service.current = currentRound;
            });
            return promise;
        };

        function getCurrent() {
            var deferred = $q.defer();

            var currentRound = financingRoundResource.get({id: 'mostRecent'});
            currentRound.$promise
                .then(function () {
                    deferred.resolve(currentRound);
                })
                .catch(function (response) {
                    // also resolve the deferred when a 404 is returned
                    // (this means that there is no active financing round atm)
                    if (response.status == 404) {
                        currentRound.active = false;
                        deferred.resolve(currentRound);
                    }
                    else {
                        deferred.reject(response);
                    }
                });

            currentRound.$promise = deferred.promise;
            return deferred.promise;
        }

        service.current = {$resolved: false};

        service.currentFinancingRound = function(){
            return service.current;
        };

        return service;
    });