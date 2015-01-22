/**
 * "page-object" for Password-Recovery-View
 */
function PasswordRecoveryForm(element) {

    this.getGeneralErrorsContainer = function() {
        return element.find('.general-error');
    };

    this.getGeneralError = function(violatedRule) {
        return this.getGeneralErrorsContainer().find('[ng-message="' + violatedRule + '"]');
    };

    this.email = new FormGroup(element.find('.form-controls-email'));

    this.getSubmitButton = function() {
        return element.find('button[type="submit"]');
    };
}
