describe('project list', function () {

    var $scope, $httpBackend, $location, projectList;

    beforeEach(function() {
        module('crowdsource');
        module('crowdsource.templates');

        inject(function($compile, $rootScope, $templateCache, $controller, _$httpBackend_, _$location_, Project) {
            $scope = $rootScope.$new();
            $httpBackend = _$httpBackend_;
            $location = _$location_;

            $controller('ProjectListController as projectList', {
                $scope: $scope,
                $location: _$location_,
                Project: Project
            });

            var template = $templateCache.get('app/project/list/project-list.html');
            projectList = $compile(template)($scope);
        });
    });

    it('should display the projects that were retrieved from backend', function () {

        $httpBackend.expectGET('/projects').respond(200, [
            { title: 'Title', shortDescription: 'Short description' },
            { title: 'Title 2', shortDescription: 'Short description 2' }
        ]);
        $scope.$digest();
        $httpBackend.flush();

        var listItems = projectList.find('li');
        expect(listItems).toHaveLength(2);

        expect($(listItems[0]).find('h1')).toHaveText('Title');
        expect($(listItems[0]).find('p')).toHaveText('Short description');

        expect($(listItems[1]).find('h1')).toHaveText('Title 2');
        expect($(listItems[1]).find('p')).toHaveText('Short description 2');
    });

    it("should redirect to the project's details page when the project tile is clicked", function () {
        $httpBackend.expectGET('/projects').respond(200, [
            { id: 'projectId', title: 'Title', shortDescription: 'Short description' }
        ]);
        $scope.$digest();
        $httpBackend.flush();

        var tile = projectList.find('li .project-tile');
        tile.click();

        expect($location.path()).toBe('/project/projectId');
    });

});
