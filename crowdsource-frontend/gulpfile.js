var gulp = require('gulp');
var sourcemaps = require('gulp-sourcemaps');
var ngAnnotate = require('gulp-ng-annotate');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');
var sass = require('gulp-sass');

var BASE_DIR = 'src/main/scss';

// sass compile task
gulp.task('sass', function () {
    return gulp.src(BASE_DIR + '/crowdsource.scss')
        .pipe(sourcemaps.init())
        .pipe(sass({outputStyle: 'compressed', includePaths: ['bower_components']}))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest("target/classes/public/css"));
});

// exec "node_modules/.bin/gulp watch" or "npn run watch" for local development. will compile sass on changes.
gulp.task('watch', function () {
    return gulp.watch(BASE_DIR + '/**/*.scss', ['sass']);
});

// sass is the default gulp task
gulp.task('default', ['sass']);
