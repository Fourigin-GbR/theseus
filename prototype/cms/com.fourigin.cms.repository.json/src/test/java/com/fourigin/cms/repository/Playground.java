package com.fourigin.cms.repository;

import com.fourigin.cms.config.ContentPageRepositoryConfiguration;
import com.fourigin.cms.models.content.ContentPage;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Playground {
    public static void main(String[] args) throws IOException {

//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        BufferedOutputStream bos = new BufferedOutputStream(baos);
//
//        {
//            InputStream stream = Playground.class.getResourceAsStream("/text.json");
//            TextContentElement element = objectMapper.readValue(stream, TextContentElement.class);
//            System.out.println(element);
//            objectMapper.writeValue(bos, element);
//            bos.write('\n');
//        }
//
//        {
//            InputStream stream = Playground.class.getResourceAsStream("/text-link.json");
//            TextLinkContentElement element = objectMapper.readValue(stream, TextLinkContentElement.class);
//            System.out.println(element);
//            objectMapper.writeValue(bos, element);
//            bos.write('\n');
//        }
//
//        {
//            InputStream stream = Playground.class.getResourceAsStream("/object.json");
//            ObjectContentElement element = objectMapper.readValue(stream, ObjectContentElement.class);
//            System.out.println(element);
//            objectMapper.writeValue(bos, element);
//            bos.write('\n');
//        }
//
//        {
//            InputStream stream = Playground.class.getResourceAsStream("/object-link.json");
//            ObjectLinkContentElement element = objectMapper.readValue(stream, ObjectLinkContentElement.class);
//            System.out.println(element);
//            objectMapper.writeValue(bos, element);
//            bos.write('\n');
//        }
//
//        {
//            InputStream stream = Playground.class.getResourceAsStream("/group.json");
//            ContentGroup element = objectMapper.readValue(stream, ContentGroup.class);
//            System.out.println(element);
//            objectMapper.writeValue(bos, element);
//            bos.write('\n');
//        }
//
//        {
//            InputStream stream = Playground.class.getResourceAsStream("/contentPage.json");
//            ContentPage element = objectMapper.readValue(stream, ContentPage.class);
//            System.out.println(element);
//            objectMapper.writeValue(bos, element);
//            bos.write('\n');
//        }
//
//        String mappedOutput = new String(baos.toByteArray(), Charset.forName("UTF-8"));
//        System.out.println(mappedOutput);

        String filename = "contentPage";
        File contentRoot = File.createTempFile("cms", "contentRoot").getParentFile();
        File outputFile = new File(contentRoot, filename + ".json");
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        InputStream pageStream = Playground.class.getResourceAsStream("/" + filename + ".json");
        IOUtils.copy(pageStream, outputStream);
        IOUtils.closeQuietly(pageStream);
        IOUtils.closeQuietly(outputStream);

        ContentPageRepositoryConfiguration configuration = new ContentPageRepositoryConfiguration();
        ContentPageRepository contentPageRepository = configuration.contentPageRepository(contentRoot.getAbsolutePath());

//        ContentPageRepository contentPageRepository = new JsonFileContentPageRepository.Builder()
//          .contentRoot(contentRoot.getAbsolutePath())
//          .objectMapper(objectMapper)
//          .build();

        ContentPage page = contentPageRepository.retrieve(filename);

        System.out.println(page);
    }
}