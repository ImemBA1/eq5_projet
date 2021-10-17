package com.gestionnaire_de_stage.controller;

import com.gestionnaire_de_stage.dto.ResponseMessage;
import com.gestionnaire_de_stage.exception.EmailAndPasswordDoesNotExistException;
import com.gestionnaire_de_stage.exception.MonitorAlreadyExistsException;
import com.gestionnaire_de_stage.model.Monitor;
import com.gestionnaire_de_stage.repository.MonitorRepository;
import com.gestionnaire_de_stage.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private MonitorRepository monitorRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody Monitor monitor) {
        Monitor createdMonitor;
        try{
            createdMonitor = monitorService.create(monitor);
        } catch (MonitorAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Ce couriel est deja en utilisation!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Parametre null"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMonitor);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidRequests(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseMessage> handleEmptyRequestBody(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .badRequest()
                .body(new ResponseMessage(ex.getMessage()));
    }

    @GetMapping("/{email}/{password}")
    public ResponseEntity<?> login(@PathVariable String email,@PathVariable String password) {
        Monitor monitor;
        try{
            monitor = monitorService.getOneByEmailAndPassword(email,password);
        } catch (EmailAndPasswordDoesNotExistException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Erreur d'Authentification!"));
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(new ResponseMessage("Parametre null!"));
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(monitor);
    }
}
