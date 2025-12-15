<template>
  <a-popconfirm :placement="placement" overlayClassName="theme-popconfirm-setting" v-if="arrow">
    <template slot="title">
      <div style="width: 173px">
        <a-select size="small" v-model="themeClass" style="width: 100%" :options="themeOptions" @change="onSelectTheme" />
        <div class="color-block-select" v-if="themeColors[themeClass]">
          <div
            v-for="(clr, i) in themeColors[themeClass]"
            :key="'tclass_' + i"
            :style="{ backgroundColor: clr.color }"
            @click="onClickSelectThemeColor(clr.color, i)"
          >
            <a-icon
              type="check"
              v-show="
                (themeColor != undefined && themeColor.toLowerCase() == clr.color.toLowerCase()) ||
                (colorClass != undefined && colorClass === 'primary-color-' + (i + 1))
              "
            />
          </div>
        </div>

        <a-radio-group
          class="font-size-radio-group"
          size="small"
          button-style="solid"
          v-model="fontSize"
          @change="onChangeFontSize"
          v-if="themeFontSizes[themeClass] && themeFontSizes[themeClass].length > 1"
        >
          <a-radio-button :value="font.fontSize" v-for="(font, i) in themeFontSizes[themeClass]" :key="'themeFontSizeRadioButton_' + i">
            {{ font.title }}
          </a-radio-button>
        </a-radio-group>
      </div>
    </template>
    <slot name="default"></slot>
    <a-icon v-if="!hasTrigger" type="skin" theme="filled" style="color: inherit; padding: 0 7px" />
  </a-popconfirm>
  <a-dropdown
    placement="bottomRight"
    :trigger="['click']"
    v-model="visible"
    @visibleChange="onVisibleChange"
    v-else
    overlayClassName="theme-popconfirm-setting"
  >
    <slot name="default"></slot>
    <a-icon v-if="!hasTrigger" type="skin" theme="filled" style="color: inherit; padding: 0 7px" />

    <div style="width: 210px; min-height: 60px; padding: 12px 16px; background: #fff; border: 1px solid #e8e8e8" slot="overlay">
      <div class="spin-center" v-if="loading">
        <a-spin />
      </div>
      <template v-else>
        <a-select size="small" v-model="themeClass" style="width: 100%" :options="themeOptions" @change="onSelectTheme" />
        <div class="color-block-select" v-if="colors.length > 1">
          <div
            v-for="(clr, i) in themeColors[themeClass]"
            :key="'class_' + i"
            :style="{ backgroundColor: clr.color }"
            @click="onClickSelectThemeColor(clr.color, i)"
          >
            <a-icon
              type="check"
              v-show="
                (themeColor != undefined && themeColor.toLowerCase() == clr.color.toLowerCase()) ||
                (colorClass != undefined && colorClass === 'primary-color-' + (i + 1))
              "
            />
          </div>
        </div>

        <a-radio-group
          class="font-size-radio-group"
          size="small"
          button-style="solid"
          v-model="fontSize"
          @change="onChangeFontSize"
          v-if="themeFontSizes[themeClass] && themeFontSizes[themeClass].length > 1"
        >
          <a-radio-button :value="font.fontSize" v-for="(font, i) in themeFontSizes[themeClass]" :key="'themeFontSizeRadioButton_' + i">
            {{ font.title }}
          </a-radio-button>
        </a-radio-group>
      </template>
    </div>
  </a-dropdown>
</template>
<style lang="less">
.theme-popconfirm-setting {
  .color-block-select {
    display: inline-flex;
    flex-wrap: wrap;
    padding: 10px 2px 0px 2px;
    > div {
      cursor: pointer;
      text-align: center;
      width: 19px;
      height: 19px;
      border-radius: 3px;
      margin-right: 5px;
      margin-bottom: 5px;
      > i {
        color: #fff;
        font-size: 12px;
        vertical-align: baseline;
      }
    }
    > div:hover {
      outline: 1px solid #bebebe;
    }
  }
  .font-size-radio-group {
    margin-top: 4px;
  }
  .ant-popover-buttons {
    display: none;
  }
  .ant-popover-message > i {
    display: none;
  }
  .ant-popover-message-title {
    padding: 0px;
  }
}
</style>

