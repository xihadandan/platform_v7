<template>
  <div style="min-width: 378px" class="work-process-standard">
    <WorkProcessHeader :widget="widget" :bodyWidget="bodyWidget"></WorkProcessHeader>
    <PerfectScrollbar :style="{ height: bodyHeight }">
      <a-timeline class="process-timeline">
        <a-timeline-item v-for="(item, index) in dataSource" :key="item.id" :class="{ 'current-task-item': isCurrentTask(item) }">
          <Icon slot="dot" type="pticon iconfont icon-ptkj-jiedian" />
          <a-row :class="{ 'process-item-head': true, 'current-task': isCurrentTask(item) }">
            <!-- <div class="ant-timeline-item-head"></div> -->
            <a-col class="task-name" span="12">
              <span :title="displayTaskName(item, false)" v-html="displayTaskName(item)"></span>
            </a-col>
            <a-col class="arrive-time" span="12">
              {{
                item.arriveTime +
                (item.skipSubmit ? ' ' + $t('WidgetWorkProcess.skipTask', '环节跳过') : ' ' + $t('WidgetWorkProcess.arrived', '到达'))
              }}
            </a-col>
          </a-row>
          <a-row class="process-item-content">
            <a-col>
              <a-row
                class="detail-item"
                v-for="(detail, detailIndex) in item.handleDetail"
                :key="detailIndex"
                v-show="detailIndex < 3 || (item.showMore && detailIndex >= 3)"
              >
                <a-col>
                  <Icon type="pticon iconfont icon-ptkj-jiedian" class="dot-icon" />
                  <Icon type="pticon iconfont icon-ptkj-shixinjiantou-zuo" class="arrow-icon"></Icon>
                  <div class="flex f_nowrap" style="width: 100%">
                    <div>
                      <div :class="{ 'current-user': isCurrentUser(detail.assigneeId) }">
                        <template v-if="detail.endTime">
                          <a-avatar
                            v-if="detail.endTime != null"
                            :size="32"
                            :src="'/proxy/org/user/view/photo/' + detail.assigneeId"
                            style="background-color: var(--w-primary-color)"
                          >
                            <span slot="icon">{{ getDisplayUsername(detail).slice(0, 1) }}</span>
                          </a-avatar>
                          <div
                            class="username"
                            :title="getDisplayUsername(detail)"
                            :class="{ todo: !detail.endTime }"
                            v-html="getDisplayUsername(detail)"
                          ></div>
                          <div class="jobname" :title="detail.endTime ? getDisplayJobName(detail) : ''" v-if="detail.endTime">
                            {{ detail.endTime ? getDisplayJobName(detail) : '' }}
                          </div>
                          <template v-if="detail.endTime && inited">
                            <template v-if="showPrimaryIdentity">
                              <template v-for="(jobName, index) in getDisplayJobNames(detail)">
                                <a-tag v-if="jobName != '...'" style="margin-bottom: 5px">
                                  {{ jobName }}
                                </a-tag>
                                <a-button size="small" type="link" v-else>{{ $t('WidgetWorkProcess.more', '更多') }}</a-button>
                              </template>
                            </template>
                            <a-popover v-else :arrowPointAtCenter="true">
                              <template slot="content">
                                <div style="display: flex; flex-wrap: wrap; max-width: 400px; align-items: baseline">
                                  <template v-for="(jobName, index) in detail.jobNames || []">
                                    <span :key="index" style="margin-bottom: 5px">
                                      <a-tag>{{ jobName }}</a-tag>
                                    </span>
                                  </template>
                                </div>
                              </template>
                              <template v-for="(jobName, index) in getDisplayJobNames(detail, true)">
                                <a-tag v-if="jobName != '...'" style="margin-bottom: 5px">
                                  {{ jobName }}
                                </a-tag>
                                <a-button size="small" type="link" v-else>{{ $t('WidgetWorkProcess.more', '更多') }}</a-button>
                              </template>
                            </a-popover>
                          </template>
                          <a-tag
                            v-if="detail.endTime && isCurrentUser(detail.assigneeId)"
                            color="var(--w-primary-color)"
                            class="current-user-tag"
                          >
                            {{ $t('WidgetWorkProcess.Me', '我') }}
                          </a-tag>
                        </template>
                        <template v-else>
                          <!-- 决策人员 -->
                          <template v-if="detail.decisionMakerName">
                            <a-avatar :size="32" icon="team" style="background-color: var(--w-primary-color)"></a-avatar>
                            <div class="jobname" style="color: var(--w-text-color-light)">
                              {{ $t('WidgetWorkProcess.decisionMaker', '决策人员') }}
                            </div>
                            <div class="todo-user-name">
                              {{ detail.decisionMakerName }}
                            </div>
                            <br />
                          </template>
                          <!-- 待办 -->
                          <a-avatar :size="32" icon="team" style="background-color: var(--w-primary-color)"></a-avatar>
                          <div class="jobname" style="color: var(--w-text-color-light)">
                            {{ $t('WidgetWorkProcess.toDoUser', '待办人员') }}
                          </div>
                        </template>
                      </div>
                      <div
                        :class="['opinion-text', { 'empty-opinion': isEmpty(detail.opinion) }]"
                        v-if="detail.endTime != null"
                        v-html="getDetailOpinion(detail)"
                      ></div>
                    </div>
                    <div
                      v-if="!detail.canceled && item.showUserOpinionPosition && detail.opinionLabel"
                      :class="'opinion-stance opinion-stance-' + detail.opinionValue"
                    >
                      <div class="opinion-label" v-html="detail.opinionLabel" :title="detail.opinionLabel"></div>
                    </div>
                  </div>
                  <div v-if="!detail.endTime" class="todo-user-name">
                    <!-- 待办 -->
                    <div v-html="getDisplayUsername(detail)"></div>
                  </div>
                  <a-collapse
                    v-if="!detail.canceled && detail.opinionFiles && detail.opinionFiles.length > 0"
                    v-model="detail.expandFiles"
                    defaultActiveKey="fileList"
                    expandIconPosition="right"
                    class="file-list-collapse"
                  >
                    <a-collapse-panel key="fileList" :showArrow="true" class="file-list-collapse-panel">
                      <div slot="header">
                        <span class="file-title">{{ $t('WidgetWorkProcess.attachment', '附件') }}（{{ detail.opinionFiles.length }}）</span>
                        <span class="file-action" @click.stop="downloadAll(detail.opinionFiles, detail)">
                          <Icon type="pticon iconfont icon-ptkj-xiazai"></Icon>
                          {{ $t('WidgetWorkProcess.downloadAll', '全部下载') }}
                        </span>
                      </div>
                      <a-list item-layout="horizontal" :data-source="detail.opinionFiles" :bordered="false">
                        <a-list-item slot="renderItem" slot-scope="item, index" class="file-item">
                          <div class="flex f_y_c" style="width: 100%">
                            <Icon :type="getFileIcon(item.fileName)" style="margin-right: 8px"></Icon>
                            <div class="file-title" :title="item.fileName" v-html="item.fileName"></div>
                            <div class="file-action">
                              <a-button type="link" size="small" @click="previewFile(item)">
                                {{ $t('WidgetWorkProcess.preview', '预览') }}
                              </a-button>
                              <a-button type="link" size="small" @click="downloadFile(item)">
                                {{ $t('WidgetWorkProcess.download', '下载') }}
                              </a-button>
                            </div>
                          </div>
                        </a-list-item>
                      </a-list>
                    </a-collapse-panel>
                  </a-collapse>
                  <div v-if="detail.endTime != null" class="action-time">
                    <span v-html="getActionInfo(detail)"></span>
                    <!-- {{ detail.endTime + ' ' + (detail.actionName != null ? detail.actionName : '提交') }} -->
                  </div>
                  <div v-if="detail.endTime != null && getActionInfoUser(detail)" class="action-user">
                    <span v-html="getActionInfoUser(detail)"></span>
                  </div>
                </a-col>
              </a-row>
            </a-col>
          </a-row>
          <a-button
            v-if="item.handleDetail && item.handleDetail.length > 3"
            type="link"
            block
            @click="showMoreHandle(index)"
            style="margin-bottom: 12px"
          >
            <template v-if="!item.showMore">
              <Icon type="pticon iconfont icon-ptkj-xianmiaojiantou-xia"></Icon>
              {{ $t('WidgetWorkProcess.viewMore', '查看更多') }}
            </template>
            <template v-else>
              <Icon type="pticon iconfont icon-ptkj-xianmiaojiantou-shang"></Icon>
              {{ $t('WidgetWorkProcess.collapse', '收起') }}
            </template>
          </a-button>
          <!-- 意见立场统计 -->
          <div class="flex" v-if="item.positionStatistics" style="margin-bottom: 12px">
            <div
              v-for="(statistic, index) in item.positionStatistics"
              :key="index"
              :class="'position-statistic position-statistic-' + statistic.value"
            >
              <span :title="statistic.label">{{ statistic.label }}</span>
              <span class="count">{{ statistic.count }}</span>
            </div>
          </div>
        </a-timeline-item>
        <a-timeline-item v-if="dataSource.length" :class="{ 'task-end': true, 'current-task-item': isOver }">
          <Icon slot="dot" type="pticon iconfont icon-ptkj-jiedian" />
          <a-row :class="{ 'process-item-head': true, 'current-task': isOver }">
            <!-- <div class="ant-timeline-item-head"></div> -->
            <a-col class="task-name" span="10">{{ $t('WidgetWorkProcess.endTask', '结束') }}</a-col>
          </a-row>
        </a-timeline-item>
        <img v-else-if="designMode" :src="designImg" style="width: 100%; height: 100%; overflow: hidden" />
        <a-empty v-else :image="emptyImg"></a-empty>
      </a-timeline>
    </PerfectScrollbar>
  </div>
