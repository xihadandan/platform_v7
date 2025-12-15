<template>
  <div class="org-batch-create-org-element">
    <div style="margin-bottom: 12px">
      <a-button type="link" icon="plus" @click="addRow">新增</a-button>
      <a-button type="link" @click="deleteSelectedRows">
        <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        删除
      </a-button>
    </div>
    <a-table
      :pagination="false"
      rowKey="uuid"
      :columns="columns"
      :data-source="elements"
      :rowSelection="rowSelection"
      :scroll="{ y: 'calc(100vh - 300px)', x: 2000 }"
      class="pt-table"
    >
      <template slot="seqSlot" slot-scope="text, record, index">
        {{ index + 1 }}
        <a-icon type="close-circle" theme="filled" v-show="record.error" style="color: #f5222d" />
        <a-icon type="check-circle" theme="filled" v-show="record.success" style="color: #52c41a" />
        <a-icon type="loading" v-show="record.loading" />
      </template>
      <template slot="nameSlot" slot-scope="text, record, index">
        <a-form-model
          :ref="'orgElementTypeForm_' + record.uuid"
          layout="inline"
          :model="record"
          :rules="{
            type: { required: true, message: '类型必选', trigger: ['blur', 'change'] },
            unitId: { required: record.type == 'unit', message: '单位必选', trigger: ['blur', 'change'] },
            name: { required: record.type !== 'unit', message: '名称必填 ', trigger: ['blur'] }
          }"
        >
          <a-form-model-item prop="type">
            <a-select
              v-model="record.type"
              style="width: 120px"
              :options="orgElementModelOptions"
              @change="onChangeType(record)"
              allow-clear
            />
          </a-form-model-item>
          <a-form-model-item prop="unitId" v-if="record.type == 'unit'">
            <a-select
              v-model="record.unitId"
              style="width: 120px"
              :options="unitOptions"
              :filterOption="filterOption"
              show-search
              allow-clear
              @change="(v, opt) => onChangeUnit(v, opt, record)"
            />
          </a-form-model-item>
          <a-form-model-item prop="name" v-else>
            <a-input v-model="record.name" style="width: 120px" allow-clear />
          </a-form-model-item>
        </a-form-model>
      </template>
      <template slot="shortNameSlot" slot-scope="text, record">
        <a-input v-model="record.shortName" allow-clear />
      </template>
      <template slot="codeSlot" slot-scope="text, record">
        <a-input v-model="record.code" allow-clear />
      </template>
      <template slot="parentSlot" slot-scope="text, record">
        <a-form-model
          :ref="'orgElementTypeForm_parentUuid' + record.uuid"
          layout="inline"
          :model="record"
          :rules="{
            parentUuid: { required: true, message: '父级节点必选', trigger: ['blur', 'change'] }
          }"
          style="width: 100%"
        >
          <a-form-model-item prop="parentUuid" style="width: 100%" :wrapper-col="{ style: { width: '100%' } }">
            <a-tree-select
              show-search
              treeNodeFilterProp="title"
              v-model="record.parentUuid"
              style="width: 100%"
              :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
              allow-clear
              :key="record.uuid + record.type"
              :tree-data="getOrgElementTreeData(record.type)"
              @change="(v, l, e) => onChangeOrgElementParentChange(v, l, e, record)"
            ></a-tree-select>
          </a-form-model-item>
        </a-form-model>
      </template>

      <template slot="dutySlot" slot-scope="text, record">
        <a-tree-select
          show-search
          treeNodeFilterProp="title"
          v-if="record.type == 'job'"
          v-model="record.dutyId"
          :treeData="dutyTreeData"
          :treeIcon="true"
          style="width: 100%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          allow-clear
        ></a-tree-select>
        <!-- <a-select
            :style="{ width: '100%' }"
            :options="userOptions"
            v-model="form.management.director"
            allowClear
            showSearch
            :filter-option="filterOption"
          /> -->
      </template>
      <template slot="directorSlot" slot-scope="text, record">
        <OrgSelect
          title="选择负责人"
          :orgVersionId="orgVersion.id"
          v-model="record.management.director"
          :checkableTypes="['user', 'job']"
          orgType="MyOrg"
          :multiSelect="false"
          :showBizOrgUnderOrg="false"
          style="width: 160px"
        />
      </template>
      <template slot="leaderSlot" slot-scope="text, record">
        <OrgSelect
          title="选择分管领导"
          :orgVersionId="orgVersion.id"
          v-model="record.management.leader"
          :checkableTypes="['user', 'job']"
          orgType="MyOrg"
          :multiSelect="false"
          :showBizOrgUnderOrg="false"
          style="width: 160px"
        />
      </template>
      <template slot="orgManagerSlot" slot-scope="text, record">
        <OrgSelect
          title="选择管理员"
          :orgVersionId="orgVersion.id"
          v-model="record.management.orgManager"
          :checkableTypes="['user']"
          :showBizOrgUnderOrg="false"
          orgType="MyOrg"
          :multiSelect="false"
          style="width: 160px"
        />
      </template>
      <template slot="remarkSlot" slot-scope="text, record">
        <a-textarea v-model="record.remark" :autosize="{ minRows: 2, maxRows: 2 }" style="width: 200px" />
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" size="small" @click="copyRow(record, index)">
          <Icon type="pticon iconfont icon-ptkj-fuzhi"></Icon>
          复制
        </a-button>
        <a-button type="link" size="small" @click="insertRow(index)">
          <Icon type="pticon iconfont icon-ptkj-jiahao"></Icon>
          插入
        </a-button>
        <a-button type="link" size="small" @click="deleteRow(index)">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
          删除
        </a-button>
      </template>
      <!-- <div slot="expandedRowRender" slot-scope="record" style="padding-left: 92px">
        <a-form-model layout="inline" :ref="'extForm_' + record.uuid" class="pt-form">
          <a-form-model-item label="负责人">
            <OrgSelect
              title="选择负责人"
              :orgVersionId="orgVersion.id"
              v-model="record.management.director"
              :checkableTypes="['user', 'job']"
              orgType="MyOrg"
              :multiSelect="false"
              style="width: 160px"
            />
          </a-form-model-item>
          <a-form-model-item label="分管领导">
            <OrgSelect
              title="选择分管领导"
              :orgVersionId="orgVersion.id"
              v-model="record.management.leader"
              :checkableTypes="['user', 'job']"
              orgType="MyOrg"
              :multiSelect="false"
              style="width: 160px"
            />
          </a-form-model-item>
          <a-form-model-item v-show="enableAuthority(record.type)" label="管理员">
            <OrgSelect
              title="选择管理员"
              :orgVersionId="orgVersion.id"
              v-model="record.management.orgManager"
              :checkableTypes="['user']"
              orgType="MyOrg"
              :multiSelect="false"
              style="width: 160px"
            />
          </a-form-model-item>
          <a-form-model-item label="备注">
            <a-textarea v-model="record.remark" :autosize="{ minRows: 2, maxRows: 2 }" style="width: 200px" />
          </a-form-model-item>
        </a-form-model>
      </div> -->
    </a-table>
  </div>
