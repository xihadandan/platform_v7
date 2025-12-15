<template>
  <a-layout class="work-view-container" theme="light">
    <a-layout-header class="work-view-header">
      <a-row>
        <a-col span="18" class="title">
          {{ title }}
        </a-col>
        <a-col span="6">
          <WorkflowToolbar
            ref="workflowToolbar"
            class="work-view-toolbar"
            v-if="workView"
            :workView="workView"
            @created="onToolbarCreated"
          ></WorkflowToolbar>
        </a-col>
      </a-row>
    </a-layout-header>
    <a-layout-content class="work-view-content">
      <WorkflowSidebarLayout v-if="workView" :workView="workView">
        <template slot="main-content">
          <slot name="extra-content" />
          <!-- workData.dataUuid为空时表示新增表单 -->
          <Scroll
            class="widget-dyfrom-out-ps"
            :style="{
              height: `calc(${scrollerHeight} - var(--w-padding-xs))`
            }"
          >
            <WidgetDyform
              v-if="workData.dyFormData && definitionVjson"
              ref="dyform"
              :isNewFormData="isNewFormData"
              :displayState="displayState"
              :formUuid="workData.dyFormData.formUuid"
              :definitionVjson="definitionVjson"
              :dataUuid="workData.dyFormData.dataUuid || workData.dataUuid"
              :formDatas="workData.dyFormData.formDatas"
              :formElementRules="formElementRules"
              @mounted="onDyformMounted"
              @formDataChanged="onDyformDataChanged"
              :dyformStyle="{ padding: 'var(--w-padding-md)' }"
            />
            <WorkflowSubflowViewer
              v-if="workData.dyFormData && definitionVjson"
              ref="subflowViewer"
              :workView="workView"
            ></WorkflowSubflowViewer>
          </Scroll>
        </template>
      </WorkflowSidebarLayout>
    </a-layout-content>

    <!-- 遮罩 -->
    <a-spin class="work-view-spin" v-show="loading.visible" :tip="loading.tip"></a-spin>
    <div v-if="loading.visible" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 10"></div>

    <!-- 自动提交 -->
    <a-modal
      dialogClass="pt-modal wf-auto-submit-modal"
      :title="autoSubmitData.title"
      :visible="autoSubmitVisible"
      :maskClosable="false"
      @ok="onAutoSubmitOk"
      @cancel="onAutoSubmitCancel"
    >
      <span v-html="autoSubmitData.message"></span>
    </a-modal>

    <!-- 阅读记录 -->
    <a-modal
      width="800px"
      dialogClass="pt-modal wf-read-records-modal"
      :title="$t('WorkflowWork.workView.readRecord', '阅读记录')"
      :visible="readLogVisible"
      :maskClosable="false"
      :footer="null"
      @cancel="readLogVisible = false"
    >
      <a-row>
        <a-col span="12">
          <div class="col-title">
            {{
              $t(
                'WorkflowWork.workView.readUserCount',
                { count: readLogData.readUser.length },
                '已阅人员 (' + readLogData.readUser.length + '人)'
              )
            }}
          </div>
          <a-list :data-source="readLogData.readUser" style="height: 330px; overflow: auto" class="pt-empty">
            <a-list-item slot="renderItem" slot-scope="item">
              <a-row style="width: 100%">
                <a-col span="12">
                  <span class="user-name">{{ item.userName }}</span>
                </a-col>
                <a-col span="12">
                  <div class="read-time">{{ item.readTimeString }}</div>
                </a-col>
              </a-row>
            </a-list-item>
          </a-list>
        </a-col>
        <a-col span="12">
          <div class="col-title">
            {{
              $t(
                'WorkflowWork.workView.unreadUserCount',
                { count: readLogData.unReadUser.length },
                '未阅人员 (' + readLogData.unReadUser.length + '人)'
              )
            }}
          </div>
          <a-list :data-source="readLogData.unReadUser" style="height: 330px; overflow: auto" class="pt-empty">
            <a-list-item slot="renderItem" slot-scope="item">
              <span class="user-name">{{ item }}</span>
            </a-list-item>
          </a-list>
        </a-col>
      </a-row>
    </a-modal>

    <!-- 选择套打模板 -->
    <a-modal
      :title="$t('WorkflowWork.workView.choosePrintTemplateModalTitle', '选择套打模板')"
      :visible="printTemplateVisible"
      :maskClosable="false"
      @ok="onPrintTemplateOk"
      @cancel="printTemplateVisible = false"
      dialogClass="pt-modal wf-print-template-modal"
    >
      <PerfectScrollbar style="height: 350px">
        <a-radio-group v-model="printTemplateData.templateId">
          <a-radio v-for="(item, index) in printTemplateData.templates" :key="index" :value="item.id">
            {{ item.name }}
          </a-radio>
        </a-radio-group>
      </PerfectScrollbar>
    </a-modal>
  </a-layout>
