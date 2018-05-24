package com.googlesource.gerrit.plugins.lfs;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.List;

//TODO, in memory cache???, locking / serialization, best format on disk, cross process in memory or cache sharing for performance?
//TODO, look into a MemoryMapped file which uses MappedByteBuffer to write data to our file using memory mapped IO

public class LfsJournalCache<K, V> {

    private GenericCache<K, V> genericCache;
    public File cacheFile;
    private static Gson gson = createGson();

    public LfsJournalCache(final String cacheFilePath) throws IOException {
        genericCache = new GenericCache<>();
        Path cachePath = Paths.get(cacheFilePath);
        if(!cachePath.toFile().exists()){
            cacheFile = new File(cachePath.toString());
            cacheFile.getParentFile().mkdirs();
            cacheFile.createNewFile();
            writeCacheToDisk();
        } else {
            this.cacheFile = cachePath.toFile();
            readCacheFromDisk();
        }
    }

    public void put(K key, V value)
    {
        genericCache.put(key, value);
        writeCacheToDisk();
    }

    public void putIfAbsent(K key, V value)
    {
        genericCache.putIfAbsent(key, value);
        writeCacheToDisk();
    }

    public boolean containsKey(K key){
        return genericCache.containsKey(key);
    }

    public boolean containsKeyAndValue(K key, V value){
        if(!containsKey(key)){
            return false;
        }
        V retVal = genericCache.get(key);
        if ( retVal instanceof List)
        {
            return ((List)retVal).contains(((List) value).get(0));
        } else {
            throw new InvalidParameterException("Add your own type support here");
        }
    }

    public V get(K key)
    {
        //Need to read the cache from disk first otherwise genericCache could be null.
        if(genericCache == null) {
            readCacheFromDisk();
        }

        V val = genericCache.get(key);
        if ( val != null ){
            return val;
        }
        readCacheFromDisk();
        return genericCache.get(key);
    }

    public void add(K key, V value )
    {
        V existingVal = null;
        //if the OID already exists in the cache-file but not for this repo then add the associated repos to the list.
        if(containsKey(key)) {
            existingVal = get(key);
        }

        //If OID not in the cach-file then just add it along with the associated repo.
        if ( existingVal == null ) {
            existingVal = value;
        }
        else {
            // for any new types, add new addition logic here.
            if ( value instanceof List)
            {
                ((List)existingVal).addAll((List)value);
            } else {
                throw new InvalidParameterException("Add your own type support here");
            }
        }

        put(key, existingVal); //i.e <OID>, [ <Repo-A> ]
    }

    private void readCacheFromDisk() {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(cacheFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        LfsJournalCache<K, V> loadedCache = gson.fromJson( fileReader, LfsJournalCache.class);
        this.genericCache = loadedCache.genericCache;
    }

    private static Gson createGson() {
        GsonBuilder gb = (new GsonBuilder())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().disableHtmlEscaping()
                .serializeNulls();
        return gb.create();
    }


    private void writeCacheToDisk() {
        try {
            try(FileWriter fw = new FileWriter(cacheFile)) {
                gson.toJson(this, fw);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
