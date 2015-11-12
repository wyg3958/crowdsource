describe('nav bar', function () {

    var $rootScope, navBar, scope;

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
            var template = $templateCache.get('app/components/layout/nav-bar/nav-bar.html');
            var view = $compile(template)(scope);
            navBar = new NavBar(view);

            scope['nav'] = {};
            scope.nav = {
                auth : {
                    currentUser : {
                        loggedIn : true
                    }
            }};

            scope.$digest();
        });
    });

    it("should contain new project button with tracking attributes", function () {
        expect(navBar.getNewProjectLink()).toHaveAttr('analytics-on');
        expect(navBar.getNewProjectLink()).toHaveAttr('analytics-category', 'Projects');
        expect(navBar.getNewProjectLink()).toHaveAttr('analytics-event', 'GoToStartNewProjectFromNavbar');
    });

    it("should contain logout button with tracking attributes", function () {
        expect(navBar.getLogoutButton()).toHaveAttr('analytics-on');
        expect(navBar.getLogoutButton()).toHaveAttr('analytics-category', 'UserActions');
        expect(navBar.getLogoutButton()).toHaveAttr('analytics-event', 'Logout');
    });

});