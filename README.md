# 主要目的:


前后端分离、集团化、CMS优化、角色权限优化、四大员


# GIT命令 
Git 全局设置
```
git config --global user.name "用户名" 
git config --global user.email "邮箱地址"
```
创建一个新仓库
```
git clone http://gitlab.well-soft.com:81/wpp/6.1.git
cd 6.2
touch README.md
git add README.md
git commit -m "add README"
git push -u origin master
```
推送现有文件夹
```
cd existing_folder
git init
git remote add origin http://gitlab.well-soft.com:81/wpp/6.1.git
git add .
git commit -m "Initial commit"
git push -u origin master
```
推送现有的 Git 仓库
```
cd existing_repo
git remote rename origin old-origin
git remote add origin http://gitlab.well-soft.com:81/wpp/6.1.git
git push -u origin --all
git push -u origin --tags
```

对比差异
```
git diff  dev-6.1.3 origin/dev-6.1.3
```


查看所有分支
```
git branch -avv
```


查看所有分支
```
git branch -avv
```

使用git删除server上的一个branch：
```
git push origin –delete 分支名

```

使用git删除本地的一个branch：
```
git branch –D 分支名

```

查看远程仓库
```
git remote –v

```
保存现有草稿
```
git stash
```
```
git stash pop
```
