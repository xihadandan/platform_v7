<template>
  <a-space class="workflow-toolbar">
    <div v-if="multiSubmitUsers.length > 1 || multiSubmitTipVisible" class="mulit-submit">
      <a-popover v-model="multiSubmitTipVisible" trigger="manual" placement="bottomRight" :getPopupContainer="() => $el">
        <template slot="content">
          <span v-html="multiSubmitTipContent"></span>
          <a-icon type="close" @click="() => (multiSubmitTipVisible = false)" />
        </template>
        <a-badge :count="multiSubmitUsers.length" class="multi-user-badge flex">
          <a-avatar
            v-for="user in multiSubmitUsers"
            :key="user.userId"
            icon="user"
            :title="user.userName"
            :src="user.photoUuid ? '/proxy/org/user/view/photo/' + user.photoUuid : undefined"
            class="multi-user-avatar"
          ></a-avatar>
        </a-badge>
      </a-popover>
    </div>
    <template v-if="inited">
      <WorkflowActionPopover
        v-for="action in actions"
        :key="action.id"
        :workFlow="workView.workFlow"
        :action="action"
        :showActionTip="showActionTip"
        :ref="action.id"
      >
        <a-popconfirm
          v-if="action.confirmConfig && action.confirmConfig.enable && action.confirmConfig.popType == 'popconfirm'"
          :title="resolvePopconfirmTitle(action.confirmConfig.title, action)"
          :ok-text="getPopconfirmI18nWord(action.confirmConfig, action.code + '_popconfirmOkText', action.confirmConfig.okText)"
          :cancel-text="getPopconfirmI18nWord(action.confirmConfig, action.code + '_popconfirmCancelText', action.confirmConfig.cancelText)"
          @confirm="onActionClick($event, action)"
        >
          <a-button
            v-if="!action.children && action.visible"
            ghost
            :code="action.id"
            :disabled="disabled"
            :type="action.type"
            @click="e => {}"
          >
            <Icon v-if="action.icon" :type="action.icon" />
            {{ action.textHidden ? '' : action.text }}
          </a-button>
        </a-popconfirm>
        <template v-else>
          <a-button
            v-if="!action.children && action.visible"
            :code="action.id"
            :disabled="disabled"
            :type="action.type"
            ghost
            @click="onActionClick($event, action)"
          >
            <Icon v-if="action.icon" :type="action.icon" />
            {{ action.textHidden ? '' : action.text }}
          </a-button>
        </template>
        <a-dropdown v-if="action.children && action.children.length" :disabled="disabled">
          <a-button ghost>
            <Icon v-if="action.icon" :type="action.icon" />
            {{ action.text }}
            <a-icon type="down" />
          </a-button>
          <a-menu slot="overlay">
            <template v-for="action in action.children">
              <a-menu-item v-if="action.visible" :key="action.id" :code="action.id" @click="onActionClick($event, action)">
                <WorkflowActionPopover
                  :workFlow="workView.workFlow"
                  :action="action"
                  :ref="action.id"
                  :showActionTip="showActionTip"
                  placement="leftTop"
                >
                  <a-popconfirm
                    v-if="action.confirmConfig && action.confirmConfig.enable && action.confirmConfig.popType == 'popconfirm'"
                    placement="left"
                    :title="resolvePopconfirmTitle(action.confirmConfig.title, action)"
                    :ok-text="getPopconfirmI18nWord(action.confirmConfig, action.code + '_popconfirmOkText', action.confirmConfig.okText)"
                    :cancel-text="
                      getPopconfirmI18nWord(action.confirmConfig, action.code + '_popconfirmCancelText', action.confirmConfig.cancelText)
                    "
                    @confirm="onActionClick($event, action)"
                  >
                    <label @click.stop="() => {}">
                      <Icon v-if="action.icon" :type="action.icon" />
                      {{ action.textHidden ? '' : action.text }}
                    </label>
                  </a-popconfirm>
                  <template v-else>
                    <label>
                      <Icon v-if="action.icon" :type="action.icon" />
                      {{ action.textHidden ? '' : action.text }}
                    </label>
                  </template>
                </WorkflowActionPopover>
              </a-menu-item>
            </template>
          </a-menu>
        </a-dropdown>
      </WorkflowActionPopover>
    </template>
  </a-space>
