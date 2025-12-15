<template>
  <Modal :ok="onConfirmExport" :title="title" @show="onShow" v-model="visible" :container="container">
    <template slot="default">
      <slot name="default"></slot>
    </template>
    <template slot="content">
      <div style="min-height: 200px">
        <div class="spin-center" v-show="downloading || loading">
          <a-spin :tip="tip" />
        </div>
        <div style="text-align: right" v-if="modifyRange">
          <a-input-group compact>
            <a-button>导出范围&nbsp;: &nbsp;</a-button>
            <a-select style="width: 200px" v-model="modifyDays" @change="onChangeModifyDays">
              <a-select-option value="">所有数据</a-select-option>
              <a-select-option value="0">今日内修改</a-select-option>
              <a-select-option value="-1">昨日至今修改</a-select-option>
              <a-select-option value="-7">本周内修改</a-select-option>
              <a-select-option value="-30">一个月内修改</a-select-option>
            </a-select>
          </a-input-group>
        </div>

        <a-result v-show="error" status="error" title="导出服务异常">
          <template #extra>
            <a-button type="primary" @click="onShow">重试</a-button>
          </template>
        </a-result>
        <a-tree
          checkable
          :tree-data="treeData"
          v-if="!loading"
          :replace-fields="{ key: 'id', title: 'name' }"
          :blockNode="true"
          :expandedKeys.sync="expandedKeys"
          v-model="treeChecked"
          :checkStrictly="true"
          :key="treeKey"
          @check="onCheckNode"
        ></a-tree>
        <!-- <DraggableTreeList
          selectMode="multiple"
          v-if="!loading"
          v-model="treeData"
          :draggable="false"
          :dragButton="false"
          expandIcon="plus"
          :title-width="300"
          ref="treeList"
          titleField="name"
          :afterSelected="afterSelected"
          @mounted="onMounted"
          :key="treeKey"
        ></DraggableTreeList> -->
        <iframe :style="{ display: 'none' }" id="exportFrame" name="exportFrame">
          <form
            :style="{ display: 'none' }"
            ref="form"
            method="post"
            target="_self"
            action="/proxy/common/iexport/service/exportDataDefinition"
          >
            <input type="hidden" name="typeUuids" v-model="typeUuids" />
            <input type="hidden" name="_csrf" :value="csrf" />
            <input type="hidden" name="fileName" :value="downloadFileName" />
            <input type="hidden" name="requestId" v-model="requestId" />
          </form>
        </iframe>
      </div>
    </template>
    <template slot="footer">
      <div style="display: flex; align-items: center; justify-content: space-between">
        <div>
          <a-checkbox :indeterminate="indeterminate" :checked="checkAll" @change="onCheckAllChange">全选</a-checkbox>
        </div>
        <div>
          <a-button type="primary" icon="export" :loading="downloading" @click="onConfirmExport">导出</a-button>
        </div>
      </div>
    </template>
  </Modal>
