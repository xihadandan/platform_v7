<template>
  <div>
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="设置">
        <a-form-model :model="widget.configuration" labelAlign="left" :wrapper-col="{ style: { textAlign: 'right' } }" :colon="false">
          <a-form-model-item label="高度">
            <a-input-number v-model="widget.configuration.sliderHeight" :min="10" />
          </a-form-model-item>
          <a-form-model-item label="圆角">
            <a-input-number v-model="widget.configuration.borderRadius" :min="0" />
            px
          </a-form-model-item>
          <a-form-model-item label="轮播指示点类型">
            <a-radio-group v-model="widget.configuration.dotShape" button-style="solid" size="small">
              <a-radio-button value="default">默认</a-radio-button>
              <a-radio-button value="dot">圆点</a-radio-button>
              <a-radio-button value="round">椭圆点</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="轮播指示点颜色">
            <a-radio-group v-model="widget.configuration.dotColorType" button-style="solid" size="small">
              <a-radio-button value="white">白色</a-radio-button>
              <a-radio-button value="black">黑色</a-radio-button>
              <a-radio-button value="primary">主题色</a-radio-button>
            </a-radio-group>
          </a-form-model-item>
          <a-form-model-item label="自动播放">
            <a-switch v-model="widget.configuration.autoplay" />
          </a-form-model-item>
          <a-form-model-item label="间隔时间(秒)">
            <a-input-number :min="1" :max="60" v-model="widget.configuration.interval" />
          </a-form-model-item>
          <a-form-model-item label="轮播图片">
            <WidgetDesignDrawer :id="'widgetUniSwiper' + widget.id" title="轮播图片" :designer="designer">
              <template slot="content">
                <SwiperSliderConfiguration :widget="widget" ref="columns"></SwiperSliderConfiguration>
              </template>
              <a-button icon="setting" type="link">设置</a-button>
            </WidgetDesignDrawer>
          </a-form-model-item>
        </a-form-model>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style></style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import SwiperSliderConfiguration from './swiper-slider-configuration.vue';
export default {
  name: 'WidgetUniSwiperConfiguration',
  components: { SwiperSliderConfiguration },
  mixins: [],
  props: {
    widget: Object,
    designer: Object
  },
  data() {
    return {
      buttonIconSelectVisible: false,
      sliderItemsVisible: false
    };
  },

  beforeCreate() {},
  computed: {},
  created() {},
  methods: {
    openMoreConfigurationDrawer: function () {
      this.sliderItemsVisible = true;
    },
    closeMoreConfigurationDrawer: function () {
      this.sliderItemsVisible = false;
    }
  },
  mounted() {},
  configuration() {
    return {
      sliderHeight: 200,
      borderRadius: 8,
      autoplay: true,
      interval: 3,
      dotShape: 'default',
      sliderItems: [],
      dotColorType: 'white'
    };
  }
};
</script>
