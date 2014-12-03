describe('user activation view', function () {

    var $scope, $httpBackend, $location, activationForm;

    beforeEach(function() {
        module('crowdsource');
        module('crowdsource.templates');

        inject(function($compile, $rootScope, $templateCache, $controller, _$httpBackend_, User, RemoteFormValidation) {
            $scope = $rootScope.$new();
            $httpBackend = _$httpBackend_;

            $controller('UserActivationController', {
                $scope: $scope,
                $routeParams: {
                    email: "test@axelspringer.de",
                    activationToken: "12345"
                },
                User: User,
                RemoteFormValidation: RemoteFormValidation
            });

            var template = $templateCache.get('app/user/activation/user-activation.html');
            activationForm = new ActivationForm($compile(template)($scope));
            $scope.$digest();
        });
    });

    function expectValidationError(inputName, violatedRule) {
        expect(activationForm[inputName].getLabelContainer()).toHaveClass('error');
        expect(activationForm[inputName].getLabel()).toHaveClass('ng-hide');
        expect(activationForm[inputName].getErrorLabelsContainer()).not.toHaveClass('ng-hide');
        expect(activationForm[inputName].getErrorLabelForRule(violatedRule)).toExist();
    }

    function expectNoValidationError(inputName) {
        expect(activationForm[inputName].getLabelContainer()).not.toHaveClass('error');
        expect(activationForm[inputName].getLabel()).not.toHaveClass('ng-hide');
        expect(activationForm[inputName].getErrorLabelsContainer()).toHaveClass('ng-hide');
    }

    function fillAndSubmitForm() {
        activationForm.password.getInputField().val('secret').trigger('input');
        activationForm.repeatedPassword.getInputField().val('secret').trigger('input');

        activationForm.getSubmitButton().click();
    }

    function fillAndSubmitFormAndFlush() {
        fillAndSubmitForm();
        $httpBackend.flush();
    }

    function expectBackendCallAndRespond(statusCode, responseBody) {
        $httpBackend.expectPOST('/user/test@axelspringer.de/activation/12345', {"email":"test@axelspringer.de","password":"secret"}).respond(statusCode, responseBody);
    }


    it('should show no validation errors when the form is untouched', function () {
        expect(activationForm.getGeneralError()).not.toExist();

        expectNoValidationError('password');
        expectNoValidationError('repeatedPassword');
    });

    it('should POST the data to the server and TODO...', function() {
        expectBackendCallAndRespond(201);

        fillAndSubmitFormAndFlush();

        // TODO: once implemented, test whatever happens after successfull server response
    });

    it('should disable the submit button and change it\'s text while loading', function() {
        expectBackendCallAndRespond(201);
        expect(activationForm.getSubmitButton()).toHaveText('Speichern');
        expect(activationForm.getSubmitButton()).not.toBeDisabled();

        fillAndSubmitForm();

        expect(activationForm.getSubmitButton()).toHaveText('Speichern...');
        expect(activationForm.getSubmitButton()).toBeDisabled();

        $httpBackend.flush();

        expect(activationForm.getSubmitButton()).toHaveText('Speichern');
        expect(activationForm.getSubmitButton()).not.toBeDisabled();
    });

    it('should show a general error when the server responds with 500', function() {
        expectBackendCallAndRespond(500);

        fillAndSubmitFormAndFlush();

        expect(activationForm.getGeneralError()).toExist();
        // TODO: once implemented, test that the user login is not attempted
    });

    it('should show "required" validation errors when the form is submitted without touching the input fields', function () {
        activationForm.getSubmitButton().click();

        expectValidationError('password', 'required');
        expectValidationError('repeatedPassword', 'required');
    });

    it('should show a validation error if the password is changed to blank', function () {
        activationForm.password.getInputField().val('secure').trigger('input');
        activationForm.password.getInputField().val('').trigger('input');

        expectValidationError('password', 'required');
    });

    it('should show a validation error if the repeated password is changed to blank', function () {
        activationForm.repeatedPassword.getInputField().val('secure').trigger('input');
        activationForm.repeatedPassword.getInputField().val('').trigger('input');

        expectValidationError('repeatedPassword', 'required');
    });

    it('should show a validation error if different passwords are entered once the form is submitted', function () {
        activationForm.password.getInputField().val('secure').trigger('input');
        activationForm.repeatedPassword.getInputField().val('something else').trigger('input');

        expectNoValidationError('repeatedPassword');

        activationForm.getSubmitButton().click();

        expectValidationError('repeatedPassword', 'remote_equal');
    });

    it('should clear the remote_equal validation error once the user starts typing again', function() {
        activationForm.password.getInputField().val('secure').trigger('input');
        activationForm.repeatedPassword.getInputField().val('something else').trigger('input');

        activationForm.getSubmitButton().click();

        activationForm.repeatedPassword.getInputField().val('secure').trigger('input');

        expectNoValidationError('repeatedPassword');
    });
});
