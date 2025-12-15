<template>
  <div>
    <!-- 子流程 -->
    <div v-if="subTaskData">
      <!-- 承办情况 -->
      <template v-for="(shareDatas, blockCode) in subTaskData && subTaskData.shareDatas">
        <div :class="'subTaskShareData_' + blockCode" v-for="(shareData, index) in shareDatas" :key="'sd_' + blockCode + '_' + index">
          <WorkflowSubflowShareData
            :workView="workView"
            :subTaskData="subTaskData"
            :shareData="shareData"
            @actionSuccess="onSubflowActionSuccess"
            :index="index"
            @reload="reloadSubTaskData(shareData, blockCode, index)"
          ></WorkflowSubflowShareData>
        </div>
      </template>
      <!-- 信息分发 -->
      <template v-for="(distributeInfos, blockCode) in subTaskData && subTaskData.distributeInfos">
        <div
          :class="'subTaskDistributeInfo_' + blockCode"
          v-for="(distributeInfo, index) in distributeInfos"
          :key="'di_' + blockCode + '_' + index"
        >
          <WorkflowSubflowDistributeInfo
            :workView="workView"
            :subTaskData="subTaskData"
            :distributeInfo="distributeInfo"
            :index="index"
          ></WorkflowSubflowDistributeInfo>
        </div>
      </template>
      <!-- 操作记录 -->
      <template v-for="(taskOperations, blockCode) in subTaskData && subTaskData.taskOperations">
        <div
          :class="'subTaskTaskOperation_' + blockCode"
          v-for="(relateOperation, index) in taskOperations"
          :key="'rp_' + blockCode + '_' + index"
        >
          <WorkflowSubflowRelateOperation
            :workView="workView"
            :subTaskData="subTaskData"
            :relateOperation="relateOperation"
            :index="index"
          ></WorkflowSubflowRelateOperation>
        </div>
      </template>
    </div>

    <!-- 分支流 -->
    <div v-if="branchTaskData">
      <!-- 承办情况 -->
      <template v-for="(shareDatas, blockCode) in branchTaskData.shareDatas">
        <div :class="'branchTaskShareData_' + blockCode" v-for="(shareData, index) in shareDatas" :key="'tsd_' + blockCode + '_' + index">
          <WorkflowBranchTaskShareData
            :workView="workView"
            :subTaskData="branchTaskData"
            :shareData="shareData"
            @actionSuccess="onSubflowActionSuccess"
            :index="index"
          ></WorkflowBranchTaskShareData>
        </div>
      </template>
      <!-- 信息分发 -->
      <template v-for="(distributeInfos, blockCode) in branchTaskData.distributeInfos">
        <div
          :class="'branchTaskDistributeInfo_' + blockCode"
          v-for="(distributeInfo, index) in distributeInfos"
          :key="'tdi_' + blockCode + '_' + index"
        >
          <WorkflowBranchTaskDistributeInfo
            :workView="workView"
            :subTaskData="branchTaskData"
            :distributeInfo="distributeInfo"
            :index="index"
          ></WorkflowBranchTaskDistributeInfo>
        </div>
      </template>
      <!-- 操作记录 -->
      <template v-for="(taskOperations, blockCode) in branchTaskData.taskOperations">
        <div
          :class="'branchTaskTaskOperation_' + blockCode"
          v-for="(relateOperation, index) in taskOperations"
          :key="'trp_' + blockCode + '_' + index"
        >
          <WorkflowBranchTaskRelateOperation
            :workView="workView"
            :subTaskData="branchTaskData"
            :relateOperation="relateOperation"
            :index="index"
          ></WorkflowBranchTaskRelateOperation>
        </div>
      </template>
    </div>
  </div>
