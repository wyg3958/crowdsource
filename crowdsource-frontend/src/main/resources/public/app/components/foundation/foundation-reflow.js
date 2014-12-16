angular.module('crowdsource')

    .factory('Foundation', function($timeout) {
        var service = {};

        service.reflow = function() {
            $timeout(function() {
                $(document).foundation('reflow');
            });
        };

        return service;
    })

    .directive('foundationReflow', function(Foundation) {
        return {
            restrict: 'A',
            link: function(scope, elem, attrs) {
                if (attrs.ngRepeat) {
                    // if this directive was applied on an ng-repeat element, reflow only after ng-repeat is done
                    if (scope.$last) {
                        Foundation.reflow();
                    }
                }
                else {
                    Foundation.reflow();
                }
            }
        }
    });
