
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
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.DigestOutputStream;
import java.text.MessageFormat;
import org.eclipse.jgit.internal.storage.file.LockFile;
import org.eclipse.jgit.lfs.errors.CorruptLongObjectException;
import org.eclipse.jgit.lfs.lib.AnyLongObjectId;
import org.eclipse.jgit.lfs.lib.Constants;
import org.eclipse.jgit.lfs.lib.LongObjectId;
import org.eclipse.jgit.lfs.server.internal.LfsServerText;

class AtomicObjectOutputStream extends OutputStream {
  private LockFile locked;
  private DigestOutputStream out;
  private boolean aborted;

  AtomicObjectOutputStream(Path path) throws IOException {
    this.locked = new LockFile(path.toFile());
    this.locked.lock();
    this.out = new DigestOutputStream(this.locked.getOutputStream(), Constants.newMessageDigest());
  }

  public void write(int b) throws IOException {
    this.out.write(b);
  }

  public void write(byte[] b) throws IOException {
    this.out.write(b);
  }

  public void write(byte[] b, int off, int len) throws IOException {
    this.out.write(b, off, len);
  }

  public void close() throws IOException {
    this.out.close();
    if(!this.aborted) {
      this.locked.commit();
    }
  }

  void abort() {
    this.locked.unlock();
    this.aborted = true;
  }
}
