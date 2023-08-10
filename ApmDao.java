package de.zeroco.apm.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class ApmDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public final String DELETE_QUERY = "DELETE FROM `apm`.`server` WHERE server.environment = ? AND server.client = ? AND server.application = ? AND server.host = ?;";
	public final String GET_QUERY = "SELECT * FROM `apm`.`server` WHERE server.environment = ? AND server.client = ? AND server.application = ? AND server.host = ?;";
	public final String LIST_QUERY = "SELECT * FROM `apm`.`server;";
	
	/**
	 * this method is used to save the data into database
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param map
	 * @param query
	 * @return
	 */
	public int save(Map<String, Object> map, String query) {
		return jdbcTemplate.update(query, map.get("environment"), map.get("client"), map.get("application"), map.get("host"), map.get("status"), map.get("firstOn"), map.get("lastUpdate"), map.get("restart"), map.get("createdId"), map.get("createdTime"), map.get("modifiedId"), map.get("modifiedTime"));
	}
	
	/**
	 * this method is used to update the data into database
	 * @author Praveen D
	 * @since 2023-08-08 
	 * @param map
	 * @param query
	 * @return
	 */
	public int update(Map<String, Object> map, String query) {
		return jdbcTemplate.update(query, map.get("status"), map.get("first_success_on"), map.get("first_failure_on"), map.get("last_update"), map.get("modifiedId"), map.get("modifiedTime"), map.get("pk_id"));
	}
	
	/**
	 * this method is used to delete the data from database
	 * @author Praveen D
	 * @since 2023-08-08 
	 * @param map
	 * @return
	 */
	public int delete(Map<String, Object> map) {
		try {
			return jdbcTemplate.update(DELETE_QUERY, map.get("environment"), map.get("client"), map.get("application"), map.get("host"));
		} catch (DataAccessException e) {
			return 0;
		}
	}
	
	/**
	 * this method is used to get the data from database
	 * @author Praveen D
	 * @since 2023-08-08 
	 * @param environment
	 * @param client
	 * @param application
	 * @param host
	 * @return
	 */
	public Map<String, Object> get(String environment, String client, String application, String host) {
		try {
			return jdbcTemplate.queryForMap(GET_QUERY, environment, client, application, host);
		} catch (DataAccessException e) {
			return null;
		}
	}
	
	/**
	 * this method is used to get the data based on page size in the form of list
	 * @author Praveen D
	 * @since 2023-08-08 
	 * @param columnName
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	public List<Map<String, Object>> list(String columnName, int pageSize, int pageNumber) {
		int offSet = pageSize * (pageNumber - 1);
		try {
			return jdbcTemplate.queryForList("SELECT * FROM `apm`.`server` ORDER BY " + columnName + " ASC LIMIT ? OFFSET ? ;", pageSize, offSet);
		} catch (DataAccessException e) {
			return null;
		}
	}
	
	/**
	 * this method is used to get the data in the form of list
	 * @author Praveen D
	 * @since 2023-08-08 
	 * @return
	 */
	public List<Map<String, Object>> list() {
		try {
			return jdbcTemplate.queryForList(LIST_QUERY);
		} catch (DataAccessException e) {
			return null;
		}
	}
	
	/**
	 * this method is used to get the sort data in the form of list
	 * @author Praveen D
	 * @since 2023-08-08 
	 * @param columnName
	 * @return
	 */
	public List<Map<String, Object>> list(String columnName) {
		try {
			return jdbcTemplate.queryForList("SELECT * FROM `apm`.`server` ORDER BY " + columnName + " ASC;");
		} catch (DataAccessException e) {
			return null;
		}
	}
}
