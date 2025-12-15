<template>
  <a-form-model>
    <a-form-model-item label="按钮名称">
      <a-input v-model="button.buttonName">
        <template slot="addonAfter">
          <WI18nInput :widget="widget" :designer="designer" :code="button.id" :target="button" v-model="button.buttonName" />
        </template>
      </a-input>
    </a-form-model-item>
    <!-- <a-form-model-item label="按钮类型">
      {{ button.default ? '内置按钮' : '扩展按钮' }}
    </a-form-model-item> -->
    <a-form-model-item label="按钮编码">
      <a-input v-model="button.code" v-show="!button.default" />
      <label v-show="button.default">{{ button.code }}</label>
    </a-form-model-item>
    <a-form-model-item label="显示/编辑类">
      <a-select v-model="button.btnShowType" :getPopupContainer="getPopupContainerByPs()">
        <a-select-option value="edit">编辑类</a-select-option>
        <a-select-option value="show">显示类</a-select-option>
      </a-select>
    </a-form-model-item>
    <a-form-model-item label="按钮图标">
      <WidgetDesignModal
        title="选择图标"
        :zIndex="1000"
        dialogClass="pt-modal widget-icon-lib-modal"
        :bodyStyle="{ height: '560px' }"
        :maxHeight="560"
        :width="640"
        mask
        bodyContainer
      >
        <IconSetBadge v-model="button.style.icon" onlyIconClass></IconSetBadge>
        <template slot="content">
          <WidgetIconLib v-model="button.style.icon" />
        </template>
      </WidgetDesignModal>
    </a-form-model-item>
    <slot name="extraInfo"></slot>
    <a-collapse :bordered="false" expandIconPosition="right" :style="{ background: '#fff' }" defaultActiveKey="eventSetting">
      <a-collapse-panel key="eventSetting">
        <template slot="header">自定义事件</template>
        <a-form-model-item label="鼠标点击">
          <WidgetCodeEditor @save="value => (button.customEvent.click = value)" :value="button.customEvent.click || ''">
            <a-button icon="code">编写代码</a-button>
          </WidgetCodeEditor>
        </a-form-model-item>
        <!-- <a-form-model-item label="鼠标移入">
          <WidgetCodeEditor @save="value => (button.mouseenterEventScript = value)" :value="button.mouseenterEventScript">
            <a-button icon="code">编写代码</a-button>
          </WidgetCodeEditor>
        </a-form-model-item>
        <a-form-model-item label="鼠标移出">
          <WidgetCodeEditor @save="value => (button.mouseleaveEventScript = value)" :value="button.mouseleaveEventScript">
            <a-button icon="code">编写代码</a-button>
          </WidgetCodeEditor>
        </a-form-model-item> -->
      </a-collapse-panel>
    </a-collapse>
  </a-form-model>
</template>

<script type="text/babel">
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'UploadAddButton',
  props: {
    button: Object
  },
  data() {
    return {
      iconVisible: this.button.style.icon != undefined
    };
  },
  methods: {
    getPopupContainerByPs,
    deleteIcon() {
      this.button.style.icon = undefined;
    },
    iconSelected(icon, data) {
      data.icon = icon;
      this.iconVisible = true;
    }
  }
};
</script>
