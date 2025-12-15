<template>
  <Modal
    :title="title"
    v-model="visible"
    :escKeyClosable="false"
    :closable="closable"
    @show="onShow"
    :cancel="onCancel"
    :width="width"
    :bodyStyle="{
      height: '500px',
      maxHeight: '500px'
    }"
    ref="importDefModalRef"
  >
    <template slot="default">
      <slot name="default"></slot>
    </template>
    <template slot="content">
      <div style="min-height: 200px">
        <!-- <div class="spin-center">
          <a-spin :tip="tip" />
        </div> -->
        <div class="import-def-container">
          <a-steps :current="currentStep" class="import-def-steps" labelPlacement="vertical">
            <a-step v-for="(item, i) in steps" :key="item.title" :title="item.title">
              <a-icon slot="icon" type="loading" v-if="i == 1 && currentStep == 1 && dataDealing" />
            </a-step>
          </a-steps>
          <a-card
            v-show="currentStep == 0"
            :bordered="false"
            :bodyStyle="{
              height: '300px'
            }"
          >
            <a-upload-dragger
              :fileList="fileList"
              ref="uploadDragger"
              name="file"
              accept=".def"
              :action="'/proxy/common/iexport/service/uploadDefFile?_csrf=' + csrf"
              @change="onUploadChange"
              :beforeUpload="beforeUpload"
              :remove="onRemoveUploadFile"
            >
              <a-icon type="check-circle" class="done-icon" style="display: none" />
              <p class="ant-upload-drag-icon">
                <a-icon type="cloud-upload" />
              </p>
              <p class="ant-upload-text">
                将定义文件拖拽至此区域或
                <a>选择文件</a>
              </p>
              <p class="ant-upload-hint">支持格式：.def</p>
            </a-upload-dragger>
          </a-card>
          <a-card
            v-show="currentStep == 1"
            :bordered="false"
            :bodyStyle="{
              position: 'relative',
              overflowX: 'hidden',
              width: '100%'
            }"
          >
            <a-alert type="info" banner :showIcon="false" style="margin-bottom: 8px">
              <template slot="message">
                已载入 {{ treeData.length }} 行数据, 冲突数据 {{ conflictCount }} 行, 异常数据 {{ errorCount }} 行
                <a-popover :title="null">
                  <a-button size="small" type="link" icon="info-circle" style="float: right">规则</a-button>
                  <template slot="content">
                    <table>
                      <thead>
                        <tr>
                          <td style="width: 80px">比对结果</td>
                          <td>比对规则</td>
                          <td style="width: 100px">处理规则</td>
                          <td>标识颜色</td>
                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          <td>新数据</td>
                          <td>当前系统不存在该数据项</td>
                          <td>新增导入</td>
                          <td
                            :style="{
                              color: statusColor.newData
                            }"
                          >
                            黑色
                          </td>
                        </tr>
                        <tr>
                          <td>重复数据</td>
                          <td>当前系统存在该数据项, 且与该数据项在数据库中的一致</td>
                          <td>不导入</td>
                          <td
                            :style="{
                              color: statusColor.duplicate
                            }"
                          >
                            绿色
                          </td>
                        </tr>
                        <tr>
                          <td>冲突数据</td>
                          <td>当前系统存在该数据项, 但与该数据项不一致(可以点击数据项查阅差异性)</td>
                          <td>覆盖导入</td>
                          <td
                            :style="{
                              color: statusColor.conflict
                            }"
                          >
                            橙色
                          </td>
                        </tr>
                        <tr>
                          <td>异常数据</td>
                          <td>数据项处理异常, 或者数据项定义数据异常</td>
                          <td>无法导入</td>
                          <td
                            :style="{
                              color: statusColor.error
                            }"
                          >
                            红色
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </template>
                </a-popover>
              </template>
            </a-alert>
            <a-progress v-show="uploadingData" status="normal" :percent="percent" style="margin-bottom: 8px" />
            <DraggableTreeList
              selectMode="multiple"
              v-if="!loading"
              v-model="treeData"
              :draggable="false"
              :dragButton="false"
              expandIcon="plus"
              :title-width="300"
              ref="treeList"
              titleField="name"
              keyField="uuid"
              @mounted="onMounted"
              :key="importTreeKey"
              :afterSelected="afterSelected"
            >
              <template slot="title" slot-scope="scope">
                <span
                  :style="{
                    cursor: 'pointer',
                    color: statusColor[scope.item.status] || 'inherit'
                  }"
                  @click.stop="openDiffDrawer(scope.item)"
                >
                  {{ scope.item.name }}
                </span>
              </template>
              <template slot="operation" slot-scope="scope">
                <template v-if="rowMap[scope.item.uuid].status == 'error'">
                  <a-tag color="red">异常原因</a-tag>
                  <span
                    v-if="rowMap[scope.item.uuid].errorMsg"
                    :title="rowMap[scope.item.uuid].errorMsg"
                    style="display: inline-block; max-width: 150px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis"
                  >
                    {{ rowMap[scope.item.uuid].errorMsg }}
                  </span>
                </template>
                <a-icon type="loading" v-show="rowMap[scope.item.uuid].loading" />
                <a-icon
                  type="check-circle"
                  v-show="!rowMap[scope.item.uuid].loading && rowMap[scope.item.uuid].status == 'success'"
                  theme="filled"
                  style="color: #52c41a"
                />
              </template>
            </DraggableTreeList>
            <Drawer
              v-model="diffDrawerVisible"
              :container="getDrawerContainer"
              :mask="true"
              :maskClosable="true"
              :destroyOnClose="true"
              :wrapStyle="{ position: 'absolute', height: 'calc(100% - 100px)', top: '45px' }"
            >
              <template slot="title">
                <div style="display: flex; align-items: center">
                  <label>差异数据对比</label>
                  <a-tag :title="diffName" style="margin-left: 10px; max-width: 200px; overflow: hidden; text-overflow: ellipsis">
                    {{ diffName }}
                  </a-tag>
                </div>
              </template>
              <template slot="content">
                <a-table
                  :customRow="customRow"
                  size="small"
                  :scroll="{ y: 257 }"
                  :bordered="true"
                  row-key="field"
                  :data-source="diffRows"
                  :columns="diffColumns"
                  :pagination="false"
                />
              </template>
            </Drawer>
          </a-card>
          <a-card v-show="currentStep == 2" :bordered="false">
            <a-result status="success" title="导入完成!">
              <template slot="subTitle">成功导入数据 {{ successImportCount }} 条, 失败数据 {{ errorImportCount }} 条</template>
            </a-result>
          </a-card>
        </div>
      </div>
    </template>
    <template slot="footer">
      <div style="display: flex; align-items: center; justify-content: space-between">
        <div>
          <a-checkbox v-show="currentStep == 1" :indeterminate="indeterminate" :checked="checkAll" @change="onCheckAllChange">
            全选
          </a-checkbox>
        </div>
        <div>
          <a-button
            type="primary"
            icon="right-square"
            @click="e => nextStep(1)"
            v-show="currentStep == 0"
            :disabled="fileList.length == 0 || loading"
          >
            下一步
          </a-button>
          <a-button icon="left-square" @click="e => nextStep(-1)" v-show="currentStep == 1 || currentStep == 2">上一步</a-button>
          <a-button
            type="primary"
            :loading="uploadItems.length > 0"
            v-show="currentStep == 1"
            icon="import"
            @click="importSelectedData"
            :disabled="dataDealing || uploadItems.length > 0"
          >
            导入
          </a-button>
          <a-button type="primary" icon="right-square" @click="e => nextStep(1)" v-show="currentStep == 1 && uploadFInished">
            下一步
          </a-button>
          <a-button type="primary" v-show="currentStep == 2" icon="check-square" @click="finishImport">完成</a-button>
        </div>
      </div>
    </template>
  </Modal>
