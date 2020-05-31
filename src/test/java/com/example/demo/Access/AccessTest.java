package com.example.demo.Access;

import com.example.demo.Validator.Calc;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.file.AccessAnwerUser;
import com.example.demo.forJsonObject.file.forUpload.FileJson;
import com.example.demo.forJsonObject.user.UserAccess;
import com.example.demo.forJsonObject.user.UserAccessAll;
import com.example.demo.forJsonObject.user.Username;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static com.example.demo.role.Role.ADMIN;
import static com.example.demo.role.Role.USER;

@RunWith (SpringRunner.class)
@SpringBootTest
public class AccessTest {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private AccessRepository accessRepository;
    @Autowired
    private UserRepository userRepository;

    // id
    @Test
    public void updReadForFile() {
        insertUser();
        UserAccess userAccess = new UserAccess();

        ArrayList<Username> usernameForShare = new ArrayList<>();
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            Username username = new Username();
            username.setUsername(userRepository.findAll().get(i).getUsername());
            usernameForShare.add(username);
        }
        usernameForShare.add(new Username("usr1"));
        userAccess.setUsernameForShare(usernameForShare);
        insertFile();
        String filename = String.valueOf(fileRepository.findAll().get(0).getId());
        AccessAnwerUser accessAnwerUser = updReadPermit(userAccess, filename, "1", "u0");

