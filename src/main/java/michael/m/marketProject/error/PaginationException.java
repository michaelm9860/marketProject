package michael.m.marketProject.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PaginationException extends RuntimeException{
    public PaginationException(String message) {
        super(message);
    }
}
