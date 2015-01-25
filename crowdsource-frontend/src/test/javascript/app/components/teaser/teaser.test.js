describe('teaser metrics service', function () {

    var $rootScope, $scope, $httpBackend, $interval, TeaserMetrics, teaser;

    beforeEach(function () {
        module('crowdsource');
        module('crowdsource.templates');

        inject(function (_$rootScope_, $compile, _$httpBackend_, _$interval_, _TeaserMetrics_) {
            $rootScope = _$rootScope_;
            $scope = $rootScope.$new();
            $httpBackend = _$httpBackend_;
            $interval = _$interval_;
            TeaserMetrics = _TeaserMetrics_;

            // freeze the time. All calls to new Date() return this time
            jasmine.clock().mockDate(new Date('2015-01-21T15:04:23.003Z'));
            jasmine.clock().install();

            var root = $compile('<teaser></teaser>')($scope);
            $scope.$digest();

            teaser = {
                root: root,
                container: root.find('> div'),
                remainingBudgetItem: root.find('.remaining-budget'),
                remainingBudget: root.find('.remaining-budget .metrics__heading'),
                remainingTime: root.find('.remaining-time .metrics__heading'),
                remainingTimeLabel: root.find('.remaining-time .metrics__subhead'),
                userCount: root.find('.user-count .metrics__heading')
            }
        });
    });

    it("should show a slim teaser if the new route is configured to show no teaser", function () {
        changeRouteWithTeaserWanted(false);

        expect(teaser.container).toHaveClass('teaser--slim');
    });

    it("should show the metrics retrieved from the backend", function () {
        expectMetricsBECall().respond(200, {remainingBudget: 54321, count: 33});
        expectFinancingRoundBECall().respond(200, financingRound('2015-01-22T22:59:59.000Z'));

        changeRouteWithTeaserWanted(true);

        expect(teaser.remainingBudgetItem).not.toHaveClass('ng-hide');
        expect(teaser.remainingBudget.text()).toBe(' $');
        expect(teaser.remainingTime.text()).toBe(' ');
        expect(teaser.remainingTimeLabel.text()).toBe('Noch in dieser Runde');

        $httpBackend.flush();

        expect(teaser.container).toHaveClass('teaser--hero');
        expect(teaser.remainingBudgetItem).not.toHaveClass('ng-hide');
        expect(teaser.remainingBudget.text()).toBe('54.321 $');
        expect(teaser.remainingTime.text()).toBe('1d 7h 55m 35s');
        expect(teaser.remainingTimeLabel.text()).toBe('Noch in dieser Runde');
        expect(teaser.userCount.text()).toBe('33 aktive Nutzer');
    });

    it("should show count down the remaining time", function () {
        expectMetricsBECall().respond(200, {remainingBudget: 54321, count: 33});
        expectFinancingRoundBECall().respond(200, financingRound('2015-01-22T22:59:59.000Z'));

        changeRouteWithTeaserWanted(true);
        $httpBackend.flush();

        expect(teaser.remainingTime.text()).toBe('1d 7h 55m 35s');

        // let the next 'new Date()'-calls to return the frozen time + 1 second
        jasmine.clock().tick(1000);

        // trigger any $invervals as if 1 second passed
        $interval.flush('1000');

        expect(teaser.remainingTime.text()).toBe('1d 7h 55m 34s');
    });

    it("should a different information if no financing round is currently active", function () {
        expectMetricsBECall().respond(200, {remainingBudget: 54321, count: 33});
        expectFinancingRoundBECall().respond(404);

        changeRouteWithTeaserWanted(true);
        $httpBackend.flush();

        expect(teaser.container).toHaveClass('teaser--hero');
        expect(teaser.remainingBudgetItem).toHaveClass('ng-hide');
        expect(teaser.remainingTime.text()).toBe('Keine aktive Runde');
        expect(teaser.remainingTimeLabel.text()).toBe("Bald geht's los mit einer neuen Runde.");
        expect(teaser.userCount.text()).toBe('33 aktive Nutzer');
    });

    it("should update the metrics when the route changes", function () {
        expectMetricsBECall().respond(200, {remainingBudget: 54321, count: 33});
        expectFinancingRoundBECall().respond(200, financingRound('2015-01-22T22:59:59.000Z'));
        changeRouteWithTeaserWanted(true);
        $httpBackend.flush();

        expect(teaser.remainingBudget.text()).toBe('54.321 $');

        expectMetricsBECall().respond(200, {remainingBudget: 123, count: 33});
        expectFinancingRoundBECall().respond(200, financingRound('2015-01-23T22:59:59.000Z'));
        changeRouteWithTeaserWanted(true);
        $httpBackend.flush();

        expect(teaser.remainingBudget.text()).toBe('123 $');
        expect(teaser.remainingTime.text()).toBe('2d 7h 55m 35s');
    });


    function expectMetricsBECall() {
        return $httpBackend.expectGET('/users/metrics');
    }

    function expectFinancingRoundBECall() {
        return $httpBackend.expectGET('/financinground/active');
    }

    function changeRouteWithTeaserWanted(showTeaser) {
        $rootScope.$broadcast('$routeChangeSuccess', {showTeaser: showTeaser});
    }

    function financingRound(endDate) {
        return {id: 'xyz', startDate: '2015-01-21T09:04:23.003Z', endDate: endDate, active: true}
    }
});