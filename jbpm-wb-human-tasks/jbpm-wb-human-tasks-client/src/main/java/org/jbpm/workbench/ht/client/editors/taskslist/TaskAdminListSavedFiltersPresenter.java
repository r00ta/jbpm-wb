/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.ht.client.editors.taskslist;

import java.util.function.Consumer;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jbpm.workbench.common.client.filters.saved.SavedFiltersPresenter;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.menu.Menus;

import static org.jbpm.workbench.common.client.PerspectiveIds.TASK_ADMIN_LIST_SAVED_FILTERS_SCREEN;

@Dependent
@WorkbenchScreen(identifier = TASK_ADMIN_LIST_SAVED_FILTERS_SCREEN)
public class TaskAdminListSavedFiltersPresenter extends SavedFiltersPresenter {

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @Inject
    public void setFilterSettingsManager(final TaskAdminListFilterSettingsManager filterSettingsManager) {
        super.setFilterSettingsManager(filterSettingsManager);
    }
}
