<template>
  <div style="position: relative">
    <div v-html="compareResult"></div>
    <a-card title="数据变更" :bordered="false">
      <a-table :columns="fieldChangeTableColumns" :data-source="dataItemRows" :pagination="false" :loading="dataItemLoading">
        <template slot="dateItemNameSlot" slot-scope="text, record">
          <div>
            <div>{{ text }}</div>
            <a-tag class="primary-color" v-if="record.DATA_ITEM_NAME !== record.DATA_ITEM_CODE">{{ record.DATA_ITEM_CODE }}</a-tag>
          </div>
        </template>
        <template slot="newValueSlot" slot-scope="text, record">
          <pre style="max-height: 60px; max-width: 200px; overflow: hidden; text-overflow: ellipsis">{{ text }}</pre>
          <a-button v-if="record.diffHtml" size="small" type="link" style="float: right" @click="showDiffHtml(record)">差异比较</a-button>
        </template>
        <template slot="oldValueSlot" slot-scope="text, record">
          <pre style="max-height: 60px; max-width: 200px; overflow: hidden; text-overflow: ellipsis">{{ text }}</pre>
        </template>
      </a-table>
    </a-card>

    <a-card title="关联数据操作变更" :bordered="false" v-show="childDataLogRows.length > 0">
      <a-table :columns="childLogTableColumns" :data-source="childDataLogRows" :pagination="false" :loading="childLogLoading">
        <template slot="dateItemNameSlot" slot-scope="text, record">
          <div>{{ text }}</div>
          <a-tag v-if="record.DATA_ITEM_CODE">{{ record.DATA_ITEM_CODE }}</a-tag>
        </template>
      </a-table>
    </a-card>
    <a-modal
      title="差异比较"
      :visible="diffModalVisible"
      :footer="null"
      width="calc(100vw - 100px)"
      :maskClosable="false"
      @cancel="diffModalVisible = false"
    >
      <Scroll style="max-height: calc(100vh - 300px)">
        <div v-html="diffHtml" class="diff-html"></div>
      </Scroll>
    </a-modal>
  </div>
</template>
<style lang="less">
.diff-html {
  .d2h-file-name-wrapper {
    .d2h-tag {
      display: none;
    }
  }
}
</style>
<script type="text/babel">
const diff = require('diff');
// import * as Diff2Html from 'diff2html';
// import 'diff2html/bundles/css/diff2html.min.css';
export default {
  name: 'AuditDataLog',
  inject: ['$event'],
  props: {},
  components: {},
  computed: {},
  data() {
    return {
      dataItemRows: [],
      childDataLogRows: [],
      dataItemLoading: false,
      childLogLoading: false,
      fieldChangeTableColumns: [
        {
          title: '标题',
          dataIndex: 'DATA_ITEM_NAME',
          scopedSlots: { customRender: 'dateItemNameSlot' }
        },
        {
          title: '新值',
          dataIndex: 'NEW_VALUE',
          width: 240,
          scopedSlots: { customRender: 'newValueSlot' },
          customCell: function () {
            return { style: 'color:green' };
          }
        },
        {
          title: '旧值',
          width: 240,
          dataIndex: 'OLD_VALUE',
          scopedSlots: { customRender: 'oldValueSlot' },
          customCell: function () {
            return { style: 'color:red' };
          }
        }
      ],
      childLogTableColumns: [
        {
          title: '数据',
          dataIndex: 'NAME',
          width: 300
        },
        {
          title: '描述',
          dataIndex: 'REMARK'
        }
      ],
      diffHtml: undefined,
      diffModalVisible: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    if (this.$event != undefined) {
      this.getDateItems(this.$event.meta.UUID);
      this.getChildDataLog(this.$event.meta.UUID);
    }
  },
  methods: {
    showDiffHtml(record) {
      import('diff2html/bundles/css/diff2html.min.css').then(() => {
        import('diff2html').then(Diff2Html => {
          this.diffModalVisible = true;
          this.diffHtml = Diff2Html.html(diff.createTwoFilesPatch('旧内容', '新内容', record.OLD_VALUE || '', record.NEW_VALUE || ''), {
            drawFileList: false,
            matching: 'lines',
            outputFormat: 'side-by-side'
          });
        });
      });
    },
    getChildDataLog(parentUuid) {
      return new Promise((resolve, reject) => {
        this.dataItemRows.splice(0, this.dataItemRows.length);
        this.childLogLoading = true;
        this.$axios
          .post(`/proxy/api/dm/loadDataById/V_AUDIT_DATA_CHILD`, {
            params: {
              parentUuid
            }
          })
          .then(({ data }) => {
            this.childLogLoading = false;
            let result = data.data;
            this.childDataLogRows.push(...result.data);
          })
          .catch(error => {});
      });
    },
    isJsonString(str) {
      try {
        if (str) {
          let parseResult = JSON.parse(str);
          return typeof parseResult != 'number' && typeof parseResult != 'boolean' && typeof parseResult != 'string';
        }
      } catch (error) {}
      return false;
    },
    getDateItems(auditUuid) {
      return new Promise((resolve, reject) => {
        this.dataItemRows.splice(0, this.dataItemRows.length);
        this.dataItemLoading = true;
        this.$axios
          .post(`/proxy/api/dm/loadDataById/VIEW_AUDIT_DATA_ITEM`, {
            params: {
              auditUuid
            }
          })
          .then(({ data }) => {
            this.dataItemLoading = false;
            let result = data.data.data;
            for (let i = 0, len = result.length; i < len; i++) {
              let newValue = result[i].NEW_VALUE,
                oldValue = result[i].OLD_VALUE;
              let newValueIsJson = this.isJsonString(newValue),
                oldValueIsJson = this.isJsonString(oldValue);
              if (newValueIsJson) {
                // 格式化
                result[i].NEW_VALUE = JSON.stringify(JSON.parse(result[i].NEW_VALUE), null, 2);
              }
              if (oldValueIsJson) {
                // 格式化
                result[i].OLD_VALUE = JSON.stringify(JSON.parse(result[i].OLD_VALUE), null, 2);
              }
              if (newValueIsJson || oldValueIsJson) {
                result[i].diffHtml = true;
              }
            }
            this.dataItemRows.push(...result);
          })
          .catch(error => {});
      });
    }
  }
};
</script>
