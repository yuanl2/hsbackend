package com.hansun.server.controller;

import com.hansun.dto.Device;
import com.hansun.dto.Order;
import com.hansun.server.common.DeviceManagerStatus;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.service.DeviceService;
import com.hansun.server.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@RestController
@RequestMapping("/api")
public class DeviceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private OrderService orderService;

    @PostConstruct
    private void init() {

    }

    @RequestMapping(value = "ping", method = RequestMethod.GET)
    public String ping() {
        return "get ping.";
    }

    @RequestMapping(value = "device", method = RequestMethod.POST)
    public ResponseEntity<?> createDevice(@RequestBody Device device, UriComponentsBuilder ucBuilder) {
        Device d = deviceService.createDevice(device);
        return new ResponseEntity<Device>(d, HttpStatus.OK);
    }

    @RequestMapping(value = "devices", method = RequestMethod.POST)
    public ResponseEntity<?> createDevices(@RequestBody List<Device> device, UriComponentsBuilder ucBuilder) {
        List<Device> list = new ArrayList<>();
        device.forEach(d -> list.add(deviceService.createDevice(d)));
        return new ResponseEntity<List<Device>>(list, HttpStatus.CREATED);
    }

    @RequestMapping(value = "devices/{userID}", method = RequestMethod.GET)
    public ResponseEntity<?> getDeviceByUserID(@PathVariable int userID,
                                               @RequestParam(value = "locationID", required = false, defaultValue = "1") int locationID,
                                               HttpServletRequest request, HttpServletResponse response) {

        String auth = request.getHeader("Authorization");
        String decode = null;
        try {
            decode = new String(java.util.Base64.getDecoder().decode(auth.substring(6,auth.length())),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String user = decode.split(":")[0];
        if (locationID > 1) {
            List<Device> list = deviceService.getDevicesByLocationID(locationID);
            return new ResponseEntity<List<Device>>(list, HttpStatus.OK);

        }
        List<Device> list = deviceService.getDevicesByOwner(userID);
        return new ResponseEntity<List<Device>>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "devices/id/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getDevice(@PathVariable Long id, UriComponentsBuilder ucBuilder) {
        return new ResponseEntity<Device>(deviceService.getDevice(id), HttpStatus.OK);
    }

    @RequestMapping(value = "devices/id/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @RequestBody Device device, UriComponentsBuilder ucBuilder) {
        Device d = deviceService.updateDevice(device);
        return new ResponseEntity<Device>(d, HttpStatus.OK);
    }

    @RequestMapping(value = "devices/id/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDevice(@PathVariable Long id,
                                          @RequestParam(value = "locationID", required = false, defaultValue = "1") int locationID,
                                          @RequestParam(value = "owner", required = false, defaultValue = "1") int owner,
                                          UriComponentsBuilder ucBuilder) {
        if (locationID > 1) {
            deviceService.deleteDeviceByLocationID(locationID);
            return new ResponseEntity<Device>(HttpStatus.NO_CONTENT);
        }
        if (owner > 1) {
            deviceService.deleteDeviceByOwner(owner);
            return new ResponseEntity<Device>(HttpStatus.NO_CONTENT);
        }
        deviceService.deleteDevice(id);
        return new ResponseEntity<Device>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping("/deviceStatus")
    public String deviceStatus(@RequestParam(value = "device_id", required = true, defaultValue = "0000000") String device_id,
                               @RequestParam(value = "pay_method", required = true, defaultValue = "wx") String pay_method,
                               HttpServletRequest request, HttpServletResponse response) throws IOException {
        Device d = deviceService.getDevice(Long.valueOf(device_id));
        Order o = orderService.getOrder(Long.valueOf(device_id));
        if (d.getManagerStatus() == DeviceManagerStatus.OPERATING.getStatus()) {
            if (d.getStatus() == DeviceStatus.SERVICE) {
                logger.error("device {} running", device_id);
                return String.valueOf(DeviceStatus.SERVICE);
            } else if (d.getStatus() == DeviceStatus.IDLE && o != null && (o.getOrderStatus() <= OrderStatus.SERVICE)) {
                logger.info("device {} idle but have order {}", device_id, o);
                return String.valueOf(DeviceStatus.STARTTASK);
            }

            if (d != null) {
                logger.info("device_id = {} device status = {}", device_id, d.getStatus());
                return String.valueOf(d.getStatus());
            } else {
                logger.error("can't get device for device_id = {}", device_id);
                return String.valueOf(DeviceStatus.INVALID);
            }
        }
        else {
            logger.info("device_id = {} device managerStatus = {}", device_id, d.getManagerStatus());
            return  String.valueOf(d.getManagerStatus());
        }
    }
}
