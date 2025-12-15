<template>
  <view
    class="work-view container"
    :style="workViewStyle"
    @touchstart="workViewTouchStart"
    @touchmove="workViewTouchMove"
    @touchend="workViewTouchEnd"
  >
    <uni-nav-bar
      :statusBar="true"
      :border="false"
      :shadow="true"
      :fixed="true"
      left-icon="left"
      :title="title"
      @clickLeft="goBack"
    >
      <block slot="right">
        <slot name="nav-menu">
          <view v-if="!isDraft && isAllowViewProcess">
            <uni-icons type="bars" size="22" @click="menuItemClick" />
          </view>
        </slot>
      </block>
    </uni-nav-bar>
    <slot name="header"></slot>
    <view class="work-view-content">
      <slot name="dyform" :dyformOptions="dyformOptions">
        <w-dyform
          class="work-view-dyform"
          v-if="dyformOptions.formData != null"
          ref="wDyform"
          :options="dyformOptions"
          @initSuccess="onDyformInitSuccess"
          @tabChange="onDyformTabChange"
        >
          <!-- #ifdef H5 || APP-PLUS -->
          <!-- 子流程 -->
          <!-- 承办情况 -->
          <!-- v-slot:[getSubflowUndertakeSituationSlotName(blockCode)] -->
          <template v-if="subTaskData">
            <template
              v-for="(shareDatas, blockCode) in subTaskData && subTaskData.shareDatas"
              :slot="getSubflowUndertakeSituationSlotName(blockCode)"
            >
              <view v-for="(shareData, index) in shareDatas" :key="'sd_' + blockCode + '_' + index">
                <workflowSubflowShareData
                  :workView="workView"
                  :subTaskData="subTaskData"
                  :shareData="shareData"
                  :index="index"
                ></workflowSubflowShareData>
              </view>
            </template>
          </template>
          <!-- 信息分发 -->
          <template v-if="subTaskData">
            <template
              v-for="(distributeInfos, blockCode) in subTaskData && subTaskData.distributeInfos"
              :slot="getSubflowInfoDistributionSlotName(blockCode)"
            >
              <view v-for="(distributeInfo, index) in distributeInfos" :key="'rp_' + blockCode + '_' + index">
                <workflowSubflowDistributeInfo
                  :workView="workView"
                  :subTaskData="subTaskData"
                  :distributeInfo="distributeInfo"
                  :index="index"
                ></workflowSubflowDistributeInfo>
              </view>
            </template>
          </template>
          <!-- 操作记录 -->
          <template v-if="subTaskData">
            <template
              v-for="(taskOperations, blockCode) in subTaskData && subTaskData.taskOperations"
              :slot="getSubflowOperationRecordSlotName(blockCode)"
            >
              <view v-for="(relateOperation, index) in taskOperations" :key="'rp_' + blockCode + '_' + index">
                <workflowSubflowRelateOperation
                  :workView="workView"
                  :subTaskData="subTaskData"
                  :relateOperation="relateOperation"
                  :index="index"
                ></workflowSubflowRelateOperation>
              </view>
            </template>
          </template>

          <!-- 分支流 -->
          <!-- 承办情况 -->
          <template v-if="branchTaskData">
            <template
              v-for="(shareDatas, blockCode) in branchTaskData && branchTaskData.shareDatas"
              :slot="getBranchTaskUndertakeSituationSlotName(blockCode)"
            >
              <view v-for="(shareData, index) in shareDatas" :key="'tsd_' + blockCode + '_' + index">
                <workflowBranchTaskShareData
                  :workView="workView"
                  :subTaskData="branchTaskData"
                  :shareData="shareData"
                  :index="index"
                ></workflowBranchTaskShareData>
              </view>
            </template>
          </template>
          <!-- 信息分发 -->
          <template v-if="branchTaskData">
            <template
              v-for="(distributeInfos, blockCode) in branchTaskData && branchTaskData.distributeInfos"
              :slot="getBranchTaskInfoDistributionSlotName(blockCode)"
            >
              <view v-for="(distributeInfo, index) in distributeInfos" :key="'trp_' + blockCode + '_' + index">
                <workflowBranchTaskDistributeInfo
                  :workView="workView"
                  :subTaskData="branchTaskData"
                  :distributeInfo="distributeInfo"
                  :index="index"
                ></workflowBranchTaskDistributeInfo>
              </view>
            </template>
          </template>
          <!-- 操作记录 -->
          <template v-if="branchTaskData">
            <template
              v-for="(taskOperations, blockCode) in branchTaskData && branchTaskData.taskOperations"
              :slot="getBranchTaskOperationRecordSlotName(blockCode)"
            >
              <view v-for="(relateOperation, index) in taskOperations" :key="'trp_' + blockCode + '_' + index">
                <workflowBranchTaskRelateOperation
                  :workView="workView"
                  :subTaskData="branchTaskData"
                  :relateOperation="relateOperation"
                  :index="index"
                ></workflowBranchTaskRelateOperation>
              </view>
            </template>
          </template>
          <!-- #endif -->
        </w-dyform>
      </slot>
      <view class="work-view-sign-opinion" v-show="hasBottomOprateBar">
        <slot name="sign-opinion">
          <view class="opinion-input" v-if="signOptionOptions.workView != null && isAllowSignOpinion">
            <input
              class="uni-input"
              v-model="signOptionOptions.opinionText"
              placeholder="审批意见"
              :focus="false"
              @click="onSignOpinionFocus"
            />
          </view>
          <view class="btn-group">
            <view v-for="(action, index) in actions" :key="index" class="btn" :class="'btn-' + action.id">
              <button :class="action.cssClass" @tap="actionTap($event, action)">
                {{ action.text }}
              </button>
            </view>
          </view>
        </slot>
      </view>
    </view>
    <!-- 签署意见 -->
    <uni-popup ref="signOpinionPopup" type="bottom">
      <workflow-opinion-editor
        v-if="signOptionOptions.workView != null"
        :options="signOptionOptions"
        :setting="workFLowSettingOPINIONFILE"
        :initOpinionText="initOpinionText"
        @signOpinionOk="onSignOpinionOk"
      ></workflow-opinion-editor>
    </uni-popup>

    <!-- 选择下一环节、流向 -->
    <uni-popup ref="toTaskPopup">
      <view class="popup-work-view-dialog">
        <view class="popup-title">
          <text class="popup-title-text">{{ toTaskData.title }}</text>
        </view>
        <view class="popup-content">
          <scroll-view class="popup-content-scroll-view" scroll-y="true">
            <view class="uni-list">
              <checkbox-group v-if="toTaskData.multiselect" @change="onToTaskCheckboxChange">
                <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in toTaskData.toTasks" :key="index">
                  <view style="display: none">
                    <checkbox :value="toTaskData.useDirection ? item.directionId : item.id" />
                  </view>
                  <view class="popup-check-item">
                    <view style="flex: 1">{{ item.name }}</view>
                    <view class="popup-check-icon" v-if="item.checked">
                      <uni-icons type="checkmarkempty" color="#2979ff" />
                    </view>
                  </view>
                </label>
              </checkbox-group>
              <radio-group v-else @change="onToTaskRadioChange">
                <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in toTaskData.toTasks" :key="index">
                  <view style="display: none">
                    <radio :value="toTaskData.useDirection ? item.directionId : item.id" />
                  </view>
                  <view class="popup-check-item">
                    <view style="flex: 1">{{ item.name }}</view>
                    <view class="popup-check-icon" v-if="item.checked">
                      <uni-icons type="checkmarkempty" color="#2979ff" />
                    </view>
                  </view>
                </label>
              </radio-group>
            </view>
          </scroll-view>
        </view>
        <view class="popup-button-box">
          <button class="popup-button" @tap="onToTaskOk">确定</button>
        </view>
      </view>
    </uni-popup>

    <!-- 选择退回环节 -->
    <uni-popup ref="rollbackTaskPopup">
      <view class="popup-work-view-dialog">
        <view class="popup-title">
          <text class="popup-title-text">{{ rollbackTaskData.title }}</text>
        </view>
        <view class="popup-content">
          <scroll-view class="popup-content-scroll-view" scroll-y="true">
            <view class="uni-list">
              <radio-group @change="onRollbackTaskRadioChange">
                <label
                  class="uni-list-cell uni-list-cell-pd"
                  v-for="(item, index) in rollbackTaskData.toTasks"
                  :key="index"
                >
                  <view style="display: none">
                    <radio :value="item.id" />
                  </view>
                  <view class="popup-check-item">
                    <view style="flex: 1">{{ item.name }}</view>
                    <view class="popup-check-icon" v-if="item.checked">
                      <uni-icons type="checkmarkempty" color="#2979ff" />
                    </view>
                  </view>
                </label>
              </radio-group>
            </view>
          </scroll-view>
        </view>
        <view class="popup-button-box">
          <button class="popup-button" @tap="onRollbackTaskOk">确定</button>
        </view>
      </view>
    </uni-popup>

    <!-- 选择特送环节 -->
    <uni-popup ref="gotoTaskPopup">
      <view class="popup-work-view-dialog">
        <view class="popup-title">
          <text class="popup-title-text">{{ gotoTaskData.title }}</text>
        </view>
        <view class="popup-content">
          <scroll-view class="popup-content-scroll-view" scroll-y="true">
            <view class="uni-list">
              <radio-group @change="onGotoTaskRadioChange">
                <label
                  class="uni-list-cell uni-list-cell-pd"
                  v-for="(item, index) in gotoTaskData.toTasks"
                  :key="index"
                >
                  <view style="display: none">
                    <radio :value="item.id" />
                  </view>
                  <view class="popup-check-item">
                    <view style="flex: 1">{{ item.name }}</view>
                    <view class="popup-check-icon" v-if="item.checked">
                      <uni-icons type="checkmarkempty" color="#2979ff" />
                    </view>
                  </view>
                </label>
              </radio-group>
            </view>
          </scroll-view>
        </view>
        <view class="popup-button-box">
          <button class="popup-button" @tap="onGotoTaskOk">确定</button>
        </view>
      </view>
    </uni-popup>

    <!-- 选择职位发起流程 -->
    <uni-popup ref="multiJobPopup">
      <view class="popup-work-view-dialog">
        <view class="popup-title">
          <text class="popup-title-text">{{ multiJobData.title }}</text>
        </view>
        <view class="popup-content">
          <scroll-view class="popup-content-scroll-view" scroll-y="true">
            <view class="uni-list">
              <radio-group @change="onMultiJobRadioChange">
                <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in multiJobData.jobs" :key="index">
                  <view style="display: none">
                    <radio :value="item.id" />
                  </view>
                  <view class="popup-check-item">
                    <view style="flex: 1">{{ item.text || item.name }}</view>
                    <view class="popup-check-icon" v-if="item.checked">
                      <uni-icons type="checkmarkempty" color="#2979ff" />
                    </view>
                  </view>
                </label>
              </radio-group>
            </view>
          </scroll-view>
        </view>
        <view class="popup-button-box">
          <button class="popup-button" @tap="onMultiJobOk">确定</button>
        </view>
      </view>
    </uni-popup>

    <!-- 办理过程 -->
    <uni-drawer ref="showRight" mode="right" :width="300">
      <workflow-process-viewer
        v-if="processViewerOptions.workView != null"
        :options="processViewerOptions"
      ></workflow-process-viewer>
    </uni-drawer>

    <!-- 是否进入连续签批 -->
    <uni-popup ref="lxqpConfirmPopup" type="dialog">
      <uni-popup-dialog
        type="info"
        @confirm="onAutoSubmitOk"
        @close="onAutoSubmitCancel"
        :title="autoSubmitData.title"
        class="wf-auto-submit-modal"
      >
        <rich-text :nodes="autoSubmitData.message"></rich-text>
      </uni-popup-dialog>
    </uni-popup>

    <!-- 阅读记录 -->
    <uni-popup ref="readLogPopup" background-color="#ffffff" borderRadius="8px 8px 0 0" @change="readLogPopupChange">
      <workflowReadLog :data="readLogData"></workflowReadLog>
    </uni-popup>

    <!-- 流程异常组织选择 -->
    <view v-if="errorOrgSelectOptions">
      <workflowErrorOrgSelect :options="errorOrgSelectOptions"></workflowErrorOrgSelect>
    </view>
  </view>
