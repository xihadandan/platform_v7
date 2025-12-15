<template>
  <div class="pt-form">
    <dateFormatComponent :options="options" valueParam="format" @change="onChange"></dateFormatComponent>
  </div>
</template>
<style></style>
<script type="text/babel">
import dateFormatComponent from './date-format-component.vue';
export default {
  name: 'dateRendererConfig',
  mixins: [],
  props: {
    options: Object
  },
  data() {
    return {};
  },

  beforeCreate() {},
  components: { dateFormatComponent },
  computed: {},
  created() {},
  methods: {
    onChange() {
      if (this.options.format) {
        this.options.format = this.options.format.replace(/D/g, 'd');
      }
    }
  },
  mounted() {}
};
</script>
