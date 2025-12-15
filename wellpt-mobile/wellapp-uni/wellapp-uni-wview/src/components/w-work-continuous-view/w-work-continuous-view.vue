<template>
  <view class="work-view-continuous">
    <uni-row class="operator-row">
      <uni-col :span="operatorSpan[0]">
        <uni-badge
          :text="pagingInfo.totalCount"
          absolute="rightTop"
          :offset="[-5, 10]"
          :custom-style="{ zIndex: 98 }"
          size="small"
        >
          <view @tap="openTodoList"
            ><w-icon icon="pticon iconfont icon-ptkj-zhankailiebiao" :size="14"></w-icon>待办列表</view
          >
        </uni-badge>
      </uni-col>
      <uni-col :span="operatorSpan[1]">
        <view :class="{ disabled: isFirstRecord() }" @tap="onPreviousClick"
          ><w-icon icon="pticon iconfont icon-ptkj-shangyigefanhui" :size="14"></w-icon>上一条</view
        >
      </uni-col>
      <uni-col :span="operatorSpan[2]">
        <view :class="{ disabled: isLastRecord() && !showLoadingMore }" @tap="onNextClick"
          ><w-icon icon="pticon iconfont icon-ptkj-xiayigeqianjin" :size="14"></w-icon>下一条</view
        >
      </uni-col>
      <uni-col :span="operatorSpan[3]" v-if="loadingRules && loadingRules.rule == 3">
        <view @tap="onHandleLaterClick"
          ><w-icon icon="pticon iconfont icon-ptkj-shizhongxuanzeshijian" :size="14"></w-icon>稍后处理</view
        >
      </uni-col>
      <uni-col :span="operatorSpan[4]">
        <view @tap="onExitContinuousClick"
          ><w-icon icon="pticon iconfont icon-ptkj-icon-ptkj-tuichudenglu-01" :size="14"></w-icon>退出</view
        >
      </uni-col>
    </uni-row>
    <!-- 待办列表 -->
    <uni-drawer ref="showRight" mode="left" :width="300">
      <wWorkContinuesTodoList
        :options="options"
        :list="dataList"
        :current="currentRecord"
        :pagingInfo="pagingInfo"
        @filterOpen="filterOpen"
        @settingOpen="settingOpen"
        @search="onSearch"
        @itemClick="onTodoItemClick"
        @loadMore="onLoadMoreClick"
      ></wWorkContinuesTodoList>
    </uni-drawer>

    <!-- 待办列表筛选 -->
    <uni-popup
      ref="filterRef"
      type="bottom"
      class="w-work-continues-todo-list__filter"
      style="z-index: 1000"
      border-radius="10px 10px 0 0"
      background-color="#ffffff"
      :is-mask-click="false"
    >
      <view class="popup-content">
        <view class="popop-title">
          <view class="left">
            <button type="default" size="mini" @tap="onFilterSettingReset">重置</button>
          </view>
          <view class="center">
            <text>筛选</text>
          </view>
          <view class="right">
            <button type="primary" size="mini" @tap="onFilterSettingOk">确定</button>
          </view>
        </view>
        <view class="filter-body">
          <view class="title">办理时限</view>
          <view class="timing-state">
            <uni-row class="timing-state-overdue">
              <uni-col :span="18">逾期({{ timingStateMap[3] }})</uni-col>
              <uni-col :span="6" style="text-align: right"
                ><uni-w-switch size="small" v-model="timingStateOverdue" style="transform: scale(0.6)"></uni-w-switch
              ></uni-col>
            </uni-row>
            <uni-row class="timing-state-due">
              <uni-col :span="18">到期({{ timingStateMap[2] }})</uni-col>
              <uni-col :span="6" style="text-align: right"
                ><uni-w-switch size="small" v-model="timingStateDue" style="transform: scale(0.6)"></uni-w-switch
              ></uni-col>
            </uni-row>
            <uni-row class="timing-state-alarm">
              <uni-col :span="18">预警({{ timingStateMap[1] }})</uni-col>
              <uni-col :span="6" style="text-align: right"
                ><uni-w-switch size="small" v-model="timingStateAlarm" style="transform: scale(0.6)"></uni-w-switch
              ></uni-col>
            </uni-row>
            <uni-row class="timing-state-normal">
              <uni-col :span="18">正常({{ timingStateMap[0] }})</uni-col>
              <uni-col :span="6" style="text-align: right"
                ><uni-w-switch size="small" v-model="timingStateNormal" style="transform: scale(0.6)"></uni-w-switch
              ></uni-col>
            </uni-row>
          </view>
          <view class="title">流程名称</view>
          <view class="flow-def-group-list">
            <uni-w-data-checkbox
              v-model="isCheckAllFlowDefId"
              :localdata="[{ text: '全选', value: true }]"
              @change="onCheckAllFlowDefIdChange"
              :multiple="true"
            ></uni-w-data-checkbox>
            <scroll-view scroll-y="true" style="height: 200px">
              <uni-w-data-checkbox
                :multiple="true"
                v-model="flowDefGroupValues"
                :localdata="flowDefGroupOptions"
                mode="list"
                @change="onCheckFlowDefGroupChange"
              />
            </scroll-view>
          </view>
        </view>
      </view>
    </uni-popup>

    <!-- 待办列表设置 -->
    <uni-popup
      ref="settingRef"
      type="bottom"
      class="w-work-continues-todo-list__setting"
      style="z-index: 1000"
      border-radius="10px 10px 0 0"
      background-color="#ffffff"
      :is-mask-click="false"
    >
      <view class="popup-content">
        <view class="popop-title">
          <view class="left">
            <button type="default" size="mini" @tap="onLoadingRulesSettingReset">重置</button>
          </view>
          <view class="center">
            <text>设置</text>
          </view>
          <view class="right">
            <button type="primary" size="mini" @tap="onLoadingRulesSettingOk">确定</button>
          </view>
        </view>
        <view class="setting-body">
          <view class="title">加载规则</view>
          <uni-w-data-checkbox
            v-if="loadingRules"
            :multiple="false"
            mode="list"
            v-model="loadingRules.rule"
            :localdata="radioOptions"
          />
          <view v-show="loadingRules && loadingRules.rule != 3">
            <view class="title">优先处理</view>
            <uni-w-data-checkbox
              style="margin: 10px 0"
              v-model="specifyFlowCategory"
              :localdata="[{ text: '指定的流程分类', value: true }]"
              :multiple="true"
            ></uni-w-data-checkbox>
            <uni-w-data-select
              v-if="specifyFlowCategory.length"
              :bordered="true"
              dropType="dropdown"
              placement="top"
              v-model="loadingRules.flowCategoryUuids"
              :multiple="true"
              placeholder="请选择指定的流程分类"
              :localdata="flowCategoryOptions"
              :showSearch="true"
            ></uni-w-data-select>
            <uni-w-data-checkbox
              style="margin: 10px 0"
              v-model="specifyFlowDefId"
              :localdata="[{ text: '指定的流程', value: true }]"
              :multiple="true"
            ></uni-w-data-checkbox>
            <uni-w-data-select
              v-if="specifyFlowDefId.length"
              :bordered="true"
              dropType="dropdown"
              placement="top"
              v-model="loadingRules.flowDefIds"
              :multiple="true"
              placeholder="请选择指定的流程"
              :localdata="flowDefIdOptions"
              :showSearch="true"
            ></uni-w-data-select>
            <uni-w-data-checkbox
              style="margin: 10px 0"
              v-if="loadingRules"
              v-model="arriveToday"
              :localdata="[{ text: '今日到达', value: true }]"
              :multiple="true"
            ></uni-w-data-checkbox>
          </view>
        </view>
      </view>
    </uni-popup>

    <!-- 是否退出连续签批 -->
    <uni-popup ref="lxqpConfirmPopup" type="dialog">
      <uni-popup-dialog type="info" @confirm="onExitNoRecordClick" title="提示" class="wf-auto-submit-modal">
        <view>全部流程已处理，即将退出连续签批模式！</view>
      </uni-popup-dialog>
    </uni-popup>
  </view>
