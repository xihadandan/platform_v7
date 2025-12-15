<template>
  <a-form-model
    class="search-form"
    :model="searchForm"
    labelAlign="left"
    ref="basicForm"
    :label-col="{ span: 7, style: { textAlign: 'right' } }"
    :wrapper-col="{ span: 17, style: { textAlign: 'right' } }"
    :colon="false"
  >
    <div v-if="displayState == 'label'" style="width: 100%; height: 100%; display: block; position: absolute; z-index: 10"></div>
    <div class="search-container flex f_y_c" style="margin-bottom: 1px; border-radius: 4px 4px 0 0">
      <div class="row-head-flag f_s_0">
        <span class="title">
          <Icon type="iconfont icon-ptkj-wenjian" style="margin-right: 4px" />
          查找项
        </span>
      </div>
      <div class="f_g_1 row-content-flag">
        <a-row>
          <a-col span="10">
            <a-form-model-item label="查找人员" prop="oldUserId">
              <OrgSelect
                v-model="searchForm.oldUserId"
                :multiSelect="false"
                :orgType="['MyOrg', 'MyLeader', 'PublicGroup']"
                @change="oldUserChange"
              />
            </a-form-model-item>
          </a-col>
          <a-col span="4" offset="10" style="text-align: right">
            <a-button
              v-show="!searchForm.expandSearch && displayState != 'label'"
              type="link"
              @click="expandSearch(true)"
              style="margin-top: 5px"
            >
              更多
              <a-icon type="double-right" :rotate="90" />
            </a-button>
            <a-button v-if="!searchForm.expandSearch && displayState != 'label'" type="primary" icon="search" @click="onSearch">
              查询
            </a-button>
          </a-col>
        </a-row>
        <a-row v-show="searchForm.expandSearch">
          <a-col span="10">
            <a-form-model-item label="流程范围" prop="flowRange">
              <a-select v-model="searchForm.flowRange" :options="flowRangeOptions" />
            </a-form-model-item>
          </a-col>
          <a-col span="14">
            <FlowSelect
              v-if="searchForm.flowRange == 'specify'"
              v-model="searchForm.flowDefId"
              style="width: 200px; margin: 5px 0 0 10px"
              @change="flowSelectChange"
            ></FlowSelect>
            <!-- <a-input v-if="searchForm.flowRange == 'specify'" v-model="searchForm.flowDefId" style="width: 200px; margin: 5px 0 0 10px" /> -->
            <a-checkbox v-model="searchForm.includeHisVersion" style="margin: 10px 0 0 5px">包含历史版本</a-checkbox>
          </a-col>
        </a-row>
        <a-row v-show="searchForm.expandSearch">
          <a-col span="10">
            <a-form-model-item label="组织范围" prop="orgRange">
              <a-select v-model="searchForm.orgRange" :options="orgRangeOptions" @change="onChangeOrgRange" />
            </a-form-model-item>
          </a-col>
          <a-col span="11">
            <a-tree-select
              v-if="searchForm.orgRange == 'specify'"
              :treeData="orgSelectTreeData"
              v-model="searchForm.orgId"
              show-search
              allow-clear
              style="max-width: 100%; min-width: 140px; margin: 5px 0 0 10px"
              :dropdown-style="{ maxHeight: '350px', overflow: 'auto' }"
              placeholder="请选择组织"
              @change="onChangeSelectOrg"
              treeNodeFilterProp="label"
            >
              <template slot="nodeTitle" slot-scope="node">
                <div style="display: flex; align-items: center; justify-content: space-between">
                  {{ node.label }}
                  <a-tag class="primary-color" style="margin-left: 8px">业务组织</a-tag>
                </div>
              </template>
            </a-tree-select>
          </a-col>
          <a-col span="4" offset="10" style="text-align: right">
            <a-button v-if="displayState != 'label'" type="link" @click="expandSearch(false)" style="margin-top: 5px">
              收起
              <a-icon type="double-right" :rotate="-90" />
            </a-button>
            <a-button v-if="displayState != 'label'" type="primary" icon="search" @click="onSearch">查询</a-button>
          </a-col>
        </a-row>
      </div>
    </div>
    <div class="search-container flex f_y_c" style="border-radius: 0 0 4px 4px">
      <div class="row-head-flag f_s_0">
        <span class="title">
          <Icon type="iconfont icon-ptkj_tihuan" style="margin-right: 4px" />
          修改项
        </span>
      </div>
      <div class="f_g_1 row-content-flag">
        <a-row>
          <a-col span="10">
            <a-form-model-item label="修改内容" prop="modifyMode">
              <a-select v-model="searchForm.modifyMode" :options="modifyModeOptions"></a-select>
            </a-form-model-item>
          </a-col>
          <a-col span="14">
            <OrgSelect
              v-model="searchForm.newUserId"
              :multiSelect="false"
              :orgType="['MyOrg', 'MyLeader', 'PublicGroup']"
              @change="newUserChange"
              style="width: 240px; margin: 4px 0 0 10px"
            />
            <span>
              流程保存
              <a-select v-model="searchForm.saveAs" :options="saveAsOptions" style="width: 100px; margin: 4px 0 0 10px"></a-select>
            </span>
          </a-col>
        </a-row>
      </div>
    </div>
  </a-form-model>
