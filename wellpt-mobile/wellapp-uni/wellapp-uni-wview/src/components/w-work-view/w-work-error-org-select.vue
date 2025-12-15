<!-- 工作流异常-人员选择-组织弹出框 -->
<template>
  <view
    class="workflow-error-org-select"
    :style="{ '--window-top': `calc(var(--top-window-height) + ${topHeight}px)` }"
  >
    <view class="workflow-error-org-select__top">
      <uni-nav-bar
        :title="orgOptions.title"
        v-if="orgOptions.title"
        leftWidth="0px"
        rightWidth="0px"
        :shadow="true"
        :border="false"
      />
      <slot>
        <view v-if="viewFormMode == 'custom'" class="flex workflow-error-org-select__top__viewFormMode">
          <view style="width: 130px"> {{ actionName }}表单权限 </view>
          <radio-group @change="radioChange">
            <view class="radio">
              <radio value="default" :checked="viewFormModeValue == 'default'" style="transform: scale(0.7)" />
              同{{ actionName }}人表单权限
            </view>
            <view class="radio">
              <radio value="readonly" :checked="viewFormModeValue == 'readonly'" style="transform: scale(0.7)" />
              只读权限
            </view>
          </radio-group>
        </view>
      </slot>
    </view>
    <w-org-select
      :orgType="orgOptions.orgType"
      :title="orgOptions.title"
      :multiSelect="orgOptions.multiSelect"
      :isPathValue="orgOptions.isPathValue"
      :checkableTypes="orgOptions.checkableTypes"
      :orgVersionId="orgOptions.orgVersionId"
      :orgVersionIds="orgOptions.orgVersionIds"
      :params="orgOptions.params"
      :checkableTypesOfOrgType="orgOptions.checkableTypesOfOrgType"
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
  props: {
    options: {
      type: Object,
      default: {
        data: {},
        methods: {},
      },
    },
  },
  data() {
    let data = assign(
      {
        value: "",
        orgId: "",
        orgOptions: {},
        footerDescriptionComponent: undefined,
        locale: {},
        viewFormMode: "", // custom自定义表单权限
        actionName: "", // 操作名
        windowHeight: "",
        topHeight: 44,
        viewFormModeValue: "default",
      },
      this.options.data
    );
    return data;
  },
  created: function () {},
  mounted: function () {
    // 更新窗口高度
    uni.getSystemInfo({
      success: (result) => {
        var windowHeight = result.windowHeight;
        console.log("windowHeight: " + windowHeight);
        this.windowHeight = windowHeight + "px";
      },
    });
    if (this.viewFormMode == "custom") {
      this.topHeight += 64;
      this.viewFormModeChange(this.viewFormModeValue);
    }
    this.$refs.orgSelect.showPopup();
  },
  computed: {},
  methods: {
    onChange({ value, label, nodes }) {
      let values = [];
      for (let i = 0, len = nodes.length; i < len; i++) {
        let key = nodes[i].key;
        if (this.orgOptions.isPathValue && nodes[i].keyPath) {
          // 值以全路径返回
          key = nodes[i].keyPath;
        }
        values.push(key);
      }
      if (isFunction(this.options.methods.onConfirm)) {
        this.options.methods.onConfirm({ value: values, label, nodes });
      }
      this.$emit("confirm", { value: values, label, nodes });
    },
    onClose() {
      if (isFunction(this.options.methods.onClose)) {
        this.options.methods.onClose();
      }
      this.$emit("close");
    },
    radioChange: function (evt) {
      this.viewFormModeValue = evt.detail.value;
      if (isFunction(this.options.methods.viewFormModeChange)) {
        this.options.methods.viewFormModeChange(this.viewFormModeValue);
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.workflow-error-org-select {
  .workflow-error-org-select__top {
    position: absolute;
    top: 0;
    z-index: 100;
    width: 100%;
    .workflow-error-org-select__top__viewFormMode {
      padding: 8px 10px;
    }
  }
  .org-select .org-input {
    display: none;
  }
}
</style>
