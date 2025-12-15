<template>
  <HtmlWrapper :title="(workBean && workBean.title) || '连续签批'">
    <a-layout class="continuous-work-view" theme="light">
      <a-layout-header>
        <div class="header-button-group">
          <a-button-group>
            <a-button type="link" size="small" :class="{ disabled: isFirstRecord() }" @click.stop="onPreviousClick($event)">
              <Icon type="pticon iconfont icon-ptkj-shangyigefanhui"></Icon>
              {{ $t('WorkflowWork.previous', ' 上一条') }}
            </a-button>
            <a-button type="link" size="small" :class="{ disabled: isLastRecord() && !showLoadingMore }" @click.stop="onNextClick($event)">
              <Icon type="pticon iconfont icon-ptkj-xiayigeqianjin"></Icon>
              {{ $t('WorkflowWork.next', ' 下一条') }}
            </a-button>
            <a-button v-if="loadingRules && loadingRules.rule == 3" type="link" size="small" @click.stop="onHandleLaterClick($event)">
              <Icon type="pticon iconfont icon-ptkj-shizhongxuanzeshijian"></Icon>
              {{ $t('WorkflowWork.waitProcessed', ' 稍候处理') }}
            </a-button>
            <a-button
              type="link"
              size="small"
              :title="$t('WorkflowWork.exitContinuous', '退出连续签批')"
              @click.stop="onExitContinuousClick($event)"
            >
              <Icon type="pticon iconfont icon-ptkj-tuichudenglu-01"></Icon>
              {{ $t('WorkflowWork.exit', ' 退出') }}
            </a-button>
          </a-button-group>
        </div>
      </a-layout-header>
      <a-layout-content>
        <a-layout class="left-sider" :style="leftSiderStyle">
          <a-drawer class="left-sider-drawer" width="450px" placement="left" :closable="false" :mask="false" :visible="!collapsed">
            <template slot="title">
              <a-row>
                <a-col span="8">
                  <span class="title">{{ $t('WorkflowWork.todoList', ' 待办列表') }}</span>
                  <span class="badge">{{ pagingInfo.totalCount }}</span>
                </a-col>
                <a-col
                  span="10"
                  @mouseenter="
                    showInputSearch = true;
                    filterVisible = false;
                  "
                  @mouseleave="showInputSearch = false"
                >
                  <a-row type="flex" justify="end">
                    <a-input-search
                      v-model="inputQueryValue"
                      v-show="!isEmptyInputQueryValue || showInputSearch"
                      :placeholder="$t('WorkflowWork.searchPlaceholder', '搜索')"
                      style="width: 200px"
                      @search="onSearch"
                    ></a-input-search>
                    <a-icon v-show="isEmptyInputQueryValue && !showInputSearch" class="search-icon" type="search"></a-icon>
                  </a-row>
                </a-col>
                <a-col class="col-icons" span="6">
                  <a-row type="flex" justify="end">
                    <a-popover v-model="filterVisible" placement="bottomRight" trigger="click">
                      <div slot="content" @click.stop="">
                        <div class="filter">
                          <h3>{{ $t('WorkflowWork.processTimeLimit', ' 办理时限') }}</h3>
                          <div class="timing-state">
                            <a-row class="timing-state-overdue">
                              <a-col span="22">{{ $t('WorkflowWork.overdue', ' 逾期') }}({{ timingStateMap[3] }})</a-col>
                              <a-col span="2"><a-switch size="small" v-model="timingStateOverdue"></a-switch></a-col>
                            </a-row>
                            <a-row class="timing-state-due">
                              <a-col span="22">{{ $t('WorkflowWork.expire', ' 到期') }}({{ timingStateMap[2] }})</a-col>
                              <a-col span="2"><a-switch size="small" v-model="timingStateDue"></a-switch></a-col>
                            </a-row>
                            <a-row class="timing-state-alarm">
                              <a-col span="22">{{ $t('WorkflowWork.alarm', ' 预警') }} ({{ timingStateMap[1] }})</a-col>
                              <a-col span="2"><a-switch size="small" v-model="timingStateAlarm"></a-switch></a-col>
                            </a-row>
                            <a-row class="timing-state-normal">
                              <a-col span="22">{{ $t('WorkflowWork.normal', ' 正常') }}({{ timingStateMap[0] }})</a-col>
                              <a-col span="2"><a-switch size="small" v-model="timingStateNormal"></a-switch></a-col>
                            </a-row>
                          </div>
                          <p />
                          <h3>{{ $t('WorkflowWork.processName', ' 流程名称') }}</h3>
                          <a-card class="flow-def-group-list">
                            <template slot="title">
                              <a-checkbox v-model="isCheckAllFlowDefId" @change="onCheckAllFlowDefIdChange">
                                {{ $t('WorkflowWork.selectAll', ' 全选') }}
                              </a-checkbox>
                            </template>
                            <a-checkbox-group
                              v-model="flowDefGroupValues"
                              :options="flowDefGroupOptions"
                              @change="onCheckFlowDefGroupChange"
                            ></a-checkbox-group>
                          </a-card>
                          <a-divider />
                          <a-row type="flex" justify="end">
                            <a-button-group>
                              <a-button type="primary" @click="onFilterSettingOk">{{ $t('WorkflowWork.ok', ' 确定') }}</a-button>
                              <a-button type="primary" @click="onFilterSettingReset">{{ $t('WorkflowWork.reset', ' 重置') }}</a-button>
                            </a-button-group>
                          </a-row>
                        </div>
                      </div>
                      <a-button style="padding: 0 5px" type="link" icon="funnel-plot">{{ $t('WorkflowWork.sift', ' 筛选') }}</a-button>
                    </a-popover>
                    <a-popover v-model="loadingRulesSettingVisible" placement="bottomRight" trigger="click">
                      <template slot="content">
                        <div class="loading-rules-setting">
                          <h3>{{ $t('WorkflowWork.loadRules', ' 加载规则') }}</h3>
                          <a-radio-group v-if="loadingRules" v-model="loadingRules.rule">
                            <a-radio :style="radioStyle" :value="1">{{ $t('WorkflowWork.loadInListOrder', ' 按列表顺序加载') }}</a-radio>
                            <a-radio :style="radioStyle" :value="2">{{ $t('WorkflowWork.loadTimeLimit', ' 按办理时限加载') }}</a-radio>
                            <a-radio :style="radioStyle" :value="3">{{ $t('WorkflowWork.loadSmart', ' 智能加载') }}</a-radio>
                          </a-radio-group>
                          <p />
                          <div v-show="loadingRules && loadingRules.rule != 3">
                            <h3>{{ $t('WorkflowWork.prioritize', ' 优先处理') }}</h3>
                            <a-checkbox :style="checkboxStyle" v-model="specifyFlowCategory">
                              {{ $t('WorkflowWork.specifiedProcessClassification', ' 指定的流程分类') }}
                            </a-checkbox>
                            <a-select
                              v-if="specifyFlowCategory"
                              v-model="loadingRules.flowCategoryUuids"
                              mode="multiple"
                              style="width: 100%"
                              :placeholder="$t('WorkflowWork.pleaseSelect', '请选择')"
                              :options="flowCategoryOptions"
                              show-search
                              :filter-option="filterSelectOption"
                            ></a-select>
                            <a-checkbox :style="checkboxStyle" v-model="specifyFlowDefId">
                              {{ $t('WorkflowWork.specifiedProcess', ' 指定的流程') }}
                            </a-checkbox>
                            <a-select
                              v-if="specifyFlowDefId"
                              v-model="loadingRules.flowDefIds"
                              mode="multiple"
                              style="width: 100%"
                              :placeholder="$t('WorkflowWork.pleaseSelect', '请选择')"
                              :options="flowDefIdOptions"
                              show-search
                              :filter-option="filterSelectOption"
                            ></a-select>
                            <a-checkbox v-if="loadingRules" :style="checkboxStyle" v-model="loadingRules.arriveToday">
                              {{ $t('WorkflowWork.arrivedToday', ' 今日到达') }}
                            </a-checkbox>
                          </div>
                          <a-divider />
                          <a-row type="flex" justify="end">
                            <a-button-group>
                              <!-- <a-button type="default" @click="loadingRulesSettingVisible = false">取消</a-button> -->
                              <a-button type="primary" @click="onLoadingRulesSettingOk">{{ $t('WorkflowWork.ok', ' 确定') }}</a-button>
                              <a-button type="primary" @click="onLoadingRulesSettingReset">
                                {{ $t('WorkflowWork.reset', ' 重置') }}
                              </a-button>
                            </a-button-group>
                          </a-row>
                        </div>
                      </template>
                      <a-button type="link" icon="setting"></a-button>
                    </a-popover>
                  </a-row>
                </a-col>
              </a-row>
            </template>
            <a-list class="todo-list" :data-source="dataList">
              <a-list-item
                class="todo-list-item"
                slot="renderItem"
                slot-scope="item, index"
                :title="item.content"
                @click="onTodoItemClick($event, item, index)"
              >
                <div :class="{ 'item-details': true, active: isCurrentWorkData(item) }">
                  <div class="title">
                    <template v-if="item.titleRenderValue">
                      <div v-html="item.titleRenderValue"></div>
                    </template>
                    <template v-else>{{ item.title }}</template>
                  </div>
                  <div>
                    {{ $t('WorkflowWork.currentTask', ' 当前环节') }}：{{ $t('WorkflowView.' + item.taskId + '.taskName', item.taskName) }}
                  </div>
                  <div>
                    {{ $t('WorkflowWork.processName', ' 流程名称') }}：{{ item.flowName }} |
                    {{ $t('WorkflowWork.initiator', ' 发起人') }}：{{
                      item.flowStartUserName || item.flowStartUserIdRenderValue || item.flowStartUserId
                    }}
                  </div>
                </div>
              </a-list-item>
              <div
                v-if="showLoadingMore"
                slot="loadMore"
                :style="{ textAlign: 'center', marginTop: '12px', height: '32px', lineHeight: '32px' }"
              >
                <a-spin v-if="loadingMore" />
                <a-button v-else type="link" @click="onLoadMoreClick">{{ $t('WorkflowWork.loadMore', ' 加载更多') }}</a-button>
              </div>
            </a-list>
          </a-drawer>
          <div :class="{ 'left-control': true, open: !collapsed }" @click="onLeftControlClick">
            <Icon
              class="trigger"
              :type="collapsed ? 'pticon iconfont icon-ptkj-zhankailiebiao' : 'pticon iconfont icon-ptkj-shouqiliebiao'"
              :style="{ fontSize: '12px' }"
            />
            <span v-show="collapsed" class="badge">{{ pagingInfo.totalCount }}</span>
          </div>
        </a-layout>
        <WorkView
          v-if="workBean"
          ref="workView"
          :workData="workBean"
          :settings="settings"
          @workViewMounted="onWorkViewMounted"
          @actionSuccess="onActionSuccess"
        ></WorkView>
        <!-- 退出连续签批模式提示框 -->
        <a-modal v-model="showNoRecordDialog" :title="$t('WorkflowWork.tips', '提示')" @ok="onExitNoRecordClick">
          <p>{{ $t('WorkflowWork.exitContinuousTips', ' 全部流程已处理，即将退出连续签批模式') }}！</p>
        </a-modal>
      </a-layout-content>
    </a-layout>
  </HtmlWrapper>