</template>

<script>
import { isEmpty, isFunction, each as forEach, trim as stringTrim, findIndex, map } from "lodash";
import { workFlowUtils } from "wellapp-uni-framework";
import wWorkContinuesTodoList from "./w-work-continues-todo-list.vue";
import "./css/index.scss";
export default {
  props: {
    options: {
      type: Object,
      required: true,
    },
  },
  components: { wWorkContinuesTodoList },
  data() {
    return {
      workViewStyle: {},
      timingStateMap: {
        0: 0,
        1: 0,
        2: 0,
        3: 0,
      }, // 过滤条件折计时状态
      timingStateNormal: true,
      timingStateAlarm: true,
      timingStateDue: true,
      timingStateOverdue: true,
      flowDefGroupOptions: [], // 过滤条件的流程名称选项
      flowDefGroupValues: [],
      isCheckAllFlowDefId: [],
      filterCriterions: [],
      radioOptions: [
        {
          text: "按列表顺序加载",
          value: 1,
        },
        {
          text: "按办理时限加载",
          value: 2,
        },
        {
          text: "智能加载",
          value: 3,
        },
      ],
      flowCategoryOptions: [], // 指定的流程分类选项
      checkboxStyle: {
        display: "block",
        height: "30px",
        lineHeight: "30px",
        marginLeft: "0px",
      },
      flowDefIdOptions: [], // 指定的流程定义ID选项
      dataStoreParams: null,
      specifyFlowCategory: [],
      specifyFlowDefId: [],
      arriveToday: [],
      loadingRules: null,
      showInputSearch: false,
      latestQueryValue: null,
      loadingMore: false,
      showLoadingMore: true,
      showNoRecordDialog: false,
      dataList: [], // 数据列表
      pagingInfo: {
        // 分页信息
        pageSize: 10,
        totalCount: 0,
      },
      currentRecord: null,
    };
  },
  created: function () {
    var _self = this;
    _self.initDataStoreParams();
    _self.getLoadingRules((result) => {
      _self.loadData(true).then(() => {
        let currentRecord = _self.getCurrentRecord();
        if (!currentRecord && _self.loadingRules && _self.loadingRules.rule == 3) {
          _self.options.workBeanToCurrentRecord();
        }
      });
    });
  },
  onUnload: function () {},
  mounted: function () {
    var _self = this;
    _self.initListData();
  },
  computed: {
    operatorSpan() {
      if (this.loadingRules && this.loadingRules.rule == 3) {
        return [5, 5, 5, 5, 4];
      }
      return [6, 6, 6, 5, 6];
    },
  },
  methods: {
    openTodoList() {
      this.$refs.showRight.open();
    },
    initListData() {
      const _self = this;
      var flowDataStoreParams = _self.getTodoListParam();
      // 加载过滤条件的办理时限信息
      this.$axios.post("/api/workflow/work/todo/groupAndCountByTimingState", flowDataStoreParams).then((result) => {
        let dataList = result.data.data.data || [];
        dataList.forEach((state) => {
          _self.timingStateMap[state.timingState] = state.count;
        });
      });

      // 加载过滤条件的流程名称信息
      this.$axios.post("/api/workflow/work/todo/groupAndCountByFlowDefId", flowDataStoreParams).then((result) => {
        let dataList = result.data && result.data.data && result.data.data.data;
        let values = [];
        _self.flowDefGroupOptions = map(dataList, function (item) {
          values.push(item.flowDefId);
          return { text: item.flowName + "(" + item.count + ")", value: item.flowDefId };
        });
        _self.flowDefGroupValues = values;
        _self.isCheckAllFlowDefId =
          _self.flowDefGroupValues.length > 0 && _self.flowDefGroupValues.length == _self.flowDefGroupOptions.length
            ? [true]
            : [];
      });

      // 加载流程分类选项数据
      this.$axios
        .get("/api/workflow/category/query", { params: { keyword: "", uuids: [], pageNo: 1, pageSize: 65535 } })
        .then((result) => {
          let dataList = result.data && result.data.data;
          _self.flowCategoryOptions = map(dataList, function (item) {
            return { text: item.name, value: item.uuid };
          });
        });
      // 加载流程定义选项数据
      this.$axios
        .get("/api/workflow/definition/query", { params: { keyword: "", uuids: [], pageNo: 1, pageSize: 65535 } })
        .then((result) => {
          let dataList = result.data && result.data.data;
          _self.flowDefIdOptions = map(dataList, function (item) {
            return { text: item.name, value: item.id };
          });
        });

      let filterCriterions = uni.getStorageSync("wfTodoListFilterCriterions_" + this.options._requestCode);
      if (filterCriterions) {
        _self.filterCriterions = JSON.parse(filterCriterions);
        forEach(_self.filterCriterions, (item) => {
          if (item.columnIndex == "timingState") {
            _self.timingStateNormal = item.value.indexOf(0) > -1;
            _self.timingStateAlarm = item.value.indexOf(1) > -1;
            _self.timingStateDue = item.value.indexOf(2) > -1;
            _self.timingStateOverdue = item.value.indexOf(3) > -1;
          } else if (item.columnIndex == "flowDefId") {
            _self.flowDefGroupValues = item.value;
          }
        });
      }
    },

    filterSelectOption(inputValue, option) {
      return (
        (option.componentOptions.propsData.value &&
          option.componentOptions.propsData.value.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0) ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(inputValue.toLowerCase()) >= 0
      );
    },
    // 初始化数据仓库参数信息
    initDataStoreParams() {
      const _self = this;
      let _requestCode = _self.options._requestCode;
      let dataStoreParams = '{"pagingInfo":{}}';
      dataStoreParams = uni.getStorageSync(`cwvDataStoreParams_${_requestCode}`) || dataStoreParams;
      _self.dataStoreParams = JSON.parse(dataStoreParams);
      _self.dataStoreParams.params = {};
      if (_self.dataStoreParams.pagingInfo) {
        _self.dataStoreParams.pagingInfo.currentPage = 1;
        _self.dataStoreParams.pagingInfo.pageSize = 10;
      } else {
        _self.dataStoreParams.pagingInfo = { currentPage: 1, pageSize: 10 };
      }
    },
    // 获取待办列表的视图查询参数
    getTodoListParam() {
      const _self = this;
      let dataStoreParams = JSON.parse(JSON.stringify(_self.dataStoreParams));
      dataStoreParams.loadingRules = JSON.parse(JSON.stringify(_self.loadingRules));
      return dataStoreParams;
    },
    // 获取加载规则
    getLoadingRules(callback) {
      var _self = this;
      this.$axios.get("/api/workflow/user/preferences/getLoadingRules").then((result) => {
        _self.loadingRules = result.data.data;
        // 指定的流程分类
        if (_self.loadingRules.flowCategoryUuids && _self.loadingRules.flowCategoryUuids.length > 0) {
          _self.specifyFlowCategory = [true];
        }
        // 指定的流程
        if (_self.loadingRules.flowDefIds && _self.loadingRules.flowDefIds.length > 0) {
          _self.specifyFlowDefId = [true];
        }
        if (_self.loadingRules.arriveToday) {
          _self.arriveToday = [true];
        }
        callback.call(_self, result);
      });
    },
    // 重新加载数据
    reloadData() {
      // this.dataList.length = 0;
      let dataStoreParams = this.dataStoreParams;
      // 分页页数重置为1
      dataStoreParams.pagingInfo.currentPage = 1;
      this.showLoadingMore = true;
      this.loadData(false, null, true);
    },
    // 加载数据
    loadData(keywordQuery = false, callback, reset = false) {
      const _self = this;
      if (reset) {
        uni.showLoading();
      }
      // 连续签批模式
      _self.loadingRules.mode = 2;
      let flowDataStoreParams = _self.getTodoListParam();
      // 关键字查询
      if (keywordQuery === true) {
        flowDataStoreParams.params.keyword = _self.latestQueryValue;
        if (_self.keywordCriterions != null) {
          flowDataStoreParams.criterions.push(_self.keywordCriterions);
        }
      } else {
        _self.latestQueryValue = null;
      }
      // 过滤条件
      if (!isEmpty(_self.filterCriterions)) {
        flowDataStoreParams.criterions = flowDataStoreParams.criterions.concat(_self.filterCriterions);
      }
      // data.pagingInfo.pageSize = '';
      if (flowDataStoreParams.loadingRules.rule === 3) {
        flowDataStoreParams.orders = [
          {
            sortName: "timingState",
            sortOrder: "desc",
          },
        ];
      }
      _self.loadingMore = true;
      // 从当前列表的下一条记录加载
      flowDataStoreParams.pagingInfo.first = reset ? 0 : _self.dataList.length;
      return this.$axios
        .post("/api/workflow/work/todo/query", flowDataStoreParams)
        .then(({ data: result }) => {
          // 过滤掉智能加载时可能附加的流程数据
          let data = (result.data.data || []).filter((item) => item.uuid != _self.appendTaskInstUuid);
          let pagination = result.data.pagination;
          let hasMerge = false;
          if (reset) {
            uni.hideLoading();
            _self.dataList.length = 0;
          } else {
            let dataListMap = {};
            let loadLength = data.length;
            _self.dataList.forEach((item) => (dataListMap[item.uuid] = item.uuid));
            data = data.filter((item) => !dataListMap[item.uuid]);
            hasMerge = loadLength != data.length;
          }
          _self.dataList = _self.dataList.concat(data);
          _self.pagingInfo = pagination;
          // 没有更多数据隐藏显示更多
          if (data.length == 0 || data.length < pagination.pageSize) {
            _self.showLoadingMore = false;
          }

          _self.loadingMore = false;

          // 回调处理
          if (isFunction(callback)) {
            callback.call(_self, result);
          }

          // 存在未加载的分页数据
          if (hasMerge) {
            _self.loadAndFillData(deepClone(flowDataStoreParams), deepClone(pagination));
          }
        })
        .catch((error) => {
          uni.hideLoading();
          _self.loadingMore = false;
        });
    },
    loadAndFillData(flowDataStoreParams, pagination) {
      const _this = this;
      flowDataStoreParams.pagingInfo.first = 0;
      flowDataStoreParams.pagingInfo.currentPage = 1;
      flowDataStoreParams.pagingInfo.pageSize = pagination.pageSize * pagination.currentPage - _this.dataList.length;
      if (_this.dataList.length > 0) {
        flowDataStoreParams.criterions.push({
          columnIndex: "uuid",
          value: _this.dataList.map((item) => item.uuid),
          type: "not in",
        });
      }
      this.$axios.post("/api/workflow/work/todo/query", flowDataStoreParams).then(({ data: result }) => {
        let data = result.data.data || [];
        let dataListMap = {};
        _this.dataList.forEach((item) => (dataListMap[item.uuid] = item.uuid));
        data = data.filter((item) => !dataListMap[item.uuid]);
        _this.dataList = _this.dataList.concat(data);
      });
    },
    // 查询
    onSearch(queryValue) {
      const _self = this;
      let dataStoreParams = _self.dataStoreParams;
      // 分页页数重置为1
      dataStoreParams.pagingInfo.currentPage = 1;
      let keyword = stringTrim(queryValue);
      if (_self.latestQueryValue == keyword) {
        return;
      }

      _self.latestQueryValue = keyword;
      if (!isEmpty(keyword)) {
        _self.keywordCriterions = {
          conditions: [
            {
              columnIndex: "title",
              value: keyword,
              type: "like",
            },
            {
              columnIndex: "taskName",
              value: keyword,
              type: "like",
            },
            {
              columnIndex: "flowStartUserId",
              value: keyword,
              type: "like",
            },
            {
              columnIndex: "flowStartUserName",
              value: keyword,
              type: "like",
            },
            {
              columnIndex: "flowName",
              value: keyword,
              type: "like",
            },
          ],
          type: "or",
        };
      } else {
        _self.keywordCriterions = null;
      }

      // 清空数据
      // _self.dataList.length = 0;
      _self.showLoadingMore = true;
      // 加载数据
      _self.loadData(true, null, true);
    },
    // 是否关键字查询
    isKeywordQuery() {
      const _self = this;
      let latestQueryValue = _self.latestQueryValue;
      return !isEmpty(latestQueryValue);
    },
    // 流程名称全选变更事件
    onCheckAllFlowDefIdChange() {
      const _self = this;
      let values = [];
      if (_self.isCheckAllFlowDefId.length) {
        forEach(_self.flowDefGroupOptions, function (item) {
          values.push(item.value);
        });
      }
      _self.flowDefGroupValues = values;
    },
    // 流程名称选择变更事件
    onCheckFlowDefGroupChange(checkedValue) {
      const _self = this;
      if (checkedValue && checkedValue.length == _self.flowDefGroupOptions.length) {
        _self.isCheckAllFlowDefId = [true];
      } else {
        _self.isCheckAllFlowDefId = [];
      }
    },
    filterOpen() {
      this.$refs.filterRef.open();
    },
    // 确定过滤条件
    onFilterSettingOk() {
      const _self = this;
      _self.collectFilterCriterions();
      uni.setStorageSync(
        "wfTodoListFilterCriterions_" + this.options._requestCode,
        JSON.stringify(_self.filterCriterions)
      );
      _self.reloadData();
      _self.$refs.filterRef.close();
    },
    // 收集过滤条件
    collectFilterCriterions() {
      const _self = this;
      _self.filterCriterions = [];
      let timingStates = [];
      if (_self.timingStateNormal) {
        timingStates.push(0);
      }
      if (_self.timingStateAlarm) {
        timingStates.push(1);
      }
      if (_self.timingStateDue) {
        timingStates.push(2);
      }
      if (_self.timingStateOverdue) {
        timingStates.push(3);
      }
      if (timingStates.length == 0) {
        timingStates.push(-1);
      }

      if (timingStates.length > 0) {
        _self.filterCriterions.push({
          columnIndex: "timingState",
          value: timingStates,
          type: "in",
        });
      }
      if (_self.flowDefGroupValues.length > 0) {
        _self.filterCriterions.push({
          columnIndex: "flowDefId",
          value: _self.flowDefGroupValues,
          type: "in",
        });
      }
    },
    // 重置过滤条件
    onFilterSettingReset() {
      const _self = this;
      _self.timingStateNormal = true;
      _self.timingStateAlarm = true;
      _self.timingStateDue = true;
      _self.timingStateOverdue = true;

      let values = [];
      forEach(_self.flowDefGroupOptions, function (item) {
        values.push(item.value);
      });
      _self.flowDefGroupValues = values;

      _self.collectFilterCriterions();
    },
    settingOpen() {
      this.$refs.settingRef.open();
    },
    // 确定加载规则
    onLoadingRulesSettingOk() {
      const _self = this;
      _self.loadingRules.arriveToday = !!_self.arriveToday.length;
      _self.saveLoadingRules();
    },
    // 重置加载规则
    onLoadingRulesSettingReset() {
      const _self = this;
      _self.loadingRules = Object.assign(_self.loadingRules, {
        rule: 1,
        flowCategoryUuids: [],
        flowDefIds: [],
        arriveToday: false,
      });
      _self.saveLoadingRules();
    },
    // 保存加载规则
    saveLoadingRules() {
      const _self = this;
      if (!_self.specifyFlowCategory.length) {
        _self.loadingRules.flowCategoryUuids = [];
      }
      if (!_self.specifyFlowDefId.length) {
        _self.loadingRules.flowDefIds = [];
      }
      this.$axios.post("/api/workflow/user/preferences/saveLoadingRules", _self.loadingRules).then((result) => {
        _self.reloadData();
      });
      _self.$refs.settingRef.close();
    },
    // 点击加载更多
    onLoadMoreClick() {
      this.loadMoreData();
    },
    // 加载更多
    loadMoreData(callback, currentPage) {
      const _self = this;
      let dataStoreParams = _self.dataStoreParams;
      // 加载指定页数
      if (currentPage) {
        dataStoreParams.pagingInfo.currentPage = currentPage;
      } else {
        // 分页页数加1
        dataStoreParams.pagingInfo.currentPage++;
      }
      // 加载数据
      _self.loadData(_self.isKeywordQuery(), callback);
    },
    // 判断记录是否当前工作区数据
    isCurrentWorkData(item) {
      return (item.taskInstUuid || item.uuid) == (this.options.workBean && this.options.workBean.taskInstUuid);
    },
    // 上一条
    onPreviousClick() {
      this.gotoPreviousRecord();
    },
    // 下一条
    onNextClick() {
      this.gotoNextRecord();
    },
    // 点击待办列表数据项
    onTodoItemClick(item, index) {
      this.showRecord(item);
      // 收起列表
      this.$refs.showRight.close();
    },
    // 跳到第一条记录
    gotoFirstRecordIfExists(showNoRecordDialog) {
      const _self = this;
      let dataList = _self.dataList; // 含已处理流程
      if (dataList.length <= 1) {
        if (showNoRecordDialog) {
          _self.showNoRecordDialog = true;
        }
        return;
      }

      let item = dataList[0];
      if (!_self.isCurrentWorkData(item)) {
        _self.changeWorkView(item);
      }
    },
    // 进入上一条记录
    gotoPreviousRecord() {
      const _self = this;
      if (_self.isFirstRecord()) {
        return;
      } else {
        let item = _self.getPreviousRecord();
        if (item) {
          _self.showRecord(item);
        } else {
          console.error("next record is null");
        }
      }
    },
    // 进入下一条记录
    gotoNextRecord(force = false, callback) {
      const _self = this;
      if (_self.isLastRecord()) {
        // 加载更多
        if (_self.showLoadingMore) {
          _self.loadMoreData((result) => {
            let data = result.data.data;
            if (data && data.length > 0) {
              let item = _self.getNextRecord();
              if (item) {
                _self.showRecord(item);
              } else {
                console.error("next record is null, goto first record if exists");
                _self.gotoFirstRecordIfExists(true);
              }
            }
            if (callback) {
              callback.call(_self);
            }
          }, 1);
        } else if (force) {
          _self.gotoFirstRecordIfExists(true);
          if (callback) {
            callback.call(_self);
          }
        } else {
          if (callback) {
            callback.call(_self);
          }
        }
      } else {
        let item = _self.getNextRecord();
        if (item) {
          _self.showRecord(item);
        } else {
          console.error("next record is null, goto first record if exists");
          _self.gotoFirstRecordIfExists(true);
        }
        if (callback) {
          callback.call(_self);
        }
      }
    },
    // 移到最后一条
    moveToLatest(record) {
      const _this = this;
      if (_this.isLastRecord()) {
        return;
      }
      let index = _this.dataList.findIndex((item) => item.uuid == record.uuid);
      if (index != -1) {
        _this.dataList.splice(index, 1);
        _this.dataList = [..._this.dataList, record];
      }
    },
    // 是否第一条记录
    isFirstRecord() {
      const _self = this;
      if (isEmpty(_self.dataList)) {
        return true;
      }

      let taskInstUuid = _self.options.workBean && _self.options.workBean.taskInstUuid;
      let item = _self.dataList[0];
      return item.uuid == taskInstUuid || item.taskInstUuid == taskInstUuid;
    },
    // 是否最后一条记录
    isLastRecord() {
      const _self = this;
      if (isEmpty(_self.dataList)) {
        return true;
      }

      let taskInstUuid = _self.options.workBean && _self.options.workBean.taskInstUuid;
      let item = _self.dataList[_self.dataList.length - 1];
      return item.uuid == taskInstUuid || item.taskInstUuid == taskInstUuid;
    },
    // 获取前一条记录
    getPreviousRecord() {
      const _self = this;
      let taskInstUuid = _self.options.workBean && _self.options.workBean.taskInstUuid;
      for (let index = 0; index < _self.dataList.length; index++) {
        let item = _self.dataList[index];
        if (item.uuid == taskInstUuid || item.taskInstUuid == taskInstUuid) {
          return _self.dataList[index - 1];
        }
      }
      return null;
    },
    // 获取当前记录
    getCurrentRecord() {
      const _self = this;
      if (_self.currentRecord) {
        return _self.currentRecord;
      }
      let taskInstUuid = _self.options.workBean && _self.options.workBean.taskInstUuid;
      let index = _self.getRecordIndexByUuid(taskInstUuid);
      if (index == -1) {
        return null;
      }
      let record = _self.dataList[index];
      _self.currentRecord = record;
      return record;
    },
    // 根据uuid获取记录索引
    getRecordIndexByUuid(uuid) {
      const _self = this;
      let index = findIndex(_self.dataList, function (item) {
        return item.uuid == uuid;
      });
      return index;
    },
    workBeanToCurrentRecord() {
      const _this = this;
      if (!_this.workBean) {
        return;
      }
      let taskInstUuid = _this.workBean.taskInstUuid;
      let flowDataStoreParams = _this.getTodoListParam();
      flowDataStoreParams.criterions = [{ columnIndex: "uuid", value: taskInstUuid, type: "eq" }];
      return this.$axios
        .post("/api/workflow/work/todo/query", flowDataStoreParams)
        .then(({ data: result }) => {
          let data = result.data && result.data.data;
          if (data.length > 0) {
            _this.currentRecord = data[0];
            _this.appendTaskInstUuid = data[0].uuid;
            _this.dataList = [...data, ..._this.dataList];
          }
        })
        .catch((error) => {});
    },
    // 添加记录
    addRecord(index, record) {
      _self.dataList.splice(index, 0, record);
      _self.pagingInfo.totalCount++;
    },
    // 删除记录
    removeRecord(record) {
      const _self = this;
      if (record == null) {
        return;
      }
      let index = _self.getRecordIndexByUuid(record.taskInstUuid || record.uuid);
      _self.dataList.splice(index, 1);
      _self.pagingInfo.totalCount--;
    },
    // 获取下一条记录
    getNextRecord() {
      const _self = this;
      let taskInstUuid = _self.options.workBean && _self.options.workBean.taskInstUuid;
      for (let index = 0; index < _self.dataList.length; index++) {
        let item = _self.dataList[index];
        if (item.uuid == taskInstUuid || item.taskInstUuid == taskInstUuid) {
          return _self.dataList[index + 1];
        }
      }
      return null;
    },
    toUpdateRecord() {
      const _self = this;
      // 更新或移除当前一条
      let currentRecord = _self.getCurrentRecord();
      _self.updateRecord(currentRecord).then((updated) => {
        if (updated) {
          _self.showRecord(currentRecord);
        } else {
          _self.gotoNextRecord(true);
        }
      });
    },
    // 更新记录
    updateRecord(record) {
      const _self = this;
      if (record == null) {
        return Promise.resolve(false);
      }
      // 连续签批模式
      _self.loadingRules.mode = 2;
      let flowDataStoreParams = _self.getTodoListParam();
      flowDataStoreParams.criterions = [{ columnIndex: "flowInstUuid", value: record.flowInstUuid, type: "eq" }];
      return this.$axios
        .post("/api/workflow/work/todo/query", flowDataStoreParams)
        .then((result) => {
          let data = result.data.data.data;
          if (data.length === 0) {
            return false;
          } else if (data.length === 1) {
            record = Object.assign(record, data[0]);
          } else {
            record = Object.assign(record, data[0]);
            let recordIndex = _self.getRecordIndexByUuid(record);
            for (let index = 1; index < data.length; index++) {
              _self.addRecord(recordIndex + index, data[index]);
            }
          }
          return true;
        })
        .catch((error) => {
          return false;
        });
    },
    // 稍候处理
    onHandleLaterClick() {
      const _self = this;
      if (_self.isLastRecord()) {
        _self.$message.warn("当前为最后一条流程！");
        return;
      }

      let workData = _self.options.workBean;
      if (workData == null) {
        return;
      }

      this.$axios
        .post(
          "/api/workflow/work/todo/dealLater",
          {
            taskInstUuid: workData.taskInstUuid,
          },
          {
            headers: {
              contentType: "application/x-www-form-urlencoded",
            },
          }
        )
        .then((result) => {
          let currentRecord = _self.getCurrentRecord();
          let item = _self.getNextRecord();
          if (item) {
            _self.gotoNextRecord();
          } else {
            _self.gotoFirstRecordIfExists();
          }
          if (currentRecord) {
            _self.moveToLatest(currentRecord);
          }
        });
    },
    // 显示记录到工作区
    showRecord(item) {
      let _self = this;
      if (!_self.isCurrentWorkData(item)) {
        _self.currentRecord = item;
        _self.changeWorkView(item);
      }
    },
    // 更新工作区数据
    updateWorkData(record) {
      this.changeWorkView(record);
    },
    // 切换工作区数据
    changeWorkView(item) {
      const _self = this;
      _self.changeDocumentInfo(item);
    },
    // 变更浏览器文档相关信息
    changeDocumentInfo(item) {
      const _self = this;
      let options = { aclRole: "TODO", _requestCode: this.options._requestCode, continuousMode: 1 };
      options.taskInstUuid = item.taskInstUuid || item.uuid;
      options.flowInstUuid = item.flowInstUuid;
      options.taskIdentityUuid = item.taskIdentityUuid;
      options.sameUserSubmitType = item.sameUserSubmitType;
      options.auto_submit = item.autoSubmit;
      options.sameUserSubmitTaskOperationUuid = item.sameUserSubmitTaskOperationUuid;
      let params = [];
      for (const key in options) {
        if (Object.hasOwnProperty.call(options, key)) {
          params.push(key + "=" + options[key]);
        }
      }
      options.url = workFlowUtils.pageUrl + "?" + params.join("&");
      options.isReLoad = true;
      this.$emit("changeOptions", options);
    },
    // 退出连续签批
    onExitNoRecordClick() {
      uni.removeStorageSync(`cwvDataStoreParams_${this.options._requestCode}`);
      this.$emit("close");
    },
    // 退出连续签批
    onExitContinuousClick() {
      const _self = this;
      if (_self.options.workBean) {
        if (_self.options.viewVue.$refs.workViewRef.workView) {
          _self.options.viewVue.$refs.workViewRef.workView.exitContinuousMode();
        }
      } else {
        uni.removeStorageSync(`cwvDataStoreParams_${this.options._requestCode}`);
        this.$emit("close");
      }
    },
  },
  watch: {
    showNoRecordDialog(v) {
      if (v) {
        this.$refs.lxqpConfirmPopup.open();
      }
    },
  },
};
</script>

<style lang="scss" scoped></style>
