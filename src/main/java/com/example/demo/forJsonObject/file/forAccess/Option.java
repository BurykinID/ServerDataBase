package com.example.demo.forJsonObject.file.forAccess;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class Option {

    private String option;

    public Option (String option) {
        this.option = option;
    }
}
