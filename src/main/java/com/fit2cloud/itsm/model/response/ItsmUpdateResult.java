package com.fit2cloud.itsm.model.response;


public class ItsmUpdateResult {


    private ErrorMsg error;

    private String data;


    public ItsmUpdateResult() {
    }


    public ErrorMsg getError() {
        return error;
    }

    public void setError(ErrorMsg error) {
        this.error = error;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public class ErrorMsg {

        private Integer code;

        private String message;


        public ErrorMsg() {
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

}
