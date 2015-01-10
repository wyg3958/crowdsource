angular.module('crowdsource')

    .factory('Foundation', function($timeout) {
        var service = {};

        service.reflow = function(reflowComponent) {
            // angular may not have fully completed it's digest cycle
            // -> delay the reflow for the next javascript cycle
            $timeout(function() {
                if (reflowComponent) {
                    // only reflow certain foundation element types, e.g. 'slider'
                    $(document).foundation(reflowComponent, 'reflow');
                }
                else {
                    $(document).foundation('reflow');
                }
            });
        };

        return service;
    })

    .directive('foundationReflow', function(Foundation) {
        return {
            restrict: 'A',
            link: function(scope, elem, attrs) {
                var reflowComponent = attrs.foundationReflow;

                if (attrs.ngRepeat) {
                    // if this directive was applied on an ng-repeat element, reflow only after ng-repeat is done
                    if (scope.$last) {
                        Foundation.reflow(reflowComponent);
                    }
                }
                else {
                    Foundation.reflow(reflowComponent);
                }
            }
        }
    });