</template>

<script>
import { isEmpty, trim as stringTrim, each as forEach, replace } from 'lodash';
import { preview, getFileIcon } from '@framework/vue/lib/preview/filePreviewApi';
import { deepClone, download } from '@framework/vue/utils/util';
import WorkProcessHeader from './work-process-header.vue';
import './css/index.less';

export default {
  name: 'WorkProcessStandard',
  props: {
    widget: Object,
    items: {
      type: Array,
      default: []
    },
    searchHighlight: {
      type: Boolean,
      default: true
    }
  },
  components: { WorkProcessHeader },
  inject: ['$workView', 'pageContext', 'designMode'],
  data() {
    const _this = this;
    let configuration = _this.widget.configuration;
    let bodyStyle = configuration.style;
    let displayDataTypes = configuration.displayDataTypes;
    let items = _this.items;
    if (displayDataTypes.length != 4) {
      items = _this.filterByActionTypes(deepClone(items), displayDataTypes);
    } else {
      items = deepClone(items);
    }
    return {
      sortAscending: true,
      currentUserRecordIndex: 0,
      dataSource: items,
      initItems: deepClone(items),
      bodyStyle,
      ...configuration,
      emptyImg: '/static/images/data-empty.png',
      designImg: '/static/images/workflow/work_process_standard.jpg',
      inited: false,
      displayJobNameCount: 1,
      showPrimaryIdentity: false
    };
  },
  computed: {
    isOver() {
      if (this.$workView && this.$workView.workView) {
        return this.$workView.workView.isOver();
      }
      let flowIsOver = true;
      this.initItems.forEach(item => {
        if (item.hasOwnProperty('endTime') && item.endTime == null) {
          flowIsOver = false;
        } else if (item.handleDetail) {
          item.handleDetail.forEach(detail => {
            if (detail.hasOwnProperty('endTime') && detail.endTime == null) {
              flowIsOver = false;
            }
          });
        }
      });
      return flowIsOver;
    },
    bodyHeight() {
      if (this.height == 'auto' || this.height == undefined) {
        return 'auto';
      }
      let padding = this.bodyStyle.padding[0] + this.bodyStyle.padding[2];
      return `calc( ${this.height} - ${padding}px)`;
    },
    bodyWidget() {
      return this;
    }
  },
  mounted() {
    const _this = this;
    setTimeout(() => {
      if (_this.$workView) {
        _this.$workView.workView.getSettings().then(settings => {
          let provessViewerSetting = settings.get('PROCESS_VIEWER') || {};
          _this.showPrimaryIdentity = provessViewerSetting.operatorIdentityMode == 'primary';
          _this.inited = true;
        });
      } else {
        _this.inited = true;
      }
    }, 500);
  },
  methods: {
    displayTaskName(item, getMatchName = true) {
      if (getMatchName && item.matchTaskName) {
        return item.matchTaskName;
      }

      if (item.taskName.includes('补审补办')) {
        if (item.supplemented) {
          return (
            this.$t('WorkflowView.' + item.taskId + '.taskName', item.taskName) +
            ' (' +
            this.$t('WorkflowWork.extraApproveExtraTask', '补审补办') +
            ')'
          );
        }
      } else {
        if (item.supplemented) {
          // 增加“补审补办”字样
          return (
            this.$t('WorkflowView.' + item.taskId + '.taskName', item.taskName) +
            ' (' +
            this.$t('WorkflowWork.extraApproveExtraTask', '补审补办') +
            ')'
          );
        }
      }
      return this.$t('WorkflowView.' + item.taskId + '.taskName', item.taskName);
    },
    isEmpty,
    filterByActionTypes(items, actionTypes) {
      let specialTypes = ['CopyTo', 'HandOver', 'GotoTask'];
      let matchActionType = actionType => {
        if (actionType) {
          if (actionTypes.includes(actionType)) {
            return true;
          } else if (actionTypes.includes('Handle') && !specialTypes.includes(actionType)) {
            return true;
          }
          return false;
        } else {
          return true;
        }
      };

      let retItems = items;
      retItems.forEach(item => {
        if (item.handleDetail) {
          item.handleDetail = item.handleDetail.filter(detail => matchActionType(detail.actionType));
        }
      });
      retItems = retItems.filter(item => (item.handleDetail ? item.handleDetail.length : matchActionType(item.actionType)));

      return retItems;
    },
    searchWorkProcess(value) {
      const _this = this;
      if (isEmpty(_this.search.keywordSearchColumns)) {
        return;
      }

      let keyword = stringTrim(value);
      if (isEmpty(keyword)) {
        _this.dataSource = deepClone(_this.initItems);
        if (!_this.sortAscending) {
          _this.sortWorkProcessDescending();
        }
      } else {
        let matchList = [];
        deepClone(_this.initItems).forEach(record => {
          if (_this.matchKeyword(record, keyword)) {
            matchList.push(record);
          }
        });
        _this.dataSource = matchList;
      }
    },
    matchKeyword(record, keyword) {
      const _this = this;
      let keywordSearchColumns = _this.search.keywordSearchColumns;
      let match = false;

      for (let key in record) {
        let propVal = record[key];
        if (keywordSearchColumns.includes(key) && propVal && propVal.indexOf && propVal.indexOf(keyword) != -1) {
          if (_this.searchHighlight) {
            record[key] = replace(propVal, keyword, `<span style="color:#e33033">${keyword}</span>`);
            if (key == 'actionName') {
              record.matchActionName = record[key];
              record[key] = propVal;
            }
            if (key == 'taskName') {
              record.matchTaskName = record[key];
              record[key] = propVal;
            }
          }
          match = true;
        }
      }

      if (record.handleDetail) {
        record.handleDetail.forEach(item => {
          if (_this.matchKeyword(item, keyword)) {
            match = true;
          }
        });
      }

      if (record.opinionFiles) {
        record.opinionFiles.forEach(item => {
          if (_this.matchKeyword(item, keyword)) {
            match = true;
          }
        });
      }

      return match;
    },
    locateCurrentTaskRecord() {
      let currentTaskEl = this.$el.querySelector('.current-task');
      currentTaskEl && currentTaskEl.scrollIntoView();
    },
    locateCurrentUserRecord() {
      const _this = this;
      let currentUserEls = _this.$el.querySelectorAll('.current-user');
      if (currentUserEls && currentUserEls.length) {
        if (_this.currentUserRecordIndex >= currentUserEls.length) {
          _this.currentUserRecordIndex = 0;
        }
        let el = currentUserEls.item(_this.currentUserRecordIndex);
        if (el) {
          el.scrollIntoView(false);
          _this.currentUserRecordIndex++;
        }
      }
    },
    sortWorkProcessAscending() {
      this.sortAscending = true;
      this.dataSource.reverse();
      this.dataSource.forEach(item => {
        item.handleDetail && item.handleDetail.reverse();
      });
    },
    sortWorkProcessDescending() {
      this.sortAscending = false;
      this.dataSource.reverse();
      this.dataSource.forEach(item => {
        item.handleDetail && item.handleDetail.reverse();
      });
    },
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
    getDisplayJobNames: function (detail, showMore) {
      let jobNames = detail.jobNames || [];
      if (jobNames.length > 1 && showMore && this.$el) {
        let clientWidth = 0;
        let $parent = this;
        while ($parent != null && clientWidth <= 0) {
          $parent = $parent.$parent;
          if ($parent && $parent.$el) {
            clientWidth = $parent.$el.clientWidth;
          }
        }
        if (clientWidth > 0) {
          if (clientWidth < 378) {
            clientWidth = 378;
          }
          let jobNameCount = parseInt((clientWidth - 78) / 200);
          if (jobNameCount > 0 && jobNames.length > jobNameCount) {
            let retJobNames = [];
            for (let index = 0; index < jobNameCount; index++) {
              retJobNames.push(jobNames[index]);
            }
            retJobNames.push('...');
            jobNames = retJobNames;
            this.displayJobNameCount = jobNameCount;
          }
        }
      }
      return jobNames;
    },
    getDetailOpinion: function (detail) {
      if (this.$workView && this.$workView.workView) {
        if (detail.actionType) {
          const actionName = this.$t('WorkflowWork.operation.' + detail.actionType, detail.actionName);
          if (actionName !== detail.actionName) {
            detail.actionName = actionName;
          }
        }
        let opinion = this.$workView.workView.getMsgI18ns(null, detail.opinion, 'WorkflowWork.opinionManager');
        if (opinion !== detail.opinion) {
          detail.opinion = opinion;
          return opinion;
        }
      }
      // if (detail && detail.actionCode === 12) {
      //   return this.$t('WidgetWorkProcess.pleaseHurryUp', detail.opinion || '请抓紧办理!');
      // }
      return isEmpty(detail.opinion)
        ? detail.actionCode != 36
          ? this.$t('WidgetWorkProcess.noOpinionText', '未填写办理意见')
          : ''
        : detail.opinion;
    },
    isCurrentTask: function (item) {
      if (item.handleDetail && item.handleDetail.length) {
        let lastItem = item.handleDetail[item.handleDetail.length - 1];
        return lastItem.taskStatus === '未完成' || lastItem.status === '未完成';
      }
      return item.taskStatus === '未完成' || item.status === '未完成';
    },
    isCurrentUser(userId) {
      return this._$USER && this._$USER.userId == userId;
    },
    getActionInfo(detail, isall) {
      let actionInfo = null;
      let actionTime = detail.endTime || '';
      let actionName = detail.actionName || this.$t('WorkflowWork.operation.Submit', '提交');
      let _actionName = detail.matchActionName || this.$t('WorkflowWork.operation.' + detail.actionType, null);
      if (_actionName) {
        actionName = _actionName;
      }
      if (detail.actionCode == 35) {
        actionName += ' (' + this.$t('WidgetWorkProcess.approveAuto', '自动审批') + ')';
      } else if (detail.actionCode == 36) {
        actionName = this.$t('WidgetWorkProcess.autoJumpRepeatTask', '重复自动跳过');
      }
      let actionType = detail.actionType || 'Submit';

      let actionObjects = '';
      if (actionType == 'GotoTask') {
        let fromTaskName = detail.suspensionState == 2 ? this.$t('WidgetWorkProcess.endTask', '结束') : detail.taskName;
        let toTaskName = detail.gotoTaskName;
        let gotoTaskActionName = _actionName
          ? _actionName + this.$t('WidgetWorkProcess.toText', '至')
          : this.$t('WidgetWorkProcess.gotoTaskToText', '跳转至');
        actionInfo = `<span class="action-goto-task-info">
            <span class="action-user-name">${detail.assignee}</span>
            ${this.$t('WidgetWorkProcess.gotoTaskFromText', '将流程从')} <span class="action-task-name">&lt;${fromTaskName}&gt;</span>
            ${gotoTaskActionName} <span class="action-task-name">&lt;${toTaskName}&gt;</span>
          </span>`;
      } else if (actionType == 'HandOver') {
        let formerHandler = detail.formerHandler;
        let toUser = detail.toUser;
        let handOverActionName = _actionName
          ? _actionName + this.$t('WidgetWorkProcess.toText', '至')
          : this.$t('WidgetWorkProcess.handoverToText', '移交至');
        actionInfo = `<div class="action-hand-over-info">
            <span class="action-user-name">${detail.assignee}</span>
            ${this.$t('WidgetWorkProcess.gotoTaskFromText', '将流程从')}<span class="action-task-name">&lt;${formerHandler}&gt;</span>
            ${handOverActionName}<span class="action-task-name">&lt;${toUser}&gt;</div>
          </span>`;
      } else if (
        detail.toUser &&
        (actionType == 'Transfer' || actionType == 'CounterSign' || actionType == 'AddSign' || actionType == 'Delegation')
      ) {
        actionObjects = '<span class="action-objects">' + detail.toUser + '</span>';
        actionName += this.$t('WidgetWorkProcess.To', '给');
      } else if (detail.copyUser && actionType == 'CopyTo') {
        if (isall) {
          actionObjects = '<span class="action-objects">' + detail.copyUser + '</span>';
          actionName += this.$t('WidgetWorkProcess.To', '给');
        } else {
          actionInfo = actionTime + ' ' + actionName;
        }
      } else if (detail.toUser && detail.copyUser) {
        actionInfo = actionTime + ' ' + actionName + ' ' + actionObjects;
        if (isall) {
          actionInfo += `<div class="action-with-copyto">${this.$t(
            'WidgetWorkProcess.copyToMeanwhile',
            '同时抄送'
          )} <span class="action-objects">${detail.copyUser}</span></div>`;
        }
      }
      return actionInfo || actionTime + ' ' + actionName + actionObjects;
    },
    getActionInfoUser(detail) {
      let actionName = detail.actionName || this.$t('WorkflowWork.operation.Submit', '提交');
      let _actionName = this.$t('WorkflowWork.operation.' + detail.actionType, null);
      if (_actionName) {
        actionName = _actionName;
      }
      let actionType = detail.actionType || 'Submit';
      let actionObjects = '';
      if (
        detail.toUser &&
        (actionType == 'Transfer' || actionType == 'CounterSign' || actionType == 'AddSign' || actionType == 'Delegation')
      ) {
        // actionObjects = '<span class="action-objects">' + detail.toUser + '</span>';
        // actionName += '对象';
      } else if (detail.copyUser && actionType == 'CopyTo') {
        actionObjects = '<span class="action-objects">' + detail.copyUser + '</span>';
        actionName += this.$t('WidgetWorkProcess.Target', '对象');
      } else if (detail.toUser && detail.copyUser) {
        actionName = this.$t('WidgetWorkProcess.copyToMeanwhile', '同时抄送');
        actionObjects = `<span class="action-objects">${detail.copyUser}</span>`;
      }
      return actionObjects ? actionName + actionObjects : '';
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
      if (_this.$workView) {
        _this.$workView.workView.getSettings().then(settings => {
          let opinionFileSetting = settings.get('OPINION_FILE') || {};
          // 下载压缩包
          if (opinionFileSetting.downloadAllType == '1') {
            downloadAllAsZip();
          } else {
            // 下载源文件
            downloadAllFile();
          }
        });
      } else {
        downloadAllAsZip();
      }
    },
    getFileIcon(fileName) {
      return getFileIcon(fileName);
    },
    showMoreHandle(index) {
      let item = this.dataSource[index];
      this.$set(this.dataSource[index], 'showMore', !item.showMore);
      this.$nextTick(() => {
        this.pageContext.emitEvent('updateWorkProcessScrollbar');
        this.pageContext.emitEvent('perfectScrollbarToResize', {});
      });
    }
  }
};
</script>

<style lang="less" scoped></style>
