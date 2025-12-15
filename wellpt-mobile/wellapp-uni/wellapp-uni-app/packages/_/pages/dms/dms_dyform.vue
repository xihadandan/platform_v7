<template>
  <view class="w-dyform-container" :style="theme">
    <uni-nav-bar
      class="pt-nav-bar"
      :title="navBarTitle"
      :shadow="false"
      :border="false"
      statusBar
      @clickLeft="back"
      left-icon="left"
    />
    <view class="w-dyform-content-wrapper">
      <scroll-view class="w-dyform-content-scroll" scroll-y="true" :show-scrollbar="false">
        <view style="background-color: #fff; height: inherit">
          <w-dyform
            v-if="!loading && (definitionJson || dyformOptions.formData || formUuid)"
            :options="dyformOptions"
            ref="wDyform"
            :key="dyformKey"
            @formDataChanged="onFormDataChanged"
            @mounted="onFormMounted"
          ></w-dyform>
        </view>
      </scroll-view>

      <view class="w-dyform-bottom-button-container" v-if="buttons.length > 0 && !loading">
        <!-- <w-button :button="dmsButton" class="f_wrap" size="small" :parentWidget="buttonParentWidget"
          :eventWidget="getDyfromWidget"></w-button> -->
        <uni-w-button-group
          style="width: 100%"
          :buttons="buttons"
          :gutter="16"
          :max="buttons.length < 2 ? 0 : 2"
          @click="(e, button) => onTrigger(e, 'click', button)"
        ></uni-w-button-group>
      </view>
    </view>
    <uni-popup ref="alertDialog" type="dialog" style="width: 200px">
      <uni-popup-dialog
        :cancelText="$t('global.close', '关闭')"
        :confirmText="$t('global.confirm', '确定')"
        :title="$t('global.sure', '确认')"
        :content="$t('global.isSure', { name: $t('WidgetSubform.button.save', '保存') }, '确认要保存吗?')"
        @confirm="dialogConfirm"
      ></uni-popup-dialog>
    </uni-popup>

    <uni-popup ref="message" type="message">
      <uni-popup-message :type="msgType" :message="messageText" :duration="2000"></uni-popup-message>
    </uni-popup>
  </view>
</template>
<script>
import { mapMutations, mapActions } from "vuex";
import { storage, appContext, utils, pageContext, store } from "wellapp-uni-framework";
import { set, get } from "lodash";
// import io from "socket.io-client";

