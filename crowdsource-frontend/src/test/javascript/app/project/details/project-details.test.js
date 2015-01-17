describe('project details', function () {

    var $scope, $httpBackend, $location, AuthenticationToken, projectDetails;

    beforeEach(function() {
        module('crowdsource');
        module('crowdsource.templates');

        localStorage.clear(); // reset, makes the user not logged in

        inject(function($compile, $rootScope, $templateCache, $controller, _$location_, _$httpBackend_, Project, _AuthenticationToken_) {
            $scope = $rootScope.$new();
            $httpBackend = _$httpBackend_;
            $location = _$location_;
            AuthenticationToken = _AuthenticationToken_;

            $controller('ProjectDetailsController as projectDetails', {
                $scope: $scope,
                $location: $location,
                $routeParams: {
                    projectId: 'xyz'
                },
                Project: Project
            });

            $httpBackend.whenGET('/financinground/active').respond(200, { active: true });

            var template = $templateCache.get('app/project/details/project-details.html');
            projectDetails = $compile(template)($scope);
        });
    });

    function prepareBackendMock(projectStatus) {
        $httpBackend.expectGET('/project/xyz').respond(200, {
            status: projectStatus,
            title: 'Title',
            shortDescription: 'Short description',
            description: 'Looong description',
            creator: { name: 'Foo Bar' },
            pledgedAmount: 13853,
            pledgeGoal: 20000,
            backers: 7
        });
    }

    it("should display the project's details that were retrieved from backend", function () {

        prepareBackendMock('PUBLISHED');

        $scope.$digest();
        $httpBackend.flush();

        expect(projectDetails.find('h1')).toHaveText('Title');
        expect(projectDetails.find('.project-status__creator strong')).toHaveText('Foo Bar');
        expect(projectDetails.find('.project-status__funding progress-bar .meter')).toHaveCss({ width: '69.265%' });
        expect(projectDetails.find('.project-status__pledge-goal')).toHaveText('$20,000');
        expect(projectDetails.find('.project-status__pledged-amount')).toHaveText('$13,853');
        expect(projectDetails.find('.project-status__backers')).toHaveText('7');
        expect(projectDetails.find('h2')).toHaveText('Short description');
        expect(projectDetails.find('.project-description')).toHaveText('Looong description');
        expect(projectDetails.find('.to-pledging-form-button')).not.toBeDisabled();
        expect(projectDetails.find('.to-pledging-form-button')).toHaveText('Zur Finanzierung');

        expect(projectDetails.find('project-comments')).not.toExist();
    });

    it("should show a not found page if no project was found", function () {

        $httpBackend.expectGET('/project/xyz').respond(404);

        $scope.$digest();
        $httpBackend.flush();

        expect($location.path()).toBe('/error/notfound');
    });

    it("should show a technical failure page if the server responds with an unexpected status code", function () {

        $httpBackend.expectGET('/project/xyz').respond(500);

        $scope.$digest();
        $httpBackend.flush();

        expect($location.path()).toBe('/error/unknown');
    });

    it("should show a different text on the to-pledging-form-button when the project is fully pledged", function () {
        prepareBackendMock('FULLY_PLEDGED');

        $scope.$digest();
        $httpBackend.flush();

        expect(projectDetails.find('.to-pledging-form-button')).toBeDisabled();
        expect(projectDetails.find('.to-pledging-form-button')).toHaveText('Zu 100% finanziert!');
    });

    it("should show the comments directive if the user is logged in", function () {
        prepareBackendMock('PUBLISHED');
        $httpBackend.expectGET('/user/current').respond(200, { budget: 55 });
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        expect(projectDetails.find('project-comments')).toExist();
    });

});
