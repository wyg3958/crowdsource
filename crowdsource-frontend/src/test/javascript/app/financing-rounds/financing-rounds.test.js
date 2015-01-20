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

        expect(financingRounds.getTableStartDate()).toHaveText(startDate.format('DD.MM.YY HH:mm'));
        expect(financingRounds.getTableEndtDate()).toHaveText(endDate.format('DD.MM.YY HH:mm'));
        expect(financingRounds.getTableBudget()).toHaveText('5.555');
        expect(financingRounds.getTableEndRoundButton()).not.toBeDisabled();
        expect(financingRounds.getStartRoundButton()).not.toExist();
        expect(financingRounds.getNotification()).toContainText('Es läuft bereits eine Finanzierungsrunde. Daher kann keine neue Runde gestartet werden.');
    });

    it("should display correct elements when no round is present", function () {
        prepareViewWithNoRunningRound();

        expect(financingRounds.getTableStartDate()).not.toExist();
        expect(financingRounds.getTableEndtDate()).not.toExist();
        expect(financingRounds.getTableBudget()).not.toExist();
        expect(financingRounds.getTableEndRoundButton()).not.toExist();
        expect(financingRounds.getStartRoundButton()).toBeDisabled();
        expect(financingRounds.getNotification()).not.toExist();


        expect(financingRounds.getEndDate().getInputField()).toHaveProp('placeholder', 'Bitte klicken...');


        expect(financingRounds.getBudget().getInputField()).toHaveProp('placeholder', '0');
        expect(financingRounds.getTableText()).toContainText('Es wurde noch keine Finanzierungsrunde gestartet');
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


    it("should post round data to backend when valid data is submitted", function () {
        var startDate = moment();
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

        financingRounds.getStartRoundButton().click();
    });


    function expectBackendCallAndRespond(statusCode, responseBody) {
        $httpBackend.expectPOST('/financingrounds', {
            "budget": "320",
            "endDate": endDate.toISOString()
        })
            .respond(statusCode, responseBody);
    }

    function fillAndSubmitForm() {

        financingRounds.find('.start-financing-round').click();
        //financingRounds.budget.getInputField().val('5555').trigger('input');

    }

    function prepareViewWithNoRunningRound() {
        prepareBackendGetWithNoRoundMock();

        $httpBackend.flush();
        $scope.$digest();
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