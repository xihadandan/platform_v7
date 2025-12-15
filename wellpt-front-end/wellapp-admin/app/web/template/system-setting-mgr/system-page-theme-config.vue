<template>
  <div class="system-page-theme-setting">
    <a-card :bordered="false" :bodyStyle="{ padding: '12px 20px' }" title="系统主题设置">
      <template slot="extra">
        <a-button type="primary" icon="save" @click="saveSystemTheme">保存</a-button>
      </template>
      <div class="spin-center" v-if="loading">
        <a-spin />
      </div>
      <a-tabs v-else>
        <a-tab-pane v-for="(tab, i) in deviceTabs" :key="tab.key" :tab="tab.title" class="theme-pane">
          <a-form-model layout="inline" :colon="false" v-if="tab.key == 'pc'" style="font-weight: bold; margin: 10px">
            <a-form-model-item label="用户自定义">
              <a-switch v-model="setting.userThemeDefinable" />
            </a-form-model-item>
          </a-form-model>
          <template v-for="(theme, j) in setting.theme[tab.key].theme">
            <a-row type="flex" :key="'theme_' + tab.key + j" class="theme-row-item two-col">
              <a-col flex="auto">
                <a-row type="flex">
                  <a-col flex="64px">
                    <a-avatar
                      :size="48"
                      shape="square"
                      :src="themePackMap[theme.themeClass] != undefined ? themePackMap[theme.themeClass].logo || undefined : undefined"
                      :icon="themePackMap[theme.themeClass] != undefined ? themePackMap[theme.themeClass].logo || 'appstore' : 'appstore'"
                      style="background-color: var(--w-primary-color)"
                    />
                  </a-col>
                  <a-col flex="auto">
                    <div>
                      {{ themePackMap[theme.themeClass] != undefined ? themePackMap[theme.themeClass].name : theme.name }}
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
              <a-col flex="120px">
                <div v-if="themePackMap[theme.themeClass] !== undefined">
                  <a-switch
                    style="width: 66px"
                    checked-children="默认"
                    un-checked-children="默认"
                    :checked="setting.theme[tab.key].defaultTheme == theme.themeClass"
                    @change="e => onChangeDefaultTheme(e, setting.theme[tab.key], theme.themeClass)"
                  />
                  <a-button type="link" size="small" icon="delete" @click="deleteSelectedTheme(setting.theme[tab.key], j, tab.key)">
                    删除
                  </a-button>
                </div>
              </a-col>
            </a-row>
          </template>
          <div class="default-theme-form-item">
            <Drawer
              title="选择主题"
              :destroyOnClose="true"
              mask
              :forceRender="false"
              :ok="e => onAddTheme(e, setting.theme[tab.key].theme, tab.key)"
            >
              <a-button type="link" icon="plus" @click="onAddBindTheme(tab.key)">添加</a-button>
              <template slot="content">
                <div style="display: flex; align-items: baseline; margin-bottom: 10px">
                  <a-checkable-tag :checked="themeSearchTags[tab.key].length == 0" @change="handleThemeSearchTagChange(undefined, tab.key)">
                    全部
                  </a-checkable-tag>
                  <div style="min-height: 26px">
                    <template v-for="(tag, i) in themeTagOptions">
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
                      <a-avatar
                        :size="48"
                        shape="square"
                        :src="themePack.logo || undefined"
                        :icon="themePack.logo == undefined ? 'appstore' : undefined"
                        style="background-color: var(--w-primary-color)"
                      />
                    </a-col>
                    <a-col flex="auto">
                      <div>{{ themePack.name }}</div>
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
                    <a-col flex="30px" style="color: var(--w-primary-color); font-size: 18px">
                      <a-checkbox :checked="themeDsSelectKeys[tab.key].includes(themePack.themeClass)" />
                    </a-col>
                  </a-row>
                </template>
              </template>
            </Drawer>
          </div>
          <template v-if="tab.key == 'pc'">
            <div class="sub-title">页面主题绑定</div>
            <template v-for="(page, i) in pageBindTheme">
              <a-row class="theme-row-item two-col" type="flex">
                <a-col flex="auto">
                  <a-row type="flex">
                    <a-col flex="calc(50% - 15px)" style="outline: 1px solid #e8e8e8; padding: 10px">
                      <a-row type="flex">
                        <a-col flex="60px">
                          <a-avatar :size="48" shape="square" icon="desktop" style="background-color: var(--w-primary-color)" />
                        </a-col>
                        <a-col flex="auto">
                          <div style="text-overflow: ellipsis; overflow: hidden; white-space: nowrap; max-width: 120px; align-self: center">
                            {{ pageMap[page.pageId] != undefined ? pageMap[page.pageId].name : page.pageId }}
                          </div>
                          <a-tag v-if="pageMap[page.pageId] != undefined" :color="pageMap[page.pageId].layoutFixed ? 'blue' : undefined">
                            {{ pageMap[page.pageId].layoutFixed == '1' ? '固定结构页面' : '可调交互页面' }}
                          </a-tag>
                        </a-col>
                      </a-row>
                    </a-col>
                    <a-col flex="30px" style="align-self: center; text-align: center">
                      <a-icon type="link" :rotate="45" style="font-size: 18px" />
                    </a-col>
                    <a-col flex="calc(50% - 15px)" style="outline: 1px solid #e8e8e8; padding: 10px">
                      <a-row type="flex" v-if="themePackMap[page.theme.themeClass] != undefined">
                        <a-col flex="60px">
                          <a-avatar
                            :size="48"
                            shape="square"
                            :src="themePackMap[page.theme.themeClass].logo || undefined"
                            :icon="themePackMap[page.theme.themeClass].logo == undefined ? 'appstore' : undefined"
                            style="background-color: var(--w-primary-color)"
                          />
                        </a-col>
                        <a-col flex="auto">
                          {{ themePackMap[page.theme.themeClass].name }}
                          <div class="color-bk-line">
                            <template v-if="themePackMap[page.theme.themeClass].colorClassify.length == 0">
                              <a-button size="small" type="link" icon="loading" />
                            </template>
                            <template v-else>
                              <span
                                v-for="(color, c) in themePackMap[page.theme.themeClass].colorClassify"
                                :class="['color-bk', page.theme.colorClass == color.colorClass ? 'selected' : '']"
                                :style="{ backgroundColor: color.value }"
                                :key="'themePack_color_' + c"
                                @click="e => changeBindPageThemeColor(page, color, c)"
                              >
                                <a-icon type="check" v-show="page.theme.colorClass == color.colorClass" />
                              </span>
                            </template>
                          </div>
                        </a-col>
                      </a-row>
                      <a-row v-else type="flex">
                        <a-col>
                          <a-tag color="red">绑定的主题已删除</a-tag>
                          <div style="color: #cdcdcd; font-size: 11px; line-height: 24px">请重新选择主题, 否则保存后将移除该配置</div>
                        </a-col>
                      </a-row>
                    </a-col>
                  </a-row>
                </a-col>
                <a-col flex="60px" style="padding-left: 20px">
                  <Modal title="页面主题绑定" :ok="e => onConfirmBindPageTheme(e, undefined, page)" :maxHeight="520">
                    <a-button
                      type="link"
                      size="small"
                      icon="setting"
                      @click="
                        () => {
                          pageSelected = page.pageId;
                          pageThemeSelected = page.theme.themeClass;
                        }
                      "
                    />
                    <template slot="content">
                      <a-form-model>
                        <a-form-model-item label="选择主题风格固定的页面">
                          <a-select style="width: 100%" v-model="pageSelected" :options="pageOptions"></a-select>
                        </a-form-model-item>
                        <a-form-model-item label="选择页面的主题">
                          <a-select style="width: 100%" v-model="pageThemeSelected" :options="themePackOptions"></a-select>
                        </a-form-model-item>
                      </a-form-model>
                    </template>
                  </Modal>
                  <a-button type="link" size="small" icon="delete" @click="pageBindTheme.splice(i, 1)" />
                </a-col>
              </a-row>
            </template>
            <br />
            <Modal title="页面主题绑定" :ok="e => onConfirmBindPageTheme(e, undefined)" :maxHeight="520">
              <a-button
                type="link"
                icon="plus"
                @click="
                  () => {
                    pageSelected = undefined;
                    pageThemeSelected = undefined;
                  }
                "
              >
                添加
              </a-button>
              <template slot="content">
                <a-form-model>
                  <a-form-model-item label="选择主题风格固定的页面">
                    <a-select style="width: 100%" v-model="pageSelected" :options="pageOptions"></a-select>
                  </a-form-model-item>
                  <a-form-model-item label="选择页面的主题">
                    <a-select style="width: 100%" v-model="pageThemeSelected" :options="themePackOptions"></a-select>
                  </a-form-model-item>
                </a-form-model>
              </template>
            </Modal>
          </template>
        </a-tab-pane>
      </a-tabs>
    </a-card>
  </div>
