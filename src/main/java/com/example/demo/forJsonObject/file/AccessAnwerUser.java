package com.example.demo.forJsonObject.file;

import com.example.demo.forJsonObject.user.Username;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/* для возврата положительного ответа на запрос о выделении прав вида:
    [
        { "userWithoutError" :
            [
                {"username" : "value"}
            ]
        },
        { "userWithError" :
            [
                {"username" : "value"}
            ]
        }
    ]
*/
@Data
@NoArgsConstructor
@Getter
@Setter
public class AccessAnwerUser {

    private ArrayList<Username> userWithoutError;
    private ArrayList<Username> userWithError;

}
