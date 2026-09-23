package com.authentication.client.imagekit;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.results.ResultList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:application.properties"})
public class DefaultImageKitClient implements ImageKitClient {

    private final ImageKit imageKitConnector;

    @Autowired
    public DefaultImageKitClient(
            @Value("${image.kit.url.endpoint:}") String imageKitUrlEndpoint,
            @Value("${image.kit.private.key:}") String imageKitPrivateKey,
            @Value("${image.kit.public.key:}") String imageKitPublicKey) {
        this.imageKitConnector = getImageKitConnector(imageKitUrlEndpoint, imageKitPrivateKey, imageKitPublicKey);
    }

    private ImageKit getImageKitConnector(String imageKitUrlEndpoint, String imageKitPrivateKey, String imageKitPublicKey) {
        var imageKit = ImageKit.getInstance();
        io.imagekit.sdk.config.Configuration config = new io.imagekit.sdk.config.Configuration(imageKitPublicKey, imageKitPrivateKey, imageKitUrlEndpoint);
        imageKit.setConfig(config);
        return imageKit;
    }

    @Override
    public String uploadImage(byte[] bytes, String fileName) throws Exception {
        var fileCreateRequest = new FileCreateRequest(bytes, fileName);
        var result = imageKitConnector.upload(fileCreateRequest);
        return result.getName();
    }

    @Override
    public Boolean resultFileListIsEmpty(String fileName) throws Exception {
        var resultList = getFileListResult(fileName);
        return resultList.getResults().isEmpty();
    }

    @Override
    public void deleteFile(String fileName) throws Exception {
        var resultList = getFileListResult(fileName);
        imageKitConnector.deleteFile(resultList.getResults().get(0).getFileId());
    }

    private ResultList getFileListResult(String fileName) throws Exception {
        var getFileListRequest = new GetFileListRequest();
        getFileListRequest.setSearchQuery(String.format("name='%s'", fileName));
        return imageKitConnector.getFileList(getFileListRequest);
    }
}
