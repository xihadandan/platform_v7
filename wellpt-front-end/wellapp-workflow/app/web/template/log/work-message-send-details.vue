<template>
  <div>
    <a-form-model
      class="basic-info"
      :model="logData"
      labelAlign="left"
      ref="basicForm"
      :rules="rules"
      :label-col="{ span: 7 }"
      :wrapper-col="{ span: 17, style: { textAlign: 'left' } }"
      :colon="false"
    >
      <a-form-model-item label="消息发送人" prop="senderName">
        {{ logData.senderName }}
      </a-form-model-item>
      <a-form-model-item label="推送时间" prop="sendTime">
        {{ logData.sendTime }}
      </a-form-model-item>
      <a-form-model-item label="推送结果" prop="sendResultCode">
        <span v-if="logData.sendResultCode == 0">成功</span>
        <span v-else-if="logData.sendResultCode == 6">部分成功</span>
        <span v-else>失败</span>
      </a-form-model-item>
      <a-tabs v-if="logData.messageOutboxUuid">
        <a-tab-pane v-if="logData.sendWay.includes('ON_LINE')" tab="在线消息" key="ON_LINE">
          <a-table :columns="onlineMsgColumns" :data-source="onlineMsgs"></a-table>
        </a-tab-pane>
        <a-tab-pane v-if="logData.sendWay.includes('SMS')" tab="手机短信" key="SMS">
          <a-table :columns="shortMsgColumns" :data-source="shortMsgs">
            <template slot="sendStatusSlot" slot-scope="text, record">
              <span v-if="text == '1'">发送成功</span>
              <span v-else-if="text == '2'">发送失败</span>
              <span v-else-if="text == '3'">重发成功</span>
              <span v-else>待发送</span>
            </template>
          </a-table>
        </a-tab-pane>
      </a-tabs>
    </a-form-model>
  </div>
</template>

<script>
export default {
  inject: ['$event'],
  data() {
    let logData = (this.$event && this.$event.meta) || {};
    return {
      logData,
      onlineMsgs: [],
      shortMsgs: [],
      onlineMsgColumns: [
        {
          title: '接收人',
          dataIndex: 'recipientName'
        },
        {
          title: '内容',
          dataIndex: 'body'
        }
      ],
      shortMsgColumns: [
        {
          title: '接收人',
          dataIndex: 'recipientName'
        },
        {
          title: '接收人手机号码',
          dataIndex: 'recipientMobilePhone'
        },
        {
          title: '内容',
          dataIndex: 'body'
        },
        {
          title: '状态',
          dataIndex: 'sendStatus',
          scopedSlots: { customRender: 'sendStatusSlot' }
        }
      ]
    };
  },
  mounted() {
    if (this.logData.messageOutboxUuid) {
      if (this.logData.sendWay.includes('ON_LINE')) {
        this.loadOnlineMsgs(this.logData.messageOutboxUuid);
      }
      if (this.logData.sendWay.includes('SMS')) {
        this.loadShortMsgs(this.logData.messageOutboxUuid);
      }
    }
  },
  methods: {
    loadOnlineMsgs(messageOutboxUuid) {
      $axios.get(`/proxy/api/message/inbox/listByMessageOutboxUuid?messageOutboxUuid=${messageOutboxUuid}`).then(({ data: result }) => {
        if (result.data) {
          this.onlineMsgs = result.data;
        }
      });
    },
    loadShortMsgs(messageOutboxUuid) {
      $axios
        .get(`/proxy/api/message/shortMessage/listByMessageOutboxUuid?messageOutboxUuid=${messageOutboxUuid}`)
        .then(({ data: result }) => {
          if (result.data) {
            this.shortMsgs = result.data;
          }
        });
    }
  }
};
</script>

<style></style>
