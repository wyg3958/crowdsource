describe('faq', function () {

    var $rootScope, view, scope;

    beforeEach(function () {
        module('crowdsource.templates');
        module('crowdsource');
        module(function (_$analyticsProvider_) {
            _$analyticsProvider_.virtualPageviews(false);
            _$analyticsProvider_.firstPageview(false);
            _$analyticsProvider_.developerMode(true);
        });

        inject(function ($compile, _$rootScope_, $templateCache, $controller) {
            $rootScope = _$rootScope_;
            scope = $rootScope.$new();
            var template = $templateCache.get('app/misc/faq.html');
            view = $compile(template)(scope);

            scope.$digest();
        });
    });

    it("should contain accordion item containing tracking attributes", function () {
        expect(view.find("accordion-item")).toHaveAttr("trackingcat", "UserActions");
        expect(view.find("accordion-item")).toHaveAttr("trackingevt", "FaqEntryOpened");

        expect(view.find("accordion-item .accordion-navigation")).toHaveAttr("analytics-on", "click");
        expect(view.find("accordion-item .accordion-navigation")).toHaveAttr("analytics-category", "UserActions");
        expect(view.find("accordion-item .accordion-navigation")).toHaveAttr("analytics-event", "FaqEntryOpened");
        expect(view.find("accordion-item .accordion-navigation")).toHaveAttr("analytics-label");
    });

});