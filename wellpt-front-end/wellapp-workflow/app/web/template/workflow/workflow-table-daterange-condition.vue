<template>
  <div>
    <a-tag v-for="item in dateRangeOptions" :key="item.value" @click="onItemClick(item)" :class="{ selected: item.selected }">
      {{ item.label }}
    </a-tag>
  </div>
</template>

<script>
import moment from 'moment';
export default {
  props: {
    parentWidget: Function
  },
  data() {
    return {
      dateRangeOptions: [
        { label: '最近一周', value: 7, selected: true },
        { label: '最近一个月', value: 30 },
        { label: '最近三个月', value: 90 },
        { label: '最近半年', value: 180 },
        { label: '最近一年', value: 365 },
        { label: '全部', value: -1 }
      ]
    };
  },
  created() {
    this.addDateRangeCondition('startTime', 7);
  },
  methods: {
    onItemClick(item) {
      const _this = this;
      _this.dateRangeOptions.forEach(dataItem => {
        dataItem.selected = false;
      });
      item.selected = true;

      let tableWidget = _this.addDateRangeCondition('startTime', item.value);
      tableWidget.$refs.searchForm.handleKeywordSearch(1);
      // tableWidget.refetch(true);
    },
    addDateRangeCondition(columnIndex, dateRange) {
      const _this = this;
      if (!_this.parentWidget) {
        return;
      }
      let tableWidget = _this.parentWidget();
      if (dateRange != -1) {
        let toDate = moment();
        let fromDate = moment().subtract(dateRange, 'days');
        tableWidget.clearOtherConditions(_this.dateRangeCondition);
        _this.dateRangeCondition = {
          columnIndex,
          value: [fromDate, toDate],
          type: 'between'
        };
        tableWidget.addOtherConditions([_this.dateRangeCondition]);
      } else {
        tableWidget.clearOtherConditions();
      }
      return tableWidget;
    }
  }
};
</script>

<style lang="less" scoped>
.selected {
  color: var(--w-primary-color);
}
</style>
