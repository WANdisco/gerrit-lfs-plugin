
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

// Copyright (C) 2015 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.lfs.fs;

import static org.eclipse.jgit.lfs.lib.Constants.DOWNLOAD;
import static org.eclipse.jgit.lfs.lib.Constants.UPLOAD;
import static org.eclipse.jgit.util.HttpSupport.HDR_AUTHORIZATION;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;

import com.google.common.base.Strings;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.googlesource.gerrit.plugins.lfs.ContentDeliveryObjectUploader;
import com.googlesource.gerrit.plugins.lfs.fs.LfsFsRequestAuthorizer;
import com.googlesource.gerrit.plugins.lfs.fs.LocalLargeFileRepository;
import com.wandisco.gerrit.gitms.shared.util.ReplicationUtils;
import org.apache.http.HttpStatus;
import org.eclipse.jgit.lfs.errors.GitMSException;
import org.eclipse.jgit.lfs.errors.InvalidLongObjectIdException;
import org.eclipse.jgit.lfs.lib.AnyLongObjectId;
import org.eclipse.jgit.lfs.lib.Constants;
import org.eclipse.jgit.lfs.lib.LongObjectId;
import org.eclipse.jgit.lfs.server.internal.LfsServerText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.eclipse.jgit.lfs.lib.AnyLongObjectId;
import org.eclipse.jgit.lfs.server.fs.FileLfsServlet;
import org.eclipse.jgit.lfs.server.fs.ObjectDownloadListener;
import org.eclipse.jgit.lfs.server.fs.ObjectUploadListener;
import org.eclipse.jgit.lfs.server.internal.LfsServerText;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

import static com.googlesource.gerrit.plugins.lfs.fs.LocalLargeFileRepository.DOWNLOAD;
import static com.googlesource.gerrit.plugins.lfs.fs.LocalLargeFileRepository.UPLOAD;
import static com.wandisco.api.lfs.LfsReplicateContent.replicateLfsData;
import static com.wandisco.utils.StringUtils.getUniqueString;
import static org.eclipse.jgit.util.HttpSupport.HDR_AUTHORIZATION;

public class LfsFsContentServlet extends FileLfsServlet {
  public interface Factory {<<<<<<< HEAD:src/main/java/com/googlesource/gerrit/plugins/lfs/fs/LfsFsContentServlet.java
  public LfsFsContentServlet(
      LfsFsRequestAuthorizer authorizer, @Assisted LocalLargeFileRepository repository) {
=======
    LfsFsContentServlet create(LocalLargeFileRepository largeFileRepository);
  }

  private static final long serialVersionUID = 1L;

  private final LfsFsRequestAuthorizer authorizer;
  private final LocalLargeFileRepository repository;
  private final long timeout;
  private static Gson gson = createGson();

  Logger log = LoggerFactory.getLogger(LfsFsContentServlet.class);

  @Inject
  public LfsFsContentServlet(LfsFsRequestAuthorizer authorizer,
                             @Assisted LocalLargeFileRepository repository) {
    super(repository, 0);
    this.authorizer = authorizer;
    this.repository = repository;
    this.timeout = 0;
  }


