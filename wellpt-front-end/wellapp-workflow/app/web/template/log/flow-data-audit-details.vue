<template>
  <div>
    <a-spin :spinning="loading">
      <a-card title="数据变更" :bordered="false">
        <a slot="extra" href="#">
          <a-switch v-model="showAllField" />
          显示所有字段
        </a>
        <a-tabs v-if="auditLogData">
          <a-tab-pane :tab="auditLogData.formName" :key="auditLogData.formUuid">
            <a-table :columns="fieldChangeTableColumns" :data-source="mainFormFields" :pagination="false">
              <template slot="nameSlot" slot-scope="text, record">
                <div>
                  <div>{{ text }}</div>
                  <a-tag class="primary-color">{{ record.code }}</a-tag>
                </div>
              </template>
              <template slot="newValueSlot" slot-scope="text, record">
                <template v-if="record.newValueExists">
                  <pre style="max-height: 60px; max-width: 200px; overflow: hidden; text-overflow: ellipsis">{{ text }}</pre>
                  <a-button v-if="record.diffHtml" size="small" type="link" style="float: right" @click="showDiffHtml(record)">
                    差异比较
                  </a-button>
                </template>
              </template>
              <template slot="oldValueSlot" slot-scope="text, record">
                <template v-if="record.oldValueExists">
                  <pre style="max-height: 60px; max-width: 200px; overflow: hidden; text-overflow: ellipsis">{{ text }}</pre>
                </template>
              </template>
            </a-table>
          </a-tab-pane>
          <a-tab-pane v-for="subform in subforms" :tab="subform.name" :key="subform.code">
            <a-table :columns="subformRowTableColumns" :data-source="subform.rows" :pagination="false">
              <template slot="sortOrderSlot" slot-scope="text, record, index">第{{ index + 1 }}行</template>
              <template slot="addedSlot" slot-scope="text, record">
                <span v-if="record.added">是</span>
                <span v-else>否</span>
              </template>
              <template slot="modifiedSlot" slot-scope="text, record">
                <span v-if="record.formFields && record.formFields.filter(item => item.diffVal && item.oldValueExists).length > 0">是</span>
                <span v-else>否</span>
              </template>
              <template slot="deletedSlot" slot-scope="text, record">
                <span v-if="record.deleted">是</span>
                <span v-else>否</span>
              </template>
              <a-table
                slot="expandedRowRender"
                slot-scope="record"
                :columns="fieldChangeTableColumns"
                :data-source="record.formFields"
                :pagination="false"
              >
                <template slot="nameSlot" slot-scope="text, record">
                  <div>
                    <div>{{ text }}</div>
                    <a-tag class="primary-color">{{ record.code }}</a-tag>
                  </div>
                </template>
                <template slot="newValueSlot" slot-scope="text, record">
                  <template v-if="record.newValueExists">
                    <pre style="max-height: 60px; max-width: 200px; overflow: hidden; text-overflow: ellipsis">{{ text }}</pre>
                    <a-button v-if="record.diffHtml" size="small" type="link" style="float: right" @click="showDiffHtml(record)">
                      差异比较
                    </a-button>
                  </template>
                </template>
                <template slot="oldValueSlot" slot-scope="text, record">
                  <template v-if="record.oldValueExists">
                    <pre style="max-height: 60px; max-width: 200px; overflow: hidden; text-overflow: ellipsis">{{ text }}</pre>
                  </template>
                </template>
              </a-table>
            </a-table>
          </a-tab-pane>
        </a-tabs>
      </a-card>
    </a-spin>
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

