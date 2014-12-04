describe('user activation view', function () {

    var $httpBackend, activationForm;

    beforeEach(function() {
        module('crowdsource');
        module('crowdsource.templates');

        inject(function($compile, $rootScope, $templateCache, $controller, _$httpBackend_, User, Authentication, RemoteFormValidation) {
            var $scope = $rootScope.$new();
            $httpBackend = _$httpBackend_;

            $controller('UserActivationController', {
                $scope: $scope,
                $routeParams: {
                    email: "test@axelspringer.de",
                    activationToken: "12345"
                },
                User: User,
                Authentication: Authentication,
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
        activationForm.password.getInputField().val('secret!!!').trigger('input');
        activationForm.repeatedPassword.getInputField().val('secret!!!').trigger('input');

        activationForm.getSubmitButton().click();
    }

    function expectBackendActivationCallAndRespond(statusCode, responseBody) {
        $httpBackend.expectPOST('/user/test@axelspringer.de/activation', {
            "email": "test@axelspringer.de", // actually not needed, but will be ignored by the backend
            "password": "secret!!!",
            "repeatedPassword": "secret!!!", // actually not needed for the backend, but will be ignored by the backend
            "activationToken": "12345"
        })
        .respond(statusCode, responseBody);
    }

    function expectBackendLoginCallAndRespond(statusCode, responseBody) {
        $httpBackend.expectPOST('/oauth/token', 'username=test%40axelspringer.de&password=secret!!!&client_id=web&grant_type=password')
            .respond(statusCode, responseBody);
    }


    it('should show no validation errors when the form is untouched', function () {
        expect(activationForm.getGeneralErrorsContainer()).not.toExist();

        expectNoValidationError('password');
        expectNoValidationError('repeatedPassword');
    });

    it('should show no validation errors when the form is filled correctly', function () {
        activationForm.password.getInputField().val('secret!!!').trigger('input');
        activationForm.repeatedPassword.getInputField().val('secret!!!').trigger('input');

        expect(activationForm.getGeneralErrorsContainer()).not.toExist();

        expectNoValidationError('password');
        expectNoValidationError('repeatedPassword');
    });

    it('should POST the data to the server, request an access token and TODO...', function() {
        expectBackendActivationCallAndRespond(201);
        expectBackendLoginCallAndRespond(200);

        fillAndSubmitForm();
        $httpBackend.flush();
        // TODO: once implemented, test whatever happens after successful server response
    });

    it('should disable the submit button and change it\'s text while loading', function() {
        expectBackendActivationCallAndRespond(201);
        expectBackendLoginCallAndRespond(200);

        expect(activationForm.getSubmitButton()).toHaveText('Speichern');
        expect(activationForm.getSubmitButton()).not.toBeDisabled();

        fillAndSubmitForm();

        expect(activationForm.getSubmitButton()).toHaveText('Speichern...');
        expect(activationForm.getSubmitButton()).toBeDisabled();

        $httpBackend.flush();

        expect(activationForm.getSubmitButton()).toHaveText('Speichern');
        expect(activationForm.getSubmitButton()).not.toBeDisabled();
    });

    it('should show an unknown error when the server responds with 500', function() {
        expectBackendActivationCallAndRespond(500);

        fillAndSubmitForm();
        $httpBackend.flush();
        expect(activationForm.getGeneralErrorsContainer()).toExist();
        expect(activationForm.getGeneralError('remote_unknown')).toExist();
        // TODO: once implemented, test that the user login is not attempted
    });

    it('should show "required" validation errors when the form is submitted without touching the input fields', function () {
        activationForm.getSubmitButton().click();

        expectValidationError('password', 'required');
        expectValidationError('repeatedPassword', 'required');
    });

    it('should show a validation error if the password is changed to blank', function () {
        activationForm.password.getInputField().val('secret!!!').trigger('input');
        activationForm.password.getInputField().val('').trigger('input');

        expectValidationError('password', 'required');
    });

    it('should show a validation error if the repeated password is changed to blank', function () {
        activationForm.repeatedPassword.getInputField().val('secret!!!').trigger('input');
        activationForm.repeatedPassword.getInputField().val('').trigger('input');

        expectValidationError('repeatedPassword', 'required');
    });

    it('should show a validation error if the password is too short', function () {
        activationForm.password.getInputField().val('123456').trigger('input');
        expectValidationError('password', 'pattern');
    });

    it('should show a validation error if the password has no special characters', function () {
        activationForm.password.getInputField().val('12345678').trigger('input');
        expectValidationError('password', 'pattern');
    });

    it('should show a validation error if the password contains whitespaces', function () {
        activationForm.password.getInputField().val('12345 67!').trigger('input');
        expectValidationError('password', 'pattern');
    });

    it('should show a validation error if the password contains whitespaces at the beginning', function () {
        activationForm.password.getInputField().val(' 1234567!').trigger('input');
        expectValidationError('password', 'pattern');
    });

    it('should show a validation error if the password contains whitespaces at the end', function () {
        activationForm.password.getInputField().val('1234567! ').trigger('input');
        expectValidationError('password', 'pattern');
    });

    it('should show a validation error if different passwords are entered once the form is submitted', function () {
        activationForm.password.getInputField().val('secure').trigger('input');
        activationForm.repeatedPassword.getInputField().val('something else').trigger('input');

        expectNoValidationError('repeatedPassword');

        activationForm.getSubmitButton().click();

        expectValidationError('repeatedPassword', 'remote_equal');
    });

    it('should show an appropriate error message if the server responds with "already_activated"', function() {
        expectBackendActivationCallAndRespond(400, { "message": "already_activated" });

        fillAndSubmitForm();
        $httpBackend.flush();

        expect(activationForm.getGeneralErrorsContainer()).toExist();
        expect(activationForm.getGeneralError('remote_already_activated')).toExist();
    });

    it('should show an appropriate error message if the server responds with 404', function() {
        expectBackendActivationCallAndRespond(404);

        fillAndSubmitForm();
        $httpBackend.flush();

        expect(activationForm.getGeneralErrorsContainer()).toExist();
        expect(activationForm.getGeneralError('remote_not_found')).toExist();
    });

    it('should clear the remote_equal validation error once the user starts typing again', function() {
        activationForm.password.getInputField().val('secret!!!').trigger('input');
        activationForm.repeatedPassword.getInputField().val('something else!').trigger('input');

        activationForm.getSubmitButton().click();

        activationForm.repeatedPassword.getInputField().val('secret!!!').trigger('input');

        expectNoValidationError('repeatedPassword');
    });
});
