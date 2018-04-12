/**
 * Created by Karsten Schäfer
 */
var gulp = require("gulp"),
    sass = require("gulp-sass");
//
//
gulp.task("default", function () {
    return gulp.src("resources/scss/**/*.scss")
        .pipe(sass().on("error", sass.logError))
        .pipe(gulp.dest("production/assets/styles"));
});


