<template>
  <view class="flex f_y_c">
    <w-icon
      style="margin-right: 5px"
      v-if="name && !mapColumn && rowData.iconPropertyObject[name]"
      :iconConfig="rowData.iconPropertyObject[name]"
      :size="size"
    ></w-icon>
    <CellRender
      v-if="rowData.clientRendererOptions[mapColumn || name]"
      :name="rowData.clientRendererOptions[mapColumn || name].rendererType"
      :slotOption="rowData.clientRendererOptions[mapColumn || name]"
      :row="rowData"
      :rowIndex="rowIndex"
      :text="rowData[mapColumn || 'property_' + name]"
      :class="className"
    ></CellRender>
    <rich-text :class="className" v-else-if="nodeContent" :nodes="nodeContent"></rich-text>
  </view>
</template>

<script>
import CellRender from "./cell-render";
import { utils } from "wellapp-uni-framework";

export default {
  props: {
    rowData: {
      type: Object,
      default: () => {},
    },
    rowIndex: {
      type: Number,
      default: 0,
    },
    name: String,
    mapColumn: String,
    iconSize: {
      type: Number,
      default: 20,
    },
    className: String,
  },
  name: "BodyItemRender",
  components: { CellRender },
  data() {
    return {};
  },
  computed: {
    size() {
      if (this.rowData.iconPropertyObject[this.name] && utils.isValidJSON(this.rowData.iconPropertyObject[this.name])) {
        let iconJson = JSON.parse(this.rowData.iconPropertyObject[this.name]);
        if (iconJson && !iconJson.showBackground) {
          return this.iconSize - 8;
        }
      }
      return this.iconSize;
    },
    nodeContent() {
      if (this.mapColumn) {
        return this.rowData[this.mapColumn + "RenderValue"]
          ? this.rowData[this.mapColumn + "RenderValue"]
          : this.rowData[this.mapColumn];
      } else if (this.name) {
        return this.rowData["property_" + this.name];
      }
      return "";
    },
  },
  methods: {},
};
</script>

<style lang="scss" scoped></style>
