<template>
  <a-dropdown placement="bottomRight" :trigger="['click']" v-model="visible" @visibleChange="onVisibleChange">
    <slot></slot>
    <div slot="overlay">
      <a-card style="width: 300px" size="small" :bodyStyle="{ padding: 0 }" class="myMsgWrap">
        <a-button size="small" slot="extra" @click="settingModalShow"><Icon type="pticon iconfont icon-ptkj-shezhi"></Icon></a-button>
        <!-- <PerfectScrollbar style="height: 600px"> -->
        <ul class="msgList">
          <template v-for="(item, index) in recentTenList">
            <li v-if="index < 4" :key="item.uuid" @click="dataModalShow(item)">
              <div class="clearfix">
                <div class="tit fl">
                  <span :style="{ background: item.iconBg }" class="msgIcon">
                    <Icon :type="item.icon" />
                  </span>
                  {{ item.classifyName || item.senderName }}
                </div>
                <div class="time fr">{{ item.receivedTime }}</div>
              </div>
              <div class="msgSubject">{{ item.subject }}</div>
              <div class="content">
                <div class="text w-ellipsis" v-html="item.body"></div>
              </div>
            </li>
          </template>
        </ul>
        <a-empty v-if="recentTenList.length == 0" description="没有新消息！" class="msg-tip" />
        <!-- </PerfectScrollbar> -->
        <template slot="actions">
          <a-button type="link" :disabled="recentTenList.length == 0" @click="readHandle">全部标记为已读</a-button>
          <a-button type="link" @click="moreHandle">
            查看全部
            <a-icon type="double-right" />
          </a-button>
        </template>
        <message-list-modal ref="messgageListRef"></message-list-modal>
        <message-online-setting-modal :show="settingShow" @close="closeSetting"></message-online-setting-modal>
        <message-detail-modal ref="messgageDetailRef"></message-detail-modal>
      </a-card>
    </div>
  </a-dropdown>
