package net.dataforte.infinispan.playground.custommbean.impl;

import org.infinispan.Cache;

import net.dataforte.infinispan.playground.custommbean.CustomMBean;

public class CustomMBeanImpl implements CustomMBean {

   private Cache<String, ?> cache;

   public CustomMBeanImpl(Cache<String, ?> cache) {
      this.cache = cache;

   }

   @Override
   public void evictKey(String key) {
      cache.evict(key);
   }

}
