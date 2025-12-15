const comps = {};

import { upperFirst } from "lodash";
let rootDevelopments = require.context("@/development", true, /\w+\.vue$/);
let pkgDevelopments = require.context("@/packages/_/development", true, /\w+\.vue$/);

rootDevelopments.keys().map((fileName) => {
  let name = getCompName(fileName);
  let comp = rootDevelopments(fileName).default;
  comps[name] = comp;
});
pkgDevelopments.keys().map((fileName) => {
  let name = getCompName(fileName);
  let comp = pkgDevelopments(fileName).default;
  comps[name] = comp;
});

console.log('二开模板组件:',comps);


function toCamelCase(str) {
  // 如果字符串中不含有 '-' 且不含有 '_'，我们认为它已经是驼峰（或不需要转换）
  if (!str.includes("-") && !str.includes("_")) {
    return str;
  }

  // 替换 '-' 或 '_' 后面的字母为大写，并移除分隔符
  return str.replace(/[-_](\w)/g, (_, char) => {
    return char ? char.toUpperCase() : "";
  });
}



function getCompName(fileName) {
  let names = fileName.split("/");
  let name = names[names.length - 1];
  return upperFirst(toCamelCase(name.split(".vue")[0]));
}

export default {
  components: {
    ...comps,
  },
};
