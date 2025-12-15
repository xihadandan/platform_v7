<template>
  <div>
    <div class="new-flow-bot-rule">
      <w-select
        :value="value"
        :formDataFieldName="name"
        :formData="formData"
        :options="options"
        :getPopupContainer="getPopupContainer"
        @change="onChange"
      />
      <a-button type="primary" @click="handleBotRule">查看</a-button>
    </div>
    <modal
      title="编辑单据转换规则"
      :width="1100"
      v-model="visible"
      :bodyStyle="{
        maxHeight: '524px',
        overflowY: 'hidden',
        padding: '12px 20px'
      }"
    >
      <template slot="content">
        <bot-rule-config-details class="new-flow-bot-rule-info" v-if="visible" />
      </template>
    </modal>
  </div>
</template>

<script>
import WSelect from '../components/w-select';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import BotRuleConfigDetails from '@admin/app/web/template/bot-rule/bot-rule-config-details.vue';

export default {
  name: 'NewFlowBotRule',
  props: {
    value: {
      type: String
    },
    name: {
      type: String,
      default: ''
    },
    formData: {
      type: Object,
      default: () => {}
    },
    options: {
      type: Array,
      default: () => []
    },
    getPopupContainer: {
      type: Function,
      default: triggerNode => {
        return triggerNode.closest('.ant-form');
      }
    }
  },
  components: {
    WSelect,
    Modal,
    BotRuleConfigDetails
  },
  data() {
    return {
      visible: false,
      event: {
        meta: undefined
      }
    };
  },
  provide() {
    return {
      $event: this.event,
      vPageState: {}
    };
  },
  methods: {
    onChange(value, option) {
      this.$emit('input', value);
      this.$emit('change', value, option);
    },
    handleBotRule() {
      console.log(this.value);
      if (this.value) {
        this.getBotRuleById(this.value).then(res => {
          this.event.meta = res;
          this.visible = true;
        });
      }
    },
    getBotRuleById(id = '') {
      const params = {
        args: JSON.stringify([id]),
        methodName: 'getById',
        serviceName: 'botRuleConfService',
        validate: true
      };

      return new Promise((resolve, reject) => {
        this.$axios
          .post('/json/data/services', {
            ...params
          })
          .then(res => {
            if (res.status === 200) {
              if (res.data && res.data.code === 0) {
                const data = res.data.data;
                resolve(data);
              }
            }
          });
      });
    }
  }
};
</script>

<style lang="less">
.new-flow-bot-rule {
  display: flex;
  align-items: center;
  .ant-btn {
    margin-left: 7px;
  }
}
.new-flow-bot-rule-info {
  .ant-row {
    display: flex;
    .ant-form-item-control-wrapper {
      flex: 1 1 auto;
    }
  }
}
</style>
