const os = require("os");

module.exports = function (source) {
  if(process.env.NODE_ENV === 'development' && process.env.UNI_PLATFORM =='h5'){
    return source;
  }
  let importComps = [],
    components = [];
  if (process.env.UNI_PLATFORM !== "mp-weixin") {
    // 非微信小程序端，采用 component is 动态组件引入，引用的组件需要混入developComps.js
    for (let component of process.UNI_DEVELOPMENT_COMPONENTS) {
      importComps.push(`import ${component.identifier} from "${component.source}.vue"`);
      components.push(component.identifier);
    }
  }
  return `
  ${importComps.join(";") + os.EOL}
  export default {
    components:{
      ${components.join(",")}
    }
  };
  `;
};

function requireDevelopComps(){
  return `
    import {capitalize,camelCase} from "lodash";
    let rootDevelopments =  require.context("@/development", true, /\w+\.vue$/);
    let pkgDevelopments =  require.context("@/packages/_/development", true, /\w+\.vue$/);
    let comps = {};
    rootDevelopments.keys().map(fileName => {
       fileName = capitalize(camelCase(fileName));
      let comp = rootDevelopments(fileName).default;
      comps[fileName] = comp;
    });
    pkgDevelopments.keys().map(fileName => {
      fileName = capitalize(camelCase(fileName));
      let comp = pkgDevelopments(fileName).default;
      comps[fileName] = comp;
    });
    debugger
     export default {
      components : {
        ...comps,
      }
    }
  `
}
