<template>
  <a-row :gutter="32" class="product-module">
    <a-col span="16">
      <div class="product-module-header">
        <div class="flex f_y_c" style="margin-bottom: 16px">
          <div class="f_s_0">选择模块：</div>
          <div style="min-width: 320px">
            <a-radio-group v-model="selectedTabKey" button-style="solid" @change="onChangeTab">
              <a-radio-button :value="tab.key" v-for="(tab, t) in tabs" :key="'radio_' + tab.key">
                {{ tab.title }}
              </a-radio-button>
            </a-radio-group>
          </div>
          <a-input-search style="width: 320px" allow-clear @search="e => onSearchModule(e, tabData[selectedTabKey].searchParams)" />
        </div>
        <div v-for="(tab, t) in tabs" :key="tab.key" v-show="tab.key == selectedTabKey">
          <div class="flex" :class="tab.key + '_tags'">
            <div class="f_s_0">选择标签：</div>
            <div class="f_g_1" style="display: flex; align-items: flex-start; flex-wrap: wrap">
              <a-checkable-tag
                class="check-tag"
                :checked="tabData[tab.key].searchParams.tagUuids.length == 0"
                @change="checked => handleAllChange('tagUuids', checked)"
              >
                全部
              </a-checkable-tag>
              <template v-for="(tag, i) in tags">
                <a-checkable-tag
                  v-if="i < displayTagMaxNum || tabData[tab.key].showMoreTag"
                  :key="tag.uuid"
                  :title="tag.name"
                  class="check-tag"
                  :checked="tabData[tab.key].searchParams.tagUuids.includes(tag.uuid)"
                  @change="checked => handleTagChange(tag.uuid, checked, 'tagUuids')"
                >
                  {{ tag.name }}
                </a-checkable-tag>
              </template>
              <a-button
                type="link"
                size="small"
                style="--w-button-font-size: var(--w-tag-text-size)"
                v-if="tags.length > displayTagMaxNum"
                @click="changeMoreTag(tab.key)"
              >
                <template v-if="tabData[tab.key].showMoreTag">
                  收起标签
                  <Icon type="up"></Icon>
                </template>
                <template v-else>
                  更多标签
                  <Icon type="down"></Icon>
                </template>
              </a-button>
            </div>
          </div>
        </div>
      </div>
      <div v-for="(tab, t) in tabs" :key="tab.key" v-show="tab.key == selectedTabKey">
        <div class="spin-center" v-show="tabData[tab.key].loading">
          <a-spin tip="加载数据中..." />
        </div>
        <PerfectScrollbar
          :style="{
            height: `calc(100vh - 370px - ${tabData[tab.key].tagHeight}px)`,
            marginRight: '-16px',
            padding: '1px 10px',
            marginBottom: '8px'
          }"
          @ps-scroll-y="onScrollY"
        >
          <a-list
            class="pt-empty hide-desciption"
            :grid="grid"
            item-layout="vertical"
            :data-source="tabData[tab.key].moduleList"
            style="padding: 0 10px; --pt-empty-image-width: 320px; --pt-empty-image-height: 200px"
          >
            <a-list-item
              slot="renderItem"
              slot-scope="item, index"
              class="module-item"
              :class="vModuleIds.includes(item.id) ? 'selected' : ''"
              @click="onSelectModule(item)"
            >
              <template slot="actions">
                <a-button type="link" size="small" @click.stop="clickToModuleDetail(item)">
                  <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
                  查看
                </a-button>
              </template>

              <a-list-item-meta>
                <template slot="description"></template>
                <template slot="title">
                  <span :title="item.name" style="font-weight: bold">{{ item.name }}</span>
                </template>
                <a-avatar
                  v-if="item.icon"
                  slot="avatar"
                  shape="square"
                  :size="40"
                  class="module-icon"
                  :style="{
                    background: item.icon.bgColor
                      ? item.icon.bgColor.startsWith('--')
                        ? 'var(' + item.icon.bgColor + ')'
                        : item.icon.bgColor
                      : 'var(--w-primary-color)',
                    borderRadius: '8px'
                  }"
                >
                  <Icon slot="icon" :type="item.icon.icon || 'appstore'" :style="{ fontSize: '24px' }" />
                </a-avatar>
              </a-list-item-meta>
              <div class="remark w-ellipsis-2" :title="item.remark || ''">{{ item.remark || ' ' }}</div>
              <Icon class="selected-icon" type="iconfont icon-ptkj-duoxuan-xuanzhong" v-if="vModuleIds.includes(item.id)" />
            </a-list-item>
          </a-list>
          <div style="text-align: center" v-if="tabData[tab.key].moduleList.length == 0 && !tabData[tab.key].loading">
            没有找到想要模块？
            <a-button type="link" size="small" @click="quickCreateVisible = true">去创建</a-button>
            <QuickCreateSelect v-model="quickCreateVisible" @createDone="refreshList" />
          </div>
        </PerfectScrollbar>
        <div style="text-align: center" v-show="!tabData[tab.key].noMore && !tabData[tab.key].loading">
          <a-button
            type="link"
            size="small"
            v-if="tabData[tab.key].searchParams.keyword == undefined || tabData[tab.key].searchParams.keyword == ''"
            @click="loadMore"
          >
            点击或滚动加载更多
          </a-button>
        </div>
      </div>
    </a-col>
    <a-col span="8" style="border-left: 1px solid var(--w-border-color-light)">
      <div class="modules-selected-container">
        <a-row type="flex" class="modules-selected-top">
          <a-col flex="200px">
            已选择
            <span style="color: var(--w-primary-color)">{{ modules.length }}</span>
            个模块
            <a-button type="link" @click="clearSelected">清空</a-button>
          </a-col>
          <a-col flex="auto">
            <a-input-search allow-clear v-model="selectedSearchValue" />
          </a-col>
        </a-row>
        <PerfectScrollbar style="height: calc(100vh - 334px); margin-right: -20px; padding-right: 20px">
          <div class="spin-center" v-if="loading">
            <a-spin />
          </div>
          <template v-else>
            <a-list class="pt-empty" item-layout="horizontal" :data-source="modules" style="padding: 12px 1px">
              <a-list-item
                slot="renderItem"
                slot-scope="item, index"
                v-show="!selectedSearchValue || item.name.indexOf(selectedSearchValue) > -1"
              >
                <template slot="actions" v-if="!item.deleted">
                  <a-button size="small" type="link" @click="clickToModuleDetail(item)">
                    <Icon type="pticon iconfont icon-szgy-zonghechaxun"></Icon>
                    查看
                  </a-button>
                </template>
                <template slot="actions">
                  <a-button size="small" type="link" icon="minus-square" @click="removeSelectedModule(index)">移除</a-button>
                </template>

                <a-list-item-meta>
                  <template slot="title">
                    <span :title="item.name" class="module-name">{{ item.name }}</span>
                    <a-tag color="red" v-if="item.deleted">模块不存在</a-tag>
                  </template>
                  <a-avatar
                    v-if="item.icon"
                    slot="avatar"
                    shape="square"
                    :size="24"
                    class="module-icon"
                    :style="{
                      background: item.icon.bgColor
                        ? item.icon.bgColor.startsWith('--')
                          ? 'var(' + item.icon.bgColor + ')'
                          : item.icon.bgColor
                        : 'var(--w-primary-color)',
                      borderRadius: '4px'
                    }"
                  >
                    <Icon slot="icon" :type="item.icon.icon || 'appstore'" :style="{ fontSize: '14px' }" />
                  </a-avatar>
                </a-list-item-meta>
              </a-list-item>
            </a-list>
          </template>
        </PerfectScrollbar>
      </div>
    </a-col>
  </a-row>
