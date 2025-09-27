/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adtekfuji.admanagerapp;

import adtekfuji.clientservice.SystemResourceFacade;
import adtekfuji.fxscene.SceneContiner;
import adtekfuji.locale.LocaleUtils;
import adtekfuji.plugin.PluginLoader;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import jp.adtekfuji.adFactory.entity.login.LoginUserInfoEntity;
import jp.adtekfuji.adFactory.entity.system.SystemOptionEntity;
import jp.adtekfuji.adFactory.enumerate.LicenseOptionType;
import jp.adtekfuji.adFactory.enumerate.MainMenuCategoryEnum;
import jp.adtekfuji.adFactory.enumerate.MenuTypeEnum;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityType;
import jp.adtekfuji.adFactory.enumerate.RoleAuthorityTypeEnum;
import jp.adtekfuji.adFactory.enumerate.SubMenuCategoryEnum;
import jp.adtekfuji.adFactory.plugin.AdManagerAppMainMenuInterface;
import jp.adtekfuji.adFactory.plugin.AdManagerAppMainMenuInterface.MenuNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ke.yokoi
 */
public class MainMenuContainer {

    private static MainMenuContainer instance = null;
    private static final Logger logger = LogManager.getLogger();
    private final List<AdManagerAppMainMenuInterface> plugins = new ArrayList<>();

    private final SystemResourceFacade systemResourceFacade = new SystemResourceFacade();

    /**
     * プラグインからオプション種別を取得します。
     * {@link AdManagerAppMainMenuInterface#getOptionType()} を使用してオプション種別を取得し、
     * 取得できない場合はデフォルト値として {@link LicenseOptionType#Unknown} を返します。
     *
     * @param plugin オプション種別を取得する対象のプラグイン
     * @return 取得したオプション種別。取得できない場合は {@link LicenseOptionType#Unknown}
     */
    private LicenseOptionType getOptionalType(AdManagerAppMainMenuInterface plugin) {
        try {
            return plugin.getOptionType();
        } catch (NoSuchFieldError ex) {
            logger.error("plugin:{} optionType is not found.", plugin.getClass().getName());
            return LicenseOptionType.Unknown;
        }
    }

