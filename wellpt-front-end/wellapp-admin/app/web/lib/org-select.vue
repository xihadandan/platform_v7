<template>
  <div class="org-select-component">
    <div
      :class="[
        'org-select-input',
        disable ? 'disable' : '',
        readonly ? 'readonly' : '',
        textonly ? 'textonly' : '',
        !readonly && !disable && !textonly && showOpenIcon ? 'showOpenIcon' : ''
      ]"
      @click="clickOpenModal"
    >
      <Icon
        type="pticon iconfont icon-ptkj-zuzhixuanze"
        class="input-suffix-icon"
        @click.stop="openModal"
        v-if="!readonly && !disable && !textonly && showOpenIcon"
      />
      <a-icon
        v-if="!readonly && !disable && !textonly && valueNodes.length > 0"
        type="close-circle"
        theme="filled"
        class="input-suffix-icon close"
        @click.stop="clearAllInput"
      />

      <a-skeleton active :loading="valueLoading" :paragraph="{ rows: 1, width: '95%' }" :title="false">
        <div
          :class="['org-select-selection', inputMaxHeight ? 'ps__child--consume' : '']"
          :style="{ 'max-height': inputMaxHeight ? inputMaxHeight : '' }"
        >
          <template v-if="displayStyle === 'IconLabel'">
            <a-tag
              v-for="(node, i) in valueNodes"
              :key="'tag_' + node.key"
              :title="node.title"
              color="blue"
              @close="closeTag(node, i)"
              :closable="!readonly && !disable && !textonly"
            >
              <Icon :type="orgElementIcon[node.type] || 'file'" />
              <template v-if="titleDisplay == 'titlePath'">{{ node[titleDisplay] || node.title }}</template>
              <template v-else>
                {{ node[titleDisplay] || node.title | textOverflow }}
              </template>
            </a-tag>
          </template>
          <template v-else-if="displayStyle === 'GroupIconLabel'">
            <a-tag
              v-for="(value, key) in valueGroupNodes"
              :key="'tag_group_' + key"
              :title="value.label"
              color="blue"
              @close="closeTagGroup(value.nodes)"
              :closable="!readonly && !disable && !textonly"
            >
              <Icon :type="orgElementIcon[key] || 'file'" />
              {{ value.label | textOverflow }}
            </a-tag>
          </template>
          <template v-else>
            {{ valueLabels.join(separator + ' ') }}
          </template>
        </div>
      </a-skeleton>
    </div>
    <a-modal
      :id="modalId"
      class="org-modal pt-modal"
      :maskClosable="false"
      :title="title"
      :visible="visible"
      :width="900"
      :zIndex="200000"
      :bodyStyle="{ padding: '0px 20px' }"
      :getContainer="getContainer"
      @cancel="onCancelModal"
      @ok="onOkModal"
      ref="modal"
    >
      <div class="org-tree-dialog-body">
        <a-row type="flex" class="f_nowarp" :gutter="12">
          <a-col flex="auto">
            <a-row type="flex" class="f_nowarp left-container">
              <a-col flex="87px" class="menu-box" style="overflow: hidden">
                <a-menu style="width: 100%" @click="menuClick" :selectedKeys="selectedOrgTypeKeys">
                  <a-menu-item v-for="opt in orgTypeOptions" :key="opt.value" :title="opt.label">
                    <div class="menu-box-title w-ellipsis-2">{{ opt.label }}</div>
                  </a-menu-item>
                </a-menu>
              </a-col>
              <a-col flex="auto">
                <div class="org-list-box">
                  <a-row type="flex" class="f_nowarp top-title">
                    <a-col flex="auto" style="width: 0">
                      <span style="line-height: 32px" v-if="orgSelectTreeData.length == 0 || !showBizOrgUnderOrg">
                        <!-- 显示当前导航名称 -->
                        {{ orgTypeValueLabelMap[selectOrgType] }}
                        <a-icon type="loading" v-show="orgSelectTreeLoading" />
                      </span>
                      <template v-else-if="onlyShowBizOrg && orgSelectTreeData.length == 1 && orgSelectTreeData[0].bizOrgId !== undefined">
                        <!-- 当仅展示业务组织且有且只有一个业务组织下拉选项时候 -->
                        <div style="display: flex; align-items: center">
                          <div class="org-tree-option-label" :title="orgSelectTreeData[0].label">{{ orgSelectTreeData[0].label }}</div>
                          <a-tag class="primary-color org-tree-option-label-tag" style="margin-left: 4px">
                            {{ $t('orgSelect.bizOrgLabel', '业务组织') }}
                          </a-tag>
                        </div>
                      </template>
                      <a-tree-select
                        v-else
                        :treeData="orgSelectTreeData"
                        v-model="currentOrgUuid"
                        show-search
                        style="max-width: 100%; min-width: 140px"
                        :dropdown-style="{ maxHeight: '400px', overflow: 'auto', minWidth: '467px' }"
                        :placeholder="$t('orgSelect.placeholder', '请选择组织')"
                        @change="onChangeSelectOrg"
                        treeNodeFilterProp="label"
                        class="org-select-tree"
                        dropdownClassName="org-select-tree-dropdown"
                        :searchPlaceholder="$t('orgSelect.searchOrgPlaceholder', '请输入关键字搜索')"
                        :getPopupContainer="triggerNode => triggerNode.parentNode"
                      >
                        <a-icon slot="suffixIcon" type="caret-down" />
                        <template slot="nodeTitle" slot-scope="node">
                          <div style="display: flex; align-items: center">
                            <div class="org-tree-option-label" :title="node.label">{{ node.label }}</div>
                            <a-tag class="primary-color org-tree-option-label-tag" style="margin-left: 4px">
                              {{ $t('orgSelect.bizOrgLabel', '业务组织') }}
                            </a-tag>
                          </div>
                        </template>
                      </a-tree-select>
                    </a-col>
                    <a-col flex="140px">
                      <!-- 搜索框 -->
                      <a-input
                        style="width: 100%"
                        v-model="orgTypeControl[selectOrgType].searchKeyword"
                        allow-clear
                        @change="onSearchTreeInputChange"
                        @pressEnter="onSearchTree"
                        :placeholder="searchInputPlaceholder"
                      >
                        <template slot="suffix">
                          <a-icon :type="searchingTree ? 'loading' : 'search'" @click.native.stop="onSearchTree" />
                        </template>
                      </a-input>
                    </a-col>
                  </a-row>
                  <a-row type="flex" class="f_nowarp org-list-box-content">
                    <a-col flex="auto" style="width: 0">
                      <div class="flex checkall-content">
                        <div class="f_g_1" style="padding-left: 8px">
                          <a-checkbox
                            v-if="treeView"
                            @change="onChangeSelectTreeAll"
                            :indeterminate="orgTypeControl[selectOrgType].indeterminate"
                            :checked="orgTypeControl[selectOrgType].checkAll"
                            v-show="multiSelect"
                          >
                            {{ $t('orgSelect.selectAll', '全选') }}
                          </a-checkbox>
                          <a-checkbox
                            v-else-if="userCheckable"
                            @change="onChangeSelectAllUser"
                            :indeterminate="orgTypeControl[selectOrgType].userViewIndeterminate"
                            :checked="orgTypeControl[selectOrgType].userViewCheckAll"
                            v-show="multiSelect"
                          >
                            {{ $t('orgSelect.selectAll', '全选') }}
                          </a-checkbox>
                        </div>
                        <div class="f_s_0" style="width: 35px" v-show="treeView && !orgTypeControl[selectOrgType].searchView">
                          <a-button
                            size="small"
                            type="icon"
                            :icon="orgTypeControl[selectOrgType].collapsed ? 'down-square' : 'up-square'"
                            @click.stop="clickCollapseTree"
                            :title="
                              orgTypeControl[selectOrgType].collapsed ? $t('orgSelect.expand', '展开') : $t('orgSelect.collapse', '折叠')
                            "
                          ></a-button>
                        </div>
                        <div
                          class="f_s_0"
                          style="width: 35px"
                          v-show="selectOrgType === 'MyOrg' || selectOrgType === 'TaskUsers' || selectOrgType === 'TaskDoneUsers'"
                        >
                          <a-button
                            type="icon"
                            size="small"
                            @click.stop="onSwitchTreeView"
                            :title="treeView ? $t('orgSelect.treeNodeView', '树形视图') : $t('orgSelect.userView', '用户视图')"
                          >
                            <Icon
                              :type="
                                treeView ? 'pticon iconfont icon-ptkj-liebiaoshitu' : 'pticon iconfont icon-ptkj-zuzhijiagoufenjishitu'
                              "
                            ></Icon>
                          </a-button>
                        </div>
                      </div>
                      <PerfectScrollbar
                        ref="treePsRef"
                        class="org-select-panel tree-panel scrool-panel"
                        :style="{
                          height: '380px'
                          // height: showMultiOrgSelect && orgSelectOptions.length > 1 ? '443px' : '410px'
                        }"
                      >
                        <a-skeleton active :loading="loading" :paragraph="{ rows: 10 }">
                          <a-tree
                            v-show="treeView && !orgTypeControl[selectOrgType].searchView"
                            :blockNode="false"
                            ref="orgTree"
                            show-icon
                            :expandedKeys.sync="orgTypeControl[selectOrgType].expandedKeys"
                            checkable
                            :checkStrictly="checkStrictly"
                            :tree-data="orgTypeControl[selectOrgType].treeData"
                            :load-data="e => onAsyncLoadData(e, selectOrgType)"
                            @expand="onExpand"
                            @select="onSelect"
                            @check="onCheckTree"
                            v-model="checkedKeys"
                            :class="['ant-tree-directory', multiSelect ? '' : 'single-select-tree']"
                            style="--org-select-list-item-width: 180px; --w-tree-title-offset-width-switcher: 14px"
                          >
                            <template slot="nodeIcon" slot-scope="data">
                              <org-element-avatar
                                :nodeData="data"
                                :orgElementIcon="orgElementIcon"
                                :titleField="titleField"
                              ></org-element-avatar>
                            </template>
                            <template slot="nodeTitle" slot-scope="data">
                              <label :title="data[titleField]" :class="['title']">
                                {{ data[titleField] }}
                                <!-- <span v-if="data.type == 'user' && data.titlePath" class="sub-title" :title="data.titlePath || data.title">
                                  {{ formatSearchTitlePath(data) }}
                                </span> -->
                              </label>
                            </template>
                          </a-tree>
                          <!-- 展示搜索结果 -->
                          <div
                            v-show="orgTypeControl[selectOrgType].searchView"
                            :class="[multiSelect ? '' : 'single-select-tree', 'search-panel']"
                          >
                            <template v-if="orgTypeControl[selectOrgType].searchResults.length">
                              <template v-for="node in orgTypeControl[selectOrgType].searchResults">
                                <a-checkbox
                                  :checked="selectedKeys.includes(node.key)"
                                  :value="node.key"
                                  class="block"
                                  :key="'ck-search-node-' + node.key"
                                  v-if="node.checkable"
                                  @change="e => onSearchCheckChange(e, node)"
                                >
                                  <div class="flex f_y_c" style="padding-left: 4px">
                                    <div class="f_s_0" style="width: 30px">
                                      <org-element-avatar
                                        :nodeData="node"
                                        :orgElementIcon="orgElementIcon"
                                        :titleField="titleField"
                                      ></org-element-avatar>
                                    </div>
                                    <div class="f_g_1 w-ellipsis-1 title">
                                      {{ node.title }}
                                      <span v-if="node.titlePath != undefined" class="sub-title" :title="node.titlePath || node.title">
                                        {{ formatSearchTitlePath(node) }}
                                      </span>
                                    </div>
                                  </div>
                                </a-checkbox>
                                <span class="uncheckable-search-item" v-else :key="'ck-search-node-' + node.key">
                                  <div class="flex f_y_c">
                                    <div class="f_s_0" style="width: 30px">
                                      <org-element-avatar
                                        :nodeData="node"
                                        :orgElementIcon="orgElementIcon"
                                        :titleField="titleField"
                                      ></org-element-avatar>
                                    </div>
                                    <div class="f_g_1 w-ellipsis-1 title">
                                      {{ node.title }}
                                      <span v-if="node.titlePath != undefined" class="sub-title" :title="node.titlePath || node.title">
                                        {{ formatSearchTitlePath(node) }}
                                      </span>
                                    </div>
                                  </div>
                                </span>
                              </template>
                            </template>
                            <a-empty :image="simpleImage" v-else />
                          </div>
                          <!-- 仅组织树视图才支持切换到用户列表视图 -->
                          <a-row
                            type="flex"
                            v-show="
                              !orgTypeControl[selectOrgType].searchView &&
                              !treeView &&
                              (selectOrgType == 'MyOrg' || selectOrgType == 'TaskUsers' || selectOrgType == 'TaskDoneUsers')
                            "
                            :class="[multiSelect ? '' : 'single-select-tree', 'tree-user']"
                          >
                            <a-col :span="23" style="padding-right: 4px">
                              <!-- <a-checkbox-group name="user-checkboxgroup"> -->
                              <div class="spin-center" v-if="letterUserLoading" style="margin-top: 200px">
                                <a-spin />
                              </div>
                              <template v-else>
                                <template v-for="group in letterUserGroup">
                                  <!-- <div :key="'user_group_' + i" :letter="letters[i] || '_'" v-if="group.length > 0"> -->
                                  <template v-for="node in group">
                                    <a-checkbox
                                      :value="node.key"
                                      :class="['block', !userCheckable ? 'no-check' : undefined]"
                                      :key="'ck-user-' + node.key"
                                      :checked="selectedKeys.includes(node.key)"
                                      @change="e => onSearchCheckChange(e, node)"
                                    >
                                      <div class="flex f_y_c" style="padding-left: 4px">
                                        <div class="f_s_0" style="width: 30px">
                                          <org-element-avatar
                                            :nodeData="node"
                                            :orgElementIcon="orgElementIcon"
                                            :titleField="titleField"
                                          ></org-element-avatar>
                                        </div>
                                        <div class="f_g_1 w-ellipsis-1 title">
                                          <span :title="node.title">{{ node.title }}</span>
                                          <span v-if="node.titlePath != undefined" class="sub-title" :title="node.titlePath || node.title">
                                            {{ formatSearchTitlePath(node) }}
                                          </span>
                                        </div>
                                      </div>
                                    </a-checkbox>
                                  </template>
                                  <!-- </div> -->
                                </template>
                              </template>
                              <!-- </a-checkbox-group> -->
                            </a-col>
                            <!-- <a-col flex="20px"></a-col> -->
                          </a-row>
                        </a-skeleton>
                      </PerfectScrollbar>

                      <a-row
                        v-show="
                          !orgTypeControl[selectOrgType].searchView &&
                          !treeView &&
                          (selectOrgType == 'MyOrg' || selectOrgType == 'TaskUsers' || selectOrgType == 'TaskDoneUsers')
                        "
                        class="tree-user tree-user-letter"
                      >
                        <Scroll style="height: 350px">
                          <a-col flex="20px">
                            <ul>
                              <li
                                v-for="(a, i) in letters"
                                :key="'leter_' + a"
                                @click.stop="onLetterClick(a, i)"
                                :class="selectedLetter === a ? 'selected' : ''"
                              >
                                {{ a.toUpperCase() }}
                              </li>
                            </ul>
                          </a-col>
                        </Scroll>
                      </a-row>
                    </a-col>
                    <!-- 预览节点下的用户视图 -->
                    <a-col flex="156px" v-if="previewUserUnderNode" class="preview-user-box">
                      <div style="line-height: 30px; padding-top: 12px">
                        <a-input-search
                          v-model.trim="previewNodeUserPage.searchWord"
                          :placeholder="
                            previewNodeUserPage.total > 0
                              ? $t(
                                  'orgSelect.countPreviewUser',
                                  { count: previewNodeUserPage.total },
                                  '成员 ' + previewNodeUserPage.total + ' 个'
                                )
                              : undefined
                          "
                          allow-clear
                          @change="onChangeSearchPreviewNodeUser"
                          class="preview-node-user-search"
                        />

                        <!-- <div v-else-if="previewNodeUserPage.total > 0" style="display: flex; align-items: center; justify-content: space-around">
                <label>成员 {{ previewNodeUserPage.total }} 个</label>
                <a-icon type="search" @click="previewNodeUserPage.resultSearchable = true" />
              </div> -->
                      </div>
                      <div v-show="previewUserNodes.length && multiSelect && userCheckable">
                        <div class="checkall-content" style="padding-left: 8px">
                          <a-checkbox
                            v-show="multiSelect && userCheckable"
                            @change="onCheckedAllPrevieUser"
                            :indeterminate="indeterminatePreviewUser"
                            :checked="checkAllPreviewUser"
                          >
                            {{ $t('orgSelect.selectAll', '全选') }}
                          </a-checkbox>
                        </div>
                      </div>
                      <PerfectScrollbar
                        class="org-select-panel tree-panel preview-user-panel scrool-panel"
                        :style="{
                          paddingBottom: '12px',
                          paddingRight: '12px',
                          marginRight: '-8px',
                          height: previewNodeUserPageHeight
                        }"
                      >
                        <div v-if="previewNodeUserLoading && previewNodeUserPage.pageIndex == 1" class="spin-center">
                          <a-spin />
                        </div>
                        <div :class="[multiSelect ? '' : 'single-select-tree']">
                          <template v-for="node in previewUserNodes">
                            <a-checkbox
                              :value="node.key"
                              :class="['block', !userCheckable ? 'no-check' : undefined]"
                              :key="'preview-ck-user-' + node.key"
                              :checked="selectedKeys.includes(node.key)"
                              @change="e => onCheckPreviewUser(e, node)"
                            >
                              <div class="flex f_y_c" style="padding-left: 4px">
                                <div class="f_s_0" style="width: 30px">
                                  <org-element-avatar
                                    :nodeData="node"
                                    :orgElementIcon="orgElementIcon"
                                    :titleField="titleField"
                                  ></org-element-avatar>
                                </div>
                                <div class="f_g_1 w-ellipsis-1 title" :title="node.title">
                                  {{ node.title }}
                                </div>
                              </div>
                            </a-checkbox>
                          </template>
                        </div>
                        <div
                          v-if="previewNodeUserLoading && previewNodeUserPage.pageIndex > 1"
                          style="text-align: center; color: var(--w-primary-color); font-size: var(--w-font-size-base)"
                        >
                          <a-icon slot="indicator" type="loading" spin />
                          {{ $t('orgSelect.loading', '加载中') }}
                        </div>
                      </PerfectScrollbar>
                      <a-button-group v-if="previewNodeUserPage.totalPage > 1" class="preview-node-user-pagination">
                        <a-button icon="up" @click="e => onNextPreviewUser(-1)" :disabled="previewNodeUserPage.pageIndex <= 1"></a-button>
                        <a-button
                          icon="down"
                          @click="e => onNextPreviewUser(1)"
                          :disabled="previewNodeUserPage.pageIndex == previewNodeUserPage.totalPage"
                        ></a-button>
                      </a-button-group>
                    </a-col>
                  </a-row>
                </div>
              </a-col>
            </a-row>
          </a-col>
          <a-col flex="268">
            <div class="right-container">
              <div class="org-list-box">
                <a-row type="flex" class="f_nowarp top-title">
                  <a-col flex="auto" style="width: 0">
                    <span style="line-height: 32px">
                      <!-- 显示当前导航名称 -->
                      {{ $t('orgSelect.selectedLabel', '已选') }}({{ selectedNodes.length }})
                    </span>
                  </a-col>
                  <a-col flex="144px">
                    <a-input
                      style="width: 100%"
                      v-model="selectionSearchKeyword"
                      allowClear
                      @pressEnter="onSearchSelection"
                      @change="onSearchSelectionInputChange"
                    >
                      <a-icon type="search" slot="suffix" @click.native.stop="onSearchSelection" />
                    </a-input>
                  </a-col>
                </a-row>
                <PerfectScrollbar class="org-select-panel selected-panel scrool-panel" style="margin: 0px -24px 0 -8px; height: 388px">
                  <ul class="selected-node-ul">
                    <template v-for="(n, i) in filterSelections || selectedNodes">
                      <li :key="'li-selected-user-' + n.key" :title="n.title">
                        <!-- <a-popover placement="right" :title="null">
                    <template slot="content">
                      <div>{{ nodePath[n.key].namePath }}</div>
                    </template>

                    <div>
                      <Icon :type="orgElementIcon[n.type] || 'file'" />
                      {{ n.title }}
                      <a-button type="link" icon="close" class="close" size="small" @click.stop="onClickDelSelectedNode(n, i)" />
                    </div>
                  </a-popover> -->
                        <div class="flex f_y_c" style="padding-left: 4px">
                          <div class="f_s_0" style="width: 30px">
                            <org-element-avatar
                              :nodeData="n"
                              :orgElementIcon="orgElementIcon"
                              :titleField="titleField"
                            ></org-element-avatar>
                          </div>
                          <div class="f_g_1">
                            <div class="w-ellipsis-1 title">{{ n.title }}</div>
                            <div v-if="n.titlePath != undefined" class="w-ellipsis-1 sub-title" :title="n.titlePath || n.title">
                              {{ formatSearchTitlePath(n) }}
                            </div>
                          </div>
                          <div class="f_s_0 operation">
                            <span
                              :title="$t('orgSelect.dragToSort', '拖动排序')"
                              class="ant-btn ant-btn-text ant-btn-sm ant-btn-background-ghost"
                            >
                              <Icon type="pticon iconfont icon-ptkj-tuodong"></Icon>
                            </span>
                            <a-button
                              ghost
                              type="text"
                              icon="close"
                              size="small"
                              :title="$t('orgSelect.delete', '删除')"
                              @click.stop="onClickDelSelectedNode(n, i)"
                            />
                          </div>
                        </div>
                      </li>
                    </template>
                  </ul>
                </PerfectScrollbar>
                <div style="margin: 0px -12px 0 -8px" v-show="selectedNodes.length">
                  <a-button block @click.stop="onClearSelection" class="clear-btn">
                    <Icon type="pticon iconfont icon-xmch-qingkongqingchu"></Icon>
                    {{ $t('orgSelect.clear', '清空') }}
                  </a-button>
                </div>
              </div>
            </div>
          </a-col>
        </a-row>
      </div>
      <template slot="footer">
        <slot name="footerDescription"></slot>
        <a-button type="default" @click="onCancelModal">{{ $t('orgSelect.cancelText', '取消') }}</a-button>
        <a-button type="primary" @click="onOkModal">{{ okText }}</a-button>
      </template>
    </a-modal>
  </div>
