package com.hansun.server;

import com.hansun.dto.Device;
import com.hansun.server.service.DeviceService;
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
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

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
                                               UriComponentsBuilder ucBuilder) {
        if (locationID > 1) {
            List<Device> list = deviceService.getDevicesByLocationID(locationID);
            return new ResponseEntity<List<Device>>(list, HttpStatus.OK);

        }
        List<Device> list = deviceService.getDevicesByOwner(userID);
        return new ResponseEntity<List<Device>>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "devices/id/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getDevice(@PathVariable String id, UriComponentsBuilder ucBuilder) {
        return new ResponseEntity<Device>(deviceService.getDevice(id), HttpStatus.OK);
    }

    @RequestMapping(value = "devices/id/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateDevice(@PathVariable int id, @RequestBody Device device, UriComponentsBuilder ucBuilder) {
        Device d = deviceService.updateDevice(device);
        return new ResponseEntity<Device>(d, HttpStatus.OK);
    }

    @RequestMapping(value = "devices/id/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDevice(@PathVariable String id,
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
}
