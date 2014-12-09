/**
 * A FormGroup represents an input field along with its label and error messages
 */
function FormGroup(element, inputFieldType) {

    inputFieldType = inputFieldType || 'input';

    this.getInputField = function() {
        return element.find(inputFieldType);
    };

    this.getLabelContainer = function() {
        return element.find('> label');
    };

    this.getLabel = function() {
        return element.find('.valid-label');
    };

    this.getErrorLabelsContainer = function() {
        return element.find('.invalid-label');
    };

    this.getErrorLabelForRule = function(violatedRule) {
        return this.getErrorLabelsContainer().find('[ng-message="' + violatedRule + '"]');
    };
}