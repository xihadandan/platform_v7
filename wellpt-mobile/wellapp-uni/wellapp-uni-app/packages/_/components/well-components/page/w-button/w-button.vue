<!-- 平台通用按钮组件(卡片,从表等) -->
<template>
  <view class="widget-common-button flex">
    <template v-if="isSwipeButton">
      <swipeButtonGroup
        style="height: 100%"
        :buttons="buttons"
        @swipButtonClick="(button, data, e) => onTrigger(e, 'click', button, true)"
      >
      </swipeButtonGroup>
    </template>
    <template v-else v-for="(button, i) in buttons">
      <template v-if="button.children">
        <uni-w-button-group
          :buttons="[button]"
          :gutter="0"
          @click="(e) => onTrigger(e, 'click', e.button, true)"
          :arrow="button.style && button.style.rightDownIconVisible"
          :size="size"
          :max="0"
        ></uni-w-button-group>
      </template>
      <template v-else>
        <uni-w-switch
          v-if="button.style.type == 'switch'"
          :size="button.style.size || size"
          v-model="button.switch.checked"
          @change="(e) => onTrigger(e, 'click', button, true)"
        />
        <uni-w-button
          v-else
          :type="button.style.type || buttonDefaultType"
          :block="button.style.block"
          :size="button.style.size || size"
          :ghost="button.style.ghost"
          :shape="button.style.shape"
          :icon="button.style.icon"
          :text="
            button.titleFormatter ? button.titleFormatter($t(button.id, button.title)) : $t(button.id, button.title)
          "
          :textHidden="button.style.textHidden"
          @click="(e) => onTrigger(e, 'click', button, true)"
        >
        </uni-w-button>
      </template>
    </template>
  </view>
</template>

<script type="text/babel">
import { debounce, template as stringTemplate, get } from "lodash";
import { utils, appContext } from "wellapp-uni-framework";
import swipeButtonGroup from "../w-list-view/swipe-button-group.vue";

