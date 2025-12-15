<template>
  <view class="item-card-body">
    <view v-if="configuration.selectable && rowData.extraIcon" class="item-card-icon uni-pt-2">
      <uni-icons :color="rowData.extraIcon.color" :size="rowData.extraIcon.size" :type="rowData.extraIcon.type" />
    </view>
    <rich-text v-if="configuration.itemContentType == '2'" :nodes="renderCustomRowContent(rowData)"></rich-text>
    <view class="item-card-body-content" v-else>
      <view class="item-card-title uni-pt-2 uni-pb-3 uni-mb-4">
        <text>{{ rowData.title }}</text>
      </view>
      <template v-for="(templateProperty, ctIndex) in contentTemplateProperties">
        <view class="uni-flex uni-row" :class="{ unread: rowData.isUnread }" :key="ctIndex">
          <view class="uni-mr-4 content-label">{{ templateProperty.title }} </view>
          <view>{{ rowData[templateProperty.name] }}</view>
        </view>
      </template>
    </view>
  </view>
</template>

<script>
import mixin from "./item-body-mixin";
export default {
  mixins: [mixin],
  computed: {
    contentTemplateProperties: function () {
      let properties = [];
      _.each(this.configuration.templateProperties, function (property) {
        if (property.name == "title") {
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
  position: relative;
  /* #ifndef APP-NVUE */
  display: flex;
  /* #endif */
  flex-direction: row;

  .item-card-body-content {
    flex: 1;
  }

  .item-card-title {
    font-size: $uni-font-size-lg;
  }

  .item-card-icon {
    margin-right: 12rpx;
    flex-direction: row;
    justify-content: center;
    align-items: center;
  }

  .unread {
    font-weight: bold;
  }
}
</style>
