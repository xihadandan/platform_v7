<template>
  <div>
    <template v-for="item in options">
      <a-form-model-item :label="item.label" :key="item.key" :label-col="item.labelCol" :wrapper-col="item.wrapperCol">
        <template v-if="item.eventType">
          <template v-if="item.disabled">
            <a-tooltip placement="topRight" arrowPointAtCenter>
              <template slot="title">异步加载时不支持全选</template>
              <span>
                <a-switch v-model="widget.configuration.editMode[item.key]" :disabled="item.disabled" />
              </span>
            </a-tooltip>
          </template>
          <template v-else>
            <a-switch
              :disabled="item.disabled"
              v-model="widget.configuration.editMode[item.key]"
              v-on:[item.eventType]="
                val => {
                  handelRelay(val, item.eventName);
                }
              "
            />
          </template>
        </template>

        <template v-else>
          <a-switch v-model="widget.configuration.editMode[item.key]" :disabled="item.disabled" />
        </template>
      </a-form-model-item>
    </template>
  </div>
</template>

<script>
export default {
  name: 'FieldSwitch',
  props: {
    options: Array,
    widget: Object
  },
  methods: {
    handelRelay(val, eventName) {
      this.$emit('handelRelay', { eventName, val });
    }
  }
};
</script>
