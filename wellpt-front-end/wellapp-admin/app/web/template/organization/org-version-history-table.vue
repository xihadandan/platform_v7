<template>
  <a-table :data-source="rows" :columns="columns" rowKey="uuid" :pagination="false" class="pt-table org-history-table">
    <template slot="stateSlot" slot-scope="text, record">
      <span v-if="text == 'PUBLISHED'" :class="text">正式版</span>
      <span v-if="text == 'DESIGNING'" :class="text">设计版</span>
      <span v-if="text == 'HISTORY'" :class="text">历史版 v{{ record.ver }}</span>
    </template>
    <template slot="effectTimeSlot" slot-scope="text, record">
      <span :class="record.state">{{ text ? text : '-' }}</span>
    </template>
    <template slot="invalidTimeSlot" slot-scope="text, record">
      <span :class="record.state">{{ text ? text : '-' }}</span>
    </template>
    <template slot="operationSlot" slot-scope="text, record, index">
      <a-button size="small" type="link" @click="detail(record)">
        <Icon type="pticon iconfont icon-szgy-zonghechaxun" />
        查看
      </a-button>
      <a-button size="small" type="link" v-if="record.state == 'DESIGNING'" @click="deleteVersion(record, index)">
        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        删除
      </a-button>
    </template>
  </a-table>
</template>
<style lang="less">
.org-history-table {
  .PUBLISHED {
    color: var(--w-success-color);
  }
  .DESIGNING {
    color: var(--w-warning-color);
  }
}
</style>
<script type="text/babel">
export default {
  name: 'OrgVersionHistoryTable',
  props: {
    orgUuid: String,
    orgId: String
  },
  components: {},
  computed: {},
  data() {
    return {
      rows: [],
      columns: [
        {
          title: '状态',
          dataIndex: 'state',
          scopedSlots: { customRender: 'stateSlot' }
        },
        {
          title: '生效时间',
          dataIndex: 'effectTime',
          scopedSlots: { customRender: 'effectTimeSlot' }
        },
        {
          title: '失效时间',
          dataIndex: 'invalidTime',
          scopedSlots: { customRender: 'invalidTimeSlot' }
        },
        {
          title: '操作',
          width: 180,
          scopedSlots: { customRender: 'operationSlot' }
        }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchHistory();
  },
  mounted() {},
  methods: {
    deleteVersion(item, index) {
      let _this = this;
      this.$confirm({
        title: '确认要删除设计版吗?',
        content: undefined,
        onOk() {
          _this.$loading('删除中');
          $axios
            .get('/proxy/api/org/organization/version/delete', { params: { uuid: item.uuid } })
            .then(({ data }) => {
              _this.$loading(false);
              if (data.code == 0) {
                _this.$message.success('删除成功');
                _this.rows.splice(index, 1);
                _this.detail(_this.rows[0]);
              }
            })
            .catch(error => {
              _this.$loading(false);
              _this.$message.error('删除失败');
            });
        },
        onCancel() {}
      });
    },
    fetchHistory() {
      $axios
        .get(`/proxy/api/org/organization/allVersion`, { params: { orgUuid: this.orgUuid, orgId: this.orgId } })
        .then(({ data }) => {
          if (data.data) {
            this.rows = data.data;
          }
        })
        .catch(error => {});
    },
    detail(item) {
      this.$emit('watch-version-detail', item);
    }
  }
};
</script>
