<template>
	<view class="w-page-container">
		<!-- 组件定义直接传入-->
		<view v-if="widgetDefinition.wtype != 'wMobilePage'">
			<!-- <w-widget :widget="widgetDefinition" :parent="{}"></w-widget> -->
			<widget :widget="widgetDefinition" :parent="{}"></widget>
		</view>
		<template v-for="item in widgetDefinition.items">
			<!-- <w-widget :widget="item" :parent="{}"></w-widget> -->
			<widget :widget="item" :parent="{}"></widget>
		</template>
	</view>
</template>

<script>
	// import wWidget from "../w-widget/w-widget";
	export default {
		components: {},
		props: {
			id: String,
			widgetDefinition: {
				type: Object,
				required: false,
			},
		},
	};
</script>

<style scoped>
	.w-page-container {}
</style>