<template>
  <view class="org-select">
    <view
      :class="[
        'org-input',
        disable ? 'disable' : '',
        readonly ? 'readonly' : '',
        textonly ? 'textonly' : '',
        showOpenIcon ? 'showOpenIcon' : '',
      ]"
      @click="showPopup"
    >
      <view class="org-value-items">
        <template v-if="displayStyle == 'IconLabel'">
          <template v-for="item in valueNodes">
            <view class="value-item" @click.stop="() => {}">
              <view class="tag">
                <w-icon :icon="orgElementIcon[item.type]" />
                <text class="text" v-if="titleDisplay == 'titlePath'">{{ item[titleDisplay] || item.title }}</text>
                <text class="text" v-else>
                  {{ item[titleDisplay] || item.title }}
                </text>

                <view @click.stop="closeTag(item)" class="close-icon">
                  <w-icon icon="closeempty" color="#9c9c9c" :size="14" /> </view
              ></view>
            </view>
          </template>
        </template>
        <template v-else-if="displayStyle == 'GroupIconLabel'">
          <template v-for="item in valueGroupNodes">
            <view class="value-item" @click.stop="() => {}">
              <view class="tag">
                <w-icon :icon="orgElementIcon[item.key]" />
                <text class="text">
                  {{ item.label }}
                </text>
                <view @click.stop="closeTagGroup(item.nodes)" class="close-icon">
                  <w-icon icon="closeempty" color="#9c9c9c" :size="14" /> </view
              ></view>
            </view>
          </template>
        </template>
        <template v-else>
          {{ valueLabels.join(separator + " ") }}
        </template>
      </view>
      <uni-load-more status="loading" :showText="false" style="position: absolute; right: 7px" v-show="valueLoading" />

      <view
        class="clear-icon"
        v-show="!readonly && !disable && !textonly && valueNodes.length > 0"
        @click.stop="onClearInput"
      >
        <w-icon icon="clear" color="#9c9c9c" :size="24" />
      </view>
    </view>
    <uni-popup
      ref="orgPopup"
      type="right"
      safeArea
      backgroundColor="#fff"
      class="org-popup"
      mask-background-color="rgba(0, 0, 0, 0.0)"
    >
      <view @click="onClickPopup" style="width: 100vw">
        <view class="header">
          <view class="title">
            <view>
              <view class="select-org-type-label" @click.stop="onChangeOrgType">
                <text>{{ orgTypeValueLabelMap[selectOrgType] }}</text
                ><w-icon icon="down" style="margin-left: 8px" />
              </view>

              <view class="org-type-select-box" v-show="showOrgTypeSelect">
                <view class="arrow_bottom"></view>
                <view class="type-options">
                  <template v-for="opt in orgTypeOptions">
                    <view class="option" @click.stop="onSelectOrgType(opt)">
                      {{ opt.label }}
                    </view></template
                  >
                </view>
              </view>
            </view>
            <view class="title-buttons">
              <w-icon
                class="switch-to-user-list"
                @onTap="onUserIndexedListToggle"
                :icon="userIndexedList ? 'list' : 'staff-filled'"
                color="#333"
              />
              <w-icon icon="closeempty" color="#333" @onTap="closePopup" />
            </view>
          </view>
          <view class="switcher-search">
            <view
              class="select-org"
              @click.stop="
                showOrgSelect = !showOrgSelect;
                showOrgTypeSelect = false;
              "
              v-show="selectOrgType == 'MyOrg' && !showSearchInput && !orgUuid"
            >
              <text class="select-org-label">{{ selectedOrgLabel }}</text>
              <view class="switcher-icon">
                <w-icon icon="bars" />
              </view>
            </view>
            <view :class="['search-box', showSearchInput ? 'show-search' : '']">
              <uni-search-bar
                v-show="showSearchInput"
                v-model="searchWord"
                @input="onSearchChange"
                @cancel="onSearchCancel"
                @clear="onSearchClear"
                cancelButton="none"
                placeholder="请输入搜索内容"
              >
              </uni-search-bar>
              <view v-show="!showSearchInput" class="search-btn" @click="switchSearchMode(true)">
                <w-icon icon="search" color="#666666" />
                <text>搜索</text>
              </view>
            </view>
            <!-- <uni-transition
              custom-class="transition"
              :mode-class="['fade', 'slide-top']"
              :show="showOrgSelect"
              :style="{ width: '100%', height: '300px', backgroundColor: 'red', position: 'absolute', top: '47px' }"
            >
            </uni-transition> -->

            <view class="switch-org-box" v-show="showOrgSelect">
              <scroll-view scroll-y="true" class="scroll-view">
                <template v-for="org in orgSelectOptions">
                  <view
                    @click.stop="onSelectOrg(org)"
                    :class="['org-select-option', currentOrgUuid == org.value ? 'selected' : '']"
                    >{{ org.label }}</view
                  >
                </template>
              </scroll-view>
              <view class="mask"></view>
            </view>
          </view>
        </view>
        <view class="content">
          <scroll-view :scroll-y="false" :scroll-x="true" class="org-tree-breadcrumb-bar">
            <uni-breadcrumb separator=">">
              <uni-breadcrumb-item
                v-for="bread in breadcrumb"
                :class="bread.index == breadcrumb.length - 1 ? 'active' : ''"
              >
                <text @click="jumpToTreeLevelNode(bread, bread.index)">
                  {{ bread.label }}
                </text>
              </uni-breadcrumb-item>
            </uni-breadcrumb>
          </scroll-view>

          <scroll-view
            scroll-y="true"
            v-if="!userIndexedList"
            style="
              height: calc(100vh - 220px - var(--window-top) - var(--status-bar-height) - var(--window-bottom));
              background-color: #fff;
            "
          >
            <uni-load-more
              iconType="circle"
              :status="loading ? 'loading' : 'noMore'"
              :contentText="{
                contentnomore:
                  searchWord != '' && selectOrgTypeControl.searchResults.length == 0 ? '查无数据' : '暂无数据',
              }"
              v-show="
                loading ||
                (!loading &&
                  ((selectOrgTypeControl && selectOrgTypeControl.currentLevelTreeData.length == 0) ||
                    (searchWord != '' && selectOrgTypeControl.searchResults.length == 0)))
              "
            />

            <template
              v-if="
                !loading &&
                selectOrgTypeControl &&
                (selectOrgTypeControl.searchResults.length > 0 || selectOrgTypeControl.currentLevelTreeData.length > 0)
              "
            >
              <view :class="['tree-panel', searchWord != '' ? 'search' : '']">
                <view
                  class="tree-node"
                  v-for="node in selectOrgTypeControl[searchWord != '' ? 'searchResults' : 'currentLevelTreeData']"
                  @click="onSelectOrgNode(node, searchWord != '' ? [] : selectOrgTypeControl.currentLevelTreeData)"
                >
                  <view
                    :class="[
                      'radio',
                      !node.checkable ? 'uncheckable' : '',
                      checkedKeys.checked.includes(node.key) ? 'checked' : '',
                    ]"
                  >
                    <w-icon
                      v-show="checkedKeys.checked.includes(node.key)"
                      icon="checkmarkempty"
                      :size="14"
                      color="#fff"
                    />
                  </view>
                  <org-node-avatar :nodeData="node" :orgElementIcon="orgElementIcon" :titleField="titleField" />
                  <view class="node-title">
                    <view class="label">{{ node[titleField] }}</view>
                    <view class="sub-label" v-show="node.type == 'user' && node.titlePath">
                      {{ displayNodeSubTitle(node) }}
                    </view>
                  </view>

                  <view
                    class="next-children"
                    v-if="searchWord == '' && ((node.children && node.children.length > 0) || node.isLeaf === false)"
                    @click.stop="toNextLevelChildren(node)"
                  >
                    <view v-show="!node.childrenLoading">
                      <text>下级 </text>
                      <w-icon icon="right" :size="14" color="var(--color-primary)"
                    /></view>

                    <uni-load-more status="loading" v-show="node.childrenLoading" :showText="false" />
                  </view> </view
              ></view>
            </template>
          </scroll-view>
          <view
            style="
              position: relative;
              height: calc(100vh - 200px - var(--window-top) - var(--status-bar-height) - var(--window-bottom));
              background-color: #fff;
            "
            v-else
          >
            <uni-w-indexed-list :options="letterUserGroup" :showSelect="true" @click="onClickLetterUser" />
          </view>
        </view>
        <view class="footer">
          <view class="select-box">
            <view :class="['radio', checkAll ? 'checked' : '']" style="margin-right: 8px" @click="onCheckAll">
              <w-icon icon="checkmarkempty" :size="14" color="#fff" v-show="checkAll" @onTap="onCheckAll" />
            </view>
            <text @click="onCheckAll">全选</text>
            <view class="divider divider-vertical"></view>
            <view @click="orgSelectedPopupToggle">
              <text>已选择</text>
              <text style="padding-left: 8px; color: var(--color-primary)" v-show="selectedNodes.length > 0"
                >{{ selectedNodes.length }} 个</text
              >
            </view>
          </view>

          <button class="mini-btn" type="primary" size="mini" @click="onConfirm">确定</button>
        </view>
      </view>
    </uni-popup>
    <uni-popup
      ref="orgSelectedPopup"
      type="bottom"
      safeArea
      borderRadius="12px 12px 0px 0px"
      class="org-popup selected-popup"
    >
      <view style="background: #fff; border-radius: 12px">
        <view class="header">
          <w-icon @onTap="orgSelectedPopupToggle" icon="left" color="#333333" :size="18" />
          <w-icon @onTap="orgSelectedPopupToggle" icon="closeempty" color="#333333" :size="18" />
        </view>
        <HM-w-dragSorts
          class="drag-sort-list"
          ref="dragSorts"
          :list="dragSelectedList"
          :autoScroll="true"
          :feedbackGenerator="true"
          :listHeight="300"
          :rowHeight="55"
          @confirm="onDragConfirm"
        >
          <template slot="content" slot-scope="{ row }">
            <view class="tree-node">
              <org-node-avatar :nodeData="row" :orgElementIcon="orgElementIcon" :titleField="titleField" />
              <view class="node-title">
                <view class="label">{{ row[titleField] }}</view>
                <view class="sub-label" v-show="row.type == 'user' && row.titlePath">
                  {{ displayNodeSubTitle(row) }}
                </view>
              </view>
              <view class="operation">
                <view class="link-button" @click.stop="onRemoveDragSelected(row)">移除</view>
              </view>
            </view>
          </template>
        </HM-w-dragSorts>
        <view class="footer">
          <button class="mini-btn cancel" type="default" size="mini" @click="orgSelectedPopupToggle">取消</button
          ><button class="mini-btn confirm" type="primary" size="mini" @click="onConfirmSelectedEdit">确定</button>
        </view>
      </view>
    </uni-popup>
    <u-toast ref="toast"></u-toast>
  </view>