</template>
<style lang="less">
.system-page-theme-setting {
  .theme-pane {
    .theme-row-item {
      padding: 10px;
      margin-left: 5px;
      margin-right: 5px;
      outline: 1px solid #e8e8e8;
      border-radius: 3px;
      margin-bottom: 10px;
      border-left: 4px solid transparent;
      display: flex;
      &.two-col {
        display: inline-flex;
        width: e('calc(50% - 12px)');
      }

      cursor: pointer;
      &.selected {
        background-color: var(--w-primary-color-4);
      }
      // &:hover {
      //   border-left: 4px solid var(--w-primary-color);
      //   background-color: var(--w-primary-color-3);
      // }
      > .ant-col {
        align-self: center;
        &:last-child {
          display: flex;
          justify-content: space-between;
          align-items: center;
        }
      }

      .color-bk-line {
        display: flex;
        align-items: center;
        margin-top: 5px;
        .color-bk {
          width: 18px;
          height: 18px;
          margin-right: 5px;
          display: inline-block;
          line-height: 18px;
          text-align: center;
          color: #fff;
          border-radius: 3px;
          &.selected {
            outline: 3px solid #fff;
          }
        }
      }
    }

    .sub-title {
      margin: 10px;
      font-weight: bold;
    }
  }
}
</style>
<script type="text/babel">
import { debounce, orderBy } from 'lodash';
import Drawer from '@pageAssembly/app/web/lib/drawer.vue';
import Modal from '@pageAssembly/app/web/lib/modal.vue';

