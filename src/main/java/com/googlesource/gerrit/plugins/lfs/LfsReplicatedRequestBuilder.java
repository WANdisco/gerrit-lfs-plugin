package com.googlesource.gerrit.plugins.lfs;

import com.wandisco.gerrit.gitms.shared.util.HttpRequestBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Builder style class to allow HTTP requests to be built up and sent to GitMS from gerrit.
 * @author ronanconway
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
      final long objectSize, final String lfsDataDir, final String lfsContentDeliveryFile) throws IOException {

    requestBuilder = new HttpRequestBuilder(host, port, lfsEndpoint);

    // Set our default request type and accept response type.
    requestBuilder.setRequestParameter("Content-Type", "application/json");
    requestBuilder.setRequestParameter("Accept", "application/json; charset=utf-8;");

    // now request specific info.
    requestBuilder.setRequestParameter("projectName", projectName);
    requestBuilder.setRequestParameter("lfsObjectOID", objectId);
    requestBuilder.setRequestParameter("lfsObjectSize", Long.toString(objectSize));
    requestBuilder.setRequestParameter("lfsDataDir", lfsDataDir);
    requestBuilder.setRequestParameter("lfsContentDeliveryFile", lfsContentDeliveryFile);

    // build the entire request ready for issuing.
    requestBuilder.buildRequest();
  }



  /**
   * Gets the status code from an HTTP response message.
   *
   * @return
   * @throws Exception
   */
  public int getHttpResponseCode() throws Exception {
    return requestBuilder.getHttpResponseCode();
  }

  /**
   * Gets the response text from the HTTP Response message, either
   * from the input stream for success or the error stream for failures.
   *
   * @return
   * @throws IOException
   */
  public String getHttpResponseMessage() throws IOException {
    return requestBuilder.getHttpResponseMessage();
  }

  /**
   * Returns the error stream if the connection failed.
   *
   * @return
   * @throws IOException
   */
  public InputStream getHttpErrorStream() throws IOException {
    return requestBuilder.getHttpErrorStream();
  }

  /**
   * Releases the connection.
   */
  public void disconnect() {
    if (requestBuilder != null) {
      requestBuilder.disconnect();
    }
  }


}
