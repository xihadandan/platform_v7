<template>
  <ul class="msg-set-wrap">
    <li class="msg-set-title">
      <div>
        新消息弹出提醒
        <span class="new-msg-tip">（开启后收到指定类型的消息时将会在右下角弹出提醒）</span>
        <a-switch v-model="mainSwitch" style="float: right" />
      </div>
    </li>
    <template v-for="(item, index) in list">
      <template v-if="item.classify && item.userPersonalises">
        <li :key="index">
          <div @click="openClassify(index)" style="cursor: pointer">
            <i :class="['msg-set-folder iconfont', item.open ? 'icon-ptkj-shixinjiantou-xia' : 'icon-ptkj-shixinjiantou-you']"></i>
            <span :style="{ background: item.classify.iconBg || defaultIconBg }">
              <Icon :type="item.classify.icon || defalutIcon" :disabled="!mainSwitch" />
            </span>
            {{ item.classify.name }}
          </div>
        </li>
        <ul class="hasClassify" :key="'ul_' + index" v-show="item.open">
          <li v-for="(citem, cindex) in item.userPersonalises" :key="citem.templateId + '_' + cindex">
            <div>
              {{ citem.templateName }}
              <a-switch v-model="citem.isSwitch" style="float: right" :disabled="!mainSwitch" />
            </div>
          </li>
        </ul>
      </template>
      <template v-else-if="item.userPersonalises">
        <li v-for="(citem, cindex) in item.userPersonalises" :key="citem.templateId + '_' + cindex">
          <div>
            {{ citem.templateName }}
            <a-switch v-model="citem.isSwitch" style="float: right" :disabled="!mainSwitch" />
          </div>
        </li>
      </template>
    </template>
  </ul>
</template>
<script type="text/babel">
export default {
  name: 'MessageOnlineSetting',
  data() {
    return {
      list: [],
      defalutIcon: 'pticon iconfont icon-xmch-wodexiaoxi',
      defaultIconBg: '#64B3EA',
      mainSwitch: true
    };
  },
  created() {
    this.queryList();
  },
  methods: {
    queryList() {
      this.loading = true;
      $axios.get('/userPersonalise/queryList').then(({ data }) => {
        if (data.code == 0 && data.data) {
          this.mainSwitch = !!data.data.mainSwitch;
          this.list = _.map(data.data.userPerClassifys, item => {
            _.each(item.userPersonalises, citem => {
              citem.isSwitch = !!citem.isPopup;
            });
            return item;
          });
        }
      });
    },
    openClassify(index) {
      let open = this.list[index].open;
      this.$set(this.list[index], 'open', !open);
    },
    reset(callback) {
      $axios.put('/userPersonalise/reset').then(({ data }) => {
        if (data.code == 0) {
          this.$message.success('重置成功');
          this.queryList();
          callback();
        }
      });
    },
    save(callback) {
      let userPer = this.list;
      let templateIds = [];
      let isPopups = [];
      for (var i = 0; i < userPer.length; i++) {
        if (userPer[i].userPersonalises && userPer[i].userPersonalises.length > 0) {
          for (var j = 0; j < userPer[i].userPersonalises.length; j++) {
            templateIds.push(userPer[i].userPersonalises[j].templateId);
            isPopups.push(userPer[i].userPersonalises[j].isSwitch ? 1 : 0);
          }
        }
      }
      let param = {
        mainSwitch: this.mainSwitch ? 1 : 0,
        templateIds: templateIds,
        isPopups: isPopups
      };
      $axios.post('/userPersonalise/saveUserPersonalise', param).then(({ data }) => {
        if (data.code == 0) {
          this.$message.success('保存成功');
          callback();
        }
      });
    }
  }
};
</script>
