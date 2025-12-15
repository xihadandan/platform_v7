<template>
  <Modal
    ref="importModal"
    :bodyStyle="bodyStyle"
    :title="title"
    :cancel="onCancel"
    :width="900"
    :height="600"
    :destroyOnClose="true"
    :visible="visible"
  >
    <slot>
      <a-button icon="import" type="default">导入</a-button>
    </slot>
    <template slot="content">
      <div class="def-data-import">
        <a-steps :current="currentStep">
          <a-step>
            <template slot="title">上传数据</template>
          </a-step>
          <a-step title="数据对比" description="" />
          <a-step title="数据导入" description="" />
        </a-steps>
        <p />
        <div class="steps-content">
          <div v-show="currentStep == 0">
            <slot name="upload-tip">
              <a-space>
                <font color="red">*</font>
                <span style="color: rgba(0, 0, 0, 0.85)">上传数据</span>
              </a-space>
              <p />
              <a-space>{{ uploadTipMessage }}</a-space>
              <p />
            </slot>
            <a-upload-dragger
              ref="uploadDragger"
              name="file"
              accept=".defpf"
              action="/repository/file/mongo/savefiles"
              @change="handleChange"
            >
              <a-icon type="check-circle" class="done-icon" style="display: none" />
              <p class="ant-upload-drag-icon">
                <a-icon type="cloud-upload" />
              </p>
              <p class="ant-upload-text">
                将定义文件拖拽至此区域或
                <a>选择文件</a>
              </p>
              <p class="ant-upload-hint">支持格式：.defpf</p>
            </a-upload-dragger>
          </div>
          <div v-show="currentStep == 1">
            <a-spin tip="导入中..." :spinning="importing">
              <a-alert type="info">
                <template slot="message">
                  <a-row type="flex">
                    <a-col flex="auto">
                      已载入{{ statistic.totalCount }}行数据，冲突数据{{ statistic.conflictCount }}行，异常数据{{ statistic.errorCount }}行
                    </a-col>
                    <a-col flex="70px">
                      <a-popover trigger="hover" placement="left">
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
                                <td>当前系统不存在该字典项，且导入数据中包含字典项的定义</td>
                                <td>新增导入</td>
                                <td style="color: black">黑色</td>
                              </tr>
                              <tr>
                                <td>重复数据</td>
                                <td>当前系统存在该字典项，且字典项在数据库中的版本号一致</td>
                                <td>不导入</td>
                                <td style="color: green">绿色</td>
                              </tr>
                              <tr>
                                <td>冲突数据</td>
                                <td>当前系统存在该字典项，但字典项在数据库中的版本号不一致</td>
                                <td>覆盖导入</td>
                                <td style="color: orange">橙色</td>
                              </tr>
                              <tr>
                                <td>异常数据</td>
                                <td>当前系统不存在该字典项，且导入数据中不包含字典项的定义或缺少依赖数据</td>
                                <td>全部无法导入</td>
                                <td style="color: red">红色</td>
                              </tr>
                            </tbody>
                          </table>
                        </template>
                        <span class="ant-alert-icon" style="top: 2px">
                          <a-icon style="color: #52adff" type="info-circle"></a-icon>
                          规则
                        </span>
                      </a-popover>
                    </a-col>
                  </a-row>
                </template>
              </a-alert>
              <p />
              <slot name="content" :treeData="treeData">
                <a-row>
                  <a-col>
                    <a-checkbox v-model="checkAll" @change="onCheckAllChange">全选</a-checkbox>
                    <a-tree
                      ref="defTree"
                      :tree-data="treeData"
                      show-icon
                      checkable
                      default-expand-all
                      :checkedKeys="checkedKeys"
                      :replaceFields="{ title: 'name', key: 'id' }"
                      @check="onTreeCheck"
                    >
                      <template slot="title" slot-scope="scope">
                        <span :style="{ color: scope.data.color }">{{ scope.name }}</span>
                      </template>
                    </a-tree>
                  </a-col>
                </a-row>
              </slot>
            </a-spin>
          </div>
          <div class="import-success" v-show="currentStep == 2">
            <slot name="import-success">
              <a-icon type="check-circle"></a-icon>
              <br />
              导入完成
            </slot>
          </div>
        </div>
      </div>
    </template>
    <template slot="footer">
      <a-button v-show="currentStep == 0" type="default" @click="onClose">取消</a-button>
      <a-button v-show="currentStep == 0" type="primary" :disabled="!uploadFile.fileID" @click="gotoNextStep(1)">下一步</a-button>
      <Modal
        ref="diffModal"
        :bodyStyle="{ padding: 0, ...bodyStyle }"
        title="差异数据对比"
        :width="900"
        :height="600"
        :destroyOnClose="true"
      >
        <a-button v-show="currentStep == 1" type="default" :disabled="importing">差异数据对比</a-button>
        <template slot="content">
          <DefDataImportDiff :uploadFile="uploadFile" :treeData="treeData"></DefDataImportDiff>
        </template>
        <template slot="footer">
          <a-button type="primary" @click="() => this.$refs.diffModal.hide()">关闭</a-button>
        </template>
      </Modal>
      <a-button v-show="currentStep == 1" type="default" :disabled="importing" @click="() => (currentStep = 0)">上一步</a-button>
      <a-button v-show="currentStep == 1" type="primary" :disabled="importing || !validData || statistic.errorCount > 0" @click="doImport">
        导入
      </a-button>
      <a-button v-show="currentStep == 2" type="primary" @click="onClose">关闭</a-button>
    </template>
  </Modal>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import DefDataImportDiff from './def-data-import-diff.vue';
