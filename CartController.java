package jp.co.internous.team2412.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import jp.co.internous.team2412.model.domain.TblCart;
import jp.co.internous.team2412.model.domain.dto.CartDto;
import jp.co.internous.team2412.model.form.CartForm;
import jp.co.internous.team2412.model.mapper.TblCartMapper;
import jp.co.internous.team2412.model.session.LoginSession;

/**
 * カート情報に関する処理のコントローラー
 * @author インターノウス
 *
 */
@Controller
@RequestMapping("/team2412/cart")
public class CartController {

	/*
	 * フィールド定義
	 */
	@Autowired
	private TblCartMapper tblCartMapper;
	
	@Autowired
	private LoginSession loginSession;

	private Gson gson = new Gson();

	/**
	 * カート画面を初期表示する。
	 * @param m 画面表示用オブジェクト
	 * @return カート画面
	 */
	@RequestMapping("/")
	public String index(Model m) {
		
		if(!loginSession.isLogined()) {
			List<CartDto> carts = tblCartMapper.findByUserId(loginSession.getTmpUserId());
			for (CartDto cart : carts) {
				int subtotal = cart.getPrice() * cart.getProductCount();
				cart.setSubtotal(subtotal);
			}
			
			m.addAttribute("loginSession", loginSession);
			m.addAttribute("carts", carts);

			return "cart";
		} else {
			List<CartDto> carts = tblCartMapper.findByUserId(loginSession.getUserId());
			for (CartDto cart : carts) {
				int subtotal = cart.getPrice() * cart.getProductCount();
				cart.setSubtotal(subtotal);
			}
			
			m.addAttribute("loginSession", loginSession);
			m.addAttribute("carts", carts);

			return "cart";
		}
	}

	/**
	 * カートに追加処理を行う
	 * @param f カート情報のForm
	 * @param m 画面表示用オブジェクト
	 * @return カート画面
	 */
	@RequestMapping("/add")
	public String addCart(CartForm f, Model m) {
		
		if(!loginSession.isLogined()) {
			TblCart cart = new TblCart();
			LocalDateTime currentTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			String formattedTime = currentTime.format(formatter);
			
			cart.setUserId(loginSession.getTmpUserId());
			cart.setProductId(f.getProductId());
			cart.setProductCount(f.getProductCount());
			cart.setUpdatedAt(formattedTime);

			int count = tblCartMapper.findCountByUserIdAndProuductId(cart.getUserId(), cart.getProductId());

			if (count > 0) {
				tblCartMapper.update(cart);
			} else {
				tblCartMapper.insert(cart);
			}
			return "forward:/team2412/cart/";
		} else {
			TblCart cart = new TblCart();
			LocalDateTime currentTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			String formattedTime = currentTime.format(formatter);
			
			cart.setUserId(loginSession.getUserId());
			cart.setProductId(f.getProductId());
			cart.setProductCount(f.getProductCount());
			cart.setUpdatedAt(formattedTime);

			int count = tblCartMapper.findCountByUserIdAndProuductId(cart.getUserId(), cart.getProductId());

			if (count > 0) {
				tblCartMapper.update(cart);
			} else {
				tblCartMapper.insert(cart);
			}
			
			return "forward:/team2412/cart/";
		}
	}

	/**
	 * カート情報を削除する
	 * @param checkedIdList 選択したカート情報のIDリスト
	 * @return true:削除成功、false:削除失敗
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/delete")
	@ResponseBody
	public boolean deleteCart(@RequestBody String checkedIdList) {
		
		try {
			JsonObject jsonObject = gson.fromJson(checkedIdList, JsonObject.class);
	        JsonArray jsonArray = jsonObject.getAsJsonArray("checkedIdList");
	        
	        List<Integer> list = new ArrayList<>();
	        jsonArray.forEach(element -> list.add(element.getAsInt()));
	        
			if (list.isEmpty()) {
				return false;
			}
			
			tblCartMapper.deleteById(list);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
