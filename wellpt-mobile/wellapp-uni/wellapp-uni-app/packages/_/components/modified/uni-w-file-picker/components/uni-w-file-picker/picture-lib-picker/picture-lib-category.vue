<template>
  <view class="picture-lib-category" :style="{ height: listHeight }">
    <view class="picture-lib-category__category">
      <view>分类：</view>
      <uni-w-data-select
        v-model="curCategory"
        valueParam="uuid"
        textParam="name"
        :localdata="selectedCategories"
        showSearch
        alignLeft
        selectorItemTextSlot
        @change="handleCategory"
      >
        <template v-slot:selectorItemText="{ item }">
          <view class="flex">
            <view class="msgIconShow" :style="{ backgroundColor: item.color }">
              <w-icon :icon="item.icon || 'pticon iconfont icon-ptkj-qiehuanshitu'" :size="14" />
            </view>
            <text class="category-name">{{ $t("PictureLibCategory." + item.uuid, item.name) }}</text>
            <text>
              ({{ selectedPictureMap[item.uuid] ? selectedPictureMap[item.uuid]["length"] : "0" }}/{{
                item.fileIDs.length
              }})
            </text>
          </view>
        </template>
      </uni-w-data-select>
    </view>
    <view class="picture-lib-category__content">
      <view class="picture-lib-category__grid">
        <view
          :class="['picture-lib-category__grid-item', selectedFileIds.includes(item.fileID) ? 'selected' : '']"
          :style="{ width: itemWidth + 'px' }"
          v-for="(item, index) in selectedCategoryImgs"
          :index="index"
          :key="index"
          @click="handleSelectedPicture(item)"
        >
          <view class="grid-item-box" style="background-color: #fff">
            <image
              class="image"
              mode="aspectFit"
              :src="item.url"
              :style="{ width: '100%', height: itemWidth * 0.62 + 'px', borderRadius: 'inherit' }"
            />
            <view class="text">
              <view class="title w-ellipsis-1">{{ item.fileName }}</view>
              <view class="remark">
                {{ item.fileName.split(".")[item.fileName.split(".").length - 1].toUpperCase() }}/{{
                  formatSize(item.fileSize)
                }}
              </view>
              <view class="remark">{{ item.width }}x{{ item.height }}px</view>
            </view>
          </view>
        </view>
      </view>
      <uni-w-empty v-if="selectedCategoryImgs.length == 0"></uni-w-empty>
    </view>
  </view>
