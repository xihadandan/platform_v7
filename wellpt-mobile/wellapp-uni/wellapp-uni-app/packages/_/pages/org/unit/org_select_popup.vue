<template>
  <view class="page-org-select" :style="containerStyle">
    <view class="page-org-select__top" v-if="!orgOptions.hideTitle">
      <uni-nav-bar
        class="pt-nav-bar"
        :title="orgOptions.title"
        v-if="orgOptions.title"
        leftWidth="0px"
        rightWidth="0px"
        :shadow="false"
        :border="false"
      />
      <slot></slot>
    </view>
    <w-org-select
      :style="containerStyle"
      :popupAnimation="false"
      :hasPopupBarHeight="false"
      :checkableTypes="orgOptions.checkableTypes"
      :checkableTypesOfOrgType="orgOptions.checkableTypesOfOrgType"
      :customClass="orgOptions.customClass"
      :customStyle="orgOptions.customStyle"
      :defaultOrgType="orgOptions.defaultOrgType"
      :displayStyle="orgOptions.displayStyle"
      :hideOrgInput="true"
      :isPathValue="orgOptions.isPathValue"
      :linkType="orgOptions.linkType"
      :multiSelect="orgOptions.multiSelect"
      :orgType="orgOptions.orgType"
      :orgTypeExtensions="orgOptions.orgTypeExtensions"
      :orgUuid="orgOptions.orgUuid"
      :orgVersionId="orgOptions.orgVersionId"
      :orgVersionIds="orgOptions.orgVersionIds"
      :params="orgOptions.params"
      :separator="orgOptions.separator"
      :title="orgOptions.title"
      :titleDisplay="orgOptions.titleDisplay"
      :titleField="orgOptions.titleField"
      :uncheckableTypes="orgOptions.uncheckableTypes"
      :viewStyles="orgOptions.viewStyles"
      v-model="value"
      @popusConfirm="onChange"
      ref="orgSelect"
      @popusClose="onClose"
    ></w-org-select>
  </view>
</template>

<script>
import { assign, isFunction } from "lodash";
export default {
  name: "orgSelectPopup",
  data() {
    return {
      eventChannel: null,
      value: [],
      orgId: "",
      orgOptions: {},
      footerDescriptionComponent: undefined,
      locale: {},
      viewFormMode: "", // custom自定义表单权限
      actionName: "", // 操作名
      windowHeight: "",
      topHeight: 44,
      options: {},
    };
  },
  computed: {
    containerStyle() {
      let style = this.theme;
      style += `--org-select-custom-top: calc(var(--top-window-height) + ${this.topHeight}px)`;
      return style;
    },
  },
  onLoad() {
    uni.showLoading();
    this.eventChannel = this.getOpenerEventChannel();
    this.eventChannel.on("orgSelectPopup", (params, options) => {
      if (options) {
        this.options = options;
      }
      this.init(params);
    });
  },
  methods: {
    init(params) {
      this.orgOptions = params;
      this.value = params.value;
      this.topHeight = params.hideTitle ? 0 : 44;
      this.$nextTick(() => {
        this.$refs.orgSelect.setValue(params.value);
        setTimeout(() => {
          this.open();
          uni.hideLoading();
        }, 500);
      });
      // 通用逻辑写在这里
    },
    onClose() {
      let _this = this;
      uni.navigateBack({
        delta: 1,
        success() {
          if (isFunction(_this.options.onClose)) {
            _this.options.onClose();
          }
          // _this.eventChannel.emit("cancel");
        },
      });
    },
    confirm() {
      let _this = this;
    },
    open() {
      this.$refs.orgSelect.showPopup();
    },
    onChange({ value, label, nodes }) {
      let _this = this;
      let values = [];
      for (let i = 0, len = nodes.length; i < len; i++) {
        let key = nodes[i].key;
        if (this.orgOptions.isPathValue && nodes[i].keyPath) {
          // 值以全路径返回
          key = nodes[i].keyPath;
        }
        values.push(key);
      }
      if (isFunction(_this.options.onConfirm)) {
        _this.options.onConfirm({ value: values, label, nodes });
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.page-org-select {
  --top-window-height: 0px;
  // #ifdef APP-PLUS
  --top-window-height: var(--status-bar-height);
  // #endif
  .page-org-select__top {
    position: absolute;
    top: 0;
    z-index: 100;
    width: 100%;
    // #ifdef APP-PLUS
    top: var(--status-bar-height);
    // #endif
  }
  .org-select .org-input {
    display: none;
  }
}
</style>
