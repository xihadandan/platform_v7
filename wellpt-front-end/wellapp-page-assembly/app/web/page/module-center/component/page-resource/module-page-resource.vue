<template>
  <a-card title="页面" class="module-page-resource-card pt-card" :bordered="false">
    <a-radio-group v-model="pageType" button-style="solid" class="page-type-select">
      <a-radio-button value="PC">桌面端</a-radio-button>
      <a-radio-button value="MOBILE">移动端</a-radio-button>
    </a-radio-group>
    <a-row type="flex" style="margin: 12px 0 16px" v-show="!pleaseSelectExport">
      <a-col flex="auto" style="margin-right: 12px">
        <a-input-search placeholder="请输入关键字" @search="onSearch" allow-clear @change="onSearchInputChange" @click.stop="() => {}" />
      </a-col>
      <a-col flex="44px">
        <a-dropdown :trigger="['click']">
          <a-button class="icon-only" title="更多新建操作"><Icon type="pticon iconfont icon-ptkj-jiahao"></Icon></a-button>
          <a-menu slot="overlay" @click="handleMenuAddClick">
            <a-menu-item key="CreateDyform">新建表单</a-menu-item>
            <a-menu-item key="CreatePage">新建页面</a-menu-item>
            <a-menu-item key="CreateBigscreen">新建大屏</a-menu-item>
            <a-menu-item key="CreateLink">新建链接</a-menu-item>
            <a-menu-item key="CreateGroup">新建分组</a-menu-item>
            <!-- <a-sub-menu key="CreateGroup" title="新建分组">
              <a-menu-item key="create-group">
                <a-input-group compact>
                  <a-input @click.stop="() => {}" style="width: 200px" placeholder="请输入分组名称" />
                  <a-button type="primary" @click.stop="e => confirmNewGroup(e)" :loading="saveGrpLoading">保存</a-button>
                </a-input-group>
              </a-menu-item>
            </a-sub-menu> -->
            <a-menu-item key="Import">导入</a-menu-item>
          </a-menu>
        </a-dropdown>
      </a-col>
      <a-col flex="32px">
        <a-dropdown :trigger="['click']">
          <a-button class="icon-only" title="更多操作">
            <Icon type="pticon iconfont icon-ptkj-gengduocaozuo"></Icon>
          </a-button>
          <a-menu slot="overlay" @click="handleMenuClick">
            <a-menu-item key="refresh">刷新</a-menu-item>
            <a-sub-menu key="export" title="导出">
              <a-menu-item key="exportAll">全部导出</a-menu-item>
              <a-menu-item key="exportSelected">选择导出</a-menu-item>
            </a-sub-menu>
            <a-sub-menu key="filterTypes" title="筛选">
              <a-menu-item key="filter_form" @click.stop="() => {}">
                <a-checkbox @click="e => onClickFilterType(e, 'form')" :checked="filterTypes.includes('form')" value="form">
                  表单
                </a-checkbox>
              </a-menu-item>
              <a-menu-item key="filter_page" @click.stop="() => {}">
                <a-checkbox @click="e => onClickFilterType(e, 'page')" :checked="filterTypes.includes('page')" value="page">
                  页面
                </a-checkbox>
              </a-menu-item>
              <a-menu-item key="filter_vBigscreen" @click.stop="() => {}">
                <a-checkbox
                  @click="e => onClickFilterType(e, 'vBigscreen')"
                  :checked="filterTypes.includes('vBigscreen')"
                  value="vBigscreen"
                >
                  大屏
                </a-checkbox>
              </a-menu-item>
              <a-menu-item key="filter_link" @click.stop="() => {}">
                <a-checkbox @click="e => onClickFilterType(e, 'link')" :checked="filterTypes.includes('link')" value="link">
                  链接
                </a-checkbox>
              </a-menu-item>
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
      <PerfectScrollbar style="height: calc(100vh - 210px); padding-right: 20px; margin-right: -20px">
        <div class="module-res-container" @copy="onCopy" @paste.stop="onPaste">
          <!-- 模块主页 -->
          <div
            style="width: 100%; height: 100%; position: fixed; z-index: 2; background: rgb(0, 0, 0, 0.2); top: 0; left: 0"
            v-if="designableTipVisible"
          ></div>
          <a-popconfirm
            placement="rightTop"
            :arrowPointAtCenter="true"
            :visible="designableTipVisible"
            overlayClassName="test"
            title="您可以在模块主页中, 自定义模块的导航和布局"
            :cancel-text="null"
            ok-text="知道了"
            :cancelButtonProps="{ style: { display: 'none' } }"
            :overlayStyle="{
              zIndex: 3
            }"
            @confirm="designableTipVisible = false"
            ref="designTipPopconfirm"
          >
            <a-row
              v-if="modulePageDef.uuid && modulePageDef.designable"
              :class="['res-item-row btn-ghost-container level-one', selectKey == modulePageDef.uuid ? 'selected btn-hover' : '']"
              style="position: relative; z-index: 3; width: 100%"
              v-show="modulePageDef._PAGE_TYPE == pageType && filterTypes.includes(modulePageDef._RES_TYPE)"
              :data-uuid="modulePageDef.uuid"
              :style="{
                [designableTipVisible ? 'backgroundColor' : undefined]: '#fff'
              }"
              type="flex"
              @click.native.stop="selectResource(modulePageDef, modulePageDef.uuid, compType(modulePageDef._RES_TYPE))"
            >
              <a-col flex="auto" class="res-item-col">
                <Icon style="color: var(--w-primary-color)" type="pticon iconfont icon-szgy-zhuye"></Icon>
                <span :title="modulePageDef.name">模块主页</span>
              </a-col>
              <a-col flex="110px" class="col-operate">
                <a-checkbox
                  style="height: 24px"
                  v-if="pleaseSelectExport"
                  :checked="exportSelectedKeys.includes(modulePageDef.uuid)"
                  @click.stop="() => {}"
                  @change="e => onChangeExportSelect(e, modulePageDef.uuid)"
                />
                <a-dropdown :trigger="['click']" v-else>
                  <a-button ghost type="link" size="small" @click.stop="() => {}" title="更多操作">
                    <Icon :type="modulePageDef._LOADING ? 'loading' : 'pticon iconfont icon-ptkj-gengduocaozuo'"></Icon>
                  </a-button>
                  <a-menu slot="overlay" @click="e => onGroupResItemClick(e, modulePageDef, [], i, undefined)">
                    <a-menu-item key="copy">复制</a-menu-item>
                    <a-menu-item key="export">导出</a-menu-item>
                  </a-menu>
                </a-dropdown>
              </a-col>
            </a-row>
          </a-popconfirm>
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
                            <a-button type="link" size="small" title="保存" style="margin-top: 4px" @click.stop="e => renameGroup(e, res)">
                              <Icon type="pticon iconfont icon-ptkj-baocun"></Icon>
                            </a-button>
                            <a-button type="link" size="small" title="取消" style="margin-top: 4px" @click.stop="e => exitRename(e, res)">
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
                          v-if="pleaseSelectExport"
                          :checked="exportSelectedKeys.includes(res.uuid)"
                          @click.stop="() => {}"
                          @change="e => onChangeExportSelect(e, res.uuid)"
                          style="height: 24px"
                        />
                        <template v-else>
                          <!-- <a-button
                            ghost
                            class="icon-only"
                            type="link"
                            title="删除"
                            @click="onGroupMenuItemClick({ key: 'delete' }, res, i)"
                          >
                            <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
                          </a-button> -->
                          <a-dropdown :trigger="['click']">
                            <a-button ghost type="link" size="small" @click.stop="() => {}" title="更多操作">
                              <Icon :type="res._LOADING ? 'loading' : 'pticon iconfont icon-ptkj-gengduocaozuo'"></Icon>
                            </a-button>
                            <a-menu slot="overlay" @click="e => onGroupMenuItemClick(e, res, i)">
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
                              <a-menu-item key="paste" v-show="copyResUuid != undefined">粘贴</a-menu-item>
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
                          v-show="
                            p._PAGE_TYPE == pageType &&
                            (filterTypes.includes(p._RES_TYPE) || filterTypes.includes(p.wtype)) &&
                            (searchWord == undefined || p.name.indexOf(searchWord) != -1)
                          "
                          type="flex"
                          :data-uuid="p.uuid"
                          :data-group="res.uuid"
                          :class="['res-item-row btn-ghost-container', 'level-two', selectKey == p.uuid ? 'selected btn-hover' : '']"
                          :key="p.uuid"
                          @click.native.stop="selectResource(p, p.uuid, compType(p._RES_TYPE), res.uuid)"
                        >
                          <a-col flex="auto" :data-group="res.uuid" class="res-item-col">
                            <Icon :type="iconType(p._RES_TYPE)" />
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
                  v-show="
                    res._PAGE_TYPE == pageType &&
                    (filterTypes.includes(res._RES_TYPE) || filterTypes.includes(res.wtype)) &&
                    (searchWord == undefined || res.name.indexOf(searchWord) != -1)
                  "
                  :data-uuid="res.uuid"
                  type="flex"
                  :class="['res-item-row  btn-ghost-container', 'level-one', selectKey == res.uuid ? 'selected btn-hover' : '']"
                  :key="'no_group_res_' + res.uuid"
                  @click.native.stop="selectResource(res, res.uuid, compType(res._RES_TYPE))"
                >
                  <a-col flex="auto" class="res-item-col">
                    <Icon :type="iconType(res._RES_TYPE)" />
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
      :title="createResourceTitle"
      :ok="e => confirmCreate(e)"
      :width="800"
      :maxHeight="600"
      v-model="modalVisible"
      :destroyOnClose="true"
    >
      <template slot="content">
        <component
          class="pt-form"
          v-if="createResourceType != undefined"
          :is="createResourceType"
          :groupOptions="groupOptions"
          :resource="editResource"
          :isPc="pageType == 'PC'"
          ref="createResource"
          :key="createResourceKey"
        />
      </template>
    </Modal>

    <Modal title="新建分组" v-model="createGroupVisible" :width="400" :ok="confirmNewGroup">
      <template slot="content">
        <a-form-model layout="inline">
          <a-form-model-item label="分组名称">
            <a-input v-model="createGroupName" style="width: 200px" placeholder="请输入分组名称" />
          </a-form-model-item>
        </a-form-model>
      </template>
    </Modal>

    <Modal :ok="confirmCopyForm" v-model="copyFormVisible" :title="'复制表单: ' + copyFormFrom.name + '(' + copyFormFrom.id + ')'">
      <template slot="content">
        <a-form-model
          :model="copyForm"
          :rules="copyRules"
          ref="copyForm"
          :colon="false"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 19 }"
        >
          <a-form-model-item prop="name" label="表单名称">
            <a-input v-model="copyForm.name" />
          </a-form-model-item>
          <a-form-model-item prop="id" label="表单ID">
            <a-input :maxLength="24" v-model="copyForm.id" @change="transformIdToUpperCase" />
          </a-form-model-item>
        </a-form-model>
      </template>
    </Modal>

    <Modal :ok="confirmCopyPage" v-model="copyPageVisible" :title="'复制页面: ' + copyPageFrom.fromName + '(' + copyPageFrom.fromId + ')'">
      <template slot="content">
        <a-form-model
          :model="copyPageFrom"
          :rules="copyPageRules"
          ref="copyPage"
          :colon="false"
          :label-col="{ span: 4 }"
          :wrapper-col="{ span: 19 }"
        >
          <a-form-model-item prop="name" label="名称">
            <a-input v-model.trim="copyPageFrom.name" />
          </a-form-model-item>
          <a-form-model-item prop="id" label="ID">
            <a-input :maxLength="120" v-model.trim="copyPageFrom.id" />
          </a-form-model-item>
        </a-form-model>
      </template>
    </Modal>

    <ExportDef :uuid="exportUuid" :type="exportType" :title="exportTitle" ref="exportDef" />
    <ImportDef ref="importDef" @importDone="refresh" />
  </a-card>