</template>
<style lang="less"></style>
<script type="text/babel">
import QuickCreateSelect from '@pageAssembly/app/web/page/module-center/component/module-quick-create-select.vue';
import { map } from 'lodash';
export default {
  name: 'ProductModule',
  props: {
    prodId: String,
    uuid: String,
    modules: Array,
    loading: Boolean
  },
  components: { QuickCreateSelect },
  computed: {
    vModuleIds() {
      let i = [];
      if (this.modules != undefined) {
        for (let m of this.modules) {
          i.push(m.moduleId);
        }
      }
      return i;
    }
  },
  data() {
    return {
      grid: { gutter: 16, xs: 1, sm: 1, md: 2, lg: 2, xl: 3, xxl: 4 },
      fetchUnderModLoading: true,
      tabs: [
        { title: '全部模块', key: 'all' },
        { title: '团队发布', key: 'team' },
        { title: '我发布的', key: 'my' }
      ],
      selectedTabKey: 'all',
      displayTagMaxNum: 8,
      tabData: {
        all: {
          loading: true,
          fetched: false,
          moduleList: [],
          searchParams: { keyword: undefined, tagUuids: [] },
          page: {
            currentPage: 1,
            pageSize: 16,
            totalCount: 0
          },
          noMore: false,
          showMoreTag: false,
          tagHeight: 32
        },
        team: {
          loading: true,
          fetched: false,
          moduleList: [],
          searchParams: { keyword: undefined, tagUuids: [] },
          page: {
            currentPage: 1,
            pageSize: 16,
            totalCount: 0
          },
          noMore: false,
          showMoreTag: false,
          tagHeight: 32
        },
        my: {
          loading: true,
          fetched: false,
          moduleList: [],
          searchParams: { keyword: undefined, tagUuids: [] },
          page: {
            currentPage: 1,
            pageSize: 16,
            totalCount: 0
          },
          noMore: false,
          showMoreTag: false,
          tagHeight: 32
        }
      },
      tabKey: 'all',
      selectedModules: [],
      tags: [],
      quickCreateVisible: false,
      tagsColors: ['pink', 'red', 'orange', 'blue', 'green', 'cyan', 'purple'],
      selectedSearchValue: ''
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.refreshList();
    this.fetchAllTags();
  },
  mounted() {},
  methods: {
    onSearchModule(e, item) {
      if (e != undefined && e.trim() != '') {
        item.keyword = e.trim();
        this.refreshList();
      } else if (e == '') {
        item.keyword = undefined;
        this.refreshList();
      }
    },
    clickToModuleDetail(item) {
      window.open(`/module/assemble/${item.moduleUuid || item.uuid}`, '_blank');
    },
    clearSelected() {
      this.modules.splice(0, this.modules.length);
      // this.selectedModules.splice(0, this.selectedModules.length);
    },
    handleTagChange(v, checked, key) {
      let searchParams = this.tabData[this.selectedTabKey].searchParams;
      let array = Array.isArray(searchParams[key]);
      if (checked) {
        if (array) {
          searchParams[key].push(v);
        } else {
          searchParams[key] = v;
        }
      } else {
        if (array) {
          searchParams[key].splice(searchParams[key].indexOf(v), 1);
        } else {
          searchParams[key] = undefined;
        }
      }
      this.refreshList();
    },
    handleAllChange(key, checked) {
      let searchParams = this.tabData[this.selectedTabKey].searchParams;
      let array = Array.isArray(searchParams[key]);
      if (checked) {
        searchParams[key] = array ? [] : undefined;
      }
      this.refreshList();
    },
    onChangeTab() {
      // this.selectedTabKey = key;
      if (this.tabData[this.selectedTabKey].moduleList.length == 0) {
        this.refreshList();
      }
    },
    removeSelectedModule(i) {
      this.modules.splice(i, 1);
      // this.selectedModules.splice(i, 1);
    },
    onSelectModule(item) {
      let idx = this.vModuleIds.indexOf(item.id);
      if (idx == -1) {
        this.modules.push({ icon: item.icon, moduleId: item.id, name: item.name, moduleName: item.name, moduleUuid: item.uuid });
        // this.selectedModules.push({ icon: item.icon, name: item.name, moduleId: item.id, moduleName: item.name, moduleUuid: item.uuid });
      } else {
        this.modules.splice(idx, 1);
        // this.selectedModules.splice(idx, 1);
      }
    },
    resetSearchForm() {
      this.searchParams.keyword = undefined;
      this.refreshList();
    },
    refreshList() {
      this.tabData[this.selectedTabKey].loading = true;
      this.tabData[this.selectedTabKey].page.currentPage = 1;
      this.fetchModules();
    },
    loadMore() {
      if (!this.tabData[this.selectedTabKey].loading) {
        this.tabData[this.selectedTabKey].loading = true;
        this.tabData[this.selectedTabKey].page.currentPage++;
        this.fetchModules();
      }
    },
    fetchAllTags() {
      $axios
        .get(`/proxy/api/app/tag/queryByApplyTo`, { params: { applyTo: 'AppModule' } })
        .then(({ data }) => {
          if (data.code == 0) {
            this.tags = data.data;
          }
        })
        .catch(error => {});
    },
    onScrollY(e) {
      let { y } = e.target.__vue__.ps.reach;
      if (y === 'end' && !this.tabData[this.selectedTabKey].noMore) {
        this.loadMore();
      }
    },

    fetchModules() {
      $axios
        .post(`/proxy/api/app/module/query`, {
          ...this.tabData[this.selectedTabKey].searchParams,
          page: this.tabData[this.selectedTabKey].page
        })
        .then(({ data }) => {
          this.tabData[this.selectedTabKey].loading = false;
          if (this.tabData[this.selectedTabKey].page.currentPage > 1) {
            if (data.data.data && data.data.data.length > 0) {
              this.tabData[this.selectedTabKey].noMore = false;
              let list = map(data.data.data, item => {
                item.icon = this.iconDataToJson(item.icon);
                return item;
              });
              this.tabData[this.selectedTabKey].moduleList.push(...list);
            } else {
              this.tabData[this.selectedTabKey].noMore = true;
              this.$message.info('无更多数据');
            }
          } else {
            this.tabData[this.selectedTabKey].noMore = false;
            let list = map(data.data.data, item => {
              item.icon = this.iconDataToJson(item.icon);
              return item;
            });
            this.tabData[this.selectedTabKey].moduleList = list;
            if (data.data.data.length == 0) {
              this.tabData[this.selectedTabKey].noMore = true;
            }
          }
          if (data.data.data.length < this.tabData[this.selectedTabKey].page.pageSize) {
            this.tabData[this.selectedTabKey].noMore = true;
          }
        })
        .catch(() => {
          this.tabData[this.selectedTabKey].loading = false;
        });
    },
    iconDataToJson(data) {
      if (!data) {
        data = {
          icon: '',
          bgColor: ''
        };
      } else {
        try {
          let iconJson = JSON.parse(data);
          if (iconJson) {
            data = iconJson;
          }
        } catch (e) {
          if (typeof data == 'string') {
            let iconJson = {
              icon: data,
              bgColor: ''
            };
            data = iconJson;
          }
          return data;
        }
      }
      return data;
    },
    changeMoreTag(tab) {
      this.tabData[tab].showMoreTag = !this.tabData[tab].showMoreTag;
      this.$nextTick(() => {
        this.tabData[tab].tagHeight = this.$el.querySelector('.' + tab + '_tags').clientHeight;
      });
    }
  }
};
</script>
