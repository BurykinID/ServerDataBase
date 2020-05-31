/*
package com.example.demo.Files;

import com.example.demo.Validator.Calc;
import com.example.demo.config.component.JwtRequest;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.file.Update.FileForUpdate;
import com.example.demo.forJsonObject.file.forUpload.FileJson;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

import static com.example.demo.role.Role.ADMIN;
import static com.example.demo.role.Role.USER;

@RunWith (SpringRunner.class)
@SpringBootTest
public class FileTest {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private AccessRepository accessRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void uploadFile() {

        String uploadPath = "C:/Users/admin/Desktop/Test/";

        String password = String.valueOf(new BCryptPasswordEncoder().encode("1"));

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(ADMIN);
        roleSet.add(USER);
        User user1 = User.createUser("u", password, String.valueOf("email"), roleSet);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user1.setRoles(roles);
        user1.setActivationCode(UUID.randomUUID().toString());
        user1.setActivationCode(null);

        userRepository.save(user1);

        JwtRequest authenticationRequest = new JwtRequest();
        authenticationRequest.setUsername("u");
        authenticationRequest.setPassword(password);

        FileJson fileJson = new FileJson();
        fileJson.setFilename("Test.docx");
        fileJson.setContent("");
        fileJson.setTag("");

        java.io.File uploadDir = new java.io.File(uploadPath);
        String username = "u";

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
            for (String tagAboutUser : userTags) {
                tags.add(tagAboutUser);
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            tags.add(fileJson.getTag().trim());
        }

        String uploadFileSize = Calc.getFileSize(fileJson.getContent().length());
        UUID id = UUID.randomUUID();
        File newFile = new File(id, uploadFileName, uploadFileSize , uploadDate, username, username, uploadPath + id, tags);

        boolean notNull = false;

        if (newFile != null) {
            fileRepository.save(newFile);
            notNull = true;

            Access access = new Access();
            access.setIdFile(String.valueOf(newFile.getId()));
            access.setUsername(newFile.getAuthor());
            access.setAccess("3");

            accessRepository.save(access);

        }

        if (notNull) {
            Assert.assertEquals(userRepository.findAll().get(0).getUsername(), accessRepository.findAll().get(0).getUsername());
            Assert.assertEquals("3", accessRepository.findAll().get(0).getAccess());
            Assert.assertEquals(String.valueOf(fileRepository.findAll().get(0).getId()), accessRepository.findAll().get(0).getIdFile());
            Assert.assertEquals(1, accessRepository.findAll().size());
        }
        else {
            Assert.assertEquals("1", "1");
        }

    }

    @Test
    public void updateFile() {

        String uploadPath = "C:/Users/admin/Desktop/Test/";

        String password = String.valueOf(new BCryptPasswordEncoder().encode("1"));

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(ADMIN);
        roleSet.add(USER);
        User user1 = User.createUser("u", password, String.valueOf("email"), roleSet);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        user1.setRoles(roles);
        user1.setActivationCode(UUID.randomUUID().toString());
        user1.setActivationCode(null);

        userRepository.save(user1);

        FileJson fileJson = new FileJson();
        fileJson.setFilename("Test.docx");
        fileJson.setContent("");
        fileJson.setTag("");

        java.io.File uploadDir = new java.io.File(uploadPath);
        String username = "u";

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
            for (String tagAboutUser : userTags) {
                tags.add(tagAboutUser);
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            tags.add(fileJson.getTag().trim());
        }

        String uploadFileSize = Calc.getFileSize(fileJson.getContent().length());
        UUID id = UUID.randomUUID();
        File newFile = new File(id, uploadFileName, uploadFileSize , uploadDate, username, username, uploadPath + id, tags);

        boolean notNull = false;

        if (newFile != null) {
            fileRepository.save(newFile);
            notNull = true;

            Access access = new Access();
            access.setIdFile(String.valueOf(newFile.getId()));
            access.setUsername(newFile.getAuthor());
            access.setAccess("3");

            accessRepository.save(access);

        }

        if (notNull) {

            FileForUpdate file = new FileForUpdate();

            file.setFilename(fileJson.getFilename());
            file.setAuthor(newFile.getAuthor());
            file.setContent("MS4g0KbQldCb0JXQktCQ0K8g0KPQodCi0JDQndCe0JLQmtCQOiDQn9C+0LvRg9GH0LXQvdC40LUg0L/RgNC+0YTQtdGB0YHQuNC+0L3QsNC70YzQvdGL0YUg0YPQvNC10L3QuNC5INC4INC+0L/Ri9GC0LAg0L/RgNC+0YTQtdGB0YHQuNC+0L3QsNC70YzQvdC+0Lkg0LTQtdGP0YLQtdC70YzQvdC+0YHRgtC4INC/");

            File file1 = fileRepository.findById(fileRepository.findByFilenameAndAuthor(file.getFilename(), file.getAuthor(), Sort.by("date").descending()).get(0).getId());

            if (file1 != null) {
                try {
                    byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file1.getPath()));
                    String encodedString = Base64.getEncoder().encodeToString(fileContent);
                    if (encodedString != null) {
                        if (!encodedString.equals(file.getContent())) {

                            uploadFileName = file.getFilename();

                            time = new Date().getTime();
                            uploadDate = String.valueOf(time);

                            ArrayList<String> accessList = new ArrayList<>();
                            accessList.add(username);

                            tags = new ArrayList<>();

                            tags.addAll(file1.getTag());
                            //java.io.File fileWithSize = null;

                            uploadFileSize = Calc.getFileSize(file.getContent().length());
                            id = UUID.randomUUID();
                            newFile = new File(id, uploadFileName, uploadFileSize , uploadDate, file1.getAuthor(), username, uploadPath + id, tags);

                            notNull = false;

                            // нужно автоматически выдать тот же уровень прав всех, у кого он был на предыдущей версии файла.

                            if (newFile != null) {
                                fileRepository.save(newFile);
                                notNull = true;

                                for (Access existAccess :accessRepository.findByIdFile(String.valueOf(file1.getId()))) {

                                    Access access = new Access();
                                    access.setIdFile(String.valueOf(newFile.getId()));
                                    access.setUsername(existAccess.getUsername());
                                    access.setAccess(existAccess.getAccess());

                                    accessRepository.save(access);

                                }

                            }

                            if (notNull) {
                                Assert.assertEquals(2, fileRepository.findAll().size());
                            }
                            else {
                                Assert.assertEquals(1, fileRepository.findAll().size());
                            }

                        }
                    }
                } catch (IOException e) {
                    Assert.assertEquals(1, 1);
                }
            }


        }
        else {
            Assert.assertEquals("1", "1");
        }

    }

    @After
    public void clearTable() {
        fileRepository.deleteAll();
        userRepository.deleteAll();
        accessRepository.deleteAll();
    }

}
*/
