<template>
  <div style="padding: 10px">
    <a-row style="padding: 10px">
      <a-button-group style="margin-left: 10px">
        <a-button @click="transferData" type="primary">开始迁移</a-button>
<!--        <a-button @click="reset">清理旧数据</a-button>-->
      </a-button-group>
    </a-row>
    <a-row>
      <a-textarea v-model="logInfo"></a-textarea>
    </a-row>
  </div>
</template>

<script>
  import { getCookie } from '@framework/vue/utils/util';

  import { Stomp } from '@pageAssembly/app/public/js/websocket/js/stomp.js';
  import SockJS from '@pageAssembly/app/public/js/websocket/js/sockjs.js';
  export default {
    name: "permission-transfer",
    data() {
      return {
        size: 'large',
        logInfo: '',
      };
    },
    mounted() {
      // setTimeout(() => {
      //   this.websocketConfig();
      // }, 200);
      this.websocketConfig();
    },
    methods: {

      transferData()
      {
        $axios
                .get(`/proxy/api/permission/transferData`, { params: {  } })
                .then(({ data }) => {})
                .catch(error => {});
      },

      websocketConfig() {
        let _this = this;
        debugger
        /*
         * 1. 连接url为endpointChat的endpoint,对应后台WebSoccketConfig的配置
         * 2. SockJS 所处理的URL 是 "http://" 或 "https://" 模式，而不是 "ws://" or  "wss://"
         */
        let backendUrl = getCookie('backend.url');
        // 消息接收websocket
        _this.socket = new SockJS(backendUrl + '/wellSocket?jwt=' + getCookie('jwt'));
        // 获取 STOMP 子协议的客户端对象
        _this.stompClient = Stomp.over(_this.socket);
        debugger
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

      },
      socketIn(message, options = {}) {
        console.log(message)
        let obj = JSON.parse(message.body);
        if (obj.type == 'permissionTransfer') {
          if (this.logInfo.length < 10000) {
            this.logInfo = obj.data + '\r\n' + this.logInfo
          } else {
            this.logInfo =  obj.data
          }
        }

      },
    },
  };
</script>

<style scoped>

</style>
