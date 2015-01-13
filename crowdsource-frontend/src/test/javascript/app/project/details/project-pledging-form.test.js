describe('project pledging form', function () {

    var $scope, $compile, $httpBackend, Authentication;

    beforeEach(function() {
        module('crowdsource');
        module('crowdsource.templates');

        inject(function($rootScope, _$compile_, _$httpBackend_, _Authentication_) {
            $scope = $rootScope.$new();
            $compile = _$compile_;
            $httpBackend = _$httpBackend_;
            Authentication = _Authentication_;
        });
    });

    function compileDirective() {
        var root = $compile('<project-pledging-form project="project"></project-pledging-form>')($scope);
        $scope.$digest();
        return {
            root: root,
            slider: root.find('.range-slider'),
            pledgeAmount: new FormGroup(root.find('.form-controls-amount')),
            pledgeButton: root.find('button'),
            successMessage: root.find('.alert-box.success'),
            pledgedAmount: root.find('.pledged-amount'),
            pledgeGoal: root.find('.pledge-goal'),
            budget: root.find('.budget')
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
        spyOn(Authentication, 'isLoggedIn').and.returnValue(data.isLoggedIn);
        $httpBackend.expectGET('/user/current').respond(data.userResponse.statusCode, data.userResponse.body);
    }


    it("should add a pledge", function () {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 100, pledgedAmount: 60 },
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
        expect(elements.successMessage).toHaveClass('ng-hide');
        expect(elements.pledgedAmount).toHaveText('$90');
        expect(elements.pledgeGoal).toHaveText('$100');
        expect(elements.budget).toHaveText('$170');

        // prepare for backend call
        $httpBackend.expectPOST('/project/123/pledge', { amount: 30 }).respond(200);
        $httpBackend.expectGET('/project/123').respond(200, { id: 123, pledgeGoal: 100, pledgedAmount: 90 });

        // submit form
        elements.pledgeButton.click();
        $httpBackend.flush();

        // expect form to be in pristine state and with new values
        expect(elements.successMessage).not.toHaveClass('ng-hide');
        expect(elements.pledgeAmount.getInputField()).toHaveValue("0");
        expect(elements.pledgedAmount).toHaveText('$90');
        expect(elements.pledgeGoal).toHaveText('$100');
        expect(elements.budget).toHaveText('$170');

        expectNoValidationError(elements.pledgeAmount);
        expect(elements.pledgeButton).toBeDisabled();
    });

    it("should disable the form until the user budget is loaded", function() {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 100, pledgedAmount: 50 },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 20 } }
        });

        var elements = compileDirective();

        expect(elements.slider).toHaveClass('disabled');
        expect(elements.pledgeButton).toBeDisabled();
        expect(elements.pledgeAmount.getInputField()).toBeDisabled();
        expectNoValidationError(elements.pledgeAmount);

        $httpBackend.flush();

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

        angular.copy({ $resolved: true, id: 123, pledgeGoal: 100, pledgedAmount: 50 }, $scope.project);
        $scope.$digest();

        expect(elements.slider).not.toHaveClass('disabled');
        expect(elements.pledgeAmount.getInputField()).not.toBeDisabled();
        expectNoValidationError(elements.pledgeAmount);
    });

    it("should disable the form if the user has no budget", function () {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 100, pledgedAmount: 50 },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 0 } }
        });

        var elements = compileDirective();
        $httpBackend.flush();

        expect(elements.slider).toHaveClass('disabled');
        expect(elements.pledgeAmount.getInputField()).toBeDisabled();
        expectNoValidationError(elements.pledgeAmount);
    });

    it("should disable the form if the user is not logged in", function () {

        $scope.project = { $resolved: true, pledgeGoal: 100, pledgedAmount: 50 };
        spyOn(Authentication, 'isLoggedIn').and.returnValue(false);

        var elements = compileDirective();

        expect(elements.slider).toHaveClass('disabled');
        expect(elements.pledgeAmount.getInputField()).toBeDisabled();
        expectNoValidationError(elements.pledgeAmount);
    });

    it("should show a validation error message if a too low amount is entered", function () {

        prepareMocks({
            project: { $resolved: true, id: 123, pledgeGoal: 100, pledgedAmount: 50 },
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
            project: { $resolved: true, id: 123, pledgeGoal: 100, pledgedAmount: 50 },
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
            project: { $resolved: true, id: 123, pledgeGoal: 500, pledgedAmount: 50 },
            isLoggedIn: true,
            userResponse: { statusCode: 200, body: { budget: 200 } }
        });

        var elements = compileDirective();
        $httpBackend.flush();

        elements.pledgeAmount.getInputField().val('201').trigger('input');
        expectValidationError(elements.pledgeAmount, 'max');
        expect(elements.pledgeButton).toBeDisabled();
    });
});
