<template>
  <!-- 备选项来源-数据字典 -->
  <div>
    <a-form-model-item label="联动设置" :wrapper-col="{ style: { textAlign: 'right' } }" v-if="showRelate">
      <a-switch v-model="widget.configuration.optionDataAutoSet" />
    </a-form-model-item>
    <template v-if="widget.configuration.optionDataAutoSet">
      <div class="wcfg-select-item">
        <div>联动字段</div>
        <!-- 被联动字段的值和数据字典的code关联 -->
        <SelectRelateField :widget="widget" />
        <div class="wcfg-select-dict-tips">
          <span>说明：系统根据联动字段值进行关联，查找ID为该值的数据字典，将该数据字典的字典列表作为本字段的备选项</span>
          <span>示例：联动字段中选择或输入【性别】后，本组件自动关联ID为【性别】的数据字典，并更新备选项为该字典的字典列表</span>
          <span>提示：不支持联动字段值多选，若需要，请通过备选项来源为“数据仓库”进行配置</span>
          <span>注：请确认选择的数据字典已按以下内容正确配置，否则将导致表单中该字段的备选项为空：</span>
          <span>1、数据字典模块中存在该字典</span>
          <span>2、该字典下的字典列表不能为空</span>
        </div>
      </div>
    </template>
    <template v-else>
      <div class="wcfg-select-item">
        <div>数据字典选择</div>
        <a-tree-select
          class="wcfg-select-dict"
          showSearch
          allowClear
          v-model="options.dataDictionaryUuid"
          style="width: 100%"
          treeNodeFilterProp="title"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="dataDictionaryTreeData"
          :replaceFields="{ title: 'name', key: 'id', value: 'id' }"
          :getPopupContainer="getPopupContainerByPs()"
          :dropdownClassName="getDropdownClassName()"
          :dropdownMatchSelectWidth="true"
        ></a-tree-select>
        <div class="wcfg-select-dict-tips">
          <span>注：请确认选择的数据字典已按以下内容正确配置，否则将导致表单中该字段的备选项为空：</span>
          <span>1、数据字典模块中存在该字典</span>
          <span>2、该字典下的字典列表不能为空</span>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { getPopupContainerByPs, getDropdownClassName } from '@dyform/app/web/page/dyform-designer/utils';
export default {
  name: 'OptionSourceDict',
  props: {
    designer: Object,
    widget: Object,
    options: Object,
    showRelate: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      dataDictionaryTreeData: [] // 数据字典
    };
  },
  created() {
    this.fetchDataDictionaryTreeData();
  },
  methods: {
    getPopupContainerByPs,
    getDropdownClassName,
    // 获取数据字典
    fetchDataDictionaryTreeData() {
      $axios
        .post('/json/data/services', {
          serviceName: 'cdDataDictionaryFacadeService',
          methodName: 'getAllDataDictionaryAsCategoryTree'
        })
        .then(({ data }) => {
          if (data.code == 0 && data.data) {
            this.dataDictionaryTreeData = data.data.children;
            this.dataDictionaryTreeData.forEach(node => {
              node.selectable = !node.nocheck;
            });
          }
        });
    }
  }
};
</script>
