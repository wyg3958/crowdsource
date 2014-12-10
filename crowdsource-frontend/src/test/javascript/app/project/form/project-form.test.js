describe('project form', function () {

    var $httpBackend, $location, projectForm;

    beforeEach(function() {
        module('crowdsource');
        module('crowdsource.templates');

        inject(function($compile, $rootScope, $templateCache, $controller, _$httpBackend_, _$location_, Project, RemoteFormValidation) {
            var $scope = $rootScope.$new();
            $httpBackend = _$httpBackend_;
            $location = _$location_;

            $controller('ProjectFormController', {
                $scope: $scope,
                $location: _$location_,
                Project: Project,
                RemoteFormValidation: RemoteFormValidation
            });

            var template = $templateCache.get('app/project/form/project-form.html');
            projectForm = new ProjectForm($compile(template)($scope));
            $scope.$digest();
        });
    });

    function expectValidationError(inputName, violatedRule) {
        expect(projectForm[inputName].getLabelContainer()).toHaveClass('error');
        expect(projectForm[inputName].getLabel()).toHaveClass('ng-hide');
        expect(projectForm[inputName].getErrorLabelsContainer()).not.toHaveClass('ng-hide');
        expect(projectForm[inputName].getErrorLabelForRule(violatedRule)).toExist();
    }

    function expectNoValidationError(inputName) {
        expect(projectForm[inputName].getLabelContainer()).not.toHaveClass('error');
        expect(projectForm[inputName].getLabel()).not.toHaveClass('ng-hide');
        expect(projectForm[inputName].getErrorLabelsContainer()).toHaveClass('ng-hide');
    }

    function fillAndSubmitForm(title, shortDescription, pledgeGoal, description) {
        projectForm.title.getInputField().val(title).trigger('input');
        projectForm.shortDescription.getInputField().val(shortDescription).trigger('input');
        projectForm.pledgeGoal.getInputField().val(pledgeGoal).trigger('input');
        projectForm.description.getInputField().val(description).trigger('input');

        projectForm.getSubmitButton().click();
    }

    function expectBackendCallAndRespond(statusCode, responseBody) {
        $httpBackend.expectPOST('/project', {
            "title":"Title",
            "shortDescription":"Short description",
            "pledgeGoal":12500,
            "description":"Looong description"
        })
        .respond(statusCode, responseBody);
    }

    it('should show no validation errors when the form is untouched', function () {
        expect(projectForm.getGeneralErrorsContainer()).not.toExist();

        expectNoValidationError('title');
        expectNoValidationError('shortDescription');
        expectNoValidationError('pledgeGoal');
        expectNoValidationError('description');
    });

    it('should show no validation errors when the form is filled correctly', function () {
        projectForm.title.getInputField().val(TestData.generateString(60)).trigger('input');
        projectForm.shortDescription.getInputField().val(TestData.generateString(140)).trigger('input');
        projectForm.pledgeGoal.getInputField().val('12500').trigger('input');
        projectForm.description.getInputField().val('Looong description').trigger('input');

        expect(projectForm.getGeneralErrorsContainer()).not.toExist();

        expectNoValidationError('title');
        expectNoValidationError('shortDescription');
        expectNoValidationError('pledgeGoal');
        expectNoValidationError('description');
    });

    it('should POST the data to the server and redirect to success page', function() {
        expectBackendCallAndRespond(200);

        fillAndSubmitForm('Title', 'Short description', '12500', 'Looong description');
        $httpBackend.flush();

        expect($location.path()).toBe('/project/new/success');
    });

    it('should disable the submit button and change it\'s text while loading', function() {
        expectBackendCallAndRespond(200);

        expect(projectForm.getSubmitButton()).toHaveText('Absenden');
        expect(projectForm.getSubmitButton()).not.toBeDisabled();

        fillAndSubmitForm('Title', 'Short description', '12500', 'Looong description');

        expect(projectForm.getSubmitButton()).toHaveText('Absenden...');
        expect(projectForm.getSubmitButton()).toBeDisabled();

        $httpBackend.flush();

        expect(projectForm.getSubmitButton()).toHaveText('Absenden');
        expect(projectForm.getSubmitButton()).not.toBeDisabled();
    });

    it('should show an unknown error when the backend call results in 500', function() {
        expectBackendCallAndRespond(500);
        spyOn($location, 'path');

        fillAndSubmitForm('Title', 'Short description', '12500', 'Looong description');
        $httpBackend.flush();
        expect(projectForm.getGeneralErrorsContainer()).toExist();
        expect(projectForm.getGeneralError('remote_unknown')).toExist();

        expect($location.path).not.toHaveBeenCalled();
    });

    it('should show "required" validation errors when the form is submitted without touching the input fields', function () {
        projectForm.getSubmitButton().click();

        expectValidationError('title', 'required');
        expectValidationError('shortDescription', 'required');
        expectValidationError('pledgeGoal', 'required');
        expectValidationError('description', 'required');
    });

    it('should show a validation error if the title is changed to blank', function () {
        projectForm.title.getInputField().val('Title').trigger('input');
        projectForm.title.getInputField().val('').trigger('input');

        expectValidationError('title', 'required');
    });

    it('should show a validation error if the title is too long', function () {
        projectForm.title.getInputField().val(TestData.generateString(61)).trigger('input');

        expectValidationError('title', 'maxlength');
    });

    it('should show a validation error if the short description is changed to blank', function () {
        projectForm.shortDescription.getInputField().val('Short description').trigger('input');
        projectForm.shortDescription.getInputField().val('').trigger('input');

        expectValidationError('shortDescription', 'required');
    });

    it('should show a validation error if the short description is too long', function () {
        projectForm.shortDescription.getInputField().val(TestData.generateString(141)).trigger('input');

        expectValidationError('shortDescription', 'maxlength');
    });

    it('should show a validation error if the pledge goal is no number', function () {
        projectForm.pledgeGoal.getInputField().val('12.01').trigger('input');

        expectValidationError('pledgeGoal', 'pattern');
    });

    it('should cut off a trailing dot in pledge goal before sending it to the server', function () {
        // this is some implicit logic of the input type="number" field and angular
        // that the trailing dot is removed automatically. This test is here to make sure this functionality
        // does not change unexpectedly

        expectBackendCallAndRespond(200);

        fillAndSubmitForm('Title', 'Short description', '12500.', 'Looong description');
        $httpBackend.flush();
    });

    it('should show a validation error if the description is changed to blank', function () {
        projectForm.description.getInputField().val('Loong description').trigger('input');
        projectForm.description.getInputField().val('').trigger('input');

        expectValidationError('description', 'required');
    });

});
