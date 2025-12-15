<template>
  <view class="item-card-body">
    <template v-if="configuration.itemContentType == '2'">
      <rich-text v-if="renderCustomRowContent(rowData)" :nodes="renderCustomRowContent(rowData)"></rich-text>
    </template>
    <view class="item-card-body-content" v-else>
      <view
        class="item-card-title flex"
        v-if="
          rowData['property_title'] ||
          rowData.clientRendererOptions.title ||
          rowData['property_rightText'] ||
          rowData.clientRendererOptions.rightText
        "
      >
        <view class="f_g_1" v-if="rowData['property_title'] || rowData.clientRendererOptions.title">
          <BodyItemRender
            :className="propertyMap['name_title'].ellipsisClass"
            :rowData="rowData"
            :rowIndex="rowIndex"
            name="title"
            :iconSize="iconSizeFormat(styleConfiguration.cardTitleSize, 16)"
          ></BodyItemRender>
          <view
            class="item-card-subtitle"
            v-if="rowData['property_subtitle'] || rowData.clientRendererOptions.subtitle"
          >
            <BodyItemRender
              :className="propertyMap['name_subtitle'].ellipsisClass"
              :rowData="rowData"
              :rowIndex="rowIndex"
              name="subtitle"
              :iconSize="iconSizeFormat(styleConfiguration.cardSubTitleSize)"
            ></BodyItemRender>
          </view>
        </view>
        <view
          class="f_s_0 item-card-extra"
          v-if="rowData['property_rightText'] || rowData.clientRendererOptions.rightText"
        >
          <BodyItemRender
            :className="propertyMap['name_rightText'].ellipsisClass"
            :rowData="rowData"
            :rowIndex="rowIndex"
            name="rightText"
            :iconSize="iconSizeFormat(styleConfiguration.cardSubTitleSize)"
          ></BodyItemRender>
        </view>
      </view>
      <template v-for="(templateProperty, ctIndex) in contentTemplateProperties">
        <view class="uni-flex uni-row item-card-content" :class="{ unread: rowData.isUnread }" :key="ctIndex">
          <view class="uni-mr-4 content-label">
            <w-icon
              style="margin-right: 5px"
              v-if="templateProperty.icon"
              :iconConfig="templateProperty.icon"
              :size="iconSizeFormat(styleConfiguration.cardSubTitleSize, undefined, templateProperty.icon)"
            ></w-icon>
            {{ $t(templateProperty.uuid, templateProperty.title) }}
          </view>
          <view class="content-value">
            <BodyItemRender
              :className="propertyMap[templateProperty.mapColumn].ellipsisClass"
              :rowData="rowData"
              :rowIndex="rowIndex"
              name="rightText"
              :name="templateProperty.name"
              :mapColumn="templateProperty.mapColumn"
            ></BodyItemRender>
          </view>
        </view>
      </template>
      <view v-if="hasBottom" class="item-card-footer">
        <view
          v-if="hasBottomData"
          class="uni-flex uni-row item-card-footer-content"
          :class="{ unread: rowData.isUnread }"
          :key="ctIndex"
        >
          <view
            class="uni-mr-4 content-label"
            v-if="rowData['property_bottomLeft'] || rowData.clientRendererOptions.bottomLeft"
          >
            <BodyItemRender
              :className="propertyMap['name_bottomLeft'].ellipsisClass"
              :rowData="rowData"
              :rowIndex="rowIndex"
              name="bottomLeft"
              :iconSize="iconSizeFormat(styleConfiguration.cardContentLabelSize)"
            ></BodyItemRender>
          </view>
          <view
            class="content-value footer-content-value"
            v-if="rowData['property_bottomRight'] || rowData.clientRendererOptions.bottomRight"
          >
            <BodyItemRender
              :className="propertyMap['name_bottomRight'].ellipsisClass"
              :rowData="rowData"
              :rowIndex="rowIndex"
              name="bottomRight"
              :iconSize="iconSizeFormat(styleConfiguration.cardContentValueSize)"
            ></BodyItemRender>
          </view>
        </view>
        <view class="item-footer-buttons" v-if="rowBottomButtons.length" @click.stop>
          <uni-w-button-group
            style="width: 100%"
            :buttons="rowBottomButtons"
            :gutter="
              styleConfiguration.itemFooterButtonGutter || styleConfiguration.itemFooterButtonGutter === 0
                ? styleConfiguration.itemFooterButtonGutter * 2
                : 32
            "
            :max="rowBottomButtons.length < 3 ? 0 : 3"
            :moreButton="moreButton"
            @click.stop="buttonClick"
          ></uni-w-button-group>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import mixin from "./item-body-mixin";
