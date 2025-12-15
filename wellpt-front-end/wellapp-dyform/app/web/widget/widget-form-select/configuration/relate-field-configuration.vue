<template>
  <!-- 联动选项设置 -->
  <div>
    <a-form-model-item label="联动选项设置" :label-col="{ span: 9 }" :wrapper-col="{ span: 14, style: { textAlign: 'right' } }">
      <WidgetDesignDrawer :id="`addRelateCondition${widget.id}`" title="联动条件配置" :designer="designer">
        <a-button type="primary" size="small" @click="addRelate">添加</a-button>
        <template slot="content">
          <RelateConditions :widget="widget" :designer="designer" :options="options" :conditionCfg="conditionCfg" />
        </template>
      </WidgetDesignDrawer>
      <a-button type="primary" size="small" @click="delRelate" style="margin-left: 10px">删除</a-button>
    </a-form-model-item>
    <RelateLists
      ref="relateLists"
      :widget="widget"
      :designer="designer"
      :options="options"
      :relateList="widget.configuration.relateList"
      @selectedRowKeys="handleSelectedRowKeys"
    />
  </div>
</template>

<script>
import { generateId } from '@framework/vue/utils/util';
export default {
  name: 'RelateFieldConfiguration',
  props: {
    widget: Object,
    designer: Object,
    options: Object
  },
  data() {
    return {
      conditionCfg: {},
      selectedRowKeys: []
    };
  },
  created() {
    if(!this.widget.configuration.relateList){
      this.widget.configuration.relateList =[];
    }
  },
  methods: {
    // 添加联动条件
    addRelate() {
      let relateList = this.widget.configuration.relateList;
      const relate = {};
      relate.id = generateId();
      relate.source = 'field';
      this.conditionCfg = relate;
      relateList.push(relate);
    },
    // 删除联动条件
    delRelate() {
      if (this.selectedRowKeys.length === 0) {
        return;
      }
      let relateList = this.widget.configuration.relateList;
      for (let index = 0; index < this.selectedRowKeys.length; index++) {
        for (let i = 0; i < relateList.length; i++) {
          if (relateList[i].id == this.selectedRowKeys[index]) {
            relateList.splice(i, 1);
            break;
          }
        }
      }
      this.$refs.relateLists.resetRowKeys();
    },
    // 选中的联动条件
    handleSelectedRowKeys(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys;
    }
  }
};
</script>
