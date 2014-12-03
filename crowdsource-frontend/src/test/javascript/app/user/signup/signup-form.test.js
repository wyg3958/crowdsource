/**
 * "page-object" for Signup-View
 */
function SignupForm(element) {

    this.getGeneralError = function() {
        return element.find('.general-error');
    };

    this.email = new FormGroup(element.find('.form-controls-email'));

    this.termsOfServiceAccepted = new FormGroup(element.find('.form-controls-termsofservice'));

    this.getSubmitButton = function() {
        return element.find('button[type="submit"]');
    };
}
