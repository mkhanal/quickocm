package org.quickocm.exception;

import java.util.Arrays;

public class UploadException extends RuntimeException {

    private String code;
    private String[] params = new String[0];

    public UploadException(String code) {
        super(code);
        this.code = code;
    }

    public UploadException(String code, String... params) {
        this.code = code;
        this.params = params;
    }

    @Override
    public String toString() {
        if (params.length == 0) return code;

        StringBuilder messageBuilder = new StringBuilder("code: " + code + ", params: { ");
        for (String param : params) {
            messageBuilder.append("; ").append(param);
        }
        messageBuilder.append(" }");
        return messageBuilder.toString().replaceFirst("; ", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UploadException that = (UploadException) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (!Arrays.equals(params, that.params)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (params != null ? Arrays.hashCode(params) : 0);
        return result;
    }

    public String getCode() {
        return code;
    }

    public String[] getParams() {
        return params;
    }
}
