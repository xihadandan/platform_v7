<template>
  <HtmlWrapper title="主题管理">
    <a-page-header :title="isIframe ? null : '主题管理'" :class="['theme-manager-container', isIframe ? 'iframe-container' : '']">
      <a-form-model :colon="false" layout="inline" class="flex f_x_s f_y_c">
        <div class="search-form-model">
          <a-form-model-item label="标签">
            <!-- <a-checkable-tag
              :checked="searchParams.tagUuids.length == 0"
              @change="checked => handleAllChange('tagUuids', checked)"
              style="min-width: 40px; margin-right: 8px"
            >
              全部
            </a-checkable-tag> -->
            <a-select mode="multiple" v-model="searchParams.tagUuids" style="width: 400px" allowClear showArrow>
              <a-select-option v-for="(tag, i) in tagOptions" :key="tag.uuid" :value="tag.uuid">
                {{ tag.name }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="状态">
            <a-select v-model="searchParams.status" style="width: 120px" showArrow allowClear>
              <a-select-option v-for="(tag, i) in statusOptions" :key="tag.value" :value="tag.value">
                {{ tag.label }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="客户端">
            <a-select v-model="searchParams.type" style="width: 120px" showArrow allowClear>
              <a-select-option v-for="(tag, i) in typeOptions" :key="tag.value" :value="tag.value">
                {{ tag.label }}
              </a-select-option>
            </a-select>
          </a-form-model-item>
          <a-form-model-item label="">
            <a-input
              v-model.trim="keyword"
              allow-clear
              style="width: 156px; margin-right: 8px"
              @pressEnter="refreshList"
              placeholder="输入关键字查询"
            />
          </a-form-model-item>
          <a-form-model-item label="" class="search-form-buttons btn_has_space">
            <a-button @click="refreshList" type="primary">
              <Icon type="" />
              查询
            </a-button>
            <a-button @click="resetSearchForm">
              <Icon type="" />
              重置
            </a-button>
          </a-form-model-item>
        </div>
      </a-form-model>
      <div class="list-header-buttons">
        <a-dropdown>
          <a-menu slot="overlay" @click="handleAddMenuClick">
            <a-menu-item key="PC">
              <Modal title="新建主题" :ok="e => saveNewThemePack(e)" :width="700" :maxHeight="600" centered ref="newThemeModal">
                <a-button type="link" size="small" @click="startCreateNewThemePack('PC')">桌面端主题</a-button>
                <template slot="content">
                  <ThemePackDetail :detail="currentPackDetail" ref="themePackDetail" />
                </template>
              </Modal>
            </a-menu-item>
            <a-menu-item key="MOBILE">
              <a-button type="link" size="small" @click="startCreateNewThemePack('MOBILE')">移动端主题</a-button>
            </a-menu-item>
          </a-menu>
          <a-button type="primary">
            <Icon type="iconfont icon-ptkj-jiahao" />
            新增
          </a-button>
        </a-dropdown>

        <ImportDef title="导入主题" @importDone="importDone">
          <a-button>
            <Icon type="ant-iconfont import" />
            导入
          </a-button>
        </ImportDef>
      </div>
      <a-list :grid="grid" :data-source="themePacks" rowKey="uuid" :loading="loading" class="theme-list">
        <div slot="loadMore" v-if="themePacks.length > 0" style="text-align: center" v-show="hasMore">
          <a-button type="link" @click="loadMore" :loading="loading">加载更多</a-button>
        </div>
        <a-list-item slot="renderItem" slot-scope="item, index" style="min-width: 320px">
          <a-card hoverable :bodyStyle="{ padding: '0px', borderRadius: '4px' }">
            <div class="item-img">
              <img v-if="item.thumbnail" :src="item.thumbnail" style="width: 247px; height: 139px; border-radius: 4px" />
              <ThemeIndexSvg
                v-else
                :color="item.defaultThemeColor ? item.defaultThemeColor : undefined"
                style="width: 247px; height: 139px"
              />
            </div>
            <div class="flex item-detail">
              <div class="f_s_0" style="padding: 24px 0px 24px 12px; width: 52px">
                <a-avatar :size="32" style="background-color: var(--w-primary-color-2); color: var(--w-primary-color)">
                  <Icon slot="icon" :type="item.type == 'PC' ? 'ant-iconfont desktop' : 'iconfont icon-ptkj-shoujishoujihaoma'"></Icon>
                </a-avatar>
              </div>
              <div class="f_g_1">
                <div class="flex">
                  <div
                    :title="item.name"
                    style="
                      max-width: 150px;
                      text-overflow: ellipsis;
                      overflow: hidden;
                      white-space: nowrap;
                      padding-right: 5px;
                      color: var(--w-text-color-dark);
                      font-size: var(--w-font-size-base);
                      font-weight: bold;
                    "
                  >
                    {{ item.name }}
                  </div>
                  <a-tag :class="['tag-no-border w-ellipsis-1', item.status == 'PUBLISHED' ? 'primary-color' : undefined]">
                    {{ item.status == 'PUBLISHED' ? '已发布' : '未发布' }}
                  </a-tag>
                </div>
                <div
                  style="
                    max-width: 150px;
                    text-overflow: ellipsis;
                    overflow: hidden;
                    white-space: nowrap;
                    padding-right: 5px;
                    color: var(--w-text-color-light);
                    font-size: var(--w-font-size-sm);
                  "
                >
                  {{ item.remark }}
                </div>
              </div>
              <div class="f_s_0" :style="{ width: '34px' }">
                <a-dropdown>
                  <a-button type="icon" size="small" title="更多操作">
                    <Icon type="pticon iconfont icon-ptkj-gengduocaidan"></Icon>
                  </a-button>
                  <a-menu slot="overlay" @click="e => handleMenuClick(e, item, index)">
                    <a-menu-item @click="startDesign(item.uuid)">配置</a-menu-item>
                    <a-menu-item key="copy">
                      <Modal
                        title="复制"
                        ref="copyModal"
                        :maxHeight="500"
                        centered
                        wrapperClass="theme-pack-copy-modal"
                        :ok="e => copyThemePack(e, item)"
                      >
                        <span @click="prepareCopyThemePack(item)">复制</span>
                        <template slot="content">
                          <a-form-model :colon="false" :model="currentPackDetail" :rules="rules" ref="copyForm">
                            <a-form-model-item label="主题类型" prop="type">
                              <a-button
                                block
                                class="theme-pack-type-button"
                                :type="currentPackDetail.type == 'PC' ? 'primary' : 'default'"
                                @click="currentPackDetail.type = 'PC'"
                              >
                                <div>
                                  <div>
                                    <a-icon type="desktop" />
                                  </div>
                                  <div>
                                    桌面端主题
                                    <div>以当前主题复制出一个桌面端主题副本</div>
                                  </div>
                                </div>
                              </a-button>
                              <a-button
                                block
                                class="theme-pack-type-button"
                                :type="currentPackDetail.type == 'MOBILE' ? 'primary' : 'default'"
                                @click="currentPackDetail.type = 'MOBILE'"
                              >
                                <div>
                                  <div>
                                    <a-icon type="mobile" />
                                  </div>
                                  <div>
                                    移动端主题
                                    <div>以当前主题复制出一个移动端主题副本</div>
                                  </div>
                                </div>
                              </a-button>
                            </a-form-model-item>
                            <a-form-model-item label="主题类名" prop="themeClass">
                              <a-input :addonBefore="themeClassPrefix" :value="classSuffix()" @change="e => mergeClassPrefix(e)" />
                            </a-form-model-item>
                          </a-form-model>
                        </template>
                      </Modal>
                    </a-menu-item>
                    <a-menu-item key="export">导出</a-menu-item>
                    <a-menu-item key="delete">删除</a-menu-item>
                  </a-menu>
                </a-dropdown>
              </div>
            </div>
          </a-card>
        </a-list-item>
      </a-list>
      <ExportDef
        ref="exportDef"
        :title="exportDef.title"
        :uuid="exportDef.uuid"
        :type="exportDef.type"
        :fileName="exportDef.fileName"
      ></ExportDef>
    </a-page-header>
  </HtmlWrapper>
</template>
<style lang="less"></style>
<script type="text/babel">
import './css/index.less';
import { debounce } from 'lodash';
import Modal from '@pageAssembly/app/web/lib/modal.vue';
import ThemeIndexSvg from './component/theme-index-svg.vue';
import ThemePackDetail from './component/theme-pack-detail.vue';
import { getQueryString } from '@framework/vue/utils/function';
import ExportDef from '@pageAssembly/app/web/lib/eximport-def/export-def.vue';
import ImportDef from '@pageAssembly/app/web/lib/eximport-def/import-def.vue';
export default {
  name: '',
  props: {},
  components: { Modal, ThemePackDetail, ThemeIndexSvg, ExportDef, ImportDef },
  computed: {},
  data() {
    return {
      grid: { gutter: 16, xs: 1, sm: 1, md: 2, lg: 3, xl: 3, xxl: 4 },
      loading: true,
      labelCol: { span: 2 },
      wrapperCol: { span: 18 },
      tagUuids: [undefined],
      searchParams: { tagUuids: [], status: undefined, type: undefined },
      keyword: '',
      page: { currentPage: 1, pageSize: 20, totalCount: 0 },
      tagOptions: [],
      statusOptions: [
        { label: '已发布', value: 'PUBLISHED' },
        { label: '未发布', value: 'UNPUBLISHED' }
      ],
      typeOptions: [
        { label: '桌面端', value: 'PC' },
        { label: '移动端', value: 'MOBILE' }
      ],
      rules: {
        themeClass: [
          { required: true, message: '主题类必填', trigger: 'blur' },
          { trigger: ['blur', 'change'], validator: this.validateThemeClass }
        ]
      },
      themePacks: [],
      currentEnableSpecify: {},
      currentPackDetail: {
        type: 'PC',
        themeClass: undefined,
        defJson: { class: undefined },
        name: undefined,
        remark: undefined,
        logo: undefined,
        tagUuids: [],
        thumbnail: undefined
      },
      themeClassPrefix: 'well-theme-',
      isIframe: false,
      getElSpacingForTarget: undefined,
      hasMore: true,
      exportDef: {
        uuid: undefined,
        title: undefined,
        type: 'themePack',
        fileName: undefined
      }
    };
  },
  beforeCreate() {},
  created() {
    import('@framework/vue/utils/util').then(util => {
      this.getElSpacingForTarget = util.getElSpacingForTarget;
    });
  },
  beforeMount() {
    this.queryPacks();
    this.queryTags();
    if (getQueryString('type') == 'iframe') {
      this.isIframe = true;
    }
  },
  mounted() {
    if (this.isIframe) {
      window.parent.postMessage('iframe content mounted', window.parent.origin);
    }
    window.addEventListener('resize', () => {
      this.setListHeight();
    });
  },
  methods: {
    importDone() {
      this.refreshList();
    },
    setListHeight() {
      let $el = this.$el.querySelector('.theme-list .ant-spin-nested-loading');
      if (this.isIframe) {
        let $iframe = window.parent.document.querySelector('iframe[src="/theme/index?type=iframe"]');
        if ($iframe && $iframe.closest('.widget-vpage')) {
          let $root = $iframe.closest('#app');
          let $parent = $root.classList.contains('preview') ? $root : $iframe.closest('.widget-vpage');
          let { maxHeight, totalBottom, totalNextSibling } = this.getElSpacingForTarget($iframe, $parent);
          $iframe.style.cssText += `;height:${maxHeight}px`;
          setTimeout(() => {
            let { maxHeight, totalBottom, totalNextSibling } = this.getElSpacingForTarget($el, this.$root.$el);
            $el.style.cssText = `;height:${maxHeight - 32}px`;
          }, 10);
        } else {
          setTimeout(() => {
            this.setListHeight();
          }, 100);
        }
      } else {
        let $parent = this.$root.$el;
        if (this.$el && this.$el.closest('.widget-vpage')) {
          $parent = this.$root.$el.classList.contains('preview') ? this.$root.$el : this.$el.closest('.widget-vpage');
        }
        const { maxHeight, totalBottom, totalNextSibling } = this.getElSpacingForTarget($el, $parent);
        $el.style.cssText = `;overflow-y:auto;overflow-x:hidden; height:${maxHeight - 32}px`;
      }
    },
    checkClass(value) {
      return new Promise((resolve, reject) => {
        $axios.get(`/proxy/api/theme/pack/getUuidByClass/${value}`, { params: {} }).then(({ data }) => {
          resolve((data.code == 0 && data.data) || data.code != 0);
        });
      });
    },
    validateThemeClass: debounce(function (rule, value, callback) {
      this.checkClass(value).then(exist => {
        callback(exist ? '主题类已存在' : undefined);
      });
    }, 500),
    mergeClassPrefix(e) {
      let value = e.target.value;
      if (value) {
        if (/[^a-zA-Z0-9_]/.test(value)) {
          this.$message.error('主题类名只允许包含字母、数字以及下划线');
        }
        value = value.replace(/[^a-zA-Z0-9_]/g, '');
      }
      this.currentPackDetail.themeClass = this.themeClassPrefix + value;
    },
    classSuffix() {
      if (this.currentPackDetail.themeClass) {
        return this.currentPackDetail.themeClass.split(this.themeClassPrefix)[1];
      }
      return undefined;
    },
    startDesign(uuid) {
      window.open(`/theme/theme-design?uuid=${uuid}`, '_blank');
    },
    prepareCopyThemePack(item) {
      this.currentPackDetail.type = item.type;
    },
    copyThemePack(callback, item) {
      this.$refs.copyForm.validate(validate => {
        if (validate) {
          this.$loading('复制中');
          $axios
            .get(`/proxy/api/theme/pack/copy/${item.uuid}`, {
              params: {
                themeClass: this.currentPackDetail.themeClass,
                name: `${item.name} (复制)`,
                type: this.currentPackDetail.type
              }
            })
            .then(({ data }) => {
              if (data.code == 0) {
                this.currentPackDetail.themeClass = undefined;
                this.currentPackDetail.type = undefined;
                this.currentPackDetail.name = undefined;
                callback(true); //关闭弹窗
                this.$message.success('复制成功');
                this.queryTags();
                this.refreshList();
              }
              this.$loading(false);
            })
            .catch(() => {
              this.$loading(false);
            });
        }
      });
    },
    deleteThemePack(item, index) {
      let _this = this;
      this.$confirm({
        title: '提示',
        content: '确认要删除吗?',
        onOk() {
          _this.$loading('删除中');
          $axios
            .get(`/proxy/api/theme/pack/delete/${item.uuid}`, {})
            .then(({ data }) => {
              if (data.code == 0) {
                _this.$message.success('删除成功');
                _this.themePacks.splice(index, 1);
              }
              _this.$loading(false);
              $axios.get(`/theme/pack/pub/notify`, { params: { type: item.type } });
            })
            .catch(() => {
              _this.$loading(false);
            });
        },
        onCancel() {}
      });
    },
    saveNewThemePack(callback) {
      let _this = this;
      this.$refs.themePackDetail.validate().then(() => {
        // this.$confirm({
        //   title: '确认要新建吗?',
        //   onOk() {
        _this.$loading('保存中');
        let i18ns = [];
        if (_this.currentPackDetail.i18n) {
          for (let key in _this.currentPackDetail.i18n) {
            for (let code in _this.currentPackDetail.i18n[key]) {
              i18ns.push({
                content: _this.currentPackDetail.i18n[key][code],
                locale: key,
                code: 'uuid'
              });
            }
          }
        }
        _this.currentPackDetail.i18ns = i18ns;
        if (_this.currentPackDetail.defJson && typeof _this.currentPackDetail.defJson !== 'string') {
          _this.currentPackDetail.defJson = JSON.stringify(_this.currentPackDetail.defJson);
        }
        $axios
          .post('/proxy/api/theme/pack/save', _this.currentPackDetail)
          .then(({ data }) => {
            if (data.code == 0) {
              if (typeof callback == 'function') {
                callback(true);
              }
              _this.$message.success('保存成功');
              _this.$refs.newThemeModal.visible = false;
              _this.loading = true;
              _this.queryTags();
              _this.refreshList();
            }
            _this.$loading(false);
          })
          .catch(() => {
            _this.$loading(false);
          });
        // },
        // onCancel() {}
        // });
      });
    },

    getCurrentEnableThemeSpecify(callback) {
      $axios.get('/proxy/api/theme/specify/getEnabled', {}).then(({ data }) => {
        if (data.data) {
          this.currentEnableSpecify = data.data;
          if (_.isFunction(callback)) {
            callback();
          }
        }
      });
    },

    queryPacks: debounce(function () {
      this.hasMore = true;
      $axios
        .post(`/proxy/api/theme/pack/query`, { ...this.searchParams, page: this.page, keyword: this.keyword })
        .then(({ data }) => {
          this.loading = false;
          if (this.page.currentPage > 1) {
            if (data.data.data) {
              this.themePacks.push(...data.data.data);
            } else {
              this.$message.info('无更多数据');
            }
          } else {
            this.themePacks = data.data.data || [];
          }
          if (data.data.data.length < this.page.pageSize) {
            this.hasMore = false;
          }
        })
        .catch(() => {
          this.loading = false;
        });
    }, 200),
    resetSearchForm() {
      this.searchParams.tagUuids = [];
      this.searchParams.type = undefined;
      this.searchParams.status = undefined;
      this.keyword = '';
      this.tagUuids = [undefined];
      this.refreshList();
    },
    refreshList() {
      this.loading = true;
      this.page.currentPage = 1;
      this.queryPacks();
      this.setListHeight();
    },
    loadMore() {
      this.loading = true;
      this.page.currentPage++;
      this.queryPacks();
    },
    handletagsUuidChange(value) {
      if (value.length == 0) {
        this.tagUuids = [undefined];
        this.searchParams.tagUuids = [];
      } else if (value.indexOf(undefined) > -1) {
        if (value.indexOf(undefined) == value.length - 1) {
          this.tagUuids = [undefined];
          this.searchParams.tagUuids = [];
        } else {
          value.splice(value.indexOf(undefined), 1);
          this.searchParams.tagUuids = value;
        }
      } else {
        this.searchParams.tagUuids = value;
      }
    },
    handleTagChange(v, checked, key) {
      let array = Array.isArray(this.searchParams[key]);
      if (checked) {
        if (array) {
          this.searchParams[key].push(v);
        } else {
          this.searchParams[key] = v;
        }
      } else {
        if (array) {
          this.searchParams[key].splice(this.searchParams[key].indexOf(v), 1);
        } else {
          this.searchParams[key] = undefined;
        }
      }
    },
    handleAllChange(key, checked) {
      let array = Array.isArray(this.searchParams[key]);
      if (checked) {
        this.searchParams[key] = array ? [] : undefined;
      }
    },
    handleMenuClick(e, item, index) {
      let uuid = item.uuid;
      if (e.key == 'delete') {
        this.deleteThemePack(item, index);
      } else if (e.key == 'export') {
        this.exportTheme(item);
      }
    },
    exportTheme(item) {
      // let ifm = document.createElement('iframe');
      // ifm.hidden = true;
      // ifm.src = `/theme/pack/export/${item.uuid}`;
      // ifm.addEventListener('load', () => {
      //   ifm.remove();
      // });
      // document.body.appendChild(ifm);

      this.exportDef.uuid = item.uuid;
      this.exportDef.title = '导出主题: ' + item.name;
      this.exportDef.fileName = item.name;
      this.$refs.exportDef.show();
    },
    handleAddMenuClick(e) {
      console.log(e);
    },
    queryTags() {
      $axios.get('/proxy/api/theme/tag/list', {}).then(({ data }) => {
        if (data.data) {
          this.tagOptions = data.data;
          this.$nextTick(() => {
            this.setListHeight();
          });
        }
      });
    },
    startCreateNewThemePack(type) {
      this.getCurrentEnableThemeSpecify(() => {
        this.currentPackDetail = {
          type,
          defJson: { class: undefined, body: JSON.parse(this.currentEnableSpecify.defJson) },
          specifyUuid: this.currentEnableSpecify.uuid,
          name: undefined,
          remark: undefined,
          logo: undefined,
          tagUuids: [],
          themeClass: undefined,
          thumbnail: undefined
        };
      });
    }
  }
  // watch: {
  //   searchParams: {
  //     deep: true,
  //     handler() {
  //       this.refreshList();
  //     }
  //   }
  // }
};
</script>