</template>
<script type="text/babel">
import { addWindowResizeHandler, download } from '@framework/vue/utils/util';
import {
  isEmpty,
  isFunction,
  isArray,
  each as forEach,
  trim as stringTrim,
  findIndex,
  concat,
  assign,
  set,
  merge,
  camelCase
} from 'lodash';
import WorkView from './WorkView.js';
import '../css/index.less';
export default {
  name: 'WorkView',
  props: {
    workData: Object,
    settings: Object
  },
  inject: ['locale', 'continuousMode'],
  provide() {
    return {
      $pageJsInstance: undefined,
      vPageState: {},
      $workView: this
    };
  },
  data() {
    let isNewFormData = isEmpty(this.workData && this.workData.dataUuid);
    // // 存在表单数据被删除的情况
    // if (!isNewFormData && this.workData.dyFormData && !this.workData.dyFormData.dataUuid) {
    //   let mainFormDatas = this.workData.dyFormData.formDatas[this.workData.dyFormData.formUuid];
    //   if (mainFormDatas && mainFormDatas.length == 1 && (!mainFormDatas[0].uuid || mainFormDatas[0].uuid == this.workData.dataUuid)) {
    //     mainFormDatas[0].uuid = this.workData.dataUuid;
    //     isNewFormData = true;
    //   }
    // }
    return {
      title: '',
      version: this.workData.version || '1.0',
      definitionVjson: null,
      displayState: this.workData && this.workData.canEditForm == false ? 'label' : 'edit', // 表单默认状态可编辑
      isNewFormData,
      scrollerHeight: '900px',
      workView: null,
      loading: {
        visible: false,
        tip: ''
      },
      autoSubmitVisible: false,
      autoSubmitData: {
        title: '',
        message: '' // 自动提交
      },
      readLogVisible: false,
      readLogData: {
        readUser: [], // 阅读记录
        unReadUser: []
      },
      printTemplateVisible: false,
      printTemplateData: {
        templates: [], // 选择套打模板
        templateId: '', // 选择的套打模板ID
        callback: null // 回调函数
      },
      loadingTempSubforms: {}
    };
  },

  beforeCreate() {
    const _self = this;
    if (EASY_ENV_IS_NODE || !_self.__developScript) {
      return;
    }
    // 初始化二开模块
    // beforeCreate 数据未初始化完，无法访问倒属性、方法等实例对象
    let workData = _self.$options.propsData.workData;
    // 外部传入的二开片段模块
    let epWorkViewFragment = workData.extraParams && workData.extraParams['ep_workViewFragment'];
    // 动态的二开片段模块
    let customRtWorkViewFragment = workData.extraParams && workData.extraParams['custom_rt_workViewFragment'];
    // 环节二开
    let customJsFragmentModule = workData.customJsFragmentModule;
    // 流程二开
    let customJsModule = workData.customJsModule;

    // 浏览器环境: 加载二开脚本
    _self.$developJsInstance = [];
    let developScript = _self.__developScript;
    try {
      // 外部传入的二开片段模块
      if (!isEmpty(epWorkViewFragment) && developScript[epWorkViewFragment]) {
        _self.$developJsInstance.push(new developScript[epWorkViewFragment].default(_self));
      }
      // 动态的二开片段模块
      if (!isEmpty(customRtWorkViewFragment) && developScript[customRtWorkViewFragment]) {
        _self.$developJsInstance.push(new developScript[customRtWorkViewFragment].default(_self));
      }
      // 环节二开
      if (!isEmpty(customJsFragmentModule) && developScript[customJsFragmentModule]) {
        _self.$developJsInstance.push(new developScript[customJsFragmentModule].default(_self));
      }
      // 流程二开
      if (!isEmpty(customJsModule) && developScript[customJsModule] && customJsModule != 'WorkView') {
        _self.$developJsInstance.push(new developScript[customJsModule].default(_self));
      }
    } catch (e) {
      console.error(e);
    }
  },
  computed: {
    formElementRules() {
      let taskForm = this.workData.taskForm;
      console.log('taskForm', taskForm);
      let formElementRules = {};
      let allFormFieldWidgetIds = taskForm && taskForm.allFormFieldWidgetIds;
      let formBtnRightSettings = taskForm && taskForm.formBtnRightSettings;
      if (allFormFieldWidgetIds) {
        let formBtnRightSettingJson = {};
        // 按钮设置内容
        forEach(formBtnRightSettings, item => {
          let param = item.split('=');
          formBtnRightSettingJson[param[0]] = JSON.parse(param[1]);
        });
        forEach(allFormFieldWidgetIds, (item, index) => {
          let itemArr = item.split('=');
          if (itemArr.length == 2) {
            let field = itemArr[0];
            let data = itemArr[1] && JSON.parse(itemArr[1]);
            if (field.split(':').length == 2) {
              // 从表字段
              let fieldName = field.split(':')[1];
              let subFormUuid = field.split(':')[0]; //从表表单id
              let subWidgetId = data.subWidgetId; //从表组件id
              let editFieldMap = taskForm.editFieldMap[subFormUuid] || []; // 编辑
              let notNullFieldMap = taskForm.notNullFieldMap[subFormUuid] || []; // 必填
              let hideFieldMap = taskForm.hideFieldMap[subFormUuid] || []; // 隐藏
              if (!formElementRules.hasOwnProperty(subWidgetId)) {
                formElementRules[subWidgetId] = { children: [] };
              }
              formElementRules[subWidgetId].children[data.id] = {
                // displayAsLabel: this.displayState == 'label',
                editable: this.displayState == 'edit' && editFieldMap.indexOf(fieldName) > -1,
                hidden: hideFieldMap.indexOf(fieldName) > -1,
                required: notNullFieldMap.indexOf(fieldName) > -1
              };
              let btnFieldName = subFormUuid + '_' + fieldName;
              if (formBtnRightSettingJson[btnFieldName]) {
                formElementRules[subWidgetId].children[data.id].editable = this.displayState == 'edit';
                let headerButton = concat(
                  formBtnRightSettingJson[btnFieldName].show.headerButton,
                  formBtnRightSettingJson[btnFieldName].edit.headerButton
                );
                let rowButton = concat(
                  formBtnRightSettingJson[btnFieldName].show.rowButton,
                  formBtnRightSettingJson[btnFieldName].edit.rowButton
                );
                // 文本情况下，不加入编辑类
                if (this.displayState == 'label') {
                  headerButton = [formBtnRightSettingJson[btnFieldName].show.headerButton];
                  rowButton = [formBtnRightSettingJson[btnFieldName].show.rowButton];
                }

                formElementRules[subWidgetId].children[data.id].buttons = {
                  headerButton: headerButton.join(';'),
                  rowButton: rowButton.join(';')
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
                  editable: this.displayState == 'edit',
                  hidden: hideFieldMap.indexOf(field) > -1
                });
                if (formBtnRightSettingJson[data.formUuid]) {
                  formElementRules[data.id].buttons =
                    this.displayState == 'edit' ? formBtnRightSettingJson[data.formUuid].edit : formBtnRightSettingJson[data.formUuid].show;
                }
              } else {
                formElementRules[data.id] = {
                  // displayAsLabel: this.displayState == 'label',
                  editable: this.displayState == 'edit' && editFieldMap.indexOf(field) > -1,
                  hidden: hideFieldMap.indexOf(field) > -1,
                  required: notNullFieldMap.indexOf(field) > -1
                };
                if (formBtnRightSettingJson[field]) {
                  formElementRules[data.id].editable = this.displayState == 'edit';
                  let headerButton = concat(
                    formBtnRightSettingJson[field].show.headerButton,
                    formBtnRightSettingJson[field].edit.headerButton
                  );
                  let rowButton = concat(formBtnRightSettingJson[field].show.rowButton, formBtnRightSettingJson[field].edit.rowButton);
                  //文本情况下，不加入编辑类
                  if (this.displayState == 'label') {
                    headerButton = [formBtnRightSettingJson[field].show.headerButton];
                    rowButton = [formBtnRightSettingJson[field].show.rowButton];
                  }
                  formElementRules[data.id].buttons = {
                    headerButton: headerButton.join(';'),
                    rowButton: rowButton.join(';')
                  };
                }
              }
            }
          }
        });
        let hideBlocks = taskForm.hideBlocks;
        let hideTabs = taskForm.hideTabs;
        forEach(hideBlocks, item => {
          formElementRules[item] = {
            hidden: true
          };
        });
        if (this.workData.dyFormData) {
          forEach(this.workData.dyFormData.blocks, item => {
            // 当前块不在规则，代表不隐藏，而组件本身是隐藏的，就要设置不隐藏
            if (item.id && !formElementRules[item.id] && item.hide) {
              formElementRules[item.id] = {
                hidden: false
              };
            }
          });
        }
        if (this.tabs) {
          // 流程规则仅对隐藏的tab页签进行配置，因此默认情况下页签都是显示的
          for (let tKey in this.tabs) {
            if (this.tabs[tKey].parentId) {
              formElementRules[tKey] = {
                hidden: false
              };
            }
          }
        }

        forEach(hideTabs, item => {
          formElementRules[item] = {
            hidden: true
          };
        });
      }

      console.log('taskForm formElementRules', formElementRules);
      return Object.keys(formElementRules).length === 0 ? null : formElementRules;
    }
  },
  created() {
    const _self = this;
    this.setI18nMessage();
    if (this.$i18n.locale !== 'zh_CN' && /[\u4e00-\u9fa5]/.test(this.workData.title)) {
      this.$translate(this.workData.title, 'zh', this.$i18n.locale.split('_')[0])
        .then(t => {
          this.workData.titleI18n = t;
          this.title = t;
          this.$emit('setTitle', this.title);
        })
        .catch(() => {
          this.title = this.workData.title;
          this.$emit('setTitle', this.title);
        });
    } else {
      this.title = this.workData.title;
      this.$emit('setTitle', this.title);
    }

    _self.workView = new WorkView({
      workData: _self.workData,
      settings: _self.settings,
      loading: _self.loading,
      $widget: EASY_ENV_IS_NODE ? null : _self,
      onLoad() {
        let definitionVjson = JSON.parse(_self.workData.dyFormData.definitionVjson);
        if (_self.workData.dyFormData.formDefinition) {
          let formDefinition = JSON.parse(_self.workData.dyFormData.formDefinition);
          _self.tabs = formDefinition.tabs;
        }

        let widgetI18ns = undefined;
        if (_self.workData.dyFormData.i18ns.length) {
          widgetI18ns = { [_self.$i18n.locale]: { Widget: {} } };
          for (let item of _self.workData.dyFormData.i18ns) {
            set(widgetI18ns[_self.$i18n.locale].Widget, item.code, item.content);
          }

          for (let l in widgetI18ns) {
            _self.$i18n.mergeLocaleMessage(l, widgetI18ns[l]);
          }
        }

        // console.log('formDefinition', formDefinition);
        // console.log('definitionVjson', definitionVjson);
        // 前端设置环节表单控制信息
        // if (_self.workData.canEditForm != null) {
        //   _self.setTaskDyformDataControlInfo(definitionVjson, formDefinition);
        // }
        // 草稿或待办才可编辑
        if ((this.isDraft() || this.isTodo()) && _self.workData.canEditForm) {
          _self.displayState = 'edit';
        } else {
          _self.displayState = 'label';
        }

        _self.definitionVjson = definitionVjson;
        // _self.setWidgetI18ns(definitionVjson.widgets);
      }
    });

    // 二开回调方法
    _self.invokeDevelopmentMethod('created', _self.workView);
  },
  beforeMount() {
    // 二开回调方法
    this.invokeDevelopmentMethod('beforeMount', this.workView);
  },
  mounted() {
    const _self = this;
    _self.scrollerHeight = window.innerHeight - 65 + 'px';
    addWindowResizeHandler(() => {
      _self.$nextTick(() => {
        _self.scrollerHeight = window.innerHeight - 65 + 'px';
      });
    });
    _self.workView.registerWindowCloseEventListerner();
    // 数据加锁
    _self.workView.lockWorkIfRequired();

    _self.$emit('workViewMounted', this.workView);

    // 二开回调方法
    _self.invokeDevelopmentMethod('mounted', _self.workView);
  },
  beforeUpdate() {
    // 二开回调方法
    this.invokeDevelopmentMethod('beforeUpdate', this.workView);
  },
  updated() {
    // 二开回调方法
    this.invokeDevelopmentMethod('updated', this.workView);
  },
  beforeDestroy() {
    this.workView.unlockWorkIfRequired();

    // 二开回调方法
    this.invokeDevelopmentMethod('beforeDestroy', this.workView);
  },
  destroyed() {
    // 二开回调方法
    this.invokeDevelopmentMethod('destroyed', this.workView);
  },
  methods: {
    setWidgetI18ns(widgets) {
      let i18ns = {};
      function findI18nObjects(json, widget) {
        function traverse(obj, belongToWidget) {
          if (typeof obj !== 'object' || obj === null) return;
          if (obj.id && obj.wtype && obj.configuration) {
            belongToWidget = obj;
          }
          if (Array.isArray(obj)) {
            for (let item of obj) {
              traverse(item, belongToWidget);
            }
          } else {
            for (let key in obj) {
              if (key === 'i18n' && obj[key] != undefined) {
                let i18n = obj[key];
                for (let langCode in i18n) {
                  if (i18ns[langCode] == undefined) {
                    i18ns[langCode] = { Widget: {} };
                  }
                  for (let c in i18n[langCode]) {
                    set(i18ns[langCode].Widget, c, i18n[langCode][c]);
                  }
                }
              } else {
                traverse(obj[key], belongToWidget);
              }
            }
          }
        }
        traverse(json, widget);
      }
      for (let wgt of widgets) {
        findI18nObjects(wgt, wgt);
      }
      console.log('查询 i18ns ', i18ns);
      for (let l in i18ns) {
        this.$i18n.mergeLocaleMessage(l, i18ns[l]);
      }
    },
    setI18nMessage() {
      let message = { global: {} };
      if (this.workData.i18n) {
        for (let key in this.workData.i18n) {
          set(message, key, this.workData.i18n[key]);
        }
      }

      if (this.settings && this.settings.ACTION) {
        if (this.settings.ACTION.buttons) {
          for (let b of this.settings.ACTION.buttons) {
            if (b.i18n && b.i18n[this.$i18n.locale]) {
              merge(message.global, b.i18n[this.$i18n.locale]);
            }
            if (b.confirmConfig && b.confirmConfig.enable && b.confirmConfig.i18n && b.confirmConfig.i18n[this.$i18n.locale]) {
              // 二次确认按钮
              merge(message.global, b.confirmConfig.i18n[this.$i18n.locale]);
            }
            if (b.multistate && b.states) {
              // 多状态按钮
              for (const state of b.states) {
                if (state.i18n) {
                  merge(message.global, state.i18n[this.$i18n.locale]);
                }
              }
            }
          }
        }

        if (this.settings.group && this.settings.group.i18n) {
          merge(message.global, this.settings.group.i18n[this.$i18n.locale]);
        }
        if (this.settings.ACTION.group && this.settings.ACTION.group.groups) {
          for (let b of this.settings.ACTION.group.groups) {
            if (b.i18n && b.i18n[this.$i18n.locale]) {
              merge(message.global, b.i18n[this.$i18n.locale]);
            }
          }
        }
      }

      this.$i18n.mergeLocaleMessage(this.$i18n.locale, { WorkflowView: message });
    },
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
    onDyformMounted() {
      let workData = this.workView.getWorkData();
      if (workData.extraParams) {
        for (let key in workData.extraParams) {
          if (key.length > 8 && key.substring(0, 8) == 'ep_dyfs_') {
            let fieldName = key.substring(8);
            let fieldVal = workData.extraParams[key];
            this.$refs.dyform.dyform.setFieldValue(fieldName, fieldVal);
          }
        }
      }
      if (workData.dyFormData.formDatas && workData.tempFormDatas) {
        this.setTempFormData(workData.dyFormData.formDatas, workData.tempFormDatas);
        if (
          workData.taskInstRecVer != null &&
          workData.tempTaskInstRecVer != null &&
          workData.taskInstRecVer != workData.tempTaskInstRecVer
        ) {
          this.workView.getWorkProcess().then(({ workProcesses = [] }) => {
            let action = this.$t('WorkflowWork.mergeData.update', '更新');
            let actionType = 'Update';
            for (let index = workProcesses.length - 1; index >= 0; index--) {
              if (
                workProcesses[index].actionName &&
                workProcesses[index].actionType &&
                workData.taskInstUuid == workProcesses[index].taskInstUuid &&
                [1, 2, 34, 3, 27, 4, 5, 7, 8, 33].includes(workProcesses[index].actionCode)
              ) {
                action = workProcesses[index].actionName;
                actionType = workProcesses[index].actionType;
                break;
              }
            }
            this.showMergeChangedFormDataModal({
              taskInstRecVer: workData.taskInstRecVer,
              action,
              actionType,
              watchValue: true
            });
          });
        }
      }
      this.invokeDevelopmentMethod('dyformMounted', this.$refs.dyform);

      // 办理意见附件
      let $opinionFiles = this.$refs.dyform.$el.querySelectorAll('.opinion-file');
      forEach($opinionFiles, $opinionFile => {
        let fileId = $opinionFile.getAttribute('fileId');
        if (fileId) {
          $opinionFile.addEventListener('click', e => {
            let url = `/proxy-repository/repository/file/mongo/download?fileID=${fileId}`;
            download({ url });
            e.stopPropagation();
          });
        }
      });
    },
    showMergeChangedFormDataModal(options) {
      if (isEmpty(this.loadingTempSubforms)) {
        this.workView.showMergeChangedFormDataModal(options);
      } else {
        setTimeout(() => {
          this.showMergeChangedFormDataModal(options);
        }, 500);
      }
    },
    setTempFormData(formDatas, tempFormDatas) {
      if (isEmpty(formDatas) || isEmpty(tempFormDatas)) {
        return;
      }
      for (let key in tempFormDatas) {
        if (this.$refs.dyform.dyform.formUuid == key) {
          let formData = formDatas[key][0] || {};
          let tempFormData = tempFormDatas[key][0] || {};
          for (let fieldName in tempFormData) {
            if (formData[fieldName] != tempFormData[fieldName] && !(formData[fieldName] == null && tempFormData[fieldName] == '')) {
              if (isArray(formData[fieldName]) || isArray(tempFormData[fieldName])) {
                if (this.isTempFormFieldChangedOfFile(formData[fieldName], tempFormData[fieldName])) {
                  this.$refs.dyform.dyform.setFieldValue(fieldName, tempFormData[fieldName]);
                }
              } else if (!this.workView || !this.workView.isDyformSystemField(fieldName)) {
                this.$refs.dyform.dyform.setFieldValue(fieldName, tempFormData[fieldName]);
              }
            }
          }
        } else {
          this.setTempFormDataOfSubform(this.$refs.dyform, key, formDatas[key], tempFormDatas[key]);
        }
      }
      setTimeout(() => {
        this.collectTempFormData(tempFormDatas);
      }, 1000);
    },
    collectTempFormData(tempFormDatas) {
      if (isEmpty(this.loadingTempSubforms)) {
        this.tempFormData = this.$refs.dyform.collectFormData();
        // 合并未加载的显示值字段
        let formDatas = this.tempFormData.dyFormData.formDatas;
        for (let key in formDatas) {
          let datas = formDatas[key] || [];
          let tempDatas = tempFormDatas[key] || [];
          datas.forEach(data => {
            let tempData = tempDatas.find(tempData => tempData.uuid == data.uuid || tempData.__uuid__ == data.uuid);
            if (tempData) {
              for (let key in data) {
                if (!data[key] && tempData[key]) {
                  data[key] = tempData[key];
                }
              }
            }
          });
        }
      } else {
        setTimeout(() => {
          this.collectTempFormData(tempFormDatas);
        }, 500);
      }
    },
    isTempFormFieldChangedOfFile(oldFiles = [], newFiles = []) {
      let oldFileIds = (oldFiles && oldFiles.map(file => file.fileID)) || [];
      let newFileIds = (newFiles && newFiles.map(file => file.fileID)) || [];
      if (oldFileIds.length != newFileIds.length) {
        return true;
      }
      oldFileIds.forEach(oldFileId => {
        let index = newFileIds.indexOf(oldFileId);
        if (index != -1) {
          let oldFile = oldFiles.find(file => file.fileID == oldFileId);
          let newFile = newFiles.find(file => file.fileID == oldFileId);
          if (oldFile.fileName == newFile.fileName) {
            newFileIds.splice(index, 1);
          }
        }
      });
      return newFileIds.length != 0;
    },
    setTempFormDataOfSubform($dyform, subformUuid, rows = [], tempRows = []) {
      let subformRecords = $dyform.dyform.subform[subformUuid] || [];
      let $subform = $dyform.dyform.$subform[subformUuid];
      // 从表未初始化
      if ((subformRecords.length == 0 && rows.length != 0) || !$subform || !$subform.formDefinitionVjson) {
        this.loadingTempSubforms[subformUuid] = true;
        setTimeout(() => {
          this.setTempFormDataOfSubform($dyform, subformUuid, rows, tempRows);
        }, 500);
        return;
      }
      delete this.loadingTempSubforms[subformUuid];

      subformRecords.forEach((record, index) => {
        let tempRow = tempRows.find(tempRow => tempRow.uuid == record.dataUuid);
        if (tempRow) {
          for (let fieldName in tempRow) {
            let formData = record.formData || {};
            if (formData[fieldName] != tempRow[fieldName] && !(formData[fieldName] == null && tempRow[fieldName] == '')) {
              if (isArray(formData[fieldName]) || isArray(tempRow[fieldName])) {
                if (this.isTempFormFieldChangedOfFile(formData[fieldName], tempRow[fieldName])) {
                  record.setFieldValue(fieldName, tempRow[fieldName]);
                }
              } else if (!this.workView || !this.workView.isDyformSystemField(fieldName)) {
                record.setFieldValue(fieldName, tempRow[fieldName]);
              }
            }
          }
        }
      });

      let addedRows = tempRows.filter(tempRow => rows.findIndex(row => row.uuid == tempRow.uuid) == -1);
      addedRows.forEach(row => {
        row.__uuid__ = row.uuid;
        delete row.uuid;
        $dyform.$emit(`WidgetSubform:${subformUuid}:addRow`, row);
      });
      let deleteRows = rows.filter(row => tempRows.findIndex(tempRow => tempRow.uuid == row.uuid) == -1);
      deleteRows.forEach(row => {
        let subformRecord = subformRecords.find(record => (record.row && record.row.uuid) == row.uuid);
        let $subform = $dyform.dyform.$subform[subformUuid];
        if ($subform) {
          $subform.delRowById((subformRecord.row && subformRecord.row[$subform.rowKey]) || row.uuid);
        }
      });
    },
    onDyformDataChanged() {
      this.invokeDevelopmentMethod('dyformDataChanged', this.$refs.dyform);
    },
    // 前端设置环节表单控制信息
    setTaskDyformDataControlInfo: function (definitionVjson, formDefinition) {
      // 前端设置环节表单区块控制信息
      this.setTaskDyformBlockControlInfo(definitionVjson, formDefinition);
      // 前端设置环节表单字段控制信息
      this.setTaskDyformFieldControlInfo(definitionVjson, formDefinition);
      // 前端设置环节表单从表控制信息
      this.setTaskDyformSubformControlInfo(definitionVjson, formDefinition);
    },
    // 前端设置环节表单区块控制信息
    setTaskDyformBlockControlInfo: function (definitionVjson, formDefinition) {
      let blocks = formDefinition.blocks || {};

      // v表单字段
      let vBlockConfigMap = {};
      let getVBlock = (widget, configuration) => {
        if (widget && widget.wtype == 'WidgetFormLayout' && configuration.code) {
          vBlockConfigMap[configuration.code] = configuration;
        }

        let items = this.getConfigurationItems(configuration);
        items.forEach(item => {
          getVBlock(item, item.configuration || {});
        });
      };
      getVBlock(null, definitionVjson);

      for (let blockCode in blocks) {
        let block = blocks[blockCode];
        let vBlockConfiguration = vBlockConfigMap[blockCode];
        if (vBlockConfiguration) {
          vBlockConfiguration.hidden = block.hide;
        }
      }
    },
    getConfigurationItems(configuration) {
      return configuration.items || configuration.widgets || configuration.tabs || configuration.cols || [];
    },
    // 前端设置环节表单字段控制信息
    setTaskDyformFieldControlInfo: function (definitionVjson, formDefinition) {
      const _self = this;
      // 主表字段
      let fields = formDefinition.fields;

      // v表单字段
      let vFieldConfigMap = {};
      let getVField = configuration => {
        if (configuration.code) {
          vFieldConfigMap[configuration.code] = configuration;
        }

        let items = _self.getConfigurationItems(configuration);
        items.forEach(item => {
          if (item.wtype == 'WidgetSubform') {
            return;
          }
          getVField(item.configuration || {});
        });
      };
      getVField(definitionVjson);

      for (let fieldName in fields) {
        let fieldDefinition = fields[fieldName];
        let vFieldConfiguration = vFieldConfigMap[fieldName];
        if (!vFieldConfiguration) {
          continue;
        }

        // 可编辑
        if (fieldDefinition.showType == '1') {
          vFieldConfiguration.defaultDisplayState = 'edit';
        } else if (fieldDefinition.showType == '2') {
          // 直接以文本的形式显示
          vFieldConfiguration.defaultDisplayState = 'label';
        } else if (fieldDefinition.showType == '3') {
          // 有输入框但只读
          vFieldConfiguration.defaultDisplayState = 'readonly';
        } else if (fieldDefinition.showType == '4') {
          // 有输入框但被disabled
          vFieldConfiguration.defaultDisplayState = 'disable';
        } else if (fieldDefinition.showType == '5') {
          // 隐藏
          vFieldConfiguration.defaultDisplayState = 'hidden';
        }

        // 必填信息
        if (_self.isFieldRequired(fieldDefinition)) {
          vFieldConfiguration.required = true;
        } else {
          vFieldConfiguration.required = false;
        }
      }
    },
    isFieldRequired(fieldDefinition) {
      if (!fieldDefinition.fieldCheckRules || fieldDefinition.fieldCheckRules.length === 0) {
        return false;
      }
      let requiredRuleIndex = fieldDefinition.fieldCheckRules.findIndex(rule => rule.value == '1');
      return requiredRuleIndex != -1;
    },
    // 前端设置环节表单从表控制信息
    setTaskDyformSubformControlInfo: function (definitionVjson, formDefinition) {
      let subforms = formDefinition.subforms || {};
      let subformDefinitions = formDefinition.subformDefinitions || [];
      subformDefinitions.forEach((definitionJson, index) => {
        subformDefinitions[index] = JSON.parse(definitionJson);
      });
      let subformDefinitionMap = {};
      subformDefinitions.forEach(subformDefinition => (subformDefinitionMap[subformDefinition.uuid] = subformDefinition));

      // v表单字段
      let vSubformConfigMap = {};
      let getVSubform = (widget, configuration) => {
        if (widget && widget.wtype == 'WidgetSubform' && configuration.formUuid) {
          vSubformConfigMap[configuration.formUuid] = configuration;
        }

        let items = this.getConfigurationItems(configuration);
        items.forEach(item => {
          getVSubform(item, item.configuration || {});
        });
      };
      getVSubform(null, definitionVjson);

      for (let formUuid in subformDefinitionMap) {
        let subform = subforms[formUuid];
        let subformDefinition = subformDefinitionMap[formUuid];
        let vSubformConfiguration = vSubformConfigMap[formUuid];
        if (!vSubformConfiguration) {
          continue;
        }
        let fields = subformDefinition.fields || {};
        let subformFiels = subform.fields || {};
        let vColumns = vSubformConfiguration.columns || [];
        let vColumnMap = {};
        vColumns.forEach(column => (vColumnMap[column.dataIndex] = column));
        for (let fieldName in fields) {
          let fieldDefinition = fields[fieldName];
          let subformFieldDefinition = subformFiels[fieldName];
          let vFieldConfiguration = vColumnMap[fieldName];
          if (!subformFieldDefinition || !vFieldConfiguration) {
            continue;
          }

          // 可编辑
          if (subformFieldDefinition.editable == '1') {
            vFieldConfiguration.defaultDisplayState = 'edit';
          } else {
            // 不可编辑
            vFieldConfiguration.defaultDisplayState = 'unedit';
          }

          // 隐藏
          if (subformFieldDefinition.hidden == '2') {
            vFieldConfiguration.defaultDisplayState = 'hidden';
          }

          // 必填
          if (this.isFieldRequired(fieldDefinition)) {
            vFieldConfiguration.required = true;
          } else {
            vFieldConfiguration.required = false;
          }
        }
      }
      // console.log('subformDefinitions', subformDefinitions);
      // console.log('vSubformConfigMap', vSubformConfigMap);
    },
    // 显示套打模板
    showPrintTemplates: function (data) {
      this.printTemplateVisible = true;
      this.printTemplateData = data;
    },
    onPrintTemplateOk: function () {
      const _self = this;
      let templateId = _self.printTemplateData.templateId;
      if (isEmpty(templateId)) {
        _self.$message.error(_self.$t('WorkflowWork.message.pleaseChoosePrintTemplate', '请选择套打模板!'));
        return;
      }
      let items = _self.printTemplateData.templates || [];
      let checkedItems = [];
      forEach(items, function (item) {
        if (item.id === templateId) {
          checkedItems.push(item);
        }
      });
      if (checkedItems.length !== 1) {
        _self.$message.error(_self.$t('WorkflowWork.message.pleaseChoosePrintTemplate', '请选择套打模板!'));
        return;
      }
      let template = checkedItems[0];

      _self.printTemplateVisible = false;
      // 重新触发回调事件
      _self.printTemplateData.callback.call(_self.workView, template);
    },
    onToolbarCreated() {
      const _self = this;
      // 自动提交
      setTimeout(function () {
        _self.autoSubmitIfRequried();
      }, 500);
    },
    // 获取相同办理人提交结果信息
    getSameUserSubmitInfo(result) {
      const _self = this;
      let data = (result.data && result.data.data) || {};
      let sameUserSubmitType = stringTrim(data.sameUserSubmitType || '0');
      let submitTaskInstUuid = stringTrim(data.sameUserSubmitTaskInstUuid);
      let submitTaskOperationUuid = stringTrim(data.sameUserSubmitTaskOperationUuid);
      if (!(!isEmpty(submitTaskInstUuid) && !isEmpty(submitTaskOperationUuid))) {
        return null;
      }

      var refreshUrl = `/workflow/work/view/todo?taskInstUuid=${submitTaskInstUuid}&flowInstUuid=${data.flowInstUuid}&sameUserSubmitType=${sameUserSubmitType}&auto_submit=true&sameUserSubmitTaskOperationUuid=${submitTaskOperationUuid}`;

      return {
        sameUserSubmitType,
        refreshUrl: _self.workView.addSystemPrefix(refreshUrl),
        taskInstUuid: submitTaskInstUuid,
        flowInstUuid: data.flowInstUuid,
        sameUserSubmitTaskOperationUuid: submitTaskOperationUuid,
        autoSubmit: true
      };
    },
    // 自动提交
    autoSubmitIfRequried: function () {
      var _self = this;
      var autoSubmit = _self.workView.getQueryString('auto_submit');
      var sameUserSubmitType = _self.workView.getQueryString('sameUserSubmitType');
      if (autoSubmit === 'true' && _self.workView.isAllowSubmit()) {
        // 不自动提交并刷新页面
        if (sameUserSubmitType == '3') {
        } else if (sameUserSubmitType == '1') {
          // 自动提交，且自动继承意见
          _self.$refs.workflowToolbar.submit();
        } else if (sameUserSubmitType == '0') {
          // 自动提交，让办理人确认是否继承上一环节意见
          var workData = _self.workView.getWorkData();
          var data = _self.workView.getOpinionEditor().getOpinion();
          let message =
            '<div class="auto-title">' +
            _self.$t('WorkflowWork.message.autoSubmitWithLastOpinion', '自动使用上一环节意见进行提交') +
            '</div>';
          if (data.text) {
            message += '<div class="auto-opinion-text">' + data.text + '</div>';
          }
          _self.autoSubmitData = {
            title:
              _self.$t('WorkflowWork.message.readyToSubmitContinuously', '即将连续提交') +
              '[' +
              _self.$t('WorkflowView.' + workData.taskId + '.taskName', workData.taskName) +
              ']',
            message: message
          };
          _self.autoSubmitVisible = true;
        }
      }
    },
    onAutoSubmitOk: function (event) {
      this.$refs.workflowToolbar.submit(event);
      this.autoSubmitVisible = false;
    },
    onAutoSubmitCancel: function () {
      const _this = this;
      _this.autoSubmitVisible = false;
      let newUrl = window.location.href;
      newUrl = newUrl.replace('&auto_submit=true', '').replace('&sameUserSubmitTaskOperationUuid=', '&optUuid=');
      window.history.replaceState(null, null, newUrl);
      let opinionEditor = _this.workView.getOpinionEditor();
      opinionEditor && opinionEditor.reset();
    },
    // 获取退回到自己的信息
    getRollbackToSelfInfo(result) {
      var _self = this;
      var data = (result.data && result.data.data) || {};
      var toTaskInstUuid = Object.keys(data.taskTodoUsers)[0];
      var currUserId = _self.workView.getCookie('cookie.current.userId');
      var taskTodoUsers = data.taskTodoUsers[toTaskInstUuid];
      var isSameUser = false;
      for (var i in taskTodoUsers) {
        if (i == currUserId) {
          isSameUser = true;
          break;
        }
      }
      return { rollbackToSelf: isSameUser, taskInstUuid: toTaskInstUuid, flowInstUuid: data.flowInstUuid };
    }
  }
};
</script>
<style lang="less" scoped>
.work-view-header {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 100;
  width: 100%;
  padding: 0px;
  border-top: 0;
  background: var(--w-primary-color);
  // box-shadow: 0 2px 0 0 var(--w-primary-color-5);
}
.work-view-header .title {
  color: var(--w-white);
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  line-height: 60px;
  font-size: var(--w-font-size-lg);
  display: inline-block;
  padding-left: var(--w-padding-md);
  font-weight: bold;
}
.work-view-header .work-view-toolbar {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  margin-right: var(--w-padding-md);
}

