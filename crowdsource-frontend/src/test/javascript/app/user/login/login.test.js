describe('user login view', function () {

    var $httpBackend, $location, loginForm;

    beforeEach(function() {
        module('crowdsource');
        module('crowdsource.templates');

        inject(function($compile, $rootScope, $templateCache, $controller, _$httpBackend_, _$location_, Authentication, RemoteFormValidation) {
            var $scope = $rootScope.$new();
            $httpBackend = _$httpBackend_;
            $location = _$location_;

            $controller('UserLoginController', {
                $scope: $scope,
                $location: $location,
                Authentication: Authentication,
                RemoteFormValidation: RemoteFormValidation
            });

            var template = $templateCache.get('app/user/login/user-login.html');
            loginForm = new LoginForm($compile(template)($scope));
            $scope.$digest();
        });
    });

    function expectValidationError(inputName, violatedRule) {
        expect(loginForm[inputName].getLabelContainer()).toHaveClass('error');
        expect(loginForm[inputName].getLabel()).toHaveClass('ng-hide');
        expect(loginForm[inputName].getErrorLabelsContainer()).not.toHaveClass('ng-hide');
        expect(loginForm[inputName].getErrorLabelForRule(violatedRule)).toExist();
    }

    function expectNoValidationError(inputName) {
        expect(loginForm[inputName].getLabelContainer()).not.toHaveClass('error');
        expect(loginForm[inputName].getLabel()).not.toHaveClass('ng-hide');
        expect(loginForm[inputName].getErrorLabelsContainer()).toHaveClass('ng-hide');
    }

    function fillAndSubmitForm() {
        loginForm.email.getInputField().val('test').trigger('input');
        loginForm.password.getInputField().val('secret!!!').trigger('input');

        loginForm.getSubmitButton().click();
    }

    function expectBackendCallAndRespond(statusCode, responseBody) {
        $httpBackend.expectPOST('/oauth/token', 'username=test%40axelspringer.de&password=secret!!!&client_id=web&grant_type=password')
            .respond(statusCode, responseBody);
    }


    it('should show no validation errors when the form is untouched', function () {
        expect(loginForm.getGeneralErrorsContainer()).not.toExist();

        expectNoValidationError('email');
        expectNoValidationError('password');
    });

    it('should request an access token from the server and redirect to index page', function() {
        expectBackendCallAndRespond(200);

        fillAndSubmitForm();

        $httpBackend.flush();
        expect($location.path()).toBe('/');
    });

    it('should disable the submit button and change it\'s text while loading', function() {
        expectBackendCallAndRespond(201);
        expect(loginForm.getSubmitButton()).toHaveText('Login');
        expect(loginForm.getSubmitButton()).not.toBeDisabled();

        fillAndSubmitForm();

        expect(loginForm.getSubmitButton()).toHaveText('Login...');
        expect(loginForm.getSubmitButton()).toBeDisabled();

        $httpBackend.flush();

        expect(loginForm.getSubmitButton()).toHaveText('Login');
        expect(loginForm.getSubmitButton()).not.toBeDisabled();
    });

    it('should show an unknown error message when the server responds with 500', function() {

        // spy on path-method
        spyOn($location, 'path');
        expectBackendCallAndRespond(500);

        fillAndSubmitForm();
        $httpBackend.flush();
        expect($location.path).not.toHaveBeenCalled();
        expect(loginForm.getGeneralErrorsContainer()).toExist();
        expect(loginForm.getGeneralError('remote_unknown')).toExist();
    });

    it('should show an unknown error message if the server responds with "an unknown result"', function() {

        // spy on path-method
        spyOn($location, 'path');
        expectBackendCallAndRespond(400, { error: "wooOooodooOoo" });

        fillAndSubmitForm();
        $httpBackend.flush();
        expect($location.path).not.toHaveBeenCalled();
        expect(loginForm.getGeneralErrorsContainer()).toExist();
        expect(loginForm.getGeneralError('remote_unknown')).toExist();
    });

    it('should show an appropriate error message if the server responds with "bad_credentials"', function() {

        // spy on path-method
        spyOn($location, 'path');
        expectBackendCallAndRespond(400, { error: "invalid_grant" });

        fillAndSubmitForm();
        $httpBackend.flush();
        expect($location.path).not.toHaveBeenCalled();
        expect(loginForm.getGeneralErrorsContainer()).toExist();
        expect(loginForm.getGeneralError('remote_bad_credentials')).toExist();
    });

    it('should show "required" validation errors when the form is submitted without touching the input fields', function () {
        loginForm.getSubmitButton().click();

        expectValidationError('email', 'required');
        expectValidationError('password', 'required');
    });

    it('should show a validation error if the email address is changed to blank', function () {
        loginForm.email.getInputField().val('test').trigger('input');
        loginForm.email.getInputField().val('').trigger('input');

        expectValidationError('email', 'required');
    });

    it('should show a validation error if an invalid email address is entered', function () {
        loginForm.email.getInputField().val('inval@id.mail').trigger('input'); // only the part before the @<domain> is supposed to be entered

        expectValidationError('email', 'pattern');
    });

    it('should show a validation error if an email address containting "_extern" is entered', function () {
        loginForm.email.getInputField().val('invalid_extern').trigger('input');

        expectValidationError('email', 'non_external_email');
    });
});