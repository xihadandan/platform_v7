const path = require("path");
const fse = require("fs-extra");
const fs = require("fs");
const lodash = require("lodash");

class CustomCompilePlugin {
	apply(compiler) {
		compiler.hooks.environment.tap("beforeCompilePlugin", (stats) => {
			if (process.env.UNI_PLATFORM == "mp-weixin") {
				compiler.hooks.emit.tapAsync("CustomCompilePlugin", (compilation, callback) => {
					// 遍历编译后的资源，对分包组件进行占位处理
					Object.keys(compilation.assets).forEach((assetName) => {
						if (assetName.endsWith(".json")) {
							let source = compilation.assets[assetName].source();
							if (source) {
								let json = JSON.parse(source);
								if (json.usingComponents) {
									if (json.componentPlaceholder == undefined) {
										json.componentPlaceholder = {};
									}
									for (let key in json.usingComponents) {
										json.componentPlaceholder[key] = "view";
									}
									source = JSON.stringify(json, null, 2);
								}
							}

							// 更新资源内容
							compilation.assets[assetName] = {
								source: () => source,
								size: () => source.length,
							};
						}
					});

					// 调用回调以继续编译过程
					callback();
				});
			}
		});
	}
}
// const LodashModuleReplacementPlugin = require("lodash-webpack-plugin");
// const BundleAnalyzerPlugin = require("webpack-bundle-analyzer").BundleAnalyzerPlugin;

// 加载 .env 环境环境 (HBuilderx 需要)
const dotenv = require("dotenv");
const envPaths = [];
if (!path.resolve(process.env.UNI_INPUT_DIR).endsWith('wellapp-uni-app')) {
	envPaths.push(path.resolve(process.env.UNI_INPUT_DIR, 'node_modules', 'wellapp-uni-app', ".env"));
	envPaths.push(path.resolve(process.env.UNI_INPUT_DIR, 'node_modules', 'wellapp-uni-app', process.env.NODE_ENV ===
		"production" ? ".env.production" : (process.env.NODE_ENV == 'test' ? ".env.test" : ".env.development")));
}
// 加载默认变量
envPaths.push(path.resolve(process.env.UNI_INPUT_DIR, ".env"));
// 加载环境变量
envPaths.push(path.resolve(
	process.env.UNI_INPUT_DIR,
	process.env.NODE_ENV === "production" ? ".env.production" : (process.env.NODE_ENV == 'test' ? ".env.test" :
		".env.development")
));
dotenv.config({
	path: envPaths,
	override: true
});

const webpack = require(path.resolve(process.env.UNI_INPUT_DIR, "node_modules", "webpack"));

if (!path.resolve(process.env.UNI_INPUT_DIR).endsWith('wellapp-uni-app')) {
	// 拷贝平台代码到主目录
	fse.copySync(path.resolve(process.env.UNI_INPUT_DIR, "node_modules", "wellapp-uni-app/packages/_"),
		path.resolve(process.env.UNI_INPUT_DIR, "packages/_"));
}

// 获取pages.json配置的应用首页地址
const pagesJson = require(path.resolve(process.env.UNI_INPUT_DIR, "pages.json"));
let indexPagePath = '/pages/welldo/index',
	loginPath = '/packages/_/pages/login/login'; // 默认平台登录页
if (pagesJson && pagesJson.pages && pagesJson.pages.length > 0) {
	indexPagePath = `/${pagesJson.pages[0].path}`;
	for (let p of pagesJson.pages) {
		if (p.path === 'pages/login/login') {
			// 项目自定义登录页
			loginPath = `/${p.path}`;
			break;
		}
	}

}
let localePaths = [],
	localeJsonString = undefined;

class CopyLocalePlugin {
	constructor(options) {
		// 插件初始化时接收的参数
		this.options = options;
		// console.log("********************* 开始合并国际化json文件 *********************");
		mergeLocaleJsonFiles(localeJsonString, process.env.NODE_ENV, true);
	}

	// Webpack 会在编译流程的不同阶段触发这个 apply 方法
	apply(compiler) {
		// 注册编译前的钩子
		if (process.env.NODE_ENV === "development") {
			compiler.hooks.watchRun.tap("CopyLocalePlugin", (compiler) => {
				let result = mergeLocaleJsonFiles(localeJsonString, "development", true);
				localeJsonString = result.localeJsonString;
			});

			compiler.hooks.beforeCompile.tapAsync("CopyLocalePlugin", (compilation, callback) => {
				if (localePaths.length) {
					compiler.hooks.afterCompile.tap("CopyLocalePlugin", (compilation) => {
						localePaths.forEach((dep) => {
							compilation.contextDependencies.add(dep);
						});
					});
				}
				callback();
			});
		}
	}
}

function traverseDirectory(dir, callback, underLocaleDirectory) {
	fs.readdirSync(dir).forEach((item) => {
		const fullPath = path.join(dir, item);
		const stats = fs.statSync(fullPath);
		if (stats.isDirectory()) {
			// 如果是 locale 目录，则递归调用
			traverseDirectory(fullPath, callback, underLocaleDirectory || item == "locale");
		} else if (stats.isFile() && /^([a-z]+_[A-Z]+)\.json$/.test(item) && underLocaleDirectory) {
			// 如果是文件且匹配国际化文件名，则调用回调函数
			callback(fullPath, item, dir);
		}
	});
}

function mergeLocaleJson(localeJSON, filePath, filename, dir, compiler) {
	try {
		let modulePath = require.resolve(path.resolve(filePath));
		delete require.cache[modulePath];
		let json = require(modulePath);
		let locale = filename.replace(".json", "");
		lodash.merge(localeJSON, {
			[locale]: json,
		});
	} catch (error) {
		console.error(error);
	}
}

