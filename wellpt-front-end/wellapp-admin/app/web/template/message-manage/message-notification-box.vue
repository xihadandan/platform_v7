<!-- 消息通知提示框 -->
<template>
  <div>
    <message-list-modal v-model="visible" ref="messgageListRef"></message-list-modal>
    <message-detail-modal ref="messgageDetailRef"></message-detail-modal>
    <maskComponent v-model="maskVisible" @onClose="maskClose"></maskComponent>
    <audio ref="audio" style="display: none">
      <source src="/static/images/dingdingSound.mp3" />
    </audio>
  </div>
</template>
<script type="text/babel">
import { generateId, deepClone, getCookie } from '@framework/vue/utils/util';
import { notification } from 'ant-design-vue';
import MessageListModal from './message-list-modal.vue';
import MessageDetailModal from './message-detail-modal.vue';
import MessageNotification from './message-notification.vue';
import maskComponent from '@pageAssembly/app/web/widget/commons/mask';
import { each } from 'lodash';
// import { Stomp } from '~/app/public/js/websocket/js/stomp.js';
// import SockJS from '~/app/public/js/websocket/js/sockjs.js';
import { Stomp } from '@pageAssembly/app/public/js/websocket/js/stomp.js';
import SockJS from '@pageAssembly/app/public/js/websocket/js/sockjs.js';
export default {
  name: 'MessageNotificationBox',
  inject: ['pageContext'],
  props: {},
  components: {
    MessageNotification,
    MessageListModal,
    MessageDetailModal,
    maskComponent
  },
  data() {
    return {
      visible: false,
      maskVisible: false,
      maskClosable: false,
      keys: [], // 打开的消息框key,
      durationTime: 4.5 // 自动关闭时间
    };
  },
  computed: {},
  mounted() {
    setTimeout(() => {
      this.websocketConfig();
    }, 200);
  },
  methods: {
    websocketConfig() {
      let _this = this;
      /*
       * 1. 连接url为endpointChat的endpoint,对应后台WebSoccketConfig的配置
       * 2. SockJS 所处理的URL 是 "http://" 或 "https://" 模式，而不是 "ws://" or  "wss://"
       */
      let backendUrl = getCookie('backend.url');
      // 消息接收websocket
      _this.socket = new SockJS(backendUrl + '/wellSocket?jwt=' + getCookie('jwt'));
      // 获取 STOMP 子协议的客户端对象
      _this.stompClient = Stomp.over(_this.socket);
      /*
       * 1. 获取到stomp 子协议后，可以设置心跳连接时间，认证连接，主动断开连接
       * 2，连接心跳有的版本的stomp.js 是默认开启的，这里我们不管版本，手工设置
       * 3. 心跳是双向的，客户端开启心跳，必须要服务端支持心跳才行
       * 4. heartbeat.outgoing 表示客户端给服务端发送心跳的间隔时间
       * 5. 客户端接收服务端心跳的间隔时间，如果为0 表示客户端不接收服务端心跳
       */
      _this.stompClient.heartbeat.outgoing = 10000;
      _this.stompClient.heartbeat.incoming = 10000;
      /*
       * 1. stompClient.connect(headers, connectCallback, errorCallback);
       * 2. headers表示客户端的认证信息,多个参数 json格式存,这里简单用的httpsessionID，可以根据业务场景变更
       *    这里存的信息，在服务端StompHeaderAccessor 对象调用方法可以取到
       * 3. connectCallback 表示连接成功时（服务器响应 CONNECTED 帧）的回调方法；
       *    errorCallback 表示连接失败时（服务器响应 ERROR 帧）的回调方法，非必须；
       */
      var headers = {};

      _this.stompClient.connect(headers, function (frame) {
        // console.log('Connected111: ' + frame);
        /*
         * 1. 订阅服务，订阅地址为服务器Controller 中的地址
         * 2. 如果订阅为公告，地址为Controller 中@SendTo 注解地址
         * 3. 如果订阅为私信，地址为setUserDestinationPrefix 前缀+@SendToUser注解地址
         *    或者setUserDestinationPrefix 前缀 + controller的convertAndSendToUser地址一致
         * 4. 这里演示为公告信息，所有订阅了的用户都能接受
         */
        _this.stompClient.subscribe('/user/topic/callBack', function (message) {
          _this.socketCallMsg(message.headers);
          _this.socketIn(message);
        }),
          function (error) {
            console.log('STOMP: ' + error);
            //重复执行 链接请求
            // setTimeout(websocketConfig, 10000);
            console.log('STOMP: Reconnecting in 10 seconds');
          },
          function (close) {
            //关闭
            console.log('STOMP close: ' + close);
          };
      });
    },
    // 回传消息
    socketCallMsg(headers) {
      let _this = this;
      // console.log('callBackMsg:', headers);
      if (headers && headers.callBackUrl) {
        /**
         *  1. 第一个参数 url 为服务器 controller中 @MessageMapping 中匹配的URL，字符串，必须参数；
         *  2. headers 为发送信息的header，json格式，JavaScript 对象，
         *     可选参数,可以携带消息头信息，也可以做事务，如果没有，传{}
         *  3. body 为发送信息的 body，字符串，可选参数
         */
        _this.stompClient.send(headers.callBackUrl, {}, headers.callBackJson);
      }
    },
    socketIn(message, options = {}) {
      let obj = JSON.parse(message.body);
      console.log(obj);
      if (obj.type == 'inboxOnLine') {
        this.inboxOnLineNotificationOpen(obj);
      } else if (obj.type == 'inboxOffLine') {
        this.inboxOffLineNotificationOpen(obj);
      } else if (obj.type == 'countOffLine') {
        // 存在多角色权限时角标数？
      }
      this.playAudio();
      this.refreshMessageData();
    },
    inboxOnLineNotificationOpen(obj) {
      if (obj.data.isOnlinePopup == 'Y') {
        let options = JSON.parse(obj.data.messageParm);
        let events = JSON.parse(options.callbackJson);
        _.assign(options, obj, {
          key: generateId(),
          notification: notification,
          size: 'small',
          displayLocation: 'message-modal',
          notificationBoxSelf: this
        });
        let modal = undefined;
        let title = options.data.subject || options.data.SUBJECT;
        const prop = {
          props: {
            messageData: options.data,
            options: options,
            widgetContext: this.pageContext.vueInstance
          },
          on: {
            close: () => {
              if (modal) {
                modal.destroy();
              } else {
                this.close(options.key, true);
              }
            },
            refresh: this.refreshMessageData
          }
        };
        // 浏览器中间弹出
        if (options.popupPosition == '2') {
          modal = this.$info({
            title: `${title}`,
            content: h => {
              return h('message-notification', { ...prop });
            },
            mask: options.displayMask,
            centered: true,
            closable: true,
            icon: '',
            width: this.getStyle(options).width ? this.getStyle(options).width : '',
            height: this.getStyle(options).height ? this.getStyle(options).height : '',
            class: 'message-notification-modal',
            footer: null,
            onCancel: () => {
              modal.destroy();
            }
          });
          if (options.autoTimeCloseWin) {
            setTimeout(() => {
              modal.destroy();
            }, this.durationTime * 1000);
          }
        } else {
          notification.open({
            class: 'notification_' + options.key,
            message: `${title}`,
            description: h => {
              return h('message-notification', { ...prop });
            },
            placement: 'bottomRight',
            key: options.key,
            duration: options.autoTimeCloseWin ? this.durationTime : null, //null时不自动关闭
            style: this.getStyle(options),
            onClose: () => {
              this.close(options.key, true);
            }
          });
          this.maskChange(options.displayMask, options);
          this.keys.push(options.key);
        }
      }
    },
    inboxOffLineNotificationOpen(obj) {
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
                  this.visible = true;
                  notification.close(key);
                  this.maskVisible = false;
                }
              }
            },
            '查看'
          );
        },
        placement: 'bottomRight',
        key: key,
        duration: null,
        onClose: () => {
          this.close(key, true);
        }
      });
      this.keys.push(key);
    },
    // 新消息声音提醒
    playAudio() {
      this.$clientCommonApi.getSystemParamValue('message.sound.mode', isOpen => {
        if (isOpen == 'on') {
          //on开启、off或其他未开启
          this.$refs.audio.play();
        }
      });
    },
    maskChange(visible, options) {
      this.maskVisible = visible;
    },
    getStyle(options) {
      let style = {};
      if (options.popupSize == '2') {
        style.width = options.popupWidth;
        style.height = options.popupHeight;
      }
      // if (options.popupPosition == '2') {
      //   style.right = '50%';
      //   // style.marginLeft = 'calc(100)'
      // }
      return style;
    },
    close(key, isCloseMask) {
      notification.close(key);
      this.keys.splice(this.keys.indexOf(key), 1);
      if (isCloseMask) {
        this.maskVisible = false;
      }
    },
    maskClose() {
      each(this.keys, item => {
        this.close(key);
      });
    },
    refreshMessageData() {
      // 刷新主导航徽标、未读消息、已读消息
      setTimeout(() => {
        window.$app.pageContext.emitEvent(`WidgetMenu:refreshMenuBadgeNumberByCode`, { code: 'HeaderOnlineMsg' });
        window.$app.pageContext.emitEvent(`WidgetMenu:refreshHeaderOnlineMsgList`);
        window.$app.pageContext.emitEvent(`refetchMessageInBoxManangeTable_read`);
        window.$app.pageContext.emitEvent(`refetchMessageInBoxManangeTable_unRead`);
        window.$app.pageContext.emitEvent(`refetchMessageBoxClassifyData`);
      }, 500);
    }
  }
};
</script>
<style lang="less">
.message-notification-modal {
  .ant-modal-confirm-btns {
    display: none;
  }
  .ant-modal-confirm-body {
    > i {
      display: none;
    }
    .anticon + .ant-modal-confirm-title + .ant-modal-confirm-content {
      margin-left: 0;
    }
  }
  .ant-modal-body {
    padding: 20px 20px 16px;
  }
}
</style>
