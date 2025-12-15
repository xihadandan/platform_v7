package com.wellsoft.pt.common.home.service;

import com.wellsoft.pt.common.home.service.vo.DataSourceVo;

import java.util.List;

/**
 * @author yt
 * @title: AdminHomeService
 * @date 2020/7/20 16:51
 * <p>
 * 管理后台 主页相关统计数据
 */
public interface AdminHomeService {

    /**
     * 数据概览
     */
    public List<DataSourceVo> dataOverview();

}