</template>
<script type="text/babel">
import '@dyform/app/web/framework/vue/install';
import '@installPageWidget';
import '@workflow/app/web/framework/vue/install';
import '@modules/.webpack.runtime.devjs.js'; // 运行期二开文件
import { deepClone } from '@framework/vue/utils/util';
import { isEmpty, isFunction, map, findIndex, trim as stringTrim, each as forEach } from 'lodash';
export default {
  provide() {
    return {
      designMode: false,
      continuousMode: true
    };
  },
  data() {
    let leftControlCollapsed = 'true';
    if (EASY_ENV_IS_BROWSER) {
      leftControlCollapsed = sessionStorage.getItem('continuous_work_view_left_control_collapsed') || leftControlCollapsed;
    }
    let collapsed = leftControlCollapsed ? leftControlCollapsed == 'true' : false;
    return {
      collapsed,
      leftSiderStyle: {
        width: collapsed ? '0px' : '450px'
      },
      filterVisible: false,
      timingStateMap: {
        0: 0,
        1: 0,
        2: 0,
        3: 0
      }, // 过滤条件折计时状态
      timingStateNormal: true,
      timingStateAlarm: true,
      timingStateDue: true,
      timingStateOverdue: true,
      flowDefGroupOptions: [], // 过滤条件的流程名称选项
      flowDefGroupValues: [],
      isCheckAllFlowDefId: false,
      loadingRulesSettingVisible: false,
      radioStyle: {
        display: 'block',
        height: '30px',
        lineHeight: '30px'
      },
      flowCategoryOptions: [], // 指定的流程分类选项
      checkboxStyle: {
        display: 'block',
        height: '30px',
        lineHeight: '30px',
        marginLeft: '0px'
      },
      flowDefIdOptions: [], // 指定的流程定义ID选项
      dataStoreParams: null,
      specifyFlowCategory: false,
      specifyFlowDefId: false,
      loadingRules: null,
      showInputSearch: false,
      inputQueryValue: '',
      latestQueryValue: null,
      loadingMore: false,
      showLoadingMore: true,
      showNoRecordDialog: false,
      dataList: [], // 数据列表
      pagingInfo: {
        // 分页信息
        pageSize: 10,
        totalCount: 0
      },
      currentRecord: null
    };
  },
  created() {
    const _self = this;
    _self.initDataStoreParams();
    _self.getLoadingRules(result => {
      _self.loadData().then(() => {
        let currentRecord = _self.getCurrentRecord();
        if (!currentRecord && _self.loadingRules && _self.loadingRules.rule == 3) {
          _self.workBeanToCurrentRecord();
        }
      });
    });
    if (this.$i18n.locale !== 'zh_CN' && this.workBean && /[\u4e00-\u9fa5]/.test(this.workBean.title)) {
      this.$translate(this.workBean.title, 'zh', this.$i18n.locale.split('_')[0]).then(t => {
        this.workBean.title = t;
        this.workBean.titleI18n = t;
      });
    }
  },
  mounted() {
    const _self = this;
    var flowDataStoreParams = _self.getTodoListParam();
    // 加载过滤条件的办理时限信息
    $axios.post('/api/workflow/work/todo/groupAndCountByTimingState', flowDataStoreParams).then(result => {
      let dataList = result.data.data.data || [];
      dataList.forEach(state => {
        _self.timingStateMap[state.timingState] = state.count;
      });
    });

    // 加载过滤条件的流程名称信息
    $axios.post('/api/workflow/work/todo/groupAndCountByFlowDefId', flowDataStoreParams).then(result => {
      let dataList = result.data && result.data.data && result.data.data.data;
      let values = [];
      _self.flowDefGroupOptions = map(dataList, function (item) {
        values.push(item.flowDefId);
        return { label: item.flowName + '(' + item.count + ')', value: item.flowDefId };
      });
      _self.flowDefGroupValues = values;
      _self.isCheckAllFlowDefId =
        _self.flowDefGroupValues.length > 0 && _self.flowDefGroupValues.length == _self.flowDefGroupOptions.length;
    });

    // 加载流程分类选项数据
    $axios.get('/api/workflow/category/query', { params: { keyword: '', uuids: [], pageNo: 1, pageSize: 65535 } }).then(result => {
      let dataList = result.data && result.data.data;
      _self.flowCategoryOptions = map(dataList, function (item) {
        return { label: item.name, value: item.uuid };
      });
    });
    // 加载流程定义选项数据
    $axios.get('/api/workflow/definition/query', { params: { keyword: '', uuids: [], pageNo: 1, pageSize: 65535 } }).then(result => {
      let dataList = result.data && result.data.data;
      _self.flowDefIdOptions = map(dataList, function (item) {
        return { label: item.name, value: item.id };
      });
    });
  },
  methods: {
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
      let _requestCode = _self.getQueryString('_requestCode');
      let dataStoreParams = '{"pagingInfo":{}}';
      if (EASY_ENV_IS_BROWSER) {
        dataStoreParams = sessionStorage.getItem(`cwvDataStoreParams_${_requestCode}`) || dataStoreParams;
      }
      _self.dataStoreParams = JSON.parse(dataStoreParams);
      _self.dataStoreParams.params = {};
      // let criterions = map(_self.dataStoreParams.criterions, function (item) {
      //   if (item.sql) {
      //     return item;
      //   }
      // });
      // _self.dataStoreParams.criterions = criterions;
      if (_self.dataStoreParams.pagingInfo) {
        _self.dataStoreParams.pagingInfo.currentPage = 1;
        _self.dataStoreParams.pagingInfo.pageSize = 10;
      } else {
        let initPageSize = 10;
        if (_self.dataStoreParams.currentPage && _self.dataStoreParams.pageSize) {
          initPageSize = _self.dataStoreParams.currentPage * _self.dataStoreParams.pageSize;
          if (initPageSize <= 0) {
            initPageSize = 10;
          }
        }
        _self.dataStoreParams.pagingInfo = {
          currentPage: 1,
          pageSize: initPageSize
        };
      }
    },
    // 获取待办列表的视图查询参数
    getTodoListParam() {
      const _self = this;
      let dataStoreParams = JSON.parse(JSON.stringify(_self.dataStoreParams));
      dataStoreParams.loadingRules = JSON.parse(JSON.stringify(_self.loadingRules));
      return dataStoreParams;
    },
    // 获取浏览器查询数值
    getQueryString(name, defaultValue) {
      if (EASY_ENV_IS_NODE) {
        return null;
      }
      var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)');
      var values = window.location.search.substr(1).match(reg);
      if (values != null) {
        return decodeURIComponent(values[2]);
      }
      if (defaultValue != null) {
        return defaultValue;
      }
      return null;
    },
    // 获取加载规则
    getLoadingRules(callback) {
      var _self = this;
      if (EASY_ENV_IS_NODE) {
        return;
      }
      $axios.get('/api/workflow/user/preferences/getLoadingRules').then(result => {
        _self.loadingRules = result.data.data;
        // 指定的流程分类
        if (_self.loadingRules.flowCategoryUuids && _self.loadingRules.flowCategoryUuids.length > 0) {
          _self.specifyFlowCategory = true;
        }
        // 指定的流程
        if (_self.loadingRules.flowDefIds && _self.loadingRules.flowDefIds.length > 0) {
          _self.specifyFlowDefId = true;
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
            sortName: 'timingState',
            sortOrder: 'desc'
          }
        ];
      }
      _self.loadingMore = true;
      // 从当前列表的下一条记录加载
      flowDataStoreParams.pagingInfo.first = reset ? 0 : _self.dataList.length;
      return $axios
        .post('/api/workflow/work/todo/query', flowDataStoreParams)
        .then(({ data: result }) => {
          // 过滤掉智能加载时可能附加的流程数据
          let data = (result.data.data || []).filter(item => item.uuid != _self.appendTaskInstUuid);
          let pagination = result.data.pagination;
          let hasMerge = false;
          if (reset) {
            _self.dataList.length = 0;
          } else {
            let dataListMap = {};
            let loadLength = data.length;
            _self.dataList.forEach(item => (dataListMap[item.uuid] = item.uuid));
            data = data.filter(item => !dataListMap[item.uuid]);
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
        .catch(error => {
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
          columnIndex: 'uuid',
          value: _this.dataList.map(item => item.uuid),
          type: 'not in'
        });
      }
      $axios.post('/api/workflow/work/todo/query', flowDataStoreParams).then(({ data: result }) => {
        let data = result.data.data || [];
        let dataListMap = {};
        _this.dataList.forEach(item => (dataListMap[item.uuid] = item.uuid));
        data = data.filter(item => !dataListMap[item.uuid]);
        _this.dataList = _this.dataList.concat(data);
      });
    },
    // 查询
    onSearch() {
      const _self = this;
      let dataStoreParams = _self.dataStoreParams;
      // 分页页数重置为1
      dataStoreParams.pagingInfo.currentPage = 1;
      let keyword = stringTrim(_self.inputQueryValue);
      if (_self.latestQueryValue == keyword) {
        return;
      }

      _self.latestQueryValue = keyword;
      if (!isEmpty(keyword)) {
        _self.keywordCriterions = {
          conditions: [
            {
              columnIndex: 'title',
              value: keyword,
              type: 'like'
            },
            {
              columnIndex: 'taskName',
              value: keyword,
              type: 'like'
            },
            {
              columnIndex: 'flowStartUserId',
              value: keyword,
              type: 'like'
            },
            {
              columnIndex: 'flowStartUserName',
              value: keyword,
              type: 'like'
            },
            {
              columnIndex: 'flowName',
              value: keyword,
              type: 'like'
            }
          ],
          type: 'or'
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
    // 上一条
    onPreviousClick() {
      this.gotoPreviousRecord();
    },
    // 下一条
    onNextClick() {
      this.gotoNextRecord();
    },
    // 点击待办列表数据项
    onTodoItemClick(event, item, index) {
      this.showRecord(item);
      // 收起列表
      this.onLeftControlClick();
    },
    // 跳到第一条记录
    gotoFirstRecordIfExists(showNoRecordDialog) {
      const _self = this;
      let dataList = _self.dataList;
      if (dataList.length <= 0) {
        if (showNoRecordDialog) {
          _self.showNoRecordDialog = true;
          _self.workBean = null;
          _self.currentRecord = null;
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
          console.error('next record is null');
        }
      }
    },
    // 进入下一条记录
    gotoNextRecord(force = false, callback) {
      const _self = this;
      if (_self.isLastRecord()) {
        // 加载更多
        if (_self.showLoadingMore) {
          _self.loadMoreData(result => {
            let data = result.data.data;
            if (data && data.length > 0) {
              let item = _self.getNextRecord();
              if (item) {
                _self.showRecord(item);
              } else {
                console.error('next record is null, goto first record if exists');
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
          _self.showRecord(item, force);
        } else {
          console.error('next record is null, goto first record if exists');
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
      let index = _this.dataList.findIndex(item => item.uuid == record.uuid);
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

      let taskInstUuid = _self.workBean && _self.workBean.taskInstUuid;
      let item = _self.dataList[0];
      return item.uuid == taskInstUuid || item.taskInstUuid == taskInstUuid;
    },
    // 是否最后一条记录
    isLastRecord() {
      const _self = this;
      if (isEmpty(_self.dataList)) {
        return true;
      }

      let taskInstUuid = _self.workBean && _self.workBean.taskInstUuid;
      let item = _self.dataList[_self.dataList.length - 1];
      return item.uuid == taskInstUuid || item.taskInstUuid == taskInstUuid;
    },
    // 获取前一条记录
    getPreviousRecord() {
      const _self = this;
      let taskInstUuid = _self.workBean && _self.workBean.taskInstUuid;
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
      let taskInstUuid = _self.workBean && _self.workBean.taskInstUuid;
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
      flowDataStoreParams.criterions = [{ columnIndex: 'uuid', value: taskInstUuid, type: 'eq' }];
      return $axios
        .post('/api/workflow/work/todo/query', flowDataStoreParams)
        .then(({ data: result }) => {
          let data = result.data && result.data.data;
          if (data.length > 0) {
            _this.currentRecord = data[0];
            _this.appendTaskInstUuid = data[0].uuid;
            _this.dataList = [...data, ..._this.dataList];
          }
        })
        .catch(error => {});
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
      let taskInstUuid = _self.workBean && _self.workBean.taskInstUuid;
      for (let index = 0; index < _self.dataList.length; index++) {
        let item = _self.dataList[index];
        if (item.uuid == taskInstUuid || item.taskInstUuid == taskInstUuid) {
          return _self.dataList[index + 1];
        }
      }
      return null;
    },
    // // 提取下一条记录，下一条记录不存在时请求后端取数据
    // fetchNextRecord(callback) {
    //   const _self = this;
    //   let record = _self.getNextRecord();
    //   if (record == null && _self.showLoadingMore) {
    //     _self.loadMoreData(result => {
    //       let data = result.data.data;
    //       if (data && data.length > 0) {
    //         let item = _self.getNextRecord();
    //         callback.call(_self, item);
    //       } else {
    //         callback.call(_self);
    //       }
    //     });
    //   } else {
    //     callback.call(_self, record);
    //   }
    // },
    // 更新记录
    updateRecord(record) {
      const _self = this;
      if (record == null) {
        return Promise.resolve(false);
      }
      // 连续签批模式
      _self.loadingRules.mode = 2;
      let flowDataStoreParams = _self.getTodoListParam();
      flowDataStoreParams.criterions = [{ columnIndex: 'flowInstUuid', value: record.flowInstUuid, type: 'eq' }];
      return $axios
        .post('/api/workflow/work/todo/query', flowDataStoreParams)
        .then(result => {
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
        .catch(error => {
          return false;
        });
    },
    // 稍候处理
    onHandleLaterClick() {
      const _self = this;
      if (_self.isLastRecord()) {
        _self.$message.warn(_self.$t('WorkflowWork.message.currentLastProcess', '当前为最后一条流程！'));
        return;
      }

      let workData = _self.workBean;
      if (workData == null) {
        return;
      }

      _self.checkWorkDataChanged(() => {
        $axios
          .post(
            '/api/workflow/work/todo/dealLater',
            {
              taskInstUuid: workData.taskInstUuid
            },
            {
              headers: {
                contentType: 'application/x-www-form-urlencoded'
              }
            }
          )
          .then(result => {
            let currentRecord = _self.getCurrentRecord();
            let item = _self.getNextRecord();
            if (item) {
              _self.gotoNextRecord(true);
            } else {
              _self.gotoFirstRecordIfExists();
            }
            if (currentRecord) {
              _self.moveToLatest(currentRecord);
            }
          });
      });
    },
    // 显示记录到工作区
    showRecord(item, force = false) {
      let _this = this;
      if (!_this.isCurrentWorkData(item)) {
        if (force) {
          _this.currentRecord = item;
          _this.changeWorkView(item);
        } else {
          _this.checkWorkDataChanged(() => {
            _this.currentRecord = item;
            _this.changeWorkView(item);
          });
        }
      }
    },
    checkWorkDataChanged(callback) {
      let _this = this;
      if (_this.workBean && _this.$refs.workView.displayState != 'label' && _this.$refs.workView.workView.isDyformDataChanged()) {
        let locale = _this.$refs.workView && _this.$refs.workView.locale;
        _this.$confirm({
          title: _this.$t('WorkflowWork.message.confirmModal', '确认框'),
          content: _this.$t('WorkflowWork.message.dyformDataChangedClose', '内容已修改，请先保存后再操作，是否放弃修改！'),
          okText: locale && locale.Modal && locale.Modal.okText,
          cancelText: locale && locale.Modal && locale.Modal.cancelText,
          onOk() {
            callback && callback.call(_this);
            // _this.currentRecord = item;
            // _this.changeWorkView(item);
          }
        });
      } else {
        callback && callback.call(_this);
        // _this.currentRecord = item;
        // _this.changeWorkView(item);
      }
    },
    // 更新工作区数据
    updateWorkData(record) {
      this.changeWorkView(record);
    },
    // 切换工作区数据
    changeWorkView(item, callback) {
      const _self = this;
      // 加载流程数据
      _self.loadWorkData(item, result => {
        _self.workBean = null;
        _self.$nextTick(() => {
          _self.workBean = result.data;
          // 变更文档信息
          if (_self.workBean) {
            _self.changeDocumentInfo(Object.assign(item, { title: _self.workBean.title }));
            if (callback) {
              callback.call(_self, _self.workBean);
            }
          } else {
            console.warn('work data is null', item);
            _self.$message.error('流程数据加载失败！');
          }
        });
      });
    },
    // 变更浏览器文档相关信息
    changeDocumentInfo(item) {
      const _self = this;
      let taskInstUuid = item.taskInstUuid || item.uuid;
      let flowInstUuid = item.flowInstUuid;
      let taskIdentityUuid = item.taskIdentityUuid;
      let sameUserSubmitType = item.sameUserSubmitType;
      let autoSubmit = item.autoSubmit;
      let sameUserSubmitTaskOperationUuid = item.sameUserSubmitTaskOperationUuid;
      let title = item.title;
      let newUrl = window.location.href;
      if (newUrl.indexOf('sameUserSubmitType') != -1) {
        newUrl = newUrl.replace(/&sameUserSubmitType=([^&]*)/gi, '');
      }
      if (newUrl.indexOf('auto_submit') != -1) {
        newUrl = newUrl.replace('&auto_submit=true', '');
      }
      if (newUrl.indexOf('sameUserSubmitTaskOperationUuid') != -1) {
        newUrl = newUrl.replace(/&sameUserSubmitTaskOperationUuid=([^&]*)/gi, '');
      }
      let params = [
        {
          key: 'taskInstUuid',
          value: taskInstUuid
        },
        {
          key: 'flowInstUuid',
          value: flowInstUuid
        }
      ];
      if (!isEmpty(taskIdentityUuid)) {
        params.push({
          key: 'taskIdentityUuid',
          value: taskIdentityUuid
        });
      }
      newUrl = _self.replaceUrlQueryString(newUrl, params);
      // 自动提交参数
      if (autoSubmit == true) {
        if (!isEmpty(sameUserSubmitType)) {
          newUrl += `&sameUserSubmitType=${sameUserSubmitType}`;
        }
        newUrl += `&auto_submit=true`;
        if (!isEmpty(sameUserSubmitTaskOperationUuid)) {
          newUrl += `&sameUserSubmitTaskOperationUuid=${sameUserSubmitTaskOperationUuid}`;
        }
      }
      window.history.replaceState(null, null, newUrl);
      if (title) {
        window.document.title = title;
      }
    },
    // 加载工作区数据
    loadWorkData(item, callback) {
      const _self = this;
      _self.$loading(true);
      $axios
        .post('/api/workflow/work/getTodoWorkData', {
          flowInstUuid: item.flowInstUuid,
          taskInstUuid: item.taskInstUuid || item.uuid,
          aclRole: 'TODO',
          loadDyFormData: true,
          sameUserSubmitTaskOperationUuid: item.sameUserSubmitTaskOperationUuid
        })
        .then(({ data: result }) => {
          _self.$loading(false);
          callback.call(_self, result);
        })
        .catch(error => {
          _self.$loading(false);
        });
    },
    // 替换url参数
    replaceUrlQueryString(rawUrl, params) {
      let url = rawUrl;
      let re = '';
      forEach(params, function (item) {
        re = eval('/(' + item.key + '=)([^&]*)/gi');
        url = url.replace(re, item.key + '=' + item.value);
      });
      return url;
    },
    // 左侧控制按钮点击事件
    onLeftControlClick() {
      const _self = this;
      _self.collapsed = !_self.collapsed;
      sessionStorage.setItem('continuous_work_view_left_control_collapsed', _self.collapsed);
      if (_self.collapsed) {
        _self.leftSiderStyle.width = '0px';
      } else {
        _self.leftSiderStyle.width = '450px';
      }
    },
    // 判断记录是否当前工作区数据
    isCurrentWorkData(item) {
      return (item.taskInstUuid || item.uuid) == (this.workBean && this.workBean.taskInstUuid);
    },
    onExitNoRecordClick() {
      window.close();
    },
    // 退出连续签批
    onExitContinuousClick() {
      const _self = this;
      if (_self.workBean) {
        if (_self.workView) {
          _self.checkWorkDataChanged(() => {
            _self.workView.allowUnloadWorkData = true;
            _self.workView.exitContinuousMode();
          });
        }
      } else {
        window.close();
      }
    },
    // 流程工作区内容加载事件
    onWorkViewMounted(workView) {
      this.workView = workView;
    },
    // 流程名称全选变更事件
    onCheckAllFlowDefIdChange() {
      const _self = this;
      let values = [];
      if (_self.isCheckAllFlowDefId) {
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
        _self.isCheckAllFlowDefId = true;
      } else {
        _self.isCheckAllFlowDefId = false;
      }
    },
    // 确定过滤条件
    onFilterSettingOk() {
      const _self = this;
      _self.collectFilterCriterions();
      _self.reloadData();
      _self.filterVisible = false;
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

      if (timingStates.length > 0 && timingStates.length < 4) {
        _self.filterCriterions.push({
          columnIndex: 'timingState',
          value: timingStates,
          type: 'in'
        });
      }
      if (_self.flowDefGroupValues.length > 0 && !_self.isCheckAllFlowDefId) {
        _self.filterCriterions.push({
          columnIndex: 'flowDefId',
          value: _self.flowDefGroupValues,
          type: 'in'
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
    // 确定加载规则
    onLoadingRulesSettingOk() {
      const _self = this;
      _self.saveLoadingRules();
    },
    // 重置加载规则
    onLoadingRulesSettingReset() {
      const _self = this;
      _self.loadingRules = Object.assign(_self.loadingRules, { rule: 1, flowCategoryUuids: [], flowDefIds: [], arriveToday: false });
      _self.saveLoadingRules();
    },
    // 保存加载规则
    saveLoadingRules() {
      const _self = this;
      if (!_self.specifyFlowCategory) {
        _self.loadingRules.flowCategoryUuids = [];
      }
      if (!_self.specifyFlowDefId) {
        _self.loadingRules.flowDefIds = [];
      }
      $axios.post('/api/workflow/user/preferences/saveLoadingRules', _self.loadingRules).then(result => {
        _self.reloadData();
      });
      _self.loadingRulesSettingVisible = false;
    },
    // 获取元素指定clss样式名的上级元素
    getParentElementByClassName(element, className) {
      let node = element;
      if (node == null) {
        return node;
      }
      while (node.parentNode != null) {
        let parentNode = node.parentNode;
        if (parentNode.classList && parentNode.classList.contains(className)) {
          return parentNode;
        } else {
          node = parentNode;
        }
      }
      return null;
    },
    // 刷新父窗口
    refreshParent(options) {
      try {
        // if (window.opener && window.opener.location && window.opener.location.reload) {
        //   window.opener.location.reload();
        // }
        this.pageContext.emitCrossTabEvent('workflow:detail:change', options || {});
      } catch (e) {
        console.error(e);
      }
    },
    // 刷新父窗口并更新列表
    refreshParentAndTodoList(options) {
      const _self = this;
      // 刷新父窗口
      _self.refreshParent();
      // 提示信息
      _self.$message.success(options.message);

      // 更新或移除当前一条
      let currentRecord = _self.getCurrentRecord();
      _self.updateRecord(currentRecord).then(updated => {
        if (updated) {
          _self.showRecord(currentRecord);
        } else {
          _self.gotoNextRecord(false, () => {
            if (!_self.getNextRecord() && _self.dataList.length <= 1) {
              _self.showNoRecordDialog = true;
              _self.workBean = null;
              _self.currentRecord = null;
            }
            _self.removeRecord(currentRecord);
          });
        }
      });
    },
    // 显示提示信息
    showResultMessage(options) {
      this.$message.success(options.message);
    },
    // 显示提示信息并重新加载当前内容
    showResultMessageAndReload(options) {
      const _self = this;
      _self.$message.success(options.message);
      // 更新当前数据
      _self.updateWorkData(_self.getCurrentRecord());
    },
    // 操作成功
    onActionSuccess(options) {
      const _self = this;
      let action = options.action;
      switch (action) {
        case 'save': // 保存
          _self.onSaveSuccess(options);
          break;
        case 'submit': // 提交
          _self.onSubmitSuccess(options);
          break;
        case 'cancel': // 撤回
          _self.refreshParent();
          break;
        case 'rollback': // 退回
          _self.onRollbackSuccess(options);
          break;
        case 'directRollback': // 直接退回
        case 'rollbackToMainFlow': // 退回主流程
        case 'transfer': // 转办
        case 'counterSign': // 会签
        case 'addSign': // 加签
        case 'remove': // 删除
        case 'recover': // 恢复
          _self.refreshParentAndTodoList(options);
          break;
        case 'copyTo': // 抄送
        case 'print': // 套打
        case 'remind': // 催办
          _self.showResultMessage(options);
          break;
        case 'attention': // 关注
        case 'unfollow': // 取消关注
        case 'handOver': // 特送个人
          _self.showResultMessageAndReload(options);
          break;
        case 'gotoTask': // 特送环节
          _self.refreshParentAndTodoList(options);
          break;
        case 'suspend': // 挂起
        case 'resume': // 恢复
          _self.showResultMessageAndReload(options);
          break;
        case 'subflowAddSubflow': // 子流程添加承办
        case 'subflowAddMajorFlow': // 子流程添加主办
        case 'subflowAddMinorFlow': // 子流程添加协办
        case 'subflowRemind': // 子流程催办
        case 'subflowSendMessage': // 子流程信息分发
        case 'subflowLimitTime': // 子流程协办时限
        case 'subflowRedo': // 子流程重办
          _self.showResultMessageAndReload(options);
          break;
        case 'subflowStop': // 子流程终止
          _self.onSubflowStopSuccess(options);
          break;
        default:
          _self.showResultMessage(options);
      }
    },
    // 保存成功
    onSaveSuccess(options) {
      this.showResultMessageAndReload(options);
    },
    // 提交成功
    onSubmitSuccess(options) {
      const _self = this;
      let result = options.result;
      // 刷新父窗口
      _self.refreshParent();

      // 提示信息
      _self.$message.success(options.message);

      let sameUserSubmitInfo = _self.$refs.workView.getSameUserSubmitInfo(result);
      // 与前一环节办理人相同时
      if (sameUserSubmitInfo) {
        _self.handleSameUserSubmitInfo(sameUserSubmitInfo);
      } else {
        // 更新或移除当前一条
        let currentRecord = _self.getCurrentRecord();
        _self.updateRecord(currentRecord).then(updated => {
          if (updated) {
            _self.showRecord(currentRecord);
          } else {
            _self.gotoNextRecord(true, () => {
              if (!_self.getNextRecord() && _self.dataList.length <= 1) {
                _self.showNoRecordDialog = true;
                _self.workBean = null;
                _self.currentRecord = null;
              }
              _self.removeRecord(currentRecord);
            });
          }
        });
      }
    },
    // 与前环节相同自动提交
    handleSameUserSubmitInfo(sameUserSubmitInfo) {
      const _self = this;
      let sameUserSubmitType = sameUserSubmitInfo.sameUserSubmitType ? sameUserSubmitInfo.sameUserSubmitType : '0';
      let refreshUrl = sameUserSubmitInfo.refreshUrl;
      let keepOnCurrentPage = false;
      switch (sameUserSubmitType) {
        case '0': // 自动提交，让办理人确认是否继承上一环节意见
        case '1': // 自动提交，且自动继承意见
          keepOnCurrentPage = true;
          break;
        case '2': // 不自动提交并关闭页面
          break;
        case '3': // 不自动提交并刷新页面
          keepOnCurrentPage = true;
          break;
        case '4': // 不自动提交并刷新页面，且不自动继承意见
          keepOnCurrentPage = true;
          sameUserSubmitInfo.sameUserSubmitTaskOperationUuid = '';
          sameUserSubmitInfo.autoSubmit = false;
          break;
      }

      // if (refreshUrl.indexOf('continuousMode') == -1) {
      //   refreshUrl += '&continuousMode=1';
      // }
      // 与前环节相同自动提交
      // if (keepOnCurrentPage) {
      let currentRecord = _self.getCurrentRecord();
      _self.changeWorkView(sameUserSubmitInfo, workData => {
        _self.workDataToRecord(workData, currentRecord);
      });
      // setTimeout(() => {
      //   window.location.href = refreshUrl;
      // }, 2000);
      // } else {
      //   _self.fetchNextRecord(record => {
      //     if (record) {
      //       let params = [
      //         {
      //           key: 'taskInstUuid',
      //           value: sameUserSubmitInfo.taskInstUuid
      //         },
      //         {
      //           key: 'flowInstUuid',
      //           value: sameUserSubmitInfo.flowInstUuid
      //         }
      //       ];
      //       let newUrl = _self.replaceUrlQueryString(refreshUrl, params);
      //       newUrl = newUrl.replace(/sameUserSubmitTaskOperationUuid=([^&]*)/gi, '');
      //       newUrl = newUrl.replace('&auto_submit=true', '');
      //       setTimeout(() => {
      //         window.location.href = newUrl;
      //       }, 2000);
      //     }
      //   });
      // }
    },
    // 退回成功
    onRollbackSuccess(options) {
      const _self = this;
      let result = options.result;
      // 刷新父窗口
      _self.refreshParent();

      // 提示信息
      _self.$message.success(options.message);

      // 退回到自己
      let rollbackToSelfInfo = _self.$refs.workView.getRollbackToSelfInfo(result);
      if (rollbackToSelfInfo && rollbackToSelfInfo.rollbackToSelf) {
        _self.rollbackToSelf(rollbackToSelfInfo);
      } else {
        // 移除当前一条
        _self.removeRecord(_self.getCurrentRecord());
        // 进入下一条
        _self.gotoNextRecord();
      }
    },
    // 退回到自己
    rollbackToSelf(rollbackToSelfInfo) {
      const _self = this;
      let currentRecord = _self.getCurrentRecord();
      _self.changeWorkView(rollbackToSelfInfo, workData => {
        _self.workDataToRecord(workData, currentRecord);
      });
    },
    // 子流程终止成功处理
    onSubflowStopSuccess(options) {
      const _self = this;
      let stopSelf = options.stopSelf;
      if (stopSelf === true) {
        _self.refreshParentAndTodoList(options);
      } else {
        _self.showResultMessageAndReload(options);
      }
    },
    // 单据流程信息更新到列表记录
    workDataToRecord(workData, record) {
      if (record.taskInstUuid) {
        record.taskInstUuid = workData.taskInstUuid;
      } else {
        record.uuid = workData.taskInstUuid;
      }
      record.flowInstUuid = workData.flowInstUuid;
      record.title = workData.title;
      record.taskId = workData.taskId;
      record.taskName = workData.taskName;
      record.taskIdentityUuid = workData.taskIdentityUuid;
    }
  },
  computed: {
    // 查询条件是否为空
    isEmptyInputQueryValue() {
      return isEmpty(this.inputQueryValue);
    }
  },
  watch: {
    // filterVisible: function (newValue) {
    //   const _self = this;
    //   _self.$nextTick(() => {
    //     let filterElement = document.querySelector('.ant-popover-inner-content .filter');
    //     let popoverElement = _self.getParentElementByClassName(filterElement, 'ant-popover');
    //     if (popoverElement != null) {
    //       if (newValue) {
    //         setTimeout(() => {
    //           popoverElement.style.left = 0;
    //           popoverElement.style.width = '450px';
    //           popoverElement.querySelector('.ant-popover-arrow').style.right = '100px';
    //         }, 1);
    //       } else {
    //         popoverElement.style.top = '97.7px';
    //         popoverElement.style.width = '0px';
    //         popoverElement.style.display = 'none';
    //       }
    //     }
    //   });
    // },
    // loadingRulesSettingVisible: function (newValue) {
    //   const _self = this;
    //   _self.$nextTick(() => {
    //     let filterElement = document.querySelector('.ant-popover-inner-content .loading-rules-setting');
    //     let popoverElement = _self.getParentElementByClassName(filterElement, 'ant-popover');
    //     if (popoverElement != null) {
    //       if (newValue) {
    //         setTimeout(() => {
    //           popoverElement.style.top = '97.7px';
    //           popoverElement.style.left = 0;
    //           popoverElement.style.width = '450px';
    //           popoverElement.querySelector('.ant-popover-arrow').style.right = '36px';
    //         }, 1);
    //       } else {
    //         popoverElement.style.width = '0px';
    //         popoverElement.style.display = 'none';
    //       }
    //     }
    //   });
    // }
  }
};
</script>
<style scoped>
.continuous-work-view >>> .ant-notification {
  top: 65px !important;
}
.continuous-work-view >>> .work-view-header + .work-view-content {
  margin-top: 0;
}
.continuous-work-view >>> .work-view-header {
  border-top: 4px solid rgba(0, 0, 0, 0.5);
}
.header-button-group {
  position: fixed;
  top: 4px;
  left: 50%;
  z-index: 101;
  width: 412px;
  height: 40px;
  line-height: 40px;
  margin-left: -206px;
  background: url(/static/images/workflow/work_view_continuous_bg.png) no-repeat;
  background-size: cover;
  text-align: center;
  transition: all 0.3s;
}
.header-button-group button {
  font-size: 14px;
  color: #fff;
}
.header-button-group button.disabled {
  color: #aaa;
  cursor: default;
}

.left-sider {
  position: fixed;
  top: 16px;
  left: 0;
  z-index: 2; /* ant-tabs-ink-bar的z-index:1*/
  height: 100%;
}

.left-sider .left-control {
  top: 60px;
  position: absolute;
  width: 40px;
  height: 32px;
  z-index: 102;
  line-height: 32px;
  text-align: center;
  color: #ffffff;
  background: rgba(0, 0, 0, 0.65);
  box-shadow: 0 0 16px 0 rgba(0, 0, 0, 0.65);
  border-top-right-radius: 50%;
  border-bottom-right-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
}
.left-sider .left-control.open {
  margin-left: 450px;
}
.left-sider .left-control .badge {
  position: absolute;
  top: -4px;
  left: 22px;
  height: 16px;
  padding: 0 5px;
  line-height: 16px;
  font-size: 12px;
  color: #ffffff;
  background: #e33033;
  border-radius: 10px;
}

.left-sider-drawer >>> .ant-drawer-header {
  position: fixed;
  top: 0;
  left: 0;
  z-index: 102;
  width: 450px;
  padding: 16px 24px;
}
.left-sider-drawer .search-icon {
  color: #8c8c8c;
  padding-top: 9px;
  padding-right: 12px;
  font-size: 14px;
  cursor: pointer;
}
.left-sider-drawer .col-icons {
  height: 32px;
  line-height: 32px;
  cursor: pointer;
}
.left-sider-drawer >>> .ant-drawer-body {
  padding: 0px;
  margin-top: 64px;
}
.left-sider-drawer >>> .ant-drawer-title .badge {
  position: relative;
  top: -10px;
  left: -5px;
  height: 20px;
  padding: 0 5px;
  line-height: 20px;
  font-size: 12px;
  color: #ffffff;
  background: #e33033;
  border-radius: 10px;
}

.filter h3 {
  padding-right: 290px;
}
.filter .timing-state >>> .ant-row {
  padding: 5px 10px;
  border-radius: 4px;
  margin-top: 10px;
}
.filter .timing-state-normal {
  background-color: #e6f7ff;
  border: 1px solid #91d5ff;
}
.filter .timing-state-alarm {
  background-color: var(--w-warning-color-1);
  border: 1px solid var(--w-warning-color);
}
.filter .timing-state-due {
  background-color: var(--w-warning-color-4);
  border: 1px solid var(--w-warning-color-8);
}
.filter .timing-state-overdue {
  background-color: var(--w-danger-color-1);
  border: 1px solid var(--w-danger-color);
}

.filter .flow-def-group-list >>> .ant-card-head {
  min-height: 32px;
}
.filter .flow-def-group-list >>> .ant-card-head-title {
  padding: 8px 0;
}
.filter .flow-def-group-list >>> .ant-card-body {
  padding: 8px 24px;
}
.filter .flow-def-group-list >>> .ant-card-body {
  height: 250px;
  overflow: auto;
}
.filter .flow-def-group-list >>> .ant-checkbox-group-item {
  display: block;
}

.loading-rules-setting h3 {
  padding-right: 320px;
}

.todo-list .todo-list-item {
  position: relative;
  padding-left: 24px;
}
.todo-list .todo-list-item:hover {
  background-color: var(--w-primary-color-1);
}
.todo-list .item-details {
  line-height: 26px;
  color: #999999;
  cursor: pointer;
}
.todo-list .title {
  font-size: 16px;
  color: #333333;
}
.todo-list .item-details.active .title {
  font-weight: bold;
  color: var(--w-primary-color);
}
.todo-list .item-details.active::before {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  content: '';
  width: 4px;
  background: var(--w-primary-color);
}
.todo-list .item-details.active::after {
  position: absolute;
  top: 50%;
  left: 4px;
  content: '';
  margin-top: -6px;
  border-left: 6px solid var(--w-primary-color);
  border-top: 6px solid transparent;
  border-bottom: 6px solid transparent;
}
</style>
