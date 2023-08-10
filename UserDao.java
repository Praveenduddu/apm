package de.zeroco.apm.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public final String INSERT_QUERY = "INSERT INTO `apm`.`user` (name, email, phone_number, password, role, login_unique, is_locked, created_id, created_time, modified_id, modified_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	public final String DELETE_QUERY = "DELETE FROM `apm`.`user` WHERE `user`.`login_unique` = ?;";
	public final String GET_QUERY = "SELECT * FROM `apm`.`user` WHERE `user`.`login_unique` = ? AND `user`.`is_deleted` = false;";
	public final String LIST_QUERY = "SELECT * FROM `apm`.`user` WHERE `user`.`is_deleted` = false;";
	
	/**
	 * this method is used to save the data into database
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param reqData
	 * @return int
	 */
	public int save(Map<String, Object> reqData) {
		try {
			return jdbcTemplate.update(INSERT_QUERY, reqData.get("name"), reqData.get("email"), reqData.get("phone_number"), reqData.get("password"), reqData.get("role"), reqData.get("login_unique"), reqData.get("is_locked"), reqData.get("created_id"), reqData.get("created_time"),  reqData.get("modified_id"), reqData.get("modified_time"));
		} catch (DataAccessException e) {
			return 0;
		}
	}
	
	/**
	 * this method is used to update the data into database
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param query
	 * @return int
	 */
	public int update(String query) {
		try {
			return jdbcTemplate.update(query);
		} catch (DataAccessException e) {
			return 0;
		}
	}
	
	/**
	 * this method is used to delete the data from database
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param userName
	 * @return int
	 */
	public int delete(String userName) {
		try {
			return jdbcTemplate.update(DELETE_QUERY, userName);
		} catch (DataAccessException e) {
			return 0;
		}
	}
	
	/**
	 * this method is used to get the data from database
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param userName
	 * @return Map<String, Object>
	 */
	public Map<String, Object> get(String userName) {
		try {
			return jdbcTemplate.queryForMap(GET_QUERY, userName);
		} catch (DataAccessException e) {
			return null;
		}
	}
	
	/**
	 * this method is used to list the data from database
	 * @author Praveen D
	 * @since 2023-08-08
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> list() {
		try {
			return jdbcTemplate.queryForList(LIST_QUERY);
		} catch (DataAccessException e) {
			return null;
		}
	}
}
