<template>
  <div class="workTimePlan-table-title">
    <template v-if="row.isDefault == '1'">
      <i class="workTime-default-icon iconfont icon-ptkj-moren"></i>
      <template v-if="history">
        <i class="iconfont icon-ptkj-lishi" :title="history" style="color: var(--w-primary-color)"></i>
      </template>
      {{ text }}v{{ row.version }}
    </template>
    <template v-else>
      <template v-if="history">
        <i class="iconfont icon-ptkj-lishi" :title="history" style="color: var(--w-primary-color)"></i>
      </template>
      {{ text }}v{{ row.version }}
    </template>
  </div>
</template>

<script type="text/babel">
// 工作时间方案列表，名称渲染
export default {
  name: 'WorkingTimePlanTableNameRender',
  props: {
    text: String,
    row: {
      type: Object,
      default: {} // edit、label
    }
  },
  components: {},
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      history: ''
    };
  },
  watch: {},
  beforeCreate() {},
  created() {},
  beforeMount() {
    let _this = this;
  },
  mounted() {
    this.listNewVersionTipByUuids();
  },
  methods: {
    listNewVersionTipByUuids() {
      $axios.get('/api/ts/work/time/plan/listNewVersionTipByUuids', { params: { uuids: this.row.uuid } }).then(({ data }) => {
        if (data.code == 0) {
          this.history = data.data && data.data[this.row.uuid] ? data.data[this.row.uuid] : '';
        }
      });
    }
  }
};
</script>
<style lang="less" scoped>
.workTimePlan-table-title {
  position: relative;
  .workTime-default-icon {
    position: absolute;
    top: -16px;
    left: -16px;
    font-size: 30px !important;
    color: var(--w-primary-color);
    line-height: 1;
  }
}
</style>
