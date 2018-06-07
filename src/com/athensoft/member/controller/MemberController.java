package com.athensoft.member.controller;

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

import com.athensoft.content.event.controller.NewsAcpController;
import com.athensoft.content.event.entity.EventMedia;
import com.athensoft.content.event.entity.News;
import com.athensoft.member.entity.Member;
import com.athensoft.member.entity.MemberStatus;
import com.athensoft.member.service.MemberService;

@Controller
@RequestMapping("/member")
public class MemberController {
	
	private static final Logger logger = Logger.getLogger(NewsAcpController.class);
	
	private static final String ACTION_EDIT = "����";
	private static final String ACTION_DELETE = "ɾ��";
	
	private MemberService memberService;
	
	@Autowired
	public void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@RequestMapping("/member_list.html")
	public String gotoMemberList(){
		return "member/member_list";
	}
	
	@RequestMapping(value="/memberListData",produces="application/json")
	@ResponseBody
	public Map<String,Object> getDataMemberList(){
		logger.info("entering /member/memberListData");
		
		ModelAndView mav = new ModelAndView();
		
		//data
		List<Member> listMembers = memberService.getAllMembers();
		logger.info("Length of news entries: "+ listMembers.size());
		
		String[][] data = getData(listMembers, ACTION_EDIT);
		
		Map<String, Object> model = mav.getModel();
		
		model.put("draw", new Integer(1));
		model.put("recordsTotal", new Integer(5));
		model.put("recordsFiltered", new Integer(5));
		model.put("data", data);
		model.put("customActionStatus","OK");
		model.put("customActionMessage","Data loaded");
		
		logger.info("leaving /member/memberListData");
		return model;
	}
	
	@RequestMapping(value="/memberEdit")
	public ModelAndView gotoMemberEdit(@RequestParam String acctName){
		logger.info("entering /member/memberEdit");
		
		//logic
		Member member = memberService.getMemberByAcctName(acctName);
		
		
		ModelAndView mav = new ModelAndView();
		
		//data
		Map<String, Object> model = mav.getModel();
		model.put("memberObject", member);
			
		//view
		String viewName = "member/member_edit";
		mav.setViewName(viewName);
		
		logger.info("leaving /member/memberEdit");
		return mav;
	}
	