<script>
import { deepClone } from '@framework/vue/utils/util';
const diff = require('diff');
export default {
  inject: ['$event'],
  data() {
    let auditData = (this.$event && this.$event.meta) || {};
    return {
      loading: false,
      showAllField: false,
      auditData,
      auditLogData: undefined,
      fieldChangeTableColumns: [
        {
          title: '标题',
          dataIndex: 'name',
          scopedSlots: { customRender: 'nameSlot' }
        },
        {
          title: '新值',
          dataIndex: 'newValue',
          width: 240,
          scopedSlots: { customRender: 'newValueSlot' },
          customCell: function () {
            return { style: 'color:green' };
          }
        },
        {
          title: '旧值',
          width: 240,
          dataIndex: 'oldValue',
          scopedSlots: { customRender: 'oldValueSlot' },
          customCell: function (record) {
            if (record.diffVal) {
              return { style: 'color:red' };
            }
            return { style: 'color:green' };
          }
        }
      ],
      subformRowTableColumns: [
        {
          title: '行号',
          dataIndex: 'sortOrder',
          scopedSlots: { customRender: 'sortOrderSlot' }
        },
        {
          title: '新增',
          dataIndex: 'added',
          scopedSlots: { customRender: 'addedSlot' }
        },
        {
          title: '修改',
          dataIndex: 'modified',
          scopedSlots: { customRender: 'modifiedSlot' }
        },
        {
          title: '删除',
          dataIndex: 'modified',
          scopedSlots: { customRender: 'deletedSlot' }
        }
      ],
      diffHtml: undefined,
      diffModalVisible: false
    };
  },
  computed: {
    mainFormFields() {
      let mainFormFields = this.auditLogData.mainFormFields || [];
      if (this.showAllField) {
        return mainFormFields;
      }
      return mainFormFields.filter(item => item.diffVal);
    },
    subforms() {
      let subforms = this.auditLogData.subforms || [];
      if (this.showAllField) {
        return subforms;
      }
      subforms = deepClone(subforms);
      subforms.forEach(subform => {
        subform.rows &&
          subform.rows.forEach(row => {
            row.formFields = row.formFields.filter(item => item.diffVal);
          });
      });
      return subforms;
    }
  },
  mounted() {
    if (this.auditData._id) {
      this.loadAuditLog(this.auditData._id);
    }
  },
  methods: {
    loadAuditLog(objectId) {
      this.loading = true;
      $axios
        .get(`/proxy/api/workflow/work/getFlowDataSnapshotAuditLog?objectId=${objectId}`)
        .then(({ data: result }) => {
          this.loading = false;
          let resultData = this.markDiff(result.data);
          this.auditLogData = resultData;
        })
        .catch(error => {
          this.loading = false;
        });
    },
    markDiff(auditLogData = {}, rawFields) {
      let formFields = rawFields || auditLogData.mainFormFields || [];
      formFields.forEach(field => {
        let newValue = field.newValue;
        let oldValue = field.oldValue;
        if ((newValue != null && typeof newValue === 'object') || (oldValue != null && typeof oldValue === 'object')) {
          field.newValue = typeof newValue === 'object' ? JSON.stringify(newValue, null, 2) : newValue;
          field.oldValue = typeof oldValue === 'object' ? JSON.stringify(oldValue, null, 2) : oldValue;
          field.diffHtml = field.newValue != field.oldValue;
          field.diffVal = field.diffHtml;
        } else {
          field.diffVal = newValue != oldValue;
        }
      });
      if (auditLogData.subforms) {
        auditLogData.subforms.forEach(subform => {
          let rows = subform.rows || [];
          rows.forEach(row => {
            this.markDiff(row, row.formFields);
          });
        });
      }
      return auditLogData;
    },
    showDiffHtml(record) {
      import('diff2html/bundles/css/diff2html.min.css').then(() => {
        import('diff2html').then(Diff2Html => {
          this.diffModalVisible = true;
          this.diffHtml = Diff2Html.html(diff.createTwoFilesPatch('旧内容', '新内容', record.oldValue || '', record.newValue || ''), {
            drawFileList: false,
            matching: 'lines',
            outputFormat: 'side-by-side'
          });
        });
      });
    }
  }
};
</script>

<style lang="less" scoped>
.diff-html {
  .d2h-file-name-wrapper {
    .d2h-tag {
      display: none;
    }
  }
}
</style>
