package jp.co.internous.team2412.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jp.co.internous.team2412.model.domain.MstDestination;
import jp.co.internous.team2412.model.mapper.MstDestinationMapper;
import jp.co.internous.team2412.model.mapper.TblCartMapper;
import jp.co.internous.team2412.model.mapper.TblPurchaseHistoryMapper;
import jp.co.internous.team2412.model.session.LoginSession;

/**
 * 決済に関する処理を行うコントローラー
 * @author インターノウス
 *
 */
@Controller
@RequestMapping("/team2412/settlement")
public class SettlementController {
	
	/*
	 * フィールド定義
	 */
	@Autowired
	private MstDestinationMapper mstDestinationMapper;
	
	@Autowired
	private TblPurchaseHistoryMapper tblPurchaseHistoryMapper;
	
	@Autowired
	private TblCartMapper tblCartMapper;
	
	@Autowired
	private LoginSession loginSession;
	
	private Gson gson = new Gson();
	
	/**
	 * 宛先選択・決済画面を初期表示する。
	 * @param m 画面表示用オブジェクト
	 * @return 宛先選択・決済画面
	 */
	@RequestMapping("/")
	public String index(Model m) {
		
		List<MstDestination> destinations = mstDestinationMapper.findByUserId(loginSession.getUserId());
		
		m.addAttribute("loginSession", loginSession);
		m.addAttribute("destinations",destinations);
		
		return "settlement";
	}
	
	/**
	 * 決済処理を行う
	 * @param destinationId 宛先情報id
	 * @return true:決済処理成功、false:決済処理失敗
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/complete")
	@ResponseBody
	public boolean complete(@RequestBody String destinationId) {

		JsonObject jsonObject = gson.fromJson(destinationId, JsonObject.class);
		int dId =  jsonObject.get("destinationId").getAsInt();
		int history = tblPurchaseHistoryMapper.insert(dId,loginSession.getUserId());
		if ( history > 0) {
			int delete = tblCartMapper.deleteByUserId(loginSession.getUserId());
			if (delete > 0) {
				return true;
			}
		} return false;
	}
}