</template>
<script>
import io from 'socket.io-client';
import { DispatchEvent } from '@framework/vue/lib/dispatchEvent';
import { deepClone } from '@framework/vue/utils/util';
import { isEmpty, template as stringTemplate, debounce, merge } from 'lodash';
export default {
  name: 'WorkflowToolbar',
  inject: ['pageContext'],
  props: {
    workView: Object
  },
  data() {
    const _this = this;
    let settings = _this.workView.options.settings || {};
    let actionSetting = settings.ACTION || {};
    let buttonMap = {};
    let buttons = actionSetting.buttons || [];
    buttons.forEach(button => {
      buttonMap[button.code] = button;
      // 多状态按钮
      if (button.multistate && button.states) {
        button.states.forEach(state => {
          if (state.code != button.code) {
            let stateButton = deepClone(button);
            stateButton.id = state.id;
            stateButton.title = state.title;
            stateButton.code = state.code;
            stateButton.defaultVisible = state.defaultVisible;
            buttonMap[stateButton.code] = stateButton;
          }
        });
      }
    });
    return {
      showActionTip: settings.GENERAL && settings.GENERAL.showActionTip,
      multiSubmitUsers: [],
      multiSubmitTipVisible: false,
      multiSubmitTipContent: '',
      actionSetting,
      inited: false,
      buttonMap,
      actions: []
    };
  },
  computed: {
    disabled() {
      return (this.workView.$widget && this.workView.$widget.displayState == 'disable') || this.workView.loading.visible;
    }
  },
  created() {
    const _this = this;
    if (!EASY_ENV_IS_BROWSER) {
      return;
    }
    this.actions = this.getActions();
    this.pageContext.handleEvent('form-element:mounted', $widget => {
      if ($widget.calculateVisibleByCondition && !this.calculateVisibleByCondition) {
        this.calculateVisibleByCondition = $widget.calculateVisibleByCondition;
        // this.updateActions();
      }
    });
    this.pageContext.handleEvent(
      'formDataChanged',
      debounce(function (args) {
        const { wDyform } = args;
        console.log(wDyform);
        if (_this.calculateVisibleByCondition) {
          _this.updateActions();
        }
      }, 250)
    );
  },
  mounted() {
    const _this = this;
    _this.$nextTick(() => {
      _this.inited = true;
      _this.$emit('created');
    });
    _this.loadDyformSocketIO();
  },
  methods: {
    updateActions() {
      let allPromise = [],
        actionList = [];
      const getAllPromise = actions => {
        for (let index = 0; index < actions.length; index++) {
          const action = actions[index];
          if (action.children) {
            getAllPromise(action.children);
          } else {
            if (!action.configuration) {
              action.configuration = {};
            }
            actionList.push(action);
            allPromise.push(this.calculateVisibleByCondition(action.configuration, true));
          }
        }
      };

      getAllPromise(this.actions);
      if (allPromise.length) {
        Promise.all(allPromise).then(visibles => {
          visibles.map((visible, index) => {
            actionList[index]['visible'] = visible;
          });
        });
      }
    },
    getActions() {
      const _this = this;
      let retActions = [];
      let allActions = EASY_ENV_IS_NODE ? [] : _this.workView.getActions();
      let group = _this.actionSetting.group || {};
      // 按钮样式、可见性处理
      allActions.forEach(action => {
        action.visible = true;
        let button = _this.buttonMap[action.code];
        if (!button) {
          if (action.uuid) {
            let i18nText = this.$t('WorkflowView.' + action.uuid, undefined);
            if (i18nText) {
              action.text = i18nText;
            }
          }
          return;
        }

        // 多状态按钮
        let multistate = button.multistate;
        if (multistate) {
          button = _this.getStateButton(action.code, button.states);
          if (button) {
            action.multistate = multistate;
            action.initId = action.id;
            action.initCode = action.code;
            action.id = button.code;
            action.code = button.code;
            action.name = button.title;
            action.text = button.title;
            action.eventHandler = button.eventHandler;
          } else {
            action.visible = false;
            return;
          }
        } else {
          // 按钮可见性处理
          let visible = button.defaultVisible;
          if (button.defaultVisibleVar && button.defaultVisibleVar.enable) {
            if (button.defaultVisibleVar.customScript) {
              let anonymousFunction = new Function(['workData', 'workView'], button.defaultVisibleVar.customScript);
              let result = anonymousFunction.apply(_this, [_this.workView.getWorkData(), _this.workView]);
              if (result && result.then) {
                result.then(visibleResult => {
                  action.visible = visibleResult ? button.defaultVisible : !visible;
                });
                visible = true;
              } else {
                visible = result ? visible : !visible;
              }
            }
          }
          action.visible = visible;
        }

        // 按钮样式处理
        if (button && button.style) {
          action.type = action.type || button.style.type;
          action.icon = action.icon || button.style.icon;
          action.textHidden = button.style.textHidden;
        }
        // 按钮弹框
        if (button && button.confirmConfig) {
          action.confirmConfig = button.confirmConfig;
        }
        // 事件处理
        if (button && button.eventHandler && isEmpty(action.eventHandler)) {
          action.eventHandler = button.eventHandler;
        }

        if (action.uuid) {
          let _text = this.$t('WorkflowView.' + action.uuid, undefined);
          if (!_text && action.uuid.indexOf('.useAsButtonName') > -1) {
            if (this.$i18n.locale === 'zh_CN') {
              _text = action.text;
            } else {
              let _actionUuid = action.uuid;
              _actionUuid = _actionUuid.replace('useAsButtonName', 'directionName');
              _text = this.$t('WorkflowView.' + _actionUuid, undefined);
            }
          }
          if (_text) {
            action.originalText = action.text;
            action.text = _text;
          } else if (action.text == button.title || action.code == button.code) {
            _text = this.$t('WorkflowView.global.' + button.code, undefined);
            if (_text) {
              action.originalText = action.text;
              action.text = _text;
            }
          }
        } else {
          if (action.code == button.code) {
            action.text = this.$t('WorkflowView.global.' + button.code, action.text);
          }
        }
      });
      allActions = allActions.filter(action => action.visible);

      // 不分组
      if (group.type == 'notGroup') {
        retActions = [...allActions];
      } else if (group.type == 'fixedGroup') {
        // 固定分组
        let groups = group.groups;
        for (let i = 0; i < allActions.length; i++) {
          let action = allActions[i];
          let actionInGroup = false;
          groups.forEach(group => {
            if (!group.children) {
              group.children = [];
            }
            group.text = group.name;
            if (group.id) {
              group.text = this.$t('WorkflowView.global.' + group.id, group.name);
            }
            if (group.buttonCodes && group.buttonCodes.includes(action.code)) {
              group.children.push(action);
              actionInGroup = true;
            }
          });
          if (actionInGroup) {
            allActions.splice(i, 1);
            i--;
          } else {
            retActions.push(action);
          }
        }
        retActions = [...retActions, ...groups];
      } else {
        // 动态分组
        let dynamicGroupBtnThreshold = group.dynamicGroupBtnThreshold || 3;
        let dynamicGroupName = this.$t('WorkflowView.global.dynamicGroupName', undefined) || this.$t('WorkflowWork.operation.More', '更多');
        if (allActions.length <= dynamicGroupBtnThreshold) {
          retActions = allActions;
        } else {
          let newMoreActions = [];
          for (let i = 0; i < allActions.length; i++) {
            let action = allActions[i];
            if (i < dynamicGroupBtnThreshold) {
              retActions.push(action);
            } else {
              newMoreActions.push(action);
            }
          }
          if (newMoreActions.length) {
            retActions.push({
              text: dynamicGroupName,
              id: 'more',
              icon: 'ant-iconfont more',
              children: newMoreActions
            });
          }
        }
      }
      return retActions;
    },
    getStateButton(actionCode, states) {
      // 状态按钮条件过滤
      let visibleStates = states.filter(button => {
        let visible = button.defaultVisible;
        if (button.defaultVisibleVar && button.defaultVisibleVar.enable) {
          if (button.defaultVisibleVar.customScript) {
            let anonymousFunction = new Function(['workData', 'workView'], button.defaultVisibleVar.customScript);
            let result = anonymousFunction.apply(_this, [_this.workView.getWorkData(), _this.workView]);
            visible = result ? visible : !visible;
          }
        }
        return visible;
      });
      let button = visibleStates.find(state => state.code == actionCode);
      if (!button && visibleStates.length) {
        button = visibleStates[0];
      }
      return button;
    },
    resolvePopconfirmTitle(title, action, code) {
      try {
        let compiler = stringTemplate(this.getPopconfirmI18nWord(action.confirmConfig, code, title));
        return compiler({ workData: this.workView.getWorkData(), action });
      } catch (error) {
        console.error(error);
      }
      return title;
    },
    getPopconfirmI18nWord(confirmConfig, code, defaultValue) {
      if (confirmConfig && confirmConfig.i18n && confirmConfig.i18n[this.$i18n.locale] && confirmConfig.i18n[this.$i18n.locale][code]) {
        return confirmConfig.i18n[this.$i18n.locale][code];
      }
      return defaultValue;
    },
    onActionClick: debounce(
      function (event, action) {
        const _this = this;
        _this.workView.setCurrentEvent(event);
        _this.workView.setCurrentAction(action);
        let methodName = action.optName || action.methodName;
        let commitLog = () => {
          _this._logger.commitBehaviorLog({
            type: 'click',
            element: {
              tag: 'BUTTON',
              text: action.name || action.text
            },
            page: {
              url: window.location.href,
              title: this.vPage != undefined ? this.vPage.title || document.title : undefined,
              id: this.vPage != undefined ? this.vPage.pageId || this.vPage.pageUuid : undefined
            },
            description: `${_this.workView.workFlow.workData.title} [${_this.workView.workFlow.workData.taskName}] : ${
              action.name || action.text
            }`
          });
        };
        let actionCallback = () => {
          commitLog();
          if (_this.workView[methodName]) {
            _this.workView[methodName].call(_this.workView, event, action);
          } else if (_this.workView.methods && _this.workView.methods[methodName]) {
            _this.workView.methods[methodName].call(_this.workView, event, action);
          } else if (!isEmpty(action.eventHandler)) {
            if (action.eventHandler.actionType === 'customJsModule' && action.eventHandler.customJsModule) {
              if (_this.$refs[action.id]) {
                _this.$refs[action.id][0].invokeDevelopmentMethod('actionPerformed', action);
              }
            } else {
              action.eventHandler.pageContext = _this.pageContext;
              action.eventHandler.$evtWidget = _this.workView.$widget;
              new DispatchEvent(action.eventHandler).dispatch();
            }
          } else {
            _this.workView.$widget.invokeDevelopmentMethod('performed', action);
            console.error(`Unknow action method [${methodName}]`, action);
          }
        };
        if (action.confirmConfig && action.confirmConfig.enable && action.confirmConfig.popType == 'confirm') {
          let title = _this.resolvePopconfirmTitle(action.confirmConfig.title, action, action.code + '_popconfirmTitle'),
            content = _this.resolvePopconfirmTitle(action.confirmConfig.content, action, action.code + '_popconfirmContent');
          _this.$confirm({
            title,
            content,
            okText: _this.getPopconfirmI18nWord(action.confirmConfig, action.code + '_popconfirmOkText', action.confirmConfig.okText),
            cancelText: _this.getPopconfirmI18nWord(
              action.confirmConfig,
              action.code + '_popconfirmCancelText',
              action.confirmConfig.cancelText
            ),
            onOk() {
              actionCallback();
            },
            onCancel() {}
          });
        } else if (methodName === 'viewFlowDesigner') {
          commitLog();
          this.pageContext.emitEvent('openViewerDrawer');
        } else {
          actionCallback();
        }
      },
      500,
      {
        leading: true,
        trailing: false
      }
    ),
    submit(event) {
      const _this = this;
      let actionCode = _this.workView.getSubmitActionCode();
      let submitAction = _this.workView.getActions().find(action => action.code == actionCode);
      if (submitAction) {
        _this.workView.currentEvent = null;
        _this.workView.currentAction = null;
        _this.workView.submit(event);
        // _this.onActionClick(event, submitAction);
      }
      // let btnElement = document.querySelector(".workflow-toolbar [code='" + actionCode + "'");
      // if (btnElement && btnElement.click) {
      //   btnElement.click();
      // }
    },
    loadDyformSocketIO() {
      const _this = this;
      let workData = _this.workView.getWorkData();
      if (!_this.workView.isTodo()) {
        return;
      }
      let multiSubmitType = workData.multiSubmitType;
      if (!multiSubmitType || multiSubmitType == 'isByOrder') {
        return;
      }
      let operationName = workData.canEditForm
        ? this.$t('WorkflowWork.opinionManager.operation.edit', '编辑')
        : this.$t('WorkflowWork.opinionManager.operation.viewData', '查阅');
      let _userid = null;
      let userId = _this._$USER.userId;
      let socket = io('/wellapp-dyform', {
        transports: ['websocket', 'polling'],
        query: {
          userId,
          dataUuid: workData.taskInstUuid
        }
      });
      socket.on('connect', () => {
        console.log(socket.id);
      });
      socket.on('socketid', res => {
        // _socketid = res.socketid;
        _userid = res.userid;
      });
      let notTip = false;
      socket.on('updateUserEditDyform', res => {
        let users = res.user || [];
        if (users.length > 1 && !notTip) {
          if (multiSubmitType === 'isAnyone') {
            _this.$notification.warning({
              message: _this.$t('WorkflowWork.message.MultiPersonProcessing', '多人办理'),
              description: _this.$t(
                'WorkflowWork.message.onlyOneToDoThisTaskCurrentMultiUserOperate',
                { userCount: users.length, operationName },
                `当前${users.length}人同时${operationName}，只需一个办理`
              ),
              duration: null,
              class: 'multi-submit',
              getContainer() {
                return _this.$el;
              }
            });
          } else if (multiSubmitType === 'isMultiSubmit') {
            _this.$notification.warning({
              message: _this.$t('WorkflowWork.message.MultiPersonProcessing', '多人办理'),
              description: _this.$t(
                'WorkflowWork.message.currentMultiUserOperate',
                { userCount: users.length, operationName },
                `当前${users.length}人同时${operationName}，只需一个办理`
              ),
              duration: null,
              class: 'multi-submit',
              getContainer() {
                return _this.$el;
              }
            });
          }
          notTip = true;
        } else {
          if (users.length > 1 && users.length > _this.multiSubmitUsers.length) {
            _this.multiSubmitTipVisible = true;
            _this.multiSubmitTipContent = _this.$t('WorkflowWork.message.NewHandlingPersonnel', '新的办理人员');
            // _this.$message.info('新的办理人员');
          }
        }
        _this.multiSubmitUsers = users;
        if (_this.$i18n && _this.$i18n.locale !== _this.$i18n.fallbackLocale) {
          _this.$clientCommonApi.getUserNamesByIds(users.map(user => user.userId)).then(userNames => {
            _this.multiSubmitUsers.forEach(user => {
              user.userName = userNames[user.userId] || user.userName;
            });
          });
        }
      });
      socket.on('dyformDataChanged', res => {
        if (res.userId != _userid) {
          if (res.tipType === 'alert') {
            let notify = (message, description) => {
              _this.$notification.warning({
                message,
                description,
                duration: null,
                class: 'multi-submit',
                getContainer() {
                  return _this.$el;
                }
              });
            };
            if (_this.$i18n.locale !== 'zh_CN') {
              _this.$translate(res.operationTip, 'zh', _this.$i18n.locale.split('_')[0]).then(text => {
                notify(
                  res.operationTip && res.operationTip.indexOf('撤回') != -1
                    ? _this.$t('WorkflowWork.message.notice', '通知')
                    : _this.$t('WorkflowWork.message.MultiPersonProcessing', '多人办理'),
                  text
                );
              });
            } else {
              notify(
                res.operationTip && res.operationTip.indexOf('撤回') != -1
                  ? _this.$t('WorkflowWork.message.notice', '通知')
                  : _this.$t('WorkflowWork.message.MultiPersonProcessing', '多人办理'),
                res.operationTip
              );
            }
            _this.workView.$widget.displayState = 'disable';
          } else {
            if (_this.$i18n.locale !== 'zh_CN') {
              _this.$translate(res.operationTip, 'zh', _this.$i18n.locale.split('_')[0]).then(text => {
                _this.multiSubmitTipVisible = true;
                _this.multiSubmitTipContent = text;
              });
            } else {
              _this.multiSubmitTipVisible = true;
              _this.multiSubmitTipContent = res.operationTip;
            }
          }
        }
      });
    }
  }
};
</script>
<style lang="less" scoped>
.workflow-toolbar {
  ::v-deep .ant-notification {
    right: 40% !important;
  }

  .multi-user-badge {
    ::v-deep .ant-badge-count {
      background-color: var(--w-warning-color);
    }
  }
  ::v-deep .ant-popover-inner-content {
    background-color: var(--w-warning-color-1);
    .user-name-label {
      color: var(--w-warning-color);
      margin-right: 3px;
    }
  }
  .multi-user-avatar {
    background-color: var(--w-primary-color);
  }
}
</style>
