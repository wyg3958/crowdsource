var gulp = require('gulp');
var sourcemaps = require('gulp-sourcemaps');
var ngAnnotate = require('gulp-ng-annotate');
var uglify = require('gulp-uglify');
var concat = require('gulp-concat');
var sass = require('gulp-sass');

var BASE_DIR = 'src/main/resources/public';
var JS_FILES = BASE_DIR + '/app/**/*.js';

gulp.task('sass', function() {
    return gulp.src(BASE_DIR + '/app/crowdsource.scss')
        .pipe(sourcemaps.init())
            .pipe(sass({ outputStyle: 'compressed', includePaths: ['bower_components'] }))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(BASE_DIR));
});

gulp.task('watch', function() {
    return gulp.watch(BASE_DIR + '/app/**/*.scss', ['sass']);
});

// Uncomment this for building a single minified javascript file
//gulp.task('js', function() {
//    return gulp.src(JS_FILES)
//        .pipe(sourcemaps.init())
//            .pipe(ngAnnotate())
//            .pipe(uglify())
//            .pipe(concat('crowdsource.min.js'))
//        .pipe(sourcemaps.write('.'))
//        .pipe(gulp.dest(BASE_DIR));
//});
//
//gulp.task('watch', function() {
//    return gulp.watch(JS_FILES, ['js']);
//});

gulp.task('default', ['sass']);
