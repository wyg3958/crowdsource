module.exports = function (config) {
    config.set({
        basePath: '../../../',

        files: [
            'src/test/javascript/lib/angular-1.3.4/angular.js',
            'src/test/javascript/lib/angular-1.3.4/angular-resource.js',

            'src/test/javascript/lib/jquery-1.11.0.min.js',
            'src/test/javascript/lib/angular-1.3.4/angular-mocks.js',

            'src/main/resources/public/app/**/*.js',
            'src/test/javascript/app/**/*.js'
        ],

        preprocessors: {
            // html->js to make templates available in the tests
            'src/main/resources/public/app/directives/hello/hello.html': ['ng-html2js']
        },

        // html->js config
        ngHtml2JsPreprocessor: {
            // create one single angular module with the name "templates" where everything gets stores
            moduleName: 'templates',
            // strip off the realtive part of the template path
            stripPrefix: 'src/main/resources/public/'
        },

        autoWatch: false,

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
