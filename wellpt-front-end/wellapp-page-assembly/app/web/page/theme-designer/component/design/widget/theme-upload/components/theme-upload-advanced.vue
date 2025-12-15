<template>
  <div>
    <a-upload
      :file-list="fileList"
      :beforeUpload="
        () => {
          return false;
        }
      "
      :showUploadList="false"
    />
    <div class="widget-file-upload-advanced-list">
      <div v-if="headerButtonComputed.length" class="widget-file-upload-advanced-list-title">
        附件
        <span class="sub-title">({{ fileList.length }})</span>
      </div>
      <a-card size="small" style="width: 100%" :bordered="false">
        <template slot="title">
          <template v-if="!headerButtonComputed.length">
            <div class="widget-file-upload-advanced-list-title">
              附件
              <span class="sub-title">({{ fileList.length }})</span>
            </div>
          </template>
          <!-- 高级视图 头部按钮 -->
          <WidgetFormFileListButtons
            v-else
            class="btn_has_space"
            size="default"
            :buttons="headerButtonComputed"
            @listButtonClicked="onListButtonClicked"
            :collapseNumber="6"
            defalutType="line"
          />
        </template>
        <template slot="extra">
          <!-- 切换视图 -->
          <a-radio-group class="view-change-btn" v-model="advancedFileListType" button-style="solid" @change="onSwitchAdvancedView">
            <a-radio-button v-for="item in fileViewList" :value="item.value" :key="item.value" :title="item.label">
              <Icon :type="item.icon" />
            </a-radio-button>
          </a-radio-group>
        </template>
        <template v-if="advancedFileListType == 'iconView'">
          <a-empty v-show="fileList.length == 0" :image="emptyImg" description="暂无数据" />
          <a-row type="flex" class="widget-file-upload-icon-view" v-show="fileList.length > 0">
            <a-col v-for="(file, i) in fileList" :key="i" flex="100px" :gutter="12">
              <div :class="['widget-file-upload-icon-view-item', file.status]">
                <div>
                  <template v-if="file.showProgress && file.status == 'uploading'">
                    <a-progress
                      type="circle"
                      :percent="file.percent"
                      :width="30"
                      strokeColor="var(--w-primary-color)"
                      :strokeWidth="10"
                      :showInfo="false"
                      :status="file.status === 'error' ? 'exception' : 'active'"
                    />
                    <label class="label-filename" :title="file.name">{{ file.name }}</label>
                    <label>文件上传中</label>
                  </template>
                  <template v-else-if="file.status === 'error'">
                    <a-icon type="exclamation-circle" theme="filled" class="error-icon" />
                    <label class="label-filename" :title="file.name">{{ file.name }}</label>
                    <label :style="{ color: 'var(--w-danger-color)' }">上传错误</label>
                  </template>
                  <template v-else>
                    <a-checkbox
                      @change="ck => onAdvancedIconViewCheckChange(ck, file, i)"
                      :checked="rowSelection.selectedRowKeys.indexOf(file.uid) != -1"
                    />
                    <a-icon :type="file.icon" class="file-icon" />
                    <label class="label-filename" :title="file.name">{{ file.name }}</label>
                    <label class="label-filesize">{{ file.formatSize }}</label>
                    <a-popover
                      :title="file.name"
                      trigger="hover"
                      :visible="file.hovered"
                      :defaultVisible="defaultVisible"
                      @visibleChange="v => onFileItemHoverVisibleChange(v, file)"
                      :getPopupContainer="trigger => trigger.closest('.ant-card')"
                      overlayClassName="widget-file-upload-list"
                    >
                      <template slot="content">
                        <a-space :size="16">
                          <span class="file-item-desc">{{ file.formatSize }}</span>
                          <span class="file-item-desc">{{ file.dbFile.createTimeStr }}</span>
                          <span class="file-item-desc">{{ file.dbFile.userName }}</span>
                        </a-space>
                        <!-- 图标视图 行按钮 -->
                        <WidgetFormFileListButtons
                          v-show="file.status === 'done'"
                          :buttons="rowButtonsComputed"
                          display="block"
                          :collapseNumber="8"
                          @listButtonClicked="onListButtonClicked"
                          :file="file"
                          :fileIndex="i"
                        />
                      </template>
                      <a-button type="icon" size="small" icon="more" class="widget-file-upload-icon-view-more" />
                    </a-popover>
                  </template>
                  <div class="file-cancal-reupload">
                    <template v-if="file.status === 'error'">
                      <a-button class="file-reupload" icon="upload" size="small" @click="handleReUpload(file)" title="重新上传"></a-button>
                    </template>
                    <template v-if="true || file.status == 'uploading' || file.status === 'error'">
                      <a-button icon="delete" size="small" @click="handleCancelUpload(file, i)" title="取消上传"></a-button>
                    </template>
                  </div>
                </div>
              </div>
            </a-col>
          </a-row>
        </template>
        <template v-else>
          <!-- 列表视图、表格视图 -->
          <a-table
            size="small"
            :pagination="false"
            :bordered="false"
            rowKey="uid"
            :showHeader="advancedFileListType == 'tableView'"
            :dataSource="fileList"
            :columns="currentFileListColumns"
            :rowSelection="rowSelection"
            :locale="locale"
            :customRow="customRow"
            :class="`widget-file-upload-${advancedFileListType == 'tableView' ? 'table' : 'list'}-view`"
          >
            <template slot="fileNameSlot" slot-scope="text, record, index">
              <template v-if="advancedFileListType == 'tableView'">
                <template v-if="record.showProgress && record.status == 'uploading'">
                  <a-progress
                    type="circle"
                    :percent="record.percent"
                    width="var(--w-upload-list-text-size)"
                    strokeColor="var(--w-primary-color)"
                    :strokeWidth="10"
                    :showInfo="false"
                    :status="record.status === 'error' ? 'exception' : 'active'"
                  />
                </template>
                <template v-else-if="record.status === 'error'">
                  <a-icon type="exclamation-circle" theme="filled" class="error-icon" />
                </template>
                <a-icon v-else :type="record.icon" />
                <label class="label-filename" :style="{ color: record.status === 'error' ? 'var(--w-danger-color)' : '' }">
                  {{ record.name }}
                </label>
              </template>
              <template v-else>
                <div class="list-view-item">
                  <template v-if="record.showProgress && record.status == 'uploading'">
                    <a-progress
                      type="circle"
                      :percent="record.percent"
                      width="var(--w-upload-list-text-size)"
                      strokeColor="var(--w-primary-color)"
                      :strokeWidth="10"
                      :showInfo="false"
                      :status="record.status === 'error' ? 'exception' : 'active'"
                    />
                  </template>
                  <template v-else-if="record.status === 'error'">
                    <a-icon type="exclamation-circle" theme="filled" class="error-icon" />
                  </template>
                  <a-icon v-else :type="record.icon" />
                  <label class="label-filename" :style="{ color: record.status === 'error' ? 'var(--w-danger-color)' : '' }">
                    {{ record.name }}
                  </label>
                  <span class="list-view-extra-info">
                    (
                    <a-space :size="16">
                      <span class="file-item-desc">{{ record.formatSize }}</span>
                      <span class="file-item-desc">{{ record.dbFile.createTimeStr }}</span>
                      <span class="file-item-desc">{{ record.dbFile.userName }}</span>
                    </a-space>
                    )
                  </span>
                  <template v-if="record.status === 'error'">
                    <a-button type="link" size="small" @click="handleReUpload(record)">重新上传</a-button>
                  </template>
                  <template v-if="record.status == 'uploading' || record.status === 'error'">
                    <a-button type="link" size="small" @click="handleCancelUpload(record, index)">取消上传</a-button>
                  </template>
                  <div class="file-list-item-buttons-absolute" v-if="record.status === 'done'">
                    <span class="file-list-item-buttons">
                      <!-- 列表视图 行按钮 -->
                      <WidgetFormFileListButtons
                        visibleTrigger="hover"
                        :buttons="rowButtonsComputed"
                        @listButtonClicked="onListButtonClicked"
                        :file="record"
                        :fileIndex="index"
                      />
                    </span>
                  </div>
                </div>
              </template>
            </template>
            <template slot="fileSizeSlot" slot-scope="text, record">{{ record.formatSize }}</template>
            <template slot="uploadTimeSlot" slot-scope="text, record">{{ record.dbFile.createTimeStr }}</template>
            <template slot="uploaderSlot" slot-scope="text, record">{{ record.dbFile.userName }}</template>
            <template slot="operationSlot" slot-scope="text, record, index">
              <template v-if="record.status === 'error'">
                <a-button type="link" size="small" @click="handleReUpload(record)">重新上传</a-button>
              </template>
              <template v-if="record.status == 'uploading' || record.status === 'error'">
                <a-button type="link" size="small" @click="handleCancelUpload(record, index)">取消上传</a-button>
              </template>
              <!-- 表格视图 行按钮 -->
              <WidgetFormFileListButtons
                v-show="record.status === 'done'"
                display="inline"
                :buttons="rowButtonsComputed"
                @listButtonClicked="onListButtonClicked"
                :file="record"
                :fileIndex="index"
              />
            </template>
          </a-table>
        </template>
      </a-card>
    </div>
  </div>