    private MainMenuContainer() {
        try {
            // オプションライセンスを取得する。
            List<SystemOptionEntity> optionLicenses = systemResourceFacade.getLicenseOptions();

            // プラグインを読み込む
            PluginLoader.rebasePath(System.getenv("ADFACTORY_HOME") + File.separator + "plugin");
            plugins.clear();
            plugins.addAll(PluginLoader.load(AdManagerAppMainMenuInterface.class));
            plugins.sort(new PluginComparator());
            logger.info("plugin:{}", plugins);

            for (AdManagerAppMainMenuInterface plugin : plugins) {
                plugin.pluginInitialize();

                // プラグインのライセンスが不要または有効な場合、プラグインのサービスを開始する。
                final LicenseOptionType optionalType = getOptionalType(plugin);
                boolean isLisenceEnabled = false;
                if (LicenseOptionType.NotRequireLicense.equals(optionalType)) {
                    isLisenceEnabled = true;
                } else {
                    Optional<SystemOptionEntity> opt = optionLicenses.stream().filter(p -> p.getOptionName().equals(optionalType.getName())).findFirst();
                    if (opt.isPresent()) {
                        isLisenceEnabled = opt.get().getEnable();
                    }
                }

                if (isLisenceEnabled) {
                    plugin.pluginServiceStart();
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex, ex);
        }
    }

    public static void createInstance() {
        if (Objects.isNull(instance)) {
            instance = new MainMenuContainer();
        }
    }

    public static MainMenuContainer getInstance() {
        if (Objects.isNull(instance)) {
            logger.fatal("not create instance");
        }
        return instance;
    }

    public void pluginDestructor() {
        for (AdManagerAppMainMenuInterface plugin : plugins) {
            plugin.pluginServiceStop();// プラグインのサービスを停止する。
            plugin.pluginDestructor();
        }
    }

    private class PluginComparator implements Comparator<AdManagerAppMainMenuInterface> {

        @Override
        public int compare(AdManagerAppMainMenuInterface o1, AdManagerAppMainMenuInterface o2) {
            if (o1.getDisplayCategory().ordinal() > o2.getDisplayCategory().ordinal()) {
                if (o1.getDisplayOrder() < o2.getDisplayOrder()) {
                    return 4;
                } else if (o1.getDisplayOrder() > o2.getDisplayOrder()) {
                    return 3;
                }
                return 2;
            } else if (o1.getDisplayCategory().ordinal() < o2.getDisplayCategory().ordinal()) {
                if (o1.getDisplayOrder() < o2.getDisplayOrder()) {
                    return -4;
                } else if (o1.getDisplayOrder() > o2.getDisplayOrder()) {
                    return -3;
                }
                return -2;
            }
            if (o1.getDisplayOrder() < o2.getDisplayOrder()) {
                return 1;
            } else if (o1.getDisplayOrder() > o2.getDisplayOrder()) {
                return -1;
            }
            return 0;
        }
    }
    
    public void makeMenuTree(final TreeView<String> treeView, List<SystemOptionEntity> optionLicenses, MenuTypeEnum menuType) {
        try {
            logger.info("makeMenuTree start.");

            Properties properties = new Properties();
            for (SystemOptionEntity optionLicence : optionLicenses) {
                properties.setProperty(optionLicence.getOptionName(), optionLicence.getEnable().toString());
            }

            Optional<SystemOptionEntity> kanbanEditor = optionLicenses.stream().filter((o) -> LicenseOptionType.KanbanEditor.getName().equals(o.getOptionName())).findFirst();
            Optional<SystemOptionEntity> workflowEditor = optionLicenses.stream().filter((o) -> LicenseOptionType.WorkflowEditor.getName().equals(o.getOptionName())).findFirst();
            Optional<SystemOptionEntity> liteOption = optionLicenses.stream().filter((o) -> LicenseOptionType.LiteOption.getName().equals(o.getOptionName())).findFirst();
            Optional<SystemOptionEntity> reporterOption = optionLicenses.stream().filter((o) -> LicenseOptionType.ReporterOption.getName().equals(o.getOptionName())).findFirst();

            final boolean isKanbanEditor = kanbanEditor.isPresent() ? kanbanEditor.get().getEnable() : false;
            final boolean isWorkflowEditor = workflowEditor.isPresent() ? workflowEditor.get().getEnable() : false;
            final boolean isLiteOption = liteOption.isPresent() ? liteOption.get().getEnable() : false;
            final boolean isReporterOption = reporterOption.isPresent() ? reporterOption.get().getEnable() : false;

            final String dailyReportDisplayName = LocaleUtils.getString("key.WorkReportTitle");
            final String progressMonitorDisplayName = LocaleUtils.getString("key.AndonSetting");
            final String workAnalysisDisplayName = LocaleUtils.getString("key.WorkAnalysis");
            final String manufacturingManagementDisplayName = LocaleUtils.getString("key.ProductionNavi.Title");

            // Build tree
            TreeItem<String> root = new TreeItem<>("root");
            root.setExpanded(true);

            Map<TreeItem<String>, Runnable> map = new HashMap<>();
            Map<MainMenuCategoryEnum, TreeItem<String>> categoryNodes = new EnumMap<>(MainMenuCategoryEnum.class);
            
            if(menuType.isTree()) {
                
                for (MainMenuCategoryEnum category : MainMenuCategoryEnum.visibleCategories()) {
                    TreeItem<String> categoryNode = new TreeItem<>(category.getDisplayName());
                    categoryNodes.put(category, categoryNode);
                }
            }

            for (AdManagerAppMainMenuInterface plugin : plugins) {
                // 権限チェック
                LoginUserInfoEntity loginUser = LoginUserInfoEntity.getInstance();
                boolean isAllow = true;
                List<RoleAuthorityType> types = plugin.getRoleAuthorityType();
                if (Objects.nonNull(types)) {
                    isAllow = false;
                    for (RoleAuthorityType auth : types) {
                        RoleAuthorityTypeEnum.add(auth);
                        if (loginUser.checkRoleAuthority(auth)) {
                            isAllow = true;
                        }
                    }
                }

                // ライセンスチェック
                LicenseOptionType pluginLicenseType = getOptionalType(plugin);
                if (!LicenseOptionType.NotRequireLicense.equals(pluginLicenseType)) {
                    boolean isLicensed = false;
                    switch (pluginLicenseType) {
                        case KanbanEditor:
                            isLicensed = (isKanbanEditor || isLiteOption);
                            break;
                        case WorkflowEditor:
                            isLicensed = (isWorkflowEditor || isLiteOption);
                            break;
                        default:
                            if (workAnalysisDisplayName.equals(plugin.getDisplayName()) && isLiteOption && !isKanbanEditor) {
                                isLicensed = false;
                            } else {
                                String optionName = pluginLicenseType.getName();
                                Optional<SystemOptionEntity> find = optionLicenses.stream().filter((o) -> optionName.equals(o.getOptionName())).findFirst();
                                if (find.isPresent()) {
                                    isLicensed = find.get().getEnable();
                                }
                            }
                            break;
                    }
                    if (isAllow && !isLicensed) {
                        isAllow = false;
                    }
                }

                if (isReporterOption && !isKanbanEditor
                        && (workAnalysisDisplayName.equals(plugin.getDisplayName())
                        || dailyReportDisplayName.equals(plugin.getDisplayName())
                        || progressMonitorDisplayName.equals(plugin.getDisplayName())
                        || manufacturingManagementDisplayName.equals(plugin.getDisplayName()))) {
                    isAllow = false;
                }

                if (isAllow) {
                    plugin.setProperties(properties);
                    if (menuType.isTree()) {
                        Map<MainMenuCategoryEnum, List<MenuNode>> nodeMap = plugin.getTreeNodes();
                        if (nodeMap != null && !nodeMap.isEmpty()) {
                            for (Map.Entry<MainMenuCategoryEnum, List<MenuNode>> entry : nodeMap.entrySet()) {
                                MainMenuCategoryEnum category = entry.getKey();
                                List<MenuNode> nodes = entry.getValue();
                                if (nodes != null && !nodes.isEmpty() && categoryNodes.containsKey(category)) {
                                    for (MenuNode menuNode : nodes) {
                                        TreeItem<String> node = menuNode.getTreeItem();
                                        categoryNodes.get(category).getChildren().add(node);
                                        map.put(node, menuNode.getAction());
                                        // Map child actions
                                        for (Map.Entry<TreeItem<String>, Runnable> childAction : menuNode.getChildActions().entrySet()) {
                                            map.put(childAction.getKey(), childAction.getValue());
                                        }
                                        // Only propagate non-null actions to avoid overwriting child actions
                                        if (menuNode.getAction() != null) {
                                            addActionsRecursively(node, menuNode.getAction(), map);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        TreeItem<String> item = new TreeItem<>(plugin.getDisplayName());
                        root.getChildren().add(item);
                        map.put(item, plugin::onSelectMenuAction);
                    }
                    
                } else {
                    logger.warn("plugin:{} is not allow.", plugin.getClass().getName());
                }
            }
            
            if (menuType.isTree()) {
                for (TreeItem<String> categoryNode : categoryNodes.values()) {
                    if (!categoryNode.getChildren().isEmpty()) {
                        root.getChildren().add(categoryNode);
                    }
                }
            }

            // Add special entries: Object Edit (conditional) and Log Out
            boolean isTraceabilityEnabled = false;
            Optional<SystemOptionEntity> traceOpt = optionLicenses.stream().filter((o) -> "@Traceability".equals(o.getOptionName())).findFirst();
            if (traceOpt.isPresent() && Boolean.TRUE.equals(traceOpt.get().getEnable() && !menuType.isTree())) {
                TreeItem<String> objEdit = new TreeItem<>(LocaleUtils.getString("key.ObjectEdit"));
                root.getChildren().add(objEdit);
                map.put(objEdit, () -> {
                    SceneContiner sc = SceneContiner.getInstance();
                    if (!sc.trans("ObjectEditScene")) {
                        return;
                    }
                    sc.visibleArea("MenuPane", false);
                    sc.visibleArea("MenuPaneUnderlay", false);
                    sc.setComponent("AppBarPane", "AppBarCompo");
                    sc.setComponent("ContentNaviPane", "ObjectEditCompo");
                });
            } else {
                TreeItem<String> objectEditItem = new TreeItem<>(SubMenuCategoryEnum.OBJECT.getDisplayName());
                Runnable objectEditAction = () -> {
                    SceneContiner sc = SceneContiner.getInstance();
                    if (!sc.trans("ObjectEditScene")) {
                        return;
                    }
                    sc.visibleArea("MenuPane", false);
                    sc.visibleArea("MenuPaneUnderlay", false);
                    sc.setComponent("AppBarPane", "AppBarCompo");
                    sc.setComponent("ContentNaviPane", "ObjectEditCompo");
                };
                categoryNodes.get(MainMenuCategoryEnum.SETTINGS).getChildren().add(objectEditItem);
                map.put(objectEditItem, objectEditAction);
            }
            
            TreeItem<String> logout = new TreeItem<>(LocaleUtils.getString("key.LogOut"));
            root.getChildren().add(logout);
            map.put(logout, () -> {
                SceneContiner sc = SceneContiner.getInstance();
                sc.trans("LoginScene");
            });

            treeView.setRoot(root);
            treeView.setShowRoot(false);
            treeView.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 1) {
                    TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
                    if (selected != null && map.containsKey(selected)) {
                        map.get(selected).run();
                    }
                }
            });
        } finally {
            logger.info("makeMenuTree end.");
        }
    }
    
     // Helper method to recursively add actions for all child nodes
    private void addActionsRecursively(TreeItem<String> node, Runnable action, Map<TreeItem<String>, Runnable> actionMap) {
        for (TreeItem<String> child : node.getChildren()) {
            actionMap.put(child, action);
            addActionsRecursively(child, action, actionMap);
        }
    }
}
