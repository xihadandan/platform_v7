<template>
  <div id="app">
    <well-row>
      <well-col :span="12">
        <well-input @change="test" />
      </well-col>
      <well-col :span="12">
        <well-button type="primary" icon="search">测试按钮</well-button>
      </well-col>
    </well-row>
  </div>
</template>

<script>
import 'ant-design-vue/dist/antd.css';
import WellButton from './components/button';
import WellInput from './components/input';
import WellRow from './components/row';
import WellCol from './components/col';
export default {
  name: 'App',
  components: { WellButton, WellInput, WellRow, WellCol },
  methods: {
    test() {
      alert('test');
    }
  }
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