</template>
<script type="text/babel">
import { UPLOAD_STATUS, headerBtnAdvanced, rowBtnAdvanced, advancedFileListTypeOptions } from './config';
import { Empty } from 'ant-design-vue';
import WidgetFormFileListButtons from '@dyformWidget/widget-form-file-upload/widget-form-file-list-buttons';
export default {
  name: 'ThemeUploadAdvanced',
  props: {
    fileListProp: {
      type: Array,
      default: () => []
    },
    defaultVisible: {
      type: Boolean,
      default: false
    },
    /*  advancedViewList: {
      type: Array,
      default: () => ["listView","tableView","iconView"]
    }, */
    advancedFileListType: {
      type: String,
      default: 'listView'
    }
  },
  components: {
    WidgetFormFileListButtons
  },
  computed: {
    // 当前 列表视图、表格视图 的显示列
    currentFileListColumns() {
      if (this.advancedFileListType === 'tableView') {
        return this.fileListColumns;
      } else {
        return [this.fileListColumns[0]];
      }
    },
    // 当前选中视图名称
    currentSelectFileViewTypeName() {
      return { listView: '列表视图', tableView: '表格视图', iconView: '图标视图' }[this.advancedFileListType];
    },
    // 视图列表
    fileViewList() {
      this.advancedViewList = this.advancedFileListType;
      return this.advancedFileListTypeOptions.filter(item => {
        item.icon = this.getFileViewTypeIcon(item.value);
        return this.advancedViewList.includes(item.value);
      });
    },
    // 头部按钮
    headerButtonComputed() {
      let headerButton,
        btnShowType = 'show';
      if (!this.displayAsLabel) {
        btnShowType = 'edit';
      }
      headerButton = this.headerButton.filter(item => {
        item.type = 'line';
        return item.defaultFlag && btnShowType === item.btnShowType;
      });
      return headerButton;
    },
    // 行按钮
    rowButtonsComputed() {
      let rowButton,
        btnShowType = 'show';
      if (!this.displayAsLabel) {
        btnShowType = 'edit';
      }
      rowButton = this.rowButton.filter(item => {
        return item.defaultFlag && btnShowType === item.btnShowType;
      });
      return rowButton;
    }
  },
  data() {
    return {
      fileList: [],
      headerButton: headerBtnAdvanced,
      rowButton: rowBtnAdvanced,
      advancedFileListTypeOptions,
      displayAsLabel: false,
      editable: true,
      emptyImg: Empty.PRESENTED_IMAGE_SIMPLE,
      rowSelection: {
        columnWidth: 30,
        selectedRowKeys: [],
        selectedRows: [],
        onChange: this.onSelectFileRowChange
      },
      locale: {},
      fileListColumns: [
        { title: '文件名', dataIndex: 'fileName', scopedSlots: { customRender: 'fileNameSlot' } },
        { title: '大小', dataIndex: 'fileSize', width: 100, scopedSlots: { customRender: 'fileSizeSlot' } },
        { title: '上传时间', dataIndex: 'uploadTime', width: 150, scopedSlots: { customRender: 'uploadTimeSlot' } },
        { title: '上传人', dataIndex: 'uploader', width: 100, scopedSlots: { customRender: 'uploaderSlot' } },
        { title: '操作', dataIndex: 'operation', width: 300, scopedSlots: { customRender: 'operationSlot' } }
      ]
    };
  },
  created() {
    this.fileList = this.fileListProp;
  },
  methods: {
    // 获取视图图标
    getFileViewTypeIcon(type) {
      return { listView: 'menu', tableView: 'table', iconView: 'appstore' }[type];
    },
    // 按钮功能转发
    onListButtonClicked(params) {
      /* let { button } = params;
      if (button && button.code) {
        this[button.code](params);
      } */
    },
    // 设置行属性
    customRow(row, index) {
      return {
        on: {
          // 事件
          click: event => {
            // 行单击选中
          },
          // dblclick: event => {},
          // contextmenu: event => {},
          mouseenter: event => {
            row.hovered = true;
          }, // 鼠标移入行
          mouseleave: event => {
            row.hovered = false;
          }
        }
      };
    },
    // 选中附件
    onSelectFileRowChange(selectedRowKeys, selectedRows) {
      this.rowSelection.selectedRowKeys = selectedRowKeys;
      this.rowSelection.selectedRows = selectedRows;
    },
    // 切换高级视图（列表、表格、图标）
    onSwitchAdvancedView(selected) {},
    onAdvancedIconViewCheckChange(checked, file, index) {},
    onFileItemHoverVisibleChange(v, file) {
      if (file.dropdownButtonVisible === true) {
        file.hovered = true;
        return;
      }
      file.hovered = file.status == UPLOAD_STATUS.DONE && v;
    }
  }
};
</script>

<style></style>
