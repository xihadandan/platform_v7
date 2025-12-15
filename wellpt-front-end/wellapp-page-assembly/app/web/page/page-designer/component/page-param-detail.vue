<template>
  <a-form-model :model="detail" :label-col="labelCol" :wrapper-col="wrapperCol" ref="form">
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="detail.name" />
    </a-form-model-item>
    <a-form-model-item label="参数">
      <a-input v-model="detail.code" />
    </a-form-model-item>
    <a-form-model-item label="是否必填">
      <a-switch v-model="detail.required" />
    </a-form-model-item>
    <a-form-model-item label="参数值">
      <draggable
        v-model="detail.valueScope"
        :group="{ name: 'pageParamVal', pull: true, put: false }"
        animation="300"
        handle=".drag-button"
      >
        <template v-for="(val, i) in detail.valueScope">
          <a-input v-model="val.value" :key="'pageParamVal_input_' + i">
            <template #addonAfter>
              <a-switch
                size="small"
                v-model="val.default"
                checked-children="默认"
                un-checked-children="默认"
                @change="e => onChangeParamDefault(e, i)"
              />
              <a-button size="small" type="link" icon="delete" @click="detail.valueScope.splice(i, 1)" />
              <a-button size="small" type="link" icon="menu" class="drag-button" />
            </template>
          </a-input>
        </template>
      </draggable>
      <a-button type="link" icon="plus" size="small" @click="addParamValueScope">添加</a-button>
    </a-form-model-item>
    <a-form-model-item label="描述">
      <a-textarea v-model="detail.remark" :auto-size="{ minRows: 5, maxRows: 5 }" />
    </a-form-model-item>
  </a-form-model>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'PageParamDetail',
  props: {
    detail: Object
  },
  components: { draggable: () => import(/* webpackChunkName: "vuedraggable" */ 'vuedraggable') },
  computed: {},
  data() {
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 16 }
    };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {},
  mounted() {},
  methods: {
    addParamValueScope() {
      this.detail.valueScope.push({
        value: undefined,
        default: false
      });
    },
    onChangeParamDefault(e, index) {
      if (this.detail.valueScope) {
        for (let i = 0, len = this.detail.valueScope.length; i < len; i++) {
          if (e && index != i) {
            this.detail.valueScope[i].default = false;
          }
        }
      }
    }
  }
};
</script>
