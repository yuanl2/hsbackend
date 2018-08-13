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
import java.util.ArrayList;
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
        Consume u = consumeService.createConsume(consume);
        return new ResponseEntity<>(u, HttpStatus.CREATED);
    }

    @RequestMapping(value = "consumes", method = RequestMethod.POST)
    public ResponseEntity<?> createConsumes(@RequestBody List<Consume> consume, UriComponentsBuilder ucBuilder) {
        List<Consume> lists = new ArrayList<>();
        consume.forEach(p -> lists.add(consumeService.createConsume(p)));
        return new ResponseEntity(lists, HttpStatus.CREATED);
    }

    @RequestMapping(value = "consume/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getConsume(@PathVariable short id, UriComponentsBuilder ucBuilder) {
        Consume consume = consumeService.getConsume(id);
        return new ResponseEntity<>(consume, HttpStatus.OK);
    }

    @RequestMapping(value = "consume/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateConsume(@RequestBody Consume consume, UriComponentsBuilder ucBuilder) {
        Consume consume1 = consumeService.updateconsume(consume);
        return new ResponseEntity<>(consume1, HttpStatus.OK);
    }

    @RequestMapping(value = "consume/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteconsume(@PathVariable short id, UriComponentsBuilder ucBuilder) {
        consumeService.deleteConsume(id);
        return new ResponseEntity<Consume>(HttpStatus.NO_CONTENT);
    }
}
