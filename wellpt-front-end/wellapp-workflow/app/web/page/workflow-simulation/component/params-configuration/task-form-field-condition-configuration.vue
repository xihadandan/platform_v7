<template>
  <div>
    满足
    <a-select
      size="small"
      :options="[
        { label: '全部', value: 'all' },
        { label: '任一', value: 'any' }
      ]"
      style="width: 65px"
      v-model="configuration.match"
    />
    条件时设值
    <a-button
      size="small"
      icon="plus"
      type="link"
      @click="
        () => {
          configuration.conditions.push({
            code: undefined,
            value: undefined,
            operator: '=='
          });
        }
      "
    >
      添加
    </a-button>
    <a-list class="condition-list" item-layout="horizontal" :data-source="configuration.conditions">
      <a-list-item slot="renderItem" slot-scope="item">
        <a-button type="link" slot="actions" @click="deleteCondition(item)">
          <Icon type="pticon iconfont icon-ptkj-shanchu"></Icon>
        </a-button>
        <a-select
          v-model="item.code"
          show-search
          :filterOption="filterSelectOption"
          @change="conditionCodeChange(item)"
          :style="{ width: '100%' }"
        >
          <a-select-opt-group>
            <span slot="label">
              <a-icon type="appstore" />
              字段
            </span>
            <a-select-option v-for="d in formFieldOptions" :key="d.name">
              {{ d.displayName }}
            </a-select-option>
          </a-select-opt-group>
          <a-select-opt-group>
            <span slot="label">
              <a-icon type="appstore" />
              其他
            </span>
            <a-select-option value="other.currentRunNum">当前执行次数</a-select-option>
          </a-select-opt-group>
        </a-select>
        <a-input-group compact>
          <a-select
            v-model="item.operator"
            :options="getOperatorOptions(item)"
            :style="{ width: !['true', 'false'].includes(item.operator) ? '50%' : '100%' }"
          />
          <a-input v-if="!['true', 'false'].includes(item.operator)" v-model="item.value" :style="{ width: '50%' }" />
        </a-input-group>
      </a-list-item>
    </a-list>
  </div>
</template>

<script>
import { filterSelectOption } from '@framework/vue/utils/function';
import { operatorOptions } from '@pageAssembly/app/web/widget/commons/constant.js';
export default {
  props: {
    configuration: Object,
    formFieldOptions: Array
  },
  data() {
    return { operatorOptions };
  },
  methods: {
    filterSelectOption,
    getOperatorOptions(condition) {
      if (condition.code == 'other.currentRunNum') {
        return this.operatorOptions.filter(item => !['true', 'false'].includes(item.value));
      }
      return this.operatorOptions;
    },
    conditionCodeChange(condition) {
      let operatorOptions = this.getOperatorOptions(condition);
      let operator = operatorOptions.find(item => item.value == condition.operator);
      if (!operator) {
        condition.operator = null;
      }
    },
    deleteCondition(condition) {
      let deleteIndex = this.configuration.conditions.indexOf(condition);
      if (deleteIndex != -1) {
        this.configuration.conditions.splice(deleteIndex, 1);
      }
    }
  }
};
</script>

<style lang="less" scoped>
.condition-list {
  ::v-deep .ant-list-item-action {
    margin-left: 0px;
  }
}
</style>
