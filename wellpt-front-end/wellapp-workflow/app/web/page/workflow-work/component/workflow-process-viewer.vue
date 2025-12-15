<template>
  <div class="process-viewer">
    <WorkflowTaskProcess :workView="workView" :timeout="500"></WorkflowTaskProcess>
    <PerfectScrollbar ref="scrollbar" :style="{ ...timelineStyle, padding: '12px ' }">
      <WidgetWorkProcess v-if="standardWidget" :widget="standardWidget"></WidgetWorkProcess>
    </PerfectScrollbar>

    <!-- 办理过程弹出框 -->
    <a-modal
      v-model="processModalVisible"
      :title="$t('WorkflowWork.siderbar.viewProcess', '办理过程')"
      width="900px"
      dialogClass="pt-modal"
      :bodyStyle="{ height: '480px' }"
      :maskClosable="false"
      :footer="null"
      @cancel="processModalVisible = false"
    >
      <WidgetWorkProcess v-if="processWidget" :widget="processWidget"></WidgetWorkProcess>
    </a-modal>
  </div>
</template>
<script type="text/babel">
import { isEmpty, trim as stringTrim, each as forEach, findIndex, set } from 'lodash';
import { preview, getFileIcon } from '@framework/vue/lib/preview/filePreviewApi';
import { download } from '@framework/vue/utils/util';
import WidgetWorkProcess from '../../../widget/widget-work-process/widget-work-process.vue';
import '../../../widget/widget-work-process/css/index.less';

