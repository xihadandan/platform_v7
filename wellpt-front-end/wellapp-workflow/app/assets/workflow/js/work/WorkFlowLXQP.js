define(['jquery', 'server', 'commons', 'constant', 'appContext', 'appModal', 'vue'], function (
  $,
  server,
  commons,
  constant,
  appContext,
  appModal,
  Vue
) {
  var UUID = commons.UUID;
  var StringUtils = commons.StringUtils;
  var StringBuilder = commons.StringBuilder;
  // 工具栏
  var WorkFlowLXQP = function (workView) {
    var _self = this;
    _self.workView = workView;
    _self.$element = _self.workView.$element;
  };
  commons.inherit(WorkFlowLXQP, $.noop, {
    // 初始化
    init: function (curData) {
      console.log('---------进入连续签批模式---------');
      var _self = this;
      window.WorkFlowLXQP = _self;
      _self.recordCurFlowDefinition = {
        uuid: curData.taskInstUuid,
        flowInstUuid: curData.flowInstUuid
      };
      _self.loadMode = 'all'; //all 全部加载 page 分页加载
      _self.create();
      _self.createVue();
      // _self.run();
      _self.bindEvent();
      _self.setTodoListScroll();
      $('.select-flow-options', _self.$leftControl).slimScroll({
        width: '100%',
        height: 190,
        color: '#000',
        opacity: 0.15,
        wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
      });
    },
    destory: function () {
      var _self = this;
      _self.$lxqpWrap.remove();
    },
    create: function () {
      var _self = this;
      var html = `<div id="lxqpWrap" class="lxqp-wrap" :class="lxqpShow ? 'show' : ''">
      <div class="top-control">
        <div class="lxqp-btn lxqp-prev" :class="prevStatus ? '' : 'disabled'" @click="prev">
          <i class="iconfont icon-ptkj-shangyigefanhui"></i>
          <span>上一条</span>
        </div>
        <div class="lxqp-btn lxqp-next" :class="nextStatus ? '' : 'disabled'" @click="next">
          <i class="iconfont icon-ptkj-xiayigeqianjin"></i>
          <span>下一条</span>
        </div>
        <div class="lxqp-btn lxqp-wait" @click="wait">
          <i class="iconfont icon-ptkj-shaohouchuli"></i>
          <span>稍候处理</span>
        </div>
        <div class="lxqp-btn lxqp-close" @click="beforeExit" title="退出连续签批">
          <i class="iconfont icon-ptkj-tuichudenglu"></i>
        </div>
      </div>
      <div class="left-control" :class="leftControlShow ? 'open' : ''">
        <div class="icon" @click="switchLeftStatus">
          <i class="icon-zk iconfont icon-ptkj-zhankailiebiao"></i>
          <i class="icon-sq iconfont icon-ptkj-shouqiliebiao"></i>
          <div class="badge flow-num">{{todoListTotal}}</div>
        </div>
        <div class="lxqp-left-wrap">
        <div class="well-loading-wrap" v-show="leftListLoading" style="top: 44px">
          <div class="well-loading">
            <div class="loader"></div>
            <div class="loading-msg">数据加载中，请稍候……</div>
          </div>
        </div>
          <div class="lxqp-left-header">
            <div class="title">待办列表<span> <span class="badge flow-num">{{todoListTotal}}</span></span></div>
            <div class="inline-block" style="float: right">
              <div class="search" :class="keywordHover ? 'hover' : ''">
                <input class="form-control" ref="input" placeholder="搜索" @blur="keywordBlur" @focus="keywordFocus" @change="keywordSearch" @keyup.enter="keywordSearch" v-model.trim="keyword">
                <div class="clear" v-show="keywordClear" @click="clearKeyword">
                  <i class="iconfont icon-ptkj-dacha-xiao"></i>
                </div>
                <i class="search-icon iconfont icon-ptkj-sousuochaxun" @click="keywordSearch"></i>
              </div>
              <div class="filter popup-item" :class="filterOrRuleStatus === 'filter' ? 'active' : ''" @click="switchFilterOrRuleStatus('filter')">
                <i class="iconfont icon-ptkj-shaixuan"></i>
                <span>筛选</span>
              </div>
              <div class="set popup-item" :class="filterOrRuleStatus === 'set' ? 'active' : ''" @click="switchFilterOrRuleStatus('set')">
                <i class="iconfont icon-ptkj-shezhi"></i>
              </div>
            </div>
          </div>
          <div class="lxqp-left-list">
            <ul class="todo-list" v-show="!showFilterData">
              <li :ref="item.uuid" :class="item.uuid === recordCurFlowDefinition.uuid ? 'active' : ''" v-for="(item, index) in todoList" :index="index" :key="item.uuid" :uuid="item.uuid" @click="clickTodoListItem(item,index)">
                <div class="item-wrap">
                  <div>{{(item.uuid === recordCurFlowDefinition.uuid && moveTop) ? moveToTop(item) : ''}}</div>
                  <div class="title ellipsis" :title="item.title" v-html="item.titleRenderValue"></div>
                  <div class="desc">
                    <div class="desc-item ellipsis mt10" :title="'当前环节：' + item.taskName">当前环节：{{item.taskName}}</div>
                    <div class="desc-item mt10 flex">
                      <span class="flowName inline-block ellipsis" :style="{width: item.flowNameWidth + 'px'}" style="margin-right: 10px;padding-right: 10px;border-right: 1px solid #999;" :title="'流程名称：' + item.flowName">流程名称：{{item.flowName}}</span>
                      <span class="flowStartUserName inline-block ellipsis" :style="{width: + item.flowStartUserNameWidth + 'px'}"  :title="'发起人：' + (item.flowStartUserName || item.flowStartUserIdRenderValue || item.flowStartUserId)">发起人：{{(item.flowStartUserName || item.flowStartUserIdRenderValue || item.flowStartUserId)}}</span>
                    </div>
                  </div>
                  <div class="desc" style="margin-top: 15px">
                    <span class="flowName1" style="padding-right: 10px;border-right: 1px solid #999;">流程名称：{{item.flowName}}</span><br>
                    <span class="flowStartUserName1">发起人：{{(item.flowStartUserName || item.flowStartUserIdRenderValue || item.flowStartUserId)}}</span>
                  </div>
                </div>
              </li>
            </ul>
            <ul class="filter-list" v-show="showFilterData">
              <li :class="item.uuid === recordCurFlowDefinition.uuid ? 'active' : ''" v-for="(item, index) in filterList" :index="index" :key="item.uuid" :uuid="item.uuid" @click="clickFilterListItem(item,index)">
                <div class="item-wrap">
                <div class="title ellipsis" :title="item.title" v-html="item.titleRenderValue"></div>
                <div class="desc">
                  <div class="desc-item ellipsis mt10" :title="'当前环节：' + item.taskName">当前环节：{{item.taskName}}</div>
                  <div class="desc-item mt10 flex">
                    <span class="flowName inline-block ellipsis" :style="{width: item.flowNameWidth + 'px'}" style="margin-right: 10px;padding-right: 10px;border-right: 1px solid #999;" :title="'流程名称：' + item.flowName">流程名称：{{item.flowName}}</span>
                    <span class="flowStartUserName inline-block ellipsis" :style="{width: + item.flowStartUserNameWidth + 'px'}"  :title="'发起人：' + (item.flowStartUserName || item.flowStartUserIdRenderValue || item.flowStartUserId)">发起人：{{(item.flowStartUserName || item.flowStartUserIdRenderValue || item.flowStartUserId)}}</span>
                  </div>
                </div>
                <div class="desc" style="margin-top: 15px">
                  <span class="flowName1" style="padding-right: 10px;border-right: 1px solid #999;">流程名称：{{item.flowName}}</span><br>
                  <span class="flowStartUserName1">发起人：{{(item.flowStartUserName || item.flowStartUserIdRenderValue || item.flowStartUserId)}}</span>
                </div>
              </div>
            </li>
            </ul>
          </div>
          <div class="bottom-scroll-btn hide">
            <div class="scroll-bottom scroll-btn"><i class="iconfont icon-ptkj-shixinjiantou-xia"></i></div>
          </div>
          <div class="lxqp-filter lxqp-popup">
            <div class="tit zoom zoom-item mb15">办理时限</div>
            <div class="zoom zoom-item">
              <div timingState="3" @click="changeFilterFlowState(3)" :class="checkFilterFlowState(3)" class="flow-timingState-btn well-btn w-btn-block has-background w-btn-danger c-danger w-line-btn w-btn-checkbox mb10">逾期 (<span>{{timingStateNum[3]}}</span>)</div>
              <div timingState="1" @click="changeFilterFlowState(1)" :class="checkFilterFlowState(1)" class="flow-timingState-btn well-btn w-btn-block has-background w-btn-warning c-warning w-line-btn w-btn-checkbox mb10">预警 (<span>{{timingStateNum[1]}}</span>)</div>
              <div timingState="0" @click="changeFilterFlowState(0)" :class="checkFilterFlowState(0)" class="flow-timingState-btn well-btn w-btn-block has-background w-btn-success c-success w-line-btn w-btn-checkbox mb10">正常 (<span>{{timingStateNum[0]}}</span>)</div>
            </div>
            <div class="tit zoom zoom-item mb15" style="margin-top: 25px">流程名称</div>
            <div class="select-flow-wrap">
              <div class="well-form-group select-all zoom zoom-item">
                <input type="checkbox" id="selectAllFlow" name="selectAll" value="all" v-model="filterFlowGroupsCheckAll" @change="changeAllChecked()">
                <label class="mb0" for="selectAllFlow">全选</label>
              </div>
              <div class="select-flow-options">
                <div class="well-form-group zoom-item" v-for="(item, index) in filterFlowGroups" :key="item.flowDefId">
                  <input type="checkbox" :id="item.flowDefId" name="flowGroup" :value="item.flowDefId" v-model="checkFilterFlowGroups">
                  <label class="mb0" :for="item.flowDefId">{{item.flowName}} ({{item.count}})</label>
                </div>
              </div>
            </div>
            <div class="btns">
              <div id="filterSubmit" class="well-btn w-btn-primary" @click="filterSubmit(false)">确定</div>
              <div class="cancel-btn well-btn w-btn-dark w-line-btn like-primary" @click="cancelPopup">取消</div>
              <div id="filterReset" class="reset-btn well-btn w-btn-dark w-line-btn like-primary mr0" @click="filterReset(false,true)">重置</div>
            </div>
          </div>
          <div class="lxqp-set lxqp-popup">
            <div class="tit zoom zoom-item">加载规则</div>
            <div class="well-form-group zoom zoom-item">
              <input type="radio" id="sort" name="rule" value="1" v-model.number="curRule">
              <label class="mb0" for="sort">按列表顺序加载</label>
            </div>
            <div class="well-form-group zoom zoom-item">
              <input type="radio" id="time" name="rule" value="2" v-model.number="curRule">
              <label class="mb0" for="time">按办理时限加载</label>
            </div>
            <div class="well-form-group zoom zoom-item">
              <input type="radio" id="smart" name="rule" value="3" v-model.number="curRule">
              <label class="mb0" for="smart">智能加载</label>
            </div>
            <div class="precedence">
              <div class="tit zoom zoom-item mb-sm">优先处理</div>
              <div class="well-form-group zoom zoom-item">
                <input type="checkbox" id="category" name="handle" value="categoryQuery" v-model="ruleHandle" @change="ruleHandleChange('categoryQuery')">
                <label class="mb0" for="category">指定的流程分类</label>
              </div>
              <div class="categoryQuery" v-show="ruleHandle.indexOf('categoryQuery') > -1">
                <input id="categoryQuery" class="form-control" placeholder="请选择">
              </div>
              <div class="well-form-group zoom zoom-item">
                <input type="checkbox" id="definition" name="handle" value="definitionQuery" v-model="ruleHandle" @change="ruleHandleChange('definitionQuery')">
                <label class="mb0" for="definition">指定的流程</label>
              </div>
              <div class="definitionQuery" v-show="ruleHandle.indexOf('definitionQuery') > -1">
                <input id="definitionQuery" class="form-control" placeholder="请选择">
              </div>
              <div class="well-form-group zoom zoom-item">
                <input type="checkbox" id="arriveToday" name="handle" value="arriveToday" v-model="ruleHandle">
                <label class="mb0" for="arriveToday">今日到达</label>
              </div>
            </div>
            <div class="btns">
              <div id="ruleSubmit" class="well-btn w-btn-primary" @click="ruleSubmit">确定</div>
              <div class="cancel-btn well-btn w-btn-dark w-line-btn like-primary" @click="cancelPopup">取消</div>
              <div id="sortReset" class="reset-btn well-btn w-btn-dark w-line-btn like-primary mr0" @click="ruleReset">重置</div>
            </div>
          </div>
        </div>
      </div>
      <div class="lxqp-loading" :class="loadingShow ? 'show' : 'hide'">
        <div class="well-loading">
          <div class="loader"></div>
          <div class="well-loading-msg">{{loadingMsg}}</div>
        </div>
      </div>
    </div>`;
      $('.original-content', _self.$element).hide();
      _self.$element.prepend(html);
    },
    createVue: function () {
      var _self = this;
      _self.VueObj = new Vue({
        el: '#lxqpWrap',
        data: {
          moveTop: true,
          lxqpShow: false,
          loadingShow: false,
          loadingMsg: '正在切换为连续签批模式......',
          leftControlShow: false,
          leftListLoading: false,
          filterOrRuleStatus: '',
          historyRuleData: null,
          curRule: null,
          loadingRules: {
            rule: 1
          },
          ruleCategorys: '',
          ruleDefinitions: '',
          ruleHandle: [],
          todoListTotal: 0,
          todoList: [],
          groupAndCountByTimingStateData: null,
          historyFilterData: null,
          filterFlowState: [0, 1, 3],
          timingStateNum: {},
          selectAllFlow: '',
          filterFlowGroups: [],
          checkFilterFlowGroups: [],
          filterFlowGroupsCheckAll: false,
          filterCriterions: [],
          filterPopupData: null,
          keyword: '',
          keywordHover: false,
          keywordClear: false,
          keywordCriterions: null,
          filterList: [],
          recordCurFlowDefinition: _self.recordCurFlowDefinition,
          prevStatus: false,
          nextStatus: true,
          iframe: null,
          waitList: [],
          showFilterData: false
        },
        watch: {
          'loadingRules.rule': function (newVal, oldVal) {
            var that = this;
            if (newVal === 3) {
              $('.precedence', _self.$leftControl).hide();
            } else {
              $('.precedence', _self.$leftControl).show();
            }
          },
          checkFilterFlowGroups() {
            if (this.checkFilterFlowGroups.length == this.filterFlowGroups.length) {
              //全选
              this.filterFlowGroupsCheckAll = true;
            } else {
              this.filterFlowGroupsCheckAll = false;
            }
          },
          keyword: function (newVal, oldVal) {
            var that = this;
            that.keyword = newVal;
            if (!newVal) {
              that.keywordCriterions = null;
              that.keywordClear = false;
              return;
            }
            that.keywordClear = true;
            that.keywordCriterions = {
              conditions: [
                {
                  columnIndex: 'title',
                  value: newVal,
                  type: 'like'
                },
                {
                  columnIndex: 'taskName',
                  value: newVal,
                  type: 'like'
                },
                {
                  columnIndex: 'flowStartUserId',
                  value: newVal,
                  type: 'like'
                },
                {
                  columnIndex: 'flowStartUserName',
                  value: newVal,
                  type: 'like'
                },
                {
                  columnIndex: 'flowName',
                  value: newVal,
                  type: 'like'
                }
              ],
              type: 'or'
            };
          }
        },
        created: function () {
          var that = this;
          that.getLoadingRules();
          that.setDataStoreParams();
        },
        mounted: function () {
          var that = this;
          var lxqpUrl = sessionStorage.lxqpUrl;
          var iframe = document.createElement('iframe');
          iframe.src = (lxqpUrl || location.href) + '&lxqpMode=true';
          iframe.id = 'lxqpFrame';
          iframe.className = 'lxqp-iframe';
          sessionStorage.lxqpUrl = location.href;
          if (iframe.attachEvent) {
            iframe.attachEvent('onload', function () {
              setTimeout(function () {
                that.loadingShow = false;
              }, 2000);
            });
          } else {
            iframe.onload = function () {
              setTimeout(function () {
                that.loadingShow = false;
              }, 2000);
            };
          }
          that.iframe = iframe;
          _self.$element.append(iframe);
          setTimeout(function () {
            that.lxqpShow = true;
          }, 0);
          that.loadingShow = true;
        },
        methods: {
          //上一条
          prev: function () {
            var that = this;
            if (!that.prevStatus) return false;
            var curIndex = that.getCurTodoItemIndex() - 1;
            that.nextRecord(that.todoList[curIndex], curIndex, 'prev');
          },
          //下一条
          next: function () {
            var that = this;
            if (!that.nextStatus) return false;
            var curIndex = that.getCurTodoItemIndex() + 1;
            var nextItem = that.todoList[curIndex];
            if (!nextItem) {
              nextItem = that.todoList[0];
              curIndex = 0;
            }
            that.nextRecord(nextItem, curIndex, 'next');
          },
          //稍候处理
          wait: function () {
            var that = this;
            var curIndex = that.getCurTodoItemIndex();
            var curData = that.todoList[curIndex];
            if (that.waitList.indexOf(curData) > -1) {
              that.waitList.splice(that.waitList.indexOf(curData), 1);
            }
            that.waitList.push(curData);
            that.todoDealLater(curData, curIndex + 1);
          },
          beforeExit: function () {
            var that = this;
            var needShowSavePopup = that.iframe.contentWindow.WorkView.needShowSavePopup();
            if (needShowSavePopup) {
              that.iframe.contentWindow.WorkView.saveDataPopup(function () {
                that.exit();
              });
            } else {
              that.exit();
            }
          },
          //退出流程签批
          exit: function () {
            var that = this;
            that.loadingMsg = '正在退出连续签批模式,请稍候......';
            that.loadingShow = true;
            that.lxqpShow = false;
            that.leftControlShow = false;
            var newUrl = that.getLXQPNewUrl();
            setTimeout(function () {
              sessionStorage.removeItem('lxqpUrl');
              location.href = newUrl;
            }, 1000);
          },
          //当前流程实例提到列表最前面
          moveToTop: function (item) {
            var that = this;
            if (that.moveTop) {
              that.moveTop = false;
              var _index = that.todoList.indexOf(item);
              that.todoList.splice(_index, 1);
              that.todoList.unshift(item);
            }
          },
          //将稍候处理的数据放到列表最后
          execWaitList: function () {
            var that = this;
            for (i = 0, len = that.waitList.length; i < len; i++) {
              for (j = 0, len2 = that.todoList.length; j < len2; j++) {
                if (that.todoList[j].uuid === that.waitList[i].uuid) {
                  var _index = that.todoList.indexOf(that.todoList[j]);
                  that.todoList.splice(_index, 1);
                  that.todoList.push(that.waitList[i]);
                  break;
                }
              }
            }
          },
          //切换左侧列表展开收起
          switchLeftStatus: function () {
            this.leftControlShow = !this.leftControlShow;
          },
          ruleHandleChange: function (type) {
            var that = this;
            if (that.ruleHandle.indexOf(type) < 0) {
              $('#' + type, _self.$leftControl)
                .val('')
                .wellSelect('val', '');
            }
          },
          //计算待办列表发起人/流程名称宽度
          computedItemWidth: function (type) {
            var that = this;
            var $el = type === 'todoList' ? _self.$todoList : _self.$FilterList;
            var list = type === 'todoList' ? that.todoList : that.filterList;
            if (!list.length) return;
            $('li', $el).each(function (index, item) {
              if (list[index].flowNameWidth) {
                return true;
              }
              var flowNameWidth = $(item).find('.flowName1').width() + 12;
              var flowStartUserNameWidth = $(item).find('.flowStartUserName1').width() + 1;
              if (flowNameWidth + flowStartUserNameWidth > 385) {
                if (flowStartUserNameWidth <= 126) {
                  flowNameWidth = 385 - flowStartUserNameWidth;
                } else if (flowNameWidth <= 259) {
                  flowStartUserNameWidth = 385 - flowNameWidth;
                } else {
                  flowNameWidth = 259;
                  flowStartUserNameWidth = 126;
                }
              }
              list[index].flowNameWidth = flowNameWidth;
              list[index].flowStartUserNameWidth = flowStartUserNameWidth;
            });
          },

          //点击待办列表数据
          clickTodoListItem: function (item, index, trigger) {
            var that = this;
            var curIndex = that.getCurTodoItemIndex();
            //点击当前数据无效
            if (index === curIndex) {
              that.getAllTodoListData();
              return false;
            }
            that.leftControlShow = false;
            var exist = that.checkFlowExist(item.uuid);
            if (!exist) {
              if (that.showFilterData) {
                top.appModal.info({
                  message: '工作不存在或者已被办理'
                });
                return;
              }
              var nextIndex = index + 1;
              var next = that.todoList[nextIndex];
              if (!next) {
                next = that.todoList[0];
                nextIndex = 0;
              }
              top.appModal.info({
                message: '工作不存在或者已被办理，将为您自动加载下一条待办',
                timer: 1000,
                callback: function () {
                  that.clickTodoListItem(next, nextIndex, true);
                }
              });
              return;
            }
            that.recordCurFlowDefinition = item;
            that.getAllTodoListData();
            that.turnNextRecord(item.uuid, item.flowInstUuid, item.title);
          },
          //点击搜索列表
          clickFilterListItem: function (item, index) {
            var that = this;
            var exist = that.checkFlowExist(item.uuid);
            if (!exist) {
              top.appModal.info({
                message: '工作不存在或者已被办理'
              });
              return;
            }
            that.recordCurFlowDefinition = item;
            that.turnNextRecord(item.uuid, item.flowInstUuid, item.title);
          },
          //获取当前流程实例的index
          getCurTodoItemIndex: function () {
            var that = this;
            var $cur = _self.$todoList.find('.active');
            return parseInt($cur.attr('index'));
          },
          //搜索
          keywordSearch: function () {
            var that = this;
            that.filterQuery();
          },
          //搜索框获得焦点时
          keywordFocus: function () {
            var that = this;
            that.keywordHover = true;
          },
          //搜索框失去焦点时
          keywordBlur: function () {
            var that = this;
            if (!that.keyword) {
              that.keywordHover = false;
            }
          },
          //清空搜索框
          clearKeyword: function () {
            var that = this;
            that.keyword = '';
            that.$refs.input.focus();
          },
          //切换筛选/规则弹窗状态
          switchFilterOrRuleStatus: function (type) {
            this.filterOrRuleStatus = this.filterOrRuleStatus === type ? '' : type;
            if (this.filterOrRuleStatus) {
              $('.lxqp-' + type)
                .addClass('active')
                .fadeIn('fast')
                .find('.zoom-item')
                .removeClass('zoom');
              $('.lxqp-' + type)
                .siblings('.lxqp-popup')
                .removeClass('active')
                .hide()
                .find('.zoom-item')
                .addClass('zoom');
              this.renderPopupData(type, false);
            } else {
              $('.lxqp-' + type)
                .removeClass('active')
                .hide()
                .find('.zoom-item')
                .addClass('zoom');
            }
          },
          //弹窗取消按钮
          cancelPopup: function () {
            var that = this;
            $('.lxqp-' + that.filterOrRuleStatus)
              .hide()
              .find('.zoom-item')
              .addClass('zoom');
            if (that.filterOrRuleStatus === 'filter') {
              that.filterFlowState = that.easyArrayDeepCopy(that.historyFilterData.filterFlowState);
              that.checkFilterFlowGroups = that.easyArrayDeepCopy(that.historyFilterData.checkFilterFlowGroups);
            } else {
              that.renderRules();
            }
            that.filterOrRuleStatus = '';
          },
          renderPopupData: function (type, submit) {
            if (type === 'filter') {
              this.renderFilterPopupData(submit);
            } else {
              this.renderRulePopupData(submit);
            }
          },
          easyArrayDeepCopy: function (source) {
            return JSON.parse(JSON.stringify(source));
          },
          //渲染筛选弹窗数据
          renderFilterPopupData: function (submit) {
            var that = this;
            if (that.historyFilterData) {
              that.filterFlowState = that.easyArrayDeepCopy(that.historyFilterData.filterFlowState);
              that.checkFilterFlowGroups = that.easyArrayDeepCopy(that.historyFilterData.checkFilterFlowGroups);
            } else {
              that.historyFilterData = {
                filterFlowState: that.easyArrayDeepCopy(that.filterFlowState),
                checkFilterFlowGroups: that.easyArrayDeepCopy(that.checkFilterFlowGroups)
              };
            }
          },
          //渲染规则弹窗数据
          renderRulePopupData: function () {
            var that = this;
            that.renderRules();
          },
          //筛选弹窗 切换流程状态按钮
          changeFilterFlowState: function (type) {
            var that = this;
            var _index = that.filterFlowState.indexOf(type);
            if (_index > -1) {
              that.filterFlowState.splice(_index, 1);
            } else {
              that.filterFlowState.push(type);
            }
          },
          checkFilterFlowState: function (type) {
            return this.filterFlowState.indexOf(type) > -1 ? 'active' : '';
          },
          //筛选提交
          filterSubmit: function (show) {
            var that = this;
            that.historyFilterData = {
              filterFlowState: that.easyArrayDeepCopy(that.filterFlowState),
              checkFilterFlowGroups: that.easyArrayDeepCopy(that.checkFilterFlowGroups)
            };
            that.filterResult(that.filterFlowState, that.checkFilterFlowGroups, show);
          },
          //筛选重置
          filterReset: function (unSubmit, show) {
            var that = this;
            that.filterFlowState = [0, 1, 3];
            that.checkFilterFlowGroups = that.filterFlowGroups.map(function (item) {
              return item.flowDefId;
            });
            // if (unSubmit) {
            //   return true;
            // }
            that.filterSubmit(true);
          },
          //筛选结果
          filterResult: function (timingState, flowGroup, show) {
            var that = this;
            that.filterCriterions = [];
            if (timingState.length) {
              that.filterCriterions.push({
                columnIndex: 'timingState',
                value: timingState,
                type: 'in'
              });
            }
            if (flowGroup.length) {
              that.filterCriterions.push({
                columnIndex: 'flowDefId',
                value: flowGroup,
                type: 'in'
              });
            }
            that.filterQuery();
            if (!show) {
              that.filterOrRuleStatus = '';
              _self.$leftControl.find('.lxqp-filter').hide().find('.zoom-item').addClass('zoom');
            }
          },
          //筛选查询
          filterQuery: function () {
            var that = this;
            that.loadingRules.mode = 2;
            var data = $.extend({}, that.getTodoListParam());
            if (that.filterCriterions) {
              data.criterions = data.criterions.concat(that.filterCriterions);
            }
            if (that.keywordCriterions) {
              data.params.keyword = that.keyword;
              data.criterions.push(that.keywordCriterions);
            }
            data.pagingInfo.pageSize = that.todoListTotal + 10;
            if (data.criterions) {
              that.showFilterData = true;
            } else {
              that.showFilterData = false;
            }
            if (data.loadingRules.rule === 3) {
              data.orders = [
                {
                  sortName: 'timingState',
                  sortOrder: 'desc'
                }
              ];
            }
            $.ajax({
              dataType: 'json',
              type: 'POST',
              contentType: 'application/json',
              url: '/api/workflow/work/todo/query',
              data: JSON.stringify(data),
              success: function (res) {
                that.filterList = res.data.data;
                that.computedItemWidth('filterList');
              },
              error: function (err) {
                console.error(err);
              }
            });
          },
          //规则提交
          ruleSubmit: function () {
            this.saveLoadingRules();
          },
          //规则重置
          ruleReset: function () {
            var that = this;
            that.saveLoadingRules(true);
          },
          //设置顶部按钮状态
          setTopBtnStatus: function () {
            var that = this;
            var curIndex = that.getCurTodoItemIndex();
            if (curIndex !== 0 && !curIndex) {
              that.nextRecord(that.todoList[0], 0, 'next');
              return;
            }
            if (that.todoListTotal === 1) {
              that.prevStatus = false;
              that.nextStatus = false;
              that.waitStatus = false;
              return;
            }
            that.nextStatus = true;
            that.waitStatus = true;
            if (curIndex === 0) {
              that.prevStatus = false;
            } else {
              that.prevStatus = true;
            }
          },
          //稍候处理
          todoDealLater: function (item, nextIndex) {
            var that = this;
            if (!that.todoList[nextIndex]) {
              top.appModal.warning('当前为最后一条流程！');
              return;
            }
            $.ajax({
              dataType: 'json',
              type: 'POST',
              contentType: 'application/x-www-form-urlencoded',
              url: '/api/workflow/work/todo/dealLater',
              data: {
                taskInstUuid: item.uuid
              },
              success: function (res) {
                that.nextRecord(that.todoList[nextIndex], nextIndex, 'wait');
              }
            });
          },
          //获取列表所有数据
          getAllTodoListData: function (moveTop) {
            var that = this;
            that.loadingRules.mode = 2;
            var data = $.extend({}, that.getTodoListParam());
            data.pagingInfo.pageSize = '';
            if (data.loadingRules.rule === 3) {
              data.orders = [
                {
                  sortName: 'timingState',
                  sortOrder: 'desc'
                }
              ];
            }
            if (moveTop) {
              that.leftListLoading = true;
            }
            $.ajax({
              dataType: 'json',
              type: 'post',
              contentType: 'application/json',
              url: '/api/workflow/work/todo/query',
              data: JSON.stringify(data),
              success: function (result) {
                that.showFilterData = false;
                that.todoList = result.data.data;
                if (moveTop) {
                  that.moveTop = true;
                }
                that.todoListTotal = result.data.pagination.totalCount;
                that.groupAndCountByTimingState();
                that.groupAndCountByFlowDefId();
                if (!that.todoListTotal) {
                  var $dialog = appModal.dialog({
                    title: '温馨提示',
                    message: '全部流程已处理，即将退出连续签批模式！',
                    height: 200,
                    width: 340,
                    dialogPosition: 'center',
                    shown: function () {
                      $('.close', $dialog).on('click', function () {
                        top.appContext.getWindowManager().closeAndRefreshParent();
                      });
                      $('.modal-body', $dialog).css({
                        overflow: 'hidden',
                        'min-height': '96px'
                      });
                      $('.bootbox-body', $dialog).css({
                        overflow: 'hidden',
                        textAlign: 'center',
                        lineHeight: '64px',
                        minHeight: '64px',
                        height: '64px'
                      });
                    },
                    buttons: {
                      ok: {
                        label: '好的',
                        className: 'well-btn w-btn-primary w-line-btn',
                        callback: function () {
                          top.appContext.getWindowManager().closeAndRefreshParent();
                        }
                      }
                    }
                  });
                  return;
                }
                that.$nextTick(function () {
                  that.setTopBtnStatus();
                  that.leftListLoading = false;
                  that.computedItemWidth('todoList');
                  if (that.loadingRules.rule !== 3) {
                    that.execWaitList();
                  } else {
                    that.waitList = [];
                  }
                });
              }
            });
          },
          //查询总数
          queryTotal: function () {
            var that = this;
            that.loadingRules.mode = 2;
            var data = $.extend({}, that.getTodoListParam());
            var total;
            $.ajax({
              dataType: 'json',
              type: 'post',
              async: false,
              contentType: 'application/json',
              url: '/api/workflow/work/todo/count',
              data: JSON.stringify(data),
              success: function (result) {
                total = result.data;
              }
            });
            return total;
          },
          groupAndCountByTimingState: function () {
            var that = this;
            var data = that.getTodoListParam();
            $.ajax({
              dataType: 'json',
              type: 'POST',
              contentType: 'application/json',
              url: '/api/workflow/work/todo/groupAndCountByTimingState',
              data: JSON.stringify(data),
              success: function (res) {
                that.renderTimingStateCount(res.data.data);
              }
            });
          },
          renderTimingStateCount: function (data) {
            var that = this;
            // 0正常、1预警、2到期、3逾期
            var timingState = {};
            $.each(data, function (i, item) {
              timingState[item.timingState] = item.count;
            });
            that.timingStateNum = $.extend(
              {
                0: 0,
                1: 0,
                3: 0
              },
              timingState
            );
          },
          groupAndCountByFlowDefId: function () {
            var that = this;
            var data = that.getTodoListParam();
            $.ajax({
              dataType: 'json',
              type: 'POST',
              contentType: 'application/json',
              url: '/api/workflow/work/todo/groupAndCountByFlowDefId',
              data: JSON.stringify(data),
              success: function (res) {
                that.renderFlowDefGroupAndCount(res.data.data);
              }
            });
          },
          renderFlowDefGroupAndCount: function (data) {
            var that = this;
            that.filterFlowGroups = data;
            that.checkFilterFlowGroups = this.filterFlowGroups.map(function (item) {
              return item.flowDefId;
            });
          },
          changeAllChecked: function () {
            if (this.filterFlowGroupsCheckAll) {
              this.checkFilterFlowGroups = this.filterFlowGroups.map(function (item) {
                return item.flowDefId;
              });
            } else {
              this.checkFilterFlowGroups = [];
            }
          },
          getTodoListParam: function () {
            var that = this;
            var todoListParam = $.extend(
              {},
              {
                loadingRules: that.loadingRules
              },
              that.dataStoreParams
            );
            return _.cloneDeep(todoListParam);
          },
          hidePopup: function (type) {
            $('.lxqp-' + type, _self.$leftControl)
              .removeClass('active')
              .hide()
              .find('.zoom-item')
              .addClass('zoom');
          },
          //保存加载规则
          saveLoadingRules: function (isReset) {
            var that = this;
            var flowCategoryUuids = $('#categoryQuery', _self.$leftControl).val();
            var flowDefIds = $('#definitionQuery', _self.$leftControl).val();
            var data = {
              mode: 2, // 加载模式，1待办视图列表、2连续签批模式
              rule: that.curRule,
              // 流程分类UUID列表
              flowCategoryUuids: flowCategoryUuids ? flowCategoryUuids.split(';') : [],
              //流程定义ID数组
              flowDefIds: flowDefIds ? flowDefIds.split(';') : [],
              // 今日到达
              arriveToday: that.ruleHandle.indexOf('arriveToday') > -1 ? true : false
            };
            var errorTip = [];
            that.ruleHandle.forEach(function (item) {
              switch (item) {
                case 'categoryQuery':
                  if (!data.flowCategoryUuids.length) {
                    errorTip.push('流程分类');
                  }
                  break;
                case 'definitionQuery':
                  if (!data.flowDefIds.length) {
                    errorTip.push('流程');
                  }
                  break;
              }
            });
            if (errorTip.length && !isReset) {
              appModal.error('请选择' + errorTip.join('、'));
              return false;
            }
            that.loadingRules = data;
            if (isReset) {
              that.loadingRules = {
                flowCategoryUuids: [],
                flowDefIds: [],
                arriveToday: false,
                rule: 1,
                mode: 2
              };
              that.curRule = 1;
              that.ruleHandle = [];
              $('#categoryQuery', _self.$leftControl).wellSelect('val', '');
              $('#definitionQuery', _self.$leftControl).wellSelect('val', '');
            }
            $.ajax({
              dataType: 'json',
              type: 'POST',
              contentType: 'application/json',
              url: '/api/workflow/user/preferences/saveLoadingRules',
              data: JSON.stringify(that.loadingRules),
              success: function (result) {
                that.clearFilter();
                that.recordCurFlow();
                that.getAllTodoListData(true);
                if (!isReset) {
                  that.hidePopup(that.filterOrRuleStatus);
                  that.filterOrRuleStatus = '';
                }
              }
            });
          },
          //清空查询条件
          clearFilter: function () {
            var that = this;
            that.keyword = '';
            that.keywordCriterions = null;
            that.filterCriterions = null;
          },
          //记录当前流程
          recordCurFlow: function () {
            var that = this;
            that.recordCurFlowDefinition = that.todoList[that.getCurTodoItemIndex()];
          },
          definitionQuery: function () {
            var that = this;
            $.ajax({
              dataType: 'json',
              type: 'get',
              contentType: 'application/json',
              url: '/api/workflow/definition/query',
              data: {
                keyword: '',
                uuids: [],
                pageNo: 1,
                pageSize: 65535
              },
              success: function (result) {
                var res = $.map(result.data, function (item) {
                  return {
                    id: item.id,
                    text: item.name,
                    data: item
                  };
                });
                $('#definitionQuery', _self.$leftControl).wellSelect({
                  data: res,
                  multiple: true,
                  chooseAll: true
                });
              }
            });
          },
          categoryQuery: function () {
            var that = this;
            $.ajax({
              dataType: 'json',
              type: 'get',
              contentType: 'application/json',
              url: '/api/workflow/category/query',
              data: {
                keyword: '',
                uuids: [],
                pageNo: 1,
                pageSize: 65535
              },
              success: function (result) {
                var res = $.map(result.data, function (item) {
                  return {
                    id: item.uuid,
                    text: item.name,
                    data: item
                  };
                });
                $('#categoryQuery', _self.$leftControl).wellSelect({
                  data: res,
                  multiple: true,
                  chooseAll: true
                });
              }
            });
          },
          setDataStoreParams: function () {
            var that = this;
            var _requestCode = Browser.getQueryString('_requestCode');
            if (!sessionStorage['lxqp_dataStoreParams']) {
              sessionStorage['lxqp_dataStoreParams'] = sessionStorage[_requestCode + '_dataStoreParams'];
            }
            var dataStoreParams =
              sessionStorage[_requestCode + '_dataStoreParams'] || sessionStorage[window.name] || sessionStorage['lxqp_dataStoreParams'];
            that.dataStoreParams = JSON.parse(dataStoreParams);
            that.dataStoreParams.params = {};
            var criterions = $.map(that.dataStoreParams.criterions, function (item) {
              if (item.sql) {
                return item;
              }
            });
            that.dataStoreParams.criterions = criterions;
            that.dataStoreParams.pagingInfo.currentPage = 1;
            that.dataStoreParams.pagingInfo.pageSize = 10;
          },
          getLoadingRules: function () {
            var that = this;
            $.ajax({
              dataType: 'json',
              type: 'GET',
              contentType: 'json',
              url: '/api/workflow/user/preferences/getLoadingRules',
              data: {},
              success: function (res) {
                that.curRule = res.data.rule;
                that.historyRuleData = $.extend({}, res.data);
                that.loadingRules = $.extend({}, res.data);
                that.renderRules();
                that.categoryQuery();
                that.definitionQuery();
                that.getAllTodoListData();
              }
            });
          },
          renderRules: function () {
            var that = this;
            that.curRule = that.loadingRules.rule;
            var $categoryQuery = $('#categoryQuery', _self.$leftControl);
            var $definitionQuery = $('#definitionQuery', _self.$leftControl);
            that.ruleHandle = [];
            if (that.loadingRules.flowCategoryUuids.length) {
              that.ruleHandle.push('categoryQuery');
              var flowCategoryUuids = that.loadingRules.flowCategoryUuids.join(';');
              if ($categoryQuery.data('wellSelect')) {
                $categoryQuery.wellSelect('val', flowCategoryUuids);
              } else {
                $categoryQuery.val(flowCategoryUuids);
              }
            } else {
              if ($categoryQuery.data('wellSelect')) {
                $categoryQuery.wellSelect('val', '');
              } else {
                $categoryQuery.val('');
              }
            }
            if (that.loadingRules.flowDefIds.length) {
              that.ruleHandle.push('definitionQuery');
              $('#definitionQuery', _self.$leftControl).val(that.loadingRules.flowDefIds.join(';'));
              var flowDefIds = that.loadingRules.flowDefIds.join(';');
              if ($definitionQuery.data('wellSelect')) {
                $definitionQuery.wellSelect('val', flowDefIds);
              } else {
                $definitionQuery.val(flowDefIds);
              }
            } else {
              if ($definitionQuery.data('wellSelect')) {
                $definitionQuery.wellSelect('val', '');
              } else {
                $definitionQuery.val('');
              }
            }
            if (that.loadingRules.arriveToday) {
              that.ruleHandle.push('arriveToday');
            }
          },
          nextRecord: function (item, index, type) {
            var that = this;
            var curIndex = that.getCurTodoItemIndex();
            var exist = that.checkFlowExist(item.uuid);
            if (index === curIndex) {
              if (exist) {
                that.getAllTodoListData();
                return false;
              }
            }
            if (type === 'click') {
              that.leftControlShow = false;
            }
            if (!exist) {
              var total = that.queryTotal();
              if (!total) {
                that.getAllTodoListData();
                return;
              }
              var nextIndex = type === 'prev' ? index - 1 : index + 1;
              var next = that.todoList[nextIndex];
              if (!next) {
                next = that.todoList[0];
                nextIndex = 0;
              }
              if (that.leftControlShow) {
                top.appModal.info({
                  message: '工作不存在或者已被办理，将为您自动加载下一条待办',
                  timer: 1000,
                  callback: function () {
                    that.nextRecord(next, nextIndex, type);
                  }
                });
              } else {
                that.nextRecord(next, nextIndex, type);
              }
              return;
            }
            that.loadingShow = true;
            that.loadingMsg = !type
              ? '正在加载数据，请稍候......'
              : type === 'prev'
              ? '正在加载上一条数据，请稍候......'
              : '正在加载下一条数据，请稍候......';
            that.recordCurFlowDefinition = item;
            that.turnNextRecord(item.uuid, item.flowInstUuid, item.title);
            if (that.showFilterData) return;
            that.getAllTodoListData();
          },
          turnNextRecord: function (taskInstUuid, flowInstUuid, title, type) {
            var that = this;
            var newUrl = Browser.replaceParamVal([
              {
                key: 'taskInstUuid',
                value: taskInstUuid
              },
              {
                key: 'flowInstUuid',
                value: flowInstUuid
              }
            ]);
            newUrl = newUrl.replace(/sameUserSubmitTaskOperationUuid=([^&]*)/gi, '');
            newUrl = newUrl.replace('&auto_submit=true', '');

            that.iframe.src = newUrl + '&lxqpMode=true';
            that.curFlowUuid = taskInstUuid;
            that.setLXQPNewUrl(newUrl);
            top.document.title = title;
            top.name = '/workflow/work/v53/view/todo?taskInstUuid=' + taskInstUuid + '&flowInstUuid=' + flowInstUuid;
            top.appContext.getWindowManager().refreshParent();
            setTimeout(function () {
              that.loadingShow = false;
            }, 2000);
          },
          //检查流程实例是否存在
          checkFlowExist: function (uuid) {
            var _self = this;
            _self.loadingRules.mode = 2;
            var data = $.extend({}, _self.getTodoListParam());
            if (!uuid) {
              uuid = _self.todoList[_self.getCurTodoItemIndex()].uuid;
            }
            data.criterions.push({
              columnIndex: 'uuid',
              value: uuid,
              type: 'like'
            });
            var exist;
            $.ajax({
              dataType: 'json',
              type: 'post',
              async: false,
              contentType: 'application/json',
              url: '/api/workflow/work/todo/query',
              data: JSON.stringify(data),
              success: function (result) {
                exist = !!result.data.data.length;
              }
            });
            return exist;
          },
          refreshSameFlow: function (url) {
            var that = this;
            that.loadingMsg = '正在加载数据，请稍候......';
            that.loadingShow = true;
            that.iframe.src = url;
            that.setLXQPNewUrl(url);
            setTimeout(function () {
              that.loadingShow = false;
            }, 2000);
            top.appContext.getWindowManager().refreshParent();
          },
          setLXQPNewUrl: function (url) {
            var that = this;
            sessionStorage.lxqpUrl = url;
            window.history.replaceState(null, null, url);
          },
          getLXQPNewUrl: function () {
            return sessionStorage.lxqpUrl;
          },
          refreshTodoList: function () {
            var that = this;
            var curIndex = that.getCurTodoItemIndex();
            var curItem = that.todoList[curIndex];
            that.nextRecord(curItem, curIndex, 'next');
          },
          //获取当前列表存在的下一条待办数据
          getExistNextRecord: function (item) {
            debugger;
            var that = this;
            var curIndex = that.getCurTodoItemIndex();
            var curItem = item || that.todoList[curIndex];
            var exist = that.checkFlowExist(curItem.uuid);
            if (exist) {
              that.recordCurFlowDefinition = curItem;
              return;
            } else {
              var nextIndex = curIndex + 1;
              if (!that.todoList[nextIndex]) {
                nextIndex = 0;
              }
              var nextItem = that.todoList[nextIndex];
              if (nextItem.uuid !== curItem.uuid) {
                that.getExistNextRecord(nextItem);
              }
            }
          }
        }
      });
      _self.$lxqpWrap = _self.$element.find('.lxqp-wrap');
      _self.$topControl = _self.$lxqpWrap.find('.top-control');
      _self.$leftControl = _self.$lxqpWrap.find('.left-control');
      _self.$leftIcon = _self.$leftControl.find('.icon');
      _self.$todoListWrap = _self.$leftControl.find('.lxqp-left-list');
      _self.$todoList = _self.$leftControl.find('.lxqp-left-list .todo-list');
      _self.$FilterList = _self.$leftControl.find('.lxqp-left-list .filter-list');
      _self.$loading = _self.$lxqpWrap.find('.lxqp-loading');
      _self.$leftLoading = _self.$leftControl.find('.well-loading-wrap');
    },
    bindEvent: function () {
      var _self = this;
      $(document).on('click', function (e) {
        var $target = $(e.target);
        if (!$target.closest('.popup-item.active').length && !$target.closest('.lxqp-popup').length) {
          var _id = $('.popup-item.active').data('id');
          $('.popup-item.active').removeClass('active');
          $('.lxqp-' + _id)
            .hide()
            .find('.zoom-item')
            .addClass('zoom');
        }
      });
      $(window).on('resize', function () {
        _self.setTodoListScroll();
      });
    },
    setTodoListScroll: function () {
      var _self = this;
      _self.$todoListWrap.slimScroll({
        width: '100%',
        color: '#000',
        opacity: 0.15,
        height: '100%',
        wheelStep: navigator.userAgent.indexOf('Firefox') > -1 ? 1 : 10
      });
    },
    //调用父窗口WorkFlowLXQP
    parentNextRecord: function (tag) {
      var _self = top.WorkFlowLXQP;
      var curIndex = _self.VueObj.getCurTodoItemIndex();
      var nextIndex = curIndex + 1;
      var nextItem = _self.VueObj.todoList[nextIndex];
      if (tag && !nextItem) {
        _self.VueObj.getAllTodoListData();
      } else {
        _self.VueObj.nextRecord(nextItem, nextIndex, 'next');
      }
    },
    refreshSameFlow: function (url) {
      var _self = top.WorkFlowLXQP;
      _self.VueObj.refreshSameFlow(url);
    }
  });
  return WorkFlowLXQP;
});
