package de.zeroco.apm.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.zeroco.apm.dao.UserDao;
import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

	@Autowired
	UserDao userDao;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	HttpSession httpSession;
	
	/**
	 * this method is used to process the reqest data and forward to dao for save operation
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param reqData
	 * @return Map<String, Object>
	 */
	public Map<String, Object> save(Map<String, Object> reqData) {
		if(!(userDao.get((String) reqData.get("login_unique")) == null)) return null;
		reqData.put("role", "USER");
		reqData.put("password", passwordEncoder.encode((String) reqData.remove("password")));
		reqData.put("is_locked", reqData.get("role").equals("admin") ? false : true);
		reqData.put("created_id", 1);
		reqData.put("created_time", LocalDateTime.now());
		reqData.put("modified_id", 1);
		reqData.put("modified_time", LocalDateTime.now());
		return userDao.save(reqData) > 0 ? reqData : null;
	}
	
	/**
	 * this method is used to process the reqest data and forward to dao for update operation
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param userName
	 * @param reqData
	 * @return Map<String, Object>
	 */
	public Map<String, Object> update(String userName, Map<String, Object> reqData) {
		Map<String, Object> userDataInDB = userDao.get(userName);
		if(userDataInDB.isEmpty()) return null;
		if(!isBlank(reqData.get("password"))) {
			reqData.put("password_history", userDataInDB.get("password"));
			reqData.replace("password", passwordEncoder.encode((String) reqData.get("password")));
		}
		reqData.put("created_id", 1);
		reqData.put("created_time", LocalDateTime.now());
		reqData.put("modified_id", 1);
		reqData.put("modified_time", LocalDateTime.now());
		return userDao.update(getUpdatedQuery(reqData, userName)) > 0 ? reqData : null;
	}
	
	/**
	 * this method is used for soft delete the user data
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param userName
	 * @return String
	 */
	public String softDelete(String userName) {
		String query = "UPDATE `apm`.`user` SET `user`.`is_deleted` = true WHERE `user`.`login_unique` = '" + userName + "';";
		return userDao.update(query) > 0 ? "Deleted Successfully" : "Deleted Unsuccessful";
	}
	
	/**
	 * this method is used to process the request data and forward to delete operation
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param userName
	 * @return String
	 */
	public String hardDelete(String userName) {
		return userDao.delete(userName) > 0 ? "Deleted Successfully" : "Deleted Unsuccessful";
	}
	
	/**
	 * this method is used to store old password in password history field and update new password in password field
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param password
	 * @param userName
	 * @return String
	 */
	public String updatePassword(String password, String userName) {
		password = passwordEncoder.encode(password);
		Map<String, Object> exictedUserData = userDao.get(userName);
		if (exictedUserData == null) {
			return "Data Not Found";
		}
		String query = "UPDATE `apm`.`user` SET `user`.`password` ='"+ password +"', `user`.`password_history` ='"+ exictedUserData.get("password") +"' WHERE `user`.`login_unique` = '" + userName + "';";
		return userDao.update(query) > 0 ? "Password Updated Successfully" : "Password Updated Unsuccessful";
	}
	
	/**
	 * this method is used to process the request forward to dao for get opeartions
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param userName
	 * @return Map<String, Object>
	 */
	public Map<String, Object> get(String userName) {
		return userDao.get(userName);
	}
	
	/**
	 * this method is used to process the request to dao and get the whole data inform lo list
	 * @author Praveen D
	 * @since 2023-08-08
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> list() {
		return userDao.list();
	}
	
	/**
	 * this method is used to generate the update query
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param reqData
	 * @param userName
	 * @return query
	 */
	public String getUpdatedQuery(Map<String, Object> reqData, String userName) {
		String setCondition = "";
		for (String column : reqData.keySet()) {
			setCondition += ",`" + column + "`= '" + reqData.get(column) + "'";
		}
		return "UPDATE `apm`.`user` SET " + setCondition.substring(1) + " WHERE (`login_unique`= '" + userName + "');";
	}
	
	/**
	 * this method is used for admin to update the user role in database
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param userName
	 * @param role
	 * @return String
	 */
	public String updateRole(String userName, String role) {
		String query = "UPDATE `apm`.`user` SET `user`.`role` ='"+ role +"' WHERE `user`.`login_unique` = '" + userName + "';";
		return userDao.update(query) > 0 ? "Role Updated Successfully" : "Role Updated Unsuccessful";
	}
	
	/**
	 * this method is used to validate the give data is empty or not
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param input
	 * @return boolean
	 */
	public boolean isBlank(Object input) {
		if (input == null) return true;
		else if (input instanceof String) {
			if (((String) input).trim().equals("")) return true;
		} else if (input instanceof Byte) {
			if ((byte) input <= 0) return true;
		} else if (input instanceof Short) {
			if ((short) input <= 0) return true;
		} else if (input instanceof Integer) {
			if ((int) input <= 0) return true;
		} else if (input instanceof Long) {
			if ((long) input <= 0) return true;
		} else if (input instanceof Float) {
			if ((float) input <= 0) return true;
		} else if (input instanceof Double) {
			if ((double) input <= 0.0) return true;
		} else if (input instanceof Boolean) {
			if ((boolean) input == false) return true;
		} else if (input instanceof Character) {
			if ((char) input == ' ') return true;
		} else if (input instanceof Map) {
			if (((Map<?, ?>) input).isEmpty()) return true;
		} else if (input instanceof Object[]) {
			if (((Object[]) input).length == 0) return true;
		} else if (input instanceof Collection) {
			if (((Collection<?>) input).isEmpty()) return true;
		}
		return false;
	}
	
	/**
	 * this method to get the matched data from database by using key value
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param value
	 * @return
	 */
	public Map<String, Object> getUserDataByValue(String value) {
		List<Map<String, Object>> userData = userDao.list();
		for (Map<String, Object> rowData : userData) {
			if (rowData.get("login_unique").equals(value) || rowData.get("email").equals(value) || rowData.get("phone_number").equals(value)) {
				return rowData;
			}
		}
		return null;
	}
	
}
