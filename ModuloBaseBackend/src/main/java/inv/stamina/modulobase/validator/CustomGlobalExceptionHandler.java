/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.validator;

import inv.stamina.modulobase.resources.dto.jwt.ExceptionResponse;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * mejorar el manejo en un futuro esto toma demasiado tiempo pero vale la pena
 * https://mkyong.com/spring-boot/spring-rest-error-handling-example/
 *
 * @author alepaco.maton
 */
@Log4j2
@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.info("handleMethodArgumentNotValid " + ex.getMessage(), ex);

        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        });
        ex.getBindingResult().getGlobalErrors().forEach((error) -> {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        });

        ExceptionResponse apiError
                = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), errors);
        ResponseEntity<Object> out = handleExceptionInternal(
                ex, apiError, headers, apiError.getStatus(), request);

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------RESPONSE----------------------\n").
                append("token ").append(request.getHeader(JwtTokenUtil.KEY_TOKEN)).append(", \n").
                append("DATA ").append(out).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return out;
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        log.info("handleMissingServletRequestParameter " + ex.getMessage(), ex);

        String error = "Se requiere este parametro " + ex.getParameterName() + ", del siguiente tipo " + ex.getParameterType();

        ExceptionResponse apiError
                = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), Arrays.asList(error));
        ResponseEntity<Object> out = new ResponseEntity<>(
                apiError, HttpHeaders.EMPTY, apiError.getStatus());

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------RESPONSE----------------------\n").
                append("token ").append(request.getHeader(JwtTokenUtil.KEY_TOKEN)).append(", \n").
                append("DATA ").append(out).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return out;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        log.info("handleConstraintViolation " + ex.getMessage(), ex);

        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach((violation) -> {
            errors.add(violation.getRootBeanClass().getName() + " "
                    + violation.getPropertyPath() + ": " + violation.getMessage());
        });

        ExceptionResponse apiError
                = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), errors);
        ResponseEntity<Object> out = new ResponseEntity<>(
                apiError, HttpHeaders.EMPTY, apiError.getStatus());

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------RESPONSE----------------------\n").
                append("token ").append(request.getHeader(JwtTokenUtil.KEY_TOKEN)).append(", \n").
                append("DATA ").append(out).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return out;
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.info("handleMethodArgumentTypeMismatch variable " + ex.getName()
                + ", typo requerido " + ex.getRequiredType()
                + ", nombrePropiedad " + ex.getPropertyName()
                + ", errorcode " + ex.getErrorCode()
                + ", valor recivido " + ex.getValue()
                + ", parametro " + ex.getParameter() + ", mensaje "
                + ex.getMessage(), ex);
        String error = ex.getName() + " debe ser del tipo " + ex.getRequiredType().getName();

        ExceptionResponse apiError
                = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), Arrays.asList(error));
        ResponseEntity<Object> out = new ResponseEntity<>(
                apiError, HttpHeaders.EMPTY, apiError.getStatus());

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------RESPONSE----------------------\n").
                append("token ").append(request.getHeader(JwtTokenUtil.KEY_TOKEN)).append(", \n").
                append("DATA ").append(out).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return out;
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        log.info("handleNoHandlerFoundException " + ex.getMessage(), ex);

        String error = "No se encuentra el mtodo " + ex.getHttpMethod() + ", con la url " + ex.getRequestURL();

        ExceptionResponse apiError = new ExceptionResponse(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), Arrays.asList(error));
        ResponseEntity<Object> out = new ResponseEntity<>(apiError, HttpHeaders.EMPTY, apiError.getStatus());

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------RESPONSE----------------------\n").
                append("token ").append(request.getHeader(JwtTokenUtil.KEY_TOKEN)).append(", \n").
                append("DATA ").append(out).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return out;
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.info("handleHttpRequestMethodNotSupported " + ex.getMessage(), ex);
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(
                " method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t).append(" "));

        ExceptionResponse apiError = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getLocalizedMessage(), Arrays.asList(builder.toString()));
        ResponseEntity<Object> out = new ResponseEntity<>(
                apiError, HttpHeaders.EMPTY, apiError.getStatus());

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------RESPONSE----------------------\n").
                append("token ").append(request.getHeader(JwtTokenUtil.KEY_TOKEN)).append(", \n").
                append("DATA ").append(out).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return out;
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.info("handleHttpMediaTypeNotSupported " + ex.getMessage(), ex);
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

        ExceptionResponse apiError = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getLocalizedMessage(), Arrays.asList(builder.substring(0, builder.length() - 2)));
        ResponseEntity<Object> out = new ResponseEntity<>(
                apiError, HttpHeaders.EMPTY, apiError.getStatus());

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------RESPONSE----------------------\n").
                append("token ").append(request.getHeader(JwtTokenUtil.KEY_TOKEN)).append(", \n").
                append("DATA ").append(out).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return out;
    }

    @ExceptionHandler({ApiException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            ApiException ex,
            WebRequest request) {
        log.info("handleMethodArgumentNotValid ApiException " + ex.getMessage(), ex);
        List<String> errors = new ArrayList<>();
        ex.getErrors().getFieldErrors().forEach((error) -> {
            errors.add(error.getDefaultMessage());//error.getField() + ": " + 
        });
        ex.getErrors().getGlobalErrors().forEach((error) -> {
            errors.add(error.getDefaultMessage()); //error.getObjectName() + ": " + 
        });

        ExceptionResponse apiError
                = new ExceptionResponse(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);

        ResponseEntity<Object> out = handleExceptionInternal(
                ex, apiError, HttpHeaders.EMPTY, apiError.getStatus(), request);

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------RESPONSE----------------------\n").
                append("token ").append(request.getHeader(JwtTokenUtil.KEY_TOKEN)).append(", \n").
                append("DATA ").append(out).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return out;

    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleExceptionEspecificos(Exception ex, WebRequest request) {
        try {
            log.info("handleExceptionEspecificos RuntimeException " + ex.getMessage(), ex);

            ExceptionResponse apiError = new ExceptionResponse(
                    HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), Arrays.asList(ex.getMessage()));

            ResponseEntity<Object> out = new ResponseEntity<>(
                    apiError, HttpHeaders.EMPTY, apiError.getStatus());

            StringBuilder sb = new StringBuilder();
            sb.append("-------------------RESPONSE----------------------\n").
                    append("token ").append(request.getHeader(JwtTokenUtil.KEY_TOKEN)).append(", \n").
                    append("DATA ").append(out).append(", \n").
                    append("------------------------------------------------\n");

            log.info(sb.toString());

            return out;
        } catch (Exception er) {
            return handleAll(er, request);
        }
    }

    @ExceptionHandler({Exception.class, ConnectException.class})
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {

        log.error("Error inesperado, " + ((ex == null) ? "" : ex.getMessage()), ex);

        ExceptionResponse apiError = new ExceptionResponse(
                HttpStatus.UNPROCESSABLE_ENTITY, ((ex == null) ? "Sn" : ex.getLocalizedMessage()), Arrays.asList(ex.getMessage()));

        ResponseEntity<Object> out = new ResponseEntity<>(apiError, HttpHeaders.EMPTY, apiError.getStatus());

        StringBuilder sb = new StringBuilder();
        sb.append("-------------------RESPONSE----------------------\n").
                append("token ").append(request.getHeader(JwtTokenUtil.KEY_TOKEN)).append(", \n").
                append("DATA ").append(out).append(", \n").
                append("------------------------------------------------\n");

        log.info(sb.toString());

        return out;
    }

}
