import { utils } from "wellapp-uni-framework";
import { get } from "lodash";
module.exports = {
  inject: ["widgetListContext"],
  props: {
    configuration: Object,
    rowData: Object,
    rowIndex: Number,
    clientRendererOptions: Object,
    styleConfiguration: Object,
    buttons: {
      type: Array,
      default() {
        return [];
      },
    },
  },
  computed: {
    hasBottomData() {
      let hasFlag = false;
      const properties = this.configuration.templateProperties;
      for (let i = 0; i < properties.length; i++) {
        const property = properties[i];
        if (property.name == "bottomRight" || property.name == "bottomLeft") {
          if (!hasFlag && property.mapColumn) {
            hasFlag = true;
          }
          break; // 现在可以合法使用 break
        }
      }
      return hasFlag;
    },
    hasBottom() {
      return this.hasBottomData || this.buttons.length;
    },
    propertyMap() {
      let map = {};
      const properties = this.configuration.templateProperties;
      for (let i = 0; i < properties.length; i++) {
        const property = properties[i];
        if (property.ellipsis) {
          property.ellipsisClass = "w-ellipsis-" + property.ellipsis;
        }
        if (property.mapColumn) {
          map[property.mapColumn] = property;
        }
        if (property.name) {
          map["name_" + property.name] = property;
        }
      }
      return map;
    },
    moreButton() {
      let type = this.configuration.rowBottomMoreButtonType || "link";
      return {
        title: "更多",
        type: type,
      };
    },
    rowBottomButtons() {
      let btns = [];
      let buttons = utils.deepClone(this.buttons);
      for (let i = 0, len = buttons.length; i < len; i++) {
        let btn = buttons[i];
        let visible = btn.defaultVisible;
        // 根据页面变量决定是否展示
        if (btn.defaultVisibleVar && btn.defaultVisibleVar.enable) {
          let _compareData = { ...this.rowData, ...this.widgetListContext.vTableHeadButtonVisibleJudgementData };
          if (btn.defaultVisibleVar.code) {
            let code = btn.defaultVisibleVar.code,
              value = btn.defaultVisibleVar.value,
              valueType = btn.defaultVisibleVar.valueType;
            operator = btn.defaultVisibleVar.operator;
            if (valueType == "variable") {
              try {
                value = get(_compareData, value);
              } catch (error) {
                console.error("无法解析变量值", value);
              }
            }
            visible = utils.expressionCompare(_compareData, code, operator, value) ? visible : !visible;
          } else if (btn.defaultVisibleVar.match != undefined && btn.defaultVisibleVar.conditions != undefined) {
            let multiMatch = (compareData) => {
              // 多组条件判断
              let match = btn.defaultVisibleVar.match == "all";
              for (let i = 0, len = btn.defaultVisibleVar.conditions.length; i < len; i++) {
                let { code, operator, value, valueType } = btn.defaultVisibleVar.conditions[i];
                if (valueType == "variable") {
                  try {
                    value = get(_compareData, value);
                  } catch (error) {
                    console.error("无法解析变量值", value);
                  }
                }
                let result = utils.expressionCompare(compareData, code, operator, value);
                if (btn.defaultVisibleVar.match == "all" && !result) {
                  match = false;
                  break;
                }
                if (btn.defaultVisibleVar.match == "any" && result) {
                  match = true;
                  break;
                }
              }
              return match;
            };

            visible = multiMatch(_compareData) ? visible : !visible;
          }
        }

        if (visible || visible == undefined) {
          btns.push(btn);
        }
      }
      return btns;
    },
  },
  methods: {
    renderCustomRowContent: function (rowData) {
      let _self = this;
      let itemContent = _self.configuration.customTemplateHtml || "";
      Object.keys(rowData).forEach((key) => {
        let propertyValue = rowData[key] || "";
        // html转义字符处理
        // if (typeof propertyValue == "string") {
        //   propertyValue = propertyValue.replace(/</g, "&lt;").replace("/>/g", "&gt;");
        // }
        itemContent = itemContent.replace(new RegExp(`\{${key}\}`, "g"), propertyValue);
      });
      return itemContent;
    },
    $t() {
      if (this.widgetListContext != undefined) {
        return this.widgetListContext.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
    getComponentName(name) {
      return name.replace(/([a-z])([A-Z])/g, "$1-$2").toLowerCase();
    },
    iconSizeFormat(fontSize, initSize, icon) {
      if (icon && utils.isValidJSON(icon)) {
        let iconJson = JSON.parse(icon);
        if (iconJson && !iconJson.showBackground) {
          return fontSize ? fontSize : initSize || 14;
        }
      }
      return fontSize ? fontSize + 8 : (initSize || 14) + 8;
    },
    buttonClick(event, button) {
      this.$emit("button-click", button, this.rowData);
    },
  },
};
