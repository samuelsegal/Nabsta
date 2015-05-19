package com.spazomatic.nabsta.db;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table IMAGE.
 */
public class Image {

    private Long id;
    private String name;
    private String file_name;

    public Image() {
    }

    public Image(Long id) {
        this.id = id;
    }

    public Image(Long id, String name, String file_name) {
        this.id = id;
        this.name = name;
        this.file_name = file_name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

}