  @Override
<<<<<<< HEAD:src/main/java/com/googlesource/gerrit/plugins/lfs/fs/LfsFsContentServlet.java
  protected void doHead(HttpServletRequest req, HttpServletResponse rsp)
      throws ServletException, IOException {
    String verifyId = req.getHeader(HttpHeaders.IF_NONE_MATCH);
    if (Strings.isNullOrEmpty(verifyId)) {
      doGet(req, rsp);
=======
  protected void doGet(HttpServletRequest req, HttpServletResponse rsp)
      throws IOException {


    AnyLongObjectId obj = getObjectToTransfer(req, rsp);
    if (obj == null) {
>>>>>>> stable-2.13_WD-Rep-Patch:src/main/java/org/eclipse/jgit/lfs/server/fs/LfsFsContentServlet.java
      return;
    }

    Optional<AnyLongObjectId> obj = validateGetRequest(req, rsp);
    if (obj.isPresent() && obj.get().getName().equalsIgnoreCase(verifyId)) {
      rsp.addHeader(HttpHeaders.ETAG, obj.get().getName());
      rsp.setStatus(HttpStatus.SC_NOT_MODIFIED);
      return;
    }

    getObject(req, rsp, obj);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse rsp)
      throws ServletException, IOException {
    Optional<AnyLongObjectId> obj = validateGetRequest(req, rsp);
    getObject(req, rsp, obj);
  }

  /**
   * Servlet doPut, the request to upload the object is intercepted and the object
   * is placed in the content delivery directory of GitMS. Following this, GitMS
   * replicates the objects to the other node replicas. Previously the doPut upload
   * was asynchronous but now performs the uploads in a synchronous fashion.
   * @param req
   * @param rsp
   * @throws ServletException
   * @throws IOException
   */
  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse rsp)
      throws IOException {
    AnyLongObjectId id = getObjectToTransfer(req, rsp);
    if (id == null) {
      return;
    }

    if (!authorizer.verifyAuthInfo(req.getHeader(HDR_AUTHORIZATION), UPLOAD, id)) {
      sendError(
          rsp,
          HttpStatus.SC_UNAUTHORIZED,
          MessageFormat.format(
              LfsServerText.get().failedToCalcSignature, "Invalid authorization token"));
      return;
    }

<<<<<<< HEAD:src/main/java/com/googlesource/gerrit/plugins/lfs/fs/LfsFsContentServlet.java
    AsyncContext context = req.startAsync();
    context.setTimeout(timeout);
    req.getInputStream()
        .setReadListener(new ObjectUploadListener(repository, context, req, rsp, id));
  }

  private Optional<AnyLongObjectId> validateGetRequest(
      HttpServletRequest req, HttpServletResponse rsp) throws IOException {
    AnyLongObjectId obj = getObjectToTransfer(req, rsp);
    if (obj == null) {
      return Optional.empty();
=======
    /* The path to GitMS content delivery which is passed to the ContentDeliveryObjectUploader
     * so It has somewhere to write the byte stream to disk.
     */
    final String repositoryProjectName = repository.getProjectName();
    final String repositoryIdentity = repository.getProjectIdentity();

    // validate that the repository has replication info on it, or we should be in here.
    // This is a WD version of this method and it should have used getLargeFileRepository which sets this obj up.
    if (Strings.isNullOrEmpty(repositoryProjectName)) {
      sendError(rsp, HttpStatus.SC_INTERNAL_SERVER_ERROR, "Missing LFS project name, when uploading content.");
      return;
    }

    // validate that the repository has replication info on it, or we should be in here.
    if (Strings.isNullOrEmpty(repositoryIdentity)) {
      sendError(rsp, HttpStatus.SC_INTERNAL_SERVER_ERROR, "Missing LFS project identity, when uploading content.");
      return;
    }

    // Create a unique name from this fixed ID, so that retries using the same content DO NOT collide with an existing CD upload.
    // e.g. upload oid 1, and if we didn't append a unique id, a retry would try to overwrite oid 1 in the CD location which can collide
    // with existing proposals.
    final String lfsUniqueName = getUniqueString(id.getName(), ".lfsdata", true);
    final Path cdRepoNameSpace = ReplicationUtils.getCDRepoNameSpace(repositoryProjectName, repositoryIdentity);

    // Resolve the unique location which is this lfs object inside our specific repos CD location.
    final Path contentDeliveryPath = Paths.get(cdRepoNameSpace.toFile().getPath(),  lfsUniqueName);

    /*
     * Writes the LFS object to the specified path on disk, in this case the path specified
     * is the content delivery location in GitMS. Once the disk write is completed the
     * channels will be closed as the class implements AutoClosable. If there is a failure and content is only
     * partially written then we need to clean up.
     */
    try(ContentDeliveryObjectUploader contentUploader =
            new ContentDeliveryObjectUploader(contentDeliveryPath, req.getInputStream())) {
      contentUploader.onDataAvailable();
    } catch (IOException e){
        log.error("Failed to fully write LFS object to the " +
            "content delivery folder of GitMS, deleting any partially written content");
        contentDeliveryPath.toFile().delete();
    }

    /*
     * Making the request to GitMS to replicate the object. If there is an error on the GitMS side then
     * the response will be passed back to the client via the sendError method.
     */

    try {
        log.info("Making request to GitMS to replicate the LFS Object");

        replicateLfsData(repository.getBackend().getName(),
            contentDeliveryPath.toFile(),
            repository.getProjectName(),
            id);
    } catch (GitMSException | IOException e) {
      sendError(rsp, HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    } catch (Exception e) {
      sendError(rsp, HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  /*
  * This method serialises the JSON response and ensure that the response is written to the stream
  * with a content type as application/json.
  */
  static void sendError(HttpServletResponse rsp, int status, String message) throws IOException {
    rsp.setContentType("application/json");
    rsp.setStatus(status);
    PrintWriter writer = rsp.getWriter();
    gson.toJson(new FileLfsServlet.Error(message), writer);
    writer.flush();
    writer.close();
    rsp.flushBuffer();
  }

  /*
   * Using GsonBuilder because configuration options are required other than the default.
   * setFieldNamingPolicy, configures Gson to apply a specific naming policy to an
   * object's field during serialization and deserialization.
   */
  private static Gson createGson() {
    GsonBuilder gb = (new GsonBuilder())
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().disableHtmlEscaping();
    return gb.create();
  }

  /*
   * Error object passed to the gson.toJson methods appendable writer,
   * contains the error message for the client.
   */
  static class Error {
    String message;
    Error(String m) {
      this.message = m;
    }
  }

  private AnyLongObjectId getObjectToTransfer(HttpServletRequest req,
                                              HttpServletResponse rsp) throws IOException {
    String info = req.getPathInfo();
    if (info.length() != 1 + Constants.LONG_OBJECT_ID_STRING_LENGTH) {
      sendError(rsp, HttpStatus.SC_UNPROCESSABLE_ENTITY,
          MessageFormat.format(LfsServerText.get().invalidPathInfo, info));
      return null;
>>>>>>> stable-2.13_WD-Rep-Patch:src/main/java/org/eclipse/jgit/lfs/server/fs/LfsFsContentServlet.java
    }

    if (repository.getSize(obj) == -1) {
      sendError(
          rsp,
          HttpStatus.SC_NOT_FOUND,
          MessageFormat.format(LfsServerText.get().objectNotFound, obj.getName()));
      return Optional.empty();
    }

    if (!authorizer.verifyAuthInfo(req.getHeader(HDR_AUTHORIZATION), DOWNLOAD, obj)) {
      sendError(
          rsp,
          HttpStatus.SC_UNAUTHORIZED,
          MessageFormat.format(
              LfsServerText.get().failedToCalcSignature, "Invalid authorization token"));
      return Optional.empty();
    }
    return Optional.of(obj);
  }

  private void getObject(
      HttpServletRequest req, HttpServletResponse rsp, Optional<AnyLongObjectId> obj)
      throws IOException {
    if (obj.isPresent()) {
      AsyncContext context = req.startAsync();
      context.setTimeout(timeout);
      rsp.getOutputStream()
          .setWriteListener(new ObjectDownloadListener(repository, context, rsp, obj.get()));
    }
  }
}
