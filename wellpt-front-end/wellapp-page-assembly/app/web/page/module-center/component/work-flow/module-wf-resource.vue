<template>
  <div>
    <a-collapse
      class="wf-resource-collapse"
      default-active-key="unDeleteList"
      :bordered="false"
      :accordion="true"
      expandIconPosition="right"
    >
      <a-collapse-panel key="unDeleteList" header="流程" class="module-page-resource-card">
        <a-row type="flex" style="margin: 12px 0 16px" v-show="!pleaseSelectExport">
          <a-col flex="auto" style="margin-right: 12px">
            <a-input-search
              placeholder="请输入关键字"
              @search="onSearch"
              allow-clear
              @change="onSearchInputChange"
              @click.stop="() => {}"
            />
          </a-col>
          <a-col flex="44px">
            <a-dropdown :trigger="['click']">
              <a-button class="icon-only" title="更多新建操作"><Icon type="pticon iconfont icon-ptkj-jiahao"></Icon></a-button>
              <a-menu slot="overlay" @click="handleMenuAddClick">
                <a-menu-item key="CreateWf">新建流程</a-menu-item>
                <a-menu-item key="CreateGroup">新建分类</a-menu-item>
                <!-- <a-sub-menu key="CreateGroup" title="新建分类">
                  <a-menu-item key="create-group">
                    <a-input-group compact>
                      <a-input @click.stop="() => {}" style="width: 200px" placeholder="请输入分类名称" />
                      <a-button type="primary" @click.stop="e => confirmNewGroup(e)" :loading="saveGrpLoading">保存</a-button>
                    </a-input-group>
                  </a-menu-item>
                </a-sub-menu> -->
                <a-menu-item key="Import">导入流程</a-menu-item>
              </a-menu>
            </a-dropdown>
          </a-col>
          <a-col flex="32px">
            <a-dropdown :trigger="['click']">
              <a-button class="icon-only" title="更多操作"><Icon type="pticon iconfont icon-ptkj-gengduocaozuo"></Icon></a-button>
              <a-menu slot="overlay" @click="handleMenuClick">
                <a-menu-item key="refresh">刷新</a-menu-item>
                <a-sub-menu key="export" title="导出">
                  <a-menu-item key="exportAll">全部导出</a-menu-item>
                  <a-menu-item key="exportSelected">选择导出</a-menu-item>
                </a-sub-menu>
                <a-menu-item key="dragSort">{{ dragSort ? '关闭' : '开启' }}排序</a-menu-item>
              </a-menu>
            </a-dropdown>
          </a-col>
        </a-row>
        <a-row type="flex" v-show="pleaseSelectExport" style="margin: 12px 0 16px">
          <a-col flex="80px" style="line-height: 32px; padding-left: 18px">
            <a-checkbox :indeterminate="indeterminate" :checked="checkAll" @change="onCheckAllChange">全选</a-checkbox>
          </a-col>
          <a-col flex="auto" style="text-align: right">
            <a-button :disabled="exportSelectedKeys.length == 0" @click="beginExportSelected" style="margin-right: 8px">
              <Icon type="pticon iconfont icon-luojizujian-yemiantiaozhuan"></Icon>
              导出选择
            </a-button>
            <a-button @click="quitExportSelect">
              <Icon type="pticon iconfont icon-ptkj-dacha"></Icon>
              退出
            </a-button>
          </a-col>
        </a-row>
        <a-spin :spinning="resourceLoading" style="min-height: 150px">
          <PerfectScrollbar style="height: calc(100vh - 220px); padding-right: 20px; margin-right: -12px">
            <div class="module-res-container" @copy="onCopy" @paste.stop="onPaste">
              <draggable
                v-model="resources"
                :group="{ name: 'res', pull: true, put: true }"
                animation="300"
                @start="dragStart"
                @end="dragEnd"
                @add="e => addSubItem(e, null)"
                handle=".drag-btn-handler"
              >
                <template v-for="(res, i) in resources">
                  <template v-if="res.children != undefined">
                    <!-- 分组 -->
                    <a-row type="flex" class="level-one" :data-uuid="res.uuid" :key="'group_' + res.uuid" :style="{ paddingBottom: '5px' }">
                      <a-col flex="100%">
                        <a-row type="flex" class="res-item-row btn-ghost-container" @paste.native.stop="onPaste">
                          <a-col
                            flex="auto"
                            class="res-item-col"
                            :data-group="res.uuid"
                            @click.native.stop="onClickGroupRow(res.uuid)"
                            :title="groupOpened.includes(res.uuid) ? '收起' : '展开'"
                          >
                            <img
                              class="svg-iconfont"
                              :src="groupOpened.includes(res.uuid) ? '/static/svg/folder-open.svg' : '/static/svg/folder-close.svg'"
                            />
                            <template v-if="res._RENAME">
                              <a-input-group compact style="display: inline-block; width: auto">
                                <a-input style="width: 110px" :defaultValue="res.name" @click.stop="() => {}">
                                  <a-icon type="loading" slot="suffix" v-if="res._RENAMING" />
                                </a-input>
                                <a-button
                                  type="link"
                                  size="small"
                                  title="保存"
                                  style="margin-top: 4px"
                                  @click.stop="e => renameGroup(e, res)"
                                >
                                  <Icon type="pticon iconfont icon-ptkj-baocun"></Icon>
                                </a-button>
                                <a-button
                                  type="link"
                                  size="small"
                                  title="取消"
                                  style="margin-top: 4px"
                                  @click.stop="e => exitRename(e, res)"
                                >
                                  <Icon type="pticon iconfont icon-ptkj-mianxingshibaitishi"></Icon>
                                </a-button>
                              </a-input-group>
                            </template>
                            <span v-else :title="res.name">
                              {{ res.name }}
                            </span>
                          </a-col>
                          <a-col flex="110px" class="col-operate">
                            <a-checkbox
                              style="height: 24px"
                              v-if="pleaseSelectExport"
                              :checked="exportSelectedKeys.includes(res.uuid)"
                              @click.stop="() => {}"
                              @change="e => onChangeExportSelect(e, res.uuid)"
                            />
                            <template v-else>
                              <a-dropdown :trigger="['click']">
                                <a-button ghost type="link" size="small" @click.stop="() => {}" title="更多操作">
                                  <Icon :type="res._LOADING ? 'loading' : 'pticon iconfont icon-ptkj-gengduocaozuo'"></Icon>
                                </a-button>
                                <a-menu slot="overlay" @click="e => onGroupMenuItemClick(e, res, i)">
                                  <a-menu-item key="paste" v-show="copyResUuid != undefined">粘贴</a-menu-item>
                                  <a-menu-item key="rename">重命名</a-menu-item>
                                  <!-- <a-sub-menu key="rename-menu" title="重命名">
                                    <a-menu-item key="rename-input">
                                      <a-input-group compact style="display: inline-block; width: auto">
                                        <a-input style="width: 200px" :defaultValue="res.name" @click.stop="() => {}">
                                          <a-icon type="loading" slot="suffix" v-if="res._RENAMING" />
                                        </a-input>
                                        <a-button type="primary" @click.stop="e => renameGroup(e, res)">保存</a-button>
                                      </a-input-group>
                                    </a-menu-item>
                                  </a-sub-menu> -->
                                  <a-menu-item key="export">导出</a-menu-item>
                                  <a-menu-item key="delete">删除</a-menu-item>
                                </a-menu>
                              </a-dropdown>
                              <span
                                v-show="dragSort"
                                class="drag-btn-handler ant-btn ant-btn-link ant-btn-sm ant-btn-background-ghost icon-only"
                                @click.stop="() => {}"
                                title="拖动排序"
                              >
                                <Icon type="pticon iconfont icon-ptkj-tuodong" />
                              </span>
                            </template>
                          </a-col>
                        </a-row>
                      </a-col>
                      <a-col flex="100%" v-show="groupOpened.includes(res.uuid)" class="sub-res-item-col">
                        <draggable
                          v-model="res.children"
                          :group="{ name: 'res', pull: true, put: canPutIntoGroup }"
                          animation="300"
                          @add="e => addSubItem(e, res.uuid)"
                          @end="dragEnd"
                          handle=".sub-drag-btn-handler"
                        >
                          <!-- 分组下的子元素 -->
                          <template v-for="(p, i) in res.children">
                            <a-row
                              v-show="searchWord == undefined || p.name.indexOf(searchWord) != -1"
                              type="flex"
                              :data-uuid="p.uuid"
                              :data-group="res.uuid"
                              :class="['res-item-row btn-ghost-container', 'level-two', selectKey == p.uuid ? 'selected btn-hover' : '']"
                              :key="p.uuid"
                              @click.native.stop="selectResource(p, p.uuid)"
                            >
                              <a-col flex="auto" :data-group="res.uuid" class="res-item-col">
                                <Icon type="pticon iconfont icon-oa-banliguocheng" />
                                <span :title="p.name">
                                  <template v-if="searchWord != undefined && p.name.indexOf(searchWord) > -1">
                                    {{ p.name.substr(0, p.name.indexOf(searchWord)) }}
                                    <span style="color: #f50">{{ searchWord }}</span>
                                    {{ p.name.substr(p.name.indexOf(searchWord) + searchWord.length) }}
                                  </template>
                                  <template v-else>{{ p.name }}</template>
                                </span>
                              </a-col>
                              <a-col flex="110px" class="col-operate" :data-group="res.uuid">
                                <a-checkbox
                                  style="height: 24px"
                                  v-if="pleaseSelectExport"
                                  :checked="exportSelectedKeys.includes(p.uuid)"
                                  @click.stop="() => {}"
                                  @change="e => onChangeExportSelect(e, p.uuid)"
                                />
                                <template v-else>
                                  <a-dropdown :trigger="['click']">
                                    <a-button ghost type="link" size="small" @click.stop="() => {}" title="更多操作">
                                      <Icon :type="p._LOADING ? 'loading' : 'pticon iconfont icon-ptkj-gengduocaozuo'"></Icon>
                                    </a-button>
                                    <a-menu slot="overlay" @click="e => onGroupResItemClick(e, p, res.children, i, res.uuid)">
                                      <a-menu-item key="edit" v-if="p._RES_TYPE == 'link'">编辑</a-menu-item>
                                      <a-menu-item key="copy">复制</a-menu-item>
                                      <a-menu-item key="export">导出</a-menu-item>
                                      <a-menu-item key="delete">删除</a-menu-item>
                                    </a-menu>
                                  </a-dropdown>
                                  <span
                                    v-show="dragSort"
                                    class="sub-drag-btn-handler ant-btn ant-btn-link ant-btn-sm ant-btn-background-ghost icon-only"
                                    @click.stop="() => {}"
                                    title="拖动排序"
                                  >
                                    <Icon type="pticon iconfont icon-ptkj-tuodong" />
                                  </span>
                                </template>
                              </a-col>
                            </a-row>
                          </template>
                          <a-empty
                            v-if="searchWord ? !searchedGroups.includes(res.uuid) : res.children.length == 0"
                            class="pt-empty hide-image"
                            :description="searchWord ? '无匹配数据' : '暂无数据'"
                          />
                        </draggable>
                      </a-col>
                    </a-row>
                  </template>
                  <template v-else>
                    <a-row
                      v-show="searchWord == undefined || res.name.indexOf(searchWord) != -1"
                      :data-uuid="res.uuid"
                      type="flex"
                      :class="['res-item-row btn-ghost-container', 'level-one', selectKey == res.uuid ? 'selected btn-hover' : '']"
                      :key="'no_group_res_' + res.uuid"
                      @click.native.stop="selectResource(res, res.uuid)"
                    >
                      <a-col flex="auto" class="res-item-col">
                        <Icon type="pticon iconfont icon-oa-banliguocheng" />
                        <span :title="res.name">
                          <template v-if="searchWord != undefined && res.name.indexOf(searchWord) > -1">
                            {{ res.name.substr(0, res.name.indexOf(searchWord)) }}
                            <span style="color: #f50">{{ searchWord }}</span>
                            {{ res.name.substr(res.name.indexOf(searchWord) + searchWord.length) }}
                          </template>
                          <template v-else>{{ res.name }}</template>
                        </span>
                      </a-col>
                      <a-col flex="110px" class="col-operate">
                        <a-checkbox
                          style="height: 24px"
                          v-if="pleaseSelectExport"
                          :checked="exportSelectedKeys.includes(res.uuid)"
                          @click.stop="() => {}"
                          @change="e => onChangeExportSelect(e, res.uuid)"
                        />
                        <template v-else>
                          <a-dropdown :trigger="['click']">
                            <a-button ghost type="link" size="small" @click.stop="() => {}" title="更多操作">
                              <Icon :type="res._LOADING ? 'loading' : 'pticon iconfont icon-ptkj-gengduocaozuo'"></Icon>
                            </a-button>
                            <a-menu slot="overlay" @click="e => onGroupResItemClick(e, res, resources, i, undefined)">
                              <a-menu-item key="edit" v-if="res._RES_TYPE == 'link'">编辑</a-menu-item>
                              <a-menu-item key="copy">复制</a-menu-item>
                              <a-menu-item key="export">导出</a-menu-item>
                              <a-menu-item key="delete">删除</a-menu-item>
                            </a-menu>
                          </a-dropdown>
                          <span
                            v-show="dragSort"
                            class="drag-btn-handler ant-btn ant-btn-link ant-btn-sm ant-btn-background-ghost icon-only"
                            @click.stop="() => {}"
                            title="拖动排序"
                          >
                            <Icon type="pticon iconfont icon-ptkj-tuodong" />
                          </span>
                        </template>
                      </a-col>
                    </a-row>
                  </template>
                </template>
              </draggable>
            </div>
          </PerfectScrollbar>
        </a-spin>
        <Modal
          v-model="copyModalVisible"
          title="复制流程定义"
          :ok="e => confirmCopyFlowDef(e)"
          :width="800"
          :maxHeight="600"
          :destroyOnClose="true"
        >
          <template slot="content">
            <a-form-model :model="copyFlowDef" :label-col="labelCol" :wrapper-col="wrapperCol" :rules="rules" ref="copyFlowDefForm">
              <a-form-model-item label="名称" prop="name">
                <a-input v-model="copyFlowDef.name" @change="changeCopyFlowName" />
              </a-form-model-item>
              <a-form-model-item label="ID" prop="id">
                <a-input v-model="copyFlowDef.id" />
              </a-form-model-item>
            </a-form-model>
          </template>
        </Modal>
      </a-collapse-panel>
      <a-collapse-panel key="logicDelete" class="module-page-resource-card">
        <div style="" slot="header">
          回收站
          <a-tooltip placement="right">
            <template slot="title">系统自动清除4天前删除的流程定义</template>
            <Icon type="pticon iconfont icon-ptkj-xinxiwenxintishi" />
          </a-tooltip>

          <a-badge :count="logicalDeleted.length" style="position: absolute; right: 40px" />
        </div>
        <PerfectScrollbar style="height: calc(100vh - 200px)">
          <div class="module-res-container">
            <div>
              <template v-for="(res, i) in logicalDeleted">
                <a-row
                  class="res-item-row level-one"
                  :data-uuid="res.uuid"
                  type="flex"
                  :key="'no_group_res_' + res.uuid"
                  @click.native.stop="selectResource(res, res.uuid)"
                >
                  <a-col flex="auto" class="res-item-col">
                    <a-icon type="apartment" />
                    <span :title="res.name">
                      {{ res.name }}
                    </span>
                  </a-col>
                  <a-col flex="32px">
                    <a-dropdown :trigger="['click']">
                      <a-button :icon="res._LOADING ? 'loading' : 'setting'" type="link" size="small" @click.stop="() => {}" />
                      <a-menu slot="overlay" @click="e => onGroupResItemClick(e, res, logicalDeleted, i, undefined)">
                        <a-menu-item key="physicalDelete">彻底删除</a-menu-item>
                        <a-menu-item key="rollback">恢复</a-menu-item>
                      </a-menu>
                    </a-dropdown>
                  </a-col>
                </a-row>
              </template>
            </div>
          </div>
        </PerfectScrollbar>
      </a-collapse-panel>
    </a-collapse>
    <ExportDef :uuid="exportUuid" :type="exportType" ref="exportDef" title="导出流程" />
    <ImportDef ref="importDef" title="导入流程" />
    <Modal title="新建流程" v-model="createModalVisible" :ok="e => confirmCreate(e)" :width="800" :maxHeight="600" :destroyOnClose="true">
      <template slot="content">
        <CreateWorkflow ref="createResource" :category="groups" class="pt-form" />
      </template>
    </Modal>
    <Modal title="新建分类" v-model="createGroupVisible" :width="400" :ok="confirmNewGroup">
      <template slot="content">
        <a-form-model layout="inline">
          <a-form-model-item label="分类名称">
            <a-input v-model="createGroupName" style="width: 200px" placeholder="请输入分类名称" />
          </a-form-model-item>
        </a-form-model>
      </template>
    </Modal>
  </div>
