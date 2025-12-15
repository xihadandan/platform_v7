"use strict";

var exec    = require("child_process");
var gulp    = require("gulp");
var plugins = require("gulp-load-plugins")();

gulp.task("lint-javascript", function()
{
  return gulp.src(["source/**/*.js", "!source/common/javascript/common/bootstrap/*.js", "!source/common/javascript/common/jquery/*.js", , "!source/common/javascript/common/webextension-polyfill-0.6.0/*.js"])
    .pipe(plugins.eslint())
    .pipe(plugins.eslint.format());
});

gulp.task("lint-style-sheets", function()
{
  return gulp.src(["source/**/*.css", "!source/common/style-sheets/common/bootstrap/*.css"])
    .pipe(plugins.plumber({ errorHandler: function(error) { global.errorHandler(error, true, this); } }))
    .pipe(plugins.csslint(".csslintrc.json"))
    .pipe(plugins.csslint.formatter("compact"));
});

gulp.task("lint-web-extension", function(callback)
{
  process.chdir("build/firefox");
  exec.exec("web-ext lint -w", function(error, output, errors)
  {
    console.log(output); // eslint-disable-line no-console
    console.log(errors); // eslint-disable-line no-console
    callback(error);
  });
  process.chdir("../..");
});

gulp.task("lint", ["lint-javascript", "lint-style-sheets"]);
