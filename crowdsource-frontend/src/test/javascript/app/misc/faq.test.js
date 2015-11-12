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
        expect(view.find("accordion-item")).toHaveAttr("trackingcategory", "UserActions");
        expect(view.find("accordion-item")).toHaveAttr("trackingevent", "FaqEntryOpened");

        expect(view.find("accordion-item .accordion-navigation a")).toHaveAttr("analytics-on");
        expect(view.find("accordion-item .accordion-navigation a")).toHaveAttr("analytics-category", "UserActions");
        expect(view.find("accordion-item .accordion-navigation a")).toHaveAttr("analytics-event", "FaqEntryOpened");
        expect(view.find("accordion-item .accordion-navigation a")).toHaveAttr("analytics-label", "faq_entry");

        var faqItemParents = view.find("accordion-item");
        var faqItemLinks = view.find("accordion-item .accordion-navigation a");
        for(var idx = 0; idx < faqItemParents.length; idx ++){
            var expVal = faqItemParents[idx].getAttribute("title");
            expect(faqItemLinks[idx]).toHaveAttr("analytics-value", expVal);
        }

    });

});