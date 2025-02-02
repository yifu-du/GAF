/*
 * Copyright© 2000 - 2021 SuperMap Software Co.Ltd. All rights reserved.
 * This program are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at http://www.apache.org/licenses/LICENSE-2.0.html.
*/
package com.supermap.gaf.portal.resources;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.supermap.gaf.commontypes.ShiroUser;
import com.supermap.gaf.shiro.SecurityUtilsExt;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.supermap.gaf.portal.menu.service.CustomationServices;

import io.swagger.annotations.Api;

/**
* @author:yw
 * @date:2021/3/25
* @Date 2021-3-12
**/
@Api(value = "门户订制和用户授权菜单信息查询接口",hidden = true)
public class CustomationsResource {
    @Autowired
    private CustomationServices customationServices;

    private  Logger log = LoggerFactory.getLogger(CustomationsResource.class);
    @ApiOperation(value = "门户订制查询",notes = "查询登录者所属租户的门户订制信息和用户授权菜单信息",hidden = true)
    @Path("")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String queryCustomation() {
        ShiroUser currentUser = SecurityUtilsExt.getUser();
        return customationServices.queryCustomation(currentUser.getTenantId());
    }

}
