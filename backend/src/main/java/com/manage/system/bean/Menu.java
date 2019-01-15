package com.manage.system.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * used for ui menu
 */
public class Menu {

	private Integer menuId;

	private String menuName;

	private String menuUrl;

	private String icon;

	private Integer menuType;

	private List<Menu> subMenus;

	public Menu() {
		super();
	}

	public Menu(Integer menuId, String menuName, String menuUrl, String icon, Integer menuType) {
		super();
		this.menuId = menuId;
		this.menuName = menuName;
		this.menuUrl = menuUrl;
		this.menuType = menuType;
		this.icon = icon;
		this.subMenus = new ArrayList<>();
	}

	public List<Menu> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(List<Menu> subMenus) {
		this.subMenus = subMenus;
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuUrl() {
		return menuUrl;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public Integer getMenuType() {
		return menuType;
	}

	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
