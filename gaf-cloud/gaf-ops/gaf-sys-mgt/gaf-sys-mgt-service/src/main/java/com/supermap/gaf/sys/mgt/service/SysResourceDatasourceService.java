/*
 * Copyright© 2000 - 2021 SuperMap Software Co.Ltd. All rights reserved.
 * This program are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at http://www.apache.org/licenses/LICENSE-2.0.html.
*/
package com.supermap.gaf.sys.mgt.service;

import com.supermap.gaf.commontypes.Page;
import com.supermap.gaf.commontypes.tree.DefaultTreeNode;
import com.supermap.gaf.sys.mgt.commontype.SysResourceDatasource;
import com.supermap.gaf.sys.mgt.vo.SysResourceDatasourceSelectVo;

import java.util.List;

/**
 * 数据源服务类
 * @author wangxiaolong
 * @date:2021/3/25
 * 
 */
public interface SysResourceDatasourceService {

    /**
     * 根据id查询数据源
     * @param datasourceId 数据源id
     * @return  数据源 若未查询到则返回null
     */
    SysResourceDatasource getById(String datasourceId);
	
	/**
     * 分页条件查询
     * @param sysResourceDatasourceSelectVo 查询条件
     * @param pageNum 当前页数
     * @param pageSize 页面大小
     * @return 分页对象
     */
	Page<SysResourceDatasource> listByPageCondition(SysResourceDatasourceSelectVo sysResourceDatasourceSelectVo, int pageNum, int pageSize);


    /**
     * 新增数据源
     * @param sysResourceDatasource 数据源
     * @return 数据源
     */
    SysResourceDatasource insertSysResourceDatasource(SysResourceDatasource sysResourceDatasource);

    /**
     * 批量插入数据源
     * @param sysResourceDatasources 数据源集合
     */
    void batchInsert(List<SysResourceDatasource> sysResourceDatasources);

    /**
     * 根据数据源id删除数据源
     * @param datasourceId  数据源id
     */
    void deleteSysResourceDatasource(String datasourceId);

    /**
     * 批量删除
     * @param datasourceIds 数据源id集合
     */
    void batchDelete(List<String> datasourceIds);
    /**
     * 更新数据源
     * @param sysResourceDatasource 数据源
     * @return 数据源
     */
    SysResourceDatasource updateSysResourceDatasource(SysResourceDatasource sysResourceDatasource);

    /**
     * 按数据源类型查询所数据源可选项
     * @param typeCodes 数据源类型集合
     * @return 若未查询到则返回空集合
     */
    List<SysResourceDatasource> getDatasources(List<String> typeCodes);

    /**
     * 查询空间数据源树
     * @return 空间数据源树节点
     */
    List<DefaultTreeNode> getTree();
}
