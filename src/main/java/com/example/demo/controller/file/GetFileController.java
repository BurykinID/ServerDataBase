package com.example.demo.controller.file;

import com.example.demo.entity.File;
import com.example.demo.forJsonObject.Response;
import com.example.demo.repository.FileRepository;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.Base64;

public class GetFileController {

    private final FileRepository fileRepository;

    public GetFileController (FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    // success, но надо обсудить
    @GetMapping (value = {"/getfile/{filename}"})
    public String getFile(@PathVariable (name = "filename") String filename) {

        File file = fileRepository.findByFilename(filename);

        Gson gson = new Gson();
        Response response = new Response();

        try {
            byte[]fileContent = FileUtils.readFileToByteArray(new java.io.File(file.getPath()));
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            String responseString = gson.toJson(encodedString);
            return responseString;
        } catch (IOException e) {
            response.setStatus("error");
            response.setDescription("error with encode Base 64");
        }

        String responseString = gson.toJson(response);

        return responseString;

    }

    /*
    only for test with docker

    @GetMapping(value = {"/check"})
    public String checkFile() {

        try {
            BufferedReader bufferedReader = new BufferedReader( new FileReader("/var/lib/postgresql/data/aaaa.txt"));
            try {
                String s = null;
                while ((s = bufferedReader.readLine()) != null) {
                    System.out.println(s);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return "check.html";
    }*/

}
