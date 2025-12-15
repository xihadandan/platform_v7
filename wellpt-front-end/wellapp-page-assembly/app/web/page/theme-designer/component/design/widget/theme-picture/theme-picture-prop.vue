<template>
  <!-- 基础样式 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="默认">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem label="边框颜色" codeLabel="--w-picture-border-color" v-model="item.wPictureBorderColor" />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-picture-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wPictureBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-picture-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wPictureBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-picture-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wPictureBorderRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem label="文字颜色" codeLabel="--w-picture-text-color" v-model="item.wPictureTextColor" />
            <StyleVarSelectFormItem
              label="字号"
              codeLabel="--w-picture-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wPictureTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-picture-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wPictureTextWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-picture-icon-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wPictureIconSize"
            />
            <StyleColorTreeSelectFormItem label="图标颜色" codeLabel="--w-picture-icon-color" v-model="item.wPictureIconColor" />
          </a-collapse-panel>
          <a-collapse-panel key="5" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-picture-select-bg" v-model="item.wPictureSelectBg" />
          </a-collapse-panel>
          <!-- <a-collapse-panel key="6" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-picture-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wPictureLrPadding"
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
              codeLabel="--w-picture-border-color-hover"
              v-model="item.wPictureBorderColorHover"
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
  name: 'ThemePictureProp',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
