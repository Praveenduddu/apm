package de.zeroco.apm.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.zeroco.apm.service.ApmService;
import de.zeroco.apm.service.UserService;
import io.swagger.annotations.Api;

@RestController
@Api(tags = "apm")
@RequestMapping("/api/server")
public class ApmController {

	@Autowired
	ApmService serverService;
	@Autowired
	UserService userService;
	
	/**
	 * this method used for save and update, validate the request data, forward to service class and return the response data
	 * @author Praveen D
	 * @since 2023-07-04
	 * @param reqData
	 * @return
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@PostMapping("/save")
	public ResponseEntity<Map<String, Object>> save(@RequestBody Map<String, Object> reqData) {
		if(userService.isBlank(reqData.get("environment")) || userService.isBlank(reqData.get("client")) || userService.isBlank(reqData.get("application")) || userService.isBlank(reqData.get("host"))) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(serverService.save(reqData));
	}
	
	/**
	 * this method is used for delete, validate the request data, forward to service class and return the status
	 * @author Praveen D
	 * @since 2023-07-04
	 * @param reqData
	 * @return
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@DeleteMapping("/delete")
	public ResponseEntity<String> delete(@RequestBody Map<String, Object> reqData) {
		if(userService.isBlank(reqData.get("environment")) || userService.isBlank(reqData.get("client")) || userService.isBlank(reqData.get("application")) || userService.isBlank(reqData.get("host"))) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(serverService.delete(reqData));
	}
	
	/**
	 * this method is used for get, validate the request data, forward to service class and return the response
	 * @author Praveen D
	 * @since 2023-07-04
	 * @param reqData
	 * @return
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@GetMapping("/get")
	public ResponseEntity<Map<String, Object>> get(@RequestBody Map<String, Object> reqData) {
		if(userService.isBlank(reqData.get("environment")) || userService.isBlank(reqData.get("client")) || userService.isBlank(reqData.get("application")) || userService.isBlank(reqData.get("host"))) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(serverService.get(reqData));
	}
	
	/**
	 * this method is used for list, validate the request data, forward to service class and return the response
	 * @author Praveen D
	 * @since 2023-07-04
	 * @return
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@GetMapping("/list")
	public ResponseEntity<List<Map<String, Object>>> list() {
		return ResponseEntity.ok(serverService.list());
	}
	
	/**
	 * this method is used for search the give data in the table and return the response
	 * @author Praveen D
	 * @since 2023-07-04
	 * @param reqData
	 * @return
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@GetMapping("/list/search")
	public ResponseEntity<List<Map<String, Object>>> linearSearch(@RequestParam String reqData) {
		if(userService.isBlank(reqData)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(serverService.getMatchDataFromDB(reqData));
	}
	
	/**
	 * this method is used for sort the data from database and return the response
	 * @author Praveen D
	 * @since 2023-07-04
	 * @param columnName
	 * @return
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@GetMapping("/list/sort")
	public ResponseEntity<List<Map<String, Object>>> sort(@RequestParam String columnName) {
		if(userService.isBlank(columnName)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(serverService.getSortDataFromDB(columnName));
	}
	
	/**
	 * this method is used to pagination the data getting from the database
	 * @author Praveen D
	 * @since 2023-07-04
	 * @param columnName
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@GetMapping("/list/paging")
	public ResponseEntity<List<Map<String, Object>>> pagination(@RequestParam String columnName, int pageSize, int pageNumber) {
		if(userService.isBlank(columnName) || userService.isBlank(pageSize) || userService.isBlank(pageNumber)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(serverService.listDataByPageSize(columnName, pageSize, pageNumber));
	}
}
