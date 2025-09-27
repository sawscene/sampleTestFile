/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp.chartplugin;

import adtekfuji.admanagerapp.chartplugin.controller.MainSceneController;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.property.AdProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.scene.control.TreeItem;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.MainMenuCategoryEnum;
import jp.adtekfuji.adFactory.enumerate.MenuTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.SubMenuCategoryEnum;
import jp.adtekfuji.adFactory.plugin.AdManagerAppMainMenuInterface;

/**
 * 作業分析プラグインメニュー
 *
 * @author s-heya
 */
public class AdManagerChartPluginMenu implements AdManagerAppMainMenuInterface {

    private final Properties properties = AdProperty.getProperties();
    
    @Override
    public String getDisplayName() {
        ResourceBundle rb = LocaleUtils.getBundle("locale.locale");
        return LocaleUtils.getString("key.WorkAnalysis");
        //return "作業分析";
    }

    @Override
    public DisplayCategoryType getDisplayCategory() {
        return DisplayCategoryType.REFERENCE_FUNCTION;
    }

    @Override
    public Integer getDisplayOrder() {
        return DisplayCategoryOrder.MIDDLE_PRIORITY.getOrder();
    }
       
    @Override
    public void onSelectMenuAction() {
        SceneContiner sc = SceneContiner.getInstance();
        sc.trans("ChartMainScene");
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
    } 

    @Override
    public LicenseOptionType getOptionType() {
        return LicenseOptionType.CsvReportOut;
    }

    @Override
    public List<RoleAuthorityType> getRoleAuthorityType() {
        return Arrays.asList((RoleAuthorityType) RoleAuthorityTypeEnum.OUTPUT_ACTUAL);
    }

    /**
     * プロパティを設定する
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {
        if (Objects.nonNull(properties))  {
            for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
                String propertyName = (String) e.nextElement();
                String propertyValue = properties.getProperty(propertyName);
                this.properties.setProperty(propertyName, propertyValue);
            }
        }
    } 
    
    @Override
    public Map<MainMenuCategoryEnum, List<MenuNode>> getTreeNodes() {
        Map<MainMenuCategoryEnum, List<MenuNode>> nodes = new HashMap<>();
        List<MenuNode> resultNodes = new ArrayList<>();
        
        TreeItem<String> chartNodes= new TreeItem<>(SubMenuCategoryEnum.ANALYSIS_TITLE.getDisplayName());
        chartNodes.setExpanded(false);
        
        TreeItem<String> timeLineItem = new TreeItem<>(SubMenuCategoryEnum.TIME_LINE.getDisplayName());                                            //タイムライン
        TreeItem<String> kanbanTotalWorkTimeItem = new TreeItem<>(SubMenuCategoryEnum.KANBAN_TOTAL_WORK_TIME.getDisplayName());                    //総作業時間
        TreeItem<String> processAvgWorkTimeItem = new TreeItem<>(SubMenuCategoryEnum.PROCESS_AVERAGE_WORK_TIME.getDisplayName());                  //平均作業時間:工程
        TreeItem<String> workerAvgWorkTimeItem = new TreeItem<>(SubMenuCategoryEnum.WORKER_AVERAGE_WORK_TIME.getDisplayName());                    //平均作業時間:作業者
        
        chartNodes.getChildren().addAll(Arrays.asList(timeLineItem,kanbanTotalWorkTimeItem,processAvgWorkTimeItem,workerAvgWorkTimeItem));
        
        // Map actions for child items
        Map<TreeItem<String>, Runnable> childActions = new HashMap<>();
        childActions.put(timeLineItem, createMenuAction("ChartTimeLineCompo",null));   
        childActions.put(kanbanTotalWorkTimeItem, createMenuAction("ChartKanbanSummaryCompo", null));            
        childActions.put(processAvgWorkTimeItem, createMenuAction("ChartWorkSummaryCompo", null));
        childActions.put(workerAvgWorkTimeItem, createMenuAction("ChartOrganizationSummaryCompo", null));
        
        resultNodes.add(new MenuNode(chartNodes, null, childActions));
        
        nodes.put(MainMenuCategoryEnum.RESULT, resultNodes);
        
        return nodes;
    }
    
        private Runnable createMenuAction(String contentComponent, Object argument) {
        return () -> {
            SceneContiner sc = SceneContiner.getInstance();
            boolean hideSideNaviPane = MenuTypeEnum.from(AdProperty.getProperties().getProperty("menuType")).isTree();
            
            if (!sc.trans("ChartMainScene", hideSideNaviPane)) {
                return;
            }
            
            sc.visibleArea("MenuPane", false);
            sc.visibleArea("MenuPaneUnderlay", false);
            sc.setComponent("AppBarPane", "AppBarCompo");
            
            
            if (argument != null) {
                sc.setComponent("ContentNaviPane", contentComponent, mainScene);
            } else {
                sc.setComponent("ContentNaviPane", contentComponent);
            }
        };
    }
 
}
