package net.dataforte.infinispan.playground.custommbean;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

public class CustomMBeanTest {

   public static void main(String[] args) throws Exception {
      DefaultCacheManager cm = new DefaultCacheManager();
      Cache<Object, Object> cache = cm.getCache();

      cache.put("key", "value");
      Thread.sleep(60000);
      cm.stop();
   }
}
