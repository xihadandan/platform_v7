<template>
  <a-collapse class="subflow-share-data" v-model="activeKey" @change="changeCollapse">
    <template #expandIcon="props">
      <a-icon type="caret-right" :rotate="props.isActive ? 90 : 0" />
    </template>
    <a-collapse-panel :key="shareData.belongToTaskInstUuid">
      <template slot="header">
        <span class="share-data-header">{{ title }}</span>
        <span v-if="dispatchInfo.dispatchingCount > 0">
          <a-icon type="rise"></a-icon>
          {{ $t('WorkflowWork.subFlowShareData.dispatchingText', '努力分发中') }}...({{
            dispatchInfo.totalCount - dispatchInfo.dispatchingCount
          }}/{{ dispatchInfo.totalCount }})
        </span>
      </template>
      <a-row class="keyword-query-container">
        <a-col span="6">
          <a-input-search
            v-model="keyword"
            :placeholder="$t('WorkflowWork.keywordPlaceholder', '关键字')"
            :enter-button="$t('WorkflowWork.operation.searchBtnText', '查询')"
            :allowClear="true"
            @search="onSearch"
          />
        </a-col>
      </a-row>
      <a-space class="share-data-toolbar" v-if="actions.length">
        <template v-for="(action, index) in actions">
          <a-button
            v-show="!action.hidden"
            :key="action.id"
            :code="action.id"
            :type="index === 0 ? 'primary' : 'default'"
            @click="onActionClick($event, action)"
          >
            <a-icon v-if="action.icon" :type="action.icon" />
            {{ $t('WorkflowView.' + action.uuid, $t('WorkflowWork.subFlowShareData.button.' + action.id, action.name)) }}
          </a-button>
        </template>
        <a-dropdown v-if="moreActions && moreActions.length > 0">
          <a-button>
            {{ $t('WorkflowWork.operation.More', '更多') }}
            <a-icon type="down" />
          </a-button>
          <a-menu slot="overlay">
            <template v-for="action in moreActions">
              <a-menu-item v-show="!action.hidden" :key="action.id" :code="action.id" @click="onActionClick($event, action)">
                <a-icon v-if="action.icon" :type="action.icon" />
                {{ $t('WorkflowView.' + action.uuid, $t('WorkflowWork.subFlowShareData.button.' + action.id, action.name)) }}
              </a-menu-item>
            </template>
          </a-menu>
        </a-dropdown>
      </a-space>
      <!-- 分发失败信息 -->
      <div class="share-data-error" v-if="dispatchInfo.errorCount > 0">
        <span>
          {{
            $t(
              'WorkflowWork.subFlowShareData.dispatchErrorCount',
              { count: dispatchInfo.errorCount },
              '存在 ' + dispatchInfo.errorCount + ' 个分发失败!'
            )
          }}
        </span>
        <span class="share-error-reason" @click="onViewShareErrorReasonClick">
          <a href="javascript:;">{{ $t('WorkflowWork.subFlowShareData.errorReasonText', '失败原因') }}</a>
        </span>
        <a-button type="primary" @click="onResendSubFlowClick">
          {{ $t('WorkflowWork.subFlowShareData.resendSubFlowTask', '一键补发') }}
        </a-button>
      </div>
      <a-table
        :columns="columns"
        :row-key="record => getRowKey(record)"
        :data-source="dataList"
        :pagination="pagination"
        :loading="loading"
        :row-selection="getRowSelectionConfig()"
        :customRow="customRow"
        :scroll="{ x: scrollX }"
        @change="onTableChange"
        class="subflow-share-table pt-table"
      >
        <template slot="todoName" slot-scope="text, record, index">
          <div class="subflow-td-todoname">
            <a-tag class="subflow-label" v-if="record.flowLabel" :title="getFlowLabel(record)">{{ getFlowLabel(record) }}</a-tag>
            <span class="subflow-label-todoname" @click="onJumpSubflowDetail(record)" :title="record.todoName">
              {{ record.todoName }}
            </span>
          </div>
        </template>
        <template v-for="(slotOption, i) in customColumnTitles">
          <span :slot="slotOption.slotName">
            <a-tooltip>
              <template slot="title">
                {{ slotOption.tipContent }}
              </template>
              {{ slotOption.hidden ? '' : slotOption.title }}
            </a-tooltip>
          </span>
        </template>
        <template v-for="(slotOption, i) in customRenderSlotOptions" :slot="slotOption.slotName" slot-scope="text, record, index">
          <!-- 前端提供的渲染函数 -->
          <template v-if="slotOption.renderType">
            <ColumnValueClientRender
              :key="slotOption.key"
              :row="record"
              :text="text"
              :rowIndex="index"
              :slotOption="slotOption"
              :invokeJsFunction="invokeJsFunction"
              :invokeDevelopmentMethod="invokeDevelopmentMethod"
              @onInvokeDevelopmentMethod="columnRenderInvokeDevelopmentMethod"
            />
          </template>
          <template
            v-else-if="
              slotOption.columnType == '2' &&
              slotOption.configuration &&
              slotOption.configuration.sources &&
              slotOption.configuration.sources.split(/[;,]/).indexOf(record.flowDefId) == -1
            "
          >
            {{ slotOption.defaultContentIfNull }}
          </template>
          <template v-else>
            {{ text || slotOption.defaultContentIfNull }}
          </template>
        </template>
      </a-table>

      <!-- 分发失败原因弹框 -->
      <a-modal
        :title="$t('WorkflowWork.subFlowShareData.dispatchErrorReasonText', '分发失败原因')"
        dialogClass="pt-modal"
        width="800px"
        :visible="shareErrorReasonModalVisible"
        @cancel="shareErrorReasonModalVisible = false"
      >
        <template slot="footer">
          <a-button type="default" @click="shareErrorReasonModalVisible = false">
            {{ $t('WorkflowWork.opinionManager.operation.cancel', '取消') }}
          </a-button>
        </template>
        <a-table :columns="getErrorReasonTableColumns()" :row-key="record => record._order" :data-source="dispatchErrorReasonList">
          <template slot="todoName" slot-scope="text, record, index">
            <a-tag v-if="record.flowLabel">{{ getFlowLabel(record) }}</a-tag>
            <!-- <a-tag v-if="getTodoNameLabel(record)">{{ getTodoNameLabel(record) }}</a-tag> -->
            <span>{{ getTodoNameText(record) }}</span>
          </template>
        </a-table>
      </a-modal>

      <!-- 子流程操作确认框 -->
      <a-modal
        dialogClass="pt-modal"
        :title="$t('WorkflowWork.subFlowShareData.addSubFlowTodo', '添加承办')"
        :visible="addSubflowConfirmModalVisible"
        @ok="onAddSubflowConfirmOk"
        @cancel="addSubflowConfirmModalVisible = false"
      >
        <a-row>
          <a-col v-if="showSelectedNewFlow">
            <a-select
              v-model="selectedNewFlow"
              :options="newFlows"
              :placeholder="$t('WorkflowWork.subFlowShareData.pleaseSelectFlow', '请选择')"
              style="width: 100%"
              @change="onSelectedNewFlowChange"
            ></a-select>
          </a-col>
          <p v-if="showSelectedNewFlow" />
          <a-col>
            <OrgSelect
              :key="1"
              v-if="subflowGranularity == 'user' || subflowGranularity == 'member'"
              v-model="subflowTaskUserId"
              :orgVersionId="orgVersionId"
              :bizOrgId="bizOrgId"
              :checkableTypes="[]"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
            <OrgSelect
              :key="2"
              v-else-if="subflowGranularity == 'job'"
              v-model="subflowTaskUserId"
              :orgVersionId="orgVersionId"
              :uncheckableTypes="['user']"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
            <OrgSelect
              :key="3"
              v-else-if="subflowGranularity == 'dept' || subflowGranularity == 'bizItem'"
              v-model="subflowTaskUserId"
              :orgVersionId="orgVersionId"
              :bizOrgId="bizOrgId"
              :uncheckableTypes="['job', 'user', 'bizRole']"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
            <OrgSelect
              :key="4"
              v-else-if="subflowGranularity == 'bizRole'"
              v-model="subflowTaskUserId"
              :orgVersionId="orgVersionId"
              :bizOrgId="bizOrgId"
              :checkableTypes="['bizRole']"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
            <OrgSelect
              :key="5"
              v-else
              v-model="subflowTaskUserId"
              :orgVersionId="orgVersionId"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
          </a-col>
        </a-row>
      </a-modal>
      <a-modal
        dialogClass="pt-modal"
        :title="$t('WorkflowWork.subFlowShareData.addPrimaryTodo', '添加主办')"
        :visible="addMajorFlowConfirmModalVisible"
        @ok="onAddMajorFlowConfirmOk"
        @cancel="addMajorFlowConfirmModalVisible = false"
      >
        <a-row>
          <a-col v-if="showSelectedNewFlow">
            <a-select
              v-model="selectedNewFlow"
              :options="newFlows"
              :placeholder="$t('WorkflowWork.subFlowShareData.pleaseSelectFlow', '请选择')"
              style="width: 100%"
              @change="onSelectedNewFlowChange"
            ></a-select>
          </a-col>
          <p v-if="showSelectedNewFlow" />
          <a-col>
            <OrgSelect
              :key="1"
              v-if="subflowGranularity == 'user' || subflowGranularity == 'member'"
              v-model="majorFlowTaskUserId"
              :orgVersionId="orgVersionId"
              :bizOrgId="bizOrgId"
              :checkableTypes="[]"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
            <OrgSelect
              :key="2"
              v-else-if="subflowGranularity == 'job'"
              v-model="majorFlowTaskUserId"
              :orgVersionId="orgVersionId"
              :uncheckableTypes="['user']"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
            <OrgSelect
              :key="3"
              v-else-if="subflowGranularity == 'dept' || subflowGranularity == 'bizItem'"
              v-model="majorFlowTaskUserId"
              :orgVersionId="orgVersionId"
              :bizOrgId="bizOrgId"
              :uncheckableTypes="['job', 'user', 'bizRole']"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
            <OrgSelect
              :key="4"
              v-else-if="subflowGranularity == 'bizRole'"
              v-model="majorFlowTaskUserId"
              :orgVersionId="orgVersionId"
              :bizOrgId="bizOrgId"
              :checkableTypes="['bizRole']"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
            <OrgSelect
              :key="5"
              v-else
              v-model="majorFlowTaskUserId"
              :orgVersionId="orgVersionId"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
          </a-col>
        </a-row>
      </a-modal>
      <a-modal
        dialogClass="pt-modal"
        :title="$t('WorkflowWork.subFlowShareData.addAssistTodo', '添加协办')"
        :visible="addMinorFlowConfirmModalVisible"
        @ok="onAddMinorFlowConfirmOk"
        @cancel="addMinorFlowConfirmModalVisible = false"
      >
        <a-row>
          <a-col v-if="showSelectedNewFlow">
            <a-select
              v-model="selectedNewFlow"
              :options="newFlows"
              :placeholder="$t('WorkflowWork.subFlowShareData.pleaseSelectFlow', '请选择')"
              style="width: 100%"
              @change="onSelectedNewFlowChange"
            ></a-select>
          </a-col>
          <p v-if="showSelectedNewFlow" />
          <a-col>
            <OrgSelect
              :key="1"
              v-if="subflowGranularity == 'user' || subflowGranularity == 'member'"
              v-model="minorFlowTaskUserId"
              :orgVersionId="orgVersionId"
              :bizOrgId="bizOrgId"
              :checkableTypes="[]"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
            <OrgSelect
              :key="2"
              v-else-if="subflowGranularity == 'job'"
              v-model="minorFlowTaskUserId"
              :orgVersionId="orgVersionId"
              :uncheckableTypes="['user']"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
            <OrgSelect
              :key="3"
              v-else-if="subflowGranularity == 'dept' || subflowGranularity == 'bizItem'"
              v-model="minorFlowTaskUserId"
              :orgVersionId="orgVersionId"
              :bizOrgId="bizOrgId"
              :uncheckableTypes="['job', 'user', 'bizRole']"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
            <OrgSelect
              :key="4"
              v-else-if="subflowGranularity == 'bizRole'"
              v-model="minorFlowTaskUserId"
              :orgVersionId="orgVersionId"
              :bizOrgId="bizOrgId"
              :checkableTypes="['bizRole']"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
            <OrgSelect
              :key="5"
              v-else
              v-model="minorFlowTaskUserId"
              :orgVersionId="orgVersionId"
              orgType="MyOrg"
              :title="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
              :placeholder="$t('WorkflowWork.subFlowShareData.orgSelectTitle', '选择组织 / 业务组织')"
            ></OrgSelect>
          </a-col>
        </a-row>
      </a-modal>
      <a-modal
        dialogClass="pt-modal"
        :title="$t('WorkflowWork.subFlowShareData.urge', '催办')"
        :visible="remindConfirmModalVisible"
        @ok="onRemindConfirmOk"
        @cancel="remindConfirmModalVisible = false"
      >
        <a-row>
          <a-col span="3" style="color: var(--w-text-color-darker)">
            {{ $t('WorkflowWork.subFlowShareData.urgeContent', '催办内容') }}
          </a-col>
          <a-col span="21">
            <a-input v-model="remindContent" rows="5" type="textarea" :maxLength="1000"></a-input>
            <div class="text-length-bar">{{ remindContent ? remindContent.length : 0 }}/1000</div>
          </a-col>
        </a-row>
      </a-modal>
      <a-modal
        dialogClass="pt-modal"
        :title="$t('WorkflowWork.subFlowShareData.infoDispatch', '信息分发')"
        :visible="sendMessageConfirmModalVisible"
        @ok="onSendMessageConfirmOk"
        @cancel="sendMessageConfirmModalVisible = false"
      >
        <a-row style="margin-bottom: 12px">
          <a-col span="3" style="color: var(--w-text-color-darker)">
            {{ $t('WorkflowWork.subFlowShareData.dispatchContent', '分发内容') }}
          </a-col>
          <a-col span="21">
            <a-input v-model="sendMessageContent" rows="5" type="textarea" :maxLength="1000"></a-input>
            <div class="text-length-bar">{{ sendMessageContent ? sendMessageContent.length : 0 }}/1000</div>
          </a-col>
        </a-row>
        <a-row>
          <a-col span="3" style="color: var(--w-text-color-darker)">{{ $t('WorkflowWork.opinionManager.attachment', '附件') }}</a-col>
          <a-col span="21">
            <a-upload
              name="file"
              :multiple="true"
              action="/repository/file/mongo/savefiles"
              list-type="text"
              :default-file-list="defaultMessageFileList"
              :customRequest="customRequest"
              @change="onSendMessageFileChange"
            >
              <a-button>
                <a-icon type="upload" />
                {{ $t('WorkflowWork.subFlowShareData.upload', '上传') }}
              </a-button>
            </a-upload>
          </a-col>
        </a-row>
      </a-modal>
      <a-modal
        dialogClass="pt-modal"
        :title="$t('WorkflowWork.subFlowShareData.assistTimeLimit', '协办时限')"
        :visible="limitTimeConfirmModalVisible"
        @ok="onLimitTimeConfirmOk"
        @cancel="limitTimeConfirmModalVisible = false"
      >
        <a-row style="margin-bottom: 12px">
          <a-col span="5" style="color: var(--w-text-color-darker)">
            {{ $t('WorkflowWork.subFlowShareData.assistTimeLimit', '反馈时限') }}
          </a-col>
          <a-col span="19"><a-date-picker v-model="subflowDueTime" /></a-col>
        </a-row>
        <a-row>
          <a-col span="5" style="color: var(--w-text-color-darker)">
            {{ $t('WorkflowWork.subFlowShareData.changeTimeLimitReason', '变更时限原因') }}
          </a-col>
          <a-col span="19">
            <a-textarea v-model="limitTimeOpinionText" :maxLength="1000"></a-textarea>
            <div class="text-length-bar">{{ limitTimeOpinionText ? limitTimeOpinionText.length : 0 }}/1000</div>
          </a-col>
        </a-row>
      </a-modal>
      <a-modal
        dialogClass="pt-modal"
        :title="$t('WorkflowWork.subFlowShareData.confirmText', '确认')"
        :visible="redoConfirmModalVisible"
        @ok="onRedoConfirmOk"
        @cancel="redoConfirmModalVisible = false"
      >
        <p style="color: var(--w-text-color-darker)">{{ $t('WorkflowWork.subFlowShareData.pleaseConfirmReDo', '请确认是否重办?') }}</p>
        <a-row>
          <a-col span="3" style="color: var(--w-text-color-darker)">
            {{ $t('WorkflowWork.subFlowShareData.redoReasonText', '重办原因') }}
          </a-col>
          <a-col span="21">
            <a-textarea v-model="redoOpinionText" :maxLength="1000"></a-textarea>
            <div class="text-length-bar">{{ redoOpinionText ? redoOpinionText.length : 0 }}/1000</div>
          </a-col>
        </a-row>
      </a-modal>
      <a-modal
        dialogClass="pt-modal"
        :title="$t('WorkflowWork.subFlowShareData.confirmText', '确认')"
        :visible="stopConfirmModalVisible"
        @ok="onStopConfirmOk"
        @cancel="stopConfirmModalVisible = false"
      >
        <p style="color: var(--w-text-color-darker)">{{ $t('WorkflowWork.subFlowShareData.pleaseConfirmStop', '请确认是否终止?') }}</p>
        <a-row>
          <a-col span="3" style="color: var(--w-text-color-darker)">
            {{ $t('WorkflowWork.subFlowShareData.stopReasonText', '终止原因') }}
          </a-col>
          <a-col span="21">
            <a-textarea v-model="stopOpinionText" :maxLength="1000"></a-textarea>
            <div class="text-length-bar">{{ stopOpinionText ? stopOpinionText.length : 0 }}/1000</div>
          </a-col>
        </a-row>
      </a-modal>
    </a-collapse-panel>
  </a-collapse>
