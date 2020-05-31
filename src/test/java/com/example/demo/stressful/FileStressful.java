/*
package com.example.demo.stressful;

import com.example.demo.Validator.Calc;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.file.forUpload.FileJson;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.demo.role.Role.USER;

@RunWith (SpringRunner.class)
@SpringBootTest
public class FileStressful {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private AccessRepository accessRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void stressfulInsertFileTest() {
        insertUser();
        for (int i = 0; i < 100000; i++){
            insertFile(i+".docx");
        }
        Assert.assertNotNull(fileRepository);

    }

    @Test
    public void stressfulGetFileTest() {

        for (int i = 0; i < 100000; i++){
            insertFile(i+".docx");
        }

        Random random = new Random();
        int randInt = random.nextInt(10000) + 1;

        String id = String.valueOf(fileRepository.findAll().get(randInt).getId());

        Assert.assertNotNull(fileRepository.findById(UUID.fromString(id)));
    }

    @After
    public void clearTable() {
        fileRepository.deleteAll();
        accessRepository.deleteAll();
        userRepository.deleteAll();
    }

    public void insertUser() {

        for (int i = 0; i < 2; i++) {

            Set<Role> roleSet = new HashSet<>();
            roleSet.add(USER);
            User user = User.createUser("u" + i, "1", String.valueOf(i), roleSet);
            user.setActivationCode(UUID.randomUUID().toString());

            userRepository.save(user);

        }

    }

    public void insertFile(String filename) {

        String uploadPath = "C:/Users/admin/Desktop/Test/";

        FileJson fileJson = new FileJson();
        fileJson.setFilename(filename);
        fileJson.setContent("MS4g0KbQldCb0JXQktCQ0K8g0KPQodCi0JDQndCe0JLQmtCQOiDQn9C+0LvRg9GH0LXQvdC40LUg0L/RgNC+0YTQtdGB0YHQuNC+0L3QsNC70YzQvdGL0YUg0YPQvNC10L3QuNC5INC4INC+0L/Ri9GC0LAg0L/RgNC+0YTQtdGB0YHQuNC+0L3QsNC70YzQvdC+0Lkg0LTQtdGP0YLQtdC70YzQvdC+0YHRgtC4INC/0L4g0LLRi9Cx0YDQsNC90L3QvtC5INGC0LXQvNC1OiDCq9CR0Y3QuiDQv9C+0LTRgdC40YHRgtC10LzQsCDRjdC70LXQutGC0YDQvtC90L3QvtCz0L4g0LTQvtC60YPQvNC10L3RgtC+0L7QsdC+0YDQvtGC0LDCuwoyLiDQodCe0JTQldCg0JbQkNCd0JjQlSDQn9Cg0JDQmtCi0JjQmtCYOiAK");
        fileJson.setTag("tag25, tag27, tag30");

        java.io.File uploadDir = new java.io.File(uploadPath);
        String username = "u0";

        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        String uploadFileName = fileJson.getFilename();

        long time = new Date().getTime();
        String uploadDate = String.valueOf(time);

        ArrayList<String> tags = new ArrayList<>();

        try {
            fileJson.setTag(fileJson.getTag().replaceAll(" ", ""));
            String[] userTags = fileJson.getTag().split(",");
            tags.addAll(Arrays.asList(userTags));
        }
        catch (ArrayIndexOutOfBoundsException e) {
            tags.add(fileJson.getTag().trim());
        }

        String uploadFileSize = Calc.getFileSize(fileJson.getContent().length());
        UUID id = UUID.randomUUID();
        File newFile = new File(id, uploadFileName, uploadFileSize , uploadDate, username, username, uploadPath + id, tags);

        fileRepository.save(newFile);

        Access access = new Access();
        access.setIdFile(String.valueOf(newFile.getId()));
        access.setUsername(newFile.getAuthor());
        access.setAccess("3");

        accessRepository.save(access);

    }


}
*/
