<template>
  <view :class="'subform' + ' field-' + subformDefinition.id" v-show="vShow">
    <w-section
      v-if="subformDefinition.isShow"
      :title="subformDefinition.displayName || subformDefinition.name"
      type="line"
      iconType="paperplane"
      @iconTap="onSubformTap"
    >
      <uni-list v-if="isModalDetail">
        <uni-list-item
          v-for="(rowData, index) in dataList"
          :key="index"
          :index="index"
          :title="getRowTitle(rowData, index)"
          showArrow
          clickable
          @tap="onItemTap($event, rowData)"
        ></uni-list-item>
      </uni-list>
      <uni-collapse class="row-data-collapse" v-else :value="isExpandAll">
        <uni-collapse-item
          v-for="(recordInfo, index) in recordInfos"
          :key="index"
          :show-animation="true"
          :title="getRowTitle(recordInfo.rowData, index)"
        >
          <w-dyform-field
            class="subform-field"
            v-for="(field, index2) in subformFields"
            :key="index2"
            :fieldDefinition="field"
            :formScope="recordInfo.formScope"
          ></w-dyform-field>
        </uni-collapse-item>
      </uni-collapse>
    </w-section>
  </view>
</template>

<script>
import { isEmpty, each } from "lodash";
import Subform from "./uni-Subform.js";
export default {
  props: {
    subformDefinition: {
      type: Object,
      required: true,
    },
    formScope: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      dataList: [],
      recordInfos: [],
      subformFields: [],
    };
  },
  created() {
    var _self = this;
    var dataList = _self.formScope.getDyform().getFormData(this.subformDefinition.uuid);
    _self.dataList = dataList;
    // 添加从表字段
    var subformField = new Subform({
      formScope: _self.formScope,
      definition: _self.subformDefinition,
      value: dataList,
    });
    if (!_self.formScope.isSubform()) {
      _self.formScope.getDyform().addField(subformField);
    }
    _self.subform = subformField;

    // 从表配置的字段
    let subformFields = [];
    each(_self.subformDefinition.fields, function (field) {
      subformFields.push(field);
    });
    subformFields.sort(function (o1, o2) {
      if (o1.order == null || o2.order == null) {
        return 0;
      }
      return o1.order - o2.order;
    });
    _self.subformFields = subformFields;
    _self.subformDefinition.fieldList = subformFields;

    // 分组列表
    if (!_self.isModalDetail) {
      var recordInfos = [];
      each(dataList, function (rowData) {
        var formScope = _self.formScope.getDyform().createDyformScope(_self.subformDefinition, rowData);
        formScope.setSubform(_self.subform);
        recordInfos.push({
          formScope: formScope,
          rowData: rowData,
        });
      });
      _self.recordInfos = recordInfos;
    }
  },
  methods: {
    onItemTap: function (e, rowData) {
      var _self = this;
      var formScope = _self.formScope.getDyform().createDyformScope(_self.subformDefinition, rowData);
      formScope.setSubform(_self.subform);
      _self.setPageParameter("record", rowData);
      _self.setPageParameter("subformDefinition", _self.subformDefinition);
      _self.setPageParameter("formScope", formScope);
      _self.setPageParameter("subform", _self.subform);
      uni.navigateTo({
        url: "/uni_modules/w-app/pages/dyform/subform_edit",
      });
    },
    onSubformTap: function () {
      var _self = this;
      _self.setPageParameter("subformDefinition", _self.subformDefinition);
      _self.setPageParameter("dataList", _self.subform.getValue());
      uni.navigateTo({
        url: "/uni_modules/w-app/pages/dyform/subform_data_table_view",
      });
    },
    getRowTitle: function (rowData, index) {
      let rowTitle = this.subformDefinition.rowTitle;
      let rowTitleValue = rowData[rowTitle] || "";
      if (rowTitleValue) {
        return index + 1 + "、" + rowTitleValue;
      }
      return "第" + (index + 1) + "行";
    },
  },
  computed: {
    // 是否弹框显示
    isModalDetail: function () {
      return this.subformDefinition.subformDisplayStyle == "2" || this.subformDefinition.displayMode == "modal";
    },
    // 分组列表是否默认展开
    isExpandAll: function () {
      let expandIndexes = [];
      if (this.subformDefinition.expandList) {
        var dataList = this.formScope.getDyform().getFormData(this.subformDefinition.uuid) || [];
        each(dataList, function (rowData, index) {
          expandIndexes.push(index + "");
        });
      }
      return expandIndexes;
    },
  },
};
</script>

<style lang="scss" scoped>
.subform {
  .row-data-collapse {
    background-color: $uni-bg-secondary-color;

    ::v-deep .uni-collapse-item__title-box {
      color: $uni-text-color;
      background-color: $uni-bg-secondary-color;
    }

    ::v-deep .uni-collapse-item__wrap {
      overflow: auto;
    }

    ::v-deep .uni-collapse-item__wrap-content {
      background-color: $uni-bg-secondary-color;
    }
  }
}
</style>
