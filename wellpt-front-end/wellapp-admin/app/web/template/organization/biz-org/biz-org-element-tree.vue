<template>
  <a-skeleton active :loading="loading" :paragraph="{ rows: 50 }">
    <a-row type="flex" style="margin-bottom: 10px">
      <a-col flex="auto">
        <a-input-search v-model="searchWord" allow-clear @change="onSearchWordChange" />
      </a-col>
    </a-row>

    <div
      class="org-type-menu"
      @click="onSelectBizOrgNode"
      :style="{
        backgroundColor: selectedNode == undefined ? ' var(--w-tree-item-background-selected)' : 'unset'
      }"
    >
      <span>
        <a-avatar shape="square" :size="24" style="background-color: var(--w-primary-color); color: #fff; margin-right: 8px">
          <Icon slot="icon" :type="'iconfont icon-luojizujian-huoquyonghuliebiao'" />
        </a-avatar>
        {{ bizOrg.name }}
      </span>
      <a-dropdown>
        <a-menu slot="overlay" @click="rootMenuClick">
          <template v-if="bizOrgConfig.uuid">
            <a-menu-item
              v-if="bizOrgDimension.id != undefined"
              :key="'addSubOrgElement_' + bizOrgDimension.id"
              :is-dimension="true"
              :element-type="bizOrgDimension.id"
            >
              新建{{ bizOrgDimension.name }}
            </a-menu-item>
            <template v-if="!bizOrgConfig.enableSyncOrg">
              <template v-for="(item, i) in orgElementModels">
                <a-menu-item
                  v-if="bizOrgConfig.allowOrgEleModel.includes(item.id)"
                  :key="'addSubOrgElement_' + item.id"
                  :element-type="item.id"
                >
                  新建{{ item.name }}
                </a-menu-item>
              </template>
            </template>
            <a-menu-item key="syncOrgElement" v-if="bizOrgConfig.enableSyncOrg">从关联组织同步</a-menu-item>
            <!-- <a-menu-item key="importOrgElement" v-if="!bizOrgConfig.enableSyncOrg">批量导入</a-menu-item>
            <a-menu-item key="exportOrgElement" v-if="!bizOrgConfig.enableSyncOrg">批量导出</a-menu-item> -->
            <a-menu-item key="translateAll">翻译全部组织节点</a-menu-item>
          </template>
          <a-menu-item v-else><a-alert message="请初始化保存业务组织配置" banner type="info" /></a-menu-item>
        </a-menu>
        <a-button size="small" :style="{ float: 'right' }" title="添加" type="text" @click.native.stop="() => {}">
          <Icon type="iconfont icon-ptkj-gengduocaozuo"></Icon>
        </a-button>
      </a-dropdown>
    </div>
    <a-divider style="margin: 2px 0"></a-divider>
    <PerfectScrollbar style="max-height: calc(100vh - 200px); margin-right: -24px; padding-right: 23px">
      <a-tree
        v-show="!searchWord"
        :expandedKeys.sync="expandedTreeKeys"
        :selectedKeys.sync="selectedOrgTreeKeys"
        :tree-data="treeData"
        class="ant-tree-directory tree-more-operations"
        :blockNode="true"
        :showIcon="true"
        @select="onSelectOrgNode"
        :key="treeKey"
        ref="treeList"
        draggable
        @dragenter="onDragTreeNodeEnter"
        @drop="onDropTreeNode"
        style="--w-tree-item-padding-l: 8px; --w-tree-node-content-wrapper-padding: 0 5px 0 0"
      >
        <template slot="nodeIcon" slot-scope="node">
          <Icon
            :style="{ color: setIconColor(node.data.elementType), fontWeight: 'normal', fontSize: '16px' }"
            v-if="!node.data.isDimension"
            :type="orgElementIcon[node.data.elementType] || setIconType(node.data.elementType)"
          />
          <template v-else-if="bizOrgDimension.id !== undefined">
            <Icon
              :style="{ color: setIconColor(node.data.elementType) }"
              v-if="bizOrgDimension.icon != undefined"
              :type="bizOrgDimension.icon"
            />
            <label
              style="
                font-size: 11px;
                background: var(--w-primary-color);
                padding: 3px 4px;
                border-radius: 3px;
                text-align: center;
                color: #fff;
                margin-right: 4px;
              "
              v-else
            >
              {{ bizOrgDimension.name.charAt(0) }}
            </label>
          </template>
        </template>
        <template slot="nodeTitle" slot-scope="node">
          <label
            class="title"
            :title="node.title"
            :style="{
              maxWidth: node.userCnt > 0 ? 'calc(100% - 86px)' : 'calc(100% - 60px)'
            }"
          >
            <a-tag
              v-if="!node.data.enabled"
              class="pt-tag bordered danger"
              style="height: 17px; padding: 0 5px; font-size: 9px; margin: 0px; display: inline-flex; align-items: center"
            >
              已停用
            </a-tag>
            {{ node.title }}
          </label>
          <span class="count" :style="{}" v-show="node.userCnt > 0">
            {{ node.userCnt }}
          </span>
          <a-dropdown>
            <a-button @click.stop="() => {}" size="small" :icon="node.loading ? 'loading' : ''" type="text" :style="{ float: 'right' }">
              <Icon type="iconfont icon-ptkj-gengduocaozuo" v-if="!node.loading"></Icon>
            </a-button>
            <a-menu slot="overlay" @click="e => onClickTreeNodeMenu(e, node)">
              <a-menu-item key="edit">编辑</a-menu-item>
              <a-menu-item key="del">删除</a-menu-item>
              <a-menu-item
                v-if="bizOrgDimension.id != undefined && node.unAllowedCreateSubDimension !== true"
                :key="'addSubOrgElement_' + bizOrgDimension.id"
                :is-dimension="true"
                :element-type="bizOrgDimension.id"
              >
                新建{{ bizOrgDimension.name }}
              </a-menu-item>
              <template v-if="node.data.isDimension">
                <!-- 业务维度下允许新增的节点类型 -->
                <template v-if="!bizOrgConfig.enableSyncOrg && bizOrgConfig.allowOrgEleModel.length > 0">
                  <!-- 人工维护 -->
                  <template v-for="(item, i) in orgElementModels">
                    <a-menu-item
                      :key="'addSubOrgElement_' + item.id"
                      :element-type="item.id"
                      v-if="bizOrgConfig.allowOrgEleModel.includes(item.id)"
                    >
                      新建{{ item.name }}
                    </a-menu-item>
                  </template>
                </template>
              </template>
              <template v-else-if="!bizOrgConfig.enableSyncOrg && !node.unAllowedCreateSubOrgElement">
                <!-- 组织节点下允许新增的组织节点类型按组织设置的执行 -->
                <template v-for="(item, i) in orgElementModels">
                  <a-menu-item
                    :key="'addSubOrgElement_' + item.id"
                    :element-type="item.id"
                    v-if="
                      bizOrgConfig.allowOrgEleModel.includes(item.id) &&
                      childAllowedMountType[node.data.elementType] &&
                      childAllowedMountType[node.data.elementType].includes(item.id)
                    "
                  >
                    新建{{ item.name }}
                  </a-menu-item>
                </template>
              </template>
            </a-menu>
          </a-dropdown>
        </template>
      </a-tree>
      <div v-show="searchWord" class="org-tree-search-result pt-empty pt-search-empty">
        <template v-for="(node, i) in searchResult">
          <div
            @click="onClickSearchNode(node)"
            :key="'searchResult_' + i"
            :class="node.key == searchResultSelectedKey ? 'selected' : undefined"
          >
            <div class="title" :title="node.title">
              <Icon
                :style="{ color: setIconColor(node.data.elementType), fontWeight: 'normal', fontSize: '16px' }"
                v-if="!node.data.isDimension"
                :type="orgElementIcon[node.data.elementType] || setIconType(node.data.elementType)"
              />
              <template v-else-if="bizOrgDimension.id !== undefined">
                <Icon
                  :style="{ color: setIconColor(node.data.elementType) }"
                  v-if="bizOrgDimension.icon != undefined"
                  :type="bizOrgDimension.icon"
                />
                <label
                  style="
                    font-size: 11px;
                    background: var(--w-primary-color);
                    padding: 3px 4px;
                    border-radius: 3px;
                    text-align: center;
                    color: #fff;
                    margin-right: 4px;
                  "
                  v-else
                >
                  {{ bizOrgDimension.name.charAt(0) }}
                </label>
              </template>
              {{ node.title }}
            </div>
            <div class="sub-title" :title="node.pathName" v-if="node.pathName != undefined">
              {{ node.pathName }}
            </div>
            <a-dropdown>
              <a-button class="operation" @click.stop="() => {}" size="small" :icon="node.loading ? 'loading' : ''" type="text">
                <Icon type="iconfont icon-ptkj-gengduocaozuo" v-if="!node.loading"></Icon>
              </a-button>
              <a-menu slot="overlay" @click="e => onClickTreeNodeMenu(e, node)">
                <a-menu-item key="edit">编辑</a-menu-item>
                <a-menu-item key="del">删除</a-menu-item>
                <a-menu-item
                  v-if="bizOrgDimension.id != undefined && node.unAllowedCreateSubDimension !== true"
                  :key="'addSubOrgElement_' + bizOrgDimension.id"
                  :is-dimension="true"
                  :element-type="bizOrgDimension.id"
                >
                  新建{{ bizOrgDimension.name }}
                </a-menu-item>
                <template v-if="node.data.isDimension">
                  <!-- 业务维度下允许新增的节点类型 -->
                  <template v-if="!bizOrgConfig.enableSyncOrg && bizOrgConfig.allowOrgEleModel.length > 0">
                    <!-- 人工维护 -->
                    <template v-for="(item, i) in orgElementModels">
                      <a-menu-item
                        :key="'addSubOrgElement_' + item.id"
                        :element-type="item.id"
                        v-if="bizOrgConfig.allowOrgEleModel.includes(item.id)"
                      >
                        新建{{ item.name }}
                      </a-menu-item>
                    </template>
                  </template>
                </template>
                <template v-else-if="!node.unAllowedCreateSubOrgElement">
                  <!-- 组织节点下允许新增的组织节点类型按组织设置的执行 -->
                  <template v-for="(item, i) in orgElementModels">
                    <a-menu-item
                      :key="'addSubOrgElement_' + item.id"
                      :element-type="item.id"
                      v-if="
                        bizOrgConfig.allowOrgEleModel.includes(item.id) &&
                        childAllowedMountType[node.data.elementType] &&
                        childAllowedMountType[node.data.elementType].includes(item.id)
                      "
                    >
                      新建{{ item.name }}
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
    <Modal
      :title="bizOrgElementModal.title"
      :width="bizOrgElementModal.width"
      :ok="onSaveBizOrgElement"
      v-model="bizOrgElementModal.visible"
      :destroyOnClose="true"
      mask
    >
      <template slot="content">
        <BizOrgElement
          ref="bizOrgElement"
          v-if="bizOrgElementModal.visible"
          :bizOrgElement="bizOrgElement"
          :orgElementModels="orgElementModels"
          :bizOrgDimension="bizOrgDimension"
          :parentAllowedMountType="parentAllowedMountType"
          :bizOrgElementTreeData="treeData"
          :bizOrgConfig="bizOrgConfig"
        ></BizOrgElement>
      </template>
    </Modal>
  </a-skeleton>
