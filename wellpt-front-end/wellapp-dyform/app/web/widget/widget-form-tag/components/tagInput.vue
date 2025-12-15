<template>
  <!-- 输入标签 -->
  <div class="tag-container">
    <template v-for="(tag, index) in tags">
      <a-tag :key="index" :closable="true" :class="tagClass" @close="() => handleClose(tag)">
        {{ tag }}
      </a-tag>
    </template>
    <a-input
      v-if="inputVisible"
      ref="input"
      type="text"
      size="small"
      :style="{ width: '78px', borderRadius: 'var(--w-tag-border-radius)' }"
      :value="inputValue"
      @change="handleInputChange"
      @blur="handleInputConfirm"
      @keyup.enter="handleInputConfirm"
    />
    <a-tag v-else class="add-tags" @click="showInput">
      <a-icon type="plus" />
    </a-tag>
  </div>
</template>

<script>
export default {
  name: 'TagInput',
  props: {
    tagClass: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      tags: [],
      inputVisible: false,
      inputValue: ''
    };
  },
  methods: {
    setValue(values) {
      this.tags = values;
    },
    //删除
    handleClose(removedTag) {
      const tags = this.tags.filter(tag => tag !== removedTag);
      this.tags = tags;
      this.$emit('updateValue', this.tags);
    },
    // 显示输入框
    showInput() {
      this.inputVisible = true;
      this.$nextTick(function () {
        this.$refs.input.focus();
      });
    },
    // 监听输入
    handleInputChange(e) {
      this.inputValue = e.target.value;
    },
    // 输入完成
    handleInputConfirm() {
      const inputValue = this.inputValue;
      let tags = this.tags;
      if (inputValue && tags.indexOf(inputValue) === -1) {
        tags = [...tags, inputValue];
      }
      Object.assign(this, {
        tags,
        inputVisible: false,
        inputValue: ''
      });
      this.$emit('updateValue', this.tags);
    }
  }
};
</script>
