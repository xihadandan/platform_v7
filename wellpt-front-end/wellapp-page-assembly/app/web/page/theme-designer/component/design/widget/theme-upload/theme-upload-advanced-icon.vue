<template>
  <!-- 高级视图-图标 -->
    <a-tabs default-active-key="1" class="style-prop-tab" size="small">
      <a-tab-pane key="1" tab="默认">
        <a-form-model layout="vertical" :colon="false">
          <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
            <a-collapse-panel key="1" header="背景">
              <StyleColorTreeSelectFormItem
                label="背景颜色"
                codeLabel="--w-upload-advanced-icon-bg"
                v-model="item.wUploadAdvancedIconBg"
              />
            </a-collapse-panel>
            <a-collapse-panel key="2" header="边距">
              <StyleVarSelectFormItem
                label="内边距"
                codeLabel="--w-upload-advanced-icon-padding"
                :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
                v-model="item.wUploadAdvancedIconPadding"
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
    name: 'ThemeUploadAdvancedIcon',
    mixins: [themePropMixin],
    props: {
      item: Object
    },
    data() {
      return {};
    }
  };
  </script>
  