<template>
	<view :style="theme" class="content">
		<w-app-page v-if="appPiPath != '' || widgetDefId != '' || pageId !=''" :appPiPath="appPiPath" :pageUuid="pageUuid"
			:pageId="pageId" :widgetDefId="widgetDefId"></w-app-page>
	</view>
</template>

<script>
	import {
		mapMutations
	} from "vuex";
	import {
		isEmpty
	} from "lodash";
	export default {
		data() {
			this.setCustomTabBar(false);
			return {
				appPiPath: "",
				pageUuid: "",
				widgetDefId: "",
				pageId: ""
			};
		},
		onLoad: function(options) {
			const _self = this;
			for (var key of ['appPiPath', 'pageUuid', 'pageId', 'widgetDefId']) {
				_self[key] = options[key] || ''
			}

		},
		onShow: function() {
			const _self = this;
			console.log("app page show");
			let appPage = "";
			// 组件定义加载的页面ID
			if (!isEmpty(_self.widgetDefId)) {
				appPage = "widgetDefId_" + _self.widgetDefId;
			} else {
				appPage = _self.appPiPath + "_" + _self.pageUuid || _self.pageId;
			}
			_self.setCustomNavBar(true);
			_self.setCurrentAppPage(appPage);
			_self.updateNavBarTitleByAppPage(appPage);
		},
		methods: {
			...mapMutations(["setCustomNavBar", "setCurrentAppPage", "updateNavBarTitleByAppPage", "setCustomTabBar"]),
		},
	};
</script>

<style scoped>
	.content {
		padding: 0;
	}
</style>