</template>

<script>
import OrgSelect from '@admin/app/web/lib/org-select.vue';
import FlowSelect from '@workflow/app/web/lib/flow-select.vue';
export default {
  props: {
    searchForm: {
      type: Object,
      default() {
        return {};
      }
    },
    displayState: String
  },
  components: { OrgSelect, FlowSelect },
  data() {
    return {
      flowRangeOptions: [
        { label: '全部流程', value: 'all' },
        { label: '指定流程', value: 'specify' }
      ],
      orgRangeOptions: [
        { label: '人员来源全部组织', value: 'all' },
        { label: '人员来源全部行政组织', value: 'xz' },
        { label: '人员来源全部业务组织', value: 'biz' },
        { label: '人员来源指定行政组织', value: 'specify' }
      ],
      orgSelectTreeData: [],
      modifyModeOptions: [
        { label: '替换为', value: 'replace' },
        { label: '新增', value: 'add' },
        { label: '删除', value: 'delete' }
      ],
      saveAsOptions: [
        { label: '当前版本', value: 'current' },
        { label: '新版本', value: 'newVersion' }
      ]
    };
  },
  methods: {
    expandSearch(expand) {
      this.searchForm.expandSearch = expand;
      this.$emit('expandSearch', expand);
    },
    oldUserChange({ value, label }) {
      this.searchForm.oldUserName = label;
    },
    newUserChange({ value, label, nodes }) {
      this.searchForm.newUserName = label;
      if (nodes.length) {
        this.searchForm.newUserPath = nodes[0].keyPath;
      } else {
        this.searchForm.newUserPath = '';
      }
    },
    flowSelectChange({ value, label }) {
      this.searchForm.flowDefName = label;
    },
    onChangeOrgRange() {
      const _this = this;
      if (_this.searchForm.orgRange == 'specify' && _this.orgSelectTreeData.length == 0) {
        _this.loadOrgSelectTreeData();
      }
    },
    loadOrgSelectTreeData() {
      const _this = this;
      $axios
        .get(`/proxy/api/org/organization/queryEnableOrgs`, { params: { system: _this._$SYSTEM_ID, fetchBizOrg: true } })
        .then(({ data: result }) => {
          if (result.data) {
            _this.addOrgSelectTreeData(_this.orgSelectTreeData, result.data);
            _this.orgSelectTreeData = [..._this.orgSelectTreeData];
          }
        })
        .catch(error => {});
    },
    addOrgSelectTreeData(treeData, orgs = [], nodeTitleSlot = false) {
      orgs.forEach(org => {
        const node = {
          label: org.name,
          value: org.id,
          children: []
        };
        if (nodeTitleSlot) {
          node.scopedSlots = { title: 'nodeTitle' };
        }
        treeData.push(node);

        if (org.bizOrgs && org.bizOrgs.length) {
          this.addOrgSelectTreeData(node.children, org.bizOrgs, true);
        }
      });
    },
    onChangeSelectOrg() {},
    onSearch() {
      this.$emit('search');
    }
  }
};
</script>

<style lang="less" scoped>
.search-form {
  --search-form-container-row-content-flag-background: var(--w-gray-color-2);
  position: relative;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--w-border-color-light);

  .search-container {
    background: var(--w-primary-color-2);
    .row-head-flag {
      width: 86px;
      text-align: center;

      .title {
        font-size: var(--w-font-size-base);
        color: var(--w-primary-color);
      }
    }
    .row-content-flag {
      padding-top: 4px;
      background-color: var(--search-form-container-row-content-flag-background);
      padding-right: 8px;
    }
  }

  .ant-form-item {
    margin: 0 0 4px;
  }
}
</style>
