/**
 * 获取 manifest.json 配置工具类
 */
class ManifestUtils {
  constructor() {
    this.manifest = null;
    this.loadManifest();
  }

  // 加载 manifest.json
  loadManifest() {
    // #ifdef H5
    // H5 端可以直接读取
    this.manifest = require("@/manifest.json");
    // this.manifest = process.env.UNI_MANIFEST || {};
    // #endif

    // #ifndef H5
    // 小程序和 App 端需要通过特定方式获取
    try {
      // 尝试从全局变量获取
      if (typeof __uniConfig !== "undefined") {
        this.manifest = __uniConfig;
      } else {
        // 如果是开发环境，可以尝试读取文件
        this.manifest = require("@/manifest.json");
      }
    } catch (error) {
      console.warn("无法直接读取 manifest.json，使用默认配置");
      this.manifest = this.getDefaultConfig();
    }
    // #endif
  }

  // 获取默认配置
  getDefaultConfig() {
    return {
      name: "uni-app",
      appid: "",
      description: "",
      versionName: "1.0.0",
      versionCode: "100",
      transformPx: false,
    };
  }

  // 获取应用名称
  getAppName() {
    return this.manifest.name || "uni-app";
  }

  // 获取应用ID
  getAppId() {
    return this.manifest.appid || "";
  }

  // 获取版本信息
  getVersionInfo() {
    return {
      versionName: this.manifest.versionName || "1.0.0",
      versionCode: this.manifest.versionCode || "100",
    };
  }

  // 获取高德地图配置
  getAmapConfig() {
    const config = {
      h5: {},
      mpWeixin: {},
      app: {},
    };

    // H5 配置
    if (this.manifest.h5 && this.manifest.h5.sdkConfigs && this.manifest.h5.sdkConfigs.maps) {
      config.h5 = this.manifest.h5.sdkConfigs.maps.amap || {};
    }

    // 微信小程序配置
    if (this.manifest["mp-weixin"]) {
      // 微信小程序的配置可能在其他地方，这里需要根据实际情况调整
      config.mpWeixin = {
        key: (this.manifest["mp-weixin"].setting && this.manifest["mp-weixin"].setting.amapKey) || "",
      };
    }

    // App 配置
    if (this.manifest["app-plus"] && this.manifest["app-plus"].distribute) {
      config.app = {
        android: this.manifest["app-plus"].distribute.android || {},
        ios: this.manifest["app-plus"].distribute.ios || {},
      };
    }

    return config;
  }

  // 获取权限配置
  getPermissions() {
    const permissions = {
      location: false,
      camera: false,
      microphone: false,
    };

    // 检查微信小程序权限
    if (this.manifest["mp-weixin"] && this.manifest["mp-weixin"].permission) {
      const wxPermission = this.manifest["mp-weixin"].permission;
      permissions.location = !!wxPermission["scope.userLocation"];
      permissions.camera = !!wxPermission["scope.camera"];
      permissions.microphone = !!wxPermission["scope.record"];
    }

    // 检查 App 权限
    if (this.manifest["app-plus"] && this.manifest["app-plus"].distribute) {
      const androidPermissions =
        (this.manifest["app-plus"].distribute.android && this.manifest["app-plus"].distribute.android.permissions) ||
        [];
      permissions.location = androidPermissions.some(
        (p) => p.includes("ACCESS_COARSE_LOCATION") || p.includes("ACCESS_FINE_LOCATION")
      );
      permissions.camera = androidPermissions.some((p) => p.includes("CAMERA"));
      permissions.microphone = androidPermissions.some((p) => p.includes("RECORD_AUDIO"));
    }

    return permissions;
  }

  // 获取所有配置
  getAllConfig() {
    return this.manifest;
  }

  // 动态更新配置（开发环境使用）
  updateConfig(newConfig) {
    if (process.env.NODE_ENV === "development") {
      this.manifest = { ...this.manifest, ...newConfig };
    }
  }
}

// 创建单例
const manifestUtils = new ManifestUtils();

export default manifestUtils;
