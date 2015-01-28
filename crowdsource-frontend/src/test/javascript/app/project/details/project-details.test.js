describe('project details', function () {

    var $scope, $httpBackend, $window, $location, AuthenticationToken, projectDetails;

    beforeEach(function () {
        module('crowdsource');
        module('crowdsource.templates');

        localStorage.clear(); // reset, makes the user not logged in

        inject(function ($compile, $rootScope, $templateCache, _$window_, $controller, _$location_, _$httpBackend_, Project, _AuthenticationToken_) {
            $scope = $rootScope.$new();
            $httpBackend = _$httpBackend_;
            $window = _$window_;
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

            $httpBackend.whenGET('/financinground/active').respond(200, {active: true});

            var template = $templateCache.get('app/project/details/project-details.html');
            projectDetails = $compile(template)($scope);
        });
    });

    function prepareBackendMock(projectStatus) {
        $httpBackend.expectGET('/project/xyz').respond(200, {
            id: 'xyz',
            status: projectStatus,
            title: 'Title',
            shortDescription: 'Short description',
            description: 'Looong description',
            creator: {name: 'Foo Bar'},
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
        expect(projectDetails.find('.project-status__funding progress-bar .cs-progress__meter')).toHaveCss({width: '69.265%'});
        expect(projectDetails.find('.project-status__pledge-goal')).toHaveText('$20.000');
        expect(projectDetails.find('.project-status__pledged-amount')).toHaveText('$13.853');
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

    it("should show a forbidden error page if the server responds with 403", function () {

        $httpBackend.expectGET('/project/xyz').respond(403);

        $scope.$digest();
        $httpBackend.flush();

        expect($location.path()).toBe('/error/forbidden');
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
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        expect(projectDetails.find('project-comments')).toExist();
    });

    it("should display the publish-button when a project is not published and the user is admin", function () {

        prepareBackendMock('PROPOSED');
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55, roles: ['ROLE_USER', 'ROLE_ADMIN']});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        expect(projectDetails.find('.publish-button')).toExist();
    });

    it("should not display the publish-button when a project is not published and the user is not admin", function () {

        prepareBackendMock('PROPOSED');
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55, roles: ['ROLE_USER']});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        expect(projectDetails.find('.publish-button')).not.toExist();
    });

    it("should not display the publish-button when a project is published", function () {

        prepareBackendMock('PUBLISHED');
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55, roles: ['ROLE_USER', 'ROLE_ADMIN']});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        expect(projectDetails.find('.publish-button')).not.toExist();
    });

    it("should display the reject-button when a project is not reject and the user is admin", function () {

        prepareBackendMock('PROPOSED');
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55, roles: ['ROLE_USER', 'ROLE_ADMIN']});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        expect(projectDetails.find('.reject-button')).toExist();
    });

    it("should not display the reject-button when a project is not published and the user is not admin", function () {

        prepareBackendMock('PROPOSED');
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55, roles: ['ROLE_USER']});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        expect(projectDetails.find('.reject-button')).not.toExist();
    });

    it("should not display the reject-button when a project is rejected", function () {

        prepareBackendMock('REJECTED');
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55, roles: ['ROLE_USER', 'ROLE_ADMIN']});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        expect(projectDetails.find('.reject-button')).not.toExist();
    });

    it("should send the patch-request to the backend when the publish-button is clicked and confirmed", function () {

        prepareBackendMock('PROPOSED');
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55, roles: ['ROLE_USER', 'ROLE_ADMIN']});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        $httpBackend.expectPATCH('/project/xyz', {status: 'PUBLISHED'}).respond(200, {status: 'PUBLISHED'});

        spyOn($window, 'confirm').and.returnValue(true);
        projectDetails.find('.publish-button').click();
        $httpBackend.flush();

        expect(projectDetails.find('.publish-button')).not.toExist();
    });

    it("should not send the patch-request to the backend when the publish confirmation is canceled", function () {

        prepareBackendMock('PROPOSED');
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55, roles: ['ROLE_USER', 'ROLE_ADMIN']});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        spyOn($window, 'confirm').and.returnValue(false);
        projectDetails.find('.publish-button').click();

        expect(projectDetails.find('.publish-button')).toExist();
    });

    it("should send the patch-request to the backend when the reject-button is clicked and confirmed", function () {

        prepareBackendMock('PROPOSED');
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55, roles: ['ROLE_USER', 'ROLE_ADMIN']});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        $httpBackend.expectPATCH('/project/xyz', {status: 'REJECTED'}).respond(200, {status: 'REJECTED'});
        spyOn($window, 'confirm').and.returnValue(true);
        projectDetails.find('.reject-button').click();
        $httpBackend.flush();

        expect(projectDetails.find('.reject-button')).not.toExist();
    });

    it("should not send the patch-request to the backend when the reject confirmation is canceled", function () {

        prepareBackendMock('PROPOSED');
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55, roles: ['ROLE_USER', 'ROLE_ADMIN']});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        spyOn($window, 'confirm').and.returnValue(false);
        projectDetails.find('.reject-button').click();
    });

    it("should redirect to unknown-error page when the reject-request fails", function () {

        prepareBackendMock('PROPOSED');
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55, roles: ['ROLE_USER', 'ROLE_ADMIN']});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        $httpBackend.expectPATCH('/project/xyz', {status: 'REJECTED'}).respond(400);
        spyOn($window, 'confirm').and.returnValue(true);
        projectDetails.find('.reject-button').click();
        $httpBackend.flush();

        expect($location.path()).toBe('/error/unknown');
    });

    it("should redirect to unknown-error page when the publish-request fails", function () {

        prepareBackendMock('PROPOSED');
        $httpBackend.expectGET('/user/current').respond(200, {budget: 55, roles: ['ROLE_USER', 'ROLE_ADMIN']});
        $httpBackend.expectGET('/project/xyz/comments').respond(200, []);

        spyOn(AuthenticationToken, 'hasTokenSet').and.returnValue(true);

        $scope.$digest();
        $httpBackend.flush();

        $httpBackend.expectPATCH('/project/xyz', {status: 'PUBLISHED'}).respond(400);
        spyOn($window, 'confirm').and.returnValue(true);
        projectDetails.find('.publish-button').click();
        $httpBackend.flush();

        expect($location.path()).toBe('/error/unknown');
    });
});
