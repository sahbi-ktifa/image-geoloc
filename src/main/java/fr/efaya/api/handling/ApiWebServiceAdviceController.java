package fr.efaya.api.handling;

import fr.efaya.api.BadGeolocationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

/**
 * Created by sktifa on 28/07/2017.
 */
@RestControllerAdvice
public class ApiWebServiceAdviceController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(BadGeolocationException.class)
    public ErrorMessage unacceptableGeolocation() {
        return new ErrorMessage("Acceptable geolocation not found", new Date());
    }
}
