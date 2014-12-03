
function ActivationForm(formEl) {

    this.getGeneralError = function() {
        return formEl.find('.general-error');
    };

    this.password = new FormGroup(formEl.find('.form-controls-password'));

    this.repeatedPassword = new FormGroup(formEl.find('.form-controls-repeated-password'));

    this.getSubmitButton = function() {
        return formEl.find('button[type="submit"]');
    };
}
