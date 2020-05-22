package controller;

import lombok.Data;

import java.sql.Date;

@Data
public class Fox {

    private int id;
    private String name;
    private String owner_fio;
    private Date birthday;
    private String color;
}
