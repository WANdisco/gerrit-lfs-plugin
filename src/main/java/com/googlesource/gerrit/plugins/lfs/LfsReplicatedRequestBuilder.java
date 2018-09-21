
/********************************************************************************
 * Copyright (c) 2014-2018 WANdisco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Apache License, Version 2.0
 *
 ********************************************************************************/
 
package com.googlesource.gerrit.plugins.lfs;

import com.wandisco.gerrit.gitms.shared.api.ApiResponse;
import com.wandisco.gerrit.gitms.shared.api.HttpRequestBuilder;

/**
 * Builder style class to allow HTTP requests to be built up and sent to GitMS from gerrit.
 * @author ronanconway
 * @author trevorgetty
 */
public class LfsReplicatedRequestBuilder {

  private final String lfsEndpoint = "/gerrit/lfs-content";
  private final HttpRequestBuilder requestBuilder;



  /**
   * @param host                   rest api endpoint host ( authenticated )
   * @param port                   rest api endpoint port
   * @param projectName            LFS project name where content is to be added to.
   * @param objectId               LFS Object unique ID.
   * @param objectSize             LFS Object file size.
   * @param lfsDataDir             LFS Object final Data storage location on disk ( Backend ).
   * @param lfsContentDeliveryFile LFS Temporary file name while in Content Delivery.
   */
  public LfsReplicatedRequestBuilder(final String host, final int port, final String projectName, final String objectId,
      final long objectSize, final String lfsDataDir, final String lfsContentDeliveryFile) {

    requestBuilder = new HttpRequestBuilder(host, port, lfsEndpoint);

    // Set our accept response type.
    requestBuilder.setHeader("Accept", "application/json; charset=utf-8;");

    // now request specific info.
    requestBuilder.setRequestParameter("projectName", projectName);
    requestBuilder.setRequestParameter("lfsObjectOID", objectId);
    requestBuilder.setRequestParameter("lfsObjectSize", Long.toString(objectSize));
    requestBuilder.setRequestParameter("lfsDataDir", lfsDataDir);
    requestBuilder.setRequestParameter("lfsContentDeliveryFile", lfsContentDeliveryFile);
  }

  /**
   * Issue the Rest Api request to the setup endpoint with whatever query or body parameters which are set.
   * @return
   */
  public ApiResponse issueRequest(){
    return requestBuilder.makeRequest();
  }

}
