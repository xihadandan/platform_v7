<template functional>
  <a-anchor-link :href="'#' + props.anchor.href" v-if="!props.anchor.hidden" :style="props.titleStyle" :title="props.anchor.label">
    <template slot="title">
      <span :class="['line', 'line-' + props.anchor.level]"></span>
      <span class="unactive-point" />
      {{ props.anchor.label }}
    </template>

    <template v-if="props.anchor.anchors && props.anchor.anchors.length > 0">
      <template v-for="(subanchor, j) in props.anchor.anchors">
        <AnchorLink :anchor="subanchor" :key="'subanchorlink_' + j" v-if="!subanchor.hidden" />
      </template>
    </template>
  </a-anchor-link>
</template>

<script type="text/babel">
export default {
  name: 'AnchorLink'
};
</script>
