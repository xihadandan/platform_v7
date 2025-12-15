<template>
  <div class="theme-specify-panel">
    <a-page-header title="圆角">
      <div slot="subTitle">
        为了统一使用规范，定义了三种圆角规格，应用在具体的组件中，圆角变量遵循偶数原则。 Corner-1 一般圆角: 2px 应用场景如: 选择框，按钮等；
        Corner-2 反馈类圆角: 4px 应用场景如: 反馈提示类组件popover、toast等; Corner-3 卡片圆角: 6px 应用场景为大卡片。
      </div>
      <a-descriptions :colon="false" :column="4">
        <template slot="title">
          圆角尺寸
          <a-button size="small" type="link" icon="plus" @click="addDerives(config.derive, config.code)" />
        </template>
        <a-descriptions-item label="变量"></a-descriptions-item>
        <a-descriptions-item label="值/公式"></a-descriptions-item>
        <a-descriptions-item label="描述"></a-descriptions-item>
        <a-descriptions-item label="预览"></a-descriptions-item>

        <template v-for="(item, i) in config.derive">
          <a-descriptions-item :label="null" :key="i">
            <a-input :value="codeSuffix(item.code, config.code)" @change="e => mergeCodeSuffix(e, item, config.code)">
              <template slot="addonBefore">
                {{ config.code + '-' }}
              </template>
            </a-input>
          </a-descriptions-item>
          <a-descriptions-item :label="null" :key="i">
            <a-input v-model="item.value" />
          </a-descriptions-item>
          <a-descriptions-item :label="null" :key="i">
            <a-input-group>
              <a-row :gutter="8">
                <a-col :span="20">
                  <a-input v-model="item.remark" />
                </a-col>
                <a-col :span="4">
                  <a-button size="small" type="link" icon="delete" v-if="i > 0" @click="deleteDerives(config, i)" />
                </a-col>
              </a-row>
            </a-input-group>
          </a-descriptions-item>
          <a-descriptions-item :label="null" :key="i">
            <div style="width: 30px; height: 30px; overflow: hidden">
              <div
                :style="{
                  [item.code]: item.value,
                  height: '80px',
                  width: '80px',
                  border: '1px solid #000',
                  'border-radius': 'var(' + item.code + ')'
                }"
              ></div>
            </div>
          </a-descriptions-item>
        </template>
      </a-descriptions>
    </a-page-header>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'ThemeRadiusSpecify',
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
