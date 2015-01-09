angular.module('crowdsource')

    .directive('rangeSlider', function() {
        return {
            require: 'ngModel',
            template:
                '<div class="range-slider" data-slider foundation-reflow="slider">' +
                    '<span class="range-slider-handle" role="slider" tabindex="0"></span>' +
                    '<span class="range-slider-active-segment"></span>' +
                '</div>',
            link: function(scope, element, attributes, ngModel) {

                var slider = element.find('[data-slider]');

                ngModel.$render = function() {
                    if (ngModel.$viewValue) {
                        slider.foundation('slider', 'set_value', ngModel.$viewValue);

                        // http://www.bradleyhamilton.com/blog/foundation-range-slider-callback-not-firing-after-setting-the-data-slider-value-dynamically
                        registerChangeListener();
                    }
                };

                function registerChangeListener() {
                    slider.on('change.fndtn.slider', function () {
                        var value = slider.attr('data-slider');
                        ngModel.$setViewValue(parseInt(value));
                    });
                }

                registerChangeListener();
            }
        };
    });