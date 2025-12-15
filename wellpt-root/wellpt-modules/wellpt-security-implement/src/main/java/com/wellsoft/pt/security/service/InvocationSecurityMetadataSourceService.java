package com.wellsoft.pt.security.service;

import com.wellsoft.pt.security.access.intercept.FilterSecurityInterceptorImpl;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Resource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author lilin
 * @ClassName: InvocationSecurityMetadataSourceService
 * 最核心的地方，就是提供某个资源对应的权限定义，即getAttributes方法返回的结果。
 * 注意，我例子中使用的是AntUrlPathMatcher这个path matcher来检查URL是否与资源定义匹配，
 * 事实上你还要用正则的方式来匹配，或者自己实现一个matcher。
 * <p>
 * 此类在初始化时，应该取到所有资源及其对应角色的定义
 * <p>
 * 说明：对于方法的spring注入，只能在方法和成员变量里注入，
 * 如果一个类要进行实例化的时候，不能注入对象和操作对象，
 * 所以在构造函数里不能进行操作注入的数据。  这里使用@PostConstruct
 * 方法获取所有的权限定义
 */

@Service
public class InvocationSecurityMetadataSourceService implements FilterInvocationSecurityMetadataSource {

    //	private AcctAuthorityDao authorityDao;
    private RequestMatcher urlMatcher;
    private Map<String, Collection<ConfigAttribute>> resourceMap = null;
    /**
     * @uml.property name="filterSecurityInterceptorImpl"
     * @uml.associationEnd inverse="invocationSecurityMetadataSourceService:com.well.oa.core.security.service.FilterSecurityInterceptorImpl"
     */
    private FilterSecurityInterceptorImpl filterSecurityInterceptorImpl;

    public InvocationSecurityMetadataSourceService() {

    }

    //	@PostConstruct
    public void loadResourceDefine() {
        resourceMap = new HashMap<String, Collection<ConfigAttribute>>();

        List<Privilege> permissionList = new ArrayList<Privilege>();//.getAllPermission();
        for (Privilege auth : permissionList) {
            //通过数据库中的信息设置，resouce和role
            ConfigAttribute ca = new SecurityConfig(auth.getName());// "ROLE_ADMIN"

            for (Resource res : auth.getResources()) {
                String url = res.getUrl();
                //把资源放入到spring security的resouceMap中
                //形式如下： "log.jsp","role_user,role_admin"
                if (resourceMap.containsKey(url)) {
                    Collection<ConfigAttribute> value = resourceMap.get(url);
                    value.add(ca);
                } else {
                    Collection<ConfigAttribute> atts = new ArrayList<ConfigAttribute>();
                    atts.add(ca);
                    resourceMap.put(url, atts);
                }
            }
        }
    }

    /**
     * <p>Title: getAttributes</p>
     * <p>Description: 根据URL，找到相关的权限配置。</p>
     *
     * @param object 是一个URL，被用户请求的url
     * @return
     * @throws IllegalArgumentException
     * @see org.springframework.security.access.SecurityMetadataSource#getAttributes(java.lang.Object)
     */
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        loadResourceDefine();
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
        //		int firstQuestionMarkIndex = url.indexOf("?");
        //
        //		if (firstQuestionMarkIndex != -1) {
        //			url = url.substring(0, firstQuestionMarkIndex);
        //		}

        Iterator<String> ite = resourceMap.keySet().iterator();
        while (ite.hasNext()) {
            String resURL = ite.next();
            //根据数据库中存储的权限路径模式和访问的路径进行匹配
            urlMatcher = new AntPathRequestMatcher(resURL);
            if (urlMatcher.matches(request)) {
                return resourceMap.get(resURL);
            }
        }
        return null;
    }

    public boolean supports(Class<?> clazz) {
        return true;
    }

    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    /**
     * Getter of the property <tt>filterSecurityInterceptorImpl</tt>
     *
     * @return Returns the filterSecurityInterceptorImpl.
     * @uml.property name="filterSecurityInterceptorImpl"
     */
    public FilterSecurityInterceptorImpl getFilterSecurityInterceptorImpl() {
        return filterSecurityInterceptorImpl;
    }

    /**
     * Setter of the property <tt>filterSecurityInterceptorImpl</tt>
     *
     * @param filterSecurityInterceptorImpl The filterSecurityInterceptorImpl to set.
     * @uml.property name="filterSecurityInterceptorImpl"
     */
    public void setFilterSecurityInterceptorImpl(FilterSecurityInterceptorImpl filterSecurityInterceptorImpl) {
        this.filterSecurityInterceptorImpl = filterSecurityInterceptorImpl;
    }

}
