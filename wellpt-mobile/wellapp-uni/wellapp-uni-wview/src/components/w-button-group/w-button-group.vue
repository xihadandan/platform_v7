<template>
  <view class="w-button-group">
    <template v-if="widget.configuration.groupType == 'dropdown'">
      <button type="primary" @click="openPopupButtons">
        {{ widget.title }}<w-icon style="margin-left: 5px" icon="down" color="#fff" />
      </button>

      <uni-popup ref="popupButtons" type="bottom" safeArea>
        <view style="background: #fff; padding: 12px">
          <view class="button-box">
            <view class="pop-button-item" v-for="button in widget.configuration.group" @click="onClick(button)">
              <w-icon :icon="button.icon" :size="36" />
              <text>{{ button.title }}</text>
            </view>
          </view>
        </view>
      </uni-popup>
    </template>
    <template v-else-if="widget.configuration.groupType == 'buttonGroup'">
      <view class="flex-button-group">
        <view class="button" v-for="button in widget.configuration.group" @click="onClick(button)">
          <w-icon :icon="button.icon" :size="36" />
          <text>{{ button.title }}</text>
        </view>
      </view>
    </template>
    <template v-else>
      <u-switch
        v-if="widget.configuration.type == 'switch'"
        v-model="checked"
        :loading="loading"
        @change="onClick(widget.configuration)"
      />
      <button v-else :class="widget.configuration.type" style="color: #fff" @click="onClick(widget.configuration)">
        {{ widget.title }}
      </button>
    </template>
  </view>
</template>
<style lang="scss" scoped>
.w-button-group {
  padding: 12px;
  .button-box {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    width: 100%;
    justify-content: space-between;
    .pop-button-item {
      width: 65px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      padding: 10px 0;
      align-items: center;
      overflow: hidden;
      text-overflow: ellipsis;
      text-wrap: nowrap;
    }
  }

  .flex-button-group {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: flex-start;
    flex-wrap: wrap;
    .button {
      width: 25%;
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 10px 0;
      font-size: 14px;
      color: #3b4144;
      border-radius: 4px;
      &:active {
        background-color: #f5f5f5;
        font-weight: bold;
        color: var(--color-primary);
      }
    }
  }
}
</style>
<script>
import mixin from "../page-widget-mixin";
import { storage, appContext } from "wellapp-uni-framework";

export default {
  name: "w-button-group",
  mixins: [mixin],
  props: {},
  components: {},
  computed: {},
  data() {
    return { checked: false, loading: false };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onClick(button) {
      let _this = this,
        eventHandler = button.eventHandler;
      eventHandler.key = button.id || this.widget.id;
      let developJs = typeof this.developJsInstance === "function" ? this.developJsInstance() : this.developJsInstance;
      if (developJs == undefined) {
        developJs = {};
      }
      if (this.$pageJsInstance != undefined) {
        developJs[this.$pageJsInstance._JS_META_] = this.$pageJsInstance;
      }
      eventHandler.$developJsInstance = developJs;
      if (button.style && button.style.type === "switch") {
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
      this.$emit("click", button);
    },
    openPopupButtons() {
      this.$refs.popupButtons.open();
    },
  },
};
</script>
