package main.java.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import main.java.Competition;
import main.java.ExcelFileUtils;
import main.java.InitScreenModel;
import main.java.service.CompetitionService;
import main.java.service.TeamPropertyService;
import main.java.teamProperty.TeamProperty;

@Controller
public class InitScreenController {

	private CompetitionService cpService;

	private TeamPropertyService tpService;

	@ModelAttribute
	private InitScreenModel createInitScreenModel() {
		return new InitScreenModel();
	}


	@RequestMapping(path = "/init")
	public ModelAndView top(@ModelAttribute InitScreenModel initScreenInfo) throws Exception {
		System.out.println("initページ表示!");
		return new ModelAndView("initScreen");
	}

	@RequestMapping(path = "/init", params = "sendDate")
	public ModelAndView selectDate(@ModelAttribute InitScreenModel initScreenInfo) throws Exception {
		List<String> messageList = new ArrayList<>();
		Date date = initScreenInfo.getDate();

		if (date == null) {
			messageList.add("日付を入力してください");
		} else {

		}
		initScreenInfo.setMessageList(messageList);
		return new ModelAndView("Compatition");
	}

	@RequestMapping(path = "/init", params = "sendFileData")
	public ModelAndView registerData(@ModelAttribute InitScreenModel initScreenInfo, HttpServletRequest request) throws Exception {
		System.out.println("コントローラーの呼び出し：registerData表示");
		List<String> messageList = new ArrayList<>();
		String saveFile = "C:/work/upload/";
		try {
//			String contentType = request.getContentType();
//			if (contentType != null && contentType.startsWith("multipart/form-data")) {
				Part cpPart = request.getPart("competitionData");
				Part tpPart = request.getPart("teamPropertyData");

				Vector<Competition> competitionList = null;
				if (cpPart != null) {
					InputStream cpis = cpPart.getInputStream();
					competitionList = ExcelFileUtils.convertCompetition(cpis);
					cpService.registerCompetition(competitionList);
					messageList.add("対戦表を登録しました");
				}
				TeamProperty teamProperty = new TeamProperty();
				if (tpPart != null) {
					InputStream tpis = tpPart.getInputStream();
					teamProperty = ExcelFileUtils.convertTeamProperty(tpis);
					tpService.registerTeamProperty(teamProperty);
					messageList.add("チーム成績を登録しました");
				}
//			}
			if (messageList.isEmpty()) {
				messageList.add("データを登録出来ませんでした");
			}
			initScreenInfo.setMessageList(messageList);

		} catch (Exception e) {

		}
		return new ModelAndView("redirect:/init");
	}
}