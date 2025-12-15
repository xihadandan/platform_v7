<template>
	<view :style="theme" class="login-settings">
		<uni-nav-bar v-if="showNavBar" class="pt-nav-bar" :title="$t('loginComponent.loginSettings', '登录设置')"
			:shadow="false" :border="false" statusBar @clickLeft="back" left-icon="left" />
		<view style="padding: 20px 12px; flex: 1">
			<uni-forms ref="form" :rules="rules" :modelValue="server" label-position="top">
				<uni-forms-item v-if="showSystemSelect" :label="$t('loginComponent.setting.systemId', '系统')" name="systemId"
					labelWidth="85" required>
					<uni-data-select v-model="server.systemId" :localdata="systemOptions"></uni-data-select>
				</uni-forms-item>
				<uni-forms-item :label="$t('loginComponent.setting.address', '服务器地址')" name="host" labelWidth="85">
					<uni-w-easyinput type="text" v-model="server.host" :placeholder="$t('global.placeholder', '请输入')" />
				</uni-forms-item>
				<uni-forms-item :label="$t('loginComponent.setting.port', '服务器端口')" name="port" labelWidth="85"
					:required="server.host ? true : false">
					<uni-w-easyinput v-model="server.port" :placeholder="$t('global.placeholder', '请输入')" type="number" />
				</uni-forms-item>
				<uni-forms-item :label="$t('loginComponent.setting.ssl', '启用SSL')" name="enableSSL" labelWidth="85">
					<uni-w-switch v-model="server.enableSSL" />
				</uni-forms-item>
				<view class="">
					<uni-w-button block type="primary" @click="submitForm">
						{{ $t("global.confirm", "确认") }}
					</uni-w-button>
				</view>
			</uni-forms>
		</view>
	</view>
</template>

<script>
	import {
		trim
	} from "lodash";
	import {
		storage
	} from "wellapp-uni-framework";
	export default {
		props: {
			showNavBar: {
				type: Boolean,
				default: true
			},
			showSystemSelect: {
				type: Boolean,
				default: true
			}
		},
		data() {
			return {
				server: {
					host:   "",
					port:   "",
					enableSSL: false,
					systemId: "",
				},
				systemOptions: []
			};
		},
		computed: {
			rules() {
				let rules = {
					port: {
						rules: [{
							required: false,
							errorMessage: this.$t("global.placeholder", "请输入") + this.$t("loginComponent.setting.port", "服务器端口"),
						}, ],
					},
				};
				if (this.systemSelectable) {
					rules.systemId = {
						rules: [{
							required: true,
							errorMessage: this.$t("global.placeholder", "请输入") + this.$t("loginComponent.setting.systemId",
								"系统ID"),
						}, ],
					};
				}
				// 有服务端地址才进行端口校验
				var host = trim(this.server.host);
				if (host) {
					rules.port.rules[0].required = true;
				} else {
					setTimeout(() => {
						this.$refs.form && this.$refs.form.validateField("port");
					}, 100);
				}
				return rules;
			},
		},
		watch: {},
		onLoad: function() {},
		created() {
			this.init();
		},
		mounted() {
			if (this.showSystemSelect) {
				this.fetchSystemOptions();
			}
		},
		methods: {
			init() {
				var serverConfig = uni.getStorageSync("server_config");
				if (typeof serverConfig == "object") {
					this.server = serverConfig;
				}
				let systemId = storage.getSystem();
				if (systemId) {
					this.server.systemId = systemId;
				}
        let defaultServerUrl = storage.getWellappBackendUrl();
        if(defaultServerUrl){
          let url= new URL(defaultServerUrl);
          this.server.host = url.host;
          this.server.port = url.port;
          this.server.enableSSL = url.protocol ==='https:'
        }

			},
			$t() {
				return this.$i18n.$t(this, ...arguments);
			},
			onEnableSLLSwitchChange: function(e) {
				this.server.enableSSL = e.detail.value;
			},
			fetchSystemOptions() {
 				this.$axios
					.get(`/api/system/listAllAppSystemInfos`, {headers: {
							// 未登录情况下，可以通过受信客户端token访问后端服务接口
							'client-token': process.env.VUE_APP_TRUSTED_CLIENT_TOKEN
						}})
					.then(({
						data: result
					}) => {
						if (result && result.data) {
							for (let d of result.data) {
								this.systemOptions.push({
									text: d.title,
									value: d.system
								})
							}
						}
						console.log('获取到所有的系统信息', result);
					});
			},
			submitForm() {
				var _self = this;
				_self.$refs["form"]
					.validate()
					.then((res) => {
						// console.log('success', res);
						_self.save(res);
					})
					.catch((err) => {
						console.log("err", err);
					});
			},
			save() {
				var _self = this;
				storage.setSystem(_self.server.systemId);
				var host = trim(_self.server.host);
				var port = trim(_self.server.port);
				var wellappBackendUrl = "";
				if (host) {
					var enableSSL = _self.server.enableSSL;
					if (enableSSL) {
						wellappBackendUrl = "https://" + host + ":" + port;
					} else {
						wellappBackendUrl = "http://" + host + ":" + port;
					}
					storage.setWellappBackendUrl(wellappBackendUrl);
				} else {
					storage.removeStorageSync("wellapp_backend_url");
				}
				uni.setStorageSync("server_config", _self.server);
				uni.showToast({
					title: this.$t("global.successText", {
						name: this.$t("WidgetSubform.button.save", "保存")
					}, "保存成功!"),
				});
				this.$emit("ok");
			},
			back() {
				uni.navigateBack({
					delta: 1,
				});
			},
		},
	};
</script>

<style lang="scss" scoped>
	.login-settings {
		::v-deep .uni-forms-item {
			.uni-forms-item__label {
				font-size: 15px;
				color: var(--w-text-color-mobile);
				font-weight: bold;
			}
		}
	}
</style>
