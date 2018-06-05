/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public class DataValidateException extends RuntimeException {

//    private Map<Integer, String> errors = new HashMap<>();
    public DataValidateException(Exception e) {
        super();
    }

    public DataValidateException(String message) {
        super(message);
    }
//    public void addError(int code, String message) {
//        if (errors.containsKey(code)) {
//            message = errors.get(code) + "\n" + message;
//        }
//        errors.put(code, message);
//    }
//
//    public Map<Integer, String> getErrors() {
//        return errors;
//    }

}
