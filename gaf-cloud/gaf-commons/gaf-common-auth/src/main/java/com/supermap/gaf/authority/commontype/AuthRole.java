/*
 * Copyright© 2000 - 2021 SuperMap Software Co.Ltd. All rights reserved.
 * This program are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at http://www.apache.org/licenses/LICENSE-2.0.html.
*/
package com.supermap.gaf.authority.commontype;

import com.alibaba.fastjson.annotation.JSONField;
import com.supermap.gaf.annotation.LogicDeleteField;
import com.supermap.gaf.annotation.ParentIdField;
import com.supermap.gaf.annotation.SortSnField;
import com.supermap.gaf.annotation.UpdatedTimeField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 角色
 *
 * @date:2021/3/25
 * @author zhm
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("角色")
public class AuthRole implements Serializable {

    public static final AuthRole TENANT_ADMIN = new AuthRole("role_000001");
    public static final AuthRole PLATFORM_ADMIN = new AuthRole("role_000000");


    public AuthRole(String roleId) {
        this.roleId = roleId;
    }

    @ApiModelProperty("角色id")
    @Id
    private String roleId;
    @ApiModelProperty("租户id")
    private String tenantId;
    @ApiModelProperty("角色分组id")
    @ParentIdField
    private String roleCatalogId;
    @ApiModelProperty(value = "排序序号", example = "1", allowableValues = "range[1,infinity]")
    @SortSnField
    private Integer sortSn = 1;
    @ApiModelProperty(value = "名称", example = "项目开发人员")
    private String roleName;
    @ApiModelProperty(value = "英文名称", example = "project-developer")
    private String nameEn;
    @ApiModelProperty(value = "编码。暂时无用")
    private String code;
    @ApiModelProperty("状态。逻辑删除字段")
    @JSONField(name = "isStatus")
    @LogicDeleteField
    private Boolean status = true;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("创建时间")
    private Date createdTime;
    @ApiModelProperty("创建人")
    private String createdBy;
    @ApiModelProperty("修改时间")
    @UpdatedTimeField
    private Date updatedTime;
    @ApiModelProperty("修改人")
    private String updatedBy;
    @ApiModelProperty(value = "类别.1:组件内置（租户可见），2：平台级（租户不可见），3：租户级", example = "1", allowableValues = "1,2,3")
    private String type;

}
