
function SignupForm(formEl) {

    this.getGeneralError = function() {
        return formEl.find('.general-error');
    };

    this.email = new FormControls(formEl.find('.form-controls-email'));

    this.termsOfServiceAccepted = new FormControls(formEl.find('.form-controls-termsofservice'));

    this.getSubmitButton = function() {
        return formEl.find('button[type="submit"]');
    };
}
