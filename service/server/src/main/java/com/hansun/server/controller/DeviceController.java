package com.hansun.server.controller;

import com.hansun.server.dto.Device;
import com.hansun.server.dto.OrderInfo;
import com.hansun.server.dto.StatusReqeust;
import com.hansun.server.common.DeviceManagerStatus;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.dto.UserInfo;
import com.hansun.server.service.DeviceService;
import com.hansun.server.service.OrderService;
import com.hansun.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostConstruct
    private void init() {

    }

    @RequestMapping(value = "ping", method = RequestMethod.GET)
    public String ping() {
        return "get ping.";
    }

    /**
     * create one device object
     *
     * @param device
     * @param ucBuilder
     * @return
     */
    @RequestMapping(value = "device", method = RequestMethod.POST)
    public ResponseEntity<?> createDevice(@RequestBody Device device, UriComponentsBuilder ucBuilder) {
        Device d = deviceService.createDevice(device);
        return new ResponseEntity<>(d, HttpStatus.OK);
    }

    /**
     * create devices in list
     *
     * @param devices
     * @param ucBuilder
     * @return
     */
    @RequestMapping(value = "devices", method = RequestMethod.POST)
    public ResponseEntity<?> createDevices(@RequestBody List<Device> devices, UriComponentsBuilder ucBuilder) {
        List<Device> list = new ArrayList<>();
        devices.forEach(d -> list.add(deviceService.createDevice(d)));
        return new ResponseEntity<>(list, HttpStatus.CREATED);
    }

    @RequestMapping(value = "devices/fault", method = RequestMethod.GET)
    public ResponseEntity<?> getFaultDeviceByUserID(@RequestParam(value = "userID", required = false, defaultValue = "0") int userID,
                                                    HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        logger.debug("getFaultDeviceByUserID token {} user {}", token, userInfo.getUserName());

        boolean isAdmin = false;

        for (String access : userInfo.getAccess()
                ) {
            if (access.equalsIgnoreCase("admin")) {
                isAdmin = true;
            }
        }
        if (isAdmin) {
            List<Device> list = deviceService.getFaultDevices();
            return new ResponseEntity<>(list, HttpStatus.OK);
        }

        List<Device> list = deviceService.getFaultDevicesByUser(userInfo.getUserID());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * query all devices belongs to this user with userID
     *
     * @param userID
     * @param locationID
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "devices", method = RequestMethod.GET)
    public ResponseEntity<?> getDeviceByUserID(@RequestParam(value = "userID", required = false, defaultValue = "0") int userID,
                                               @RequestParam(value = "locationID", required = false, defaultValue = "1") int locationID,
                                               HttpServletRequest request, HttpServletResponse response) {

        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }

        boolean isAdmin = false;

        for (String access : userInfo.getAccess()
                ) {
            if (access.equalsIgnoreCase("admin")) {
                isAdmin = true;
            }
        }
        if (isAdmin) {
            List<Device> list = deviceService.getAllDevices();
            return new ResponseEntity<>(list, HttpStatus.OK);
        }

        if (locationID > 1) {
            List<Device> list = deviceService.getDevicesByLocationID(locationID);
            return new ResponseEntity<>(list, HttpStatus.OK);

        }
        List<Device> list = deviceService.getDevicesByUser(userInfo.getUserID());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "device/id/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getDevice(@PathVariable Long id, UriComponentsBuilder ucBuilder) {
        return new ResponseEntity<>(deviceService.getDevice(id), HttpStatus.OK);
    }

    @RequestMapping(value = "device/id/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateDevice(@PathVariable Long id, @RequestBody Device device, UriComponentsBuilder ucBuilder) {
        Device d = deviceService.updateDevice(device);
        return new ResponseEntity<>(d, HttpStatus.OK);
    }

    @RequestMapping(value = "device/id/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> updateDeviceManagerStatus(@PathVariable Long id, @RequestBody StatusReqeust statusReqeust, UriComponentsBuilder ucBuilder) {
        Device d = deviceService.updateDeviceManagerStatus(id, statusReqeust.getStatus());
        return new ResponseEntity<>(d, HttpStatus.OK);
    }

    @RequestMapping(value = "devices/managerstatus", method = RequestMethod.POST)
    public ResponseEntity<?> updateDeviceListManagerStatus(@RequestBody StatusReqeust statusReqeust, UriComponentsBuilder ucBuilder) {
        List<Device> d = deviceService.updateDeviceListManagerStatus(statusReqeust.getLists(), statusReqeust.getStatus());
        return new ResponseEntity<>(d, HttpStatus.OK);
    }

    @RequestMapping(value = "devices/consumetype", method = RequestMethod.POST)
    public ResponseEntity<?> updateDeviceListConsumeType(@RequestBody StatusReqeust statusReqeust, UriComponentsBuilder ucBuilder) {
        List<Device> d = deviceService.updateDeviceListConsumeType(statusReqeust.getLists(), statusReqeust.getStatus());
        return new ResponseEntity<>(d, HttpStatus.OK);
    }

    @RequestMapping(value = "device/id/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteByDeviceID(@PathVariable String id, UriComponentsBuilder ucBuilder) {
        deviceService.deleteDevice(Long.valueOf(id));
        return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "devices/id/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDevice(@PathVariable long id,
                                          @RequestParam(value = "locationID", required = false, defaultValue = "1") int locationID,
//                                          @RequestParam(value = "owner", required = false, defaultValue = "1") int owner,
                                          UriComponentsBuilder ucBuilder) {
        if (locationID > 1) {
            deviceService.deleteDeviceByLocationID(locationID);
            return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
        }
//        if (owner > 1) {
//            deviceService.deleteDeviceByOwner(owner);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
        deviceService.deleteDevice(id);
        return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/deviceStatus")
    public String deviceStatus(@RequestParam(value = "device_id", required = true, defaultValue = "0000000") String device_id,
                               @RequestParam(value = "pay_method", required = true, defaultValue = "wx") String pay_method,
                               HttpServletRequest request, HttpServletResponse response) throws IOException {
        Device d = deviceService.getDevice(Long.valueOf(device_id));
        OrderInfo o = orderService.getOrder(Long.valueOf(device_id));
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
        } else {
            logger.info("device_id = {} device managerStatus = {}", device_id, d.getManagerStatus());
            return String.valueOf(d.getManagerStatus());
        }
    }
}
