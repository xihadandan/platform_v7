#!/usr/bin/env node

'use strict';

const minimist = require('minimist');
const devCompiler = require('../src/scripts/compiler-dev');
const ptCompiler = require('../src/scripts/compiler');

async function run() {
  const argv = minimist(process.argv.slice(2));
  const min = !!argv.min;

  if (!argv.pt) {
    // **业务线环境 (默认)**

    // - 生成主题资源静态文件
    if (argv.resource) {
      devCompiler.generateDevResourceJson(min);
    }

    // - 生成主题文件
    if (argv.theme === 'all') {
      await devCompiler.compileAllDevThemes(min);
    } else if (argv.theme) {
      // await devCompiler.regenerateTheme(argv.theme);
      await devCompiler.regenerateDevTheme(argv.theme);
    }
  } else {
    // **平台环境**

    // - 生成主题资源静态文件
    if (argv.resource) {
      ptCompiler.generateResourceJson(min);
    }

    // - 生成主题文件
    if (argv.theme === 'all') {
      await ptCompiler.compileAllThemes(min);
    } else if (argv.theme) {
      await ptCompiler.regenerateTheme(argv.theme, min);
    }
  }
}

run();
