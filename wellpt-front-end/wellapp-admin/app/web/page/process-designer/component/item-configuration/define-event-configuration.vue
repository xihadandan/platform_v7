<template>
  <a-form-model
    class="basic-info"
    :model="defineEvent"
    labelAlign="left"
    ref="basicForm"
    :rules="rules"
    :label-col="{ span: 7 }"
    :wrapper-col="{ span: 17, style: { textAlign: 'left' } }"
    :colon="false"
  >
    <a-form-model-item label="名称" prop="name">
      <a-input v-model="defineEvent.name" />
    </a-form-model-item>
    <a-form-model-item label="是否里程碑事件">
      <a-radio-group v-model="defineEvent.milestone">
        <a-radio :value="true">是</a-radio>
        <a-radio :value="false">否</a-radio>
      </a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="备注" prop="remark">
      <a-input v-model="defineEvent.remark" type="textarea" />
    </a-form-model-item>
  </a-form-model>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
export default {
  props: {
    defineEvent: {
      type: Object,
      default() {
        return { name: '', id: `event_${generateId('SF')}`, builtIn: false, milestone: false, remark: '' };
      }
    }
  },
  data() {
    return {
      rules: {
        name: [{ required: true, message: '名称不能为空！', trigger: 'blur' }]
      }
    };
  },
  methods: {
    collect() {
      return this.$refs.basicForm
        .validate()
        .then(valid => {
          if (valid) {
            return this.defineEvent;
          }
          return null;
        })
        .catch(valid => {
          console.log('valid ', valid);
          return false;
        });
    }
  }
};
</script>

<style></style>