</template>

<script>
import { each as forEach, isEmpty, findIndex, trim as stringTrim, set, debounce, assign, merge, orderBy } from 'lodash';
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import WorkflowSubflowWorkProcess from './workflow-subflow-work-process.vue';
import { upload, getFileIcon } from '@framework/vue/lib/preview/filePreviewApi';
import CommonTableSerial from '../../workflow-designer/component/commons/common-table-serial.vue';
import TableRenderMixin from '../cell-render/table.renderMixin';
import ColumnValueClientRender from '../cell-render/column-value-client-render';
import moment from 'moment';
import { deepClone } from '@framework/vue/utils/util';
export default {
  name: 'WorkflowSubflowShareData',
  inject: ['pageContext'],
  mixins: ['TableRenderMixin'],
  props: {
    workView: Object,
    subTaskData: Object,
    shareData: Object,
    index: Number
  },
  components: {
    OrgSelect,
    CommonTableSerial,
    ColumnValueClientRender
  },
  data() {
    let serialColumnWidth = 60;
    if (this.$i18n.locale.startsWith('en')) {
      serialColumnWidth = 130;
    }
    return {
      activeKey: undefined,
      actions: [],
      moreActions: [],
      keyword: '',
      loading: false,
      dataList: [],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0
      },
      dispatchInfo: {
        state: 1, // 分发状态， 0分发中、1已分发、2分发失败
        dispatchingCount: 0, //分发中的数量
        errorCount: 0, // 分发失败的数量
        totalCount: 0 // 总数量
      }, // 分发信息
      shareErrorReasonModalVisible: false, // 分发失败原因弹框是否显示
      dispatchErrorReasonList: [], // 分发失败原因数据列表
      flowWorkProcesses: {},
      selectedRowKeys: [],
      selectedRows: [],
      showSelectedNewFlow: true, // 显示选择子流程下拉框
      selectedNewFlow: '', // 选择的子流程
      newFlows: [], // 子流程下拉选项
      subflowGranularity: '',
      subflowTaskUserId: '', // 添加承办人员
      majorFlowTaskUserId: '', // 添加主办人员
      minorFlowTaskUserId: '', // 添加协办人员
      remindContent: '', // 催办内容
      sendMessageContent: '', // 信息分发内容
      defaultMessageFileList: [],
      sendMessageFileList: [], // 信息分发文件列表
      subflowDueTime: '', // 协办时限
      addSubflowConfirmModalVisible: false, // 添加承办确认框是否显示
      addMajorFlowConfirmModalVisible: false, // 添加主办确认框是否显示
      addMinorFlowConfirmModalVisible: false, // 添加协办确认框是否显示
      remindConfirmModalVisible: false, // 催办确认框是否显示
      sendMessageConfirmModalVisible: false, // 信息分发确认框是否显示
      limitTimeConfirmModalVisible: false, // 协办时限确认框是否显示
      limitTimeOpinionText: '',
      redoConfirmModalVisible: false, // 重办确认框是否显示
      redoOpinionText: '',
      stopConfirmModalVisible: false, // 终止确认框是否显示
      stopOpinionText: '',
      orgVersionId: undefined, // 组织版本ID
      bizOrgId: undefined, // 业务组织ID
      columns: [],
      fixedColumnsBodyRowsHeight: {},
      selectionColumnWidth: 48,
      serialColumnWidth,
      fixedColumnWidth: 0,
      autoColumnWidth: 0,
      wrapperWidth: 0,
      scrollX: 1200,
      sorter: undefined,
      customColumnTitles: [], // 表格标题
      customRenderSlotOptions: [] // 表格列
    };
  },
  created() {
    const _self = this;
    let shareData = _self.shareData;
    let buttons = shareData.buttons || [];
    if (buttons.length <= 1) {
      _self.actions = buttons;
    } else {
      let actions = [];
      let moreActions = [];
      for (let i = 0; i < buttons.length; i++) {
        if (i <= 1) {
          actions.push(buttons[i]);
        } else {
          moreActions.push(buttons[i]);
        }
        _self.$set(buttons[i], 'hidden', false);
        if (buttons[i].className && buttons[i].className.indexOf('hide') != -1) {
          _self.$set(buttons[i], 'hidden', true);
        }
      }
      _self.actions = actions;
      _self.moreActions = moreActions;
    }

    // 加载组织版本
    if (_self.subTaskData.businessType) {
      _self.loadOrgVersionIdByOrgId(_self.subTaskData.businessType);
    }

    // 初始化表格数据
    _self.shareDataResult2Table(_self.shareData);

    // 初始化分发信息
    _self.initDispatchInfo();
    _self.getTableColumns();
  },
  mounted() {
    if (this.subTaskData.expandList == '1') {
      this.activeKey = [this.index == 0 ? this.shareData.belongToTaskInstUuid : this.index];
    }
    if (this.activeKey !== undefined) {
      this.$nextTick(() => {
        this.setScrollX();
      });
    }
    this.pageContext.handleEvent('updateLayoutStyle', () => {
      this.setScrollX();
      this.vcTable.syncFixedTableRowHeight();
    });
  },
  methods: {
    changeCollapse(key) {
      if (Array.isArray(key) && key.length) {
        this.$nextTick(() => {
          this.setScrollX();
          this.vcTable.syncFixedTableRowHeight();
        });
      }
    },
    getRowKey(record) {
      return record.taskInstUuid || record.flowInstUuid;
    },
    customRow(record, index) {
      const rowKey = this.getRowKey(record);
      let rowHeight;
      if (this.fixedColumnsBodyRowsHeight[rowKey]) {
        rowHeight = this.fixedColumnsBodyRowsHeight[rowKey];
      }
      return {
        style: {
          // height: `${rowHeight}px`
        },
        on: {
          mouseleave: event => {}
        }
      };
    },
    setScrollX() {
      let table = this.$el.querySelector('.subflow-share-table');
      if (table) {
        this.wrapperWidth = table.parentNode.offsetWidth;
        let isFixedColumn = false;
        const cententWidth = this.selectionColumnWidth + this.serialColumnWidth + this.fixedColumnWidth + this.autoColumnWidth;
        if (cententWidth > this.wrapperWidth) {
          isFixedColumn = true;
          // this.scrollX = cententWidth + 20;
          if (this.$i18n.locale.startsWith('en')) {
            this.scrollX = cententWidth + 90;
          } else {
            this.scrollX = cententWidth + 20;
          }
        } else {
          this.scrollX = false;
        }
        this.columns.map(col => {
          if (col.fixed !== undefined) {
            col.fixed = isFixedColumn ? 'left' : false;
          }
        });
      }
    },
    mountedSerial({ vcTable }) {
      if (!this.vcTable) {
        this.vcTable = vcTable;
      }
    },
    // 获取表格列配置
    getTableColumns() {
      const _self = this;
      let shareData = _self.shareData;
      let columns = [];
      this.customColumnTitles.splice(0, this.customColumnTitles.length);
      this.customRenderSlotOptions.splice(0, this.customRenderSlotOptions.length);

      let fixedColumnWidth = 0,
        autoColumnWidth = 0;
      columns.push({
        title: this.$t('WorkflowWork.subFlowShareData.column.serialNumber', '序号'),
        dataIndex: '_order',
        width: this.serialColumnWidth,
        fixed: 'left',
        align: 'center',
        // scopedSlots: { customRender: '_order' }
        customRender: (text, record, index, column) => {
          return {
            attrs: {},
            children: <common-table-serial order={record['_order']} record={record} index={index} onMounted={_self.mountedSerial} />
          };
        }
      });
      let isFixed = false;
      let colIndex = -1;
      let previousColumnIndex = '';
      forEach(shareData.columns, (column, index) => {
        let col = deepClone(column);
        let configuration = col.configuration ? JSON.parse(col.configuration) : undefined;
        col.configuration = configuration;
        Object.assign(col, col.configuration);
        col.scopedSlots = {};
        let title = _self.$t('WorkflowView.' + col.uuid, _self.$t('WorkflowWork.subFlowShareData.column.' + col.index, col.name));
        col.title = title;
        col.dataIndex = col.index;
        let slotOptions = {
          dataIndex: col.index,
          slotName: col.uuid,
          title: title,
          key: col.uuid, // 刷新使用
          configuration: configuration,
          columnType: col.type
        };
        if (configuration) {
          if (configuration.i18n && !isEmpty(configuration.i18n)) {
            this.mergeWidgetI18nMessages(configuration.i18n);
          }
          // 显示
          if (!configuration.hidden) {
            colIndex++;
            // 使用渲染器配置
            if (configuration.renderFunction != null && configuration.renderFunction.type) {
              if (configuration.defaultContentIfNull) {
                slotOptions.defaultContentIfNull = _self.$t(
                  'WorkflowView.' + col.uuid + '_defaultContentIfNull',
                  configuration.defaultContentIfNull
                );
              }
              if (configuration.renderFunction != null && configuration.renderFunction.type) {
                slotOptions.options = configuration.renderFunction.options || {};
                slotOptions.renderType = configuration.renderFunction.type;
                col.scopedSlots.customRender = col.uuid;
              }
              this.customRenderSlotOptions.push(slotOptions);
            } else {
              col = this.getCustomRender(
                col,
                configuration.defaultContentIfNull
                  ? _self.$t('WorkflowView.' + col.uuid + '_defaultContentIfNull', configuration.defaultContentIfNull)
                  : '',
                colIndex,
                previousColumnIndex
              );
            }
            if (configuration.sortable.enable && col.type == '2') {
              col.sorter = true;
              // col.sorter = function (a, b) {
              //   return _self.sorter(a, b, col);
              // };
            }
            // 定义列标题提示内容
            if (configuration.titleHidden) {
              col.title = ''; // 隐藏标题
            } else if (configuration.showTip && configuration.tipContent) {
              this.customColumnTitles.push({
                title: title,
                slotName: 'title_' + col.uuid,
                tipContent: _self.$t('WorkflowView.' + col.uuid + '_tipContent', configuration.tipContent),
                hidden: !!col.titleHidden
              });
              col.slots = { title: 'title_' + col.uuid };
              col._title = title;
              delete col.title;
            } else if (col.title) {
              let title_prop = {
                attrs: {
                  title: title
                }
              };
              col.title = <span {...title_prop}>{title}</span>;
            }

            // 自定义列头展示
            col.customHeaderCell = this.customHeaderCell;
            col.customCell = (record, rowIndex) => {
              return _self.customCell(record, rowIndex, col);
            };
          }
        } else {
          colIndex++;
          col = this.getCustomRender(col, '', colIndex, previousColumnIndex);
        }

        if (!col.width) {
          const columnWidthMap = {
            todoName: 200,
            currentTaskName: 140,
            // currentTodoUserName: 182,
            workProcesses: 400,
            resultFiles: 400,
            remainingTime: 140,
            dueTime: 120
          };
          if (this.$i18n.locale.startsWith('en')) {
            // columnWidthMap.workProcesses = 350;
            // columnWidthMap.resultFiles = 320;
            columnWidthMap.remainingTime = 170;
            columnWidthMap.dueTime = 200;
          }
          col.width = columnWidthMap[col.index] || 182;
        }

        col.key = col.index + '_' + index; // 避免使用同一个字段渲染多个列导致的冲突

        // 隐藏列不展示
        if (!configuration || !configuration.hidden) {
          if (!isFixed) {
            fixedColumnWidth += col.width;
            isFixed = true;
            col.fixed = 'left';
          } else {
            autoColumnWidth += col.width;
          }
          previousColumnIndex = col.index;
          columns.push(col);
        }
      });
      this.columns = columns;
      this.fixedColumnWidth = fixedColumnWidth;
      this.autoColumnWidth = autoColumnWidth;
    },
    // 获取未设置渲染器列配置
    getCustomRender(col, defaultContentIfNull, colIndex, previousColumnIndex) {
      let _self = this;
      let mergeColumn = false;
      let sources = col.type == '2' && col.sources ? col.sources.split(/[;,]/) : [];
      let customRenderWorkProcesses = (value, row, index) => {
        const obj = {
          children: _self.flowWorkProcesses[row.flowInstUuid] ? (
            <WorkflowSubflowWorkProcess workProcesses={_self.flowWorkProcesses[row.flowInstUuid]} record={row} index={index} />
          ) : (
            defaultContentIfNull
          ),
          attrs: {}
        };
        if (row.dispatchState != 1) {
          obj.attrs.colSpan = 0;
        }
        return obj;
      };
      const fileIconColorMap = {
        'file-word': 'var(--w-primary-color)',
        'file-excel': 'var(--w-success-color)',
        'file-ppt': 'var(--w-danger-color)',
        'file-pdf': 'var(--w-info-color)',
        'file-image': 'var(--w-warning-color)'
      };
      let customRenderFiles = (value, row, index) => {
        let files = value || [];
        let countFiles = _self.$t('WorkflowWork.subFlowDisInfo.countFiles', { count: files.length }, '共' + files.length + '个附件'),
          btnText = _self.$t('WorkflowWork.opinionManager.operation.downloadAll', '全部下载');
        // 拓展字段，当前数据不在列值来源内,值置空
        if (col.type == '2' && sources.indexOf(row.flowDefId) == -1) {
          files = [];
        }
        const obj = {
          children: [
            files.map(item => {
              const fileType = getFileIcon(item.fileName);
              const fileIconStyle = `margin-right:6px;color:${fileIconColorMap[fileType] || 'var(--w-primary-color)'}`;
              return (
                <div key={item.fileID} class="file-item" title={item.fileName}>
                  <icon type={fileType} style={fileIconStyle} />
                  {item.fileName}
                </div>
              );
            }),
            files.length > 0 ? (
              <div>
                <span style="font-size:12px;color:var(--w-gray-color-9)">{countFiles}</span>
                <a-button type="link" style="font-size:12px" onClick={$event => _self.onDownAllFilesClick($event, files)}>
                  {btnText}
                </a-button>
              </div>
            ) : (
              defaultContentIfNull
            )
          ],
          attrs: {}
        };
        if (row.dispatchState != 1) {
          obj.attrs.colSpan = 0;
        }
        return obj;
      };
      let customRenderContent = (value, row, index) => {
        // 拓展字段，当前数据不在列值来源内,值置空
        if (col.type == '2' && sources.indexOf(row.flowDefId) == -1) {
          value = '';
        }
        if (Array.isArray(value)) {
          if (value.filter(item => !isEmpty(item.fileID)).length > 0) {
            return customRenderFiles(value, row, index);
          } else {
            value = '';
          }
        }
        const obj = {
          children: typeof value == 'object' ? JSON.stringify(value) : value || defaultContentIfNull,
          attrs: {}
        };
        if (row.dispatchState != 1) {
          obj.attrs.colSpan = 0;
        }
        return obj;
      };
      let customRenderTaskName = (value, row, index) => {
        let children = '';
        if (value === '结束') {
          children = _self.$t('WorkflowWork.endTaskName', value);
        } else {
          children = value
            ? _self.$t('WorkflowView.subflow.' + row.flowDefId + '.' + row.currentTaskId + '.taskName', value)
            : defaultContentIfNull;
        }
        const obj = {
          children,
          attrs: {}
        };
        if (row.dispatchState != 1) {
          obj.attrs.colSpan = 0;
        }
        return obj;
      };
      // 分发状态
      if (_self.dispatchInfo.state != 1 && previousColumnIndex == 'todoName') {
        let ingTxt = _self.$t('WorkflowWork.subFlowShareData.message.dispatching', '分发中'),
          errText = _self.$t('WorkflowWork.subFlowShareData.message.dispatchError', '分发错误');
        col.customRender = (text, row, index) => {
          if (row.dispatchState == 1) {
            return text;
          }
          return {
            children:
              row.dispatchState == 0 ? (
                <span class="subflow-dispatching">
                  <a-icon type="rise"></a-icon>
                  {ingTxt}
                </span>
              ) : (
                <span class="subflow-dispatching error">
                  <a-icon type="rise"></a-icon>
                  {errText}
                </span>
              ),
            attrs: {
              class: 'td-dispatch-info',
              colSpan: _self.columns.length - colIndex - 1
            }
          };
        };
        previousColumnIndex = col.index;
        mergeColumn = true;
        return col;
      }

      if (mergeColumn) {
        // 固定列
        if (column.type == '1') {
          // 承办部门
          if (col.index == 'todoName') {
            col.scopedSlots = { customRender: 'todoName' };
          } else if (col.index == 'workProcesses') {
            // 办理过程
            col.customRender = customRenderWorkProcesses;
          } else if (col.index == 'resultFiles') {
            // 结果附件
            col.customRender = customRenderFiles;
          } else if (col.index == 'currentTaskName') {
            col.customRender = customRenderTaskName;
          } else {
            col.customRender = customRenderContent;
          }
        } else {
          // 扩展列
          col.customRender = customRenderContent;
        }
      } else {
        // 固定列
        if (col.type == '1') {
          // 承办部门
          if (col.index == 'todoName') {
            col.scopedSlots = { customRender: 'todoName' };
          } else if (col.index == 'workProcesses') {
            // 办理过程
            col.customRender = customRenderWorkProcesses;
          } else if (col.index == 'currentTaskName') {
            col.customRender = customRenderTaskName;
          } else if (col.index == 'resultFiles') {
            // 结果附件
            col.customRender = customRenderFiles;
          }
        } else {
          // 扩展列
          col.customRender = customRenderContent;
        }
      }
      return col;
    },
    // 列排序
    sorter(rowa, rowb, col) {
      let sortable = col.sortable,
        v1 = rowa[col.dataIndex],
        v2 = rowb[col.dataIndex];
      if (sortable.alogrithmType === 'orderByNumber') {
        return Number(v1) - Number(v2);
      } else if (sortable.alogrithmType === 'orderByDate' && sortable.datePattern) {
        let v1Date = moment(v1, sortable.datePattern),
          v2Date = moment(v2, sortable.datePattern);
        if (v1Date._isValid && v2Date._isValid) {
          return v1Date.isAfter(v2Date) ? 1 : v1Date.isSame(v2Date) ? 0 : -1;
        } else if (v1Date._isValid) {
          return -1;
        } else if (v2Date._isValid) {
          return -1;
        }
        return 0;
      } else if (sortable.alogrithmType === 'orderByPinYin') {
        // rowa 拼音在  rowb之前，返回负数，否则正数，相等则为0
        if (v1) {
          return v1.localeCompare(v2, 'pinyin');
        } else {
          return -1; // 空排最后
        }
      } else if (sortable.alogrithmType === 'orderByChar') {
        if (v1) {
          return v1.localeCompare(v2);
        } else {
          return -1; // 空排最后
        }
      } else if (sortable.alogrithmType === 'orderByDefine' && sortable.script) {
        let ans = 0;
        // 执行自定义代码比较
        this.pageContext.executeCodeSegment(
          sortable.script,
          { value: [v1, v2], row: [rowa, rowb], dataIndex: col.dataIndex },
          this,
          function (rt) {
            ans = rt;
          }
        );
        return ans;
      }
      return 0;
    },
    isWebRender(options) {
      return (
        options.type === 'vueTemplateDataRender' ||
        TableRenderMixin.methods[options.type] != undefined ||
        TableRenderMixin.methods.isCellRender(options.type)
      );
    },
    customHeaderCell(col) {
      return {
        style: {
          textAlign: col.configuration ? col.configuration.titleAlign : 'left'
        }
      };
    },

    customCell(record, rowIndex, col) {
      let style = `text-align:${col.configuration.contentAlign};`;
      if (col.index === '__markFlagColumn') {
        style += 'position:relative';
      }
      if (col.configuration.customStyle != undefined) {
        style += col.configuration.customStyle;
      }
      if (col.configuration.clickEvent && col.configuration.clickEvent.enable) {
        style += 'color:var(--w-primary-color);cursor:pointer;';
      }

      return {
        class: 'text-align-' + col.configuration.contentAlign,
        style,
        on: {
          click: event => {
            if (col.configuration.clickEvent && col.configuration.clickEvent.enable) {
              // 执行单元格点击事件
              let eventHandler = col.configuration.clickEvent.eventHandler;
              if (eventHandler.trigger === 'click') {
                eventHandler.meta = record;
                eventHandler.$evt = event;
                this.dispatchEventHandler(eventHandler);
              }
              event.stopPropagation();
            }
          }
        }
      };
    },
    dispatchEventHandler(eventHandler) {
      eventHandler.pageContext = this.pageContext;
      eventHandler.$evtWidget = this;
      let developJs = typeof this.developJsInstance === 'function' ? this.developJsInstance() : this.developJsInstance;
      if (developJs == undefined) {
        developJs = {};
      }
      if (this.$pageJsInstance != undefined) {
        developJs[this.$pageJsInstance._JS_META_] = this.$pageJsInstance;
      }
      eventHandler.$developJsInstance = developJs;
      if (eventHandler.actionType) {
        new DispatchEvent(eventHandler).dispatch();
      } else if (eventHandler.customScript) {
        this.pageContext.executeCodeSegment(
          eventHandler.customScript,
          { meta: eventHandler.meta || {}, eventParams: eventHandler.eventParams || {} },
          this
        );
      }
    },
    onJumpSubflowDetail(record) {
      if (record.dispatchState == 1) {
        const newUrl = this.getSubflowDetailUrl(record);
        window.open(newUrl, '_blank');
      }
    },
    getFlowLabel(record) {
      let flowLabel;
      if (record.flowLabelId) {
        flowLabel = this.$t('WorkflowView.' + record.flowLabelId, null);
      }
      if (!flowLabel) {
        if (record.flowLabelId == 'flowLabel_major' || record.flowLabel === '主办') {
          flowLabel = this.$t('WorkflowWork.subFlowShareData.flowLabel_major', null);
        } else if (record.flowLabel === '协办') {
          flowLabel = this.$t('WorkflowWork.subFlowShareData.flowLabel_assisted', null);
        }
      }
      if (!flowLabel) {
        flowLabel = record.flowLabel;
      }
      return flowLabel;

      // return record.flowLabelId == 'flowLabel_major'
      //   ? this.$t('WorkflowWork.subFlowShareData.flowLabel_major', '主办')
      //   : this.$t('WorkflowView.' + record.flowLabelId, record.flowLabel);
    },
    loadOrgVersionIdByOrgId(orgId) {
      if (orgId.startsWith('O')) {
        $axios.get(`/proxy/api/org/organization/version/getOrgVersionIdByOrgId?orgId=${orgId}`).then(({ data: result }) => {
          this.orgVersionId = result.data;
        });
      } else {
        this.bizOrgId = orgId;
      }
    },
    // 初始化分发信息
    initDispatchInfo() {
      const _self = this;
      // 更新分发状态
      _self.updateDispatchInfo();

      // 分发中定时重新加载数据
      if (_self.isDispatching()) {
        if (_self.dispatchingUpdateTimer) {
          clearInterval(_self.dispatchingUpdateTimer);
          _self.dispatchingUpdateTimer = null;
        }
        _self.dispatchingUpdateTimer = setInterval(function () {
          // _self.$emit('reload', _self.shareData);
          _self.onSearch();
          _self.updateDispatchInfo();
        }, 10 * 1000);
      } else {
        if (_self.dispatchingUpdateTimer) {
          clearInterval(_self.dispatchingUpdateTimer);
          _self.dispatchingUpdateTimer = null;
        }
      }
    },
    // 更新分发状态
    updateDispatchInfo() {
      const _self = this;
      let shareData = _self.shareData;
      let shareItems = shareData.shareItems;
      let dispatchingCount = 0;
      let errorCount = 0;
      let dispatchErrorReasonList = [];
      forEach(shareItems, function (item, index) {
        if (item.dispatchState == 0) {
          dispatchingCount++;
        } else if (item.dispatchState == 2) {
          errorCount++;
          // 提取分发失败原因列表数据
          let todoName = '';
          let todoNameIndex = findIndex(item.columnValues, function (currentValue) {
            return currentValue.index == 'todoName';
          });
          if (todoNameIndex != -1) {
            todoName = item.columnValues[todoNameIndex].value;
          }
          let errorReason = {
            _order: dispatchErrorReasonList.length + 1,
            flowLabel: item.flowLabel,
            flowLabelId: item.flowLabelId,
            todoName: todoName,
            dispatchResultMsg: _self.workView.getMsgI18ns(null, item.dispatchResultMsg, 'WorkflowWork.subFlowShareData')
          };
          dispatchErrorReasonList.push(errorReason);
        }
      });
      _self.dispatchErrorReasonList = dispatchErrorReasonList;
      if (dispatchingCount > 0) {
        _self.dispatchInfo = {
          state: 0,
          dispatchingCount,
          errorCount: 0, // 标记为0，分发中不显示分发失败信息
          totalCount: shareItems.length
        };
      } else if (errorCount > 0) {
        _self.dispatchInfo = {
          state: 2,
          dispatchingCount,
          errorCount,
          totalCount: shareItems.length
        };
      } else {
        _self.dispatchInfo.dispatchingCount = 0;
        _self.dispatchInfo.state = 1;
      }
    },
    // 是否分发中
    isDispatching() {
      return this.dispatchInfo.state == 0;
    },
    // 是否分发中或失败
    isDispatchingOrError() {
      return this.dispatchInfo.state != 1;
    },
    // 查看失败原因
    onViewShareErrorReasonClick() {
      this.shareErrorReasonModalVisible = true;
    },
    // 一键补发
    onResendSubFlowClick() {
      const _self = this;
      // let subTaskData = _self.subTaskData;
      $axios
        .post('/api/workflow/work/resendSubFlow', {
          belongToTaskInstUuid: _self.shareData.belongToTaskInstUuid
        })
        .then(result => {
          if (result.data.code == 0) {
            _self.$message.info(_self.$t('WorkflowWork.subFlowShareData.message.underResendSubFlowTask', '一键补发中!'));
            // 重新加载数据
            _self.$emit('reload', _self.shareData);
          } else {
            _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.resendSubFlowTaskFail', '补发失败!'));
          }
        });
    },
    // 获取分发失败的列信息
    getErrorReasonTableColumns() {
      let columns = [
        {
          title: this.$t('WorkflowWork.subFlowShareData.column.serialNumber', '序号'),
          dataIndex: '_order',
          width: '60px',
          align: 'center',
          scopedSlots: { customRender: '_order' }
        },
        {
          title: this.$t('WorkflowWork.subFlowShareData.column.todoDept', '承办部门'),
          dataIndex: 'todoName',
          scopedSlots: { customRender: 'todoName' }
        },
        {
          title: this.$t('WorkflowWork.subFlowShareData.column.dispatchFailResultMsg', '分发失败原因'),
          dataIndex: 'dispatchResultMsg',
          width: '550px'
        }
      ];
      return columns;
    },
    // 表格查询
    onSearch() {
      this.loadShareDatasByPage(this.keyword);
    },
    // 操作按钮点击处理
    onActionClick(event, action) {
      const _self = this;
      let actionId = action.id;
      switch (actionId) {
        case 'add-subflow': // 添加承办
          _self.addSubflow();
          break;
        case 'add-major-flow': // 添加主办
          _self.addMajorFlow();
          break;
        case 'add-minor-flow': // 添加协办
          _self.addMinorFlow();
          break;
        case 'remind': // 催办
          _self.remind();
          break;
        case 'send-message': // 信息分发
          _self.sendMessage();
          break;
        case 'limit-time': // 协办时限
          _self.limitTime();
          break;
        case 'redo': // 重办
          _self.redo();
          break;
        case 'stop': // 中止
          _self.stop();
          break;
        case 'closeSubView': // 关闭子流程查看本流程
          _self.closeSubView(action);
          break;
        case 'allowSubView': // 允许子流程查看本流程
          _self.allowSubView(action);
          break;
      }
    },
    // 添加承办
    addSubflow() {
      const _self = this;
      // 显示确认框
      _self.addSubflowConfirmModalVisible = true;
      _self.subflowTaskUserId = '';
      _self.loadNewFlowLabelInfos(_self.shareData.belongToTaskInstUuid);
    },
    loadNewFlowLabelInfos: function (parentTaskInstUuid, isMajor) {
      const _self = this;
      _self.newFlows = [];
      _self.selectedNewFlow = '';
      $axios
        .get('/api/workflow/work/getNewFlowLabelInfos', {
          params: {
            taskInstUuid: parentTaskInstUuid,
            roleFlag: isEmpty(isMajor) ? '0' : isMajor
          }
        })
        .then(result => {
          let dataList = result.data.data || [];
          let newFlows = [];
          forEach(dataList, function (newFlow) {
            let label = newFlow.label;
            if (newFlow.ids && newFlow.ids.length === 1) {
              label = _self.$t('WorkflowView.flowLabel_' + newFlow.ids[0], newFlow.label);
            }
            newFlows.push({
              label,
              value: newFlow.ids.join(';'),
              isMajor: newFlow.isMajor,
              granularity: newFlow.granularity
            });
          });
          _self.newFlows = newFlows;
          // 默认选中第一个
          if (newFlows.length > 0) {
            _self.selectedNewFlow = newFlows[0].value;
            _self.onSelectedNewFlowChange();
          }
        });
    },
    onSelectedNewFlowChange() {
      const _this = this;
      _this.newFlows.forEach(item => {
        if (item.value == _this.selectedNewFlow) {
          _this.subflowGranularity = item.granularity;
        }
      });
    },
    // 返回发起子流程接口地址
    getStartSubflowUrl() {
      return '/api/workflow/work/startSubFlow';
    },
    onAddSubflowConfirmOk() {
      const _self = this;
      if (_self.requiredNewFlow && isEmpty(_self.selectedNewFlow)) {
        _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.todoFlowMustSelect', '办理流程不能为空!'));
        return;
      }
      if (isEmpty(_self.subflowTaskUserId)) {
        _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.todoUserMustSelect', '办理人不能为空!'));
        return;
      }

      let taskUsers = _self.subflowTaskUserId.split(';');
      let isMajor = _self.isMajorNewFlow(_self.selectedNewFlow);
      let actionName = _self.getActionName(
        'add-subflow',
        _self.$t('WorkflowWork.subFlowShareData.actionName.addTodDeptOrUser', '添加承办部门(人)')
      );
      _self.doStartSubflow({
        newFlowId: _self.selectedNewFlow,
        taskUsers,
        isMajor,
        actionName
      });

      _self.addSubflowConfirmModalVisible = false;
    },
    doStartSubflow({ newFlowId, taskUsers, isMajor, actionName }) {
      const _self = this;
      $axios
        .post(_self.getStartSubflowUrl(), {
          belongToTaskInstUuid: _self.shareData.belongToTaskInstUuid,
          belongToFlowInstUuid: _self.shareData.belongToFlowInstUuid,
          newFlowId,
          isMajor,
          taskUsers,
          businessType: _self.subTaskData.businessType,
          businessRole: _self.subTaskData.businessRole,
          actionName
        })
        .then(result => {
          if (result.data.code == -5002) {
            _self.workView.handlerError.call(_self.workView, result, () => {});
          } else {
            _self.$message.success(_self.$t('WorkflowWork.subFlowShareData.message.operateSuccess', '操作成功!'), 3, () => {
              window.location.reload();
            });
          }
        })
        .catch(error => {
          _self.workView.handlerError.call(_self.workView, error, () => {});
        });
    },
    isMajorNewFlow(newFlowId) {
      let newFlow = this.newFlows.find(item => item.value == newFlowId);
      return newFlow != null ? newFlow.isMajor : false;
    },
    getActionName(id, defaultName) {
      let button = this.shareData.buttons.find(item => item.id == id);
      return button != null ? button.name : defaultName;
    },
    // 添加主办
    addMajorFlow() {
      const _self = this;
      // 显示确认框
      _self.addMajorFlowConfirmModalVisible = true;
      _self.majorFlowTaskUserId = '';
      _self.loadNewFlowLabelInfos(_self.shareData.belongToTaskInstUuid, '1');
    },
    onAddMajorFlowConfirmOk() {
      const _self = this;
      if (_self.showSelectedNewFlow && isEmpty(_self.selectedNewFlow)) {
        _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.primaryTodoFlowMustSelect', '主办流程不能为空!'));
        return;
      }
      if (isEmpty(_self.majorFlowTaskUserId)) {
        _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.todoUserMustSelect', '办理人不能为空!'));
        return;
      }

      let taskUsers = _self.majorFlowTaskUserId.split(';');
      let isMajor = true;
      let actionName = _self.getActionName('add-major-flow', _self.$t('WorkflowWork.subFlowShareData.actionName.addMajorTodo', '添加主办'));
      _self.doStartSubflow({
        newFlowId: _self.selectedNewFlow,
        taskUsers,
        isMajor,
        actionName
      });

      _self.addMajorFlowConfirmModalVisible = false;
    },
    // 添加协办
    addMinorFlow() {
      const _self = this;
      // 显示确认框
      _self.addMinorFlowConfirmModalVisible = true;
      _self.minorFlowTaskUserId = '';
      _self.loadNewFlowLabelInfos(_self.shareData.belongToTaskInstUuid, '2');
    },
    onAddMinorFlowConfirmOk() {
      const _self = this;
      if (isEmpty(_self.selectedNewFlow)) {
        _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.assistFlowMustSelect', '协办流程不能为空!'));
        return;
      }
      if (isEmpty(_self.minorFlowTaskUserId)) {
        _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.todoUserMustSelect', '办理人不能为空!'));
        return;
      }

      let taskUsers = _self.minorFlowTaskUserId.split(';');
      let isMajor = true;
      let actionName = _self.getActionName(
        'add-minor-flow',
        _self.$t('WorkflowWork.subFlowShareData.actionName.addAssistTodo', '添加协办')
      );
      _self.doStartSubflow({
        newFlowId: _self.selectedNewFlow,
        taskUsers,
        isMajor,
        actionName
      });

      _self.addMinorFlowConfirmModalVisible = false;
    },
    // 催办
    remind() {
      const _self = this;
      if (isEmpty(_self.selectedRowKeys)) {
        _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.pleaseSelectOneRecord', '至少选择一条记录!'));
        return;
      }
      // 显示确认框
      _self.remindConfirmModalVisible = true;
    },
    onRemindConfirmOk() {
      const _self = this;
      if (isEmpty(stringTrim(_self.remindContent))) {
        _self.$message.info(_self.$t('WorkflowWork.subFlowShareData.message.pleaseInputUrgeContent', '请填写催办内容!'));
        return;
      }

      let taskInstUuids = _self.selectedRowKeys;
      let msgContent = stringTrim(_self.remindContent);
      $axios
        .post('/api/workflow/work/remind', {
          taskInstUuids: taskInstUuids,
          opinionLabel: '',
          opinionValue: '',
          opinionText: msgContent
        })
        .then(result => {
          if (result.data.code == -5002) {
            _self.workView.handlerError.call(_self.workView, result, () => {});
          } else {
            _self.$emit('actionSuccess', {
              action: 'subflowRemind',
              result,
              message: _self.$t('WorkflowWork.subFlowShareData.message.urgeSuccess', '催办成功!')
            });
          }
        })
        .catch(error => {
          _self.workView.handlerError.call(_self.workView, error, () => {});
        });

      // 关闭确认框
      _self.remindConfirmModalVisible = false;
    },
    // 信息分发
    sendMessage() {
      const _self = this;
      if (isEmpty(_self.selectedRowKeys)) {
        _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.pleaseSelectOneRecord', '至少选择一条记录!'));
        return;
      }
      _self.defaultMessageFileList = [];
      _self.sendMessageFileList = [];
      // 显示确认框
      _self.sendMessageConfirmModalVisible = true;
    },
    customRequest(requestOption) {
      upload(requestOption);
    },
    onSendMessageFileChange(info) {
      const _self = this;
      if (info.file.status === 'done') {
        let files = [];
        forEach(info.fileList, function (fileInfo) {
          let data = fileInfo.response.data || [];
          for (let i = 0; i < data.length; i++) {
            files.push(data[i]);
          }
        });
        _self.sendMessageFileList = files;
      }
    },
    onSendMessageConfirmOk() {
      const _self = this;
      if (isEmpty(stringTrim(_self.sendMessageContent))) {
        _self.$message.info(_self.$t('WorkflowWork.subFlowShareData.message.pleaseInputDispatchContent', '请填写分发内容!'));
        return;
      }
      let taskInstUuids = _self.selectedRowKeys;
      let contentInfo = stringTrim(_self.sendMessageContent);
      let repoFileIds = [];
      let files = _self.sendMessageFileList;
      for (var i in files) {
        repoFileIds.push(files[i].fileID);
      }
      $axios
        .post('/api/workflow/work/distributeInfo', {
          taskInstUuids: taskInstUuids,
          content: contentInfo,
          fileIds: repoFileIds
        })
        .then(result => {
          if (result.data.code == -5002) {
            _self.workView.handlerError.call(_self.workView, result, () => {});
          } else {
            _self.$emit('actionSuccess', {
              action: 'subflowSendMessage',
              result,
              message: _self.$t('WorkflowWork.subFlowShareData.message.dispatchSuccess', '分发成功!')
            });
          }
        })
        .catch(error => {
          _self.workView.handlerError.call(_self.workView, error, () => {});
        });

      // 关闭确认框
      _self.sendMessageConfirmModalVisible = false;
    },
    // 协办时限
    limitTime() {
      const _self = this;
      if (isEmpty(_self.selectedRowKeys)) {
        _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.pleaseSelectOneRecord', '至少选择一条记录!'));
        return;
      }
      // 显示确认框
      _self.limitTimeConfirmModalVisible = true;
    },
    getChangeLimitTimeUrl() {
      return '/api/workflow/work/changeFlowDueTime';
    },
    getChangeLimitTimeData() {
      return [];
    },
    onLimitTimeConfirmOk() {
      const _self = this;
      if (isEmpty(_self.subflowDueTime)) {
        _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.pleaseSelectFeedbackTimeLimit', '请选择反馈时限'));
        return;
      }
      let flowInstUuids = _self.selectedRows.map(row => row.flowInstUuid);
      // let selection = [];
      // forEach(_self.selectedRowKeys, function (taskInstUuid) {
      //   let index = findIndex(_self.dataList, function (o) {
      //     return o.taskInstUuid == taskInstUuid;
      //   });
      //   if (index != -1) {
      //     flowInstUuids.push(_self.dataList[index].flowInstUuid);
      //     selection.push(_self.dataList[index]);
      //   }
      // });
      $axios
        .post(_self.getChangeLimitTimeUrl(), {
          flowInstUuids: flowInstUuids,
          taskDataItems: _self.getChangeLimitTimeData(_self.selectedRows),
          dueTime: _self.subflowDueTime,
          opinionText: _self.limitTimeOpinionText
        })
        .then(result => {
          if (result.data.code == -5002) {
            _self.workView.handlerError.call(_self.workView, result, () => {});
          } else {
            _self.$emit('actionSuccess', {
              action: 'subflowLimitTime',
              result,
              message: _self.$t('WorkflowWork.subFlowShareData.message.operateSuccess', '操作成功!')
            });
          }
        })
        .catch(error => {
          _self.workView.handlerError.call(_self.workView, error, () => {});
        });

      // 关闭确认框
      _self.limitTimeConfirmModalVisible = false;
    },
    // 重办
    redo() {
      const _self = this;
      if (isEmpty(_self.selectedRowKeys)) {
        _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.pleaseSelectOneRecord', '至少选择一条记录!'));

        return;
      }
      // 显示确认框
      _self.redoConfirmModalVisible = true;
    },
    getRedoUrl() {
      return '/api/workflow/work/redoFlow';
    },
    onRedoConfirmOk() {
      const _self = this;
      // 子流程重办
      let redoUrl = _self.getRedoUrl();
      $axios
        .post(redoUrl, {
          taskInstUuids: _self.selectedRowKeys,
          opinionText: _self.redoOpinionText
        })
        .then(result => {
          if (result.data.code == -5002) {
            _self.workView.handlerError.call(_self.workView, result, () => {});
          } else {
            _self.$emit('actionSuccess', {
              action: 'subflowRedo',
              result,
              message: _self.$t('WorkflowWork.subFlowShareData.message.redoSuccess', '重办成功!')
            });
          }
        })
        .catch(error => {
          _self.workView.handlerError.call(_self.workView, error, () => {});
        });
      // 关闭确认框
      _self.redoConfirmModalVisible = false;
    },
    // 中止
    stop() {
      const _self = this;
      if (isEmpty(_self.selectedRowKeys)) {
        _self.$message.error(_self.$t('WorkflowWork.subFlowShareData.message.pleaseSelectOneRecord', '至少选择一条记录!'));

        return;
      }
      // 显示确认框
      _self.stopConfirmModalVisible = true;
    },
    getStopUrl() {
      return '/api/workflow/work/stopFlow';
    },
    onStopConfirmOk() {
      const _self = this;
      let workData = _self.workView.getWorkData();
      let currentTaskInstUuid = workData.taskInstUuid;
      let taskInstUuids = [];
      // 是否终止自身
      let stopSelf = false;
      forEach(_self.selectedRowKeys, function (taskInstUuid) {
        taskInstUuids.push(taskInstUuid);
        if (currentTaskInstUuid == taskInstUuid) {
          stopSelf = true;
        }
      });
      let stopFunction = function (taskInstUuids, interactionTaskData) {
        let stopUrl = _self.getStopUrl();
        $axios
          .post(stopUrl, {
            taskInstUuids: taskInstUuids,
            interactionTaskData: interactionTaskData,
            opinionText: _self.stopOpinionText
          })
          .then(result => {
            if (result.data.code == -5002) {
              _self.workView.handlerError.call(_self.workView, result, () => {
                stopFunction.call(_self, taskInstUuids, _self.workView.workFlow.getInteractionTaskData());
              });
            } else {
              _self.$emit('actionSuccess', {
                action: 'subflowStop',
                result,
                message: _self.$t('WorkflowWork.subFlowShareData.message.stopSuccess', '终止成功!'),
                stopSelf
              });
            }
          })
          .catch(error => {
            var callback = function () {
              stopFunction.call(_self, taskInstUuids, _self.workView.workFlow.getInteractionTaskData());
            };
            _self.workView.handlerError.call(_self.workView, error, callback);
          });
      };
      // 关闭确认框
      _self.stopConfirmModalVisible = false;
      stopFunction.call(_self, taskInstUuids, {});
    },
    getTodoNameLabel(record) {
      let text = record.todoName;
      if (isEmpty(text)) {
        return;
      }
      let label = '';
      let todoNames = text.split(' ');
      if (todoNames.length > 1) {
        label = todoNames.shift();
      }
      return label;
    },
    getSubflowDetailUrl(record) {
      const _self = this;
      let url = `/workflow/work/view/subflow/share?taskInstUuid=${record.taskInstUuid}&flowInstUuid=${record.flowInstUuid}&belongToFlowInstUuid=${_self.shareData.belongToFlowInstUuid}`;
      return _self.workView.addSystemPrefix(url);
    },
    getTodoNameText(record) {
      let text = record.todoName;
      if (isEmpty(text)) {
        return;
      }
      let label = '';
      let todoNames = text.split(' ');
      if (todoNames.length > 1) {
        label = todoNames.shift();
      }
      return todoNames.join(' ');
    },
    // 表格变更事件处理
    onTableChange(pagination, filters, sorter) {
      const _self = this;
      this.sorter = sorter;
      // console.log(pagination);
      _self.loadShareDatasByPage(_self.keyword, pagination);
    },
    // 全部下载点击事件处理
    onDownAllFilesClick(event, logicFileInfos) {
      let fileIds = [];
      forEach(logicFileInfos, function (fileInfo) {
        fileIds.push(fileInfo.fileID);
      });
      this.workView.downloadFilesByFileIds(fileIds);
    },
    // 行选择配置
    getRowSelectionConfig() {
      const _self = this;
      if (isEmpty(_self.shareData.buttons)) {
        return null;
      }
      return {
        columnWidth: _self.selectionColumnWidth,
        selectedRowKeys: _self.selectedRowKeys,
        onChange: _self.onSelectChange,
        getCheckboxProps: record => ({ props: { disabled: isEmpty(record.taskInstUuid) } }) // 不存在环节实例UUID的数据禁用选择
      };
    },
    // 行选择变更处理
    onSelectChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys;
      this.selectedRows = selectedRows;
    },
    // 初始化加载数据
    initShareDatasByPage() {
      const _self = this;
      if (_self.inited) {
        return;
      }
      _self.inited = true;
      _self.loadShareDatasByPage();
    },
    // 分页加载数据
    loadShareDatasByPage(keyword = '', pagination = {}) {
      const _self = this;
      let belongToTaskInstUuid = _self.shareData.belongToTaskInstUuid;
      let subTaskData = _self.subTaskData;
      let page = pagination.current || 1;
      let pageSize = pagination.pageSize || 10;
      _self.loading = true;
      $axios
        .post('/api/workflow/work/loadShareDatasByPage', {
          keyword: keyword,
          pageNum: page,
          pageSize: pageSize,
          taskInstUuid: subTaskData.taskInstUuid,
          flowInstUuid: subTaskData.flowInstUuid,
          parentFlowInstUuid: _self.shareData.belongToFlowInstUuid || subTaskData.parentFlowInstUuid,
          subFlowInstUuids: [],
          belongToTaskInstUuid: belongToTaskInstUuid,
          orders:
            this.sorter && this.sorter.columnKey != null
              ? [{ sortName: this.sorter.field, sortOrder: this.sorter.order == 'ascend' ? 'asc' : 'desc' }]
              : []
        })
        .then(result => {
          // console.log(result);
          let dataList = result.data.data;
          if (dataList && dataList.length > 0) {
            _self.shareDataResult2Table(dataList[0]);
            _self.shareData.shareItems = dataList[0].shareItems || [];
          } else {
            _self.shareDataResult2Table({ pageNo: 1, pageSize: 0, totalCount: 0 });
          }
          _self.loading = false;
        });
    },
    // 查询数据转到表格
    shareDataResult2Table(shareDataResult) {
      const _self = this;
      let dataList = [];
      let shareItems = shareDataResult.shareItems || [];
      let flowWorkProcesses = _self.flowWorkProcesses;
      let flowInstUuids = [];
      let subflowI18ns = {};
      let flowInstDefId = {};
      forEach(shareItems, function (item, index) {
        let record = Object.assign({}, item);
        if (item.i18ns) {
          for (let k in item.i18ns) {
            set(subflowI18ns, item.flowDefId + '.' + k, item.i18ns[k]);
          }
        }
        if (item.columnValues) {
          forEach(item.columnValues, function (columnValue) {
            if (_self.columns) {
              let hasIndex = findIndex(_self.columns, { index: columnValue.index });
              if (hasIndex > -1) {
                let col = _self.columns[hasIndex];
                if (col.type == '2' && col.sources) {
                  let sources = col.sources.split(/[;,]/);
                  // 拓展字段，当前数据不在列值来源内,值置空
                  if (sources.indexOf(item.flowDefId) == -1) {
                    columnValue.value = '';
                  }
                }
              }
            }
            record[columnValue.index] = columnValue.value;
          });
        }
        record._order = index + 1;
        record.remainingTime = _self.workView.getMsgI18ns(null, record.remainingTime, 'WorkflowWork.subFlowShareData');
        record.currentTaskName = _self.workView.getMsgI18ns(null, record.currentTaskName, 'WorkflowWork.subFlowShareData');
        if (_self.$i18n.locale !== 'zh_CN' && /[\u4e00-\u9fa5]/.test(record.currentTaskName)) {
          let value = record.currentTaskName,
            currentTaskName;
          if (value === '结束') {
            currentTaskName = _self.$t('WorkflowWork.endTaskName', value);
          } else {
            currentTaskName = value
              ? _self.$t('WorkflowView.subflow.' + record.flowDefId + '.' + record.currentTaskId + '.taskName', value)
              : record.currentTaskName;
          }
          record.currentTaskName = currentTaskName;
        }

        dataList.push(record);
        if (record.taskInstUuid && flowWorkProcesses[record.flowInstUuid] == null) {
          flowInstUuids.push(record.flowInstUuid);
          flowInstDefId[record.flowInstUuid] = item.flowDefId;
        }
      });

      _self.$i18n.mergeLocaleMessage(_self.$i18n.locale, {
        WorkflowView: {
          subflow: subflowI18ns
        }
      });
      // if (_self.sorter && _self.sorter.columnKey != null) {
      //   dataList = orderBy(dataList, [_self.sorter.field], [_self.sorter.order == 'ascend' ? 'asc' : 'desc']);
      // }
      // 承办数据
      _self.dataList = dataList;
      // 分页信息
      _self.pagination = {
        current: shareDataResult.pageNo || _self.pagination.current,
        pageSize: shareDataResult.pageSize || _self.pagination.pageSize,
        total: parseInt(shareDataResult.totalCount || _self.pagination.totalCount || shareItems.length)
      };
      // 删除分发中合并的后面单元格
      if (_self.isDispatchingOrError()) {
        _self.$nextTick(() => {
          let dispatchingEles = _self.$el.getElementsByClassName('subflow-dispatching');
          forEach(dispatchingEles, function (dispatchingEle) {
            let dispatchTd = dispatchingEle.parentNode;
            let trEle = dispatchTd.parentNode;
            let children = trEle.children;
            for (let i = children.length - 1; i > 0; i--) {
              if (children[i] == dispatchTd) {
                break;
              } else {
                children[i].remove();
              }
            }
          });
        });
      }
      // 加载办理过程
      if (!isEmpty(flowInstUuids)) {
        _self.loadWorkProcessDetails(flowInstUuids, flowInstDefId);
      }
    },
    // 加载办理过程详细信息
    loadWorkProcessDetails(flowInstUuids, flowInstDefId) {
      const _self = this;
      $axios.get('/api/workflow/work/getWorkProcesses', { params: { flowInstUuids: flowInstUuids.toString() } }).then(result => {
        console.log(result);
        let data = result.data.data;
        if (data) {
          for (let key in data) {
            if (data[key]) {
              for (let d of data[key]) {
                d.flowDefId = flowInstDefId[key];
                d.opinion = _self.workView.getMsgI18ns(null, d.opinion, 'WorkflowWork.opinionManager');
              }
            }
            _self.$set(_self.flowWorkProcesses, key, data[key]);
          }
          this.$nextTick(() => {
            if (this.vcTable) {
              this.vcTable.syncFixedTableRowHeight();
            }
          });
        }
      });
    },
    // 关闭子流程查看本流程
    closeSubView(action) {
      const _this = this;
      let flowInstUuid = _this.shareData.belongToFlowInstUuid;
      let taskId = _this.shareData.belongToTaskId;
      let buttons = _this.shareData.buttons || [];
      $axios
        .post(`/api/workflow/work/setViewMainFlow`, {
          flowInstUuid: flowInstUuid,
          taskId: taskId,
          childLookParent: '0'
        })
        .then(({ data: result }) => {
          if (result.code === 0) {
            _this.$message.success(
              _this.$t(
                'WorkflowWork.subFlowShareData.message.closeSubFlowSuccessAndCannotViewCurrentFlowDetail',
                '关闭成功! 子流程不可查看本流程详细信息!'
              )
            );
            action.hidden = true;
            let btn = buttons.find(button => button.id == 'allowSubView');
            if (btn) {
              btn.hidden = false;
            }
          } else {
            _this.$message.error(result.msg || _this.$t('WorkflowWork.subFlowShareData.message.closeFail', '关闭失败!'));
          }
        });
    },
    // 允许子流程查看本流程
    allowSubView(action) {
      const _this = this;
      let flowInstUuid = _this.shareData.belongToFlowInstUuid;
      let taskId = _this.shareData.belongToTaskId;
      let buttons = _this.shareData.buttons || [];
      $axios
        .post(`/api/workflow/work/setViewMainFlow`, {
          flowInstUuid: flowInstUuid,
          taskId: taskId,
          childLookParent: '1'
        })
        .then(({ data: result }) => {
          if (result.code === 0) {
            _this.$message.success(
              _this.$t(
                'WorkflowWork.subFlowShareData.message.startSubFlowSuccessAndCanViewCurrentFlowDetail',
                '开启成功! 子流程可查看本流程详细信息!'
              )
            );
            action.hidden = true;
            let btn = buttons.find(button => button.id == 'closeSubView');
            if (btn) {
              btn.hidden = false;
            }
          } else {
            _this.$message.error(result.msg || _this.$t('WorkflowWork.subFlowShareData.message.startFail', '开启失败!'));
          }
        });
    },
    getSelf() {
      return this;
    },
    mergeWidgetI18nMessages(i18n) {
      let allMessage = this.$i18n._getMessages();
      if (i18n != undefined) {
        for (let m in i18n) {
          if (m == this.$i18n.locale) {
            let widgetI18n = allMessage[m] && allMessage[m].WorkflowView;
            if (!widgetI18n) {
              widgetI18n = {};
            }
            merge(widgetI18n, i18n[m]);
            this.$i18n.mergeLocaleMessage(m, {
              WorkflowView: widgetI18n
            });
          }
        }
      }
    }
  },
  computed: {
    title() {
      const _self = this;
      if (!isEmpty(_self.shareData.title)) {
        return _self.shareData.title;
      }
      return _self.$t('WorkflowWork.workView.undertakingSituation', '承办情况') + ' ' + (_self.index + 1);
    }
  },
  watch: {
    activeKey(key) {
      if (!isEmpty(key)) {
        this.initShareDatasByPage();
      }
    },
    'shareData.shareItems': {
      handler: function (newItems, oldItems) {
        console.log('shareItems');
        this.initDispatchInfo();
        this.shareDataResult2Table(this.shareData);
      },
      deep: false
    }
  }
};
</script>

