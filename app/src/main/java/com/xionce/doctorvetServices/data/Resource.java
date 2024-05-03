package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.io.File;

public class Resource {

    public enum resource_types {IMAGE, VIDEO, AUDIO, FILE, DOWNLOAD}

    @Expose
    private String unique_id = HelperClass.generateGUID();
    @Expose
    private String table_name;
    @Expose
    private String table_id;
    @Expose
    private String options;
    @Expose
    private String notes;
    @Expose
    private String local_path;
    @Expose
    private String type = "PRIVATE";
    @Expose
    private Integer deleted;

    private String file_name;
    private String file_url;
    private String url;

    public Resource(final String local_path) {
        this.local_path = local_path;
        this.file_name = HelperClass.getFileNameFromPath(local_path);
    }
    public Resource(final String local_path, String table_name) {
        this.local_path = local_path;
        this.file_name = HelperClass.getFileNameFromPath(local_path);
        this.table_name = table_name;
    }
    public Resource(final String local_path, String table_name, String options) {
        this.local_path = local_path;
        this.file_name = HelperClass.getFileNameFromPath(local_path);
        this.table_name = table_name;
        setOptions(options);
    }

    public String getUnique_id() {
        return unique_id;
    }
    public String getExtension() {
        return local_path.substring(local_path.lastIndexOf("."));
    }
    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }
    public String getFile_name() {
        return file_name;
    }
    public String getFile_url() {
        return file_url;
    }
    public byte[] getByteArray() {
        File file = new File(local_path);
        return HelperClass.fileToByteArray(file);
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getAndroid_dir_type() {
        return HelperClass.getAndroidDirType(file_name); //android_dir_type;
    }
    public String getLocal_path() {
        return local_path;
    }
    public void setLocal_path(String local_path) {
        this.local_path = local_path;
    }
    public resource_types getResourceType() {
        if (!fileExists())
            return resource_types.DOWNLOAD;

        String extension = getExtension().toLowerCase();

        if (extension.equals(".jpg") || extension.equals(".jpeg"))
            return resource_types.IMAGE;

        if (extension.equals(".mp4"))
            return resource_types.VIDEO;

        if (extension.equals(".mp3"))
            return resource_types.AUDIO;

        return resource_types.FILE;
    }
    public Integer getDeleted() {
        return deleted;
    }
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTable_name() {
        return table_name;
    }
    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }
    public String getOptions() {
        return options;
    }
    public void setOptions(String options) {
        this.options = options;
        if (options.equalsIgnoreCase("IS_THUMB_HIGH") || options.equalsIgnoreCase("IS_THUMB"))
            this.type = "PUBLIC";
    }

    public boolean fileExists() {
        File file = new File(getLocal_path());
        if (file.exists())
            return true;

        return false;
    }

}