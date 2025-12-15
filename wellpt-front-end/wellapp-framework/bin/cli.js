#!/usr/bin/env node

'use strict';
const path = require('path');
const fs = require('fs');
const program = require('commander');
const Archive = require('archive-tool');
const pkg = require(path.resolve('./package.json'));
const os = require('os');
const { execSync } = require('child_process');
const chalk = require('chalk');

program
  .command('build')
  .option('--target [target]', '输出目录 , 默认 ./output')
  .option('--filename [filename]', '压缩文件名 , 默认工程 package.json 里面的名称')
  .option('--format [format]', '输出压缩文件格式 , 默认 zip , 可选 tar')
  .option('--registry [registry]', 'npm 仓库地址')
  .option('--target [path]', '压缩输出目录 , 默认 ./output')
  .description('压缩构建工程包')
  .action(option => {
    const platform = os.platform();
    // 执行编译
    console.log(chalk.green('------ start build prod files ------'));
    execSync(`cross-env easy clean && easy build prod`, { stdio: 'inherit' });
    console.log(chalk.green(`------ start build prod file successfully ------ `));

    const config = {
      target: './output',
      filename: pkg.name,
      format: 'zip',
      source: ['favicon.png'],
      installDeps: {
        registry: 'http://192.168.0.111:4873'
      }
    };
    if (program.target || option.target) {
      config.target = program.target || option.target;
    }
    if (program.filename || option.filename) {
      config.filename = program.filename || option.filename;
    }
    if (program.target || option.target) {
      config.target = program.target || option.target;
    }
    if (program.registry || option.registry) {
      config.installDeps.registry = program.registry || option.registry;
    }
    if (program.format || option.format) {
      config.format = program.format || option.format;
    }

    const archive = new Archive(config);
    let defaultInstallDeps = archive.installDeps;
    let startTime = Date.now(),
      time = undefined;
    console.log(chalk.green(`------ 开始安装依赖 ------`));
    archive.installDeps = function (target, options) {
      if (platform == 'win32') {
        archive.installDeps = function (target, options) {
          execSync(`cd ${path.resolve(target)} && npm install --production`, { stdio: 'inherit' });
          console.log(chalk.green(`------ npm install --production dependencies successfully ------`));
        };
      } else {
        defaultInstallDeps.call(archive, target, options);
      }
      time = minutesSeconds(startTime, Date.now());
      console.log(chalk.green(`------ ✅ 安装依赖结束 , 耗时 : ${time.minutes}分${time.seconds}秒 ------`));
    };
    startTime = Date.now();
    console.log(chalk.green(`------ 开始打包 ------`));
    archive.zip();
    time = minutesSeconds(startTime, Date.now());
    console.log(chalk.green(`------ ✅ 打包结束 , 耗时 : ${time.minutes}分${time.seconds}秒 ------`));
  });

function minutesSeconds(start, end) {
  const diffMs = new Date(end) - new Date(start);
  const totalSeconds = Math.floor(diffMs / 1000);
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  return {
    minutes,
    seconds
  };
}

program.parse(process.argv);
