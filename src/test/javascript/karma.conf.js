module.exports = function (config) {
    config.set({
        basePath: '../../../',

        files: [
            'src/main/resources/public/lib/angular-1.3.0-rc.4/angular.min.js',
            'src/main/resources/public/lib/angular-1.3.0-rc.4/angular-resource.min.js',

            'src/test/javascript/lib/jquery-1.11.0.min.js',
            'src/test/javascript/lib/angular-1.3.0-rc.4/angular-mocks.js',

            'src/main/resources/public/app/crowdsource.js',

            'src/main/resources/public/app/services/helloservice.js',
            'src/test/javascript/app/services/helloservicetest.js',

            'src/main/resources/public/app/directives/hello/hello.js',
            'src/main/resources/public/app/directives/hello/hello.html',
            'src/test/javascript/app/directives/hello/hellotest.js'
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
            outputFile: 'target/surefire-reports/karma.xml',
            suite: ''
        }
    });
};
