<template>
  <a-breadcrumb>
    <a-breadcrumb-item v-for="(item, index) in items" :key="item.uuid">
      <a-icon v-if="index == 0" type="home" />
      <template v-if="index != items.length - 1">
        <a href="#" @click="onItemClick($event, item)">{{ item.name }}</a>
      </template>
      <template v-else>
        {{ item.name }}
      </template>
    </a-breadcrumb-item>
  </a-breadcrumb>
</template>

<script>
export default {
  props: {
    initItems: {
      type: Array,
      default() {
        return [];
      }
    }
  },
  data() {
    return {
      items: this.initItems
    };
  },
  methods: {
    setItems(items = []) {
      this.items = items;
    },
    addItem(item) {
      this.items.push(item);
    },
    onItemClick(event, data) {
      this.items.length = this.items.findIndex(item => item.uuid == data.uuid) + 1;
      this.items = [...this.items];
      this.$emit('click', data);
    }
  }
};
</script>

<style></style>
