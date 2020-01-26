package com.controller;

import com.dao.StorageDAO;
import com.model.Storage;
import com.service.StorageService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/storage")
public class StorageController {

    private StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService, StorageDAO dao) {
        this.storageService = storageService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/save")
    public ResponseEntity<Storage> save(@RequestBody Storage storage) {

        try {

            return new ResponseEntity<>(storageService.save(storage), HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get")
    public ResponseEntity<Storage> findById(@RequestParam(value = "id") long id) {

        try {

            Storage storage = storageService.findById(id);

            return new ResponseEntity<>(storage, HttpStatus.OK);
        } catch (NotFoundException e) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update")
    public ResponseEntity<Storage> update(@RequestBody Storage storage) {

        try {

            return new ResponseEntity<>(storageService.update(storage), HttpStatus.OK);

        } catch (HibernateOptimisticLockingFailureException e) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (DataIntegrityViolationException e) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/delete")
    public ResponseEntity<String> delete(@RequestParam(value = "id") Long id) {

        try {

            storageService.delete(id);
            return new ResponseEntity<>("Deleted suxessfuly", HttpStatus.OK);

        } catch (EntityNotFoundException e) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
