<template>
  <div class="opinion-editor">
    <WorkflowTaskProcess :workView="workView"></WorkflowTaskProcess>
    <Scroll style="height: calc(100vh - 120px)">
      <div style="padding: 12px">
        <WorkflowOpinionRecents
          :workView="workView"
          :opinionData="recentOpinionData"
          :quickSubmit="true && !signOpinionFullScreenVisible"
          @opinionItemClick="onOpinionItemClick"
          @signAndSubmitClick="onSignAndSubmitClick"
          @recentOpinionDeleted="onRecentOpinionDeleted"
        ></WorkflowOpinionRecents>
        <a-card
          class="sign-opinion"
          :title="$t('WorkflowWork.opinionManager.signOpinionPanel.title', '签署意见')"
          :bodyStyle="{ padding: '12px' }"
        >
          <a-button slot="extra" type="icon" class="icon-only" @click="onSignOpinionFullScreenClick">
            <Icon type="pticon iconfont icon-ptkj-quanping" :title="$t('WorkflowWork.opinionManager.operation.fullscreen', '全屏')" />
          </a-button>
          <!-- 意见立场 -->
          <div v-if="enableOpinionPosition && workView.isTodo()" class="opinion-position">
            <a-radio-group name="radioGroup" v-model="opinion.value" @change="onOpinionPositionChange">
              <a-radio v-for="item in opinions" :key="item.code" :value="item.code" @click="onOpinionPositionItemClick($event, item)">
                {{ item.content }}
              </a-radio>
            </a-radio-group>
          </div>
          <div class="opinion-text">
            <!-- <div class="opinion-text-title">签署内容</div> -->
            <a-input
              v-model="opinion.text"
              type="textarea"
              rows="5"
              :placeholder="$t('WorkflowWork.opinionManager.pleaseSignOpinionContent', '请输入签署内容...')"
              :maxLength="1000"
              style="height: auto"
            />
          </div>
        </a-card>
        <WorkflowOpinionFile
          v-if="enableOpinionFile"
          class="opinion-file"
          :workView="workView"
          :fileList="fileList"
          :opinionFileSetting="opinionFileSetting"
          @change="onFileChange"
        ></WorkflowOpinionFile>
        <a-row class="btn-container" type="flex">
          <a-col span="24">
            <a-button
              type="primary"
              size="large"
              block
              v-if="showConfirmSubmitButton && (workView.isNewWork() || workView.isTodo())"
              :disabled="buttonDisabled"
              @click="onSubmitBtnClick"
            >
              <span style="font-size: var(--w-font-size-base)">
                {{ $t('WorkflowWork.opinionManager.operation.confirmSubmit', '确定提交') }}
              </span>
            </a-button>
          </a-col>
        </a-row>
      </div>
    </Scroll>
    <!-- 签署意见全屏 -->
    <a-modal
      v-model="signOpinionFullScreenVisible"
      :title="$t('WorkflowWork.opinionManager.signOpinionPanel.title', '签署意见')"
      width="900px"
      :bodyStyle="{ height: '492px' }"
      :maskClosable="false"
      :closable="!buttonDisabled"
      @cancel="onCancelSignOpinionClick"
      dialogClass="pt-modal"
    >
      <div class="wf-opinion-sign-modal-body">
        <a-button type="icon" class="icon-only open-recents" v-if="!openRecents" @click="openRecents = true">
          <Icon
            type="pticon iconfont icon-luojizujian-huifujishi"
            :title="$t('WorkflowWork.opinionManager.operation.expandOpinion', '展开意见')"
          />
        </a-button>
        <a-row>
          <a-col :span="openRecents ? 15 : 24">
            <!-- 意见立场 -->
            <div v-if="enableOpinionPosition && workView.isTodo()" class="opinion-position">
              <a-radio-group name="radioGroup" v-model="opinion.value" @change="onOpinionPositionChange">
                <a-radio v-for="item in opinions" :key="item.code" :value="item.code" @click="onOpinionPositionItemClick($event, item)">
                  {{ item.content }}
                </a-radio>
              </a-radio-group>
            </div>
            <div class="opinion-text">
              <a-input
                v-model="opinion.text"
                type="textarea"
                rows="10"
                :placeholder="$t('WorkflowWork.opinionManager.pleaseSignOpinionContent', '请输入签署内容...')"
                :maxLength="1000"
                :style="{
                  maxHeight: enableOpinionFile ? (enableOpinionPosition ? '206px' : '240px') : enableOpinionPosition ? '356px' : '392px',
                  height: enableOpinionFile ? (enableOpinionPosition ? '206px' : '240px') : enableOpinionPosition ? '356px' : '392px'
                }"
              />
              <div style="text-align: right; margin-top: 4px">{{ opinion.text ? opinion.text.length : 0 }}/1000</div>
            </div>
            <WorkflowOpinionFile
              v-if="enableOpinionFile"
              :workView="workView"
              :fileList="fileList"
              :opinionFileSetting="opinionFileSetting"
              :remoteDelete="false"
              @change="onFileChange"
              :listStyle="{ maxHeight: '122px', height: '122px' }"
            ></WorkflowOpinionFile>
          </a-col>
          <a-col :span="openRecents ? 9 : 0" style="padding-top: 12px">
            <WorkflowOpinionRecents
              :workView="workView"
              :opinionData="recentOpinionData"
              :listStyle="{ height: enableOpinionFile ? '402px' : '390px' }"
              :quickSubmit="showOpinionModalQuickSubmit"
              @opinionItemClick="onOpinionItemClick"
              @signAndSubmitClick="(e, item) => onSignAndSubmitClick(e, item, true)"
              @recentOpinionDeleted="onRecentOpinionDeleted"
            >
              <template slot="extraIcon">
                <a-button type="icon" class="icon-only" v-if="openRecents" @click="openRecents = false">
                  <Icon
                    type="pticon iconfont icon-ptkj-youshouzhan"
                    :title="$t('WorkflowWork.opinionManager.operation.collapse', '收起')"
                  />
                </a-button>
              </template>
            </WorkflowOpinionRecents>
          </a-col>
        </a-row>
      </div>
      <template slot="footer">
        <a-button @click="onCancelSignOpinionClick" :disabled="buttonDisabled">
          {{ $t('WorkflowWork.opinionManager.operation.cancel', '取消') }}
        </a-button>
        <a-button
          type="primary"
          v-if="signOpinionOptions && signOpinionOptions.buttons && signOpinionOptions.buttons.confirm"
          @click="onSignOpinionOptionButtonClick($event, signOpinionOptions.buttons.confirm)"
          :disabled="buttonDisabled"
        >
          <!-- {{ signOpinionOptions.buttons.confirm.label }} -->
          {{ $t('WorkflowWork.opinionManager.operation.ok', '确定') }}
        </a-button>
        <template v-else>
          <a-button
            v-for="defaultAction in opinionModalDefaultActions"
            :key="defaultAction.id"
            type="primary"
            @click="onSignOpinionDefaultButtonClick($event, defaultAction)"
            :disabled="buttonDisabled"
          >
            {{ $t('WorkflowView.global.' + defaultAction.id, defaultAction.text) }}
          </a-button>
        </template>
        <a-button
          v-if="!(signOpinionOptions && signOpinionOptions.buttons && signOpinionOptions.buttons.confirm)"
          type="primary"
          @click="onOkSignOpinionClick"
          :disabled="buttonDisabled"
        >
          {{ $t('WorkflowWork.opinionManager.operation.ok', '确定') }}
        </a-button>
      </template>
    </a-modal>
  </div>
