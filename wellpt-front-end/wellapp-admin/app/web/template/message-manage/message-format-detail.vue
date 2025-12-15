<template>
  <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="formRules" ref="form">
    <a-tabs default-active-key="1" type="card">
      <a-tab-pane key="1" tab="基本信息">
        <a-form-model-item label="名称" prop="name">
          <a-input v-model="form.name" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="id" prop="id">
          <a-input v-model="form.id" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="编号">
          <a-input v-model="form.code" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="分类">
          <a-select v-model="form.classifyUuid" showSearch allow-clear @change="classifyChange">
            <a-select-option v-for="d in allClassifyOptions" :key="d.uuid">
              {{ d.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="所属模块">
          <a-select v-model="form.moduleId" showSearch allow-clear :filter-option="filterOption">
            <a-select-option v-for="d in allModuleOptions" :key="d.id">
              {{ d.text }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
        <a-form-model-item label="类型">
          <a-radio-group v-model="form.type">
            <a-radio value="SYSTEM" name="type">系统消息</a-radio>
            <a-radio value="USER" name="type">用户消息</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="提醒方式">
          <a-checkbox-group v-model="form.sendWays">
            <a-checkbox value="ON_LINE" name="sendWays">在线消息</a-checkbox>
            <a-checkbox value="EMAIL" name="sendWays">邮件</a-checkbox>
            <a-checkbox value="SMS" name="sendWays">手机短信</a-checkbox>
            <a-checkbox value="INTEFACE" name="sendWays">发送消息触发接口实现</a-checkbox>
            <a-checkbox value="DINGTALK" name="sendWays">钉钉对接</a-checkbox>
          </a-checkbox-group>
        </a-form-model-item>
        <a-form-model-item label="接口实现" v-show="form.sendWays.indexOf('INTEFACE') > -1" prop="messageInteface">
          <a-tree-select
            v-model="form.messageInteface"
            style="width: 100%"
            :replaceFields="{ children: 'children', title: 'name', key: 'id', value: 'id' }"
            :tree-data="allIntefaceSourceOptions"
            tree-checkable
            allow-clear
          />
        </a-form-model-item>
        <a-form-model-item label="发送时间">
          <a-radio-group v-model="form.sendTime">
            <a-radio value="IN_TIME" name="sendTime">即时发送</a-radio>
            <a-radio value="WORK_TIME" name="sendTime">工作时间发送</a-radio>
            <a-radio value="SCHEDULE_TIME" name="sendTime">定时发送</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="定时时间" v-show="form.sendTime == 'SCHEDULE_TIME'">
          <a-input v-model="form.scheduleTime" style="width: 100%" allow-clear />
          <span>可以通过输入${变量}的方式指定，如${CreateTime}</span>
        </a-form-model-item>
        <a-form-model-item label="发送消息触发事件">
          <a-tree-select
            v-model="form.messageEvent"
            style="width: 100%"
            :replaceFields="{ children: 'children', title: 'name', key: 'id', value: 'id' }"
            :tree-data="allMessageEventOptions"
            tree-checkable
            allow-clear
          />
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="2" tab="在线消息" :forceRender="form.sendWays.indexOf('ON_LINE') > -1">
        <a-form-model-item label="标题">
          <a-input v-model="form.onlineSubject" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="内容">
          <a-textarea v-model="form.onlineBody" :auto-size="{ minRows: 3, maxRows: 5 }" allow-clear />
        </a-form-model-item>
        <a-card title="附加链接" :bordered="false" style="width: 100%">
          <a-form-model-item label="源标题">
            <a-input v-model="form.relatedTitle" allow-clear />
          </a-form-model-item>
          <a-form-model-item label="源地址">
            <a-textarea v-model="form.relatedUrl" :auto-size="{ minRows: 3, maxRows: 5 }" allow-clear />
          </a-form-model-item>
        </a-card>
        <a-card title="回调事件" :bordered="false" style="width: 100%">
          <div style="pading: 8px 0; text-align: right">
            <a-button size="small" type="primary" @click="addMsgEvent">新建</a-button>
            <a-button size="small" @click="delTableItems('msgEvent')">删除</a-button>
            <a-button size="small" @click="upTableItems('msgEvent')">上移</a-button>
            <a-button size="small" @click="downTableItems('msgEvent')">下移</a-button>
          </div>
          <a-table
            :data-source="form.msgEventData"
            :row-selection="{ selectedRowKeys: msgEvent_selectedRowKeys, onChange: msgEventChange }"
            :pagination="false"
            ref="msgEventTableRef"
            :row-key="(record, index) => index"
          >
            <a-table-column key="type" data-index="type" title="类型">
              <template slot-scope="text, record">
                <a-select v-model="record.type" allow-clear>
                  <a-select-option value="preset">内置事件</a-select-option>
                  <a-select-option value="customize">自定义事件</a-select-option>
                </a-select>
              </template>
            </a-table-column>
            <a-table-column key="title" data-index="title" title="名称" :width="120">
              <template slot-scope="text, record, index">
                <a-form-model-item
                  label=""
                  style="margin-bottom: 0"
                  :prop="`msgEventData:${index}:title`"
                  :rules="formRules.tableTitle || {}"
                >
                  <template v-if="record.type == 'preset'">
                    <a-select v-model="record.titleCode" @change="value => tableTitleChange(value, index)">
                      <a-select-option value="reply">回复</a-select-option>
                      <a-select-option value="forward">转发</a-select-option>
                      <a-select-option value="delete">删除</a-select-option>
                    </a-select>
                  </template>
                  <template v-else>
                    <a-input v-model="record.title" allow-clear />
                  </template>
                </a-form-model-item>
              </template>
            </a-table-column>
            <a-table-column key="code" data-index="code" title="编码" :width="180">
              <template slot-scope="text, record, index">
                <a-form-model-item
                  style="margin-bottom: 0"
                  :prop="`msgEventData:${index}:code`"
                  :name="['msgEventData', index, 'code']"
                  :rules="formRules.tableCode || {}"
                >
                  <a-input v-model="record.code" v-if="record.type == 'customize'" allow-clear />
                  <span v-else>
                    {{ record.code }}
                  </span>
                </a-form-model-item>
              </template>
            </a-table-column>
            <a-table-column key="displayLocation" data-index="displayLocation" title="展示位置" :width="340">
              <template slot-scope="text, record">
                <a-checkbox-group v-model="record.displayLocation">
                  <a-checkbox value="message-modal">弹窗展示</a-checkbox>
                  <a-checkbox value="message-list">消息列表</a-checkbox>
                  <a-checkbox value="message-detail">消息详情</a-checkbox>
                </a-checkbox-group>
              </template>
            </a-table-column>
            <a-table-column key="group" data-index="group" title="组别" :width="80">
              <template slot-scope="text, record">
                <a-input v-model="record.group" style="width: 80px" allow-clear />
              </template>
            </a-table-column>
            <a-table-column key="style" data-index="style" title="按钮及事件">
              <template slot-scope="text, record, index">
                <a-button
                  :size="record.style.size || 'default'"
                  :type="record.style.type || 'default'"
                  :shape="record.style.shape"
                  @click="editButtonConfiguration(record, index)"
                >
                  <Icon :type="record.style.icon" v-if="record.style.icon" />
                  <span v-if="!record.style.textHidden">配置</span>
                </a-button>
              </template>
            </a-table-column>
          </a-table>
        </a-card>
        <a-form-model-item label="消息提醒方式">
          <a-radio-group v-model="form.reminderType">
            <a-radio :value="1" name="reminderType">计数徽标</a-radio>
            <a-radio :value="2" name="reminderType">计数徽标 + 弹窗</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <template v-if="form.reminderType == 2">
          <a-form-model-item label="弹窗位置">
            <a-select v-model="form.popupPosition" showSearch allow-clear>
              <a-select-option :value="1">浏览器右下角</a-select-option>
              <a-select-option :value="2">浏览器中间</a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="弹窗大小">
            <a-radio-group v-model="form.popupSize">
              <a-radio :value="1" name="popupSize">默认</a-radio>
              <a-radio :value="2" name="popupSize">自定义</a-radio>
            </a-radio-group>
            <template v-if="form.popupSize == 2">
              <a-form-model-item label="宽" :style="{ display: 'inline-block', 'margin-bottom': 0 }">
                <a-input v-model="form.popupWidth" style="width: calc(100% - 50px)" allow-clear />
              </a-form-model-item>
              <a-form-model-item label="高" :style="{ display: 'inline-block', 'margin-bottom': 0 }">
                <a-input v-model="form.popupHeight" style="width: calc(100% - 50px)" allow-clear />
              </a-form-model-item>
            </template>
          </a-form-model-item>
          <a-form-model-item label="显示遮罩">
            <a-switch v-model="form.displayMask" />
          </a-form-model-item>
          <a-form-model-item label="弹窗计时关闭">
            <a-switch v-model="form.autoTimeCloseWin" />
          </a-form-model-item>
        </template>
      </a-tab-pane>
      <a-tab-pane key="3" tab="电子邮件" :forceRender="form.sendWays.indexOf('EMAIL') > -1">
        <a-form-model-item label="标题">
          <a-input v-model="form.emailSubject" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="内容">
          <a-textarea v-model="form.emailBody" :auto-size="{ minRows: 3, maxRows: 5 }" allow-clear />
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="4" tab="手机短信" :forceRender="form.sendWays.indexOf('SMS') > -1">
        <a-form-model-item label="内容">
          <a-textarea v-model="form.smsBody" :auto-size="{ minRows: 3, maxRows: 5 }" allow-clear />
        </a-form-model-item>
      </a-tab-pane>
      <a-tab-pane key="5" tab="钉钉消息" :forceRender="form.sendWays.indexOf('DINGTALK') > -1">
        <a-form-model-item label="消息类型">
          <a-radio-group v-model="form.dtMessageType">
            <a-radio value="ActionCard" name="dtMessageType">卡片</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="跳转方式">
          <a-radio-group v-model="form.dtJumpType">
            <a-radio value="single" name="dtJumpType">整体跳转</a-radio>
            <a-radio value="multi" name="dtJumpType">独立跳转</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-form-model-item label="标题" prop="dtTitle">
          <a-input v-model="form.dtTitle" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="内容" prop="dtBody">
          <a-textarea v-model="form.dtBody" :auto-size="{ minRows: 3, maxRows: 5 }" allow-clear />
        </a-form-model-item>
        <a-form-model-item label="按钮排列方式">
          <a-radio-group v-model="form.dtBtnOrientation">
            <a-radio :value="0" name="dtBtnOrientation">竖直列排</a-radio>
            <a-radio :value="1" name="dtBtnOrientation">横向列排</a-radio>
          </a-radio-group>
        </a-form-model-item>
        <a-card title="附加链接" :bordered="false" style="width: 100%">
          <template v-if="form.dtJumpType == 'multi'">
            <div style="pading: 8px 0; text-align: right">
              <a-button size="small" type="primary" @click="addMsgLink">新建</a-button>
              <a-button size="small" @click="delTableItems('msgLink')">删除</a-button>
              <a-button size="small" @click="upTableItems('msgLink')">上移</a-button>
              <a-button size="small" @click="downTableItems('msgLink')">下移</a-button>
            </div>
            <a-table
              :data-source="form.msgLinkData"
              :row-selection="{ selectedRowKeys: msgLink_selectedRowKeys, onChange: msgLinkChange }"
              :pagination="false"
              :row-key="(record, index) => index"
            >
              <a-table-column key="title" data-index="title" title="源标题">
                <template slot-scope="text, record">
                  <a-input v-model="record.title" allow-clear />
                </template>
              </a-table-column>
              <a-table-column key="url" data-index="url" title="源地址">
                <template slot-scope="text, record">
                  <a-input v-model="record.url" allow-clear />
                </template>
              </a-table-column>
            </a-table>
          </template>
          <template v-else>
            <a-form-model-item label="源标题">
              <a-input v-model="form.dtUriTitle" allow-clear />
            </a-form-model-item>
            <a-form-model-item label="源地址">
              <a-textarea v-model="form.dtUri" :auto-size="{ minRows: 3, maxRows: 5 }" allow-clear />
            </a-form-model-item>
          </template>
        </a-card>
      </a-tab-pane>
    </a-tabs>
    <a-modal
      v-model="msgEventModalVisible"
      title="回调事件"
      @ok="msgEventHandleOk"
      :width="600"
      bodyStyle="max-height:600px;overflow-y:auto"
    >
      <AdminSettingButtonConfiguration
        :button="msgEventRecode"
        :hasHandler="msgEventRecode.type == 'customize'"
        :handler="msgEventRecode.eventHandler"
        ref="msgEventConfigurationRef"
        hideParams="text,code"
      />
    </a-modal>
  </a-form-model>
</template>

<script type="text/babel">
import { generateId, deepClone } from '@framework/vue/utils/util';
import AdminSettingButtonConfiguration from '../common/button-configuration-admin';
import { getPopupContainerByPs } from '@dyform/app/web/page/dyform-designer/utils';
const presetEventsInfo = {
  forward: {
    id: generateId(),
    type: 'preset',
    displayLocation: ['message-detail', 'message-list'],
    title: '转发',
    titleCode: 'forward',
    code: 'btnForwardMsg',
    eventHandler: {},
    role: [],
    style: {
      icon: 'pticon iconfont icon-oa-zhuanban',
      textHidden: false,
      type: 'primary'
    },
    visible: true
  },
  reply: {
    id: generateId(),
    type: 'preset',
    title: '回复',
    titleCode: 'reply',
    code: 'btnReplyMsg',
    eventHandler: {},
    role: [],
    style: {
      icon: 'pticon iconfont icon-ptkj-xiaoxitongzhibiaoti',
      textHidden: false,
      type: 'primary'
    },
    visible: true
  },
  delete: {
    id: generateId(),
    type: 'preset',
    title: '删除',
    titleCode: 'delete',
    code: 'btnDelMsg',
    displayLocation: ['message-list'],
    eventHandler: {},
    role: [],
    style: {
      icon: 'pticon iconfont icon-ptkj-shanchu',
      textHidden: false,
      type: 'primary'
    },
    visible: true
  }
};
export default {
  name: 'MessageFormatDetail',
  props: {
    uuid: String,
    displayState: {
      type: String,
      default: 'edit' // edit、label
    }
  },
  components: { AdminSettingButtonConfiguration },
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    return {
      $evtWidget: undefined,
      $dialogWidget: undefined,
      presetEventsInfo,
      form: {
        name: '',
        id: '',
        type: 'SYSTEM',
        sendWays: ['ON_LINE'],
        sendTime: 'IN_TIME',
        dtMessageType: 'ActionCard',
        dtJumpType: 'single',
        dtBtnOrientation: 0,
        reminderType: 1,
        popupPosition: 1,
        popupSize: 1,
        displayMask: false,
        autoTimeCloseWin: true,
        msgEventData: [],
        msgLinkData: [],
        messageInteface: []
      },
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      rules: {
        name: { required: true, message: '名称必填', trigger: ['blur', 'change'] },
        id: { required: true, message: 'id必填', trigger: ['blur', 'change'] }
      },
      dtRules: {
        dtTitle: { required: true, message: '钉钉标题必填', trigger: ['blur', 'change'] },
        dtBody: { required: true, message: '钉钉内容必填', trigger: ['blur', 'change'] }
      },
      tableRules: {
        tableTitle: { required: true, message: '回调事件名称必填', trigger: ['blur'] },
        tableCode: { required: true, message: '回调事件编码必填', trigger: ['blur'] }
      },
      allClassifyOptions: [],
      allModuleOptions: [],
      allIntefaceSourceOptions: [],
      allMessageEventOptions: [],
      popupPositionOptions: [],
      msgLink_selectedRowKeys: [],
      msgLink_selectedRows: [],
      msgEvent_selectedRowKeys: [],
      msgEvent_selectedRows: [],
      msgEventModalVisible: false,
      msgEventRecode: {},
      msgEventIndex: undefined,
      wTemplate: {
        $options: {
          methods: {
            saveForm: this.saveForm
          },
          META: {
            method: {
              saveForm: '保存表单'
            }
          }
        }
      }
    };
  },
  META: {
    method: {
      saveForm: '保存表单'
    }
  },
  computed: {
    formRules() {
      let rules = _.assignIn({}, this.rules);
      if (this.form.sendWays.indexOf('ON_LINE') > -1) {
        rules = _.assignIn(rules, this.tableRules);
      }
      if (this.form.sendWays.indexOf('INTEFACE') > -1) {
        rules = _.assignIn(rules, {
          messageInteface: { required: true, message: '接口实现必填', trigger: ['blur', 'change'] }
        });
      }
      if (this.form.sendWays.indexOf('EMAIL') > -1) {
      }
      if (this.form.sendWays.indexOf('SMS') > -1) {
      }
      if (this.form.sendWays.indexOf('DINGTALK') > -1) {
        rules = _.assignIn(rules, this.dtRules);
      }
      return rules;
    }
  },
  provide() {
    return {
      appId: () => (this.form.moduleId ? [this.form.moduleId] : [])
    };
  },
  watch: {},
  beforeCreate() {},
  created() {
    this.getAllModuleOptions();
    this.getAllClassifyOptions();
    this.getIntefaceSourceList();
    this.getEventClientSourceList();
  },
  beforeMount() {
    let _this = this;
  },
  mounted() {
    let $event = this._provided.$event;
    this.$evtWidget = $event && $event.$evtWidget;
    this.$dialogWidget = this._provided && this._provided.dialogContext;
    if ($event && $event.eventParams) {
      this.form.id = $event.eventParams.id || '';
    }
    if (!this.form.id) {
      this.$set(this.form, 'id', 'MSG_' + generateId('SF'));
      this.form.msgEventData = [];
      this.form.msgEventData.push(this.presetEventsInfo.forward);
      this.form.msgEventData.push(this.presetEventsInfo.delete);
    } else {
      this.getFormData();
    }
  },
  methods: {
    getPopupContainerByPs,
    getFormData() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'messageTemplateService',
          methodName: 'getBeanById',
          args: JSON.stringify([this.form.id]),
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            if (data.data.callbackJson) {
              data.data.msgEventData = _.map(JSON.parse(data.data.callbackJson).events, item => {
                if (!item.title) {
                  if (item.type == 'preset' && item.text) {
                    let _item = this.presetEventsInfo[item.text];
                    if (_item) {
                      item.title = _item.title;
                      item.titleCode = _item.titleCode;
                    } else {
                      _.each(this.presetEventsInfo, (citem, index) => {
                        if (citem.title == item.text) {
                          item.title = citem.title;
                          item.titleCode = citem.titleCode;
                        }
                      });
                    }
                  } else {
                    item.title = item.text;
                  }
                }
                if (!item.style) {
                  item.style = {};
                  if (item.btnLib) {
                    if (item.btnLib.btnInfo) {
                      item.style.type = item.btnLib.btnInfo.type || '';
                    }
                    if (item.btnLib.iconInfo) {
                      item.style.icon = item.btnLib.iconInfo.fileIDs ? 'pticon ' + item.btnLib.iconInfo.fileIDs : '';
                    }
                    item.style.size = item.btnLib.btnSize || '';
                  }
                }
                if (!item.id) {
                  item.id = generateId();
                }
                if (!item.eventHandler) {
                  item.eventHandler = {};
                }
                if (!item.role) {
                  item.role = [];
                }
                return item;
              });
            }
            data.data.displayMask = !!data.data.displayMask;
            data.data.autoTimeCloseWin = !!data.data.autoTimeCloseWin;
            data.data.msgLinkData = data.data.dtBtnJsonList ? JSON.parse(data.data.dtBtnJsonList) : [];
            data.data.messageInteface = data.data.messageInteface ? data.data.messageInteface.split(';') : [];

            _this.form = data.data;
          }
        });
    },
    // 模块
    getAllModuleOptions() {
      let _this = this;
      _this.$axios
        .post('/common/select2/query', {
          serviceName: 'appModuleMgr',
          queryMethod: 'loadSelectData',
          params: {
            systemUnitId: this._$SYSTEM_ID
          },
          searchValue: '',
          pageSize: 1000,
          pageNo: 1
        })
        .then(({ data }) => {
          if (data.results) {
            _this.allModuleOptions = data.results;
          }
        });
    },
    // 分类
    getAllClassifyOptions() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'messageClassifyService',
          methodName: 'queryList',
          args: JSON.stringify(['', '']),
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.allClassifyOptions = data.data;
          }
        });
    },
    // 接口实现
    getIntefaceSourceList() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'messageService',
          methodName: 'getIntefaceSourceList',
          args: '[-1, 1]'
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.allIntefaceSourceOptions = data.data;
          }
        });
    },
    //发送消息触发事件
    getEventClientSourceList() {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'messageEventService',
          methodName: 'getEventClientSourceList',
          args: '[-1, 1]'
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            _this.allMessageEventOptions = data.data;
          }
        });
    },
    classifyChange(value, option) {
      if (value) {
        let _option = _.find(this.allClassifyOptions, { uuid: value });
        this.form.classifyName = _option && _option.name ? _option.name : '';
      } else {
        this.form.classifyName = '';
      }
    },
    tableTitleChange(value, index) {
      let presetEventsInfo = deepClone(this.presetEventsInfo);
      if (value) {
        let item = presetEventsInfo[value];
        if (item) {
          this.$set(this.form.msgEventData[index], 'code', item.code);
          this.$set(this.form.msgEventData[index], 'title', item.title);
          this.$set(this.form.msgEventData[index].style, 'icon', item.style.icon);
        }
      }
    },
    msgLinkChange(selectedRowKeys, selectedRows) {
      this.msgLink_selectedRowKeys = selectedRowKeys; //选中的keys
      this.msgLink_selectedRows = selectedRows; //选中的行
    },
    msgEventChange(selectedRowKeys, selectedRows) {
      this.msgEvent_selectedRowKeys = selectedRowKeys; //选中的keys
      this.msgEvent_selectedRows = selectedRows; //选中的行
    },
    resetTableSelectKey(param) {
      let selectedRowKeys = [];
      _.each(this[param + '_selectedRows'], (item, index) => {
        const hasIndex = _.findIndex(this.form[param + 'Data'], { id: item.id });
        if (hasIndex > -1) {
          selectedRowKeys.push(hasIndex);
        }
      });
      this[param + '_selectedRowKeys'] = selectedRowKeys;
    },
    addMsgEvent() {
      this.form.msgEventData.push({
        type: 'customize',
        style: {
          icon: '',
          textHidden: false,
          type: ''
        },
        displayLocation: '',
        id: generateId(),
        eventHandler: {},
        role: [],
        title: '',
        code: ''
      });
      this.$forceUpdate();
    },
    delTableItems(param) {
      if (this[param + '_selectedRows'].length == 0) {
        this.$message.error('请选择删除的项');
        return false;
      }
      const newData = _.filter(this.form[param + 'Data'], (item, index) => {
        return _.findIndex(this[param + '_selectedRows'], { id: item.id }) == -1;
      });
      this.form[param + 'Data'] = newData;
      this[param + '_selectedRowKeys'] = [];
      this[param + '_selectedRows'] = [];
    },
    upTableItems(param) {
      if (this[param + '_selectedRows'].length == 0) {
        this.$message.error('请选择上移的项');
        return false;
      }
      _.each(_.sortBy(this[param + '_selectedRowKeys']), (item, index) => {
        if (item > 0) {
          const currentItem = this.form[param + 'Data'][item];
          this.form[param + 'Data'].splice(item, 1);
          this.form[param + 'Data'].splice(item - 1, 0, currentItem);
        }
      });
      this.resetTableSelectKey(param);
    },
    downTableItems(param) {
      if (this[param + '_selectedRows'].length == 0) {
        this.$message.error('请选择下移的项');
        return false;
      }
      _.each(this[param + '_selectedRowKeys'].reverse(), (item, index) => {
        if (item < this.form[param + 'Data'].length - 1) {
          const currentItem = this.form[param + 'Data'][item];
          this.form[param + 'Data'].splice(item + 2, 0, currentItem);
          this.form[param + 'Data'].splice(item, 1);
        }
      });
      this.resetTableSelectKey(param);
    },
    addMsgLink() {
      this.form.msgLinkData.push({
        id: generateId()
      });
    },
    msgEventHandleOk() {
      let record = JSON.parse(JSON.stringify(this.msgEventRecode));
      this.form.msgEventData[this.msgEventIndex].eventHandler = record.eventHandler;
      this.form.msgEventData[this.msgEventIndex].style = record.style;
      this.msgEventModalVisible = false;
    },
    saveForm() {
      console.log('保存表单');
      this.$refs.form.validate(valid => {
        if (valid) {
          this.beforeSaveReq();
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    beforeSaveReq() {
      let bean = JSON.parse(JSON.stringify(this.form));
      bean.callbackJson = JSON.stringify({
        adjusted: true,
        events: bean.msgEventData
      });
      if (bean.dtJumpType == 'multi') {
        bean.dtUri = '';
        bean.dtUriTitle = '';
        bean.dtBtnJsonList = JSON.stringify(bean.msgLinkData);
      } else {
        bean.dtBtnOrientation = null;
        bean.dtBtnJsonList = null;
      }
      bean.messageInteface = this.form.messageInteface.join(';');
      bean.displayMask = bean.displayMask ? 1 : 0;
      bean.autoTimeCloseWin = bean.autoTimeCloseWin ? 1 : 0;
      if (!bean.systemUnitId) {
        bean.systemUnitId = this._$USER.systemUnitId;
      }
      bean.msgEventData = JSON.stringify(bean.msgEventData);
      this.saveFormData(bean);
    },
    saveFormData(bean) {
      let _this = this;
      $axios
        .post('/json/data/services', {
          serviceName: 'messageTemplateService',
          methodName: 'saveBean',
          args: JSON.stringify([bean]),
          validate: false
        })
        .then(({ data }) => {
          if (data.code == 0) {
            this.$message.success('保存成功');
            if (this.$evtWidget) {
              let options = this.$evtWidget.getDataSourceProvider().options;
              this.$evtWidget.refetch && this.$evtWidget.refetch(options);
            }
            if (this.$dialogWidget) {
              this.$dialogWidget.close();
            }
          } else {
            this.$message.error(data.msg || '保存失败');
          }
        })
        .catch(err => {
          let data = err && err.response && err.response.data;
          this.$message.error(data.msg || '保存失败');
        });
    },
    editButtonConfiguration(record, index) {
      // if (record.type == 'customize') {
      this.msgEventIndex = index;
      this.msgEventRecode = JSON.parse(JSON.stringify(record));
      this.msgEventModalVisible = true;
      // }
    },
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    }
  }
};
</script>
