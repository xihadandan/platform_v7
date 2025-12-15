<template>
  <div>
    <a-table rowKey="uuid" :pagination="false" :columns="columns" :dataSource="dataSource">
      <div class="rebuild-index-table-title" slot="title">
        <span>索引重建任务</span>
        <a-button type="primary" @click="openLog">
          <a-icon type="setting" />
          执行记录
        </a-button>
      </div>
      <template slot="typeSlot" slot-scope="text, record, index">
        {{ repeatTypeMap[record.repeatType]['label'] }}
      </template>
      <template slot="titmeSlot" slot-scope="text, record, index">
        <template v-if="record.repeatType === repeatTypeOptions[0]['value']">
          {{ record.executeTime }}
        </template>
        <template v-else>{{ getRepeatDisplayTime(record) }}</template>
      </template>
      <template slot="stateSlot" slot-scope="text, record, index">
        <a-tag :color="stateMap[record.state]['color']">{{ stateMap[record.state]['label'] }}</a-tag>
      </template>
      <template slot="operationSlot" slot-scope="text, record, index">
        <a-button type="link" size="small" icon="setting" @click="setItem(record, index)">查看</a-button>
        <!-- 删除 -->
        <a-button type="link" size="small" icon="delete" @click="delItem(index)">删除</a-button>
      </template>
      <template slot="footer">
        <a-button type="link" @click="addItem" icon="plus">添加</a-button>
      </template>
    </a-table>

    <drawer v-model="visible" title="索引重建任务" :width="500" :container="getContainer" :mask="true" wrapClassName="result-page-drawer">
      <template slot="content" v-if="visible">
        <a-form-model :colon="false" layout="vertical" class="pt-form">
          <a-form-model-item label="重复周期">
            <a-select v-model="currentItem.repeatType" :options="repeatTypeOptions" :disabled="onlyView" />
          </a-form-model-item>
          <a-form-model-item label="时间" v-if="currentItem.repeatType === repeatTypeOptions[0]['value']">
            <a-date-picker
              :format="defaultPattern"
              :valueFormat="defaultPattern"
              :showTime="{
                format: defaultPattern,
                valueFormat: defaultPattern
              }"
              :disabled="onlyView"
              v-model="currentItem.executeTime"
              style="width: 230px"
            />
          </a-form-model-item>
          <template v-else>
            <a-form-model-item label="间隔">
              <div class="rebuild-repeat-interval-unit">
                <span>每</span>
                <a-input v-model="currentItem.repeatInterval" />
                <a-select v-model="currentItem.repeatUnit" :options="repeatUnitOptions" />
              </div>

              <a-checkbox-group
                v-if="currentItem.repeatUnit === repeatUnitOptions[1]['value']"
                v-model="currentItem.repeatDaysOfWeek"
                :options="repeatDaysOfWeekOptions"
              />
              <a-select
                v-if="currentItem.repeatUnit === repeatUnitOptions[3]['value']"
                v-model="currentItem.repeatMonthOfYear"
                :options="repeatMonthOfYearOptions"
                style="width: 200px; margin-bottom: 10px; display: block"
              />
              <a-select
                v-if="currentItem.repeatUnit === repeatUnitOptions[2]['value'] || currentItem.repeatUnit === repeatUnitOptions[3]['value']"
                v-model="currentItem.repeatDayOfMonth"
                :options="repeatDayOfMonthOptions"
                style="width: 200px"
              />
              <div>
                <a-time-picker v-model="currentItem.timePoint" format="HH:mm" valueFormat="HH:mm" style="width: 200px; margin-top: 10px" />
              </div>
            </a-form-model-item>
          </template>
          <a-form-model-item label="任务状态">
            <a-tag :color="stateMap[currentItem.state]['color']">{{ stateMap[currentItem.state]['label'] }}</a-tag>
          </a-form-model-item>
        </a-form-model>
      </template>
      <template slot="footer">
        <template v-if="onlyView">
          <a-button @click="closeDrawer">取消</a-button>
        </template>
        <template v-else>
          <a-button type="primary" @click="saveItme">保存</a-button>
          <a-button
            type="primary"
            v-show="currentItem && currentItem.state === statOptions[1]['value']"
            @click="handleEditSate(statOptions[0]['value'])"
          >
            启用
          </a-button>
          <a-button
            type="primary"
            v-show="currentItem && currentItem.state === statOptions[0]['value']"
            @click="handleEditSate(statOptions[1]['value'])"
          >
            停用
          </a-button>
          <a-button @click="closeDrawer">取消</a-button>
        </template>
      </template>
    </drawer>

    <modal v-model="logVisible" title="索引重建记录" :width="900" wrapperClass="rebuild-indexlog-modal-wrap">
      <template slot="content">
        <rebuild-index-log v-if="logVisible" />
      </template>
    </modal>
  </div>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import RebuildIndexLog from './rebuild-index-log.vue';
