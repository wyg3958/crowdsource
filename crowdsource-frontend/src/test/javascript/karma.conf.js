module.exports = function (config) {
    config.set({
        basePath: '../../../',

        files: [
            // foundation library is mocked via testsupport/mocked-libraries.testsupport.js
            'bower_components/jquery/dist/jquery.js',
            'bower_components/jasmine-jquery/lib/jasmine-jquery.js',
            'bower_components/angular/angular.js',
            'bower_components/angular-resource/angular-resource.js',
            'bower_components/angular-route/angular-route.js',
            'bower_components/angular-messages/angular-messages.js',
            'bower_components/angular-i18n/angular-locale_de.js',
            'bower_components/angular-mocks/angular-mocks.js',
            'bower_components/angular-ellipsis/src/angular-ellipsis.js',
            'bower_components/ngScrollTo/ng-scrollto.js',
            'bower_components/moment/moment.js',
            'bower_components/moment-timezone/builds/moment-timezone-with-data.js',

            // actually, this file would have been picked up by the wildcard pattern app/**/*.js
            // but maybe in a wrong order. crowdsource.js defines the single crowdsource angular module and
            // must come first.
            'src/test/javascript/testsupport/mocked-libraries.testsupport.js',
            'src/main/resources/public/app/crowdsource.js',
            'src/main/resources/public/app/**/*.js',
            'src/test/javascript/**/*.js',

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
