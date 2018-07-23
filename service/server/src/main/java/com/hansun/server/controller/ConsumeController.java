package com.hansun.server.controller;

import com.hansun.server.dto.Consume;
import com.hansun.server.service.ConsumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@RestController
@RequestMapping("/api")
public class ConsumeController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConsumeService consumeService;

    @PostConstruct
    private void init() {

    }

    @RequestMapping(value = "consume", method = RequestMethod.POST)
    public ResponseEntity<?> createConsume(@RequestBody Consume consume, UriComponentsBuilder ucBuilder) {
        logger.debug("create Consume ", consume);
        Consume u = consumeService.createConsume(consume);
        return new ResponseEntity<Consume>(u, HttpStatus.CREATED);
    }

    @RequestMapping(value = "consumes", method = RequestMethod.POST)
    public ResponseEntity<?> createConsumes(@RequestBody List<Consume> consume, UriComponentsBuilder ucBuilder) {
        logger.debug("create Consume list ", consume);
        consume.forEach(p -> consumeService.createConsume(p));
        return new ResponseEntity("create consumes success.", HttpStatus.CREATED);
    }

    @RequestMapping(value = "consume/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getConsume(@PathVariable int id, UriComponentsBuilder ucBuilder) {
        logger.debug("get Consume id ", id);
        Consume consume = consumeService.getConsume(id);
        return new ResponseEntity<Consume>(consume, HttpStatus.OK);
    }

    @RequestMapping(value = "consume/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateConsume(@RequestBody Consume consume, UriComponentsBuilder ucBuilder) {
        logger.debug("update Consume ", consume);
        Consume consume1 = consumeService.updateconsume(consume);
        return new ResponseEntity<Consume>(consume1, HttpStatus.OK);
    }

    @RequestMapping(value = "consume/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteconsume(@PathVariable int id, UriComponentsBuilder ucBuilder) {
        logger.debug("delete consume id ", id);
        consumeService.deleteConsume(id);
        return new ResponseEntity<Consume>(HttpStatus.NO_CONTENT);
    }
}