function mergeLocaleJsonFiles(localeJsonString, mode, logPrint) {
	const startTime = process.hrtime(); // 记录开始时间
	const localeJSON = {};
	let localePaths = [];
	let initPath = path.join(process.env.UNI_INPUT_DIR, "packages", "_", "components");
	traverseDirectory(
		initPath,
		function(filePath, filename, dir) {
			localePaths.push(dir);
			mergeLocaleJson(localeJSON, filePath, filename, dir);
		},
		false
	);
	if (!fs.existsSync("node_modules/.locale")) {
		fs.mkdirSync("node_modules/.locale");
	}
	let str = JSON.stringify(localeJSON);
	if (localeJsonString != str) {
		localeJsonString = str;
		for (let key in localeJSON) {
			fs.writeFileSync(
				`node_modules/.locale/${key}.json`,
				process.env.NODE_ENV == "production" ?
				JSON.stringify(localeJSON[key]) :
				JSON.stringify(localeJSON[key], null, "\t")
			);
		}

		const endTime = process.hrtime(startTime); // 记录结束时间
		const duration = (endTime[0] * 1e9 + endTime[1]) / 1e6; // 转换为毫秒
		if (logPrint) {
			console.log(`国际化json数据处理耗时: ${duration.toFixed(2)} ms`);
		}
	}

	return {
		localePaths,
		localeJsonString,
	};
}

module.exports = {
	// 生产构建时禁用eslint-loader
	lintOnSave: process.env.NODE_ENV !== "production",
	// webpack配置 - 简单配置方式
	configureWebpack: {
		module: {
			rules: [{
					test: /RenderDevelopTemplate\.vue$/,
					use: "render-develop-template-loader",
				},
				// {
				// 	test: /developComps\.js$/,
				// 	use: "w-template-register-loader",
				// 	include: /@develop/,
				// },
				// FIXME: 二开整体组件不推荐，应对组件的部分能力进行二开
				// {
				//   test: /w-widget-development\.vue$/,
				//   use: "widget-development-loader",
				//   include: /w-widget-development/,
				// },
				{
					test: /dms_dyform_view\.vue$/,
					use: "dms-dyform-view-loader",
					include: path.join(process.env.UNI_INPUT_DIR, "packages", "_", "pages", "dms"),
				},
				{
					test: /work_view\.vue$/,
					use: "work-view-loader",
					include: path.join(process.env.UNI_INPUT_DIR, "packages", "_", "pages", "workflow"),
				},
				// {
				//   test: path.join(process.env.UNI_INPUT_DIR, "main.js"),
				//   use: "global-component-loader",
				// },
			],
		},
		resolve: {
			alias: {
				"@develop": path.join(process.env.UNI_INPUT_DIR, "node_modules", "wellapp-uni-framework", "src", "@develop"),
				// "@dcloudio": path.join(process.env.UNI_INPUT_DIR, "packages", "_", "components"),
				lodash: path.join(process.env.UNI_INPUT_DIR, "node_modules", "lodash-es"),
				"@locale": path.join(process.cwd(), "node_modules", ".locale"),
				// "@modules"path.join(process.cwd(), "node_modules", "wellapp-uni-framework", "src", "@develop"),
				// "@wellappStaticResource": path.join(__dirname, "node_modules", "wellapp-static-resource", "app", 'public',
				// 	'resource'),
				// "@wellapp-uni-framework": path.join(__dirname, "node_modules", "wellapp-uni-framework", "src"),
				// vue$: path.resolve(__dirname, 'src/common/vue/vue.esm.js')
				// 更改h5-vue使用的库
				// vue$: '@dcloudio/vue-cli-plugin-uni/packages/h5-vue/dist/vue.esm.js'
				// mp-weixin使用库，不包含vue编译器，要重新提供
				// vue$: '@dcloudio/vue-cli-plugin-uni/packages/mp-vue/dist/mp.runtime.esm.js',
				"@workflow-components": path.join(
					process.env.UNI_INPUT_DIR,
					"packages",
					"_",
					"components",
					"well-components",
					"workflow"
				),
				"@dyform-components": path.join(
					process.env.UNI_INPUT_DIR,
					"packages",
					"_",
					"components",
					"well-components",
					"dyform"
				),
				"@page-components": path.join(
					process.env.UNI_INPUT_DIR,
					"packages",
					"_",
					"components",
					"well-components",
					"page"
				),
			},
		},
		resolveLoader: {
			modules: [
				"./node_modules",
				path.join(process.env.UNI_INPUT_DIR, "node_modules", "wellapp-uni-framework", "src", "loader"),
			],
		},
		performance: {
			//入口起点的最大体积
			maxEntrypointSize: 50000000,
			//生成文件的最大体积
			maxAssetSize: 30000000,
		},

		plugins: [
			new webpack.DefinePlugin({
				INDEX_PAGE_PATH: JSON.stringify(indexPagePath),
				LOGIN_PAGE_PATH: JSON.stringify(loginPath),
				VUE_APP_WELLAPP_BACKEND_URL: JSON.stringify(process.env.VUE_APP_WELLAPP_BACKEND_URL),
			}),
			new CustomCompilePlugin(),
			new CopyLocalePlugin(),
			// new LodashModuleReplacementPlugin(),
			// new BundleAnalyzerPlugin({ generateStatsFile: true }),
		],

		// pluginOptions: {
		//   define: {
		//     $axios: "uni.$axios",
		//   },
		// },
	},
	transpileDependencies: [],
};
