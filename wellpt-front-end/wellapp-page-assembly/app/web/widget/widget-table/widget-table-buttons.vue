<template>
  <a-space>
    <slot name="prefix"></slot>
    <template v-for="(button, i) in buttons">
      <template v-if="button.children">
        <div>
          <a-dropdown :key="i" :getPopupContainer="triggerNode => triggerNode.parentNode" @click.native.stop="e => {}" :trigger="['click']">
            <a-menu slot="overlay">
              <template v-for="(b, j) in button.children">
                <a-menu-item
                  :key="'child-' + j"
                  @click="$evt => onTrigger($evt, 'click', b, true)"
                  @dblclick="$evt => onTrigger($evt, 'dblclick', b, true)"
                  :title="b.style.textHidden == true ? $t(b.id, b.title) : ''"
                >
                  <template v-if="b.confirmConfig && b.confirmConfig.enable && b.confirmConfig.popType == 'popconfirm'">
                    <a-popconfirm
                      :title="resolvePopconfirmTitle(b.confirmConfig.title, b.id + '_popconfirmTitle')"
                      :ok-text="$t(b.id + '_popconfirmOkText', b.confirmConfig.okText)"
                      :cancel-text="$t(b.id + '_popconfirmCancelText', b.confirmConfig.cancelText)"
                      :zIndex="10000"
                      @confirm="$evt => onTrigger($evt, 'click', b)"
                    >
                      <label @click.stop="() => {}">
                        <Icon v-if="b.style.icon" :type="b.style.icon" :size="20" />
                        {{ b.style.textHidden == true ? '' : b.titleFormatter ? b.titleFormatter($t(b.id, b.title)) : $t(b.id, b.title) }}
                      </label>
                    </a-popconfirm>
                  </template>
                  <label v-else>
                    <Icon v-if="b.style.icon" :type="b.style.icon" :size="20" />
                    {{ b.style.textHidden == true ? '' : b.titleFormatter ? b.titleFormatter($t(b.id, b.title)) : $t(b.id, b.title) }}
                  </label>
                </a-menu-item>
              </template>
            </a-menu>
            <a-button :size="size" :type="button.type || button.style.type || 'default'">
              <Icon v-if="button.style != undefined && button.style.icon" :type="button.style.icon" :size="iconSize(button)" />
              {{ button.style.textHidden == true ? '' : $t(button.id, button.title) }}
              <a-icon type="down" v-if="button.style.rightDownIconVisible" />
            </a-button>
          </a-dropdown>
        </div>
      </template>
      <template v-else>
        <template v-if="button.confirmConfig && button.confirmConfig.enable && button.confirmConfig.popType == 'popconfirm'">
          <a-popconfirm
            :title="resolvePopconfirmTitle(button.confirmConfig.title, button.id + '_popconfirmTitle')"
            :ok-text="$t(button.id + '_popconfirmOkText', button.confirmConfig.okText)"
            :cancel-text="$t(button.id + '_popconfirmCancelText', button.confirmConfig.cancelText)"
            placement="bottomLeft"
            @confirm="$evt => onTrigger($evt, 'click', button)"
          >
            <a-button
              v-if="button.style.type != 'switch'"
              :key="i"
              :ghost="ghost"
              :code="button.code || button.id"
              :shape="button.style.shape"
              :type="button.style.type || buttonDefaultType"
              :size="button.style.size || size"
              @click.stop="() => {}"
              :title="
                button.style.textHidden == true
                  ? button.titleFormatter
                    ? button.titleFormatter($t(button.id, button.title))
                    : $t(button.id, button.title)
                  : ''
              "
            >
              <Icon :type="button.style.icon" v-if="button.style.icon" :size="iconSize(button)" />
              {{
                button.style.textHidden == true
                  ? ''
                  : button.titleFormatter
                  ? button.titleFormatter($t(button.id, button.title))
                  : $t(button.id, button.title)
              }}
            </a-button>
          </a-popconfirm>
        </template>
        <template v-else>
          <a-button
            v-if="button.style.type != 'switch'"
            :key="i"
            :ghost="ghost"
            :code="button.code || button.id"
            :shape="button.style.shape"
            :type="button.style.type || buttonDefaultType"
            :size="button.style.size || size"
            @click.stop="$evt => onTrigger($evt, 'click', button)"
            @dblclick.stop="$evt => onTrigger($evt, 'dblclick', button)"
            :title="
              button.style.textHidden == true
                ? button.titleFormatter
                  ? button.titleFormatter($t(button.id, button.title))
                  : $t(button.id, button.title)
                : ''
            "
          >
            <Icon :type="button.style.icon" v-if="button.style.icon" :size="iconSize(button)" />
            {{
              button.style.textHidden == true
                ? ''
                : button.titleFormatter
                ? button.titleFormatter($t(button.id, button.title))
                : $t(button.id, button.title)
            }}
          </a-button>
          <a-switch
            v-else
            :key="i"
            :checked="button.switch.checked"
            :loading="loading"
            :code="button.code || button.id"
            :checked-children="button.switch.checkedText || null"
            :un-checked-children="button.switch.UnCheckedText || null"
            @change="(checked, $evt) => onTrigger($evt, 'click', button)"
            :style="{ width: 'max-content' }"
          />
        </template>
      </template>
    </template>
    <slot name="suffix"></slot>
    <div v-if="mask" style="position: absolute; width: 100%; height: 100%; z-index: 1; top: 0; left: 0"></div>
  </a-space>
