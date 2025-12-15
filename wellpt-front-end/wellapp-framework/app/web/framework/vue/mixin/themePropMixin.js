
import { kebabCase } from 'lodash';
export default {
  props: {
  },
  inject: ['packJson'],

  computed: {
    basicStyleCssVars() {
      let colorConfig = this.packJson.colorConfig;
      let styleVars = {};
      // 色彩相关
      styleVars[colorConfig.themeColor.classify[0].code] = colorConfig.themeColor.classify[0].value;
      // 主题色
      Object.assign(styleVars, this.deriveVars(colorConfig.themeColor.classify[0]));

      // 中性色、功能色
      for (let key of ['neutralColor', 'functionColor']) {
        let classify = colorConfig[key].classify;
        for (let i = 0, len = classify.length; i < len; i++) {
          Object.assign(styleVars, this.deriveVars(classify[i]))
        }
      }

      // 字体
      let fontConfig = this.packJson.fontConfig, { fontSize, fontWeight } = fontConfig;
      fontSize = fontSize.classify[0];
      styleVars[fontSize.code] = fontSize.value;
      Object.assign(styleVars, this.deriveVars(fontSize));
      Object.assign(styleVars, this.deriveVars(fontWeight));

      // 间距
      let spaceConfig = this.packJson.spaceConfig, { paddingConfig, marginConfig } = spaceConfig;
      Object.assign(styleVars, this.deriveVars(paddingConfig));
      Object.assign(styleVars, this.deriveVars(marginConfig));

      // 阴影
      Object.assign(styleVars, this.deriveVars(this.packJson.shadowConfig.levelOne));
      Object.assign(styleVars, this.deriveVars(this.packJson.shadowConfig.levelTwo));
      Object.assign(styleVars, this.deriveVars(this.packJson.shadowConfig.levelThree));

      // 边框
      Object.assign(styleVars, this.deriveVars(this.packJson.borderConfig.borderStyle));
      Object.assign(styleVars, this.deriveVars(this.packJson.borderConfig.borderWidth));

      // 高度
      Object.assign(styleVars, this.deriveVars(this.packJson.heightConfig));

      // 圆角
      Object.assign(styleVars, this.deriveVars(this.packJson.radiusConfig));



      return styleVars;
    },


  },
  methods: {

    deriveVars(tar) {
      let v = {};
      if (tar.derive) {
        for (let i = 0, len = tar.derive.length; i < len; i++) {
          if (tar.derive[i].code && tar.derive[i].value != undefined) {
            v[tar.derive[i].code] = tar.derive[i].value;
          }
        }
      }
      return v;
    },

    getStyleDeriveOptions(tar) {
      let options = [];
      for (let i = 0, len = tar.derive.length; i < len; i++) {
        options.push({
          label: tar.derive[i].code,
          value: tar.derive[i].code,
          remark: tar.derive[i].remark
        })
      }
      return options
    },

    getComponentConfig(quoteKey, keys) {
      let quoteStyle = this.packJson.componentConfig[quoteKey];
      let colorVars = {};
      for (let prop of keys) {
        if (quoteStyle[prop]) {
          for (let key in quoteStyle[prop]) {
            if (quoteStyle[prop][key] != undefined) {
              if (key.toLowerCase().indexOf('boxshadow') != -1) {
                // 阴影样式处理
                colorVars['--' + kebabCase(key)] = quoteStyle[prop][key].startsWith('inset ')
                  ? 'inset var(' + quoteStyle[prop][key].split('inset ')[1] + ')'
                  : `var(${quoteStyle[prop][key]})`;
              } else {
                colorVars['--' + kebabCase(key)] = quoteStyle[prop][key].startsWith('--w-')
                  ? `var(${quoteStyle[prop][key]})`
                  : quoteStyle[prop][key];
              }
            }
          }
        }
      }
      const vars = { ...colorVars, ...this.basicStyleCssVars };
      const themeStyle = {};
      themeStyle[quoteKey] = vars;
      this.$emit('setThemeStyle', themeStyle);
      return vars;
    }
  }

}
