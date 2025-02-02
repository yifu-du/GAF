/*
 * Copyright© 2000 - 2021 SuperMap Software Co.Ltd. All rights reserved.
 * This program are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at http://www.apache.org/licenses/LICENSE-2.0.html.
*/
package com.supermap.gaf.webgis.entity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;
import javax.validation.constraints.*;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * GIS数据服务字段
 * @author wangxiaolong 
 * @date yyyy-mm-dd
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("GIS数据服务字段")
public class WebgisDataServiceField implements Serializable{
    @NotNull
    @ApiModelProperty("GIS服务字段id")
    private String gisServiceFieldId;
    @NotNull
    @ApiModelProperty("GIS数据服务")
    private String gisDataServiceId;
    @ApiModelProperty("字段名称")
    private String fieldName;
    @ApiModelProperty("字段别名")
    private String fieldAlias;
    @ApiModelProperty("字段类型")
    private String fieldTypeCode;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("状态")
    @JSONField(name="isStatus")
    private Boolean status;
    @ApiModelProperty("创建时间")
    private Date createdTime;
    @ApiModelProperty("创建人")
    private String createdBy;
    @ApiModelProperty("修改时间")
    private Date updatedTime;
    @ApiModelProperty("修改人")
    private String updatedBy;
}
