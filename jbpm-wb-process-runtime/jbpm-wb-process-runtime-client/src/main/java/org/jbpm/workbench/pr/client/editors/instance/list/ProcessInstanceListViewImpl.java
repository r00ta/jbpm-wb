/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.workbench.pr.client.editors.instance.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.Column;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.jbpm.workbench.common.client.list.AbstractMultiGridView;
import org.jbpm.workbench.common.client.list.ExtendedPagedTable;
import org.jbpm.workbench.common.client.list.ListTable;
import org.jbpm.workbench.common.client.util.*;
import org.jbpm.workbench.pr.client.resources.i18n.Constants;
import org.jbpm.workbench.pr.model.ProcessInstanceSummary;
import org.kie.api.runtime.process.ProcessInstance;
import org.uberfire.client.views.pfly.widgets.ConfirmPopup;
import org.uberfire.ext.widgets.table.client.ColumnMeta;
import org.uberfire.mvp.Command;

import static org.jbpm.workbench.pr.model.ProcessInstanceDataSetConstants.*;

@Dependent
@Templated(value = "/org/jbpm/workbench/common/client/list/AbstractMultiGridView.html", stylesheet = "/org/jbpm/workbench/common/client/resources/css/kie-manage.less")
public class ProcessInstanceListViewImpl extends AbstractMultiGridView<ProcessInstanceSummary, ProcessInstanceListPresenter>
        implements ProcessInstanceListPresenter.ProcessInstanceListView {

    private final Constants constants = Constants.INSTANCE;

    @Inject
    private ConfirmPopup confirmPopup;

    @Inject
    private ManagedInstance<GenericErrorSummaryCountCell> popoverCellInstance;

    @Override
    public List<String> getInitColumns() {
        return Arrays.asList(COL_ID_SELECT,
                             COLUMN_PROCESS_INSTANCE_ID,
                             COLUMN_PROCESS_NAME,
                             COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                             COLUMN_PROCESS_VERSION,
                             COLUMN_LAST_MODIFICATION_DATE,
                             COLUMN_ERROR_COUNT,
                             COL_ID_ACTIONS);
    }

    @Override
    public List<String> getBannedColumns() {
        return Arrays.asList(COL_ID_SELECT,
                             COLUMN_PROCESS_INSTANCE_ID,
                             COLUMN_PROCESS_NAME,
                             COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                             COL_ID_ACTIONS);
    }

    @Override
    public String getEmptyTableCaption() {
        return constants.No_Process_Instances_Found();
    }

    @Override
    public List<AnchorListItem> getBulkActionsItems(ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable) {
        List<AnchorListItem> bulkActionsItems = new ArrayList<>();
        bulkActionsItems.add(getBulkAbort(extendedPagedTable));
        bulkActionsItems.add(getBulkSignal(extendedPagedTable));
        return bulkActionsItems;
    }

    @Override
    public void initColumns(final ListTable<ProcessInstanceSummary> extendedPagedTable) {
        final ColumnMeta checkColumnMeta = initChecksColumn(extendedPagedTable);
        ColumnMeta<ProcessInstanceSummary> actionsColumnMeta = initActionsColumn();
        Column<ProcessInstanceSummary, ?> errorCountColumn = initErrorCountColumn();
        final Column<ProcessInstanceSummary, String> startColumn = createTextColumn(COLUMN_START,
                                                                                    process -> DateUtils.getDateTimeStr(process.getStartTime()));

        final List<ColumnMeta<ProcessInstanceSummary>> columnMetas = getGeneralColumnMetas(extendedPagedTable,
                                                                                           startColumn,
                                                                                           checkColumnMeta,
                                                                                           actionsColumnMeta,
                                                                                           errorCountColumn);

        columnMetas.addAll(renameVariables(extendedPagedTable, columnMetas));

        extendedPagedTable.addColumns(columnMetas);

        extendedPagedTable.setColumnWidth(checkColumnMeta.getColumn(),
                                          CHECK_COLUMN_WIDTH,
                                          Style.Unit.PX);
        extendedPagedTable.setColumnWidth(errorCountColumn,
                                          ERROR_COLUMN_WIDTH,
                                          Style.Unit.PX);

        extendedPagedTable.setColumnWidth(actionsColumnMeta.getColumn(),
                                          ACTIONS_COLUMN_WIDTH,
                                          Style.Unit.PX);

        extendedPagedTable.getColumnSortList().push(startColumn);
    }

    protected List<ColumnMeta<ProcessInstanceSummary>> getGeneralColumnMetas(final ListTable<ProcessInstanceSummary> extendedPagedTable,
                                                                             final Column<ProcessInstanceSummary, String> startColumn,
                                                                             final ColumnMeta checkColumnMeta,
                                                                             final ColumnMeta<ProcessInstanceSummary> actionsColumnMeta,
                                                                             final Column<ProcessInstanceSummary, ?> errorCountColumn) {
        Column<ProcessInstanceSummary, ?> slaComplianceColumn = initSlaComplianceColumn();
        extendedPagedTable.addSelectionIgnoreColumn(checkColumnMeta.getColumn());
        extendedPagedTable.addSelectionIgnoreColumn(actionsColumnMeta.getColumn());
        extendedPagedTable.addSelectionIgnoreColumn(errorCountColumn);

        final List<ColumnMeta<ProcessInstanceSummary>> columnMetas = new ArrayList<ColumnMeta<ProcessInstanceSummary>>();

        columnMetas.add(checkColumnMeta);
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_INSTANCE_ID,
                                                          process -> String.valueOf(process.getProcessInstanceId())),
                                         constants.Id()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_NAME,
                                                          process -> process.getProcessName()),
                                         constants.Name()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                                          process -> process.getProcessInstanceDescription()),
                                         constants.Process_Instance_Description()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_IDENTITY,
                                                          process -> process.getInitiator()),
                                         constants.Initiator()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PROCESS_VERSION,
                                                          process -> process.getProcessVersion()),
                                         constants.Version()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_PARENT_PROCESS_INSTANCE_ID,
                                                          process -> process.getParentId() == -1 ? "" : process.getParentId().toString()),
                                         constants.Parent_Process_Instance_Id()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_STATUS,
                                                          process -> {
                                                              switch (process.getState()) {
                                                                  case ProcessInstance.STATE_ACTIVE:
                                                                      return constants.Active();
                                                                  case ProcessInstance.STATE_ABORTED:
                                                                      return constants.Aborted();
                                                                  case ProcessInstance.STATE_COMPLETED:
                                                                      return constants.Completed();
                                                                  case ProcessInstance.STATE_PENDING:
                                                                      return constants.Pending();
                                                                  case ProcessInstance.STATE_SUSPENDED:
                                                                      return constants.Suspended();
                                                                  default:
                                                                      return constants.Unknown();
                                                              }
                                                          }),
                                         constants.State()));
        startColumn.setDefaultSortAscending(false);
        columnMetas.add(new ColumnMeta<>(startColumn,
                                         constants.Start_Date()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_LAST_MODIFICATION_DATE,
                                                          process -> DateUtils.getDateTimeStr(process.getLastModificationDate())),
                                         constants.Last_Modification_Date()));

        columnMetas.add(new ColumnMeta<>(slaComplianceColumn,
                                         constants.SlaCompliance()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_SLA_DUE_DATE,
                                                          process -> DateUtils.getDateTimeStr(process.getSlaDueDate())),
                                         constants.SlaDueDate()));
        columnMetas.add(new ColumnMeta<>(createTextColumn(COLUMN_CORRELATION_KEY,
                                                          process -> process.getCorrelationKey()),
                                         constants.Correlation_Key()));
        columnMetas.add(new ColumnMeta<>(errorCountColumn,
                                         constants.Errors()));
        columnMetas.add(actionsColumnMeta);
        return columnMetas;
    }

    protected Column initGenericColumn(final String key) {
        return createTextColumn(key,
                                instance -> instance.getDomainDataValue(key));
    }

    protected AnchorListItem getBulkAbort(final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable) {
        final AnchorListItem bulkAbortNavLink = GWT.create(AnchorListItem.class);
        bulkAbortNavLink.setText(constants.Bulk_Abort());
        bulkAbortNavLink.setIcon(IconType.BAN);
        bulkAbortNavLink.setIconFixedWidth(true);
        bulkAbortNavLink.addClickHandler(event -> confirmPopup.show(constants.Abort_Confirmation(),
                                                                    constants.Abort(),
                                                                    constants.Abort_Process_Instances(),
                                                                    getAbortCommand(extendedPagedTable))
        );
        return bulkAbortNavLink;
    }

    protected AnchorListItem getBulkSignal(final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable) {
        final AnchorListItem bulkSignalNavLink = GWT.create(AnchorListItem.class);
        bulkSignalNavLink.setText(constants.Bulk_Signal());
        bulkSignalNavLink.setIcon(IconType.BELL);
        bulkSignalNavLink.setIconFixedWidth(true);
        bulkSignalNavLink.addClickHandler(event -> getSignalCommand(extendedPagedTable).execute());
        return bulkSignalNavLink;
    }

    protected Command getSignalCommand(final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable) {
        return () -> presenter.bulkSignal(extendedPagedTable.getSelectedItems());
    }

    protected Command getAbortCommand(final ExtendedPagedTable<ProcessInstanceSummary> extendedPagedTable) {
        return () -> presenter.bulkAbort(extendedPagedTable.getSelectedItems());
    }

    protected Column<ProcessInstanceSummary, ProcessInstanceSummary> initErrorCountColumn() {

        Column<ProcessInstanceSummary, ProcessInstanceSummary> column = new Column<ProcessInstanceSummary, ProcessInstanceSummary>(
                popoverCellInstance.get().init(presenter)) {

            @Override
            public ProcessInstanceSummary getValue(ProcessInstanceSummary process) {
                return process;
            }
        };

        column.setSortable(true);
        column.setDataStoreName(COLUMN_ERROR_COUNT);
        return column;
    }

    protected Column<ProcessInstanceSummary, Integer> initSlaComplianceColumn() {

        Column<ProcessInstanceSummary, Integer> column = new Column<ProcessInstanceSummary, Integer>(
                new SLAComplianceCell()) {

            @Override
            public Integer getValue(ProcessInstanceSummary process) {
                return process.getSlaCompliance();
            }
        };

        column.setSortable(true);
        column.setDataStoreName(COLUMN_SLA_COMPLIANCE);
        return column;
    }

    @Override
    protected List<ConditionalAction<ProcessInstanceSummary>> getConditionalActions() {
        return Arrays.asList(

                new ConditionalAction<>(
                        constants.Signal(),
                        processInstance -> presenter.signalProcessInstance(processInstance),
                        presenter.getSignalActionCondition(),
                        false
                ),

                new ConditionalAction<>(
                        constants.Abort(),
                        processInstance -> {
                            confirmPopup.show(constants.Abort_Confirmation(),
                                              constants.Abort(),
                                              constants.Abort_Process_Instance(),
                                              () -> presenter.abortProcessInstance(processInstance.getDeploymentId(),
                                                                                   processInstance.getProcessInstanceId()));
                        },
                        presenter.getAbortActionCondition(),
                        false
                ),

                new ConditionalAction<>(
                        constants.ViewJobs(),
                        processInstance -> presenter.openJobsView(Long.toString(processInstance.getProcessInstanceId())),
                        presenter.getViewJobsActionCondition(),
                        true
                ),

                new ConditionalAction<>(
                        constants.ViewTasks(),
                        processInstance -> presenter.openTaskView(Long.toString(processInstance.getProcessInstanceId())),
                        presenter.getViewTasksActionCondition(),
                        true
                ),

                new ConditionalAction<>(
                        constants.ViewErrors(),
                        processInstance -> presenter.openErrorView(Long.toString(processInstance.getProcessInstanceId())),
                        presenter.getViewErrorsActionCondition(),
                        true
                )

        );
    }
}
