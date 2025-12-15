<template>
  <view :style="theme" class="org-unit-dialog">
    <uni-nav-bar
      class="custom-uni-nav-bar"
      :statusBar="true"
      :fixed="true"
      :left-icon="'left'"
      :title="'组织选择'"
      @clickLeft="goBack"
    >
      <block slot="right">
        <view class="nav-bar-right" @tap="showSelectedPopup">
          <text>已选</text><text class="nav-bar-text-count">({{ value && value.length }})</text>
        </view>
      </block>
    </uni-nav-bar>
    <scroll-view class="scroll-h" :scroll-x="true" :show-scrollbar="false" :scroll-into-view="scrollIntoView">
      <view
        v-for="(tab, index) in tabs"
        :key="tab.id"
        class="uni-tab-item"
        :id="tab.id"
        :data-current="index"
        @click="onTabTap"
      >
        <text class="uni-tab-item-title" :class="tabIndex == index ? 'uni-tab-item-title-active' : ''">{{
          tab.name
        }}</text>
      </view>
    </scroll-view>
    <view class="line-h"></view>
    <swiper :current="tabIndex" class="swiper-box org-box" :style="swiperStyle" :duration="300" @change="onTabChange">
      <swiper-item class="swiper-item" v-for="(tab, index1) in tabs" :key="index1">
        <view class="container">
          <!-- 查询区域 -->
          <view v-if="!isMoreOptList" class="uni-flex uni-row">
            <view style="flex: 1; margin-right: 15px">
              <uni-search-bar
                class="keyword-query-input"
                radius="5"
                placeholder="搜索"
                clearButton="auto"
                cancelButton="auto"
                @confirm="keywordQuery"
                @focus="keywordQueryFocus"
                @cancel="cancelKeywordQuery"
              />
            </view>
            <view v-if="!doKeywordQuery" class="uni-flex uni-row list-header-buttons">
              <button v-if="showHideJobButton" class="mini-btn" type="default" @click="onHideJobClick">隐藏职位</button>
              <button v-if="showShowJobButton" class="mini-btn" type="default" @click="onShowJobClick">显示职位</button>
              <button v-if="showUserListButton" class="mini-btn" type="default" @click="onUserListClick">
                人员列表
              </button>
            </view>
          </view>
          <!-- 组织版本导航 -->
          <!-- <view class="uni-flex uni-row"> -->
          <scroll-view :scroll-left="navScrollLeft" class="scroll-h org-nav-container" scroll-x="true">
            <!-- 组织导航切换 -->
            <view v-if="navData[tab.id] != null && navData[tab.id].length == 1" class="uni-flex uni-row nav-container">
              <view v-for="(nav, navIndex) in navData[tab.id]" :key="navIndex" class="uni-flex uni-row">
                <view class="nav-text">{{ nav.name }}</view>
                <view v-if="nav.switchVersion" class="nav-text">
                  <uni-icons type="tune-filled" @click="showSwitchOrgVersion($event, navData[tab.id])"></uni-icons>
                </view>
              </view>
            </view>
            <view
              v-else-if="navData[tab.id] != null && navData[tab.id].length > 1"
              class="uni-flex uni-row nav-container"
            >
              <view v-for="(nav, navIndex) in navData[tab.id]" :key="navIndex">
                <view v-if="navIndex >= 0 && navIndex < navData[tab.id].length - 1">
                  <view class="uni-link uni-center uni-common-mt nav-text" @tap="onNavClick($event, nav)"
                    >{{ nav.name }} ></view
                  >
                </view>
                <view v-else-if="navIndex == navData[tab.id].length - 1">
                  <view class="uni-center uni-common-mt nav-text">{{ nav.name }}</view>
                </view>
              </view>
            </view>
          </scroll-view>
          <!-- </view> -->
          <!-- 组织数据列表 -->
          <scroll-view :style="listScrollViewStyle" scroll-y="true">
            <uni-list>
              <uni-list-item
                class="org-list-item"
                v-for="(item, dataIndex) in listData[tab.id]"
                :key="dataIndex"
                :showArrow="item.isNav"
                clickable
                @click="onItemClick($event, item)"
              >
                <template slot="body">
                  <view class="org-list-item-body">
                    <view style="flex: 1">
                      <view v-if="item.isNav"> </view>
                      <text> {{ item.name }}{{ item.parentJobName != null ? "——" + item.parentJobName : "" }} </text>
                    </view>
                    <view class="popup-check-icon" v-if="item.checked">
                      <uni-icons type="checkmarkempty" :size="22" color="#2979ff" />
                    </view>
                  </view>
                </template>
              </uni-list-item>
            </uni-list>
            <!-- 底部操作按钮高度填充 -->
            <view style="height: 50px"></view>
            <!-- <view class="uni-list uni-list-container">
                    <view v-for="(item, dataIndex) in listData[tab.id]" :key="dataIndex" class="uni-flex uni-row uni-list-item">
                      <view v-if="item.checkable" style="margin-right: 20rpx;">
                        <uni-icons :type="item.checked ? 'checkbox-filled' : 'circle'" :color="item.checked ? '#007aff' : '#C0C0C0'" size="24" />
                      </view>
                      <text class="uni-indexed-list__item-content">{{ item.name }}</text> -->
            <!-- <view>
                        <checkbox :value="item.value" :checked="item.checked" />
                      </view>
                      <view class="uni-flex uni-row">
                        {{item.name}}{{item.parentJobName != null ? "——" + item.parentJobName : ""}}
                      </view> -->
            <!-- </view>
                 </view> -->
          </scroll-view>
        </view>
      </swiper-item>
    </swiper>
    <!-- 组织版本切换弹窗内容 -->
    <uni-popup ref="popup">
      <view class="popup-org-dialog">
        <view class="popup-title">
          <text class="popup-title-text">组织版本切换</text>
        </view>
        <view class="popup-content">
          <scroll-view class="popup-content-scroll-view" scroll-y="true">
            <view class="uni-list">
              <radio-group @change="onOrgVersionRadioChange">
                <label class="uni-list-cell uni-list-cell-pd" v-for="(item, index) in orgVersions" :key="index">
                  <view style="display: none">
                    <radio :value="item.id" :checked="item.checked" />
                  </view>
                  <view class="popup-check-item">
                    <view style="flex: 1">{{ item.name }}</view>
                    <view class="popup-check-icon" v-if="item.checked">
                      <uni-icons type="checkmarkempty" color="#2979ff" />
                    </view>
                  </view>
                </label>
              </radio-group>
            </view>
          </scroll-view>
        </view>
        <view class="popup-button-box">
          <button class="popup-button" @tap="onOrgVersionOk">确定</button>
        </view>
      </view>
    </uni-popup>
    <!-- 查看已选 -->
    <uni-popup ref="selectedPopup">
      <view class="popup-org-dialog">
        <view class="popup-title">
          <text class="popup-title-text">组织选择</text>
        </view>
        <view class="popup-content">
          <scroll-view class="popup-content-scroll-view" scroll-y="true">
            <uni-list>
              <uni-list-item class="item-selected" v-for="(item, index) in value" :key="index" :title="item.name">
                <template slot="footer">
                  <view class="uni-flex" style="justify-content: center; align-items: center">
                    <uni-icons
                      class="icon"
                      type="close"
                      size="24"
                      @click="onRemoveSelectedClick($event, item)"
                    ></uni-icons>
                  </view>
                </template>
              </uni-list-item>
            </uni-list>
          </scroll-view>
        </view>
      </view>
    </uni-popup>
    <view class="org-dialog-button-group">
      <view class="org-dialog-button" @tap="onCancel">
        <text class="org-dialog-button-text">取消</text>
      </view>
      <view class="org-dialog-button uni-border-left" @tap="onOk">
        <text class="org-dialog-button-text org-button-color">确定</text>
      </view>
    </view>
  </view>
