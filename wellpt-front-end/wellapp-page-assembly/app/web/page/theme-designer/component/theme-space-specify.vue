<template>
  <div class="theme-specify-panel space-specify">
    <a-page-header title="间距">
      <div slot="subTitle">
        网格是构成页面栅格系统的最小单位。尽量保持单位是偶数，这样在页面放大或者放大或者缩小时还能保持清晰。 Well UI 网格的基数为 4,
        符合偶数的思路同时能够匹配多数主流的显示设备。间距规则以4的倍数为原则, 推荐使用间距: 4、8、12、16、20、24、32
      </div>
      <a-row type="flex" style="flex-flow: row nowrap" v-for="(key, k) in ['paddingConfig', 'marginConfig']" :key="key">
        <a-col flex="auto">
          <a-descriptions :colon="false" :column="3">
            <template slot="title">
              {{ key == 'paddingConfig' ? '内边距' : '外边距' }}
              <a-button size="small" type="link" icon="plus" @click="addDerives(config[key].derive, config[key].code)" />
            </template>
            <a-descriptions-item label="变量"></a-descriptions-item>
            <a-descriptions-item label="值/公式"></a-descriptions-item>
            <a-descriptions-item label="描述"></a-descriptions-item>

            <template v-for="(item, i) in config[key].derive">
              <a-descriptions-item :label="null" :key="i">
                <a-input
                  :value="codeSuffix(item.code, config[key].code)"
                  @click="onSelectItem(key, item)"
                  @change="e => mergeCodeSuffix(e, item, config[key].code)"
                >
                  <template slot="addonBefore">
                    <div @click="onSelectItem(key, item)">{{ config[key].code + '-' }}</div>
                  </template>
                </a-input>
              </a-descriptions-item>
              <a-descriptions-item :label="null" :key="i">
                <a-input v-model="item.value" @click="onSelectItem(key, item)" />
              </a-descriptions-item>
              <a-descriptions-item :label="null" :key="i">
                <a-input-group>
                  <a-row :gutter="8">
                    <a-col :span="20">
                      <a-input v-model="item.remark" @click="onSelectItem(key, item)" />
                    </a-col>
                    <a-col :span="4">
                      <a-button size="small" type="link" icon="delete" @click="deleteDerives(config[key], i)" />
                      <a-button size="small" type="link" icon="file-search" @click="onSelectItem(key, item)" />
                    </a-col>
                  </a-row>
                </a-input-group>
              </a-descriptions-item>
            </template>
          </a-descriptions>
        </a-col>
        <a-col flex="200px" class="preview-col">
          <div>
            <label>
              {{ select[key].code }}
              <code>{{ select[key].value }}</code>
            </label>
            <div v-if="key == 'paddingConfig'">
              <div :class="key" v-show="select[key].value">
                <span>border</span>
                <div :style="setPaddingStyle(key)">
                  <div></div>
                </div>
              </div>
            </div>
            <div v-else>
              <div :class="key" v-show="select[key].value" :style="setMarginStyle(key)">
                <div>
                  <span>border</span>
                  <div></div>
                </div>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'ThemeSpaceSpecify',
  props: {
    config: Object
  },
  components: {},
  computed: {},

  data() {
    return {
      select: {
        paddingConfig: {},
        marginConfig: {}
      }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.select.paddingConfig = this.config.paddingConfig.derive[0];
    this.select.marginConfig = this.config.marginConfig.derive[0];
  },
  methods: {
    onSelectItem(key, item) {
      this.select[key] = item;
    },
    mergeCodeSuffix(e, item, prefix) {
      item.code = prefix + '-' + e.target.value;
    },
    codeSuffix(code, prefix) {
      return code.split(prefix + '-')[1];
    },

    setPaddingStyle(key) {
      let style = {};
      let item = this.select[key];
      if (item.value) {
        if (key == 'paddingConfig') {
          style[item.code] = item.value;
          style.padding = 'var(' + item.code + ')';
        }
      }

      return style;
    },
    setMarginStyle() {
      let item = this.select.marginConfig;
      if (item.value) {
        return {
          [item.code]: item.value,
          padding: 'var(' + item.code + ')'
        };
      }
      return {};
    },
    deleteDerives(tar, index) {
      if (index == undefined) {
        tar.derive = [];
      } else {
        tar.derive.splice(index, 1);
      }
    },
    addDerives(derive, prefix) {
      derive.push({
        code: prefix + '-' + (derive.length + 1),
        value: undefined,
        remark: undefined
      });
    }
  },
  watch: {}
};
</script>
