<template>
  <div class="file-card-view" @mouseenter="e => (showCheckbox = true)" @mouseleave="e => (showCheckbox = false)">
    <a-card hoverable>
      <a-checkbox
        v-if="multiSelect && (showCheckbox || checked)"
        v-model="checked"
        class="file-checkbox"
        @change="onFileSelectionChange"
        @click.stop="e => {}"
      ></a-checkbox>
      <a-popover :title="row.name" :mouseEnterDelay="0.6">
        <template slot="content">
          <p>文件类型：{{ getFileTypeName(row) }}</p>
          <p>创建时间：{{ row.createTime }}</p>
        </template>
        <a-icon :style="iconStyle" :type="getFileIcon(row)"></a-icon>
        <p />
        <a-card-meta class="file-name-container" :title="row.name"></a-card-meta>
      </a-popover>
    </a-card>
  </div>
</template>

<script>
import { getFileIcon } from '@framework/vue/lib/preview/filePreviewApi';
export default {
  props: {
    row: Object,
    eventWidget: Function,
    rowIndex: Number
  },
  data() {
    return {
      checked: false,
      showCheckbox: false,
      iconStyle: {
        fontSize: '50px'
      },
      tableWidget: this.eventWidget()
    };
  },
  computed: {
    multiSelect() {
      return this.tableWidget.widget.configuration.rowSelectType == 'checkbox';
    },
    selectedRows() {
      return this.tableWidget.selectedRows;
    }
  },
  watch: {
    'tableWidget.selectedRows': {
      deep: true,
      handler: function () {
        this.updateCheckState();
      }
    }
  },
  mounted() {
    this.updateCheckState();
  },
  methods: {
    getFileIcon(file) {
      return this.isFolder(file) ? 'folder' : getFileIcon(file.name);
    },
    isFolder(file) {
      return 'application/folder' == file.contentType;
    },
    isDyform(file) {
      return 'application/dyform' == file.contentType;
    },
    getFileTypeName(file) {
      const _this = this;
      if (_this.isFolder(file)) {
        return '文件夹';
      }
      if (_this.isDyform(file)) {
        return '表单文件';
      }

      let fileTypeName = file.contentType || '其他';
      let fileExtension = _this.getFileExtension(file.name);

      return fileExtension ? fileExtension.toUpperCase() + ' 文件' : fileTypeName;
    },
    getFileExtension(fileName) {
      var lfn = fileName.toLowerCase();
      var len = lfn.length;
      var start = lfn.lastIndexOf('.');
      if (start !== -1) {
        var ext = lfn.substring(start + 1, len);
        return ext;
      }
      return '';
    },
    updateCheckState() {
      this.checked = this.selectedRows.findIndex(selected => selected.uuid == this.row.uuid) != -1;
    },
    onFileSelectionChange() {
      const _this = this;
      if (_this.checked) {
        _this.tableWidget.addSelectedRows([_this.row]);
      } else {
        _this.tableWidget.cancelSelectRowByIndex(_this.rowIndex);
      }
    }
  }
};
</script>

<style lang="less" scoped>
.file-card-view {
  text-align: center;

  .file-name-container ::v-deep .ant-card-meta-title {
  }

  .file-checkbox {
    position: absolute;
    left: 5px;
    top: 5px;
  }
}
</style>
