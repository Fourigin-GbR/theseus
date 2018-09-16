/**
 * Created by Karsten Schäfer
 */
var gulp = require("gulp"),
    sass = require("gulp-sass");
//
//
gulp.task("sass", function () {
    return gulp.src("resources/scss/**/*.scss")
        .pipe(sass().on("error", sass.logError))
        .pipe(gulp.dest("production/assets/styles"));
});

gulp.task("sass:watch", function () {
    gulp.watch("resources/scss/**/*.scss", ["sass"]);
});
