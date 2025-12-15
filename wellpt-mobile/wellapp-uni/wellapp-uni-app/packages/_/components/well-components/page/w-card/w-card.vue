<template>
  <uni-card
    :class="['w-card', 'w-card-' + widget.configuration.size, widgetClass, widget.configuration.code]"
    :is-shadow="true"
    v-show="vShow"
    :border="widget.configuration.bordered"
    :margin="cardStyle.margin"
    :spacing="cardStyle.spacing"
    :padding="cardStyle.padding"
    :key="'w-card_' + widget.id"
  >
    <template v-if="!hideHeader">
      <template slot="title" v-if="!widget.configuration.hideTitle || widget.configuration.headerButton.enable">
        <uni-section
          :title="widget.configuration.hideTitle ? '' : $t('title', widget.configuration.title)"
          :sub-title="widget.configuration.hideSubTitle ? '' : $t('subTitle', widget.configuration.subTitle)"
          :titleFontSize="cardStyle.titleFontSize"
          :subTitleFontSize="cardStyle.subTitleFontSize"
        >
          <template v-slot:decoration v-if="widget.configuration.titleIcon">
            <w-icon
              :icon="widget.configuration.titleIcon"
              :size="cardStyle.iconSize"
              iconStyle="margin-right:8px"
            ></w-icon>
          </template>
          <template v-slot:right v-if="widget.configuration.headerButton.enable">
            <w-button
              :button="widget.configuration.headerButton"
              :developJsInstance="developJsInstance"
              :parentWidget="getSelf"
            ></w-button>
            <!-- <text @click="onPopupButtons" style="color: var(--color-primary)">操作</text> -->
          </template>
        </uni-section>
      </template>
    </template>
    <view :style="{ height: bodyHeight }" :class="{ bodyScroll: bodyHeight !== 'auto' }">
      <view v-for="(wgt, w) in widget.configuration.widgets" :key="w + 1">
        <!-- <view>{{wgt.id}}</view> -->
        <!-- 			<w-card v-if="wgt.wtype == 'WidgetCard'" :widget="wgt" :parent="widget" :key="wgt.id"></w-card> -->
        <!-- <w-tabs v-else-if="wgt.wtype == 'WidgetUniTab' || wgt.wtype == 'WidgetTab'" :widget="wgt"
				:parent="widget"></w-tabs> -->

        <!-- <w-loop-widget :widget="wgt" :parent="widget" :key="wgt.id"></w-loop-widget> -->
        <!-- <widget :widget="wgt" :parent="widget" :key="wgt.id"></widget> -->
        <widget :widget="wgt" :parent="widget" :key="'card_' + (w + 1)"></widget>
      </view>
    </view>
    <!-- <w-button-action-sheet :button="widget.configuration.headerButton.buttons" ref="buttonSheet" /> -->
  </uni-card>
</template>

<script>
import mixin from "../page-widget-mixin";
export default {
  mixins: [mixin],
  name: "w-card",
  data() {
    return {
      smallSize: {
        margin: "0px",
        padding: "6px",
        spacing: "6px",
        titleFontSize: "14px",
        subTitleFontSize: "12px",
        iconSize: 14,
      },
      defaultSize: {
        margin: "0px",
        padding: "10px",
        spacing: "10px",
        titleFontSize: "16px",
        subTitleFontSize: "14px",
        iconSize: 16,
      },
      largeSize: {
        margin: "0px",
        padding: "14px",
        spacing: "14px",
        titleFontSize: "18px",
        subTitleFontSize: "16px",
        iconSize: 18,
      },
    };
  },
  components: {
    /* 			'w-loop-widget': () => import('../w-widget/w-loop-widget.vue')
     */
    /* 			'w-card': () => import('../w-card/w-card.vue')
     */
  },
  created() {},
  mounted: function () {},
  computed: {
    cardStyle() {
      let size = this[(this.widget.configuration.size || "default") + "Size"];
      if (this.widget.configuration.uniConfiguration) {
        if (
          this.widget.configuration.uniConfiguration.hideCard ||
          this.widget.configuration.uniConfiguration.hideCardBody
        ) {
          size.margin = "0px";
          size.padding = "0px";
          size.spacing = "0px";
        }
      }
      return size;
    },
    hideHeader() {
      if (this.widget.configuration.uniConfiguration) {
        if (this.widget.configuration.uniConfiguration.hideCard) {
          return true;
        } else if (this.widget.configuration.uniConfiguration.hideCardHeader) {
          return true;
        }
      }
      return false;
    },
    bodyHeight() {
      if (this.widget.configuration.height == "auto" || this.widget.configuration.height == undefined) {
        return "auto";
      }
      let padding = this.widget.configuration.style.padding[0] + this.widget.configuration.style.padding[2];
      if (this.cardStyle.padding == "0px") {
        padding = 0;
      }
      let borderHeight = 0;
      if (this.widget.configuration.bordered) {
        borderHeight = 2;
      }
      return `calc( ${this.widget.configuration.height} - ${padding}px - ${borderHeight}px )`;
    },
  },
  methods: {
    onPopupButtons() {
      this.$refs.buttonSheet.show = true;
    },
    getSelf() {
      return this;
    },
  },
};
</script>
<style lang="scss" scoped>
.w-card {
  > .uni-section {
    border-bottom: 1px solid var(--w-border-color-light);

    ::v-deep .uni-section-header__content {
      min-width: 100px;

      .uni-section__content-title {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
  .bodyScroll {
    overflow: auto;
    > * {
      height: 100%;
      > * {
        height: 100%;
      }
    }
  }
}
</style>
