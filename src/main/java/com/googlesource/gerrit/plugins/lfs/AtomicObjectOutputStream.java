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
  private AnyLongObjectId id;

  AtomicObjectOutputStream(Path path, AnyLongObjectId id) throws IOException {
    this.locked = new LockFile(path.toFile());
    this.locked.lock();
    this.id = id;
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
      this.verifyHash();
      this.locked.commit();
    }
  }

  private void verifyHash() {
    AnyLongObjectId contentHash = LongObjectId.fromRaw(this.out.getMessageDigest().digest());
    if(!contentHash.equals(this.id)) {
      this.abort();
      throw new CorruptLongObjectException(this.id, contentHash, MessageFormat.format(LfsServerText.get().corruptLongObject, new Object[]{contentHash, this.id}));
    }
  }

  void abort() {
    this.locked.unlock();
    this.aborted = true;
  }
}