</template>
<style lang="less" scoped>
.org-type-menu {
  padding: 8px 4px 8px 8px;
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
</style>
<script type="text/babel">
import BizOrgConfig from './biz-org-config.vue';
import { generateId } from '@framework/vue/utils/util';
import { debounce, sortBy } from 'lodash';

import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import BizOrgElement from './biz-org-element.vue';
export default {
  name: 'BizOrgElementTree',
  inject: ['bizOrgDimension'],
  props: {
    bizOrgConfig: Object,
    bizOrg: Object
  },
  components: { Modal, Drawer, BizOrgElement },
  computed: {},
  data() {
    return {
      orgElementModels: [],
      loading: true,
      searchWord: undefined,
      expandedTreeKeys: [],
      selectedOrgTreeKeys: [],
      treeData: [],
      treeKey: generateId(),
      treeKeyNodeMap: {},
      orgElementIcon: {},
      searchResult: [],
      modalWidth: 800,
      orgSetting: {},
      childAllowedMountType: {},
      parentAllowedMountType: {},
      bizOrgElementModal: {
        title: undefined,
        visible: false,
        width: 800,
        bizOrgElement: undefined
      },
      selectedNode: undefined,
      searchResultSelectedKey: undefined,
      onlyTranslateEmpty: true,
      translatingAll: false
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    Promise.all([
      BizOrgConfig.methods.fetchOrgElementModel.call(this),
      this.getSetting(),
      this.fetchAllBizOrgElement(this.bizOrg.uuid)
    ]).then(results => {
      let orgElementModels = results[0];
      for (let item of orgElementModels) {
        this.orgElementIcon[item.id] = item.icon;
        this.orgElementModels.push(item);
        this.$emit('onFetchOrgElementModels', this.orgElementModels);
        this.setChildAllowedMountType();
      }
      let list = results[2];
      this.$emit('fetchBizOrgElementCount', list.length);
      this.buildElementTreeData(list);
      this.loading = false;
    });
  },
  mounted() {},
  methods: {
    // 设置图标颜色
    setIconColor(type) {
      if (['dept', 'classify'].includes(type)) {
        return 'var(--w-warning-color)';
      } else if (['job'].includes(type)) {
        return 'var(--w-success-color)';
      } else if (['unit'].includes(type)) {
        return 'var(--w-info-color)';
      }
      return 'var(--w-primary-color)';
    },
    setIconType(type) {
      if (['unit'].includes(type)) {
        return 'iconfont icon-ptkj-danwei-01';
      } else if (['dept'].includes(type)) {
        return 'iconfont icon-ptkj-qunzu';
      } else if (['job'].includes(type)) {
        return 'iconfont icon-ptkj-zhiwei';
      }
      return 'iconfont icon-ptkj-xiangmuguanli';
    },
    onDragTreeNodeEnter() {},
    onDropTreeNode(e) {
      let dragDataRef = e.dragNode.dataRef,
        dropPosition = e.dropPosition;
      //   console.log('onDropTreeNode', e);
      // console.log(dragDataRef, dropPosition, e.node.eventKey, e.dragNode.eventKey);

      // 要保证同一个父级内移动
      let dropParent = e.node.$parent;
      if (dropParent == undefined || dropParent.value != dragDataRef.parentKey) {
        this.$message.info('只能同级内移动排序');
        return;
      }

      let list = dragDataRef.parentKey ? this.treeKeyNodeMap[dragDataRef.parentKey].children : this.treeData;
      let dropIndex = -1;
      for (let i = 0, len = list.length; i < len; i++) {
        if (list[i].key == e.dragNode.eventKey) {
          dropIndex = i;
          list.splice(dropIndex, 1);
          break;
        }
      }
      for (let i = 0, len = list.length; i < len; i++) {
        if (list[i].key == e.node.eventKey) {
          let dragNode = this.treeKeyNodeMap[e.dragNode.eventKey];
          // dropPosition = -1 : 拖拽到目标节点前
          // dropPosition > -1 : 拖拽到目标节点后
          list.splice(dropPosition > 0 ? i + 1 : i, 0, dragNode);
          break;
        }
      }
      this.resortUpdateNodes(list);
    },
    resortUpdateNodes(list) {
      let id = [];
      for (let i of list) {
        id.push(i.key);
      }
      this.$axios
        .post(`/proxy/api/org/biz/resortOrgElements`, id)
        .then(({ data }) => {})
        .catch(error => {});
    },
    onClickSearchNode(n) {
      this.searchResultSelectedKey = n.key;
      this.$emit('selectNode', n);
    },
    onSearchWordChange: debounce(function () {
      this.searchResult.splice(0, this.searchResult.length);
      if (this.searchWord) {
        for (let key in this.treeKeyNodeMap) {
          let node = this.treeKeyNodeMap[key];
          if (node.title.toLowerCase().indexOf(this.searchWord.toLowerCase()) >= 0) {
            let n = JSON.parse(JSON.stringify(node));
            if (n.parentKey) {
              let pathName = [],
                parent = this.treeKeyNodeMap[n.parentKey];
              while (parent != undefined) {
                pathName.push(parent.title);
                parent = this.treeKeyNodeMap[parent.parentKey];
              }
              n.pathName = pathName.reverse().join('/');
            }
            this.searchResult.push(n);
          }
        }
      }
    }, 300),
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
    },

    getSetting() {
      return new Promise((resolve, reject) => {
        $axios.get('/proxy/api/org/elementModel/queryOrgSetting', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
          if (data.code == 0 && data.data) {
            for (let i = 0, len = data.data.length; i < len; i++) {
              this.$set(this.orgSetting, data.data[i].attrKey, data.data[i]);
              this.orgSetting[data.data[i].attrKey].attrValueJson = data.data[i].attrVal ? JSON.parse(data.data[i].attrVal) : {};
            }
            resolve();
          }
        });
      });
    },

    onClickTreeNodeMenu(e, node) {
      if (e.key == 'edit') {
        this.bizOrgElement = {};
        this.bizOrgElement.uuid = node.data.uuid;
        this.bizOrgElement.isDimension = node.data.isDimension;
        this.bizOrgElement.elementType = node.data.elementType;
        this.bizOrgElement.id = node.data.id;
        this.bizOrgElement.bizOrgUuid = this.bizOrg.uuid;
        this.bizOrgElement.orgElementId = node.data.orgElementId;
        this.bizOrgElement.remark = node.data.remark;
        this.bizOrgElement.name = node.data.name;
        this.bizOrgElement.parentUuid = node.parentKey;
        if (node.parentKey != undefined) {
          this.bizOrgElement.parentName = this.treeKeyNodeMap[node.parentKey].data.name;
        }
        this.bizOrgElementModal.visible = true;
        this.bizOrgElementModal.title = node.title;
      } else if (e.key == 'del') {
        let _this = this;
        this.$confirm({
          title: `确认要删除 [ ${node.data.name} ] 吗?`,
          content: undefined,
          onOk() {
            _this.$set(node, 'loading', true);
            _this.deleteBizOrgElement(node.key, node);
          },
          onCancel() {}
        });
      } else if (e.key.startsWith('addSubOrgElement_')) {
        this.bizOrgElement = {
          parentUuid: node.key
        };
        let elementTypeAttr = e.domEvent.target.attributes['element-type'];
        this.bizOrgElement.isDimension = e.domEvent.target.attributes['is-dimension'] !== undefined;
        if (elementTypeAttr) {
          this.bizOrgElement.elementType = elementTypeAttr.value;
        }
        this.bizOrgElement.bizOrgUuid = this.bizOrg.uuid;
        this.bizOrgElementModal.visible = true;
        this.bizOrgElementModal.title = e.domEvent.target.textContent;
      }
    },
    deleteBizOrgElement(uuid, node) {
      this.$loading('删除中...');
      $axios
        .get(`/proxy/api/org/biz/deleteBizOrgElementByUuid`, {
          params: { uuid }
        })
        .then(({ data }) => {
          this.$loading(false);
          if (data.code == 0) {
            this.$message.success('删除成功');
            if (node.parentKey != undefined) {
              let parent = this.treeKeyNodeMap[node.parentKey];
              for (let i = 0, len = parent.children.length; i < len; i++) {
                if (parent.children[i].key == node.key) {
                  parent.children.splice(i, 1);
                  delete this.treeKeyNodeMap[node.key];
                  break;
                }
              }
            } else {
              delete this.treeKeyNodeMap[node.key];
              for (let i = 0, len = this.treeData.length; i < len; i++) {
                if (this.treeData[i].key == node.key) {
                  this.treeData.splice(i, 1);
                  break;
                }
              }
            }
          } else {
            this.$message.error('删除失败');
          }
        })
        .catch(error => {
          this.$loading(false);
          this.$message.error('删除失败');
        });
    },
    rootMenuClick({ key, item, domEvent }) {
      if (key.indexOf('addSubOrgElement_') != -1) {
        this.bizOrgElement = {};
        let elementTypeAttr = item.$el.attributes['element-type'];
        this.bizOrgElement.isDimension = item.$el.attributes['is-dimension'] !== undefined;
        if (elementTypeAttr) {
          this.bizOrgElement.elementType = elementTypeAttr.value;
        }
        this.bizOrgElement.bizOrgUuid = this.bizOrg.uuid;
        this.bizOrgElementModal.visible = true;
        this.bizOrgElementModal.title = domEvent.target.textContent;
      } else if (key === 'importOrgElement') {
      } else if (key === 'batchCreateOrgElement') {
      } else if (key == 'syncOrgElement') {
        this.startSyncOrgElement();
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
            .get(`/proxy/api/org/biz/organization/element/translateAll`, {
              params: { bizOrgUuid: _this.bizOrg.uuid, onlyTranslateEmpty: _this.onlyTranslateEmpty }
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
    startSyncOrgElement() {
      let loading = this.$message.loading('开始同步业务组织', 0);
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy/api/org/biz/syncBizOrg`, { params: { uuid: this.bizOrg.uuid } })
          .then(({ data }) => {
            setTimeout(loading, 0);
            resolve();
            if (data.code == 0) {
              this.$success({
                title: '成功',
                content: '已完成业务组织同步'
              });
              this.loading = true;
              this.fetchAllBizOrgElement(this.bizOrg.uuid).then(list => {
                this.buildElementTreeData(list);
                this.loading = false;
              });
            } else if (data.code == 1) {
              this.$warning({
                title: '提示',
                content: '业务组织同步中, 请稍后再试',
                onOk() {}
              });
            } else {
              this.$error({
                title: '异常',
                content: data.code == 2 ? data.msg : '业务组织同步异常'
              });
            }
          })
          .catch(error => {
            setTimeout(loading, 0);
            this.$error({
              title: '异常',
              content: '业务组织同步异常'
            });
            console.error(error);
          });
      });
    },
    onSaveBizOrgElement() {
      this.$refs.bizOrgElement.save().then(e => {
        // 添加到树节点里
        this.bizOrgElementModal.visible = false;
        let node = {
          key: e.uuid,
          id: e.id,
          title: e.name,
          label: e.name,
          data: e,
          unAllowedCreateSubDimension: undefined,
          unAllowedCreateSubOrgElement: undefined,
          parentKey: e.parentUuid,
          scopedSlots: { title: 'nodeTitle', icon: 'nodeIcon' }
        };

        let oldOne = this.treeKeyNodeMap[e.uuid],
          lastParentUuid = undefined;
        if (oldOne) {
          // 旧节点
          lastParentUuid = oldOne.parentKey;
          if (lastParentUuid) {
            let oldParent = this.treeKeyNodeMap[lastParentUuid];
            if (oldParent && lastParentUuid != e.parentUuid) {
              // 父级点发生变更，从旧父级点移除
              for (let i = 0, len = oldParent.children.length; i < len; i++) {
                if (oldParent.children[i].key == node.key) {
                  oldParent.children.splice(i, 1);
                  break;
                }
              }
            }
          } else if (lastParentUuid == null && e.parentUuid) {
            // 从顶级节点移除
            for (let i = 0, len = this.treeData.length; i < len; i++) {
              if (this.treeData[i].key == oldOne.key) {
                this.treeData.splice(i, 1);
                break;
              }
            }
          }

          oldOne.title = e.name;
          oldOne.label = oldOne.title;
          oldOne.data = e;
          oldOne.parentKey = e.parentUuid;
          node.children = oldOne.children;
        }
        if (lastParentUuid != e.parentUuid || this.treeKeyNodeMap[e.uuid] == undefined) {
          if (e.parentUuid == null) {
            this.treeData.push(node);
          } else {
            let parent = this.treeKeyNodeMap[e.parentUuid];
            if (parent.children == undefined) {
              this.$set(parent, 'children', []);
            }

            parent.children.push(node);
          }
        }

        this.treeKeyNodeMap[e.uuid] = node;

        this.updateTreeNodeCreateOperation();
        this.setTreeNodePath();
      });
    },
    fetchAllBizOrgElement(bizOrgUuid) {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy/api/org/biz/getAllBizOrgElementByBizOrgUuid`, { params: { bizOrgUuid } })
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {
            this.$message.error('业务组织节点服务异常');
          });
      });
    },
    buildElementTreeData(elements) {
      let map = {};
      this.treeData.splice(0, this.treeData.length);
      if (elements.length) {
        for (let e of elements) {
          let node = {
            key: e.uuid,
            value: e.uuid,
            id: e.id,
            title: e.name,
            label: e.name,
            data: e,
            unAllowedCreateSubDimension: undefined,
            unAllowedCreateSubOrgElement: undefined,
            parentKey: e.parentUuid,
            scopedSlots: { title: 'nodeTitle', icon: 'nodeIcon' }
          };
          map[e.uuid] = node;
          if (e.parentUuid == undefined) {
            node.titlePath = e.name;
            this.treeData.push(node);
          }
        }
        for (let key in map) {
          let n = map[key];
          if (n.parentKey != undefined) {
            let parent = map[n.parentKey];
            if (parent) {
              if (parent.children == undefined) {
                this.$set(parent, 'children', []);
              }
              parent.children.push(n);
            }
          }
        }
      }
      this.treeKeyNodeMap = map;
      this.setTreeNodePath();
      console.log(map);
      this.updateTreeNodeCreateOperation();
    },

    setTreeNodePath() {
      let setNodePath = (n, p) => {
        if (p) {
          n.titlePath = p.titlePath + ' - ' + n.title;
        } else {
          n.titlePath = n.title;
        }
        if (n.children && n.children.length > 0) {
          n.children = sortBy(n.children, function (o) {
            return typeof o.data.seq == 'number' ? o.data.seq : 0;
          });
          for (let c of n.children) {
            setNodePath(c, n);
          }
        }
      };
      this.treeData = sortBy(this.treeData, function (o) {
        return typeof o.data.seq == 'number' ? o.data.seq : 0;
      });
      for (let n of this.treeData) {
        setNodePath(n, undefined);
      }
    },

    updateTreeNodeCreateOperation() {
      for (let key in this.treeKeyNodeMap) {
        this.treeKeyNodeMap[key].unAllowedCreateSubDimension = undefined;
        this.treeKeyNodeMap[key].unAllowedCreateSubOrgElement = undefined;
      }
      let setChildNodeUnCreate = n => {
        if (!n.data.isDimension && this.bizOrgConfig.allowOrgLevel == 1) {
          n.unAllowedCreateSubOrgElement = true;
        }
        if (n.children != undefined) {
          for (let c of n.children) {
            setChildNodeUnCreate(c);
          }
        }
      };
      for (let key in this.treeKeyNodeMap) {
        let n = this.treeKeyNodeMap[key];
        if (n.data.isDimension) {
          if (this.bizOrgConfig.allowDimensionLevel == 1) {
            n.unAllowedCreateSubDimension = true;
          }
          if (n.children != undefined) {
            for (let c of n.children) {
              setChildNodeUnCreate(c);
            }
          }
        }
        if (this.bizOrgConfig.allowDimensionLevel == 1) {
          // 判断节点下是否可以创建子维度
          if (n.data.isDimension) {
            n.unAllowedCreateSubDimension = true;
          } else if (n.parentKey != undefined) {
            // 向上判断是否有父级维度
            let parent = this.treeKeyNodeMap[n.parentKey];
            while (parent != undefined) {
              if (parent.data.isDimension) {
                n.unAllowedCreateSubDimension = true;
                break;
              } else {
                parent = this.treeKeyNodeMap[parent.parentKey];
              }
            }
          }
        }
      }
      console.log(this.treeKeyNodeMap);
    },
    onSelectBizOrgNode() {
      this.$emit('selectNode', undefined);
      this.selectedNode = undefined;
      this.selectedOrgTreeKeys.splice(0, this.selectedOrgTreeKeys.length);
    },
    onSelectOrgNode(selectedKeys, { node, selected }) {
      this.selectedNode = selected ? node : undefined;
      this.$emit('selectNode', selected ? node.dataRef : undefined);
    }
  }
};
</script>
