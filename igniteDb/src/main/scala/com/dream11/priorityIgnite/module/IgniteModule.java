package com.dream11.priorityIgnite.module;

import com.dream11.priorityIgnite.module.cacheConfig.annotations.CriteriaWeightsCacheAnnotation;
import com.dream11.priorityIgnite.module.cacheConfig.impl.CriteriaWeightsCacheConfiguration;
import com.dream11.priorityIgnite.module.provider.DataRegionConfigProvider;
import com.dream11.priorityIgnite.module.provider.IgniteConfigProvider;
import com.dream11.priorityIgnite.module.provider.IpFinderProvider;
import com.dream11.priorityIgnite.module.provider.TcpDiscoverySpiProvider;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.apache.ignite.configuration.CacheConfiguration;
import com.dream11.common.util.ConfigProvider;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;

public class IgniteModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CacheConfiguration.class).annotatedWith(CriteriaWeightsCacheAnnotation.class).toInstance(CriteriaWeightsCacheConfiguration.getConfig());
        bind(ConfigProvider.class).toInstance(getConfigProvider());
        bind(IgniteConfig.class).toProvider(IgniteConfigProvider.class).in(Singleton.class);
        bind(TcpDiscoveryVmIpFinder.class).toProvider(IpFinderProvider.class).in(Singleton.class);
        bind(DataRegionConfiguration.class).toProvider(DataRegionConfigProvider.class).in(Singleton.class);
        bind(TcpDiscoverySpi.class).toProvider(TcpDiscoverySpiProvider.class).in(Singleton.class);
        //classof[CacheConfiguration[String,Double]]
    }

    public ConfigProvider getConfigProvider() {
        return new ConfigProvider(getObjectMapper());
    }

    @Provides
    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new DefaultScalaModule());
        return objectMapper;
    }
}
