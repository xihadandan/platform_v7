<template>
  <div class="template-card-view">
    <a-row type="flex">
      <a-col :span="6" flex="80px">
        <Icon :type="row.icon || defaultIcon" style="font-size: 75px"></Icon>
      </a-col>
      <a-col :span="18" flex="auto">
        <div class="title">{{ row.name }}</div>
        <div class="remark" :title="row.remark">{{ row.remark }}</div>
      </a-col>
    </a-row>
    <a-row>
      <a-col :span="18">
        <span>创建时间：{{ formatDate(row.createTime) }}</span>
        <span>使用次数：{{ row.usedCount || 0 }}</span>
      </a-col>
      <a-col :span="6" class="actions">
        <slot name="actions" />
      </a-col>
    </a-row>
    <div class="selection"><slot name="selection" /></div>
  </div>
</template>

<script>
import moment from 'moment';
export default {
  props: {
    row: Object,
    eventWidget: Function,
    rowIndex: Number
  },
  data() {
    return {
      defaultIcon:
        '{"iconClass":"iconfont icon-ptkj-xiangmuku-01","shape":"rect","style":"outlined","color":"#69A0EE","showBackground":false,"cssStyle":{"color":"#69A0EE","borderRadius":"20%"}}'
    };
  },
  methods: {
    formatDate(date) {
      return moment(date).format('YYYY-MM-DD');
    }
  }
};
</script>

<style lang="less" scoped>
.template-card-view {
  position: relative;
  padding: 12px;
  .title {
    font-size: var(--w-font-size-2xl);
    font-weight: var(--w-font-weight-semibold);
  }
  .remark {
    padding: 12px 0;
    height: 75px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    line-clamp: 3;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
  }
  .actions {
    text-align: right;
  }

  .selection {
    position: absolute;
    top: 12px;
    right: 12px;
  }
  &:hover {
    .selection {
      display: block;
    }
  }
}
</style>
