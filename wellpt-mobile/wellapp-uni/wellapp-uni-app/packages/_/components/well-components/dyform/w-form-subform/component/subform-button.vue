<template>
  <view :class="['flex-row-buttons', position]" @click.stop>
    <slot name="addonBefore"></slot>
    <!-- <view :class="['button']" @click="(e) => onClickButton(e, bt)" v-for="(bt, i) in buttons" :key="'button_' + i">
      <w-icon v-if="bt.style && bt.style.icon" :icon="bt.style.icon" :size="18" color="var(--color-primary)" />
      <text>{{ bt.title }}</text>
    </view> -->
    <w-button
      :button="button"
      :size="size"
      :buttonDefaultType="buttonDefaultType"
      class="f_wrap"
      :visibleJudgementData="visibleJudgementData"
      @button-click="buttonClick"
      :buttonPredicate="buttonPredicate"
      :isSwipeButton="isSwipeButton"
      :style="{ height: isSwipeButton ? '100%' : '' }"
      :parentWidget="parentWidget"
      :i18nPrefix="i18nPrefix"
    ></w-button>
  </view>
</template>
<script>
import { utils, appContext } from "wellapp-uni-framework";
import { get } from "lodash";

export default {
  props: {
    button: Object,
    buttonDefaultType: {
      type: String,
      default: "link",
    },
    size: {
      type: String,
      default: "small",
    },
    position: String, // 按钮位置tableHeader、rowEnd、rowCol
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
    i18nPrefix: {
      type: String,
      default: "WidgetSubform.button.",
    },
  },
  components: {},
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
        Object.assign(btn, btn.style);
        if (typeof this.buttonPredicate == "function") {
          let isTrue = this.buttonPredicate(btn);
          if (!isTrue) {
            continue;
          }
        }

        // if (this.unauthorizedResource && this.unauthorizedResource.includes(btn.id)) {
        //   continue;
        // }

        let visible = btn.defaultVisible;
        // 根据页面变量决定是否展示
        if (btn.defaultVisibleVar && btn.defaultVisibleVar.enable) {
          let _compareData = { ...this.meta, ...this.visibleJudgementData };
          if (btn.defaultVisibleVar.code) {
            let code = btn.defaultVisibleVar.code,
              value = btn.defaultVisibleVar.value,
              valueType = btn.defaultVisibleVar.valueType,
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
          visibleButtons.push(btn);
          visibleBtnIds.push(btn.id);
        }
      }

      return visibleButtons;
    },
  },
  data() {
    let _button = JSON.parse(JSON.stringify(this.button));
    return {
      buttonConf: _button,
      switchChecked: true,
      loading: false,
    };
  },
  beforeCreate() {},
  created() {
    // 默认动态分组
    this.button.buttonGroup.type = "dynamicGroup";
    this.button.buttonGroup.dynamicGroupName = "";
    this.button.buttonGroup.icon = "iconfont icon-ptkj-gengduocaozuo";
    if (!this.button.buttonGroup.style) {
      this.button.buttonGroup.style = {};
    }
    this.button.buttonGroup.style.type = "link";
    this.button.buttonGroup.dynamicGroupBtnThreshold = 3;
    if (this.position == "tableRowEnd") {
      this.button.buttonGroup.dynamicGroupBtnThreshold = 1;
    }
  },
  beforeMount() {},
  mounted() {},
  methods: {
    onClickButton(e, button) {
      let _this = this;
      let dispatch = (button, eventHandler) => {
        appContext.dispatchEvent({
          ui: _this,
          ...eventHandler,
        });
      };

      if (button.hasOwnProperty("eventHandler")) {
        if (Array.isArray(button.eventHandler)) {
          for (let i = 0, len = button.eventHandler.length; i < len; i++) {
            if (button.eventHandler[i].trigger === "click" && button.eventHandler[i].actionType) {
              dispatch(button, button.eventHandler[i]);
            }
          }
        } else {
          if (button.eventHandler.trigger === "click" && button.eventHandler.actionType) {
            dispatch(button, button.eventHandler);
          }
        }
      }

      this.$emit("button-click", e, button);
    },
    buttonClick(e, button) {
      this.$emit("button-click", e, button);
    },
    $t() {
      if (arguments[2]) {
      }
    },
  },
};
</script>
