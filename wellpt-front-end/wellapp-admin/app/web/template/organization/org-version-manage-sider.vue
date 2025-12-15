<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 50 }">
    <a-row type="flex" style="margin-bottom: 10px">
      <a-col flex="auto">
        <a-input-search v-model="searchWord" @change="onChangeSearchWord" @search="onSearch" allow-clear :loading="searchState == 1" />
      </a-col>
    </a-row>
    <!-- <a-menu style="width: 100%; border-right: 0px" mode="vertical" @click="menuClick" v-model="selectedMenuKeys">
      <a-menu-item key="allUser">
        <Iconn type="pticon iconfont icon-ptkj-zuzhirenshi" />
        全部人员
        <span class="">{{total}}</span>
      </a-menu-item>
      <a-menu-item key="quitUser">
        <a-icon type="user-delete" />
        离职人员
      </a-menu-item>
    </a-menu> -->
    <div class="spin-center" v-if="searchState == 1">
      <a-spin />
    </div>
    <div
      v-if="!orgElementManageLimited"
      class="org-type-menu handler"
      :class="searchResultSelectedId == 'allUser' ? 'selected' : undefined"
      @click="menuClick({ key: 'allUser' })"
    >
      <Icon type="pticon iconfont icon-ptkj-zuzhirenshi" />
      全部人员
      <span class="count">{{ total }}</span>
    </div>
    <div class="org-type-menu">
      <Icon type="pticon iconfont icon-ptkj-danweijituanzidanwei" />
      组织
      <a-dropdown v-if="editable">
        <a-menu slot="overlay" @click="rootMenuClick">
          <template v-for="(item, i) in orgElementModels">
            <a-menu-item :key="'addSubOrgElement_' + item.id" :element-type="item.id" v-if="item.unAddable !== true">
              {{ item.id == 'unit' ? '添加' : '新建' }}{{ item.name }}
            </a-menu-item>
          </template>

          <!-- <a-menu-item key="addOutterOrg" elementtype="outterOrg">添加外部组织</a-menu-item> -->
          <a-menu-item key="batchCreateOrgElement">
            <div style="width: calc(100% - 25px); position: absolute; border-top: 1px solid #e8e8e8; margin-top: -5px"></div>
            批量创建
          </a-menu-item>
          <a-menu-item key="translateAll">翻译全部组织节点</a-menu-item>
          <a-menu-item key="importOrgElement">导入</a-menu-item>
          <a-menu-item key="exportOrgElement">导出</a-menu-item>
        </a-menu>
        <a-button size="small" :style="{ float: 'right' }" title="添加" type="text">
          <Icon type="iconfont icon-ptkj-gengduocaozuo"></Icon>
        </a-button>
      </a-dropdown>
    </div>

    <PerfectScrollbar style="height: calc(100vh - 215px); margin-right: -4px; padding-right: 12px">
      <a-tree
        v-show="!searchWord"
        :expandedKeys.sync="expandedTreeKeys"
        :selectedKeys.sync="selectedOrgTreeKeys"
        :tree-data="orgElementTreeData"
        class="ant-tree-directory tree-more-operations org-element-tree"
        :blockNode="true"
        :showIcon="true"
        @select="onSelectOrgNode"
        :key="treeKey"
        ref="treeList"
        draggable
        @drop="ondrop"
        @dragstart="ondragstart"
      >
        <template slot="nodeIcon" slot-scope="node">
          <Icon
            v-if="!(node.children && node.children.length)"
            :type="orgElementIcon[node.data.type] || 'iconfont icon-ptkj-zuzhiguanli'"
          />
        </template>
        <template slot="nodeTitle" slot-scope="node">
          <label
            class="title"
            :title="node.title"
            :style="{
              maxWidth: node.userCnt > 0 ? 'calc(100% - 86px)' : 'calc(100% - 60px)'
            }"
          >
            {{ node.title }}
            <a-tag color="red" v-if="orgElementModelMap[node.data.type] == undefined">不可用</a-tag>
          </label>
          <span class="count" :style="{}" v-show="node.userCnt > 0">
            {{ node.userCnt }}
          </span>
          <a-dropdown overlayClassName="org-version-tree-node-operation-dropdown" v-if="orgElementModelMap[node.data.type] != undefined">
            <a-button
              class="tree-ghost-btn"
              @click.stop="() => {}"
              size="small"
              :icon="node.loading ? 'loading' : undefined"
              type="text"
              :style="{ float: 'right' }"
            >
              <Icon v-if="!node.loading" type="iconfont icon-ptkj-gengduocaozuo"></Icon>
            </a-button>
            <a-menu slot="overlay" @click="e => onClickTreeNodeMenu(e, node)">
              <a-menu-item key="edit">{{ subNodeEditable(node, node.data.type) ? '编辑' : '查看' }}</a-menu-item>
              <a-menu-item key="del" v-if="subNodeDeleteable(node, node.data.type)">删除</a-menu-item>
              <template v-if="editable">
                <template v-for="(item, i) in orgElementModels">
                  <a-menu-item
                    :key="'addSubOrgElement_' + item.id"
                    :element-type="item.id"
                    v-show="
                      childAllowedMountType[node.data.type] &&
                      childAllowedMountType[node.data.type].includes(item.id) &&
                      subNodeTypeAddable(node, item.id)
                    "
                  >
                    新建{{ item.name }}
                  </a-menu-item>
                </template>
              </template>
            </a-menu>
          </a-dropdown>

          <!-- <span
            :style="{
              position: 'absolute',
              display: 'inline-block',
              width: '22px',
              fontSize: '11px',
              height: '16px',
              textAlign: 'center',
              color: 'inherit',
              outline: node.userCnt ? '1px solid #e8e8e8' : 'unset',
              borderRadius: '7px',
              lineHeight: '16px',
              right: '23px',
              'margin-top': '4px'
            }"
            :title="node.userCnt"
          >
            {{ node.userCnt > 99 ? '99+' : node.userCnt || ' ' }}
          </span> -->
        </template>
      </a-tree>
      <div v-show="searchWord && searchState == 3" class="org-tree-search-result pt-empty pt-search-empty">
        <template v-for="(item, i) in searchResult">
          <div
            @click="onClickSearchNode(item)"
            :key="'searchResult_' + i"
            :class="item.node.data.id == searchResultSelectedId ? 'selected' : undefined"
          >
            <div class="title" :title="item.fullTitle">
              <Icon :type="orgElementIcon[item.node.data.type] || 'iconfont icon-ptkj-zuzhiguanli'" />
              <span v-html="filterSearchTitle(item.node.title, searchWord)"></span>
              <a-tag color="red" v-if="orgElementModelMap[item.node.data.type] == undefined">不可用</a-tag>
            </div>
            <div class="sub-title" :title="item.pathName" v-if="item.parentNode != undefined">
              <Icon :type="orgElementIcon[item.parentNode.data.type] || ''" />
              {{ item.pathName }}
            </div>

            <a-dropdown v-if="orgElementModelMap[item.node.data.type] != undefined">
              <a-button
                class="tree-ghost-btn operation"
                @click.stop="() => {}"
                size="small"
                :icon="item.node.loading ? 'loading' : undefined"
                type="text"
              >
                <Icon v-if="!item.node.loading" type="iconfont icon-ptkj-gengduocaozuo"></Icon>
              </a-button>
              <a-menu slot="overlay" @click="e => onClickTreeNodeMenu(e, item.node)">
                <a-menu-item key="edit">{{ editable ? '编辑' : '查看' }}</a-menu-item>
                <a-menu-item key="del" v-if="editable">删除</a-menu-item>
                <template v-if="editable">
                  <template v-for="(mod, i) in orgElementModels">
                    <a-menu-item
                      :key="'addSubOrgElement_' + mod.id"
                      :element-type="mod.id"
                      v-show="childAllowedMountType[item.node.data.type] && childAllowedMountType[item.node.data.type].includes(mod.id)"
                    >
                      新建{{ mod.name }}
                    </a-menu-item>
                  </template>
                </template>
              </a-menu>
            </a-dropdown>
          </div>
        </template>
        <a-empty
          v-show="searchResult.length == 0"
          description="未找到任何结果"
          style="--pt-empty-image-width: 100px; --pt-empty-image-height: 65px"
        />
      </div>
    </PerfectScrollbar>

    <a-modal
      :title="orgElementModalTitle"
      :visible="orgElementModalVisible"
      :destroyOnClose="true"
      :maskClosable="false"
      @ok="onOkOrgElementModal"
      @cancel="onCancelOrgElementModal"
      :width="744"
      :bodyStyle="{ height: '460px', overflowY: 'auto' }"
      class="pt-modal"
      :okButtonProps="{
        style: {
          display: orgElementModalVisible && currentOrgElementEditable ? 'inline-block' : 'none'
        }
      }"
    >
      <OrgElement
        ref="orgElement"
        :type="orgElementType"
        :uuid="orgElementUuid"
        :parentUuid="orgElementParentUuid"
        :orgRoles="orgRoles"
        :orgElementModels="orgElementModels"
        :orgVersion="orgVersionDetails"
        :orgSetting="orgSetting"
        :orgElementTreeData="orgElementTreeData"
        :displayState="currentOrgElementEditable ? 'edit' : 'label'"
      />
    </a-modal>

    <a-modal
      title="批量导入组织"
      :visible="batchUploadModalVisible"
      :destroyOnClose="true"
      :maskClosable="false"
      :footer="null"
      @cancel="cancelBatchImport"
      :width="800"
      :bodyStyle="{ height: '380px', overflowY: 'auto' }"
      dialogClass="batchUploadOrgDataModal"
      class="pt-modal"
    >
      <a-row>
        <a-col :span="24">
          <div v-if="importResult.total == -1" class="import-container-box">
            <div style="color: var(--w-text-color-darker); font-size: var(--w-font-size-lg); line-height: 32px">等待导入组织</div>
            <div class="import-box" v-show="importResult.total == -1">
              <div class="title pt-title-vertical-line">下载导入模版</div>
              <div>
                <Icon type="file-excel" style="color: var(--w-success-color)"></Icon>
                导入模板.xlsx
                <a-button type="link" :loading="downloading" @click.stop="clickDownloadImportTptFile">
                  <Icon type="pticon iconfont icon-ptkj-xiazai"></Icon>
                  下载
                </a-button>
              </div>
            </div>
            <div class="import-box" v-if="importResult.total == -1">
              <div class="title pt-title-vertical-line">
                上传数据
                <span style="color: var(--w-text-color-light); font-size: var(--w-font-size-base)">请按照导入模版，填充数据后上传</span>
              </div>
              <a-upload-dragger name="file" :multiple="false" :showUploadList="false" :customRequest="customRequest" accept=".xls,.xlsx">
                <p class="ant-upload-drag-icon" style="margin-bottom: 0">
                  <Icon type="pticon iconfont icon-ptkj-shangchuan" style="font-size: 40px; color: var(--w-text-color-light)" />
                </p>
                <p class="ant-upload-hint" style="padding: 0 150px">
                  将自定义文件拖拽至此区域或
                  <a-button type="link" size="small">选择文件</a-button>
                  支持格式：xls、xlsx
                </p>
              </a-upload-dragger>
            </div>
          </div>
          <a-result
            v-else
            :status="importResult.total != -1 ? 'success' : 'info'"
            :title="importResult.total != -1 ? '导入操作完成!' : '等待导入组织'"
          >
            <template slot="subTitle">
              <span v-show="importResult.total != -1">
                导入结果: 总数 {{ importResult.total }} / 成功
                <span style="color: var(--w-success-color)">{{ importResult.success }}</span>
                条 / 失败
                <span style="color: var(--w-danger-color)">{{ importResult.total - importResult.success }}</span>
                条
              </span>
            </template>
            <template #extra>
              <a-upload name="file" :multiple="false" :showUploadList="false" :customRequest="customRequest" accept=".xls,.xlsx">
                <a-button :disabled="uploading" type="primary">
                  <a-icon type="upload" />
                  上传文件
                </a-button>
              </a-upload>
              <a-button v-show="importResult.total != -1" key="buy" @click.stop="clickDownloadImportResult">下载导入结果.xlsx</a-button>
            </template>
          </a-result>
        </a-col>
      </a-row>
    </a-modal>
    <Modal
      title="批量创建组织节点"
      v-model="batchCreateDrawerVisible"
      :destroyOnClose="true"
      width="calc(100% - 200px)"
      mask
      maxHeight="calc(-200px + 100vh)"
      :bodyStyle="{ height: 'calc(-200px + 100vh)' }"
      centered
    >
      <template slot="content">
        <OrgBatchCreateOrgElement
          :orgElementModels="orgElementModels"
          :orgElementTreeData="orgElementTreeData"
          :orgVersion="orgVersionDetails"
          :orgSetting="orgSetting"
          @orgElementChange="onBatchCreateOrgElement"
          ref="orgBatchCreateOrgElementRef"
        />
      </template>
      <template slot="footer">
        <a-button @click="onBatchCreateOrgElementCancel">取消</a-button>
        <a-button type="danger" @click="onBatchCreateOrgElementClearAll">清空</a-button>
        <a-button type="primary" @click="onBatchCreateOrgElementConfirm">创建</a-button>
      </template>
    </Modal>
  </a-skeleton>
