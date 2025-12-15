<template>
  <a-layout :class="['widget-dyform-setting', !widget.configuration.titleVisible ? 'no-title' : undefined]">
    <a-layout-header class="widget-dyform-setting-head flex" v-if="showHead" ref="wHeader">
      <h1 v-if="widget.configuration.titleVisible" class="f_g_1 w-ellipsis" :title="title">
        <Icon :type="widget.configuration.titleIcon || null" />
        {{ title }}
      </h1>
      <div
        :class="{ 'widget-dyform-setting-buttons': true, f_s_0: widget.configuration.titleVisible }"
        :style="!widget.configuration.titleVisible ? { width: '100%', 'text-align': 'right' } : ''"
      >
        <WidgetTableButtons
          :button="button"
          v-if="widget.configuration.buttonPosition === 'top'"
          :eventWidget="eventWidget"
          :meta="metaData"
          :developJsInstance="developJsInstance"
          :visibleJudgementData="visibleJudgementData"
          :key="buttonKey"
        />
      </div>
    </a-layout-header>
    <a-layout-content
      :class="['widget-dyform-setting-content', widget.configuration.buttonPosition === 'top' ? 'no-footer' : '']"
      ref="wContent"
    >
      <!-- <WidgetDyform :formUuid="widget.configuration.formUuid" v-bind="widget.props" ref="wDyform" /> -->
      <Scroll :style="{ height: scrollHeight }">
        <div v-if="dyformComponentLoading" class="spin-center">
          <a-spin />
        </div>
        <component
          v-else
          is="WidgetDyform"
          :formUuid="formUuid"
          :displayState="displayState"
          :formElementRules="formElementRules"
          v-bind="widget.props"
          ref="wDyform"
          @mounted="onDyformMounted"
          :key="wDyformKey"
          @formDataChanged="onFormDataChanged"
          @dyformTitleChanged="onDyformTitleChanged"
          :dyformStyle="{ padding: inContainer ? 'unset' : 'var(--w-padding-md)' }"
        />
      </Scroll>
    </a-layout-content>
    <a-layout-footer
      style="border-top: 1px solid #e8e8e8"
      ref="wFooter"
      v-if="widget.configuration.buttonPosition === 'bottom'"
      :class="['widget-dyform-setting-footer', 'align-' + widget.configuration.button.buttonAlign]"
    >
      <WidgetTableButtons
        :developJsInstance="developJsInstance"
        :button="button"
        v-if="widget.configuration.buttonPosition === 'bottom'"
        :eventWidget="eventWidget"
        :meta="metaData"
        :visibleJudgementData="visibleJudgementData"
        :key="buttonKey"
      />
    </a-layout-footer>
  </a-layout>
</template>
<style lang="less">
.widget-dyform-setting {
  &.no-title {
    .widget-dyform-setting-content {
      margin: 0px !important;
    }
  }
}
.widget-dyform-setting-content {
  // padding: 10px 0px 10px 10px;
}

.ant-modal-body .widget-dyform-setting-content {
  min-height: auto;
}

#app {
  > .widget-dyform-setting {
    min-height: e('calc(100vh)');
  }
}
</style>

<script type="text/babel">
import widgetMixin from '@framework/vue/mixin/widgetMixin';
import { deepClone, queryString, expressionCompare } from '@framework/vue/utils/util';
import md5 from '@framework/vue/utils/md5';
import { executeJSFormula } from '@framework/vue/utils/util';
import { clone } from 'lodash';

// import '@dyform/app/web/framework/vue/install';

