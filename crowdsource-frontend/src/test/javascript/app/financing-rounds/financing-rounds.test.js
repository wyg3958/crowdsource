describe('financing rounds', function () {

    var $scope, $httpBackend, $location, financingRounds;

    beforeEach(function () {
        module('crowdsource');
        module('crowdsource.templates');

        localStorage.clear(); // reset

        inject(function ($compile, $rootScope, $templateCache, $controller, _$location_, _$httpBackend_, FinancingRound) {
            $scope = $rootScope.$new();
            $httpBackend = _$httpBackend_;
            $location = _$location_;

            $controller('FinancingRoundsController as financingRounds', {
                $scope: $scope,
                $location: $location,
                $routeParams: {
                    projectId: 'xyz'
                },
                FinancingRound: FinancingRound
            });

            var template = $templateCache.get('app/financing-rounds/financing-rounds.html');
            financingRounds = $compile(template)($scope);
        });
    });


    it("should display correct elements when active round is present", function () {

        var startDate = moment();
        var endDate = startDate.add(5, 'days');

        $httpBackend.expectGET('/financingrounds').respond(200, [
            {
                "budget": "5555",
                "startDate": startDate.toISOString(),
                "endDate": endDate.toISOString(),
                "active": true
            }
        ]);

        $httpBackend.flush();
        $scope.$digest();

        expect(financingRounds.find('tbody .startdate')).toHaveText(startDate.format('DD.MM.YY HH:mm'));
        expect(financingRounds.find('tbody .enddate')).toHaveText(endDate.format('DD.MM.YY HH:mm'));
        expect(financingRounds.find('tbody .budget')).toHaveText('5.555');
        expect(financingRounds.find('.stop-button')).not.toBeDisabled();
        expect(financingRounds.find('.newround-start')).not.toExist();
        expect(financingRounds.find('form .notification')).toContainText('Es läuft bereits eine Finanzierungsrunde. Daher kann keine neue Runde gestartet werden.');
    });

    it("should display correct elements when no round is present", function () {
        prepareBackendGetWithNoRoundMock();
        $httpBackend.flush();
        $scope.$digest();

        expect(financingRounds.find('tbody .startdate')).not.toExist();
        expect(financingRounds.find('tbody .enddate')).not.toExist();
        expect(financingRounds.find('tbody .budget')).not.toExist();
        expect(financingRounds.find('.stop-button')).not.toExist();
        expect(financingRounds.find('.newround-start')).toBeDisabled();
        expect(financingRounds.find('form .notification')).not.toExist();

        expect(financingRounds.find('.newround-enddate')).toHaveProp('placeholder', 'Bitte klicken...');
        expect(financingRounds.find('.budget')).toHaveProp('placeholder', '0');
        expect(financingRounds.find('tbody td')).toContainText('Es wurde noch keine Finanzierungsrunde gestartet');
    });

//    it("should display error text when entering text in budget field", function () {
//        prepareBackendGetWithNoRoundMock();
//        $httpBackend.flush();
//        $scope.$digest();
//
//        console.log(financingRounds);
//
//
//        financingRounds.find('.newround-budget').val('a').trigger('input');
//        expect(financingRounds.find('span')).toHaveText('Bitte gib nur Ziffern ein');
//
//    });


    function fillAndSubmitForm() {

        financingRounds.find('.start-financing-round').click();
        //financingRounds.budget.getInputField().val('5555').trigger('input');

    }

    function prepareBackendGetWithActiveRoundMock() {
        $httpBackend.expectGET('/financingrounds').respond(200, [
            {
                "budget": "5555",
                "startDate": "2010-01-01T00:00:00.000Z",
                "endDate": "2020-12-31T00:00:00.000Z",
                "active": true
            }
        ]);
    }

    function prepareBackendGetWithNoRoundMock() {
        $httpBackend.expectGET('/financingrounds').respond(200, []);
    }
});