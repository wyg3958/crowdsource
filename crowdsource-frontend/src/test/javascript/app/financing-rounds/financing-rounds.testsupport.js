function FinancingRounds(element) {

    var defaultRow = '.row-0';

    this.getGeneralErrorsContainer = function () {
        return element.find('.general-error');
    };

    this.getGeneralError = function (violatedRule) {
        return this.getGeneralErrorsContainer().find('[ng-message="' + violatedRule + '"]');
    };

    this.getBudget = function () {
        return new FormGroup(element.find('.form-controls-budget'));
    };

    this.getEndDate = function () {

        return new FormGroup(element.find('.form-controls-enddate'));
    };

    this.getTableStartDate = function (row) {
        row = row || defaultRow;
        return element.find(row + ' .startdate');
    };

    this.getTableEndDate = function (row) {
        row = row || defaultRow;
        return element.find(row + ' .enddate');
    };

    this.getTableBudget = function (row) {
        row = row || defaultRow;
        return element.find(row + ' .budget');
    };

    this.getTableEndRoundButton = function (row) {
        row = row || defaultRow;
        return element.find(row + ' .stop-button');
    };

    this.getTableEndRoundCancelButton = function (row) {
        row = row || defaultRow;
        return element.find(row + ' .cancel-button');
    }

    this.getTableEndRoundConfirmMessage = function (row) {
        row = row || defaultRow;
        return element.find(row + ' .confirm-message');
    }

    this.getStartRoundButton = function () {
        return element.find('.newround-start');
    };

    this.getNotification = function () {
        return element.find('form .notification');
    };

    this.getTableText = function () {
        return element.find('tbody td');
    };

    this.getAlertBox = function () {
        return element.find('.alert-box');
    }
}