</template>

<script>
// import DraggableTreeList from '@pageAssembly/app/web/widget/commons/draggable-tree-list';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import CreateWorkflow from './create-workflow.vue';
import { debounce, orderBy } from 'lodash';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';
export default {
  name: 'ModuleWorkflowResource',
  inject: ['currentModule', 'pageContext'],
  components: {
    ExportDef,
    ImportDef,
    Modal,
    draggable: () => import(/* webpackChunkName: "vuedraggable" */ 'vuedraggable'),
    CreateWorkflow
  },
  computed: {
    resSeqMap() {
      let map = {};
      for (let g of this.resSeq) {
        map[g.resUuid] = g.seq;
      }
      return map;
    },
    resourceMap() {
      let map = {};
      for (let r of this.resources) {
        map[r.uuid] = r;
        if (r.children != undefined) {
          for (let c of r.children) {
            map[c.uuid] = c;
          }
        }
      }
      return map;
    },
    category() {
      let list = [];
      for (let r of this.resources) {
        if (r.children != undefined) {
          list.push({
            uuid: r.uuid,
            name: r.name
          });
        }
      }
      return list;
    }
  },
  data() {
    return {
      dragSort: false,
      defaultExpandAllGroup: true,
      searchWord: undefined,
      saveGrpLoading: false,
      resourceLoading: true,
      selectKey: undefined,
      copyResUuid: undefined,
      list: [],
      groups: [],
      // group: [],
      // category: [],
      logicalDeleted: [],
      resources: [],
      groupOpened: [],
      resSeq: [],
      labelCol: { span: 4 },
      wrapperCol: { span: 16 },
      rules: {
        name: [{ required: true, message: '名称必填', trigger: 'blur' }],
        id: [
          { required: true, message: 'ID必填', trigger: 'blur' },
          { pattern: /^\w+$/, message: '流程ID只允许包含字母、数字以及下划线', trigger: 'blur' },
          { trigger: ['blur', 'change'], validator: this.validateId }
        ]
      },
      copyFlowDef: {
        name: undefined,
        id: undefined,
        fromUuid: undefined
      },
      copyModalVisible: false,
      pleaseSelectExport: false,
      exportSelectedKeys: [],
      indeterminate: false,
      checkAll: false,
      exportUuid: [],
      exportType: [],
      createModalVisible: false,
      createGroupVisible: false,
      createGroupName: '',
      searchedGroups: []
    };
  },
  created() {},
  methods: {
    changeCopyFlowName: debounce(function () {
      if (this.copyFlowDef.name && this.copyFlowDef.name.trim() !== '') {
        this.translateName2Id();
      }
    }, 600),
    translateName2Id: debounce(function () {
      this.translating = true;
      this.$translate(this.copyFlowDef.name, 'zh', 'en')
        .then(text => {
          this.translating = false;
          let val = text.toUpperCase().replace(/( )/g, '_');
          this.$set(this.copyFlowDef, 'id', val);
        })
        .catch(error => {
          this.translating = false;
        });
    }, 200),
    handleExportMenuClick(e) {
      if (e.key == 'exportSelected') {
        this.pleaseSelectExport = true;
      } else if (e.key == 'exportAll') {
        this.exportUuid.splice(0, this.exportUuid.length);
        this.exportType.splice(0, this.exportType.length);
        this.exportUuid = Object.keys(this.resourceMap);
        for (let e of this.exportUuid) {
          this.exportType.push(this.resourceMap[e].children == undefined ? 'flowDefinition' : 'flowCategory');
        }
        this.$refs.exportDef.show();
      }
    },
    onCheckAllChange(e) {
      this.checkAll = e.target.checked;
      this.exportSelectedKeys.splice(0, this.exportSelectedKeys.length);
      if (e.target.checked) {
        this.exportSelectedKeys.push(...Object.keys(this.resourceMap));
      }
      this.indeterminate = false;
    },
    quitExportSelect() {
      this.checkAll = false;
      this.indeterminate = false;
      this.pleaseSelectExport = false;
      this.exportSelectedKeys.splice(0, this.exportSelectedKeys.length);
    },
    onChangeExportSelect(e, key) {
      if (e.target.checked) {
        this.exportSelectedKeys.push(key);
      } else {
        this.exportSelectedKeys.splice(this.exportSelectedKeys.indexOf(key), 1);
      }
      let allKeys = Object.keys(this.resourceMap);
      this.indeterminate = this.exportSelectedKeys.length && this.exportSelectedKeys.length < allKeys.length;
      this.checkAll = this.exportSelectedKeys.length && this.exportSelectedKeys.length == allKeys.length;
    },
    beginExportSelected() {
      this.exportUuid.splice(0, this.exportUuid.length);
      this.exportUuid.push(...JSON.parse(JSON.stringify(this.exportSelectedKeys)));
      let exportType = [];
      for (let e of this.exportUuid) {
        exportType.push(this.resourceMap[e].children == undefined ? 'flowDefinition' : 'flowCategory');
        if (this.resourceMap[e].children != undefined) {
          // 导出流程分类带出下面的流程导出
          for (let i = 0, len = this.resourceMap[e].children.length; i < len; i++) {
            let c = this.resourceMap[e].children[i];
            if (!this.exportUuid.includes(c.uuid)) {
              exportType.push('flowDefinition');
              this.exportUuid.push(c.uuid);
            }
          }
        }
      }
      this.exportType = exportType;
      this.$refs.exportDef.show();
    },
    validateId: debounce(function (rule, value, callback) {
      $axios
        .get('/proxy/api/workflow/definition/countById', {
          params: {
            flowDefId: value
          }
        })
        .then(({ data }) => {
          callback(data.data == '0' ? undefined : 'ID已存在');
        });
    }, 300),
    confirmNewGroup(e) {
      let name = this.createGroupName;
      // let name = e.target.previousElementSibling.value.trim();
      if (name) {
        this.saveGrpLoading = true;
        $axios.post(`/proxy/api/workflow/category/save`, { name, moduleId: this.currentModule.id }).then(({ data }) => {
          this.saveGrpLoading = false;
          this.groups.push({
            uuid: data.data,
            name
          });
          let idx = 0;
          for (let i = 0, len = this.resources.length; i < len; i++) {
            if (this.resources[i].children == undefined) {
              idx = i;
              break;
            }
          }
          this.resources.splice(idx, 0, { uuid: data.data, name, children: [] });
          // e.target.previousSibling.__vue__.handleReset();
          this.createGroupVisible = false;
          this.$message.success('分类添加成功');
        });
      } else {
        this.$message.error('请填写分类名称');
      }
    },
    onClickGroupRow(uuid) {
      let idx = this.groupOpened.indexOf(uuid);
      if (idx != -1) {
        this.groupOpened.splice(idx, 1);
      } else {
        this.groupOpened.push(uuid);
      }
    },
    onSearchInputChange(e) {
      if (e.target.value == '') {
        this.searchWord = undefined;
      }
    },
    onSearch(v) {
      this.searchWord = v;
      this.searchedGroups.splice(0, this.searchedGroups.length);
      for (let r of this.resources) {
        if (r.children != undefined) {
          for (let c of r.children) {
            if (c.name.indexOf(this.searchWord) != -1) {
              this.openGroup(r.uuid);
              this.searchedGroups.push(r.uuid);
              break;
            }
          }
        }
      }
    },
    canPutIntoGroup(s1, s2, dragItem) {
      if (dragItem._underlying_vm_.children == undefined) {
        return true;
      }
      return false;
    },

    dragStart(e) {
      if (e.item._underlying_vm_.children != undefined) {
        this.closeGroup(e.item._underlying_vm_.uuid);
      }
    },
    dragEnd() {
      this.updateSeq();
    },
    updateSeq() {
      let seq = [];
      for (let i = 0, len = this.resources.length; i < len; i++) {
        seq.push({
          seq: i + 1,
          title: this.resources[i].name,
          resUuid: this.resources[i].uuid,
          moduleId: this.currentModule.id,
          type: 'workflow'
        });
        if (this.resources[i].children && this.resources[i].children.length) {
          for (let j = 0, jlen = this.resources[i].children.length; j < jlen; j++) {
            seq.push({
              seq: j + 1,
              title: this.resources[i].children[j].name,
              resUuid: this.resources[i].children[j].uuid,
              moduleId: this.currentModule.id,
              type: 'workflow'
            });
          }
        }
      }
      if (seq.length) $axios.post('/proxy/api/app/module/res/seq/save', seq).then(() => {});
    },
    updateGroupMember(memberUuid, groupUuid, callback) {
      $axios.get('/proxy/api/app/module/resGroup/updateMember', { params: { memberUuid, groupUuid } }).then(({ data }) => {
        if (data.data) {
          $axios.get('/proxy/api/workflow/definition/updateFlowDefCategory', { params: { uuid: memberUuid, categoryUuid: groupUuid } });
          if (typeof callback == 'function') {
            callback.call(this);
          }
        }
      });
    },
    deleteFlowDef(res, callback, type) {
      $axios
        .post(`/proxy/api/workflow/definition/${type || 'logical'}/delete?uuid=${res.uuid}`)
        .then(({ data }) => {
          if (data.code == 0) {
            if (type !== 'physical') {
              res.deleteStatus = 1;
              this.logicalDeleted.push(res);
            }
            if (typeof callback == 'function') {
              callback(data.code == 0);
            }
          } else {
            console.error(data);
            this.$message.error('删除失败');
            this.$set(res, '_LOADING', false);
          }
        })
        .catch(error => {
          let msg = '';
          if (error.response && error.response.data && error.response.data.msg) {
            msg = '：' + error.response.data.msg;
          }
          this.$message.error('删除失败' + msg);
          this.$set(res, '_LOADING', false);
        });
    },
    onGroupResItemClick({ item, key }, res, siblings, index, groupUuid) {
      let _this = this;
      if (key == 'delete') {
        this.$confirm({
          title: '提示',
          content: `确定要删除[${res.name}]流程吗?`,
          okText: '确定',
          cancelText: '取消',
          onOk() {
            _this.deleteGroupRes(res, siblings, index, groupUuid);
          },
          onCancel() {}
        });
      } else if (key == 'copy') {
        this.onCopy(res.uuid, groupUuid);
        // this.copyGroupRes(res, siblings);
      } else if (key == 'export') {
        this.exportType.splice(0, this.exportType.length);
        this.exportUuid.splice(0, this.exportUuid.length);
        this.exportType.push('flowDefinition');
        this.exportUuid.push(res.uuid);
        this.exportTitle = '导出流程';
        this.$refs.exportDef.show();
      } else if (key == 'physicalDelete') {
        this.$confirm({
          title: '提示',
          content: `确定要彻底删除[${res.name}]流程吗?`,
          okText: '确定',
          cancelText: '取消',
          onOk() {
            _this.deleteGroupRes(res, siblings, index, undefined, 'physical');
          },
          onCancel() {}
        });
      } else if (key == 'rollback') {
        res.deleteStatus = 0;
        siblings.splice(index, 1);
        this.rebuildResource();
        this.recoveryFlowDefinition(res.uuid);
      }
    },
    onCopy(e, groupUuid) {
      if (typeof e == 'string') {
        // 点击复制按钮
        this.copyResUuid = e;
        this.copyResInGroup = groupUuid;
      } else {
        // ctrl + c 复制
        this.copyResUuid = this.selectKey;
        // 默认拷贝到当前组
        this.copyResInGroup = e.target.parentElement.dataset.group;
      }
      this.$message.success('已复制, 请选择要粘贴的分组或者 CTRL + V 直接粘贴');
    },
    onPaste(e) {
      let groupUuid = this.copyResInGroup;
      if (typeof e == 'string') {
        // 指定了粘贴分组
        groupUuid = e;
      } else if (e && e.target.dataset.group) {
        // 鼠标选中了分组行的 ctrl + v
        groupUuid = e.target.dataset.group;
      }

      if (this.copyResUuid != undefined) {
        for (let l of this.list) {
          if (l.uuid == this.copyResUuid) {
            this.confirmCopyWorkflow(l);
            break;
          }
        }
      }
    },
    confirmCopyFlowDef(e) {
      this.$refs.copyFlowDefForm.validate(passed => {
        if (passed) {
          $axios
            .post(
              `/proxy/api/workflow/definition/copy?uuid=${this.copyFlowDef.fromUuid}&newFlowDefId=${this.copyFlowDef.id}&newFlowDefName=${this.copyFlowDef.name}`
            )
            .then(({ data }) => {
              if (data.data) {
                e(true);
                if (this.copyFlowDef.fromCategory) {
                  let append = false;
                  for (let r of this.resources) {
                    if (r.children != undefined && r.uuid == this.copyFlowDef.fromCategory) {
                      r.children.push({
                        uuid: data.data,
                        name: this.copyFlowDef.name,
                        id: this.copyFlowDef.id,
                        category: this.copyFlowDef.fromCategory
                      });
                      append = true;
                      break;
                    }
                  }
                  if (!append) {
                    this.refresh();
                  }
                } else {
                  this.resources.push({
                    uuid: data.data,
                    name: this.copyFlowDef.name,
                    id: this.copyFlowDef.id
                  });
                }
              }
            })
            .catch(error => {});
        }
      });
    },
    confirmCopyWorkflow(item) {
      this.copyFlowDef.name = item.name + ' - 副本';
      this.copyFlowDef.id = item.id + '_copy';
      this.copyFlowDef.fromUuid = item.uuid;
      this.copyFlowDef.fromCategory = item.category;
      this.copyModalVisible = true;
    },
    deleteGroupRes(res, siblings, index, groupUuid, type) {
      let afterDelete = success => {
        if (success) {
          // if (groupUuid != undefined) {
          //   this.updateGroupMember(res.uuid, null);
          // }
          siblings.splice(index, 1);
          if (this.selectKey == res.uuid) {
            this.$emit('select', undefined, undefined);
          }
        }
        this.$set(res, '_LOADING', false);
      };
      this.$set(res, '_LOADING', true);
      this.deleteFlowDef(res, afterDelete, type);
    },

    addSubItem(e, groupUuid) {
      this.updateGroupMember(e.item._underlying_vm_.uuid, groupUuid, () => {
        if (groupUuid) {
          e.item._underlying_vm_.category = groupUuid;
          this.openGroup(groupUuid);
        } else {
          e.item._underlying_vm_.category = undefined;
        }
      });
    },
    closeGroup(uuid) {
      let idx = this.groupOpened.indexOf(uuid);
      if (idx != -1) {
        this.groupOpened.splice(idx, 1);
      }
    },
    openGroup(uuid) {
      if (!this.groupOpened.includes(uuid)) {
        this.groupOpened.push(uuid);
      }
    },
    deleteGroup(res, index) {
      this.$set(res, '_LOADING', true);
      $axios.post(`/proxy/api/workflow/category/deleteWhenNotUsed?uuid=${res.uuid}`, {}).then(({ data }) => {
        if (data.data == 1) {
          this.resources.push(...res.children);
          this.resources.splice(index, 1);
        } else {
          this.$message.error('分组已被使用, 不允许删除');
        }
      });
    },
    confirmCreate(e) {
      let _this = this;
      this.$refs.createResource.save(data => {
        e(true);
        if (data && data.uuid) {
          if (data.category) {
            for (let r of this.resources) {
              if (r.uuid == data.category) {
                r.children.push(data);
                break;
              }
            }
          } else {
            this.resources.push(data);
          }
        }
      });
    },
    onGroupMenuItemClick({ item, key }, res, index) {
      if (key == 'delete') {
        this.$confirm({
          title: '提示',
          content: `确定要删除[${res.name}]分组吗?`,
          okText: '确定',
          cancelText: '取消',
          onOk() {
            this.deleteGroup(res, index);
          },
          onCancel() {}
        });
      } else if (key == 'rename') {
        this.$set(res, '_RENAME', true);
      } else if (key == 'paste') {
        this.onPaste(res.uuid);
      } else if (key == 'export') {
        if (res.children && res.children.length) {
          this.exportType.splice(0, 1);
          this.exportUuid.splice(0, 1);
          this.exportType.push('flowCategory');
          this.exportUuid.push(res.uuid);
          this.exportTitle = '导出分类';
          this.$refs.exportDef.show();

          // 导出流程分类带出下面的流程导出
          for (let i = 0, len = res.children.length; i < len; i++) {
            let c = res.children[i];
            if (!this.exportUuid.includes(c.uuid)) {
              this.exportType.push('flowDefinition');
              this.exportUuid.push(c.uuid);
            }
          }
        } else {
          this.$set(res, '_LOADING', true);
          this.$refs.exportDef.createExportRequest('flowCategory', res.uuid, `流程分类: ${res.name}`).then(() => {
            this.$set(res, '_LOADING', false);
          });
        }
      }
    },
    selectResource(data, key) {
      console.log('选择资源: ', arguments);
      this.selectKey = key;
      this.$emit('select', 'WorkflowPreview', data);
    },
    exitRename(e, res) {
      this.$set(res, '_RENAME', false);
      this.$set(res, '_RENAMING', false);
    },
    renameGroup(e, res) {
      let name = e.target.previousElementSibling.value.trim();
      this.$set(res, '_RENAMING', true);
      $axios.post(`/proxy/api/workflow/category/save`, { uuid: res.uuid, name, moduleId: this.currentModule.id }).then(({ data }) => {
        if (data.data) {
          res.name = name;
          this.exitRename(e, res);
        } else {
          this.$message.error('重命名分组异常');
        }
      });
    },
    fetchFlowCategoryAndFlowDefs() {
      this.resourceLoading = true;
      return new Promise((resolve, reject) => {
        $axios
          .all([
            $axios.post('/proxy/api/workflow/definition/module/query', [this.currentModule.id]),
            $axios.get('/proxy/api/workflow/category/getModuleFlowCategory', { params: { moduleId: this.currentModule.id } }),
            $axios.get('/proxy/api/app/module/res/seq/list', { params: { moduleId: this.currentModule.id, type: 'workflow' } })
          ])
          .then(
            $axios.spread((defs, groups, resSeq) => {
              this.resourceLoading = false;
              //当这两个请求都完成的时候会触发这个函数，两个参数分别代表返回的结果
              this.groups = groups.data.data;
              this.groupOpened = [];
              for (let g of this.groups) {
                if (this.defaultExpandAllGroup) {
                  this.groupOpened.push(g.uuid);
                }
              }
              if (defs.data.data) {
                this.list = defs.data.data;
              }
              this.resSeq = resSeq.data.data;
              resolve();
            })
          );
      });
    },
    rebuildResource() {
      let groupMap = {},
        noCategoryDefs = [];
      this.logicalDeleted = [];
      this.resources = [];
      if (this.groups.length) {
        for (let g of this.groups) {
          let grp = {
            uuid: g.uuid,
            name: g.name,
            children: []
          };
          if (this.resSeqMap[g.uuid]) {
            grp._SEQ = this.resSeqMap[g.uuid];
          }
          this.resources.push(grp);
          groupMap[g.uuid] = grp;
        }
      }
      if (this.list.length) {
        for (let l of this.list) {
          if (l.deleteStatus == 1 || l.deleteStatus == 2) {
            this.logicalDeleted.push(l);
            continue;
          }
          if (this.resSeqMap[l.uuid]) {
            l._SEQ = this.resSeqMap[l.uuid];
          }
          if (l.category && groupMap[l.category]) {
            groupMap[l.category].children.push(l);
          } else {
            noCategoryDefs.push(l);
          }
        }
      }
      this.resources.push(...noCategoryDefs);

      this.resources = orderBy(this.resources, ['_SEQ'], ['asc']);
      for (let r of this.resources) {
        if (r.children && r.children.length) {
          r.children = orderBy(r.children, ['_SEQ'], ['asc']);
        }
      }
    },
    recoveryFlowDefinition(flowDefUuid) {
      $axios.post(`/proxy/api/workflow/definition/recovery?uuid=${flowDefUuid}`);
    },
    refresh: debounce(function () {
      this.resourceLoading = true;
      this.resources = [];
      this.groups = [];
      this.list = [];
      this.groupOpened = [];
      this.fetchFlowCategoryAndFlowDefs().then(() => {
        this.rebuildResource();
      });
    }, 300),
    handleMenuAddClick(e) {
      if (e.key == 'Import') {
        this.$refs.importDef.show();
      } else if (e.key == 'CreateWf') {
        this.createModalVisible = true;
      } else if (e.key == 'CreateGroup') {
        this.createGroupVisible = true;
        this.createGroupName = '';
      }
    },
    handleMenuClick(e) {
      if (e.key == 'refresh') {
        this.refresh();
      } else if (e.key == 'exportAll' || e.key == 'exportSelected') {
        this.handleExportMenuClick(e);
      } else if (e.key == 'dragSort') {
        this.dragSort = !this.dragSort;
      }
    }
  },
  beforeMount() {
    this.refresh();
  }
};
</script>
