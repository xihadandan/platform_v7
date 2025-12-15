const fs = require("fs");
const fse = require("fs-extra");
const path = require("path");
const lodash = require("lodash");
const syncWellAppPages = function () {
  if (process.syncWellAppPages) {
    return;
  }
  var sourceFolder = path.join(
    path.resolve(process.env.UNI_INPUT_DIR, "node_modules", "wellapp-uni-for-prod", "src", "uni_modules", "w-app")
  );
  var targetFolder = path.join(path.resolve(process.env.UNI_INPUT_DIR, "uni_modules", "w-app"));
  if (fs.existsSync(sourceFolder)) {
    fse.copySync(sourceFolder, targetFolder);
    process.syncWellAppPages = true;
  }
};

const getWellAppPageJson = function () {
  var jsonFilePath = path.join(
    path.resolve(process.env.UNI_INPUT_DIR, "node_modules", "wellapp-uni-for-prod", "src", "pages.json")
  );
  var wellAppPageJson = {};
  if (fs.existsSync(jsonFilePath)) {
    wellAppPageJson = JSON.parse(fs.readFileSync(jsonFilePath, "utf8"));
  }
  return wellAppPageJson;
};

const appendSubPackages = function (pageJson) {
  let packagesDir = path.resolve(process.env.UNI_INPUT_DIR, "packages");
  if (fs.existsSync(packagesDir)) {
    fs.readdirSync(packagesDir).forEach(function (dir) {
      let pagesJsonFile = path.resolve(packagesDir, dir, "pages.json");
      if (fs.existsSync(pagesJsonFile)) {
        let json = JSON.parse(JSON.stringify(require(pagesJsonFile)));
        if (json.subPackages && json.subPackages.length) {
          pageJson.subPackages.push(...json.subPackages);
        }
      }
    });
  }
};

const appendWellAppPageJson = function (pageJson, subPackages) {
  let wellAppPageJson = getWellAppPageJson();
  let appendTo = pageJson.pages;
  if (subPackages) {
    if (pageJson.subPackages == undefined) {
      pageJson.subPackages = [];
    }
    let subPack = {
      root: "uni_modules/w-app/pages",
      pages: [],
    };
    pageJson.subPackages.push(subPack);
    appendTo = subPack.pages;
  }
  let pages = wellAppPageJson.pages || [];
  for (let index = 0; index < pages.length; index++) {
    let page = pages[index];
    if (subPackages) {
      page.path = page.path.split("uni_modules/w-app/pages/")[1];
    }
    appendTo.push(page);
  }
};

let scanDevelopmentDirs = function (dir, startsWithDir) {
  if (!fs.existsSync(path.join(process.env.UNI_INPUT_DIR, "node_modules"))) {
    return [];
  }
  let dirs = [];
  fs.readdirSync(path.resolve(process.env.UNI_INPUT_DIR, "node_modules")).forEach(function (dirPath) {
    if (dirPath.startsWith(startsWithDir)) {
      let devDirPath = path.join(process.env.UNI_INPUT_DIR, "node_modules", dirPath, "src/development/");
      dirs.push(devDirPath);
      devDirPath = path.join(process.env.UNI_INPUT_DIR, "node_modules", dirPath, "/app/web/template");
      if (fs.existsSync(devDirPath)) {
        dirs.push(devDirPath);
      }
    }
  });
  return dirs;
};
let scanVueFiles = function (dir, callback) {
  if (!fs.existsSync(dir)) {
    return;
  }
  fs.readdirSync(dir).forEach(function (filename) {
    let filePath = path.join(dir, filename);
    let fileStat = fs.statSync(filePath);
    if (fileStat.isFile()) {
      if (filename.endsWith(".vue")) {
        callback.call(this, filename, dir, filePath);
      }
    } else {
      scanVueFiles(filePath, callback);
    }
  });
};
const toCamelCase = function (str) {
  // 如果字符串中不含有 '-' 且不含有 '_'，我们认为它已经是驼峰（或不需要转换）
  if (!str.includes("-") && !str.includes("_")) {
    return str;
  }

  // 替换 '-' 或 '_' 后面的字母为大写，并移除分隔符
  return str.replace(/[-_](\w)/g, (_, char) => {
    return char ? char.toUpperCase() : "";
  });
};

