import WidgetNavBarDevelopment from "@develop/WidgetNavBarDevelopment";

class TestWidgetNavBarDevelopment extends WidgetNavBarDevelopment {
  test() {
    alert("执行了测试方法");
  }
  created() {
    /**
     * 监听下拉框值变更
     */
    this.pageContext.handleEvent(
      "ITwcwLOYNuBaXhBClxZhiXPNnVavNfgX:EzFuKLxJdrnohPjjqzlDglDDGlZJMnJG:change",
      ({ value, optionItem }) => {
        console.log(value, optionItem);
      }
    );
  }
  mounted() {
    console.log("测试方法挂载");
    this.setSelectValue("1");
  }
  get META() {
    return {
      name: "测试顶部导航栏二开",
      hook: {
        test: "测试方法",
      },
    };
  }
}
export default TestWidgetNavBarDevelopment;
