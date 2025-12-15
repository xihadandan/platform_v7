<template>
  <!-- 高级视图-表格 -->
    <a-tabs default-active-key="1" class="style-prop-tab" size="small">
      <a-tab-pane key="1" tab="默认">
        <a-form-model layout="vertical" :colon="false">
          <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
            <a-collapse-panel key="1" header="边框">
              <StyleColorTreeSelectFormItem
                label="边框颜色"
                codeLabel="--w-upload-advanced-table-color"
                v-model="item.wUploadAdvancedTableColor"
              />
              <StyleVarSelectFormItem
                label="边框样式"
                codeLabel="--w-upload-advanced-table-style"
                :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
                v-model="item.wUploadAdvancedTableStyle"
              />
              <StyleVarSelectFormItem
                label="边框宽度"
                codeLabel="--w-upload-advanced-table-width"
                :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
                v-model="item.wUploadAdvancedTableWidth"
              />
            </a-collapse-panel>
            <a-collapse-panel key="2" header="圆角">
              <StyleVarSelectFormItem
                label="圆角尺寸"
                codeLabel="--w-upload-advanced-table-radius"
                :options="getStyleDeriveOptions(packJson.radiusConfig)"
                v-model="item.wUploadAdvancedTableRadius"
              />
            </a-collapse-panel>
            <a-collapse-panel key="3" header="背景">
              <StyleColorTreeSelectFormItem
                label="背景颜色"
                codeLabel="--w-upload-advanced-table-bg"
                v-model="item.wUploadAdvancedTableBg"
              />
            </a-collapse-panel>
            <a-collapse-panel key="4" header="边距">
              <StyleVarSelectFormItem
                label="内边距"
                codeLabel="--w-upload-advanced-table-margin"
                :options="getStyleDeriveOptions(packJson.spaceConfig.marginConfig)"
                v-model="item.wUploadAdvancedTableMargin"
              />
            </a-collapse-panel>
          </a-collapse>
        </a-form-model>
      </a-tab-pane>
    </a-tabs>
  </template>
  <script type="text/babel">
  import themePropMixin from '@framework/vue/mixin/themePropMixin';
  
  export default {
    name: 'ThemeUploadAdvancedTable',
    mixins: [themePropMixin],
    props: {
      item: Object
    },
    data() {
      return {};
    }
  };
  </script>
  