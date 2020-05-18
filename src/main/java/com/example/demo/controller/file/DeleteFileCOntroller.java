package com.example.demo.controller.file;

import com.example.demo.config.component.JwtToken;
import com.example.demo.entity.Access;
import com.example.demo.entity.File;
import com.example.demo.entity.User;
import com.example.demo.forJsonObject.user.Username;
import com.example.demo.repository.AccessRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.role.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.UUID;

@RestController()
public class DeleteFileCOntroller {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final AccessRepository accessRepository;
    private final JwtToken jwtToken;

    @Value ("${upload.path}")
    private String uploadPath;

    public DeleteFileCOntroller (FileRepository fileRepository, UserRepository userRepository, AccessRepository accessRepository, JwtToken jwtToken) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.accessRepository = accessRepository;
        this.jwtToken = jwtToken;
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity deleteFileForId(@RequestHeader("Authorization") String token,
                                          @PathVariable("id") String id) {

        User user = userRepository.findByUsername(jwtToken.getUsernameFromToken(token.substring(7)));

        if (user != null) {

            File file = fileRepository.findById(UUID.fromString(id));

            if (file != null) {

                String fileID = String.valueOf(file.getId());

                if (user.getRoles().contains(Role.ADMIN) ||
                        accessRepository.findByUsernameAndIdFile(user.getUsername(), String.valueOf(file.getId())).getAccess().equals("3"))
                {
                    java.io.File file1 = new java.io.File(uploadPath + file.getId());
                    file1.delete();
                    fileRepository.delete(file);

                    ArrayList <Access> forDelete = accessRepository.findByIdFile(fileID);
                    if (forDelete.size() > 0)

                        for (int i = 0; i < forDelete.size(); i++) {
                            accessRepository.delete(forDelete.get(i));
                        }


                }

                return new ResponseEntity("File was deleted", HttpStatus.OK);

            }

            return new ResponseEntity("File does not exists", HttpStatus.NOT_FOUND);

        }

        return new ResponseEntity("User does not found", HttpStatus.NOT_FOUND);


    }

    @DeleteMapping(value = "/delete/file/{filename}")
    public ResponseEntity deleteFileFilename(@RequestHeader ("Authorization") String token,
                                                @RequestBody Username username,
                                                @PathVariable ("filename") String filename) {

        User user = userRepository.findByUsername(jwtToken.getUsernameFromToken(token.substring(7)));

        if (user != null) {

            ArrayList<File> file = fileRepository.findByFilenameAndAuthor(filename, username.getUsername(), Sort.by("date").descending());

            if (file.size() > 0) {

                for (int i = 0; i < file.size(); i++) {

                    String fileID = String.valueOf(file.get(i).getId());

                    if (user.getRoles().contains(Role.ADMIN) ||
                        accessRepository.findByUsernameAndIdFile(user.getUsername(), String.valueOf(file.get(i).getId())).getAccess().equals("3"))
                        {
                            java.io.File file1 = new java.io.File(uploadPath + file.get(i).getId());
                            file1.delete();
                            fileRepository.delete(file.get(i));
                            ArrayList <Access> forDelete = accessRepository.findByIdFile(fileID);
                            if (forDelete.size() > 0)

                                for (int j = 0; j < forDelete.size(); j++) {
                                    accessRepository.delete(forDelete.get(j));
                                }
                        }

                }

                return new ResponseEntity("File was deleted", HttpStatus.OK);

            }

            return new ResponseEntity("File does not exists", HttpStatus.NOT_FOUND);

        }

        return new ResponseEntity("User does not found", HttpStatus.NOT_FOUND);

    }

}