</template>
<style lang="scss"></style>
<script>
import "./index.scss";
import { orderBy, debounce, uniqBy } from "lodash";
import OrgNodeAvatar from "./component/org-node-avatar.vue";

const framework = require("wellapp-uni-framework");
export default {
  name: "w-org-select",
  props: {
    title: {
      type: String,
      default: function () {
        return "选择人员";
      },
    },

    value: {
      type: [String, Array],
    },

    isPathValue: {
      // 值以路径形式返回
      type: Boolean,
      default: false,
    },

    titleDisplay: {
      type: String,
      default: "title",
      validator: function (v) {
        return ["title", "shortTitle", "titlePath"].includes(v);
      },
    },
    titleField: {
      // 节点标题字段：可以使用全称或者简称来展示树节点
      type: String,
      default: "title",
      validator: function (v) {
        return ["title", "shortTitle"].includes(v);
      },
    },
    checkableTypes: {
      // unit / job / dept / user 以及自定义组织模型的类型
      type: Array,
    },
    uncheckableTypes: {
      type: Array,
    },
    checkableTypesOfOrgType: {
      // 按选择项类型区分可选模型的类型
      type: Object,
      default() {
        return {};
      },
    },
    separator: {
      // 分隔符
      type: String,
      default: ";",
    },
    displayStyle: {
      type: String,
      default: "IconLabel",
      validator: function (v) {
        return ["Label", "IconLabel", "GroupIconLabel"].includes(v);
      },
    },
    readonly: {
      type: Boolean,
      default: false,
    },
    disable: {
      type: Boolean,
      default: false,
    },
    showOpenIcon: {
      type: Boolean,
      default: true,
    },
    textonly: {
      type: Boolean,
      default: false,
    },

    orgUuid: String, // 指定组织
    orgVersionId: String, // 指定组织版本号
    orgVersionIds: Array, // 可选的组织版本ID列表
    orgType: {
      // 可选择的组织选择项类型
      type: [String, Array],
      default: function () {
        return ["MyOrg", "MyLeader"];
      },
    },
    defaultOrgType: String,
    // 参数
    params: {
      type: Object,
      default() {
        return {};
      },
    },
    // 多选
    multiSelect: {
      type: Boolean,
      default: true,
    },

    orgTypeExtensions: Array,
  },
  components: { OrgNodeAvatar },
  filters: {
    textOverflow(text) {
      if (text.length > 10) {
        // 超过30个字长度，省略后续字处理
        return text.substr(0, 10) + "...";
      }
      return text;
    },
  },
  computed: {
    selectOrgTypeControl() {
      return this.orgTypeControl[this.selectOrgType];
    },
    selectedOrgLabel() {
      let label = "";
      if (this.currentOrgUuid && this.orgSelectOptions.length > 0) {
        for (let i = 0, len = this.orgSelectOptions.length; i < len; i++) {
          if (this.orgSelectOptions[i].value == this.currentOrgUuid) {
            return this.orgSelectOptions[i].label;
          }
        }
      }
      return label;
    },

    valueLabels() {
      let label = [];
      for (let i = 0, len = this.valueNodes.length; i < len; i++) {
        let title = this.valueNodes[i][this.titleDisplay] || this.valueNodes[i].title;
        label.push(title);
      }
      return label;
    },

    valueGroupNodes() {
      // 按类型拆分
      let group = {};
      for (let i = 0, len = this.valueNodes.length; i < len; i++) {
        let n = this.valueNodes[i];
        if (group[n.type] == undefined) {
          group[n.type] = {
            nodes: [],
            key: n.type,
            label: "",
            labels: [],
          };
        }
        group[n.type].nodes.push(n);
        group[n.type].label += (n[this.titleDisplay] || n.title) + " ";
        group[n.type].labels.push(n[this.titleDisplay] || n.title);
      }
      return group;
    },
  },

  data() {
    let _value = this.value,
      valueLoading = false;
    if (typeof _value === "string") {
      let separator = new RegExp(";|,|" + this.separator);
      _value = _value.split(separator);
    }
    if (_value && _value.length > 0) {
      valueLoading = true;
    }
    // TODO：由组织参数设置中配置的组织选择项读取
    let defaultOrgTypeOptions = [
        { value: "MyOrg", label: "我的组织" },
        { value: "MyLeader", label: "我的领导" },
        { value: "MyDept", label: "我的部门" },
        { value: "MyUnderling", label: "我的下属" },
        { value: "TaskUsers", label: "办理人" },
        { value: "TaskDoneUsers", label: "已办人员" },
        { value: "PublicGroup", label: "公共群组" },
      ],
      orgTypeValueLabelMap = {};
    defaultOrgTypeOptions.forEach((o) => {
      orgTypeValueLabelMap[o.value] = o.label;
    });
    if (this.orgTypeExtensions != undefined) {
      this.orgTypeExtensions.forEach((o) => {
        orgTypeValueLabelMap[o.value] = o.label;
      });
    }
    let orgTypeControl = {},
      defaultTypeControl = {
        currentOrgUuid: "", // 当前组织
        selectedOrgVersionId: "", // 当前组织版本
        fetched: false, // 数据已拉取
        treeData: [], //树形数据
        currentLevelTreeData: [],
        searchKeyword: undefined, // 搜索关键字
        searchResults: [], // 搜索结果
        searchView: false, // 是否搜索视图
        checkAll: false, // 全选
        indeterminate: false, // 半选
        collapsed: true, // 是否折叠树
        expandedKeys: [], // 展开树的key集合
        userViewIndeterminate: false,
        userViewCheckAll: false,
        treeKeys: { checkable: [], all: [] },
        async: false,
        asyncOrg: new Set(),
        asyncUser: new Set(),
        underSerCount: {},
      },
      selectedOrgTypeKeys = [],
      orgTypeOptions = [];
    if (typeof this.orgType == "string") {
      orgTypeOptions.push({ label: orgTypeValueLabelMap[this.orgType] || this.orgType, value: this.orgType });
      selectedOrgTypeKeys = [this.orgType];
      // 不同类型的变量控制
      orgTypeControl[this.orgType] = { ...defaultTypeControl };
    } else if (Array.isArray(this.orgType)) {
      this.orgType.forEach((t) => {
        let key = typeof t === "string" ? t : t.value;
        orgTypeOptions.push(typeof t === "string" ? { label: orgTypeValueLabelMap[t] || t, value: t } : t);
        orgTypeControl[key] = { ...defaultTypeControl };
      });
      selectedOrgTypeKeys = [typeof this.orgType[0] === "string" ? this.orgType[0] : this.orgType[0].value];
    }
    // if (orgTypeControl.MyOrg) {
    //   orgTypeControl.MyOrg.async = true; // 我的组织为异步查询节点
    // }
    if (this.defaultOrgType) {
      selectedOrgTypeKeys = [this.defaultOrgType];
    }
    let checkStrictly = true;
    return {
      orgElementIcon: { user: "person", group: "staff" },
      setting: null,
      loading: true,
      checkStrictly,
      selectedOrgTypeKeys,
      treeView: true,
      userIndexedList: false,
      letterUserGroup: [],
      checkedKeys: checkStrictly ? { checked: _value || [], halfChecked: [] } : [],
      previewUserUnderNode: false,
      jobLevelVisible: true, // 组织弹窗中显示职位节点/层级
      selectOrgType: selectedOrgTypeKeys[0],
      previewUserNodes: [],
      selectedNodes: [],
      valueNodes: [],
      checkedIdPath: [],
      indeterminatePreviewUser: false,
      checkAllPreviewUser: false,
      checkAll: false,
      letters: "abcdefghijklmnopqrstuvwxyz".split(""),
      selectedLetter: undefined,
      inputWidth: 100,
      orgTypeControl,
      valueLoading,
      nodePath: {},
      orgSelectOptions: [],
      defaultOrgTypeOptions,
      orgTypeOptions,
      selectionSearchKeyword: undefined,
      filterSelections: undefined,
      orgVersionOptions: undefined,
      selectedOrgVersionId: this.orgVersionId,
      currentOrgUuid: undefined,
      isInternalChange: false,
      letterUserLoading: false,
      previewNodeUserLoading: false,
      previewNodeUserPage: {
        pageIndex: 0,
        pageSize: 15,
        total: 0,
        totalPage: 0,
        searchWord: undefined,
      },
      searchInputPlaceholder: undefined,
      searchingTree: false,
      orgTypeValueLabelMap,
      showOrgTypeSelect: false,
      showOrgSelect: false,
      breadcrumb: [{ label: "全部", value: "1" }],
      selectedParentKey: undefined,
      treeData: [],
      showSearchInput: false,
      searchWord: "",
      dragSelectedList: [],
    };
  },
  beforeCreate() {},
  created() {
    this.onSearchChange = debounce(this.onSearchChange.bind(this), 300);
    if (this.orgUuid) {
      this.currentOrgUuid = this.orgUuid;
      if (this.selectOrgType == "MyOrg") {
        this.showSearchInput = true;
      }
    }
    this.getOrgPromise = Promise.resolve(this.currentOrgUuid != undefined);
  },
  beforeMount() {
    this.fetchOrgElementModel();
    //TODO: 后续默认的系统相关特性由后端统一输出到前台
    if (this.currentOrgUuid === undefined && this.orgVersionId === undefined) {
      // 获取当前系统的默认组织
      this.getOrgSelectOptions();
      this.getOrg(() => {
        if (this.value != undefined || this.value != "" || this.value.length > 0) {
          this.setValue(this.value);
        }
        this.fetchOrgTree();
      });
    } else if (this.currentOrgUuid != undefined) {
      if (this.value != undefined || this.value != "" || this.value.length > 0) {
        this.setValue(this.value);
      }
    }
  },
  mounted() {},
  methods: {
    onCheckAll() {
      debugger;
      this.checkAll = !this.checkAll;
      if (this.userIndexedList) {
        for (let key in this.letterUserNodeMap) {
          let j = this.checkedKeys.checked.indexOf(key);
          if (this.checkAll) {
            if (j != -1) {
              continue;
            }
            this.checkedKeys.checked.push(key);
            this.selectedNodes.push(this.letterUserNodeMap[key]);
          } else {
            if (j == -1) {
              continue;
            }
            this.removeSelected(key);
          }
        }
      } else {
        for (let n of this.selectOrgTypeControl.currentLevelTreeData) {
          let j = this.checkedKeys.checked.indexOf(n.key);
          if (this.checkAll) {
            if (j != -1) {
              continue;
            }
            this.checkedKeys.checked.push(n.key);
            this.selectedNodes.push(n);
          } else {
            if (j == -1) {
              continue;
            }
            this.removeSelected(n.key);
          }
        }
      }
    },
    onClickLetterUser(e) {
      let { select, item } = e;
      let key = item.id;
      let i = this.checkedKeys.checked.indexOf(key);
      if (i != -1) {
        this.removeNodeChecked(key);
        this.removeSelected(key);
      } else {
        this.checkedKeys.checked.push(key);
        this.selectedNodes.push(this.letterUserNodeMap[key]);
      }
    },
    updateLetterUserGroup(params) {
      const _this = this;
      this.loading = true;
      this.letterUserNodeMap = {};
      let setLetterGroup = (users) => {
        let letterGroup = [];
        for (let i = 0, length = _this.letters.length; i < length; i++) {
          // 字母按顺序添加
          letterGroup.push({
            letter: _this.letters[i].toUpperCase(),
            data: [],
          });
        }

        users.forEach((d) => {
          if (d.data.pinYin) {
            let pinYin = d.data.pinYin.trim();
            let c = pinYin.charAt(0);
            let index = _this.letters.indexOf(c);
            if (index == -1) {
              index = _this.letters.length;
            }

            let u = {
              name: d.title,
              id: d.key,
              subName: _this.displayNodeSubTitle(d),
              checked: _this.checkedKeys.checked.includes(d.key),
            };
            _this.letterUserNodeMap[d.key] = d;
            letterGroup[index].data.push(u);
          }
        });
        this.letterUserGroup = letterGroup;
        console.log("用户字母列表", this.letterUserGroup);
        this.loading = false;
        // this.updateUserViewAllCheckboxState();
      };
      this.searchInputPlaceholder = undefined;
      if (this.selectOrgTypeControl.asyncUser && this.selectOrgTypeControl.asyncUser.has(this.currentOrgUuid)) {
        // 只会展示第一页 100 个数据，其他数据由点击字母或者搜索加载
        // this.searchInputPlaceholder = "请输入用户姓名或者拼音搜索更多";
        this.fetchOrgTypeUsers(
          this.selectOrgType,
          Object.assign({}, { pageIndex: 0, pageSize: 100 }, params || {})
        ).then(({ users }) => {
          setLetterGroup(users);
        });
        return;
      }

      let users = [],
        userIds = [];
      let filterUser = (list) => {
        if (list && list.length > 0) {
          for (let d of list) {
            if (d.data && d.data.type === "user") {
              if (
                !userIds.includes(d.key) &&
                ((d.data.pinYin &&
                  (_this.searchWord == undefined ||
                    _this.searchWord == "" ||
                    d.data.pinYin.indexOf(_this.searchWord)) != -1) ||
                  d.title.indexOf(_this.searchWord) != -1)
              ) {
                users.push(d);
                userIds.push(d.key);
              }
            }
          }
          // 遍历下一级
          for (let d of list) {
            if (d.children) {
              filterUser(d.children);
            }
          }
        }
      };
      filterUser(this.selectOrgTypeControl.treeData);

      setLetterGroup(users);
    },
    onUserIndexedListToggle() {
      this.userIndexedList = !this.userIndexedList;
      this.searchWord = "";
      this.checkAll = false;
      this.showOrgTypeSelect = false;
      this.showOrgSelect = false;
      if (this.userIndexedList) {
        this.updateLetterUserGroup();
        this.showSearchInput = true;
      } else {
        this.showSearchInput = this.selectOrgType == "MyOrg" && this.orgUuid;
      }
    },
    onRemoveDragSelected(row) {
      let dragList = this.$refs.dragSorts.dragList;
      for (let i = 0, len = dragList.length; i < len; i++) {
        if (dragList[i].key == row.key) {
          this.$refs.dragSorts.splice(i, 1);
          break;
        }
      }
    },
    onDragConfirm(e) {
      let { index, moveTo, list } = e;
      this.dragSortedConfirmList = list;
    },
    onConfirmSelectedEdit() {
      if (this.dragSortedConfirmList != undefined) {
        this.selectedNodes.splice(0, this.selectedNodes.length);
        this.selectedNodes.push(...this.dragSortedConfirmList);
      }
      this.orgSelectedPopupToggle();
    },
    orgSelectedPopupToggle() {
      let popup = this.$refs.orgSelectedPopup;
      if (popup.showPopup) {
        popup.close();
      } else {
        popup.open();
        this.dragSelectedList = JSON.parse(JSON.stringify(this.selectedNodes));
        this.dragSortedConfirmList = undefined;
      }
    },

    onClickPopup() {
      this.showOrgSelect = false;
      this.showOrgTypeSelect = false;
    },
    displayNodeSubTitle(node) {
      if (node.titlePath) {
        let paths = node.titlePath.split("/");
        paths.reverse().splice(0, 1);
        return paths.splice(0, 2).join(" - ");
      }
      return "";
    },
    onSearchChange(value) {
      let v = value.trim();
      this.checkAll = false;
      if (v != "") {
        if (this.userIndexedList) {
          this.updateLetterUserGroup();
          return;
        }
        this.loading = true;
        new Promise((resolve, reject) => {
          if (
            this.selectOrgTypeControl.async === true ||
            (this.selectOrgTypeControl.asyncOrg && this.selectOrgTypeControl.asyncOrg.has(this.currentOrgUuid))
          ) {
            // 判断当前选择的组织类型下是否异步加载
            let results = [];
            let filterData = (list) => {
              if (list && list.length > 0) {
                for (let i = 0, len = list.length; i < len; i++) {
                  let item = list[i];
                  if (
                    item.title.toLowerCase().indexOf(v.toLowerCase()) > -1 ||
                    (item.type == "user" &&
                      item.data &&
                      ((item.data.pinYin && item.data.pinYin.indexOf(v) != -1) ||
                        (item.data.loginName && item.data.loginName.indexOf(v) != -1)))
                  ) {
                    results.push(item);
                  }
                  if (item.children && item.children.length > 0) {
                    filterData(item.children);
                  }
                }
              }
            };
            filterData(this.selectOrgTypeControl.treeData);
            resolve(results);
          } else {
            // 搜索
            this.requestFetchOrgTypeData(this.selectOrgType, Object.assign({}, this.params, { keyword: v })).then(
              (map) => {
                resolve(map.dataList || []);
              }
            );
          }
        }).then((dataList) => {
          this.breadcrumb.splice(1, this.breadcrumb.length - 1);
          this.breadcrumb.push({
            label: `包含“${v}”的搜索结果`,
            index: this.breadcrumb.length,
            value: "2",
          });
          console.log("返回搜索结果", dataList);
          this.selectOrgTypeControl.searchResults = dataList;
          this.selectOrgTypeControl.searchView = true;
          this.loading = false;
        });
      } else {
        this.breadcrumb.splice(1, this.breadcrumb.length - 1);
        if (this.userIndexedList) {
          this.updateLetterUserGroup();
          return;
        }
        this.selectOrgTypeControl.currentLevelTreeData = this.selectOrgTypeControl.treeData;
      }
    },
    onSearchCancel() {
      this.breadcrumb.splice(1, this.breadcrumb.length - 1);
      console.log("onSearchCancel", arguments);
      this.switchSearchMode(false);
      this.searchWord = "";
    },
    onSearchClear() {},
    switchSearchMode(input) {
      this.showSearchInput = input;
    },
    onChangeOrgType() {
      this.showOrgTypeSelect = !this.showOrgTypeSelect;
      this.showOrgSelect = false;
      if (this.selectOrgType == "MyOrg" && this.orgUuid) {
        this.showSearchInput = true;
      } else {
        this.showSearchInput = false;
      }
    },
    onConfirm() {
      this.valueNodes.splice(0, this.valueNodes.length);
      this.valueNodes.push(...this.selectedNodes);
      this.$emit("popusConfirm", { nodes: this.valueNodes, label: this.valueLabels, value: this.value });
      this.emitValueChange();
      this.closePopup();
    },
    isHalfChecked(node) {
      let childrenChecked = false;
      for (let i = 0, len = this.checkedKeys.checked.length; i < len; i++) {
        if (this.checkedKeys.checked[i].indexOf(node.key) != -1) {
          childrenChecked = true;
          break;
        }
      }
      return childrenChecked && this.checkedKeys.checked.includes(node.key);
    },
    removeSelected(n) {
      let key = typeof n == "string" ? n : n.key;
      for (let i = 0, len = this.selectedNodes.length; i < len; i++) {
        if (this.selectedNodes[i].key == key) {
          this.selectedNodes.splice(i, 1);
          if (this.checkedKeys.checked.includes(key)) {
            this.checkedKeys.checked.splice(this.checkedKeys.checked.indexOf(key), 1);
          }
          break;
        }
      }
    },
    onSelectOrgNode(node, siblings) {
      if (node.checkable) {
        if (this.checkedKeys.checked.includes(node.key)) {
          this.checkedKeys.checked.splice(this.checkedKeys.checked.indexOf(node.key), 1);
          this.removeSelected(node);
          let siblingsChecked = false;
          for (let i = 0, len = siblings.length; i < len; i++) {
            if (this.checkedKeys.checked.includes(siblings[i].key)) {
              siblingsChecked = true;
              break;
            }
          }
          if (!siblingsChecked) {
            this.checkedKeys.halfChecked.splice(this.checkedKeys.halfChecked.indexOf(node.parentKey), 1);
          }
        } else {
          this.checkedKeys.checked.push(node.key);
          this.selectedNodes.push(node);
          if (node.keyPath) {
            let paths = node.keyPath.split("/");
            for (let p of paths) {
              if (!this.checkedKeys.checked.includes(p) && !this.checkedKeys.halfChecked.includes(p)) {
                this.checkedKeys.halfChecked.push(p);
              }
            }
          }
        }
      } else {
        this.$refs.toast.show({
          duration: 1000,
          type: "default",
          message: "不可选",
        });
      }
    },
    jumpToTreeLevelNode(bread, index) {
      this.checkAll = false;
      if (index != this.breadcrumb.length - 1) {
        this.breadcrumb.splice(index + 1, this.breadcrumb.length - index - 1);
        if (bread.value == "1") {
          this.selectOrgTypeControl.currentLevelTreeData = this.selectOrgTypeControl.treeData;
        } else {
          this.selectOrgTypeControl.currentLevelTreeData = bread.refTreeNodes;
        }
      }
    },
    toNextLevelChildren(node) {
      this.checkAll = false;
      let promise = new Promise((resolve, reject) => {
        if (node.children == null && !node.isLeaf && this.selectOrgTypeControl.async) {
          this.$set(node, "childrenLoading", true);
          // 异步获取下级节点数据
          this.requestFetchOrgTypeData(
            this.selectOrgType,
            Object.assign({}, this.params, {
              parentKey: node.key,
              checkedKeys: this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys,
            })
          ).then((map) => {
            let { dataList, allKeys, checkableKeys, halfCheckedKeys } = map;
            this.$set(node, "children", dataList);
            this.$set(node, "childrenLoading", false);
            resolve();
          });
        } else {
          resolve();
        }
      });
      promise.then(() => {
        this.selectOrgTypeControl.currentLevelTreeData = node.children;
        this.breadcrumb.push({
          label: node.title,
          value: node.key,
          index: this.breadcrumb.length,
          refTreeNodes: this.selectOrgTypeControl.currentLevelTreeData,
        });
      });
    },
    fetchOrgTree(callback) {
      if (this.selectOrgType) {
        let _this = this;
        // 当前导航，如果获取过、组织和版本id值与原先一致，该组织异步请求过，则不再请求接口获取数据
        if (
          this.selectOrgTypeControl.fetched &&
          this.selectOrgTypeControl.currentOrgUuid == this.currentOrgUuid &&
          this.selectOrgTypeControl.selectedOrgVersionId == this.selectedOrgVersionId &&
          !(this.selectOrgTypeControl.asyncOrg && this.selectOrgTypeControl.asyncOrg.has(this.currentOrgUuid))
        ) {
          this.loading = false;
          this.selectOrgTypeControl.currentLevelTreeData = this.selectOrgTypeControl.treeData;
          if (typeof callback === "function") {
            callback.call(this);
          }
          return;
        }
        this.loading = true;
        this.requestFetchOrgTypeData(
          this.selectOrgType,
          Object.assign({}, this.params, {
            checkedKeys: this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys,
          })
        ).then((map) => {
          let { dataList, allKeys, checkableKeys, halfCheckedKeys, selectOrgType } = map;
          if (this.selectOrgType != selectOrgType) {
            // 左侧导航切换后，可能不在接口返回的类别里
            _this.orgTypeControl[selectOrgType].treeData.splice(0, _this.orgTypeControl[selectOrgType].treeData.length);
            _this.orgTypeControl[selectOrgType].treeData = dataList;
            _this.orgTypeControl[selectOrgType].currentLevelTreeData = dataList;
            _this.orgTypeControl[selectOrgType].treeKeys = {
              checkable: Array.from(checkableKeys),
              all: Array.from(allKeys),
            };
            _this.orgTypeControl[selectOrgType].fetched = true;
            _this.orgTypeControl[selectOrgType].currentOrgUuid = this.currentOrgUuid;
            _this.orgTypeControl[selectOrgType].selectedOrgVersionId = this.selectedOrgVersionId;
          } else {
            _this.selectOrgTypeControl.treeData.splice(0, _this.selectOrgTypeControl.treeData.length);
            _this.selectOrgTypeControl.treeData = dataList;
            _this.selectOrgTypeControl.currentLevelTreeData = dataList;
            _this.selectOrgTypeControl.treeKeys = { checkable: Array.from(checkableKeys), all: Array.from(allKeys) };

            if (halfCheckedKeys != undefined && halfCheckedKeys.size) {
              _this.checkedKeys.halfChecked.push(...Array.from(halfCheckedKeys));
            }
            _this.selectOrgTypeControl.fetched = true;
            _this.selectOrgTypeControl.currentOrgUuid = this.currentOrgUuid;
            _this.selectOrgTypeControl.selectedOrgVersionId = this.selectedOrgVersionId;

            // 非异步加载情况下，统计节点下的用户数目
            if (
              !(
                _this.selectOrgTypeControl.async == true ||
                (_this.selectOrgTypeControl.asyncOrg &&
                  _this.selectOrgTypeControl.asyncOrg.has(_this.currentOrgUuid)) ||
                (_this.selectOrgTypeControl.asyncUser && _this.selectOrgTypeControl.asyncUser.has(_this.currentOrgUuid))
              )
            ) {
              // underSerCount
            }
          }
          _this.loading = false;
          if (typeof callback === "function") {
            callback.call(_this);
          }
        });
      }
    },

    requestFetchOrgTypeData(selectOrgType, params) {
      return new Promise((resolve, reject) => {
        const currentOrgUuid = this.currentOrgUuid;
        let reqParams = Object.assign(
          {
            orgUuid: this.currentOrgUuid,
            orgVersionId: this.selectedOrgVersionId || this.orgVersionId,
          },
          params
        );
        if (selectOrgType == "MyOrg" && this.orgTypeControl.MyOrg.async) {
          // 我的组织强制切换为异步拉取数据
          reqParams.async = true;
        }
        this.$axios.post(`/api/org/organization/fetchOrgTree/${selectOrgType}`, reqParams).then(({ data, headers }) => {
          console.log("查询组织树数据返回：", data);
          if (headers["force-query-async"] == "true") {
            // 后端会根据相关数据决定是否强制切换为异步检索数据
            this.orgTypeControl[selectOrgType].asyncOrg.add(currentOrgUuid);
          }
          if (headers["force-user-query-async"] == "true") {
            // 后端辉根据相关数据决定是否强制切换为异步检索用户数据
            this.orgTypeControl[selectOrgType].asyncUser.add(currentOrgUuid);
          }

          if (data.code == 0) {
            let allKeys = new Set(),
              checkableKeys = new Set(),
              halfCheckedKeys = new Set();
            if (data.data) {
              let checkableTypes = this.checkableTypesOfOrgType[selectOrgType] || this.checkableTypes || [],
                checkableStrictly = checkableTypes.length > 0; // 如果有传第可选类型的节点，则按该类型严格限制可选的节点
              let uncheckableTypes = this.uncheckableTypes || [];
              // 组织弹窗中显示职位节点/层级
              let dataList = data.data || [];
              // 批量设置图标、标题插槽
              let nodeFilter = (array) => {
                if (array) {
                  for (let i = 0, len = array.length; i < len; i++) {
                    allKeys.add(array[i].key);
                    if (checkableStrictly && array[i].type) {
                      array[i].checkable = checkableTypes.includes(array[i].type);
                    }
                    if (uncheckableTypes.length) {
                      array[i].checkable = !uncheckableTypes.includes(array[i].type);
                    }
                    if (array[i].checkable) {
                      checkableKeys.add(array[i].key);
                    }
                    if (array[i].halfChecked) {
                      halfCheckedKeys.add(array[i].key);
                    }

                    if (array[i].children && array[i].children.length == 0) {
                      array[i].isLeaf = true;
                    }
                    // 排序
                    if (array[i].children) {
                      array[i].children = orderBy(
                        array[i].children,
                        (d) => {
                          return d.data ? d.data.seq : 0;
                        },
                        ["asc"]
                      );

                      nodeFilter(array[i].children);
                    }
                  }
                }
              };
              nodeFilter(dataList);
              resolve({ dataList, allKeys, checkableKeys, halfCheckedKeys, selectOrgType });
            } else {
              resolve({ dataList: [], allKeys, checkableKeys, halfCheckedKeys, selectOrgType });
            }
          }
        });
      }).catch(() => {});
    },
    getOrg(callback) {
      let _this = this;
      let request = () => {
        _this.getOrgPromise = new Promise((resolve, reject) => {
          _this.$axios
            .get(`/api/org/organization/version/published`, { params: { system: this._$SYSTEM_ID } })
            .then(({ data }) => {
              resolve(data.data);
            })
            .catch(() => {});
        });
        return _this.getOrgPromise;
      };
      let _callback = (data) => {
        if (typeof callback === "function") {
          if (data) {
            _this.currentOrgUuid = data.organization.uuid;
          }
          callback.call(_this);
        }
      };
      request().then((d) => {
        _callback(d);
      });
    },
    getOrgSelectOptions() {
      let request = () => {
        return new Promise((resolve, reject) => {
          this.$axios
            .get(`/api/org/organization/queryEnableOrgs`, { params: { system: this._$SYSTEM_ID } })
            .then(({ data }) => {
              if (data.data) {
                resolve(data.data);
              }
            })
            .catch((error) => {});
        });
      };

      let callback = (list) => {
        for (let i = 0, len = list.length; i < len; i++) {
          this.orgSelectOptions.push({
            label: list[i].name,
            value: list[i].uuid,
          });
        }
      };
      request().then((list) => {
        callback(list);
      });
    },
    onSelectOrg(org) {
      if (this.loading) {
        return;
      }
      if (this.currentOrgUuid != org.value) {
        this.checkAll = false;
        this.currentOrgUuid = org.value;
        this.showOrgSelect = false;
        this.breadcrumb.splice(1, this.breadcrumb.length - 1);
        this.fetchOrgTree();
      }
    },
    onSelectOrgType(opt) {
      if (this.loading) {
        return;
      }
      if (opt.value != this.selectOrgType) {
        this.checkAll = false;
        this.selectOrgType = opt.value;
        this.showOrgTypeSelect = false;
        this.fetchOrgTree();
      }
    },
    getSetting() {
      let _this = this;
      return new Promise((resolve, reject) => {
        if (_this.setting == null) {
          let callback = (list) => {
            _this.setting = {};
            for (let i = 0, len = list.length; i < len; i++) {
              _this.setting[list[i].attrKey] = list[i];
              _this.setting[list[i].attrKey].attrValueJson = list[i].attrVal ? JSON.parse(list[i].attrVal) : {};
            }
            console.log("查询组织参数设置: ", _this.setting);
            if (_this.setting) {
              if (_this.setting["ORG_DIALOG_SELECT_PREVIEW"]) {
                _this.previewUserUnderNode = _this.setting["ORG_DIALOG_SELECT_PREVIEW"].enable;
              }
              if (_this.setting["ORG_DIALOG_JOB_LEVEL_VISIBLE"]) {
                _this.jobLevelVisible = _this.setting["ORG_DIALOG_JOB_LEVEL_VISIBLE"].enable;
              }
            }

            resolve();
          };
          let request = () => {
            return new Promise((resolve, reject) => {
              _this.$axios
                .get("/api/org/elementModel/queryOrgSetting", { params: { system: this._$SYSTEM_ID } })
                .then(({ data }) => {
                  if (data.code == 0 && data.data) {
                    resolve(data.data);
                  }
                });
            });
          };

          request().then((list) => {
            callback(list);
          });
        } else {
          resolve();
        }
      });
    },
    fetchOrgTypeUsers(selectOrgType, params, page) {
      return new Promise((resolve, reject) => {
        this.$axios
          .post(
            `/api/org/organization/fetchOrgTreeUser/${selectOrgType}`,
            Object.assign(
              {
                orgUuid: this.currentOrgUuid,
                orgVersionId: this.selectedOrgVersionId || this.orgVersionId,
              },
              params || {},
              this.params
            )
          )
          .then(({ data, headers }) => {
            console.log("查询组织类型下的用户返回：", data);
            if (data.code == 0 && data.data) {
              if (headers["force-query-async"] == "true") {
              }
              resolve({ users: data.data.nodes || [], total: data.data.total });
            }
          })
          .catch(() => {
            // _this.$message.error("组织服务异常");
          });
      });
    },
    fetchOrgElementModel() {
      let _this = this;
      let request = () => {
        return new Promise((resolve, reject) => {
          _this.$axios
            .get("/api/org/elementModel/getAllOrgElementModels", { params: { system: this._$SYSTEM_ID } })
            .then(({ data }) => {
              if (data.code == 0 && data.data) {
                resolve(data.data);
              }
            });
        });
      };
      let callback = (results) => {
        for (let i = 0, len = results.length; i < len; i++) {
          if (results[i].enable) {
            _this.orgElementIcon[results[i].id] = results[i].icon;
          }
        }
      };
      request().then((results) => {
        callback(results);
      });
    },
    showPopup() {
      if (this.readonly || this.disable || this.textonly) {
        return;
      }
      this.$refs.orgPopup.open();
      this.selectedNodes = this.valueNodes ? JSON.parse(JSON.stringify(this.valueNodes)) : [];
      this.selectOrgType = this.selectedOrgTypeKeys.length > 0 ? this.selectedOrgTypeKeys[0] : undefined;
      this.loading = true;
      this.userIndexedList = false;
      this.checkAll = false;
      this.getSetting();
      if (this.selectedNodes.length > 0) {
        for (let n of this.selectedNodes) {
          this.checkedKeys.checked.push(n.key);
        }
      }
      if (this.getOrgPromise) {
        this.getOrgPromise.then(() => {
          this.fetchOrgTree();
        });
      }
    },
    fetchNodesByKeys(keys, orgTypes) {
      let _this = this;
      return new Promise((resolve, reject) => {
        this.$axios
          .post(`/api/org/organization/fetchOrgTreeNodesByTypeKeys`, {
            orgUuid: this.currentOrgUuid,
            orgVersionId: this.selectedOrgVersionId || this.orgVersionId,
            orgTypes,
            keys,
          })
          .then(({ data }) => {
            console.log("查询组织节点数据返回：", data);
            resolve(data.data);
          })
          .catch(() => {});
      });
    },
    initCheckedValue() {
      if (
        this.orgTypeOptions.length > 0 &&
        ((this.checkStrictly && this.checkedKeys.checked.length > 0) || this.checkedKeys.length > 0)
      ) {
        let keys = [].concat(this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys),
          orginalKeys = [].concat(keys);
        let types = [];
        this.orgTypeOptions.forEach((o) => {
          types.push(o.value);
        });
        this.selectedNodes.splice(0, this.selectedNodes.length);
        this.fetchNodesByKeys(keys, types).then((nodes) => {
          this.valueLoading = false;
          if (nodes && nodes.length > 0) {
            this.selectedNodes = uniqBy(nodes, (item) => item.key); //this.isPathValue ? 'keyPath' : 'key'
            if (this.selectedNodes.length == keys.length) {
              keys = [];
              this.selectedNodes = orderBy(
                this.selectedNodes,
                (d) => {
                  return orginalKeys.indexOf(d.key);
                },
                ["asc"]
              );

              this.valueNodes = JSON.parse(JSON.stringify(this.selectedNodes));
            }
          }
        });
      }
    },
    closePopup() {
      this.$refs.orgPopup.close();
      this.searchWord = "";
      this.breadcrumb.splice(1, this.breadcrumb.length - 1);
      this.selectedNodes.splice(0, this.selectedNodes.length);
      this.checkedKeys.checked.splice(0, this.checkedKeys.checked.length);
      this.selectOrgTypeControl.currentLevelTreeData = this.selectOrgTypeControl.treeData;
      this.selectOrgTypeControl.searchResults.splice(0, this.selectOrgTypeControl.searchResults.length);
      this.$emit("popusClose", this.valueNodes);
    },
    removeNodeChecked(n) {
      let checked = this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys;
      let j = checked.indexOf(n.key);
      if (j != -1) {
        checked.splice(j, 1);
      }
      j = checked.indexOf(n.keyPath);
      if (j != -1) {
        checked.splice(j, 1);
      }
    },
    closeTag(node) {
      this.removeNodeChecked(this.valueNodes[i]);
      for (let i = 0, len = this.valueNodes.length; i < len; i++) {
        if (this.valueNodes[i].key == node.key) {
          this.valueNodes.splice(i, 1);
          break;
        }
      }
      this.emitValueChange();
    },
    closeTagGroup(nodes) {
      let del = [];
      for (let i = 0, len = nodes.length; i < len; i++) {
        del.push(nodes[i].key);
      }
      for (let i = 0; i < this.valueNodes.length; i++) {
        if (del.includes(this.valueNodes[i].key)) {
          this.removeNodeChecked(this.valueNodes[i]);
          this.valueNodes.splice(i--, 1);
        }
      }

      this.emitValueChange();
    },
    onClearInput() {
      this.valueNodes.splice(0, this.valueNodes.length);
      this.checkedKeys.checked.splice(0, this.checkedKeys.checked.length);
      this.checkedKeys.halfChecked.splice(0, this.checkedKeys.halfChecked.length);
      this.selectedNodes.splice(0, this.selectedNodes.length);
      this.emitValueChange();
    },
    emitValueChange() {
      let values = [];
      for (let i = 0, len = this.valueNodes.length; i < len; i++) {
        let key = this.valueNodes[i].key;
        if (this.isPathValue && this.valueNodes[i].keyPath) {
          // 值以全路径返回
          key = this.valueNodes[i].keyPath;
        }
        values.push(key);
      }
      this.$emit(
        "input",
        typeof this.value === "string" || this.value == undefined ? values.join(this.separator) : values
      );
      let string = typeof this.value === "string" || this.value == undefined; // 以字符方式返回
      this.$emit("change", {
        value: string ? values.join(this.separator) : values,
        label: string ? this.getValueLabel() : this.valueLabels,
        nodes: this.valueNodes,
      });
    },
    getValueLabel() {
      return this.valueLabels.join(this.separator);
    },
    setValue(v) {
      if (v != undefined && v.length > 0) {
        let _value = v;
        if (typeof v === "string" && this.multiSelect) {
          let separator = new RegExp(";|,|" + this.separator);
          _value = v.split(separator);
        }
        if (this.checkStrictly) {
          this.checkedKeys.checked = _value;
        } else {
          this.checkedKeys = _value;
        }

        if (!this.currentOrgUuid) {
          this.getOrg(() => {
            this.initCheckedValue();
          });
        } else {
          this.initCheckedValue();
        }
      } else {
        this.onClearInput();
      }
    },
  },

  watch: {
    value: {
      handler(v, o) {
        let checked = this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys;
        if (v == undefined || v == null) {
          v = "";
        }
        if (typeof v === "string" && this.multiSelect) {
          let separator = new RegExp(";|,|" + this.separator);
          let _value = [].concat(v.split(separator));
          checked.splice(0, checked.length);
          checked.push(..._value);
        } else if (Array.isArray(v)) {
          let _value = [].concat(v);
          checked.splice(0, checked.length);
          checked.push(..._value);
        }
        if (checked.length == 0) {
          this.valueNodes.splice(0, this.valueNodes.length);
          this.selectedNodes.splice(0, this.selectedNodes.length);
          return;
        }
        if (this.valueNodes.length != checked.length) {
          this.initCheckedValue();
        }
      },
    },
  },
};
</script>
