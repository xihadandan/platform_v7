<template>
  <a-form-model-item :label="formLabel">
    <WTreeSelect
      ref="curCompRef"
      :treeData="treeData"
      :value="value"
      placeholder="请选择城市"
      :editMode="editMode"
      :showCheckedStrategy="selectParent ? 'SHOW_ALL' : 'SHOW_CHILD'"
      :getPopupContainer="getPopupContainerByPs()"
      @change="onChangeTree"
      style="max-width: 1920px"
    />
  </a-form-model-item>
</template>
<style></style>
<script type="text/babel">
import formConfigureMixin from '../mixin/formConfigure.mixin';
import WTreeSelect from '../widget-form-select/components/WTreeSelect.vue';
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
import { map, each } from 'lodash';
export default {
  name: 'LocationSelect',
  mixins: [formConfigureMixin],
  inject: ['designer'],
  components: { WTreeSelect },
  props: {
    value: String,
    widget: {
      type: Object,
      default: function () {
        return {
          configuration: {}
        };
      }
    },
    formLabel: {
      type: String,
      default: '选择城市'
    },
    selectParent: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      treeData: [],
      selectOptions: [],
      optionsValueMap: {},
      editMode: {
        allowClear: true,
        showSearch: true
      }
    };
  },

  beforeCreate() {},
  computed: {},
  created() {
    this.fetchTreeDataByDataDic('CHINA_AD_CODE');
  },
  methods: {
    getPopupContainerByPs,
    onChangeTree(value, label, extra) {
      // if (!label && isArray(value)) {
      //   label = map(value, 'label');
      // }
      console.log(value);
      this.$emit('input', value);
    },
    // 获取树形下拉框 数据字典备选项
    fetchTreeDataByDataDic(dataDicUuid) {
      this.getLabelValueOptionByDataDic(dataDicUuid, (results, key) => {
        this.treeData = this.reSetTreeData(results);
      });
    },
    reSetTreeData(treeData) {
      this.selectOptions.splice(0, this.selectOptions.length);
      const tree2Map = data => {
        return map(data, child => {
          child.s_tree_key = child.id || child.uuid;
          child.s_tree_value = child.real || child.value || child.s_tree_key;
          child.s_tree_title = child.display || child.name || child.label;
          this.selectOptions.push({
            label: child.s_tree_title,
            value: child.s_tree_value
          });
          // 所有选项data值
          this.optionsValueMap[child.s_tree_value] = child.data;
          if (!child.s_tree_label) {
            child.s_tree_label = child.s_tree_title;
          }
          if (child.children && child.children.length) {
            each(child.children, c_child => {
              c_child.s_tree_title = c_child.display || c_child.name || c_child.label;
              c_child.s_tree_label = c_child.s_tree_title;
            });
            child.children = tree2Map(child.children);
          }
          return child;
        });
      };
      return tree2Map(treeData);
    }
  },
  mounted() {}
};
</script>
