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
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.MainMenuCategory;
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
    
    MainSceneController mainSceneController;
        
    final String menuType = properties.getProperty("menuType");
    
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
    public Map<MainMenuCategory, List<MenuNode>> getSubMenuDisplayNames() {
        Map<MainMenuCategory, List<MenuNode>> nodeMap = new HashMap<>();
        List<MenuNode> resultNodes = new ArrayList<>();
        
        List<MenuNode> analysisChildren = new ArrayList<>();
        analysisChildren.add(new MenuNode(LocaleUtils.getString("key.SubMenuTitle.TimeLine"), null)); 
        analysisChildren.add(new MenuNode(LocaleUtils.getString("key.SubMenuTitle.KanbanTotalWorkTime"), null)); 
        analysisChildren.add(new MenuNode(LocaleUtils.getString("key.SubMenuTitle.ProcessAverageWorkTime"), null)); 
        analysisChildren.add(new MenuNode(LocaleUtils.getString("key.SubMenuTitle.WorkerAverageWorkTime"), null)); 
        resultNodes.add(new MenuNode(LocaleUtils.getString("key.SubMenuTitle.AnalysisTitle"), analysisChildren)); 
        nodeMap.put(MainMenuCategory.RESULT, resultNodes);

        return nodeMap;
    }
    

    @Override
    public void onSelectMenuAction(String subMenuDisplayName) {
        SceneContiner sc = SceneContiner.getInstance();
        sc.trans("ChartMainScene");
//        if (Objects.isNull(this.mainSceneController)) {
//                Object controller = SceneContiner.getInstance().getSceneController();
//                if (controller instanceof MainSceneController mainSceneController1) {
//                    this.mainSceneController = mainSceneController1;
//                } 
//            }
        sc.visibleArea("MenuPane", false);
        sc.visibleArea("MenuPaneUnderlay", false);
        
        if (MenuTypeEnum.TREE.getValue().equals(menuType)) {
            
            Map<String, Object[]> componentMap = new HashMap<>();
            componentMap.put(LocaleUtils.getString("key.SubMenuTitle.TimeLine"), new Object[]{"ChartTimeLineCompo", mainSceneController});
            componentMap.put(LocaleUtils.getString("key.SubMenuTitle.KanbanTotalWorkTime"), new Object[]{"ChartKanbanSummaryCompo"});
            componentMap.put(LocaleUtils.getString("key.SubMenuTitle.ProcessAverageWorkTime"), new Object[]{"ChartWorkSummaryCompo"});
            componentMap.put(LocaleUtils.getString("key.SubMenuTitle.WorkerAverageWorkTime"), new Object[]{"ChartOrganizationSummaryCompo"});

            Object[] info = componentMap.get(subMenuDisplayName);
            if (info.length > 1 && info[1] != null) {
                sc.setComponent("ContentNaviPane", info[0].toString(), info[1]);
            } else {
                sc.setComponent("ContentNaviPane", info[0].toString());
            } 
        }
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
    public void onSelectSubMenuAction(String subMenudisplayName) {
        onSelectMenuAction(subMenudisplayName);
    }
    
     public List<TreeMenuNode> getMenuNodes() {
        return Arrays.asList(new TreeMenuNode(MenuNode.leaf(SubMenuCategoryEnum.KANBAN.getDisplayName()), MainMenuCategory.OPERATION),
                new TreeMenuNode(MenuNode.leaf(SubMenuCategoryEnum.LITE_KANBAN_TITLE.getDisplayName()), MainMenuCategory.LITE),
                new TreeMenuNode(MenuNode.leaf("SETTINGS1"), MainMenuCategory.SETTINGS)
        );
    }
}
