package com.keepitfresh.web;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.keepitfresh.model.Item;
import com.keepitfresh.model.ItemService;

@Controller
public class ItemController {

    @Autowired
    private ItemService service;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(
                dateFormat, false));
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showItemsList(ModelMap model) {
        String user = getLoggedInUserName();
        model.addAttribute("items", service.retrieveItems(user));
        return "list-items";
    }

    @RequestMapping(value = "/add-item", method = RequestMethod.GET)
    public String showAddItemPage(ModelMap model) {
        model.addAttribute("item", new Item());
        return "item";
    }

    @RequestMapping(value = "/add-item", method = RequestMethod.POST)
    public String addItem(ModelMap model, @Valid Item item, BindingResult result) {
        if (result.hasErrors())
            return "item";
        service.addItem(getLoggedInUserName(), item.getName(),
                item.getCategory(), item.getQuantity(), item.getExpDate());
        model.clear();// to prevent request parameter "name" to be passed
        return "redirect:/";
    }

    private String getLoggedInUserName() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal instanceof UserDetails)
            return ((UserDetails) principal).getUsername();

        return principal.toString();
    }

    @RequestMapping(value = "/update-item", method = RequestMethod.GET)
    public String showUpdateItemPage(ModelMap model, @RequestParam int id) {
        model.addAttribute("item", service.retrieveItem(id));
        return "item";
    }

    @RequestMapping(value = "/update-item", method = RequestMethod.POST)
    public String updateItem(ModelMap model, @Valid Item item,
            BindingResult result) {
        if (result.hasErrors())
            return "item";

        item.setUser(getLoggedInUserName());
        service.updateItem(item);

        model.clear();// to prevent request parameter "name" to be passed
        return "redirect:/";
    }

    @RequestMapping(value = "/delete-item", method = RequestMethod.GET)
    public String deleteItem(@RequestParam int id) {
    	service.deleteItem(id);
        return "redirect:/";
    }
}