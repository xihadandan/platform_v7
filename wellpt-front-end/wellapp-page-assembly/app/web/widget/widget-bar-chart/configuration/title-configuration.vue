<template>
  <div>
    <a-form-model-item label="显示">
      <a-switch v-model="titleConfig.show" />
    </a-form-model-item>
    <div v-show="titleConfig.show">
      <a-form-model-item label="主标题">
        <a-input v-model="titleConfig.text" allow-clear>
          <template slot="addonAfter">
            <WidgetDesignDrawer :id="'chartTitleConfig' + widget.id" title="主标题文字样式" :designer="designer">
              <Icon type="pticon iconfont icon-ptkj-shezhi" title="主标题文字样式设置" />
              <template slot="content">
                <TextStyleConfiguration :textStyle="titleConfig.textStyle" :widget="widget" :designer="designer" />
              </template>
            </WidgetDesignDrawer>
            <WI18nInput :widget="widget" :designer="designer" :target="titleConfig" code="title" v-model="titleConfig.text" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="副标题">
        <a-input v-model="titleConfig.subtext" allow-clear>
          <template slot="addonAfter">
            <WidgetDesignDrawer :id="'chartSubTitleConfig' + widget.id" title="副标题文字样式" :designer="designer">
              <Icon type="pticon iconfont icon-ptkj-shezhi" title="副标题文字样式设置" />
              <template slot="content">
                <TextStyleConfiguration :textStyle="titleConfig.subtextStyle" :widget="widget" :designer="designer" />
              </template>
            </WidgetDesignDrawer>
            <WI18nInput :widget="widget" :designer="designer" :target="titleConfig" code="subTitle" v-model="titleConfig.subtext" />
          </template>
        </a-input>
      </a-form-model-item>
      <a-form-model-item label="主副标题间距">
        <a-input-number v-model="titleConfig.itemGap" :min="1" />
      </a-form-model-item>

      <template v-for="(opt, i) in positionDist">
        <a-form-model-item :label="opt.label" :key="'postDist_' + i" :label-col="{ style: { width: '80px' } }" style="display: flex">
          <a-input-group compact style="width: 190px">
            <a-input-number
              v-model="titleConfig[opt.value]"
              :min="0"
              style="width: calc(100% - 60px)"
              v-if="titleConfig[opt.value + 'Unit'] == 'px' || titleConfig[opt.value + 'Unit'] == '%'"
            />
            <a-select
              v-model="titleConfig[opt.value + 'Unit']"
              :style="{
                width: titleConfig[opt.value + 'Unit'] == 'px' || titleConfig[opt.value + 'Unit'] == '%' ? '60px' : '100%'
              }"
              :getPopupContainer="getPopupContainerNearestPs()"
              defaultValue="px"
            >
              <a-select-option value="px">px</a-select-option>
              <a-select-option value="%">%</a-select-option>
              <template v-if="opt.value == 'left'">
                <a-select-option value="left">居左</a-select-option>
                <a-select-option value="center">居中</a-select-option>
                <a-select-option value="right">居右</a-select-option>
              </template>
              <template v-if="opt.value == 'top'">
                <a-select-option value="top">顶端</a-select-option>
                <a-select-option value="middle">居中</a-select-option>
                <a-select-option value="bottom">底端</a-select-option>
              </template>
            </a-select>
          </a-input-group>
        </a-form-model-item>
      </template>
    </div>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import TextStyleConfiguration from './text-style-configuration.vue';
import { getPopupContainerNearestPs } from '@framework/vue/utils/function.js';
export default {
  name: 'TitleConfiguration',
  props: { widget: Object, designer: Object },
  components: { TextStyleConfiguration },
  computed: {
    titleConfig() {
      return this.widget.configuration.titleConfig;
    }
  },
  data() {
    return {
      positionDist: [
        { label: '离左侧距离', value: 'left' },
        { label: '离上侧距离', value: 'top' },
        { label: '离右侧距离', value: 'right' },
        { label: '离下侧距离', value: 'bottom' }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: { getPopupContainerNearestPs }
};
</script>
