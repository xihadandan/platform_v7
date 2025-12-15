<template>
  <a-skeleton active :loading="loading">
    <a-timeline style="padding-top: 10px">
      <template v-for="(line, i) in lines">
        <a-timeline-item style="cursor: pointer" :data-key="line.key">
          <div
            slot="dot"
            style="
              width: 10px;
              height: 10px;
              border: 2px solid transparent;
              border-radius: 100px;
              color: #1890ff;
              border-color: #1890ff;
              background: #1890ff;
            "
            v-if="line.key == selected"
          />
          <div>
            <div
              @click="e => clickTimelineItem(line)"
              :style="{
                background: selected == line.key ? 'var(--w-primary-color-3)' : 'none',
                borderRadius: '12px',
                width: 'fit-content',
                padding: '2px 14px'
              }"
            >
              {{ line.title }}
              <a-icon
                style="margin-left: 5px"
                :type="log[line.key].hide ? 'caret-up' : 'caret-down'"
                @click="log[line.key].hide = !log[line.key].hide"
              />
            </div>
            <div
              v-show="!log[line.key].hide"
              :style="{
                marginTop: '7px',
                background: selected == line.key ? 'var(--w-primary-color-2)' : 'rgb(247 247 247)',
                borderRadius: '2px',
                width: '100%',
                padding: '2px 14px'
              }"
            >
              <div v-if="log[line.key].remark" style="margin-bottom: 10px">{{ log[line.key].remark || '' }}</div>
              <a-row style="align-items: baseline; line-height: 28px" type="flex">
                <a-col flex="auto">
                  创建人
                  <UserDisplay
                    style="color: var(--w-primary-color)"
                    :userId="log[line.key]._creator"
                    emptyText="--"
                    v-if="log[line.key]._creator != undefined"
                  />
                </a-col>
                <a-col flex="175px" style="font-size: 12px; color: #999999">创建时间 {{ log[line.key].time }}</a-col>
                <a-col flex="55px" style="font-size: 12px; color: #999999">
                  <a-button type="link" size="small" @click="e => clickToDetail(line)" icon="file-search" />
                  <a-popconfirm
                    title="确定要删除该版本吗?"
                    ok-text="删除"
                    cancel-text="取消"
                    @confirm="e => clickToRemove(line, i)"
                    placement="leftBottom"
                  >
                    <a-button type="link" size="small" icon="delete" />
                  </a-popconfirm>
                </a-col>
              </a-row>
            </div>
          </div>
        </a-timeline-item>
      </template>
    </a-timeline>
  </a-skeleton>
</template>
<style lang="less"></style>
<script type="text/babel">
export default {
  name: 'VersionTimeline',
  props: {
    lines: Array,
    current: String,
    upgradeId: String,
    remove: Function,
    toDetail: Function
  },
  components: {},
  computed: {},
  data() {
    let log = {};
    for (let l of this.lines) {
      let creator = l.data && l.data.creator;
      log[l.key] = { remark: l.data && l.data.remark, time: l.time, hide: this.current !== l.key, creator, _creator: creator };
    }
    return { log, loading: true, selected: this.current };
  },
  beforeCreate() {},
  created() {},
  beforeMount() {
    this.getVersionRemarks();
  },
  mounted() {},
  methods: {
    clickTimelineItem(line) {
      this.$emit('clickItem', line);
      this.selected = line.key;
    },
    clickToDetail(line) {
      if (typeof this.toDetail == 'function') {
        this.toDetail(line, deleted => {
          if (deleted) {
            _this.lines.splice(i, 1);
          }
        });
      }
    },
    clickToRemove(line, i) {
      let _this = this;
      if (typeof this.remove == 'function') {
        this.remove(line, deleted => {
          if (deleted) {
            _this.lines.splice(i, 1);
          }
        });
      }
    },
    getVersionRemarks() {
      $axios.get(`/proxy/api/app/res/upgrade/log`, { params: { id: this.upgradeId } }).then(({ data }) => {
        this.loading = false;
        if (data.code == 0 && data.data) {
          for (let d of data.data) {
            if (this.log[d.resUuid]) {
              this.log[d.resUuid].remark = d.remark;
              this.log[d.resUuid].hide = true;
              this.log[d.resUuid]._creator = d.creator;
            }
          }
        }
      });
    }
  }
};
</script>
