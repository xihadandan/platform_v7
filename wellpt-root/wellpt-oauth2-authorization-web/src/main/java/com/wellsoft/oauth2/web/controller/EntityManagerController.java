package com.wellsoft.oauth2.web.controller;

import com.google.common.base.Throwables;
import com.wellsoft.oauth2.entity.BaseEntity;
import com.wellsoft.oauth2.repository.GeneralJpaRepository;
import com.wellsoft.oauth2.utils.AjaxUtils;
import com.wellsoft.oauth2.web.support.BasicResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description: 实体类管理控制层
 *
 * @author chenq
 * @date 2019/9/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/10    chenq		2019/9/10		Create
 * </pre>
 */
public class EntityManagerController<T extends BaseEntity> extends AbstractController<T> {
    @Resource
    protected GeneralJpaRepository generalJpaRepository;

    @Override
    public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
        String msg = toIndex(model, request, response, requestMapping() + "/index");
        return msg;
    }

    protected String toIndex(Model model, HttpServletRequest request, HttpServletResponse response,
                             String defaultView) {
        return defaultView;
    }


    @Override
    public String _new(Model model, T entity, HttpServletRequest request,
                       HttpServletResponse response) {
        String msg = toNew(model, entity, request, response, requestMapping() + "/new");
        return msg;
    }

    protected String toNew(Model model, T entity, HttpServletRequest request,
                           HttpServletResponse response,
                           String defaultView) {
        model.addAttribute("entity", entity);
        return defaultView;
    }

    @Override
    public String show(Model model, @PathVariable Long uuid, HttpServletRequest request,
                       HttpServletResponse response) {
        String msg = toShow(model, uuid, request, response, requestMapping() + "/show");
        return msg;
    }

    protected String toShow(Model model, Long uuid, HttpServletRequest request,
                            HttpServletResponse response,
                            String defaultView) {
        model.addAttribute("entity", toModel(uuid));
        return defaultView;
    }

    @Override
    public String edit(Model model, @PathVariable Long uuid, HttpServletRequest request,
                       HttpServletResponse response) {
        String msg = toEdit(model, uuid, request, response, requestMapping() + "/show");
        return msg;
    }

    protected String toEdit(Model model, Long uuid, HttpServletRequest request,
                            HttpServletResponse response,
                            String defaultView) {
        model.addAttribute("entity", toModel(uuid));
        return defaultView;
    }

    @Override
    public ResponseEntity create(Model model, RedirectAttributes redirectAttributes,
                                 @RequestBody @Validated T entity,
                                 BindingResult errors,
                                 HttpServletRequest request, HttpServletResponse response) {
        ResponseEntity responseEntity = ResponseEntity.ok(null);
        try {
            responseEntity = toCreate(model, redirectAttributes, entity, errors, request, response);
            redirectAttributes.addFlashAttribute(STATUS_CODE,
                    AjaxUtils.STATUS_CODE_SUCCESS);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(STATUS_CODE,
                    AjaxUtils.STATUS_CODE_FAILURE);
            throw Throwables.propagate(e);
        }
        return responseEntity;
    }

    protected ResponseEntity toCreate(Model model, RedirectAttributes redirectAttributes, T entity,
                                      BindingResult errors,
                                      HttpServletRequest request, HttpServletResponse response) {

        generalJpaRepository.save(entity);
        return ResponseEntity.ok(BasicResponse.success().setData(entity));
    }

    @Override
    public @ResponseBody
    ResponseEntity update(RedirectAttributes model, @RequestBody @Validated T entity,
                          BindingResult errors,
                          HttpServletRequest request, HttpServletResponse response) {
        T t = null;
        try {
            t = toUpdate(model, entity, errors, request, response);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
        return ResponseEntity.ok(BasicResponse.success().setData(t));
    }

    protected T toUpdate(RedirectAttributes model, T entity, BindingResult errors,
                         HttpServletRequest request, HttpServletResponse response) {
        generalJpaRepository.save(entity);
        return entity;
    }

    @Override
    public @ResponseBody
    ResponseEntity delete(RedirectAttributes model, @PathVariable Long uuid, T entity,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        try {
            generalJpaRepository.delete(uuid, entityClass);
        } catch (Exception e2) {
            throw Throwables.propagate(e2);
        }
        return ResponseEntity.ok(BasicResponse.success());
    }

    protected String toDelete(RedirectAttributes model, Long uuid, T entity,
                              HttpServletRequest request,
                              HttpServletResponse response, String defaultView) {
        if (null != entity && entity.getUuid() != null) {
            generalJpaRepository.delete(uuid, entityClass);
        }
        return defaultView;
    }

    @Override
    public @ResponseBody
    ResponseEntity batchDelete(RedirectAttributes model, @RequestParam("items[]") Long[] items,
                               HttpServletRequest request, HttpServletResponse response) {
        try {
            toBatchDelete(model, items, request, response, null);
        } catch (Exception e) {
            model.addFlashAttribute(STATUS_CODE, AjaxUtils.STATUS_CODE_FAILURE);
            throw Throwables.propagate(e);
        }
        return ResponseEntity.ok(BasicResponse.success());
    }


    protected String toBatchDelete(RedirectAttributes model, Long[] items,
                                   HttpServletRequest request,
                                   HttpServletResponse response, String defaultView) {
        if (null != items && items.length > 0) {
            generalJpaRepository.delete(items, entityClass);
        }
        return null;
    }

    public String requestMapping() {
        RequestMapping requestMapping = this.getClass().getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            if (requestMapping.value().length <= 0) {
                return "";
            }
            return requestMapping.value()[0];
        }
        return "";
    }

    protected T toModel(Long uuid) {
        return (T) generalJpaRepository.findOne(uuid, entityClass);
    }


    /**
     * 返回分页数据
     *
     * @param model
     * @param dataTable
     * @param request
     * @param response
     * @return
     */
    @Override
    public DataTable.PageJsonInfo pageData(Model model, DataTable dataTable,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        Page page = generalJpaRepository.findAll(filter(model, request), dataTable.toPageRequest(),
                entityClass);
        pageCallback(page);
        return new DataTable.PageJsonInfo(page);
    }

    @Override
    protected void pageCallback(Page page) {

    }
}
