function FinancingRounds(element) {

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

    this.getTableStartDate = function () {
        return element.find('tbody .startdate');
    };

    this.getTableEndtDate = function () {
        return element.find('tbody .enddate');
    };

    this.getTableBudget = function () {
        return element.find('tbody .budget');
    };

    this.getTableEndRoundButton = function () {
        return element.find('.stop-button');
    };

    this.getStartRoundButton = function () {
        return element.find('.newround-start');
    };

    this.getNotification = function () {
        return element.find('form .notification');
    };

    this.getTableText = function () {
        return element.find('tbody td');
    }
}
