<template>
  <div style="min-width: 378px" class="work-process-simple">
    <WorkProcessHeader :widget="widget" :bodyWidget="bodyWidget"></WorkProcessHeader>
    <PerfectScrollbar :style="{ height: bodyHeight }">
      <a-timeline class="process-timeline">
        <template v-for="group in dataSource">
          <a-timeline-item v-for="(item, index) in group.handleDetail || []" :key="item.id">
            <template slot="dot">
              <a-avatar :size="40" :src="'/proxy/org/user/view/photo/' + item.assigneeId" style="background-color: var(--w-primary-color)">
                <span slot="icon">{{ getDisplayUsername(item).slice(0, 1) }}</span>
              </a-avatar>
            </template>
            <div class="process-item-content">
              <div class="flex f_nowrap" style="width: 100%">
                <div>
                  <div :class="{ 'current-task': isCurrentTask(item) }">
                    <span class="task-name">
                      <span :title="displayTaskName(item, false)" v-html="displayTaskName(item)"></span>
                    </span>
                    <span
                      :class="{ username: true, 'current-user': isCurrentUser(item.assigneeId) }"
                      v-html="getDisplayUsername(item)"
                    ></span>
                    <span class="jobname" :title="item.endTime ? getDisplayJobName(item) : ''" v-if="item.endTime">
                      {{ item.endTime ? getDisplayJobName(item) : '' }}
                    </span>
                    <template v-if="item.endTime && inited">
                      <template v-if="showPrimaryIdentity">
                        <template v-for="(jobName, index) in getDisplayJobNames(item, true)">
                          <a-tag v-if="jobName != '...'">
                            {{ jobName }}
                          </a-tag>
                          <a-button size="small" type="link" v-else>{{ $t('WidgetWorkProcess.more', '更多') }}</a-button>
                        </template>
                      </template>
                      <a-popover v-else>
                        <template slot="content">
                          <span v-for="(jobName, index) in item.jobNames || []" :key="index">
                            <p v-if="displayJobNameCount && index > 0 && index % (displayJobNameCount + 1) == 0" />
                            <a-tag>{{ jobName }}</a-tag>
                          </span>
                        </template>
                        <template v-for="(jobName, index) in getDisplayJobNames(item, true)">
                          <a-tag v-if="jobName != '...'">
                            {{ jobName }}
                          </a-tag>
                          <a-button size="small" type="link" v-else>{{ $t('WidgetWorkProcess.more', '更多') }}</a-button>
                        </template>
                      </a-popover>
                    </template>
                    <a-tag v-if="item.endTime && isCurrentUser(item.assigneeId)" color="var(--w-primary-color)" class="current-user-tag">
                      {{ $t('WidgetWorkProcess.Me', '我') }}
                    </a-tag>
                  </div>
                  <div
                    :class="['opinion-text', { 'empty-opinion': isEmpty(item.opinion) }]"
                    v-if="item.endTime != null"
                    v-html="getDetailOpinion(item)"
                  ></div>
                  <div class="opinion-text" style="color: var(--w-primary-color)" v-if="!item.endTime">
                    {{ $t('WidgetWorkProcess.doing', '办理中') }}
                  </div>
                </div>
                <div
                  v-if="!item.canceled && item.showUserOpinionPosition && item.opinionLabel"
                  :class="'opinion-stance opinion-stance-' + item.opinionValue"
                >
                  <span class="opinion-label" v-html="item.opinionLabel" :title="item.opinionLabel"></span>
                </div>
              </div>
              <a-collapse
                v-if="!item.canceled && item.opinionFiles && item.opinionFiles.length > 0"
                v-model="item.expandFiles"
                defaultActiveKey="fileList"
                expandIconPosition="right"
                class="file-list-collapse"
              >
                <a-collapse-panel key="fileList" :showArrow="true" class="file-list-collapse-panel">
                  <div slot="header">
                    <span class="file-title">{{ $t('WidgetWorkProcess.attachment', '附件') }}（{{ item.opinionFiles.length }}）</span>
                    <span class="file-action" @click.stop="downloadAll(item.opinionFiles, item)">
                      <Icon type="pticon iconfont icon-ptkj-xiazai"></Icon>
                      {{ $t('WidgetWorkProcess.downloadAll', '全部下载') }}
                    </span>
                  </div>
                  <a-list item-layout="horizontal" :data-source="item.opinionFiles" :bordered="false">
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
              <div v-if="item.endTime" class="action-description" v-html="getActionInfo(item, true)"></div>
            </div>
            <!-- <div v-else class="action-description">{{ item.submitTime + ' 送达' }}</div> -->
            <!-- 意见立场统计 -->
            <div class="flex" v-if="index == group.handleDetail.length - 1 && group.positionStatistics" style="margin-top: 32px">
              <span class="position-taskname">{{ item.taskName }}：</span>
              <div class="flex">
                <div
                  v-for="(statistic, index) in group.positionStatistics"
                  :key="index"
                  :class="'position-statistic position-statistic-' + statistic.value"
                >
                  <span>{{ statistic.label }}</span>
                  <span class="count">{{ statistic.count }}</span>
                </div>
              </div>
            </div>
          </a-timeline-item>
        </template>
        <a-timeline-item v-if="dataSource.length" class="task-end">
          <template slot="dot">
            <a-avatar :size="40" :style="{ backgroundColor: isOver ? 'var(--w-primary-color)' : 'var(--w-fill-color-light)' }">
              <Icon slot="icon" type="pticon iconfont icon-ptkj-piliangtingyong" style="font-size: 28px"></Icon>
            </a-avatar>
          </template>
          <div class="process-item-content">
            <a-row :class="{ 'current-task': isOver }">
              <a-col class="task-name" span="10">{{ $t('WidgetWorkProcess.endTask', '结束') }}</a-col>
            </a-row>
          </div>
        </a-timeline-item>
        <img v-else-if="designMode" :src="designImg" style="width: 100%; height: 100%; overflow: hidden" />
        <a-empty v-else :image="emptyImg"></a-empty>
      </a-timeline>
    </PerfectScrollbar>
  </div>
</template>

<script>
import WorkProcessHeader from './work-process-header.vue';
import WorkProcessStandard from './work-process-standard.vue';
export default {
  name: 'WorkProcessSimple',
  extends: WorkProcessStandard,
  components: { WorkProcessHeader },
  data() {
    return {
      designImg: '/static/images/workflow/work_process_simple.jpg'
    };
  }
};
</script>

<style lang="less" scoped></style>
