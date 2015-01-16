describe('project pledging form', function () {

    var $scope, $compile, $httpBackend, AuthenticationToken;

    beforeEach(function() {
        module('crowdsource');
        module('crowdsource.templates');

        localStorage.clear(); // reset

        inject(function($rootScope, _$compile_, _$httpBackend_, _AuthenticationToken_) {
            $scope = $rootScope.$new();
            $compile = _$compile_;
            $httpBackend = _$httpBackend_;
            AuthenticationToken = _AuthenticationToken_;
        });
    });

    function compileDirective() {
        var root = $compile('<project-pledging-form project="project"></project-pledging-form>')($scope);
        $scope.$digest();
        return {
            root: root,
            slider: root.find('.range-slider'),
            pledgeAmount: new FormGroup(root.find('.form-controls-amount')),
            pledgableAmount: root.find('.pledgable-amount'),
            pledgeButton: root.find('button'),
            pledgedAmount: root.find('.pledged-amount'),
            pledgeGoal: root.find('.pledge-goal'),
            budget: root.find('.budget'),
            notification: root.find('.notification')
        };
    }

    function expectValidationError(formGroup, violatedRule) {
        expect(formGroup.getLabelContainer()).toHaveClass('error');
        expect(formGroup.getLabel()).toHaveClass('ng-hide');
        expect(formGroup.getErrorLabelsContainer()).not.toHaveClass('ng-hide');
        expect(formGroup.getErrorLabelForRule(violatedRule)).toExist();
    }

    function expectNoValidationError(formGroup) {
        expect(formGroup.getLabelContainer()).not.toHaveClass('error');
        expect(formGroup.getLabel()).not.toHaveClass('ng-hide');
        expect(formGroup.getErrorLabelsContainer()).toHaveClass('ng-hide');
    }

    function prepareMocks(data) {
        $scope.project = data.project;
        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(data.isLoggedIn);
        $httpBackend.expectGET('/user/current').respond(data.userResponse.statusCode, data.userResponse.body);
    }

    function getGeneralError(elements, errorCode) {
        return elements.root.find('[ng-message="' + errorCode + '"]');
    }


    it("should add a pledge", function () {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 100, pledgedAmount: 60, status: 'PUBLISHED' },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 200 } }
        });

        var elements = compileDirective();
        $httpBackend.flush();

        // type in 30
        elements.pledgeAmount.getInputField().val('30').trigger('input');

        // expect everything to have changed
        expectNoValidationError(elements.pledgeAmount);
        expect(elements.pledgeButton).not.toBeDisabled();
        expect(elements.pledgeButton).toHaveText('Jetzt finanzieren');
        expect(elements.notification).toHaveClass('ng-hide');
        expect(elements.pledgedAmount).toHaveText('$90');
        expect(elements.pledgeGoal).toHaveText('$100');
        expect(elements.budget).toHaveText('$170');
        expect(elements.pledgableAmount).toHaveText('$40');

        // prepare for backend calls
        $httpBackend.expectPOST('/project/123/pledge', { amount: 30 }).respond(200);
        $httpBackend.expectGET('/project/123').respond(200, { id: 123, pledgeGoal: 100, pledgedAmount: 90 });
        $httpBackend.expectGET('/user/current').respond(200, { budget: 170 });

        // submit form
        elements.pledgeButton.click();
        expect(elements.pledgeButton).toBeDisabled();
        expect(elements.pledgeButton).toHaveText('Bitte warten...');
        $httpBackend.flush();

        // expect form to be in pristine state and with new values
        expect(elements.notification).not.toHaveClass('ng-hide');
        expect(elements.notification).toHaveText('Deine Finanzierung war erfolgreich.');
        expect(elements.pledgeAmount.getInputField()).toHaveValue("0");
        expect(elements.pledgedAmount).toHaveText('$90');
        expect(elements.pledgeGoal).toHaveText('$100');
        expect(elements.budget).toHaveText('$170');
        expect(elements.pledgableAmount).toHaveText('$10');

        expectNoValidationError(elements.pledgeAmount);
        expect(elements.pledgeButton).toBeDisabled();
        expect(elements.pledgeButton).toHaveText('Jetzt finanzieren');
        expect(elements.root.find('.general-error')).not.toExist();
    });

    it("should disable the form until the user budget is loaded", function() {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 100, pledgedAmount: 50, status: 'PUBLISHED' },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 20 } }
        });

        var elements = compileDirective();

        expect(elements.notification).toHaveClass('ng-hide');
        expect(elements.slider).toHaveClass('disabled');
        expect(elements.pledgeButton).toBeDisabled();
        expect(elements.pledgeAmount.getInputField()).toBeDisabled();
        expectNoValidationError(elements.pledgeAmount);

        $httpBackend.flush();

        expect(elements.notification).toHaveClass('ng-hide');
        expect(elements.slider).not.toHaveClass('disabled');
        expect(elements.pledgeAmount.getInputField()).not.toBeDisabled();
        expectNoValidationError(elements.pledgeAmount);
    });

    it("should disable the form until the project details are loaded", function () {

        prepareMocks({
            project: { $resolved: false },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 20 } }
        });

        var elements = compileDirective();
        $httpBackend.flush();

        expect(elements.slider).toHaveClass('disabled');
        expect(elements.pledgeAmount.getInputField()).toBeDisabled();
        expectNoValidationError(elements.pledgeAmount);

        angular.copy({ $resolved: true, id: 123, pledgeGoal: 100, pledgedAmount: 50, status: 'PUBLISHED' }, $scope.project);
        $scope.$digest();

        expect(elements.slider).not.toHaveClass('disabled');
        expect(elements.pledgeAmount.getInputField()).not.toBeDisabled();
        expectNoValidationError(elements.pledgeAmount);
    });

    it("should disable the form if the user has no budget", function () {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 100, pledgedAmount: 50, status: 'PUBLISHED' },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 0 } }
        });

        var elements = compileDirective();
        $httpBackend.flush();

        expect(elements.slider).toHaveClass('disabled');
        expect(elements.pledgeAmount.getInputField()).toBeDisabled();
        expect(elements.pledgableAmount).toHaveText('$0');
        expect(elements.budget).toHaveText('$0');
        expectNoValidationError(elements.pledgeAmount);
    });

    it("should disable the form if the user is not logged in", function () {

        $scope.project = { $resolved: true, pledgeGoal: 100, pledgedAmount: 50, status: 'PUBLISHED' };
        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(false);

        var elements = compileDirective();

        expect(elements.slider).toHaveClass('disabled');
        expect(elements.pledgeAmount.getInputField()).toBeDisabled();
        expectNoValidationError(elements.pledgeAmount);
    });

    it("should show a validation error message if a too low amount is entered", function () {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 100, pledgedAmount: 50, status: 'PUBLISHED' },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 100 } }
        });

        var elements = compileDirective();
        $httpBackend.flush();

        elements.pledgeAmount.getInputField().val('0').trigger('input');
        expectValidationError(elements.pledgeAmount, 'min');
        expect(elements.pledgeButton).toBeDisabled();
    });

    it("should show a validation error message if the entered pledge amount exceeds the pledge goal", function () {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 100, pledgedAmount: 50, status: 'PUBLISHED' },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 200 } }
        });

        var elements = compileDirective();
        $httpBackend.flush();

        elements.pledgeAmount.getInputField().val('51').trigger('input');
        expectValidationError(elements.pledgeAmount, 'max');
        expect(elements.pledgeButton).toBeDisabled();
    });

    it("should show a validation error message if the entered pledge amount exceeds the user budget", function () {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 500, pledgedAmount: 50, status: 'PUBLISHED' },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 200 } }
        });

        var elements = compileDirective();
        $httpBackend.flush();

        elements.pledgeAmount.getInputField().val('201').trigger('input');
        expectValidationError(elements.pledgeAmount, 'max');
        expect(elements.pledgeButton).toBeDisabled();
    });

    it("should show a validation error message if the entered pledge amount is no even number", function () {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 500, pledgedAmount: 50, status: 'PUBLISHED' },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 200 } }
        });

        var elements = compileDirective();
        $httpBackend.flush();

        elements.pledgeAmount.getInputField().val('1.2').trigger('input');
        expectValidationError(elements.pledgeAmount, 'pattern');
        expect(elements.pledgeButton).toBeDisabled();
    });

    it("should recover from a over-pledge", function() {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 500, pledgedAmount: 50, status: 'PUBLISHED' },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 200 } }
        });

        var elements = compileDirective();
        $httpBackend.flush();

        // type in 30
        elements.pledgeAmount.getInputField().val('30').trigger('input');

        // prepare for backend call
        $httpBackend.expectPOST('/project/123/pledge', { amount: 30 }).respond(400, { errorCode: 'pledge_goal_exceeded' });
        $httpBackend.expectGET('/project/123').respond(200, { id: 123, pledgeGoal: 500, pledgedAmount: 480 }); // the pledged amount is 480 now!
        $httpBackend.expectGET('/user/current').respond(200, { budget: 200 });

        // submit form
        elements.pledgeButton.click();

        expect(elements.pledgeButton).toBeDisabled();
        expect(elements.pledgeButton).toHaveText('Bitte warten...');

        $httpBackend.flush();

        // expect form to be updated with the new values from backend
        expect(elements.notification).toHaveClass('ng-hide');
        expect(elements.pledgeAmount.getInputField()).toHaveValue("30");
        expect(elements.pledgedAmount).toHaveText('$480');
        expect(elements.pledgeGoal).toHaveText('$500');
        expect(elements.budget).toHaveText('$200');
        expect(elements.pledgableAmount).toHaveText('$20');

        expect(elements.root.find('.general-error')).toExist();
        expect(getGeneralError(elements, 'remote_pledge_goal_exceeded')).toExist();
        expectValidationError(elements.pledgeAmount, 'max');
        expect(elements.pledgeButton).toBeDisabled();
        expect(elements.pledgeButton).toHaveText('Jetzt finanzieren');

        // retry with 20
        elements.pledgeAmount.getInputField().val('10').trigger('input');

        expect(elements.pledgeButton).not.toBeDisabled();

        // prepare for backend calls
        $httpBackend.expectPOST('/project/123/pledge', { amount: 10 }).respond(200);
        $httpBackend.expectGET('/project/123').respond(200, { id: 123, pledgeGoal: 500, pledgedAmount: 490 });
        $httpBackend.expectGET('/user/current').respond(200, { budget: 190 });

        // submit form
        elements.pledgeButton.click();

        expect(elements.root.find('.general-error')).not.toExist();
        expect(getGeneralError(elements, 'remote_pledge_goal_exceeded')).not.toExist();
        expect(elements.pledgeButton).toBeDisabled();
        expect(elements.pledgeButton).toHaveText('Bitte warten...');

        $httpBackend.flush();

        // expect form to be in pristine state and with new values
        expect(elements.notification).not.toHaveClass('ng-hide');
        expect(elements.notification).toHaveText('Deine Finanzierung war erfolgreich.');
        expect(elements.pledgeAmount.getInputField()).toHaveValue("0");
        expect(elements.pledgedAmount).toHaveText('$490');
        expect(elements.pledgeGoal).toHaveText('$500');
        expect(elements.budget).toHaveText('$190');
        expect(elements.pledgableAmount).toHaveText('$10');

        expectNoValidationError(elements.pledgeAmount);
        expect(elements.pledgeButton).toBeDisabled();
        expect(elements.pledgeButton).toHaveText('Jetzt finanzieren');
    });

    it("should show a message that the user has no budget anymore", function () {
        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 500, pledgedAmount: 50, status: 'PUBLISHED' },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 0 } }
        });

        var elements = compileDirective();
        $httpBackend.flush();

        expect(elements.notification).not.toHaveClass('ng-hide');
        expect(elements.notification).toHaveText('Dein Budget ist leider aufgebraucht. Du kannst dieses Projekt nicht weiter finanzieren. Bitte warte ab, bis die nächste Finanzierungsrunde startet, dann wird der Finanzierungstopf erneut auf alle Benutzer aufgeteilt.');
    });

    it("should show a message that the user is not logged in", function () {

        $scope.project = { $resolved: true, pledgeGoal: 100, pledgedAmount: 50, status: 'PUBLISHED' };
        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(false);

        var elements = compileDirective();

        expect(elements.notification).not.toHaveClass('ng-hide');
        expect(elements.notification).toHaveText('Bitte logge dich ein, um Projekte finanziell zu unterstützen.');
    });

    it("should show a message that the project is fully pledged", function () {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 500, pledgedAmount: 50, status: 'FULLY_PLEDGED' },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 0 } }
        });

        var elements = compileDirective();
        $httpBackend.flush();

        expect(elements.notification).not.toHaveClass('ng-hide');
        expect(elements.notification).toHaveText('Das Project ist zu 100% finanziert. Eine weitere Finanzierung ist nicht mehr möglich.');
    });
});
