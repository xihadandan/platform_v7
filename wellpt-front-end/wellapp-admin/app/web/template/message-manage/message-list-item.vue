<template>
  <div class="msg-list-card-item">
    <div class="table-msg-title">
      <template v-if="row.CLASSIFY_NAME != null">
        <span class="table-msg-cate" :style="{ background: msgCatBg }">{{ row.CLASSIFY_NAME }}</span>
      </template>
      {{ row.SUBJECT }}
      <template v-if="pageType == 'outBox'">
        <span class="table-msg-time">{{ row.SENT_TIME | getTime }}</span>
        <span class="table-msg-name">收件人：{{ row.RECIPIENT_NAME }}</span>
      </template>
      <template v-else>
        <span class="table-msg-time">{{ row.RECEIVED_TIME | getTime }}</span>
        <span class="table-msg-name">来自：{{ row.SENDER_NAME }}</span>
      </template>
    </div>
    <div class="table-msg-content" v-html="row.BODY"></div>
    <template v-if="isCheckbox">
      <slot name="selection"></slot>
    </template>
    <div class="underline-btn flex f_x_s">
      <div class="flex" style="width: calc(100% - 120px)">
        <!-- <div style="margin-right: 8px">
          <slot name="actions"></slot>
        </div> -->
        <div>
          <MessageButton ref="buttonsRef" :data="messageData" :options="buttonOptions"></MessageButton>
        </div>
      </div>
      <div v-if="relatedUrl">
        <a-button type="link" style="--w-button-padding-lr: 0" @click.stop="relatedHandle">
          <Icon type="pticon iconfont icon-ptkj-liucheng"></Icon>
          {{ relatedTitle }}
        </a-button>
      </div>
    </div>
    <message-detail-modal ref="messgageDetailRef"></message-detail-modal>
  </div>
</template>
<script type="text/babel">
import moment from 'moment';
import MessageDetailModal from './message-detail-modal.vue';
import MessageButton from './message-buttons.vue';
export default {
  name: 'MessageListItem',
  props: ['row', 'rowIndex', 'invokeJsFunction', 'invokeDevelopmentMethod', 'eventWidget'],
  inject: ['pageContext', 'vPageState', '$event'],
  data() {
    let messageData = {};
    if (this.row) {
      _.each(this.row, (item, index) => {
        messageData[_.camelCase(index)] = item;
      });
    }
    let tableWidget = this.eventWidget ? this.eventWidget() : {};
    return {
      messageData,
      isCheckbox: false,
      defaultIconBg: '#64B3EA',
      buttonOptions: {
        messageData,
        size: 'small',
        displayLocation: 'message-list',
        getTableSelf: this.eventWidget
      },
      tableWidget
    };
  },
  components: {
    MessageButton,
    MessageDetailModal
  },
  filters: {
    getTime(time) {
      let now = moment();
      let _time = moment(time);
      if (_time.isSame(now, 'day')) {
        return _time.format('HH:mm');
      } else if (_time.isSame(now, 'year')) {
        return _time.format('MM-DD HH:mm');
      } else {
        return _time.format('yyyy-MM-DD HH:mm');
      }
    }
  },
  computed: {
    disabled() {
      return this.isCheckbox;
    },
    msgCatBg() {
      let msgClassifyList = window.localStorage.getItem('msgClassifyList');
      if (msgClassifyList) {
        if (this.row.CLASSIFY_UUID) {
          let classifyItem = _.find(JSON.parse(msgClassifyList), { uuid: this.row.CLASSIFY_UUID });
          if (classifyItem) {
            return classifyItem.iconBg || this.defaultIconBg;
          }
        }
      }
      return this.defaultIconBg;
    },
    messageParm() {
      let messageParm = this.row.MESSAGE_PARM || this.row.messageParm;
      return messageParm ? JSON.parse(messageParm) : undefined;
    },
    relatedTitle() {
      if (this.messageParm) {
        return this.messageParm.relatedTitle;
      } else {
        return this.row.relatedTitle || this.row.RELATED_TITLE || '';
      }
    },
    relatedUrl() {
      let url = '';
      if (this.messageParm) {
        url = this.messageParm.relatedUrl;
      } else {
        url = this.row.relatedUrl || this.row.RELATED_URL || '';
      }
      if (url && url.indexOf('http') != 0 && this._$SYSTEM_ID) {
        url = '/sys/' + this._$SYSTEM_ID + '/_' + url;
      }
      return url;
    },
    pageType() {
      if (this.eventWidget && this.eventWidget().pageType) {
        return this.eventWidget().pageType;
      }
      return '';
    }
  },
  mounted() {
    if (this.tableWidget) {
      this.isCheckbox = this.tableWidget.isBatchHandle;
    }
  },
  methods: {
    moment,
    relatedHandle() {
      window.open(this.relatedUrl);
    }
  }
};
</script>
<style lang="less">
.msg-list-card-item {
  padding: 15px 20px 40px;
  position: relative;
  .table-msg-title {
    min-height: 20px;
    color: #333333;
    font-size: var(--w-font-size-lg);
    line-height: 1;
    margin-bottom: 10px;
    span {
      color: #999999;
      font-size: var(--w-font-size-base);
      float: right;
      vertical-align: middle;
      line-height: 1;
      padding: 0;
      &.table-msg-name {
        margin-right: 20px;
        color: #666666;
      }
      &.table-msg-cate {
        color: #ffffff;
        font-size: var(--w-font-size-sm);
        margin-right: 7px;
        padding: 0 5px;
        border-radius: 4px;
        float: none;
        height: 22px;
        line-height: 22px;
        display: inline-block;
        margin-top: -3px;
      }
    }
  }
  .table-msg-content {
    color: #666666;
    font-size: var(--w-font-size-base);
    line-height: 20px;
    margin-bottom: 10px;
    height: 40px;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    white-space: normal;
    word-break: break-all;

    img {
      max-width: 100%;
    }
  }
  .underline-btn {
    position: absolute;
    bottom: 0px;
    left: 0px;
    width: 100%;
    height: 40px;
    z-index: 4;
    line-height: 40px;
    padding: 0px 16px;
    box-sizing: border-box;
    text-align: left;
    background: #fafafa;

    .msg-relatedUrl {
      float: right;
    }
  }
  .ant-checkbox-wrapper {
    position: absolute;
    top: -2px;
    left: -2px;
    width: 100%;
    height: 100%;
    display: block;
    content: '';
    z-index: 10;
    .ant-checkbox-inner {
      width: 36px;
      height: 36px;
      font-family: iconfont;
      font-size: 36px;
      color: #e8e8e8;
      border: 0;
      background-color: transparent;
      padding-top: 4px;
      padding-left: 2px;

      &::before {
        content: '\e9c2';
      }
      &::after {
        content: none;
      }
      &:hover {
        border: 0;
      }
    }
    .ant-checkbox-checked {
      .ant-checkbox-inner {
        color: var(--w-primary-color);
      }
      &::after {
        border: 0;
      }
    }
  }
}
</style>
