package de.zeroco.apm.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.zeroco.apm.dao.ApmDao;
import jakarta.servlet.http.HttpSession;

@Service
public class ApmService {

	@Autowired
	ApmDao apmDao;
	@Autowired
	HttpSession session;
	
	public final String SCHEMA = "apm";
	public final String TABLE = "server";
	public String[] columns = {"environment", "client", "application", "host", "status", "first_success_on", "last_update", "restart", "created_id", "created_time", "modified_id", "modified_time"};
	public String[] updateColumns = {"status", "first_success_on", "first_failure_on", "last_update", "modified_id", "modified_time"};
	
	/**
	 * the method is used, to check the data is exist in database, if data is prest it update the data else insert the data.
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param reqData
	 * @return
	 */
	public Map<String, Object> save(Map<String, Object> reqData) {
		String status = (boolean)reqData.get("status") == true ? "success" : "failure";
		reqData.replace("status", status);
		Map<String, Object> serverData = apmDao.get((String)reqData.get("environment"), (String)reqData.get("client"), (String)reqData.get("application"), (String)reqData.get("host"));
		if (serverData != null) {
			return update(reqData, serverData);
		} else {
			return insert(reqData, "server");
		}
	}
	
	/**
	 * this method is used to process the request data and forward to dao class for save operations
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param reqData
	 * @param tableName
	 * @return
	 */
	public Map<String, Object> insert(Map<String, Object> reqData, String tableName) {
		reqData.put("firstOn", session.getAttribute("time"));
		columns[5] = reqData.get("status").equals("success") ? "first_success_on" : "first_failure_on";
		reqData.put("lastUpdate", session.getAttribute("time"));
		reqData.put("restart", 1);
		reqData.put("createdId", session.getAttribute("id"));
		reqData.put("createdTime", session.getAttribute("time"));
		reqData.put("modifiedId", session.getAttribute("id"));
		reqData.put("modifiedTime", session.getAttribute("time"));
		return apmDao.save(reqData, getInsertQuery(SCHEMA, tableName, Arrays.asList(columns))) > 0 ? reqData : null;
	}
	
	/**
	 * this method is used to process the request data and forward to dao class for update operations
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param reqData
	 * @param serverData
	 * @return
	 */
	public Map<String, Object> update(Map<String, Object> reqData, Map<String, Object> serverData) {
		String query = getUpdateQuery(SCHEMA, TABLE, Arrays.asList(updateColumns));
		if (serverData.get("status").equals("success") && reqData.get("status").equals("failure")) {
			serverData.replace("status", "failure");
			serverData.replace("first_failure_on", session.getAttribute("time"));
			insert(reqData, "server_audit");
		} else if (serverData.get("status").equals("failure") && reqData.get("status").equals("success")) {
			serverData.replace("status", "success");
			if (serverData.get("first_success_on") == null) {
				serverData.replace("first_success_On", session.getAttribute("time"));
			}
			insert(reqData, "server_audit");
		}
		Map<String, Object> responseData = responseMap(serverData, (LocalDateTime) session.getAttribute("time"));
		serverData.replace("last_update", session.getAttribute("time"));
		serverData.put("modifiedId", session.getAttribute("id"));
		serverData.put("modifiedTime", session.getAttribute("time"));
		return apmDao.update(serverData, query) > 0 ? responseData : null;
	}
	
	/**
	 * this method is used to process the request data and forward to dao class for delete operations
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param reqData
	 * @return status
	 */
	public String delete(Map<String, Object> reqData) {
		return apmDao.delete(reqData) > 0 ? "Deleted Successfully" : "Deleted Unsuccessful";
	}
	
	/**
	 * this method is used to process the request data and forward to dao class for get operations
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param reqData
	 * @return responseData
	 */
	public Map<String, Object> get(Map<String, Object> reqData) {
		return apmDao.get((String)reqData.get("environment"), (String)reqData.get("client"), (String)reqData.get("application"), (String)reqData.get("host"));
	}
	