</template>
<style lang="less"></style>
<script type="text/babel">
import Modal from '../modal.vue';
import DraggableTreeList from '@pageAssembly/app/web/widget/commons/draggable-tree-list';
import { generateId, getCookie } from '@framework/vue/utils/util';
import moment from 'moment';
export default {
  name: 'ExportDef',
  inject: ['csrf'],
  props: {
    type: String | Array,
    uuid: String | Array,
    fileName: String,
    modifyRange: {
      type: Boolean,
      default: false
    },
    title: {
      type: String,
      default: '导出定义'
    },
    exportDependency: {
      type: Boolean,
      default: true
    },
    container: Function
  },
  components: { Modal, DraggableTreeList },
  computed: {
    tip() {
      if (this.downloading) {
        return '下载中';
      } else if (this.loading) {
        return '加载中';
      }
      return null;
    },
    okButtonProps() {
      return {
        props: {
          loading: this.downloading,
          icon: 'export'
        }
      };
    },
    originalTypeUuids() {
      let arr = [];
      for (let t of this.originalData) {
        arr.push(t.type + ':' + t.id);
      }
      return arr;
    }
  },
  data() {
    return {
      originalData: [],
      modifyDays: '',
      specifyModifyDays: undefined,
      visible: false,
      error: false,
      indeterminate: false,
      requestId: undefined,
      checkAll: false,
      loading: true,
      downloading: false,
      typeUuids: undefined,
      treeData: [],
      treeKey: 'exportDefTreeKey',
      downloadFileName: this.fileName,
      expandedKeys: [],
      treeChecked: {
        checked: [],
        halfChecked: []
      },
      allOriginalKeys: [],
      filteredKeys: [],
      now: undefined
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    onCheckNode(checkedKeys, { checked, checkedNodes, node, event }) {
      let judgementKeys = this.modifyDays == '' ? this.allOriginalKeys : this.filteredKeys;
      this.indeterminate = checkedKeys.checked.length && checkedKeys.checked.length < judgementKeys.length;
      this.checkAll = checkedKeys.checked.length && checkedKeys.checked.length == judgementKeys.length;
    },
    afterSelected(item, keys) {
      this.indeterminate = keys.length && keys.length < this.$refs.treeList.allKeys.length;
      this.checkAll = keys.length && keys.length == this.$refs.treeList.allKeys.length;
    },
    onCheckAllChange(e) {
      this.checkAll = e.target.checked;
      // if (e.target.checked) {
      //   this.$refs.treeList.selectAll();
      // } else {
      //   this.$refs.treeList.setSelectedKeys([]);
      // }
      this.indeterminate = false;
      if (this.modifyDays == '') {
        this.treeChecked.checked.splice(0, this.treeChecked.checked.length);
        if (this.checkAll) {
          this.treeChecked.checked.push(...(this.modifyDays == '' ? this.allOriginalKeys : this.filteredKeys));
        }
      } else {
        for (let f of this.filteredKeys) {
          if (this.checkAll) {
            if (!this.treeChecked.checked.includes(f)) {
              this.treeChecked.checked.push(f);
            }
          } else {
            if (this.treeChecked.checked.includes(f)) {
              this.treeChecked.checked.splice(this.treeChecked.checked.indexOf(f), 1);
            }
          }
        }
      }
    },
    onMounted() {
      // this.$refs.treeList.selectAll();
      // this.$refs.treeList.expandAll();
      // this.checkAll = true;
    },
    onConfirmExport(e) {
      let _this = this;
      // let items =this.treeChecked.checked;// this.$refs.treeList.getSelectedItems();
      if (this.treeChecked.checked.length) {
        let typeUuids = [];
        for (let i of this.treeChecked.checked) {
          typeUuids.push(`${this.treeNodeKeyMap[i].type}:${i}`);
        }
        this.downloadFileName = encodeURIComponent(this.treeNodeKeyMap[this.treeChecked.checked[0]].name);
        this.typeUuids = typeUuids.join(';');
        this.requestId = generateId();
        if (!this.csrf) {
          this.csrf = window.__INITIAL_STATE__.csrf;
        }
        this.$nextTick(() => {
          _this.downloading = true;
          this.$refs.form.submit();
          let t = setInterval(() => {
            if (getCookie(_this.requestId)) {
              _this.downloading = false;
              clearInterval(t);
            }
          }, 1000);
        });
      } else {
        this.$message.info('请勾选要导出的数据');
      }
    },
    show() {
      this.visible = true;
    },
    onShow() {
      this.error = false;
      this.loading = true;
      this.checkAll = false;
      this.indeterminate = false;
      this.modifyDays = '';
      this.fetchExportDataDefTree();
    },

    createExportRequest(type, uuid, fileName) {
      return new Promise((resolve, reject) => {
        let iframe = document.createElement('iframe');
        iframe.setAttribute('style', 'display:none');
        let form = document.createElement('form');
        iframe.appendChild(form);
        document.body.appendChild(iframe);
        form.method = 'POST';
        form.target = '_self';
        form.action = '/proxy/common/iexport/service/exportDataDefinition';
        let typeUuidInput = document.createElement('input');
        typeUuidInput.setAttribute('name', 'typeUuids');
        let typeUuid = null;
        if (Array.isArray(type)) {
          let val = [];
          for (let i = 0, len = type.length; i < len; i++) {
            val.push(`${type[i]}:${uuid[i]}`);
          }
          typeUuid = val.join(';');
        } else {
          typeUuid = `${type}:${uuid}`;
        }
        typeUuidInput.setAttribute('value', typeUuid);
        form.appendChild(typeUuidInput);

        let csrf = document.createElement('input');
        csrf.setAttribute('name', '_csrf');
        csrf.setAttribute('value', window.__INITIAL_STATE__.csrf);
        form.appendChild(csrf);

        let fileNameInput = document.createElement('input');
        fileNameInput.setAttribute('name', 'fileName');
        fileNameInput.setAttribute('value', encodeURIComponent(fileName));
        form.appendChild(fileNameInput);

        let requestIdInput = document.createElement('input'),
          requestId = generateId();
        requestIdInput.setAttribute('name', 'requestId');
        requestIdInput.setAttribute('value', requestId);
        form.appendChild(requestIdInput);

        form.submit();
        let t = setInterval(() => {
          if (getCookie(requestId)) {
            clearInterval(t);
            iframe.remove();
            resolve();
          }
        }, 1000);
        // <form
        //     :style="{ display: 'none' }"
        //     ref="form"
        //     method="post"
        //     target="_self"
        //     action="/proxy/common/iexport/service/exportDataDefinition"
        //   >
        //     <input type="hidden" name="typeUuids" v-model="typeUuids" />
        //     <input type="hidden" name="_csrf" :value="csrf" />
        //     <input type="hidden" name="fileName" :value="downloadFileName" />
        //     <input type="hidden" name="requestId" v-model="requestId" />
        //   </form>
      });
    },
    onChangeModifyDays() {
      this.filteredKeys.splice(0, this.filteredKeys.length);
      if (this.modifyDays == '') {
        this.treeData = JSON.parse(JSON.stringify(this.originalTreeData));
        this.treeKey = 'exportDefTreeKey_' + new Date().getTime();
        this.onCheckNode(this.treeChecked, {});
      } else {
        let list = [],
          _now = moment(this.now).startOf('day'),
          modifyDays = parseInt(this.modifyDays),
          checked = [];
        for (let o of this.originalData) {
          if (moment(o.version).startOf('day').diff(_now, 'days') >= modifyDays) {
            list.push(o);
            this.filteredKeys.push(o.id);
            if (this.treeChecked.checked.includes(o.id)) {
              checked.push(o.id);
            }
          }
        }
        this.treeData = list;
        this.treeKey = 'exportDefTreeKey_' + new Date().getTime();
        this.onCheckNode({ checked }, {});
      }
    },
    fetchExportDataDefTree() {
      this.treeData.splice(0, this.treeData.length);
      // 获取定义导出数据
      let uuids = Array.isArray(this.uuid) ? this.uuid : [this.uuid],
        types = Array.isArray(this.type) ? this.type : [];
      if (typeof this.type === 'string') {
        types = Array(uuids.length).fill(this.type);
      }
      $axios
        .post(`/proxy/common/iexport/service/getExportDataDefinitionTree`, {
          uuids,
          types,
          exportDependency: this.exportDependency,
          thread: true
        })
        .then(({ headers, data }) => {
          this.now = new Date(headers.date);
          this.loading = false;
          this.treeData = data;
          this.originalTreeData = JSON.parse(JSON.stringify(data));
          this.originalData.splice(0, this.originalData.length);
          this.allOriginalKeys.splice(0, this.allOriginalKeys.length);
          this.filteredKeys.splice(0, this.filteredKeys.length);
          this.expandedKeys.splice(0, this.expandedKeys.length);
          this.treeChecked.checked.splice(0, this.treeChecked.checked.length);
          this.indeterminate = false;
          this.treeNodeKeyMap = {};
          this.checkAll = false;
          if (this.treeData.length > 0) {
            this.expandedKeys.push(this.treeData[0].id);
            this.checkAll = true;
          }
          let tree2List = tree => {
            if (tree) {
              for (let t of tree) {
                this.allOriginalKeys.push(t.id);
                this.treeNodeKeyMap[t.id] = t;
                this.originalData.push({
                  id: t.id,
                  name: t.name,
                  type: t.type,
                  version: parseInt(t.version)
                });
                tree2List.call(this, t.children);
              }
            }
          };
          tree2List.call(this, this.treeData);
          if (this.checkAll) {
            this.treeChecked.checked.push(...this.allOriginalKeys);
          }
        })
        .catch(error => {
          this.loading = false;
          this.error = true;
          console.error(error);
        });
    }
  }
};
</script>
