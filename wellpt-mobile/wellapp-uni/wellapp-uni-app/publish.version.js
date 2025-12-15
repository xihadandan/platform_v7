'use strict';
const fs = require('fs');
const path = require('path');
const { spawn, exec } = require('child_process');

const args = process.argv.splice(2); // 参数
let _version = undefined; // 更新模块的版本号
let v = require('./package.json').version;
let modVersion = false;
// -v1.0.2-rc
// -p
let publish = false;
if (args == null || args.length == 0) {
  console.error('ERROR: Miss arguments.');
  console.log('       -v : 版本号');
  console.log('       -p : 发布');
  return;
}
for (let a = 0, length = args.length; a < length; a++) {
  if (args[a] === '-p') {
    publish = true;
    continue;
  }

  if (args[a] == '-v') {
    _version = args[++a];
  }
}

if (_version == undefined) {
  _version = v;
} else {
  modVersion = v != _version;
}

let webPackageJson = require('./package.json'), wellappModules = [__dirname];
for (let key in webPackageJson.dependencies) {
  if (key.indexOf('wellapp-') != -1) {
    wellappModules.push(path.resolve(__dirname, '../' + key));
  }
}
let promise = [];
for (let dir of wellappModules) {
  promise.push(publishVersion(dir));
}
function publishVersion(dir) {
  return new Promise((resolve, reject) => {
    let json = getPackageJson(dir);
    if (!json) {
      resolve();
      return;
    }
    console.log(`📦 处理模块目录: ${dir}`);
    json = JSON.parse(json);
    if (modVersion) {
      json.version = _version;
      for (let d in json.dependencies) {
        if (d.indexOf('wellapp-') != -1) {
          json.dependencies[d] = _version;
        }
      }
    }

    outputPackJSON(dir, JSON.stringify(json, null, 2), () => {
      const __publish = function () {
        spawn('cmd.exe', ['/c', `npm unpublish --force && npm publish`], {
          cwd: dir,
          stdio: 'inherit',
          shell: process.platform === 'win32'
        }).on('close', code => {
          if (code == 0) {
            resolve();
          } else {
            reject();
          }
        });
      }
      if (publish) {
          __publish();
      }
    });
  })

}


function outputPackJSON(f, json, callback) {
  let packagePath = path.join(f, '/package.json');
  if (!fs.existsSync(packagePath)) {
    return null;
  }
  if (modVersion) {
    fs.open(packagePath, 'w', (err, fd) => {
      fs.write(fd, json, 0, 'utf-8', err => {
        callback();
      });
    });
  } else {
    callback();
  }

}

function getPackageJson(f) {
  let packagePath = path.join(f, '/package.json');
  if (!fs.existsSync(packagePath)) {
    return null;
  }
  var _packageJson = fs.readFileSync(packagePath);
  return _packageJson.toString();
}

Promise.allSettled(promise).then(values => {
  for (let i = 0; i < wellappModules.length; i++) {
    if (values[i].status == 'rejected') {
      console.error('❌ ' + wellappModules[i] + ' publish failed.');
    } else {
      console.error('✅ ' + wellappModules[i] + '@' + _version + ' publish success');
    }
  }
})
