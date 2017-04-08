package com.hansun.server;

import com.hansun.dto.Device;
import com.hansun.server.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@RestController
@RequestMapping("/service/api/v1")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @PostConstruct
    private void init() {

    }

    @RequestMapping(value = "ping", method = GET)
    public String ping() {
        return "get ping.";
    }

    @RequestMapping(value = "devices", method = RequestMethod.POST)
    public ResponseEntity<Device> createDevice(@ModelAttribute Device device, HttpServletRequest request) {
        Device d = deviceService.createDevice(device);
        return new ResponseEntity<Device>(d, HttpStatus.OK);
    }

    @RequestMapping(value = "devices/{userID}", method = RequestMethod.GET)
    public ResponseEntity<List<Device>> getDeviceByUserID(@PathVariable int userID,
                                          @RequestParam(value = "locationID", required = false, defaultValue = "1") int locationID,
                                          HttpServletRequest request) {
        if (locationID > 1) {
            List<Device> list = deviceService.getDevicesByLocationID(locationID);
            return new ResponseEntity<List<Device>>(list,HttpStatus.OK);

        }
        List<Device> list =  deviceService.getDevicesByOwner(userID);
        return new ResponseEntity<List<Device>>(list,HttpStatus.OK);
    }

    @RequestMapping(value = "devices/id/{id}", method = RequestMethod.GET)
    public Device getDevice(@PathVariable int id, HttpServletRequest request) {
        return deviceService.getDevice(id);
    }

    @RequestMapping(value = "devices/id/{id}", method = RequestMethod.PUT)
    public Device updateDevice(@PathVariable int id, @RequestBody Device device, HttpServletRequest request) {
        return deviceService.updateDevice(device);
    }

    @RequestMapping(value = "devices/id/{id}", method = RequestMethod.DELETE)
    public void deleteDevice(@PathVariable int id,
                             @RequestParam(value = "locationID", required = false, defaultValue = "1") int locationID,
                             @RequestParam(value = "owner", required = false, defaultValue = "1") int owner,
                             HttpServletRequest request) {
        if (locationID > 1) {
            deviceService.deleteDeviceByLocationID(locationID);
            return;
        }
        if (owner > 1) {
            deviceService.deleteDeviceByOwner(owner);
            return;
        }
        deviceService.deleteDevice(id);
    }
}
