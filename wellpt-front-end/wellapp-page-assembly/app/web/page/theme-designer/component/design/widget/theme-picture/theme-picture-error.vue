<template>
  <!-- 错误提示 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-picture-error-border-color"
              v-model="item.wPictureErrorBorderColor"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-picture-error-text-color" v-model="item.wPictureErrorTextColor" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-picture-error-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wPictureErrorTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-picture-error-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wPictureErrorTextWeight"
            />
          </a-collapse-panel>
          <!-- <a-collapse-panel key="3" header="图标">
              <StyleVarSelectFormItem
                label="图标尺寸"
                codeLabel="--w-picture-error-icon-size"
                :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
                v-model="item.wPictureErrorIconSize"
              />
              <StyleColorTreeSelectFormItem
                label="图标颜色"
                codeLabel="--w-picture-error-icon-color"
                v-model="item.wPictureErrorIconColor"
              />
            </a-collapse-panel> -->
        </a-collapse>
      </a-form-model>
    </a-tab-pane>
    <a-tab-pane key="2" tab="悬停">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem
              label="边框颜色"
              codeLabel="--w-picture-error-border-color-hover"
              v-model="item.wPictureErrorBorderColorHover"
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
  name: 'ThemePictureError',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
