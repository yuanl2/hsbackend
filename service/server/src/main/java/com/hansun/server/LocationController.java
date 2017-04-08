package com.hansun.server;

import com.hansun.dto.*;
import com.hansun.server.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@RestController
@RequestMapping("service/api/v1")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostConstruct
    private void init() {

    }

    @RequestMapping(value = "locations", method = RequestMethod.POST)
    public ResponseEntity createLocation(@RequestBody Location location, HttpServletRequest request) {
        Location l = locationService.createLocation(location);
        return ResponseEntity.ok(l);
    }

    @RequestMapping(value = "locations", method = RequestMethod.GET)
    public ResponseEntity<List<Location>> getLocationByUserID(@RequestParam(value = "userID", required = false, defaultValue = "1") int userID,
                                                              HttpServletRequest request) {
        List<Location> list = locationService.getLocationByUserID(userID);
        return new ResponseEntity<List<Location>>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "locations/{id}", method = RequestMethod.GET)
    public Location getLocation(@PathVariable int id, HttpServletRequest request) {

        return locationService.getLocation(id);
    }

    @RequestMapping(value = "locations/{id}", method = RequestMethod.PUT)
    public Location updateLocation(@PathVariable int id, @RequestBody Location Location, HttpServletRequest request) {
        return locationService.updateLocation(Location);
    }

    @RequestMapping(value = "locations/{id}", method = RequestMethod.DELETE)
    public void deleteLocation(@PathVariable int id,
                               @RequestParam(value = "provinceID", required = false, defaultValue = "1") int provinceID,
                               @RequestParam(value = "userID", required = false, defaultValue = "1") int userID,
                               @RequestParam(value = "cityID", required = false, defaultValue = "1") int cityID,
                               HttpServletRequest request) {
        if (provinceID > 1) {
            locationService.deleteLocationByProvinceID(provinceID);
            return;
        }

        if (userID > 1) {
            locationService.deleteLocationByUserID(userID);
            return;
        }
        if (cityID > 1) {
            locationService.deleteLocationByCityID(cityID);
            return;
        }
        locationService.deleteLocationByLocationID(id);
    }

    @RequestMapping(value = "locations/province", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<Province> createProvince(@RequestBody Province province, HttpServletRequest request) {
        Province l = locationService.createProvince(province);
        return ResponseEntity.ok(l);
    }

    @RequestMapping(value = "locations/province/{id}", method = RequestMethod.DELETE)
    public void deleteProvince(@PathVariable int id, HttpServletRequest request) {
        locationService.deleteProvinceByProvinceID(id);
        return;
    }

    @RequestMapping(value = "locations/province/{id}", method = RequestMethod.PUT)
    public Province updateProvince(@PathVariable int id, @RequestBody Province province, HttpServletRequest request) {
        return locationService.updateProvince(province);
    }

    @RequestMapping(value = "locations/province/{id}", method = RequestMethod.GET)
    public Province getProvince(@PathVariable int id, HttpServletRequest request) {
        return locationService.getProvince(id);
    }

    @RequestMapping(value = "locations/city", method = RequestMethod.POST)
    public ResponseEntity<City> createCity(@RequestBody City city, HttpServletRequest request) {
        City l = locationService.createCity(city);
        return ResponseEntity.ok(l);
    }

    @RequestMapping(value = "locations/city/{id}", method = RequestMethod.DELETE)
    public void deleteCity(@PathVariable int id, HttpServletRequest request) {
        locationService.deleteCityByCityID(id);
        return;
    }

    @RequestMapping(value = "locations/city/{id}", method = RequestMethod.PUT)
    public City updateCity(@PathVariable int id, @RequestBody City city, HttpServletRequest request) {
        return locationService.updateCity(city);
    }

    @RequestMapping(value = "locations/city/{id}", method = RequestMethod.GET)
    public City getCity(@PathVariable int id, HttpServletRequest request) {
        return locationService.getCity(id);
    }

    @RequestMapping(value = "locations/area", method = RequestMethod.POST)
    public ResponseEntity<Area> createArea(@RequestBody Area area, HttpServletRequest request) {
        Area l = locationService.createArea(area);
        return ResponseEntity.ok(l);
    }

    @RequestMapping(value = "locations/area/{id}", method = RequestMethod.DELETE)
    public void deleteArea(@PathVariable int id, HttpServletRequest request) {
        locationService.deleteAreaByAreaID(id);
        return;
    }

    @RequestMapping(value = "locations/area/{id}", method = RequestMethod.PUT)
    public Area updateArea(@PathVariable int id, @RequestBody Area area, HttpServletRequest request) {
        return locationService.updateArea(area);
    }

    @RequestMapping(value = "locations/area/{id}", method = RequestMethod.GET)
    public Area getArea(@PathVariable int id, HttpServletRequest request) {
        return locationService.getArea(id);
    }
}
