#!/usr/bin/env node

'use strict';
const gulp = require('gulp');
const svgstore = require('gulp-svgstore');
const rename = require('gulp-rename');
const svgo = require('gulp-svgo');
const tap = require('gulp-tap');
const lodash = require('lodash');

const isWatchMode = process.argv.includes('--watch') || process.argv.includes('-w');
function createSvgSprite() {
  console.log('🔧 Start Generate SVG Sprite ...');
  const start = Date.now();
  return (
    gulp
      .src(['app/web/assets/svg-icons/*.svg', 'node_modules/wellapp-web/app/web/assets/svg-icons/*.svg']) // 源图标目录
      .pipe(
        tap(function (file) {
          if (!file.path.includes('wellapp-web')) {
            // 业务项目的图标需加上前缀做区分，避免 ID 重复
            file.basename = `svg-icon-biz-${file.basename}`;
          } else {
            file.basename = `svg-icon-${file.basename}`;
          }
        })
      )
      .pipe(
        svgo({
          // 压缩每个 SVG
          plugins: [
            { name: 'removeMetadata', active: false } // ✅ 禁用删除 metadata
          ]
        })
      )
      .pipe(
        svgstore({
          inlineSvg: true
        })
      ) // 生成 <svg><symbol>...</symbol></svg>
      .pipe(rename('svg-sprite-icon.svg')) // 输出文件名
      // .pipe(gulp.dest('app/public'))// 输出目录
      .pipe(gulp.dest('node_modules')) // 输出目录
      .on('end', () => {
        console.log(`✅ SVG sprite generation completed successfully , Time elapsed: ${(Date.now() - start) / 1000}s `);
      })
      .on('error', err => {
        console.error('❌ SVG Sprite failed:', err);
      })
  );
}
createSvgSprite();
// 如果是监听模式，启动监听
if (isWatchMode) {
  // 使用 gulp.watch 监听文件变化
  gulp.watch(['app/web/assets/svg-icons/*.svg'], createSvgSprite);
}