<style lang="less" scoped>
.share-data-header {
  position: relative;
  padding: 12px px 16px 12px 40px;
  color: rgba(0, 0, 0, 0.85);
  line-height: 22px;
  cursor: pointer;
  transition: all 0.3s;
}
.todo-name-label {
  background: #dfe4ed;
  color: var(--w-primary-color);
  font-size: 10px;
  border: 1px solid #bfc8dc;
  border-radius: 10%;
  padding: 2px;
  margin-right: 3px;
}
.share-data-toolbar {
  margin-bottom: 12px;
}
.share-data-error {
  border: 1px solid var(--w-danger-color);
  border-radius: 4px;
  background-color: var(--w-danger-color-1);
  padding: 3px 12px;
  margin-bottom: 12px;
}
.share-error-reason {
}
.subflow-share-data ::v-deep .ant-table-tbody tr td .subflow-dispatching {
  width: 100%;
  text-align: right !important;
  display: block;
}
.subflow-share-data ::v-deep .ant-table-tbody tr td.td-dispatch-info {
  text-align: right !important;
}
.td-dispatch-info .subflow-dispatching.error {
  color: red;
}
.text-length-bar {
  text-align: right;
  margin-top: 4px;
}
.subflow-share-data {
  .pt-table {
    ::v-deep .ant-spin-nested-loading > div > .ant-spin .ant-spin-dot {
      left: 0;
    }
  }
}
</style>
