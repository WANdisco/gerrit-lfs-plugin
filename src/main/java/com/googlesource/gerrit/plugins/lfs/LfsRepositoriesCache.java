// Copyright (C) 2016 The Android Open Source Project
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

package com.googlesource.gerrit.plugins.lfs;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Singleton;

import org.eclipse.jgit.lfs.server.LargeFileRepository;

@Singleton
public class LfsRepositoriesCache {
  private final Cache<LfsBackend, LargeFileRepository> cache;

  LfsRepositoriesCache() {
    this.cache = CacheBuilder.newBuilder().build();
  }

  public LargeFileRepository get(LfsBackend backend) {
    return cache.getIfPresent(backend);
  }

  public void put(LfsBackend cfg, LargeFileRepository repo) {
    cache.put(cfg, repo);
  }
}
