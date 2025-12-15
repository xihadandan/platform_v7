<template>
  <div class="theme-specify-panel shadow-specify">
    <a-page-header title="阴影">
      <div slot="subTitle">平台提供了阴影变量用于不同的场景。</div>
      <template v-for="(key, k) in ['levelOne', 'levelTwo', 'levelThree']">
        <a-row type="flex" style="flex-flow: row nowrap">
          <a-col flex="auto">
            <a-descriptions :colon="false" :column="6">
              <template slot="title" v-if="k === 0">基础阴影</template>
              <a-descriptions-item>
                <template slot="label">
                  {{ label[key] }}
                </template>
              </a-descriptions-item>
              <a-descriptions-item label="X轴偏移"></a-descriptions-item>
              <a-descriptions-item label="Y轴偏移"></a-descriptions-item>
              <a-descriptions-item label="模糊度"></a-descriptions-item>
              <a-descriptions-item label="扩展值"></a-descriptions-item>
              <a-descriptions-item label="阴影颜色"></a-descriptions-item>

              <template v-for="(item, i) in config[key].derive">
                <a-descriptions-item :label="null">
                  {{ item.code }}
                </a-descriptions-item>
                <a-descriptions-item :label="null">
                  <a-input-group compact>
                    <a-input-number :value="getOffsetValue(item.value, 0)" @change="e => onChangeOffset(e, item, 0)" />
                    <a-button>px</a-button>
                  </a-input-group>
                </a-descriptions-item>
                <a-descriptions-item :label="null">
                  <a-input-group compact>
                    <a-input-number :value="getOffsetValue(item.value, 1)" @change="e => onChangeOffset(e, item, 1)" />
                    <a-button>px</a-button>
                  </a-input-group>
                </a-descriptions-item>
                <a-descriptions-item :label="null">
                  <a-input-group compact>
                    <a-input-number :value="getOffsetValue(item.value, 2)" @change="e => onChangeOffset(e, item, 2)" />
                    <a-button>px</a-button>
                  </a-input-group>
                </a-descriptions-item>
                <a-descriptions-item :label="null">
                  <a-input-group compact>
                    <a-input-number :value="getOffsetValue(item.value, 3)" @change="e => onChangeOffset(e, item, 3)" />
                    <a-button>px</a-button>
                  </a-input-group>
                </a-descriptions-item>
                <a-descriptions-item :label="null">
                  <ColorPicker :value="getOffsetValue(item.value, 4)" @input="e => onChangeOffset(e, item, 4)" />
                </a-descriptions-item>
              </template>
            </a-descriptions>
          </a-col>
          <a-col flex="300px" class="preview-col" :style="{ paddingTop: key != 'levelOne' ? '40px' : '83px' }">
            <div>
              <div :style="{ boxShadow: config[key].derive[0].value }">
                {{ config[key].derive[0].code }}
              </div>
              <div>
                <template v-for="(der, i) in config[key].derive">
                  <div :key="'preview-shadow' + key + i" v-if="i > 0" :style="{ boxShadow: der.value }">
                    {{ der.code }}
                  </div>
                </template>
              </div>
            </div>
          </a-col>
        </a-row>
      </template>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
import ColorPicker from '@pageAssembly/app/web/widget/commons/color-picker.vue';

export default {
  name: 'ThemeShadowSpecify',
  props: {
    config: Object
  },
  components: { ColorPicker },
  computed: {},
  data() {
    return { selectItem: {}, label: { levelOne: '一级阴影', levelTwo: '二级阴影', levelThree: '三级阴影' } };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    getOffsetValue(value, offset) {
      let parts = value.split(' ');
      if (offset < 4) {
        let int = parseInt(parts[offset]);
        return offset == 3 ? (int == 0 ? undefined : int) : int;
      }
      return parts[offset];
    },
    onChangeOffset(value, item, offset) {
      let parts = item.value.split(' ');
      parts[offset] = offset != 4 ? value + 'px' : value;
      item.value = parts.join(' ');
    },
    onSelectItem(item) {
      this.selectItem = item;
    }
  }
};
</script>
