<template>
  <div class="widget-carousel">
    <a-carousel
      :autoplay="configuration.autoplay"
      :dots="configuration.dots"
      :dotPosition="configuration.dotPosition"
      :dotsClass="configuration.dotsClass"
      :effect="configuration.effect"
      :arrows="configuration.arrows"
      :autoplaySpeed="autoplaySpeed"
      :pauseOnHover="configuration.pauseOnHover"
      :adaptiveHeight="true"
      :beforeChange="beforeChange"
      ref="carousel"
    >
      <!-- <template v-if="configuration.arrows">
      <div slot="prevArrow" slot-scope="props" class="custom-slick-arrow" style="left: 10px">
        <a-icon type="left-circle" />
      </div>
      <div slot="nextArrow" slot-scope="props" class="custom-slick-arrow" style="right: 10px">
        <a-icon type="right-circle" />
      </div>
    </template> -->
      <div
        class="widget-carousel-item"
        v-for="item in configuration.children"
        :key="item.id"
        :style="{
          ...itemHeight,
          position: 'relative',
          backgroundImage: `url('${item.src}')`,
          backgroundRepeat: 'no-repeat',
          backgroundPosition: 'center',
          backgroundSize: `${item.size}`
        }"
      >
        <WidgetTableButtons
          v-if="item.buttonEnable"
          :style="itemButtonStyle(item)"
          :button="setItemButtonGroup(item)"
          :developJsInstance="developJsInstance"
        />
      </div>
    </a-carousel>
    <a-empty
      v-if="designMode && configuration.children.length === 0"
      :style="{
        margin: 0,
        padding: 'var(--w-padding-xs) var(--w-padding-md)',
        ...vBackground
      }"
    />
  </div>
</template>

<script>
import widgetMixin from '@framework/vue/mixin/widgetMixin';
export default {
  name: 'WidgetCarousel',
  mixins: [widgetMixin],
  computed: {
    autoplaySpeed() {
      return this.configuration.autoplaySpeed * 1000;
    },
    itemHeight() {
      return {
        height: Number(this.configuration.height) + this.configuration.heightUnit
      };
    },
    itemButtonStyle() {
      return args => {
        const { buttonStyle } = args;
        let style = {
          position: 'absolute'
        };
        if (buttonStyle.xAxisDir !== null) {
          style[buttonStyle.xAxisDir] = buttonStyle.xAxis + buttonStyle.xAxisUnit;
        }
        if (buttonStyle.yAxisDir !== null) {
          style[buttonStyle.yAxisDir] = buttonStyle.yAxis + buttonStyle.yAxisUnit;
        }
        return style;
      };
    },
    defaultEvents() {
      return [
        {
          id: 'slickNext',
          title: '切换到下一面板',
          codeSnippet: `
          /**
           * 切换到下一面板
           */
          this.pageContext.emitEvent({{事件编码}});
          `
        },
        {
          id: 'slickPrev',
          title: '切换到上一面板',
          codeSnippet: `
          /**
           * 切换到上一面板
           */
          this.pageContext.emitEvent({{事件编码}});
          `
        },
        {
          id: 'slickGoTo',
          title: '切换到指定面板',
          eventParams: [
            {
              paramKey: 'slideNumber',
              remark: '幻灯片编号'
            },
            {
              paramKey: 'dontAnimate',
              remark: 'true/false , dontAnimate = true 时不使用动画 ，默认 false'
            }
          ],
          codeSnippet: `
          /**
           * 切换到指定面板
           * slideNumber: 幻灯片编号
           * dontAnimate: dontAnimate = true 时不使用动画 ，默认 false
           */
          this.pageContext.emitEvent({{事件编码}}, slideNumber, dontAnimate);
          `
        }
      ];
    }
  },
  methods: {
    slickNext() {
      this.$refs.carousel.next();
    },
    slickPrev() {
      this.$refs.carousel.prev();
    },
    slickGoTo(args, dontAnimate) {
      let slideNumber;
      if (typeof args === 'object' && args.eventParams) {
        // 通过组件事件派发传递的
        slideNumber = args.eventParams.slideNumber;
        dontAnimate = args.eventParams.dontAnimate;
      } else {
        slideNumber = args;
      }
      let dont = false;
      if (dontAnimate === 'true' || dontAnimate === true) {
        dont = true;
      }
      this.$refs.carousel.goTo(slideNumber, dont);
    },
    beforeChange(from, to) {
      if (!from) {
        from = this.$refs.carousel.$children[0].$children[0].currentSlide;
      }
      this.pageContext.emitEvent(`${this.widget.id}:beforeChange`, { from, to });
    },
    setItemButtonGroup(item) {
      if (item.buttonGroup) {
        item.buttonGroup.dynamicGroupName = this.$t(`${item.id}_dynamicGroupName`, item.buttonGroup.dynamicGroupName);
      }
      return {
        enable: item.buttonEnable,
        buttons: item.buttons,
        buttonGroup: item.buttonGroup
      };
    }
  }
};
</script>

<style lang="less">
.widget-carousel {
  .slick-slide {
    > div {
      font-size: 0;
    }
    .widget-carousel-item {
      font-size: var(--w-font-size);
    }
  }
}
.ant-carousel .custom-slick-arrow {
  width: 25px;
  height: 25px;
  font-size: 25px;
  color: #fff;
  background-color: rgba(31, 45, 61, 0.11);
  opacity: 0.3;
}
</style>
