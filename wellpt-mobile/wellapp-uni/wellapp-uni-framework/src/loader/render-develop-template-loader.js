const os = require("os");
const fs = require("fs");
const generateMpWidgetDevelopmentCode = function (source) {
  let splitter = source.indexOf(os.EOL) == -1 ? "\n" : os.EOL; // 有些编辑器换行符可以自行设置
  let sources = source.split(splitter);
  let codes = [];

  for (let index = 0; index < sources.length; index++) {
    let lineCode = sources[index];
    codes.push(lineCode);
    if (lineCode != null) {
      if (lineCode.indexOf("<!-- #ifdef MP -->") != -1) {
        appendWidgetDevelopmentCode(codes);
      }
    }
  }
  return codes.join(splitter);
};

const appendWidgetDevelopmentCode = function (codes) {
  let developmentComponents = process.UNI_DEVELOPMENT_COMPONENTS || [];
  for (let index = 0; index < developmentComponents.length; index++) {
    let component = developmentComponents[index];

    let componentCode = `<${component.name} v-if="isComponent == '${component.name}'" :widget="widget"
      :parent="parent" :options="options" ref="component" />`;
    codes.push(componentCode);
  }
};

module.exports = function (source) {
  let code = generateMpWidgetDevelopmentCode(source);
  // fs.writeFile("./wxcomponents/wxcomp.vue", code, "utf8", (err) => {
  //   if (err) {
  //     console.error("写入文件出错:", err);
  //     return;
  //   }
  //   console.log("文件写入成功");
  // });
  return code;
};
