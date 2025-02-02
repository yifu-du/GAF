/*
 * Copyright© 2000 - 2021 SuperMap Software Co.Ltd. All rights reserved.
 * This program are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at http://www.apache.org/licenses/LICENSE-2.0.html.
*/
package com.supermap.gaf.storage.entity;

import lombok.Data;

/**
 * @date:2021/3/25
 * @author heykb
 */
@Data
public class PresignUploadRequest {
    private String contentMd5;
    private String presignUrl;


    public PresignUploadRequest(String presignUrl) {
        this.presignUrl = presignUrl;
    }

    public PresignUploadRequest(String presignUrl, String contentMd5) {
        this.contentMd5 = contentMd5;
        this.presignUrl = presignUrl;
    }
}
