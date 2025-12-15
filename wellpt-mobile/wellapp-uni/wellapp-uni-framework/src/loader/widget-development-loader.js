const os = require("os");
const generateMpWidgetDevelopmentCode = function (source) {
  let splitter = source.indexOf(os.EOL) == -1 ? "\n" : os.EOL; // 有些编辑器换行符可以自行设置
  let sources = source.split(splitter);
  let codes = [];
  for (let index = 0; index < sources.length; index++) {
    let lineCode = sources[index];
    codes.push(lineCode);
    if (lineCode != null && lineCode.indexOf(" #ifdef MP") != -1) {
      appendWidgetDevelopmentCode(codes);
    }
  }
  return codes.join(splitter);
};

const appendWidgetDevelopmentCode = function (codes) {
  let developmentComponents = process.UNI_DEVELOPMENT_COMPONENTS || [];
  for (let index = 0; index < developmentComponents.length; index++) {
    let component = developmentComponents[index];
    let componentCode = `<${component.name} v-if="componentType == '${component.name}' ||
      componentType == '${component.identifier}'" :widget="componentDefinition"
      :parent="parent">
      </${component.name}>`;
    codes.push(componentCode);
  }
};

module.exports = function (source) {
  if (process.env.UNI_PLATFORM == "h5" || process.env.UNI_PLATFORM == "app-plus") {
    return source;
  }
  // 部分小程序不支持动态组件，动态插入二开组件代码
  let code = generateMpWidgetDevelopmentCode(source);
  return code;
};
