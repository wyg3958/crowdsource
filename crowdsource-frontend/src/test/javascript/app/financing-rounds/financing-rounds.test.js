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
            view = $compile(template)($scope);

            financingRounds = new FinancingRounds(view);
        });
    });


    it("should display correct elements when no round is present", function () {
        prepareViewWithNoRunningRound();

        expect(financingRounds.getTableStartDate()).not.toExist();
        expect(financingRounds.getTableEndDate()).not.toExist();
        expect(financingRounds.getTableBudget()).not.toExist();
        expect(financingRounds.getTableEndRoundButton()).not.toExist();
        expect(financingRounds.getStartRoundButton()).toBeDisabled();
        expect(financingRounds.getNotification()).not.toExist();
        expect(financingRounds.getEndDate().getInputField()).toHaveProp('placeholder', 'Bitte klicken...');
        expect(financingRounds.getBudget().getInputField()).toHaveProp('placeholder', '0');
        expect(financingRounds.getTableText()).toContainText('Es wurde noch keine Finanzierungsrunde gestartet');
    });


    it("should display correct elements when active round is present", function () {
        var startDate = moment().tz('Europe/Berlin');
        var endDate = startDate.add(5, 'days');

        prepareBackendGetWithOneRoundMock(startDate, endDate);
        $httpBackend.flush();
        $scope.$digest();

        expect(financingRounds.getTableStartDate()).toHaveText(startDate.format('DD.MM.YY HH:mm'));
        expect(financingRounds.getTableEndDate()).toHaveText(endDate.format('DD.MM.YY HH:mm'));
        expect(financingRounds.getTableBudget()).toHaveText('5.555');
        expect(financingRounds.getTableEndRoundButton()).not.toBeDisabled();
        expect(financingRounds.getStartRoundButton()).not.toExist();
        expect(financingRounds.getNotification()).toContainText('Es läuft bereits eine Finanzierungsrunde. Daher kann keine neue Runde gestartet werden.');
    });


    it("should display two finished rounds", function () {
        var date = moment().tz('Europe/Berlin');
        var startDate = date.subtract(10, 'days');
        var endDate = startDate.add(5, 'days');
        var row = '.row-1';

        prepareBackendGetWithTwoRoundsMock(startDate, endDate);
        $httpBackend.flush();
        $scope.$digest();

        expect(financingRounds.getTableStartDate()).toHaveText(startDate.format('DD.MM.YY HH:mm'));
        expect(financingRounds.getTableEndDate()).toHaveText(endDate.format('DD.MM.YY HH:mm'));
        expect(financingRounds.getTableBudget()).toHaveText('1.111');

        expect(financingRounds.getTableStartDate(row)).toHaveText(startDate.format('DD.MM.YY HH:mm'));
        expect(financingRounds.getTableEndDate(row)).toHaveText(endDate.format('DD.MM.YY HH:mm'));
        expect(financingRounds.getTableBudget(row)).toHaveText('2.222');

        expect(financingRounds.getTableEndRoundButton()).not.toExist();
        expect(financingRounds.getTableEndRoundButton(row)).not.toExist();

        expect(financingRounds.getStartRoundButton()).toExist();
        expect(financingRounds.getNotification()).not.toContainText('Es läuft bereits eine Finanzierungsrunde. Daher kann keine neue Runde gestartet werden.');
    });


    it("should display budget input field error messages when wrong data is given", function () {
        prepareViewWithNoRunningRound();

        financingRounds.getBudget().getInputField().val('1.23').trigger('input');
        expectBudgetValidationError('pattern');

        financingRounds.getBudget().getInputField().val('0').trigger('input');
        expectBudgetValidationError('min');

        financingRounds.getBudget().getInputField().val('').trigger('input');
        expectBudgetValidationError('required');
    });


    it("should send round data to backend and display it when valid data is submitted", function () {
        var startDate = moment().tz('Europe/Berlin');
        var endDate = startDate.add(5, 'days');
        var budget = 320;

        var modifiedEndDate = endDate.hour(23);
        modifiedEndDate = modifiedEndDate.minute(59);
        modifiedEndDate = modifiedEndDate.second(59);
        modifiedEndDate = modifiedEndDate.millisecond(0);

        prepareViewWithNoRunningRound();

        $httpBackend.expectPOST('/financinground', {
            "budget": budget,
            "endDate": endDate.toISOString()
        })
            .respond(200, {
                "id": "4711",
                "startDate": startDate.toISOString(),
                "endDate": modifiedEndDate.toISOString(),
                "budget": budget,
                "active": true
            });
        expect(financingRounds.getStartRoundButton()).toBeDisabled();

        financingRounds.getEndDate().getInputField().val(endDate.format('DD.MM.YYYY')).trigger('input');
        financingRounds.getBudget().getInputField().val('320').trigger('input');
        expect(financingRounds.getStartRoundButton()).not.toBeDisabled();


        $httpBackend.expectGET('/financingrounds').respond(200, [
            {
                "budget": "5555",
                "startDate": startDate.toISOString(),
                "endDate": endDate.toISOString(),
                "active": true
            }
        ]);

        financingRounds.getStartRoundButton().click();
        $httpBackend.flush();
        expect(financingRounds.getAlertBox()).toContainText('Finanzierungsrunde gestartet.');
    });


    it("should stop running round when stop is confirmed", function () {
        var startDate = moment().tz('Europe/Berlin');
        var endDate = startDate.add(5, 'days');

        prepareBackendGetWithOneRoundMock(startDate, endDate);
        $httpBackend.flush();
        $scope.$digest();

        $httpBackend.expectPUT('/financinground/4711/cancel', {})
            .respond(200, {
                "id": "4711",
                "startDate": startDate.toISOString(),
                "endDate": endDate.toISOString(),
                "budget": 4444,
                "active": false
            });
        prepareBackendGetWithOneRoundMock(startDate, endDate);

        expect(financingRounds.getTableEndRoundCancelButton()).not.toExist();

        financingRounds.getTableEndRoundButton().click();
        expect(financingRounds.getTableEndRoundCancelButton()).toExist();
        expect(financingRounds.getTableEndRoundConfirmMessage()).toContainText('Wirklich beenden?');

        financingRounds.getTableEndRoundButton().click();
        $httpBackend.flush();
        expect(financingRounds.getAlertBox()).toContainText('Finanzierungsrunde gestoppt.');
    });


    it("should not stop running round when stop is canceled", function () {
        var startDate = moment().tz('Europe/Berlin');
        var endDate = startDate.add(5, 'days');

        prepareBackendGetWithOneRoundMock(startDate, endDate);
        $httpBackend.flush();
        $scope.$digest();

        expect(financingRounds.getTableEndRoundCancelButton()).not.toExist();

        expect(financingRounds.getTableEndRoundButton()).toHaveText('Beenden');
        financingRounds.getTableEndRoundButton().click();
        expect(financingRounds.getTableEndRoundButton()).toHaveText('Ja');
        expect(financingRounds.getTableEndRoundCancelButton()).toExist();
        expect(financingRounds.getTableEndRoundConfirmMessage()).toContainText('Wirklich beenden?');

        financingRounds.getTableEndRoundCancelButton().click();
        expect(financingRounds.getTableEndRoundConfirmMessage()).not.toExist();
        expect(financingRounds.getTableEndRoundButton()).toHaveText('Beenden');
    });


    it("should display alert message when backend not responding on round start", function () {
        var startDate = moment().tz('Europe/Berlin');
        var endDate = startDate.add(5, 'days');
        var budget = 320;

        var modifiedEndDate = endDate.hour(23);
        modifiedEndDate = modifiedEndDate.minute(59);
        modifiedEndDate = modifiedEndDate.second(59);
        modifiedEndDate = modifiedEndDate.millisecond(0);

        prepareViewWithNoRunningRound();

        $httpBackend.expectPOST('/financinground', {
            "budget": budget,
            "endDate": modifiedEndDate.toISOString()
        }).respond(500, null);

        expect(financingRounds.getStartRoundButton()).toBeDisabled();

        financingRounds.getEndDate().getInputField().val(endDate.format('DD.MM.YYYY')).trigger('input');
        financingRounds.getBudget().getInputField().val('320').trigger('input');
        expect(financingRounds.getStartRoundButton()).not.toBeDisabled();

        financingRounds.getStartRoundButton().click();
        $httpBackend.flush();
        expect(financingRounds.getAlertBox()).toContainText('Fehler beim Starten der Finanzierungsrunde!');
    });

    it("should display alert message when backend not responding on round end", function () {
        var startDate = moment().tz('Europe/Berlin');
        var endDate = startDate.add(5, 'days');

        prepareBackendGetWithOneRoundMock(startDate, endDate);
        $httpBackend.flush();
        $scope.$digest();

        $httpBackend.expectPUT('/financinground/4711/cancel', {}).respond(500, null);

        financingRounds.getTableEndRoundButton().click();
        financingRounds.getTableEndRoundButton().click();
        $httpBackend.flush();

        expect(financingRounds.getAlertBox()).toContainText('Fehler beim Stoppen der Finanzierungsrunde!');
    });

    it("should display alert message when backend not responding on get rounds", function () {
        $httpBackend.expectGET('/financingrounds').respond(500, null);
        $httpBackend.flush();
        $scope.$digest();

        expect(financingRounds.getAlertBox()).toContainText('Fehler beim Abrufen der Finanzierungsrunden');
    });

    function prepareViewWithNoRunningRound() {
        prepareBackendGetWithNoRoundMock();

        $httpBackend.flush();
        $scope.$digest();
    }

    function prepareBackendGetWithOneRoundMock(startDate, endDate) {
        $httpBackend.expectGET('/financingrounds').respond(200, [
            {
                "id": "4711",
                "budget": "5555",
                "startDate": startDate.toISOString(),
                "endDate": endDate.toISOString(),
                "active": true
            }
        ]);
    }

    function prepareBackendGetWithTwoRoundsMock(startDate, endDate) {
        $httpBackend.expectGET('/financingrounds').respond(200, [
            {
                "budget": "1111",
                "startDate": startDate.toISOString(),
                "endDate": endDate.toISOString(),
                "active": false
            },
            {
                "budget": "2222",
                "startDate": startDate.toISOString(),
                "endDate": endDate.toISOString(),
                "active": false
            }
        ]);
    }

    function prepareBackendGetWithNoRoundMock() {
        $httpBackend.expectGET('/financingrounds').respond(200, []);
    }

    function expectBudgetValidationError(violatedRule) {
        expect(financingRounds.getBudget().getLabelContainer()).toHaveClass('error');
        expect(financingRounds.getBudget().getLabel()).toHaveClass('ng-hide');
        expect(financingRounds.getBudget().getErrorLabelsContainer()).not.toHaveClass('ng-hide');
        expect(financingRounds.getBudget().getErrorLabelForRule(violatedRule)).toExist();
    }
});