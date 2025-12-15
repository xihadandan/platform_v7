<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/taglibs.jsp" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>


    <%@ include file="/meta.jsp" %>


    <%--<link rel="stylesheet" href="${base}assets/3rd-libs/layui-v2.5.4/css/layui.css" media="all">
    <link rel="stylesheet" href="${base}assets/styles/css/layuimini.css" media="all">
    <link rel="stylesheet" href="${base}assets/styles/css/public.css" media="all">
    <link rel="stylesheet" href="${base}assets/3rd-libs/font-awesome-4.7.0/css/font-awesome.min.css" media="all">--%>

    <style id="layuimini-bg-color">
        .layui-layout-admin .layui-header {
            background-color: #1aa094 !important;
        }

        .layui-header > ul > .layui-nav-item.layui-this, .layuimini-tool i:hover {
            background-color: #197971 !important;
        }

        .layui-layout-admin .layui-logo {
            background-color: #0c0c0c !important;
        }

        .layui-side.layui-bg-black, .layui-side.layui-bg-black > .layui-left-menu > ul {
            background-color: #23262e !important;
        }

        .layui-left-menu .layui-nav .layui-nav-child a:hover:not(.layui-this) {
            background-color: #3b3f4b;
        }

        .layui-layout-admin .layui-nav-tree .layui-this, .layui-layout-admin .layui-nav-tree .layui-this > a, .layui-layout-admin .layui-nav-tree .layui-nav-child dd.layui-this, .layui-layout-admin .layui-nav-tree .layui-nav-child dd.layui-this a {
            background-color: #1aa094 !important;
        }
    </style>
    <link id="layuicss-layer" rel="stylesheet"
          href="${base}assets/3rd-libs/layui-v2.5.4/css/modules/layer/default/layer.css"
          media="all">
    <title>统一认证管理后台</title>
</head>
<body class="layui-layout-body layuimini-all">
<div class="layui-layout layui-layout-admin">

    <div class="layui-header header">
        <div class="layui-logo">
        </div>
        <a>
            <div class="layuimini-tool"><i title="展开" class="fa fa-outdent" data-side-fold="1"></i></div>
        </a>

        <ul class="layui-nav layui-layout-left layui-header-menu layui-header-pc-menu mobile layui-hide-xs">
            <sec:authorize access="hasRole('ROLE_ADMIN')">
                <li class="layui-nav-item " data-menu="clientManager">
                    <a href="javascript:"><i class="fa fa-server"></i> 应用管理</a>
                </li>

                <li class="layui-nav-item " data-menu="userManager">
                    <a href="javascript:"><i class="fa fa-user-circle-o"></i> 用户管理</a>
                </li>
            </sec:authorize>
        </ul>
        <ul class="layui-nav layui-layout-left layui-header-menu mobile layui-hide-sm">
            <li class="layui-nav-item">
                <a href="javascript:"><i class="fa fa-list-ul"></i>选择模块</a>
                <dl class="layui-nav-child layui-header-mini-menu">
                </dl>
            </li>
        </ul>

        <ul class="layui-nav layui-layout-right login-user-info">
            <%--<li class="layui-nav-item">
                <a href="javascript:;" data-refresh="刷新"><i class="fa fa-refresh"></i></a>
            </li>
            <li class="layui-nav-item">
                <a href="javascript:;" data-clear="清理" class="layuimini-clear"><i class="fa fa-trash-o"></i></a>
            </li>--%>
            <li class="layui-nav-item layuimini-setting">
                <a href="javascript:">
                    <sec:authorize access="hasRole('ROLE_USER')">
                        <sec:authentication property="principal.name"/>
                    </sec:authorize>
                </a>
                <dl class="layui-nav-child">
                    <sec:authorize access="hasRole('ROLE_USER')">
                        <dd>
                            <a href="javascript:"
                               data-iframe-tab="${base}user/<sec:authentication property="principal.username"/>/editByAccoutNumber"
                               data-title="基本资料"
                               data-icon="fa fa-gears">基本资料</a>
                        </dd>
                    </sec:authorize>
                    <sec:authorize access="hasRole('ROLE_ADMIN')">
                        <dd>
                            <a href="javascript:" data-title="修改密码" data-icon="fa fa-gears"
                               id="modifyPassword">修改密码</a>
                        </dd>
                    </sec:authorize>
                    <dd>
                        <a href="javascript:" class="login-out">退出登录</a>
                    </dd>
                </dl>
            </li>

        </ul>
    </div>

    <div class="layui-side layui-bg-black">
        <div class="layui-side-scroll layui-left-menu" id="menu">
            <sec:authorize access="hasRole('ROLE_ADMIN')">
                <ul class="layui-nav layui-nav-tree layui-left-nav-tree layui-hide" id="clientManager">
                    <li class="layui-nav-item">
                        <a href="javascript:" class="layui-menu-tips" data-type="tabAdd" data-tab-mpi="client_register"
                           data-tab="${base}client"
                           target="_self"><i
                                class="fa fa-pencil"></i><span class="layui-left-nav"> 注册应用</span></a>
                    </li>

                </ul>

                <ul class="layui-nav layui-nav-tree layui-left-nav-tree layui-hide" id="userManager">
                    <li class="layui-nav-item">
                        <a href="javascript:" class="layui-menu-tips" data-type="tabAdd" data-tab-mpi="user_register"
                           data-tab="${base}user"
                           target="_self"><i
                                class="fa fa-user-o"></i><span class="layui-left-nav"> 注册用户</span></a>
                    </li>

                </ul>
            </sec:authorize>

        </div>
    </div>

    <div class="layui-body">
        <div class="layui-tab" lay-filter="layuiminiTab" id="top_tabs_box">
            <ul class="layui-tab-title" id="top_tabs">
                <li class="layui-this" id="layuiminiHomeTabId" lay-id=""><i class="fa fa-home"></i> <span>首页</span></li>
            </ul>
            <ul class="layui-nav closeBox">
                <li class="layui-nav-item">
                    <a href="javascript:"> <i class="fa fa-dot-circle-o"></i> 页面操作</a>
                    <dl class="layui-nav-child">
                        <dd><a href="javascript:" data-page-close="other"><i class="fa fa-window-close"></i> 关闭其他</a>
                        </dd>
                        <dd><a href="javascript:" data-page-close="all"><i class="fa fa-window-close-o"></i> 关闭全部</a>
                        </dd>
                    </dl>
                </li>
            </ul>
            <div class="layui-tab-content clildFrame">
                <div id="layuiminiHomeTabIframe" class="layui-tab-item layui-show">
                </div>
            </div>
        </div>
    </div>

</div>


<%@ include file="/commonJs.jsp" %>
<script type="text/javascript" src="${base}assets/js/index.js"></script>

</body>
</html>