</template>
<script>
var UNIT_TREE_TYPE = [
  "MYUNIT",
  "MYDEPT",
  "MYLEADER",
  "MYUNDERLING",
  "MYCOMPANY",
  "DUTYGROUP",
  "PUBLICGROUP",
  "MYGROUP",
];
// var UNIT_TREE_TYPE_NAME = ['我的单位', '我的部门', '我的领导', '我的下属', '我的集团', '职务群组', '公共群组', '个人群组'];
import { isEmpty, isArray, each, trim, indexOf } from "lodash";
import { v4 as uuidv4 } from "uuid";
export default {
  components: {},
  data() {
    return {
      swiperStyle: {
        flex: 1,
        height: "600px",
      },
      listScrollViewStyle: {
        height: "450px",
      },
      options: {},
      orgOptionMap: {},
      orgOptionList: [],
      orgOptionTypeData: {},
      orgTypes: {},
      visitChain: {},
      orgVersions: [],
      navData: {},
      listData: {},
      value: [],
      doKeywordQuery: false,
      showHideJobButton: true,
      showShowJobButton: true,
      showUserListButton: true,
      tabs: [],
      scrollIntoView: "",
      tabIndex: 0,
      navScrollLeft: 0,
    };
  },
  onLoad: function () {
    var params = this.getPageParameter("unitParams");
    // console.log("params:", params)
    this.options = params;
    this.init();
  },
  mounted() {
    // 更新窗口高度
    uni.getSystemInfo({
      success: (result) => {
        var windowHeight = result.windowHeight;
        const query = uni.createSelectorQuery().in(this);
        query
          .select(".line-h")
          .boundingClientRect((data) => {
            this.swiperStyle.height = windowHeight - data.bottom + "px";
          })
          .exec();
      },
    });
  },
  methods: {
    goBack() {
      uni.navigateBack({
        delta: 1,
      });
    },
    init: function () {
      var _self = this;
      var options = _self.options;
      // 标题
      if (options.title) {
        uni.setNavigationBarTitle({ title: options.title });
      }
      // 初始化值
      _self.initValue();
      // 将type转成数组格式
      _self.loadOrgOptionList(function (orgOptionList) {
        if ("all" == options.type.toLowerCase()) {
          // 显示全部
          if (orgOptionList) {
            options.types = [];
            each(orgOptionList, function (opt) {
              if (opt.id === "Role" && self.options.showRole) {
                options.types.push(opt.id);
              } else if (opt.isShow) {
                options.types.push(opt.id);
              }
            });
          } else {
            options.types = UNIT_TREE_TYPE;
          }
        } else {
          options.types = options.type.split(";");
        }
        if (isArray(options.moreOptList) && options.moreOptList.length > 0) {
          each(options.moreOptList, function (item) {
            item.isEnable = 1;
            item.isShow = 1;
            options.type = item.id;
            _self.orgOptionTypeData[options.type] = item.treeData;
            options.types.push(item.id);
            orgOptionList.push(item);
          });
          options.showType = false;
        }
        var tabs = [];
        each(orgOptionList, function (orgOption) {
          if (orgOption.isEnable == 1 && orgOption.isShow == 1) {
            tabs.push(orgOption);
          }
        });
        _self.orgOptionList = tabs;
        _self.tabs = tabs;
        // 设置导航、列表数据属性
        each(orgOptionList, function (orgOption) {
          _self.$set(_self.navData, orgOption.id, []);
          _self.$set(_self.listData, orgOption.id, []);
        });
        // 加载数据
        _self.loadData(_self.tabIndex);
      });
    },
    // 初始化值
    initValue: function () {
      var _self = this;
      var options = _self.options;
      var initValues = options.initValues || [];
      var initLabels = options.initLabels || [];
      var initDatas = [];
      if (initValues && initLabels) {
        var values = initValues;
        var labels = initLabels;
        for (var i = 0; i < values.length; i++) {
          //检查是否标准格式
          if (values[i].substr(0, 1) == "V") {
            var idAndVersion = values[i].split("/");
            var versionId = idAndVersion[0];
            var id = idAndVersion[1];
            var name = labels[i];
            var node = {
              id: id,
              type: id.substr(0, 1),
              name: name,
              orgVersionId: versionId,
            };
            initDatas.push(node);
          } else {
            //非标准格式，则采用原值，那只有ID,NAME, 主要是用于邮件功能
            let node = {
              id: values[i],
              name: labels[i],
            };
            // 如果是个合法的组织ID,则计算type
            if (_self.isValidOrgId(values[i])) {
              node.type = values[i].substr(0, 1);
            }
            initDatas.push(node);
          }
        }
      }
      _self.value = initDatas;
      // 初始值计算回调
      if (options.computeInitData) {
        options.computeInitData.call(options, _self.value);
      }
      _self.updateSelectedText();
    },
    isValidOrgId: function (orgId) {
      if (!isEmpty(orgId)) {
        var type = orgId.substr(0, 1);
        if ("U" == type) {
          //用户
          return true;
        } else if ("O" == type) {
          //组织节点
          return true;
        } else if ("B" == type) {
          //业务单位
          return true;
        } else if ("D" == type) {
          //部门
          return true;
        } else if ("J" == type) {
          //职位
          return true;
        } else if ("DU" == type) {
          //职务
          return true;
        } else if ("G" == type) {
          //群组
          return true;
        }
      }
      return false;
    },
    // 加载组织选择项
    loadOrgOptionList: function (callback, unitId) {
      var _self = this;
      var options = _self.options;
      var orgOptionList = _self.orgOptionMap[unitId];
      if (isArray(options.moreOptList) && options.moreOptList.length) {
        callback.call(this, []);
      } else if (orgOptionList == null || typeof orgOptionList === "undefined") {
        uni.request({
          service: "multiOrgService.getOrgOptionListByUnitId",
          data: [unitId, false],
          success: function (result) {
            orgOptionList = _self.orgOptionMap[unitId] = result.data.data;
            if (!options.showRole) {
              orgOptionList = orgOptionList.filter(function (currentValue) {
                return !(currentValue.id === "Role");
              });
            }
            callback.call(this, orgOptionList);
          },
        });
      }
    },
    // 加载数据
    loadData: function (tabIndex, callback) {
      var _self = this;
      var options = _self.options;
      var orgOption = _self.orgOptionList[tabIndex];
      var type = orgOption.id;
      var resultData = _self.orgOptionTypeData[type];
      if (resultData == null || typeof resultData === "undefined") {
        // 默认不要用户数据
        var isNeedUser = "0";
        if (indexOf(options.selectTypes, "U") >= 0) {
          isNeedUser = "1";
        }
        var params = {
          isNeedUser: isNeedUser, //是否展示用户
          orgVersionId: _self.orgTypes[type], //指定对应的版本
          isInMyUnit: options.isInMyUnit,
          eleIdPath: options.eleIdPath,
          otherParams: options.otherParams,
        };
        if ("MyUnit" === type) {
          // 我的单位
          params.unitId = options.unitId;
        } else if ("MyDept" === type) {
          // 我的部门
        } else if ("MyLeader" === type) {
          // 我的上级
        } else if ("MyUnderling" === type) {
          // 我的下属
        } else if ("PublicGroup" === type) {
          // 公共群组
        } else if ("PrivateGroup" === type) {
          // 个人群组
        } else if ("MyCompany" === type) {
          // 我的集团
          params.unitId = options.unitId;
        }
        uni.request({
          data: [type, params],
          service: "multiOrgTreeDialogService.children",
          success: function (result) {
            resultData = _self.orgOptionTypeData[type] = result.data.data;
            if (resultData && _self.isSelectOrgType(type)) {
              each(resultData, function (node) {
                node.isParent = node.type === "V" ? true : false;
              });
              if (null == _self.orgTypes[type]) {
                _self.orgTypes[type] = resultData[0].id; // 默认选中第一个组织版本
              }
            }
            if (callback) {
              callback.call(_self, resultData);
            } else {
              _self.loadDataByPaths([type]);
            }
          },
        });
      } else {
        if (callback) {
          callback.call(_self, resultData);
        } else {
          _self.loadDataByPaths([type]);
        }
      }
    },
    loadDataByPaths: function (paths) {
      var _self = this;
      var type = paths[0];
      // var options = _self.options;
      if (paths.length === 1 && _self.isSelectOrgType(type)) {
        // 组织版本不作为跟节点
        paths.push(_self.orgTypes[type]);
      }
      // var unitId = paths[paths.length - 1];
      var optionType = _self.getOptionTypeDataByPath(paths);
      if (
        paths.length == 1 ||
        optionType.isParent === true ||
        (isArray(optionType.children) && optionType.children.length > 0)
      ) {
        _self.visitChain = paths.concat(); // 复制paths
        // var divId = 'div_' + type + '_content';
        if (paths.length == 1) {
          // _self.appendChildrenItem($('#' + divId + ' .mui-scroll')[0], _self.data[type]);
          _self.showDataList(_self.orgOptionTypeData[type]);
        } else {
          // _self.appendChildrenItem($('#' + divId + ' .mui-scroll')[0], _self.loadChildrenData(type, optionType));
          _self.loadChildrenData(type, optionType, function (resultData) {
            _self.showDataList(resultData);
          });
        }
      }
    },
    getOptionTypeDataByPath: function (paths) {
      var _self = this;
      var clonePaths = paths.concat();
      var type = clonePaths.shift();
      var data = _self.orgOptionTypeData[type];
      each(clonePaths, function (path) {
        var children = data;
        if (false === isArray(data)) {
          children = data.children;
        }
        for (var i = 0; i < children.length; i++) {
          if (children[i].id === path) {
            data = children[i];
            return;
          }
        }
      });
      return data;
    },
    loadChildrenData: function (type, parent, callback) {
      var _self = this;
      var options = _self.options;
      if (options.viewStyle === "list") {
        return _self.loadSearchData(type, "", function (searchData) {
          _self.visitChain = [];
          _self.visitChain.push(type);
          if (_self.isSelectOrgType(type)) {
            _self.visitChain.push(_self.orgTypes[type]);
          }
          _self.showDataList(searchData);
        });
      }
      var resultData = parent.children;
      if (resultData == null || typeof resultData === "undefined") {
        // 默认不要用户数据
        var isNeedUser = "0";
        if (indexOf(options.selectTypes, "U") >= 0) {
          isNeedUser = "1";
        }
        var params = {
          checkedIds: [],
          treeNodeId: parent.id,
          isNeedUser: isNeedUser, //是否展示用户
          orgVersionId: _self.orgTypes[type], //指定对应的版本
          isInMyUnit: options.isInMyUnit,
          eleIdPath: options.eleIdPath,
          otherParams: options.otherParams,
        };
        if ("MyUnit" === type) {
          // 我的单位
          params.unitId = options.unitId;
        } else if ("MyDept" === type) {
          // 我的部门
        } else if ("MyLeader" === type) {
          // 我的上级
        } else if ("MyUnderling" === type) {
          // 我的下属
        } else if ("PublicGroup" === type) {
          // 公共群组
        } else if ("PrivateGroup" === type) {
          // 个人群组
        } else if ("MyCompany" === type) {
          // 我的集团
          params.unitId = options.unitId;
        }
        uni.request({
          data: [type, params],
          service: "multiOrgTreeDialogService.children",
          success: function (result) {
            resultData = result.data.data;
            parent.children = resultData;
            _self.afterChildrenDataLoad(type, parent, resultData, callback);
          },
        });
      } else {
        _self.afterChildrenDataLoad(type, parent, resultData, callback);
      }
    },
    afterChildrenDataLoad(type, parent, resultData, callback) {
      var _self = this;
      var options = _self.options;
      var idPath = parent.idPath ? parent.idPath + "/" + parent.id : parent.id;
      var namePath = parent.namePath ? parent.namePath + "/" + parent.name : parent.name;
      each(resultData || [], function (children) {
        children.idPath = idPath;
        children.namePath = namePath;
      });
      if (options.viewJob === "hide" && isArray(resultData) && resultData.length > 0) {
        var newData = [];
        for (var i = 0; i < resultData.length; i++) {
          var childNode = resultData[i];
          if (childNode.type == "J") {
            // var jobName = childNode.name;
            _self.loadChildrenData(type, childNode, function (userList) {
              // if (isArray(userList) && userList.length > 0) {
              //   for (var j = 0; j < userList.length; j++) {
              //     // userList[j].jobName = jobName;
              //     newData.push(userList[j]);
              //   }
              // }
              if (isArray(userList)) {
                _self.decorateResultData(userList);
                each(userList, function (user) {
                  // var idPaths = user.idPath ? user.idPath.split('/') : [];
                  // var namePaths = user.namePath ? user.namePath.split('/') : [];
                  // var id;
                  // var parentName = namePaths[namePaths.length - 1];
                  // for (var i = 0; i < idPaths.length; i++) {
                  //   if (!(id = idPaths[i])) {
                  //     // 空值
                  //   } else if (id.indexOf('D') === 0) {
                  //     parentName = namePaths[i];
                  //   } else if (id.indexOf('J') === 0) {
                  //     parentName = namePaths[i];
                  //   }
                  // }
                  // user.parentName = parentName;
                  newData.push(user);
                });
              }
            }); // child[i].children;
          } else {
            newData.push(childNode);
          }
        }
        callback.call(_self, newData);
        // _self.showDataList(newData);
      } else {
        callback.call(_self, resultData);
        // _self.showDataList(resultData);
      }
    },
    decorateResultData: function (resultData) {
      var _self = this;
      each(resultData, function (unit) {
        if (unit.decorate === true) {
          return;
        }
        _self.decorateItem(unit);
      });
      // 已选择的值选中
      _self.checkedIfValueExists(resultData);
    },
    decorateItem: function (unit, search) {
      var _self = this;
      var options = _self.options;
      // 处理名称
      if (isEmpty(unit.id)) {
        unit.id = "undefined-unit-id-" + new uuidv4();
        uni.showToast({ title: "unit.id is blank for:" + unit.name });
      }
      var lsName = unit.name;
      var nameType = options.nameType;
      if (options.type === "JobDuty") {
        console.log("decorateItem", options.type);
      } else if (nameType && nameType.charAt(1) == "1") {
        lsName = lsName.substring(lsName.lastIndexOf("/") + 1, lsName.length);
      } else if (nameType && nameType.charAt(1) == "2") {
        if (lsName.indexOf("/") != -1) lsName = lsName.substring(lsName.indexOf("/") + 1, lsName.length);
      }
      unit.name = lsName;

      // var checkClass = self.options.multiple ? 'mui-checkbox' : 'mui-radio';
      // var checkType = self.options.multiple ? 'checkbox' : 'radio';
      var isNav = unit.isParent === true || (isArray(unit.children) && unit.children.length > 0);
      if (unit.type == "U") {
        var idPaths = unit.idPath ? unit.idPath.split("/") : [];
        var namePaths = unit.namePath ? unit.namePath.split("/") : [];
        var parentDeptName, parentJobName, id;
        // var parentName = namePaths[namePaths.length - 1];
        for (var i = 0; i < idPaths.length; i++) {
          if (!(id = idPaths[i])) {
            // 空值
          } else if (id.indexOf("D") === 0) {
            parentDeptName = namePaths[i];
          } else if (id.indexOf("J") === 0) {
            parentJobName = namePaths[i];
          }
        }
        var dataPath = _self.visitChain.join(".") + "." + unit.id;
        if (search) {
          // 搜索结果显示：部门+职位
        } else if (options.viewJob === "hide") {
          // 树中隐藏职位时，要显示人员职位
          parentDeptName = "";
          var parentId = idPaths[idPaths.length - 1];
          if (parentId && parentId.indexOf("J") === 0) {
            dataPath = _self.visitChain.join(".") + "." + parentId + "." + unit.id;
          }
        } else if (options.viewJob === "show") {
          // 树正常模式
          parentJobName = "";
          parentDeptName = "";
        }
        unit.dataPath = dataPath;
        unit.isNav = isNav;
        unit.parentDeptName = parentDeptName;
        unit.parentJobName = parentJobName;
      } else {
        dataPath = _self.visitChain.join(".") + "." + unit.id;
        unit.dataPath = dataPath;
        unit.isNav = isNav;
      }
      // 用户列表
      if (options.viewStyle === "list" && !unit.type) {
        unit.checkable = true;
      } else {
        unit.checkable = _self.hasCheckbox(unit.type);
      }
      unit.decorate = true;
    },
    hasCheckbox: function (lsType) {
      var _self = this;
      return indexOf(_self.options.selectTypes, lsType) > -1;
    },
    checkedIfValueExists(resultData) {
      var _self = this;
      var values = _self.value;
      var valueMap = {};
      each(values, function (selected) {
        valueMap[selected.id] = selected;
      });
      each(resultData, function (unit) {
        if (unit.checkable == true) {
          if (valueMap[unit.id] != null) {
            unit.checked = true;
          } else {
            unit.checked = false;
          }
        }
        _self.updateItemExtraIcon(unit);
      });
    },
    // 删除已选
    onRemoveSelectedClick: function (event, item) {
      var _self = this;
      var values = _self.value;
      for (var index = 0; index < values.length; index++) {
        if (item.id == values[index].id) {
          values.splice(index, 1);
        }
      }
      // 更新已选择文本信息
      _self.updateSelectedText();
      // 更新列表选中状态
      var listData = _self.listData || {};
      for (var key in listData) {
        var array = listData[key];
        each(array, function (unit) {
          if (unit.id === item.id) {
            unit.checked = false;
            _self.updateItemExtraIcon(unit);
          }
        });
      }
    },
    // 更新值
    updateValue: function (item) {
      var _self = this;
      var values = _self.value;
      if (item.checked) {
        for (var index = 0; index < values.length; index++) {
          // 已选择，直接返回
          if (item.id == values[index].id) {
            return;
          }
        }
        values.push(item);
      } else {
        // 取消选择
        for (let index = 0; index < values.length; index++) {
          if (item.id == values[index].id) {
            values.splice(index, 1);
          }
        }
      }
    },
    // 更新已选择文本信息
    updateSelectedText: function () {
      // var _self = this;
      // var selectedText = "已选(" + _self.value.length + ")";
      // //#ifdef H5
      // setTimeout(function () {
      //   var headerBtnNodes = document.querySelectorAll(".uni-page-head .uni-page-head-ft .uni-page-head-btn");
      //   if (headerBtnNodes.length > 0) {
      //     var selectedNode = headerBtnNodes[0].querySelector(".uni-btn-icon");
      //     selectedNode.innerText = selectedText;
      //   }
      // }, 1);
      // //#endif
      // // #ifdef APP-PLUS
      // var currentWebview = _self.$mp.page.$getAppWebview();
      // currentWebview.setTitleNViewButtonStyle(0, {
      //   //h5端会报错
      //   text: selectedText,
      // });
      // // #endif
    },
    updateItemExtraIcon: function (item) {
      var _self = this;
      if (item.checkable) {
        _self.$set(item, "extraIcon", item.extraIcon || { size: 24 });
        _self.$set(item.extraIcon, "type", item.checked ? "checkbox-filled" : "circle");
        _self.$set(item.extraIcon, "color", item.checked ? "#007aff" : "#C0C0C0");
        // item.extraIcon = item.extraIcon || {size: 24};
        // item.extraIcon.type = item.checked ? "checkbox-filled" : "circle";
        // item.extraIcon.color = item.checked ? "#007aff" : "#C0C0C0";
      }
    },
    onNavClick: function (event, nav) {
      var _self = this;
      var paths = nav.dataPath.split(".");
      _self.loadDataByPaths(paths);
      _self.updateNavScrollLeft(paths);
    },
    updateNavScrollLeft: function (paths) {
      this.navScrollLeft = ((paths && paths.length) || 1) * 200;
    },
    onItemClick: function (event, item) {
      var _self = this;
      // 导航夹
      if (item.isNav) {
        var paths = item.dataPath.split(".");
        _self.loadDataByPaths(paths);
        _self.updateNavScrollLeft(paths);
      } else {
        // 可选择
        if (item.checkable) {
          item.checked = !item.checked;
          _self.updateValue(item);
          _self.updateSelectedText();
          _self.updateItemExtraIcon(item);
        }
      }
    },
    showDataList: function (resultData) {
      var _self = this;
      // var options = _self.options;
      var orgOption = _self.orgOptionList[_self.tabIndex];
      var type = orgOption.id;
      // var excludeValues = options.excludeValues || [];
      var dataChain = _self.getDataChainByPath(_self.visitChain);
      var navs = [];
      var dataPath = _self.visitChain[0];
      each(dataChain, function (unit, index) {
        var name = index == 0 ? _self._getTypeLabel(_self.visitChain[0]) : unit.name;
        if (_self.isSelectOrgType(type)) {
          if (index == 0) {
            console.log(unit);
          } else if (index == 1) {
            // 版本切换
            var verList = _self.orgOptionTypeData[type];
            if (!verList || verList.length <= 1) {
              unit.switchVersion = false;
            } else {
              unit.switchVersion = true;
            }
            dataPath += "." + _self.visitChain[index];
            unit.dataPath = dataPath;
            navs.push(unit);
          } else {
            dataPath += "." + _self.visitChain[index];
            unit.dataPath = dataPath;
            navs.push(unit);
          }
        } else {
          if (index == 0) {
            navs.push({
              name: name,
              dataPath: dataPath,
            });
          } else {
            dataPath += "." + _self.visitChain[index];
            unit.dataPath = dataPath;
            navs.push(unit);
          }
        }
      });
      // console.log("navs: ");
      // console.log(JSON.stringify(navs));
      // console.log(dataChain);
      // console.log(resultData);
      _self.decorateResultData(resultData);
      _self.navData[orgOption.id] = navs;
      _self.listData[orgOption.id] = resultData;
      // _self.$set(_self.listData, orgOption.id, resultData);
    },
    _getTypeIndex: function (type) {
      var _self = this;
      for (var i = 0; i < _self.orgOptionList.length; i++) {
        if (_self.orgOptionList[i].id === type) {
          return i;
        }
      }
      return 0;
    },
    _getTypeLabel: function (type) {
      var _self = this;
      var index = _self._getTypeIndex(type);
      var label = index != -1 ? _self.orgOptionList[index].name : "";
      return isEmpty(label) ? _self.options.title : label;
    },
    getDataChainByPath: function (paths, type) {
      var _self = this;
      var data,
        dataChain = [];
      var clonePaths = paths.concat();
      if (isEmpty(trim(type))) {
        // 默认第一节点
        type = clonePaths.shift();
        data = _self.orgOptionTypeData[type];
        dataChain.push(data);
      } else {
        data = _self.orgOptionTypeData[type];
      }
      each(clonePaths, function (path) {
        var children = data;
        if (false === isArray(data)) {
          children = data.children;
        }
        for (var i = 0; i < children.length; i++) {
          if (children[i].id === path) {
            data = children[i];
            dataChain.push(data);
            return;
          }
        }
      });
      return dataChain;
    },
    // 是否选择组织版本(组织版本不作为跟节点)
    isSelectOrgType: function (type) {
      var _self = this;
      var options = _self.options;
      return !options.showRoot && indexOf(["MyUnit", "MyDept", "MyLeader", "MyUnderling", "MyCompany"], type) > -1;
    },
    // 切换组织版本
    showSwitchOrgVersion: function () {
      var _self = this;
      var orgOption = _self.orgOptionList[_self.tabIndex];
      var type = orgOption.id;
      var versions = _self.orgOptionTypeData[type];
      var val = _self.orgTypes[type];

      // if(orgNavData.length > 1) {
      //   var orgVersion = orgNavData[1];
      var orgVersions = [];
      each(versions, function (v) {
        var orgVer = Object.assign({}, v);
        if (val == orgVer.id) {
          orgVer.checked = true;
        }
        orgVersions.push(orgVer);
      });
      _self.orgVersions = orgVersions;
      // open 方法传入参数 等同在 uni-popup 组件上绑定 type属性
      _self.$refs.popup.open("bottom");
      // }
    },
    onOrgVersionRadioChange: function (event) {
      var _self = this;
      var items = _self.orgVersions;
      for (let i = 0; i < items.length; i++) {
        const item = items[i];
        if (item.id === event.detail.value) {
          // item.checked = true;
          _self.$set(item, "checked", true);
        } else {
          // item.checked = false;
          _self.$set(item, "checked", false);
        }
      }
    },
    onOrgVersionOk: function () {
      var _self = this;
      _self.$refs.popup.close("bottom");
      var items = _self.orgVersions;
      var checkedValues = [];
      for (var i = 0; i < items.length; i++) {
        const item = items[i];
        if (item.checked === true) {
          checkedValues.push(item.id);
        }
      }
      if (checkedValues.length > 0) {
        var orgOption = _self.orgOptionList[_self.tabIndex];
        var type = orgOption.id;
        _self.orgTypes[type] = checkedValues[0];
        _self.loadDataByPaths([type]);
      }
    },
    onTabTap(e) {
      var _self = this;
      let index = e.target.dataset.current || e.currentTarget.dataset.current;
      _self.switchTab(index);
    },
    onTabChange(e) {
      var _self = this;
      let index = e.target.current || e.detail.current;
      _self.switchTab(index);
      // 变更按钮状态
      _self.changeButtonState();
      // 加载数据
      _self.loadData(index);
    },
    switchTab: function (index) {
      this.tabIndex = index;
      this.scrollIntoView = this.tabs[index].id;
    },
    changeButtonState: function () {
      var _self = this;
      var options = _self.options;
      var orgOption = _self.orgOptionList[_self.tabIndex];
      var type = orgOption.id;
      var viewStype = orgOption.attach;
      // 支持按列表展示,默认只支持树
      if (viewStype && viewStype.indexOf("list") > -1 && indexOf(options.selectTypes, "U") > -1) {
        _self.showUserListButton = true;
      } else {
        _self.showUserListButton = false;
        options.viewStyle = null;
      }
      if (_self.isSelectOrgType(type)) {
        _self.showHideJobButton = true;
        _self.showShowJobButton = true;
      } else {
        _self.showHideJobButton = false;
        _self.showShowJobButton = false;
      }
    },
    keywordQuery: function (e) {
      var _self = this;
      _self.doKeywordQuery = true;
      var searchText = trim(e.value);
      _self.loadKeywordQuery(searchText);
    },
    keywordQueryFocus: function () {
      this.doKeywordQuery = true;
    },
    cancelKeywordQuery: function () {
      var _self = this;
      _self.doKeywordQuery = false;
      _self.loadKeywordQuery("");
    },
    loadKeywordQuery: function (searchText) {
      var _self = this;
      var type = _self.visitChain[0];
      if (isEmpty(searchText)) {
        _self.orgOptionTypeData[type] = null;
        _self.loadData(_self.tabIndex);
      } else if (searchText != _self.searchText) {
        _self.loadSearchData(type, searchText, function (searchData) {
          _self.visitChain = [];
          _self.visitChain.push(type);
          if (_self.isSelectOrgType(type)) {
            _self.visitChain.push(_self.orgTypes[type]);
          }
          _self.showDataList(searchData);
        });
      }
      _self.searchText = searchText;
    },
    loadSearchData: function (type, searchText, callback) {
      var _self = this;
      var options = _self.options;
      // var type = options.type;
      var filterData = [];
      var service = "multiOrgTreeDialogService.search";
      var params = {
        isNeedUser: "1", //是否展示用户
        keyword: searchText,
        otherParams: options.otherParams,
        eleIdPath: options.eleIdPath,
        isInMyUnit: options.isInMyUnit,
        orgVersionId: _self.orgTypes[type], //指定对应的版本
      };
      if (indexOf(["MyUnit", "MyCompany"], type) > -1) {
        params.unitId = options.unitId;
      }
      if (trim(params.orgVersionId).length <= 0 && _self.orgTypes) {
        params.orgVersionId = _self.orgTypes[Object.keys(_self.orgTypes)[0]];
      }
      if (options.viewStyle == "list") {
        params.sort = "code";
        params.isNeedUser = "2";
        service = "multiOrgTreeDialogService.allUserSearch";
      }
      uni.request({
        service: service,
        data: [type, params],
        success: function (result) {
          filterData = result.data.data;
          if (_self.isSelectOrgType(type) && params.orgVersionId) {
            each(_self.orgOptionTypeData[type], function (parent) {
              if (params.orgVersionId === parent.id) {
                parent.children = filterData && options.viewStyle == "list" ? filterData.nodes : filterData;
              }
            });
          } else {
            _self.orgOptionTypeData[type] = filterData;
          }
          if (options.viewStyle == "list" && filterData) {
            callback.call(_self, filterData.nodes);
          } else {
            callback.call(_self, filterData);
          }
        },
      });
    },
    onHideJobClick: function () {
      this.showOrHideJob("hide");
    },
    onShowJobClick: function () {
      this.showOrHideJob("show");
    },
    showOrHideJob: function (viewJob) {
      var _self = this;
      var options = _self.options;
      var orgOption = _self.orgOptionList[_self.tabIndex];
      var type = orgOption.id;
      if (options.viewStyle === "list") {
        _self.orgOptionTypeData[type] = null; // 切换到跟目录
        _self.visitChain = [type];
        _self.loadData(_self.tabIndex, function () {
          options.viewJob = viewJob;
          options.viewStyle = "tree";
          _self.loadDataByPaths(_self.visitChain);
        });
      } else {
        options.viewJob = viewJob;
        options.viewStyle = "tree";
        _self.loadDataByPaths(_self.visitChain);
      }
    },
    onUserListClick: function () {
      var _self = this;
      var options = _self.options;
      var orgOption = _self.orgOptionList[_self.tabIndex];
      var type = orgOption.id;
      options.viewStyle = "list";
      _self.loadDataByPaths([type]); // ["MyUnit"]  ["MyCompany"]
    },
    onCancel: function () {
      console.log("onCancel");
      var _self = this;
      var options = _self.options;
      if (options.close) {
        options.close.call(options);
      }
      uni.navigateBack({
        delta: 1,
      });
    },
    onOk: function () {
      console.log("onOk");
      var _self = this;
      var options = _self.options;
      // 获取选择的值
      var checkNodes = _self.value; // _self.getSelectNodes();
      var name = [];
      var id = [];
      var email = [];
      var employeeNumber = [];
      var loginName = [];
      each(checkNodes, function (unit) {
        if (isEmpty(unit.orgVersionId) || "justId" === options.valueFormat) {
          id.push(unit.id);
        } else {
          id.push(unit.orgVersionId + "/" + unit.id);
        }
        name.push(unit.name);
        email.push(unit.email || "");
        employeeNumber.push(unit.employeeNumber || "");
        loginName.push(unit.loginName || "");
      });
      var returnValue = {
        id: id.join(_self.options.separator),
        name: name.join(_self.options.separator),
        email: email.join(_self.options.separator),
        employeeNumber: employeeNumber.join(_self.options.separator),
        loginName: loginName.join(_self.options.separator),
      };
      if (options.ok) {
        options.ok.call(options, returnValue, checkNodes);
      }
      if (options.callback) {
        options.callback.call(options, id, name, checkNodes);
      }
      uni.navigateBack({
        delta: 1,
      });
    },
    getSelectNodes: function () {
      var _self = this;
      var nodes = [];
      var listData = _self.listData || {};
      for (var key in listData) {
        var array = listData[key];
        each(array, function (unit) {
          if (unit.checked === true) {
            nodes.push(unit);
          }
        });
      }
      return nodes;
    },
    onNavigationBarButtonTap(e) {
      var _self = this;
      // 查看已选
      if (e.type == "selected") {
        _self.$refs.selectedPopup.open("bottom");
      }
    },
    showSelectedPopup() {
      this.$refs.selectedPopup.open("bottom");
    },
  },
  computed: {
    isMoreOptList: function () {
      var _self = this;
      var options = _self.options;
      return isArray(options.moreOptList) && options.moreOptList.length > 0;
    },
  },
};
</script>
<style lang="scss" scoped>
/* #ifndef APP-PLUS */
page {
  width: 100%;
  min-height: 100%;
  display: flex;
}

