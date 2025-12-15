<template>
  <view v-if="configuration.itemContentType == '2'">
    <rich-text
      class="list-item-body"
      v-if="renderCustomRowContent(rowData)"
      :nodes="renderCustomRowContent(rowData)"
    ></rich-text>
  </view>
  <view v-else class="list-item-body">
    <view
      class="list-item-body uni-list-item__content"
      :class="{ 'uni-list-item__content--center': configuration.selectable }"
    >
      <view class="uni-list-item__content-body">
        <view
          class="uni-list-item__content-title"
          v-if="rowData['property_title'] || rowData.clientRendererOptions.title"
        >
          <BodyItemRender
            :className="propertyMap['name_title'].ellipsisClass"
            :rowData="rowData"
            :rowIndex="rowIndex"
            name="title"
            :iconSize="iconSizeFormat(styleConfiguration.listTitleSize, 16)"
          ></BodyItemRender>
        </view>
        <view
          class="uni-list-item__content-subtitle"
          v-if="rowData['property_subtitle'] || rowData.clientRendererOptions.subtitle"
        >
          <BodyItemRender
            :className="propertyMap['name_subtitle'].ellipsisClass"
            :rowData="rowData"
            :rowIndex="rowIndex"
            name="subtitle"
            :iconSize="iconSizeFormat(styleConfiguration.listSubTitleSize)"
          ></BodyItemRender>
        </view>
        <view class="uni-list-item__content-note" v-if="rowData['property_note'] || rowData.clientRendererOptions.note">
          <BodyItemRender
            :className="propertyMap['name_note'].ellipsisClass"
            :rowData="rowData"
            :rowIndex="rowIndex"
            name="note"
            :iconSize="iconSizeFormat(styleConfiguration.listNoteSize, 12)"
          ></BodyItemRender>
        </view>
      </view>
      <view
        class="uni-list-item__content-extra"
        v-if="rowData['property_rightText'] || rowData.clientRendererOptions.rightText"
      >
        <BodyItemRender
          :className="'uni-list-item__content-extra-text ' + propertyMap['name_rightText'].ellipsisClass"
          :rowData="rowData"
          :rowIndex="rowIndex"
          name="rightText"
          :iconSize="iconSizeFormat(styleConfiguration.listExtraSize)"
        ></BodyItemRender>
      </view>
    </view>
    <view class="list-item-footer" v-if="hasBottom">
      <view class="uni-flex uni-row list-item-footer-content" v-if="hasBottomData">
        <view class="uni-mr-4 content-label">
          <BodyItemRender
            v-if="rowData['property_bottomLeft'] || rowData.clientRendererOptions.bottomLeft"
            :className="propertyMap['name_bottomLeft'].ellipsisClass"
            :rowData="rowData"
            :rowIndex="rowIndex"
            name="bottomLeft"
            :iconSize="iconSizeFormat(styleConfiguration.listContentLabelSize)"
          ></BodyItemRender>
        </view>
        <view class="content-value">
          <BodyItemRender
            v-if="rowData['property_bottomRight'] || rowData.clientRendererOptions.bottomRight"
            :className="propertyMap['name_bottomRight'].ellipsisClass"
            :rowData="rowData"
            :rowIndex="rowIndex"
            name="bottomRight"
            :iconSize="iconSizeFormat(styleConfiguration.listContentValueSize)"
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
</template>

<script>
import mixin from "./item-body-mixin";
import BodyItemRender from "./body-item-render.vue";
export default {
  mixins: [mixin],
  components: { BodyItemRender },
  methods: {},
};
</script>

<style lang="scss" scoped>
.list-item-body {
  --list-title-size: var(--w-font-size-md);
  --list-title-color: var(--w-text-color-mobile);
  --list-title-weight: bold;
  --list-title-padding: 0;
  --list-title-margin: 0;

  --list-subtitle-size: var(--w-font-size-base);
  --list-subtitle-color: var(--w-text-color-base);
  --list-subtitle-weight: normal;
  --list-subtitle-padding: 0;
  --list-subtitle-margin: var(--w-margin-3xs) 0 0;

  --list-note-size: var(--w-font-size-sm);
  --list-note-color: var(--w-text-color-light);
  --list-note-weight: normal;
  --list-note-padding: 0;
  --list-note-margin: var(--w-margin-3xs) 0 0;

  --list-extra-size: var(--w-font-size-base);
  --list-extra-color: var(--w-text-color-light);
  --list-extra-weight: normal;
  --list-extra-padding: 0;
  --list-extra-margin: 0;

  --list-extra-width: 100px;

  --list-content-label-size: var(--w-font-size-base);
  --list-content-label-color: var(--w-text-color-base);
  --list-content-label-weight: normal;
  --list-content-value-size: var(--w-font-size-base);
  --list-content-value-color: var(--w-text-color-mobile);
  --list-content-value-weight: normal;
  --list-content-justify: space-between;
  --list-content-value-justify: normal;
  --list-content-padding: var(--w-padding-3xs) 0 0;

  --list-footer-margin: var(--w-padding-xs) 0 0;
  --list-footer-padding: var(--w-padding-2xs) 0 0;
  --list-footer-border-color: transparent;

  --item-footer-button-hr-color: var(--w-border-color-light);
  --item-footer-button-hr-height: 18px;
  --item-footer-button-padding: 0;

  width: 100%;
  word-break: break-all;

  .list-item-footer {
    .list-item-footer-content {
      display: flex;
      align-items: center;
      justify-content: var(--list-content-justify);
      margin: var(--list-footer-margin);
      padding: var(--list-footer-padding);
      border-top: 1px solid var(--list-footer-border-color);
      .content-label {
        min-width: 90px;
        font-size: var(--list-content-label-size);
        color: var(--list-content-label-color);
        font-weight: var(--list-content-label-weight);
      }
      .content-value {
        font-size: var(--list-content-value-size);
        color: var(--list-content-value-color);
        font-weight: var(--list-content-value-weight);
        width: 100%;
        > * {
          justify-content: var(--list-content-value-justify);
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
  }
}
.uni-list-item__content {
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex: 1;
  color: var(--list-title-color);
  // overflow: hidden;
  flex-direction: row;
  justify-content: space-between;
  overflow: hidden;
}

.uni-list-item__content-body {
  flex-grow: 1;
}
.uni-list-item__content-extra {
  display: flex;
  align-items: center;
  padding-left: 12px;
  flex-shrink: 0;
  max-width: var(--list-extra-width);
  .uni-list-item__content-extra-text {
    font-size: var(--list-extra-size);
    color: var(--list-extra-color);
    font-weight: var(--list-extra-weight);
    padding: var(--list-extra-padding);
    margin: var(--list-extra-margin);
  }
}

.uni-list-item__content--center {
  justify-content: center;
}

.uni-list-item__content-title {
  font-size: var(--list-title-size);
  color: var(--list-title-color);
  font-weight: var(--list-title-weight);
  padding: var(--list-title-padding);
  margin: var(--list-title-margin);
  overflow: hidden;
}

.uni-list-item__content-subtitle {
  font-size: var(--list-subtitle-size);
  color: var(--list-subtitle-color);
  font-weight: var(--list-subtitle-weight);
  padding: var(--list-subtitle-padding);
  margin: var(--list-subtitle-margin);
  overflow: hidden;
}

.uni-list-item__content-note {
  font-size: var(--list-note-size);
  color: var(--list-note-color);
  font-weight: var(--list-note-weight);
  padding: var(--list-note-padding);
  margin: var(--list-note-margin);
  overflow: hidden;
}

.unread {
  font-weight: bold;
}
</style>
