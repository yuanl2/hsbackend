package com.hansun.server.controller;

import com.hansun.dto.*;
import com.hansun.server.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@RestController
@RequestMapping("/api")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostConstruct
    private void init() {

    }

    @RequestMapping(value = "location", method = RequestMethod.POST)
    public ResponseEntity<?> createLocation(@RequestBody Location location, HttpServletRequest request) {
        Location l = locationService.createLocation(location);
        return new ResponseEntity<Location>(l, HttpStatus.CREATED);
    }

    @RequestMapping(value = "locations", method = RequestMethod.POST)
    public ResponseEntity<?> createLocations(@RequestBody List<Location> location, HttpServletRequest request) {
        location.forEach(p -> locationService.createLocation(p));
        return new ResponseEntity("create locations success.", HttpStatus.CREATED);
    }

    @RequestMapping(value = "location", method = RequestMethod.GET)
    public ResponseEntity<?> getLocationByUserID(@RequestParam(value = "userID", required = false, defaultValue = "1") int userID,
                                                              HttpServletRequest request) {
        List<Location> list = locationService.getLocationByUserID(userID);
        return new ResponseEntity<List<Location>>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "location/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getLocation(@PathVariable int id, HttpServletRequest request) {
        return new ResponseEntity<Location>(locationService.getLocation(id), HttpStatus.OK);
    }

    @RequestMapping(value = "location/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateLocation(@PathVariable int id, @RequestBody Location Location, HttpServletRequest request) {
        Location l = locationService.updateLocation(Location);
        return new ResponseEntity<Location>(l, HttpStatus.OK);
    }

    @RequestMapping(value = "location/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteLocation(@PathVariable int id,
                                            @RequestParam(value = "provinceID", required = false, defaultValue = "1") int provinceID,
                                            @RequestParam(value = "userID", required = false, defaultValue = "1") int userID,
                                            @RequestParam(value = "cityID", required = false, defaultValue = "1") int cityID,
                                            HttpServletRequest request) {
        if (provinceID > 1) {
            locationService.deleteLocationByProvinceID(provinceID);
            return new ResponseEntity<Location>(HttpStatus.NO_CONTENT);
        }

        if (userID > 1) {
            locationService.deleteLocationByUserID(userID);
            return new ResponseEntity<Location>(HttpStatus.NO_CONTENT);
        }
        if (cityID > 1) {
            locationService.deleteLocationByCityID(cityID);
            return new ResponseEntity<Location>(HttpStatus.NO_CONTENT);
        }
        locationService.deleteLocationByLocationID(id);
        return new ResponseEntity<Location>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "province", method = RequestMethod.POST)
    public ResponseEntity<?> createProvince(@RequestBody Province province, HttpServletRequest request) {
        Province l = locationService.createProvince(province);
        return new ResponseEntity("create province success.", HttpStatus.CREATED);
    }

    @RequestMapping(value = "provinces", method = RequestMethod.POST)
    public ResponseEntity<?> createProvinces(@RequestBody List<Province> province, UriComponentsBuilder ucBuilder) {
        province.forEach(p -> locationService.createProvince(p));
        return new ResponseEntity("create provinces success.", HttpStatus.CREATED);
    }

    @RequestMapping(value = "provinces", method = RequestMethod.GET)
    public ResponseEntity<?> getProvinces() {
        List<Province> result = locationService.getAllProvince();
        return new ResponseEntity<List<Province>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "province/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProvince(@PathVariable int id, HttpServletRequest request) {
        locationService.deleteProvinceByProvinceID(id);
        return new ResponseEntity<Province>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "province/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Province> updateProvince(@PathVariable int id, @RequestBody Province province, HttpServletRequest request) {
        Province p = locationService.updateProvince(province);
        return new ResponseEntity<Province>(p, HttpStatus.OK);
    }

    @RequestMapping(value = "province/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getProvince(@PathVariable int id, HttpServletRequest request) {
        return new ResponseEntity<Province>(locationService.getProvince(id), HttpStatus.OK);
    }

    @RequestMapping(value = "city", method = RequestMethod.POST)
    public ResponseEntity<?> createCity(@RequestBody City city, HttpServletRequest request) {
        City l = locationService.createCity(city);
        return new ResponseEntity("create city success.", HttpStatus.CREATED);
    }

    @RequestMapping(value = "citys", method = RequestMethod.POST)
    public ResponseEntity<?> createCitys(@RequestBody List<City> city, HttpServletRequest request) {
        city.forEach(p -> locationService.createCity(p));
        return new ResponseEntity("create citys success.", HttpStatus.CREATED);
    }

    @RequestMapping(value = "citys", method = RequestMethod.GET)
    public ResponseEntity<?> getCitys() {
        List<City> result = locationService.getAllCity();
        return new ResponseEntity<List<City>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "city/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCity(@PathVariable int id, HttpServletRequest request) {
        locationService.deleteCityByCityID(id);
        return new ResponseEntity<City>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "city/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCity(@PathVariable int id, @RequestBody City city, HttpServletRequest request) {
        return new ResponseEntity<City>(locationService.updateCity(city), HttpStatus.OK);
    }

    @RequestMapping(value = "city/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getCity(@PathVariable int id, HttpServletRequest request) {
        return new ResponseEntity<City>(locationService.getCity(id), HttpStatus.OK);
    }

    @RequestMapping(value = "area", method = RequestMethod.POST)
    public ResponseEntity<?> createArea(@RequestBody Area area, HttpServletRequest request) {
        Area l = locationService.createArea(area);
        return new ResponseEntity("create area success.", HttpStatus.CREATED);
    }

    @RequestMapping(value = "areas", method = RequestMethod.POST)
    public ResponseEntity<?> createAreas(@RequestBody List<Area> area, HttpServletRequest request) {
        area.forEach(p -> locationService.createArea(p));
        return new ResponseEntity("create areas success.", HttpStatus.CREATED);
    }

    @RequestMapping(value = "areas", method = RequestMethod.GET)
    public ResponseEntity<?> getAreas() {
        List<Area> result = locationService.getAllArea();
        return new ResponseEntity<List<Area>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "area/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteArea(@PathVariable int id, HttpServletRequest request) {
        locationService.deleteAreaByAreaID(id);
        return new ResponseEntity<Area>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "area/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateArea(@PathVariable int id, @RequestBody Area area, HttpServletRequest request) {
        return new ResponseEntity<Area>(locationService.updateArea(area), HttpStatus.OK);
    }

    @RequestMapping(value = "area/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getArea(@PathVariable int id, HttpServletRequest request) {
        return new ResponseEntity<Area>(locationService.getArea(id), HttpStatus.OK);
    }
}
