package michael.m.marketProject.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class TrafficTooHighException extends RuntimeException{
    public TrafficTooHighException() {
        super("Traffic is high at the moment so your request could not be processed. Please try again in a few seconds.");
    }
}