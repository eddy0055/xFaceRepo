package com.xpand.xface.bean.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcabi.log.Logger;
import com.xpand.xface.dao.BoatDAO;
import com.xpand.xface.dao.HtmlPageInfoDAO;
import com.xpand.xface.dao.LocationMapDAO;
import com.xpand.xface.dao.RoleInfoDAO;
import com.xpand.xface.dao.UserInfoDAO;
import com.xpand.xface.entity.Boat;
import com.xpand.xface.entity.HtmlPageInfo;
import com.xpand.xface.entity.LocationMap;
import com.xpand.xface.entity.RoleInfo;
import com.xpand.xface.entity.UserInfo;
import com.xpand.xface.util.LogUtil;

/**
 * Class representing the result of the server-side pagination. Its JSON
 * serialization can be privided to the client as table data structure.
 *
 * @author David Castelletti
 *
 */
public class TablePage {

	/**
	 * The draw counter that this object is a response to - from the draw parameter
	 * sent as part of the data request.
	 */
	private int draw;

	/**
	 * Total records, before filtering.
	 */
	private long recordsTotal;

	/**
	 * Total records, after filtering (i.e. the total number of records after
	 * filtering has been applied - not just the number of records being returned
	 * for this page of data).
	 */
	private long recordsFiltered;

	/**
	 * The data to be displayed in the table. This is an array of data source
	 * objects, one for each row, which will be used by DataTables.
	 */
	private List<Map<String, Object>> data;

	/**
	 * If an error occurs during the running of the server-side processing script,
	 * you can inform the user of this error by passing back the error message to be
	 * displayed using this parameter. Do not include if there is no error.
	 */
	@JsonInclude(Include.NON_EMPTY)
	private String error;

	public TablePage() {
		
	}
	public TablePage(String transactionId, JpaRepository<?,?> repoDAO, PaginationCriteria pc) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in TablePage constructor with parameter pc:"+pc.toString()));		
		PageRequest pq = null;
		Sort sort = null;		
		int indexColumn = 0;
		if (pc.getOrder().size()>0){
			Logger.info(this, LogUtil.getLogInfo(transactionId, "sort column:"+pc.getColumns().get(pc.getOrder().get(0).getColumn()).getName())+" order:"+pc.getOrder().get(0).getDir());
			indexColumn = pc.getOrder().get(0).getColumn();			
			if ("asc".equals(pc.getOrder().get(0).getDir())){
				sort = new Sort(Sort.Direction.ASC, pc.getColumns().get(indexColumn).getName());
			}else {
				sort = new Sort(Sort.Direction.DESC, pc.getColumns().get(indexColumn).getName());
			}			
		}else {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "no sort information send along with request"));			
		}		
		int pageNumber = pc.getStart() / pc.getLength();
		if (sort==null) {
			pq = new PageRequest(pageNumber, pc.getLength());
		}else {
			pq = new PageRequest(pageNumber, pc.getLength(), sort);
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, "page number is "+pageNumber));		
				
		Page<?> result = repoDAO.findAll(pq);
		if (repoDAO instanceof RoleInfoDAO) {
			RoleInfo roleInfo = null;
			for (Object content:result.getContent()) {			
				roleInfo = (RoleInfo) content;
				roleInfo.setRoleDetailInfoList(null);
				roleInfo.setUserInfoList(null);
				roleInfo.getHtmlPageInfo().setRoleInfoList(null);
			}
		}else if (repoDAO instanceof UserInfoDAO) {
			UserInfo userInfo = null;
			for (Object content:result.getContent()) {			
				userInfo = (UserInfo) content;
				//userInfo.setRoleInfo(null);
				userInfo.getRoleInfo().setHtmlPageInfo(null);
				userInfo.getRoleInfo().setRoleDetailInfoList(null);
				userInfo.getRoleInfo().setUserInfoList(null);
			}
		}else if(repoDAO instanceof HtmlPageInfoDAO){
			HtmlPageInfo htmlPageInfo = null;
			for(Object content:result.getContent()) {
				htmlPageInfo = (HtmlPageInfo) content;
				htmlPageInfo.setRoleInfoList(null);
			}	
		}else if(repoDAO instanceof LocationMapDAO){	
			LocationMap locationMap = null;
			for(Object content:result.getContent()) {
				locationMap  = (LocationMap) content;
				locationMap.setHwIPCList(null);				
				locationMap.setHwGateInfoList(null);
			}
		}else if(repoDAO instanceof BoatDAO){
			Boat boatInfo = null; 
			for(Object content:result.getContent()) {
				boatInfo = (Boat) content;
				boatInfo.setBoatScheduleList(null);
			}
		}
		
		this.draw = pc.getDraw();
        this.recordsTotal = result.getTotalElements();        
        this.recordsFiltered = result.getTotalElements();
        try {
        	Logger.info(this, LogUtil.getLogInfo(transactionId, "get data"));
        	this.data = this.getPageEntries(transactionId, result.getContent());
        	Logger.info(this, LogUtil.getLogInfo(transactionId, "generate data done"));
        }catch (Exception ex) {        	
        	Logger.error(this, LogUtil.getLogError(transactionId, "error while get data in paging:"+repoDAO.getClass().getName(), ex));       	
        }        
        Logger.info(this, LogUtil.getLogInfo(transactionId, "out TablePage"));
	}
	public TablePage(String transactionId, PaginationCriteria pc, Page<?> result) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in TablePage constructor with result"));		
		this.draw = pc.getDraw();
        this.recordsTotal = result.getTotalElements();        
        this.recordsFiltered = result.getTotalElements();
        try {
        	Logger.info(this, LogUtil.getLogInfo(transactionId, "get data"));
        	this.data = this.getPageEntries(transactionId, result.getContent());        	
        	Logger.info(this, LogUtil.getLogInfo(transactionId, "generate data done"));
        }catch (Exception ex) {        	
        	Logger.error(this, LogUtil.getLogError(transactionId, "error while get data in paging", ex));       	
        }        
        Logger.info(this, LogUtil.getLogInfo(transactionId, "out TablePage"));
	}
	public List<Map<String, Object>> getPageEntries(String transactionId, List<?> dataList) throws Exception {                
        Logger.info(this, LogUtil.getLogInfo(transactionId, "Table data retrieved..."));
        List<Map<String, Object>> records = new ArrayList<>(dataList.size());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            dataList.forEach(i -> {
                @SuppressWarnings("unchecked")
				Map<String, Object> m = objectMapper.convertValue(i, Map.class);                
                records.add(m.entrySet().stream()
                        .collect(Collectors.toMap(k -> k.getKey(), 
                        		v ->  v.getValue()==null?"":v.getValue())));                
            });                        
            
            Logger.info(this, LogUtil.getLogInfo(transactionId, "Data map generated..."));
        } catch (Exception e) {
        	Logger.info(this, LogUtil.getLogInfo(transactionId, "Error fetching page entries."));
            throw e;
        }
        return records;
    }
	
	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public long getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public long getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
