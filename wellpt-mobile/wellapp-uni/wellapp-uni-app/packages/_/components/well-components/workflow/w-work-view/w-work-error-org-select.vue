<!-- 工作流异常-人员选择-组织弹出框 -->
<template>
  <view class="workflow-error-org-select">
    <view class="workflow-error-org-select__top">
      <uni-nav-bar
        class="pt-nav-bar"
        :title="orgOptions.title"
        v-if="orgOptions.title"
        leftWidth="0px"
        rightWidth="0px"
        :statusBar="true"
        :shadow="false"
        :border="false"
      />
      <slot>
        <view v-if="viewFormMode == 'custom'" class="flex workflow-error-org-select__top__viewFormMode">
          <view style="width: 130px">
            {{ $t("WorkflowWork.modal.actionFormPrivilegeLabel", { actionName }, actionName + "表单权限") }}
          </view>
          <radio-group @change="radioChange">
            <view class="radio">
              <radio value="default" :checked="viewFormModeValue == 'default'" style="transform: scale(0.7)" />
              {{ $t("WorkflowWork.modal.sameActionFromPrivilegeLabel", { actionName }, `同${actionName}人表单权限`) }}
            </view>
            <view class="radio">
              <radio value="readonly" :checked="viewFormModeValue == 'readonly'" style="transform: scale(0.7)" />
              {{ $t("WorkflowWork.modal.readPrivilege", "只读权限") }}
            </view>
          </radio-group>
        </view>
      </slot>
    </view>
    <w-org-select
      :style="{ '--org-select-custom-top': `calc(var(--top-window-height) + ${topHeight}px)` }"
      :orgType="orgOptions.orgType"
      :title="orgOptions.title"
      :multiSelect="orgOptions.multiSelect"
      :isPathValue="orgOptions.isPathValue"
      :checkableTypes="orgOptions.checkableTypes"
      :orgVersionId="orgOptions.orgVersionId"
      :orgVersionIds="orgOptions.orgVersionIds"
      :bizOrgId="orgOptions.bizOrgId"
      :orgIdOptions="orgOptions.orgIdOptions"
      :params="orgOptions.params"
      :viewStyles="orgOptions.viewStyles"
      :checkableTypesOfOrgType="orgOptions.checkableTypesOfOrgType"
      :hasPopupBarHeight="false"
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
    const views = uni.createSelectorQuery().in(this);
    views
      .select(".workflow-error-org-select__top")
      .boundingClientRect((data) => {
        if (data) {
          this.topHeight = data.bottom;
        }
        // if (this.viewFormMode == "custom") {
        //   this.topHeight += 64;
        //   this.viewFormModeChange(this.viewFormModeValue);
        // }
      })
      .exec();
    this.$refs.orgSelect.showPopup();
  },
  computed: {},
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
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
  --top-window-height: 0px;
  // #ifdef APP-PLUS
  --top-window-height: 0px;
  // #endif
  .workflow-error-org-select__top {
    position: absolute;
    top: 0;
    z-index: 100;
    width: 100%;
    .workflow-error-org-select__top__viewFormMode {
      padding: 8px 10px;
      background-color: #ffffff;
    }
  }
  .org-select .org-input {
    display: none;
  }
  ::v-deep .org-popup.uni-popup .uni-popup__wrapper.right {
    padding-top: var(--org-select-custom-top);
  }
}
</style>
