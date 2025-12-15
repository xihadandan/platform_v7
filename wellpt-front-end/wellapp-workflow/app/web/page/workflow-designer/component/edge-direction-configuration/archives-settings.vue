<template>
  <!-- 流向属性-归档设置 -->
  <div>
    <a-form-model-item class="form-item-vertical">
      <template slot="label">
        <a-tooltip placement="topRight" :arrowPointAtCenter="true">
          <div slot="title">添加归档规则，将本流程的流转数据自动保存到文件库</div>
          <label>
            归档设置
            <a-icon type="exclamation-circle" />
          </label>
        </a-tooltip>
      </template>
      <a-table
        rowKey="archiveId"
        :showHeader="false"
        :pagination="false"
        size="small"
        :bordered="false"
        :columns="columns"
        :locale="locale"
        :data-source="dataSource"
        class="timers-table no-border"
      >
        <template slot="nameSlot" slot-scope="text, record">
          {{ record.displayName }}
        </template>
        <template slot="operationSlot" slot-scope="text, record, index">
          <a-icon type="setting" @click="setItem(record, index)" />
          <!-- 删除 -->
          <a-icon type="delete" style="margin-left: 10px" @click="delItem(index)" />
        </template>
        <template slot="footer">
          <div class="timers-table-footer">
            <span>
              <a-button type="link" @click="addItem" icon="plus">添加</a-button>
            </span>
          </div>
        </template>
      </a-table>
    </a-form-model-item>
    <modal
      title="归档规则"
      v-model="visible"
      :container="getContainer"
      :ok="saveItme"
      okText="保存"
      :width="770"
      :bodyStyle="{
        height: '500px'
      }"
      wrapperClass="flow-timer-modal-wrap"
    >
      <template slot="content">
        <archive-info ref="refItem" v-if="visible" :formData="currentItem" />
      </template>
    </modal>
  </div>
</template>

<script>
import { archiveWayOptions } from '../designer/constant';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import ArchiveInfo from './archive-info.vue';

export default {
  name: 'EdgeDirectionArchivesSettings',
  inject: ['designer'],
  props: {
    formData: {
      type: Object,
      default: () => {}
    },
    dataSource: {
      type: Array,
      default: () => []
    }
  },
  components: {
    Modal,
    ArchiveInfo
  },
  data() {
    return {
      archiveWayOptions,
      locale: {
        emptyText: <span>暂无数据</span>
      },
      columns: [
        { title: '名称', dataIndex: 'name', scopedSlots: { customRender: 'nameSlot' } },
        { title: '操作', dataIndex: 'operation', width: 70, scopedSlots: { customRender: 'operationSlot' }, align: 'center' }
      ],
      currentIndex: 0,
      currentItem: undefined,
      visible: false,
      createItem: () => {
        const date = new Date();
        const archiveWayName = archiveWayOptions[0]['text'];
        return {
          archiveId: date.getTime(),
          archiveWay: '1',
          archiveWayName,
          archiveStrategy: '1',
          botRuleName: '',
          botRuleId: '',
          destFolderName: '',
          destFolderUuid: '',
          fillDateTime: false,
          subFolderRule: '',
          archiveScriptType: '',
          archiveScript: '',
          displayName: ''
        };
      }
    };
  },
  created() {
    this.dataSource.forEach(item => {
      this.archiveWayOptions.forEach(opt => {
        if (opt.id === item.archiveWay) {
          item.archiveWayName = opt.text;
        }
      });
      item.displayName = this.getArchiveDisplayName(item);
    });
  },
  methods: {
    setItem(record, index) {
      this.currentIndex = index;
      this.currentItem = JSON.parse(JSON.stringify(record));
      this.visible = true;
    },
    delItem(index) {
      this.dataSource.splice(index, 1);
    },
    addItem() {
      this.currentItem = this.createItem();
      this.visible = true;
    },
    saveItme(callback) {
      this.$refs.refItem.validate(({ valid, error, data }) => {
        if (valid) {
          const findIndex = this.dataSource.findIndex(item => {
            return item.archiveId === this.currentItem.archiveId;
          });
          this.currentItem.displayName = this.getArchiveDisplayName(this.currentItem);
          if (findIndex === -1) {
            this.dataSource.push(this.currentItem);
          } else {
            this.dataSource.splice(findIndex, 1, this.currentItem);
          }
          callback(true);
        }
      });
    },
    getContainer() {
      return document.querySelector('.edit-widget-property-container');
    },
    getArchiveDisplayName(archiveRule) {
      let displayName = '';
      if (archiveRule) {
        displayName = archiveRule.archiveWayName;
        const archiveWay = archiveRule.archiveWay;
        // 转换规则
        if (archiveWay == '3' || archiveWay == '5') {
          if (archiveRule.botRuleName) {
            displayName += ' 转换规则(' + archiveRule.botRuleName + ')';
          }
        }
        // 文件夹
        if (archiveWay == '1' || archiveWay == '2' || archiveWay == '3' || archiveWay == '5') {
          if (archiveRule.destFolderName) {
            displayName += ' 文件夹(' + archiveRule.destFolderName + ')';
          }
        }
      }
      return displayName;
    }
  }
};
</script>