        Assert.assertEquals(usernameForShare.size()-1,accessAnwerUser.getUserWithoutError().size());
        Assert.assertEquals(1, accessAnwerUser.getUserWithError().size());
        Assert.assertEquals("usr1", accessAnwerUser.getUserWithError().get(0).getUsername());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        accessRepository.deleteAll();

    }

    @Test
    public void dropReadForFile() {
        insertUser();
        UserAccess userAccess = new UserAccess();

        ArrayList<Username> usernameForShare = new ArrayList<>();
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            Username username = new Username();
            username.setUsername(userRepository.findAll().get(i).getUsername());
            usernameForShare.add(username);
        }
        usernameForShare.add(new Username("usr1"));
        userAccess.setUsernameForShare(usernameForShare);

        insertFile();
        String filename = String.valueOf(fileRepository.findAll().get(0).getId());
        AccessAnwerUser accessAnwerUser = deletePermit(userAccess, filename, "0", "u0");

        Assert.assertEquals(usernameForShare.size()-1,accessAnwerUser.getUserWithoutError().size());
        Assert.assertEquals(1, accessAnwerUser.getUserWithError().size());
        Assert.assertEquals("usr1", accessAnwerUser.getUserWithError().get(0).getUsername());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        accessRepository.deleteAll();
    }

    @Test
    public void updWriteForFile() {
        insertUser();
        UserAccess userAccess = new UserAccess();

        ArrayList<Username> usernameForShare = new ArrayList<>();
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            Username username = new Username();
            username.setUsername(userRepository.findAll().get(i).getUsername());
            usernameForShare.add(username);
        }
        usernameForShare.add(new Username("usr1"));
        userAccess.setUsernameForShare(usernameForShare);
        insertFile();
        String filename = String.valueOf(fileRepository.findAll().get(0).getId());
        AccessAnwerUser accessAnwerUser = updPermit(userAccess, filename, "2", "u0");

        Assert.assertEquals(usernameForShare.size()-1,accessAnwerUser.getUserWithoutError().size());
        Assert.assertEquals(1, accessAnwerUser.getUserWithError().size());
        Assert.assertEquals("usr1", accessAnwerUser.getUserWithError().get(0).getUsername());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        accessRepository.deleteAll();

    }

    @Test
    public void dropWriteForFile() {
        insertUser();
        UserAccess userAccess = new UserAccess();

        ArrayList<Username> usernameForShare = new ArrayList<>();
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            Username username = new Username();
            username.setUsername(userRepository.findAll().get(i).getUsername());
            usernameForShare.add(username);
        }
        usernameForShare.add(new Username("usr1"));
        userAccess.setUsernameForShare(usernameForShare);
        insertFile();
        String filename = String.valueOf(fileRepository.findAll().get(0).getId());
        AccessAnwerUser accessAnwerUser = deletePermit(userAccess, filename, "1", "u0");

        Assert.assertEquals(usernameForShare.size()-1,accessAnwerUser.getUserWithoutError().size());
        Assert.assertEquals(1, accessAnwerUser.getUserWithError().size());
        Assert.assertEquals("usr1", accessAnwerUser.getUserWithError().get(0).getUsername());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        accessRepository.deleteAll();

    }

    @Test
    public void updDeleteForFile() {
        insertUser();
        UserAccess userAccess = new UserAccess();

        ArrayList<Username> usernameForShare = new ArrayList<>();
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            Username username = new Username();
            username.setUsername(userRepository.findAll().get(i).getUsername());
            usernameForShare.add(username);
        }
        usernameForShare.add(new Username("usr1"));
        userAccess.setUsernameForShare(usernameForShare);
        insertFile();
        String filename = String.valueOf(fileRepository.findAll().get(0).getId());

        AccessAnwerUser accessAnwerUser = updPermit(userAccess, filename, "3", "u0");

        Assert.assertEquals(usernameForShare.size()-1,accessAnwerUser.getUserWithoutError().size());
        Assert.assertEquals(1, accessAnwerUser.getUserWithError().size());
        Assert.assertEquals("usr1", accessAnwerUser.getUserWithError().get(0).getUsername());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        accessRepository.deleteAll();

    }

    @Test
    public void dropDeleteForFile() {
        insertUser();
        UserAccess userAccess = new UserAccess();
        ArrayList<Username> usernameForShare = new ArrayList<>();
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            Username username = new Username();
            username.setUsername(userRepository.findAll().get(i).getUsername());
            usernameForShare.add(username);
        }
        usernameForShare.add(new Username("usr1"));
        userAccess.setUsernameForShare(usernameForShare);
        insertFile();
        String filename = String.valueOf(fileRepository.findAll().get(0).getId());
        AccessAnwerUser accessAnwerUser = deletePermit(userAccess, filename, "2", "u0");

        Assert.assertEquals(usernameForShare.size()-1,accessAnwerUser.getUserWithoutError().size());
        Assert.assertEquals(1, accessAnwerUser.getUserWithError().size());
        Assert.assertEquals("usr1", accessAnwerUser.getUserWithError().get(0).getUsername());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        accessRepository.deleteAll();

    }

    // для расшаривания по ID
    public AccessAnwerUser updPermit(UserAccess userAccess, String id, String lvlAccessInput, String token) {
        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccess != null) {

            String username = token;

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            File file = fileRepository.findById(UUID.fromString(id));

            if (user != null) {
                if (file != null) {
                    boolean isAuthor = file.getAuthor().equals(username);

                    if (!isAuthor) {
                        isAdmin = checkAdmin(username);
                    }

                    if (isAuthor || isAdmin) {
                        // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                        ArrayList<Username> usernameForShare = userAccess.getUsernameForShare();

                        if (usernameForShare != null) {
                            for (int i = 0; i < usernameForShare.size(); i++) {

                                User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                // проверка на то, что пользователь из списка вообще существует
                                if (newUser != null) {

                                    if (!checkAdmin(newUser.getUsername()) && !newUser.getUsername().equals(file.getAuthor())) {

                                        Access access = accessRepository.findByUsernameAndIdFile(newUser.getUsername(), String.valueOf(file.getId()));

                                        if (access != null) {
                                            int lvlAccess = Integer.parseInt(access.getAccess());
                                            int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                            if (lvlAccess < lvlAccessInt)
                                                access.setAccess(lvlAccessInput);
                                        }
                                        else {

                                            access = new Access(String.valueOf(file.getId()), newUser.getUsername(), lvlAccessInput);;
                                        }
                                        accessRepository.save(access);
                                        // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));

                                    }
                                    else {
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));
                                    }

                                }
                                else {
                                    if (!responseWithError.contains(usernameForShare.get(i)))
                                        responseWithError.add(usernameForShare.get(i));
                                }
                            }

                            accessAnwerUser.setUserWithError(responseWithError);
                            accessAnwerUser.setUserWithoutError(responseWithoutError);

                            return accessAnwerUser;

                        }
                        else {
                            return null;
                        }
                    }
                    else {
                        return null;
                    }
                }
                else {
                    return null;
                }
            }
            else if (file != null) {
                return null;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    public AccessAnwerUser updReadPermit(UserAccess userAccess, String id, String lvlAccessInput, String token) {
        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccess != null) {

            String username = token;

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            File file = fileRepository.findById(UUID.fromString(id));

            if (user != null) {
                if (file != null) {
                    boolean isAuthor = file.getAuthor().equals(username);

                    if (!isAuthor) {
                        isAdmin = checkAdmin(username);
                    }

                    Access accessUser = accessRepository.findByUsernameAndIdFile(username, String.valueOf(file.getId()));

                    int lvlUser = Integer.parseInt(accessUser.getAccess());

                    if (isAuthor || isAdmin || lvlUser >= 1) {
                        // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                        ArrayList<Username> usernameForShare = userAccess.getUsernameForShare();

                        if (usernameForShare != null) {
                            for (int i = 0; i < usernameForShare.size(); i++) {

                                User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                // проверка на то, что пользователь из списка вообще существует
                                if (newUser != null) {

                                    if (!checkAdmin(newUser.getUsername()) && !newUser.getUsername().equals(file.getAuthor())) {

                                        Access access = accessRepository.findByUsernameAndIdFile(newUser.getUsername(), String.valueOf(file.getId()));

                                        if (access != null) {
                                            int lvlAccess = Integer.parseInt(access.getAccess());
                                            int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                            if (lvlAccess < lvlAccessInt)
                                                access.setAccess(lvlAccessInput);
                                        }
                                        else {
                                            access = new Access(String.valueOf(file.getId()), newUser.getUsername(), lvlAccessInput);
                                        }
                                        accessRepository.save(access);
                                        // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));

                                    }
                                    else {
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));
                                    }

                                }
                                else {
                                    if (!responseWithError.contains(usernameForShare.get(i)))
                                        responseWithError.add(usernameForShare.get(i));
                                }
                            }

                            accessAnwerUser.setUserWithError(responseWithError);
                            accessAnwerUser.setUserWithoutError(responseWithoutError);

                            return accessAnwerUser;

                        }
                        else {
                            return null;
                        }
                    }
                    else {
                        return null;
                    }
                }
                else {
                    return null;
                }
            }
            else if (file != null) {
                return null;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    public AccessAnwerUser deletePermit(UserAccess userAccess, String id, String lvlAccessInput, String token) {

        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccess != null) {

            String username = token;

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            File file = fileRepository.findById(UUID.fromString(id));

            if (user != null) {
                if (file != null) {
                    boolean isAuthor = file.getAuthor().equals(username);

                    if (!isAuthor) {
                        isAdmin = checkAdmin(username);
                    }

                    if (isAuthor || isAdmin) {
                        // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                        ArrayList<Username> usernameForShare = userAccess.getUsernameForShare();

                        if (usernameForShare != null) {
                            for (int i = 0; i < usernameForShare.size(); i++) {

                                User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                // проверка на то, что пользователь из списка вообще существует
                                if (newUser != null) {

                                    if (!checkAdmin(newUser.getUsername()) && !newUser.getUsername().equals(file.getAuthor())) {

                                        Access access = accessRepository.findByUsernameAndIdFile(newUser.getUsername(), String.valueOf(file.getId()));

                                        if (access != null) {
                                            int lvlAccess = Integer.parseInt(access.getAccess());
                                            int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                            if (lvlAccess > lvlAccessInt)
                                                access.setAccess(lvlAccessInput);
                                        }
                                        else {
                                            access = new Access(String.valueOf(file.getId()), newUser.getUsername(), lvlAccessInput);
                                        }
                                        if (lvlAccessInput.equals("0"))
                                            accessRepository.delete(access);
                                        else
                                            accessRepository.save(access);
                                        // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));

                                    }
                                    else {
                                        if (!responseWithoutError.contains(usernameForShare.get(i)))
                                            responseWithoutError.add(usernameForShare.get(i));
                                    }

                                }
                                else {
                                    if (!responseWithError.contains(usernameForShare.get(i)))
                                        responseWithError.add(usernameForShare.get(i));
                                }
                            }

                            accessAnwerUser.setUserWithError(responseWithError);
                            accessAnwerUser.setUserWithoutError(responseWithoutError);

                            return accessAnwerUser;

                        }
                        else {
                            return null;
                        }
                    }
                    else {
                        return null;
                    }
                }
                else {
                    return null;
                }
            }
            else if (file != null) {
                return null;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }

    }

    // filename and author
    @Test
    public void updAllReadForFile() {
        insertUser();
        UserAccessAll userAccessAll = new UserAccessAll();

        ArrayList<Username> usernameForShare = new ArrayList<>();
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            Username username = new Username();
            username.setUsername(userRepository.findAll().get(i).getUsername());
            usernameForShare.add(username);
        }
        usernameForShare.add(new Username("usr1"));

        userAccessAll.setAuthor("u0");
        userAccessAll.setUsernameForShare(usernameForShare);

        String filename = "Test.docx";
        insertFile();
        AccessAnwerUser accessAnwerUser = deleteAllPermit(userAccessAll, filename, "2", "u0");

        Assert.assertEquals(usernameForShare.size()-1,accessAnwerUser.getUserWithoutError().size());
        Assert.assertEquals(1, accessAnwerUser.getUserWithError().size());
        Assert.assertEquals("usr1", accessAnwerUser.getUserWithError().get(0).getUsername());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        accessRepository.deleteAll();

    }

    @Test
    public void dropAllReadForFile() {
        insertUser();
        UserAccessAll userAccessAll = new UserAccessAll();

        ArrayList<Username> usernameForShare = new ArrayList<>();
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            Username username = new Username();
            username.setUsername(userRepository.findAll().get(i).getUsername());
            usernameForShare.add(username);
        }
        usernameForShare.add(new Username("usr1"));

        userAccessAll.setAuthor("u0");
        userAccessAll.setUsernameForShare(usernameForShare);

        String filename = "Test.docx";
        insertFile();
        AccessAnwerUser accessAnwerUser = deleteAllPermit(userAccessAll, filename, "2", "u0");

        Assert.assertEquals(usernameForShare.size()-1,accessAnwerUser.getUserWithoutError().size());
        Assert.assertEquals(1, accessAnwerUser.getUserWithError().size());
        Assert.assertEquals("usr1", accessAnwerUser.getUserWithError().get(0).getUsername());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        accessRepository.deleteAll();

    }

    @Test
    public void updAllWriteForFile() {

        insertUser();
        UserAccessAll userAccessAll = new UserAccessAll();

        ArrayList<Username> usernameForShare = new ArrayList<>();
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            Username username = new Username();
            username.setUsername(userRepository.findAll().get(i).getUsername());
            usernameForShare.add(username);
        }
        usernameForShare.add(new Username("usr1"));

        userAccessAll.setAuthor("u0");
        userAccessAll.setUsernameForShare(usernameForShare);

        String filename = "Test.docx";
        insertFile();
        AccessAnwerUser accessAnwerUser = deleteAllPermit(userAccessAll, filename, "2", "u0");

        Assert.assertEquals(usernameForShare.size()-1,accessAnwerUser.getUserWithoutError().size());
        Assert.assertEquals(1, accessAnwerUser.getUserWithError().size());
        Assert.assertEquals("usr1", accessAnwerUser.getUserWithError().get(0).getUsername());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        accessRepository.deleteAll();

    }

    @Test
    public void dropAllWriteForFile() {
        insertUser();
        UserAccessAll userAccessAll = new UserAccessAll();

        ArrayList<Username> usernameForShare = new ArrayList<>();
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            Username username = new Username();
            username.setUsername(userRepository.findAll().get(i).getUsername());
            usernameForShare.add(username);
        }
        usernameForShare.add(new Username("usr1"));

        userAccessAll.setAuthor("u0");
        userAccessAll.setUsernameForShare(usernameForShare);

        String filename = "Test.docx";
        insertFile();
        AccessAnwerUser accessAnwerUser = deleteAllPermit(userAccessAll, filename, "2", "u0");

        Assert.assertEquals(usernameForShare.size()-1,accessAnwerUser.getUserWithoutError().size());
        Assert.assertEquals(1, accessAnwerUser.getUserWithError().size());
        Assert.assertEquals("usr1", accessAnwerUser.getUserWithError().get(0).getUsername());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        accessRepository.deleteAll();

    }

    @Test
    public void updAllDeleteForFile() {
        insertUser();
        UserAccessAll userAccessAll = new UserAccessAll();

        ArrayList<Username> usernameForShare = new ArrayList<>();
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            Username username = new Username();
            username.setUsername(userRepository.findAll().get(i).getUsername());
            usernameForShare.add(username);
        }
        usernameForShare.add(new Username("usr1"));

        userAccessAll.setAuthor("u0");
        userAccessAll.setUsernameForShare(usernameForShare);

        String filename = "Test.docx";
        insertFile();
        AccessAnwerUser accessAnwerUser = deleteAllPermit(userAccessAll, filename, "2", "u0");

        Assert.assertEquals(usernameForShare.size()-1,accessAnwerUser.getUserWithoutError().size());
        Assert.assertEquals(1, accessAnwerUser.getUserWithError().size());
        Assert.assertEquals("usr1", accessAnwerUser.getUserWithError().get(0).getUsername());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        accessRepository.deleteAll();

    }

    @Test
    public void dropAllDeleteForFile() {
        insertUser();
        UserAccessAll userAccessAll = new UserAccessAll();

        ArrayList<Username> usernameForShare = new ArrayList<>();
        for (int i = 0; i < userRepository.findAll().size(); i++) {
            Username username = new Username();
            username.setUsername(userRepository.findAll().get(i).getUsername());
            usernameForShare.add(username);
        }
        usernameForShare.add(new Username("usr1"));

        userAccessAll.setAuthor("u0");
        userAccessAll.setUsernameForShare(usernameForShare);

        String filename = "Test.docx";
        insertFile();
        AccessAnwerUser accessAnwerUser = deleteAllPermit(userAccessAll, filename, "2", "u0");

        Assert.assertEquals(usernameForShare.size()-1,accessAnwerUser.getUserWithoutError().size());
        Assert.assertEquals(1, accessAnwerUser.getUserWithError().size());
        Assert.assertEquals("usr1", accessAnwerUser.getUserWithError().get(0).getUsername());

        userRepository.deleteAll();
        fileRepository.deleteAll();
        accessRepository.deleteAll();

    }

    public void insertUser() {

        for (int i = 0; i < 5; i++) {

            Set<Role> roleSet = new HashSet<>();
            roleSet.add(USER);
            User user = User.createUser("u" + i, "1", String.valueOf(i), roleSet);
            user.setActivationCode(UUID.randomUUID().toString());

            userRepository.save(user);

        }

    }

    public void insertFile() {

        String uploadPath = "C:/Users/admin/Desktop/Test/";

        FileJson fileJson = new FileJson();
        fileJson.setFilename("Test.docx");
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

    // для расшаривания по filename и author
    public AccessAnwerUser updAllReadPermit(UserAccessAll userAccessAll, String filename, String lvlAccessInput, String token) {

        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccessAll != null) {
            String username = token;

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            ArrayList<File> files = fileRepository.findByFilenameAndAuthor(filename, userAccessAll.getAuthor(), Sort.by("date").ascending());

            if (user != null) {

                if (files .size() >= 1) {
                    for (File file : files) {

                        boolean isAuthor = file.getAuthor().equals(username);

                        if (!isAuthor) {
                            isAdmin = checkAdmin(username);
                        }

                        Access accessUser = accessRepository.findByUsernameAndIdFile(username, String.valueOf(file.getId()));

                        int lvlUser = Integer.parseInt(accessUser.getAccess());

                        if (isAuthor || isAdmin || lvlUser >= 1) {
                            // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                            ArrayList<Username> usernameForShare = userAccessAll.getUsernameForShare();

                            if (usernameForShare != null) {
                                for (int i = 0; i < usernameForShare.size(); i++) {

                                    User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                    // проверка на то, что пользователь из списка вообще существует
                                    if (newUser != null) {

                                        if (!checkAdmin(newUser.getUsername()) && !newUser.getUsername().equals(file.getAuthor())) {

                                            Access access = accessRepository.findByUsernameAndIdFile(newUser.getUsername(), String.valueOf(file.getId()));

                                            if (access != null) {
                                                int lvlAccess = Integer.parseInt(access.getAccess());
                                                int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                                if (lvlAccess < lvlAccessInt)
                                                    access.setAccess(lvlAccessInput);
                                            }
                                            else {
                                                access = new Access(String.valueOf(file.getId()), newUser.getUsername(), lvlAccessInput);
                                            }
                                            accessRepository.save(access);
                                            // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                            if (!responseWithoutError.contains(usernameForShare.get(i)))
                                                responseWithoutError.add(usernameForShare.get(i));

                                        }
                                        else {
                                            if (!responseWithoutError.contains(usernameForShare.get(i)))
                                                responseWithoutError.add(usernameForShare.get(i));
                                        }

                                    }
                                    else {
                                        if (!responseWithError.contains(usernameForShare.get(i)))
                                            responseWithError.add(usernameForShare.get(i));
                                    }
                                }

                                accessAnwerUser.setUserWithError(responseWithError);
                                accessAnwerUser.setUserWithoutError(responseWithoutError);

                            }
                            else {
                                return null;
                            }
                        }
                        else {
                            return null;
                        }

                    }
                }
                else {
                    return null;
                }

                return accessAnwerUser;

            }
            else if (files != null) {
                return null;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }

    }

    public AccessAnwerUser updAllPermit(UserAccessAll userAccessAll, String filename, String lvlAccessInput, String token) {
        Gson gson = new Gson();
        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccessAll != null) {
            String username = token;

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            ArrayList<File> files = fileRepository.findByFilenameAndAuthor(filename, userAccessAll.getAuthor(), Sort.by("date").ascending());

            if (user != null) {
                if (files.size() >= 1) {

                    for (File file : files) {
                        boolean isAuthor = file.getAuthor().equals(username);

                        if (!isAuthor) {
                            isAdmin = checkAdmin(username);
                        }

                        if (isAuthor || isAdmin) {
                            // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                            ArrayList<Username> usernameForShare = userAccessAll.getUsernameForShare();

                            if (usernameForShare != null) {
                                for (int i = 0; i < usernameForShare.size(); i++) {

                                    User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                    // проверка на то, что пользователь из списка вообще существует
                                    if (newUser != null) {

                                        if (!checkAdmin(newUser.getUsername()) && !newUser.getUsername().equals(file.getAuthor())) {

                                            Access access = accessRepository.findByUsernameAndIdFile(newUser.getUsername(), String.valueOf(file.getId()));

                                            if (access != null) {
                                                int lvlAccess = Integer.parseInt(access.getAccess());
                                                int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                                if (lvlAccess < lvlAccessInt)
                                                    access.setAccess(lvlAccessInput);
                                            }
                                            else {
                                                access = new Access(String.valueOf(file.getId()), newUser.getUsername(), lvlAccessInput);
                                            }
                                            accessRepository.save(access);
                                            // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                            if (!responseWithoutError.contains(usernameForShare.get(i)))
                                                responseWithoutError.add(usernameForShare.get(i));

                                        }
                                        else {
                                            if (!responseWithoutError.contains(usernameForShare.get(i)))
                                                responseWithoutError.add(usernameForShare.get(i));
                                        }

                                    }
                                    else {
                                        if (!responseWithError.contains(usernameForShare.get(i)))
                                            responseWithError.add(usernameForShare.get(i));
                                    }
                                }

                                accessAnwerUser.setUserWithError(responseWithError);
                                accessAnwerUser.setUserWithoutError(responseWithoutError);

                            }
                            else {
                                return null;
                            }
                        }
                        else {
                            return null;
                        }
                    }

                    return accessAnwerUser;

                }
                else {
                    return null;
                }
            }
            else if (files != null) {
                return null;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    public AccessAnwerUser deleteAllPermit(UserAccessAll userAccessAll, String filename, String lvlAccessInput, String token) {

        ArrayList<Username> responseWithoutError = new ArrayList<>();
        ArrayList<Username> responseWithError = new ArrayList<>();
        AccessAnwerUser accessAnwerUser = new AccessAnwerUser();

        if (userAccessAll != null) {
            String username = token;

            boolean isAdmin = false;

            User user = userRepository.findByUsername(username);
            ArrayList<File> files = fileRepository.findByFilenameAndAuthor(filename, userAccessAll.getAuthor(), Sort.by("date").ascending());

            if (user != null) {
                if (files.size() >= 1) {

                    for (File file : files) {
                        boolean isAuthor = file.getAuthor().equals(username);

                        if (!isAuthor) {
                            isAdmin = checkAdmin(username);
                        }

                        if (isAuthor || isAdmin) {
                            // получение списка пользователей, для которых необходимо расширить доступ на чтение файла.
                            ArrayList<Username> usernameForShare = userAccessAll.getUsernameForShare();

                            if (usernameForShare != null) {
                                for (int i = 0; i < usernameForShare.size(); i++) {

                                    User newUser = userRepository.findByUsername(usernameForShare.get(i).getUsername());
                                    // проверка на то, что пользователь из списка вообще существует
                                    if (newUser != null) {

                                        if (!checkAdmin(newUser.getUsername()) && !newUser.getUsername().equals(file.getAuthor())) {

                                            Access access = accessRepository.findByUsernameAndIdFile(newUser.getUsername(), String.valueOf(file.getId()));

                                            if (access != null) {
                                                int lvlAccess = Integer.parseInt(access.getAccess());
                                                int lvlAccessInt = Integer.parseInt(lvlAccessInput);
                                                if (lvlAccess > lvlAccessInt)
                                                    access.setAccess(lvlAccessInput);
                                            }
                                            else {
                                                access = new Access(String.valueOf(file.getId()), newUser.getUsername(), lvlAccessInput);
                                            }
                                            if (lvlAccessInput.equals("0"))
                                                accessRepository.delete(access);
                                            else
                                                accessRepository.save(access);
                                            // в любом случае помещаем пользователя в список тех, кому успешно разрешён доступ
                                            if (!responseWithoutError.contains(usernameForShare.get(i)))
                                                responseWithoutError.add(usernameForShare.get(i));

                                        }
                                        else {
                                            if (!responseWithoutError.contains(usernameForShare.get(i)))
                                                responseWithoutError.add(usernameForShare.get(i));
                                        }

                                    }
                                    else {
                                        if (!responseWithError.contains(usernameForShare.get(i)))
                                            responseWithError.add(usernameForShare.get(i));
                                    }
                                }

                                accessAnwerUser.setUserWithError(responseWithError);
                                accessAnwerUser.setUserWithoutError(responseWithoutError);

                            }
                            else {
                                return null;
                            }
                        }
                        else {
                            return null;
                        }
                    }

                    return accessAnwerUser;

                }
                else {
                    return null;
                }
            }
            else if (files != null) {
                return null;
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    public boolean checkAdmin(String username) {
        return userRepository.findByUsername(username).getRoles().contains(ADMIN);
    }

    @After
    public void clearTable() {
        fileRepository.deleteAll();
        userRepository.deleteAll();
        accessRepository.deleteAll();
    }

}
