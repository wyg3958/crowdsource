var gulp = require('gulp');
var sourcemaps = require('gulp-sourcemaps');
var ngAnnotate = require('gulp-ng-annotate');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');
var sass = require('gulp-sass');

var BASE_DIR = 'src/main';
var SCSS_DIR = BASE_DIR + '/scss';
var APP_DIR = BASE_DIR + '/resources/public';

var BASE_DEST_DIR = 'target/classes/public';

// sass compile task
gulp.task('sass', function () {
    return gulp.src(SCSS_DIR + '/crowdsource.scss')
        .pipe(sourcemaps.init())
        .pipe(sass({outputStyle: 'compressed', includePaths: ['bower_components']}))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(BASE_DEST_DIR + '/css'));
});

gulp.task('copy-html-js', function() {
    return gulp.src(APP_DIR + '/**/*')
        .pipe(gulp.dest(BASE_DEST_DIR + '/')); // the trailing slash is important, else the files will be copied into a wrong directory
});

// exec "node_modules/.bin/gulp watch" or "npn run watch" for local development. will compile sass on changes.
gulp.task('watch', function () {
    gulp.watch(SCSS_DIR + '/**/*.scss', ['sass']);
    gulp.watch(APP_DIR + '/**/*', ['copy-html-js']);
});

// sass is the default gulp task
gulp.task('default', ['sass']);
