const os = require("os");

const appendGlobalCompomentsCode = function (codes) {
  let developmentComponents = process.UNI_DEVELOPMENT_COMPONENTS || [];
  for (let index = 0; index < developmentComponents.length; index++) {
    let developmentComponent = developmentComponents[index];
    let identifier = developmentComponent.identifier;
    let name = developmentComponent.name;
    let source = developmentComponent.source;
    codes.push(`import ${identifier} from '${source}.vue';`);
    codes.push(`Vue.component('${name}',${identifier});`);
  }
};

const generateDevelopmentCompomentsCode = function (source) {
  let splitter = source.indexOf(os.EOL) == -1 ? "\n" : os.EOL; // 有些编辑器换行符可以自行设置
  let sources = source.split(splitter);
  let codes = [];
  for (let index = 0; index < sources.length; index++) {
    let lineCode = sources[index];
    codes.push(lineCode);
    if (lineCode != null && lineCode.indexOf(" Vue ") != -1) {
      appendGlobalCompomentsCode(codes);
    }
  }
  return codes.join(splitter);
};

module.exports = function (source) {
  // 注册二开组件
  // let code = generateDevelopmentCompomentsCode(source);
  return source;
};
