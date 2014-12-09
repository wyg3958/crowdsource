
function ActivationForm(element) {

    this.getGeneralErrorsContainer = function() {
        return element.find('.general-error');
    };

    this.getGeneralError = function(violatedRule) {
        return this.getGeneralErrorsContainer().find('[ng-message="' + violatedRule + '"]');
    };

    this.password = new FormGroup(element.find('.form-controls-password'));

    this.repeatedPassword = new FormGroup(element.find('.form-controls-repeated-password'));

    this.getSubmitButton = function() {
        return element.find('button[type="submit"]');
    };
}
