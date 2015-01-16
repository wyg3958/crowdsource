describe('user signup view', function () {

    var $httpBackend, $location, signupForm;

    beforeEach(function() {
        module('crowdsource');
        module('crowdsource.templates');

        localStorage.clear(); // reset

        inject(function($compile, $rootScope, $templateCache, $controller, _$httpBackend_, _$location_, User, RemoteFormValidation) {
            var $scope = $rootScope.$new();
            $httpBackend = _$httpBackend_;
            $location = _$location_;

            $controller('UserSignupController as signup', {
                $scope: $scope,
                $location: $location,
                User: User,
                RemoteFormValidation: RemoteFormValidation
            });

            var template = $templateCache.get('app/user/signup/user-signup.html');
            var view = $compile(template)($scope);

            $scope.$digest();
            signupForm = new SignupForm(view);
        });
    });

    function expectValidationError(inputName, violatedRule) {
        expect(signupForm[inputName].getLabelContainer()).toHaveClass('error');
        expect(signupForm[inputName].getLabel()).toHaveClass('ng-hide');
        expect(signupForm[inputName].getErrorLabelsContainer()).not.toHaveClass('ng-hide');
        expect(signupForm[inputName].getErrorLabelForRule(violatedRule)).toExist();
    }

    function expectNoValidationError(inputName) {
        expect(signupForm[inputName].getLabelContainer()).not.toHaveClass('error');
        expect(signupForm[inputName].getLabel()).not.toHaveClass('ng-hide');
        expect(signupForm[inputName].getErrorLabelsContainer()).toHaveClass('ng-hide');
    }

    function fillAndSubmitForm() {
        signupForm.email.getInputField().val('test').trigger('input');
        signupForm.termsOfServiceAccepted.getInputField().click();

        signupForm.getSubmitButton().click();
    }

    function expectBackendCallAndRespond(statusCode, responseBody) {
        $httpBackend.expectPOST('/user', {"email":"test@axelspringer.de","termsOfServiceAccepted":true}).respond(statusCode, responseBody);
    }


    it('should show no validation errors when the form is untouched', function () {
        expect(signupForm.getGeneralErrorsContainer()).not.toExist();

        expectNoValidationError('email');
        expectNoValidationError('termsOfServiceAccepted');
    });

    it('should POST the data to the server and redirect to success page', function() {

        // expect a valid call and return "created"
        expectBackendCallAndRespond(201);

        fillAndSubmitForm();

        // flush backend (assertion will be evaluated)
        $httpBackend.flush();

        // make sure location was changed
        expect($location.path()).toBe('/signup/test@axelspringer.de/success');
    });

    it('should disable the submit button and change it\'s text while loading', function() {
        expectBackendCallAndRespond(201);
        expect(signupForm.getSubmitButton()).toHaveText('Registrieren');
        expect(signupForm.getSubmitButton()).not.toBeDisabled();

        fillAndSubmitForm();

        expect(signupForm.getSubmitButton()).toHaveText('Registrieren...');
        expect(signupForm.getSubmitButton()).toBeDisabled();

        $httpBackend.flush();

        expect(signupForm.getSubmitButton()).toHaveText('Registrieren');
        expect(signupForm.getSubmitButton()).not.toBeDisabled();
    });

    it('should show an unknown error when the server responds with 500', function() {

        // spy on path-method
        spyOn($location, 'path');
        expectBackendCallAndRespond(500);

        fillAndSubmitForm();
        $httpBackend.flush();
        expect($location.path).not.toHaveBeenCalled();
        expect(signupForm.getGeneralErrorsContainer()).toExist();
        expect(signupForm.getGeneralError('remote_unknown')).toExist();
    });

    it('should show "required" validation errors when the form is submitted without touching the input fields', function () {
        signupForm.getSubmitButton().click();

        expectValidationError('email', 'required');
        expectValidationError('termsOfServiceAccepted', 'required');
    });

    it('should show a validation error if the email address is changed to blank', function () {
        signupForm.email.getInputField().val('test').trigger('input');
        signupForm.email.getInputField().val('').trigger('input');

        expectValidationError('email', 'required');
    });

    it('should show a validation error if an invalid email address is entered', function () {
        signupForm.email.getInputField().val('inval@id.mail').trigger('input'); // only the part before the @<domain> is supposed to be entered

        expectValidationError('email', 'email');
    });

    it('should show a validation error if an email address containting "_extern" is entered', function () {
        signupForm.email.getInputField().val('invalid_extern').trigger('input');

        expectValidationError('email', 'non_external_email');
    });

    it('should show a validation error if the terms of service checkbox was unchecked', function () {
        signupForm.termsOfServiceAccepted.getInputField().click(); // check
        signupForm.termsOfServiceAccepted.getInputField().click(); // uncheck

        expectValidationError('termsOfServiceAccepted', 'required');
    });

    it('should show a validation error if the server responds with the field violation {"email": "not_activated"}', function() {
        expectBackendCallAndRespond(400, { "errorCode": "field_errors", "fieldViolations": { "email": "not_activated" } });

        fillAndSubmitForm();
        $httpBackend.flush();
        expect(signupForm.getGeneralErrorsContainer()).not.toExist();
        expectValidationError('email', 'remote_not_activated');
    });

    it('should clear the remote validation errors once the user starts typing again', function() {
        expectBackendCallAndRespond(400, { "errrorCode": "field_errors", "fieldViolations": { "email": "not_activated" } });

        fillAndSubmitForm();
        $httpBackend.flush();
        signupForm.email.getInputField().val('something-different').trigger('input');
        expectNoValidationError('email');
    });

    it('should show an unknown error when the server responds with a field violation for an unknown field', function() {
        expectBackendCallAndRespond(400, { "errrorCode": "field_errors", "fieldViolations": { "unknownField": "foo" } });

        fillAndSubmitForm();
        $httpBackend.flush();
        expect(signupForm.getGeneralErrorsContainer()).toExist();
        expect(signupForm.getGeneralError('remote_unknown')).toExist();
    });

    it('should show terms of service text when the tos link is clicked', function () {
        expect(signupForm.getTosPanel()).not.toExist();
        signupForm.getTosLinkForValidLabel().click();
        expect(signupForm.getTosPanel()).toExist();
    });

    it('should show terms of service text when the invalid form submit tos link is clicked', function () {
        expect(signupForm.getTosPanel()).not.toExist();
        signupForm.getTosLinkForInvalidLabel().click();
        expect(signupForm.getTosPanel()).toExist();
    });
});


