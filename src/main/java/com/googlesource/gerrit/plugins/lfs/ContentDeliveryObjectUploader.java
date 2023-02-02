
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ReadListener;

import org.eclipse.jgit.lfs.lib.AnyLongObjectId;

/**
 * Class responsible for writing the LFS object to disk.
 * Writes to the specified content delivery path, which is
 * configurable via application.properties.
 * @author ronanconway
 */
public class ContentDeliveryObjectUploader implements ReadListener, AutoCloseable {
  private static Logger LOG = Logger.getLogger(ContentDeliveryObjectUploader.class.getName());
  private final InputStream in;
  private final ReadableByteChannel inChannel;
  private final AtomicObjectOutputStream out;
  private WritableByteChannel channel;
  private final ByteBuffer buffer = ByteBuffer.allocateDirect(8192);

  public ContentDeliveryObjectUploader(Path path, InputStream inputStream) throws IOException {
    this.in = inputStream;
    this.inChannel = Channels.newChannel(this.in);
    this.out = getOutputStream(path);
    this.channel = Channels.newChannel(this.out);
  }

  /*
   * Reads a sequence of bytes from the inChannel into the buffer
   * which is then written into the channel of the output stream.
   */
  public void onDataAvailable() throws IOException {
    while(this.in.available() != -1) {
      if(this.inChannel.read(this.buffer) <= 0) {
        this.buffer.flip();

        while(this.buffer.hasRemaining()) {
          this.channel.write(this.buffer);
        }

        this.close();
        return;
      }

      this.buffer.flip();
      this.channel.write(this.buffer);
      this.buffer.compact();
    }
  }

  /*
   * Creates the directory and returns a new AtomicObjectOutputStream using the content delivery path
   * and the LFS object OID
   */
  public AtomicObjectOutputStream getOutputStream(Path path) throws IOException {
    Path parent = path.getParent();
    if(parent != null) {
      Files.createDirectories(parent, new FileAttribute[0]);
    }
    return new AtomicObjectOutputStream(path);
  }

  public void onAllDataRead() throws IOException {
    this.close();
  }

  /*
  * Once we're done we close the channels.
  * This class implements AutoClosable and will call
  * this automatically
  */
  public void close() throws IOException {
    this.inChannel.close();
    this.channel.close();
  }

  //Overriden implementation from the interface.
  public void onError(Throwable e) {
    try {
      this.out.abort();
      this.inChannel.close();
      this.channel.close();
    } catch (IOException ex) {
      LOG.log(Level.SEVERE, ex.getMessage(), ex);
    }

  }
}
