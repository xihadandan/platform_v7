const gulp = require('gulp');
const javascriptObfuscator = require('gulp-javascript-obfuscator');

// 混淆js代码
gulp.task('jsObfuscator', done => {
  const ruleMap = {
    'wellapp-admin': {
      'app/router.js': 'app',
      'app/controller/**/*.js': 'app/controller',
      'app/assets/**/*.js': 'app/assets',
      'app/service/**/*.js': 'app/service'
    },
    'wellapp-dyform': {
      'config/**/*.js': 'config',
      'app/router.js': 'app',
      'app/io/**/*.js': 'app/io',
      'app/controller/**/*.js': 'app/controller',
      'app/assets/**/*.js': 'app/assets',
      'app/service/**/*.js': 'app/service'
    },
    'wellapp-for-prod': {
      'app/router.js': 'app',
      'app/controller/**/*.js': 'app/controller',
      // 'app/assets/**/*.js': 'app/assets',
      'app/service/**/*.js': 'app/service'
    },
    'wellapp-framework': {
      'app.js': '/',
      'app/*.js': 'app',
      'lib/**/*.js': 'lib',
      'config/**/*.js': 'config',
      'app/extend/**/*.js': 'app/extend',
      'app/controller/**/*.js': 'app/controller',
      'app/io/**/*.js': 'app/io',
      'app/middleware/**/*.js': 'app/middleware',
      'app/service/**/*.js': 'app/service'
    },
    'wellapp-mobile': {
      'app/router.js': 'app',
      'app/component/**/*.js': 'app/component',
      'app/controller/**/*.js': 'app/controller',
      'app/service/**/*.js': 'app/service'
    },
    'wellapp-page-assembly': {
      'app/router.js': 'app',
      'app/constant.js': 'app',
      'app/component/**/*.js': 'app/component',
      'app/controller/**/*.js': 'app/controller',
      'app/service/**/*.js': 'app/service',
      'app/assets/**/*.js': 'app/assets'
    },
    'wellapp-workflow': {
      'app/router.js': 'app',
      'app/controller/**/*.js': 'app/controller',
      'app/service/**/*.js': 'app/service',
      'app/assets/**/*.js': 'app/assets'
    }
  };

  for (let key in ruleMap) {
    let data = ruleMap[key];
    console.log(`开始压缩混淆代码模块: ${key}`);
    for (let path in data) {
      console.log(`压缩混淆路径: ${path}`);
      gulp
        .src(`node_modules/${key}/${path}`)
        .pipe(javascriptObfuscator({ compact: true, selfDefending: true }))
        .pipe(gulp.dest(`node_modules/${key}/${data[path]}`));
    }
    console.log(`结束压缩混淆代码模块: ${key}`);
  }
  done();
});
