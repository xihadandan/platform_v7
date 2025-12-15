'use strict';
const message = process.env['HUSKY_GIT_PARAMS'];
const { readFileSync } = require('fs');
const { join } = require('path');

const wellCommitTypes = [
  'task', // 任务
  'bug', // 问题
  'story', // 需求
  'build', // 构建
  'chore', // 日常
  'ci', // 集成
  'docs', // 文档
  'refactor', // 重构，未更改功能
  'release', // 新版本放行
  'style', // 仅更改代码风格
  'test', // 更改单元测试文件
  'typo' // 拼写错误
];

function wellCommitLint() {
  const gitMessageFilePath = join(__dirname, '..', message);
  const gitMessage = readFileSync(gitMessageFilePath, { encoding: 'utf-8' }).split('\n')[0];

  // 不校验合并分支时的提交信息
  if (gitMessage.match(/^Merge branch.*/)) {
    return [2, 'always', [gitMessage]];
  }

  const PATTERN = /^(\w+)(#\w+|#\u65e0)?: (.+)$/gu; // 中文字符“无”的unicode编码为`\u65e0`
  const match = PATTERN.exec(gitMessage);

  if (!match) {
    console.error('提交信息不符合公司规范');
    process.exit(1);
  }

  const [commitMsg, commitType, relatedNo, msgBody] = match;

  // 提交类型需要与预定义一致
  if (!wellCommitTypes.includes(commitType)) {
    console.error('公司目前使用如下提交类型：', wellCommitTypes);
    process.exit(1);
  }

  // 'bug','task','story' 三种类型的提交编号需要为数字或者中文字符“无”
  if (['bug', 'task', 'story'].includes(commitType) && !/^(#\d+|#\u65e0)$/u.test(relatedNo)) {
    console.error(`'bug','task','story' 三种类型的提交编号需要为数字或者中文字符“无”`);
    process.exit(1);
  }

  return [2, 'always', [...wellCommitTypes, relatedNo ? commitType + relatedNo : commitType]];
}

module.exports = {
  rules: {
    'type-enum': wellCommitLint,
    'type-case': [2, 'always', 'lower-case']
  }
};
