package net.schmuse.bookish_giggle;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Error {

    @SerializedName("code")
    private Integer code;
    @SerializedName("msg")
    private String msg;

    public Error() {
        //no args constructor
    }

    public Error(ErrorCode code) {
        this.code = code.getErrorCode();
        this.msg = code.getErrorMsg();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static String toJsonError(ErrorCode code) {
        Error error = new Error(code);
        Gson gson = new Gson();
        return gson.toJson(error);
    }
}