export default {
  props: {
    title: {
      type: String,
      default: '数据导入'
    },
    bodyStyle: {
      type: Object,
      default() {
        return { height: '350px' };
      }
    },
    dataType: {
      type: String,
      default: '*' // 支持导入的数据类型
    },
    uploadTipMessage: {
      type: String,
      default: '请上传需导入的数据，同时只能导入一个文件'
    },
    import: Function
  },
  components: { Modal, DefDataImportDiff },
  data() {
    return {
      currentStep: 0,
      fileList: [],
      uploadFile: {},
      treeData: [],
      allKeys: [],
      checkAll: true,
      checkedKeys: [],
      importing: false,
      conflictColor: 'orange',
      errorColor: 'red',
      statistic: {
        totalCount: 0,
        conflictCount: 0,
        errorCount: 0
      },
      validData: true,
      visible: false
    };
  },
  created() {},
  methods: {
    show() {
      this.visible = true;
    },
    gotoNextStep(step) {
      if (step == 1 && !this.uploadFile.fileID) {
        this.$message.error('请先上传文件！');
        return;
      } else if (step == 1) {
        this.loadImportTree(this.uploadFile.fileID);
      }
      this.currentStep = step;
    },
    loadImportTree(fileID) {
      $axios
        .post('/json/data/services', {
          serviceName: 'iexportService',
          methodName: 'getImportTree',
          args: JSON.stringify([fileID])
        })
        .then(({ data }) => {
          if (data.data) {
            if (!this.validate(data.data)) {
              return;
            }
            this.treeData = data.data;
            this.allKeys = [];
            this.extractTreeKeys(this.treeData, this.allKeys);
            this.checkedKeys = [...this.allKeys];
            this.checkAll = true;
            this.statistic = {
              totalCount: 0,
              conflictCount: 0,
              errorCount: 0
            };
            this.extractStatistic(this.treeData, this.statistic);
            this.$emit('load', {
              treeData: this.treeData,
              statistic: this.statistic,
              fileID,
              extractTreeKeys: this.extractTreeKeys,
              extractStatistic: this.extractStatistic
            });
          }
        });
    },
    validate(data) {
      if (this.dataType == '*') {
        this.validData = true;
        return true;
      }
      if (data && data.length == 1) {
        if (data[0].data && data[0].data.type != this.dataType) {
          this.validData = false;
          this.$message.error(`上传的数据的类型不是有效的类型——${this.dataType}，请返回重新上传！`);
          return;
        }
      }
      this.validData = true;
      return true;
    },
    extractTreeKeys(treeNodes, allKeys) {
      treeNodes.forEach(item => {
        allKeys.push(item.id);
        this.extractTreeKeys(item.children || [], allKeys);
      });
    },
    extractStatistic(treeNodes, statistic) {
      treeNodes.forEach(item => {
        if (item.data) {
          statistic.totalCount++;
          if (item.data.color == this.conflictColor) {
            statistic.conflictCount++;
          } else if (item.data.color == this.errorColor) {
            statistic.errorCount++;
          }
        }
        this.extractStatistic(item.children || [], statistic);
      });
    },
    onCheckAllChange() {
      if (this.checkAll) {
        this.checkedKeys = [...this.allKeys];
      } else {
        this.checkedKeys = [];
      }
    },
    onTreeCheck(checkedKeys) {
      this.checkedKeys = checkedKeys;
      if (this.checkedKeys.length == this.allKeys.length) {
        this.checkAll = true;
      } else {
        this.checkAll = false;
      }
    },
    doImport() {
      let importIds = [...this.checkedKeys];
      if (importIds.length == 0) {
        this.$message.warning('请选择要导入的数据！');
        return;
      }

      let importing = importIds => {
        this.importing = true;
        $axios
          .post('/json/data/services', {
            serviceName: 'iexportService',
            methodName: 'importData',
            args: JSON.stringify([this.uploadFile.fileID, false, importIds.join(';')])
          })
          .then(({ data }) => {
            if (data.code == 0) {
              this.$message.success('导入成功！');
              this.currentStep = 2;
            }
            this.importing = false;
          })
          .catch(() => {
            this.importing = false;
          });
      };

      if (this.import) {
        this.import((ids = importIds) => {
          importing(ids);
        });
      } else {
        importing(importIds);
      }
    },
    handleChange(info) {
      if (info.file.status === 'done') {
        this.uploadFile = info.file.response.data[0];
        info.fileList.reverse();
        info.fileList.length = 1;
        this.$nextTick(() => {
          let nodes = this.$refs.uploadDragger.$el.querySelectorAll('.ant-upload-list-item-name');
          nodes.forEach(node => {
            let doneIcon = this.$refs.uploadDragger.$el.querySelector('.done-icon');
            let cloneIcon = doneIcon.cloneNode(true);
            cloneIcon.style.display = 'inline-block';
            node.append(cloneIcon);
          });
        });
      }
    },
    onClose() {
      this.$refs.importModal.hide();
      this.onCancel();
    },
    onCancel() {
      this.currentStep = 0;
      this.uploadFile = {};
      this.treeData = [];
      this.allKeys = [];
      this.checkedKeys = [];
      this.statistic = {
        totalCount: 0,
        conflictCount: 0,
        errorCount: 0
      };
    }
  }
};
</script>

<style lang="less" scoped>
.def-data-import {
  .import-success {
    font-size: 24px;
    text-align: center;
    .anticon {
      color: green;
    }
  }
  .data-info {
    background-color: #f6f8fa;

    .name {
      font-size: 24px;
      font-weight: bold;
    }
  }
  .done-icon {
    color: #52c41a;
    margin-left: 0.4em;
  }
}
</style>
