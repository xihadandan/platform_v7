"use strict";

var gulp    = require("gulp");
var merge   = require("merge-stream");
var plugins = require("gulp-load-plugins")();

global.buildAbout = function(browserName)
{
  return merge(
    gulp.src("source/common/html/about/about.html")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/about")),
    gulp.src("source/common/javascript/about/about.js")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/about/javascript")),
    gulp.src("source/common/style-sheets/about/about.css")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/about"))
  );
};

global.buildAll = function(browserName)
{
  return merge(
    global.buildAbout(browserName),
    global.buildBackground(browserName),
    global.buildCommon(browserName),
    global.buildConfiguration(browserName),
    global.buildContent(browserName),
    global.buildLocales(browserName),
    global.buildOptions(browserName),
    global.buildOverlay(browserName),
  );
};

global.buildBackground = function(browserName)
{
  return merge(
    gulp.src("source/common/html/background/background.html")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/background")),
    gulp.src("source/common/javascript/background/background.js")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/background/javascript"))
  );
};

global.buildCommon = function(browserName)
{
  return merge(
    gulp.src("source/common/javascript/common/bootstrap/*")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/common/javascript/bootstrap")),
    gulp.src("source/common/javascript/common/jquery/*")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/common/javascript/jquery")),
    gulp.src("source/common/javascript/common/webextension-polyfill-0.6.0/*")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/common/javascript/webextension-polyfill-0.6.0")),
    gulp.src("source/common/javascript/common/common.js")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/common/javascript")),
    gulp.src("source/common/javascript/locales/locales.js")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/locales")),
    gulp.src("source/common/javascript/storage/storage.js")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/storage")),
    gulp.src("source/common/javascript/page/page.js")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/page")),
    gulp.src("source/common/javascript/upgrade/upgrade.js")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/upgrade")),
    gulp.src(["source/common/style-sheets/common/bootstrap/bootstrap.css", "source/common/style-sheets/common/bootstrap/font-awesome.css"])
      .pipe(plugins.concat("bootstrap.css"))
      .pipe(plugins.replace("fonts/fontawesome-webfont", "../fonts/fontawesome-webfont"))
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/common/style-sheets")),
    gulp.src("source/common/style-sheets/common/bootstrap/bootstrap.css.map")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/common/style-sheets")),
    gulp.src("source/common/style-sheets/generated/common.css")
      .pipe(plugins.rename("generated.css"))
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/common/style-sheets")),
    gulp.src("images/common/logos/**")
      .pipe(gulp.dest("build/" + browserName + "/common/images/logos")),
    gulp.src("source/common/fonts/*")
      .pipe(gulp.dest("build/" + browserName + "/common/fonts"))
  );
};

global.buildConfiguration = function(browserName)
{
  return gulp.src(["configuration/common/manifest-top.json", "configuration/" + browserName + "/manifest.json", "configuration/common/manifest-bottom.json"])
    .pipe(plugins.concat("manifest.json"))
    .pipe(plugins.replace('{"remove-top": "",', ""))
    .pipe(plugins.replace('"remove-bottom": ""}', ""))
    .pipe(plugins.replace(/^\s*[\r\n]/gm, ""))
    .pipe(global.filterProperties(browserName)())
    .pipe(gulp.dest("build/" + browserName));
};

global.buildContent = function(browserName)
{
  return gulp.src(["source/common/javascript/common/common.js", "source/common/javascript/content/content.js"])
    .pipe(plugins.concat("content.js"))
    .pipe(global.filterProperties(browserName)())
    .pipe(gulp.dest("build/" + browserName + "/content"));
};

global.buildLocales = function(browserName)
{
  return gulp.src("source/common/locales/**")
    .pipe(global.filterProperties(browserName)())
    .pipe(gulp.dest("build/" + browserName + "/_locales"));
};

global.buildOptions = function(browserName)
{
  return merge(
    gulp.src(["source/common/html/options/options.html"])
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/options")),
    gulp.src(["source/common/javascript/options/options.js"])
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/options/javascript")),
    gulp.src(["source/common/style-sheets/options/options.css"])
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/options")),
    gulp.src(["images/common/options.png", "images/common/options-2x.png"])
      .pipe(gulp.dest("build/" + browserName + "/options/images"))
  );
};

global.buildOverlay = function(browserName)
{
  return merge(
    gulp.src("source/common/html/overlay/overlay.html")
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/overlay")),
    gulp.src(["source/common/javascript/overlay/*", "source/" + browserName + "/javascript/overlay/*"])
      .pipe(plugins.concat("overlay.js"))
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/overlay/javascript")),
    gulp.src(["source/common/style-sheets/overlay/overlay.css", "source/" + browserName + "/style-sheets/overlay/overlay.css"])
      .pipe(plugins.concat("overlay.css"))
      .pipe(global.filterProperties(browserName)())
      .pipe(gulp.dest("build/" + browserName + "/overlay")),
    gulp.src(["images/common/feature.png", "images/common/feature-2x.png", "images/common/toolbar.png", "images/common/toolbar-2x.png"])
      .pipe(gulp.dest("build/" + browserName + "/overlay/images"))
  );
};

