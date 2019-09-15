package com.controller;

import com.exception.BadRequestException;
import com.model.File;

import org.springframework.beans.factory.annotation.Autowired;
import com.service.FileService;
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
;

@Controller
@RequestMapping("/file")
public class FileController {

    private FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/save")
    public ResponseEntity<File> save(@RequestBody File file) {

        try {

            return new ResponseEntity<>(fileService.save(file), HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (BadRequestException e) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get")
    public ResponseEntity<File> findById(@RequestParam(value = "id") long id) {

        try {

            File file = fileService.findById(id);

            return new ResponseEntity<>(file, HttpStatus.OK);
        } catch (EntityNotFoundException e) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/update")
    public ResponseEntity<File> update(@RequestBody File file) {

        try {

            return new ResponseEntity<>(fileService.update(file), HttpStatus.OK);

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

            fileService.delete(id);
            return new ResponseEntity<>("Deleted suxessfuly", HttpStatus.OK);

        } catch (EntityNotFoundException e) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/putFile")
    public ResponseEntity<File> putFile(@RequestParam(value = "storageId") long storageId,
                                        @RequestParam(value = "fileId") long fileId) {

        try {

            return new ResponseEntity<>(fileService.put(storageId, fileId), HttpStatus.OK);

        } catch (EntityNotFoundException e) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/deleteFile")
    public ResponseEntity<String> deleteFile(@RequestParam(value = "storageId") long storageId,
                                             @RequestParam(value = "fileId") long fileId) {
        try {

            fileService.delete(storageId, fileId);

            return new ResponseEntity<>("File was deleted from storage", HttpStatus.OK);

        } catch (EntityNotFoundException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/transferAll")
    public ResponseEntity<String> transferAll(@RequestParam(value = "from") long stIdFrom,
                                             @RequestParam(value = "to") long stIdTo) {
        try {

            fileService.transferAll(stIdFrom, stIdTo);

            return new ResponseEntity<>("Files was transferred successfully", HttpStatus.OK);

        } catch (EntityNotFoundException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/transferFile")
    public ResponseEntity<String> transferAll(@RequestParam(value = "from") long stIdFrom,
                                              @RequestParam(value = "to") long stIdTo,
                                              @RequestParam(value = "fileId") long fileId) {
        try {

            fileService.transferFile(stIdFrom, stIdTo, fileId);

            return new ResponseEntity<>("Files was transferred successfully", HttpStatus.OK);

        } catch (EntityNotFoundException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