</template>

<script>
import { isEmpty, isFunction, isArray, each, trim as stringTrim, findIndex, concat, assign } from "lodash";
import { appContext, workFlowUtils } from "wellapp-uni-framework";
import WorkView from "./uni-WorkView";
import workflowOpinionEditor from "./workflow-opinion-editor.vue";
import workflowProcessViewer from "./workflow-process-viewer.vue";
import workflowSubflowShareData from "./workflow-subflow-share-data.vue";
import workflowSubflowDistributeInfo from "./workflow-subflow-distribute-info.vue";
import workflowSubflowRelateOperation from "./workflow-subflow-relate-operation.vue";
import workflowBranchTaskShareData from "./workflow-branch-task-share-data.vue";
import workflowBranchTaskDistributeInfo from "./workflow-branch-task-distribute-info.vue";
import workflowBranchTaskRelateOperation from "./workflow-branch-task-relate-operation.vue";
import workflowErrorOrgSelect from "./w-work-error-org-select.vue";
import workflowReadLog from "./w-workflow-read-log.vue";

const EVENTS = {
  load: "load",
  dyformInitSuccess: "dyformInitSuccess",
};

export default {
  props: {
    options: {
      type: Object,
      required: true,
    },
  },
  components: {
    workflowOpinionEditor,
    workflowProcessViewer,
    workflowSubflowShareData,
    workflowSubflowDistributeInfo,
    workflowSubflowRelateOperation,
    workflowBranchTaskShareData,
    workflowBranchTaskDistributeInfo,
    workflowBranchTaskRelateOperation,
    workflowErrorOrgSelect,
    workflowReadLog,
  },
  data() {
    let isNewFormData = isEmpty(this.workData && this.workData.dataUuid);
    return {
      definitionVjson: null,
      displayState: this.workData && this.workData.canEditForm == false ? "label" : "edit", // 表单默认状态可编辑
      isNewFormData,
      title: "流程单据",
      buttonRect: {},
      touchInfo: {},
      dyformOptions: {},
      workViewStyle: {
        height: "500px",
        // transform: "translate3d(0, 0, 0)"
      },
      workView: null,
      subTaskData: null, // 子流程数据
      branchTaskData: null, // 分支流数据
      isDraft: true,
      actions: [],
      moreActions: [],
      signOptionOptions: {
        workView: null,
        buttons: [],
        opinionText: "",
      },
      processViewerOptions: {
        workView: null,
      },
      toTaskData: {
        title: "",
        useDirection: true, // 使用流向
        multiselect: false, // 是否多选
        toTasks: [], // 下一流程
      },
      rollbackTaskData: {
        title: "",
        toTasks: [], // 退回环节
      },
      gotoTaskData: {
        title: "",
        toTasks: [], // 特送环节
      },
      multiJobData: {
        title: "",
        jobs: [], // 选择职位
      },
      tabs: undefined,
      autoSubmitVisible: false,
      autoSubmitData: {
        title: "",
        message: "", // 自动提交
      },
      readLogVisible: false,
      readLogData: {
        readUser: [], // 阅读记录
        unReadUser: [],
      },
      printTemplateVisible: false,
      printTemplateData: {
        templates: [], // 选择套打模板
        templateId: "", // 选择的套打模板ID
        callback: null, // 回调函数
      },
      errorOrgSelectOptions: undefined,
      initOpinionText: "", //记录初始签署意见
    };
  },

  created: function () {
    var _self = this;
    var options = _self.options;
    var taskInstUuid = options.taskInstUuid;
    this.workData = _self.options.workBean;
    let workData = this.workData;
    _self.title = workData.title;
    _self.isDraft = options.aclRole == "DRAFT" || isEmpty(taskInstUuid);
    console.log(_self.options.settings);
    _self.initOpinionText = workData.opinionText || "";
    new WorkView({
      workData: this.workData,
      settings: _self.options.settings,
      // loading: _self.loading,
      $widget: _self,
      onLoad() {
        _self.workView = this;
        let definitionVjson = JSON.parse(_self.workData.dyFormData.definitionVjson);
        if (_self.workData.dyFormData.formDefinition) {
          let formDefinition = JSON.parse(_self.workData.dyFormData.formDefinition);
          _self.tabs = formDefinition.tabs;
        }
        _self.definitionVjson = definitionVjson;
        // 加载子流程数据
        if (workData.subTaskData) {
          _self.loadSubTaskData(workData.subTaskData);
        }
        // 加载分支流流数据
        if (workData.branchTaskData) {
          _self.loadBranchTaskData(workData.branchTaskData);
        }
        // 流程数据加载事件
        _self.$emit(EVENTS.load, _self.workView);

        // 草稿或待办才可编辑
        if ((this.isDraft() || this.isTodo()) && _self.workData.canEditForm) {
          _self.displayState = "edit";
        } else {
          _self.displayState = "label";
        }

        _self.initDyform();
        _self.signOptionOptions.workView = _self.workView;
        _self.createActionSheetIfRequried();

        if (!_self.options.settings) {
          _self.getSetting().then((settings) => {
            _self.options.settings = settings;
            _self.workView.settings = settings;
          });
        }

        _self.$nextTick(() => {
          // 待办时自动触发签署意见弹出框
          if (_self.workView.isDraft() || _self.workView.isTodo()) {
            _self.$refs.signOpinionPopup.open();
            _self.$refs.signOpinionPopup.close();
          }
        });
      },
    });
    _self.processViewerOptions.workView = _self.workView;
    invokeDevelopmentMethod('created');
  },
  onUnload: function () {},
  mounted: function () {
    // 更新窗口高度
    uni.getSystemInfo({
      success: (result) => {
        var windowHeight = result.windowHeight;
        console.log("windowHeight: " + windowHeight);
        this.workViewStyle.height = windowHeight + "px";
        // const query = uni.createSelectorQuery().in(this);
        // query.select('.footer').boundingClientRect(data => {
        //   console.log("footer: " + JSON.stringify(data));
        // }).exec();
      },
    });
     invokeDevelopmentMethod('mounted');
  },
  computed: {
    formElementRules() {
      let workData = this.workData;
      let taskForm = workData.taskForm;
      console.log("taskForm", taskForm);
      let formElementRules = {};
      let allFormFieldWidgetIds = taskForm.allFormFieldWidgetIds;
      let formBtnRightSettings = taskForm.formBtnRightSettings;
      if (allFormFieldWidgetIds) {
        let formBtnRightSettingJson = {};
        // 按钮设置内容
        each(formBtnRightSettings, (item) => {
          let param = item.split("=");
          formBtnRightSettingJson[param[0]] = JSON.parse(param[1]);
        });
        each(allFormFieldWidgetIds, (item, index) => {
          let itemArr = item.split("=");
          if (itemArr.length == 2) {
            let field = itemArr[0];
            let data = itemArr[1] && JSON.parse(itemArr[1]);
            if (field.split(":").length == 2) {
              // 从表字段
              let fieldName = field.split(":")[1];
              let subFormUuid = field.split(":")[0]; //从表表单id
              let subWidgetId = data.subWidgetId; //从表组件id
              let editFieldMap = taskForm.editFieldMap[subFormUuid] || []; // 编辑
              let notNullFieldMap = taskForm.notNullFieldMap[subFormUuid] || []; // 必填
              let hideFieldMap = taskForm.hideFieldMap[subFormUuid] || []; // 隐藏
              if (!formElementRules.hasOwnProperty(subWidgetId)) {
                formElementRules[subWidgetId] = { children: [] };
              }
              formElementRules[subWidgetId].children[data.id] = {
                // displayAsLabel: this.displayState == 'label',
                editable: this.displayState == "edit" && editFieldMap.indexOf(fieldName) > -1,
                hidden: hideFieldMap.indexOf(fieldName) > -1,
                required: notNullFieldMap.indexOf(fieldName) > -1,
              };
              let btnFieldName = subFormUuid + "_" + fieldName;
              if (formBtnRightSettingJson[btnFieldName]) {
                formElementRules[subWidgetId].children[data.id].editable = this.displayState == "edit";
                let headerButton = concat(
                  formBtnRightSettingJson[btnFieldName].show.headerButton,
                  formBtnRightSettingJson[btnFieldName].edit.headerButton
                );
                let rowButton = concat(
                  formBtnRightSettingJson[btnFieldName].show.rowButton,
                  formBtnRightSettingJson[btnFieldName].edit.rowButton
                );
                // 文本情况下，不加入编辑类
                if (this.displayState == "label") {
                  headerButton = [formBtnRightSettingJson[btnFieldName].show.headerButton];
                  rowButton = [formBtnRightSettingJson[btnFieldName].show.rowButton];
                }

                formElementRules[subWidgetId].children[data.id].buttons = {
                  headerButton: headerButton.join(";"),
                  rowButton: rowButton.join(";"),
                };
              }
            } else {
              // 主表字段
              let editFieldMap = taskForm.editFieldMap[taskForm.formUuid] || []; // 编辑
              let notNullFieldMap = taskForm.notNullFieldMap[taskForm.formUuid] || []; // 必填
              let hideFieldMap = taskForm.hideFieldMap[taskForm.formUuid] || []; // 隐藏
              if (data.outerId) {
                // 从表
                if (!formElementRules.hasOwnProperty(data.id)) {
                  formElementRules[data.id] = { children: [] };
                }
                assign(formElementRules[data.id], {
                  // displayAsLabel: this.displayState == 'label',
                  editable: this.displayState == "edit",
                  hidden: hideFieldMap.indexOf(field) > -1,
                });
                if (formBtnRightSettingJson[data.formUuid]) {
                  formElementRules[data.id].buttons =
                    this.displayState == "edit"
                      ? formBtnRightSettingJson[data.formUuid].edit
                      : formBtnRightSettingJson[data.formUuid].show;
                }
              } else {
                formElementRules[data.id] = {
                  // displayAsLabel: this.displayState == 'label',
                  editable: this.displayState == "edit" && editFieldMap.indexOf(field) > -1,
                  hidden: hideFieldMap.indexOf(field) > -1,
                  required: notNullFieldMap.indexOf(field) > -1,
                };
                if (formBtnRightSettingJson[field]) {
                  formElementRules[data.id].editable = this.displayState == "edit";
                  let headerButton = concat(
                    formBtnRightSettingJson[field].show.headerButton,
                    formBtnRightSettingJson[field].edit.headerButton
                  );
                  let rowButton = concat(
                    formBtnRightSettingJson[field].show.rowButton,
                    formBtnRightSettingJson[field].edit.rowButton
                  );
                  //文本情况下，不加入编辑类
                  if (this.displayState == "label") {
                    headerButton = [formBtnRightSettingJson[field].show.headerButton];
                    rowButton = [formBtnRightSettingJson[field].show.rowButton];
                  }
                  formElementRules[data.id].buttons = {
                    headerButton: headerButton.join(";"),
                    rowButton: rowButton.join(";"),
                  };
                }
              }
            }
          }
        });
        let hideBlocks = taskForm.hideBlocks;
        let hideTabs = taskForm.hideTabs;
        each(hideBlocks, (item) => {
          formElementRules[item] = {
            hidden: true,
          };
        });
        if (workData.dyFormData) {
          each(workData.dyFormData.blocks, (item) => {
            // 当前块不在规则，代表不隐藏，而组件本身是隐藏的，就要设置不隐藏
            if (item.id && !formElementRules[item.id] && item.hide) {
              formElementRules[item.id] = {
                hidden: false,
              };
            }
          });
        }
        if (this.tabs) {
          // 流程规则仅对隐藏的tab页签进行配置，因此默认情况下页签都是显示的
          for (let tKey in this.tabs) {
            if (this.tabs[tKey].parentId) {
              formElementRules[tKey] = {
                hidden: false,
              };
            }
          }
        }

        each(hideTabs, (item) => {
          formElementRules[item] = {
            hidden: true,
          };
        });
      }

      console.log("taskForm formElementRules", formElementRules);
      return Object.keys(formElementRules).length === 0 ? null : formElementRules;
    },
    isAllowSignOpinion: function () {
      return this.workView && this.workView.isAllowSignOpinion();
    },
    isAllowViewProcess: function () {
      return this.workView && this.workView.isAllowViewProcess();
    },
    // 是否显示底部操作栏
    hasBottomOprateBar() {
      return this.isAllowSignOpinion || (this.actions && this.actions.length);
    },
    // 流程设置全局设置(签署意见附件)
    workFLowSettingOPINIONFILE() {
      if (this.options.settings) {
        return this.options.settings["OPINION_FILE"];
      }
      return { accept: "" };
    },
  },
  methods: {
    // 调用二开方法
    invokeDevelopmentMethod() {
      if (!this.$developJsInstance) {
        return;
      }
      this.$developJsInstance.forEach(developJsInstance => {
        let method = arguments[0];
        let args = [];
        if (arguments.length > 1) {
          for (let i = 1, len = arguments.length; i < len; i++) {
            args.push(arguments[i]);
          }
        }
        if (typeof developJsInstance[method] === 'function') {
          developJsInstance[method].apply(developJsInstance, args);
        }
      });
    },
    goBack() {
      uni.navigateBack({
        delta: 1,
      });
    },
    workViewTouchStart(e) {
      // console.log(e);
      if (this.isDraft) {
        return;
      }
      this.touchInfo.touchStartX = e.touches[0].pageX;
      this.touchInfo.touchStartY = e.touches[0].pageY;
    },
    workViewTouchMove(e) {
      // console.log(e);
      if (this.isDraft) {
        return;
      }
      let deltaX = e.changedTouches[0].pageX - this.touchInfo.touchStartX;
      let deltaY = e.changedTouches[0].pageY - this.touchInfo.touchStartY;
      if (Math.abs(deltaX) > 50 && Math.abs(deltaX) > Math.abs(deltaY)) {
        if (deltaX < 0) {
          // 左划效果
          // let distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
          // this.workViewStyle.transform = "translate3d(" + -distance + "px, 0, 0)";
          // console.log("workViewStyle transform translate3d(" + -distance + "px, 0, 0)");
        }
      }
    },
    workViewTouchEnd(e) {
      if (this.isDraft) {
        return;
      }
      // let touchEndX = e.changedTouches[0].pageX;
      // let touchEndY = e.changedTouches[0].pageY;
      let deltaX = e.changedTouches[0].pageX - this.touchInfo.touchStartX;
      let deltaY = e.changedTouches[0].pageY - this.touchInfo.touchStartY;
      if (Math.abs(deltaX) > 50 && Math.abs(deltaX) > Math.abs(deltaY)) {
        if (deltaX < 0) {
          // 左划打开办理过程
          // if (this.workView.dyform.isTabLayout()) {
          //   return;
          // }
          this.showDrawer("showRight");
        }
      }
      // setTimeout(() => {
      // this.workViewStyle.transform = "translate3d(0, 0, 0)";
      // }, 200);
    },
    // 打开窗口
    showDrawer(e) {
      if (this.disableDrawer) {
        return;
      }
      this.$refs[e].open();
    },
    // 关闭窗口
    closeDrawer(e) {
      this.$refs[e].close();
    },
    menuItemClick() {
      this.showDrawer("showRight");
    },
    // 加载子流程数据
    loadSubTaskData(subTaskData) {
      const _self = this;
      uni.request({
        url: "/api/workflow/work/loadSubTaskData",
        method: "POST",
        data: subTaskData,
        success: function (result) {
          let data = result.data.data;
          _self.subTaskData = data;
        },
      });
    },
    // 加载分支流流数据
    loadBranchTaskData(branchTaskData) {
      const _self = this;
      uni.request({
        url: "/api/workflow/work/loadBranchTaskData",
        method: "POST",
        data: branchTaskData,
        success: function (result) {
          let data = result.data.data;
          _self.branchTaskData = data;
        },
      });
    },
    // 初始化表单
    initDyform() {
      var _self = this;
      let workData = _self.workData;
      var dyFormData = workData.dyFormData;
      var isFirst = isEmpty(workData.flowInstUuid);
      var dyformOptions = {
        formData: dyFormData,
        formUuid: dyFormData.formUuid,
        dataUuid: isFirst ? null : dyFormData.dataUuid,
        recordInitFormDatas: true,
        displayAsLabel: this.displayState == "label",
        optional: {
          isFirst: isFirst,
          title: workData.title,
        },
        formDefinition: dyFormData.definitionVjson,
        formElementRules: this.formElementRules,
      };
      this.dyformOptions = dyformOptions;
      this.$nextTick(() => {
        _self.onDyformInitSuccess(_self.$refs.wDyform);
      });
    },
    onDyformInitSuccess: function (dyform) {
      this.workView.dyform = dyform;
      // 输入的动态表单值
      let workData = this.workView.getWorkData();
      if (workData.extraParams) {
        for (let key in workData.extraParams) {
          if (key.length > 8 && key.substring(0, 8) == "ep_dyfs_") {
            let fieldName = key.substring(8);
            let fieldVal = workData.extraParams[key];
            this.$refs.wDyform.dyform.setFieldValue(fieldName, fieldVal);
          }
        }
      }
      this.$emit(EVENTS.dyformInitSuccess, this.workView);
    },
    onDyformTabChange: function (tabIndex) {
      const _self = this;
      _self.disableDrawer = true;
      setTimeout(function () {
        _self.disableDrawer = false;
      }, 1);
    },
    createActionSheetIfRequried() {
      var _self = this;
      let showBtnCount = 2;
      let actions = _self.workView.getActions();
      if (actions.length <= showBtnCount) {
        _self.actions = actions;
      } else {
        let newActions = [];
        let newMoreActions = [];
        for (var i = 0; i < actions.length; i++) {
          let action = actions[i];
          if (i < showBtnCount - 1) {
            newActions.push(action);
          } else if (i == showBtnCount - 1) {
            newActions.push({
              id: "more",
              name: "more",
              text: "更多",
            });
            newMoreActions.push(action);
          } else {
            newMoreActions.push(action);
          }
        }
        _self.actions = newActions;
        _self.moreActions = newMoreActions;
      }
      setTimeout(function () {
        _self.autoSubmitIfRequried();
      }, 500);
    },
    actionTap(e, action) {
      const _self = this;
      // 更多
      // console.log(JSON.stringify(action));
      let methodName = action.methodName || action.optName;
      if (action.id == "more") {
        _self.actionSheetTap();
      } else if (_self.workView[methodName]) {
        // 流程操作
        // 绑定当前事件
        _self.workView.setCurrentEvent(e);
        _self.workView.setCurrentAction(action);
        _self.workView[methodName].call(_self.workView, e, action);
      } else if (_self.workView.methods && _self.workView.methods[methodName]) {
        _self.workView.setCurrentEvent(e);
        _self.workView.setCurrentAction(action);
        _self.workView.methods[methodName].call(_self.workView, e, action);
      } else if (!isEmpty(action.eventHandler)) {
        action.eventHandler.pageContext = _self.pageContext;
        action.eventHandler.$evtWidget = _self.workView.$widget;
        let eventHandler = action.eventHandler || {};
        let eventParams = action.eventParams || {};
        appContext.dispatchEvent({
          ui: _self,
          ...eventHandler,
          ...eventParams,
        });
      } else if (action.piUuid) {
        appContext.startApp({
          ui: _self,
          workView: _self.workView,
          piUuid: action.piUuid,
          params: action.params || {},
        });
      }
    },
    actionSheetTap() {
      const that = this;
      var itemList = [];
      for (var i = 0; i < that.moreActions.length; i++) {
        itemList.push(that.moreActions[i].text);
      }
      uni.showActionSheet({
        itemList: itemList,
        popover: {
          // 104: navbar + topwindow 高度，暂时 fix createSelectorQuery 在 pc 上获取 top 不准确的 bug
          top: that.buttonRect.top + 104 + that.buttonRect.height,
          left: that.buttonRect.left + that.buttonRect.width / 2,
        },
        success: (e) => {
          console.log(e.tapIndex);
          that.actionTap(e, that.moreActions[e.tapIndex]);
        },
      });
    },
    // 签署意见获取焦点事件
    onSignOpinionFocus: function () {
      var _self = this;
      _self.$refs.signOpinionPopup.open("bottom");
    },
    onSignOpinionOk: function (data) {
      var _self = this;
      _self.$refs.signOpinionPopup.close();
      _self.workView.opinionToWorkData(data);
      _self.signOptionOptions.opinionText = data.text;
      _self.signOptionOptions.opinionLabel = data.label;
      _self.signOptionOptions.opinionValue = data.value;
      _self.signOptionOptions.opinionFiles = data.files;
    },
    // 打开签署意见
    openToSignOpinion: function (data) {
      var _self = this;
      _self.signOptionOptions.buttons = data.buttons;
      _self.signOptionOptions.action = data.action;
      _self.$refs.signOpinionPopup.open("bottom");
    },
    // 选择下一环节、流向
    judgmentBranchFlowNotFound: function (data) {
      this.toTaskData = data;
      this.$refs.toTaskPopup.open("bottom");
    },
    onToTaskCheckboxChange: function (event) {
      var _self = this;
      var useDirection = _self.toTaskData.useDirection;
      var items = _self.toTaskData.toTasks;
      var values = event.detail.value;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        var value = useDirection ? item.directionId : item.id;
        if (values.indexOf(value) >= 0) {
          // item.checked = true;
          _self.$set(item, "checked", true);
        } else {
          // item.checked = false;
          _self.$set(item, "checked", false);
        }
      }
    },
    onToTaskRadioChange: function (event) {
      var _self = this;
      var useDirection = _self.toTaskData.useDirection;
      var items = _self.toTaskData.toTasks;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        var value = useDirection ? item.directionId : item.id;
        if (value === event.detail.value) {
          // item.checked = true;
          _self.$set(item, "checked", true);
        } else {
          // item.checked = false;
          _self.$set(item, "checked", false);
        }
      }
    },
    // 选择下一环节、流向
    onToTaskOk: function () {
      var _self = this;
      var useDirection = _self.toTaskData.useDirection;
      var items = _self.toTaskData.toTasks;
      var checkedValues = [];
      each(items, function (item) {
        if (item.checked === true) {
          if (useDirection) {
            checkedValues.push(item.directionId);
          } else {
            checkedValues.push(item.id);
          }
        }
      });
      if (checkedValues.length === 0) {
        uni.showToast({ title: _self.toTaskData.title });
        return;
      }

      var fromTaskId = _self.toTaskData.fromTaskId;
      var callbackOptions = _self.toTaskData.options;
      var workFlow = callbackOptions.workFlow;
      var callback = callbackOptions.callback;
      var callbackContext = callbackOptions.callbackContext;
      var workData = workFlow.getWorkData();
      var toTaskId = checkedValues.join(";");
      if (useDirection) {
        workFlow.setTempData("toDirectionId", toTaskId);
        var toDirectionIds = workData.toDirectionIds || {};
        toDirectionIds[fromTaskId] = toTaskId;
        workFlow.setTempData("toDirectionIds", toDirectionIds);
      } else {
        workFlow.setTempData("toTaskId", toTaskId);
        var toTaskIds = workData.toTaskIds || {};
        toTaskIds[fromTaskId] = toTaskId;
        workFlow.setTempData("toTaskIds", toTaskIds);
      }

      _self.$refs.toTaskPopup.close();
      // 重新触发回调事件
      if (isFunction(callback)) {
        callback.call(callbackContext);
      }
    },
    // 选择退回环节
    rollbackTaskNotFound: function (data) {
      this.rollbackTaskData = data;
      this.$refs.rollbackTaskPopup.open("bottom");
    },
    onRollbackTaskRadioChange: function (event) {
      var _self = this;
      var items = _self.rollbackTaskData.toTasks;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        var value = item.id;
        if (value === event.detail.value) {
          // item.checked = true;
          _self.$set(item, "checked", true);
        } else {
          // item.checked = false;
          _self.$set(item, "checked", false);
        }
      }
    },
    // 选择退回环节
    onRollbackTaskOk: function () {
      var _self = this;
      var items = _self.rollbackTaskData.toTasks;
      var checkedItems = [];
      each(items, function (item) {
        if (item.checked === true) {
          checkedItems.push(item);
        }
      });
      if (checkedItems.length !== 1) {
        uni.showToast({ title: "请选择退回环节！" });
        return;
      }

      var rollbackOptions = _self.rollbackTaskData.options;
      var workFlow = rollbackOptions.workFlow;
      var callback = rollbackOptions.callback;
      var callbackContext = rollbackOptions.callbackContext;
      var item = checkedItems[0];
      var rollbackToTaskId = item.id;
      var rollbackToTaskInstUuid = item.taskInstUuid;
      workFlow.setTempData("rollbackToTaskId", rollbackToTaskId);
      workFlow.setTempData("rollbackToTaskInstUuid", rollbackToTaskInstUuid);

      _self.$refs.toTaskPopup.close();
      // 重新触发回调事件
      if (isFunction(callback)) {
        callback.call(callbackContext);
      }
    },
    // 选择特送环节
    gotoTaskNotFound: function (data) {
      this.gotoTaskData = data;
      this.$refs.gotoTaskPopup.open("bottom");
    },
    onGotoTaskRadioChange: function (event) {
      var _self = this;
      var items = _self.gotoTaskData.toTasks;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        var value = item.id;
        if (value === event.detail.value) {
          // item.checked = true;
          _self.$set(item, "checked", true);
        } else {
          // item.checked = false;
          _self.$set(item, "checked", false);
        }
      }
    },
    // 选择特送环节
    onGotoTaskOk: function () {
      var _self = this;
      var items = _self.gotoTaskData.toTasks;
      var checkedItems = [];
      each(items, function (item) {
        if (item.checked === true) {
          checkedItems.push(item);
        }
      });
      if (checkedItems.length !== 1) {
        uni.showToast({ title: "请选择特送环节！" });
        return;
      }

      var fromTaskId = _self.gotoTaskData.fromTaskId;
      var gotoOptions = _self.gotoTaskData.options;
      var workFlow = gotoOptions.workFlow;
      var callback = gotoOptions.callback;
      var callbackContext = gotoOptions.callbackContext;
      var item = checkedItems[0];
      var gotoTaskId = item.id;
      workFlow.setTempData("fromTaskId", fromTaskId);
      workFlow.setTempData("gotoTaskId", gotoTaskId);

      _self.$refs.gotoTaskPopup.close();
      // 重新触发回调事件
      if (isFunction(callback)) {
        callback.call(callbackContext);
      }
    },
    // 选择职位发起流程
    multiJobNotSelected: function (data) {
      this.multiJobData = data;
      this.$refs.multiJobPopup.open("bottom");
    },
    onMultiJobRadioChange: function (event) {
      var _self = this;
      var items = _self.multiJobData.jobs;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        var value = item.id;
        if (value === event.detail.value) {
          // item.checked = true;
          _self.$set(item, "checked", true);
        } else {
          // item.checked = false;
          _self.$set(item, "checked", false);
        }
      }
    },
    // 选择职位发起流程
    onMultiJobOk: function () {
      var _self = this;
      var items = _self.multiJobData.jobs;
      var checkedItems = [];
      each(items, function (item) {
        if (item.checked === true) {
          checkedItems.push(item);
        }
      });
      if (checkedItems.length !== 1) {
        uni.showToast({ title: "请选择职位发起流程！" });
        return;
      }

      var multiJobData = _self.multiJobData.options;
      var workFlow = multiJobData.workFlow;
      var callback = multiJobData.callback;
      var callbackContext = multiJobData.callbackContext;
      var item = checkedItems[0];
      var jobId = item.id;
      workFlow.setTempData("jobSelected", jobId);

      _self.$refs.multiJobPopup.close();
      // 重新触发回调事件
      if (isFunction(callback)) {
        callback.call(callbackContext);
      }
    },
    getSubflowUndertakeSituationSlotName(blockCode) {
      return "block_" + blockCode; // this.subTaskData.undertakeSituationPlaceHolder;
    },
    getSubflowInfoDistributionSlotName(blockCode) {
      return "block_" + blockCode; //this.subTaskData.infoDistributionPlaceHolder;
    },
    getSubflowOperationRecordSlotName(blockCode) {
      return "block_" + blockCode; //this.subTaskData.operationRecordPlaceHolder;
    },
    getBranchTaskUndertakeSituationSlotName(blockCode) {
      return "block_" + blockCode; //this.branchTaskData.undertakeSituationPlaceHolder;
    },
    getBranchTaskInfoDistributionSlotName(blockCode) {
      return "block_" + blockCode; //this.branchTaskData.infoDistributionPlaceHolder;
    },
    getBranchTaskOperationRecordSlotName(blockCode) {
      return "block_" + blockCode; //this.branchTaskData.operationRecordPlaceHolder;
    },
    // 检查流程管理-全局设置
    checkWorkFlowSettting(key) {
      return this.$axios.get(`/api/workflow/setting/getByKey?key=${key}`).then(({ data: result }) => {
        if (result.data && result.data.attrVal) {
          let setting = JSON.parse(result.data.attrVal);
          return setting;
        } else {
          return {};
        }
      });
    },
    // 获取所有流程设置数据
    getSetting() {
      return this.$axios.get(`/api/workflow/setting/list`).then(({ data: result }) => {
        let settings = {};
        result.data.forEach((item) => {
          let attrVal = JSON.parse(item.attrVal);
          attrVal.enabled = item.enabled;
          settings[item.attrKey] = attrVal;
        });
        return settings;
      });
    }, // 获取相同办理人提交结果信息
    getSameUserSubmitInfo(result) {
      const _self = this;
      let data = (result.data && result.data.data) || {};
      let sameUserSubmitType = stringTrim(data.sameUserSubmitType || "0");
      let submitTaskInstUuid = stringTrim(data.sameUserSubmitTaskInstUuid);
      let submitTaskOperationUuid = stringTrim(data.sameUserSubmitTaskOperationUuid);
      if (!(!isEmpty(submitTaskInstUuid) && !isEmpty(submitTaskOperationUuid))) {
        return null;
      }

      var refreshUrl = `?aclRole=TODO&taskInstUuid=${submitTaskInstUuid}&flowInstUuid=${data.flowInstUuid}&sameUserSubmitType=${sameUserSubmitType}&auto_submit=true&sameUserSubmitTaskOperationUuid=${submitTaskOperationUuid}`;

      return {
        sameUserSubmitType,
        refreshUrl: _self.workView.addSystemPrefix(refreshUrl),
        taskInstUuid: submitTaskInstUuid,
        flowInstUuid: data.flowInstUuid,
        sameUserSubmitTaskOperationUuid: submitTaskOperationUuid,
        autoSubmit: true,
      };
    },
    // 自动提交
    autoSubmitIfRequried: function () {
      var _self = this;
      var autoSubmit = _self.workView.getQueryString("auto_submit");
      var sameUserSubmitType = _self.workView.getQueryString("sameUserSubmitType");
      if (autoSubmit === "true" && _self.workView.isAllowSubmit()) {
        // 不自动提交并刷新页面
        if (sameUserSubmitType == "3") {
        } else if (sameUserSubmitType == "1") {
          // 自动提交，且自动继承意见
          _self.$refs.workflowToolbar.submit();
        } else if (sameUserSubmitType == "0") {
          this.$nextTick(() => {
            // 自动提交，让办理人确认是否继承上一环节意见
            var workData = _self.workView.getWorkData();
            var data = _self.workView.getOpinionEditor().getOpinion();
            let message = '<div class="auto-title">自动使用上一环节意见进行提交</div>';
            if (data.text) {
              message += '<div class="auto-opinion-text">' + data.text + "</div>";
            }
            _self.autoSubmitData = {
              title: "即将连续提交[" + workData.taskName + "]",
              message: message,
            };
            _self.autoSubmitVisible = true;
          });
        }
      }
    },
    onAutoSubmitOk: function (event) {
      this.submit();
      this.autoSubmitVisible = false;
    },
    onAutoSubmitCancel: function () {
      const _this = this;
      _this.autoSubmitVisible = false;
      _this.options.optUuid = this.options.sameUserSubmitTaskOperationUuid;
      delete _this.options.auto_submit;
      delete _this.options.sameUserSubmitTaskOperationUuid;
      let opinionEditor = _this.workView.getOpinionEditor();
      opinionEditor &&
        opinionEditor.reset((data) => {
          _this.workView.opinionToWorkData(data);
          _this.signOptionOptions.opinionText = data.text;
          _this.signOptionOptions.opinionLabel = data.label;
          _this.signOptionOptions.opinionValue = data.value;
          _this.signOptionOptions.opinionFiles = data.files;
        });
    },
    // 获取退回到自己的信息
    getRollbackToSelfInfo(result) {
      var _self = this;
      var data = (result.data && result.data.data) || {};
      var toTaskInstUuid = Object.keys(data.taskTodoUsers)[0];
      var currUserId = this._$USER.userId;
      var taskTodoUsers = data.taskTodoUsers[toTaskInstUuid];
      var isSameUser = false;
      for (var i in taskTodoUsers) {
        if (i == currUserId) {
          isSameUser = true;
          break;
        }
      }
      return { rollbackToSelf: isSameUser, taskInstUuid: toTaskInstUuid, flowInstUuid: data.flowInstUuid };
    },
    setErrorOrgSelectOptions(options) {
      this.errorOrgSelectOptions = options;
    },
    clearErrorOrgSelectOptions() {
      this.errorOrgSelectOptions = undefined;
    },
    // 提交事件
    submit() {
      const _this = this;
      let actionCode = _this.workView.getSubmitActionCode();
      let submitAction = _this.workView.getActions().find((action) => action.code == actionCode);
      if (submitAction) {
        _this.workView.currentEvent = null;
        _this.workView.currentAction = null;
        _this.workView.submit();
      }
    },
    readLogPopupChange(e) {
      this.readLogVisible = e.show;
    },
  },
  watch: {
    autoSubmitVisible(v) {
      if (this.$refs.lxqpConfirmPopup) {
        if (v) {
          this.$refs.lxqpConfirmPopup.open();
        } else {
          this.$refs.lxqpConfirmPopup.close();
        }
      }
    },
    readLogVisible(v) {
      if (v) {
        this.$refs.readLogPopup.open("bottom");
      } else {
        this.$refs.readLogPopup.close();
      }
    },
  },
};
</script>

