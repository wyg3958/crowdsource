module.exports = function (config) {
    config.set({
        basePath: '../../../',

        files: [
            'bower_components/jquery/dist/jquery.js',
            'bower_components/jasmine-jquery/lib/jasmine-jquery.js',
            'bower_components/angular/angular.js',
            'bower_components/angular-resource/angular-resource.js',
            'bower_components/angular-route/angular-route.js',
            'bower_components/angular-messages/angular-messages.js',
            'bower_components/angular-mocks/angular-mocks.js',

            'src/main/resources/public/app/**/*.js',
            'src/test/javascript/**/*.test.js',

            'src/main/resources/public/app/**/*.html'
        ],

        preprocessors: {
            // html->js to make templates available in the tests
            'src/main/resources/public/app/**/*.html': ['ng-html2js']
        },

        // html->js config
        ngHtml2JsPreprocessor: {
            // create one single angular module with the name "templates" where everything gets stores
            moduleName: 'crowdsource.templates',
            // strip off the realtive part of the template path
            stripPrefix: 'src/main/resources/public/'
        },

        autoWatch: true,

        frameworks: ['jasmine'],

        browsers: ['PhantomJS'],

        plugins: [
            'karma-phantomjs-launcher',
            'karma-ng-html2js-preprocessor',
            'karma-jasmine',
            'karma-jasmine-jquery',
            'karma-junit-reporter'
        ],

        reporters: ['progress', 'junit'],

        junitReporter: {
            outputFile: 'target/surefire-reports/TEST-karma.xml',
            suite: 'karma'
        }
    });
};