import { each } from "lodash";
import BodyItemRender from "./body-item-render.vue";

export default {
  mixins: [mixin],
  components: { BodyItemRender },
  computed: {
    contentTemplateProperties: function () {
      let properties = [];
      each(this.configuration.templateProperties, function (property) {
        if (property.name && property.name !== "note") {
          return;
        }
        properties.push(property);
      });
      return properties;
    },
  },
  methods: {},
};
</script>

<style lang="scss" scoped>
.item-card-body {
  --card-title-size: var(--w-font-size-md);
  --card-title-color: var(--w-text-color-mobile);
  --card-title-weight: bold;
  --card-title-border-color: var(--w-border-color-mobile);
  --card-title-padding: 0 0 var(--w-padding-xs);
  --card-title-margin: 0 0 var(--w-margin-3xs);
  --card-content-label-width: 100px;
  --card-content-label-size: var(--w-font-size-base);
  --card-content-label-color: var(--w-text-color-base);
  --card-content-label-weight: normal;
  --card-content-value-size: var(--w-font-size-base);
  --card-content-value-color: var(--w-text-color-mobile);
  --card-content-value-weight: normal;
  --card-content-value-align: left;
  --card-content-justify: normal;
  --card-content-value-justify: normal;
  --card-content-padding: var(--w-padding-3xs) 0 0;

  --card-footer-margin: var(--w-padding-xs) 0 0;
  --card-footer-padding: var(--w-padding-2xs) 0 0;
  --card-footer-border-color: transparent;

  --card-subtitle-size: var(--w-font-size-base);
  --card-subtitle-color: var(--w-text-color-base);
  --card-subtitle-weight: normal;
  --card-subtitle-padding: 0;
  --card-subtitle-margin: var(--w-margin-3xs) 0 0;

  --card-extra-size: var(--w-font-size-base);
  --card-extra-color: var(--w-text-color-light);
  --card-extra-weight: normal;
  --card-extra-padding: 0;
  --card-extra-margin: 0;
  --card-extra-width: unset;

  --item-footer-button-hr-color: var(--w-border-color-light);
  --item-footer-button-hr-height: 18px;
  --item-footer-button-padding: 0;

  color: var(--w-text-color-base);
  position: relative;
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: row;
  color: var(--w-text-color-base);
  word-break: break-all;

  .item-card-body-content {
    flex: 1;
  }

  .item-card-title {
    font-size: var(--card-title-size);
    color: var(--card-title-color);
    border-bottom: 1px solid var(--card-title-border-color);
    font-weight: var(--card-title-weight);
    padding: var(--card-title-padding);
    margin: var(--card-title-margin);
  }

  .item-card-subtitle {
    font-size: var(--card-subtitle-size);
    color: var(--card-subtitle-color);
    font-weight: var(--card-subtitle-weight);
    padding: var(--card-subtitle-padding);
    margin: var(--card-subtitle-margin);
  }

  .item-card-extra {
    font-size: var(--card-extra-size);
    color: var(--card-extra-color);
    font-weight: var(--card-extra-weight);
    padding: var(--card-extra-padding);
    margin: var(--card-extra-margin);
    max-width: var(--card-extra-width);
  }
  .content-label {
    min-width: 90px;
    width: var(--card-content-label-width);
    font-size: var(--card-content-label-size);
    color: var(--card-content-label-color);
    font-weight: var(--card-content-label-weight);
  }
  .content-value {
    font-size: var(--card-content-value-size);
    color: var(--card-content-value-color);
    font-weight: var(--card-content-value-weight);
    text-align: var(--card-content-value-align);
  }
  .item-card-content {
    padding: var(--card-content-padding);
    justify-content: var(--card-content-justify);
  }
  .item-card-footer {
    align-items: center;
    margin: var(--card-footer-margin);
    padding: var(--card-footer-padding);
    border-top: 1px solid var(--card-footer-border-color);
    justify-content: var(--card-content-justify);

    .footer-content-value {
      width: 100%;
      > * {
        justify-content: var(--card-content-value-justify);
      }
    }
  }
  .item-footer-buttons {
    padding: var(--item-footer-button-padding);
    .uni-w-button-group-container {
      ::v-deep .uni-w-button-group {
        .uni-w-button-group__item {
          position: relative;
          &::after {
            position: absolute;
            content: "";
            width: 1px;
            height: var(--item-footer-button-hr-height);
            background-color: var(--item-footer-button-hr-color);
            right: 0;
            top: 50%;
            margin-top: calc(0px - var(--item-footer-button-hr-height) / 2);
          }
          &:last-child::after {
            content: none;
          }
        }
      }
    }
  }

  .unread {
    font-weight: bold;
  }
}
</style>
