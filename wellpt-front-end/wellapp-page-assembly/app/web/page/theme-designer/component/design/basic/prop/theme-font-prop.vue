<template>
  <div>
    <a-form-model layout="vertical" :colon="false" v-if="subKey === 'fontFamily' || subKey == 'codeFamily'">
      <a-form-model-item>
        <template slot="label">
          {{ item.code }}
        </template>
        <a-select v-model="item.value" style="width: 100%">
          <a-select-option v-for="(s, i) in item.scope" :value="s" :key="'scope_' + i">{{ s }}</a-select-option>
        </a-select>
      </a-form-model-item>
      <a-form-model-item label="描述">
        {{ item.remark || '无' }}
      </a-form-model-item>
    </a-form-model>
    <a-form-model layout="vertical" :colon="false" v-if="subKey == 'fontSize'">
      <a-form-model-item label="名称">
        <a-input v-model="item.title" />
      </a-form-model-item>
      <a-form-model-item label="基础字号">
        <a-input v-model="item.value" />
      </a-form-model-item>
      <a-form-model-item v-for="(font, i) in item.derive" :label="font.code" :key="'font_size_' + i">
        <a-input v-model="font.value" />
      </a-form-model-item>
    </a-form-model>
    <a-form-model layout="vertical" :colon="false" v-if="subKey == 'fontWeight'">
      <a-form-model-item v-for="(font, i) in item.derive" :label="font.code" :key="'font_weight_' + i">
        <a-input v-model="font.value" />
      </a-form-model-item>
    </a-form-model>
    <a-form-model layout="vertical" :colon="false" v-if="subKey == 'lineHeight'">
      <a-form-model-item :label="item.code">
        <a-input v-model="item.value" />
      </a-form-model-item>
    </a-form-model>
  </div>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'ThemeFontProp',
  props: {
    item: Object,
    subKey: String
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
  methods: {}
};
</script>
