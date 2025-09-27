/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.kanbaneditplugin;

import adtekfuji.admanagerapp.kanbaneditplugin.common.KanbanEditConfig;
import adtekfuji.clientservice.ClientServiceProperty;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import javafx.scene.control.TreeItem;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.MainMenuCategoryEnum;
import jp.adtekfuji.adFactory.enumerate.MenuTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.SubMenuCategoryEnum;
import jp.adtekfuji.adFactory.plugin.AdManagerAppMainMenuInterface;

/**
 *
 * @author ke.yokoi
 */
public class AdManagerAppKanbanPluginMenu implements AdManagerAppMainMenuInterface {

    private final Properties properties = AdProperty.getProperties();

    @Override
    public String getDisplayName() {
        return LocaleUtils.getString("key.EditKanbanTitle");
    }

    @Override
    public DisplayCategoryType getDisplayCategory() {
        return DisplayCategoryType.MANAGEMENT_FUNCTION;
    }

    @Override
    public Integer getDisplayOrder() {
        return DisplayCategoryOrder.MIDDLE_PRIORITY.getOrder() + 1;
    }

    @Override
    public void onSelectMenuAction() {
        SceneContiner sc = SceneContiner.getInstance();
        if (!sc.trans("KanbanEditScene")) {
            return;
        }
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        sc.setComponent("AppBarPane", "AppBarCompo");

        boolean isKanbanEditMenuEnabled = KanbanEditConfig.isKanbanEditMenuEnabled();
        boolean isKanbanEditor = ClientServiceProperty.isLicensed(LicenseOptionType.KanbanEditor.getName());
        boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());
        
        // カンバン編集メニューを使用するか
            if ((isKanbanEditMenuEnabled && isKanbanEditor) || (isKanbanEditor && isLiteOption)) {
                sc.setComponent("SideNaviPane", "KanbanEditMenuCompo");// カンバン編集メニュー
            } else {
                // メニューは使用しない
                if (isKanbanEditor) {
                    sc.setComponent("ContentNaviPane", "KanbanListCompo");// カンバン編集画面
                } else if (isLiteOption) {
                    sc.setComponent("ContentNaviPane", "LiteKanbanListCompo");// Liteカンバン編集画面
                }
            }
    }

    @Override
    public LicenseOptionType getOptionType() {
        return LicenseOptionType.KanbanEditor;
    }

    @Override
    public List<RoleAuthorityType> getRoleAuthorityType() {
        return Arrays.asList((RoleAuthorityType) RoleAuthorityTypeEnum.REFERENCE_KANBAN, (RoleAuthorityType) RoleAuthorityTypeEnum.MAKED_KANBAN);
    }

    /**
     * プロパティを設定する
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        if (Objects.nonNull(properties)) {
            for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();) {
                String propertyName = (String) e.nextElement();
                String propertyValue = properties.getProperty(propertyName);
                this.properties.setProperty(propertyName, propertyValue);
            }
        }
    }
    
    @Override
    public Map<MainMenuCategoryEnum, List<MenuNode>> getTreeNodes() {
        Map<MainMenuCategoryEnum, List<MenuNode>> nodes = new HashMap<>();

        nodes.put(MainMenuCategoryEnum.OPERATION, new ArrayList<>(Arrays.asList(
            new MenuNode(
                new TreeItem<>(SubMenuCategoryEnum.KANBAN.getDisplayName()), // カンバン
                createMenuAction("KanbanListCompo")
            )
        )));
        nodes.put(MainMenuCategoryEnum.LITE, new ArrayList<>(Arrays.asList(
            new MenuNode(
                new TreeItem<>(SubMenuCategoryEnum.LITE_KANBAN_TITLE.getDisplayName()), // Liteカンバン
                createMenuAction("LiteKanbanListCompo")
            )
        )));
        nodes.put(MainMenuCategoryEnum.UNUSED_MENU_CATEGORY, new ArrayList<>(Arrays.asList(
            new MenuNode(
                 new TreeItem<>(SubMenuCategoryEnum.KANBAN_PLANLOADING.getDisplayName()), // 生産計画の読み込み
                createMenuAction("KanbanImportCompo")
            )
        )));

        // Remove nodes if conditions not met
        boolean isKanbanEditor = ClientServiceProperty.isLicensed(LicenseOptionType.KanbanEditor.getName());
        boolean isLiteOption = ClientServiceProperty.isLicensed(LicenseOptionType.LiteOption.getName());
        boolean isKanbanImportEnabled = KanbanEditConfig.isUseKanbanImport();

        if (!isKanbanEditor) {
            nodes.remove(MainMenuCategoryEnum.OPERATION);
        }
        if (isLiteOption) {
            nodes.remove(MainMenuCategoryEnum.LITE);
        }
        if (!isKanbanImportEnabled) {
            nodes.remove(MainMenuCategoryEnum.UNUSED_MENU_CATEGORY);
        }

        return nodes;
    }
    
      private Runnable createMenuAction(String contentComponent) {
        return () -> {
            SceneContiner sc = SceneContiner.getInstance();
            boolean hideKanbanMenu = MenuTypeEnum.from(AdProperty.getProperties().getProperty("menuType")).isTree();
            if (!sc.trans("KanbanEditScene", hideKanbanMenu)) {
                return;
            }
            sc.visibleArea("MenuPane", false);
            sc.visibleArea("MenuPaneUnderlay", false);
            sc.setComponent("AppBarPane", "AppBarCompo");
            sc.setComponent("ContentNaviPane", contentComponent);
        };
    }
}
