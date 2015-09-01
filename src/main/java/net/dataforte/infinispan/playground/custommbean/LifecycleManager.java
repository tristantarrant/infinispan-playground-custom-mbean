package net.dataforte.infinispan.playground.custommbean;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.infinispan.Cache;
import org.infinispan.commons.CacheException;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.factories.ComponentRegistry;
import org.infinispan.factories.components.ManageableComponentMetadata;
import org.infinispan.jmx.CacheJmxRegistration;
import org.infinispan.jmx.JmxUtil;
import org.infinispan.jmx.ResourceDMBean;
import org.infinispan.lifecycle.AbstractModuleLifecycle;
import org.kohsuke.MetaInfServices;

import net.dataforte.infinispan.playground.custommbean.impl.CustomMBeanImpl;

@MetaInfServices(org.infinispan.lifecycle.ModuleLifecycle.class)
public class LifecycleManager extends AbstractModuleLifecycle {
   private MBeanServer mbeanServer;

   private String jmxDomain;

   @Override
   public void cacheStarted(ComponentRegistry cr, String cacheName) {
      Cache<String, ?> cache = cr.getComponent(Cache.class);

      // Resolve MBean server instance
      GlobalConfiguration globalCfg = cr.getGlobalComponentRegistry().getGlobalConfiguration();
      mbeanServer = JmxUtil.lookupMBeanServer(globalCfg);

      String cacheManagerName = cr.getGlobalComponentRegistry().getGlobalConfiguration().globalJmxStatistics().cacheManagerName();
      String groupName = getGroupJmxName(cacheManagerName, cache);
      jmxDomain = JmxUtil.buildJmxDomain(globalCfg, mbeanServer, groupName);

      // Register our custom MBean, picking metadata from repo
      ManageableComponentMetadata customMBeanMetadata = cr.getGlobalComponentRegistry().getComponentMetadataRepo()
            .findComponentMetadata(CustomMBean.class)
            .toManageableComponentMetadata();
      try {
         CustomMBeanImpl customMBean = new CustomMBeanImpl(cache);
         ResourceDMBean mbean = new ResourceDMBean(customMBean, customMBeanMetadata);
         ObjectName mbeanObjectName = new ObjectName(jmxDomain + ":"
               + groupName + ",component=" + customMBeanMetadata.getJmxObjectName());
         JmxUtil.registerMBean(mbean, mbeanObjectName, mbeanServer);
      } catch (Exception e) {
         throw new CacheException("Unable to create ", e);
      }
   }

   @Override
   public void cacheStopping(ComponentRegistry cr, String cacheName) {
      Cache<String, ?> cache = cr.getComponent(Cache.class);
      // Unregister MBeans
      if (mbeanServer != null) {
         String cacheManagerName = cr.getGlobalComponentRegistry().getGlobalConfiguration().globalJmxStatistics().cacheManagerName();
         String queryMBeanFilter = jmxDomain + ":" + getGroupJmxName(cacheManagerName, cache) + ",*";
         JmxUtil.unregisterMBeans(queryMBeanFilter, mbeanServer);
      }
   }

   private String getGroupJmxName(String cacheManagerName, Cache cache) {
      return String.format("type=Cache,name=%s,manager=%s", cache.getName(), ObjectName.quote(cacheManagerName));
   }
}