export default {
  name: 'WidgetDyformSetting',
  mixins: [widgetMixin],
  props: {
    containerHeight: Number | String,
    initFormData: Object
  },
  inject: ['locale', 'widgetDyformContext'],
  data() {
    let displayState = undefined;
    if (this.widgetDyformContext != undefined) {
      displayState = this.widgetDyformContext.displayState;
    }
    if (this.widget.props && this.widget.props.displayState) {
      displayState = this.widget.props.displayState;
    }
    let formElementRules = [];
    if (this.widgetDyformContext != undefined) {
      let ruleKey = 'formElementRules';
      if (this.widgetDyformContext.dataUuid != undefined) {
        ruleKey = displayState === 'edit' ? 'editStateFormElementRules' : 'labelStateFormElementRules';
      }
      if (this.widget.configuration[ruleKey]) {
        for (let i = 0, len = this.widget.configuration[ruleKey].length; i < len; i++) {
          formElementRules[this.widget.configuration[ruleKey][i].id] = {
            readonly: this.widget.configuration[ruleKey][i].readonly,
            editable: this.widget.configuration[ruleKey][i].editable,
            disable: this.widget.configuration[ruleKey][i].disable,
            hidden: this.widget.configuration[ruleKey][i].hidden,
            displayAsLabel: this.widget.configuration[ruleKey][i].displayAsLabel,
            required: this.widget.configuration[ruleKey][i].required === true
          };
          let children = this.widget.configuration[ruleKey][i].children;
          if (children) {
            let childrenRules = {};
            formElementRules[this.widget.configuration[ruleKey][i].id].children = childrenRules;
            for (let j = 0, jlen = children.length; j < jlen; j++) {
              childrenRules[children[j].id] = {
                readonly: children[j].readonly,
                editable: children[j].editable,
                disable: children[j].disable,
                hidden: children[j].hidden,
                displayAsLabel: children[j].displayAsLabel,
                required: children[j].required === true
              };
            }
          }
        }
      }
    }
    if (this.widget.props && this.widget.props.formElementRules) {
      formElementRules = this.widget.props.formElementRules;
    }
    let { enableStateForm, labelStateFormUuid, editStateFormUuid } = this.widget.configuration;
    let formUuid = this.widget.configuration.formUuid;
    if (enableStateForm) {
      if (displayState == undefined || displayState == 'label') {
        formUuid = labelStateFormUuid || formUuid;
      } else if (
        displayState == 'edit' &&
        ((this.widgetDyformContext != undefined && this.widgetDyformContext.dataUuid != undefined) ||
          this.widget.props.dataUuid != undefined)
      ) {
        formUuid = editStateFormUuid || formUuid;
      }
    }
    if (this.widget.props && this.widget.props.formUuid) {
      formUuid = this.widget.props.formUuid;
    }
    return {
      formUuid,
      wDyformKey: this.widget.id + '_dyform',
      buttonKey: this.widget.id + '_buttons',
      inContainer: this.dyform != undefined,
      title: null,
      dyformComponentLoading: true,
      dataUuid: this.widget.props ? this.widget.props.dataUuid : undefined,
      displayState,
      formElementRules,
      height: this.containerHeight || (this.containerStyle ? this.containerStyle.height : 'calc(100vh)'), // containerStyle 由父级 provide
      scrollHeight: this.dyform == undefined ? '600px' : '100%',
      formData: {},
      formDataMD5: '1',
      enableDataBinding: this.widget.configuration.enableDataBinding,
      metaData: {
        containerWid: this.widget.props ? this.widget.props.containerWid : undefined,
        targetPosition: this.widget.props ? this.widget.props.targetPosition : undefined,
        dmsId: this.widget.id,
        $dyform: this.$refs.dyform,
        wTableId: this.widget.props ? this.widget.props.wTableId : undefined,
        namespace: this.widget.props ? this.widget.props.namespace : undefined
      },
      dyformMounted: false
    };
  },
  watch: {},
  beforeCreate() {
    Promise.all([import('@dyform/app/web/framework/vue/install'), import('@installWorkflowWidget')]).then(m => {
      this.dyformComponentLoading = false;
    });
  },
  components: {},
  computed: {
    visibleJudgementData() {
      return this.getVisibleJudgementData();
    },
    showFooter() {
      return widget.configuration.buttonPosition === 'bottom';
    },
    showHead() {
      return this.widget.configuration.buttonPosition === 'top' || this.widget.configuration.titleVisible;
    },
    button() {
      let buttons = clone(this.widget.configuration.button.buttons),
        visibleButtons = [];

      for (let i = 0, len = buttons.length; i < len; i++) {
        if (buttons[i].visibleType === 'visible') {
          visibleButtons.push(buttons[i]);
          buttons[i].visible = true;
          continue;
        }

        if (buttons[i].visibleType === 'hidden') {
          continue;
        }

        // 根据条件判断展示:
        if (buttons[i].visibleType === 'visible-condition') {
          if (
            (this.dataUuid == undefined && buttons[i].visibleCondition.formStateConditions.indexOf('createForm') != -1) ||
            (this.dataUuid != undefined &&
              this.displayState === 'edit' &&
              buttons[i].visibleCondition.formStateConditions.indexOf('edit') != -1) ||
            (this.dataUuid != undefined &&
              this.displayState === 'label' &&
              buttons[i].visibleCondition.formStateConditions.indexOf('label') != -1)
          ) {
            // 关联角色
            if (buttons[i].visibleCondition.userRoleConditions && buttons[i].visibleCondition.userRoleConditions.length > 0) {
              if (!this._hasAnyRole(buttons[i].visibleCondition.userRoleConditions)) {
                continue;
              }
            }

            // 自定义条件
            buttons[i].defaultVisibleVar = this.getButtonDefaultVisibleVar(buttons[i]);
            buttons[i].defaultVisible = true;
            buttons[i].visible = true;
            buttons[i].title = this.$t(buttons[i].id, buttons[i].title);
            visibleButtons.push(buttons[i]);
          }
        }
      }

      let btn = clone(this.widget.configuration.button);
      btn.buttons = visibleButtons;
      btn.buttonGroup.dynamicGroupName = this.$t('dynamicGroupName', btn.buttonGroup.dynamicGroupName);
      return btn;
    }
  },
  created() {},
  methods: {
    getButtonDefaultVisibleVar(button) {
      return {
        enable: button.visibleCondition.enableDefineCondition,
        // 兼容旧版语法转换
        conditions: button.visibleCondition.defineCondition.cons.map(item => {
          if (/^[_a-z]+(_[a-z]+)*$/.test(item.code)) {
            // 表单字段编码
            item.code = `_FORM_DATA_.${item.code}`;
          }
          return item;
        }),
        match: button.visibleCondition.defineCondition.operator == 'and' ? 'all' : 'any'
      };
    },
    getVisibleJudgementData() {
      return {
        ...this._vShowByDateTime,
        ...this._vShowByUserData,
        ...this._vShowByWorkflowData,
        _FORM_DATA_: this.dyformMounted ? this.$refs.wDyform.dyform.formData : {},
        _URL_PARAM_: this.vUrlParams
      };
    },
    getDyformData() {
      return this.$refs.wDyform.dyform.formData;
    },
    onFormDataChanged(e) {
      if (this.$refs.wDyform) {
        this.formData = JSON.parse(JSON.stringify(this.$refs.wDyform.dyform.formData));
        this.formDataMD5 = md5(JSON.stringify(this.formData));
        this.invokeDevelopmentMethod('afterFormDataChanged', this.$refs.wDyform.dyform.formData);
      }
      this.$nextTick(() => {
        this.buttonKey = `${this.widget.id}_buttons_` + new Date().getTime();
      });

      this.updateModalButton();
    },
    eventWidget() {
      return this.$refs.wDyform;
    },
    updateModalButton() {
      if (this.widget.props.containerWid && this.widget.props.targetPosition === 'widgetModal') {
        let $dyform = this.$refs.wDyform,
          containerWid = this.widget.props.containerWid,
          targetPosition = this.widget.props.targetPosition,
          dmsId = this.widget.id,
          wTableId = this.widget.props.wTableId;
        // 通知弹窗进行按钮更新
        this.pageContext.emitEvent(`WidgetModal:${this.widget.props.containerWid}:SetButton`, this.button, $dyform, {
          containerWid,
          targetPosition,
          dmsId,
          wTableId,
          $dyform,
          ...this.getVisibleJudgementData(),
          $developJsInstance: this.$developJsInstance
        });
      }
    },
    onDyformMounted(e) {
      this.invokeDevelopmentMethod('dyformMounted');
      this.$emit('dyformMounted');
      this.metaData.$dyform = e || this.$refs.dyform;
      if (this.$refs.wDyform.dataUuid == undefined && this.initFormData != undefined) {
        // 初始化数据
        this.$refs.wDyform.setFormData(this.initFormData);
      }

      if (this.widget.props.containerWid && this.widget.props.targetPosition === 'widgetModal') {
        this.updateModalButton();
        // 通知弹窗进行标题更新
        this.pageContext.emitEvent(`WidgetModal:${this.widget.props.containerWid}:Title`, this.title);
      }

      this.dyformMounted = true;
      this.loadBindingFormData();

      if (
        this.widgetDyformContext != undefined &&
        this.widget.configuration.dataBinding != undefined &&
        this.widget.configuration.dataBinding.syncSaveWithMainForm
      ) {
        if (this.widgetDyformContext.dyform.relaWidgetDyform == undefined) {
          this.widgetDyformContext.dyform.relaWidgetDyform = {};
        }
        this.widgetDyformContext.dyform.relaWidgetDyform[this.formUuid] = this.$refs.wDyform;
        let syncFieldValue = this.widget.configuration.dataBinding.syncFieldValue;
        if (syncFieldValue && syncFieldValue.mainFormField && syncFieldValue.currentFormField) {
          this.widgetDyformContext.dyform.handleEvent(`${this.widgetDyformContext.formUuid}:afterCollectFormData`, data => {
            let formDatas = data.dyFormData.formDatas;
            let currentFormData = formDatas[this.formUuid][0],
              mainFormData = formDatas[this.widgetDyformContext.formUuid][0];
            if (data.dyFormData.updatedFormDatas == undefined) {
              data.dyFormData.updatedFormDatas = {};
            }
            if (currentFormData && mainFormData) {
              if (syncFieldValue.syncToCurrentForm) {
                currentFormData[syncFieldValue.currentFormField.toLowerCase()] = mainFormData[syncFieldValue.mainFormField.toLowerCase()];
                if (data.dyFormData.updatedFormDatas[this.formUuid] == undefined) {
                  data.dyFormData.updatedFormDatas[this.formUuid] = {
                    [currentFormData.uuid]: []
                  };
                } else if (data.dyFormData.updatedFormDatas[this.formUuid][currentFormData.uuid] == undefined) {
                  data.dyFormData.updatedFormDatas[this.formUuid][currentFormData.uuid] = [];
                }
                data.dyFormData.updatedFormDatas[this.formUuid][currentFormData.uuid].push(syncFieldValue.currentFormField);
              } else {
                mainFormData[syncFieldValue.mainFormField.toLowerCase()] = currentFormData[syncFieldValue.currentFormField.toLowerCase()];
                if (this.widgetDyformContext.dataUuid !== undefined) {
                  if (data.dyFormData.updatedFormDatas[this.widgetDyformContext.formUuid] == undefined) {
                    data.dyFormData.updatedFormDatas[this.widgetDyformContext.formUuid] = {
                      [mainFormData.uuid]: []
                    };
                  } else if (data.dyFormData.updatedFormDatas[this.widgetDyformContext.formUuid][mainFormData.uuid] == undefined) {
                    data.dyFormData.updatedFormDatas[this.widgetDyformContext.formUuid][mainFormData.uuid] = [];
                  }
                  data.dyFormData.updatedFormDatas[this.widgetDyformContext.formUuid][mainFormData.uuid].push(syncFieldValue.mainFormField);
                }
              }
            }
          });
        }
      }
    },
    adjustLayoutContentHeight() {
      let wContent = this.$refs.wContent;
      let contentHeight = this.height,
        calc = false;
      if (typeof contentHeight === 'string') {
        calc = contentHeight.indexOf('calc') == 0;
        if (calc) {
          // 是计算表达式的情况下
          contentHeight = contentHeight.substring(contentHeight.indexOf('calc(') + 5, contentHeight.length - 1).trim();
        } else if (contentHeight.endsWith('px')) {
          contentHeight = parseInt(contentHeight);
        }
      }

      // 获取头部、底部占用高度
      let wHeader = this.$refs.wHeader,
        wFooter = this.$refs.wFooter,
        headerHeight = 0,
        footerHeight = 0;
      if (wHeader) {
        let _style = window.getComputedStyle(wHeader.$el);
        headerHeight = wHeader.$el.getBoundingClientRect().height + parseInt(_style.marginBottom) + parseInt(_style.marginTop);
      }

      if (wFooter) {
        let _style = window.getComputedStyle(wFooter.$el);
        footerHeight = wFooter.$el.getBoundingClientRect().height + parseInt(_style.marginBottom) + parseInt(_style.marginTop);
      }
      let calcHeight = null;

      if (calc) {
        console.log(
          `内容高度 ${calcHeight} = 容器高度 (${contentHeight}) - 头部高度 (${headerHeight}) - 底部高度 ${footerHeight} - 内容padding 20px`
        );
        calcHeight = `calc(${contentHeight} - ${headerHeight + footerHeight}px - 20px)`;
        console.log(`内容高度 ${calcHeight} = ${calcHeight}`);
      } else {
        calcHeight = contentHeight - headerHeight - footerHeight - 20;
        console.log(
          `内容高度 ${calcHeight} = 容器高度 (${contentHeight}) - 头部高度 (${headerHeight}) - 底部高度 ${footerHeight} - 内容padding 20px`
        );
      }

      this.scrollHeight = calcHeight;
    },
    // metaData() {
    //   // 获取浏览器地址栏上的
    //   return {
    //     containerWid: this.widget.props ? this.widget.props.containerWid : undefined,
    //     targetPosition: this.widget.props ? this.widget.props.targetPosition : undefined,
    //     dmsId: this.widget.id,
    //     $dyform: this.$refs.dyform,
    //     wTableId: this.widget.props ? this.widget.props.wTableId : undefined
    //   };
    // }
    setTitle() {
      // 编辑状态时标题
      if (this.widget.props && this.widget.props.dataUuid) {
        this.title = this.$t('editStateTitle', this.widget.configuration.editStateTitle);
      } else {
        this.title = this.$t('title', this.widget.configuration.title);
      }
      // 查阅状态时标题
      if (this.widget.props.displayState === 'label') {
        this.title = this.$t('labelStateTitle', this.widget.configuration.labelStateTitle);
      }
      this.$emit('titleChanged', this.title);
    },
    widgetPropsChange() {
      if (this.widget.props) {
        // 编辑状态时标题
        this.setTitle();

        if (this.metaData.containerWid && this.metaData.targetPosition === 'widgetModal') {
          let $dyform = this.$refs.wDyform,
            containerWid = this.metaData.containerWid,
            targetPosition = this.metaData.targetPosition,
            dmsId = this.widget.id,
            wTableId = this.metaData.wTableId;
          // 通知弹窗进行按钮更新
          this.pageContext.emitEvent(`WidgetModal:${this.metaData.containerWid}:SetButton`, this.button, $dyform, {
            containerWid,
            targetPosition,
            dmsId,
            wTableId,
            $dyform,
            ...this.getVisibleJudgementData()
          });
          // 通知弹窗进行标题更新
          this.pageContext.emitEvent(`WidgetModal:${this.metaData.containerWid}:Title`, this.title);
        } else {
          // 刷新按钮
          this.buttonKey = `${this.widget.id}_buttons_` + new Date().getTime();
        }
      }
    },
    queryFormDataByCondition(condition) {
      return new Promise((resolve, reject) => {
        if (condition && condition.length > 0) {
          let _this = this;
          let depData = this.widgetDependentVariableDataSource(),
            dependencyCodes = new Set(),
            params = {};
          for (let i = 0, len = condition.length; i < len; i++) {
            if (condition[i].valueType == 'prop' && condition[i].value && condition[i].value.startsWith(':MAIN_FORM_DATA_')) {
              let code = condition[i].value.split(':MAIN_FORM_DATA_')[1];
              dependencyCodes.add(code);
              if (depData[code] != undefined && depData[code] != '') {
                params[condition[i].value.substring(1)] = depData[code];
              }
            }
          }

          // 存在通过条件查询关联表单字段数据的，则发起后端条件查询
          if (dependencyCodes.size == 0 || (Object.keys(params).length == dependencyCodes.size && dependencyCodes.size > 0)) {
            if (JSON.stringify(this.tempNamedParams) != JSON.stringify(params)) {
              this.tempNamedParams = params;
              this.getFormDataUuidByWhereByFormUuid(this.formUuid, condition, params).then(list => {
                if (list && list.length > 0) {
                  _this.widget.props.dataUuid = list[0];
                  this.$refs.wDyform.dataUuid = list[0];
                  this.$refs.wDyform.fetchFormData(list[0]);
                } else {
                  if (_this.widget.props.dataUuid != undefined) {
                    _this.widget.props.dataUuid = undefined;
                    _this.$refs.wDyform.dataUuid = undefined;
                    _this.$refs.wDyform.clearFormData();
                  }
                }
                resolve();
              });
            }
          }
        } else {
          resolve();
        }
      });
    },
    getFormDataUuidByWhereByFormUuid(formUuid, where, namedParams = {}) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/dyform/data/getFormDataUuidByWhereByFormUuid/${formUuid}`, {
            where,
            namedParams,
            order: 'order by create_time asc'
          })
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {});
      });
    },
    loadBindingFormData() {
      return new Promise((resolve, reject) => {
        if (this.enableDataBinding && this.dataUuid == undefined && this.dyformMounted) {
          let { type, condition, dataUuidExpr, syncFieldValue, syncSaveWithMainForm, underCurrentSystem } =
            this.widget.configuration.dataBinding;
          if (type == 'query') {
            let syncFieldValue = this.widget.configuration.dataBinding.syncFieldValue;
            let _condition = [];
            if (
              condition.length == 0 &&
              syncSaveWithMainForm &&
              syncFieldValue &&
              syncFieldValue.mainFormField &&
              syncFieldValue.currentFormField
            ) {
              _condition.push({
                prop: syncFieldValue.currentFormField,
                sign: '=',
                value: ':MAIN_FORM_DATA_' + syncFieldValue.mainFormField,
                valueType: 'prop'
              }); //.concat(condition.length > 0 ? [{ sign: 'AND' }] : []);
            }
            if (underCurrentSystem) {
              if (_condition.length > 0) {
                _condition.push({
                  sign: 'AND'
                });
              }
              _condition.push({
                prop: 'system',
                sign: '=',
                value: 'currentSystem',
                valueType: 'var'
              });
            }
            if (condition.length > 0) {
              _condition.push({
                sign: 'AND'
              });
              _condition.push(...condition);
            }

            this.queryFormDataByCondition(_condition).then(() => {
              resolve();
            });
          } else if (type == 'useDataUuid') {
            let _this = this;
            executeJSFormula(dataUuidExpr.value, this.widgetDependentVariableDataSource()).then(dataUuid => {
              if (dataUuid != undefined) {
                _this.$refs.wDyform.dataUuid = dataUuid;
                _this.$refs.wDyform.fetchFormData(true);
                resolve();
              }
            });
          }
        }
      });
    },
    afterChangeableDependDataChanged() {
      this.loadBindingFormData();
    },
    onDyformTitleChanged(title) {
      this.$emit('dyformTitleChanged', title);
    }
  },
  beforeMount() {
    this.metaData.$dyform = this.$refs.dyform;
    if (this.widget.props === undefined) {
      let urlParams = queryString(location.search.substr(1));
      this.widget.props = {};
      if (this.widgetDyformContext == undefined && urlParams.dataUuid) {
        this.widget.props.dataUuid = urlParams.dataUuid;
        this.dataUuid = urlParams.dataUuid;
      }
      if (urlParams.displayState) {
        this.widget.props.displayState = urlParams.displayState;
        this.displayState = urlParams.displayState;
      }
    }
  },
  mounted() {
    let _this = this;
    this.widgetPropsChange();
    this.pageContext.offEvent(`WidgetDyformSetting:${this.widget.id}:refresh`);
    this.pageContext.handleEvent(`WidgetDyformSetting:${this.widget.id}:refresh`, data => {
      if (data) {
        if (data.props) {
          if (data.props.displayState) {
            _this.displayState = data.props.displayState;
          }
          _this.widget.props = data.props;
          if (
            _this.widget.props &&
            _this.widget.props.formElementRules &&
            md5(JSON.stringify(_this.formElementRules)) != md5(JSON.stringify(_this.widget.props.formElementRules))
          ) {
            _this.formElementRules = _this.widget.props.formElementRules;
          }
          _this.$loading();
        }
        _this.$refs.wDyform.clearDyformCache().then(() => {
          _this.$loading(false);
          _this.wDyformKey = _this.widget.id + '_dyform_' + new Date().getTime();
          _this.$nextTick(() => {
            _this.widgetPropsChange();
          });
        });
      }
    });
    this.pageContext.handleEvent(`${this.widget.id}:afterSaveDataSuccess`, data => {
      this.invokeDevelopmentMethod('afterSaveDataSuccess', data);
    });
    if (this.dyform == undefined) {
      this.adjustLayoutContentHeight();
    }
  },

  beforeDestroy() {}
};
</script>
