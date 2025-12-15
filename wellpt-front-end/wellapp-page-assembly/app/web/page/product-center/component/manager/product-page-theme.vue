<template>
  <div class="theme-pane-tabs">
    <a-tabs class="setting-pane-tabs">
      <a-tab-pane v-for="tab in deviceTabs" :key="tab.key" :tab="tab.title" class="theme-pane">
        <!-- <a-alert :show-icon="false" message="选择当前产品可用的主题，用户将可以切换使用" type="info" banner style="margin-bottom: 20px" /> -->

        <div class="default-theme-form-item" style="margin-bottom: 12px">
          <Modal
            title="选择主题"
            :ok="e => onConfirmSelectTheme(e, tab.key)"
            wrapperClass="theme-pane"
            :container="getThemePackModalContainer"
            :maxHeight="520"
            :width="800"
          >
            <a-button type="primary" icon="plus" style="margin-right: 8px" @click="onClickAddTheme(tab.key)">添加</a-button>
            <template slot="content">
              <div style="display: flex; align-items: baseline; margin-bottom: 10px">
                <a-checkable-tag :checked="themeSearchTags[tab.key].length == 0" @change="handleThemeSearchTagChange(undefined, tab.key)">
                  全部
                </a-checkable-tag>
                <div style="min-height: 26px">
                  <template v-for="tag in themeTagOptions">
                    <a-checkable-tag
                      :key="tag.uuid"
                      :checked="themeSearchTags[tab.key].includes(tag.uuid)"
                      @change="checked => handleThemeSearchTagChange(tag.uuid, tab.key)"
                    >
                      {{ tag.name }}
                    </a-checkable-tag>
                  </template>
                </div>
              </div>
              <div v-if="themeDsLoading" class="spin-loading-center">
                <a-spin />
              </div>
              <template v-else>
                <a-empty v-if="themeDataSource[tab.key].length == 0"></a-empty>
                <a-row
                  v-else
                  type="flex"
                  v-for="(themePack, i) in themeDataSource[tab.key]"
                  :key="'themePack_' + i"
                  @click.native="onClickSelectThemePack(themePack, tab.key, i)"
                  :class="['theme-row-item', themeDsSelectKeys[tab.key].includes(themePack.themeClass) ? 'selected' : '']"
                >
                  <a-col flex="64px">
                    <a-avatar :size="48" :src="themePack.logo || undefined">
                      <Icon slot="icon" :type="themePack.logo || 'pticon iconfont icon-ptkj-zhutifengge'"></Icon>
                    </a-avatar>
                  </a-col>
                  <a-col flex="auto">
                    <div class="theme-name">{{ themePack.name }}</div>
                    <div class="color-bk-line">
                      <!-- 主题可选色 -->
                      <template v-if="themePack.colorClassify.length == 0">
                        <a-button size="small" icon="loading" type="link" />
                      </template>
                      <template v-else>
                        <span
                          v-for="(color, c) in themePack.colorClassify"
                          :class="['color-bk', color.default ? 'selected' : '']"
                          :style="{ backgroundColor: color.value }"
                          :key="'themePack_color_' + themePack.uuid + c"
                          @click.stop="onChangeSelectColorDefault(color, themePack.colorClassify)"
                        >
                          <a-icon type="check" v-show="color.default" />
                        </span>
                      </template>
                    </div>
                  </a-col>
                  <a-col flex="30px" style="color: var(--w-primary-color); font-size: 18px; margin-right: 16px">
                    <a-checkbox :checked="themeDsSelectKeys[tab.key].includes(themePack.themeClass)" />
                  </a-col>
                </a-row>
              </template>
            </template>
          </Modal>
          <a-button icon="plus" @click="clickToCreateThemePack">创建主题</a-button>
        </div>
        <div class="tip">选择当前产品可用的主题，用户将可以切换使用</div>
        <template v-for="(theme, j) in setting.theme[tab.key].theme">
          <a-row type="flex" :key="'theme_' + tab.key + j" class="theme-row-item">
            <a-col flex="auto">
              <a-row type="flex">
                <a-col flex="64px">
                  <a-avatar
                    :size="48"
                    :src="themePackMap[theme.themeClass] != undefined ? themePackMap[theme.themeClass].logo || undefined : undefined"
                  >
                    <Icon
                      slot="icon"
                      :type="
                        themePackMap[theme.themeClass] != undefined
                          ? themePackMap[theme.themeClass].logo || 'pticon iconfont icon-ptkj-zhutifengge'
                          : 'pticon iconfont icon-ptkj-zhutifengge'
                      "
                    ></Icon>
                  </a-avatar>
                </a-col>
                <a-col flex="auto">
                  <div>
                    <span class="theme-name">
                      {{ themePackMap[theme.themeClass] != undefined ? themePackMap[theme.themeClass].name : theme.name }}
                    </span>
                    <a-tag
                      style="margin-left: 10px"
                      v-if="themePackMap[theme.themeClass] != undefined && themePackMap[theme.themeClass].status == 'UNPUBLISHED'"
                    >
                      已下架
                    </a-tag>
                    <template v-if="themePackMap[theme.themeClass] == undefined">
                      <a-tag color="red" style="margin-left: 10px">已删除</a-tag>
                      <span style="color: #cdcdcd; font-size: 11px">保存后将自动移除该配置</span>
                    </template>
                  </div>

                  <div class="color-bk-line" v-if="themePackMap[theme.themeClass] != undefined">
                    <!-- 主题可选色 -->
                    <template v-if="themePackMap[theme.themeClass].colorClassify.length == 0">
                      <a-button size="small" icon="loading" type="link" />
                    </template>
                    <template v-else>
                      <span
                        v-for="(color, c) in themePackMap[theme.themeClass].colorClassify"
                        :key="c"
                        class="color-bk"
                        :style="{ backgroundColor: color.value }"
                        @click="setThemeColorAsDefault(theme, c)"
                      >
                        <a-icon type="check" v-show="theme.colorClass == color.colorClass" />
                      </span>
                    </template>
                  </div>
                </a-col>
              </a-row>
            </a-col>
            <a-col flex="140px">
              <div v-if="themePackMap[theme.themeClass] !== undefined">
                <a-switch
                  checked-children="默认"
                  :checked="setting.theme[tab.key].defaultTheme == theme.themeClass"
                  @change="e => onChangeDefaultTheme(e, setting.theme[tab.key], theme.themeClass)"
                />
                <a-button type="link" @click="setting.theme[tab.key].theme.splice(j, 1)">
                  <Icon type="pticon iconfont icon-ptkj-shanchu" />
                  删除
                </a-button>
              </div>
            </a-col>
          </a-row>
        </template>

        <div class="sub-title" style="margin-bottom: 0px">页面主题绑定</div>
        <div class="tip">某些页面希望有固定的主题风格，不随用户切换主题而变更样式</div>
        <a-spin v-if="pageBindThemeLoading" />
        <template v-for="(page, i) in pageBindTheme[tab.key]">
          <a-row class="theme-bind-row-item" type="flex" :key="i">
            <a-col flex="auto">
              <a-row type="flex">
                <a-col class="theme-row-item" style="width: 273px; padding: 6px 0 6px 20px">
                  <a-row type="flex" style="flex-wrap: nowrap">
                    <a-col flex="60px">
                      <a-avatar :size="48">
                        <Icon slot="icon" type="pticon iconfont icon-szgy-zhuye"></Icon>
                      </a-avatar>
                    </a-col>
                    <a-col flex="auto">
                      <div
                        :title="pageMap[page.uuid].name"
                        style="
                          max-width: 150px;
                          text-overflow: ellipsis;
                          overflow: hidden;
                          white-space: nowrap;
                          padding-right: 5px;
                          color: var(--w-text-color-dark);
                          font-size: var(--w-font-size-base);
                          font-weight: bold;
                          margin-bottom: 4px;
                        "
                      >
                        {{ pageMap[page.uuid].name }}
                      </div>
                      <a-tag class="tag-no-border" :color="pageMap[page.uuid].layoutFixed ? 'blue' : undefined">
                        {{ pageMap[page.uuid].layoutFixed ? '统一导航布局' : '自定义' }}
                      </a-tag>
                    </a-col>
                  </a-row>
                </a-col>
                <a-col flex="30px" style="align-self: center; text-align: center">
                  <a-icon type="link" :rotate="45" style="font-size: 18px; color: var(--w-text-color-dark)" />
                </a-col>
                <a-col class="theme-row-item" style="width: 377px; padding: 6px 0 6px 20px">
                  <a-row type="flex" style="flex-wrap: nowrap">
                    <template v-if="themePackMap[page.theme.class] != undefined">
                      <a-col flex="64px">
                        <a-avatar
                          :size="48"
                          :src="themePackMap[page.theme.class] != undefined ? themePackMap[page.theme.class].logo || undefined : undefined"
                        >
                          <Icon
                            slot="icon"
                            :type="
                              themePackMap[page.theme.class] != undefined
                                ? themePackMap[page.theme.class].logo || 'pticon iconfont icon-ptkj-zhutifengge'
                                : 'pticon iconfont icon-ptkj-zhutifengge'
                            "
                          ></Icon>
                        </a-avatar>
                      </a-col>
                      <a-col flex="auto">
                        <span class="theme-name">{{ themePackMap[page.theme.class].name }}</span>
                        <div class="color-bk-line">
                          <span
                            v-for="(color, c) in themePackMap[page.theme.class].colorClassify"
                            :class="['color-bk', page.theme.colorClass == color.colorClass ? 'selected' : '']"
                            :style="{ backgroundColor: color.value }"
                            :key="'themePack_color_' + c"
                            @click="e => changeBindPageThemeColor(page, color)"
                          >
                            <a-icon type="check" v-show="page.theme.colorClass == color.colorClass" />
                          </span>
                        </div>
                      </a-col>
                    </template>
                    <template v-else>
                      <a-col>
                        <a-tag color="red">绑定的主题已删除</a-tag>
                        <div style="color: #cdcdcd; font-size: 11px; line-height: 24px">请重新选择主题, 否则保存后将移除该配置</div>
                      </a-col>
                    </template>
                  </a-row>
                </a-col>
              </a-row>
            </a-col>
            <a-col flex="150px" style="padding-left: 20px; align-self: center">
              <Modal
                title="页面主题绑定"
                :ok="e => onConfirmBindPageTheme(e, tab.key, page)"
                :container="getThemePackModalContainer"
                :maxHeight="520"
              >
                <a-button
                  type="link"
                  size="small"
                  @click="
                    () => {
                      pageSelected = page.uuid;
                      pageThemeSelected = page.theme.class;
                    }
                  "
                >
                  <Icon type="pticon iconfont icon-ptkj-shezhi"></Icon>
                  设置
                </a-button>
                <template slot="content">
                  <a-form-model>
                    <a-form-model-item label="选择主题风格固定的页面">
                      <a-select style="width: 100%" v-model="pageSelected">
                        <a-select-option v-for="(page, i) in pages[tab.key]" :value="page.uuid" :key="'pageOpt_' + i">
                          {{ page.name }}
                        </a-select-option>
                      </a-select>
                    </a-form-model-item>
                    <a-form-model-item label="选择页面的主题">
                      <a-select style="width: 100%" v-model="pageThemeSelected" :options="themePackOptions"></a-select>
                    </a-form-model-item>
                  </a-form-model>
                </template>
              </Modal>
              <a-button type="link" size="small" @click="pageBindTheme[tab.key].splice(i, 1)">
                <Icon type="pticon iconfont icon-ptkj-shanchu" />
                删除
              </a-button>
            </a-col>
          </a-row>
        </template>

        <div>
          <Modal
            title="页面主题绑定"
            :ok="e => onConfirmBindPageTheme(e, tab.key)"
            :container="getThemePackModalContainer"
            :maxHeight="520"
          >
            <a-button type="primary" icon="plus" @click="e => beforeOpenPageThemeModal()">添加绑定</a-button>
            <template slot="content">
              <a-form-model>
                <a-form-model-item label="选择主题风格固定的页面">
                  <a-select style="width: 100%" v-model="pageSelected">
                    <a-select-option v-for="(page, i) in pages[tab.key]" :value="page.uuid" :key="'pageOpt_' + i">
                      {{ page.name }}
                    </a-select-option>
                  </a-select>
                </a-form-model-item>
                <a-form-model-item label="选择页面的主题">
                  <a-select style="width: 100%" v-model="pageThemeSelected">
                    <template v-for="(theme, i) in themePackOptions">
                      <a-select-option v-if="themePackMap[theme.value].status == 'PUBLISHED'" :value="theme.value" :key="'themeOpt_' + i">
                        {{ theme.label }}
                      </a-select-option>
                    </template>
                  </a-select>
                </a-form-model-item>
              </a-form-model>
            </template>
          </Modal>
        </div>
      </a-tab-pane>
    </a-tabs>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import '../../css/assemble.less';
