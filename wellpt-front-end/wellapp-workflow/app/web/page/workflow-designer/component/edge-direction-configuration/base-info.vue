<template>
  <!-- 流向属性-基本设置 -->
  <div>
    <a-form-model-item class="form-item-vertical" prop="name" label="名称">
      <a-input v-model="formData.name" @change="changeName">
        <template slot="addonAfter">
          <w-switch
            :checkedValue="true"
            :unCheckedValue="false"
            checkedChildren="自适应"
            unCheckedChildren="自适应"
            v-model="formData.autoUpdateName"
          />
          <w-i18n-input :target="formData" :code="formData.id + '.directionName'" v-model="formData.name" />
        </template>
      </a-input>
    </a-form-model-item>
    <template v-if="formData.type !== '3'">
      <a-form-model-item class="form-item-vertical" label="ID">
        {{ formData.id }}
      </a-form-model-item>
      <a-form-model-item class="form-item-vertical" label="连接线">
        <a-radio-group v-model="directionType" size="small" @change="changeDirectionType">
          <a-radio-button v-for="item in directionOptions" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-radio-button>
        </a-radio-group>
        <a-button size="small" style="margin-left: 50px" @click="handleReverse">反向</a-button>
      </a-form-model-item>
    </template>

    <a-form-model-item class="form-item-vertical" v-if="formData.type === '2' || formData.type === '3'">
      <template slot="label">
        <label>分支条件设置</label>
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <div slot="title">
            流程出现判断节点时，可通过设置分支条件让流程进行自动判断和流转
            <br />
            条件分支：将本流向设置为条件分支，需要配置分支条件，满足条件时才可使用本流向
            <br />
            缺省分支：将本流向设置为缺省分支，当同一判断节点下其他流向都不满足条件时，默认使用本流向
          </div>
          <a-icon type="exclamation-circle" />
        </a-tooltip>
      </template>
      <a-radio-group v-model="formData.isDefault" size="small">
        <a-radio v-for="item in isDefaultConfig" :key="item.value" :value="item.value">
          {{ item.label }}
        </a-radio>
      </a-radio-group>
      <template v-if="formData.isDefault == '0'">
        <condition-setting v-model="formData.conditions" :orgVersionId="orgVersionId" :types="['1', '2', '3', '7', '4']" text="条件" />
      </template>
      <div>
        <w-checkbox v-model="formData.isEveryCheck" unCheckedValue="">每次提交时检查条件</w-checkbox>
      </div>
    </a-form-model-item>
    <template v-if="formData.type !== '3'">
      <a-form-model-item class="form-item-vertical">
        <template slot="label">
          <label>排序号</label>
          <a-tooltip placement="topRight" :arrowPointAtCenter="true">
            <div slot="title">当出现多个流向需要选择时，可以设置排序号以调整选项的优先次序，例如设置编号 010、020</div>
            <a-icon type="exclamation-circle" />
          </a-tooltip>
        </template>
        <a-input v-model="formData.sortOrder" />
      </a-form-model-item>
      <a-form-model-item class="form-item-vertical" label="流向说明">
        <div style="display: flex">
          <a-textarea
            v-model="formData.remark"
            :rows="4"
            :style="{
              height: 'auto',
              'margin-top': '10px',
              'margin-right': '10px'
            }"
          />
          <w-i18n-input :target="formData" :code="formData.id + '.remark'" v-model="formData.remark" />
        </div>
      </a-form-model-item>
      <a-form-model-item class="form-item-vertical" label="">
        <w-checkbox v-model="formData.showRemark">流程签批时显示流向说明</w-checkbox>
      </a-form-model-item>
    </template>
  </div>
</template>

<script>
import { isDefaultConfig, directionOptions } from '../designer/constant';
import WCheckbox from '../components/w-checkbox';
import WSwitch from '../components/w-switch';
import ConditionSetting from '../commons/condition-setting.vue';
import WI18nInput from '@framework/vue/designer/w-i18n-input.vue';

export default {
  name: 'EdgeDirectionBaseInfo',
  inject: ['designer', 'workFlowData'],
  props: {
    graphItem: {
      type: Object,
      default: () => {}
    },
    formData: {
      type: Object,
      default: () => {}
    },
    rules: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    WCheckbox,
    WSwitch,
    ConditionSetting,
    WI18nInput
  },
  computed: {
    orgVersionId() {
      return this.workFlowData.property.orgVersionId || '';
    },
    directionType2() {
      let type = this.directionOptions[0]['value'];
      let line = this.formData.line;

      if (!line) {
        const selectedEdge = this.graphItem.getSelectedCell();
        const edgeRouter = selectedEdge.getRouter();
        line = edgeRouter.name;
      }

      if (line.indexOf(this.directionOptions[1]['value']) === 0) {
        type = this.directionOptions[1]['value'];
      }
      return type;
    }
  },
  data() {
    return {
      isDefaultConfig,
      directionOptions,
      directionType: ''
    };
  },
  created() {
    this.directionType = this.getDirectionType();
  },
  methods: {
    getDirectionType() {
      let type = this.directionOptions[0]['value'];
      let line = this.formData.line;

      if (!line) {
        const selectedEdge = this.graphItem.getSelectedCell();
        const edgeRouter = selectedEdge.getRouter();
        line = edgeRouter.name;
      }

      if (line.indexOf(this.directionOptions[1]['value']) === 0) {
        type = this.directionOptions[1]['value'];
      }
      return type;
    },
    changeName(e) {
      this.formData.autoUpdateName = false;
      this.graphItem.setSelectedEdgeLabels(e.target.value);
    },
    changeDirectionType(event) {
      const value = event.target.value;
      this.graphItem.modifyRoute(value);
    },
    handleReverse() {
      this.graphItem.reverseEdge();
    }
  }
};
</script>
