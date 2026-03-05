package pharmacie.config;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

// Define the ApiError class or use an existing one
class ApiError {
    private HttpStatus status;
    private String message;
    private String details;

    // Constructeur généré par @AllArgsConstructor
    public ApiError(HttpStatus status, String message, String details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }

    // Getters and setters
    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
