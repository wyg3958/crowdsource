describe('project list', function () {

    var $scope, $httpBackend, $location, projectList;

    beforeEach(function () {
        module('crowdsource');
        module('crowdsource.templates');

        localStorage.clear(); // reset

        inject(function ($compile, $rootScope, $templateCache, $controller, _$httpBackend_, _$location_, Project) {
            $scope = $rootScope.$new();
            $httpBackend = _$httpBackend_;
            $location = _$location_;

            $controller('ProjectListController as projectList', {
                $scope: $scope,
                $location: _$location_,
                Project: Project
            });

            var template = $templateCache.get('app/project/list/project-list.html');

            // wrap the template in a <div>, or $compile will return multiple elements for each top level element
            projectList = $compile('<div>' + template + '<div>')($scope);
        });
    });

    it('should display the projects that were retrieved from backend', function () {

        $httpBackend.expectGET('/projects').respond(200, [
            project('Title', 'Short Description', 100, 10, 1),
            project('Title 2', 'Short Description 2', 100, 20, 2)
        ]);
        $scope.$digest();
        $httpBackend.flush();

        var listItems = projectList.find('.project-tile');
        expect(listItems).toHaveLength(2);
        expect($(listItems[0]).find('h1')).toHaveText('Title');
        expect($(listItems[0]).find('p')).toHaveText('Short Description');
        expect($(listItems[0]).find('.meter').css("width")).toBe("10%");
        expect($(listItems[0]).find('.backers-count')).toHaveText('1');
        expect($(listItems[0]).find('.backers-text').text()).toBe('Backer');
        expect($(listItems[0]).find('.pledged-amount').text()).toBe('10 $');

        expect($(listItems[1]).find('h1')).toHaveText('Title 2');
        expect($(listItems[1]).find('p')).toHaveText('Short Description 2');
        expect($(listItems[1]).find('.meter').css("width")).toBe("20%");
        expect($(listItems[1]).find('.backers-count')).toHaveText('2');
        expect($(listItems[1]).find('.backers-text').text()).toBe('Backers');
        expect($(listItems[1]).find('.pledged-amount').text()).toBe('20 $');

        expect(projectList.find('.no-projects')).toHaveClass('ng-hide');
    });

    it("should redirect to the project's details page when the project tile is clicked", function () {
        $httpBackend.expectGET('/projects').respond(200, [
            project('Title', 'Short Description', 100, 10, 1),
        ]);
        $scope.$digest();
        $httpBackend.flush();

        var tile = projectList.find('.project-tile');
        tile.click();

        expect($location.path()).toBe('/project/projectId');
    });

    it('should show a message if no projects exist yet', function () {
        $httpBackend.expectGET('/projects').respond(200, []);
        $scope.$digest();
        $httpBackend.flush();

        expect(projectList.find('.no-projects')).not.toHaveClass('ng-hide');
    });

    it('should hide the "create a new project" section while loading', function () {
        $httpBackend.expectGET('/projects').respond(200, []);
        $scope.$digest();

        expect(projectList.find('.no-projects')).toHaveClass('ng-hide');

        $httpBackend.flush();

        expect(projectList.find('.no-projects')).not.toHaveClass('ng-hide');
    });

    function project(title, shortDescription, pledgeGoal, pledgedAmount, backers) {
        var project = {};
        project.id = 'projectId';
        project.title = title;
        project.shortDescription = shortDescription;
        project.pledgeGoal = pledgeGoal;
        project.pledgedAmount = pledgedAmount;
        project.backers = backers;
        return project;
    }
});
