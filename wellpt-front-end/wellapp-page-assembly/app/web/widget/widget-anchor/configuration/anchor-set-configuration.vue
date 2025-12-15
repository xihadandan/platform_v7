<template>
  <div>
    <a-form-model-item label="关联锚点元素" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
      <a-select
        v-model="anchor.href"
        :showSearch="true"
        :filterOption="anchorFilterOption"
        :options="anchorScopeWidgetOptions.selectOptions"
        :style="{ width: '100%' }"
        @change="(v, opt) => onSelectAnchorWidget(v, opt, anchor)"
      ></a-select>
    </a-form-model-item>
    <a-form-model-item label="锚点名称" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }" required>
      <a-input v-model="anchor.label">
        <template slot="addonAfter">
          <WI18nInput :widget="widget" :target="anchor" :designer="designer" :code="anchor.id" v-model="anchor.label" />
        </template>
      </a-input>
    </a-form-model-item>
    <a-form-model-item label="父级锚点" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
      <a-select
        v-model="anchor.pid"
        :options="anchorOptions"
        :style="{ width: '100%' }"
        @change="onChangeAnchorParent"
        allowClear
      ></a-select>
    </a-form-model-item>
  </div>
</template>
<style></style>
<script type="text/babel">
import { deepClone } from '@framework/vue/utils/util';

export default {
  name: 'AnchorSetConfiguration',
  mixins: [],
  props: {
    designer: Object,
    anchor: Object,
    widget: Object,
    anchorScopeWidgetOptions: Object
  },
  data() {
    return { lastParentId: this.anchor.pid };
  },

  beforeCreate() {},
  components: {},
  computed: {
    anchorOptions() {
      let options = [];
      let subOptions = function (anchors) {
        if (anchors)
          for (let i = 0, len = anchors.length; i < len; i++) {
            if (anchors[i].id === this.anchor.id) {
              continue;
            }
            options.push({
              value: anchors[i].id,
              label: anchors[i].label
            });
          }
      };
      for (let i = 0, len = this.widget.configuration.anchors.length; i < len; i++) {
        if (this.widget.configuration.anchors[i].id === this.anchor.id) {
          continue;
        }
        options.push({ value: this.widget.configuration.anchors[i].id, label: this.widget.configuration.anchors[i].label });
        subOptions.call(this, this.widget.configuration.anchors[i].anchors);
      }
      return options;
    }
  },
  created() {},
  methods: {
    anchorFilterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    },
    onSelectAnchorWidget(v, opt, anchor) {
      if (v && !anchor.label) {
        anchor.label = opt.componentOptions.children[0].text.trim();
      }
    },

    findParent(anchors, id) {
      for (let i = 0, len = anchors.length; i < len; i++) {
        if (anchors[i].id === id) {
          return anchors[i];
        }
        let parent = this.findParent(anchors[i].anchors, id);
        if (parent != null) {
          return parent;
        }
      }
    },
    remove(anchors, id) {
      for (let i = 0, len = anchors.length; i < len; i++) {
        if (anchors[i].id === id) {
          anchors.splice(i, 1);
          break;
        }
      }
    },

    onChangeAnchorParent(v) {
      if (this.lastParentId != null) {
        // 从旧的父级下子锚点集移除
        let parent = this.findParent(this.widget.configuration.anchors, this.lastParentId);
        this.remove(parent.anchors, this.anchor.id);
      } else {
        for (let i = 0, len = this.widget.configuration.anchors.length; i < len; i++) {
          if (this.widget.configuration.anchors[i].id === this.anchor.id) {
            this.widget.configuration.anchors.splice(i, 1);
            break;
          }
        }
      }
      if (v) {
        // 添加到新的父级下子锚点集合
        let parent = this.findParent(this.widget.configuration.anchors, v);
        parent.anchors.push(this.anchor);
        this.anchor.pid = parent.id;
        this.anchor.level = parent.level + 1;
      } else {
        this.widget.configuration.anchors.push(this.anchor);
        this.anchor.pid = undefined;
        this.anchor.level = 1;
      }
      if (this.anchor.level <= 2 && !this.anchor.hasOwnProperty('anchors')) {
        this.$set(this.anchor, 'anchors', this.anchor.anchorsTemp || []);
      }
      if (this.anchor.level == 3 && this.anchor.hasOwnProperty('anchors')) {
        this.anchor.anchorsTemp = deepClone(this.anchor.anchors);
        delete this.anchor.anchors; // 暂时删除该子集
      }

      this.lastParentId = v;
    }
  },

  mounted() {}
};
</script>
