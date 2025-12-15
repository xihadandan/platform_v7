well-init
=======

Wellsoft 应用初始化工具，所有 Wellsoft 应用开发必须安装。

## 安装

```bash
$ npm i well-init -g
$ well-init -h
```

## 创建 `simple` 类型的web应用

```bash
$ well-init --type simple [dest]
```

## 不输入类型可以选择

```bash
$ well-init dest
? Please select a boilerplate type (Use arrow keys)
❯ simple - Simple web app
  plugin - web plugin
```

## 支持的参数

```
Usage: well-init [dir] --type=simple

Options:
  --type          模板类型，支持: simple/plugin
  --dir           目录
  --force, -f     强制覆盖目录  
  --template      本地模板的目录路径
  --package       模板包名
  --registry, -r  npm仓库，支持: wellsoft/china/npm/custom
  --silent        静默初始化，项目选项使用默认值
  --version       展示版本号
  -h, --help      帮助
  
```


## License

[MIT](LICENSE)