</template>
<script>
import { findIndex, head, map, set, template } from "lodash";
import { storage } from "wellapp-uni-framework";
import { get_file_ext } from "../utils";
export default {
  name: "PictureLibPicker",
  props: {
    pictureLibUuids: {
      type: Array,
      default: () => [],
    },
  },

  data() {
    return {
      isShow: false,
      current: 0,
      tabs: [
        { name: this.$t("WidgetFormFileUpload.pictureLibrary", "图片库") },
        { name: this.$t("WidgetFormFileUpload.localUpload", "本地上传") },
      ],
      curCategory: "", //分类uuid
      selectedCategories: [], //分类列表
      selectedCategoryImgs: [], //分类图片列表
      selectedPictureMap: {},
      selectedFiles: [],
      itemWidth: 150,
    };
  },
  computed: {
    selectedFileIds() {
      let ids = [];
      for (const key in this.selectedPictureMap) {
        const item = this.selectedPictureMap[key];
        if (key == this.curCategory) {
          for (let index = 0; index < item.length; index++) {
            ids.push(item[index]["fileID"]);
          }
        }
      }
      return ids;
    },
    listHeight() {
      let height = this.selectedFiles.length ? "60px" : "0px";
      return `calc(100% - ${height})`;
    },
  },
  created() {
    this.queryAllCategory();
    this.adjustColumns();
    // 监听屏幕旋转
    uni.onWindowResize(() => {
      this.adjustColumns();
    });
  },
  mounted() {},
  methods: {
    $t() {
      return this.$i18n.$t(this, ...arguments);
    },
    formatSize(size, pointLength, units) {
      var unit;
      units = units || ["B", "KB", "MB", "GB", "TB"];
      while ((unit = units.shift()) && size > 1024) {
        size = size / 1024;
      }
      return (unit === "B" ? size : size.toFixed(pointLength || 2)) + unit;
    },
    queryAllCategory() {
      this.$axios.get("/api/basicdata/img/category/queryAllCategory").then((res) => {
        const result = res.data;
        if (result && result.code == "0") {
          result.data.sort(function (a, b) {
            return a.code.localeCompare(b.code);
          });
          let selectedCategories = [];
          let hasPictureIndex = undefined;
          for (let index = 0; index < result.data.length; index++) {
            const item = result.data[index];
            if (item.i18ns && item.i18ns.length) {
              let i18n = this.$i18n.i18nsToI18n(this, item.i18ns, "defId");
              this.$i18n.mergeWidgetI18nMessages(this, i18n, "PictureLibCategory");
            }
            if (this.pictureLibUuids.includes(item.uuid)) {
              selectedCategories.push(item);
              if (hasPictureIndex === undefined && item.fileIDs.length) {
                hasPictureIndex = selectedCategories.length - 1;
              }
            }
          }
          this.selectedCategories = selectedCategories;
          if (selectedCategories.length > 0) {
            this.handleCategory(selectedCategories[hasPictureIndex || 0].uuid);
          }
        }
      });
    },

    // 选择图片
    handleSelectedPicture(item) {
      const index = findIndex(this.selectedPictureMap[this.curCategory], (pic) => pic.fileID == item.fileID);
      if (index == -1) {
        this.selectedPictureMap[this.curCategory].push(item);
      } else {
        this.selectedPictureMap[this.curCategory].splice(index, 1);
      }
      this.setSelectedFiles();
    },
    // 点击分类
    handleCategory(value) {
      if (value) {
        this.curCategory = value;
        this.queryImgsByCategoryUuid(value);
        if (this.selectedPictureMap[value] == undefined) {
          this.$set(this.selectedPictureMap, value, []);
        }
      }
    },
    setSelectedFiles() {
      let files = [];
      for (let index = 0; index < this.selectedCategories.length; index++) {
        const category = this.selectedCategories[index];
        const selectedFiles = this.selectedPictureMap[category.uuid];
        if (selectedFiles) {
          files = files.concat(selectedFiles);
        }
      }
      this.$emit("change", files);
      this.selectedFiles = files;
    },
    // 确定选择图片
    confirmSelectPicture(callback) {
      this.$emit("confirm", this.selectedFiles);
      if (typeof callback == "function") {
        callback(this.selectedFiles);
      }
    },
    // 清空选中图片
    clearSelectPicture() {
      this.selectedPictureMap = {};
      this.setSelectedFiles();
    },
    // 查询分类下的图片
    queryImgsByCategoryUuid(uuid) {
      this.$axios.get(`/api/basicdata/img/category/queryImgs/${uuid}`).then(({ data }) => {
        if (data && data.code == "0") {
          this.selectedCategoryImgs = map(data.data, (item) => {
            let url = `/repository/file/mongo/download?fileID=${item.fileID}`;
            item.url = storage.fillAccessResourceUrl(url);
            item.path = item.url;
            item.extname = get_file_ext(item.fileName).ext;
            item.status = "success";
            item.uid = item.uid ? item.uid : item.uuid;
            item.name = item.name ? item.name : item.fileName;
            item.size = item.size ? item.size : item.fileSize;
            return item;
          });
          let hasIndex = findIndex(this.selectedCategories, { uuid: uuid });
          if (hasIndex > -1) {
            this.$set(this.selectedCategories[hasIndex], "fileIDs", map(data.data, "fileID"));
          }
        }
      });
    },
    change(e) {},
    adjustColumns() {
      // 获取屏幕宽度
      const systemInfo = uni.getSystemInfoSync();
      const screenWidth = systemInfo.screenWidth;

      // 根据屏幕宽度调整列数
      if (screenWidth >= 768) {
        this.itemWidth = (screenWidth - 24 - 8 * 5) / 5; // 大屏幕显示5列
      } else if (screenWidth >= 414) {
        this.itemWidth = (screenWidth - 24 - 8 * 4) / 4; // 中等屏幕显示4列
      } else if (screenWidth >= 375) {
        this.itemWidth = (screenWidth - 24 - 8 * 3) / 3; // 小屏幕显示3列
      } else {
        this.itemWidth = (screenWidth - 24 - 8 * 2) / 2; // 超小屏幕显示2列
      }
    },
  },
};
</script>
<style lang="scss" scoped>
.picture-lib-category {
  .picture-lib-category__category {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
    padding: 0 var(--w-padding-xs);
  }

  /* 图片库分类图标 */
  .msgIconShow {
    margin-right: var(--w-margin-2xs);
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    color: #ffffff;
    background-color: var(--w-primary-color);
  }
  .picture-lib-category__grid {
    display: flex;
    flex-wrap: wrap;
    margin: 0 8px;
    .picture-lib-category__grid-item {
      padding: var(--w-padding-3xs);
      .grid-item-box {
        border: 1px solid var(--w-border-color-mobile);
        border-radius: 4px;
        width: 100%;
        .text {
          padding: var(--w-padding-3xs);
        }
        .remark {
          font-size: var(--w-font-size-sm);
          color: var(--w-text-color-light);
        }
      }
      &.selected {
        .grid-item-box {
          border: 1px solid var(--w-primary-color);
        }
      }
    }
  }
  .picture-lib-category__content {
    margin-top: var(--w-margin-xs);
    height: calc(100% - 60px);
    overflow-y: auto;
  }
}
</style>
