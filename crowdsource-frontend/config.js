var baseDir = 'src/main',
    scssDir = baseDir + '/scss',
    appDir = baseDir + '/resources/public',
    jsFiles = appDir + '/**/*.js';


var config = {
    baseDir: baseDir,
    scssDir: scssDir,
    appDir: baseDir + '/resources/public',
    jsFiles: appDir + '/**/*.js',
    scssFiles: scssDir + '/**/*.scss',
    resourceFiles: [appDir + '/**/*', '!' + jsFiles],

    jsLibFiles: {
        minified: [
            'bower_components/angular-ellipsis/src/angular-ellipsis.min.js',
            'bower_components/moment/min/moment.min.js',
            'bower_components/moment-timezone/builds/moment-timezone-with-data.min.js'],
        unminified: [
            'bower_components/angular-i18n/angular-locale_de.js',
            'bower_components/ngScrollTo/ng-scrollto.js',
            'bower_components/foundation-datepicker/js/foundation-datepicker.js'
        ]
    },

    baseDestDir: 'target/classes/public'
};

module.exports = config;