<style lang="scss" scoped>
@import url(./css/index.scss);
.work-view {
  width: 100%;
  height: 100%;
  padding: 0;
  background: $uni-bg-color;
  overflow: auto;

  .work-view-content {
  }

  .work-view-dyform {
    padding-bottom: 50px;
  }

  .work-view-sign-opinion {
    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    justify-content: center;
    align-items: center;

    position: fixed;
    left: 0;
    right: 0;
    /* #ifdef H5 */
    left: var(--window-left);
    right: var(--window-right);
    /* #endif */
    bottom: var(--window-bottom, 0);
    z-index: 10;

    background: $uni-bg-color;
    box-shadow: $uni-shadow-base;
    border-top: 3px solid $uni-border-3;
    height: 50px;
    padding: 0 5px;

    .opinion-input {
      margin-right: 6px;
      /* #ifdef APP-PLUS */
      margin-bottom: 3px;
      /* #endif */

      .uni-input {
        width: 120px;
        border: 1px solid #c7c7c7;
        border-radius: 20px;
        padding: 0px 15px;
        height: 35px;
        line-height: 35px;
        background-color: $uni-bg-secondary-color;
        color: $uni-text-color;

        ::v-deep .input-placeholder {
          color: $uni-text-color-placeholder;
        }
      }
    }

    .btn-group {
      -webkit-flex: 1;
      flex: 1;
      display: flex;
      flex-direction: row;
      justify-content: center;
      align-items: center;

      .btn {
        -webkit-flex: 1;
        flex: 1;
        margin-right: 10rpx;
      }
      // .btn-more {
      //   -webkit-flex: 0.5;
      //   flex: 0.5;
      // }

      .uniui-iconfont::before {
        margin-right: 2px;
      }
    }
    .w-btn-inverse {
      background-color: $uni-btn-color-inverse; // #000000; // 黑色
    }
    .w-btn-default {
      background-color: $uni-btn-color-default; // #d4d4d4; // 灰色
    }
    .w-btn-primary {
      background-color: $uni-btn-color-primary; // #007aff; // 蓝色
    }
    .w-btn-success {
      background-color: $uni-btn-color-success; // #3aa322; // 绿色
    }
    .w-btn-info {
      background-color: $uni-btn-color-info; // #2aaedd; // 浅蓝
    }
    .w-btn-warning {
      background-color: $uni-btn-color-warning; // #e99f00; // 橙色
    }
    .w-btn-danger {
      background-color: $uni-btn-color-danger; // #e33033; // 红色
    }
  }

  .uni-list-cell {
    justify-content: flex-start;
  }

  .button-sp-area {
    margin: 0 auto;
    width: 60%;
  }

  .popup-work-view-dialog {
    color: $uni-text-color;
    background-color: $uni-bg-secondary-color;
    height: 300px;

    .uni-list {
      background-color: $uni-bg-secondary-color;
    }

    .popup-title {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */
      flex-direction: row;
      align-items: center;
      justify-content: center;
      height: 40px;
      border-bottom: 1px solid $uni-border-3;
    }

    .popup-title-text {
      font-size: 16px;
      color: $uni-text-color;
      font-weight: bold;
    }

    .popup-content {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */
      flex-direction: row;
      justify-content: center;
      // padding-top: 10px;

      .popup-content-scroll-view {
        height: 200px;
      }

      .hide-confirm-btn {
        height: 250px;
      }

      .popup-check-item {
        display: flex;
        flex-direction: row;
        width: 100%;
        height: 26px;
        justify-content: center;
        align-items: center;

        .popup-check-icon {
          width: 20px;
          padding-right: 6px;
        }
      }
    }

    .popup-button-box {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */
      flex-direction: row;
      padding: 10px 15px;

      .popup-button {
        flex: 1;
        border-radius: 50px;
        background-color: $uni-primary;
        color: #fff;
        font-size: 16px;
      }

      .popup-button::after {
        border-radius: 50px;
      }
    }
  }
}
</style>
