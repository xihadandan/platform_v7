<template>
  <view :style="theme" class="popup-dialog">
    <view class="dialog-content">
      <w-list-view
        v-if="listViewWidgetDefinition != null"
        :widget="listViewWidgetDefinition"
        @beforeRenderRowData="beforeRenderRowData"
        @selectionChange="selectionChange"
      ></w-list-view>
    </view>
    <view class="org-dialog-button-group">
      <view class="org-dialog-button" @tap="onCancel">
        <text class="org-dialog-button-text">取消</text>
      </view>
      <view class="org-dialog-button uni-border-left" @tap="onOk">
        <text class="org-dialog-button-text org-button-color">确定</text>
      </view>
    </view>
  </view>
</template>

<script>
import { isEmpty, each } from "lodash";
export default {
  data() {
    return {
      selection: [],
      listViewWidgetDefinition: null,
    };
  },
  onLoad() {
    var _self = this;
    var options = _self.getPageParameter("options");
    _self.options = options;
    var fieldDefinition = options.fieldDefinition;
    // console.log(JSON.stringify(fieldDefinition));
    var listViewId = fieldDefinition.relationDataIdTwo;
    var dataStoreId = fieldDefinition.relationDataValueTwo;
    // 视图配置
    if (!isEmpty(listViewId)) {
      uni.request({
        service: "appContextService.getAppWidgetDefinitionById",
        data: [listViewId, null],
        success: function (result) {
          _self.listViewWidgetDefinition = _self.fillMobileListViewDefinition(
            JSON.parse(result.data.data.definitionJson)
          );
        },
      });
    } else if (!isEmpty(dataStoreId)) {
      // 数据仓库配置
      var widget = {
        configuration: {
          dataStoreId: dataStoreId,
        },
      };
      _self.listViewWidgetDefinition = _self.fillMobileListViewDefinition(widget);
    }
  },
  methods: {
    fillMobileListViewDefinition: function (widget) {
      // console.log(JSON.stringify(widget));
      var configuration = widget.configuration;
      // 可选择
      configuration.selectable = true;
      // 禁用多选
      configuration.multiSelect = false;
      // configuration.templateProperties = [{
      // 	title: "标题",
      // 	mapColumn: "_title",
      // 	name: "title"
      // }];
      return widget;
    },
    // 渲染数据前事件监听处理
    beforeRenderRowData: function (rowData, configuration) {
      // 获取前3列的显示值
      var title = "";
      var note = "";
      var rightText = "";
      if (configuration.columns) {
        each(configuration.columns, function (column, index) {
          if (column.hidden == "1" || index > 2) {
            return;
          }
          var columnValue = rowData[column.name + "RenderValue"] || rowData[column.name];
          if (index == 0) {
            title = columnValue;
          } else if (index == 1) {
            note = columnValue;
          } else if (index == 2) {
            rightText = columnValue;
          }
        });
      } else {
        var index = 0;
        for (var key in rowData) {
          var columnValue = rowData[key + "RenderValue"] || rowData[key];
          if (index == 0) {
            title = columnValue;
          } else if (index == 1) {
            note = columnValue;
          } else if (index == 2) {
            rightText = columnValue;
          }
          index++;
        }
      }
      rowData.title = title;
      rowData.note = note;
      rowData.rightText = rightText;
    },
    // 选择变更事件监听处理
    selectionChange: function (selection) {
      var _self = this;
      _self.selection = selection;
    },
    onCancel() {
      // 返回取消更改
      uni.navigateBack({
        delta: 1,
      });
    },
    onOk() {
      var _self = this;
      var options = _self.options;
      var selection = _self.selection;
      if (!selection || selection.length == 0) {
        uni.showToast({ title: "请选择数据！" });
        return;
      }
      if (options.ok) {
        options.ok.call(options, selection);
      }
      uni.navigateBack({
        delta: 1,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
.popup-dialog {
  width: 100%;
  background-color: $uni-bg-color;

  .org-dialog-button-group {
    position: fixed;
    width: 100%;
    bottom: var(--window-bottom, 0);
    background-color: $uni-bg-color;

    /* #ifndef APP-NVUE */
    display: flex;
    /* #endif */
    flex-direction: row;
    border-top-color: #f5f5f5;
    border-top-style: solid;
    border-top-width: 1px;

    .org-dialog-button {
      /* #ifndef APP-NVUE */
      display: flex;
      /* #endif */

      box-shadow: $uni-shadow-base;

      flex: 1;
      flex-direction: row;
      justify-content: center;
      align-items: center;
      height: 40px;
    }

    .org-border-left {
      border-left-color: #f0f0f0;
      border-left-style: solid;
      border-left-width: 1px;
    }

    .org-dialog-button-text {
      font-size: 16px;
      color: $uni-text-color;
    }

    .org-button-color {
      color: var(--w-primary-color);
    }
  }
}
</style>
