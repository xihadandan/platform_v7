<template>
  <div>
    <a-form-model-item class="form-item-vertical">
      <template slot="label">
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <div slot="title">
            同一个用户在流程中的多个环节做为办理人时，可能会重复审批。通过审批去重设置可自动审批或跳过，减少重复审批次数
          </div>
          <label>
            审批去重设置
            <a-icon type="exclamation-circle" />
          </label>
        </a-tooltip>
      </template>
    </a-form-model-item>
    <a-form-model-item label="审批去重功能">
      <w-switch v-model="formData.enabledAutoSubmit" :checkedValue="true" :unCheckedValue="false" @change="changeEnable" />
    </a-form-model-item>
    <a-form-model-item v-if="formData.enabledAutoSubmit">
      <template slot="label">
        <a-button type="link" size="small" @click="openDrawer">
          <Icon type="pticon iconfont icon-ptkj-shezhi" />
          <span>设置规则</span>
        </a-button>
      </template>
    </a-form-model-item>

    <drawer
      v-model="visible"
      title="审批去重规则设置"
      :width="525"
      :closable="false"
      :container="getContainer"
      wrapClassName="permission-drawer-wrap auto-submit-drawer-wrap"
    >
      <template slot="content">
        <auto-submit-rule-info v-if="visible" ref="refItem" :formData="autoSubmitRule" @changeMode="changeRuleMode" />
      </template>
      <template slot="footer">
        <a-button type="primary" @click="saveRule">保存</a-button>
        <a-button @click="resetDefault">恢复默认</a-button>
        <a-button @click="closeDrawer">取消</a-button>
      </template>
    </drawer>
  </div>
</template>

<script>
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import WSwitch from '../components/w-switch';
import AutoSubmitRuleInfo from './auto-submit-rule-info.vue';
import AutoSubmitRule from '../designer/AutoSubmitRule';
const drawerSelector = '.configuration-drawer-container';

export default {
  name: 'AutoSubmitRuleDrawer',
  inject: ['pageContext', 'designer'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    Drawer,
    WSwitch,
    AutoSubmitRuleInfo
  },
  data() {
    let autoSubmitRule;
    if (this.formData.autoSubmitRule) {
      autoSubmitRule = JSON.parse(JSON.stringify(this.formData.autoSubmitRule));
    }
    return {
      containerCreated: false,
      visible: false,
      autoSubmitRule
    };
  },
  mounted() {
    this.pageContext.handleEvent('closeAllDrawer', this.closeDrawer);
  },
  beforeDestroy() {
    this.pageContext.offEvent('closeAllDrawer');
  },
  methods: {
    changeEnable(checked) {
      if (checked) {
        if (!this.formData.autoSubmitRule) {
          this.autoSubmitRule = new AutoSubmitRule({
            mode: this.designer.flowDefinition.autoSubmitMode
          });
        }
      }
    },
    changeRuleMode(mode) {
      this.autoSubmitRule = new AutoSubmitRule({
        mode
      });
    },
    closeDrawer() {
      this.visible = false;
    },
    openDrawer() {
      this.pageContext.emitEvent('closeAllDrawer');
      if (!this.containerCreated) {
        this.containerCreated = true;
      }
      this.visible = true;
    },
    saveRule() {
      this.$refs.refItem.validate(({ valid, error, data }) => {
        if (valid) {
          this.formData.autoSubmitRule = this.autoSubmitRule;
          this.visible = false;
        }
      });
    },
    resetDefault() {
      this.autoSubmitRule = new AutoSubmitRule({
        mode: this.designer.flowDefinition.autoSubmitMode
      });
      this.visible = false;
    },
    getContainer() {
      return document.querySelector(drawerSelector);
    }
  }
};
</script>