.work-view-content {
  height: 100vh;
  background: linear-gradient(to bottom, var(--w-primary-color), var(--w-primary-color-1) 400px);
  padding: var(--w-padding-xs) var(--w-padding-md);

  .widget-dyform {
    background: var(--w-fill-color-base);
    border-radius: var(--w-border-radius-base);
    min-height: e('calc(100vh - var(--w-padding-xs) * 2)');
  }
  .widget-dyfrom-out-ps {
    background: var(--w-fill-color-base);
    border-radius: var(--w-border-radius-base);
  }
}
.work-view-header + .work-view-content {
  min-height: e('calc(100vh - 64px)');
  height: 100%;
  padding-top: 0;
  margin-top: 64px;

  .widget-dyform {
    min-height: e('calc(100vh - 64px - 14px)');
  }
}

.work-view-container {
  background: var(--w-fill-color-base);

  ::v-deep .ant-spin-spinning {
    position: fixed;
    left: 50%;
    top: 35%;
    z-index: 100;
  }
}
</style>
<style lang="less">
.widget-dyfrom-out-ps {
  .widget-dyform {
    .widget-subform-table {
      .ant-table {
        &.ant-table-empty {
          .ant-table-body {
            overflow: visible !important;
          }
        }
        .ant-table-content {
          > .ant-table-body {
            overflow: visible !important;
          }
        }
      }
    }
  }
}
</style>