</template>
<style lang="less">
.org-batch-create-org-element {
  .pt-table {
    .ant-table-tbody > tr > td {
      padding: var(--w-padding-2xs) var(--w-padding-3xs) var(--w-padding-2xs) var(--w-padding-3xs);
      .ant-form-inline .ant-form-item-with-help {
        margin-bottom: 0;
      }
    }
  }
}
</style>
<script type="text/babel">
import { generateId } from '@framework/vue/utils/util';
import { upperCase, debounce } from 'lodash';
import OrgSelect from '@admin/app/web/lib/org-select.vue';

export default {
  name: 'OrgBatchCreateOrgElement',
  inject: ['pageContext', 'parentAllowedMountType', 'orgElementManagementMap'],
  props: { orgVersion: Object, orgElementModels: Array, orgElementTreeData: Array, orgSetting: Object },
  components: { OrgSelect },
  data() {
    return {
      elements: [],
      unitIdMap: {},
      unitOptions: [],
      dutyTreeData: [],
      columns: [
        {
          title: '序号',
          width: 70,
          scopedSlots: { customRender: 'seqSlot' }
        },
        { title: '类型 / 名称', width: 290, scopedSlots: { customRender: 'nameSlot' } },
        { title: '简称', width: 150, scopedSlots: { customRender: 'shortNameSlot' } },
        { title: '编号', width: 150, scopedSlots: { customRender: 'codeSlot' } },
        { title: '父级节点', width: 160, scopedSlots: { customRender: 'parentSlot' } },
        { title: '职务', scopedSlots: { customRender: 'dutySlot' } },
        { title: '负责人', scopedSlots: { customRender: 'directorSlot' } },
        { title: '分管领导', scopedSlots: { customRender: 'leaderSlot' } },
        { title: '管理员', scopedSlots: { customRender: 'orgManagerSlot' } },
        { title: '备注', scopedSlots: { customRender: 'remarkSlot' } },
        { title: '操作', width: 220, scopedSlots: { customRender: 'operationSlot' }, fixed: 'right' }
      ],
      rowSelection: {
        selectedRowKeys: [],
        selectedRows: [],
        onChange: this.onSelectRowChange
      },
      commitRows: [],
      committing: false
    };
  },
  computed: {
    orgElementModelOptions() {
      let options = [];
      for (let o of this.orgElementModels) {
        if (o.unAddable !== true) {
          options.push({
            value: o.id,
            label: o.name
          });
        }
      }
      return options;
    },
    vOrgElementTreeData() {
      let treeData = JSON.parse(JSON.stringify(this.orgElementTreeData));
      if (treeData.length) {
        let build = data => {
          for (let i = 0; i < data.length; i++) {
            let tar = data[i];
            tar.value = tar.key;
            delete tar.title;
            delete tar.key;
            if (tar.children) build(tar.children);
          }
        };
        build(treeData);
      }
      return treeData;
    }
  },
  watch: {
    commitRows: {
      deep: true,
      handler(v, o) {
        if (this.committing && v.length == 0) {
          // 提交结束了
          this.committing = false;
          this.pageContext.emitEvent('orgVersionManageSiderRefresh');
          this.$loading(false);
        }
      }
    }
  },
  beforeCreate() {},
  created() {
    this.resizeObserverHandler = debounce(this.resizeObserverHandler.bind(this), 300);
  },
  beforeMount() {
    this.getUnitOptions();
    this.getDutyTreeData();
  },
  mounted() {
    // 监听表格高度变化
    let tbody = this.$el.querySelector('.ant-table-scroll .ant-table-body');
    if (tbody) {
      new ResizeObserver(this.resizeObserverHandler).observe(this.$el.querySelector('.ant-table-scroll .ant-table-body'));
    }
  },
  methods: {
    resizeObserverHandler() {
      let scrollTable = this.$el.querySelector('.ant-table');
      if (scrollTable && scrollTable.__vue__ && scrollTable.__vue__._provided.table) {
        scrollTable.__vue__._provided.table.syncFixedTableRowHeight();
      }
    },
    onChangeOrgElementParentChange(v, l, e, item) {
      item.parentOrgElementType = undefined;
      if (v && e) {
        item.parentOrgElementType = e.triggerNode.dataRef.data.type;
      }
    },
    onChangeType(item) {
      if (item.type && item.parentOrgElementType) {
        if (!(this.parentAllowedMountType[item.type] && this.parentAllowedMountType[item.type].includes(item.parentOrgElementType))) {
          item.parentOrgElementType = undefined;
          item.parentUuid = undefined;
        }
      }
    },
    getOrgElementTreeData(type) {
      let treeData = JSON.parse(JSON.stringify(this.orgElementTreeData));
      if (treeData.length) {
        let build = data => {
          for (let i = 0; i < data.length; i++) {
            let tar = data[i];
            tar.value = tar.key;
            delete tar.title;
            delete tar.key;
            tar.disabled = !(
              this.parentAllowedMountType &&
              this.parentAllowedMountType[type] &&
              this.parentAllowedMountType[type].includes(tar.data.type)
            );
            if (!tar.disabled) {
              if (tar.orgAuthority) {
                tar.disabled = tar.orgAuthority[type] == undefined || !tar.orgAuthority[type].includes('add');
              }
            }
            if (tar.children) build(tar.children);
          }
        };
        build(treeData);
      }
      return treeData;
    },
    clearAll() {
      this.elements.splice(0, this.elements.length);
      this.rowSelection.selectedRowKeys.splice(0, this.rowSelection.selectedRowKeys.length);
      this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length);
    },
    copyRow(item, index) {
      let _item = JSON.parse(JSON.stringify(item));
      _item.uuid = generateId();
      this.elements.splice(index + 1, 0, _item);
    },
    deleteRow(index) {
      this.elements.splice(index, 1);
    },
    insertRow(index) {
      this.elements.splice(index + 1, 0, {
        uuid: generateId(),
        type: undefined,
        name: undefined,
        shortName: undefined,
        code: undefined,
        parentUuid: undefined,
        dutyId: undefined,
        remark: undefined,
        management: {}
      });
    },
    deleteSelectedRows() {
      for (let i = 0; i < this.rowSelection.selectedRowKeys.length; i++) {
        let rowIndex = this.elements.findIndex(element => element.uuid == this.rowSelection.selectedRowKeys[i]);
        if (rowIndex != -1) {
          this.elements.splice(rowIndex, 1);
        }
      }
      this.rowSelection.selectedRowKeys.splice(0, this.rowSelection.selectedRowKeys.length);
      this.rowSelection.selectedRows.splice(0, this.rowSelection.selectedRows.length);
    },
    addRow() {
      this.elements.push({
        uuid: generateId(),
        type: undefined,
        name: undefined,
        shortName: undefined,
        code: undefined,
        parentUuid: undefined,
        dutyId: undefined,
        remark: undefined,
        management: {}
      });
    },
    onSelectRowChange(selectedRowKeys, selectedRows) {
      this.rowSelection.selectedRowKeys = selectedRowKeys;
      this.rowSelection.selectedRows = selectedRows;
    },
    onChangeUnit(v, opt, record) {
      let formData = this.form || record;
      if (v != undefined) {
        formData.sourceId = v;
        formData.name = this.unitIdMap[v].name;
        formData.shortName = this.unitIdMap[v].shortName;
      }
    },
    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    getDutyTreeData() {
      let _this = this;
      $axios
        .post(`/proxy/api/org/organization/fetchOrgTree/OrgDutySeq`, {})
        .then(({ data }) => {
          console.log('查询职务数据返回：', data);
          if (data.data) {
            let build = d => {
              for (let i = 0, len = d.length; i < len; i++) {
                d[i].selectable = d[i].checkable;
                d[i].value = d[i].key;
                if (d[i].selectable) {
                  // 添加单选框
                  d[i].icon = ({ selected }) => {
                    return <a-radio checked={selected} />;
                  };
                }
                if (d[i].children) {
                  build(d[i].children);
                } else {
                  d[i].isLeaf = true;
                }
              }
            };
            build(data.data);
            _this.dutyTreeData = data.data;
          }
        })
        .catch(error => {
          console.log(error);
          _this.$message.error('组织服务异常');
        });
    },
    getUnitOptions() {
      $axios.get(`/proxy/api/org/unit/listUnits`, { params: { system: null, tenant: null } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          for (let i = 0, len = data.data.length; i < len; i++) {
            this.unitIdMap[data.data[i].id] = data.data[i];
            this.unitOptions.push({
              label: data.data[i].name,
              value: data.data[i].id
            });
          }
        }
      });
    },
    commitRowDone(i) {
      let index = this.commitRows.indexOf(i);
      if (index !== -1) {
        this.commitRows.splice(index, 1);
      }
      this.$set(this.elements[i], 'loading', false);
    },
    createOrgElement() {
      if (this.elements.length) {
        this.$loading();
        this.committing = true;
        this.commitRows.splice(0, this.commitRows.length);
        let addSeq = {};
        for (let i = 0, len = this.elements.length; i < len; i++) {
          this.commitRows.push(i);
          let key = this.elements[i].parentUuid == null ? 0 : this.elements[i].parentUuid;
          if (addSeq[key] == undefined) {
            addSeq[key] = 1;
          } else {
            addSeq[key] = addSeq[key] + 1;
          }
          this.elements[i].seq = addSeq[key];
          this.validateRow(i)
            .then(index => {
              this.commitRow(index);
            })
            .catch(i => {
              this.commitRowDone(i);
            });
        }
      }
    },
    validateRow(i) {
      let _this = this;
      let element = this.elements[i];
      this.$set(element, 'loading', true);
      return new Promise((resolve, reject) => {
        let promises = [];
        promises.push(
          new Promise((resolve, reject) => {
            _this.$refs['orgElementTypeForm_' + element.uuid].validate(pass => {
              if (pass) {
                resolve(i);
              } else {
                reject(i);
              }
            });
          })
        );
        promises.push(
          new Promise((resolve, reject) => {
            _this.$refs['orgElementTypeForm_parentUuid' + element.uuid].validate(pass => {
              if (pass) {
                resolve(i);
              } else {
                reject(i);
              }
            });
          })
        );
        Promise.all(promises)
          .then(() => {
            _this.$set(element, 'success', true);
            _this.$set(element, 'error', false);
            resolve(i);
          })
          .catch(() => {
            _this.$set(element, 'success', false);
            _this.$set(element, 'error', true);

            reject(i);
          });
      });
    },
    commitRow(i) {
      return new Promise((resolve, reject) => {
        let element = this.elements[i];
        let form = {
          name: element.name,
          unitId: element.unitId,
          orgVersionId: this.orgVersion.id,
          orgVersionUuid: this.orgVersion.uuid,
          state: this.orgVersion.state,
          parentUuid: element.parentUuid,
          type: element.type,
          management: element.management,
          remark: element.remark,
          sourceId: element.sourceId,
          seq: element.seq
        };
        if (form.management.orgManager) {
          form.management.enableAuthority = true;
        }
        if (element.dutyId) {
          form.jobDuty = { dutyId: element.dutyId };
        }
        console.log(`提交第${i + 1}行数据: `, form);
        $axios
          .post('/proxy/api/org/organization/version/saveOrgElement', form)
          .then(({ data }) => {
            this.commitRowDone(i);
            if (data.code == 0) {
              this.$set(element, 'loading', false);
              this.$set(element, 'success', true);
              this.$emit('orgElementChange');
            } else {
              this.$set(element, 'error', true);
              console.error(`序号: ${i + 1} , 批量创建组织节点 [${form.name}] 失败: `, data);
            }
          })
          .catch(error => {
            this.commitRowDone(i);
            this.$set(element, 'error', true);
            console.error(`序号: ${i + 1} , 批量创建组织节点 [${form.name}] 失败: `, error);
          });
      });
    },

    enableAuthority(elementType) {
      const _this = this;
      let grantOptions = _this.orgSetting[`ORG_${upperCase(elementType)}_EDIT_GRANT`] || {};
      return grantOptions.enable;
    }
  }
};
</script>
