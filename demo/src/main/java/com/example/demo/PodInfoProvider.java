package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class PodInfoProvider implements InfoContributor {

    final Map<String, Properties> props;

    public PodInfoProvider(
            @Value("${podInfoProvider.locations}") List<String> locations,
            @Value("${podInfoProvider.excludedKeys}") List<String> excludes) {
        this.props = readPodInfo(locations, excludes);
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("pod", props).build();
    }

    private Map<String, Properties> readPodInfo(List<String> locations, List<String> excludes) {
        Map<String, Properties> m = new HashMap<>();
        locations.forEach(l -> readPropertyFile(l, excludes, m));
        return m;
    }


    private void readPropertyFile(String path, List<String> excludes, Map<String, Properties> m) {
        Properties properties = new Properties();
        try {
            File file = ResourceUtils.getFile(path);
            InputStream in = new FileInputStream(file);
            properties.load(in);

            excludes.forEach(properties::remove);

            properties.forEach((k, v) -> {
                String value = ((String) v).replaceAll("^\"|\"$", "");
                properties.setProperty((String) k, value);
            });

            m.put(file.getName(), properties);

        } catch (IOException e) {
            // ignore
        }
    }
}