	@RequestMapping(value="/updateMember",method=RequestMethod.POST)
	public ModelAndView updateMember(@RequestParam String itemJSONString) {
		
		logger.info("entering /event/updateMember");
		
		/* initial settings */
		ModelAndView mav = new ModelAndView();
		
		//set model
//      Map<String, Object> model = mav.getModel();
        JSONObject jsonObj= new JSONObject(itemJSONString);
   
        Member member = new Member();
        member.setAcctName(jsonObj.getString("acctName"));
        member.setName1(jsonObj.getString("name1"));
        member.setName2(jsonObj.getString("name2"));
        member.setGender(jsonObj.getInt("gender"));
        member.setNationality(jsonObj.getString("nationality"));
        member.setPhone1(jsonObj.getString("phone1"));
        member.setPhone2(jsonObj.getString("phone2"));
        member.setWechat(jsonObj.getString("wechat"));
        member.setEmail(jsonObj.getString("email"));
        member.setDegree(jsonObj.getString("degree"));
        member.setOccupation(jsonObj.getString("occupation"));
        member.setDob(jsonObj.getString("dob"));
        member.setPobProvince(jsonObj.getString("pobProvince"));
        member.setPobCity(jsonObj.getString("pobCity"));
        member.setHomeAddress(jsonObj.getString("homeAddress"));
        member.setPostalcode(jsonObj.getString("postalcode"));
        member.setMemberLevel(jsonObj.getInt("memberLevel"));
        member.setMemberStatus(jsonObj.getInt("memberStatus"));
        member.setMemberActiveDate(new Date(jsonObj.getString("memberActiveDate")));
        logger.info("news = "+member);
          
		/* business logic*/
        //long itemId = itemService.createItem(ic); 

        memberService.updateMember(member);
		
		/* assemble model and view */
//      model.put("news", news);
        String viewName = "member/member_edit";
		mav.setViewName(viewName);		
		
		logger.info("leaving /member/updateMember");
		return mav;		
	}
	
	
	private String[][] getData(List<Member> list, String actionName){
		int entryLength = list.size();
		final int COLUMN_NUM = 11;
		String[][] data = new String[entryLength][COLUMN_NUM];
		
		String field0 = "";	//check box
		String field1 = "";	//acctName
		String field2 = "";	//name1
		String field3 = "";	//name2
		String field4 = "";	//gender
		String field5 = "";	//phone1
		String field6 = "";	//phone2
		String field7 = "";	//wechat
		String field8 = "";	//email
		String field9 = "";	//memberStatus
		String field10 = "";	//action
		
		for(int i=0; i<entryLength ; i++){			
			field0 = "<input type='checkbox' name='id[]' value="+list.get(i).getAcctName()+">";
			field1 = list.get(i).getAcctName()+"";
			field2 = list.get(i).getName1();
			field3 = list.get(i).getName2();
			field4 = getGender(list.get(i).getGender());
			field5 = list.get(i).getPhone1().trim();
			field6 = list.get(i).getPhone2().trim();
			field7 = list.get(i).getWechat().trim();
			field8 = list.get(i).getEmail().trim();
			
			int intMemberStatus = list.get(i).getMemberStatus();
			String[] memberStatusPair = getMemberStatusPair(intMemberStatus);
			String memberStatusKey = memberStatusPair[0];
			String memberStatus = memberStatusPair[1];
			field9 = "<span class='label label-sm label-"+memberStatusKey+"'>"+memberStatus+"</span>";
			field10 = "<a href='/acp/member/"+getAction(actionName)+"?acctName="+field1+"' class='btn btn-xs default btn-editable'><i class='fa fa-pencil'></i> "+actionName+"</a>";
			
			//logger.info("field8="+field8);
			
			data[i][0] = field0;
			data[i][1] = field1;
			data[i][2] = field2;
			data[i][3] = field3;
			data[i][4] = field4;
			data[i][5] = field5;
			data[i][6] = field6;
			data[i][7] = field7;
			data[i][8] = field8;
			data[i][9] = field9;
			data[i][10] = field10;
		}
		
		return data;
	}
	
	private String getGender(int gender){
		String strGender = "";
		if(1==gender){
			strGender = "��";
		}else if(2==gender){
			strGender = "Ů";
		}else{
			strGender = "δ֪";
		}
		return strGender;
	}
	
	private String getAction(String actionName){
		String action = "";
		switch(actionName){
		case ACTION_EDIT:
			action = "memberEdit";
			break;
		case ACTION_DELETE:
			action = "deleteMember";
			break;
		}
		return action;
	}
	
	private String[] getMemberStatusPair(int intStatus){
		String[] statusPair = new String[2];
		
		String status = "";
		String statusKey = "";
		switch(intStatus){
			case MemberStatus.APPLIED: 
//				status = "Applied";
				status = "������";
				statusKey = "warning";
				break;
			case MemberStatus.APPROVED: 
//				status = "Approved";
				status = "��ͨ��Ԥ��";
				statusKey = "info";
				break;
			case MemberStatus.ACTIVE: 
//				status = "Active";
				status = "�Ѽ���";
				statusKey = "success";
				break;
			case MemberStatus.INACTIVE: 
//				status = "Inactive";
				status = "δ����";
				statusKey = "default";
				break;
			case MemberStatus.PENDING: 
//				status = "Pending";
				status = "�����";
				statusKey = "danger";
				break;
			case MemberStatus.BANNED: 
//				status = "Banned";
				status = "�ѽ�ֹ";
				statusKey = "danger";
				break;
			default: 
				break;
		}
		
		statusPair[0]=statusKey;
		statusPair[1]=status;
		
		
		return statusPair;
	}
}