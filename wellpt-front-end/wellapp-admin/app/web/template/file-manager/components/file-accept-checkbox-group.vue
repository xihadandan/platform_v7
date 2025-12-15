<template>
  <div class="file-accept-checkbox-group">
    <div>支持的文件格式</div>
    <a-checkbox-group v-model="configuration.fileAccept" style="width: 100%">
      <a-row v-for="(num, index) in fileAcceptOptionGroup" :key="index">
        <a-col :span="12">
          <a-checkbox :value="fileAcceptOptions[index * 2].value" @change="groupCheckedChange">
            {{ fileAcceptOptions[index * 2].label }}
          </a-checkbox>
          (
          <a-checkbox
            v-for="(item, key) in containItems(fileAcceptOptions[index * 2].value)"
            :key="key"
            :value="item.value"
            @change="groupItemCheckedChange($event, fileAcceptOptions[index * 2].value)"
          >
            {{ item.label }}
          </a-checkbox>
          )
        </a-col>
        <a-col :span="12">
          <template v-if="fileAcceptOptions[index * 2 + 1]">
            <a-checkbox :value="fileAcceptOptions[index * 2 + 1].value" @change="groupCheckedChange">
              {{ fileAcceptOptions[index * 2 + 1].label }}
            </a-checkbox>
            (
            <a-checkbox
              v-for="(item, key) in containItems(fileAcceptOptions[index * 2 + 1].value)"
              :key="key"
              :value="item.value"
              @change="groupItemCheckedChange($event, fileAcceptOptions[index * 2 + 1].value)"
            >
              {{ item.label }}
            </a-checkbox>
            )
          </template>
        </a-col>
      </a-row>
    </a-checkbox-group>
  </div>
</template>

<script>
export default {
  props: {
    configuration: Object
  },
  data() {
    return {
      fileAcceptOptions: [
        { label: 'Word', value: '.doc;.docx;.wps' },
        { label: 'PDF', value: '.pdf' },
        { label: 'Excel', value: '.xls;.xlsx;.et' },
        { label: 'PPT', value: '.ppt;.pptx;.dps' },
        { label: '图片', value: '.jpg;.jpeg;.png;.gif' },
        { label: '视频', value: '.avi;.mp4;.mpeg;.wmv' },
        { label: '音频', value: '.mp3;.wma;.wav;.flac' },
        { label: '文本', value: '.txt' },
        { label: '压缩包', value: '.zip;.rar;.7z' }
      ]
    };
  },
  computed: {
    fileAcceptOptionGroup() {
      return Math.ceil(this.fileAcceptOptions.length / 2);
    }
  },
  methods: {
    containItems(value) {
      let items = value.split(';');
      return items.map(item => ({ label: item, value: item }));
    },
    groupCheckedChange(event) {
      let checked = event.target.checked;
      let value = event.target.value;
      let items = value.split(';');
      if (items.length <= 1) {
        return;
      }

      if (checked) {
        items.forEach(item => {
          if (!this.configuration.fileAccept.includes(item)) {
            this.configuration.fileAccept.push(item);
          }
        });
      } else {
        items.forEach(item => {
          let index = this.configuration.fileAccept.indexOf(item);
          if (index !== -1) {
            this.configuration.fileAccept.splice(index, 1);
          }
        });
      }
    },
    groupItemCheckedChange(event, groupValue) {
      let checked = event.target.checked;
      let value = event.target.value;
      let items = groupValue.split(';');
      let checkedAll = checked;
      if (value == groupValue) {
        return;
      }

      if (checked) {
        items.forEach(item => {
          if (value != item && !this.configuration.fileAccept.includes(item)) {
            checkedAll = false;
          }
        });
      }

      if (checkedAll) {
        this.configuration.fileAccept.push(groupValue);
      } else {
        let index = this.configuration.fileAccept.indexOf(groupValue);
        if (index !== -1) {
          this.configuration.fileAccept.splice(index, 1);
        }
      }
    }
  }
};
</script>

<style lang="less" scoped>
.file-accept-checkbox-group {
  padding: 0 0 0 12px;
  background-color: #efefef;
}
</style>
