import WidgetFileSourceDevelopment from '@develop/WidgetFileSourceDevelopment';

class DemoFileSourceDevelopment extends WidgetFileSourceDevelopment {
  get META() {
    return {
      name: '附件来源_demo示例',
    };
  }

  /**
  * 选择文件
  */
  chooseFile() {
    console.log('chooseFile');
  }
}

export default DemoFileSourceDevelopment;
