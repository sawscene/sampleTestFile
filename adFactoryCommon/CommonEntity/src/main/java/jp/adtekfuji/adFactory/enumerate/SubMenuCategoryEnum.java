/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jp.adtekfuji.adFactory.enumerate;

import adtekfuji.locale.LocaleUtils;

/**
 *
 * @author SashinRanjitkar
 */
public enum SubMenuCategoryEnum {
    
    KANBAN("key.SubMenuTitle.Kanban"),
    LITE_KANBAN_TITLE("key.SubMenuTitle.LiteKanbanTitle"),
    PLAN_LOADING("key.SubMenuTitle.PlanLoading"),
    MONITOR_TYPE("key.SubMenuTitle.MonitorType"),
    MONITOR_TYPE_ELS("key.SubMenuTitle.MonitorTypeELS"),
    ANALYSIS_TITLE("key.SubMenuTitle.AnalysisTitle"),
    TIME_LINE("key.SubMenuTitle.TimeLine"),
    KANBAN_TOTAL_WORK_TIME("key.SubMenuTitle.KanbanTotalWorkTime"),
    PROCESS_AVERAGE_WORK_TIME("key.SubMenuTitle.ProcessAverageWorkTime"),
    WORKER_AVERAGE_WORK_TIME("key.SubMenuTitle.WorkerAverageWorkTime"),
    LEDGER_MANAGER("key.SubMenuTitle.LedgerManager"),
    LINE_TIMER("key.SubMenuTitle.LineTimer"),
    ORGANIZATION("key.SubMenuTitle.Organization"),
    OUTPUT_ACTUAL("key.SubMenuTitle.OutputActual"),
    EDIT_SUMMARY_REPORT_TITLE("key.SubMenuTitle.EditSummaryReportTitle"),
    REASON_REGISTRATION_TITLE("key.SubMenuTitle.ReasonRegistration.Title"),
    INTERRUPT("key.SubMenuTitle.Interrupt"),
    DELAY("key.SubMenuTitle.Delay"),
    CALL("key.SubMenuTitle.Call"),
    KANBAN_STATUS_DEFECT("key.SubMenuTitle.KanbanStatusDefect"),
    STATUS_PATTERN_BREAK_TIME("key.SubMenuTitle.StatusPatternBreakTime"),
    INDIRECT_WORK("key.SubMenuTitle.IndirectWork"),
    LABEL("key.SubMenuTitle.Label"),
    APPROVAL_ROUTE("key.SubMenuTitle.ApprovalRoute"),
    WORKING_STATUS("key.SubMenuTitle.WorkingStatus"),
    EDIT_ROLE_TITLE("key.SubMenuTitle.EditRoleTitle"),
    STOCK_VIEW("key.SubMenuTitle.StockView"),
    PAYOUT_MANAGEMENT("key.SubMenuTitle.PayoutManagement"),
    LOT_TRACE_TITLE("key.SubMenuTitle.LotTraceTitle"),
    LOT_TRACE_PRODUCT("key.SubMenuTitle.LotTraceProduct"),
    LOT_TRACE_MATERIAL("key.SubMenuTitle.LotTraceMaterial"),
    LOT_TRACE_AUTHORITY_WORKER("key.SubMenuTitle.LotTraceAuthorityWoker"),
    OPERATION_LOG("key.SubMenuTitle.OperationLog"),
    IMPORT("key.SubMenuTitle.Import"),
    PROCESS("key.SubMenuTitle.Process"),
    ORDER_PROCESSES("key.SubMenuTitle.OrderProcesses"),
    LITE_ORDER_PROCESS("key.SubMenuTitle.LiteOrderProcess"),
    WORK_REPORT_TITLE("key.SubMenuTitle.WorkReportTitle"),
    EQUIPMENT("key.SubMenuTitle.Equipment"),
    OBJECT("key.SubMenuTitle.Object"),
    PRODUCTION_NAVI_ROSTER("key.SubMenuTitle.ProductionNavi.Roster"),
    KANBAN_IMPORT("key.SubMenuTitle.KanbanImport");

    private final String resourceKey;

    private SubMenuCategoryEnum(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    public String getResourceKey() {
        return resourceKey;
    }
    
    public String getDisplayName() {
        return LocaleUtils.getString(resourceKey);
    }
        
}
