# wellapp-theme

平台主题包

### 新项目

使用 `wellapp-init` 创建新项目时，会一起安装安装主题包，无需额外配置。

### 现有项目

1. 安装主题包依赖:

```bash
$ npm i wellapp-theme@62.9.0-beta-theme --save
# or
$ yarn add wellapp-theme@62.9.0-beta-theme
```

2. `package.json` 的 `scripts` 下增加下面四项:

```json {highlight=[3,4,5,6]}
{
  "scripts": {
    "predev": "wellapp-theme --resource --theme all",
    "predebug": "wellapp-theme --resource --theme all",
    "prepublishOnly": "wellapp-theme --resource --min --theme all",
    "postpublish": "wellapp-theme --resource"
  }
}
```

3. 创建下面三个文件夹

```bash
# 业务线主题编译后文件
$ mkdir -p app/assets/themes
$ touch app/assets/themes/.gitkeep

#主题资源配置文件
$ mkdir -p config/resource
$ touch config/resource/.gitkeep

# 业务线主题入口
$ mkdir -p app/styles
$ touch app/styles/index.less
```

### 使用方式

在 `app/styles/index.less` 导入所有需要的样式文件。

### 主题包提供的命名

#### 运行

```bash
$ wellpt-theme [options]
```

#### options

- `theme` - 生成指定主题文件，配置为 `all` 时将生成所有主题文件.
- `resource` - 生成主题静态资源文件.
- `min` - 生成压缩版样式文件.
