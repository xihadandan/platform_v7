<template>
  <a-carousel :autoplay="sliderItems.length > 1 && widget.configuration.autoplay" class="widget-uni-swiper">
    <template v-if="sliderItems.length == 0">
      <div :style="{ height: widget.configuration.sliderHeight + 'px' }">
        <a-icon
          type="picture"
          :style="{
            color: 'var(--w-primary-color)',
            fontSize: widget.configuration.sliderHeight + 'px',
            position: 'absolute',
            left: '38px'
          }"
        />
        <div class="empty-dot"></div>
        <div class="empty-dot empty-dot-1"></div>
      </div>
    </template>
    <template v-else>
      <template v-for="(item, index) in sliderItems">
        <div :key="index" v-if="item.src" :style="{ height: widget.configuration.sliderHeight + 'px', position: 'relative' }">
          <img
            :src="formateImgSrc(item.src)"
            :style="{
              width: '100%',
              height: '100%',
              objectFit: 'cover',
              borderRadius: widget.configuration.borderRadius ? widget.configuration.borderRadius + 'px' : '8px'
            }"
          />
          <div
            v-if="item.text"
            class="sub-text"
            :style="{
              borderRadius: widget.configuration.borderRadius
                ? '0 0 ' + widget.configuration.borderRadius + 'px ' + widget.configuration.borderRadius + 'px'
                : ' 0 0 8px 8px'
            }"
          >
            {{ item.text }}
          </div>
        </div>
      </template>
    </template>
  </a-carousel>
</template>

<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
export default {
  name: 'WidgetUniSwiper',
  mixins: [editWgtMixin],
  data() {
    return {};
  },
  watch: {},
  beforeCreate() {},
  components: {},
  computed: {
    sliderItems: function () {
      var items = [];
      var sliderItems = this.widget.configuration.sliderItems;
      for (var i = 0; i < sliderItems.length; i++) {
        if (!sliderItems[i].hidden) {
          items.push(sliderItems[i]);
        }
      }
      return items;
    }
  },
  created() {},
  methods: {
    formateImgSrc(src) {
      if (src) {
        return src.startsWith('url("') ? src.substring(5, src.length - 2) : src;
      }
      return undefined;
    }
  },
  mounted() {}
};
</script>
<style scoped lang="less">
.widget-uni-swiper {
  .empty-dot {
    position: absolute;
    bottom: 7px;
    width: 10px;
    height: 10px;
    border-radius: 5px;
    background: #4f4c4c;
    left: 50%;
    transform: translate(-16px, 0);
    &-1 {
      transform: translate(0px, 0);
      background: #a9a1a1;
    }
  }

  .sub-text {
    position: absolute;
    bottom: 0px;
    z-index: 1;
    background: rgba(0, 0, 0, 0.3);
    width: 100%;
    line-height: 34px;
    color: #fff;
    padding: 0 12px;
  }
}
</style>