const getDevelopmentComponents = function () {
  // 加入当前工程的目录
  let devDirs = [path.resolve(process.env.UNI_INPUT_DIR, "development")];

  fs.readdirSync(path.resolve(process.env.UNI_INPUT_DIR, "packages")).forEach(function (dir) {
    let develop = path.resolve(process.env.UNI_INPUT_DIR, "packages", dir, "development");
    if (fs.existsSync(develop)) {
      devDirs.push(develop);
    }
  });

  let usingComponents = {};
  for (var devDir in devDirs) {
    scanVueFiles(devDirs[devDir], function (filename, dir, filePath) {
      let componentName = path.basename(filename).replace(/\.\w+$/, "");
      let componentPath = path.join(dir.replace(process.env.UNI_INPUT_DIR, "@"), componentName);
      // windows系统中\\替换为/
      if (process.platform.startsWith("win")) {
        componentPath = componentPath.replace(/\\/g, "/");
      }
      usingComponents[componentName] = componentPath;
    });
  }
  return usingComponents;
};

const registerDevelopmentCompoments = function (pageJson) {
  let usingComponents = getDevelopmentComponents();
  let developmentComponents = [];
  for (let name in usingComponents) {
    const identifier = lodash.upperFirst(toCamelCase(name));
    let source = usingComponents[name];
    // /**
    //  * 微信小程序不支持 component is 的动态组件方式，因此会在需要二开模板组件的template内动态插入组件，并采用easycom按需加载
    //  * 从而在编译时候才能进行 componentPlaceholder 占位符处理
    //  */
    // if (process.env.UNI_PLATFORM == "mp-weixin") {
    //   pageJson.easycom.custom["^" + name + "$"] = `${source}.vue`;
    // }
    developmentComponents.push({
      name,
      identifier,
      source,
    });
  }

  if (process.env.UNI_PLATFORM == "mp-weixin") {
    pageJson.easycom.custom["widget"] = `packages/_/components/well-components/page/widget`;
  }
  process.UNI_DEVELOPMENT_COMPONENTS = developmentComponents;
  // 全局组件通过global-component-loader加载，不使用uni内置的方式加载
  // pageJson.globalStyle.usingComponents = Object.assign(pageJson.globalStyle.usingComponents || {}, usingComponents);

  pageJson.globalStyle.usingComponents = Object.assign(pageJson.globalStyle.usingComponents || {}, {
    widget: "packages/_/components/well-components/page/widget",
    RenderDevelopTemplate: "node_modules/wellapp-uni-framework/src/@develop/RenderDevelopTemplate",
  });
};

module.exports = {
  load: function (pageJson, loader) {
    if (pageJson.easycom == undefined) {
      pageJson.easycom = {
        autoscan: true,
        custom: {},
      };
    }

    Object.assign(pageJson.easycom.custom, {
      "^([a-zA-Z0-9-]+)-w-([a-zA-Z0-9-]+)": "@/packages/_/components/modified/$1-w-$2/components/$1-w-$2/$1-w-$2.vue",
      "^w-((?!(form-|work-|dyform))[a-zA-Z0-9-]*)": "@/packages/_/components/well-components/page/w-$1/w-$1.vue",
      "^w-((dy)*form[a-zA-Z0-9-]*)": "@/packages/_/components/well-components/dyform/w-$1/w-$1.vue",
      "^w-(work-[a-zA-Z0-9-]+)": "@/packages/_/components/well-components/workflow/w-$1/w-$1.vue",
      "^popup-([a-zA-Z0-9-]+)": "@/packages/_/components/popup-components/popup-$1/popup-$1.vue",
      "^u-(.*)": "@/packages/_/components/uview-ui/components/u-$1/u-$1.vue",
      "^uni-((?!w-)[a-zA-Z0-9-]+)": "@/packages/_/components/uni-ui/lib/uni-$1/uni-$1.vue",
      "^cell-([a-zA-Z0-9-]+)-render":
        "@/packages/_/components/well-components/page/w-list-view/cell-render/cell-$1-render.vue",

      // "^uni-((?!w-)[a-zA-Z0-9-]+)": "@dcloudio/uni-ui/lib/uni-$1/uni-$1.vue",
      // "^u-(.*)": "uview-ui/components/u-$1/u-$1.vue",
    });

    // 同步平台应用页面
    // syncWellAppPages();

    // 添加平台应用的页面
    // 小程序主包有限制不得超过2M
    appendSubPackages(pageJson);
    // appendWellAppPageJson(pageJson, process.env.UNI_PLATFORM == "mp-weixin");

    // 注册二开组件
    registerDevelopmentCompoments(pageJson);

    fse.writeJSONSync(path.resolve(process.env.UNI_INPUT_DIR, ".pages.runtime.json"), pageJson, {
      spaces: 2,
    });

    // console.log("\x1b[33m" + "输出 pageJson :  " + "\x1b[0m");
    // console.log(pageJson);

    return pageJson;
  },
};
