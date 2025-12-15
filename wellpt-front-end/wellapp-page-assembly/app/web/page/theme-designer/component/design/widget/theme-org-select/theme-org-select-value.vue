<template>
  <!-- 返回值-标签 -->
  <a-tabs default-active-key="1" class="style-prop-tab" size="small">
    <a-tab-pane key="1" tab="标签">
      <a-form-model layout="vertical" :colon="false">
        <a-collapse default-active-key="1" :bordered="false" :accordion="true" expandIconPosition="right">
          <a-collapse-panel key="1" header="边框">
            <StyleColorTreeSelectFormItem label="边框颜色" codeLabel="--w-org-tag-border-color" v-model="item.wOrgTagBorderColor" />
            <StyleVarSelectFormItem
              label="边框样式"
              codeLabel="--w-org-tag-border-style"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderStyle)"
              v-model="item.wOrgTagBorderStyle"
            />
            <StyleVarSelectFormItem
              label="边框宽度"
              codeLabel="--w-org-tag-border-width"
              :options="getStyleDeriveOptions(packJson.borderConfig.borderWidth)"
              v-model="item.wOrgTagBorderWidth"
            />
          </a-collapse-panel>
          <a-collapse-panel key="2" header="圆角">
            <StyleVarSelectFormItem
              label="圆角尺寸"
              codeLabel="--w-org-tag-border-radius"
              :options="getStyleDeriveOptions(packJson.radiusConfig)"
              v-model="item.wOrgTagBorderRadius"
            />
          </a-collapse-panel>
          <a-collapse-panel key="3" header="文字">
            <StyleColorTreeSelectFormItem label="标签颜色" codeLabel="--w-org-tag-text-color" v-model="item.wOrgTagTextColor" />
            <StyleVarSelectFormItem
              label="标签字号"
              codeLabel="--w-org-tag-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wOrgTagTextSize"
            />
            <StyleColorTreeSelectFormItem label="文本颜色" codeLabel="--w-org-text-color" v-model="item.wOrgTextColor" />
            <StyleVarSelectFormItem
              label="文本字号"
              codeLabel="--w-org-text-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wOrgTextSize"
            />
            <StyleVarSelectFormItem
              label="字重"
              codeLabel="--w-org-tag-text-weight"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontWeight)"
              v-model="item.wOrgTagTextWeight"
            />
          </a-collapse-panel>
          <a-collapse-panel key="4" header="图标">
            <StyleVarSelectFormItem
              label="图标尺寸"
              codeLabel="--w-org-tag-close-size"
              :options="getStyleDeriveOptions(packJson.fontConfig.fontSize.classify[0])"
              v-model="item.wOrgTagCloseSize"
            />
          </a-collapse-panel>
          <a-collapse-panel key="5" header="背景">
            <StyleColorTreeSelectFormItem label="背景颜色" codeLabel="--w-org-tag-bg-color" v-model="item.wOrgTagBgColor" />
          </a-collapse-panel>
          <a-collapse-panel key="6" header="边距">
            <StyleVarSelectFormItem
              label="左右内边距"
              codeLabel="--w-org-tag-lr-padding"
              :options="getStyleDeriveOptions(packJson.spaceConfig.paddingConfig)"
              v-model="item.wOrgTagLrPadding"
            />
          </a-collapse-panel>
          <a-collapse-panel key="7" header="高度">
            <StyleVarSelectFormItem
              label="高度"
              codeLabel="--w-org-tag-height"
              :options="getStyleDeriveOptions(packJson.heightConfig)"
              v-model="item.wOrgTagHeight"
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
  name: 'ThemeOrgSelectValue',
  mixins: [themePropMixin],
  props: {
    item: Object
  },
  data() {
    return {};
  }
};
</script>