	/**
	 * this method is used to process the request data and forward to dao class for list operations
	 * @author Praveen D
	 * @since 2023-08-08
	 * @return responseData
	 */
	public List<Map<String, Object>> list() {
		return apmDao.list();
	}
	
	/**
	 * the method is used to generate the insert query base on input parameters
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param schema
	 * @param tableName
	 * @param columns
	 * @return query
	 */
	public String getInsertQuery(String schema, String tableName, List<String> columns) {
		String col = "";
		String placeHolders = "";
		for (String column : columns) {
			col += "," + "`" + column + "`";
			placeHolders += ",?";
		}
		return "INSERT INTO `" + schema + "`.`" + tableName + "`(" + col.substring(1) + ") VALUES (" + placeHolders.substring(1) + ");";
	}
	
	/**
	 * this method is used to generate update query based on input parameters
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param schema
	 * @param tableName
	 * @param columns
	 * @return query
	 */
	public String getUpdateQuery(String schema, String tableName, List<String> columns) {
		String setCondition = "";
		for (String column : columns) {
			setCondition += "," + "`" + column + "`" + "= ?";
		}
		return "UPDATE `" + schema + "`.`" + tableName + "` SET " + setCondition.substring(1) + " WHERE (`pk_id` = ?);";
	}
	
	/**
	 * this method is used to get the user data based on key value
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param value
	 * @return matchData
	 */
	public List<Map<String,Object>> getMatchDataFromDB(String value) {
		List<Map<String, Object>> serverData = apmDao.list();
		List<Map<String, Object>> matchData = new ArrayList<>();
		for (Map<String, Object> rowData : serverData) {
			if(rowData.get("client").equals(value) || rowData.get("environment").equals(value) || rowData.get("host").equals(value) || rowData.get("application").equals(value)) {
				matchData.add(rowData);
			}
		}
		return matchData;
	}
	
	/**
	 * this method is used to process the request data, forward to dao and get sorted data
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param columnName
	 * @return
	 */
	public List<Map<String, Object>> getSortDataFromDB(String columnName) {
		return apmDao.list(columnName);
	}
	
	/**
	 * this method is used to process the request data, forward to dao and the data based on page size
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param columnName
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	public List<Map<String, Object>> listDataByPageSize(String columnName, int pageSize, int pageNumber) {
		return apmDao.list(columnName, pageSize, pageNumber);
	}
	
	/**
	 * this method is used to process the map and generate the response
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param reqData
	 * @param reqTime
	 * @return responseData
	 */
	public Map<String, Object> responseMap(Map<String, Object> reqData, LocalDateTime reqTime) {
		Map<String, Object> responseData = new LinkedHashMap<>();
		LocalDateTime lastUpdate = (LocalDateTime) reqData.get("last_update");
		responseData.put("success", reqData.get("status").equals("success") ? true : false);
		if ((boolean) responseData.get("success")) {
			LocalDateTime firstSuccessOn = reqTime;
			responseData.put("first_success_on", firstSuccessOn);
			responseData.put("duration", getDurationBetweenTwoDatesInMinutes(lastUpdate, firstSuccessOn));
		} else {
			LocalDateTime firstFailureOn = reqTime;
			responseData.put("first_failure_on", firstFailureOn);
			responseData.put("duration", getDurationBetweenTwoDatesInMinutes(lastUpdate, firstFailureOn));
		}
		responseData.put("last_update", reqData.get("last_update"));
		responseData.put("restart", reqData.get("restart"));
		return responseData;
	}
	
	/**
	 * this method is used to calculate the difference between to dates in minutes
	 * @author Praveen D
	 * @since 2023-08-08
	 * @param fromDate
	 * @param toDate
	 * @return minutes
	 */
	public long getDurationBetweenTwoDatesInMinutes(LocalDateTime fromDate, LocalDateTime toDate) {
		return Duration.between(fromDate, toDate).toMinutes();
	}
	
}
