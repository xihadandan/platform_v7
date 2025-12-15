<template>
  <!-- 环节属性-权限设置 -->
  <div>
    <permission-drawer label="发起权限" title="发起权限设置" :formData="formData" v-model="formData.startRights" v-if="isStartTask" />
    <permission-drawer label="待办权限" title="待办权限设置" type="rights" :formData="formData" v-model="formData.rights" />
    <a-form-model-item label="待办意见立场">
      <w-switch v-model="formData.enableOpinionPosition" />
    </a-form-model-item>
    <div class="ant-form-item" v-if="formData.enableOpinionPosition === '1'">
      <div style="flex: 1">
        <a-button type="link" @click="setDefaultOpinionPosition">
          <Icon type="pticon iconfont icon-ptkj-shezhi" />
          <span>默认设置</span>
        </a-button>
        <a-form-model-item prop="optNames" label="" class="todo-opinion-form-item" :wrapper-col="{ style: { textAlign: 'left' } }">
          <div class="todo-opinion-container">
            <div class="opinion-position-item" v-for="(record, index) in formData.optNames" :key="index">
              <div class="opinion-position-name">{{ record.argValue }}({{ record.value }})</div>
              <div class="opinion-position-btn-group">
                <a-button type="link" size="small" @click="setOpinionPosition(record, index)">
                  <Icon type="pticon iconfont icon-ptkj-shezhi" />
                </a-button>
                <a-button type="link" size="small" @click="delOpinionPosition(record, index)">
                  <Icon type="pticon iconfont icon-ptkj-shanchu" />
                </a-button>
              </div>
            </div>
            <a-button type="link" icon="plus" size="small" @click="addOpinionPosition">添加</a-button>
          </div>
        </a-form-model-item>
        <w-checkbox v-model="formData.requiredOpinionPosition">意见立场必填</w-checkbox>
        <div class="opinion-process-container">
          <div class="opinion-process-title">办理过程显示设置</div>
          <div class="ant-form-item">
            <div class="opinion-process-content">
              <w-checkbox v-model="formData.showUserOpinionPosition">显示用户意见立场值</w-checkbox>
              <w-checkbox v-model="formData.showOpinionPositionStatistics">显示意见立场统计</w-checkbox>
            </div>
          </div>
        </div>
      </div>
    </div>
    <permission-drawer label="已办权限" title="已办权限设置" type="doneRights" :formData="formData" v-model="formData.doneRights" />
    <permission-drawer label="督办权限" title="督办权限设置" type="monitorRights" :formData="formData" v-model="formData.monitorRights" />
    <permission-drawer label="监控权限" title="监控权限设置" type="adminRights" :formData="formData" v-model="formData.adminRights" />
    <permission-drawer
      label="抄送对象权限"
      title="抄送对象权限设置"
      type="copyToRights"
      :formData="formData"
      v-model="formData.copyToRights"
    />
    <permission-drawer
      label="查阅人员权限"
      title="查阅人员权限设置"
      type="viewerRights"
      :formData="formData"
      v-model="formData.viewerRights"
    />
    <more-show-component>
      <custom-buttons v-model="formData.buttons" />
      <!-- <a-form-model-item prop="granularity" :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
        <template slot="label">最大权限粒度</template>
        <w-select
          v-model="formData.granularity"
          :options="granularityOptions"
          :replaceFields="{
            title: 'label',
            key: 'value',
            value: 'value'
          }"
        />
      </a-form-model-item> -->
    </more-show-component>
    <a-modal
      title="意见立场"
      :visible="opinionPositionVisible"
      @ok="saveOpinionPosition"
      @cancel="opinionPositionVisible = false"
      :width="600"
      :bodyStyle="{ height: '200px', 'overflow-y': 'auto' }"
      wrapClassName="flow-timer-modal-wrap"
      :getContainer="getContainer"
      :destroyOnClose="true"
    >
      <opinion-position-info ref="opinionInfoRef" :formData="currentOpinionPosition" :nodeData="formData" />
    </a-modal>
  </div>
</template>

<script>
import PermissionItem from './permission-item.vue';
import PermissionDrawer from './permission-drawer.vue';
import WSwitch from '../components/w-switch';
import WCheckbox from '../components/w-checkbox';
import { opinionPositionList, granularityOptions } from '../designer/constant';
import { deepClone, generateId } from '@framework/vue/utils/util';
import OpinionPositionInfo from './opinion-position-info.vue';
import CustomButtons from './custom-buttons.vue';
import MoreShowComponent from '../commons/more-show-component.vue';
import WSelect from '../components/w-select';
import mixins from '../mixins';

export default {
  name: 'TaskPropertyPermissionSettings',
  inject: ['designer', 'graph'],
  mixins: [mixins],
  props: {
    formData: {
      type: Object,
      default: () => {}
    }
  },
  components: {
    PermissionItem,
    PermissionDrawer,
    WSwitch,
    WCheckbox,
    OpinionPositionInfo,
    MoreShowComponent,
    CustomButtons,
    WSelect
  },
  data() {
    return {
      granularityOptions,
      opinionPositionList,
      opinionPositionVisible: false,
      currentOpinionPosition: {},
      currentOpinionPositionIndex: -1
    };
  },
  methods: {
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    },
    // 设置默认待办意见立场
    setDefaultOpinionPosition() {
      let opinionPositionList = deepClone(this.opinionPositionList);

      const argValues = opinionPositionList.map(opinion => {
        return opinion.argValue;
      });

      const from = 'zh',
        to = (this.$i18n.locale && this.$i18n.locale.split('_')[0]) || 'en';
      this.$translate(argValues, from, to).then(i18nMap => {
        this.formData.optNames = opinionPositionList.map(item => {
          const code = this.formData.id + '.opinionPosition.' + item.value;
          if (!item.i18n) {
            item.i18n = {};
            item.i18n['zh_CN'] = { [code]: item.argValue };
          }

          if (i18nMap[item.argValue] && this.$i18n.locale) {
            item.i18n[this.$i18n.locale] = { [code]: i18nMap[item.argValue] };
          }
          return item;
        });
      });
    },
    addOpinionPosition() {
      this.currentOpinionPosition = {};
      this.currentOpinionPositionIndex = -1;
      this.opinionPositionVisible = true;
    },
    delOpinionPosition(data, index) {
      this.formData.optNames.splice(index, 1);
    },
    setOpinionPosition(data, index) {
      this.currentOpinionPosition = data;
      this.currentOpinionPositionIndex = index;
      this.opinionPositionVisible = true;
    },
    saveOpinionPosition() {
      this.$refs.opinionInfoRef.save(({ valid, error, data }) => {
        if (valid) {
          const findIndex = this.formData.optNames.findIndex(item => item.value === data.value);
          if (findIndex > -1 && findIndex !== this.currentOpinionPositionIndex) {
            this.$message.error('意见值不能重复');
            return;
          }
          if (data.i18n) {
            let i18n = {};
            for (let locale in data.i18n) {
              i18n[locale] = {};
              for (let key in data.i18n[locale]) {
                if (data.i18n[locale][key]) {
                  let newKey = key;
                  if (key.split('.').length < 3) {
                    newKey = key + '.' + data.value;
                  }
                  i18n[locale][newKey] = data.i18n[locale][key];
                }
              }
            }
            data.i18n = i18n;
          }
          if (this.currentOpinionPositionIndex == -1) {
            this.formData.optNames.push(data);
          } else {
            this.formData.optNames.splice(this.currentOpinionPositionIndex, 1, data);
          }
          this.opinionPositionVisible = false;
        }
      });
    }
  }
};
</script>
