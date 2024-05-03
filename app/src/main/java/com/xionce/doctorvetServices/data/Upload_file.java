package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Upload_file {
    @Expose
    private String guid;
    @Expose
    private String file_base64;
    @Expose
    private String file_extension;
    @Expose
    private Boolean upload_finished;
    @Expose
    private String postfix;

    private byte[] byteArray;

    public Upload_file() {
    }
    public Upload_file(String guid) {
        this.guid = guid;
    }
    public Upload_file(String guid, String file_base64, String file_extension) {
        this.guid = guid;
        this.file_base64 = file_base64;
        this.file_extension = file_extension;
    }

    public String getGuid() {
        return guid;
    }
    public void setFile_base64(String file_base64) {
        this.file_base64 = file_base64;
    }
    public String getFile_extension() {
        return file_extension;
    }
    public void setUpload_finished(Boolean upload_finished) {
        this.upload_finished = upload_finished;
    }
    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }
    public byte[] getByteArray() {
        return byteArray;
    }
    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

}