export default {
  inject: [],
  data() {
    return {
      formKeyword: "",
      formUuid: "",
      dataUuid: null,
      definitionJson: "",
      msgType: "success",
      dyformKey: new Date().getTime(),
      chooseDyformToPreview: false,
      messageText: "",
      dyformOptions: {
        formData: null,
        formUuid: null,
        dataUuid: null,
      },
      formSelectOptions: {},
      hasSaveBtn: false, // 显示保存按钮
      dmsButton: undefined,
      navBarTitle: "",
      loading: false,
      $developJsInstance: {},
      formData: undefined,
      formDataMD5: "",
      dmsTableId: "",
      dmsWidget: undefined,
      formMounted: false,
      closeAfterSave: undefined,
    };
  },
  computed: {
    buttons() {
      let buttons = [];
      if (!this.loading && this.dmsWidget != undefined && this.formMounted) {
        return this.getButtons(this.dmsWidget.configuration.button);
      }
      return buttons;
    },

    _vShowByWorkflowData() {
      let data = {};
      if (this.workviewContext != undefined) {
        data = {
          taskId: this.workviewContext.workData.taskId,
          taskName: this.workviewContext.workData.taskName,
          flowDefId: this.workviewContext.workData.flowDefId,
          version: parseFloat(this.workviewContext.workData.version),
        };
      }
      return {
        _WORKFLOW_: data,
      };
    },
    _vShowByDateTime() {
      let now = new Date();
      return {
        _DATETIME_: {
          currentDateString: parseInt(now.format("YYYYMMDD")),
          currentFullDateTimeString: parseInt(now.format("YYYYMMDDHHmmss")),
          currentWeekDay: now.getDay(),
          currentMonth: now.getMonth() + 1,
          currentQuarter: now.getQuarter(),
          currentYear: now.getFullYear(),
          currentDay: now.getDate(),
          currentHour: now.getHours(),
        },
      };
    },
    _vShowByUserData() {
      let data = {};
      if (this._$USER) {
        data.userId = this._$USER.userId;
        data.userName = this._$USER.userName;
        data.loginName = this._$USER.loginName;
        data.roles = this._$USER.roles;
        // 解析出默认组织中的单位ID、部门ID、职位ID以及名称的信息
        let userSystemOrgDetails = this._$USER.userSystemOrgDetails;
        if (userSystemOrgDetails) {
          let details = userSystemOrgDetails.details;
          if (details.length) {
            let d = details[0]; // 只取默认组织上的
            let { mainJob, otherJobs, mainDept, otherDepts, unit } = d;
            data.mainJobId = mainJob ? mainJob.eleId : undefined;
            data.mainJobIdPath = mainJob ? mainJob.eleIdPath : undefined;
            data.mainJobName = mainJob ? mainJob.name : undefined;
            data.mainJobNamePath = mainJob ? mainJob.eleNamePath : undefined;
            data.mainDeptId = mainDept ? mainDept.eleId : undefined;
            data.mainDeptIdPath = mainDept ? mainDept.eleIdPath : undefined;
            data.mainDeptName = mainDept ? mainDept.name : undefined;
            data.mainDeptNamePath = mainDept ? mainDept.eleNamePath : undefined;
            data.unitId = unit ? unit.eleId : undefined;
            data.unitName = unit ? unit.name : undefined;
            data.jobIds = [];
            if (data.mainJobId) {
              data.jobIds.push(data.mainJobId);
            }
            if (otherJobs && otherJobs.length) {
              otherJobs.forEach((job) => {
                data.jobIds.push(job.eleId);
              });
            }
            data.jobNames = [];
            if (data.mainJobName) {
              data.jobNames.push(data.mainJobName);
            }
            if (otherJobs && otherJobs.length) {
              otherJobs.forEach((job) => {
                data.jobNames.push(job.name);
              });
            }
            data.deptIds = [];
            if (data.mainDeptId) {
              data.deptIds.push(data.mainDeptId);
            }
            if (otherDepts && otherDepts.length) {
              otherDepts.forEach((dept) => {
                data.deptIds.push(dept.eleId);
              });
            }
            data.deptNames = [];
            if (data.mainDeptName) {
              data.deptNames.push(data.mainDeptName);
            }
            if (otherDepts && otherDepts.length) {
              otherDepts.forEach((dept) => {
                data.deptNames.push(dept.name);
              });
            }
          }
        }
      }
      return {
        _USER_: data,
      };
    },
  },
  onLoad(options) {
    var _this = this;
    if (options.formUuid) {
      _this.formUuid = options.formUuid;
      _this.dyformOptions.formUuid = _this.formUuid;
    }
    if (options.dataUuid) {
      _this.dataUuid = options.dataUuid;
      _this.dyformOptions.dataUuid = _this.dataUuid;
    }
    if (options.displayState) {
      _this.dyformOptions.displayState = options.displayState;
    }
    if (options.accessToken) {
      storage.setAccessToken(options.accessToken);
      _this.accessToken = options.accessToken;
    }
    if (options.hasSaveBtn) {
      _this.hasSaveBtn = !!options.hasSaveBtn;
    }
    if (options.title) {
      this.navBarTitle = options.title;
    }
    if (options.dmsTableId) {
      this.dmsTableId = options.dmsTableId;
    }
    if (options.closeAfterSave) {
      this.closeAfterSave = options.closeAfterSave;
    }

    if (options.dmsId) {
      // 数据管理ID
      _this.dmsId = options.dmsId;
      if (options.from) {
        _this.fromPage = options.from;
      }
      _this.loading = true;
      uni.$on("refresh:" + _this.dmsId, function (options) {
        _this.refresh(options);
      });
    }
  },
  created() {},
  mounted() {
    if (this.dmsId != undefined) {
      this.initDmsOptions();
    }
  },

  onShow: function () {
    const _self = this;
    uni.setNavigationBarTitle({
      title: _self.navBarTitle,
    });
  },
  onBackPress(e) {
    if (e.from === "backbutton") {
      this.back();
      return true; // 阻止默认返回行为
    }
  },
  methods: {
    ...mapMutations(["setNavBarTitle", "setCustomNavBar", "setCurrentAppPage"]),
    ...mapActions(["handlePageReturn"]),
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    refresh(options) {
      this.loading = true;
      if (options.dataUuid) {
        this.dataUuid = options.dataUuid;
        this.dyformOptions.dataUuid = this.dataUuid;
      }
      setTimeout(() => {
        this.initDmsOptions();
      }, 100);
    },
    getDyfromWidget() {
      return this.$refs.wDyform;
    },
    /**
     * 执行事件设置的配置
     * @param {事件设置} eventHandler
     */
    dispatchEventHandler(button, eventHandler, evt) {
      let _this = this;

      eventHandler.pageContext = this.pageContext;
      eventHandler.$evtWidget = this.$refs.wDyform;
      eventHandler.key = button.id;

      // 元数据通过事件传递
      let meta = {};
      eventHandler.meta = meta;
      eventHandler.$evt = evt;
      let developJs = typeof this.developJsInstance === "function" ? this.developJsInstance() : this.developJsInstance;
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

      if (button.style.type === "switch") {
        evt.checked = !button.switch.checked;
        // 开关按钮需要有loading效果
        eventHandler.before = () => {
          _this.loading = true;
        };
        eventHandler.after = (success) => {
          _this.loading = false;
          if (typeof success === "boolean" && success) {
            // 开关执行成功回调通知变更状态
            button.switch.checked = !button.switch.checked;
          }
        };
      }

      if (eventHandler.actionType) {
        appContext.dispatchEvent({
          ui: _this,
          ...eventHandler,
        });
      }
      // 阻止事件冒泡被行点击捕获
      if (evt.stopPropagation) {
        evt.stopPropagation();
      } else if (evt.domEvent && evt.domEvent.stopPropagation) {
        evt.domEvent.stopPropagation();
      }
    },
    resolvePopconfirmTitle(_title, translateCode, confirmConfig) {
      try {
        let meta = this.meta;
        if (typeof this.meta == "function") {
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
      return title;
    },

    onTrigger(evt, trigger, button, hiddenDropdown = false) {
      let _this = this;
      let dispatch = (button, eventHandler, evt) => {
        if (button.confirmConfig && button.confirmConfig.enable) {
          // && button.confirmConfig.popType == "confirm"
          let _title = this.resolvePopconfirmTitle(
              button.confirmConfig.title,
              button.id + "_popconfirmTitle",
              button.confirmConfig
            ),
            _content =
              button.confirmConfig.popType == "popconfirm"
                ? this.resolvePopconfirmTitle(
                    button.confirmConfig.content,
                    button.id + "_popconfirmContent",
                    button.confirmConfig
                  )
                : undefined;

          uni.showModal({
            title: _title,
            confirmText: button.confirmConfig.okText
              ? _this.$t("Widget." + button.id + "_popconfirmOkText", button.confirmConfig.okText)
              : this.$t("global.confirm", "确认"),
            cancelText: button.confirmConfig.cancelText
              ? _this.$t("Widget." + button.id + "_popconfirmCancelText", button.confirmConfig.cancelText)
              : this.$t("global.cancel", "取消"),
            content: _content,
            success: function (res) {
              if (res.confirm) {
                _this.dispatchEventHandler(button, eventHandler, evt);
              } else if (res.cancel) {
                _this.dispatchEventHandler(button, eventHandler, evt);
              }
            },
          });
        } else {
          _this.dispatchEventHandler(button, eventHandler, evt);
        }
      };

      // 双击发了3个事件。两次点击事件，最后一次是dblclick
      if (button.hasOwnProperty("eventHandler")) {
        if (Array.isArray(button.eventHandler)) {
          for (let i = 0, len = button.eventHandler.length; i < len; i++) {
            if (button.eventHandler[i].trigger === trigger) {
              dispatch(button, button.eventHandler[i], evt);
            }
          }
        } else {
          if (button.eventHandler.trigger === trigger) {
            dispatch(button, button.eventHandler, evt);
          }
        }
      }

      if (!(button.confirmConfig && button.confirmConfig.enable)) {
        // 不存在确认框，向上触发按钮事件触发类型
        this.$emit("button-" + trigger, evt, button);
      }

      if (evt.stopPropagation) {
        evt.stopPropagation();
      } else if (evt.domEvent != undefined) {
        evt.domEvent.stopPropagation();
      }
    },

    initDmsOptions() {
      this.getWidget({ id: this.dmsId, appPageId: this.fromPage, appPageUuid: this.fromPage })
        .then((widget) => {
          if (widget) {
            this.loading = false;
            if (widget.configuration.i18n) {
              this.setI18nMessage(widget.id, widget.configuration.i18n);
            }
            this.initJsModules(widget);
            if (this.dyformOptions.displayState == undefined) {
              this.dyformOptions.displayState = this.dataUuid != null ? "label" : "edit";
            }
            this.setTitle(widget);
            this.dmsWidget = widget;

            let ruleKey = "formElementRules",
              formElementRules = {};
            if (this.dataUuid != null) {
              // 有数据的情况下，编辑或者查阅
              ruleKey =
                this.dyformOptions.displayState === "edit" ? "editStateFormElementRules" : "labelStateFormElementRules";
              if (widget.configuration.enableStateForm) {
                let formUuidKey =
                  this.dyformOptions.displayState === "edit" ? "editStateFormUuid" : "labelStateFormUuid";
                this.formUuid = widget.configuration[formUuidKey] || widget.configuration.formUuid;
                this.dyformOptions.formUuid = this.formUuid;
              }
            }
            if (widget.configuration[ruleKey]) {
              for (let i = 0, len = widget.configuration[ruleKey].length; i < len; i++) {
                formElementRules[widget.configuration[ruleKey][i].id] = {
                  readonly: widget.configuration[ruleKey][i].readonly,
                  editable: widget.configuration[ruleKey][i].editable,
                  disable: widget.configuration[ruleKey][i].disable,
                  hidden: widget.configuration[ruleKey][i].hidden,
                  displayAsLabel: widget.configuration[ruleKey][i].displayAsLabel,
                  required: widget.configuration[ruleKey][i].required,
                };

                let children = widget.configuration[ruleKey][i].children;
                if (children) {
                  let childrenRules = {};
                  formElementRules[widget.configuration[ruleKey][i].id].children = childrenRules;
                  for (let j = 0, jlen = children.length; j < jlen; j++) {
                    childrenRules[children[j].id] = {
                      readonly: children[j].readonly,
                      editable: children[j].editable,
                      disable: children[j].disable,
                      hidden: children[j].hidden,
                      displayAsLabel: children[j].displayAsLabel,
                      required: children[j].required,
                    };
                  }
                }
              }
              this.dyformOptions.formElementRules = formElementRules;
              this.invokeDevelopmentMethod("mounted");
            }
          }
        })
        .catch((err) => {
          console.error("initDmsOptions", err);
        });
    },
    setTitle(widget) {
      // 编辑状态时标题
      if (this.dataUuid) {
        this.navBarTitle =
          this.$t("Widget." + widget.id + ".editStateTitle", widget.configuration.editStateTitle) ||
          this.$t("global.edit", "编辑");
      } else {
        this.navBarTitle =
          this.$t("Widget." + widget.id + ".title", widget.configuration.title) || this.$t("global.create", "新建");
      }
      // 查阅状态时标题
      if (this.dyformOptions.displayState === "label") {
        this.navBarTitle =
          this.$t("Widget." + widget.id + ".labelStateTitle", widget.configuration.labelStateTitle) ||
          this.$t("global.view", "查看");
      }
    },
    initJsModules(widget) {
      this.$developJsInstance = {};
      if (widget.configuration.jsModules && widget.configuration.jsModules.length > 0) {
        for (let i = 0, len = widget.configuration.jsModules.length; i < len; i++) {
          let js = widget.configuration.jsModules[i].key;
          try {
            this.$developJsInstance[js] = appContext.jsInstance(js, this);
          } catch (error) {
            console.error(error);
          }
        }
      }
    },
    invokeDevelopmentMethod() {
      try {
        for (let k in this.$developJsInstance) {
          let args = Array.from(arguments),
            method = args.shift(0); // 第一个参数是访问方法，移出后剩余的参数组就是方法入参
          if (typeof this.$developJsInstance[k][method] === "function") {
            let returnObj = this.$developJsInstance[k][method].apply(this.$developJsInstance[k], args);
            if (returnObj != undefined) {
              // 二开有返回对象，直接返回
              return returnObj;
            }
            // 无则继续执行其他二开脚本的方法
          }
        }
      } catch (error) {
        console.error("调用二开脚本方法失败：", error);
      }
    },
    getButtons: function (buttonSource) {
      let _self = this;
      let btns = [],
        result = [];
      let moreBtn = {
        id: "more",
        text: this.$t("global.more", "更多"),
        children: [],
      };
      let compareData = this.getConditionData();
      for (let button of buttonSource.buttons) {
        let visible = true;

        if (button.visibleType === "hidden") {
          visible = false;
        } else if (button.visibleType === "visible-condition") {
          // 根据条件判断展示:
          if (
            (this.dataUuid == undefined && button.visibleCondition.formStateConditions.indexOf("createForm") != -1) ||
            (this.dataUuid != undefined &&
              this.dyformOptions.displayState === "edit" &&
              button.visibleCondition.formStateConditions.indexOf("edit") != -1) ||
            (this.dataUuid != undefined &&
              this.dyformOptions.displayState === "label" &&
              button.visibleCondition.formStateConditions.indexOf("label") != -1)
          ) {
            // 关联角色
            if (button.visibleCondition.userRoleConditions && button.visibleCondition.userRoleConditions.length > 0) {
              if (this._hasAnyRole && !this._hasAnyRole(button.visibleCondition.userRoleConditions)) {
                visible = false;
              }
              if (visible) {
                visible = _self.matchVisibleCondition(button, compareData);
              }
            } else if (
              button.visibleCondition.enableDefineCondition &&
              button.visibleCondition.defineCondition.cons.length > 0
            ) {
              visible = visible = _self.matchVisibleCondition(button, compareData);
            }
          } else {
            visible = false;
          }
        }

        if (visible) {
          if (button.icon && button.icon.className) {
            if (button.icon.className.startsWith("/") && !button.icon.src) {
              if (button.icon.className.startsWith("/static/")) {
                button.icon.src = button.icon.className;
              } else {
                button.icon.src = storage.fillAccessResourceUrl(button.icon.className);
              }
            }
          }
          button.text = _self.$t(button.uuid, button.text);
          button.type = button.style ? button.style.type || "primary" : "primary";
          if (!button.title) {
            button.title = button.text;
          }
          btns.push(Object.assign({}, button));
        }
      }

      if (btns.length > 2) {
        let noGroupButtons = btns.splice(0, 1);
        moreBtn.children = btns;
        noGroupButtons.push(moreBtn);
        btns = noGroupButtons;
      }
      result.push(...btns);
      return result;
    },
    matchVisibleCondition(button, compareData) {
      let visible = false;
      if (button.visibleCondition.enableDefineCondition && button.visibleCondition.defineCondition.cons.length > 0) {
        // 多组条件判断
        let match = button.visibleCondition.defineCondition.operator == "and";
        for (let i = 0, len = button.visibleCondition.defineCondition.cons.length; i < len; i++) {
          let { code, operator, value, valueType } = button.visibleCondition.defineCondition.cons[i];
          if (/^[_a-z]+(_[a-z]+)*$/.test(code)) {
            // 兼容旧版表单字段获取语法
            code = `_FORM_DATA_.${code}`;
          }
          if (valueType == "variable") {
            try {
              value = get(compareData, value);
            } catch (error) {
              console.error("无法解析变量值", value);
            }
          }
          let result = utils.expressionCompare(compareData, code, operator, value);
          if (button.visibleCondition.defineCondition.operator == "and" && !result) {
            match = false;
            break;
          }
          if (button.visibleCondition.defineCondition.operator == "or" && result) {
            match = true;
            break;
          }
        }
        visible = match;
      }
      return visible;
    },
    getConditionData() {
      return {
        ...this._vShowByUserData,
        ...this._vShowByDateTime,
        ...this._vShowByWorkflowData,
        ...(this.formMounted && this.$refs.wDyform != undefined
          ? {
              ...this.$refs.wDyform.dyform.formData, // 兼容旧语法
              _FORM_DATA_: this.$refs.wDyform.dyform.formData, // 主表数据
            }
          : {}),
      };
    },
    onFormMounted() {
      this.formMounted = true;
    },
    setI18nMessage(id, i18ns) {
      let widgetI18ns = undefined;
      if (i18ns) {
        widgetI18ns = {
          [this.$i18n.locale]: {
            Widget: {
              [id]: {},
            },
          },
        };
        for (let key in i18ns[this.$i18n.locale]) {
          let keyArr = key.split(id + ".");
          set(widgetI18ns[this.$i18n.locale].Widget[id], keyArr[1], i18ns[this.$i18n.locale][key]);
        }
        this.$i18n.mergeLocaleMessage(this.$i18n.locale, widgetI18ns[this.$i18n.locale]);
      }
    },
    getWidget({ id, appPageId, appPageUuid }) {
      return new Promise((resolve, reject) => {
        uni.$axios
          .get("/server-api/api/app/widget/getWidgetById", { params: { id, appPageId, appPageUuid } })
          .then(({ data }) => {
            let wgt = data.data;
            if (!wgt) {
              console.warn("no found widget definition");
              reject();
            } else {
              resolve(JSON.parse(wgt.definitionJson));
            }
          })
          .catch(() => {});
      });
    },
    buttonParentWidget() {
      return this;
    },
    onSave() {
      let _this = this;
      this.$refs.wDyform.collectFormData(true, function (valid, msg, formData) {
        console.log("收集表单数据", arguments);
        if (valid) {
          _this.$refs.alertDialog.open();
          _this.commitFormData = formData;
        }
      });
    },
    dialogConfirm() {
      let _this = this;
      uni.showLoading({ title: this.$t("global.saving", "保存中"), mask: true });
      _this.invokeDevelopmentMethod("beforeSaveData", this.commitFormData.dyFormData);
      this.$axios
        .post("/api/dyform/data/saveFormData", this.commitFormData.dyFormData)
        .then(({ data }) => {
          uni.hideLoading();
          if (data.code === 0) {
            _this.msgType = "success";
            _this.messageText = this.$t(
              "global.successText",
              { name: this.$t("WidgetSubform.button.save", "保存") },
              "保存成功!"
            );
          } else {
            _this.msgType = "error";
            _this.messageText = this.$t(
              "global.failText",
              { name: this.$t("WidgetSubform.button.save", "保存") },
              "保存失败!"
            );
          }
          _this.$refs.message.open();
          _this.invokeDevelopmentMethod("afterSaveDataSuccess", data);
        })
        .catch((error) => {
          uni.hideLoading();
          console.error(error);
        });
    },
    back() {
      let _this = this;
      uni.navigateBack({
        delta: 1,
        success: function () {
          let $table = pageContext.getComponent(_this.dmsTableId);
          if ($table) {
            $table.refresh();
          }
          _this.handlePageReturn();
        },
      });
    },
    onFormDataChanged(e) {
      if (this.$refs.wDyform) {
        this.formData = JSON.parse(JSON.stringify(this.$refs.wDyform.dyform.formData));
        this.formDataMD5 = utils.md5(JSON.stringify(this.formData));
        this.invokeDevelopmentMethod("afterFormDataChanged", this.$refs.wDyform.dyform.formData);
      }
      // this.$nextTick(() => {
      //   this.buttonKey = `${this.widget.id}_buttons_` + new Date().getTime();
      // });

      // this.updateModalButton();
    },
  },
};
</script>
<style lang="scss" scoped>
.w-dyform-preview {
  width: 100vw;
  height: 100vh;
  background-color: var(--w-bg-color-mobile-bg);
}

.w-dyform-container {
  height: 100vh;
  display: flex;
  flex-direction: column;

  .w-dyform-content-wrapper {
    flex: 1;
    display: flex;
    flex-direction: column;
    height: calc(100vh - var(--status-bar-height) - 44px);

    /* 减去导航栏高度 */
    .w-dyform-content-scroll {
      flex: 1;
      overflow-y: auto;
      padding: 20rpx;
      width: auto;
      /* 不需要额外 bottom padding，因为按钮是固定定位的 */
    }

    .w-dyform-bottom-button-container {
      padding: 30rpx 20rpx 40rpx;
      background-color: white;
      /* 可选：添加底部阴影效果 */
      box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
      /* 为了确保按钮在最底部，可以使用 position: fixed，但当前flex布局已经可以很好处理 */
    }
  }

  .popup-dialog {
    width: 300px;
    border-radius: 11px;
    background-color: #fff;
  }

  .w-dyform-buttons {
    display: flex;
    flex-direction: row;
    justify-content: space-around;
    align-items: center;
    background-color: #fff;
    border-top: 1px #eee solid;
    height: 45px;
    position: fixed;
    bottom: 0;
    width: 100vw;

    > .button {
      display: flex;
      flex-direction: row;
      align-items: center;
    }
  }
}
</style>
