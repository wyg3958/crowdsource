var baseDir = 'src/main',
    scssDir = baseDir + '/scss',
    appDir  = baseDir + '/resources/public',
    jsFiles = appDir  + '/**/*.js';


var config = {
  baseDir:   baseDir,
  scssDir:   scssDir,
  appDir:    baseDir + '/resources/public',
  jsFiles:   appDir  + '/**/*.js',
  scssFiles: scssDir + '/**/*.scss',
  resourceFiles: [appDir + '/**/*', '!' + jsFiles],

  jsLibFiles: {
    minified: ['bower_components/angular-ellipsis/src/angular-ellipsis.min.js'],
    unminified: ['bower_components/angular-i18n/angular-locale_de.js']
  },

  baseDestDir: 'target/classes/public'
};

module.exports = config;
