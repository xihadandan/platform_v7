<template>
  <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model-item label="名称">
          <a-input v-model="widget.title" />
        </a-form-model-item>
        <a-form-model-item label="JS模块">
          <JsModuleSelect v-model="configuration.jsModules" dependencyFilter="WidgetCarouselDevelopment" width="205px" />
        </a-form-model-item>
        <carousel-list :designer="designer" :widget="widget" :dataSource="configuration.children" />
        <a-form-model-item label="自动切换">
          <a-switch v-model="configuration.autoplay" />
        </a-form-model-item>
        <template v-if="configuration.autoplay">
          <a-form-model-item label="切换频率">
            <w-input-number v-model="configuration.autoplaySpeed" :min="3" suffix="秒" />
          </a-form-model-item>
          <a-form-model-item label="悬停暂停">
            <a-switch v-model="configuration.pauseOnHover" />
          </a-form-model-item>
        </template>
        <a-form-model-item label="切换效果">
          <a-radio-group v-model="configuration.effect" size="small">
            <a-radio-button v-for="item in effectOptions" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-radio-button>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="面板指示点">
          <a-switch v-model="configuration.dots" />
        </a-form-model-item>
        <template v-if="configuration.dots">
          <a-form-model-item label="面板指示点类名">
            <a-input v-model="configuration.dotsClass" />
          </a-form-model-item>
          <a-form-model-item label="面板指示点位置">
            <a-radio-group v-model="configuration.dotPosition" size="small">
              <a-radio-button v-for="item in dotPositionOptions" :key="item.value" :value="item.value">
                {{ item.label }}
              </a-radio-button>
            </a-radio-group>
          </a-form-model-item>
        </template>
        <a-form-model-item label="高度">
          <w-input-number v-model="configuration.height" :min="10">
            <template slot="addonAfter">
              <a-select v-model="configuration.heightUnit" :options="lengthUnitOptions" style="width: 80px" />
            </template>
          </w-input-number>
        </a-form-model-item>
        <!--  <a-form-model-item label="箭头">
          <a-switch v-model="configuration.arrows" />
        </a-form-model-item> -->
      </a-tab-pane>
      <a-tab-pane key="2" tab="事件设置">
        <WidgetEventConfiguration :widget="widget" :designer="designer"></WidgetEventConfiguration>
      </a-tab-pane>
    </a-tabs>
  </a-form-model>
</template>

<script>
import { dotPositionOptions, effectOptions, lengthUnitOptions } from './constant';
import CarouselList from './carousel-list.vue';
import WInputNumber from '../components/w-input-number/index';

export default {
  name: 'WidgetCarouselConfiguration',
  props: {
    widget: Object,
    designer: Object
  },
  components: {
    CarouselList,
    WInputNumber
  },
  provide() {
    return {
      designer: this.designer,
      widget: this.widget
    };
  },
  data() {
    const { configuration } = this.widget;
    return {
      configuration,
      dotPositionOptions,
      effectOptions,
      lengthUnitOptions
    };
  },
  methods: {}
};
</script>

<style lang="less">
.table-footer-right {
  .ant-table-footer {
    padding: 0px;
    float: right;
    text-align: right;
    width: 100%;
  }
}
</style>
