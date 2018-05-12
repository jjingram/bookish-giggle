package net.schmuse.bookish_giggle;

public enum ErrorCode {

    CONTENT_TYPE_HEADER_REQUIRED(0, "Content-Type header required");

    private Integer code;
    private String msg;

    ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getErrorCode() {
        return code;
    }

    public String getErrorMsg() {
        return msg;
    }

}