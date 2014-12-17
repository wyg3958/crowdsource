var gulp = require('gulp');
var sourcemaps = require('gulp-sourcemaps');
var ngAnnotate = require('gulp-ng-annotate');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');
var sass = require('gulp-sass');

var BASE_DIR = 'src/main';
var SCSS_DIR = BASE_DIR + '/scss';
var APP_DIR = BASE_DIR + '/resources/public';

var JS_FILES = APP_DIR + '/**/*.js';
var SCSS_FILES = SCSS_DIR + '/**/*.scss';

// all files except javascript files
var RESOURCE_FILES = [APP_DIR + '/**/*', '!' + JS_FILES];

var JS_LIB_FILES = [
    'bower_components/angular-ellipsis/src/angular-ellipsis.min.js'
];

var BASE_DEST_DIR = 'target/classes/public';

// sass compile task
gulp.task('sass', function () {
    return gulp.src(SCSS_DIR + '/crowdsource.scss')
        .pipe(sourcemaps.init())
        .pipe(sass({outputStyle: 'compressed', includePaths: ['bower_components']}))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(BASE_DEST_DIR + '/css'));
});

// copies all files except js files to the target directory (js files are processed by the js task)
gulp.task('resources', function() {
    return gulp.src(RESOURCE_FILES)
        .pipe(gulp.dest(BASE_DEST_DIR + '/')); // the trailing slash is important, else the files will be copied into a wrong directory
});

// js compile task
gulp.task('js', function() {
    return gulp.src(JS_FILES)
        .pipe(sourcemaps.init())
            .pipe(ngAnnotate())
            .pipe(uglify())
            .pipe(concat('crowdsource.min.js'))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(BASE_DEST_DIR + '/app'));
});

// concat javascript libraries
gulp.task('js-libs', function() {
    return gulp.src(JS_LIB_FILES)
        .pipe(concat('libs.min.js'))
        .pipe(gulp.dest(BASE_DEST_DIR + '/app'));
});

// exec "node_modules/.bin/gulp watch" or "npn run watch" for local development. will compile sass on changes.
gulp.task('watch', function () {
    gulp.watch(JS_FILES, ['js']);
    gulp.watch(RESOURCE_FILES, ['resources']);
    gulp.watch(SCSS_FILES, ['sass']);
});

// sass is the default gulp task
gulp.task('default', ['js-libs', 'js', 'resources', 'sass']);
