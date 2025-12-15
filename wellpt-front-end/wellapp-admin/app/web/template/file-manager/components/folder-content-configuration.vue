<template>
  <div>
    <a-form-model-item label="文档类型" prop="configuration.dataTypes">
      <a-checkbox-group v-model="configuration.dataTypes">
        <a-checkbox v-for="item in dataTypeOptions" :key="item.value" :value="item.value">{{ item.label }}</a-checkbox>
      </a-checkbox-group>
    </a-form-model-item>
    <div v-show="configuration.dataTypes.includes('FILE')">
      <div class="item-title">文件实体设置</div>
      <a-form-model-item label="支持的实体文件格式">
        <a-radio-group v-model="configuration.fileAcceptMode" button-style="solid">
          <a-radio-button value="unlimited">不限制</a-radio-button>
          <a-radio-button value="limited">限制</a-radio-button>
        </a-radio-group>
      </a-form-model-item>
      <FileAcceptCheckboxGroup v-if="configuration.fileAcceptMode == 'limited'" :configuration="configuration"></FileAcceptCheckboxGroup>
      <FileWatermark
        :widget="{ configuration }"
        :designer="pageContext"
        :bodyStyle="{ height: 'calc(100vh - 40px)', paddingTop: '50px', paddingRight: '10px' }"
      >
        <span slot="itemLabel">文档水印</span>
      </FileWatermark>
    </div>
    <keep-alive>
      <div v-if="configuration.dataTypes.includes('DYFORM')">
        <div class="item-title">动态表单设置</div>
        <FolderDyformConfiguration :configuration="configuration"></FolderDyformConfiguration>
      </div>
    </keep-alive>
    <a-form-model-item v-if="configuration.dataTypes.length" label="全文检索">
      <a-switch v-model="configuration.enabledFulltextIndex" checked-children="启用" un-checked-children="禁用" />
    </a-form-model-item>
  </div>
</template>

<script>
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';
import WidgetDesignDrawer from '@pageAssembly/app/web/widget/commons/widget-design-drawer.vue';
import FileWatermark from '@dyform/app/web/widget/widget-form-file-upload/configuration/file-watermark.vue';
import FileAcceptCheckboxGroup from './file-accept-checkbox-group.vue';
import FolderDyformConfiguration from './folder-dyform-configuration.vue';

if (!Vue.options.components['WidgetDesignDrawer']) {
  Vue.component(
    'WidgetDesignDrawer',
    Vue.extend({
      extends: WidgetDesignDrawer,
      created() {
        this.drawerContainer = () => {
          return document.body;
        };
        this.zIndex = 1000;
        this.wrapStyleTop = '0';
      }
    })
  );
}
if (!Vue.options.components['ColorPicker']) {
  Vue.component('ColorPicker', ColorPicker);
}
export default {
  props: {
    configuration: Object
  },
  components: {
    FileWatermark,
    FileAcceptCheckboxGroup,
    FolderDyformConfiguration
  },
  inject: ['pageContext', '$event', 'vPageState'],
  data() {
    if (!this.configuration.fileAcceptMode) {
      this.$set(this.configuration, 'fileAcceptMode', 'unlimited');
      this.$set(this.configuration, 'fileAccept', []);
    }
    if (!this.configuration.fileWatermarkConfig) {
      this.$set(this.configuration, 'fileWatermarkConfig', { effectScope: [] });
    }
    return {
      dataTypeOptions: [
        {
          label: '文件实体',
          value: 'FILE'
        },
        {
          label: '动态表单',
          value: 'DYFORM'
        }
        // {
        //   label: '在线文档',
        //   value: 'ONLINE'
        // }
      ]
    };
  }
};
</script>

<style lang="less" scoped>
.item-title {
  color: inherit;
  display: block;
  font-size: 14px;
  font-weight: 600;
}
</style>
