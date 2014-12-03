
function FormGroup(controlsEl) {

    this.getInputField = function() {
        return controlsEl.find('input');
    };

    this.getLabelContainer = function() {
        return controlsEl.find('> label');
    };

    this.getLabel = function() {
        return controlsEl.find('.valid-label');
    };

    this.getErrorLabelsContainer = function() {
        return controlsEl.find('.invalid-label');
    };

    this.getErrorLabelForRule = function(violatedRule) {
        return this.getErrorLabelsContainer().find('[ng-message="' + violatedRule + '"]');
    };
}