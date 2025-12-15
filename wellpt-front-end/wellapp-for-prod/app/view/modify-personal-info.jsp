<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/pt/common/taglibs.jsp"%>
<form class="form-horizontal" role="form" id="personalInfoSetting" data-user-json='${userJson}'>
	<div class="form-group">
		<input type="hidden" value="${user.uuid}" name="userUuid" />
	</div>
	<div class="form-group">
		<label class="col-sm-3 control-label">用户</label>
		<div class="col-sm-9">
			<input type="text" class="form-control" name="userName" value="${user.userName}" disabled />
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-3 control-label">性别</label>
		<div class="col-sm-9">
			<label class="radio-inline"> <input type="radio" name="sex" value="1" <c:if test="${user.sex eq '1'}">checked="checked"</c:if>/>
				男
			</label> <label class="radio-inline"> <input type="radio" name="sex" value="2" <c:if test="${user.sex eq '2'}">checked="checked"</c:if>/>
				女
			</label>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-3 control-label">身份证</label>
		<div class="col-sm-9">
			<input type="text" class="form-control" name="idNumber" value="${user.idNumber}" />
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-3 control-label">手机<font color="red">*</font></label>
		<div class="col-sm-5">
			<input type="text" class="form-control" name="mobilePhone" value="${user.mobilePhone}" />
		</div>
		<!-- 
		<div class="col-sm-4">
			<label><input type="checkbox" name="receiveSmsMessage"/>接收短消息</label>
		</div>
		 -->
	</div>
	<div class="form-group">
		<label class="col-sm-3 control-label">家庭电话</label>
		<div class="col-sm-9">
			<input type="text" class="form-control" name="homePhone" value="${user.homePhone}" />
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-3 control-label">办公电话/分机<font color="red">*</font></label>
		<div class="col-sm-9">
			<input type="text" class="form-control" name="officePhone" value="${user.officePhone}" />
		</div>
	</div>
	<div class="form-group hidden">
		<label class="col-sm-3 control-label">传真</label>
		<div class="col-sm-9">
			<input type="text" class="form-control" name="fax" value="${user.fax}" />
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-3 control-label">邮件(主)</label>
		<div class="col-sm-9">
			<input type="text" class="form-control" name="mainEmail" value="${user.mainEmail}" />
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-3 control-label">照片</label>
		<div class="col-sm-9">
			<input type="hidden" class="form-control" name="photoUuid" value="${user.photoUuid}" /> <img
				class="img-responsive img-rounded pull-left" id="personalInfoPhotoImage" src="${ctx}/org/user/view/photo/${user.photoUuid}"
				style="background-color: #ddd; max-width: 100px; max-height: 100px; display: inline" />
			<button type="button" class="btn btn-primary pull-right" id="personalInfoPhotoSelectBtn">选择图片</button>
		</div>
	</div>	
</form>