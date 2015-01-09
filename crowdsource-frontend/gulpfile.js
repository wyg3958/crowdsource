var gulp =        require('gulp');
var sourcemaps =  require('gulp-sourcemaps');
var ngAnnotate =  require('gulp-ng-annotate');
var uglify =      require('gulp-uglify');
var concat =      require('gulp-concat');
var merge =       require('merge-stream');
var sass =        require('gulp-sass');
var plumber =     require('gulp-plumber');


// Include our config
var config = require('./config.js');


// ---
// Gulp Tasks Definitions
// ---

// SASS compile task

gulp.task('sass', function () {
    return gulp.src(config.scssDir + '/crowdsource.scss')

        // Prevents errors from interrupting watch task
        .pipe(plumber(function (err) {
            console.log(err);
            this.emit('end');
        }))

        .pipe(sourcemaps.init())
            .pipe(sass({
                includePaths: ['bower_components'],

                // Sourcemaps will only work, if sass output is not compressed or minified,
                // comment out the following line to make sourcemaps work:
                output_style: 'compressed'
            }))
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest(config.baseDestDir + '/css'));
});

// copies all files except js files to the target directory (js files are processed by the js task)
gulp.task('resources', function() {
    return gulp.src(config.resourceFiles)
        .pipe(gulp.dest(config.baseDestDir + '/')); // the trailing slash is important, else the files will be copied into a wrong directory
});

// js compile task
gulp.task('js', function() {
    return gulp.src(config.jsFiles)
        .pipe(sourcemaps.init())
            .pipe(ngAnnotate())
            .pipe(uglify())
            .pipe(concat('crowdsource.min.js'))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(config.baseDestDir + '/app'));
});

// concat javascript libraries
gulp.task('js-libs', function() {
    var alreadyMinified = gulp.src(config.jsLibFiles.minified);

    var nowMinified = gulp.src(config.jsLibFiles.unminified)
        .pipe(ngAnnotate())
        .pipe(uglify());

    merge(alreadyMinified, nowMinified)
        .pipe(concat('libs.min.js'))
        .pipe(gulp.dest(config.baseDestDir + '/app'));
});

// exec "node_modules/.bin/gulp watch" or "npn run watch" for local development. will compile sass on changes.
gulp.task('watch', function () {
    gulp.watch(config.jsFiles, ['js']);
    gulp.watch(config.resourceFiles, ['resources']);
    gulp.watch(config.scssFiles, ['sass']);
});

// sass is the default gulp task
gulp.task('default', ['js-libs', 'js', 'resources', 'sass']);