</template>
<script>
import { each as forEach, isArray, isEmpty } from 'lodash';
export default {
  name: 'WorkflowSubflowViewer',
  props: {
    workView: Object
  },
  data() {
    return {
      workData: null,
      subTaskData: null, // 子流程数据
      branchTaskData: null // 分支流数据
    };
  },
  created() {
    const _self = this;
    _self.init();
  },
  methods: {
    init() {
      const _self = this;
      let workData = _self.workView.getWorkData();
      _self.workData = workData;
      _self.subTaskData = workData.subTaskData;
      _self.branchTaskData = workData.branchTaskData;
      // 子流程数据
      if (_self.subTaskData) {
        _self.initSubTaskDataJson = JSON.stringify(_self.subTaskData);
        _self.loadSubTaskData(_self.subTaskData, result => {
          let data = result.data.data;
          _self.subTaskData = data;
          _self.$nextTick(() => {
            _self.layoutSubTaskData();
          });
        });
      }
      // 分支流数据
      if (_self.branchTaskData) {
        _self.loadBranchTaskData(_self.branchTaskData, result => {
          let data = result.data.data;
          _self.branchTaskData = data;
          _self.$nextTick(() => {
            _self.layoutBranchTaskData();
          });
        });
      }
    },
    // 加载子流程数据
    loadSubTaskData(subTaskData, callback) {
      const _self = this;
      $axios.post('/api/workflow/work/loadSubTaskData', subTaskData).then(result => {
        callback.call(_self, result);
      });
    },
    // 加载分支流数据
    loadBranchTaskData(branchTaskData, callback) {
      const _self = this;
      $axios.post('/api/workflow/work/loadBranchTaskData', branchTaskData).then(result => {
        callback.call(_self, result);
      });
    },
    // 重新加载子流程数据
    reloadSubTaskData(shareData, blockCode, shareDataIndex) {
      const _self = this;
      let initSubTaskData = JSON.parse(_self.initSubTaskDataJson);
      _self.loadSubTaskData(initSubTaskData, result => {
        let data = result.data.data;
        forEach(data.shareDatas, function (data, placeholder) {
          if (placeholder != blockCode) {
            return;
          }
          if (isEmpty(data.title)) {
            data.title = _self.$t('WorkflowWork.workView.undertakingSituation', '承办情况') + (shareDataIndex + 1);
          }
          if (data[shareDataIndex] && shareData.belongToTaskInstUuid == data[shareDataIndex].belongToTaskInstUuid) {
            Object.assign(shareData, data[shareDataIndex]);
          }
        });
      });
    },
    getI18nTitle(item = {}) {
      let title = item.title;
      if (!isEmpty(title)) {
        const workflowName = this.workView.options.workData.name;
        if (workflowName && title.includes(workflowName)) {
          const i18nName = this.$t('WorkflowView.workflowName', workflowName);
          title = title.replace(new RegExp(workflowName, 'g'), i18nName);
        }
      }
      return title;
    },
    formatTaskData(dataList, type) {
      dataList.map(item => {
        item.title = this.getI18nTitle(item);
        if (type === 'operations' && item.operations) {
          item.operations.map(opera => {
            opera.opinionText = this.workView.getMsgI18ns(null, opera.opinionText, 'WorkflowWork.opinionManager');
          });
        }
      });
    },
    // 将承办情况、信息分发、操作记录移入表单区块内
    layoutSubTaskData() {
      const _self = this;
      let subTaskData = _self.subTaskData;
      let shareDatas = subTaskData.shareDatas || {};
      let distributeInfos = subTaskData.distributeInfos || {};
      let taskOperations = subTaskData.taskOperations || {};
      for (let blockCode in shareDatas) {
        this.formatTaskData(shareDatas[blockCode]);
        _self.moveElementToFormBlock(`subTaskShareData_${blockCode}`, blockCode);
      }
      for (let blockCode in distributeInfos) {
        this.formatTaskData(distributeInfos[blockCode]);
        _self.moveElementToFormBlock(`subTaskDistributeInfo_${blockCode}`, blockCode);
      }
      for (let blockCode in taskOperations) {
        this.formatTaskData(taskOperations[blockCode], 'operations');
        _self.moveElementToFormBlock(`subTaskTaskOperation_${blockCode}`, blockCode);
      }
    },
    // 将承办情况、信息分发、操作记录移入表单区块内
    layoutBranchTaskData() {
      const _self = this;
      let branchTaskData = _self.branchTaskData;
      let shareDatas = branchTaskData.shareDatas || {};
      let distributeInfos = branchTaskData.distributeInfos || {};
      let taskOperations = branchTaskData.taskOperations || {};
      for (let blockCode in shareDatas) {
        _self.moveElementToFormBlock(`branchTaskShareData_${blockCode}`, blockCode);
      }
      for (let blockCode in distributeInfos) {
        _self.moveElementToFormBlock(`branchTaskDistributeInfo_${blockCode}`, blockCode);
      }
      for (let blockCode in taskOperations) {
        _self.moveElementToFormBlock(`branchTaskTaskOperation_${blockCode}`, blockCode);
      }
    },
    moveElementToFormBlock(elCls, blockCode) {
      const _this = this;
      let $tabPane;
      let $el = document.querySelector(`[w-code='${blockCode}']`);
      if (!$el) {
        try {
          $tabPane = document.querySelector(`#${blockCode}.tab-pane-container`); // 标签页
          if ($tabPane) {
            $el = $tabPane.firstChild;
          }
        } catch (e) {}
      }
      if ($el) {
        let divEl = document.createElement('div');
        if ($tabPane) {
          $el.insertBefore(divEl, $el.firstChild);
        } else {
          $el.appendChild(divEl);
        }
        let sourceEls = _this.$el.querySelectorAll('.' + elCls);
        sourceEls &&
          sourceEls.forEach(sourceEl => {
            divEl.appendChild(sourceEl);
          });
      } else {
        let sourceEls = _this.$el.querySelectorAll('.' + elCls);
        sourceEls &&
          sourceEls.forEach(sourceEl => {
            sourceEl.style.display = 'none';
          });
      }
    },
    // 子流程操作成功
    onSubflowActionSuccess(options) {
      this.workView.emitActionSuccess(options);
    }
  }
};
</script>
<style scoped></style>
