import WidgetDyformDevelopment from '@develop/WidgetDyformDevelopment';

class TestWidgetDyformDevelopment extends WidgetDyformDevelopment {

	test() {
		alert('执行了测试方法')
	}
	mounted() {
		console.log('测试方法挂载')
	}
	get META() {
		return {
			name: '测试表单二开',
			hook: {
				test: '测试方法'
			}
		}
	}
}
export default TestWidgetDyformDevelopment;