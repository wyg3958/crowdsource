describe('hello directive', function () {

    var $scope;
    var $httpBackend;
    var helloDirective;

    // load module and templates
    beforeEach(module('crowdsource', 'templates'));

    // init scope
    beforeEach(inject(function ($rootScope) {
        // new scope for test
        $scope = $rootScope.$new();
    }));

    // provide and configure $httpBackend
    beforeEach(inject(function (_$httpBackend_) {
        $httpBackend = _$httpBackend_;
        // always provide data on initial load
        $httpBackend.expect('GET', 'hello').respond(200, {message: 'hi'});
    }));

    // build directive object
    beforeEach(inject(function ($compile) {

        // html using paging-directive
        var html = "<hello></hello>";

        // compile
        var element = $compile(html)($scope);

        // init
        $scope.$digest();

        // flush http backend (to provide data)
        $httpBackend.flush();

        // build directive object for easy access in tests
        helloDirective = {
            element: $(element)
        };
    }));

    it('to display welcome text', function () {
        // find hello-div
        var helloDiv = helloDirective.element.find(".hello");
        expect(helloDiv.text()).toBe('AS CrowdSource says hi');
    });
});