</template>
<script type="text/babel">
import {
  // messageDetailModal,
  // messageSettingModal,
  readMsgService
  // messageListModal
} from '@admin/app/web/template/message-manage/message-utils.js';
import MessageListModal from '@admin/app/web/template/message-manage/message-list-modal.vue';
import MessageOnlineSettingModal from '@admin/app/web/template/message-manage/message-online-setting-modal.vue';
import MessageDetailModal from '@admin/app/web/template/message-manage/message-detail-modal.vue';
import moment from 'moment';
export default {
  name: 'HeaderOnlineMsg',
  inject: ['pageContext'],
  props: {},
  components: {
    MessageListModal,
    MessageOnlineSettingModal,
    MessageDetailModal
  },
  computed: {},
  data() {
    return {
      visible: false,
      recentTenList: [],
      msgIcons: [],
      defalutIcon: 'pticon iconfont icon-ptkj-xiaoxitongzhibiaoti',
      settingShow: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.getMsgIcons();
    this.queryRecentTenLists();
    this.checkLicense();
    this.pageContext.handleEvent(`WidgetMenu:refreshHeaderOnlineMsgList`, () => {
      this.queryRecentTenLists();
    });
  },
  methods: {
    queryRecentTenLists() {
      $axios.get('/message/inbox/queryRecentTenLists').then(({ data }) => {
        if (data.code == 0) {
          this.recentTenList = _.map(data.data, item => {
            return this.iconSet(item);
          });
        }
      });
    },
    getMsgIcons() {
      $axios.get('/proxy/api/message/classify/queryList', { params: { name: '', message: '' } }).then(({ data }) => {
        if (data.code == 0) {
          this.msgIcons = data.data;
        }
      });
    },
    iconSet(item) {
      let classify = _.find(this.msgIcons, { uuid: item.classifyUuid });
      if (!classify) {
        classify = {};
      }
      item.icon = classify.icon || this.defalutIcon;
      item.iconBg = classify.iconBg || 'var(--w-primary-color)';
      return item;
    },
    settingModalShow() {
      this.settingShow = true;
      this.visible = false;
      // messageSettingModal();
    },
    closeSetting() {
      this.settingShow = false;
    },
    dataModalShow(item) {
      this.$refs.messgageDetailRef.showModal({
        messageData: item,
        title: '来自 ' + item.senderName + '(' + item.receivedTime + ')',
        readOnly: true,
        pageContext: this.pageContext
      });
      this.visible = false;
      // messageDetailModal({
      //   messageData: item,
      //   title: '来自 ' + item.senderName + '(' + item.receivedTime + ')',
      //   readOnly: true,
      //   pageContext: this.pageContext
      // });
      if (!item.isread) {
        readMsgService([item.uuid], () => {
          this.queryRecentTenLists();
          this.pageContext.emitEvent(`WidgetMenu:refreshMenuBadgeNumberByCode`, { code: 'HeaderOnlineMsg' });
          this.pageContext.emitEvent(`refetchMessageInBoxManangeTable_read`);
          this.pageContext.emitEvent(`refetchMessageInBoxManangeTable_unRead`);
        });
      }
    },
    allModalShow() {
      this.$refs.messgageListRef.showModal();
      this.visible = false;
      // 模块，消息管理，全部消息
      // window.open('/webpage/pt-message-mgr/156058359382933504');
      // messageListModal({});
    },
    readHandle() {
      $axios.put('/message/inbox/updateToReadState').then(({ data }) => {
        if (data.code == 0) {
          this.$message.success('标记成功！');
          this.queryRecentTenLists();
          this.pageContext.emitEvent(`WidgetMenu:refreshMenuBadgeNumberByCode`, { code: 'HeaderOnlineMsg' });
          this.pageContext.emitEvent(`refetchMessageInBoxManangeTable_read`);
          this.pageContext.emitEvent(`refetchMessageInBoxManangeTable_unRead`);
        }
      });
    },
    moreHandle() {
      this.allModalShow();
    },
    onVisibleChange(visible) {
      if (!this.visible) {
        this.visible = visible;
      }
    },
    checkLicense() {
      let userId = this._$USER.userId;
      let profileKey = `profile_${userId}`;
      let profile = localStorage.getItem(profileKey);
      if (profile == 'prd') {
        return;
      }
      $axios.get(`/proxy/api/security/license/info`).then(({ data: result }) => {
        if (result.data) {
          if (result.data.profile == 'prd') {
            localStorage.setItem(profileKey, 'prd');
          } else {
            this.showDevProfileTips(profileKey, profile, result.data);
          }
        }
      });
    },
    showDevProfileTips(profileKey, profile, devProfile) {
      let runningDay = devProfile.runningDay || 10;
      let tipTime = (devProfile.startTime && moment(devProfile.startTime)) || moment();
      tipTime.add(runningDay - 2, 'days');
      let currentTime = moment();
      if (currentTime.isAfter(tipTime)) {
        this.$notification.warning({
          message: '开发版(未授权)',
          description: `系统将于${tipTime.add(2, 'days').format('YYYY-MM-DD HH:mm:ss')}退出运行？`,
          duration: null
        });
      } else if (profile != 'notip') {
        this.$confirm({
          icon: 'warning',
          title: '开发版(未授权)',
          content: `当前系统为开发版本，请使用授权版本？`,
          cancelText: '不再提示',
          onOk() {},
          onCancel() {
            localStorage.setItem(profileKey, 'notip');
          }
        });
      }
    }
  }
};
</script>
<style lang="less">
/* 头部组件 - 我的消息弹窗 */
.myMsgWrap {
  .ant-card-actions {
    > li {
      margin: 0;
    }
  }
}
.myMsgWrap .msgList {
  list-style: none;
  padding-inline-start: 0;
  margin-block-end: 0;
}
.myMsgWrap .msgList li {
  width: 100%;
  padding: 10px 20px;
  border-bottom: 1px #eeeeee solid;
  cursor: pointer;
}
.myMsgWrap .msgList li .tit {
  word-break: break-all;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1;
  font-size: 12px;
  color: #999999;
  width: 50%;
  text-align: left;
}
.myMsgWrap .msgList li .tit .msgIcon {
  width: 24px;
  height: 24px;
  line-height: 24px;
  margin-right: 10px;
  color: #ffffff;
  vertical-align: middle;
  opacity: 0.75;
  font-weight: 500;
  border-radius: 50%;
  display: inline-block;
  text-align: center;
}
.myMsgWrap .msgList li .time {
  line-height: 24px;
  font-size: 10px;
  color: #999999;
}
.myMsgWrap .msgList li .msgSubject {
  margin-top: 8px;
  font-size: 14px;
  color: #333333;
  line-height: 1;
  text-align: left;
}
.myMsgWrap .msgList li .content {
  word-break: break-all;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-top: 6px;
  line-height: 18px;
  font-size: 12px;
  color: #666666;
  text-align: left;
  height: 36px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
}
.myMsgWrap .msgList li .content .text {
  width: 100%;
  line-height: 18px;
  display: flex;
}
.myMsgWrap .msgList li:hover {
  background: var(--w-primary-color-2);
}
.myMsgWrap .msgList li:hover .tit i {
  opacity: 1;
}
.myMsgWrap .msg-tip {
  padding: 100px 0;
}
.fl {
  float: left;
}
.fr {
  float: right;
}
</style>
