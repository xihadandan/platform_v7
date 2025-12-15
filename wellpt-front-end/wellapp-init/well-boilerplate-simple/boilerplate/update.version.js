'use strict';
const fs = require('fs');
const path = require('path');
const exec = require('child_process').exec;

const args = process.argv.splice(2); // 参数
let _version = '0'; // 更新模块的版本号
// -v1.0.2-rc
// -p
let publish = false;
if (args == null || args.length == 0) {
  console.error('ERROR: Miss arguments.');
  console.log('       -v : 版本号');
  console.log('       -p : 发布');
  console.log('       -link : 关联本地模块');
  return;
}
for (let a = 0, length = args.length; a < length; a++) {
  if (args[a] === '-p') {
    publish = true;
    continue;
  }
  if (args[a] === '-link') {
    linkPackage();
    return;
  }
  if (args[a] == '-v') {
    _version = args[++a];
  }
}
if (_version == '0') {
  console.error('ERROR: version argument is required. by -v options, for example: node update.version.js -v 1.0.2-rc');
  return;
}

updateVersion();

function updateVersion() {
  // 1. 先更新主框架版本号
  exec(
    'cd .. & cd wellapp-framework & npm version ' + _version, //
    (error, sout, serr) => {
      console.log('wellapp-framework@' + _version + ' update version success.');
      if (publish) {
        exec('cd .. & cd wellapp-framework & npm unpublish --force & npm publish', pe => {
          console.log('wellapp-framework@' + _version + ' publish ' + (pe ? 'failed' : 'success') + '.');
        });
      }
      fs.readdirSync('../').forEach(function (dirPath) {
        if (dirPath.indexOf('wellapp-') === -1 || dirPath == 'wellapp-init' || dirPath == 'wellapp-framework') {
          return;
        }
        let json = getPackageJson(dirPath);

        if (!json) return;

        json = JSON.parse(json);
        json.version = _version;
        //console.error(json.dependencies);
        for (let d in json.dependencies) {
          if (d.indexOf('wellapp-') != -1) {
            json.dependencies[d] = _version;
          }
        }
        outputPackJSON(dirPath, JSON.stringify(json, null, 2), publish);
      });
    }
  );
}

function linkPackage() {
  fs.readdirSync('../').forEach(function (dirPath) {
    if (dirPath.indexOf('wellapp-') === -1 || dirPath == 'wellapp-init' || dirPath == 'wellapp-framework') {
      return;
    }
    let json = getPackageJson(dirPath);
    json = JSON.parse(json);
    //console.error(json.dependencies);
    for (let d in json.dependencies) {
      if (d.indexOf('wellapp-') != -1) {
        json.dependencies[d] = '../' + d;
      }
    }
    outputPackJSON(dirPath, JSON.stringify(json, null, 2), false);
  });
}

function outputPackJSON(f, json, _p) {
  let packagePath = path.join(path.resolve('../'), f, '/package.json');
  if (!fs.existsSync(packagePath)) {
    return null;
  }
  fs.open(packagePath, 'w', (err, fd) => {
    fs.write(fd, json, 0, 'utf-8', err => {
      if (_version != '0') {
        console.log(f + '@' + _version + ' update version success.');
      }
      if (_p) {
        exec('cd .. & cd ' + f + ' & npm unpublish --force & npm publish', x => {
          console.log(f + '@' + _version + ' publish ' + (x ? 'failed' : 'success') + '.');
        });
      }
    });
  });
}

function getPackageJson(f) {
  let packagePath = path.join(path.resolve('../'), f, '/package.json');
  if (!fs.existsSync(packagePath)) {
    return null;
  }
  var _packageJson = fs.readFileSync(packagePath);
  return _packageJson.toString();
}
