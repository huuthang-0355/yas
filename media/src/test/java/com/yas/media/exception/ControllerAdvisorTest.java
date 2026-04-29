package com.yas.media.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.UnsupportedMediaTypeException;
import com.yas.media.viewmodel.ErrorVm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

class ControllerAdvisorTest {

    private ControllerAdvisor advisor;
    private WebRequest webRequest;
    private HttpServletRequest servletRequest;

    @BeforeEach
    void setUp() {
        advisor = new ControllerAdvisor();
        webRequest = mock(ServletWebRequest.class);
        servletRequest = mock(HttpServletRequest.class);
        when(((ServletWebRequest) webRequest).getRequest()).thenReturn(servletRequest);
        when(servletRequest.getServletPath()).thenReturn("/test-path");
    }

    @Test
    void handleUnsupportedMediaTypeException_ShouldReturnBadRequest() {
        UnsupportedMediaTypeException ex = new UnsupportedMediaTypeException("Unsupported");
        ResponseEntity<ErrorVm> response = advisor.handleUnsupportedMediaTypeException(ex, webRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        NotFoundException ex = new NotFoundException("Not found");
        ResponseEntity<ErrorVm> response = advisor.handleNotFoundException(ex, webRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleMethodArgumentNotValid_ShouldReturnBadRequest() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "defaultMessage");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorVm> response = advisor.handleMethodArgumentNotValid(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleConstraintViolation_ShouldReturnBadRequest() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);

        when(violation.getRootBeanClass()).thenAnswer(invocation -> Object.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("violation message");
        when(ex.getConstraintViolations()).thenReturn(Set.of(violation));

        ResponseEntity<ErrorVm> response = advisor.handleConstraintViolation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleIoException_ShouldReturnInternalServerError() {
        RuntimeException ex = new RuntimeException("Runtime exception");
        ResponseEntity<ErrorVm> response = advisor.handleIoException(ex, webRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void handleOtherException_ShouldReturnInternalServerError() {
        Exception ex = new Exception("Generic exception");
        ResponseEntity<ErrorVm> response = advisor.handleOtherException(ex, webRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