</template>
<style lang="less"></style>
<script type="text/babel">
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import CreatePage from './create-page.vue';
import CreateDyform from './create-dyform.vue';
import CreateLink from './create-link.vue';
import CreateBigscreen from './create-bigscreen.vue';
import { orderBy, debounce } from 'lodash';
import { generateId } from '@framework/vue/utils/util';
import { convertFormField2DataModelColumns } from '@dyform/app/web/page/dyform-designer/utils';
import { DEFAULT_SYS_COLUMNS } from '../data-model/const';
import moment from 'moment';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';
export default {
  name: 'ModulePageResource',
  inject: ['currentModule', 'pageContext'],
  props: {},
  components: {
    ExportDef,
    ImportDef,
    Modal,
    CreatePage,
    CreateDyform,
    CreateLink,
    CreateBigscreen,
    draggable: () => import(/* webpackChunkName: "vuedraggable" */ 'vuedraggable')
  },
  computed: {
    groupOptions() {
      let options = [];
      for (let grp of this.groups) {
        options.push({
          label: grp.name,
          value: grp.uuid
        });
      }
      return options;
    },
    groupUuidMap() {
      let map = {};
      for (let g of this.groups) {
        map[g.uuid] = g;
      }
      return map;
    },
    formDefUuidMap() {
      let map = {};
      for (let g of this.formDefinitions) {
        map[g.uuid] = g;
      }
      return map;
    },
    pageDefUuidMap() {
      let map = {};
      for (let g of this.pageDefinitions) {
        map[g.uuid] = g;
      }
      return map;
    },

    resSeqMap() {
      let map = {};
      for (let g of this.resSeq) {
        map[g.resUuid] = g.seq;
      }
      return map;
    },
    resourceMap() {
      let map = {};
      for (let i = 0, len = this.resources.length; i < len; i++) {
        map[this.resources[i].uuid] = this.resources[i];
        if (this.resources[i].children) {
          for (let j = 0, jlen = this.resources[i].children.length; j < jlen; j++) {
            map[this.resources[i].children[j].uuid] = this.resources[i].children[j];
          }
        }
      }
      return map;
    }
  },
  data() {
    return {
      defaultExpandAllGroup: true,
      pageType: 'PC',
      dragSort: false,
      createResourceType: undefined,
      editResource: {},
      modalVisible: false,
      createResourceTitle: '',
      resources: [],
      filterTypes: ['form', 'page', 'link', 'vBigscreen'],
      groups: [],
      formDefinitions: [],
      pageDefinitions: [],
      links: [],
      groupOpened: [],
      loading: 0,
      selectKey: undefined,
      resSeq: [],
      searchWord: undefined,
      resourceLoading: true,
      saveGrpLoading: false,
      createResourceKey: 'CreateResourceKey_0',
      copyResUuid: undefined,
      copyNewFormId: undefined,
      modulePageDef: { uuid: undefined, designable: false },
      designableTipVisible: false,
      copyFormVisible: false,
      copyForm: {
        id: undefined,
        name: undefined
      },
      copyFormFrom: {
        uuid: undefined,
        id: undefined,
        name: undefined
      },
      copyRules: {
        name: [{ required: true, trigger: ['blur'], message: '必填' }],
        id: [
          { required: true, trigger: ['blur', 'change'], message: '必填' },
          {
            pattern: /^\w+$/,
            message: '表单ID只允许包含字母、数字以及下划线',
            //'表单ID只允许包含字母、数字以及下划线'
            trigger: ['blur', 'change']
          },
          { trigger: ['blur', 'change'], validator: this.validateFormId }
        ]
      },
      copyPageVisible: false,
      copyPageFrom: {
        uuid: undefined,
        fromId: undefined,
        fromName: undefined,
        id: undefined,
        name: undefined
      },
      copyPageRules: {
        name: [{ required: true, trigger: ['blur'], message: '必填' }],
        id: [
          { required: true, trigger: ['blur', 'change'], message: '必填' },
          {
            pattern: /^\w+$/,
            message: 'ID只允许包含字母、数字以及下划线',
            trigger: ['blur', 'change']
          },
          { trigger: ['blur', 'change'], validator: this.validatePageId }
        ]
      },
      exportUuid: undefined,
      exportType: undefined,
      exportTitle: '导出定义',
      pleaseSelectExport: false,
      exportSelectedKeys: [],
      indeterminate: false,
      checkAll: false,
      createGroupVisible: false,
      createGroupName: '',
      isDesignableTipVisible: false,
      searchedGroups: []
    };
  },
  provide() {
    return {
      resources: this.resources
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.refresh();
  },
  mounted() {
    let _this = this;
    this.pageContext.handleEvent('CreateModuleResourceSuccess', data => {
      if (_this.selectGroup != undefined) {
        // 资源添加到分组下
        data.groupUuid = _this.selectGroup;
        _this.updateGroupMember(data.uuid, _this.selectGroup, _this.resType2MemberType(data._RES_TYPE));
      }
      _this.addResourceItem(data);
      _this.selectResource(data, data.uuid, _this.compType(data._RES_TYPE));
    });

    this.pageContext.handleEvent('ModuleResourceRefresh', data => {
      _this.refresh();
    });
    this.pageContext.handleCrossTabEvent('ModuleResourceRefresh', data => {
      _this.refresh();
    });
  },
  methods: {
    validatePageId: debounce(function (rule, value, callback) {
      $axios
        .get('/proxy/api/webapp/page/definition/existId', {
          params: { id: value }
        })
        .then(({ data }) => {
          callback(data.data === false ? undefined : '页面ID已存在');
        });
    }, 300),
    onCheckAllChange(e) {
      this.checkAll = e.target.checked;
      this.exportSelectedKeys.splice(0, this.exportSelectedKeys.length);
      if (e.target.checked) {
        this.exportSelectedKeys.push(...Object.keys(this.resourceMap));
        if (this.modulePageDef.designable && this.modulePageDef.uuid) {
          this.exportSelectedKeys.push(this.modulePageDef.uuid);
        }
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
      if (this.modulePageDef.designable && this.modulePageDef.uuid) {
        allKeys.push(this.modulePageDef.uuid);
      }
      this.indeterminate = this.exportSelectedKeys.length && this.exportSelectedKeys.length < allKeys.length;
      this.checkAll = this.exportSelectedKeys.length && this.exportSelectedKeys.length == allKeys.length;
    },
    beginExportSelected() {
      this.exportUuid = JSON.parse(JSON.stringify(this.exportSelectedKeys));
      let exportType = [];
      for (let u of this.exportUuid) {
        exportType.push(this.resType2MemberType(this.resourceMap[u]._RES_TYPE));
      }
      this.exportType = exportType;
      this.$refs.exportDef.show();
    },
    handleExportMenuClick(e) {
      if (e.key == 'exportSelected') {
        this.pleaseSelectExport = true;
      } else if (e.key == 'exportAll') {
        this.exportUuid = Object.keys(this.resourceMap);
        let exportType = [];
        for (let u of this.exportUuid) {
          exportType.push(this.resType2MemberType(this.resourceMap[u]._RES_TYPE));
        }
        this.exportType = exportType;
        this.$refs.exportDef.show();
      }
    },
    refresh: debounce(function () {
      this.resourceLoading = true;
      this.loading = 0;
      this.fetchModuleResSeq();
      this.fetchModuleGroup(() => {
        this.fetchModulePageDefinition();
        this.fetchModuleFormDefinition();
        this.fetchModuleUrlLink();
      });
    }, 300),
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
              this.searchedGroups.push(r.uuid);
              this.openGroup(r.uuid);
              break;
            }
          }
        }
      }
    },
    onFilterChange(e) {
      if (e.target.checked) {
        this.filterTypes.push(e.target.value);
      } else {
        let idx = this.filterTypes.indexOf(e.target.value);
        if (idx != -1) {
          this.filterTypes.splice(idx, 1);
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
    addSubItem(e, groupUuid) {
      this.updateGroupMember(e.item._underlying_vm_.uuid, groupUuid, this.resType2MemberType(e.item._underlying_vm_._RES_TYPE), () => {
        if (groupUuid) {
          this.openGroup(groupUuid);
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
    compType(key) {
      if (key == 'page') {
        return 'PagePreview';
      } else if (key == 'form') {
        return 'DyformPreview';
      }
      return undefined;
    },
    iconType(key) {
      if (key == 'form') {
        return 'pticon iconfont icon-ptkj-biaogeshitu';
      } else if (key == 'page') {
        return 'pticon iconfont icon-ptkj-yemian';
      } else if (key == 'link') {
        return 'pticon iconfont icon-ptkj-lianjiebangding';
      }
      return 'file';
    },
    resourceTypeName(data = {}) {
      if (data._RES_TYPE == 'form') {
        return '表单';
      } else if (data._RES_TYPE == 'page') {
        if (data.wtype == 'vBigscreen') {
          return '大屏';
        }
        return '页面';
      } else if (data._RES_TYPE == 'link') {
        return '链接';
      } else if (data.hasOwnProperty('children')) {
        return '分组';
      }
      return '';
    },
    onClickGroupRow(uuid) {
      let idx = this.groupOpened.indexOf(uuid);
      if (idx != -1) {
        this.groupOpened.splice(idx, 1);
      } else {
        this.groupOpened.push(uuid);
      }
    },
    updateSeq() {
      let seq = [];
      for (let i = 0, len = this.resources.length; i < len; i++) {
        seq.push({
          seq: i + 1,
          title: this.resources[i].name,
          resUuid: this.resources[i].uuid,
          moduleId: this.currentModule.id,
          type: this.resources[i].children != undefined ? 'group' : this.resources[i]._RES_TYPE || 'page'
        });
        if (this.resources[i].children && this.resources[i].children.length) {
          for (let j = 0, jlen = this.resources[i].children.length; j < jlen; j++) {
            seq.push({
              seq: j + 1,
              title: this.resources[i].children[j].name,
              resUuid: this.resources[i].children[j].uuid,
              moduleId: this.currentModule.id,
              type: this.resources[i].children[j]._RES_TYPE || 'page'
            });
          }
        }
      }
      console.log('资源顺序更新: ', seq);
      if (seq.length) $axios.post('/proxy/api/app/module/res/seq/save', seq).then(() => {});
    },
    fetchModulePageDefinition() {
      let _this = this;
      $axios.post('/proxy/api/webapp/page/definition/queryLatestPageDefinitionByAppIds', [this.currentModule.id]).then(({ data }) => {
        let list = data.data;
        if (list) {
          this.pageDefinitions = [];
          for (let p of list) {
            if (p.isDefault) {
              // 模块主页不展示
              this.modulePageDef.uuid = p.uuid;
              this.modulePageDef.id = p.id;
              this.modulePageDef.name = p.name;
              this.modulePageDef._PAGE_TYPE = 'PC';
              this.modulePageDef._RES_TYPE = 'page';
              this.modulePageDef.designable = p.designable; // 允许模块主页被设计
              this.modulePageDef.tipVisible = false;
              this.$emit('modulePageLoaded', this.modulePageDef);
              this.pageContext.handleEvent(`ModulePageResource:UpdateModulePageDesignable`, designable => {
                _this.isDesignableTipVisible = designable;
                _this.modulePageDef.designable = designable;
              });
              continue;
            }
            p._RES_TYPE = 'page';
            p._PAGE_TYPE = p.isPc == '1' ? 'PC' : 'MOBILE';
            this.pageDefinitions.push(p);
          }

          this.loading++;
        }
      });
    },
    fetchModuleUrlLink() {
      $axios
        .get('/proxy/api/security/resource/getModuleMenuResources', { params: { moduleId: this.currentModule.id } })
        .then(({ data }) => {
          if (data.code == 0) {
            this.loading++;
            if (data.data) {
              this.links = [];
              this.links.push(...data.data);
              for (let p of data.data) {
                delete p.children;
                p._RES_TYPE = 'link';
                p._PAGE_TYPE = p.applyTo == 'PC' ? 'PC' : 'MOBILE';
              }
            }
          }
        });
    },
    updateGroupMember(memberUuid, groupUuid, type, callback) {
      $axios.get('/proxy/api/app/module/resGroup/updateMember', { params: { memberUuid, groupUuid, type } }).then(({ data }) => {
        if (data.data) {
          if (typeof callback == 'function') {
            callback.call(this);
          }
        }
      });
    },
    fetchModuleFormDefinition() {
      $axios
        .get('/proxy/api/dyform/definition/queryModuleFormDefinition', { params: { moduleId: this.currentModule.id } })
        .then(({ data }) => {
          this.loading++;
          // 仅展示最新版本的数据
          let list = orderBy(
            data.data,
            [
              o => {
                return parseFloat(o.version);
              }
            ],
            ['desc']
          );
          let added = [];
          this.formDefinitions = [];
          for (let p of list) {
            if (!added.includes(p.id)) {
              p._RES_TYPE = 'form';
              p._PAGE_TYPE = p.formType == 'P' || p.formType == 'V' ? 'PC' : 'MOBILE';
              this.formDefinitions.push(p);
              added.push(p.id);
            } else {
              // TODO: 多个版本的情况下，删除等操作需要处理
            }
          }
        });
    },
    fetchModuleResSeq() {
      $axios.get('/proxy/api/app/module/res/seq/list', { params: { moduleId: this.currentModule.id } }).then(({ data }) => {
        this.resSeqLoaded = true;
        this.resSeq = data.data;
      });
    },
    fetchModuleGroup(callback) {
      $axios
        .all([
          $axios.get('/proxy/api/app/module/resGroup/list', { params: { moduleId: this.currentModule.id } }),
          $axios.get('/proxy/api/app/module/resGroup/member', { params: { moduleId: this.currentModule.id } })
        ])
        .then(
          $axios.spread((groups, members) => {
            //当这两个请求都完成的时候会触发这个函数，两个参数分别代表返回的结果
            this.groups = groups.data.data;
            this.groupOpened = [];
            for (let g of this.groups) {
              if (this.defaultExpandAllGroup) {
                this.groupOpened.push(g.uuid);
              }
            }
            if (members.data.data) {
              this.groupMembers = members.data.data;
            }
            this.loading++;
            callback.call(this);
          })
        );
    },
    exitRename(e, res) {
      this.$set(res, '_RENAME', false);
      this.$set(res, '_RENAMING', false);
    },
    renameGroup(e, res) {
      let name = e.target.previousElementSibling.value.trim();
      this.$set(res, '_RENAMING', true);
      $axios.get(`/proxy/api/app/module/resGroup/rename`, { params: { uuid: res.uuid, name } }).then(({ data }) => {
        if (data.data) {
          res.name = name;
          this.exitRename(e, res);
        } else {
          this.$message.error('重命名分组异常');
        }
      });
    },
    deleteGroup(res, index) {
      this.$set(res, '_LOADING', true);
      $axios.get(`/proxy/api/app/module/resGroup/delete`, { params: { uuid: res.uuid } }).then(({ data }) => {
        if (data.data) {
          let groupIndex = this.groups.findIndex(group => group.uuid == res.uuid);
          if (groupIndex != -1) {
            this.groups.splice(groupIndex, 1);
          }
          this.resources.push(...res.children);
          this.resources.splice(index, 1);
          this.updateSeq();
          this.$set(res, '_LOADING', false);
        } else {
          this.$message.error('删除分组异常');
        }
      });
    },
    confirmNewGroup(e) {
      let name = this.createGroupName;
      // if (e) {
      //   name = e.target.previousElementSibling.value.trim();
      // }
      if (name) {
        this.saveGrpLoading = true;
        $axios.post(`/proxy/api/app/module/resGroup/save`, { name, moduleId: this.currentModule.id }).then(({ data }) => {
          this.groups.push({
            uuid: data.data,
            name
          });
          this.resources.push({ uuid: data.data, name, children: [] });
          this.updateSeq();
          this.saveGrpLoading = false;
          this.createGroupVisible = false;
          this.$message.success('分组添加成功');
          // e.target.previousSibling.__vue__.handleReset();
        });
      } else {
        this.$message.error('请填写分组名称');
      }
    },
    addResourceItem(data) {
      data._PAGE_TYPE = this.pageType;
      let add = true;
      if (data._RES_TYPE == 'page' || this.createResourceType == 'CreatePage' || this.createResourceType == 'CreateBigscreen') {
        data._RES_TYPE = 'page';
        this.pageDefinitions.push(data);
      } else if (data._RES_TYPE == 'form' || this.createResourceType == 'CreateDyform') {
        data._RES_TYPE = 'form';
        this.formDefinitions.push(data);
      } else if (data._RES_TYPE == 'link' || this.createResourceType == 'CreateLink') {
        data._RES_TYPE = 'link';
        add = document.querySelector("[data-uuid='" + data.uuid + "']") == null;
        if (add) {
          this.links.push(data);
        }
      }
      if (add) {
        if (data.groupUuid != undefined) {
          this.groupOpened.push(data.groupUuid);
          for (let r of this.resources) {
            if (r.uuid == data.groupUuid) {
              r.children.push(data);
              break;
            }
          }
        } else {
          this.resources.push(data);
        }
        this.updateSeq();
      }
    },
    confirmCreate(e) {
      let _this = this;
      this.$refs.createResource.save(data => {
        if (data && data.uuid) {
          e(true);
          _this.addResourceItem(data);
          if (data._RES_TYPE == 'page' || data._RES_TYPE == 'form') {
            _this.selectResource(data, data.uuid, _this.compType(data._RES_TYPE));
          }
        }
        _this.createResourceType = undefined;
      });
    },
    handleMenuAddClick(e) {
      if (e.key == 'Import') {
        this.$refs.importDef.show();
      } else if (e.key == 'CreateGroup') {
        this.createGroupVisible = true;
        this.createGroupName = '';
      } else {
        this.editResource = {};
        this.openCreateResourceModal({ title: e.item.$el.innerText.trim(), type: e.key });
      }
    },
    onClickFilterType(e, key) {
      let idx = this.filterTypes.indexOf(key);
      if (idx != -1) {
        this.filterTypes.splice(idx, 1);
      } else {
        this.filterTypes.push(key);
      }
      e.stopPropagation();
    },
    handleMenuClick(e) {
      if (e.key == 'refresh') {
        this.refresh();
      } else if (e.key == 'exportAll' || e.key == 'exportSelected') {
        this.handleExportMenuClick(e);
      } else if (e.key == 'dragSort') {
        this.dragSort = !this.dragSort;
      }
    },

    selectFilterResourceType(key) {
      let index = this.filterTypes.indexOf(key);
      if (index == -1) {
        this.filterTypes.push(key);
      } else {
        this.filterTypes.splice(index, 1);
      }
    },
    openCreateResourceModal({ title, type }) {
      this.modalVisible = true;
      this.createResourceTitle = title;
      this.createResourceType = type;
    },
    selectResource(data, key, compName, group) {
      console.log('选择资源: ', arguments);
      this.selectKey = key;
      this.selectGroup = group;
      this.$emit('select', compName, data);
    },
    sortResourceData() {
      let resources = [],
        grpIndexMap = {},
        member2Group = {};
      for (let grp of this.groups) {
        if (this.resSeqMap[grp.uuid] != undefined) {
          grp._SEQ = this.resSeqMap[grp.uuid];
        }
        this.$set(grp, 'children', []);
        resources.push(grp);
        grpIndexMap[grp.uuid] = resources.length - 1;
        if (grp._SEQ == undefined) {
          grp._SEQ = resources.length;
        }
      }
      if (this.groupMembers) {
        for (let m of this.groupMembers) {
          member2Group[m.memberUuid] = m.groupUuid;
        }
      }

      let setGroupMember = data => {
        for (let p of data) {
          if (this.resSeqMap[p.uuid] != undefined) {
            p._SEQ = this.resSeqMap[p.uuid];
          }
          if (member2Group[p.uuid] != undefined) {
            // 添加到分组下
            let _children = resources[grpIndexMap[member2Group[p.uuid]]].children;
            _children.push(p);
            if (p._SEQ == undefined) {
              p._SEQ = _children.length;
            }
          } else {
            resources.push(p);
            if (p._SEQ == undefined) {
              p._SEQ = resources.length;
            }
          }
        }
      };
      setGroupMember.call(this, this.pageDefinitions);
      setGroupMember.call(this, this.formDefinitions);
      setGroupMember.call(this, this.links);

      resources = orderBy(resources, ['_SEQ'], ['asc']);
      for (let r of resources) {
        if (r.children && r.children.length) {
          r.children = orderBy(r.children, ['_SEQ'], ['asc']);
        }
      }

      console.log('排序资源', resources);
      this.resources = resources;
      this.resourceLoading = false;
      this.$emit('resourceLoaded', this.resources);
    },
    deleteLink(uuid, callback) {
      $axios
        .delete(`/proxy/api/security/resource/remove/${uuid}`)
        .then(({ data }) => {
          if (data.code == 0) {
            this.$message.success('删除链接成功');
            if (typeof callback == 'function') {
              callback.call(this, true);
            }
            for (let i = 0, len = this.links.length; i < len; i++) {
              if (this.links[i].uuid == uuid) {
                this.links.splice(i, 1);
                break;
              }
            }
          }
        })
        .catch(error => {
          if (typeof callback == 'function') {
            callback.call(this, false);
          }
        });
    },
    deletePage(item, callback) {
      $axios
        .get(`/proxy/api/webapp/page/definition/deletePageById/${item.id}`)
        .then(({ data }) => {
          if (data.code == 0) {
            this.$message.success('删除页面成功');
            if (typeof callback == 'function') {
              callback.call(this, true);
            }
            for (let i = 0, len = this.pageDefinitions.length; i < len; i++) {
              if (this.pageDefinitions[i].uuid == item.uuid) {
                this.pageDefinitions.splice(i, 1);
                break;
              }
            }
          }
        })
        .catch(() => {
          if (typeof callback == 'function') {
            callback.call(this, false);
          }
        });
    },
    resType2MemberType(type) {
      if (type == 'page') {
        return 'appPageDefinition';
      } else if (type == 'form') {
        return 'formDefinition';
      } else if (type == 'link') {
        return 'resource';
      }
      return 'appModuleResGroup';
    },
    deleteFormDef(item, callback) {
      $axios
        .post('/json/data/services', {
          serviceName: 'formDefinitionService',
          methodName: 'getOne',
          args: JSON.stringify([item.uuid])
        })
        .then(({ data }) => {
          let formType = data.data ? data.data.formType : undefined;
          $axios
            .post('/json/data/services', {
              serviceName: 'dyFormFacade',
              methodName: 'dropForm',
              args: JSON.stringify([item.uuid])
            })
            .then(({ data }) => {
              if (data.code == 0) {
                this.$message.success('删除表单成功');
                // 删除数据模型
                if (formType == 'P') {
                  $axios.get(`/proxy/api/dm/drop/${this.resourceMap[item.uuid].id}`);
                }
                if (typeof callback == 'function') {
                  callback.call(this, true);
                }
                for (let i = 0, len = this.formDefinitions.length; i < len; i++) {
                  if (this.formDefinitions[i].uuid == item.uuid) {
                    this.formDefinitions.splice(i, 1);
                    break;
                  }
                }
              }
            })
            .catch(error => {
              let res = error.response;
              if (res.data && res.data.errorCode) {
                this.$message.error('删除表单失败: ' + res.data.msg);
              }
              if (typeof callback == 'function') {
                callback.call(this, false);
              }
            });
        });
    },

    deleteGroupRes(res, siblings, index, groupUuid) {
      let afterDelete = success => {
        if (success) {
          if (groupUuid != undefined) {
            this.updateGroupMember(res.uuid, null);
          }
          siblings.splice(index, 1);
          if (this.selectKey == res.uuid) {
            this.$emit('select', undefined, undefined);
          }
        }
        this.$set(res, '_LOADING', false);
      };
      this.$set(res, '_LOADING', true);
      if (res._RES_TYPE == 'page') {
        this.deletePage(res, afterDelete);
      } else if (res._RES_TYPE == 'form') {
        this.deleteFormDef(res, afterDelete);
      } else if (res._RES_TYPE == 'link') {
        this.deleteLink(res.uuid, afterDelete);
      }
    },
    copyGroupRes(res, siblings) {
      if (res._RES_TYPE == 'page') {
      } else if (res._RES_TYPE == 'form') {
      }
    },
    onGroupResItemClick({ item, key }, res, siblings, index, groupUuid) {
      if (key == 'delete') {
        let _this = this;
        let name = '[' + res.name + ']' + this.resourceTypeName(res);
        this.$confirm({
          title: '提示',
          content: `确定要删除${name}吗?`,
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
        if (res._RES_TYPE == 'link') {
          this.$set(res, '_LOADING', true);
          this.$refs.exportDef.createExportRequest('resource', res.uuid, `链接: ${res.name}`).then(() => {
            this.$set(res, '_LOADING', false);
          });
        } else if (res._RES_TYPE == 'page') {
          this.exportType = 'appPageDefinition';
          this.exportUuid = res.uuid;
          this.exportTitle = '导出页面';
          this.$refs.exportDef.show();
        } else if (res._RES_TYPE == 'form') {
          this.exportType = 'formDefinition';
          this.exportUuid = res.uuid;
          this.exportTitle = '导出表单';
          this.$refs.exportDef.show();
        }
      } else if (key == 'edit') {
        if (res._RES_TYPE == 'link') {
          this.editResource = res;
          this.createResourceKey = 'createResourceKey_' + new Date().getTime();
          this.openCreateResourceModal({ title: '编辑链接', type: 'CreateLink' });
        }
      }
    },
    onGroupMenuItemClick({ item, key }, res, index) {
      let _this = this;
      if (key == 'delete') {
        let name = '[' + res.name + ']' + this.resourceTypeName(res);
        this.$confirm({
          title: '提示',
          content: `确定要删除${name}分组吗?`,
          okText: '确定',
          cancelText: '取消',
          onOk() {
            _this.deleteGroup(res, index);
          },
          onCancel() {}
        });
      } else if (key == 'rename') {
        this.$set(res, '_RENAME', true);
      } else if (key == 'paste') {
        this.onPaste(res.uuid);
      } else if (key == 'export') {
        if (res.children && res.children.length) {
          this.exportType = 'appModuleResGroup';
          this.exportUuid = res.uuid;
          this.exportTitle = '导出分组';
          this.$refs.exportDef.show();
        } else {
          this.$set(res, '_LOADING', true);
          this.$refs.exportDef.createExportRequest('appModuleResGroup', res.uuid, `分组: ${res.name}`).then(() => {
            this.$set(res, '_LOADING', false);
          });
        }
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
        console.log('开始复制粘贴资源: ', this.resourceMap[this.copyResUuid]);
        let res = this.resourceMap[this.copyResUuid];
        if (res._RES_TYPE == 'page') {
          this.readyCopyPage(res, groupUuid);
          // this.copyPage(res, groupUuid);
        } else if (res._RES_TYPE == 'form') {
          this.readyCopyForm(res, groupUuid);
        } else if (res._RES_TYPE == 'link') {
          this.copyLink(res, groupUuid);
        }
      }
    },
    onConfirmCopyForm(e) {
      if (this.copyFormId) {
        e(true);
      }
    },
    transformIdToUpperCase(e) {
      if (e.target.value) {
        this.copyForm.id = e.target.value.toUpperCase();
        let start = e.target.selectionStart;
        this.$nextTick(() => {
          e.target.setSelectionRange && e.target.setSelectionRange(start, start);
        });
      }
    },
    confirmCopyPage() {
      this.copyPage(this.copyPageFrom, this.copyPageFrom.groupUuid);
    },
    confirmCopyForm() {
      this.$loading();
      this.$refs.copyForm.validate(pass => {
        if (pass) {
          $axios
            .post('/json/data/services', {
              serviceName: 'formDefinitionService',
              methodName: 'isFormExistById',
              args: JSON.stringify([this.copyForm.id])
            })
            .then(({ data }) => {
              if (data.data === false) {
                // 不存在，则拷贝表单
                // 获取表单定义
                $axios
                  .post(`/proxy/api/dyform/definition/getFormDefinitionByUuid?formUuid=${this.copyFormFrom.uuid}`)
                  .then(response => {
                    if (response.data) {
                      let formDef = response.data;
                      formDef.id = this.copyForm.id;
                      formDef.name = this.copyForm.name;
                      formDef.version = '1.0';
                      formDef.uuid = undefined;
                      formDef.tableName = 'UF_' + formDef.id;
                      formDef.relationTbl = formDef.tableName + '_RL';
                      let jsonVSource = JSON.parse(formDef.definitionVjson);
                      delete jsonVSource.id;
                      let json = jsonVSource;
                      json.tableName = formDef.tableName;
                      json.uuid = undefined;
                      json.id = formDef.id;
                      json.name = formDef.name;
                      json.relationTbl = formDef.relationTbl;
                      formDef.definitionVjson = JSON.stringify(json);
                      let fields = json.fields;
                      json = JSON.parse(formDef.definitionJson);
                      json.tableName = formDef.tableName;
                      formDef.definitionJson = JSON.stringify(json);

                      console.log(formDef);
                      let formData = new FormData();
                      formData.set('formDefinition', JSON.stringify(formDef));
                      $axios.post(`/proxy/pt/dyform/definition/save`, formData).then(saveRes => {
                        this.resourceLoading = false;
                        this.$loading(false);
                        if (saveRes.data.success) {
                          this.$message.success('复制表单成功');
                          this.resourceLoading = true;
                          let _resData = {
                            uuid: saveRes.data.data,
                            name: formDef.name,
                            id: formDef.id,
                            _RES_TYPE: 'form',
                            _PAGE_TYPE: formDef.formType == 'M' ? 'MOBILE' : 'PC'
                          };
                          this.formDefinitions.push(_resData);
                          this.copyWidgetDefinitionAsNew(this.copyFormFrom.uuid, _resData);
                          this.afterCopyResourceSuccess(_resData, this.copyForm.groupUuid);
                          this.copyFormVisible = false;
                          if (fields.length > 0 && formDef.formType == 'P') {
                            // 有字段才生成数据模型，否则默认不生成
                            // 生成数据模型
                            let columnJson = JSON.parse(JSON.stringify(DEFAULT_SYS_COLUMNS));
                            columnJson.push(...convertFormField2DataModelColumns(fields, 'P'));
                            // 添加默认字段
                            $axios.get(`/proxy/api/dm/getDetails`, { params: { id: formDef.id } }).then(({ data }) => {
                              if (data.data.uuid) {
                                data.data.createMainTable = false;
                                data.data.createRlTable = false;
                                data.data.columnJson = JSON.stringify(columnJson);
                                $axios.post(`/proxy/api/dm/save`, data.data);
                              } else {
                                $axios.post(`/proxy/api/dm/save`, {
                                  id: formDef.id,
                                  name: formDef.name,
                                  type: 'TABLE',
                                  columnJson: JSON.stringify(columnJson),
                                  createMainTable: false,
                                  createRlTable: false,
                                  // createVnTable: false,
                                  module: formDef.moduleId
                                });
                              }
                            });
                          }
                        } else {
                          this.$message.error('复制表单失败');
                        }
                      });
                    }
                  })
                  .catch(() => {
                    this.$loading(false);
                  });
              } else {
                this.$message.error('已存在ID');
                this.$loading(false);
              }
            });
        } else {
          this.$loading(false);
        }
      });
    },

    copyWidgetDefinitionAsNew(fromPageUuid, formData) {
      $axios
        .get(`/proxy/api/app/widget/copyWidgetDefinitionAsNew`, {
          params: {
            fromPageUuid,
            toPageId: formData.id,
            toPageUuid: formData.uuid
          }
        })
        .then(({ data }) => {})
        .catch(error => {});
    },
    readyCopyPage(item, groupUuid) {
      this.copyPageFrom.id = 'page_' + moment().format('yyyyMMDDHHmmss');
      this.copyPageFrom.groupUuid = groupUuid;
      this.copyPageFrom.name = this.getCopyName(this.pageDefinitions, item.name);
      this.copyPageFrom.uuid = item.uuid;
      this.copyPageFrom.fromName = item.name;
      this.copyPageFrom.fromId = item.id;
      this.copyPageVisible = true;
    },
    readyCopyForm(item, groupUuid) {
      this.copyForm.id = undefined;
      this.copyForm.groupUuid = groupUuid;
      this.copyForm.name = this.getCopyName(this.formDefinitions, item.name);
      this.copyFormFrom.uuid = item.uuid;
      this.copyFormFrom.name = item.name;
      this.copyFormFrom.id = item.id;
      this.copyFormVisible = true;
    },
    getCopyName(sources, originName) {
      let names = [],
        name = originName + ' - 副本';
      for (let p of sources) {
        names.push(p.name);
      }
      let num = 0;
      while (names.includes(name)) {
        name = originName + ` - 副本(${++num})`;
      }
      return name;
    },
    copyPage(item, groupUuid) {
      // #region 复制页面定义
      this.$refs.copyPage.validate(pass => {
        if (pass) {
          this.resourceLoading = true;
          this.copyPageVisible = false;
          $axios
            .post('/json/data/services', {
              serviceName: 'appPageDefinitionService',
              methodName: 'get',
              args: JSON.stringify([item.uuid])
            })
            .then(({ data }) => {
              if (data.code == 0) {
                let pageDefinition = data.data;
                pageDefinition.uuid = undefined;
                pageDefinition.name = item.name || this.getCopyName(this.pageDefinitions, pageDefinition.name);
                let jsonSource = JSON.parse(pageDefinition.definitionJson);
                delete jsonSource.id;
                let json = jsonSource; // 不需要重置组件ID，避免导致二开中使用组件ID进行逻辑处理无效: this.copyDefinitionJson(jsonSource);
                json.uuid = undefined;
                json.title = pageDefinition.name;
                json.id = item.id;
                pageDefinition.id = json.id;
                pageDefinition.definitionJson = JSON.stringify(json);
                pageDefinition.appWidgetDefinitionElements = json.appWidgetDefinitionElements;
                pageDefinition.functionElements = json.functionElements;
                $axios
                  .post('/web/design/savePageDefinition', pageDefinition)
                  .then(({ data }) => {
                    this.resourceLoading = false;
                    if (data.code == 0) {
                      this.$message.success('复制页面成功');
                      this.copyPageVisible = false;
                      let _resData = {
                        uuid: data.data,
                        id: json.id,
                        name: pageDefinition.name,
                        wtype: pageDefinition.wtype,
                        _RES_TYPE: 'page',
                        _PAGE_TYPE: pageDefinition.isPc == '1' ? 'PC' : 'MOBILE'
                      };
                      this.pageDefinitions.push(_resData);
                      this.afterCopyResourceSuccess(_resData, groupUuid);
                    }
                  })
                  .catch(() => {
                    this.copyPageVisible = false;
                  });
              }
            })
            .catch(() => {
              this.copyPageVisible = false;
            });
        }
      });

      // #endregion
    },
    // 拷贝定义json，需要重置内部的组件ID
    copyDefinitionJson(jsonSource) {
      console.log('复制页面, 修改定义前: ', jsonSource);
      try {
        let json = JSON.parse(JSON.stringify(jsonSource));
        // json 组件定义的id替换
        let oldId2New = {};
        let _generateNewId = obj => {
          if (Array.isArray(obj) && obj.length > 0) {
            for (let i = 0, len = obj.length; i < len; i++) {
              _generateNewId(obj[i]);
            }
          } else if (typeof obj == 'object') {
            for (let key in obj) {
              if (key == 'id' && obj.wtype != undefined) {
                // 新ID进行替换
                let _id = oldId2New[obj.id] || generateId();
                oldId2New[obj.id] = _id;
                obj.id = _id;
              } else if (typeof obj[key] == 'object' || Array.isArray(obj[key])) {
                _generateNewId(obj[key]);
              }
            }
          }
        };
        _generateNewId(json);

        // console.log('递归复制新的定义json', JSON.parse(JSON.stringify(json)));
        // 替换引用到旧ID值的定义
        let jsonStr = JSON.stringify(json);
        console.log('修改定义生成的ID映射表: ', oldId2New);
        for (let id in oldId2New) {
          jsonStr = jsonStr.replace(new RegExp(id, 'g'), oldId2New[id]);
        }
        json = JSON.parse(jsonStr);
        console.log('复制页面, 修改定义后: ', json);
        return json;
      } catch (error) {
        console.error(error);
        this.$loading(false);
        this.resourceLoading = false;
        this.$message.error('复制失败');
      }
    },

    copyLink(item, groupUuid) {
      this.resourceLoading = true;
      let name = this.getCopyName(this.links, item.name);
      $axios
        .post('/proxy/api/security/resource/saveBean', {
          name,
          url: item.url,
          remark: item.remark,
          moduleId: this.currentModule.id,
          code: generateId(),
          applyTo: item._PAGE_TYPE
        })
        .then(({ data }) => {
          if (data.code == 0) {
            let _resData = {
              uuid: data.data,
              name,
              remark: item.remark,
              url: item.url,
              _RES_TYPE: 'link',
              _PAGE_TYPE: item._PAGE_TYPE
            };
            this.links.push(_resData);
            this.afterCopyResourceSuccess(_resData, groupUuid);
          }
        });
    },

    afterCopyResourceSuccess(res, groupUuid) {
      if (groupUuid != undefined) {
        this.updateGroupMember(res.uuid, groupUuid, this.resType2MemberType(res._RES_TYPE), () => {
          this.openGroup(groupUuid);
          for (let r of this.resources) {
            if (r.uuid == groupUuid) {
              r.children.push(res);
              break;
            }
          }
          this.updateSeq();
          this.resourceLoading = false;
        });
      } else {
        this.resources.push(res);
        this.updateSeq();
        this.resourceLoading = false;
      }

      this.copyResUuid = undefined;
      this.copyResInGroup = undefined;
    },
    UpdateModulePageDesignable() {
      if (this.isDesignableTipVisible) {
        this.designableTipVisible = true;
      }
      this.isDesignableTipVisible = false;
    }
  },
  watch: {
    resources: {
      deep: true,
      handler() {
        this.$emit('resourceLoaded', this.resources);
      }
    },
    loading: {
      handler(v) {
        if (v >= 4 && this.resSeqLoaded) {
          // 资源数据进行排序
          this.sortResourceData();
        }
      }
    }
  }
};
</script>