import {
  createRebuildRule,
  repeatTypeOptions,
  repeatTypeMap,
  statOptions,
  stateMap,
  repeatUnitOptions,
  repeatUnitMap,
  repeatDaysOfWeekOptions,
  repeatDaysOfWeekMap,
  repeatDayOfMonthOptions,
  repeatDayOfMonthMap,
  repeatMonthOfYearOptions,
  repeatMonthOfYearMap
} from '../SearchIndex';

export default {
  name: 'RebuildIndexTable',
  props: {
    dataSource: {
      type: Array,
      default: () => []
    }
  },
  components: {
    Drawer,
    Modal,
    RebuildIndexLog
  },
  data() {
    return {
      repeatTypeOptions,
      repeatTypeMap,
      statOptions,
      stateMap,
      repeatUnitOptions,
      repeatUnitMap,
      repeatDaysOfWeekOptions,
      repeatDaysOfWeekMap,
      repeatDayOfMonthOptions,
      repeatDayOfMonthMap,
      repeatMonthOfYearOptions,
      repeatMonthOfYearMap,
      columns: [
        { title: '重复周期', dataIndex: 'repeatType', scopedSlots: { customRender: 'typeSlot' } },
        { title: '时间', dataIndex: 'timeStr', scopedSlots: { customRender: 'titmeSlot' } },
        { title: '任务状态', dataIndex: 'state', scopedSlots: { customRender: 'stateSlot' } },
        { title: '操作', dataIndex: 'operation', width: 200, scopedSlots: { customRender: 'operationSlot' } }
      ],
      currentIndex: 0,
      currentItem: undefined,
      visible: false,
      defaultPattern: 'yyyy-MM-DD HH:mm',
      logVisible: false
    };
  },
  created() {
    this.dataSource.forEach(item => {
      if (!item.uuid) {
        item.uuid = generateId();
      }
    });
  },
  computed: {
    // readonly(只读的) 仅查看(onlyView)
    onlyView() {
      return (
        this.currentItem &&
        this.currentItem.repeatType === this.repeatTypeOptions[0]['value'] &&
        this.currentItem.state === this.statOptions[2]['value']
      );
    }
  },
  methods: {
    // 改变状态
    handleEditSate(state) {
      this.currentItem.state = state;
      this.saveItme();
    },
    getRepeatDisplayTime(record) {
      const unit = this.repeatUnitMap[record.repeatUnit]['label'];
      let timeStr = `每${record.repeatInterval}${unit}，`;
      if (record.repeatUnit === this.repeatUnitOptions[1]['value']) {
        timeStr = timeStr + this.repeatDaysOfWeekMap[record.repeatDaysOfWeek]['label'];
      } else if (record.repeatUnit === this.repeatUnitOptions[2]['value']) {
        timeStr = timeStr + this.repeatDayOfMonthMap[record.repeatDayOfMonth]['label'];
      } else if (record.repeatUnit === this.repeatUnitOptions[3]['value']) {
        timeStr = timeStr + record.repeatMonthOfYear + this.repeatDayOfMonthMap[record.repeatDayOfMonth]['label'];
      }
      if (record.repeatUnit !== this.repeatUnitOptions[0]['value']) {
        timeStr = timeStr + ' ';
      }
      return timeStr + record.timePoint;
    },
    setItem(record, index) {
      this.currentIndex = index;
      this.currentItem = JSON.parse(JSON.stringify(record));
      this.visible = true;
    },
    delItem(index) {
      this.dataSource.splice(index, 1);
    },
    addItem() {
      this.currentIndex = -1;
      this.currentItem = createRebuildRule();
      this.currentItem.uuid = generateId();
      this.visible = true;
    },
    saveItme(callback) {
      if (this.currentIndex === -1) {
        this.dataSource.push(this.currentItem);
      } else {
        this.dataSource.splice(this.currentIndex, 1, this.currentItem);
      }
      this.visible = false;
    },
    openDrawer() {
      this.visible = true;
    },
    closeDrawer() {
      this.visible = false;
    },
    getContainer() {
      return document.body;
    },
    openLog() {
      this.logVisible = true;
    }
  }
};
</script>
