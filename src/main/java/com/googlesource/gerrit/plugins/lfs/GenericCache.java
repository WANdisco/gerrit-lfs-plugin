
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

import java.util.concurrent.ConcurrentHashMap;

public class GenericCache<K, V>
{
  private ConcurrentHashMap<K, V> map;

  public GenericCache()
  {
    map = new ConcurrentHashMap<K, V>();
  }

  public void put(K key, V value)
  {
    map.put(key, value);
  }

  public boolean containsKey(K key) { return map.containsKey(key); }

  public boolean containsValue(V value) { return map.containsValue(value); }

  public void putIfAbsent(K key, V value)
  {
    map.putIfAbsent(key, value);
  }

  public V get(K key)
  {
    return map.get(key);
  }

  public void remove(K key){
    map.remove(key);
  }

  public void clearCache(){
    map.clear();
  }

  public int size(){
    return map.size();
  }
}