export default {
  name: "w-button",
  inject: ["pageContext", "vPageState", "$pageJsInstance", "unauthorizedResource", "namespace", "widgetContext"], //"parentLayContentId",
  props: {
    button: Object,
    ghost: Boolean,
    mask: Boolean,
    buttonDefaultType: {
      type: String,
      default: "",
    },
    size: {
      type: String,
      default: "default",
    },
    position: "", // 按钮位置tableHeader、rowEnd、rowCol
    meta: {
      // 按钮元信息：可以把业务数据等其他信息附加上去，事件派发会统一处理meta数据到状态管理数据内
      type: Object | Function,
      default: function () {
        return {};
      },
    },
    buttonPredicate: Function,
    developJsInstance: Object | Function,
    parentWidget: Object | Function,
    eventWidget: Object | Function, // 事件主体组件
    visibleJudgementData: {
      type: Object,
      default: function () {
        return {};
      },
    },
    isSwipeButton: Boolean,
    i18nPrefix: String, // 国际化前缀
  },
  data() {
    let _button = JSON.parse(JSON.stringify(this.button));
    this.$emit("buttonDataBeforeInit", { button: _button });

    let parentVm = this.parentWidget != undefined ? this.parentWidget : undefined;
    if (typeof this.parentWidget === "function") {
      parentVm = this.parentWidget();
    }
    return {
      buttonConf: _button,
      switchChecked: true,
      loading: false,
      parentVm,
    };
  },

  beforeCreate() {},
  components: { swipeButtonGroup },
  computed: {
    buttonMap() {
      let map = {};
      for (let i = 0, len = this.buttonConf.buttons.length; i < len; i++) {
        map[this.buttonConf.buttons[i].id] = this.buttonConf.buttons[i];
      }
      return map;
    },

    buttons() {
      let buttonGroup = this.buttonConf.buttonGroup,
        visibleButtons = [],
        visibleBtnIds = [];
      if (this.buttonConf.buttons.length == 0) {
        return [];
      }
      for (let i = 0, len = this.buttonConf.buttons.length; i < len; i++) {
        let btn = this.buttonConf.buttons[i];
        if (typeof this.buttonPredicate == "function") {
          let isTrue = this.buttonPredicate(btn);
          if (!isTrue) {
            continue;
          }
        }
        if (btn.role && btn.role.length > 0) {
          // 判断是否有权限
          if (!(Array.isArray(btn.role) ? this._hasAnyRole(btn.role) : this._hasRole(btn.role))) {
            continue;
          }
        }

        if (this.unauthorizedResource && this.unauthorizedResource.includes(btn.id)) {
          continue;
        }

        let visible = btn.defaultVisible;
        btn.title = this.$t(btn.id, btn.title, btn);
        // 根据页面变量决定是否展示
        let meta = typeof this.meta == "function" ? this.meta() : this.meta;
        if (btn.defaultVisibleVar && btn.defaultVisibleVar.enable) {
          let _compareData = { ...this.vPageState, ...meta, ...this.visibleJudgementData };
          if (btn.defaultVisibleVar.code) {
            let code = btn.defaultVisibleVar.code,
              value = btn.defaultVisibleVar.value,
              valueType = btn.defaultVisibleVar.valueType,
              operator = btn.defaultVisibleVar.operator;
            if (this.position == "tableHeader") {
              if (meta.selectedRows && meta.selectedRows.length > 0) {
                // 有选中行的情况下，根据选中行数据的字段匹配判断条件
                for (let rowIndex = 0; rowIndex < meta.selectedRows.length; rowIndex++) {
                  let row = meta.selectedRows[rowIndex];
                  let _compareData = { ...this.vPageState, ...row, ...this.visibleJudgementData };
                  if (valueType == "variable") {
                    try {
                      value = get(_compareData, value);
                    } catch (error) {
                      console.error("无法解析变量值", value);
                    }
                  }
                  visible = utils.expressionCompare(_compareData, code, operator, value)
                    ? visible
                    : !btn.defaultVisible;
                  if (!visible) {
                    break;
                  }
                }
              } else {
                if (valueType == "variable") {
                  try {
                    value = get(_compareData, value);
                  } catch (error) {
                    console.error("无法解析变量值", value);
                  }
                }
                visible = utils.expressionCompare(_compareData, code, operator, value) ? visible : !visible;
              }
            } else {
              if (valueType == "variable") {
                try {
                  value = get(_compareData, value);
                } catch (error) {
                  console.error("无法解析变量值", value);
                }
              }
              visible = utils.expressionCompare(_compareData, code, operator, value) ? visible : !visible;
            }
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

            if (this.position == "tableHeader") {
              if (meta.selectedRows && meta.selectedRows.length > 0) {
                // 有选中行的情况下，根据选中行数据的字段匹配判断条件
                for (let rowIndex = 0; rowIndex < meta.selectedRows.length; rowIndex++) {
                  let row = meta.selectedRows[rowIndex];
                  visible = multiMatch({ ...this.vPageState, ...row, ...this.visibleJudgementData })
                    ? visible
                    : !btn.defaultVisible;
                  if (!visible) {
                    break;
                  }
                }
              } else {
                visible = multiMatch(_compareData) ? visible : !visible;
              }
            } else {
              visible = multiMatch(_compareData) ? visible : !visible;
            }
          }
        }

        if (visible || visible == undefined) {
          visibleButtons.push(btn);
          visibleBtnIds.push(btn.id);
        }

        if (btn.style && btn.style.type == "switch") {
          // 开关按钮的选中根据条件判断
          let { operator, code, value, valueType } = btn.switch.checkedCondition;
          let _compareData = { ...this.vPageState, ...meta, ...this.visibleJudgementData };
          if (valueType == "variable") {
            try {
              value = get(_compareData, value);
            } catch (error) {
              console.error("无法解析变量值", value);
            }
          }
          if (code) {
            this.$set(btn.switch, "checked", utils.expressionCompare(_compareData, code, operator, value));
          }
        }
      }
      if (buttonGroup.type === "notGroup") {
        // 不分组
        return visibleButtons;
      } else if (buttonGroup.type === "dynamicGroup") {
        // 动态分组
        let threshold = buttonGroup.dynamicGroupBtnThreshold;
        if (threshold !== undefined && threshold > 0 && visibleButtons.length > threshold) {
          let noGroupButtons = visibleButtons.splice(0, threshold);
          return noGroupButtons.concat([
            {
              title: this.parentVm
                ? this.$t("dynamicGroupName", buttonGroup.dynamicGroupName)
                : buttonGroup.dynamicGroupName,
              icon: buttonGroup.icon,
              children: visibleButtons,
              size: visibleButtons[0] ? visibleButtons[0].size : "",
              style: buttonGroup.style,
              ...buttonGroup.style,
            },
          ]);
        }
        return visibleButtons;
      } else {
        // 固定分组
        let btnAddedGroup = [];
        for (let i = 0, len = buttonGroup.groups.length; i < len; i++) {
          let btnTitle = buttonGroup.groups[i].name || buttonGroup.groups[i].title;
          let groupButton = {
            title: this.parentVm ? this.$t(buttonGroup.groups[i].id, btnTitle) : btnTitle,
            children: [],
            style: buttonGroup.style,
            ...buttonGroup.groups[i].style,
            type: buttonGroup.groups[i].type || "",
          };
          for (let j = 0, jlen = buttonGroup.groups[i].buttonIds.length; j < jlen; j++) {
            let btn = this.buttonMap[buttonGroup.groups[i].buttonIds[j]];
            if (btn && visibleBtnIds.includes(btn.id)) {
              groupButton.children.push(btn);
              btnAddedGroup.push(btn.id);
            }
          }
          if (groupButton.children.length) {
            // 分组按钮放在第一个加入分组的按钮位置
            for (let i = 0; i < visibleButtons.length; i++) {
              if (btnAddedGroup.includes(visibleButtons[i].id)) {
                if (groupButton) {
                  visibleButtons.splice(i--, 1, groupButton);
                  groupButton = null;
                } else {
                  visibleButtons.splice(i--, 1);
                }
              }
            }
          }
        }
        return visibleButtons;
      }
    },
  },
  created() {},
  methods: {
    /**
     * 执行事件设置的配置
     * @param {事件设置} eventHandler
     */
    dispatchEventHandler(button, eventHandler, evt) {
      let _this = this;
      let _parent = this.parentWidget != undefined ? this.parentWidget : undefined;
      if (typeof this.parentWidget === "function") {
        _parent = this.parentWidget();
      }
      eventHandler.pageContext = this.pageContext;
      eventHandler.$evtWidget = this;
      eventHandler.key = button.id;
      if (this.eventWidget != undefined) {
        if (typeof this.eventWidget === "function") {
          eventHandler.$evtWidget = this.eventWidget(_parent);
        } else {
          eventHandler.$evtWidget = this.eventWidget;
        }
      }
      let meta = this.meta;
      if (typeof this.meta == "function") {
        meta = this.meta();
      }
      // 元数据通过事件传递
      eventHandler.meta = meta;
      eventHandler.$evt = evt;
      let developJs = typeof this.developJsInstance === "function" ? this.developJsInstance() : this.developJsInstance;
      if (developJs == undefined) {
        developJs = {};
      }
      if (this.$pageJsInstance != undefined) {
        developJs[this.$pageJsInstance._JS_META_] = this.$pageJsInstance;
      }
      if (meta != undefined && meta.$developJsInstance) {
        for (let key in meta.$developJsInstance) {
          developJs[key] = meta.$developJsInstance[key];
        }
      }
      eventHandler.$developJsInstance = developJs;

      // 动态解析当前父级布局ID
      // if (eventHandler.targetPosition == "widgetLayout" && eventHandler.containerWid == undefined) {
      //   let containerWid = undefined;
      //   if (this.parentLayContentId != undefined) {
      //     if (typeof this.parentLayContentId == "function") {
      //       containerWid = this.parentLayContentId();
      //     } else {
      //       containerWid = this.parentLayContentId;
      //     }
      //   }
      //   if (containerWid) {
      //     eventHandler.containerWid = containerWid;
      //   } else {
      //     // 如果找不到当前父级布局，则自动切换为新窗口打开
      //     eventHandler.targetPosition = "newWindow";
      //   }
      // }

      if (button.style.type === "switch") {
        evt.checked = !button.switch.checked;
        // 开关按钮需要有loading效果
        eventHandler.before = () => {
          _this.loading = true;
        };
        eventHandler.after = (success) => {
          _this.loading = false;
          if (typeof success === "boolean" && success) {
            // 开关执行成功回调通知变更状态
            button.switch.checked = !button.switch.checked;
          }
        };
      }

      if (eventHandler.actionType) {
        appContext.dispatchEvent({
          ui: _this,
          ...eventHandler,
        });
      }
      // 阻止事件冒泡被行点击捕获
      if (evt.stopPropagation) {
        evt.stopPropagation();
      } else if (evt.domEvent && evt.domEvent.stopPropagation) {
        evt.domEvent.stopPropagation();
      }
    },
    resolvePopconfirmTitle(_title, translateCode, confirmConfig) {
      try {
        let meta = this.meta;
        if (typeof this.meta == "function") {
          meta = this.meta();
        }
        let title = _title,
          key = `Widget.${translateCode}`; //`Widget.${this.widgetContext.widget.id}.${translateCode}`;
        // 通过 t 函数获取 message （避免message 里面的变量占位符被i18n默认解析掉）
        if (this.widgetContext) {
          title = this.$i18n.t(key);
        } else {
          title = this.$i18n.t(`${translateCode}`);
        }
        let compiler = stringTemplate(title == key || title == translateCode ? _title : title);
        return compiler(meta || {});
      } catch (error) {}
      return title;
    },

    onTrigger(evt, trigger, button, hiddenDropdown = false) {
      let _this = this;
      let dispatch = (button, eventHandler, evt) => {
        if (button.confirmConfig && button.confirmConfig.enable) {
          // && button.confirmConfig.popType == "confirm"
          let _title = this.resolvePopconfirmTitle(
              button.confirmConfig.title,
              button.id + "_popconfirmTitle",
              button.confirmConfig
            ),
            _content =
              button.confirmConfig.popType == "confirm"
                ? this.resolvePopconfirmTitle(
                    button.confirmConfig.content,
                    button.id + "_popconfirmContent",
                    button.confirmConfig
                  )
                : undefined;

          uni.showModal({
            title: _title,
            confirmText: button.confirmConfig.okText
              ? _this.$t("Widget." + button.id + "_popconfirmOkText", button.confirmConfig.okText)
              : this.$t("global.confirm", "确认"),
            cancelText: button.confirmConfig.cancelText
              ? _this.$t("Widget." + button.id + "_popconfirmCancelText", button.confirmConfig.cancelText)
              : this.$t("global.cancel", "取消"),
            content: _content,
            success: function (res) {
              if (res.confirm) {
                _this.dispatchEventHandler(button, eventHandler, evt);
                _this.$emit("button-" + trigger, evt, button);
              } else if (res.cancel) {
                _this.dispatchEventHandler(button, eventHandler, evt);
              }
            },
          });
        } else {
          _this.dispatchEventHandler(button, eventHandler, evt);
        }
      };

      // 双击发了3个事件。两次点击事件，最后一次是dblclick
      if (button.hasOwnProperty("eventHandler")) {
        if (Array.isArray(button.eventHandler)) {
          for (let i = 0, len = button.eventHandler.length; i < len; i++) {
            if (button.eventHandler[i].trigger === trigger) {
              dispatch(button, button.eventHandler[i], evt);
            }
          }
        } else {
          if (button.eventHandler.trigger === trigger) {
            dispatch(button, button.eventHandler, evt);
          }
        }
      }

      if (!(button.confirmConfig && button.confirmConfig.enable)) {
        // 不存在确认框，向上触发按钮事件触发类型
        this.$emit("button-" + trigger, evt, button);
      }

      if (evt.stopPropagation) {
        evt.stopPropagation();
      } else if (evt.domEvent != undefined) {
        evt.domEvent.stopPropagation();
      }
    },
    $t() {
      if (arguments[2] && this.i18nPrefix) {
        let btn = arguments[2];
        if (btn.default) {
          // 默认按钮使用默认配置
          return this.$t(this.i18nPrefix + btn.id, btn.title);
        }
      }
      if (this.parentVm != undefined) {
        return this.parentVm.$t(...arguments);
      }
      return this.$i18n.t(...arguments);
    },
  },

  mounted() {
    this.$emit("buttonMounted", this.$el);
  },
  updated() {
    this.$emit("buttonUpdated", this.$el);
  },
};
</script>