</template>
<script type="text/babel">
import { trim as stringTrim, each as forEach, isEmpty, isFunction, debounce } from 'lodash';
import WorkflowOpinionRecents from './workflow-opinion-recents.vue';
import WorkflowOpinionFile from './workflow-opinion-file.vue';
import { addDbHeader } from '@framework/vue/utils/function';
const SIGN_OPINION_ACTIONS = [
  'submit',
  'rollback',
  'directRollback',
  'rollbackToMainFlow',
  'cancel',
  'transfer',
  'counterSign',
  'remind',
  'handOver',
  'gotoTask'
];
export default {
  name: 'WorkflowOpinionEditor',
  components: {
    WorkflowOpinionRecents,
    WorkflowOpinionFile
  },
  props: {
    workView: Object
  },
  data() {
    const workData = this.workView.getWorkData();
    let text = workData.opinionText || ''; // 自动提交带入的办理意见
    let label = workData.opinionLabel || ''; // 自动提交带入的办理意见
    let value = workData.opinionValue || ''; // 自动提交带入的办理意见
    let initOpinionFileIds =
      workData.opinionFileIds ||
      (workData.opinionFiles && workData.opinionFiles.length ? workData.opinionFiles.map(item => item.fileID).join(';') : ''); // 自动提交带入的办理附件
    return {
      signOpinionOptions: {},
      signOpinionFullScreenVisible: false,
      enableOpinionPosition: false,
      enableOpinionTextWysiwyg: false, // 意见即时显示
      records: [], // 信息记录
      requiredOpinionPosition: false,
      opinions: [], // 意见立场
      recents: [], // 最近使用
      commonOpinionCategory: {
        opinions: []
      }, // 个人常用意见分类
      publics: [], // 公共意见
      openRecents: true,
      opinion: {
        text,
        label,
        value
      },
      allowRestore: isEmpty(text), // 是否允许还原本地意见
      initOpinionText: text, // 初始化的办理意见
      action: null,
      enableOpinionFile: false, // 开启意见附件
      opinionEditorSetting: null,
      opinionFileSetting: null,
      fileList: [],
      initOpinionFileIds,
      showConfirmSubmitButton: true,
      taskId: ''
    };
  },
  computed: {
    recentOpinionData() {
      return {
        recents: this.recents, // 最近使用
        commonOpinionCategory: this.commonOpinionCategory, // 个人常用意见分类
        publics: this.publics // 公共意见
      };
    },
    opinionModalDefaultActions() {
      let actions = EASY_ENV_IS_NODE ? [] : this.workView.getActions();
      actions = actions.filter(action => SIGN_OPINION_ACTIONS.includes(action.methodName));
      actions.reverse();
      return actions;
    },
    showOpinionModalQuickSubmit() {
      let signOpinionOptions = this.signOpinionOptions;
      if (!signOpinionOptions.action) {
        return true;
      }
      return signOpinionOptions.action == 'submit';
    },
    buttonDisabled() {
      return (this.workView.$widget && this.workView.$widget.displayState == 'disable') || this.workView.loading.visible;
    }
  },
  watch: {
    'opinion.text': function (newVal, oldVal) {
      this.opinionTextWysiwyg(newVal);
    }
  },
  created() {
    const _this = this;
    _this.workView.getSettings().then(settings => {
      _this.opinionEditorSetting = settings.get('OPINION_EDITOR') || {};
      _this.opinionFileSetting = settings.get('OPINION_FILE') || {};
      _this.enableOpinionFile = _this.opinionFileSetting.enabled;
      _this.showConfirmSubmitButton =
        _this.opinionEditorSetting.showConfirmSubmitButton != null ? _this.opinionEditorSetting.showConfirmSubmitButton : true;
    });
  },
  beforeMount() {
    var _self = this;
    var workView = _self.workView;
    workView.setOpinionEditor(_self);
    // 还原本地意见
    if (_self.allowRestore) {
      _self.restore();
      _self.initOpinionText = _self.opinion.text;
    } else if (!isEmpty(_self.initOpinionFileIds)) {
      _self.initOpinionFiles(_self.initOpinionFileIds);
    }
    _self.init(result => {
      var data = result.data.data;
      _self.enableOpinionPosition = data.enableOpinionPosition;
      _self.requiredOpinionPosition = data.requiredOpinionPosition;
      if (data.opinions) {
        _self.opinions = data.opinions.map(item => {
          item.content = _self.$t('WorkflowView.' + _self.taskId + '.opinionPosition.' + item.code, item.content);
          return item;
        });
      }
      _self.recents = data.recents || [];
      _self.commonOpinionCategory = _self.getUserCommonOpinionCategory(data.userOpinionCategories);
      _self.publics = data.publicOpinionCategory.opinions || [];
      if (!_self.enableOpinionPosition && _self.opinion) {
        _self.opinion.label = null;
        _self.opinion.value = null;
      }
    });
  },
  mounted() {
    const _self = this;
    // 撤回时要能带出之前提交填写的意见
    if (_self.workView.isDraft() || _self.workView.isTodo()) {
      let workData = _self.workView.getWorkData();
      _self.records = workData.records || [];
      _self.enableOpinionTextWysiwyg = _self.records.find(item => item.enableWysiwyg) != null;
      if (_self.workView.isTodo()) {
        $axios.get(`/proxy/api/task/operation/getLastestCancelAfterByFlowInstUuid/${workData.flowInstUuid}`).then(({ data: result }) => {
          let cancelData = result.data;
          if (cancelData) {
            _self.initOpinionText = cancelData.opinionText || _self.initOpinionText;
            _self.opinion.text = cancelData.opinionText || _self.opinion.text;
            _self.opinion.label = cancelData.opinionLabel || _self.opinion.label;
            _self.opinion.value = cancelData.opinionValue || _self.opinion.value;
            if (cancelData.opinionFileIds) {
              _self.initOpinionFiles(cancelData.opinionFileIds);
            }
          }
        });
      }
    }
  },
  methods: {
    init: function (callback) {
      var _self = this;
      var workView = _self.workView;
      var workData = workView.getWorkData();
      var flowDefUuid = workData.flowDefUuid;
      var taskId = workData.taskId;
      this.taskId = taskId;
      var config = addDbHeader({});
      $axios.get('/proxy/api/workflow/work/getCurrentUserOpinion2Sign/' + flowDefUuid + '/' + taskId, config).then(function (result) {
        callback.call(_self, result);
      });
    },
    initOpinionFiles(fileIds) {
      $axios.get(`/proxy/repository/file/mongo/getNonioFiles?fileID=${fileIds}`).then(({ data: result }) => {
        if (result.data) {
          this.fileList = result.data.map(file => ({
            uuid: file.fileID,
            fileID: file.fileID,
            name: file.fileName,
            status: 'done'
          }));
          this.fileList2Opinion(this.fileList);
        }
      });
    },
    // 获取个人公共意见分类
    getUserCommonOpinionCategory: function (userOpinionCategories) {
      if (isEmpty(userOpinionCategories)) {
        return [];
      }
      let commonOpinionCategory = null;
      forEach(userOpinionCategories, function (userOpinionCategory) {
        if (userOpinionCategory.id == '001') {
          commonOpinionCategory = userOpinionCategory;
        }
      });
      return commonOpinionCategory != null ? commonOpinionCategory : { opinions: [] };
    },
    // 最近意见删除
    onRecentOpinionDeleted: function () {
      var _self = this;
      _self.init(result => {
        var data = result.data.data;
        _self.recents = data.recents || [];
      });
    },
    // 点击附加签署意见
    onOpinionItemClick: function (e, item) {
      this.opinion.text += stringTrim(item.content);
    },
    // 全屏签署意见
    onSignOpinionFullScreenClick: function (e) {
      this.openEditorModal();
    },
    // 意见即时显示
    opinionTextWysiwyg: debounce(function (newVal) {
      const _this = this;
      if (!_this.enableOpinionTextWysiwyg) {
        return;
      }
      let records = _this.records;
      records.forEach(record => {
        if (!(record.enableWysiwyg && record.includeOpinionTextVariable)) {
          return;
        }
        _this.previewOpinionTextRecord(record, newVal);
      });
    }, 250),
    previewOpinionTextRecord(record, newVal) {
      const _this = this;
      // 前置条件
      if (record.enablePreCondition && !isEmpty(record.recordConditions)) {
        let opinion = _this.opinion;
        let workData = _this.workView.getWorkData();
        let dyFormData = _this.workView.collectFormData();
        let data = {
          dyFormData: dyFormData,
          flowInstUuid: workData.flowInstUuid,
          opinionLabel: opinion.label,
          opinionText: opinion.text,
          opinionValue: opinion.value,
          record,
          taskInstUuid: workData.taskInstUuid,
          flowDefUuid: workData.flowDefUuid
        };
        $axios.post('/api/workflow/work/checkRecordPreCondition', data).then(({ data: result }) => {
          if (result.data) {
            _this.changeDyformRecordFieldValue(record, newVal);
          }
        });
      } else {
        _this.changeDyformRecordFieldValue(record, newVal);
      }
    },
    changeDyformRecordFieldValue(record, newValue) {
      const _this = this;
      let fieldName = record.field;
      let dyformWidget = _this.workView.getDyformWidget();
      // 记录初始的表单字段值记录
      if (!record.keepInitFieldValue) {
        let fieldValue = dyformWidget.dyform.getFieldValue(fieldName);
        record.keepInitFieldValue = true;
        record.initFieldValue = fieldValue;
      }
      let previewValue = record.initFieldValue || '';
      let previewText = `<div class="opinion-preview">
          <div class='opinion-user'>${_this._$USER.localUserName || _this._$USER.userName}（${this.$t(
        'WorkflowWork.opinionManager.Me',
        '我'
      )}）</div>
          <div class='opinion-text'>${newValue}</div>
        </div>`;
      if (record.assembler == 'descTaskFormOpinionAssembler') {
        previewValue = previewText + previewValue;
      } else {
        previewValue += previewText;
      }
      dyformWidget.dyform.setFieldValue(fieldName, previewValue);
    },
    // 意见立场变更，设置意见立场名称
    onOpinionPositionChange: function (e) {
      const _self = this;
      let label = '';
      let value = e.target.value;
      for (let index = 0; index < _self.opinions.length; index++) {
        if (_self.opinions[index].code == value) {
          label = _self.opinions[index].content;
          break;
        }
      }
      _self.opinion.label = label;
    },
    // 意见立场点击处理
    onOpinionPositionItemClick: function (e, item) {
      // 已选中取消选中
      if (this.opinion.value == item.code) {
        this.opinion.value = '';
        this.opinion.label = '';
      }
    },
    // 签署并提交
    onSignAndSubmitClick: function (e, item, closeModal) {
      this.opinion.text += stringTrim(item.content);
      // 点击签署意见按钮进行提交时，清空签署意见操作信息
      if (this.workView.currentAction && this.workView.currentAction.id == 'B004011') {
        this.workView.currentEvent = null;
        this.workView.currentAction = null;
      }
      this.onSubmitBtnClick(e);
      if (closeModal) {
        this.signOpinionFullScreenVisible = false;
      }
    },
    // 确定提交
    onSubmitBtnClick: function (e) {
      var _self = this;
      if (_self.isRequiredSubmitOpinion() && isEmpty(stringTrim(_self.opinion.text))) {
        _self.$message.error(this.$t('WorkflowWork.opinionManager.message.pleaseSignOpinion', '请先签署意见!'));
      } else {
        // 执行提交/转办/会签等操作
        if (!isEmpty(_self.action) && isFunction(_self.workView[_self.action])) {
          _self.workView[_self.action]();
        } else {
          // _self.hide();
          if (!_self.signOpinionFullScreenVisible) {
            _self.workView.currentEvent = null;
            _self.workView.currentAction = null;
          }
          _self._logger.commitBehaviorLog({
            type: 'click',
            element: {
              tag: 'BUTTON',
              text: e.target ? e.target.textContent || '提交' : '提交'
            },
            page: {
              url: window.location.href,
              title: _self.vPage != undefined ? _self.vPage.title || document.title : undefined,
              id: _self.vPage != undefined ? _self.vPage.pageId || _self.vPage.pageUuid : undefined
            },
            description: `${_self.workView.workFlow.workData.title} [${_self.workView.workFlow.workData.taskName}] :  ${
              e.target ? e.target.textContent || '提交' : '提交'
            }`
          });
          _self.workView.submit();
        }
      }
    },
    // 是否提交必填签署意见
    isRequiredSubmitOpinion: function () {
      return this.workView.isRequiredSubmitOpinion();
    },
    isRequiredOpinionPosition: function () {
      return this.enableOpinionPosition && this.requiredOpinionPosition && this.workView.isTodo();
    },
    // 打开签署意见
    openToSign: function (options = {}) {
      const _this = this;
      if (_this.isOpenEditorModal()) {
        _this.openEditorModal(options);
      } else {
        _this.$emit('openToSignOpinion', options);
      }
    },
    // 打开签署意见
    openToSignIfRequired: function (options = {}) {
      const _self = this;
      let action = options.action;
      let opinionText = stringTrim(_self.opinion.text);
      let opinionValue = stringTrim(_self.opinion.value);
      if (isEmpty(opinionText)) {
        if (_self.isOpenEditorModal()) {
          _self.openEditorModal(options);
        } else {
          _self.$emit('openToSignOpinion', options);
        }
        _self.$message.info(this.$t('WorkflowWork.opinionManager.message.pleaseSignOpinion', '请先签署意见!'));
        return true;
      } else if (
        _self.isRequiredOpinionPosition() &&
        isEmpty(opinionValue) &&
        (action == 'submit' || action == 'rollback' || action == 'directRollback')
      ) {
        if (_self.isOpenEditorModal()) {
          _self.openEditorModal(options);
        } else {
          _self.$emit('openToSignOpinion', options);
        }
        _self.$message.info(this.$t('WorkflowWork.opinionManager.message.pleaseChooseOpinionStanceFirst', '请先选择意见立场!'));
        return true;
      }
      return false;
    },
    onCancelSignOpinionClick() {
      const _this = this;
      _this.signOpinionFullScreenVisible = false;
      if (_this.opinionSnapshot) {
        _this.restore(_this.opinionSnapshot);
      }
    },
    onOkSignOpinionClick() {
      const _this = this;
      _this.signOpinionFullScreenVisible = false;
      _this.store();
    },
    onSignOpinionOptionButtonClick(event, button) {
      const _this = this;
      if (_this.openToSignIfRequired(_this.signOpinionOptions)) {
        return;
      }
      _this.signOpinionFullScreenVisible = false;

      button.callback.call(button.callbackScope);
    },
    onSignOpinionDefaultButtonClick(event, action) {
      const _this = this;
      if (_this.workView.isRequiredActionOpinion(action.methodName)) {
        if (_this.openToSignIfRequired(_this.signOpinionOptions)) {
          return;
        }
      }
      _this.signOpinionFullScreenVisible = false;

      _this.workView.setCurrentEvent(event);
      _this.workView.setCurrentAction(action);
      let methodName = action.optName || action.methodName;
      if (_this.workView[methodName]) {
        _this.workView[methodName].call(_this.workView, event, action);
      } else if (_this.workView.methods && _this.workView.methods[methodName]) {
        _this.workView.methods[methodName].call(_this.workView, event, action);
      } else {
        console.error(`Unknow action method [${methodName}]`, action);
      }
    },
    openEditorModal(options = {}) {
      const _this = this;
      _this.signOpinionOptions = options;
      _this.signOpinionFullScreenVisible = true;
      // 意见快照，用于弹窗取消时还原
      _this.opinionSnapshot = JSON.stringify(_this.opinion);
    },
    isOpenEditorModal() {
      let opinionEditorSetting = this.opinionEditorSetting;
      return opinionEditorSetting && (opinionEditorSetting.showMode == 'modal' || opinionEditorSetting.showMode == 'all');
    },
    getOpinion: function () {
      return this.opinion;
    },
    // 办理意见是否变更
    isOpinionChanged: function () {
      return this.initOpinionText != this.opinion.text || (this.opinion.value && this.opinion.value != '') || false;
    },
    // 本地存储签署意见
    store() {
      if (EASY_ENV_IS_BROWSER) {
        sessionStorage.setItem(this.getStorageKey(), JSON.stringify(this.opinion));
      }
    },
    // 本地还原签署意见
    restore(customOpinion) {
      if (EASY_ENV_IS_BROWSER) {
        try {
          let opinion = customOpinion || sessionStorage.getItem(this.getStorageKey());
          if (opinion) {
            if (/^{.*}$/gs.test(opinion)) {
              this.opinion = JSON.parse(opinion);
            } else {
              this.opinion.text = opinion;
            }
            this.fileList = this.opinion.files || [];
          }
        } catch (error) {
          console.error(error);
        }
      }
    },
    // 重置签署意见
    reset() {
      this.opinion = {
        text: '',
        label: '',
        value: '',
        files: []
      };
      this.fileList = [];
      this.store();
    },
    // 获取签署意见存储的key
    getStorageKey() {
      let workView = this.workView;
      let workData = workView.getWorkData();
      let flowDefUuid = workData.flowDefUuid;
      let flowInstUuid = workData.flowInstUuid;
      let taskInstUuid = workData.taskInstUuid;
      let userId = workData.userId || '';
      if (workView.isDraft()) {
        return flowDefUuid + userId;
      }
      return flowInstUuid + userId;
    },
    onFileChange(fileList) {
      const _this = this;
      _this.fileList2Opinion(fileList);
      _this.showOrHideUploading();
    },
    fileList2Opinion(fileList) {
      this.fileList = fileList;
      this.opinion.files = fileList
        .filter(file => file.status == 'done')
        .map(file => ({
          uuid: file.fileID,
          name: file.name,
          fileName: file.name,
          fileID: file.fileID,
          status: file.status
        }));
    },
    showOrHideUploading() {
      let isUploading = this.fileList.findIndex(file => file.status == 'uploading') != -1;
      if (isUploading) {
        this.workView.loading.visible = true;
      } else {
        this.workView.loading.visible = false;
      }
    }
  }
};
</script>
<style lang="less">
body {
  --w-wf-opinion-editor-border-radius: 4px;
}
</style>
<style lang="less" scoped>
.opinion-editor {
  .sign-opinion {
    margin-top: 12px;
    margin-bottom: 12px;
    --w-card-border-radius: var(--w-wf-opinion-editor-border-radius);
    --w-card-head-bg-color: var(--w-gray-color-2);
    --w-card-head-min-height: 40px;
    --w-card-head-padding: 0 4px 0 12px;
    --w-card-head-title-padding: 4px 0;
    --w-card-head-title-color: var(--w-text-color-dark);
    --w-card-head-title-size: var(--w-font-size-base);
    --w-card-head-title-weight: bold;

    .opinion-text {
      .opinion-text-title {
        background-color: var(--w-gray-color-2);
        color: var(--w-text-color-dark);
        font-size: var(--w-font-size-base);
        font-weight: bold;
        line-height: 32px;
        padding: 8px 12px 0;
        border-radius: var(--w-wf-opinion-editor-border-radius) var(--w-wf-opinion-editor-border-radius) 0 0;
      }
      --w-input-border-color: transparent;
      --w-input-background-color: var(--w-gray-color-1);
      --w-input-border-radius: 0 0 var(--w-wf-opinion-editor-border-radius) var(--w-wf-opinion-editor-border-radius);
      --w-input-border-color-focus: transparent;
      --w-input-border-color-hover: transparent;
      --w-input-box-shadow-focus: transparent;
      --w-input-lr-padding: 0px;
    }

    .opinion-position {
      background-color: var(--w-primary-color-1);
      margin-bottom: 12px;
      border-radius: 4px;
      padding: 8px;
    }
  }
  .opinion-file {
    margin-bottom: 12px;
  }
}
.wf-opinion-sign-modal-body {
  --w-wf-opinion-editor-border-radius: 4px;
  border: 1px solid var(--w-border-color-light);
  border-radius: 4px;
  position: relative;
  > .ant-row > .ant-col {
    padding: var(--w-padding-md);

    &:last-child {
      border-left: 1px solid var(--w-border-color-light);
    }

    .opinion-position {
      margin-bottom: 12px;
      width: e('calc(100% - 50px)');
      background-color: var(--w-primary-color-1);
      border-radius: 4px;
      padding: 8px;
    }
    .opinion-text {
      --w-input-border-color: var(--w-gray-color-1);
      --w-input-background-color: var(--w-gray-color-1);
      --w-input-border-radius: var(--w-wf-opinion-editor-border-radius);
      --w-input-border-color-focus: transparent;
      --w-input-border-color-hover: transparent;
      --w-input-box-shadow-focus: transparent;
      .ant-input {
        padding: 0;
      }
      margin-bottom: 12px;
    }
    .file-list-container {
      border: none;
      ::v-deep {
        .ant-card-head {
          border-bottom: 0;
          .ant-card-head-title {
            display: none;
          }
          .ant-card-extra {
            line-height: var(--w-card-head-title-line-height);
            float: left;
            margin-left: unset;
          }
        }
        .ant-card-body {
          background-color: var(--w-gray-color-2);
          padding: 0 12px;
        }
      }
    }
    .opinion-tabs-container {
      margin-left: -8px;
      margin-right: -20px;
      ::v-deep .ant-tabs {
        border: none;
        > .ant-tabs-bar {
          padding-left: 12px;
          padding-right: 12px;
          background-color: transparent;
          .ant-tabs-ink-bar {
            height: 0;
          }
          border-bottom: 0;
        }
        .ant-list-item {
          border-radius: var(--w-wf-opinion-editor-border-radius);
        }
        .ps {
          padding-right: 12px;
        }
      }
    }
  }
  .open-recents {
    position: absolute;
    top: 20px;
    right: 20px;
    z-index: 1;
  }
}
</style>
