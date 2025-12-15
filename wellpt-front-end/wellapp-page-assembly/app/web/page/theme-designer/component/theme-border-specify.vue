<template>
  <div class="theme-specify-panel">
    <a-page-header title="边框">
      <div slot="subTitle">边框不随主题变化而变化，默认设置为常量</div>
      <a-descriptions :colon="false" :column="4" v-for="(key, k) in ['borderStyle', 'borderWidth']" :key="key">
        <template slot="title">
          {{ key == 'borderStyle' ? '边框样式' : '边框尺寸' }}
          <a-button size="small" type="link" icon="plus" @click="addDerives(config[key].derive, config[key].code)" />
        </template>
        <a-descriptions-item label="变量"></a-descriptions-item>
        <a-descriptions-item label="值/公式"></a-descriptions-item>
        <a-descriptions-item label="描述"></a-descriptions-item>
        <a-descriptions-item label="预览"></a-descriptions-item>

        <template v-for="(item, i) in config[key].derive">
          <a-descriptions-item :label="null">
            <a-input :value="codeSuffix(item.code, config[key].code)" @change="e => mergeCodeSuffix(e, item, config[key].code)">
              <template slot="addonBefore">
                {{ config[key].code + '-' }}
              </template>
            </a-input>
          </a-descriptions-item>
          <a-descriptions-item :label="null">
            <a-input v-model="item.value" />
          </a-descriptions-item>
          <a-descriptions-item :label="null">
            <a-input-group>
              <a-row :gutter="8">
                <a-col :span="20">
                  <a-input v-model="item.remark" />
                </a-col>
                <a-col :span="4">
                  <a-button
                    size="small"
                    type="link"
                    icon="delete"
                    v-if="(key === 'borderStyle' && i > 0) || (key == 'borderWidth' && i > 3)"
                    @click="deleteDerives(key === 'borderStyle' ? config.borderStyle : config.borderWidth, i)"
                  />
                </a-col>
              </a-row>
            </a-input-group>
          </a-descriptions-item>
          <a-descriptions-item :label="null">
            <div :style="setStyle(item, key == 'borderStyle' ? 'border-bottom-style' : 'border-bottom-width')"></div>
          </a-descriptions-item>
        </template>
      </a-descriptions>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import { debounce, kebabCase } from 'lodash';

export default {
  name: 'ThemeBorderSpecify',
  props: {
    config: Object
  },
  components: {},
  computed: {},

  data() {
    return {};
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    mergeCodeSuffix(e, item, prefix) {
      item.code = prefix + '-' + e.target.value;
    },
    codeSuffix(code, prefix) {
      return code.split(prefix + '-')[1];
    },

    setStyle(item, styleProp) {
      let style = {
        [item.code]: item.value || 0
      };

      if (styleProp !== 'border-bottom-width') {
        if (styleProp == 'border-bottom-style' && item.value == 'double') {
          style['border-width'] = '3px';
        } else {
          style['border-width'] = '1px';
        }
        style[styleProp] = 'var(' + item.code + ')';
      } else {
        style['border-style'] = 'solid';
        style['border-width'] = '0px 0px ' + 'var(' + item.code + ')' + ' 0px';
      }
      return style;
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