import { debounce, orderBy } from 'lodash';
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'ProductPageTheme',
  inject: ['pageContext'],
  props: {
    setting: Object,
    pages: Object
  },
  components: { Modal },
  computed: {
    pageMap() {
      let map = {};
      for (let k in this.pages) {
        for (let p of this.pages[k]) {
          map[p.uuid] = p;
        }
      }
      return map;
    }

    // pageBinds(){
    //   let uuid = [];
    //   for(let type of ['pc','mobile']){
    //     for(let p of this.pageBindTheme[type]){
    //       uuid.push(p.uuid)
    //     }
    //   }
    //   return uuid;
    // }
  },
  data() {
    return {
      themeStatus: {},
      themePack: {
        pc: [
          {
            themeClass: '',
            name: '系统默认'
          }
        ],
        mobile: [
          {
            themeClass: '',
            name: '系统默认'
          }
        ]
      },
      themeDsUnFetched: true,
      themeDsSelectKeys: {
        pc: [],
        mobile: []
      },
      themeDsSelections: {
        pc: [],
        mobile: []
      },
      themeSearchTags: {
        pc: [],
        mobile: []
      },
      themeDataSource: {
        pc: [],
        mobile: []
      },
      themeTagOptions: [],
      themeDsLoading: true,
      deviceTabs: [
        { key: 'pc', title: '桌面端' },
        { key: 'mobile', title: '移动端' }
      ],
      pageBindThemeLoading: true,
      pageBindTheme: {
        pc: [],
        mobile: []
      },
      pageSelected: undefined,
      pageThemeSelected: undefined,
      themePackMap: {},
      themePackDetailMap: {},
      themePackOptions: []
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    // 获取所有主题
    this.fetchThemes().then(list => {
      let themeClass = [];
      if (list) {
        for (let d of list) {
          d.colorClassify = []; // 解析主题色
          if (d.themeColor != undefined) {
            let themeColors = d.themeColors.split(';');
            for (let i = 0, len = themeColors.length; i < len; i++) {
              let isDefault = themeColors[i] == d.defaultThemeColor,
                colorClass = `primary-color-${i + 1}`;
              d.colorClassify.push({
                value: themeColors[i],
                default: isDefault,
                colorClass
              });
              if (isDefault) {
                d.colorClass = colorClass;
              }
            }
          } else {
            this.fetchThemePackDetail(d.uuid).then(result => {
              if (result.defJson) {
                let classify = JSON.parse(result.defJson).body.colorConfig.themeColor.classify;
                for (let i = 0, len = classify.length; i < len; i++) {
                  let colorClass = `primary-color-${i + 1}`;
                  classify[i].colorClass = colorClass;
                  d.colorClassify.push(classify[i]);
                  if (classify[i].default === true) {
                    d.colorClass = colorClass;
                  }
                }
              }
            });
          }
          this.themePackOptions.push({
            label: d.name,
            value: d.themeClass
          });
          themeClass.push(d.themeClass);
          this.$set(this.themePackMap, d.themeClass, d);
        }
      }
      this.pageContext.emitEvent(`product:fetch:themeClass`, themeClass);
      this.fetchPageBindTheme();
    });
  },
  mounted() {},
  methods: {
    fetchPageBindTheme() {
      $axios
        .get(`/proxy/api/app/prod/version/getProdVersionRelaPage`, {
          params: {
            prodVersionUuid: this.setting.prodVersionUuid
          }
        })
        .then(({ data }) => {
          this.pageBindThemeLoading = false;
          if (data.data) {
            let pageIds = { pc: [], mobile: [] };
            for (let key in this.pages) {
              for (let p of this.pages[key]) {
                pageIds[key].push(p.id);
              }
            }
            for (let d of data.data) {
              if (d.theme) {
                this.pageBindTheme[pageIds.pc.includes(d.pageId) ? 'pc' : 'mobile'].push({
                  uuid: d.pageUuid,
                  id: d.pageId,
                  theme: JSON.parse(d.theme)
                });
              }
            }
            this.setting.pageBindTheme = this.pageBindTheme;
          }
        })
        .catch(error => {});
    },
    changeBindPageThemeColor(page, color) {
      page.theme.color = color.value;
      page.theme.colorClass = color.colorClass;
    },
    onChangeSelectColorDefault(color, list) {
      for (let i = 0, len = list.length; i < len; i++) {
        list[i].default = false;
      }
      color.default = true;
    },
    onConfirmBindPageTheme(e, type, page) {
      if (this.pageSelected && this.pageThemeSelected) {
        let pageMap = {};
        for (let p of this.pages[type]) {
          pageMap[p.uuid] = p;
        }

        if (page != undefined) {
          page.theme.class = this.pageThemeSelected;
          page.uuid = this.pageSelected;
          page.id = this.pageMap[this.pageSelected].id;
          page.theme.color = this.themePackMap[this.pageThemeSelected].defaultColor;
          page.theme.colorClass = this.themePackMap[this.pageThemeSelected].colorClass;
        } else {
          let pageThemes = this.pageBindTheme[type];
          let update = false;
          let themePack = this.themePackMap[this.pageThemeSelected];
          for (let t of pageThemes) {
            if (t.uuid == this.pageSelected) {
              if (themePack && t.theme.class != this.pageThemeSelected) {
                t.theme.color = themePack.defaultColor;
                t.theme.colorClass = themePack.colorClass;
              }
              t.theme.class = this.pageThemeSelected;
              update = true;
              break;
            }
          }
          if (!update) {
            pageThemes.push({
              uuid: this.pageSelected,
              id: this.pageMap[this.pageSelected].id,
              theme: {
                class: this.pageThemeSelected,
                color: themePack.defaultColor,
                colorClass: themePack.colorClass
              }
            });
          }
        }

        e(true);
      }
    },
    beforeOpenPageThemeModal() {
      this.pageSelected = undefined;
      this.pageThemeSelected = undefined;
    },
    fetchThemes(tagUuids, type, status) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/theme/pack/query`, { status, tagUuids, type: type != undefined ? type.toUpperCase() : undefined })
          .then(({ data }) => {
            resolve(data.data.data);
          })
          .catch(() => {});
      });
    },
    fetchThemePackByClass(themeClasses) {
      return new Promise((resolve, reject) => {
        $axios.post(`/proxy/api/theme/pack/listDetailsByThemeClasses`, themeClasses).then(({ data }) => {
          if (data.code == 0 && data.data) {
            resolve(data.data);
          }
        });
      });
    },

    clickToCreateThemePack() {
      window.open('/theme/index', '_blank');
    },
    getThemePackModalContainer() {
      return this.$el.querySelector('.default-theme-form-item');
    },
    onConfirmSelectTheme(e, type) {
      let selections = this.themeDsSelections[type];
      let theme = this.setting.theme[type].theme;
      let themeClassMap = {};
      for (let t of theme) {
        themeClassMap[t.themeClass] = t;
      }
      selections = orderBy(selections, 'index', 'asc');
      for (let s of selections) {
        if (themeClassMap[s.themeClass] == undefined) {
          let colorClass = undefined;
          for (let c of s.colorClassify) {
            if (c.default === true) {
              colorClass = c.colorClass;
              break;
            }
          }
          theme.push({
            themeClass: s.themeClass,
            colorClass,
            name: s.name
          });
        } else {
          let colorClass = undefined;

          for (let c of s.colorClassify) {
            if (c.default === true) {
              colorClass = c.colorClass;
              break;
            }
          }
          themeClassMap[s.themeClass].colorClass = colorClass;
        }
      }
      e(true);
    },
    onClickSelectThemePack(pack, type, i) {
      let idx = this.themeDsSelectKeys[type].indexOf(pack.themeClass);
      if (idx == -1) {
        pack.index = i;
        this.themeDsSelectKeys[type].push(pack.themeClass);
        this.themeDsSelections[type].push(pack);
      } else {
        this.themeDsSelectKeys[type].splice(idx, 1);
        this.themeDsSelections[type].splice(idx, 1);
      }
    },
    handleThemeSearchTagChange(uuid, type) {
      let list = this.themeSearchTags[type];
      if (uuid) {
        let idx = list.indexOf(uuid);
        if (idx != -1) {
          list.splice(idx, 1);
        } else {
          list.push(uuid);
        }
      } else {
        list.length = 0;
        // list = []; // 全选情况
      }
      this.themeDsSelectKeys[type] = [];
      this.themeDsSelections[type] = [];
      this.refreshThemeDataSource(list.length == 0 ? undefined : list, type);
    },
    refreshThemeDataSource: debounce(function (tagUuids, type) {
      this.themeDsLoading = true;
      this.fetchThemes(tagUuids, type, 'PUBLISHED').then(list => {
        this.themeDataSource[type] = [];
        if (list) {
          for (let i = 0, len = list.length; i < len; i++) {
            let d = list[i];
            d.colorClassify = [];
            if (d.themeColor != undefined) {
              let themeColors = d.themeColors.split(';');
              for (let i = 0, len = themeColors.length; i < len; i++) {
                let isDefault = themeColors[i] == d.defaultThemeColor,
                  colorClass = `primary-color-${i + 1}`;
                d.colorClassify.push({
                  value: themeColors[i],
                  default: isDefault,
                  colorClass
                });
                if (isDefault) {
                  d.colorClass = colorClass;
                }
              }
            } else {
              this.fetchThemePackDetail(d.uuid).then(result => {
                if (result.defJson) {
                  let classify = JSON.parse(result.defJson).body.colorConfig.themeColor.classify;
                  for (let i = 0, len = classify.length; i < len; i++) {
                    let colorClass = `primary-color-${i + 1}`;
                    classify[i].colorClass = colorClass;
                    d.colorClassify.push(classify[i]);
                    if (classify[i].default === true) {
                      d.colorClass = colorClass;
                    }
                  }
                }
              });
            }
            this.themeDataSource[type].push(d);
          }
        }

        this.themeDsLoading = false;
      });
    }, 500),

    onClickAddTheme(type) {
      this.queryTags();
      this.themeDsSelectKeys[type] = [];
      this.themeDsSelections[type] = [];
      if (this.themeDataSource[type].length == 0) {
        this.fetchThemes(undefined, type, 'PUBLISHED').then(list => {
          this.themeDsLoading = false;
          if (list) {
            for (let d of list) {
              d.colorClassify = []; // 解析主题色
              if (d.themeColor != undefined) {
                let themeColors = d.themeColors.split(';');
                for (let i = 0, len = themeColors.length; i < len; i++) {
                  let isDefault = themeColors[i] == d.defaultThemeColor,
                    colorClass = `primary-color-${i + 1}`;
                  d.colorClassify.push({
                    value: themeColors[i],
                    default: isDefault,
                    colorClass
                  });
                  if (isDefault) {
                    d.colorClass = colorClass;
                  }
                }
              } else {
                this.fetchThemePackDetail(d.uuid).then(result => {
                  if (result.defJson) {
                    let classify = JSON.parse(result.defJson).body.colorConfig.themeColor.classify;
                    for (let i = 0, len = classify.length; i < len; i++) {
                      let colorClass = `primary-color-${i + 1}`;
                      classify[i].colorClass = colorClass;
                      d.colorClassify.push(classify[i]);
                      if (classify[i].default === true) {
                        d.colorClass = colorClass;
                      }
                    }
                  }
                });
              }

              this.themeDataSource[type].push(d);
            }
          }
        });
      }
    },
    fetchThemePackDetail(uuid) {
      return new Promise((resolve, reject) => {
        if (this.themePackDetailMap[uuid] != undefined) {
          resolve(this.themePackDetailMap[uuid]);
        } else {
          $axios.get(`/proxy/api/theme/pack/details/${uuid}`).then(({ data }) => {
            this.themePackDetailMap[uuid] = data.data;
            resolve(data.data);
          });
        }
      });
    },
    setThemeColorAsDefault(theme, index) {
      theme.colorClass = `primary-color-${index + 1}`;
    },
    onChangeDefaultTheme(ck, target, theme) {
      this.$set(target, 'defaultTheme', theme);
    },

    queryTags() {
      if (!this.tagFetched) {
        this.tagFetched = true;
        $axios.get('/proxy/api/theme/tag/list', {}).then(({ data }) => {
          if (data.data) {
            this.themeTagOptions = data.data;
          }
        });
      }
    }
  }
};
</script>
