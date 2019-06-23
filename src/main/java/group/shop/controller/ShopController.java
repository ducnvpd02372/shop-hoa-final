package group.shop.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import group.shop.entity.Customers;
import group.shop.entity.Orders;
import group.shop.services.CustomerService;
import group.shop.services.OrderBean;
import group.shop.services.OrderService;
import group.shop.services.ProdutService;

@Controller
@RequestMapping("shop")
public class ShopController {
	@Autowired
	private HttpSession session;
	@Autowired
	private OrderService orderService;
	@Autowired
	private ProdutService produtService;
	@Autowired
	private CustomerService customerService;
	private Customers customers;
	private float total=0;
	@RequestMapping("/prod/{id}")
	public String page(@PathVariable("id") int prodId,ModelMap modelMap){
		modelMap.addAttribute("prod", produtService.findById(prodId).get());
		
		return "shop/product";
		
	}
	
	@RequestMapping("prod")
	public String page(ModelMap modelMap){
		modelMap.addAttribute("prods", produtService.findAll());
		return "shop/categories";
	}
	@RequestMapping("checkout")
	public String checkout(ModelMap modelMap){
		if(session.getAttribute("accLoginC")==null) {
			modelMap.addAttribute("customer", new Customers());
		}
		else {
			modelMap.addAttribute("customer",session.getAttribute("accLoginC"));
		}
		OrderBean cart= (OrderBean) session.getAttribute("cart");
		if(cart!=null) {
			cart.forEach((k,v) -> {
				
				total= total+ ((Orders) v).getTolalmoney();
			});
		}
		
		modelMap.addAttribute("total", total);
		return "shop/checkout";
	}
	
	@RequestMapping(value="save",method=RequestMethod.POST)
	public String save(@Valid @ModelAttribute("customer")Customers customers, BindingResult result ,ModelMap model) {
		System.out.println("+++");
		customerService.save(customers);
		OrderBean cart= (OrderBean) session.getAttribute("cart");
		if(cart==null) {
			
			return "redirect:checkout";
			
		}
		
		cart.forEach((k,v) -> {
			if(((Orders) v).getCustomers()==null) {
				 
				((Orders) v).setCustomers(customers);
				orderService.save((Orders) v);
			}else {
				orderService.save((Orders) v);
			}
			
		});

		model.addAttribute("mess", true);
		return "shop/checkout";
	}
	 
}