export default {
  name: 'WorkflowProcessViewer',
  inject: ['pageContext'],
  props: {
    workView: Object
  },
  components: { WidgetWorkProcess },
  data() {
    return {
      opinionPositionConfig: [],
      items: [],
      timelineStyle: {
        height: '500px'
      },
      processViewerSetting: null,
      processModalVisible: false,
      processWidget: null
    };
  },
  computed: {
    standardWidget() {
      let widget = this.workView.workProcess.getWidgetConfiguration();
      widget.configuration.allowSwitchDisplayStyle = false;
      widget.configuration.displayStyle = 'standard';
      widget.configuration.search.keywordSearchEnable = false;
      widget.configuration.search.locateCurrentUserRecord = false;
      widget.configuration.search.locateCurrentTaskRecord = false;
      widget.configuration.enabledSort = false;
      return widget;
    },
    isOver() {
      return this.workView.isOver();
    }
  },
  created() {
    const _self = this;
    if (EASY_ENV_IS_NODE) {
      return;
    }
    const workView = _self.workView;
    const workData = workView.getWorkData();
    const flowInstUuid = workData.flowInstUuid;
    if (!isEmpty(flowInstUuid)) {
      workView.getWorkProcess().then(data => {
        if (data) {
          _self.opinionPositionConfig = data.opinionPositionConfig;
          _self.items = data.items;
        }
      });
    }

    _self.workView.getSettings().then(settings => {
      _self.processViewerSetting = settings.get('PROCESS_VIEWER') || {};
    });
  },
  beforeMount() {
    this.workView.setProcessViewer(this);
  },
  mounted() {
    this.timelineStyle.height = window.innerHeight - 120 + 'px';
    this.pageContext.handleEvent('updateWorkProcessScrollbar', () => {
      this.$refs.scrollbar.update();
    });
  },
  methods: {
    getDisplayUsername: function (detail) {
      let username = stringTrim(detail.assignee);
      if (isEmpty(username)) {
        return username;
      }
      return username.replaceAll(';', '，');
    },
    getDisplayJobName: function (detail) {
      let deptName = stringTrim(detail.deptName);
      let mainJobName = stringTrim(detail.mainJobName);
      if (isEmpty(deptName) && isEmpty(mainJobName)) {
        return '';
      }
      return '(' + deptName + ' ' + mainJobName + ')';
    },
    getDetailOpinion: function (detail) {
      return isEmpty(detail.opinion)
        ? detail.actionCode != 36
          ? this.$t('WorkflowWork.opinionManager.message.noWriteOpinion', '未填写办理意见')
          : ''
        : detail.opinion;
    },
    isCurrentTask: function (item) {
      return item.taskStatus === '未完成';
    },
    getActionInfo(detail) {
      let actionInfo = null;
      let actionTime = detail.endTime || '';
      let actionName = detail.actionName || this.$t('WorkflowWork.operation.Submit', '提交');
      let actionType = detail.actionType || 'Submit';
      let actionObjects = '';
      if (actionType == 'GotoTask') {
        let fromTaskName = detail.suspensionState == 2 ? this.$t('WorkflowWork.endTaskName', '结束') : detail.taskName;
        let toTaskName = detail.gotoTaskName;
        actionInfo = `<span class="action-goto-task-info">
            <span class="action-user-name">${detail.assignee}</span>
            将流程从<span class="action-task-name"><${fromTaskName}></span>
            跳转至<span class="action-task-name"><${toTaskName}></span>
          </span>`;
      } else if (actionType == 'HandOver') {
        let formerHandler = detail.formerHandler;
        let toUser = detail.toUser;
        actionInfo = `<span class="action-hand-over-info">
            <span class="action-user-name">${detail.assignee}</span>
            将流程从<span class="action-task-name"><${formerHandler}></span>
            移交至<span class="action-task-name"><${toUser}></span>
          </span>`;
      } else if (
        detail.toUser &&
        (actionType == 'Transfer' || actionType == 'CounterSign' || actionType == 'AddSign' || actionType == 'Delegation')
      ) {
        actionObjects = '<span class="action-objects">' + detail.toUser + '</span>';
        actionName += '给';
      } else if (detail.copyUser && actionType == 'CopyTo') {
        actionObjects = '<span class="action-objects">' + detail.copyUser + '</span>';
        actionName += '给';
      } else if (detail.toUser && detail.copyUser) {
        actionInfo = actionTime + ' ' + actionName + ' ' + actionObjects;
        actionInfo += `<div class="action-with-copyto">同时抄送 <span class="action-objects">${detail.copyUser}</span></div>`;
      }
      return actionInfo || actionTime + ' ' + actionName + ' ' + actionObjects;
    },
    previewFile(file) {
      this.getFileRepositoryServerHost().then(fileRepositoryServerHost => {
        this.fileRepositoryServerHost = fileRepositoryServerHost;
        preview(`${fileRepositoryServerHost}/wopi/files/${file.fileID}?access_token=${this._$USER.token}`);
      });
    },
    getFileRepositoryServerHost() {
      if (this.fileRepositoryServerHost) {
        return Promise.resolve(this.fileRepositoryServerHost);
      } else {
        return this.$clientCommonApi.getSystemParamValue('sys.context.path');
      }
    },
    downloadFile(file) {
      let url = `/proxy-repository/repository/file/mongo/download?fileID=${file.fileID}`;
      download({ url });
    },
    downloadAll(files, detail) {
      const _this = this;
      let downloadAllAsZip = () => {
        let url = `/proxy-repository/repository/file/mongo/downAllFiles`;
        let fileName = `${detail.taskName}_${detail.assignee}_${new Date().format('yyyyMMDDHHmmss')}`;
        let fileIDs = JSON.stringify(files);
        download({ url, data: { fileName, fileIDs, includeFolder: false } });
      };
      let downloadAllFile = () => {
        files.forEach((file, index) => {
          setTimeout(() => {
            _this.downloadFile(file);
          }, 1000 * index);
        });
      };
      _this.workView.getSettings().then(settings => {
        let opinionFileSetting = settings.get('OPINION_FILE') || {};
        // 下载压缩包
        if (opinionFileSetting.downloadAllType == '1') {
          downloadAllAsZip();
        } else {
          // 下载源文件
          downloadAllFile();
        }
      });
    },
    getFileIcon(fileName) {
      return getFileIcon(fileName);
    },
    show() {
      const _this = this;
      if (_this.isOpenProcessModal()) {
        _this.openProcessModal();
      } else {
        _this.$emit('open');
      }
    },
    isOpenProcessModal() {
      let processViewerSetting = this.processViewerSetting;
      return processViewerSetting && (processViewerSetting.showMode == 'modal' || processViewerSetting.showMode == 'all');
    },
    openProcessModal() {
      this.processModalVisible = true;
      this.processWidget = this.getProcessWidget();
    },
    setProcessI18n(widget) {
      let colDataIndexI18nMap = {
        taskName: this.$t('WorkflowWork.workProcess.taskName'),
        assignee: this.$t('WorkflowWork.workProcess.assignee'),
        actionName: this.$t('WorkflowWork.workProcess.actionName'),
        opinion: this.$t('WorkflowWork.workProcess.opinion'),
        endTime: this.$t('WorkflowWork.workProcess.endTime'),
        opinionFiles: this.$t('WorkflowWork.workProcess.opinionFiles')
      };
      let widgetI18ns = { [this.$i18n.locale]: { Widget: {} } };

      widget.configuration.columns.map(col => {
        if (typeof col.i18n === 'undefined' && colDataIndexI18nMap[col.dataIndex]) {
          let key = [widget.id] + '.' + [col.id];
          // col.i18n = {};
          // if (typeof col.i18n[this.$i18n.locale] === 'undefined') {
          //   col.i18n[this.$i18n.locale] = {};
          // }
          // col.i18n[this.$i18n.locale][key] = colDataIndexI18nMap[col.dataIndex];

          set(widgetI18ns[this.$i18n.locale].Widget, key, colDataIndexI18nMap[col.dataIndex]);
        }
      });
      for (let l in widgetI18ns) {
        this.$i18n.mergeLocaleMessage(l, widgetI18ns[l]);
      }
    },
    getProcessWidget() {
      let widget = this.workView.workProcess.getWidgetConfiguration();
      widget.configuration.allowSwitchDisplayStyle = true;
      widget.configuration.displayStyle = 'table';
      widget.configuration.height = '440px';
      widget.configuration.style.height = '440px';
      widget.configuration.search.keywordSearchEnable = false;
      widget.configuration.search.locateCurrentUserRecord = false;
      widget.configuration.search.locateCurrentTaskRecord = false;
      widget.configuration.enabledSort = false;
      this.setProcessI18n(widget);
      return widget;
    }
  }
};
</script>
<style lang="less" scoped>
// .process-timeline {
//   margin: 15px;
//   overflow: auto;

