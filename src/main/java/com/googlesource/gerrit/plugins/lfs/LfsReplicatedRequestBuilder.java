package com.googlesource.gerrit.plugins.lfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Builder style class to allow HTTP requests to be built up and sent to GitMS from gerrit.
 * @author ronanconway
 */
public class LfsReplicatedRequestBuilder {

  private String host;
  private String port;
  private final String lfsEndpoint = "/gerrit/lfs";
  private URL url;
  private HttpURLConnection httpURLConnection;
  private String lfsObjectOID;
  private String lfsObjectSize;
  private String projectName;
  private String lfsDataDir;
  private String lfsContentDeliveryFile;

  public LfsReplicatedRequestBuilder(final String host, final String port) {
    this.host = host;
    this.port = port;
  }

  public LfsReplicatedRequestBuilder setProjectName(final String projectName){
    this.projectName = "projectName=" + projectName;
    return this;
  }

  public LfsReplicatedRequestBuilder setLfsDataDir(final String lfsDataDir){
    this.lfsDataDir = "lfsDataDir=" + lfsDataDir;
    return this;
  }

  public LfsReplicatedRequestBuilder setLfsObjectOID(final String lfsObjectOID){
    this.lfsObjectOID = "lfsObjectOID=" + lfsObjectOID;
    return this;
  }

  public LfsReplicatedRequestBuilder setLfsObjectSize(final long lfsObjectSize){
    this.lfsObjectSize = "lfsObjectSize=" + lfsObjectSize;
    return this;
  }

  public LfsReplicatedRequestBuilder setLfsContentDeliveryPath(final String lfsContentDeliveryFile){
    this.lfsContentDeliveryFile = "lfsContentDeliveryFile=" + lfsContentDeliveryFile;
    return this;
  }

  /**
   * Building the URL with the lfs request
   * information to send to GitMS /lfs endpoint
   * @return
   */
  public LfsReplicatedRequestBuilder setRequestURI(){
    try {
      String requestData = this.lfsEndpoint
          + "?" + this.lfsDataDir
          + "&" + this.projectName
          + "&" + this.lfsObjectOID
          + "&" + this.lfsObjectSize
          + "&" + this.lfsContentDeliveryFile;
      this.url = new URL("http", host, Integer.parseInt(port), requestData);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return this;
  }

  /**
   * Open the connection and set the request properties.
   * @return
   * @throws IOException
   */
  public LfsReplicatedRequestBuilder setHttpConnection() throws IOException {
    this.httpURLConnection = (HttpURLConnection) this.url.openConnection();
    httpURLConnection.setDoOutput(true);
    httpURLConnection.setDoInput(true);
    httpURLConnection.setUseCaches(false);
    httpURLConnection.setRequestMethod("PUT");
    httpURLConnection.setRequestProperty("Content-Type", "application/xml");
    httpURLConnection.setRequestProperty("Accept", "application/json; charset=utf-8;");
    return this;
  }

  /**
   *
   * @return
   */
  public URL getUrl() {
    return url;
  }

  /**
   * Gets the status code from an HTTP response message.
   * @return
   * @throws IOException
   */
  public int getHttpResponseCode() throws IOException {
    return this.httpURLConnection.getResponseCode();
  }

  /**
   * Returns the error stream if the connection failed.
   * @return
   * @throws IOException
   */
  public InputStream getHttpErrorStream() throws IOException {
    return this.httpURLConnection.getErrorStream();
  }

  /**
   * Releases the connection.
   */
  public void disconnect(){
    if(httpURLConnection!= null) {
      this.httpURLConnection.disconnect();
    }
  }

  /**
   * Return the LfsReplicatedRequestBuilder object
   * @return
   */
  public LfsReplicatedRequestBuilder buildRequest() {
    return this;
  }
}
