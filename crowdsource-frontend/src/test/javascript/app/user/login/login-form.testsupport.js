/**
 * "page-object" for Login-View
 */
function LoginForm(element) {

    this.getGeneralErrorsContainer = function () {
        return element.find('.general-error');
    };

    this.getGeneralError = function (violatedRule) {
        return this.getGeneralErrorsContainer().find('[ng-message="' + violatedRule + '"]');
    };

    this.email = new FormGroup(element.find('.form-controls-email'));

    this.password = new FormGroup(element.find('.form-controls-password'));

    this.getSubmitButton = function () {
        return element.find('button[type="submit"]');
    };
}
