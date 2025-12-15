<template>
  <Modal
    :bodyStyle="bodyStyle"
    :title="title"
    :width="modalWidth"
    okText="导出"
    :ok="doExport"
    :destroyOnClose="true"
    @show="onModalShow"
    :visible="visible"
  >
    <slot>
      <a-button type="link" size="small">
        <Icon type="pticon iconfont icon-luojizujian-yemiantiaozhuan"></Icon>
        导出
      </a-button>
    </slot>
    <template slot="content">
      <div class="data-export">
        <slot name="content" :treeData="treeData">
          <a-row>
            <a-col>
              <a-checkbox v-model="checkAll" @change="onCheckAllChange">全选</a-checkbox>
              <a-tree
                ref="defTree"
                :tree-data="treeData"
                show-icon
                checkable
                :checkedKeys="checkedKeys"
                :replaceFields="{ title: 'name', key: 'id' }"
                @select="onTreeSelect"
                @check="onTreeCheck"
              >
                <template slot="title" slot-scope="scope">
                  <span>{{ scope.name }}</span>
                </template>
              </a-tree>
            </a-col>
          </a-row>
        </slot>
        <iframe style="display: none" id="download_iframe" name="download_iframe">
          <form ref="downloadForm" style="display: none" method="post" target="_self" :action="downloadUrl">
            <input type="text" name="fileName" v-model="downloadFields.fileName" />
            <input type="text" name="uuid" v-model="downloadFields.uuid" />
            <input type="text" name="type" v-model="downloadFields.type" />
            <input type="text" name="treeNodeIds" v-model="downloadFields.treeNodeIds" />
          </form>
        </iframe>
      </div>
    </template>
  </Modal>
</template>

<script>
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import { getCookie } from '@framework/vue/utils/util';
export default {
  props: {
    dataUuid: String | Array,
    dataType: String,
    title: {
      type: String,
      default: '数据导出'
    },
    bodyStyle: {
      type: Object,
      default() {
        return { height: '350px' };
      }
    },
    export: Function,
    modalWidth: {
      type: String | Number,
      default: 900
    }
  },
  components: { Modal },
  data() {
    let dataUuidString = Array.isArray(this.dataUuid) ? this.dataUuid.join(';') : this.dataUuid;
    return {
      treeData: [],
      allKeys: [],
      selectedKeys: [],
      checkedKeys: [],
      checkAll: true,
      loading: false,
      downloadUrl: '',
      downloadFields: {
        fileName: '',
        uuid: dataUuidString,
        type: this.dataType,
        treeNodeIds: ''
      },
      visible: false,
      dataUuidString
    };
  },
  methods: {
    show() {
      this.visible = true;
    },
    onModalShow() {
      this.loading = true;
      this.loadExportTree();
    },
    loadExportTree() {
      $axios
        .post('/json/data/services', {
          serviceName: 'iexportService',
          methodName: 'getExportTree',
          args: JSON.stringify([this.dataUuidString, this.dataType])
        })
        .then(({ data }) => {
          if (data.data) {
            this.treeData = data.data;
            this.allKeys = [];
            this.extractTreeKeys(this.treeData, this.allKeys);
            this.checkedKeys = [...this.allKeys];
            this.$emit('load', { treeData: this.treeData });
          }
        });
    },
    extractTreeKeys(treeNodes, allKeys) {
      treeNodes.forEach(item => {
        allKeys.push(item.id);
        this.extractTreeKeys(item.children || [], allKeys);
      });
    },
    onCheckAllChange() {
      if (this.checkAll) {
        this.checkedKeys = [...this.allKeys];
      } else {
        this.checkedKeys = [];
      }
    },
    onTreeSelect(selectedKeys) {
      this.selectedKeys = selectedKeys;
    },
    onTreeCheck(checkedKeys) {
      this.checkedKeys = checkedKeys;
      if (this.checkedKeys.length == this.allKeys.length) {
        this.checkAll = true;
      } else {
        this.checkAll = false;
      }
    },
    doExport(callback) {
      if (this.treeData.length == 0) {
        this.$message.warning('没有数据可导出！');
        return;
      }

      // 自定义导出
      if (this.export) {
        this.export(this.downloadFields, callback);
      } else {
        if (this.checkedKeys.length == 0) {
          this.$message.warning('请选择要导出的数据！');
          return;
        }
        this.downloadFields = {
          fileName: this.treeData[0].name,
          uuid: this.dataUuidString,
          type: this.dataType,
          treeNodeIds: [...this.checkedKeys].join(';')
        };
        this.download();
      }
    },
    download(downloadFields, callback) {
      if (downloadFields) {
        this.downloadFields = downloadFields;
      }

      let backendUrl = getCookie('backend.url');
      let downloadUrl = `${backendUrl}/common/iexport/service/exportData`;
      var _auth = getCookie('_auth');
      if (_auth) {
        downloadUrl += '?' + _auth + '=' + getCookie(_auth);
      }
      this.downloadUrl = downloadUrl;
      this.$nextTick(() => {
        this.$refs.downloadForm.submit();
        if (callback) {
          callback(true);
        }
      });
    }
  }
};
</script>

<style lang="less" scoped>
.data-export {
  .ant-tree-directory.tree-more-operations {
    li .ant-tree-node-content-wrapper {
      display: inline;
    }
  }
}
</style>
