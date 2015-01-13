describe('project pledging form', function () {

    var $scope, $compile, $httpBackend, Authentication;

    beforeEach(function() {
        module('crowdsource');
        module('crowdsource.templates');

        inject(function($rootScope, _$compile_, _$httpBackend_, _Authentication_) {
            $scope = $rootScope.$new();
            $compile = _$compile_;
            $httpBackend = _$httpBackend_;
            Authentication = _Authentication_;
        });
    });

    function compileDirective() {
        var root = $compile('<project-pledging-form project="project"></project-pledging-form>')($scope);
        $scope.$digest();
        return {
            root: root,
            slider: root.find('.range-slider'),
            pledgeButton: root.find('button')
        };
    }

    it("should disable the slider until the user budget is loaded", function() {

        $scope.project = { $resolved: true, pledgeGoal: 100, pledgedAmount: 50 };
        spyOn(Authentication, 'isLoggedIn').and.returnValue(true);
        $httpBackend.expectGET('/user/current').respond(200, { budget: 20 });

        var elements = compileDirective();

        expect(elements.slider).toHaveClass('disabled');
        expect(elements.pledgeButton).toBeDisabled();

        $httpBackend.flush();

        expect(elements.slider).not.toHaveClass('disabled');
    });

    it("should disable the slider until the project details are loaded", function () {

        $scope.project = { $resolved: false };
        spyOn(Authentication, 'isLoggedIn').and.returnValue(true);
        $httpBackend.expectGET('/user/current').respond(200, { budget: 20 });

        var elements = compileDirective();
        $httpBackend.flush();

        expect(elements.slider).toHaveClass('disabled');

        angular.copy({ $resolved: true, pledgeGoal: 100, pledgedAmount: 50 }, $scope.project);
        $scope.$digest();

        expect(elements.slider).not.toHaveClass('disabled');
    });

    it("should disable the slider if the user has no budget", function () {

        $scope.project = { $resolved: true, pledgeGoal: 100, pledgedAmount: 50 };
        spyOn(Authentication, 'isLoggedIn').and.returnValue(true);
        $httpBackend.expectGET('/user/current').respond(200, { budget: 0 });

        var elements = compileDirective();
        $httpBackend.flush();

        expect(elements.slider).toHaveClass('disabled');
    });

    it("should disable the slider if the user is not logged in", function () {

        $scope.project = { $resolved: true, pledgeGoal: 100, pledgedAmount: 50 };
        spyOn(Authentication, 'isLoggedIn').and.returnValue(false);

        var elements = compileDirective();

        expect(elements.slider).toHaveClass('disabled');
    });
});
