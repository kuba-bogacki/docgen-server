package com.authentication.config;

import io.imagekit.sdk.ImageKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:application.properties"})
public class ImageKitConfiguration {

    private final String imageKitUrlEndpoint;
    private final String imageKitPrivateKey;
    private final String imageKitPublicKey;

    @Autowired
    public ImageKitConfiguration(
            @Value("${image.kit.url.endpoint:}") String imageKitUrlEndpoint,
            @Value("${image.kit.private.key:}") String imageKitPrivateKey,
            @Value("${image.kit.public.key:}") String imageKitPublicKey) {
        this.imageKitUrlEndpoint = imageKitUrlEndpoint;
        this.imageKitPrivateKey = imageKitPrivateKey;
        this.imageKitPublicKey = imageKitPublicKey;
    }

    public ImageKit imageKitProvider() {
        ImageKit imageKit = ImageKit.getInstance();
        io.imagekit.sdk.config.Configuration config = new io.imagekit.sdk.config.Configuration(imageKitPublicKey, imageKitPrivateKey, imageKitUrlEndpoint);
        imageKit.setConfig(config);
        return imageKit;
    }
}
