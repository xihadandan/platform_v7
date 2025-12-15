<template>
  <view class="swipe-button-group">
    <template v-for="(button, btnIndex) in visibleButtons">
      <view
        class="swipe-button"
        :class="button.cssClass"
        :style="{
          backgroundColor: button.hasOwnProperty('bgColor') ? button.bgColor || 'initial' : '',
          color: button.hasOwnProperty('textColor') ? button.textColor || 'initial' : '',
        }"
        @click.stop="(e) => swipButtonClick(e, button)"
        :key="btnIndex"
      >
        <template v-if="button.icon && button.icon.className">
          <image
            v-if="button.icon.src"
            style="width: 24px; height: 24px"
            class="image"
            mode="aspectFit"
            :src="button.icon.src"
          />
          <w-icon
            v-else
            :icon="button.icon && button.icon.className"
            iconClass="swipe-buttion-icon"
            color="inherit"
            :size="24"
          ></w-icon>
          <view v-if="!button.hiddenText" class="swipe-button-text">{{ button.text }}</view>
        </template>
        <template v-else-if="button.icon && typeof button.icon == 'string'">
          <w-icon :iconConfig="button.icon" iconClass="swipe-buttion-icon" color="inherit" :size="24"></w-icon>
          <view v-if="!button.hiddenText" class="swipe-button-text">{{ button.title }}</view>
        </template>
        <template v-else-if="button.style && button.style.icon">
          <w-icon :icon="button.style.icon" iconClass="swipe-buttion-icon" color="inherit" :size="24"></w-icon>
          <view v-if="!button.hiddenText" class="swipe-button-text">{{ button.title }}</view>
        </template>
        <view v-else class="swipe-button-text">{{ button.text || button.title }}</view>
      </view>
    </template>
  </view>
</template>

<script>
import { utils, appContext } from "wellapp-uni-framework";
import { get } from "lodash";
export default {
  props: {
    buttons: Array,
    rowData: Object,
    widget: Object,
    buttonPredicate: Function,
    visibleJudgementData: {
      type: Object,
      default: function () {
        return {};
      },
    },
  },
  data() {
    return {
      sheetActions: [],
    };
  },
  computed: {
    visibleButtons() {
      let visibleButtons = [],
        visibleBtnIds = [];
      let moreBtn = {
        id: "more",
        text: this.$t("global.more", "更多"),
        children: [],
      };
      if (this.buttons.length == 0) {
        return [];
      }
      for (let i = 0, len = this.buttons.length; i < len; i++) {
        let btn = this.buttons[i];
        Object.assign(btn, btn.style);

        if (!btn.hasOwnProperty("cssClass") && btn.type) {
          btn.cssClass = "btn-" + btn.type;
        }

        if (typeof this.buttonPredicate == "function") {
          let isTrue = this.buttonPredicate(btn);
          if (!isTrue) {
            continue;
          }
        }

        if (this.unauthorizedResource && this.unauthorizedResource.includes(btn.id)) {
          continue;
        }

        let visible = btn.defaultVisible;
        // 根据页面变量决定是否展示
        if (btn.defaultVisibleVar && btn.defaultVisibleVar.enable) {
          let _compareData = { ...this.rowData, ...this.visibleJudgementData };
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
          visibleButtons.push(btn);
          visibleBtnIds.push(btn.id);
        }
      }
      if (this.widget && this.widget.wtype == "WidgetTable") {
        if (visibleButtons.length > 2) {
          let noGroupButtons = visibleButtons.splice(0, 1);
          moreBtn.children = visibleButtons;
          noGroupButtons.push(moreBtn);
          visibleButtons = noGroupButtons;
        }
      }

      return visibleButtons;
    },
  },
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    swipButtonClick: function (e, button) {
      let _self = this;
      if (button.children && button.children.length) {
        this.sheetActions = button.children;
        uni.$emit("ptActionSheetShow", {
          actions: this.sheetActions,
          click: this.sheetActionClick,
          cancelText: this.$t("global.cancel", "取消"),
        });
        // uni.showActionSheet({
        //   itemList: _self.sheetActions.map((item) => item.title),
        //   success: function (res) {
        //     console.log(res);
        //     button = _self.sheetActions[res.tapIndex];
        //     _self.$emit("swipButtonClick", button, this.rowData, e);
        //   },
        // });
      } else {
        this.$emit("swipButtonClick", button, this.rowData, e);
      }
    },
    sheetActionClick(e) {
      this.$emit("swipButtonClick", e.button, this.rowData, e);
    },
  },
};
</script>

<style lang="scss" scoped>
.swipe-button-group {
  /* #ifndef APP-NVUE */
  display: flex;
  height: 100%;
  /* #endif */
  flex: 1;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  // padding: 0 20px;
  // background-color: #ff5a5f;

  .swipe-button {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 0 24px;
    height: 100%;
  }

  .swipe-buttion-icon {
    margin-right: 2px;
    padding-bottom: 4px;
    color: inherit;
  }

  .swipe-button-text {
    color: inherit;
    font-size: $uni-font-size-base;
  }

  .btn-inverse {
    background-color: $uni-btn-color-inverse; // #000000; // 黑色
    color: $uni-text-color-inverse;
  }
  .btn-default {
    background-color: $uni-btn-color-default; // #d4d4d4; // 灰色
    color: $uni-text-color;
  }
  .btn-primary {
    background-color: $uni-btn-color-primary; // #007aff; // 蓝色
    color: $uni-text-color-inverse;
  }
  .btn-success {
    background-color: $uni-btn-color-success; // #3aa322; // 绿色
    color: $uni-text-color-inverse;
  }
  .btn-info {
    background-color: $uni-btn-color-info; // #2aaedd; // 浅蓝
    color: $uni-text-color-inverse;
  }
  .btn-warning {
    background-color: $uni-btn-color-warning; // #e99f00; // 橙色
    color: $uni-text-color-inverse;
  }
  .btn-danger {
    background-color: $uni-btn-color-danger; // #e33033; // 红色
    color: $uni-text-color-inverse;
  }
  .btn-link {
    color: $uni-btn-color-primary;
  }
}
</style>
