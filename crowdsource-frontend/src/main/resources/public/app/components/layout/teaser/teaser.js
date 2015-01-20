angular.module('crowdsource')

    .directive('teaser', function() {
        var directive = {};

        directive.templateUrl = 'app/components/layout/teaser/teaser.html',
        directive.controllerAs = 'teaser';
        directive.bindToController = true;

        directive.controller = function() {

        };

        return directive;
    });
