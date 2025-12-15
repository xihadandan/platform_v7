<template>
  <div class="theme-specify-panel height-specify">
    <a-page-header title="高度">
      <div slot="subTitle">为统一设计到开发的布局语言, 减少还原损耗, 高度采用布局一致的常用模度8倍数原则, 具备动态的韵律感。</div>
      <a-row type="flex" style="flex-flow: row nowrap">
        <a-col flex="auto">
          <a-descriptions :colon="false" :column="3">
            <template slot="title">
              常用高度
              <a-button size="small" type="link" icon="plus" @click="addDerives(config.derive, config.code)" />
            </template>
            <a-descriptions-item label="变量"></a-descriptions-item>
            <a-descriptions-item label="值/公式"></a-descriptions-item>
            <a-descriptions-item label="描述"></a-descriptions-item>

            <template v-for="(item, i) in config.derive">
              <a-descriptions-item :label="null" :key="i">
                <a-input
                  :value="codeSuffix(item.code, config.code)"
                  @change="e => mergeCodeSuffix(e, item, config.code)"
                  @click="onSelectItem(item)"
                >
                  <template slot="addonBefore">
                    {{ config.code + '-' }}
                  </template>
                </a-input>
              </a-descriptions-item>
              <a-descriptions-item :label="null" :key="i">
                <a-input v-model="item.value" @click="onSelectItem(item)" />
              </a-descriptions-item>
              <a-descriptions-item :label="null" :key="i">
                <a-input-group>
                  <a-row :gutter="8">
                    <a-col :span="20">
                      <a-input v-model="item.remark" @click="onSelectItem(item)" />
                    </a-col>
                    <a-col :span="4">
                      <a-button size="small" type="link" icon="delete" @click="deleteDerives(config, i)" />
                      <a-button size="small" type="link" icon="file-search" @click="onSelectItem(item)" />
                    </a-col>
                  </a-row>
                </a-input-group>
              </a-descriptions-item>
            </template>
          </a-descriptions>
        </a-col>
        <a-col flex="200px" class="preview-height-col">
          <div>
            <label>
              {{ selectItem.code }}
              <code>{{ selectItem.value }}</code>
            </label>
            <div
              v-show="selectItem.value"
              :style="{
                [selectItem.code]: selectItem.value,
                height: 'var(' + selectItem.code + ')'
              }"
            >
              <div>
                <div :style="{ 'line-height': 'var(' + selectItem.code + ')' }">
                  <span>
                    {{ selectItem.value }}
                  </span>
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
  name: 'ThemeHeightSpecify',
  props: {
    config: Object
  },
  components: {},
  computed: {},
  data() {
    return { selectItem: {} };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {
    this.selectItem = this.config.derive[0];
  },
  methods: {
    onSelectItem(item) {
      this.selectItem = item;
    },
    mergeCodeSuffix(e, item, prefix) {
      item.code = prefix + '-' + e.target.value;
    },
    codeSuffix(code, prefix) {
      return code.split(prefix + '-')[1];
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
        code: prefix + '-' + derive.length,
        value: undefined,
        remark: undefined
      });
    }
  }
};
</script>
