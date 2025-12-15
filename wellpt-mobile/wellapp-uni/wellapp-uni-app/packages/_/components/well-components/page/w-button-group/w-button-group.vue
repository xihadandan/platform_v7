<!-- 用于配置的基础组件（按钮） -->
<template>
  <view class="w-button-group" :class="customClassCom" :style="{ textAlign: widget.configuration.align }">
    <template v-if="widget.configuration.groupType">
      <uni-w-button-group :buttons="buttons" :gutter="gutter" @click="onClick" arrow :max="0"></uni-w-button-group>
    </template>
    <template v-else-if="vShow">
      <uni-w-switch
        v-if="widget.configuration.type == 'switch'"
        v-model="widget.configuration.switch.defaultChecked"
        :loading="loading"
        @change="onClick(widget.configuration)"
        :size="widget.configuration.size"
      />
      <uni-w-button
        v-else
        :type="widget.configuration.type"
        :block="widget.configuration.block"
        :size="widget.configuration.size"
        :ghost="widget.configuration.ghost"
        :shape="widget.configuration.shape"
        :icon="widget.configuration.icon"
        @click="onClick(widget.configuration)"
      >
        {{ widget.configuration.textHidden ? "" : $t(widget.id, widget.title) }}
        <w-icon
          slot="suffix"
          :icon="widget.configuration.suffixIcon"
          v-if="widget.configuration.suffixIcon"
          iconStyle="color: var(--w-button-font-color); font-size: var(--w-button-icon-size)"
        />
      </uni-w-button>
    </template>
  </view>
</template>
<style lang="scss" scoped>
.w-button-group {
  --w-button-group-component-button-margin-top: 0px;
  --w-button-group-component-button-margin-bottom: var(--w-padding-2xs);
  > .w-button {
    margin-top: var(--w-button-group-component-button-margin-top);
    margin-bottom: var(--w-button-group-component-button-margin-bottom);
  }
  ::v-deep .uni-w-button-group {
    --uni-w-button-group-padding-bottom: var(--w-padding-2xs);
  }
}
.w-page-container > * > .w-button-group {
  --w-button-group-component-button-margin-top: var(--w-padding-2xs);
  padding: 0 var(--w-padding-2xs);
}
</style>
<script>
import mixin from "../page-widget-mixin";
import { storage, appContext, utils } from "wellapp-uni-framework";
import { get } from "lodash";

export default {
  name: "w-button-group",
  mixins: [mixin],
  props: {},
  components: {},
  computed: {
    buttons() {
      let buttons = [];
      let groupBtns = [];
      for (let i = 0; i < this.widget.configuration.group.length; i++) {
        let btn = this.widget.configuration.group[i];
        btn.title = this.$t(btn.id, btn.title);
        // 根据表达式判断
        let visible = true;
        if (btn.defaultVisible != undefined) {
          visible = btn.defaultVisible;
          if (
            // 根据条件判断显隐
            btn.defaultVisibleVar &&
            btn.defaultVisibleVar.enable &&
            btn.defaultVisibleVar.conditions != undefined &&
            btn.defaultVisibleVar.conditions.length > 0
          ) {
            // 多组条件判断
            let match = btn.defaultVisibleVar.match == "all";
            let _showByData = {};
            if (this._vShowByData) {
              _showByData = this._vShowByData;
            } else if (this.dyform != undefined) {
              _showByData = this.dyform.formData;
            }
            let _compareDataSource = this.widgetDependentVariableDataSource();
            let _compareData = {
              ...this.vPageState,
              ..._compareDataSource,
              ...(_showByData || {}),
            };
            for (let i = 0, len = btn.defaultVisibleVar.conditions.length; i < len; i++) {
              let { code, operator, value, valueType } = btn.defaultVisibleVar.conditions[i];
              if (valueType == "variable") {
                try {
                  value = get(_compareData, value);
                } catch (error) {
                  console.error("无法解析变量值", value);
                }
              }
              let result = utils.expressionCompare(_compareData, code, operator, value);
              if (btn.defaultVisibleVar.match == "all" && !result) {
                match = false;
                break;
              }
              if (btn.defaultVisibleVar.match == "any" && result) {
                match = true;
                break;
              }
            }
            visible = match ? visible : !visible;
          }
        }
        if (visible) {
          if (this.widget.configuration.groupType == "buttonGroup") {
            btn.type = this.widget.configuration.type;
            btn.size = this.widget.configuration.size;
            buttons.push(btn);
          } else if (this.widget.configuration.groupType == "dropdown") {
            groupBtns.push(btn);
          }
        }
      }
      if (this.widget.configuration.groupType == "dropdown") {
        let btn = Object.assign({}, this.widget.configuration, { children: groupBtns });
        btn.title = this.$t(this.widget.id, this.widget.title);
        buttons.push(btn);
      }
      return buttons;
    },
    gutter() {
      if (this.widget.configuration.groupType == "buttonGroup" && this.widget.configuration.enableSpace) {
        return this.widget.configuration.buttonSpace || 8;
      }
      return 0;
    },
  },
  data() {
    return {
      checked: false,
      loading: false,
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onClick(button) {
      let _this = this,
        eventHandler = button.eventHandler;
      if (eventHandler) {
        eventHandler.key = button.id || this.widget.id;
        let developJs =
          typeof this.developJsInstance === "function" ? this.developJsInstance() : this.developJsInstance;
        if (developJs == undefined) {
          developJs = {};
        }
        if (this.$pageJsInstance != undefined) {
          developJs[this.$pageJsInstance._JS_META_] = this.$pageJsInstance;
        }
        eventHandler.$developJsInstance = developJs;
        if (button && button.type === "switch") {
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
          this.appContext.dispatchEvent({
            ui: _this,
            ...eventHandler,
          });
        }
      }
      console.log("点击按钮");
    },
    openPopupButtons() {
      this.$refs.popupButtons.open();
    },
  },
};
</script>
