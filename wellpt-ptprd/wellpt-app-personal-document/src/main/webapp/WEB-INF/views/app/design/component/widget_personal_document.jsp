<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/pt/common/taglibs.jsp" %>
<link href="${ctx }/resources/pt/css/app/design/app_widget_configurer.css" rel="stylesheet">
<div id="widget_personal_document">
    <form id="widget_personal_document_form" class="form-horizontal">
        <div id="widget_personal_document_tabs">
            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation" class="active"><a href="#widget_personal_document_tabs_base_info">基础信息</a></li>
                <li role="presentation"><a href="#widget_personal_document_tabs_nav_info">导航信息</a></li>
            </ul>
            <div class="tab-content" style="height: 500px">
                <div role="tabpanel" class="tab-pane active" id="widget_personal_document_tabs_base_info">
                    <div class="form-group">
                        <label for="name" class="col-sm-2 control-label">名称</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control w-configurer-option" name="name" id="name">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="categoryCode" class="col-sm-2 control-label">分类</label>
                        <div class="col-sm-10">
                            <input type="hidden" class="w-configurer-option" name="categoryName" id="categoryName"/>
                            <input type="text"
                                   class="form-control w-configurer-option" height="34px" id="categoryCode"
                                   name="categoryCode"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="code" class="col-sm-2 control-label">编号</label>
                        <div class="col-sm-10">
                            <input type="text" class="form-control w-configurer-option" height="34px" id="code"
                                   name="code"/>
                        </div>
                    </div>
                </div>
                <div role="tabpanel" class="tab-pane" id="widget_personal_document_tabs_nav_info">
                    <div class="form-group">
                        <label for="nav-menuItems" class="col-sm-0 control-label"></label>
                        <div class="col-sm-12">
                            <div class="nav-menuItems"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>