export default {
  name: 'SystemPageThemeConfig',
  props: {},
  components: { Drawer, Modal },
  computed: {},
  data() {
    return {
      loading: true,
      themePackMap: {},
      pageMap: {},
      pageOptions: [],
      themePackOptions: [],
      pageSelected: undefined,
      pageThemeSelected: undefined,
      themeStatus: {},
      themePack: {
        pc: [],
        mobile: []
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
      loading: true,
      deviceTabs: [
        { key: 'pc', title: '桌面端' }
        // { key: 'mobile', title: '移动端' }
      ],
      pageBindTheme: [],
      setting: {
        userThemeDefinable: false,
        theme: {
          pc: { theme: [], defaultTheme: undefined },
          mobile: { theme: [], defaultTheme: undefined }
        }
      },
      themePackDetailMap: {}
    };
  },
  beforeCreate() {},
  created() {
    // if (this._$SYSTEM_ID == undefined) {
    //   this._$SYSTEM_ID = 'office_oa'; // 测试使用
    // }
  },
  beforeMount() {
    this.fetchSystemPageSetting().then(d => {
      if (d != undefined) {
        this.setting.userThemeDefinable = d.userThemeDefinable;
        if (d.themeStyle != undefined) {
          let themeStyle = JSON.parse(d.themeStyle);
          this.$set(this.setting, 'theme', themeStyle);
          for (let type in themeStyle) {
            let theme = themeStyle[type].theme;
            for (let t of theme) {
              this.themeDsSelectKeys[type].push(t.themeClass);
            }
          }
        }
      }
    });
    this.fetchAllPages();
    this.queryTags();
    let type = 'pc';
    if (this.themeDataSource[type].length == 0) {
      this.fetchThemes(undefined, type).then(list => {
        this.loading = false;
        for (let i = 0, len = list.length; i < len; i++) {
          let d = list[i];
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

          this.$set(this.themePackMap, d.themeClass, d);
          this.themePackOptions.push({
            label: d.name,
            value: d.themeClass
          });
        }
        this.themeDsLoading = false;
      });
    }
    this.fetchSystemPageBindTheme().then(list => {
      if (list) {
        for (let i = 0, len = list.length; i < len; i++) {
          let theme = JSON.parse(list[i].theme);
          this.pageBindTheme.push({
            pageId: list[i].pageId,
            pageName: undefined,
            theme
          });
        }
      }
    });
  },
  mounted() {},
  methods: {
    onAddBindTheme(type) {
      if (this.themeDsSelectKeys[type].length > 0 && this.themeDsSelectKeys[type].length != this.themeDsSelections[type].length) {
        for (let d of this.themeDataSource[type]) {
          if (this.themeDsSelectKeys[type].includes(d.themeClass)) {
            this.themeDsSelections[type].push(d);
            let themeStyle = this.setting.theme;
            if (d.colorClassify) {
              for (let type in themeStyle) {
                for (let theme of themeStyle[type].theme) {
                  let i = parseInt(theme.colorClass.replace('primary-color-', ''));
                  if (i <= d.colorClassify.length) {
                    for (let j = 0, jlen = d.colorClassify.length; j < jlen; j++) {
                      d.colorClassify[j].default = false;
                    }
                    d.colorClassify[i - 1].default = true;
                  }
                }
              }
            }
          }
        }
      }
    },
    deleteSelectedTheme(item, index, type) {
      let themeClass = item.theme.splice(index, 1)[0];
      this.themeDsSelectKeys[type].splice(this.themeDsSelectKeys[type].indexOf(themeClass), 1);
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
    fetchAllPages() {
      $axios
        .get(`/proxy/api/system/queryAppSystemPages`, { params: { tenant: this._$USER.tenantId, system: this._$SYSTEM_ID } })
        .then(({ data }) => {
          if (data.data) {
            let list = data.data;
            for (let i = 0, len = list.length; i < len; i++) {
              if (this.pageMap[list[i].id] == undefined) {
                this.pageOptions.push({
                  label: list[i].name,
                  value: list[i].id
                });
              }
              this.pageMap[list[i].id] = list[i];
            }
          }
        })
        .catch(error => {});
    },
    onChangeSelectColorDefault(color, list) {
      for (let i = 0, len = list.length; i < len; i++) {
        list[i].default = false;
      }
      color.default = true;
    },
    onAddTheme(e, list, type) {
      console.log(this.themeDsSelections);
      let themeClassMap = {};
      for (let t of list) {
        themeClassMap[t.themeClass] = t;
      }
      let selections = this.themeDsSelections[type];
      if (selections.length > 0) {
        for (let i = 0, len = selections.length; i < len; i++) {
          if (themeClassMap[selections[i].themeClass] == undefined) {
            let colorClass = undefined;
            if (selections[i].colorClassify) {
              for (let j = 0, jlen = selections[i].colorClassify.length; j < jlen; j++) {
                if (selections[i].colorClassify[j].default) {
                  colorClass = `primary-color-${j + 1}`;
                  break;
                }
              }
            }
            list.push({
              themeClass: selections[i].themeClass,
              name: selections[i].name,
              colorClass
            });
          } else {
            let colorClass = undefined;
            if (selections[i].colorClassify) {
              for (let j = 0, jlen = selections[i].colorClassify.length; j < jlen; j++) {
                if (selections[i].colorClassify[j].default) {
                  colorClass = `primary-color-${j + 1}`;
                  break;
                }
              }
            }
            themeClassMap[selections[i].themeClass].colorClass = colorClass;
          }
        }
      }
      e(true);
      // this.themeDsSelections[type].splice(0, this.themeDsSelections[type].length);
      // this.themeDsSelectKeys[type].splice(0, this.themeDsSelectKeys[type].length);
    },
    fetchSystemPageSetting() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/system/getTenantSystemPageSetting`, {
            params: {
              tenant: this._$USER.tenantId,
              system: this._$SYSTEM_ID
            }
          })
          .then(({ data }) => {
            if (data.data) {
              resolve(data.data);
            }
          })
          .catch(error => {});
      });
    },
    fetchSystemPageBindTheme() {
      return new Promise((resolve, reject) => {
        $axios
          .get(`/proxy/api/system/getAppSystemPageThemesBySystem`, {
            params: {
              tenant: this._$USER.tenantId,
              system: this._$SYSTEM_ID
            }
          })
          .then(({ data }) => {
            resolve(data.data);
          })
          .catch(error => {});
      });
    },
    changeBindPageThemeColor(page, color) {
      page.theme.color = color.value;
      page.theme.colorClass = color.colorClass;
    },
    onConfirmBindPageTheme(e, type, page) {
      if (this.pageSelected && this.pageThemeSelected) {
        if (page != undefined) {
          page.theme.themeClass = this.pageThemeSelected;
          page.pageId = this.pageSelected;
          page.theme.color = this.themePackMap[this.pageThemeSelected].defaultColor;
          page.theme.colorClass = this.themePackMap[this.pageThemeSelected].colorClass;
        } else {
          let pageThemes = this.pageBindTheme;
          let update = false;
          let themePack = this.themePackMap[this.pageThemeSelected];
          for (let t of pageThemes) {
            if (t.pageId == this.pageSelected) {
              if (themePack && t.theme.class != this.pageThemeSelected) {
                t.theme.color = themePack.defaultColor;
                t.theme.colorClass = themePack.colorClass;
              }
              t.theme.themeClass = this.pageThemeSelected;
              update = true;
              break;
            }
          }
          if (!update) {
            pageThemes.push({
              pageId: this.pageSelected,
              theme: {
                themeClass: this.pageThemeSelected,
                color: themePack.defaultColor,
                colorClass: themePack.colorClass
              }
            });
          }
        }
      }

      e(true);
    },
    beforeOpenPageThemeModal() {
      this.pageSelected = undefined;
      this.pageThemeSelected = undefined;
    },
    fetchThemes(tagUuids, type) {
      return new Promise((resolve, reject) => {
        $axios
          .post(`/proxy/api/theme/pack/query`, { status: 'PUBLISHED', tagUuids, type: type != undefined ? type.toUpperCase() : undefined })
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
          let color = [];
          for (let c of s.colorClassify) {
            color.push({
              value: c.value,
              default: c.default
            });
          }
          theme.push({
            themeClass: s.themeClass,
            name: s.name,
            logo: s.logo,
            color
          });
        } else {
          let color = [];
          for (let c of s.colorClassify) {
            color.push({
              value: c.value,
              default: c.default
            });
          }
          themeClassMap[s.themeClass].color = color;
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
      this.fetchThemes(tagUuids, type).then(list => {
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
        this.fetchThemes(undefined, type).then(list => {
          for (let d of list) {
            d.colorClassify = JSON.parse(d.defJson).body.colorConfig.themeColor.classify;
            this.themeDataSource[type].push(d);
          }
          this.themeDsLoading = false;
        });
      }
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
    },
    saveSystemTheme() {
      this.$loading('保存中');
      for (let key of ['pc', 'mobile']) {
        let themes = this.setting.theme[key].theme;
        for (let i = 0; i < themes.length; i++) {
          if (this.themePackMap[themes[i].themeClass] == undefined) {
            themes.splice(i, 1);
            i--;
          }
        }
      }
      let submitData = {
        system: this._$SYSTEM_ID,
        userThemeDefinable: this.setting.userThemeDefinable,
        theme: JSON.stringify(this.setting.theme)
      };
      let pageThemes = [];
      if (this.pageBindTheme.length) {
        for (let i = 0; i < this.pageBindTheme.length; i++) {
          if (this.themePackMap[this.pageBindTheme[i].theme.themeClass] == undefined) {
            this.pageBindTheme.splice(i, 1);
            i--;
            continue;
          }
          pageThemes.push({
            pageId: this.pageBindTheme[i].pageId,
            theme: JSON.stringify(this.pageBindTheme[i].theme)
          });
        }
      }
      submitData.pageThemes = pageThemes;

      $axios
        .post(`/proxy/api/system/saveAppSystemPageTheme`, submitData)
        .then(({ data }) => {
          this.$loading(false);
          this.$message.success('保存成功');
          $axios
            .get(`/api/cache/deleteByKey`, {
              params: {
                key: `TENANT_SYSTEM_INFO:${this._$USER.tenantId}:${this._$SYSTEM_ID}`
              }
            })
            .then(({ data }) => {})
            .catch(error => {});
        })
        .catch(() => {
          this.$loading(false);
          this.$message.error('保存失败');
        });
    }
  }
};
</script>