</template>
<script type="text/babel">
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
import { debounce, template as stringTemplate, cloneDeep, get } from 'lodash';
import { expressionCompare, generateId, executeJSFormula } from '@framework/vue/utils/util';

export default {
  name: 'WidgetTableButtons',
  inject: [
    'pageContext',
    'vPageState',
    '$pageJsInstance',
    'unauthorizedResource',
    'parentLayContentId',
    'namespace',
    'widgetContext',
    'designMode',
    'widgetContext'
  ],
  props: {
    button: Object,
    ghost: Boolean,
    mask: Boolean,
    buttonDefaultType: {
      type: String,
      default: ''
    },
    size: {
      type: String,
      default: 'default'
    },
    position: '', // 按钮位置tableHeader、rowEnd、rowCol
    meta: {
      // 按钮元信息：可以把业务数据等其他信息附加上去，事件派发会统一处理meta数据到状态管理数据内
      type: Object | Function,
      default: function () {
        return {};
      }
    },
    buttonPredicate: Function,
    developJsInstance: Object | Function,
    parentWidget: Object | Function,
    eventWidget: Object | Function, // 事件主体组件
    translateKeyWrapper: Function,
    visibleJudgementData: {
      type: Object,
      default: function () {
        return {};
      }
    },
    jsFormulaFunctions: Array,
    designModeIsDisabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    let _button = JSON.parse(JSON.stringify(this.button));
    this.$emit('buttonDataBeforeInit', { button: _button });
    return {
      buttonConf: _button,
      switchChecked: true,
      loading: false
    };
  },

  beforeCreate() {},
  components: {},
  computed: {
    buttonMap() {
      let map = {};
      for (let i = 0, len = this.buttonConf.buttons.length; i < len; i++) {
        map[this.buttonConf.buttons[i].id] = this.buttonConf.buttons[i];
      }
      return map;
    },

    buttons() {
      let buttonGroup = this.buttonConf.buttonGroup,
        visibleButtons = [],
        visibleBtnIds = [];
      if (this.buttonConf.buttons.length == 0) {
        return [];
      }
      for (let i = 0, len = this.buttonConf.buttons.length; i < len; i++) {
        let btn = this.buttonConf.buttons[i];
        if (typeof this.buttonPredicate == 'function') {
          let isTrue = this.buttonPredicate(btn);
          if (!isTrue) {
            continue;
          }
        }
        if (btn.role && btn.role.length > 0 && !this.designMode) {
          // 判断是否有权限
          if (!(Array.isArray(btn.role) ? this._hasAnyRole(btn.role) : this._hasRole(btn.role))) {
            continue;
          }
        }

        if (this.unauthorizedResource && this.unauthorizedResource.includes(btn.id)) {
          continue;
        }

        let visible = btn.defaultVisible;
        // 根据页面变量决定是否展示
        let meta = typeof this.meta == 'function' ? this.meta() : this.meta;
        if (!this.designMode && btn.defaultVisibleVar && btn.defaultVisibleVar.enable) {
          let _compareData = { ...this.vPageState, ...meta, ...this.visibleJudgementData };
          if (btn.defaultVisibleVar.code) {
            let code = btn.defaultVisibleVar.code,
              value = btn.defaultVisibleVar.value,
              operator = btn.defaultVisibleVar.operator;
            if (btn.defaultVisibleVar.valueType == 'variable') {
              try {
                value = get(_compareData, value);
              } catch (error) {
                console.error('无法解析变量值', value);
              }
            }
            if (this.position == 'tableHeader') {
              if (meta.selectedRows && meta.selectedRows.length > 0) {
                // 有选中行的情况下，根据选中行数据的字段匹配判断条件
                for (let rowIndex = 0; rowIndex < meta.selectedRows.length; rowIndex++) {
                  let row = meta.selectedRows[rowIndex];
                  visible = expressionCompare({ ...this.vPageState, ...row, ...this.visibleJudgementData }, code, operator, value)
                    ? visible
                    : !btn.defaultVisible;
                  if (!visible) {
                    break;
                  }
                }
              } else {
                visible = expressionCompare(_compareData, code, operator, value) ? visible : !visible;
              }
            } else {
              visible = expressionCompare(_compareData, code, operator, value) ? visible : !visible;
            }
          } else if (btn.defaultVisibleVar.match != undefined && btn.defaultVisibleVar.conditions != undefined) {
            let multiMatch = compareData => {
              // 多组条件判断
              let match = btn.defaultVisibleVar.match == 'all';
              for (let i = 0, len = btn.defaultVisibleVar.conditions.length; i < len; i++) {
                let { code, operator, value, valueType } = btn.defaultVisibleVar.conditions[i];
                if (valueType == 'variable') {
                  try {
                    value = get(compareData, value);
                  } catch (error) {
                    console.error('无法解析变量值', value);
                  }
                }
                let result = expressionCompare(compareData, code, operator, value);
                if (btn.defaultVisibleVar.match == 'all' && !result) {
                  match = false;
                  break;
                }
                if (btn.defaultVisibleVar.match == 'any' && result) {
                  match = true;
                  break;
                }
              }
              return match;
            };

            if (this.position == 'tableHeader') {
              if (meta.selectedRows && meta.selectedRows.length > 0) {
                // 有选中行的情况下，根据选中行数据的字段匹配判断条件
                for (let rowIndex = 0; rowIndex < meta.selectedRows.length; rowIndex++) {
                  let row = meta.selectedRows[rowIndex];
                  visible = multiMatch({ ...this.vPageState, ...row, ...this.visibleJudgementData }) ? visible : !btn.defaultVisible;
                  if (!visible) {
                    break;
                  }
                }
              } else {
                visible = multiMatch(_compareData) ? visible : !visible;
              }
            } else {
              visible = multiMatch(_compareData) ? visible : !visible;
            }
          }
        }

        if (visible || visible == undefined) {
          visibleButtons.push(btn);
          visibleBtnIds.push(btn.id);
        }

        if (btn.style && btn.style.type == 'switch') {
          // 开关按钮的选中根据条件判断
          let { operator, code, value } = btn.switch.checkedCondition;
          if (code) {
            this.$set(
              btn.switch,
              'checked',
              expressionCompare({ ...this.vPageState, ...meta, ...this.visibleJudgementData }, code, operator, value)
            );
          }
        }
      }
      if (buttonGroup.type === 'notGroup') {
        // 不分组
        return visibleButtons;
      } else if (buttonGroup.type === 'dynamicGroup') {
        // 动态分组
        let threshold = buttonGroup.dynamicGroupBtnThreshold;
        if (threshold != undefined && threshold > 0 && visibleButtons.length > threshold) {
          let noGroupButtons = visibleButtons.splice(0, threshold);
          return noGroupButtons.concat([
            { title: this.$t('dynamicGroupName', buttonGroup.dynamicGroupName), children: visibleButtons, style: buttonGroup.style || {} }
          ]);
        }
        return visibleButtons;
      } else {
        // 固定分组
        // let fixedGroupButtons = [];
        let btnAddedGroup = [];
        for (let i = 0, len = buttonGroup.groups.length; i < len; i++) {
          let groupButton = {
            title: this.$t(buttonGroup.groups[i].id, buttonGroup.groups[i].name || buttonGroup.groups[i].title),
            children: [],
            type: buttonGroup.groups[i].type,
            style: buttonGroup.groups[i].style || {}
          };
          for (let j = 0, jlen = buttonGroup.groups[i].buttonIds.length; j < jlen; j++) {
            let btn = this.buttonMap[buttonGroup.groups[i].buttonIds[j]];
            if (btn && visibleBtnIds.includes(btn.id)) {
              groupButton.children.push(btn);
              btnAddedGroup.push(btn.id);
            }
          }
          if (groupButton.children.length) {
            // 分组按钮放在第一个加入分组的按钮位置
            for (let i = 0; i < visibleButtons.length; i++) {
              if (btnAddedGroup.includes(visibleButtons[i].id)) {
                if (groupButton) {
                  visibleButtons.splice(i--, 1, groupButton);
                  groupButton = null;
                } else {
                  visibleButtons.splice(i--, 1);
                }
              }
            }
            // fixedGroupButtons.push(groupButton);
          }
        }
        // for (let i = 0; i < visibleButtons.length; i++) {
        //   if (btnAddedGroup.includes(visibleButtons[i].id)) {
        //     visibleButtons.splice(i--, 1);
        //   }
        // }

        return visibleButtons; // visibleButtons.concat(fixedGroupButtons);
      }
    }
  },
  created() {
    // let i18nMessages = {};
    // for (let i = 0, len = this.buttonConf.buttons.length; i < len; i++) {
    //   let btn = this.buttonConf.buttons[i];
    //   if (btn.i18n != undefined) {
    //     Object.assign(i18nMessages, btn.i18n);
    //   }
    // }
    // if (this.buttonConf.buttonGroup.i18n) {
    //   Object.assign(i18nMessages, this.buttonConf.buttonGroup.i18n);
    // }
    // if (this.buttonConf.buttonGroup.groups.length) {
    //   for (let g of this.buttonConf.buttonGroup.groups) {
    //     if (g.i18n) {
    //       Object.assign(i18nMessages, g.i18n);
    //     }
    //   }
    // }
    // if (Object.keys(i18nMessages).length) {
    //   console.log(i18nMessages);
    //   for (let lang in i18nMessages) {
    //     this.$i18n.mergeLocaleMessage(lang, {
    //       Widget: {
    //         [this.widgetContext.widget.id]: i18nMessages[lang]
    //       }
    //     });
    //   }
    // }
  },
  methods: {
    iconSize(button) {
      return { small: 18, default: 20, large: 24 }[button.style.size || this.size];
    },
    $t() {
      let meta = this.meta;
      if (typeof this.meta == 'function') {
        meta = this.meta();
      }
      if (meta && meta.targetPosition === 'widgetModal' && meta.dmsId) {
        // 数据模型派发到弹窗组件的按钮
        let key = 'Widget.' + meta.dmsId + '.' + arguments[0];
        let value = this.$i18n.t(key);
        if (value == key) {
          return arguments[1];
        }
        return value;
      }
      if (typeof this.translateKeyWrapper == 'function') {
        arguments[0] = this.translateKeyWrapper(arguments[0]);
      }
      if (this.widgetContext != undefined) {
        return this.widgetContext.$t(...arguments);
      }

      return this.$i18n.t(...arguments);
    },
    /**
     * 执行事件设置的配置
     * @param {事件设置} eventHandler
     */
    dispatchEventHandler(button, eventHandler, evt) {
      let _this = this;
      let _parent = this.parentWidget != undefined ? this.parentWidget : undefined;
      if (typeof this.parentWidget === 'function') {
        _parent = this.parentWidget();
      }
      eventHandler.pageContext = this.pageContext;
      eventHandler.$evtWidget = this;
      eventHandler.key = button.id;
      if (this.eventWidget != undefined) {
        if (typeof this.eventWidget === 'function') {
          eventHandler.$evtWidget = this.eventWidget(_parent);
        } else {
          eventHandler.$evtWidget = this.eventWidget;
        }
      }
      let meta = this.meta;
      if (typeof this.meta == 'function') {
        meta = this.meta();
      }

      // 元数据通过事件传递
      eventHandler.meta = meta;
      eventHandler.$evt = evt;
      let developJs = typeof this.developJsInstance === 'function' ? this.developJsInstance() : this.developJsInstance;
      if (developJs == undefined) {
        developJs = {};
      }
      if (this.$pageJsInstance != undefined) {
        developJs[this.$pageJsInstance._JS_META_] = this.$pageJsInstance;
      }
      if (meta != undefined && meta.$developJsInstance) {
        for (let key in meta.$developJsInstance) {
          developJs[key] = meta.$developJsInstance[key];
        }
      }
      eventHandler.$developJsInstance = developJs;

      // 动态解析当前父级布局ID
      if (eventHandler.targetPosition == 'widgetLayout' && eventHandler.containerWid == undefined) {
        let containerWid = undefined;
        if (this.parentLayContentId != undefined) {
          if (typeof this.parentLayContentId == 'function') {
            containerWid = this.parentLayContentId();
          } else {
            containerWid = this.parentLayContentId;
          }
        }
        if (containerWid) {
          eventHandler.containerWid = containerWid;
        } else {
          // 如果找不到当前父级布局，则自动切换为新窗口打开
          eventHandler.targetPosition = 'newWindow';
        }
      }

      if (button.style.type === 'switch') {
        evt.checked = !button.switch.checked;
        // 开关按钮需要有loading效果
        eventHandler.before = () => {
          _this.loading = true;
        };
        eventHandler.after = success => {
          _this.loading = false;
          if (typeof success === 'boolean' && success) {
            // 开关执行成功回调通知变更状态
            button.switch.checked = !button.switch.checked;
          }
        };
      }
      if (
        eventHandler.actionType == 'workflow' &&
        eventHandler.workflowIdSource == 'selectedRowColumnValue' &&
        eventHandler.workflowIdColumn
      ) {
        // 解析选中行对应的流程ID
        if (meta && meta[eventHandler.workflowIdColumn]) {
          eventHandler.workflowId = meta[eventHandler.workflowIdColumn];
        } else if (this.widgetContext.selectedRows && this.widgetContext.selectedRows.length > 0) {
          eventHandler.workflowId = this.widgetContext.selectedRows[0][eventHandler.workflowIdColumn];
        }
      }
      if (eventHandler.actionType == 'workflow' && eventHandler.workflowId == undefined) {
        this.$message.error(this.$t('WidgetTable.message.errorWorkflowIdExplain', '解析流程错误, 请确认数据的流程配置正确性'));
        return;
      }

      if (eventHandler.actionType) {
        if (button.behaviorLogConfig && button.behaviorLogConfig.enable) {
          let calculateDataSource = {
            BUTTON_META_DATA: meta,
            ...(this.widgetContext != undefined ? this.widgetContext.widgetDependentVariableDataSource() : {})
          };
          this._logger.commitBehaviorLog({
            type: 'click',
            element: {
              tag: 'BUTTON',
              text: button.title,
              id: button.id
            },
            page: {
              url: window.location.href,
              title: this.vPage != undefined ? this.vPage.title || document.title : undefined,
              id: this.vPage != undefined ? this.vPage.pageId || this.vPage.pageUuid : undefined
            },
            businessCode:
              button.behaviorLogConfig.businessCode && button.behaviorLogConfig.businessCode.value
                ? executeJSFormula(button.behaviorLogConfig.businessCode.value, calculateDataSource, this.jsFormulaFunctions)
                : undefined,
            description:
              button.behaviorLogConfig.description && button.behaviorLogConfig.description.value
                ? executeJSFormula(button.behaviorLogConfig.description.value, calculateDataSource, this.jsFormulaFunctions)
                : undefined,
            extraInfo:
              button.behaviorLogConfig.extraInfo && button.behaviorLogConfig.extraInfo.value
                ? executeJSFormula(button.behaviorLogConfig.extraInfo.value, calculateDataSource, this.jsFormulaFunctions)
                : undefined
          });
        }
        this.tryFillDataInDyformOpened(eventHandler);
        new DispatchEvent(eventHandler).dispatch();
      }
      // 阻止事件冒泡被行点击捕获
      if (evt.stopPropagation) {
        evt.stopPropagation();
      } else if (evt.domEvent && evt.domEvent.stopPropagation) {
        evt.domEvent.stopPropagation();
      }
    },
    tryFillDataInDyformOpened(eventHandler) {
      if (
        ((eventHandler.actionType == 'workflow' && eventHandler.sendToApprove !== true) ||
          (eventHandler.actionType == 'dataManager' && eventHandler.action == 'BasicWidgetDyformDevelopment.create')) &&
        ((this.widgetContext.widget.wtype == 'WidgetTable' && this.widgetContext.selectedRows.length > 0) ||
          this.widgetContext.widget.wtype == 'WidgetDyformSetting') &&
        eventHandler.extraOptions &&
        eventHandler.extraOptions.enableFillFormDataBySelectedRow
      ) {
        eventHandler.broadcastChannel = generateId(6);
        const channel = new BroadcastChannel(eventHandler.broadcastChannel);
        const row = this.widgetContext.widget.wtype == 'WidgetTable' ? cloneDeep(this.widgetContext.selectedRows[0]) : {};
        channel.onmessage = evt => {
          // 新增表单数据填充
          if (evt.data.message == 'setFormDefaultValueDone' && (!evt.data.dataUuid || evt.data.isNewFormData)) {
            channel.postMessage({
              message: 'loading'
            });
            let defaultFormData = evt.data.defaultFormData;
            let {
              dyformSourceType,
              fillFieldMapping,
              fillRuleType,
              fillSubformRuleType,
              sourceDyformDataUuidColumn,
              sourceFormUuid,
              targetFormUuid,
              sourceDyformUuidColumn,
              sourceTaskInstUuidColumn,
              ignoreTargetFieldDefaultValue,
              enableFillSubformData,
              enableFillMainFormData,
              filleSubformFieldMapping
            } = eventHandler.extraOptions;
            if (this.widgetContext.widget.wtype == 'WidgetDyformSetting') {
              sourceDyformDataUuidColumn = 'uuid';
            }
            let defaultFieldKeys = [];
            if (!ignoreTargetFieldDefaultValue && defaultFormData) {
              for (let k in defaultFormData) {
                if (defaultFormData[k] !== '') {
                  defaultFieldKeys.push(k);
                }
              }
            }
            let fillData = (formUuid, dataUuid) => {
              if (formUuid && dataUuid) {
                this.fetchDyformData(formUuid, dataUuid, enableFillSubformData === true).then(formDatas => {
                  if (formDatas[formUuid] && formDatas[formUuid].length == 1) {
                    if (!enableFillMainFormData) {
                      delete formDatas[formUuid];
                    }

                    let fetchAllSubformDatas = [],
                      nestformKeys = [];
                    for (let key in formDatas) {
                      if (key !== formUuid && formDatas[key].length > 0) {
                        fetchAllSubformDatas.push(
                          new Promise((resolve, reject) => {
                            let subs = [];
                            for (let r of formDatas[key]) {
                              subs.push(
                                new Promise((resolve, reject) => {
                                  this.fetchDyformData(key, r.uuid, true).then(subformDatas => {
                                    delete subformDatas[key];
                                    // 合并
                                    for (let k in subformDatas) {
                                      if (formDatas[k] == undefined) {
                                        formDatas[k] = [];
                                      }
                                      formDatas[k].push(...subformDatas[k]);
                                    }
                                    nestformKeys.push(...Object.keys(subformDatas));
                                    r.nestformDatas = {
                                      formDatas: subformDatas
                                    };
                                    resolve();
                                  });
                                })
                              );
                            }
                            Promise.all(subs).then(() => {
                              resolve();
                            });
                          })
                        );
                      }
                    }
                    Promise.all(fetchAllSubformDatas).then(() => {
                      for (let key in formDatas) {
                        let mappingOption =
                            key == formUuid
                              ? eventHandler.extraOptions.fillFieldMapping
                              : eventHandler.extraOptions.filleSubformFieldMapping,
                          ruleType = key == formUuid ? fillRuleType : fillSubformRuleType;
                        if (key !== formUuid && ruleType == 'fillDataByFieldMapping') {
                          // 过滤出从表配置
                          mappingOption = mappingOption.filter(mapping => mapping.sourceFormUuid == key);
                          if (mappingOption.length == 0 && !nestformKeys.includes(key)) {
                            delete formDatas[key];
                            continue;
                          }
                        }

                        for (let i = 0, len = formDatas[key].length; i < len; i++) {
                          if (ruleType == 'fillDataByFieldMapping' && !nestformKeys.includes(key)) {
                            let postFormData = {};
                            if (formDatas[key][i].nestformDatas) {
                              postFormData.nestformDatas = formDatas[key][i].nestformDatas;
                            }
                            if (mappingOption && mappingOption.length > 0) {
                              mappingOption.forEach(mapping => {
                                if (mapping.sourceField && mapping.targetField && !defaultFieldKeys.includes(mapping.targetField)) {
                                  let value = mapping.sourceField.startsWith('ROW.')
                                    ? row[mapping.sourceField.replace('ROW.', '')]
                                    : formDatas[key][i][mapping.sourceField];
                                  if (value != undefined && value !== '') {
                                    postFormData[mapping.targetField] = value;
                                  }
                                }
                              });
                            }
                            formDatas[key][i] = postFormData;
                          } else if (ruleType == 'fillDataBySameCode' || nestformKeys.includes(key)) {
                            [
                              'uuid',
                              'create_time',
                              'modify_time',
                              'creator',
                              'modifier',
                              'tenant',
                              'system',
                              'rec_ver',
                              'status',
                              'form_uuid'
                            ]
                              .concat(defaultFieldKeys)
                              .forEach(prop => {
                                delete formDatas[key][i][prop];
                              });
                          }
                        }

                        if (ruleType == 'fillDataByFieldMapping' && key == formUuid) {
                          // 主表转目标表单
                          formDatas[targetFormUuid] = formDatas[key];
                        }
                        if (key !== formUuid) {
                          // 从表转目标从表
                          formDatas[mappingOption.targetFormUuid] = formDatas[key];
                        }
                      }

                      let allPromises = [];
                      // 处理附件字段
                      for (let key in formDatas) {
                        allPromises.push(this.setFormDataAttachFieldValue(formDatas[key]));
                      }
                      console.log('复制表单数据', formDatas);
                      Promise.all(allPromises).then(() => {
                        channel.postMessage({
                          message: 'setFormData',
                          formDatas
                        });
                      });
                    });
                  }
                });
              }
            };
            if (dyformSourceType == 'sourceDyformConstant') {
              if (sourceFormUuid && sourceDyformDataUuidColumn) {
                fillData(
                  sourceFormUuid,
                  this.widgetContext.widget.wtype == 'WidgetDyformSetting'
                    ? eventHandler.meta.$dyform.dataUuid
                    : row[sourceDyformDataUuidColumn]
                );
              }
            } else if (dyformSourceType == 'sourceDyformFromColumn') {
              fillData(row[sourceDyformUuidColumn], row[sourceDyformDataUuidColumn]);
            } else if (dyformSourceType == 'sourceDyformFromTaskInst') {
              this.fetchDyformDataByWorkflow(row[sourceTaskInstUuidColumn]).then(({ dataUuid, formUuid }) => {
                fillData(formUuid, dataUuid);
              });
            }
          }
        };
      }
    },
    setFormDataAttachFieldValue(formDatas) {
      let rowPromises = [];
      for (let row of formDatas) {
        if (Object.keys(row).length > 0) {
          for (let key in row) {
            if (Array.isArray(row[key]) && row[key].length > 0 && row[key][0].fileID) {
              let files = row[key],
                promiseCopyFiles = [];
              files.forEach(f => {
                promiseCopyFiles.push(
                  new Promise((resolve, reject) => {
                    $axios
                      .post('/json/data/services', {
                        serviceName: 'mongoFileService',
                        methodName: 'copyFileAndRename',
                        args: JSON.stringify([f.fileID, f.fileName])
                      })
                      .then(({ data }) => {
                        if (data.data) {
                          resolve({ key, file: data.data });
                        }
                      });
                  })
                );
              });
              rowPromises.push(
                new Promise((resolve, reject) => {
                  Promise.all(promiseCopyFiles).then(files => {
                    row[files[0].key] = [];
                    files.forEach(item => {
                      row[item.key].push(item.file.logicFileInfo);
                    });
                    resolve();
                  });
                })
              );
            }
          }
        }
      }
      return Promise.all(rowPromises);
    },
    resolvePopconfirmTitle(_title, translateCode, confirmConfig) {
      try {
        let meta = this.meta;
        if (typeof this.meta == 'function') {
          meta = this.meta();
        }
        let title = _title,
          key = `Widget.${translateCode}`; //`Widget.${this.widgetContext.widget.id}.${translateCode}`;
        // 通过 t 函数获取 message （避免message 里面的变量占位符被i18n默认解析掉）
        if (this.widgetContext) {
          title = this.$i18n.t(key);
        } else {
          title = this.$i18n.t(`${translateCode}`);
        }
        let compiler = stringTemplate(title == key || title == translateCode ? _title : title);
        return compiler(meta || {});
      } catch (error) {}
      return _title;
    },

    onTrigger(evt, trigger, button, hiddenDropdown = false) {
      if (this.designMode && this.designModeIsDisabled) {
        return;
      }
      let _this = this;
      let dispatch = (button, eventHandler, evt) => {
        if (button.enableUnselectRowTip && button.unselectRowTip) {
          let meta = this.meta;
          if (typeof this.meta == 'function') {
            meta = this.meta();
          }
          if (meta && meta.selectedRowKeys && meta.selectedRowKeys.length == 0) {
            this.$message.info(this.$t(button.id + '_unselectRowTip', button.unselectRowTip));
            return;
          }
        }

        if (button.confirmConfig && button.confirmConfig.enable && button.confirmConfig.popType == 'confirm') {
          let _title = this.resolvePopconfirmTitle(button.confirmConfig.title, button.id + '_popconfirmTitle', button.confirmConfig),
            _content = this.resolvePopconfirmTitle(button.confirmConfig.content, button.id + '_popconfirmContent', button.confirmConfig);
          let confirmConfig = {
            title: _title,
            content: _content,
            onOk() {
              if (eventHandler) {
                _this.dispatchEventHandler(button, eventHandler, evt);
              }
              _this.$emit('button-' + trigger, button);
            },
            onCancel() {}
          };
          if (button.confirmConfig.okText) {
            confirmConfig.okText = _this.$t('Widget.' + button.id + '_popconfirmOkText', button.confirmConfig.okText);
          }
          if (button.confirmConfig.cancelText) {
            confirmConfig.cancelText = _this.$t('Widget.' + button.id + '_popconfirmCancelText', button.confirmConfig.cancelText);
          }
          this.$confirm(confirmConfig);
        } else if (eventHandler) {
          _this.dispatchEventHandler(button, eventHandler, evt);
        }
      };

      // 双击发了3个事件。两次点击事件，最后一次是dblclick
      if (button.hasOwnProperty('eventHandler')) {
        if (Array.isArray(button.eventHandler)) {
          for (let i = 0, len = button.eventHandler.length; i < len; i++) {
            if (button.eventHandler[i].trigger === trigger) {
              dispatch(button, cloneDeep(button.eventHandler[i]), evt);
            }
          }
        } else {
          if (button.eventHandler.trigger === trigger) {
            dispatch(button, cloneDeep(button.eventHandler), evt);
          }
        }
      } else {
        // 存在类似从表导出导入等按钮，无eventHandler且有确认框的情况
        dispatch(button, undefined, evt);
      }

      if (!(button.confirmConfig && button.confirmConfig.enable && button.confirmConfig.popType == 'confirm')) {
        // 不存在确认框，向上触发按钮事件触发类型
        this.$emit('button-' + trigger, button);
      }

      if (evt.stopPropagation) {
        evt.stopPropagation();
      } else if (evt.domEvent != undefined) {
        evt.domEvent.stopPropagation();
      }
    },
    fetchDyformData(formUuid, dataUuid, includeSubform = false) {
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'dyFormFacade',
            methodName: includeSubform ? 'getFullFormData' : 'getFormDataOfMainform',
            args: JSON.stringify([formUuid, dataUuid])
          })
          .then(({ data }) => {
            resolve(includeSubform ? data.data : { [formUuid]: [data.data] });
          });
      });
    },
    fetchDyformDataByWorkflow(taskInstUuid) {
      return new Promise((resolve, reject) => {
        $axios
          .post('/json/data/services', {
            serviceName: 'flowService',
            methodName: 'getFormUuidAndDataUuidByTaskInstUuid',
            args: JSON.stringify([taskInstUuid])
          })
          .then(({ data }) => {
            if (data.data) {
              resolve({
                formUuid: data.data.formUuid,
                dataUuid: data.data.dataUuid
              });
            } else {
              resolve({});
            }
          });
      });
    }
  },

  mounted() {
    this.$emit('buttonMounted', this.$el);
  },
  updated() {
    this.$emit('buttonUpdated', this.$el);
  }
};
</script>

<style lang="less">
.ant-space-item {
  .widget-table-buttons-dropdown {
    display: none;
  }
}
</style>
