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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.zeroco.apm.service.UserService;

@RestController
public class UserController {

	@Autowired
	UserService userService;
	
	/**
	 * this method is used to handle the save request and return the generated response
	 * @author Praveen D
	 * @since 2023-08-08 
	 * @param reqData
	 * @return ResponseEntity<Map<String, Object>>
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@PostMapping("/save")
	public ResponseEntity<Map<String, Object>> save(@RequestBody Map<String, Object> reqData) {
		if(userService.isBlank(reqData.get("name")) || userService.isBlank(reqData.get("email")) || userService.isBlank(reqData.get("phone_number")) || userService.isBlank(reqData.get("password")) || userService.isBlank(reqData.get("login_unique"))) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(userService.save(reqData));
	}
	
	/**
	 * this method is used to handle the update request and return the generated response
	 * @author Praveen D
	 * @since 2023-08-08 
	 * @param userName
	 * @param reqData
	 * @return ResponseEntity<Map<String, Object>>
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@PutMapping("/update")
	public ResponseEntity<Map<String, Object>> update(@RequestParam String userName, @RequestBody Map<String, Object> reqData) {
		if(userService.isBlank(userName) || userService.isBlank(reqData)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return ResponseEntity.ok(userService.update(userName, reqData));
	}
	
	/**
	 * this method is used to handle hard delete request and return the generated response
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param userName
	 * @return ResponseEntity<String>
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/hard/delete")
	public ResponseEntity<String> hardDelete(@RequestParam String userName) {
		if(userService.isBlank(userName)) {
			return new ResponseEntity<>("Data Not Sufficient", HttpStatus.BAD_REQUEST); 
		}
		return ResponseEntity.ok(userService.hardDelete(userName));
	}
	
	/**
	 * this method is used to handle soft delete request and return the generated response
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param userName
	 * @return ResponseEntity<String>
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@DeleteMapping("/soft/delete")
	public ResponseEntity<String> softDelete(@RequestParam String userName) {
		if(userService.isBlank(userName)) {
			return new ResponseEntity<>("Data Not Sufficient", HttpStatus.BAD_REQUEST); 
		}
		return ResponseEntity.ok(userService.softDelete(userName));
	}
	
	/**
	 * this method is used to handle update password request and return the generated response
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param password
	 * @param userName
	 * @return ResponseEntity<String>
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@PutMapping("/update/password")
	public ResponseEntity<String> updatePassword(@RequestParam String password, String userName) {
		if(userService.isBlank(userName) || userService.isBlank(password)) {
			return new ResponseEntity<>("Data Not Sufficient", HttpStatus.BAD_REQUEST); 
		}
		return ResponseEntity.ok(userService.updatePassword(password, userName));
	}
	
	/**
	 * this method is used to handle update role request and return the generated response
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param role
	 * @param userName
	 * @return ResponseEntity<String>
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/update/role")
	public ResponseEntity<String> updateRole(@RequestParam String userName, String role) {
		if(userService.isBlank(userName) || userService.isBlank(role)) {
			return new ResponseEntity<>("Data Not Sufficient", HttpStatus.BAD_REQUEST); 
		}
		return ResponseEntity.ok(userService.updateRole(userName, role));
	}
	
	/**
	 * this method is used to handle get request and return the generated response
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param userName
	 * @return ResponseEntity<Map<String, Object>>
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@GetMapping("/get")
	public ResponseEntity<Map<String, Object>> get(@RequestParam String userName) {
		if(userService.isBlank(userName)) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST); 
		}
		return ResponseEntity.ok(userService.get(userName));
	}
	
	/**
	 * this method is used to handle list request and return the generated response
	 * @author Praveen D
	 * @since 2023-08-08
	 * @return ResponseEntity<List<Map<String, Object>>>
	 */
	@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
	@GetMapping("/list")
	public ResponseEntity<List<Map<String, Object>>> list() {
		return ResponseEntity.ok(userService.list());
	}
	
}
