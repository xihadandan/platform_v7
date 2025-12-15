import { generateId } from '@framework/vue/utils/util';
import { notification } from 'ant-design-vue';

// 打开消息详情弹框（弃用）
export const messageDetailModal = function (options) {
  const _this = this;
  options.isModal = true;
  options.relatedBtnInButtons = true;
  options.displayLocation = 'message-detail';
  let Modal = Vue.extend({
    template: `<a-modal dialogClass="pt-modal" :title="title" :visible="visible" width="900px" :bodyStyle="{padding: '12px 0 0'}" :maskClosable="false" ok-text="确定" cancel-text="取消" @ok="handleOk" @cancel="handleCancel">
      <div style="height: 530px; overflow:auto">
        <MessageDetail ref="detailRef" :data="messageData" :options="options"></MessageDetail>
      </div>
      <template slot="footer">
        <MessageButton ref="buttonsRef" :data="messageData" :options="options" ></MessageButton>
      </template>
      </a-modal>`,
    components: {
      MessageDetail: () => import('./message-detail.vue'),
      MessageButton: () => import('./message-buttons.vue')
    },
    data: function () {
      let messageData = {};
      if (options.messageData) {
        _.each(options.messageData, (item, index) => {
          messageData[_.camelCase(index)] = item;
        });
      }
      return { options, messageData, visible: true };
    },
    created() { },
    computed: {
      title() {
        return options.title || '消息';
      }
    },
    mounted() {
      if (!this.options.getModalSelf) {
        this.options.getModalSelf = this.getSelf;
      }
      if (!this.options.getDetailRef) {
        this.options.getDetailRef = this.getDetailRef;
      }
    },
    methods: {
      getSelf() {
        return this;
      },
      getDetailRef() {
        return this.$refs.detailRef;
      },
      handleCancel() {
        this.visible = false;
        this.$destroy();
      },
      handleOk() {
        this.visible = false;
        this.$destroy();
      }
    }
  });
  let modal = new Modal();
  modal.$mount();
};
// 打开全部消息弹框（弃用）
export const messageListModal = function (options) {
  const _this = this;
  let Modal = Vue.extend({
    style: import('./msg.less'),
    template: `<a-modal :footer="null" class="myMsgSettingModal" :visible="visible" width="1200px" :bodyStyle="{padding: '0'}" :maskClosable="false"  @cancel="handleCancel">
        <template #title>
          <div class="flex title_tab">
            <div :class="{'active':activeKey == '1'}" @click="changeTab('1')">
              <Icon type="iconfont icon-xmch-wodexiaoxi"></Icon>
                消息通知
            </div>
            <div  :class="{'active':activeKey == '2'}" @click="changeTab('2')">
              <Icon type="iconfont icon-ptkj-tijiaofabufasong"></Icon>
              已发送
            </div>
          </div>
        </template>
        <div style="height: 700px; overflow:auto">
          <iframe  v-show="activeKey == '1'" :src="'/webpage/pt-message-mgr/156003355070562304'" class="msg-list-iframe"></iframe>
          <iframe  v-show="activeKey == '2'" :src="'/webpage/pt-message-mgr/157781665161150464'" class="msg-list-iframe"></iframe>
        </div>
      </a-modal>`,
    components: {},
    data: function () {
      return { title: '消息通知', visible: true, activeKey: '1' };
    },
    created() { },
    computed: {},
    methods: {
      changeTab(tab) {
        this.activeKey = tab;
      },
      handleCancel() {
        this.visible = false;
        this.$destroy();
      }
    }
  });
  let modal = new Modal();
  modal.$mount();
};
// 打开消息设置弹框
export const messageSettingModal = function (options) {
  const _this = this;
  let Modal = Vue.extend({
    style: import('./msg.less'),
    template: `<a-modal :title="title" class="myMsgSettingModal" :visible="visible" width="700px" :bodyStyle="{padding: '24px 10px'}" :maskClosable="false" @cancel="handleCancel">
      <div style="height: 530px; overflow:auto">
        <MessageSetting ref="settingRef"></MessageSetting>
      </div>
      <template slot="footer">
        <a-button type="primary" @click="handleOk">保存</a-button>
        <a-button @click="handleReset">恢复默认</a-button>
        <a-button @click="handleCancel">取消</a-button>
      </template>
      </a-modal>`,
    components: { MessageSetting: () => import('./message-online-setting.vue') },
    data: function () {
      return { visible: true, title: '消息通知设置' };
    },
    created() { },
    computed: {},
    methods: {
      handleCancel() {
        this.visible = false;
        this.$destroy();
      },
      handleOk() {
        this.$refs.settingRef.save(() => {
          this.visible = false;
          this.$destroy();
        });
      },
      handleReset() {
        this.$refs.settingRef.reset(() => {
          this.visible = false;
          this.$destroy();
        });
      }
    }
  });
  let modal = new Modal();
  modal.$mount();
};

// socket打开消息通知提醒框
export const messageNotificationOpen = function (options) {
  const message = getMessage();
  let obj = JSON.parse(message.body);
  if (obj.type == 'inboxOnLine') {
    inboxOnLineNotificationOpen(options, obj);
  } else if (obj.type == 'inboxOffLine') {
    inboxOffLineNotificationOpen(options, obj);
  } else if (obj.type == 'countOffLine') {
    // 存在多角色权限时角标数？
  }
};

export const inboxOnLineNotificationOpen = function (options, obj) {
  import('./message-notification.vue');
  _.assign(options, obj, {
    key: generateId(),
    notification: notification,
    size: 'small',
    displayLocation: 'message-modal'
  });
  let title = options.data.subject;
  const prop = {
    props: {
      messageData: options.data,
      options: options
    }
  };
  notification.open({
    message: `${title}`,
    description: h => {
      return h('message-notification', { ...prop });
    },
    placement: 'bottomRight',
    key: options.key,
    duration: null
  });
  // 刷新主导航徽标
  window.$app.pageContext.emitEvent(`WidgetMenu:refreshMenuBadgeNumberByCode`, { code: 'HeaderOnlineMsg' });
};

export const inboxOffLineNotificationOpen = function (options, obj) {
  const key = generateId();
  notification.open({
    message: '消息通知',
    description: h => {
      return h(
        'div',
        {
          style: 'color:#666'
        },
        [
          h('span', '您有'),
          h(
            'span',
            {
              style: 'color:#f00;margin: 0 5px;font-size:16px;'
            },
            obj.data
          ),
          h('span', '条新消息')
        ]
      );
    },
    btn: h => {
      return h(
        'a-button',
        {
          props: {
            type: 'primary',
            size: 'small'
          },
          on: {
            click: () => {
              // 消息管理，全部消息
              window.open('/webpage/pt-message-mgr/156058359382933504');
              notification.close(key);
            }
          }
        },
        '查看'
      );
    },
    placement: 'bottomRight',
    key: key
  });
};

// 设置为已读
export const readMsgService = function (uuids, successCallback) {
  $axios.post('/message/content/read', { uuids: uuids }).then(({ data }) => {
    if (data.code == 0) {
      if (typeof successCallback == 'function') {
        successCallback();
      }
    }
  });
};
export const presetEventsInfo = {
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
      icon: '',
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
      icon: '',
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
