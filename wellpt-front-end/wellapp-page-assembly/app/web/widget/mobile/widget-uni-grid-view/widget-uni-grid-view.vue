<template>
  <div :class="['widget-uni-grid-view', widget.configuration.bordered === false ? 'no-bordered' : '']">
    <template v-if="groups.groupItems.length == 1">
      <template v-for="(groupItem, g) in groups.groupItems">
        <a-row :key="'group' + g">
          <template v-for="(item, i) in groupItem">
            <a-col class="uni-grid-item" :key="'group_' + g + '_item_' + i" :span="24 / numColumns">
              <WidgetUniGridItem :configuration="widget.configuration" :item="item" />
            </a-col>
          </template>
        </a-row>
      </template>
    </template>
    <template v-else-if="groups.groupItems.length > 1">
      <a-carousel :after-change="afterCarouselChange" :dots="true">
        <div v-for="(groupItem, g) in groups.groupItems" style="margin-bottom: 30px">
          <a-row :key="'group' + g">
            <template v-for="(item, i) in groupItem">
              <a-col class="uni-grid-item" :key="'group_' + g + '_item_' + i" :span="24 / numColumns">
                <WidgetUniGridItem :configuration="widget.configuration" :item="item" />
              </a-col>
            </template>
          </a-row>
        </div>
      </a-carousel>
    </template>
  </div>
</template>

<script type="text/babel">
import editWgtMixin from '@framework/vue/designer/editWidgetMixin';
import WidgetUniGridItem from './components/widget-uni-grid-item';

export default {
  name: 'WidgetUniGridView',
  mixins: [editWgtMixin],
  data() {
    return {};
  },
  watch: {},
  beforeCreate() {},
  components: { WidgetUniGridItem },
  computed: {
    wDisplayAsReadonly() {
      return false;
    },
    numColumns: function () {
      return Number(this.widget.configuration.numColumns);
    },
    groups: function () {
      let configuration = this.widget.configuration;
      let numColumns = Number(configuration.numColumns);
      let sliderGroups = this.getSliderGroups(configuration.columns, numColumns, configuration);
      return sliderGroups;
    }
  },
  created() {},
  methods: {
    afterCarouselChange() {},
    getSliderGroups: function (rawColumns, numColumns, config) {
      var columns = [];
      for (var i = 0; i < rawColumns.length; i++) {
        var column = rawColumns[i];
        if (column.hidden || column.hidden == '1') {
          continue;
        }
        columns.push(column);
      }
      var groups = {
        groupItems: [],
        totalCount: 0,
        groupCount: 0
      };
      var totalCount = columns.length;
      groups.totalCount = totalCount;
      let swiperItemRowCount = config.swiperItemRowCount,
        useSwiper = this.widget.configuration.enableSwiper;
      if (useSwiper) {
        // 要判断单元格数目是否满足滑动需要
        if (totalCount <= numColumns * swiperItemRowCount) {
          useSwiper = false;
        }
      }
      if (!useSwiper) {
        groups.groupItems.push(columns);
        groups.groupCount = 1;
      } else {
        for (var i = 0; i < columns.length; i++) {
          var column = columns[i];
          var groupIndex = parseInt(i / (swiperItemRowCount * numColumns));
          if (groups.groupItems[groupIndex] == null) {
            groups.groupItems[groupIndex] = [];
          }
          groups.groupCount = groupIndex + 1;
          groups.groupItems[groupIndex].push(column);
        }
      }

      console.log(groups);
      return groups;
    }
  },
  mounted() {}
};
</script>
<style lang="less">
.widget-uni-grid-view {
  border-top: 1px solid #d2d2d2;
  border-left: 1px solid #d2d2d2;
  &.no-bordered {
    border: unset;
    .uni-grid-item {
      border: unset;
    }
  }

  .ant-carousel {
    .slick-dots li {
      > button {
        background: rgba(0, 0, 0, 0.3);
        width: 8px;
        height: 8px;
        border-radius: 50%;
      }
      &.slick-active button {
        background-color: rgb(0, 0, 0);
      }
    }
  }
}
</style>
