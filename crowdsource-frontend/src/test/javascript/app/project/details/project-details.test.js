describe('project details', function () {

    var $scope, $httpBackend, $location, projectDetails;

    beforeEach(function() {
        module('crowdsource');
        module('crowdsource.templates');

        inject(function($compile, $rootScope, $templateCache, $controller, _$location_, _$httpBackend_, Project) {
            $scope = $rootScope.$new();
            $httpBackend = _$httpBackend_;
            $location = _$location_;

            $controller('ProjectDetailsController as projectDetails', {
                $scope: $scope,
                $location: $location,
                $routeParams: {
                    projectId: 'xyz'
                },
                Project: Project
            });

            var template = $templateCache.get('app/project/details/project-details.html');
            projectDetails = $compile(template)($scope);
        });
    });

    it("should display the project's details that were retrieved from backend", function () {

        $httpBackend.expectGET('/project/xyz').respond(200, { title: 'Title', shortDescription: 'Short description', description: 'Looong description' });
        $scope.$digest();
        $httpBackend.flush();

        expect(projectDetails.find('h1')).toHaveText('Title');
        expect(projectDetails.find('blockquote')).toHaveText('Short description');
        expect(projectDetails.find('.project-description')).toHaveText('Looong description');
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

});
