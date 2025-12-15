<template>
  <view :style="theme" class="subform-data-table-view">
    <view class="uni-container">
      <uni-table class="table" ref="table" :loading="false" border stripe emptyText="">
        <uni-tr>
          <uni-th align="center" width="50">序号</uni-th>
          <uni-th v-for="(field, index1) in fields" :key="index1" align="center">{{
            field.tagName || field.displayName
          }}</uni-th>
        </uni-tr>
        <uni-tr v-for="(item, index2) in dataList" :key="index2">
          <uni-td align="center">{{ index2 + 1 }}</uni-td>
          <uni-td v-for="(field, index3) in fields" :key="index3" align="center">{{ item[field.name] }}</uni-td>
        </uni-tr>
      </uni-table>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      fields: [],
      dataList: [],
    };
  },
  onLoad() {
    var _self = this;
    var subformDefinition = _self.getPageParameter("subformDefinition");
    var dataList = _self.getPageParameter("dataList");
    _self.fields = subformDefinition.fieldList || subformDefinition.fields;
    // console.log(JSON.stringify(subformDefinition));
    _self.dataList = dataList;
    uni.setNavigationBarTitle({ title: subformDefinition.displayName || subformDefinition.name });
  },
  methods: {},
};
</script>

<style lang="scss" scoped>
.subform-data-table-view {
  width: 100%;
  color: $uni-text-color;
  background-color: $uni-bg-color;

  .uni-container {
    overflow: auto;
  }

  .table {
    ::v-deep .uni-table {
      background-color: $uni-bg-secondary-color;
    }

    .uni-table-th {
      color: $uni-text-color;
      background-color: $uni-bg-secondary-color;
    }

    .uni-table-tr:nth-child(2n + 3) {
      background-color: $uni-bg-secondary-color;
    }
    .uni-table-td {
      color: $uni-text-color;
    }
  }
}
</style>
