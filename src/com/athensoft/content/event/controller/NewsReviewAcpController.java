package com.athensoft.content.event.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.athensoft.content.event.entity.Event;
import com.athensoft.content.event.entity.EventMedia;
import com.athensoft.content.event.entity.EventReview;
import com.athensoft.content.event.entity.News;
import com.athensoft.content.event.service.EventMediaService;
import com.athensoft.content.event.service.EventReviewService;
import com.athensoft.content.event.service.NewsService;
import com.athensoft.util.UUIDHelper;

/**
 * News Review Controller for ACP
 * @author Athens
 * @version 1.0
 */
@Controller
public class NewsReviewAcpController {
	
	private static final Logger logger = Logger.getLogger(NewsReviewAcpController.class);
	
	/**
	 * News Service instance
	 */
	private NewsService newsService;
	
	@Autowired
	public void setNewsService(NewsService newsService) {
		this.newsService = newsService;
	}
	
	/**
	 * EventReview Service instance
	 */
	private EventReviewService eventReviewService;
		
	@Autowired
	public void setEventReviewService(EventReviewService eventReviewService) {
		this.eventReviewService = eventReviewService;
	}
	
	/**
	 * get news review objects in JSON data form, which comply with criteria
	 * the data for showing in datatable in front-end pages is contained in a 2-dimension array
	 * @param itemJSONString search criteria object in JSON format
	 * @return a map structure containing data rendered to views
	 */
	@RequestMapping(value="/events/newsReviewSearchFilterData",produces="application/json")
	@ResponseBody
	public Map<String, Object> getDataSearchNewsReviewByFilter(@RequestParam String itemJSONString){
		logger.info("entering /events/newsReviewSearchFilterData");
		
		ModelAndView mav = new ModelAndView();
		
		//data
		Map<String, Object> model = mav.getModel();
		JSONObject jobj= new JSONObject(itemJSONString);
		
		String where1 = jobj.getString("eventReviewNo").trim();
		
		/* where2a, where2b */
		String strEventReviewDateFrom = jobj.getString("eventReviewDateFrom").trim();
		String strEventReviewDateTo= jobj.getString("eventReviewDateTo").trim();
		
		if(strEventReviewDateFrom==null){
			strEventReviewDateFrom = "";
		}
		if(strEventReviewDateTo==null){
			strEventReviewDateTo = "";
		}
		String where2a = strEventReviewDateFrom;
		String where2b = strEventReviewDateTo;
		
		logger.info("strViewNumFrom="+strEventReviewDateFrom+"##");
		logger.info("strViewNumTo="+strEventReviewDateTo+"##");
		
		String where3 = jobj.getString("eventReviewCustomer").trim();
		String where4 = jobj.getString("eventReviewContent").trim();
		int where5 = jobj.getInt("eventReviewStatus");
		
		/* construct query string */
		StringBuffer queryString = new StringBuffer();
		queryString.append(where1.length()==0?" ":" and review_uuid like '%"+where1+"%' ");
		
		
		String queryString_where2 = "";
		if(strEventReviewDateFrom.equals("")&&strEventReviewDateTo.equals("")){
			queryString_where2 = " ";
		}else if(!strEventReviewDateFrom.equals("")&&strEventReviewDateTo.equals("")){
			/* select * from event_review where date(post_datetime) >= adddate('2017-01-12', -1); */
			queryString_where2 = " and date(review_datetime) >= '"+where2a+"' ";
		}else if(strEventReviewDateFrom.equals("")&&!strEventReviewDateTo.equals("")){
			/* select * from event_review where date(post_datetime) <= '2017-02-05'; */
			queryString_where2 = " and date(review_datetime) <= '"+where2b+"' ";
		}else if(!strEventReviewDateFrom.equals("")&&!strEventReviewDateTo.equals("")){
			/*
			select * from event_news where date(post_datetime) between adddate('2017-01-12', -1) and '2017-02-05';
			*/
			
			int dateFlag = strEventReviewDateFrom.compareTo(strEventReviewDateTo);
			if(dateFlag<0){
				queryString_where2 = " and (date(review_datetime) between adddate('"+where2a+"', -1) and '"+where2b+"' ) ";
			}else if(dateFlag==0){
				queryString_where2 = " and (date(review_datetime) =  '"+where2a+"' ) ";
			}else{
				queryString_where2 = " and (date(review_datetime) between adddate('"+where2b+"', -1) and '"+where2a+"' ) ";
			}
		}
		queryString.append(queryString_where2);
		
		queryString.append(where3.length()==0?" ":" and customer_id like '%"+where3+"%' ");
		queryString.append(where4.length()==0?" ":" and review_content like '%"+where4+"%' ");
				
		queryString.append(where5==0?" ":" and review_status = "+where5+" ");
		
		logger.info("QueryString = "+ queryString.toString());
		
//		List<Event> listNews = newsService.getNewsByFilter(queryString.toString());
//		logger.info("Length of news entries = "+ listNews.size());
		
		List<EventReview> listEventReview = eventReviewService.getEventReviewByFilter(queryString.toString());
		logger.info("Length of news review entries = "+ listEventReview.size());
		
		
		int entryLength = listEventReview.size();
		final int COLUMN_NUM = 6;		//the number of column showing in the data-table at the front web page
		String[][] data = new String[entryLength][COLUMN_NUM];
		
		String field0 = "";	//review UUID, review no.
		String field1 = "";	//review datetime
		String field2 = "";	//review customer
		String field3 = "";	//review content
		String field4 = "";	//review status
		String field5 = "";	//action
		
		for(int i=0; i<entryLength ; i++){			
			field0 = listEventReview.get(i).getReviewUUID()+"";
			field1 = listEventReview.get(i).getReviewDatetime()+"";
			field2 = listEventReview.get(i).getCustomerId()+"";
			field3 = listEventReview.get(i).getReviewContent();
			
			
			int intEventReviewStatus = listEventReview.get(i).getReviewStatus();
			String eventReviewStatus = "";
			String eventReviewStatusKey = "";
			switch(intEventReviewStatus){
				case EventReview.APPROVED: 
					eventReviewStatus = "Approved";
					eventReviewStatusKey = "success";
					break;
				case EventReview.PENDING: 
					eventReviewStatus = "Pending";
					eventReviewStatusKey = "warning";
					break;
				case EventReview.REJECTED: 
					eventReviewStatus = "Rejected";
					eventReviewStatusKey = "default";
					break;
				default: 
					break;
			}
			
			field4 = "<span class='label label-sm label-"+eventReviewStatusKey+"'>"+eventReviewStatus+"</span>";
			field5 = "<a href='/acp/events/eventsNewsReviewEdit?reviewUUID="+field1+"' class='btn btn-xs default btn-editable'><i class='fa fa-pencil'></i> Edit</a>";
			
			data[i][0] = field0;
			data[i][1] = field1;
			data[i][2] = field2;
			data[i][3] = field3;
			data[i][4] = field4;
			data[i][5] = field5;
		}
		
		model.put("draw", new Integer(1));
		model.put("recordsTotal", new Integer(5));
		model.put("recordsFiltered", new Integer(5));
		model.put("data", data);
		model.put("customActionStatus","OK");
		model.put("customActionMessage","OK");
		
		logger.info("leaving /events/newsReviewSearchFilterData");
		
		return model;
	}
	
	
	
	
	/**
	 * get news review objects in JSON data form<p>
	 * WARNING: DO NOT GET ALL EVENTREVIEW OBJECT IN PRODUCT. JUST FOR TEST.
	 * @return data table of new review objects
	 */
	@RequestMapping(value="/events/eventsNewsReviewListData",produces="application/json")
	@ResponseBody
	public Map<String,Object> getDataNewsReviewList(){
		logger.info("entering /events/eventsNewsReviewListData");
		
		ModelAndView mav = new ModelAndView();
		Map<String, Object> model = mav.getModel();
		
		//data
		List<EventReview> listEventReview = eventReviewService.getAllEventReview();
		logger.info("Length of EventReview entries: "+ listEventReview.size());
		
		int entryLength = listEventReview.size();
		final int COLUMN_NUM = 6;
		String[][] data = new String[entryLength][COLUMN_NUM];
		
		String field0 = "";	//review uuid
		String field1 = "";	//review datatime
		String field2 = ""; //customer id
		String field3 = "";	//review content
		String field4 = "";	//review status
		String field5 = "";	//action
	
		
		for(int i=0; i<entryLength ; i++){			
//			field0 = "<input type='checkbox' name='id[]' value="+listEventReview.get(i).getEventUUID()+">";
			field0 = listEventReview.get(i).getReviewUUID()+"";
			field1 = listEventReview.get(i).getReviewDatetime()+"";
			field2 = listEventReview.get(i).getCustomerId()+"";
			field3 = listEventReview.get(i).getReviewContent();
			
			
			int intReviewStatus = listEventReview.get(i).getReviewStatus();
			String reviewStatus = "";
			String reviewStatusKey = "";
			switch(intReviewStatus){
				case EventReview.APPROVED: 
					reviewStatus = "Approved";
					reviewStatusKey = "success";
					break;
				case EventReview.PENDING: 
					reviewStatus = "Pending";
					reviewStatusKey = "info";
					break;
				case EventReview.REJECTED: 
					reviewStatus = "Rejected";
					reviewStatusKey = "warning";
					break;
				default: 
					break;
			}
			
			
			field4 = "<span class='label label-sm label-"+reviewStatusKey+"'>"+reviewStatus+"</span>";
			field5 = "<a href='/acp/events/eventsNewsReviewEdit?reviewUUID="+field0+"' class='btn btn-xs default btn-editable'><i class='fa fa-pencil'></i> Edit</a>";
			
			data[i][0] = field0;
			data[i][1] = field1;
			data[i][2] = field2;
			data[i][3] = field3;
			data[i][4] = field4;
			data[i][5] = field5;
		}
		
		model.put("draw", new Integer(1));
		model.put("recordsTotal", new Integer(5));
		model.put("recordsFiltered", new Integer(5));
		model.put("data", data);
		model.put("customActionStatus","OK");
		model.put("customActionMessage","OK");
		
		logger.info("leaving /events/eventsNewsReviewListData");
		return model;
	}
	
}