</template>
<style lang="less" scoped>
.org-type-menu {
  padding: 8px 4px 8px 20px;
  color: var(--w-text-color-dark);
  font-size: var(--w-font-size-base);
  border-radius: 4px;
  font-weight: bold;

  > i {
    font-size: var(--w-font-size-base);
  }

  &.handler {
    &:hover {
      background: var(--w-primary-color-1);
      cursor: pointer;
    }
  }

  .count {
    color: var(--w-text-color-light);
    position: relative;
    padding-left: var(--w-padding-3xs);
    font-weight: normal;
    &::before {
      position: absolute;
      content: '';
      width: 1px;
      height: var(--w-font-size-base);
      left: 0;
      top: 50%;
      margin-top: e('calc(0px -  var(--w-font-size-base)/2)');
      background: var(--w-text-color-light);
    }
  }

  &.selected {
    color: var(--w-primary-color);
    background: var(--w-primary-color-1);
    .count {
      color: var(--w-primary-color);
    }
  }
}
.ant-tree-title {
  .title {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    display: inline-block;
    vertical-align: middle;
  }
  .count {
    color: var(--w-text-color-light);
    position: relative;
    padding-left: var(--w-padding-3xs);
    vertical-align: middle;
    &::before {
      position: absolute;
      content: '';
      width: 1px;
      height: var(--w-font-size-base);
      left: 0;
      top: 50%;
      margin-top: e('calc(0px -  var(--w-font-size-base)/2)');
      background: var(--w-text-color-light);
    }
  }
}
.ant-tree-node-content-wrapper {
  &:hover {
    .ant-btn-background-ghost.ant-btn-link {
      color: var(--w-button-font-color-hover);
    }
  }
}
</style>
<style lang="less">
.org-element-tree.tree-more-operations {
  --w-tree-level-padding-left: 20px;

  li.drag-over > span[draggable] {
    background-color: unset !important;
  }
  &.ant-tree-block-node li .ant-tree-node-content-wrapper {
    margin-left: 4px;
    // width: e('calc(100% - 38px)');
  }
  .ant-tree-switcher {
    margin-left: 20px;
    position: relative;
    &.ant-tree-switcher_close,
    &.ant-tree-switcher_open {
      > .ant-tree-switcher-icon {
        display: none;
      }
      &::after {
        content: '';
        position: absolute;
        width: 14px;
        height: 14px;
        background: url(/static/svg/folder-close.svg) no-repeat center;
        top: 50%;
        margin-top: 4px;
      }
    }
    &.ant-tree-switcher_open {
      &::after {
        background: url(/static/svg/folder-open.svg) no-repeat center;
      }
    }
    &.ant-tree-switcher-noop {
      display: none;
      + .ant-tree-node-content-wrapper {
        margin-left: 20px;
        width: e('calc(100% + 8px)');
      }
    }
  }
}
.org-tree-search-result {
  > div {
    position: relative;
    padding: 10px 32px;
    border-radius: 4px;
    cursor: pointer;
    &.selected {
      background-color: var(--w-primary-color-1);
    }
    &:hover {
      background-color: var(--w-primary-color-1);
    }
    > .title {
      font-size: var(--w-font-size-base);
      color: var(--w-text-color-dark);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    > .sub-title {
      font-size: var(--w-font-size-sm);
      color: var(--w-text-color-light);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    > .operation {
      position: absolute;
      right: 4px;
      top: 50%;
      margin-top: -12px;
    }
  }
}
.batchUploadOrgDataModal {
  .ant-result-info .ant-result-icon > .anticon {
    color: var(--w-primary-color);
  }
  .ant-result-title {
    font-size: var(--w-font-size-lg);
    color: #000000;
    font-weight: bold;
  }
  .ant-result-subtitle {
    font-size: 14px;
    color: var(--w-text-color-light);
  }
  .import-container-box {
    width: 80%;
    margin: 0 auto;
  }
  .import-box {
    text-align: left;
    border-radius: 4px;
    background: var(--w-gray-color-2);
    padding: 12px 20px;
    .title {
      line-height: 32px;
      font-weight: bold;
      font-size: var(--w-font-size-base);
      color: var(--w-text-color-dark);
    }
    margin-top: 12px;
  }
  .ant-upload.ant-upload-drag {
    background: #ffffff;
  }
}
</style>
<script type="text/babel">
import OrgElement from './org-element.vue';
import { debounce, sortBy, filter } from 'lodash';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import OrgBatchCreateOrgElement from './org-batch-create-org-element.vue';
import { deepClone } from '@framework/vue/utils/util';
export default {
  name: 'OrgVersionManageSider',
  inject: ['pageContext', 'orgElementManagementMap', 'orgElementManageLimited'],
  props: {
    orgVersionUuid: String,
    orgSetting: Object,
    displayState: {
      type: String,
      default: 'edit' // edit、label
    }
  },
  components: { OrgElement, Drawer, OrgBatchCreateOrgElement, Modal },
  data() {
    return {
      loading: true,
      uuid: this.orgVersionUuid,
      orgVersionDetails: {},
      orgElementTreeData: [],
      setting: {},
      orgRoles: [],
      jobOptions: [],
      searchWord: undefined,
      selectedMenuKeys: ['allUser'],
      treeKey: 'orgElementTreeData',
      searchResult: [],
      searchResultSelectedId: undefined,

      expandedTreeKeys: [],
      selectedOrgTreeKeys: [],
      total: 0,
      orgElementModalTitle: undefined,
      orgElementModalVisible: false,
      labelCol: { span: 4 },
      wrapperCol: { span: 19 },
      orgElementDetails: {},
      orgElementIcon: {},
      orgElementModels: [],
      orgElementType: undefined,
      orgElementUuid: undefined,
      orgElementParentUuid: undefined,
      treeKeyNodeMap: {},
      selectedTreeKeys: [],

      childAllowedMountType: {},
      parentAllowedMountType: {},
      batchUploadModalVisible: false,
      uploading: false,
      importResult: { total: -1, success: 0, result: undefined },
      downloading: false,
      expandAll: false,
      batchCreateDrawerVisible: false,
      onlyTranslateEmpty: true,
      translatingAll: false,
      searchState: 0
    };
  },
  computed: {
    orgElementModelMap() {
      let map = {};
      this.orgElementModels.forEach(model => {
        map[model.id] = model;
      });
      console.log('orgElementModelMap', map);
      return map;
    },
    editable() {
      return this.displayState == 'edit';
    },
    currentOrgElementEditable() {
      if (this.orgElementManagementMap[this.orgElementUuid]) {
        // 管理节点不允许编辑
        return false;
      }
      if (
        this.editable &&
        this.orgElementUuid != undefined &&
        this.treeKeyNodeMap[this.orgElementUuid] &&
        this.orgElementManagementMap[this.orgElementUuid] == undefined
      ) {
        let node = this.treeKeyNodeMap[this.orgElementUuid];
        let orgAuthority = this.treeKeyNodeMap[this.orgElementUuid].orgAuthority;
        if (orgAuthority) {
          return orgAuthority[node.data.type] && orgAuthority[node.data.type].includes('edit');
        }
      }
      return this.editable;
    }
  },
  watch: {},
  provide() {
    return {
      childAllowedMountType: this.childAllowedMountType,
      parentAllowedMountType: this.parentAllowedMountType
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getOrgVersionDetails();
    this.fetchOrgElementModel();
  },
  mounted() {
    let _this = this;
    this.pageContext.handleEvent('refreshUserCountStatics', function () {
      _this.getUserCountStatics(() => {});
    });
    this.pageContext.handleEvent('orgVersionManageSiderRefresh', function () {
      _this.getOrgVersionDetails();
    });
  },
  methods: {
    onOkOrgElementModal() {
      if (this.currentOrgElementEditable) {
        this.saveOrgElement();
      } else {
        this.orgElementModalVisible = false;
        this.orgElementUuid = undefined;
      }
    },
    onCancelOrgElementModal() {
      this.orgElementModalVisible = false;
      this.orgElementUuid = undefined;
    },
    onBatchCreateOrgElement() {
      this.$emit('orgElementChange');
    },
    onBatchCreateOrgElementClearAll() {
      this.$refs.orgBatchCreateOrgElementRef.clearAll();
    },
    onBatchCreateOrgElementConfirm() {
      this.$refs.orgBatchCreateOrgElementRef.createOrgElement();
    },
    onBatchCreateOrgElementCancel() {
      this.batchCreateDrawerVisible = false;
    },
    filterSearchTitle(title, searchWord) {
      let arr = title.split(searchWord);
      return `${arr[0]}<span style="color:var(--w-danger-color)">${searchWord}</span>${arr[1]}`;
    },
    clearSelectData() {
      this.searchResultSelectedId = undefined;
      this.orgElementIdSelected = undefined;
      this.orgRoleUuidSelected = undefined;
      this.selectedMenuKeys.splice(0, this.selectedMenuKeys.length);
      this.selectedOrgTreeKeys.splice(0, this.selectedOrgTreeKeys.length);
      this.selectedTreeKeys.splice(0, this.selectedTreeKeys.length);
    },
    onClickSearchNode(item) {
      let { node, parentNode } = item;
      let _parent = this.treeKeyNodeMap[node.parentKey];
      let pathName = [node.data.name];
      while (_parent) {
        pathName.push(_parent.title);
        _parent = this.treeKeyNodeMap[_parent.parentKey];
      }
      this.clearSelectData();
      let data = {
        orgElementId: node.data.id,
        orgRoleUuid: undefined,
        title: pathName.reverse().join(' / ')
      };
      this.$emit('selected', data);
      this.searchResultSelectedId = node.data.id;
      this.orgElementIdSelected = node.data.id;
      this.selectedOrgTreeKeys.push(node.data.uuid);
    },
    onSelectOrgNode(selectedKeys, { node }) {
      let item = node.dataRef;
      let _selectedKeys = deepClone(selectedKeys);
      let _parent = this.treeKeyNodeMap[item.parentKey];
      let pathName = [item.data.name];
      while (_parent) {
        pathName.push(_parent.title);
        _parent = this.treeKeyNodeMap[_parent.parentKey];
      }
      this.clearSelectData();
      let data = {
        orgElementId: item.data.id,
        orgRoleUuid: undefined,
        userCnt: item.userCnt,
        title: pathName.reverse().join(' / ')
      };
      this.$emit('selected', data);
      this.orgElementIdSelected = data.orgElementId;
      this.searchResultSelectedId = data.orgElementId;
      this.selectedOrgTreeKeys = _selectedKeys;
    },

    expandAllNode() {
      this.expandAll = !this.expandAll;
      this.expandedTreeKeys.splice(0, this.expandedTreeKeys.length);
      if (this.expandAll) {
        this.expandedTreeKeys.push(...Object.keys(this.treeKeyNodeMap));
      }
    },
    onClickRoleTreeNodeMenu(e, scope) {
      if (!scope.dataRef) {
        scope.dataRef = scope;
      }
      scope.dataRef.menuVisible = false;
      if (e.key == 'del') {
        this.orgRole.uuid = scope.key;
        this.deleteOrgRole();
      } else if (e.key == 'edit') {
        this.orgRole = {
          orgVersionUuid: this.uuid,
          orgUuid: this.orgUuid,
          uuid: scope.key,
          name: scope.title,
          remark: scope.dataRef.remark,
          id: scope.dataRef.id
        };
        this.orgRoleModalVisible = true;
      }
    },
    onClickTreeNodeMenu(e, scope) {
      this.orgElementModalTitle = e.domEvent.target.textContent;
      this.orgElementType = e.domEvent.target.attributes['element-type'] ? e.domEvent.target.attributes['element-type'].value : undefined;
      // scope.dataRef.menuVisible = false;
      if (e.key == 'del') {
        let _this = this;
        this.$confirm({
          title: `确认要删除 [ ${scope.title} ] 吗?`,
          content: undefined,
          onOk() {
            _this.$set(scope, 'loading', true);
            _this.deleteOrgElement(scope.key, scope);
          },
          onCancel() {}
        });
      } else if (e.key == 'edit') {
        this.orgElementModalVisible = true;
        this.orgElementUuid = scope.key;
        this.orgElementType = scope.data.type;
      } else if (e.key.indexOf('addSubOrgElement_') != -1) {
        this.orgElementModalVisible = true;
        this.orgElementUuid = undefined;
        this.orgElementParentUuid = scope.key;
      }
    },
    onSelectRole(item) {
      this.clearSelectData();
      this.$emit('selected', {
        orgElementId: undefined,
        orgRoleUuid: item.key,
        title: item.title,
        userCnt: item.userCnt
      });
      this.orgRoleUuidSelected = item.key;
      this.selectedTreeKeys.push(item.key);
    },
    onSelectRoleTreeNode(keys, e) {
      if (keys && keys[0] != 'orgRoleTree_0000') {
        let _selectedKeys = deepClone(keys);
        this.clearSelectData();
        if (!e.selected) {
          this.selectedTreeKeys.push(e.node.dataRef.key);
          // this.menuClick({ key: 'allUser' });
          return;
        }
        this.$emit('selected', {
          orgElementId: undefined,
          orgRoleUuid: _selectedKeys[0],
          title: e.node.dataRef.title,
          userCnt: e.node.dataRef.userCnt
        });
        this.orgRoleUuidSelected = _selectedKeys[0];
        this.selectedTreeKeys = _selectedKeys;
      }
    },

    onTreeMouseleave(e) {
      e.node.dataRef.hover = false;
    },
    onTreeMouseenter(e) {
      e.node.dataRef.hover = true;
    },

    deleteOrgElement(uuid, item) {
      $axios
        .get('/proxy/api/org/organization/version/deleteOrgElement', { params: { uuid } })
        .then(({ data }) => {
          this.$set(item, 'loading', false);
          if (data.code == 0) {
            this.$message.success('删除成功');
            this.$emit('orgElementChange');
            // 删除子节点
            let node = this.treeKeyNodeMap[uuid];
            let delChildren = node => {
              if (node.children && node.children.length) {
                for (let i = 0, len = node.children.length; i < len; i++) {
                  delChildren(item.children[i]);
                  delete this.treeKeyNodeMap[node.children[i].key];
                }
              }
            };
            delChildren(node);

            // 移除当前节点
            let pkey = this.treeKeyNodeMap[uuid].parentKey;
            let children = pkey ? this.treeKeyNodeMap[pkey].children : this.orgElementTreeData;
            for (let i = 0, len = children.length; i < len; i++) {
              if (children[i].key === uuid) {
                children.splice(i, 1);
                break;
              }
            }

            if (this.searchWord && this.searchResult.length) {
              // 当前搜索面板内删除
              for (let i = 0; i < this.searchResult.length; i++) {
                if (this.searchResult[i].node.key === uuid) {
                  this.searchResult.splice(i, 1);
                  break;
                }
              }
            }
            this.getUserCountStatics(() => {});
          } else {
            this.$message.success('删除失败');
          }
        })
        .catch(() => {
          this.$set(item, 'loading', false);
        });
    },
    clickDownloadImportTptFile() {
      this.downloading = true;
      let _this = this;
      import('@framework/vue/lib/xlsx/xlsx.mjs').then(XLSX => {
        _this.downloading = false;
        let SheetNames = [],
          Sheets = {};
        _this.orgElementModels.forEach(m => {
          SheetNames.push(m.name);
          Sheets[m.name] = XLSX.utils.json_to_sheet([{ 名称: null, 简称: null, 编号: null, 上级: null, 备注: null }]);
        });

        let workbook = {
          SheetNames,
          Sheets
        };
        XLSX.write(workbook, { bookType: 'xlsx', bookSST: true, type: 'base64' });
        XLSX.writeFile(workbook, '导入模板.xlsx');
      });
    },
    clickDownloadImportResult() {
      let _this = this;
      import('@framework/vue/lib/xlsx/xlsx.mjs').then(XLSX => {
        let result = _this.importResult.result,
          sheetDatas = [];
        for (let k in result) {
          if (!sheetDatas[result[k].result.sheetName]) {
            sheetDatas[result[k].result.sheetName] = [];
          }
          sheetDatas[result[k].result.sheetName].push({
            名称: result[k].name,
            简称: result[k].shortName,
            编号: result[k].code,
            上级: result[k].parent,
            备注: result[k].reamrk,
            导入结果: result[k].result.success ? '成功' : '失败',
            失败原因: result[k].result.msg,
            row: result[k].result.row
          });
        }

        // 重排序
        for (let s in sheetDatas) {
          sheetDatas[s] = sortBy(sheetDatas[s], o => {
            return o.row;
          });

          sheetDatas[s].forEach(d => {
            delete d.row;
          });
        }

        let SheetNames = [],
          Sheets = {};
        for (let s in sheetDatas) {
          SheetNames.push(s);

          Sheets[s] = XLSX.utils.json_to_sheet(sheetDatas[s]);
        }

        let workbook = {
          SheetNames,
          Sheets
        };
        XLSX.write(workbook, { bookType: 'xlsx', bookSST: true, type: 'base64' });
        XLSX.writeFile(workbook, '下载导入结果.xlsx');
      });
    },
    cancelBatchImport() {
      this.batchUploadModalVisible = false;
      this.importResult = {
        total: -1,
        result: undefined,
        success: 0
      };
    },
    customRequest(options) {
      let _this = this;
      this.uploading = true;
      let file = options.file;
      console.log(file);
      let formData = new FormData();
      // this.orgElementModels.forEach(m => {
      //   types.push(m.id);
      // });
      // formData.set('types', id);
      let headers = {
        'Content-Disposition': `attachment; filename="${encodeURIComponent(file.name)}"`,
        'Content-Type': 'multipart/form-data'
      };
      formData.set('file', file);
      _this.$loading('导入中...');
      $axios
        .post(`/proxy/api/org/organization/import/${this.uuid}`, formData, {
          headers: headers
        })
        .then(({ data }) => {
          _this.$loading(false);
          console.log(data);
          _this.uploading = false;
          // TODO:提示上传结果
          _this.getOrgVersionDetails();
          // 导入结果解析:
          _this.importResult.total = 0;
          _this.importResult.success = 0;
          for (let name in data.data) {
            let d = data.data[name];
            if (d.result.row > 0) {
              _this.importResult.total++;
              _this.importResult.success = _this.importResult.success + (d.result.success ? 1 : 0);
            }
          }

          _this.importResult.result = data.data;
        })
        .catch(function (error) {
          _this.$loading(false);
          console.error('上传文件失败, 异常: ', error);
        });
    },

    rootMenuClick({ key, item, domEvent }) {
      if (key.indexOf('addSubOrgElement_') != -1) {
        this.orgElementType = item.$el.attributes['element-type'].value;
        this.orgElementModalVisible = true;
        this.orgElementModalTitle = domEvent.target.textContent;
        this.orgElementUuid = undefined;
        this.orgElementParentUuid = undefined;
      } else if (key === 'importOrgElement') {
        this.batchUploadModalVisible = true;
      } else if (key === 'batchCreateOrgElement') {
        this.batchCreateDrawerVisible = true;
      } else if (key == 'translateAll') {
        this.translateAllTreeNode();
      }
    },
    translateAllTreeNode() {
      let _this = this;
      this.$confirm({
        title: '确定要翻译全部组织节点吗?',
        content: h => (
          <div>
            <a-checkbox checked={_this.onlyTranslateEmpty} onChange={e => (_this.onlyTranslateEmpty = e.target.checked)}>
              仅翻译未进行翻译的文字内容
            </a-checkbox>
          </div>
        ),
        onOk() {
          _this.translatingAll = true;
          _this.$loading('翻译中...');
          _this.$axios
            .get(`/proxy/api/org/organization/version/element/translateAll`, {
              params: { orgVersionUuid: _this.uuid, onlyTranslateEmpty: _this.onlyTranslateEmpty }
            })
            .then(({ data }) => {
              _this.translatingAll = false;
              _this.$loading(false);
              if (data.code == 0) {
                _this.$message.success('翻译成功');
              } else {
                _this.$message.error('翻译异常');
              }
            })
            .catch(error => {
              _this.$message.error('翻译异常');
              _this.$loading(false);
            });
        },
        onCancel() {}
      });
    },
    menuClick(e) {
      if (e.key === 'allUser') {
        this.$emit('selected', {
          orgElementId: undefined,
          orgRoleUuid: undefined,
          userCnt: this.total,
          title: '全部人员'
        });
        this.clearSelectData();
        this.searchResultSelectedId = 'allUser';
      }
    },
    onChangeSearchWord: debounce(function (v) {
      this.onSearch();
    }, 500),
    onSearch(v) {
      // this.searchResultSelectedId = undefined;
      this.searchState = 0;
      if (this.searchWord != undefined && this.searchWord.trim() != '') {
        this.searchState = 1;
        setTimeout(() => {
          this.searchResult.splice(0, this.searchResult.length);
          for (let k in this.treeKeyNodeMap) {
            let node = this.treeKeyNodeMap[k];
            if (node.hidden) {
              continue;
            }
            if (node.title.indexOf(this.searchWord) != -1) {
              let _node = {
                node,
                parentNode: this.treeKeyNodeMap[node.parentKey]
              };
              this.searchResult.push(_node);
              let fullTitle = node.title;
              if (node.parentKey) {
                let _parent = this.treeKeyNodeMap[node.parentKey],
                  pathName = [this.treeKeyNodeMap[node.parentKey].data.name];
                while (_parent) {
                  pathName.push(_parent.title);
                  _parent = this.treeKeyNodeMap[_parent.parentKey];
                }
                _node.pathName = pathName.join(' / ');
                fullTitle = pathName.reverse().join(' / ') + node.title;
              }
              _node.fullTitle = fullTitle;
            }
          }

          this.$nextTick(() => {
            this.searchState = 3;
          });
        }, 500);
      } else {
        this.searchResult.splice(0, this.searchResult.length);
      }
    },
    saveOrgElement() {
      this.$refs.orgElement.save().then(node => {
        this.$emit('orgElementChange');
        this.onOrgElementSave(node);
      });
    },
    onOrgElementSave(node) {
      let originalNode = this.treeKeyNodeMap[node.uuid];
      let updateOrgUserPath = false;
      if (!originalNode) {
        this.$loading(false);
        let treeNode = {
          key: node.uuid,
          id: node.id,
          title: node.name,
          label: node.name,
          data: node,
          parentKey: node.parentUuid,
          hover: false,
          menuVisible: false,
          scopedSlots: { title: 'nodeTitle', icon: 'nodeIcon' }
        };
        if (node.parentUuid) {
          // 查找父节点加入其下
          let parent = this.treeKeyNodeMap[node.parentUuid];
          if (parent.children == undefined) {
            this.$set(parent, 'children', []);
          }
          parent.children.push(treeNode);
          if (parent.orgAuthority) {
            treeNode.orgAuthority = parent.orgAuthority;
          }
          if (parent.userAuthority) {
            treeNode.userAuthority = parent.userAuthority;
          }
          treeNode.level = parent.level + 1;
          if (!this.expandedTreeKeys.includes(node.parentUuid)) {
            this.expandedTreeKeys.push(node.parentUuid);
          }
        } else {
          this.orgElementTreeData.push(treeNode);
        }
        this.treeKeyNodeMap[treeNode.key] = treeNode;
      } else {
        // originalNode.title = `${node.name}${originalNode.userCnt != undefined ? ` - ${originalNode.userCnt}人` : ''}`;
        originalNode.label = node.name;
        originalNode.data = node;
        originalNode.title = node.name;

        // 父级点发生变更
        let oldParentUuid = originalNode.parentKey;
        let newParent = node.parentUuid ? this.treeKeyNodeMap[node.parentUuid] : null;
        if (node.parentUuid == oldParentUuid) {
          this.orgElementModalVisible = false;
          return;
        }
        updateOrgUserPath = true;
        if (oldParentUuid && oldParentUuid != node.parentUuid) {
          // 从旧的移出
          let oldParentChildren = this.treeKeyNodeMap[oldParentUuid].children;
          for (let i = 0, len = oldParentChildren.length; i < len; i++) {
            if (oldParentChildren[i].key === node.uuid) {
              oldParentChildren.splice(i, 1);
              break;
            }
          }
        } else if (!oldParentUuid) {
          // 从顶层移出
          for (let i = 0, len = this.orgElementTreeData.length; i < len; i++) {
            if (this.orgElementTreeData[i].key == node.uuid) {
              this.orgElementTreeData.splice(i, 1);
              break;
            }
          }
        }

        if (newParent && oldParentUuid != node.parentUuid) {
          // 移入新的父节点
          if (newParent.children === undefined) {
            this.$set(newParent, 'children', []);
          }
          newParent.children.push(originalNode);
          if (newParent.orgAuthority) {
            originalNode.orgAuthority = newParent.orgAuthority;
          }
          if (newParent.userAuthority) {
            originalNode.userAuthority = newParent.userAuthority;
          }
          originalNode.level = newParent.level + 1;
          originalNode.parentKey = node.parentUuid;
        } else if (!newParent) {
          // 移到了顶层
          originalNode.parentKey = undefined;
          originalNode.level = 1;
          this.orgElementTreeData.push(originalNode);
        }
      }

      if (node.parentUuid && !this.expandedTreeKeys.includes(node.parentUuid)) {
        this.expandedTreeKeys.push(node.parentUuid);
      }
      this.orgElementModalVisible = false;
      if (updateOrgUserPath) {
        this.$loading('更新组织用户中...');
        $axios
          .get(`/proxy/api/org/organization/updateOrgUserUnderOrgElement`, { params: { orgElementUuid: node.uuid } })
          .then(({ data }) => {
            this.$loading(false);
          })
          .catch(error => {
            this.$loading(false);
            this.$message.error('更新组织用户异常');
          });
      }
    },
    setChildAllowedMountType() {
      if (Object.keys(this.orgSetting).length > 0 && this.orgElementModels.length > 0) {
        for (let i = 0, len = this.orgElementModels.length; i < len; i++) {
          let m = this.orgElementModels[i].id,
            s = this.orgSetting[`${m.toUpperCase()}_MOUNT_SET`];
          if (s && s.enable) {
            let attrVal = s.attrVal ? JSON.parse(s.attrVal) : {};
            if (attrVal.modelIds && attrVal.modelIds.length) {
              if (this.parentAllowedMountType[m] == undefined) {
                this.$set(this.parentAllowedMountType, m, attrVal.modelIds);
              }
              attrVal.modelIds.forEach(id => {
                if (this.childAllowedMountType[id] == undefined) {
                  this.$set(this.childAllowedMountType, id, []);
                }
                this.childAllowedMountType[id].push(m);
              });
            }
          }
        }
      }
      console.log('允许挂载节点数据：', this.childAllowedMountType);
    },

    initOrgElementTreeData() {
      this.treeKeyNodeMap = {};
      this.expandedTreeKeys.splice(0, this.expandedTreeKeys.length);
      this.orgElementTreeData = [];
      if (this.orgVersionDetails.orgElements) {
        // 递归
        let treeData = [],
          _this = this,
          elements = JSON.parse(JSON.stringify(this.orgVersionDetails.orgElements));
        console.log('查询组织版本详情: ', elements);
        for (let i = 0, len = elements.length; i < len; i++) {
          let ele = elements[i],
            node = {
              key: ele.uuid,
              title: ele.name,
              // name: ele.name,
              id: ele.id,
              label: ele.name,
              data: ele,
              hover: false,
              userCnt: 0,
              menuVisible: false,
              parentKey: ele.parentUuid,
              children: [],
              scopedSlots: { title: 'nodeTitle', icon: 'nodeIcon' }
            };
          this.treeKeyNodeMap[node.key] = node;
        }
        let emptyManagementKeys = Object.keys(this.orgElementManagementMap),
          visibleKeys = new Set(Object.keys(this.orgElementManagementMap));
        for (let key in this.treeKeyNodeMap) {
          let n = this.treeKeyNodeMap[key];
          if (emptyManagementKeys.length == 0 || visibleKeys.has(key) || visibleKeys.has(n.parentKey)) {
            if (n.parentKey) {
              let p = this.treeKeyNodeMap[n.parentKey];
              p.children.push(n);
              if (emptyManagementKeys.length > 0 && !visibleKeys.has(n.parentKey)) {
                treeData.push(n);
              }
            } else {
              treeData.push(n);
            }
            visibleKeys.add(key);
          } else {
            n.hidden = true;
          }
        }

        // level 设置 / 排序
        let setLevel = (nodes, level) => {
          for (let i = 0, len = nodes.length; i < len; i++) {
            nodes[i].level = level;
            let orgElementAuthority = nodes[i].orgAuthority,
              userAuthority = nodes[i].userAuthority;
            if (this.orgElementManagementMap && this.orgElementManagementMap[nodes[i].key]) {
              if (this.orgElementManagementMap[nodes[i].key].orgAuthority) {
                orgElementAuthority = this.orgElementManagementMap[nodes[i].key].orgAuthority;
                nodes[i].orgAuthority = orgElementAuthority;
              }

              if (this.orgElementManagementMap[nodes[i].key].userAuthority) {
                userAuthority = this.orgElementManagementMap[nodes[i].key].userAuthority;
                nodes[i].userAuthority = userAuthority;
              }
            }
            nodes[i].children = sortBy(nodes[i].children, function (o) {
              if (orgElementAuthority) {
                o.orgAuthority = orgElementAuthority;
              }
              if (userAuthority) {
                o.userAuthority = userAuthority;
              }
              return o.data.seq;
            });
            this.treeKeyNodeMap[nodes[i].key] = nodes[i];

            setLevel(nodes[i].children, level + 1);
          }
        };
        treeData = sortBy(treeData, function (o) {
          return o.data.seq;
        });
        setLevel(treeData, 1);

        if (treeData.length && treeData[0].children && treeData[0].children.length) {
          this.expandedTreeKeys.splice(0, this.expandedTreeKeys.length);
          this.expandedTreeKeys.push(treeData[0].key);
        }

        console.log(treeData);
        this.orgElementTreeData.splice(0, this.orgElementTreeData.length);
        this.orgElementTreeData.push(...treeData);
        this.treeKey = 'orgElementTree_' + new Date().getTime();
        this.$nextTick(() => {
          if (this.orgElementManageLimited) {
            // 选中第一个管理节点
            let orgElementId = treeData[0].id,
              orgRoleUuid = undefined,
              title = treeData[0].title;
            this.$emit('selected', { orgElementId, orgRoleUuid, title, refresh: false });
            this.orgElementIdSelected = orgElementId;
            this.searchResultSelectedId = orgElementId;
            this.selectedOrgTreeKeys.push(treeData[0].key);
          }
        });
      }
    },

    getOrgVersionDetails() {
      let _this = this;
      $axios.get(`/proxy/api/org/organization/version/details`, { params: { orgUuid: this.orgUuid, uuid: this.uuid } }).then(({ data }) => {
        console.log('查询返回组织版本详情: ', data.data);
        if (data.code == 0 && data.data) {
          _this.uuid = data.data.uuid;
          _this.orgUuid = data.data.orgUuid;
          _this.getUserCountStatics();
          _this.orgRoles = data.data.orgRoles;
          _this.orgVersionDetails = data.data;
          _this.initOrgElementTreeData();
        }
        _this.loading = false;
      });
    },

    fetchOrgElementModel() {
      let _this = this;
      $axios.get('/proxy/api/org/elementModel/getAllOrgElementModels', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          let models = {},
            selfDefModels = [];
          for (let i = 0, len = data.data.length; i < len; i++) {
            if (data.data[i].enable) {
              models[data.data[i].id] = data.data[i];
              _this.orgElementIcon[data.data[i].id] = data.data[i].icon;
              // 自定义的
              if (!['unit', 'dept', 'job'].includes(data.data[i].id)) {
                selfDefModels.push(data.data[i]);
              }
            }
          }
          _this.orgElementModels.push(models['unit']);
          _this.orgElementModels.push(models['dept']);
          _this.orgElementModels.push(models['job']);
          _this.orgElementModels = _this.orgElementModels.concat(selfDefModels);
          _this.setChildAllowedMountType();
          _this.filterOrgElementModelsUnCreate();
        }
      });
    },
    save() {
      this.$refs.orgElement.save();
    },

    vLableWidth(scope) {
      return 195 - (scope.level - 1) * 18 + 'px';
    },
    getUserCountStatics(callback) {
      let _this = this;
      $axios.get('/proxy/api/org/organization/version/countAllUserCount', { params: { orgVersionUuid: this.uuid } }).then(({ data }) => {
        if (data.code == 0 && data.data) {
          _this.total = data.data;
          if (!_this.orgElementManageLimited) {
            let orgElementId = _this.orgElementIdSelected,
              orgRoleUuid = _this.orgRoleUuidSelected,
              title = '全部人员';
            _this.$emit('selected', { orgElementId, orgRoleUuid, title, userCnt: _this.total, refresh: false });
            _this.searchResultSelectedId = 'allUser';
          }

          if (typeof callback === 'function') {
            callback.call(_this);
          }
        }
      });
    },
    ondragstart(info) {
      let dropdowns = document.querySelectorAll('.org-version-tree-node-operation-dropdown');
      if (dropdowns.length > 0) {
        dropdowns.forEach(function (item) {
          item.style.display = 'none';
        });
      }
    },
    ondrop(info) {
      const dragParentPath = info.dragNode.pos.split('-').slice(0, -1).join('-');
      const dropParentPath = info.node.pos.split('-').slice(0, -1).join('-');

      if (dragParentPath !== dropParentPath) {
        this.$message.warning('只允许在同级节点间拖动');
        return;
      }

      const dropKey = info.node.eventKey;
      const dragKey = info.dragNode.eventKey;
      const dropPos = info.node.pos.split('-');
      const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);
      const loop = (data, key, callback) => {
        data.forEach((item, index, arr) => {
          if (item.key === key) {
            return callback(item, index, arr);
          }
        });
      };

      let dragObj;
      let data = info.node.dataRef.parentKey ? this.treeKeyNodeMap[info.node.dataRef.parentKey].children : this.orgElementTreeData;
      let originalUuids = [];
      for (let i = 0, len = data.length; i < len; i++) {
        originalUuids.push(data[i].key);
      }
      loop(data, dragKey, (item, index, arr) => {
        arr.splice(index, 1);
        dragObj = item;
      });

      let i;
      loop(data, dropKey, (item, index, arr) => {
        i = index;
      });
      if (dropPosition === -1) {
        data.splice(i, 0, dragObj);
      } else {
        data.splice(i + 1, 0, dragObj);
      }
      let list = [],
        sortedUuids = [];
      for (let j = 0, jlen = data.length; j < jlen; j++) {
        sortedUuids.push(data[j].key);
        list.push({
          uuid: data[j].key,
          seq: j + 1
        });
      }
      if (JSON.stringify(sortedUuids) !== JSON.stringify(originalUuids)) {
        $axios
          .post(`/proxy/api/org/organization/version/updateOrgElementSeq`, list)
          .then(({ data }) => {
            if (data.code == 0) {
              this.$message.success('保存排序成功');
            }
          })
          .catch(error => {
            this.$message.error('保存排序失败');
          });
      }
    },
    // getOrgRoles() {
    //   let _this = this;
    //   $axios.get('/proxy/api/org/organization/version/listRole', { params: { orgVersionUuid: this.uuid } }).then(({ data }) => {
    //     if (data.code == 0 && data.data) {
    //       _this.orgRoles = data.data;
    //     }
    //   });
    // }

    subNodeEditable(node, type) {
      let editable = this.editable;
      if (editable) {
        if (this.orgElementManagementMap[node.key]) {
          return false;
        }
        return node.orgAuthority ? node.orgAuthority[type] && node.orgAuthority[type].includes('edit') : editable;
      }
      return editable;
    },
    subNodeDeleteable(node, type) {
      let editable = this.editable;
      if (editable) {
        if (this.orgElementManagementMap[node.key]) {
          return false;
        }
        return node.orgAuthority ? node.orgAuthority[type] && node.orgAuthority[type].includes('delete') : editable;
      }
      return editable;
    },
    subNodeTypeAddable(node, type) {
      let editable = this.editable;
      if (editable && node.orgAuthority) {
        return node.orgAuthority[type] && node.orgAuthority[type].includes('add');
      }
      return editable;
    },
    filterOrgElementModelsUnCreate() {
      if (this.orgElementManagementMap != undefined && Object.keys(this.orgElementManagementMap).length > 0) {
        for (let m of this.orgElementModels) {
          m.unAddable = true;
          for (let key in this.orgElementManagementMap) {
            let mag = this.orgElementManagementMap[key];
            if (mag && mag.orgAuthority && mag.orgAuthority[m.id] && mag.orgAuthority[m.id].includes('add')) {
              m.unAddable = false;
              break;
            }
          }
        }
      }
    }
  }
};
</script>
