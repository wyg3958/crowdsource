describe('project comments directive', function () {

    var $scope, $compile, $httpBackend, AuthenticationToken;

    beforeEach(function() {
        module('crowdsource');
        module('crowdsource.templates');

        localStorage.clear(); // reset

        inject(function($rootScope, _$compile_, _$httpBackend_, _AuthenticationToken_) {
            $scope = $rootScope.$new();
            $compile = _$compile_;
            $httpBackend = _$httpBackend_;
            AuthenticationToken = _AuthenticationToken_;
        });
    });

    function compileDirective() {
        var root = $compile('<project-comments project="project"></project-comments>')($scope);
        $scope.$digest();
        return {
            root: root
        };
    }

    function expectValidationError(formGroup, violatedRule) {
        expect(formGroup.getLabelContainer()).toHaveClass('error');
        expect(formGroup.getLabel()).toHaveClass('ng-hide');
        expect(formGroup.getErrorLabelsContainer()).not.toHaveClass('ng-hide');
        expect(formGroup.getErrorLabelForRule(violatedRule)).toExist();
    }

    function expectNoValidationError(formGroup) {
        expect(formGroup.getLabelContainer()).not.toHaveClass('error');
        expect(formGroup.getLabel()).not.toHaveClass('ng-hide');
        expect(formGroup.getErrorLabelsContainer()).toHaveClass('ng-hide');
    }

    function getGeneralError(elements, errorCode) {
        return elements.root.find('[ng-message="' + errorCode + '"]');
    }


    it("should show the comments of the project", function () {

        $scope.project = { id: 'xxyyzz' };

        $httpBackend.expectGET('/project/xxyyzz/comments').respond(200, [
            {"created":"2015-01-17T18:21:04.351Z","userName":"User Name","comment":"aa"},
            {"created":"2015-01-17T18:34:57.167Z","userName":"Foo Bart","comment":"foooaaah text\n\naargh"}]);

        var elements = compileDirective();
        $httpBackend.flush();

        var comments = elements.root.find('.comment');
        expect(comments).toHaveLength(2);

        expect($(comments[0]).find('.comment-user')).toHaveText('User Name');
        expect($(comments[0]).find('.comment-date')).toHaveText('17.01.15 19:21');
        expect($(comments[0]).find('.comment-comment')).toHaveText('aa');

        expect($(comments[1]).find('.comment-user')).toHaveText('Foo Bart');
        expect($(comments[1]).find('.comment-date')).toHaveText('17.01.15 19:34');
        expect($(comments[1]).find('.comment-comment')).toHaveText('foooaaah text\n\naargh');
    });

    it("should not show the comments panel while loading and if there are no comments for this project", function () {
        // TODO
        expect(true).toBe(false);
    });

    it("should add a comment", function () {
        // TODO
        expect(true).toBe(false);
    });

    it("should show a required error if the comment field is filled and cleared again", function () {
        // TODO
        expect(true).toBe(false);
    });

    it("should show a remote_unknown error if an unknown error occurred", function () {
        // TODO
        expect(true).toBe(false);
    });
});
