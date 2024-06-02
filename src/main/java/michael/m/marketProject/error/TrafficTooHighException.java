package michael.m.marketProject.error;

public class TrafficTooHighException extends RuntimeException{
    public TrafficTooHighException() {
        super("Traffic is high at the moment so your request could not be processed. Please try again in a few seconds.");
    }
}