</template>

<script type="text/babel">
import './org-select.less';
import { orderBy, difference, pullAll, uniqBy, debounce, isEmpty } from 'lodash';
import { Empty } from 'ant-design-vue';
import { conductCheck } from 'ant-design-vue/es/vc-tree/src/util.js';
import { generateId } from '@framework/vue/utils/util';
import orgElementAvatar from './org-element-avatar.vue';
import md5 from '@framework/vue/utils/md5';

export default {
  name: 'OrgSelect',
  inject: ['$tempStorage', 'dyform'],
  props: {
    title: {
      type: String,
      default: function () {
        return this.$t('orgSelect.modalTitle', '选择人员');
      }
    },

    value: {
      type: [String, Array]
    },

    isPathValue: {
      // 值以路径形式返回
      type: Boolean,
      default: false
    },

    titleDisplay: {
      type: String,
      default: 'title',
      validator: function (v) {
        return ['title', 'shortTitle', 'titlePath'].includes(v);
      }
    },
    titleField: {
      // 节点标题字段：可以使用全称或者简称来展示树节点
      type: String,
      default: 'title',
      validator: function (v) {
        return ['title', 'shortTitle'].includes(v);
      }
    },
    checkableTypes: {
      // unit / job / dept / user 以及自定义组织模型的类型
      type: Array
    },
    uncheckableTypes: {
      type: Array
    },
    checkableTypesOfOrgType: {
      // 按选择项类型区分可选模型的类型
      type: Object,
      default() {
        return {};
      }
    },
    separator: {
      // 分隔符
      type: String,
      default: ';'
    },
    displayStyle: {
      type: String,
      default: 'IconLabel',
      validator: function (v) {
        return ['Label', 'IconLabel', 'GroupIconLabel'].includes(v);
      }
    },
    readonly: {
      type: Boolean,
      default: false
    },
    disable: {
      type: Boolean,
      default: false
    },
    showOpenIcon: {
      type: Boolean,
      default: true
    },
    textonly: {
      type: Boolean,
      default: false
    },
    // 组织输入框高度，最小32px,如果设置最大高度，超出部分，出现下拉滚动条
    inputMaxHeight: {
      type: String,
      default: ''
    },
    orgUuid: String, // 指定组织
    orgVersionId: String, // 指定组织版本号
    orgVersionIds: Array, // 可选的组织版本ID列表
    bizOrgId: String, // 指定业务组织ID,
    orgIdOptions: Object, // 组织ID选择项配置<组织ID，业务组织ID列表>
    orgType: {
      // 可选择的组织选择项类型
      type: [String, Array],
      default: function () {
        return ['MyOrg', 'MyLeader'];
      }
    },
    defaultOrgType: String,
    // 参数
    params: {
      type: Object,
      default() {
        return {};
      }
    },
    // 多选
    multiSelect: {
      type: Boolean,
      default: true
    },
    // 视图风格{MyOrg: 'tree/list'}
    viewStyles: {
      type: {
        type: Object,
        default() {}
      }
    },
    orgTypeExtensions: Array,
    enableCache: {
      type: Boolean,
      default: false
    },
    forcePreviewUserUnderNode: Object,
    showBizOrgUnderOrg: {
      // 展示组织选择下的业务组织
      type: Boolean,
      default: true
    },
    onlyShowBizOrg: {
      // 仅展示业务组织下拉选择
      type: Boolean,
      default: false
    },
    filterNodeData: Object,
    refetchDataOnOpenModal: Boolean,
    getContainer: {
      type: Function,
      default: () => document.body
    }
  },
  components: {
    orgElementAvatar
  },
  data() {
    let _value = this.value,
      valueLoading = false;
    if (_value && typeof _value === 'string') {
      let separator = new RegExp(';|,|' + this.separator);
      _value = _value.split(separator);
    }
    if (_value && _value.length > 0) {
      valueLoading = true;
    }
    // TODO：由组织参数设置中配置的组织选择项读取
    let defaultOrgTypeOptions = [
        { value: 'MyOrg', label: this.$t('orgSelect.orgOption.MyOrg', '我的组织') },
        { value: 'MyLeader', label: this.$t('orgSelect.orgOption.MyLeader', '我的领导') },
        { value: 'MyDept', label: this.$t('orgSelect.orgOption.MyDept', '我的部门') },
        { value: 'MyUnderling', label: this.$t('orgSelect.orgOption.MyUnderling', '我的下属') },
        { value: 'TaskUsers', label: this.$t('orgSelect.orgOption.TaskUsers', '办理人') },
        { value: 'TaskDoneUsers', label: this.$t('orgSelect.orgOption.TaskDoneUsers', '已办人员') },
        { value: 'PublicGroup', label: this.$t('orgSelect.orgOption.PublicGroup', '公共群组') }
      ],
      orgTypeValueLabelMap = {};
    defaultOrgTypeOptions.forEach(o => {
      orgTypeValueLabelMap[o.value] = o.label;
    });
    if (this.orgTypeExtensions != undefined) {
      this.orgTypeExtensions.forEach(o => {
        orgTypeValueLabelMap[o.value] = o.label;
      });
    }
    let orgTypeControl = {},
      defaultTypeControl = {
        currentOrgUuid: '', // 当前组织
        selectedOrgVersionId: '', // 当前组织版本
        fetched: false, // 数据已拉取
        treeData: [], //树形数据
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
        asyncOrg: new Set(),
        asyncUser: new Set()
      },
      selectedOrgTypeKeys = [],
      orgTypeOptions = [];
    if (typeof this.orgType == 'string') {
      orgTypeOptions.push({ label: orgTypeValueLabelMap[this.orgType] || this.orgType, value: this.orgType });
      selectedOrgTypeKeys = [this.orgType];
      // 不同类型的变量控制
      orgTypeControl[this.orgType] = { ...defaultTypeControl };
    } else if (Array.isArray(this.orgType)) {
      this.orgType.forEach(t => {
        let key = typeof t === 'string' ? t : t.value;
        let _label = typeof t === 'string' ? this.$t('orgSelect.orgOption.' + key, null) : null;
        if (typeof t !== 'string') {
          t.label = this.$t('orgSelect.orgOption.' + t.value, t.label);
        }
        orgTypeOptions.push(typeof t === 'string' ? { label: _label || orgTypeValueLabelMap[t] || t, value: t } : t);
        orgTypeControl[key] = { ...defaultTypeControl };
      });
      selectedOrgTypeKeys = [typeof this.orgType[0] === 'string' ? this.orgType[0] : this.orgType[0].value];
    }
    if (this.defaultOrgType) {
      selectedOrgTypeKeys = [this.defaultOrgType];
    }
    let checkStrictly = true;
    return {
      modalId: generateId(),
      options: [],
      visible: false,
      orgElementIcon: { user: 'user', group: 'team' },
      setting: null,
      loading: true,
      checkStrictly,
      selectedOrgTypeKeys,
      treeView: true,
      letterUserGroup: undefined,
      checkedKeys: checkStrictly ? { checked: _value || [], halfChecked: [] } : [],
      previewUserUnderNode: this.forcePreviewUserUnderNode != undefined ? this.forcePreviewUserUnderNode : false,
      selectOrgType: selectedOrgTypeKeys[0],
      previewUserNodes: [],
      selectedNodes: [],
      valueNodes: [],
      checkedIdPath: [],
      indeterminatePreviewUser: false,
      checkAllPreviewUser: false,
      letters: [],
      selectedLetter: undefined,
      inputWidth: 100,
      orgTypeControl,
      valueLoading,
      nodePath: {},
      orgSelectOptions: [],
      orgSelectTreeData: [],
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
        searchWord: undefined
      },
      searchInputPlaceholder: undefined,
      searchingTree: false,
      orgTypeValueLabelMap,
      orgSelectTreeLoading: false,
      orgElementModelType: ['user', 'job', 'dept', 'unit']
    };
  },
  filters: {
    textOverflow(text) {
      if (text.length > 10) {
        // 超过30个字长度，省略后续字处理
        return text.substr(0, 10) + '...';
      }
      return text;
    }
  },
  computed: {
    userCheckable() {
      // 可选项为空或者包含都代表用户可选
      let checkable = this.checkableTypes == undefined || this.checkableTypes.length == 0 || this.checkableTypes.includes('user');
      if (this.uncheckableTypes != undefined && this.uncheckableTypes.includes('user')) {
        return false;
      }
      return checkable;
    },
    tagMaxWidth() {
      return Math.min(200, this.inputWidth) + 'px';
    },
    valueLabels() {
      let label = [];
      for (let i = 0, len = this.valueNodes.length; i < len; i++) {
        let title = this.valueNodes[i][this.titleDisplay] || this.valueNodes[i].title;
        label.push(title);
      }
      return label;
    },

    selectedKeys() {
      let keys = [];
      for (let i = 0, len = this.selectedNodes.length; i < len; i++) {
        keys.push(this.selectedNodes[i].key);
      }
      return keys;
    },
    valueGroupNodes() {
      // 按类型拆分
      let group = {};
      for (let i = 0, len = this.valueNodes.length; i < len; i++) {
        let n = this.valueNodes[i];
        if (group[n.type] == undefined) {
          group[n.type] = {
            nodes: [],
            label: ''
          };
        }
        group[n.type].nodes.push(n);
        group[n.type].label += (n[this.titleDisplay] || n.title) + ' ';
      }
      return group;
    },

    okText() {
      return this.$t('orgSelect.okText', '确定') + ` (${this.selectedNodes.length}${this.$t('orgSelect.unit')})`;
    },
    memberCount() {
      return this.previewUserNodes.length;
    },

    selectOrgTypeControl() {
      return this.orgTypeControl[this.selectOrgType];
    },
    showMultiOrgSelect() {
      return this.treeView && this.selectOrgType == 'MyOrg' && this.orgUuid == undefined && this.orgVersionId == undefined;
    },
    // 用户预览滚动列表高度
    previewNodeUserPageHeight() {
      if (this.multiSelect && this.userCheckable) {
        // 有全选
        return this.previewNodeUserPage.totalPage > 1 ? '298px' : '340px';
      }
      return this.previewNodeUserPage.totalPage > 1 ? '332px' : '360px';
    },
    isShowNodeFilterSetting() {
      if (this.filterNodeData) {
        if (this.filterNodeData.showType == 'function' && this.filterNodeData.showFunction) {
          return true;
        } else if (this.filterNodeData.showData) {
          return true;
        }
      }
      return false;
    },
    isHideNodeFilterSetting() {
      if (this.filterNodeData) {
        if (this.filterNodeData.hideType == 'function' && this.filterNodeData.hideFunction) {
          return true;
        } else if (this.filterNodeData.hideData) {
          return true;
        }
      }
      return false;
    }
  },
  beforeCreate() {
    this.simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;
  },
  created() {
    if (this.orgUuid) {
      this.currentOrgUuid = this.orgUuid;
    }
    this.getOrgPromise = Promise.resolve(this.currentOrgUuid != undefined);
  },
  beforeMount() {
    this.fetchOrgElementModel();
    //TODO: 后续默认的系统相关特性由后端统一输出到前台
    if (this.currentOrgUuid === undefined && this.orgVersionId === undefined && this.bizOrgId === undefined) {
      // 获取当前系统的默认组织
      this.orgSelectTreeLoading = true;
      this.getOrgSelectOptions();
      this.getOrg().then(data => {
        if (data) {
          this.currentOrgUuid = data.organization.uuid;
          this.initCheckedValue();
        }
      });
    } else if (this.bizOrgId != undefined) {
      this.getBizOrg(() => {
        this.initCheckedValue();
      });
    } else if (this.orgVersionId != undefined) {
      this.orgSelectTreeLoading = true;
      this.getOrgVersion(data => {
        this.initCheckedValue();
        if (this.orgVersionIds && this.orgVersionIds.length == 1 && this.orgVersionIds[0] == this.orgVersionId) {
          if (data.organization && data.organization.bizOrgs && data.organization.bizOrgs.length > 0) {
            this.setBizOrgSelectTreeDataByOrg(data.organization);
          }
        } else {
          this.setBizOrgSelectTreeDataByOrg(data.organization, false);
        }
        this.orgSelectTreeLoading = false;
      });
    } else if (this.currentOrgUuid != undefined) {
      this.orgSelectTreeLoading = true;
      this.getOrg({
        orgUuid: this.currentOrgUuid,
        fetchBizOrg: true
      }).then(data => {
        this.initCheckedValue();
        this.setBizOrgSelectTreeDataByOrg(data.organization);
        this.orgSelectTreeLoading = false;
      });
    }

    if (this.orgVersionIds && !this.bizOrgId && (this.currentOrgUuid || this.orgVersionId)) {
      this.loadOrgVersions().then(orgVersions => {
        orgVersions.forEach(orgVersion => {
          if (orgVersion.orgUuid == this.currentOrgUuid || orgVersion.id == this.orgVersionId) {
            return;
          }
          this.selectedOrgVersionId = this.orgVersionId = null;
          this.setBizOrgSelectTreeDataByOrg(
            {
              uuid: orgVersion.orgUuid,
              id: orgVersion.orgId,
              name: orgVersion.orgName,
              bizOrgs: orgVersion.bizOrgs
            },
            false
          );
        });
      });
    }
  },
  mounted() {
    this.inputWidth = this.$el.querySelector('.org-select-input').getBoundingClientRect().width;
  },
  methods: {
    setBizOrgSelectTreeDataByOrg(org, requiredBizOrg = true) {
      if (!this.matchOrgIdOption(org.id)) {
        return;
      }

      let option = this.orgSelectTreeData.find(item => item.value == org.uuid);
      if (option) {
        return;
      }

      // 初始化组织下拉选择业务组织
      let hasMoreOrgVersion = this.orgVersionIds != undefined && this.orgVersionIds.length > 1;
      let opt = {
        label: hasMoreOrgVersion ? org.name : this.$t('orgSelect.orgOption.MyOrg', '我的组织'),
        value: org.uuid,
        children: []
      };
      let bizOrgOpt = [];
      if (!requiredBizOrg || (org.bizOrgs && org.bizOrgs.length > 0)) {
        let matchBizOrgId = false;
        for (let j = 0, jlen = org.bizOrgs.length; j < jlen; j++) {
          if (!this.matchBizOrgIdOption(org.id, org.bizOrgs[j].id)) {
            continue;
          }
          matchBizOrgId = true;
          let o = {
            label: org.bizOrgs[j].name,
            value: org.bizOrgs[j].uuid,
            bizOrgId: org.bizOrgs[j].id,
            scopedSlots: { title: 'nodeTitle' }
          };
          opt.children.push(o);
          if (this.onlyShowBizOrg) {
            bizOrgOpt.push(o);
          }
        }
        if (matchBizOrgId || !requiredBizOrg) {
          if (this.onlyShowBizOrg) {
            this.orgSelectTreeData.push(...bizOrgOpt);
            this.currentOrgUuid = bizOrgOpt[0].value;
            this.currentBizOrgUuid = bizOrgOpt[0].value;
          } else {
            this.orgSelectTreeData.push(opt);
          }
        }
      }
    },
    matchOrgIdOption(orgId) {
      if (!this.orgIdOptions || isEmpty(this.orgIdOptions)) {
        return true;
      }
      return this.orgIdOptions.hasOwnProperty(orgId);
    },
    matchBizOrgIdOption(orgId, bizOrgId) {
      if (!this.orgIdOptions) {
        return true;
      }
      let bizOrgIds = this.orgIdOptions[orgId];
      if (!bizOrgIds || bizOrgIds.length == 0) {
        return true;
      }
      return bizOrgIds.includes(bizOrgId);
    },
    // 设置图标颜色
    setIconColor(type) {
      if (['dept', 'classify'].includes(type)) {
        return 'var(--w-warning-color)';
      } else if (['job'].includes(type)) {
        return 'var(--w-success-color)';
      }
      return 'var(--w-primary-color)';
    },
    formatSearchTitlePath(node) {
      if (node.titlePath) {
        let paths = node.titlePath.split('/');
        paths.reverse().splice(0, 1);
        return paths.join(' / ');
      }
      return '';
    },
    clearAllInput() {
      // if (this.checkStrictly) {
      //   this.checkedKeys.checked.splice(0, this.checkedKeys.checked.length);
      //   this.checkedKeys.halfChecked.splice(0, this.checkedKeys.halfChecked.length);
      // } else {
      //   this.checkedKeys.splice(0, this.checkedKeys.length);
      // }
      this.valueNodes.splice(0, this.valueNodes.length);
      let checked = this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys;
      if (checked && !Array.isArray(checked)) {
        checked = checked.split(this.separator);
      }
      checked.splice(0, checked.length);

      this.emitValueChange();
    },
    getValueLabel() {
      return this.valueLabels.join(this.separator);
    },
    onSearchTreeInputChange: debounce(function (e) {
      if (e.target.value == '') {
        this.orgTypeControl[this.selectOrgType].searchView = false;
        this.orgTypeControl[this.selectOrgType].searchResults = [];
        this.updateTreeAllCheckboxState();
        this.onSearchTree();
      }
    }, 300),
    onSearchCheckChange(e, node) {
      if (e.target.checked) {
        if (!this.multiSelect) {
          // 单选
          this.selectedNodes.splice(0, this.selectedNodes.length);
        }
        if (!this.selectedKeys.includes(node.key)) {
          this.selectedNodes.push(node);
        }
        if (!this.checkedKeys.checked.includes(node.key)) {
          this.checkedKeys.checked.push(node.key);
        }
      } else {
        let i = this.selectedKeys.indexOf(node.key);
        if (i != -1) {
          this.selectedNodes.splice(i, 1);
        }
        i = this.checkedKeys.checked.indexOf(node.key);
        if (i != -1) {
          this.checkedKeys.checked.splice(i, 1);
        }
      }
      this.updateSearchTreeAllCheckboxState();
      this.updateUserViewAllCheckboxState();
    },
    onSearchSelectionInputChange(e) {
      if (e.target.value === '') {
        this.filterSelections = undefined;
      }
    },
    onSearchSelection() {
      if (this.selectionSearchKeyword) {
        this.filterSelections = [];
        for (let i = 0, len = this.selectedNodes.length; i < len; i++) {
          let item = this.selectedNodes[i];
          let title = this.selectedNodes[i][this.titleField] || this.selectedNodes[i].title;
          if (
            title.indexOf(this.selectionSearchKeyword) != -1 ||
            (item.type == 'user' &&
              item.data &&
              ((item.data.userNo && item.data.userNo.indexOf(this.selectionSearchKeyword) != -1) ||
                (item.data.pinYin && item.data.pinYin.indexOf(this.selectionSearchKeyword) != -1) ||
                (item.data.loginName && item.data.loginName.indexOf(this.selectionSearchKeyword) != -1)))
          ) {
            this.filterSelections.push(this.selectedNodes[i]);
          }
        }
      }
    },
    onSearchTree() {
      let v = this.selectOrgTypeControl.searchKeyword;
      if (this.treeView) {
        if (v) {
          this.searchingTree = true;
          if (this.selectOrgTypeControl.asyncOrg && this.selectOrgTypeControl.asyncOrg.has(this.currentOrgUuid)) {
            this.requestFetchOrgTypeData(this.selectOrgType, Object.assign({}, this.params, { keyword: v })).then(map => {
              let dataList = map.dataList || [];
              this.selectOrgTypeControl.searchResults = dataList;
              this.selectOrgTypeControl.searchView = true;
              this.updateSearchTreeAllCheckboxState();
              this.searchingTree = false;
            });
            return;
          }

          let _keyEntities = this.getTreeKeyEntities();
          let results = [];
          _keyEntities.forEach(n => {
            let dataRef = n.node.componentOptions.propsData.dataRef;
            if (
              dataRef.title.indexOf(v) != -1 ||
              (dataRef.type == 'user' &&
                dataRef.data &&
                ((dataRef.data.userNo && dataRef.data.userNo.indexOf(v) != -1) ||
                  (dataRef.data.pinYin && dataRef.data.pinYin.indexOf(v) != -1) ||
                  (dataRef.data.loginName && dataRef.data.loginName.indexOf(v) != -1)))
            ) {
              //用户可以拼音和登录名搜索
              results.push(n.node.componentOptions.propsData.dataRef);
            }
          });
          this.selectOrgTypeControl.searchResults = results;
          this.selectOrgTypeControl.searchView = true;
          this.updateSearchTreeAllCheckboxState();
          this.searchingTree = false;
        }
      } else {
        this.searchingTree = false;
        // 查询用户
        // 重置掉字母选择，查询就按实际输入查询不限制字母口头，否则如果不想以字母开头去检索，还得做取消字母选择的操作，没有必要
        this.selectedLetter = undefined;
        this.updateLetterUserGroup(v ? { keyword: v } : {});
      }
    },
    onOrgVersionChange() {
      this.selectOrgTypeControl.fetched = false;
      this.getOrgVersion(() => {
        this.fetchOrgTree(() => {
          this.updateHalfChecked();
          this.updateTreeAllCheckboxState();
          this.updateLetterUserGroup();
          this.initCheckedValue();
        });
      });
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
    removeNodeChecked(n) {
      let checked = this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys;
      if (checked && !Array.isArray(checked)) {
        checked = checked.split(this.separator);
      }
      let j = checked.indexOf(n.key);
      if (j != -1) {
        checked.splice(j, 1);
      }
      j = checked.indexOf(n.keyPath);
      if (j != -1) {
        checked.splice(j, 1);
      }
    },
    closeTag(node, i) {
      this.removeNodeChecked(this.valueNodes[i]);
      this.valueNodes.splice(i, 1);

      this.emitValueChange();
    },
    getTreePanelContainer() {
      return document.querySelector(`#${this.modalId} .org-tree-dialog-body .tree-panel`);
    },

    updateHalfChecked(checkedKeys, halfCheckedKeys, checked, node) {
      this.$nextTick(() => {
        let _keyEntities = this.getTreeKeyEntities();
        let result = conductCheck(checkedKeys, checked, _keyEntities, {
          checkedKeys,
          halfCheckedKeys
        });
        this.checkedKeys.halfChecked = result.halfCheckedKeys;

        for (let i = 0, len = this.checkedKeys.checked.length; i < len; i++) {
          let k = this.checkedKeys.checked[i];
          let obj = _keyEntities.has(k) ? _keyEntities.get(k).parent : null;
          while (obj) {
            if (!this.checkedKeys.checked.includes(obj.key) && !this.checkedKeys.halfChecked.includes(obj.key)) {
              this.checkedKeys.halfChecked.push(obj.key);
            }
            obj = obj.parent;
          }
        }

        this.$refs.orgTree && this.$refs.orgTree.$forceUpdate();
      });
    },
    clickOpenModal(e) {
      if (
        (e.target.classList.contains('org-select-input') || e.target.nodeName === 'DIV') &&
        !this.readonly &&
        !this.disable &&
        !this.textonly
      ) {
        this.openModal();
      }
    },
    openModal() {
      let _this = this;
      this.visible = true;
      this.selectOrgType = this.selectedOrgTypeKeys.length > 0 ? this.selectedOrgTypeKeys[0] : undefined;
      this.isInternalChange = true;
      this.selectedNodes = this.valueNodes ? JSON.parse(JSON.stringify(this.valueNodes)) : [];
      // 开启弹框是要重新加载数据，将fetched置为false
      if (this.refetchDataOnOpenModal && this.selectOrgTypeControl && this.selectOrgTypeControl.fetched) {
        this.selectOrgTypeControl.fetched = false;
      }

      // 根据已选值
      if (this.checkStrictly) {
        if (typeof this.checkedKeys.checked === 'string') {
          this.checkedKeys.checked = [];
        }
        this.checkedKeys.checked.splice(0, this.checkedKeys.checked.length);
        this.checkedKeys.halfChecked.splice(0, this.checkedKeys.halfChecked.length);
      } else {
        this.checkedKeys.splice(0, this.checkedKeys.length);
      }
      // 选中节点：
      if (this.selectedNodes.length > 0) {
        let checked = this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys;
        let halfChecked = [];
        for (let i = 0, len = this.selectedNodes.length; i < len; i++) {
          checked.push(this.selectedNodes[i].key);
          if (this.selectedNodes[i].keyPath) {
            halfChecked.push(...this.selectedNodes[i].keyPath.split('/'));
          }
        }
        this.checkedKeys.halfChecked = Array.from(new Set(halfChecked));
      }
      this.getSetting();
      this.getOrgPromise.then(() => {
        this.fetchOrgTree(() => {
          // 树形、列表视图按传入的显示风格切换
          if (this.treeView && this.viewStyles && this.viewStyles[this.selectOrgType] == 'list') {
            this.onSwitchTreeView();
          }
          this.updateHalfChecked(this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys, this.checkedKeys.halfChecked, true);
          this.updateTreeAllCheckboxState();
          this.$nextTick(() => {
            this.isInternalChange = false;
          });
        });
      });

      setTimeout(() => {
        let $scroll = document.querySelectorAll(`#${_this.modalId} .scrool-panel`);
        $scroll.forEach(s => {
          s.__vue__.update();
        });
        import('sortablejs').then(Sortable => {
          Sortable.default.create(document.querySelector(`#${this.modalId} .org-tree-dialog-body .selected-node-ul`), {
            handle: 'li',
            onEnd: function (e) {
              let temp = _this.selectedNodes.splice(e.oldIndex, 1)[0];
              _this.selectedNodes.splice(e.newIndex, 0, temp);
            }
          });
        });
      }, 600);
    },

    onClearSelection() {
      this.selectedNodes = [];
      this.checkedKeys = this.checkStrictly ? { checked: [], halfChecked: [] } : [];
      // this.$refs.orgTree.$forceUpdate();
      // this.previewUserNodes = [];
      this.selectedIndex = -1;
      this.indeterminatePreviewUser = false;
      this.checkAllPreviewUser = false;
      this.treeView = true;
      this.letterUserLoading = true;
      this.letterUserGroup = undefined;
      // 重置类型变量参数
      for (let k in this.orgTypeControl) {
        this.orgTypeControl[k].searchKeyword = undefined;
        this.orgTypeControl[k].searchResults = [];
        this.orgTypeControl[k].searchView = false;
        this.orgTypeControl[k].checkAll = false;
        this.orgTypeControl[k].indeterminate = false;
        this.orgTypeControl[k].previewNode = undefined;
        // this.orgTypeControl[k].collapsed = true;
        // this.orgTypeControl[k].expandedKeys = [];
      }
    },
    onCancelModal() {
      this.visible = false;
      let _this = this;
      _this.$emit('cancel');
      setTimeout(() => {
        _this.resetPreviewNodeUserPage();
        _this.onClearSelection();
      }, 300);
    },
    onOkModal() {
      this.valueNodes = JSON.parse(JSON.stringify(this.selectedNodes)); // 最终结果值
      this.visible = false;
      this.emitValueChange();
      let _this = this;
      setTimeout(() => {
        _this.onClearSelection();
      }, 300);
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
      this.$emit('input', typeof this.value === 'string' || this.value == undefined ? values.join(this.separator) : values);
      let string = typeof this.value === 'string' || this.value == undefined; // 以字符方式返回
      this.isInternalChange = true;
      this.$nextTick(() => {
        this.isInternalChange = false;
      });
      this.$emit('change', {
        value: string ? values.join(this.separator) : values,
        label: string ? this.getValueLabel() : this.valueLabels,
        nodes: this.valueNodes
      });
    },
    onChangePreviewUserCheckGroup() {
      this.deleteUnCheckedSelectedNodes();
    },
    onCheckedAllPrevieUser(e) {
      // 全选预览成员
      this.checkNode(this.previewUserNodes, e.target.checked);
      this.updatePreviewAllCheckboxState();
    },
    onClickDelSelectedNode(node, i) {
      this.selectedNodes.splice(i, 1);
      let idx = this.checkStrictly
        ? this.checkedKeys.checked
          ? this.checkedKeys.checked.indexOf(node.key)
          : -1
        : this.checkedKeys.indexOf(node.key);
      if (idx != -1) {
        this.checkedKeys.checked.splice(idx, 1);
      }

      this.updatePreviewAllCheckboxState();
      this.updateTreeAllCheckboxState();
      this.updateHalfChecked([], [], true);
      this.isInternalChange = true;
      this.$nextTick(() => {
        this.isInternalChange = false;
      });
    },

    checkNode(node, checked) {
      let checkedKeys = this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys;
      let nodes = Array.isArray(node) ? node : [node];
      for (let j = 0, jlen = nodes.length; j < jlen; j++) {
        let n = nodes[j];
        if (checked) {
          if (!this.multiSelect) {
            // 单选
            this.selectedNodes.splice(0, this.selectedNodes.length);
            checkedKeys.splice(0, checkedKeys.length);
          }
          if (!this.selectedKeys.includes(n.key)) {
            this.selectedNodes.push(n);
            checkedKeys.push(n.key);
            this.nodePath[n.key] = { keyPath: n.keyPath, namePath: n.namePath };
          }
        } else {
          checkedKeys.splice(checkedKeys.indexOf(n.key), 1);
          for (let i = 0, len = this.selectedNodes.length; i < len; i++) {
            if (this.selectedNodes[i].key === n.key) {
              this.selectedNodes.splice(i, 1);
              break;
            }
          }
        }
      }

      this.updatePreviewAllCheckboxState();
      this.updateTreeAllCheckboxState();
      this.updateHalfChecked();
      this.isInternalChange = true;
      this.$nextTick(() => {
        this.isInternalChange = false;
      });
    },

    onCheckPreviewUser(e, node) {
      if (this.userCheckable) {
        this.checkNode(node, e.target.checked);
      }
    },

    updatePreviewAllCheckboxState() {
      this.$nextTick(() => {
        let totalChecked = document.querySelectorAll('#' + this.modalId + ' .preview-user-panel :checked').length;
        this.indeterminatePreviewUser = totalChecked > 0 && totalChecked < this.previewUserNodes.length;
        this.checkAllPreviewUser = this.previewUserNodes.length > 0 && totalChecked === this.previewUserNodes.length;
      });
    },

    updateTreeAllCheckboxState() {
      this.$nextTick(() => {
        let checked = JSON.parse(JSON.stringify(this.selectedKeys));
        let total = this.selectOrgTypeControl.treeKeys.checkable.length;
        let totalCheckKeys = [].concat(this.selectOrgTypeControl.treeKeys.checkable);
        pullAll(totalCheckKeys, checked);
        this.selectOrgTypeControl.indeterminate = !!checked.length && totalCheckKeys.length > 0;
        this.selectOrgTypeControl.checkAll = total > 0 && totalCheckKeys.length == 0;
      });
    },
    updateSearchTreeAllCheckboxState() {
      this.$nextTick(() => {
        let totalChecked = document.querySelectorAll('#' + this.modalId + ' .search-panel :checked').length;
        let total = document.querySelectorAll('#' + this.modalId + ' .search-panel .ant-checkbox-input').length;
        this.selectOrgTypeControl.indeterminate = totalChecked > 0 && totalChecked < total;
        this.selectOrgTypeControl.checkAll = total > 0 && totalChecked === total;
      });
    },

    updateUserViewAllCheckboxState() {
      this.$nextTick(() => {
        let checked = JSON.parse(JSON.stringify(this.selectedKeys));
        this.letterUserGroup;
        let total = this.letterUsers ? this.letterUsers.length : 0;
        let totalCheckKeys = [];
        if (this.letterUsers) {
          for (let u of this.letterUsers) {
            totalCheckKeys.push(u.key);
          }
        }
        pullAll(totalCheckKeys, checked);
        this.selectOrgTypeControl.userViewIndeterminate = !!checked.length && totalCheckKeys.length > 0;
        this.selectOrgTypeControl.userViewCheckAll = total > 0 && totalCheckKeys.length == 0;
      });
    },

    getTreeKeyEntities() {
      return this.$refs.orgTree ? this.$refs.orgTree.$refs.tree.$data._keyEntities : [];
    },

    onCheckTree(checkedKeys, { checked, node, checkedNodes }) {
      if (checked) {
        if (!this.multiSelect) {
          // 单选
          this.selectedNodes.splice(0, this.selectedNodes.length);
          if (this.checkStrictly) {
            this.checkedKeys.checked.splice(0, this.checkedKeys.checked.length - 1);
          } else {
            this.checkedKeys.splice(0, this.checkedKeys.length);
          }
        }
        if (!this.selectedKeys.includes(node.eventKey)) {
          this.selectedNodes.push(node.$options.propsData.dataRef);

          this.nodePath[node.eventKey] = {};
          let keyPath = [node.eventKey],
            namePath = [node.dataRef.title];
          let parent = node.$parent;
          while (parent && parent.dataRef) {
            // 业务角色下的用户路径存在节点重复
            if (parent.dataRef.type == 'bizRole' && parent.dataRef.key && parent.dataRef.key.includes('/')) {
              keyPath.push(parent.dataRef.key.split('/')[1]);
            } else {
              keyPath.push(parent.dataRef.key);
            }
            namePath.push(parent.dataRef.title);
            parent = parent.$parent;
          }
          this.nodePath[node.eventKey] = {
            keyPath: keyPath.reverse().join('/'),
            namePath: namePath.reverse().join('/')
          };
          console.log(this.nodePath);
        }
      } else {
        this.deleteUnCheckedSelectedNodes();
      }

      this.updateTreeAllCheckboxState();
      this.updateHalfChecked([node.eventKey], checkedKeys.halfChecked, checked, node);
      this.isInternalChange = true;
      this.$nextTick(() => {
        this.isInternalChange = false;
      });
    },

    deleteUnCheckedSelectedNodes() {
      // 从已选中剔除
      let keys = this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys;
      for (let i = 0; i < this.selectedNodes.length; i++) {
        if (!keys.includes(this.selectedNodes[i].key)) {
          this.selectedNodes.splice(i--, 1);
        }
      }
    },
    onSwitchTreeView() {
      if (this.selectOrgTypeControl.searchView) {
        return;
      }
      this.selectOrgTypeControl.searchKeyword = undefined;
      this.searchInputPlaceholder = undefined;
      this.treeView = !this.treeView;
      if (!this.treeView) {
        this.selectedLetter = undefined;
        this.fetchSortLetters().then(() => {
          this.updateLetterUserGroup();
        });
      }
      this.updateTreeAllCheckboxState();
    },
    updateLetterUserGroup(params) {
      const _this = this;
      this.letterUserLoading = true;
      let setLetterGroup = users => {
        let letterGroup = [];
        for (let i = 0, length = this.letters.length; i <= length; i++) {
          // 字母按顺序添加
          letterGroup.push([]);
        }
        let sortField = _this.$i18n.locale == 'zh_CN' ? 'pinYin' : 'userName';
        users.forEach(d => {
          if (d.data[sortField]) {
            let pinYin = d.data[sortField].trim();
            let c = pinYin.charAt(0);
            let index = _this.letters.indexOf(c);
            if (index == -1) {
              index = _this.letters.length;
            }
            letterGroup[index].push(d);
          }
        });
        this.letterUsers = users;
        this.letterUserGroup = letterGroup;
        this.letterUserLoading = false;
        this.updateUserViewAllCheckboxState();
      };
      this.searchInputPlaceholder = undefined;
      if (this.selectOrgTypeControl.asyncUser && this.selectOrgTypeControl.asyncUser.has(this.currentOrgUuid)) {
        // 只会展示第一页 100 个数据，其他数据由点击字母或者搜索加载
        this.searchInputPlaceholder = this.$t('orgSelect.searchInputPlaceholder', '请输入用户姓名或者拼音搜索更多');
        this.fetchOrgTypeUsers(this.selectOrgType, Object.assign({}, { pageIndex: 0, pageSize: 100 }, params || {})).then(({ users }) => {
          setLetterGroup(users);
        });
        return;
      }

      let _keyEntities = this.getTreeKeyEntities();
      let users = [];
      _keyEntities.forEach(k => {
        let d = k.node.componentOptions.propsData.dataRef;
        if ((d.data && d.data.type === 'user') || d.type == 'user') {
          if (
            (d.data.pinYin &&
              (this.selectOrgTypeControl.searchKeyword == undefined ||
                this.selectOrgTypeControl.searchKeyword == '' ||
                d.data.pinYin.indexOf(this.selectOrgTypeControl.searchKeyword)) != -1) ||
            d.title.indexOf(this.selectOrgTypeControl.searchKeyword) != -1
          ) {
            users.push(d);
          }
        }
      });
      setLetterGroup(users);
    },
    onLetterClick(letter, index) {
      const _this = this;
      _this.selectedLetter = letter;
      if (
        // 非异步加载且非搜索关键字用户情况下, 通过点击字母滚动到指定位置
        !(this.selectOrgTypeControl.asyncUser && this.selectOrgTypeControl.asyncUser.has(this.currentOrgUuid)) &&
        (this.selectOrgTypeControl.searchKeyword == undefined || this.selectOrgTypeControl.searchKeyword == '')
      ) {
        let users = _this.letterUserGroup[index];
        if (users && users.length > 0) {
          let firstUser = users[0];
          let userEl = document.querySelector(`.org-tree-dialog-body .tree-user input[value='${firstUser.key}']`);
          userEl && userEl.scrollIntoView();
        }
        return;
      }
      this.updateLetterUserGroup({
        letter: letter
      });
    },
    onChangeSelectAllUser(e) {
      // 用户拼音视图下的全选
      if (this.letterUserGroup) {
        for (let group of this.letterUserGroup) {
          if (group.length > 0) {
            for (let user of group) {
              if (!this.selectOrgTypeControl.userViewCheckAll) {
                if (!this.selectedKeys.includes(user.key)) {
                  this.selectedNodes.push(user);
                }
              } else {
                if (this.selectedKeys.includes(user.key)) {
                  this.selectedNodes.splice(this.selectedKeys.indexOf(user.key), 1);
                }
              }
            }
          }
        }
      }
      this.updateUserViewAllCheckboxState();
    },
    onChangeSelectTreeAll(e) {
      if (this.selectOrgTypeControl.searchView) {
        let searchResults = this.selectOrgTypeControl.searchResults;
        if (e.target.checked) {
          for (let i = 0, len = searchResults.length; i < len; i++) {
            if (!this.selectedKeys.includes(searchResults[i].key) && searchResults[i].checkable) {
              this.selectedNodes.push(searchResults[i]);
            }
          }
        } else {
          let keys = [];
          searchResults.forEach(s => {
            if (s.checkable) {
              keys.push(s.key);
            }
          });
          for (let i = 0; i < this.selectedNodes.length; i++) {
            if (keys.includes(this.selectedNodes[i].key)) {
              this.selectedNodes.splice(i--, 1);
            }
          }
        }
        this.updateSearchTreeAllCheckboxState();
        return;
      }

      if (e.target.checked) {
        let orginalChecked = this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys;
        let checked = Array.from(new Set(orginalChecked.concat(this.selectOrgTypeControl.treeKeys.checkable)));
        if (this.checkStrictly) {
          this.checkedKeys.checked = checked;
          this.checkedKeys.halfChecked = [];
        } else {
          this.checkedKeys = checked;
        }
        // TODO: 自动展开第一级
        this.$refs.orgTree.$forceUpdate();
        // 差异选中的值作为新增的
        let diff = difference(checked, orginalChecked);
        let _this = this;
        let _keyEntities = this.getTreeKeyEntities();
        diff.forEach(d => {
          _this.selectedNodes.push(_keyEntities.get(d).node.componentOptions.propsData.dataRef);
        });
      } else {
        let keys = this.selectOrgTypeControl.treeKeys.checkable;
        let orginalChecked = this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys;
        pullAll(orginalChecked, keys); // 从原选中集合中剔除
        this.deleteUnCheckedSelectedNodes();
        this.$refs.orgTree.$forceUpdate();
      }
      this.updateTreeAllCheckboxState();
    },
    clickCollapseTree() {
      this.selectOrgTypeControl.collapsed = !this.selectOrgTypeControl.collapsed;
      if (this.selectOrgTypeControl.collapsed) {
        this.selectOrgTypeControl.expandedKeys = [];
        if (this.treeView && !this.orgTypeControl[this.selectOrgType].searchView) {
          if (this.$refs.treePsRef && this.$refs.treePsRef.ps) {
            // 收起时，滚动条回到顶部
            this.$refs.treePsRef.ps.element.scrollTop = 0;
          }
        }
      } else {
        let _keyEntities = this.getTreeKeyEntities();
        this.selectOrgTypeControl.expandedKeys = Array.from(_keyEntities.keys());
      }
    },
    menuClick({ key }) {
      this.selectOrgType = key;
      this.selectedOrgTypeKeys = [key];
      this.resetPreviewNodeUserPage();
      this.fetchOrgTree(() => {
        this.updateHalfChecked();
        this.updateTreeAllCheckboxState();

        // 多个支持用户列表的类型切换
        this.$nextTick(() => {
          if (this.selectOrgType === 'MyOrg' || this.selectOrgType === 'TaskUsers' || this.selectOrgType === 'TaskDoneUsers') {
            if (!this.treeView) {
              this.selectedLetter = undefined;
              this.updateLetterUserGroup();
            } else {
              if (this.viewStyles && this.viewStyles[this.selectOrgType] == 'list') {
                this.onSwitchTreeView();
              }
            }
          } else {
            this.treeView = true;
          }
        });
      });
    },
    onChangeSearchPreviewNodeUser: debounce(function (e) {
      if (this.selectOrgTypeControl.previewNode) {
        if (this.selectOrgTypeControl.asyncUser && this.selectOrgTypeControl.asyncUser.has(this.currentOrgUuid)) {
          this.previewNodeUserPage.pageIndex = -1;
          this.onNextPreviewUser(1, true);
          return;
        }
        this.filterPreviewNodeUserChildren(this.selectOrgTypeControl.previewNode);
      }
    }, 300),
    onNextPreviewUser(index, isSearch) {
      if (!this.previewNodeUserLoading) {
        if (this.selectOrgTypeControl.asyncUser && this.selectOrgTypeControl.asyncUser.has(this.currentOrgUuid)) {
          this.previewNodeUserLoading = true;
          this.previewNodeUserPage.pageIndex += index;
          this.fetchOrgTypeUsers(this.selectOrgType, {
            orgElementId: this.selectOrgTypeControl.previewNodeUserElementId,
            pageIndex: this.previewNodeUserPage.pageIndex,
            pageSize: this.previewNodeUserPage.pageSize,
            keyword: this.previewNodeUserPage.searchWord
          }).then(({ users, total }) => {
            // 计算总页数
            this.previewNodeUserPage.total = total;
            this.previewNodeUserPage.totalPage = Math.ceil(this.previewNodeUserPage.total / this.previewNodeUserPage.pageSize);
            this.previewUserNodes.splice(0, this.previewUserNodes.length);
            if (users && users.length > 0) {
              this.previewUserNodes.push(...users);
            } else {
              this.$message.info(
                isSearch ? this.$t('orgSelect.message.searchNoResult', '查无数据') : this.$t('orgSelect.message.noMoreData', '无更多数据')
              );
            }
            this.previewNodeUserLoading = false;
            this.updatePreviewAllCheckboxState();
          });
          return;
        }
      }
    },
    onSelect(selectedKeys, { node }) {
      this.previewUserNodes = [];
      this.indeterminatePreviewUser = false;
      this.checkAllPreviewUser = false;
      this.selectOrgTypeControl.previewNode = node;
      let asyncPreviewUser = node.dataRef.asyncPreviewUser === true;
      if (asyncPreviewUser || (this.selectOrgTypeControl.asyncUser && this.selectOrgTypeControl.asyncUser.has(this.currentOrgUuid))) {
        // 异步加载节点下的用户
        this.previewNodeUserLoading = true;
        this.selectOrgTypeControl.previewNodeUserElementId = node.dataRef.key;
        this.previewNodeUserPage.pageIndex = 0;
        this.fetchOrgTypeUsers(this.selectOrgType, {
          orgElementId: this.selectOrgTypeControl.previewNodeUserElementId,
          pageIndex: this.previewNodeUserPage.pageIndex++,
          pageSize: this.previewNodeUserPage.pageSize
        }).then(({ users, total }) => {
          this.previewUserNodes.splice(0, this.previewUserNodes.length);
          // 计算总页数
          this.previewNodeUserPage.total = total;
          this.previewNodeUserPage.totalPage = Math.ceil(this.previewNodeUserPage.total / this.previewNodeUserPage.pageSize);

          if (users && users.length > 0) {
            if (users.length == total) {
              // 后端返回全部数据，则代表不需要分页
              this.previewNodeUserPage.totalPage = 1;
            }
            this.previewUserNodes.push(...users);
          }
          this.previewNodeUserLoading = false;
          this.updatePreviewAllCheckboxState();
        });
        return;
      }
      this.filterPreviewNodeUserChildren(node);
    },
    filterPreviewNodeUserChildren(node) {
      if (node && node.dataRef.children) {
        let userNodes = [],
          keys = [];
        let getUserNodes = (list, keyPath, namePath) => {
          if (list) {
            for (let i = 0, len = list.length; i < len; i++) {
              if (list[i].data && list[i].data.type === 'user' && !keys.includes(list[i].key)) {
                if (this.previewNodeUserPage.searchWord) {
                  let inSearch = list[i].title.indexOf(this.previewNodeUserPage.searchWord) > -1;
                  if (!inSearch && list[i].data) {
                    if (list[i].data.userNo && list[i].data.userNo.indexOf(this.previewNodeUserPage.searchWord) > -1) {
                      inSearch = true;
                    }
                    if (list[i].data.pinYin && list[i].data.pinYin.indexOf(this.previewNodeUserPage.searchWord) > -1) {
                      inSearch = true;
                    }
                  }
                  if (!inSearch) {
                    continue;
                  }
                }
                let n = JSON.parse(JSON.stringify(list[i]));
                if (!n.fictional) {
                  // n.keyPath = keyPath ? keyPath + '/' + n.key : n.key;
                  n.namePath = namePath ? namePath + '/' + n.title : n.title;
                }
                userNodes.push(n);
                keys.push(n.key);
              }
              getUserNodes(
                list[i].children,
                keyPath ? keyPath + '/' + list[i].key : list[i].key,
                namePath ? namePath + '/' + list[i].title : list[i].title
              );
            }
          }
        };
        let parent = node.$parent,
          keyPath = node.dataRef.fictional ? [] : [node.dataRef.key],
          namePath = node.dataRef.fictional ? [] : [node.dataRef.title];
        while (parent && parent.dataRef) {
          if (parent.dataRef.fictional) {
            continue;
          }
          keyPath.push(parent.dataRef.key);
          namePath.push(parent.dataRef.title);
          parent = parent.$parent;
        }
        getUserNodes(
          node.dataRef.children,
          keyPath.length > 0 ? keyPath.reverse().join('/') : '',
          namePath.length > 0 ? namePath.reverse().join('/') : ''
        );
        userNodes = orderBy(userNodes, [
          function (o) {
            return o.data.userNo;
          },
          function (o) {
            return o.data.pinYin;
          }
        ]);
        this.previewUserNodes = userNodes;
        this.previewNodeUserPage.total = userNodes.length;
        this.updatePreviewAllCheckboxState();
      }
    },
    onExpand(expandedKeys, { expanded, node }) {
      // let dataRef = node.dataRef;
    },
    onAsyncLoadData(node, selectOrgType) {
      return new Promise(resolve => {
        if (node.dataRef.children) {
          resolve();
          return;
        }

        this.requestFetchOrgTypeData(
          selectOrgType,
          Object.assign({}, this.params, {
            parentKey: node.dataRef.key,
            checkedKeys: this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys
          })
        ).then(map => {
          let { dataList, allKeys, checkableKeys, halfCheckedKeys } = map;
          this.$set(node.dataRef, 'children', dataList);
          if (checkableKeys) {
            this.selectOrgTypeControl.treeKeys.checkable.push(...checkableKeys);
          }
          if (halfCheckedKeys) {
            this.checkedKeys.halfChecked = Array.from(new Set([].concat(this.checkedKeys.halfChecked).concat(Array.from(halfCheckedKeys))));
          }
          if (allKeys) {
            this.selectOrgTypeControl.treeKeys.all.push(...allKeys);
          }
          resolve();
        });
      });
    },

    fetchOrgTypeUsers(selectOrgType, params, page) {
      let _this = this;
      return new Promise((resolve, reject) => {
        let requestParams = Object.assign(
          {
            orgUuid: this.currentOrgUuid,
            orgVersionId: this.selectedOrgVersionId || this.orgVersionId,
            bizOrgUuid: this.currentBizOrgUuid
          },
          params || {},
          this.params
        );
        if (_this.filterNodeData != undefined) {
          if (_this.filterNodeData.showType !== 'function') {
            if (_this.filterNodeData.showData) {
              requestParams.includeKeys = _this.filterNodeData.showData.split(';');
            }
          }
          if (!_this.filterNodeData.hideType !== 'function') {
            if (_this.filterNodeData.hideData) {
              requestParams.excludeKeys = _this.filterNodeData.hideData.split(';');
            }
          }
        }
        $axios
          .post(`/proxy/api/org/organization/fetchOrgTreeUser/${selectOrgType}`, requestParams)
          .then(({ data, headers }) => {
            console.log('查询组织类型下的用户返回：', data);
            if (data.code == 0 && data.data) {
              if (headers['force-query-async'] == 'true') {
              }
              resolve({ users: data.data.nodes || [], total: data.data.total });
            }
          })
          .catch(() => {
            _this.$message.error(_this.$t('orgSelect.message.serverError', '组织服务异常'));
          });
      });
    },
    fetchOrgElementModel() {
      let _this = this;
      let request = () => {
        return new Promise((resolve, reject) => {
          $axios.get('/proxy/api/org/elementModel/getAllOrgElementModels', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
            if (data.code == 0 && data.data) {
              resolve(data.data);
            }
          });
        });
      };
      let callback = results => {
        for (let i = 0, len = results.length; i < len; i++) {
          if (results[i].enable) {
            _this.orgElementIcon[results[i].id] = results[i].icon;
          }
        }
      };
      if (this.enableCache) {
        this.$tempStorage.getCache(
          'getAllOrgElementModels',
          () => {
            return request();
          },
          results => {
            callback(results);
          }
        );
      } else {
        request().then(results => {
          callback(results);
        });
      }
    },
    getSetting() {
      let _this = this;
      return new Promise((resolve, reject) => {
        if (_this.setting == null) {
          let callback = list => {
            _this.setting = {};
            for (let i = 0, len = list.length; i < len; i++) {
              _this.setting[list[i].attrKey] = list[i];
              _this.setting[list[i].attrKey].attrValueJson = list[i].attrVal ? JSON.parse(list[i].attrVal) : {};
            }
            console.log('查询组织参数设置: ', _this.setting);
            if (_this.setting) {
              if (_this.setting['ORG_DIALOG_SELECT_PREVIEW'] && _this.forcePreviewUserUnderNode === undefined) {
                _this.previewUserUnderNode = _this.setting['ORG_DIALOG_SELECT_PREVIEW'].enable;
              }
            }

            resolve();
          };
          let request = () => {
            return new Promise((resolve, reject) => {
              $axios.get('/proxy/api/org/elementModel/queryOrgSetting', { params: { system: this._$SYSTEM_ID } }).then(({ data }) => {
                if (data.code == 0 && data.data) {
                  resolve(data.data);
                }
              });
            });
          };
          if (this.enableCache) {
            this.$tempStorage.getCache(
              this._$SYSTEM_ID + ':queryOrgSetting',
              () => {
                return request();
              },
              list => {
                callback(list);
              }
            );
          } else {
            request().then(list => {
              callback(list);
            });
          }
        } else {
          resolve();
        }
      });
    },

    filterOption(input, option) {
      return (
        option.componentOptions.propsData.value.toLowerCase().indexOf(input.toLowerCase()) >= 0 ||
        option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
      );
    },
    onChange(value, option) {
      this.$emit('input', value);
    },
    requestFetchOrgTypeData(selectOrgType, params) {
      let _this = this;
      if (_this.filterNodeData != undefined) {
        if (_this.filterNodeData.showType !== 'function') {
          if (_this.filterNodeData.showData) {
            params.includeKeys = _this.filterNodeData.showData.split(';');
          }
        }
        if (!_this.filterNodeData.hideType !== 'function') {
          if (_this.filterNodeData.hideData) {
            params.excludeKeys = _this.filterNodeData.hideData.split(';');
          }
        }
      }
      if (this.currentBizOrgUuid && (selectOrgType === 'MyLeader' || selectOrgType === 'MyUnderling')) {
        return new Promise((resolve, reject) => {
          let allKeys = new Set(),
            checkableKeys = new Set(),
            halfCheckedKeys = new Set();
          resolve({ dataList: [], allKeys, checkableKeys, halfCheckedKeys, selectOrgType });
        });
      }
      return new Promise((resolve, reject) => {
        const currentOrgUuid = this.currentOrgUuid;
        $axios
          .post(
            this.currentBizOrgUuid == undefined ||
              selectOrgType === 'PublicGroup' ||
              selectOrgType === 'TaskUsers' ||
              selectOrgType === 'TaskDoneUsers'
              ? `/proxy/api/org/organization/fetchOrgTree/${selectOrgType}`
              : `/proxy/api/org/biz/fetchBizOrgTree/${this.currentBizOrgUuid}`,
            Object.assign(
              {
                orgUuid: this.currentOrgUuid,
                orgVersionId: this.selectedOrgVersionId || this.orgVersionId,
                orgType: selectOrgType == 'MyDept' ? selectOrgType : undefined,
                currentBizOrgUuid: selectOrgType === 'TaskUsers' || selectOrgType === 'TaskDoneUsers' ? this.currentBizOrgUuid : undefined
              },
              params
            )
          )
          .then(({ data, headers }) => {
            console.log('查询组织树数据返回：', data);
            if (headers['force-query-async'] == 'true') {
              // 后端会根据相关数据决定是否强制切换为异步检索数据
              this.orgTypeControl[selectOrgType].asyncOrg.add(currentOrgUuid);
            }
            if (headers['force-user-query-async'] == 'true') {
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
                let jobFilter = (list, parent) => {};
                // 组织弹窗中显示职位节点/层级
                let dataList = data.data || [];
                let filterDataList = [];

                // 显示节点判断，返回true，则显示在树的第一层
                if (_this.isShowNodeFilterSetting) {
                  let setShowFilterData = array => {
                    if (array) {
                      for (let i = 0, len = array.length; i < len; i++) {
                        if (_this.filterNodeData.showType == 'function' && _this.filterNodeData.showFunction) {
                          try {
                            let func = new Function('node', 'isPc', 'dyform', _this.filterNodeData.showFunction);
                            let isContain = func(array[i], true, _this.dyform);
                            if (isContain) {
                              filterDataList.push(array[i]);
                            }
                          } catch (error) {}
                        } else if (_this.filterNodeData.showData) {
                          let keyParam = _this.filterNodeData.showType ? 'keyPath' : 'key';
                          let showData = _this.filterNodeData.showData.split(';');
                          if (showData.indexOf(array[i][keyParam]) > -1) {
                            filterDataList.push(array[i]);
                          }
                        }
                        if (array[i].children) {
                          setShowFilterData(array[i].children);
                        }
                      }
                    }
                  };
                  setShowFilterData(dataList);
                  if (filterDataList.length) {
                    // 显示节点，按照节点类型，key值排序
                    filterDataList = filterDataList.map(item => ({
                      ...item,
                      typeCom: _this.orgElementModelType.indexOf(item.type) // 处理字段
                    }));
                    filterDataList = orderBy(filterDataList, ['key'], ['asc']);
                    filterDataList = orderBy(filterDataList, ['typeCom'], ['desc']);
                    dataList = filterDataList;
                  }
                }

                // 批量设置图标、标题插槽
                let setScopedSlots = (array, node) => {
                  if (array) {
                    let delLength = 0;
                    for (let idx = 0, len = array.length; idx < len; idx++) {
                      let isDel = false;
                      let i = idx - delLength;
                      if (_this.isHideNodeFilterSetting) {
                        if (_this.filterNodeData.hideType == 'function' && _this.filterNodeData.hideFunction) {
                          try {
                            let func = new Function('node', _this.filterNodeData.hideFunction);
                            let isContain = func(array[i]);
                            if (isContain) {
                              isDel = true;
                              delLength++;
                              array.splice(i, 1);
                            }
                          } catch (error) {}
                        } else if (_this.filterNodeData.hideData) {
                          let keyParam = _this.filterNodeData.hideType ? 'keyPath' : 'key';
                          let hideData = _this.filterNodeData.hideData.split(';');
                          if (hideData.indexOf(array[i][keyParam]) > -1) {
                            isDel = true;
                            delLength++;
                            array.splice(i, 1);
                          }
                        }
                      }
                      if (!isDel) {
                        allKeys.add(array[i].key);
                        array[i].scopedSlots = { icon: 'nodeIcon', title: 'nodeTitle' };
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
                            d => {
                              return d.data && d.data.seq != undefined ? parseInt(d.data.seq) : 0;
                            },
                            ['asc']
                          );

                          setScopedSlots(array[i].children, array[i]);
                        }
                      } else if (node) {
                        setNoChildrenSlots(node);
                      }
                    }
                  }
                };
                let setNoChildrenSlots = node => {
                  if (!node.children || (node.children && node.children.length == 0)) {
                    node.isLeaf = true;
                  }
                };
                setScopedSlots(dataList);
                resolve({ dataList, allKeys, checkableKeys, halfCheckedKeys, selectOrgType });
              } else {
                resolve({ dataList: [], allKeys, checkableKeys, halfCheckedKeys, selectOrgType });
              }
            }
          });
      }).catch(() => {
        this.$message.error(this.$t('orgSelect.message.loadDataError', '组织数据加载失败'));
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
          if (typeof callback === 'function') {
            callback.call(this);
          }
          return;
        }
        this.loading = true;
        let requestParams = Object.assign({}, this.params, {
          checkedKeys: this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys
        });
        this.requestFetchOrgTypeData(this.selectOrgType, requestParams).then(map => {
          let { dataList, allKeys, checkableKeys, halfCheckedKeys, selectOrgType } = map;
          if (this.selectOrgType != selectOrgType) {
            // 左侧导航切换后，可能不在接口返回的类别里
            _this.orgTypeControl[selectOrgType].treeData.splice(0, _this.orgTypeControl[selectOrgType].treeData.length);
            _this.orgTypeControl[selectOrgType].treeData = dataList;
            _this.orgTypeControl[selectOrgType].treeKeys = { checkable: Array.from(checkableKeys), all: Array.from(allKeys) };
            _this.orgTypeControl[selectOrgType].fetched = true;
            _this.orgTypeControl[selectOrgType].currentOrgUuid = this.currentOrgUuid;
            _this.orgTypeControl[selectOrgType].selectedOrgVersionId = this.selectedOrgVersionId;
          } else {
            _this.selectOrgTypeControl.treeData.splice(0, _this.selectOrgTypeControl.treeData.length);
            _this.selectOrgTypeControl.treeData = dataList;
            _this.selectOrgTypeControl.treeKeys = { checkable: Array.from(checkableKeys), all: Array.from(allKeys) };

            if (halfCheckedKeys != undefined && halfCheckedKeys.size) {
              _this.checkedKeys.halfChecked.push(...Array.from(halfCheckedKeys));
            }
            _this.selectOrgTypeControl.fetched = true;
            _this.selectOrgTypeControl.currentOrgUuid = this.currentOrgUuid;
            _this.selectOrgTypeControl.selectedOrgVersionId = this.selectedOrgVersionId;
          }
          _this.loading = false;
          _this.$nextTick(() => {
            if (typeof callback === 'function') {
              callback.call(_this);
            }
          });
        });
      }
    },

    fetchBizOrgTree() {},

    fetchAllOrgNodesByKeys(keys) {
      let _this = this;
      return new Promise((resolve, reject) => {
        let request = () => {
          return new Promise((resolve, reject) => {
            $axios
              .post(`/proxy/api/org/organization/fetchOrgTreeNodesByKeys`, {
                orgUuid: this.currentOrgUuid,
                orgVersionId: this.selectedOrgVersionId || this.orgVersionId,
                keys
              })
              .then(({ data }) => {
                console.log('查询组织节点数据返回：', data);
                resolve(data.data);
              })
              .catch(() => {
                _this.$message.error(_this.$t('orgSelect.message.serverError', '组织服务异常'));
              });
          });
        };

        let key = md5(
          JSON.stringify({
            type: `fetchAllOrgNodesByKeys`,
            orgUuid: this.currentOrgUuid,
            orgVersionId: this.selectedOrgVersionId || this.orgVersionId,
            keys
          })
        );
        if (this.enableCache) {
          this.$tempStorage.getCache(
            `fetchAllOrgNodesByKeys:${key}`,
            () => {
              return request();
            },
            data => {
              resolve(data);
            }
          );
        } else {
          request().then(data => {
            resolve(data);
          });
        }
      });
    },

    fetchNodesByKeys(keys, type, callback) {
      let _this = this;
      let request = () => {
        return new Promise((resolve, reject) => {
          $axios
            .post(`/proxy/api/org/organization/fetchOrgTreeNodesByKeys/${type}`, {
              orgUuid: this.currentOrgUuid,
              orgVersionId: this.selectedOrgVersionId || this.orgVersionId,
              keys
            })
            .then(({ data }) => {
              console.log('查询组织节点数据返回：', data);
              resolve(data.data);
            })
            .catch(() => {
              _this.$message.error(_this.$t('orgSelect.message.serverError', '组织服务异常'));
            });
        });
      };

      let key = md5(
        JSON.stringify({
          type: `fetchOrgTreeNodesByKeys/${type}`,
          orgUuid: this.currentOrgUuid,
          orgVersionId: this.selectedOrgVersionId || this.orgVersionId,
          keys
        })
      );
      if (this.enableCache) {
        this.$tempStorage.getCache(
          `fetchOrgTreeNodesByKeys:${type}:${key}`,
          () => {
            return request();
          },
          data => {
            if (typeof callback == 'function') {
              callback.call(_this, data);
            }
          }
        );
      } else {
        request().then(data => {
          if (typeof callback == 'function') {
            callback.call(_this, data);
          }
        });
      }
    },
    initCheckedValue() {
      // 有初始值的情况
      if (this.orgTypeOptions.length > 0 && ((this.checkStrictly && this.checkedKeys.checked.length > 0) || this.checkedKeys.length > 0)) {
        this.valueLoading = this.valueNodes.length == 0;
        let keys = [].concat(this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys);
        let types = [];
        this.orgTypeOptions.forEach(o => {
          types.push(o.value);
        });
        let orginalKeys = [].concat(keys);
        this.selectedNodes = [];
        let getSelectNodes = i => {
          this.fetchNodesByKeys(keys, types[i], nodes => {
            if (nodes && nodes.length > 0) {
              const selectedNodes = this.selectedNodes.concat(nodes);
              this.selectedNodes = uniqBy(selectedNodes, 'key'); //this.isPathValue ? 'keyPath' : 'key'
              if (this.selectedNodes.length == keys.length) {
                keys = [];
                this.valueLoading = false;
                this.selectedNodes = orderBy(
                  this.selectedNodes,
                  d => {
                    return orginalKeys.indexOf(d.key);
                  },
                  ['asc']
                );

                this.valueNodes = JSON.parse(JSON.stringify(this.selectedNodes));
                this.isInternalChange = false;
                this.$emit('checkedValueInitFinish');
                return;
              }
              // 剔除已查询到的keys
              for (let s = 0, slen = this.selectedNodes.length; s < slen; s++) {
                let curKey = this.selectedNodes[s].key;
                if (!this.valueNodes[s]) {
                  // console.error(`selected node more than value node`, this.selectedNodes, this.valueNodes);
                  continue;
                }
                if (this.isPathValue && this.valueNodes[s].keyPath) {
                  curKey = this.selectedNodes[s].keyPath;
                }
                if (keys.includes(curKey)) {
                  keys.splice(keys.indexOf(curKey), 1);
                }
              }
            }
            if (keys.length === 0 || i + 1 == types.length) {
              this.valueLoading = false;
              this.selectedNodes = orderBy(
                this.selectedNodes,
                d => {
                  return orginalKeys.indexOf(d.key);
                },
                ['asc']
              );
              this.valueNodes = JSON.parse(JSON.stringify(this.selectedNodes));
              this.$nextTick(() => {
                this.isInternalChange = false;
                this.$emit('checkedValueInitFinish');
              });
            } else if (keys.length > 0 && i + 1 < types.length) {
              getSelectNodes.call(this, ++i);
            }
          });
        };
        getSelectNodes.call(this, 0);
      } else {
        this.$emit('checkedValueInitFinish');
      }
    },
    getOrgSelectOptions() {
      let request = () => {
        return new Promise((resolve, reject) => {
          $axios
            .get(`/proxy/api/org/organization/queryEnableOrgs`, { params: { system: this._$SYSTEM_ID, fetchBizOrg: true } })
            .then(({ data }) => {
              if (data.data) {
                resolve(data.data);
              }
            })
            .catch(error => {});
        });
      };

      let callback = list => {
        this.orgSelectTreeLoading = false;
        for (let i = 0, len = list.length; i < len; i++) {
          if (!this.matchOrgIdOption(list[i].id)) {
            continue;
          }
          let opt = {
            label: list[i].name,
            value: list[i].uuid,
            children: []
          };

          if (list[i].bizOrgs) {
            for (let j = 0, jlen = list[i].bizOrgs.length; j < jlen; j++) {
              if (!this.matchBizOrgIdOption(list[i].id, list[i].bizOrgs[j].id)) {
                continue;
              }
              opt.children.push({
                label: list[i].bizOrgs[j].name,
                value: list[i].bizOrgs[j].uuid,
                bizOrgId: list[i].bizOrgs[j].id,
                scopedSlots: { title: 'nodeTitle' }
              });
            }
          }
          this.orgSelectTreeData.push(opt);
          this.orgSelectOptions.push(opt);
        }
      };
      if (this.enableCache) {
        this.$tempStorage.getCache(
          this._$SYSTEM_ID + ':queryEnableOrgs',
          () => {
            return request();
          },
          list => {
            callback(list);
          }
        );
      } else {
        request().then(list => {
          callback(list);
        });
      }
    },
    getOrg(params = {}) {
      let _this = this;
      return new Promise((resolve, reject) => {
        let request = () => {
          _this.getOrgPromise = new Promise((resolve, reject) => {
            $axios
              .get(`/proxy/api/org/organization/version/published`, {
                params: { system: this._$SYSTEM_ID, ...params }
              })
              .then(({ data }) => {
                resolve(data.data);
              })
              .catch(() => {
                _this.$message.error(_this.$t('orgSelect.message.serverError', '组织服务异常'));
              });
          });
          return _this.getOrgPromise;
        };
        let _callback = data => {
          resolve(data);
        };
        if (this.enableCache) {
          this.$tempStorage.getCache(
            this._$SYSTEM_ID + (params.orgUuid || '') + ':versionPublished',
            () => {
              return request();
            },
            data => {
              _callback(data);
            }
          );
        } else {
          request().then(d => {
            _callback(d);
          });
        }
      });
    },
    onSelectOrgNode(value, node) {
      console.log('选中', arguments);
    },
    onChangeSelectOrg(key, label, node) {
      this.currentBizOrgUuid = undefined;
      let bizOrgId = node && node.triggerNode.dataRef.bizOrgId;
      this.selectOrgTypeControl.fetched = false;
      this.selectOrgTypeControl.searchKeyword = undefined;
      this.selectOrgTypeControl.searchView = false;
      this.letterUserLoading = true;
      this.letterUserGroup = undefined;
      this.searchInputPlaceholder = undefined;
      this.resetPreviewNodeUserPage();
      if (bizOrgId !== undefined) {
        this.currentBizOrgUuid = key;
      }
      this.fetchOrgTree(() => {
        if (!(this.selectOrgTypeControl.asyncOrg && this.selectOrgTypeControl.asyncOrg.has(this.currentOrgUuid))) {
          this.updateHalfChecked();
        }
        this.updateTreeAllCheckboxState();
        this.updateLetterUserGroup();
        this.letterUserLoading = false;
      });
    },
    resetPreviewNodeUserPage() {
      this.previewNodeUserPage.pageIndex = 0;
      this.previewNodeUserPage.total = 0;
      this.previewNodeUserPage.totalPage = 0;
      this.previewNodeUserPage.resultSearchable = false;
      this.previewUserNodes = [];
      this.previewNodeUserPage.searchWord = undefined;
    },
    getBizOrg(callback) {
      $axios.get(`/proxy/api/org/organization/version/getBizOrgUuidByBizOrgId?bizOrgId=${this.bizOrgId}`).then(({ data: result }) => {
        if (result.code == 0 && result.data) {
          this.currentBizOrgUuid = result.data;
          if (typeof callback == 'function') {
            callback.call(this);
          }
        }
      });
    },
    getOrgVersion(callback) {
      this.getOrgPromise = $axios
        .get(`/proxy/api/org/organization/version/byId`, {
          params: { id: this.selectedOrgVersionId || this.orgVersionId, fetchBizOrg: true }
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.currentOrgUuid = data.data.orgUuid;
            if (typeof callback == 'function') {
              callback.call(this, data.data);
            }
          }
        })
        .catch(error => {});
    },
    loadOrgVersions() {
      if (this.orgVersionIds && this.orgVersionIds.length > 1) {
        return $axios
          .get(`/proxy/api/org/organization/version/listByOrgVersionIds?orgVersionIds=${this.orgVersionIds}&fetchBizOrg=true`)
          .then(({ data }) => {
            this.orgVersionOptions = data.data;
            return this.orgVersionOptions;
          });
      }
      return Promise.resolve([]);
    },
    setValue(v) {
      if (v != undefined && v.length > 0) {
        this.isInternalChange = true;
        let _value = v;
        if (typeof v === 'string' && this.multiSelect) {
          let separator = new RegExp(';|,|' + this.separator);
          _value = v.split(separator);
        }
        if (this.checkStrictly) {
          this.checkedKeys.checked = _value;
        } else {
          this.checkedKeys = _value;
        }
        this.$nextTick(() => {
          this.isInternalChange = false;
        });
        if (!this.currentOrgUuid) {
          this.getOrg({}).then(() => {
            this.initCheckedValue();
          });
        } else {
          this.initCheckedValue();
        }
      } else {
        this.clearAllInput();
      }
    },

    fetchSortLetters() {
      return new Promise((resolve, reject) => {
        this.$axios
          .get(`/proxy/api/app/codeI18n/getLocaleSortLetters`, { params: {} })
          .then(({ data }) => {
            if (data.data) {
              this.letters.splice(0, this.letters.length);
              this.letters.push(...data.data.split(','));
            } else {
              this.letters.push(...'abcdefghijklmnopqrstuvwxyz'.split(''));
            }
            resolve();
          })
          .catch(error => {
            this.letters.push(...'abcdefghijklmnopqrstuvwxyz'.split(''));
            resolve();
          });
      });
    }
  },

  watch: {
    value: {
      handler(v, o) {
        if (this.isInternalChange) {
          // console.log('组织内部值变更，不需要重新渲染节点');
        } else {
          let checked = this.checkStrictly ? this.checkedKeys.checked : this.checkedKeys;
          // console.log('组织外部值变更，需要重新渲染节点');
          if (this.valueNodes.length > 0 && (v == undefined || v.length == 0)) {
            this.valueNodes.splice(0, this.valueNodes.length);
            this.selectedNodes.splice(0, this.selectedNodes.length);
            checked.splice(0, checked.length);
            this.isInternalChange = true;
            this.$nextTick(() => {
              this.isInternalChange = false;
            });
            return;
          }

          if (typeof v === 'string') {
            if (this.multiSelect) {
              let separator = new RegExp(';|,|' + this.separator);
              let _value = [].concat(v.split(separator));
              checked.splice(0, checked.length);
              checked.push(..._value);
            } else {
              checked.splice(0, checked.length);
              checked.push(v);
            }
          } else if (Array.isArray(v)) {
            let _value = [].concat(v);
            checked.splice(0, checked.length);
            checked.push(..._value);
          }

          this.isInternalChange = true;
          this.initCheckedValue();
          this.$nextTick(() => {
            this.isInternalChange = false;
          });
        }
      }
    }
  }
};
</script>
