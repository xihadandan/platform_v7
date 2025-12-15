<template>
  <span>
    <a-tag
      v-for="(tag, i) in tags"
      :key="'formTag_' + i"
      :closable="true"
      @close="removeTag(tag, i)"
      :title="tag.name"
      :class="['tag-no-border', 'tag-size-' + size]"
    >
      <span
        :style="{
          display: 'inline-block',
          'max-width': '100px',
          'white-space': 'nowrap',
          overflow: 'hidden',
          lineHeight: 1,
          'text-overflow': 'ellipsis'
        }"
      >
        {{ tag.name }}
      </span>
    </a-tag>
    <a-input
      :size="size"
      style="width: 150px"
      v-model.trim="newTagName"
      v-show="tagInputVisible"
      @blur="createTag"
      @keyup.enter="createTag"
      ref="input"
      :maxLength="32"
    />
    <span :class="['icon-btn', 'icon-btn-' + size]" title="添加标签" @click="addTagInput" v-show="!tagInputVisible">
      <Icon type="pticon iconfont icon-ptkj-jiahao" />
    </span>
  </span>
</template>
<style lang="less" scoped>
.icon-btn {
  position: relative;
  padding-left: var(--w-padding-2xs);
  cursor: pointer;

  &:hover {
    color: var(--w-primary-color);

    &::after {
      border-color: var(--w-primary-color);
    }
  }

  &::after {
    content: '';
    width: 32px;
    height: 32px;
    border: 1px solid var(--w-border-color-base);
    border-radius: var(--w-border-radius-2);
    display: block;
    position: absolute;
    top: -6px;
    left: 0;
  }

  &-small {
    padding-left: var(--w-padding-3xs);

    &::after {
      width: 24px;
      height: 24px;
      top: -3px;
    }
  }
}
</style>
<script type="text/babel">
export default {
  name: 'DataTag',
  props: {
    value: Array,
    applyTo: {
      type: String,
      required: true
    },
    dataId: String,
    labelInValue: {
      type: Boolean,
      default: true
    },
    size: {
      type: String,
      default: 'small'
    }
  },
  components: {},
  computed: {},
  data() {
    return { tags: [], tagInputVisible: false, newTagName: undefined };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.fetchDataTags();
  },
  mounted() {},
  methods: {
    fetchDataTags() {
      if (this.dataId != undefined) {
        $axios
          .get(`/proxy/api/app/tag/queryDataTag`, { params: { dataId: this.dataId } })
          .then(({ data }) => {
            if (data.code == 0 && data.data) {
              this.tags = data.data;
            }
            this.emitChange();
          })
          .catch(error => {});
      }
    },

    createTag() {
      if (this.newTagName == undefined) {
        this.tagInputVisible = false;
        return;
      }
      for (let t of this.tags) {
        if (t.name == this.newTagName) {
          this.$message.warn('标签重复');
          return false;
        }
      }
      if (this.applyTo != undefined) {
        $axios
          .get(`/proxy/api/app/tag/create`, { params: { name: this.newTagName, applyTo: this.applyTo } })
          .then(({ data }) => {
            if (data.code == 0) {
              this.tagInputVisible = false;

              this.tags.push({
                uuid: data.data,
                name: this.newTagName
              });
              this.newTagName = undefined;
              this.emitChange();
            }
          })
          .catch(error => {});
      }
    },
    emitChange() {
      if (this.labelInValue) {
        this.$emit('input', JSON.parse(JSON.stringify(this.tags)));
        return;
      }
      let uuids = [];
      for (let t of this.tags) {
        uuids.push(t.uuid);
      }
      this.$emit('input', uuids);
    },

    removeTag(tag, i) {
      let _this = this;
      this.$confirm({
        title: '提示',
        content: '确定要删除吗?',
        okText: '确定',
        cancelText: '取消',
        onOk() {
          $axios
            .get(`/proxy/api/app/tag/deleteDataTag`, { params: { dataId: _this.dataId, tagUuid: tag.uuid } })
            .then(({ data }) => {
              if (data.code == 0) {
                _this.tags.splice(i, 1);
                _this.emitChange();
              }
            })
            .catch(error => {});
        },
        onCancel() {}
      });
    },

    addTagInput() {
      this.tagInputVisible = true;
      this.newTagName = undefined;
      let _this = this;
      setTimeout(() => {
        _this.$refs.input.focus();
      }, 300);
    }
  }
};
</script>