<script type="text/babel">
import { each, some } from 'lodash';
export default {
  name: 'ThemeSet',
  inject: ['THEME', 'PROD_CONTEXT_PATH', 'USER'],
  props: {
    arrow: {
      type: Boolean,
      default: false
    },
    placement: {
      type: String,
      default: 'bottomRight'
    }
  },
  data() {
    return {
      // themeMetadata: THEME_METADATA, // 由webpack代码注入
      themeClass: '',
      themeColor: undefined,
      colorClass: undefined,
      fontSize: undefined,
      fontSizeClass: undefined,
      hasTrigger: false,
      visible: false,
      loading: true,
      themeSource: []
    };
  },
  computed: {
    colors() {
      if (this.themeClass) {
        return this.themeColors[this.themeClass] || [];
      }
      return [];
      // return [
      //   '#cf1322', // 中国红
      //   '#fadb14', // 日出黄
      //   '#5b8c00', // 青柠绿
      //   // '#95de64', //健康绿
      //   '#13c2c2', //明青
      //   '#0958d9', // 科技蓝
      //   '#722ed1' // 酱紫
      //   // '#eb2f96' // 洋红
      // ];
    },
    fontSizes() {
      if (this.themeClass) {
        return this.themeFontSizes[this.themeClass] || [];
      }
      return [];
    },
    themeColors() {
      let map = {};
      // if (this.themeMetadata) {
      //   for (let i = 0, len = this.themeMetadata.length; i < len; i++) {
      //     map[this.themeMetadata[i].class] = this.themeMetadata[i].colors;
      //   }
      // }
      if (this.themeSource) {
        for (let t of this.themeSource) {
          map[t.class] = t.color;
        }
      }
      return map;
    },
    themeFontSizes() {
      let map = {};
      // if (this.themeMetadata) {
      //   for (let i = 0, len = this.themeMetadata.length; i < len; i++) {
      //     map[this.themeMetadata[i].class] = this.themeMetadata[i].fontSizes;
      //   }
      // }

      if (this.themeSource) {
        for (let t of this.themeSource) {
          map[t.class] = t.fontSize;
        }
      }
      return map;
    },
    themeOptions() {
      let opt = [{ label: '系统默认', value: '' }];
      // if (this.themeMetadata) {
      //   for (let i = 0, len = this.themeMetadata.length; i < len; i++) {
      //     opt.push({
      //       label: this.themeMetadata[i].title,
      //       value: this.themeMetadata[i].class
      //     });
      //   }
      // }
      if (this.THEME.fixed) {
        opt.splice(0, 1);
      }
      if (this.themeSource) {
        for (let t of this.themeSource) {
          opt.push({
            label: t.title,
            value: t.class
          });
        }
      }
      return opt;
    }
  },

  components: {},

  methods: {
    onVisibleChange(visible) {
      if (!this.visible) {
        this.visible = visible;
      }
    },
    commitThemeData() {
      let dataValue = JSON.stringify({
        class: this.themeClass,
        colorClass: this.colorClass,
        color: this.themeClass == '' ? '' : this.themeColor,
        fontSize: this.themeClass == '' ? '' : this.fontSize,
        fontSizeClass: this.fontSizeClass
      });
      let data = new FormData();
      data.append('dataKey', 'USER_THEME');
      data.append('dataValue', dataValue);
      data.append('moduleId', this.PROD_CONTEXT_PATH.split('/')[2]);
      data.append('remark', '用户主题类');

      $axios
        .post('/proxy/api/user/preferences/save', data, {
          headers: { USER_THEME_UPDATE: dataValue } // 提交更新的值到header，后端response后由app.js进行session更新
        })
        .then(() => {
          $axios
            .post(`/api/cache/set`, {
              value: dataValue,
              key: `PROD_VERSION_PAGE_USER_THEME:${this.USER.userId}:${this.PROD_CONTEXT_PATH.split('/')[2]}`
            })
            .then(({ data }) => {})
            .catch(error => {});
        });
    },

    onChangeFontSize(e) {
      this.fontSizeClass = undefined;
      this.THEME.fontSizeClass = undefined;
      let fontSizes = this.themeFontSizes[this.themeClass];
      for (let i = 0, len = fontSizes.length; i < len; i++) {
        if (fontSizes[i].fontSize == this.fontSize && i != 0) {
          this.fontSizeClass = 'font-size-' + (i + 1);
          this.THEME.fontSizeClass = this.fontSizeClass;
          break;
        }
      }
      this.commitThemeData();
    },
    onClickSelectThemeColor(clr, i) {
      if (clr == this.lastThemeColor) {
        // 反选
        // this.themeColor = undefined;
      } else {
        this.themeColor = clr;
      }

      each(this.themeColors[this.themeClass], (item, index) => {
        if (index == i) {
          item.default = true;
        } else {
          item.default = false;
        }
      });

      this.lastThemeColor = this.themeColor;
      this.THEME.themeColor = this.themeColor;
      // 设置主题色
      this.colorClass = `primary-color-${i + 1}`;
      this.THEME.colorClass = this.colorClass;
      this.commitThemeData();
    },
    onSelectTheme() {
      if (this.themeClass === '') {
        this.themeColor = undefined;
        this.colorClass = undefined;
        this.fontSizeClass = undefined;
        this.THEME.themeColor = undefined;
        // document.body.style.removeProperty('--w-primary-color');
      } else {
        let colors = this.themeColors[this.themeClass];
        some(colors, (c, i) => {
          if (c.default) {
            this.themeColor = c.color;
            this.colorClass = `primary-color-${i + 1}`;
          }
          return c.default;
        });
      }
      this.THEME.themeClass = this.themeClass;
      // 设置主题色
      this.THEME.colorClass = this.colorClass;
      this.commitThemeData();
    },

    fetchThemeSource() {
      return new Promise((resolve, reject) => {
        let fetchPublished = filter => {
          $axios
            .get(`/theme/pack/queryPublishedThemeOptions?type=PC`, { params: {} })
            .then(({ data }) => {
              let map = {},
                defaultColor = {};
              if (filter) {
                for (let f of filter.theme) {
                  map[f.themeClass] = f;
                  defaultColor[f.themeClass] = f.colorClass;
                }
              }
              for (let d = 0, dlen = data.length; d < dlen; d++) {
                if (map[data[d].class]) {
                  if (this.THEME.fixed === true && data[d].class != this.THEME.themeClass) {
                    continue;
                  }
                  this.themeSource.push(data[d]);
                  for (let i = 0, len = data[d].color.length; i < len; i++) {
                    if (this.themeColor && this.themeColor.toLowerCase() == data[d].color[i].color.toLowerCase()) {
                      data[d].color[i].default = true;
                    } else if (defaultColor[data[d].class] && defaultColor[data[d].class] == `primary-color-${i + 1}`) {
                      data[d].color[i].default = true;
                    }
                  }
                }
              }

              resolve();
            })
            .catch(error => {
              console.log(error);
            });
        };
        if (this.PROD_CONTEXT_PATH) {
          $axios
            .get(`/proxy/api/app/prod/version/getSetting`, { params: { versionUuid: this.PROD_CONTEXT_PATH.split('/')[2] } })
            .then(({ data }) => {
              if (data.data) {
                let theme = JSON.parse(data.data.theme);
                fetchPublished.call(this, theme.pc);
              }
            })
            .catch(error => {
              console.log(error);
            });
        } else {
          fetchPublished.call(this);
        }
      });
    }
  },
  beforeMount() {},
  mounted() {
    this.themeClass = this.THEME.themeClass;
    this.colorClass = this.THEME.colorClass;
    this.themeColor = this.THEME.themeColor;
    this.fontSize = this.THEME.fontSize;
    this.fontSizeClass = this.THEME.fontSizeClass;
    this.lastThemeColor = this.THEME.themeColor;
    // 获取已发布的主题
    this.fetchThemeSource().then(() => {
      this.loading = false;
      // 判断是否主题类存在
      if (this.themeClass) {
        for (let o of this.themeSource) {
          if (o.class == this.themeClass) {
            return;
          }
        }
        this.themeClass = '';
        this.colorClass = undefined;
        this.themeColor = undefined;
        this.lastThemeColor = undefined;
        this.fontSize = undefined;
      }
    });
  }
};
</script>