//   .process-item-head {
//     background-color: var(--w-primary-color-3); //#eaedf3;
//     padding: 5px 3px;
//     border-radius: 4px;
//   }
//   .process-item-head.current-task {
//     background: var(--w-primary-color); //  #284989;
//     color: #ffffff;
//   }
//   .process-item-head .task-name {
//     overflow: hidden;
//     text-overflow: ellipsis;
//     white-space: nowrap;
//   }
//   .process-item-head .arrive-time {
//     text-align: right;
//     padding-right: 0.5em;
//   }

//   .process-item-content {
//     .detail-item {
//       line-height: 26px;
//       padding: 10px;
//       margin: 8px 0px;
//       background: var(--w-primary-color-1); // #f5f5f5;
//       border-radius: 4px;
//     }
//     .username {
//       word-break: break-all;
//       overflow: hidden;
//       text-overflow: ellipsis;
//       white-space: nowrap;
//       display: inline-block;
//       color: #333333;
//       margin-left: 6px;
//       font-size: 14px;
//       vertical-align: middle;
//     }
//     .username.todo {
//       display: block;
//       white-space: pre-wrap;
//     }
//     .opinion-icon {
//       position: absolute;
//       top: -5px;
//       right: 0;
//       color: #2aaedd;
//       font-size: 60px;
//       opacity: 0.3;
//     }
//     .opinion-label {
//       position: absolute;
//       top: 17px;
//       right: 0;
//       white-space: nowrap;
//       text-overflow: ellipsis;
//       -webkit-transform: rotate(-38deg);
//       transform: rotate(-38deg);
//       font-size: 12px;
//       width: 80px;
//       text-align: center;
//       padding-left: 10px;
//     }
//     .action-time {
//       text-align: right;
//       padding-right: 0.5em;
//     }

//     .file-list-collapse {
//       background: var(--w-primary-color-1); // #f5f5f5;

//       .file-list-collapse-panel {
//         border: none;
//       }

//       .file-list-collapse-extra {
//         position: absolute;
//         right: 6px;
//       }

//       .file-item {
//         padding: 4px 0;
//         border: none;
//         ::v-deep .ant-list-item-action {
//           position: absolute;
//           right: 0;
//           display: none;
//           background: var(--w-bg-color-body);
//         }

//         &:hover {
//           background-color: var(--w-primary-color-1);
//           ::v-deep .ant-list-item-action {
//             display: inline-block;
//           }
//         }
//       }
//     }
//   }

//   .col-position-statistics-center-line {
//     ::v-deep .ant-divider-horizontal {
//       margin: 12px 0;
//     }
//   }

//   ::v-deep .ant-timeline-item-head-blue {
//     color: var(--w-primary-color);
//     border-color: var(--w-primary-color);
//   }
// }
</style>
