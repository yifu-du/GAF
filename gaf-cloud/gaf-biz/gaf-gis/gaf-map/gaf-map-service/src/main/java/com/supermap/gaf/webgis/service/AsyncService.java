/*
 * Copyright© 2000 - 2021 SuperMap Software Co.Ltd. All rights reserved.
 * This program are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution and is available at http://www.apache.org/licenses/LICENSE-2.0.html.
*/
package com.supermap.gaf.webgis.service;

import com.supermap.gaf.webgis.entity.WebgisService;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @date:2021/3/25
 * @author heykb
 */
@Component
public interface AsyncService {

    CompletableFuture<Void> batchRegistryWebgis(WebgisService webgisService, String type, String resultCode);
}
