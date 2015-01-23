describe('teaser metrics service', function () {

    var $rootScope, $scope, $compile, $httpBackend, $interval, Route, TeaserMetrics, onRouteChangeSuccessListener;

    beforeEach(function () {
        module('crowdsource.templates');
        module('crowdsource', function($provide) {
            $provide.decorator('Route', function($delegate) {
                // callback mock
                $delegate.onRouteChangeSuccess = function (cb) {
                    onRouteChangeSuccessListener = cb;
                };

                // noop mock, will be spyed on later
                $delegate.getCurrentRoute = function () {};
                return $delegate;
            });
        });

        inject(function (_$rootScope_, _$compile_, _$httpBackend_, _$interval_, _Route_, _TeaserMetrics_) {
            $rootScope = _$rootScope_;
            $scope = $rootScope.$new();
            $compile = _$compile_;
            $httpBackend = _$httpBackend_;
            $interval = _$interval_;
            Route = _Route_;
            TeaserMetrics = _TeaserMetrics_;

            // freeze the time. All calls to new Date() return this time
            jasmine.clock().mockDate(new Date('2015-01-21T15:04:23.003Z'));
            jasmine.clock().install();
        });
    });

    function renderDirective() {
        var root = $compile('<teaser></teaser>')($scope);
        $scope.$digest();

        return {
            root: root,
            container: root.find('> div'),
            remainingBudgetItem: root.find('.remaining-budget'),
            remainingBudget: root.find('.remaining-budget .metrics__heading'),
            remainingTime: root.find('.remaining-time .metrics__heading'),
            remainingTimeLabel: root.find('.remaining-time .metrics__subhead'),
            userCount: root.find('.user-count .metrics__heading')
        }
    }

    it("should show a slim teaser if the new route is configured to show no teaser", function () {
        expectFinancingRoundBECall().respond(404);
        currentRouteWantsTeaser(false);

        var teaser = renderDirective();
        expect(teaser.container).toHaveClass('teaser--slim');
    });

    it("should show the metrics retrieved from the backend", function () {
        expectFinancingRoundBECall().respond(200, financingRound('2015-01-22T22:59:59.000Z'));
        expectMetricsBECall().respond(200, { remainingBudget: 54321, count: 33 });

        currentRouteWantsTeaser(true);
        var teaser = renderDirective();

        expect(teaser.remainingBudgetItem).not.toHaveClass('ng-hide');
        expect(teaser.remainingBudget.text()).toBe('$');
        expect(teaser.remainingTime.text()).toBe(' ');
        expect(teaser.remainingTimeLabel.text()).toBe('Noch in dieser Runde');

        $httpBackend.flush();

        expect(teaser.container).toHaveClass('teaser--hero');
        expect(teaser.remainingBudgetItem).not.toHaveClass('ng-hide');
        expect(teaser.remainingBudget.text()).toBe('$54.321');
        expect(teaser.remainingTime.text()).toBe('1d 7h 55m 35s');
        expect(teaser.remainingTimeLabel.text()).toBe('Noch in dieser Runde');
        expect(teaser.userCount.text()).toBe('33 aktive Nutzer');
    });

    it("should only render the directive once if the directive is initialized before the $routeChangeStart event fired", function () {
        expectFinancingRoundBECall().respond(200, financingRound('2015-01-23T22:59:59.000Z'));
        expectMetricsBECall().respond(200, { remainingBudget: 123, count: 33 });

        currentRouteWantsTeaser(true);
        var teaser = renderDirective();
        changeRoute({ showTeaser: true });

        $httpBackend.flush();

        expect(teaser.container).toHaveClass('teaser--hero');
    });

    it("should show count down the remaining time", function () {
        expectFinancingRoundBECall().respond(200, financingRound('2015-01-22T22:59:59.000Z'));
        expectMetricsBECall().respond(200, { remainingBudget: 54321, count: 33 });

        currentRouteWantsTeaser(true);
        var teaser = renderDirective();
        $httpBackend.flush();

        expect(teaser.remainingTime.text()).toBe('1d 7h 55m 35s');

        // let the next 'new Date()'-calls to return the frozen time + 1 second
        jasmine.clock().tick(1000);

        // trigger any $invervals as if 1 second passed
        $interval.flush('1000');

        expect(teaser.remainingTime.text()).toBe('1d 7h 55m 34s');
    });

    it("should show a different information if no financing round is currently active", function () {
        expectFinancingRoundBECall().respond(404);
        expectMetricsBECall().respond(200, { remainingBudget: 54321, count: 33 });

        currentRouteWantsTeaser(true);
        var teaser = renderDirective();
        $httpBackend.flush();

        expect(teaser.container).toHaveClass('teaser--hero');
        expect(teaser.remainingBudgetItem).toHaveClass('ng-hide');
        expect(teaser.remainingTime.text()).toBe('Keine aktive Runde');
        expect(teaser.remainingTimeLabel.text()).toBe("Bald geht's los mit einer neuen Runde.");
        expect(teaser.userCount.text()).toBe('33 aktive Nutzer');
    });

    it("should show the teaser when the route changes and the next routes wants a teaser to be shown", function () {
        expectFinancingRoundBECall().respond(404); // FinancingRound service produces the call

        currentRouteWantsTeaser(false);
        var teaser = renderDirective();

        expect(teaser.container).toHaveClass('teaser--slim');

        expectMetricsBECall().respond(200, { remainingBudget: 123, count: 33 });
        expectFinancingRoundBECall().respond(200, financingRound('2015-01-23T22:59:59.000Z'));

        changeRoute({ showTeaser: true }, { showTeaser: false });
        $httpBackend.flush();

        expect(teaser.container).toHaveClass('teaser--hero');
    });

    it("should hide the teaser when the route changes and the next routes wants a teaser to be hidden", function () {
        expectFinancingRoundBECall().respond(200, financingRound('2015-01-23T22:59:59.000Z'));
        expectMetricsBECall().respond(200, { remainingBudget: 123, count: 33 });

        currentRouteWantsTeaser(true);
        var teaser = renderDirective();
        $httpBackend.flush();

        expect(teaser.container).toHaveClass('teaser--hero');

        changeRoute({ showTeaser: false }, { showTeaser: true });

        expect(teaser.container).toHaveClass('teaser--slim');
    });

    it("should update the metrics when the route changes back and forth", function () {
        expectFinancingRoundBECall().respond(200, financingRound('2015-01-22T22:59:59.000Z'));
        expectMetricsBECall().respond(200, { remainingBudget: 54321, count: 33 });

        currentRouteWantsTeaser(true);
        var teaser = renderDirective();
        $httpBackend.flush();

        expect(teaser.remainingBudget.text()).toBe('$54.321');

        // new route wants no teaser
        changeRoute({ showTeaser: false }, { showTeaser: true });

        expect(teaser.container).toHaveClass('teaser--slim');

        expectMetricsBECall().respond(200, { remainingBudget: 123, count: 33 });
        expectFinancingRoundBECall().respond(200, financingRound('2015-01-23T22:59:59.000Z'));

        // new route wants teaser
        changeRoute({ showTeaser: true }, { showTeaser: false });
        $httpBackend.flush();

        expect(teaser.container).toHaveClass('teaser--hero');
        expect(teaser.remainingBudget.text()).toBe('$123');
        expect(teaser.remainingTime.text()).toBe('2d 7h 55m 35s');
    });

    function expectMetricsBECall() {
        return $httpBackend.expectGET('/users/metrics');
    }

    function expectFinancingRoundBECall() {
        return $httpBackend.expectGET('/financinground/active');
    }

    function currentRouteWantsTeaser(showTeaser) {
        spyOn(Route, 'getCurrentRoute').and.returnValue({ showTeaser: showTeaser });
    }

    function changeRoute(currentRoute, previousRoute) {
        onRouteChangeSuccessListener({}, currentRoute, previousRoute);
        $scope.$digest();
    }

    function financingRound(endDate) {
        return { id: 'xyz', startDate: '2015-01-21T09:04:23.003Z', endDate: endDate, active: true }
    }
});