/* #endif */

.org-unit-dialog {
  flex: 1;
  flex-direction: column;
  overflow: hidden;
  background-color: $uni-bg-color;
  /* #ifndef APP-PLUS */
  height: 100vh;
  /* #endif */

  .nav-bar-right {
    font-size: 14px;
  }
  .nav-bar-text-count {
    margin-bottom: 2px;
  }

  .org-box {
  }

  .org-nav-container {
    background-color: $uni-bg-secondary-color;
  }

  .scroll-h {
    width: 750rpx;
    /* #ifdef H5 */
    width: 100%;
    /* #endif */
    height: 80rpx;
    white-space: nowrap;
  }

  .line-h {
    height: 1rpx;
    background-color: #cccccc;
  }

  .uni-tab-item {
    display: inline-block;
    flex-wrap: nowrap;
    padding-left: 34rpx;
    padding-right: 34rpx;
  }

  .uni-tab-item-title {
    color: #555;
    font-size: 30rpx;
    height: 80rpx;
    line-height: 80rpx;
    flex-wrap: nowrap;
    /* #ifndef APP-PLUS */
    white-space: nowrap;
    /* #endif */
  }

  .uni-tab-item-title-active {
    color: $uni-text-color-active; // #007aff;
  }

  .swiper-box {
    flex: 1;
    height: "500px";
  }

  .swiper-item {
    flex: 1;
    flex-direction: row;
  }

  .scroll-v {
    flex: 1;
    /* #ifndef MP-ALIPAY */
    flex-direction: column;
    /* #endif */
    width: 750rpx;
    width: 100%;
  }

  .update-tips {
    position: absolute;
    left: 0;
    top: 41px;
    right: 0;
    padding-top: 5px;
    padding-bottom: 5px;
    background-color: #fddd9b;
    align-items: center;
    justify-content: center;
    text-align: center;
  }

  .update-tips-text {
    font-size: 14px;
    color: #ffffff;
  }

  .refresh {
    width: 750rpx;
    width: 100%;
    height: 64px;
    justify-content: center;
  }

  .refresh-view {
    flex-direction: row;
    flex-wrap: nowrap;
    align-items: center;
    justify-content: center;
  }

  .refresh-icon {
    width: 30px;
    height: 30px;
    transition-duration: 0.5s;
    transition-property: transform;
    transform: rotate(0deg);
    transform-origin: 15px 15px;
  }

  .refresh-icon-active {
    transform: rotate(180deg);
  }

  .loading-icon {
    width: 20px;
    height: 20px;
    margin-right: 5px;
    color: #999999;
  }

  .loading-text {
    margin-left: 2px;
    font-size: 16px;
    color: #999999;
  }

  .loading-more {
    align-items: center;
    justify-content: center;
    padding-top: 10px;
    padding-bottom: 10px;
    text-align: center;
  }

  .loading-more-text {
    font-size: 28rpx;
    color: #999;
  }

  .keyword-query-input {
    width: 100%;

    ::v-deep .uni-searchbar__box-search-input {
      color: $uni-text-color;
    }
    ::v-deep .uni-searchbar__box {
      background-color: var(--bg-search-bar-color) !important;
    }
    ::v-deep .uni-searchbar__cancel {
      color: $uni-text-color;
    }
  }
  .list-header-buttons {
    margin-top: 10px;
    margin-right: 10px;
  }
  .nav-container {
    margin-left: 20rpx;
    justify-content: flex-start;
  }
  .nav-text {
    margin: 0rpx 10rpx;
    padding: 0rpx;
    height: 70rpx;
    line-height: 70rpx;
    text-align: center;
    font-size: 26rpx;
    color: $uni-text-color;
  }
  .mini-btn {
    height: 35px;
    line-height: 35px;
    padding: 0 5px;
  }
  .uni-list-cell {
    justify-content: flex-start;
  }
  .uni-list-container {
    padding-left: 15px;
    position: relative;
    /* #ifndef APP-NVUE */
    box-sizing: border-box;
    /* #endif */
    /* #ifdef H5 */
    cursor: pointer;
    /* #endif */
  }

  .org-list-item {
    background-color: $uni-bg-secondary-color;
  }
  .org-list-item-body {
    width: 100%;
    height: 32px;
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: center;
    color: $uni-text-color;
  }

  // .uni-list-item {
  //   position: relative;
  //   /* #ifndef APP-NVUE */
  //   box-sizing: border-box;
  //   /* #endif */
  //   min-height: 50px;
  //   padding: 10px;
  //   padding-left: 0;
  //   border-bottom-style: solid;
  //   border-bottom-width: 1px;
  //   border-bottom-color: #dedede;
  // }
  .footer {
    width: 100%;
    position: fixed;
    bottom: var(--window-bottom, 0);
    background-color: #fff;
  }

  .popup-org-dialog {
    background-color: $uni-bg-secondary-color;
    height: 300px;

    .popup-title {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */
      flex-direction: row;
      align-items: center;
      justify-content: center;
      height: 40px;
      border-bottom: 1px solid $uni-border-3;
    }

    .popup-title-text {
      font-size: 16px;
      color: $uni-text-color;
      font-weight: bold;
    }

    .popup-content {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */
      flex-direction: row;
      justify-content: center;
      // padding-top: 10px;

      .popup-content-scroll-view {
        height: 200px;
      }

      .hide-confirm-btn {
        height: 250px;
      }

      .uni-list {
        background-color: $uni-bg-secondary-color;
      }

      .item-selected {
        background-color: $uni-bg-secondary-color;
        ::v-deep .uni-list-item__content-title {
          color: $uni-text-color;
        }
        .icon {
          color: $uni-icon-color !important;
        }
      }

      .popup-check-item {
        display: flex;
        flex-direction: row;
        width: 100%;
        height: 26px;
        justify-content: center;
        align-items: center;
        color: $uni-text-color;

        .popup-check-icon {
          width: 20px;
          padding-right: 6px;
        }
      }
    }

    .popup-button-box {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */
      flex-direction: row;
      padding: 10px 15px;

      .popup-button {
        flex: 1;
        border-radius: 50px;
        background-color: $uni-primary;
        color: #fff;
        font-size: 16px;
      }

      .popup-button::after {
        border-radius: 50px;
      }
    }
  }

  .org-dialog-button-group {
    width: 100%;
    position: fixed;
    bottom: var(--window-bottom, 0);
    background-color: $uni-bg-secondary-color;

    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    border-top-color: #f5f5f5;
    border-top-style: solid;
    border-top-width: 1px;

    .org-dialog-button {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */

      box-shadow: $uni-shadow-base;

      flex: 1;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      height: 40px;
    }

    .org-border-left {
      border-left-color: #f0f0f0;
      border-left-style: solid;
      border-left-width: 1px;
    }

    .org-dialog-button-text {
      font-size: 16px;
      color: $uni-text-color;
    }

    .org-button-color {
      color: #007aff;
    }
  }
}
</style>
