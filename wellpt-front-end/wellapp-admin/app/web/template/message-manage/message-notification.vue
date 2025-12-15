<template>
  <div class="message-notification">
    <Scroll :style="{'height': hasHeight?vHeight:'','max-height':vHeight,marginRight: '-12px',paddingRight: '12px'}">
      <div class="message-notification-body" v-html="form.body||form.BODY"></div>
      <UploadSimpleComponent
          :fileIds="attach"
          @change="attendChange"
          ref="fjRef"
          :editable="false"
          :separator=/[,;]/g
        ></UploadSimpleComponent>
      <div style="text-align: right" v-if="relatedTitle">
        <a-button type="link" @click="relatedHandle"><Icon type="pticon iconfont icon-ptkj-liucheng"></Icon>{{ relatedTitle }}</a-button>
      </div>
    </Scroll>
    <div style="margin-top: 12px;">
      <MessageButton ref="buttonsRef" :data="messageData" :options="options" @close="onClose" @refresh="onRefresh"></MessageButton>
    </div>
  </div>
</template>
<script type="text/babel">
import MessageButton from './message-buttons.vue';
import UploadSimpleComponent from '../common/upload-simple-component';
export default {
  name: 'MessageNotification',  
  inject: ['pageContext'],
  props: {
    messageData: {
      type: Object,
      defalut: () => {
        return {};
      }
    },
    options: {
      type: Object,
      defalut: () => {
        return {};
      }
    },
    widgetContext:Object
  },
  provide() {
    return {
      widgetContext:this.widgetContext
    };
  },
  components: {
    MessageButton,UploadSimpleComponent
  },
  data() {
    let messageData = this.messageData;
    return {
      form: messageData,
      attach: '',
      hasHeight: false
    };
  },
  computed: {
    vHeight(){
      let options = this.options;
      if (options.popupSize == '2') {
        if(options.popupHeight && options.popupHeight != 'auto'){
          this.hasHeight = true;
          return 'calc('+options.popupHeight+' - 100px)';
        }
      }
      this.hasHeight = false;
      return '400px';
    },
    messageParm() {
      let messageParm = this.form.MESSAGE_PARM || this.form.messageParm;
      return messageParm ? JSON.parse(messageParm) : undefined;
    },
    readOnly() {
      return this.options && this.options.readOnly;
    },
    relatedTitle() {
      if (this.messageParm) {
        return this.messageParm.relatedTitle;
      } else {
        return this.form.relatedTitle || this.form.RELATED_TITLE || '';
      }
    },
    relatedUrl() {
      let url = '';
      if (this.messageParm) {
        url = this.messageParm.relatedUrl;
      } else {
        url = this.form.relatedUrl || this.form.RELATED_URL || '';
      }
      if(url && url.indexOf('http') != 0 && this._$SYSTEM_ID){
        url = '/sys/'+ this._$SYSTEM_ID +'/_' + url;
      }
      return url;
    },
    relatedShow() {
      return this.relatedUrl && this.relatedTitle;
    }
  },
  mounted() {
    if (this.messageParm) {
      this.attach = this.messageParm.attach;
    } else if (this.form.attach) {
      this.attach = this.messageParm.attach;
    }
    if (this.attach) {
      this.$refs.fjRef.setValue(this.attach);
    }
    this.popupWidthHandle();
  },
  methods: {
    // 如果有配宽度，需要给notification设置宽度
    popupWidthHandle(){
      let options = this.options;
      if (options.popupSize == '2') {
        if(options.popupWidth && options.popupWidth != 'auto'){
          if(this.$root.$el && this.$root.$el.classList.contains('ant-notification')){
            this.$root.$el.style.width = options.popupWidth;
          }
        }
      }
    },
    relatedHandle() {
      window.open(this.relatedUrl);
    },
    onClose(){
      this.$emit('close');
    },
    onRefresh(){
      this.$emit('refresh');
    }
  }
};
</script>
<style lang="less">
.message-notification {
  & &-body {
    overflow: auto;
    word-break: break-all;
    img {
      width: 100%;
      height: 100%;
    }
  }
}
</style>
