/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;
import java.util.ResourceBundle;

/**
 *
 * @author SashinRanjitkar
 */
public enum MainMenuCategory {
    OPERATION ("key.MainMenuTitle.Operation",true),   // 運用
    LITE      ("key.MainMenuTitle.Lite",true),        // Lite
    REPORTER  ("key.MainMenuTitle.Reporter",true),    // Reporter
    RESULT    ("key.MainMenuTitle.ActualOutput",true),// 実績
    WAREHOUSE ("key.MainMenuTitle.WareHouse",true),   // 倉庫
    SETTINGS  ("key.MainMenuTitle.Settings",true),    // 設定
    UNUSED_MENU_CATEGORY("key.MainMenuTitle.Unused",false);

    private final String resourceKey;
    private final boolean visible;

    MainMenuCategory(String resourceKey, boolean visible) {
        this.resourceKey = resourceKey;
        this.visible = visible;
    }

    /** key as-is (useful when you want the i18n key)
     * @return  
    */
    public String getResourceKey() {
        return resourceKey;
    }
    
    public boolean isVisible() { 
        return visible; 
    }

//    public String getDisplayName(ResourceBundle rb) {
//        if (rb != null && rb.containsKey(resourceKey)) {
//            return LocaleUtils.getString(resourceKey);
//        }
//        return name(); // fallback if no bundle or missing key
//    }
    
     public String getDisplayName() {
        return LocaleUtils.getString(resourceKey);
    }

    /**
     * 🔧 Utility method: get only categories that should be displayed
     * @return 
     */
    public static MainMenuCategory[] visibleCategories() {
        return java.util.Arrays.stream(values())
                .filter(MainMenuCategory::isVisible)
                .toArray(MainMenuCategory[]::new);
    }
    
//    public String getDisplayName() {
//        String s = LocaleUtils.getString(resourceKey);
//        return (s == null || s.isBlank()) ? name() : s;
//    }
}