</template>
<style lang="less">
.import-def-container {
  .import-def-steps {
    width: e('calc(100% - 150px)');
    margin: 0 auto;
    --w-steps-title-color: var(--w-text-color-darker);
    --w-steps-wait-title-color: var(--w-text-color-darker);

    .ant-steps-item-icon {
      width: 36px;
      height: 36px;
      line-height: 36px;
    }
    .ant-steps-item-title {
      font-weight: bold;
    }
    .ant-steps-item-wait {
      .ant-steps-item-title {
        color: var(--w-steps-title-color);
      }
    }
    &:not(.ant-steps-vertical) .ant-steps-item-custom .ant-steps-item-icon {
      width: 36px;
    }
  }
}
</style>
<script type="text/babel">
import Modal from '../modal.vue';
import Drawer from '../drawer.vue';
import DraggableTreeList from '@pageAssembly/app/web/widget/commons/draggable-tree-list';
export default {
  name: 'ImportDef',
  inject: ['csrf'],
  props: {
    title: {
      type: String,
      default: '导入定义'
    },
    filterType: String | Array,
    width: { type: Number, default: 850 }
  },
  components: { Modal, DraggableTreeList, Drawer },
  computed: {},
  data() {
    if (!this.csrf) {
      this.csrf = window.__INITIAL_STATE__.csrf;
    }
    return {
      percent: 0,
      fileList: [],
      currentStep: 0,
      dataDealing: false,
      visible: false,
      errorCount: 0,
      conflictCount: 0,
      successImportCount: 0,
      errorImportCount: 0,
      uploadFInished: false,
      indeterminate: false,
      checkAll: false,
      closable: true,
      steps: [
        {
          title: '上传数据'
        },
        {
          title: '数据对比'
        },
        {
          title: '数据导入'
        }
      ],
      treeData: [],
      loading: true,
      uploadingData: false,
      diffDrawerVisible: false,
      diffName: undefined,
      diffItem: { importData: undefined, currentData: undefined },
      importTreeKey: 'importDefTreeList_0000',
      statusColor: {
        conflict: 'orange',
        error: 'red',
        duplicate: 'green',
        newData: '#000'
      },
      diffRows: [],
      rowMap: {},
      uploadItems: [],
      diffColumns: [
        {
          dataIndex: 'field',
          title: '字段',
          width: 150
        },
        {
          dataIndex: 'importVal',
          title: '已上传数据'
        },
        {
          dataIndex: 'currentVal',
          title: '系统数据'
        }
      ]
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    afterSelected(item, keys) {
      this.indeterminate = keys.length && keys.length < this.$refs.treeList.allKeys.length;
      this.checkAll = keys.length && keys.length == this.$refs.treeList.allKeys.length;
    },
    onCheckAllChange(e) {
      this.checkAll = e.target.checked;
      if (e.target.checked) {
        this.$refs.treeList.selectAll();
      } else {
        this.$refs.treeList.setSelectedKeys([]);
      }
      this.indeterminate = false;
    },
    customRow(record) {
      let style = {};
      if (record.diff) {
        style.backgroundColor = '#fff1f0';
        style.boxShadow = '0px 0px 3px 1px #eba5a5';
      }
      return {
        style
      };
    },
    openDiffDrawer(item) {
      if (item.status == 'conflict') {
        this.diffDrawerVisible = true;
        let importData = JSON.parse(item.importData);
        let currentData = JSON.parse(item.currentData);
        this.diffRows.splice(0, this.diffRows.length);
        this.diffName = item.name;
        for (let key in importData) {
          this.diffRows.push({
            field: key,
            importVal: importData[key],
            currentVal: currentData[key],
            diff: importData[key] != currentData[key]
          });
        }
        for (let key in currentData) {
          if (importData[key] == undefined) {
            this.diffRows.push({
              field: key,
              importVal: undefined,
              currentVal: currentData[key],
              diff: true
            });
          }
        }
      } else {
        this.diffDrawerVisible = false;
      }
    },
    show() {
      this.visible = true;
    },
    onShow() {
      this.currentStep = 0;
      this.loading = true;
      this.fileList.splice(0, this.fileList.length);
      this.treeData.splice(0, this.treeData.length);
      this.successImportCount = 0;
      this.errorImportCount = 0;
      this.rowMap = {};
      this.percent = 0;
      this.closable = true;
      this.uploadingData = false;
      this.uploadFInished = false;
      this.diffDrawerVisible = false;
      this.diffRows.splice(0, this.diffRows.length);
    },
    onCancel() {},
    finishImport() {
      this.visible = false;
      //TODO: 触发导入完成的逻辑，如果导入角色、权限、页面信息等，就需要刷新权限资源缓存数据
      $axios.post(`/proxy/api/security/role/publishRoleUpdatedEvent?reloadAll=true`);
      this.$emit('importDone');
    },
    nextStep(step) {
      this.currentStep = this.currentStep + step;
      if (this.currentStep == 1) {
        this.loading = false;
        this.diff;
      }
      if (this.currentStep == 0) {
        this.diffDrawerVisible = false;
      }
    },
    onMounted() {
      // this.$refs.treeList.selectAll();
    },
    importSelectedData() {
      let selectedItems = this.$refs.treeList.getSelectedItems();
      if (selectedItems.length) {
        this.uploadItems.splice(0, this.uploadItems.length);
        this.successImportCount = 0;
        this.errorImportCount = 0;
        let done = 0;
        this.percent = 0;
        this.closable = false; // 发生了导入行为，则必须进行下一步完成动作，禁止通过 x 关闭弹窗
        let dataModels = [];
        let _importList = (list, filter) => {
          return new Promise((resolve, reject) => {
            for (let item of list) {
              if (filter && item.type == 'dataModel') {
                dataModels.push(item);
                this.rowMap[item.uuid].loading = true;
                continue;
              }

              this.importRecord(item).then(() => {
                done++;
                this.percent = Number(((done / selectedItems.length) * 100).toFixed(2));
                if (dataModels.length > 0 && selectedItems.length - dataModels.length == done) {
                  resolve();
                }
              });
            }
            if (dataModels.length == selectedItems.length) {
              resolve();
            }
          });
        };
        _importList.call(this, selectedItems, true).then(() => {
          if (dataModels.length) {
            // 数据模型分批到最后导入，避免数据库冲突
            _importList.call(this, dataModels, false);
          }
        });
      } else {
        this.$message.info('请勾选要导入的数据');
      }
    },
    importRecord(item) {
      this.uploadingData = true;
      this.uploadItems.push(item.uuid);
      this.rowMap[item.uuid].loading = true;
      this.rowMap[item.uuid].errorMsg = undefined;
      this.rowMap[item.uuid].status = undefined;
      return new Promise((resolve, reject) => {
        let form = {
          type: item.type,
          uuid: item.uuid,
          filename: item.filename,
          filePhysicalID: item.filePhysicalID,
          attachment: item.attachment,
          name: item.name
        };
        $axios
          .post(`/proxy/common/iexport/service/importDefData`, [form])
          .then(({ data }) => {
            this.rowMap[item.uuid].loading = false;
            this.uploadItems.splice(this.uploadItems.indexOf(item.uuid), 1);
            resolve();
            for (let d of data.data) {
              if (d.uuid == item.uuid) {
                this.rowMap[d.uuid].status = d.status;
                if (d.status == 'success') {
                  this.successImportCount++;
                } else {
                  this.errorImportCount++;
                }
                if (d.errorMsg) {
                  this.rowMap[d.uuid].errorMsg = d.errorMsg;
                }
              }
            }
          })
          .catch(error => {});
      });
    },
    beforeUpload(file) {
      return file.name.endsWith('.def');
    },
    onRemoveUploadFile() {
      this.fileList.splice(0, 1);
    },
    onUploadChange(info) {
      if (info.file.status == undefined) {
        info.fileList.splice(0, info.fileList.length);
      } else if (info.file.status == 'uploading') {
        this.fileList = info.fileList;
        this.loading = true;
        if (info.fileList.length > 1) {
          info.fileList.splice(0, 1);
        }
      } else if (info.file.status === 'done') {
        this.treeData.splice(0, this.treeData.length);
        let fileIndexes = info.file.response.fileIndexes;
        if (this.filterType != undefined) {
          for (let f of fileIndexes) {
            if (this.filterType == f.type || (Array.isArray(this.filterType) && this.filterType.includes(f.type))) {
              this.treeData.push(f);
            }
          }
        } else {
          this.treeData.push(...fileIndexes);
        }
        this.loading = false;
        this.conflictCount = 0;
        this.errorCount = 0;
        this.dataDealing = true;
        this.rowMap = {};
        for (let t of this.treeData) {
          this.$set(this.rowMap, t.uuid, {
            status: undefined,
            loading: false
          });
        }
        this.diffCompare(this.treeData).then(data => {
          let map = {};
          for (let t of this.treeData) {
            map[t.uuid] = t;
          }
          let importKeys = [];
          for (let d of data) {
            map[d.uuid].status = d.status;
            map[d.uuid].loading = false;
            if (d.status == 'newData' || d.status == 'conflict') {
              importKeys.push(d.uuid);
            }
            if (d.status == 'conflict') {
              this.conflictCount++;
              map[d.uuid].importData = d.importData;
              map[d.uuid].currentData = d.currentData;
            }
            if (d.status == 'error') {
              this.errorCount++;
            }
          }

          this.importTreeKey = 'importDefTreeList_' + new Date().getTime();
          this.$nextTick(() => {
            this.$refs.treeList.setSelectedKeys(importKeys);
            this.dataDealing = false;
            this.checkAll = importKeys.length && importKeys.length == this.treeData.length;
            this.indeterminate = importKeys.length && importKeys.length < this.treeData.length;
          });
        });
      }
    },
    diffCompare(param) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/common/iexport/service/compareImportDataDiffToCurrent`, param)
          .then(({ data }) => {
            console.log('差异性数据比较返回: ', data.data);
            resolve(data.data);
          })
          .catch(error => {});
      });
    },
    getDrawerContainer() {
      return this.$refs.importDefModalRef.$refs.modalComponentRef.$el.querySelector('.ant-modal-body');
    }
  },
  watch: {
    uploadItems: {
      deep: true,
      handler(v) {
        if (v.length == 0) {
          this.uploadFInished = true;
        }
      }
    }
  }
};
</script>
