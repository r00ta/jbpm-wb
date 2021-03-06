/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.workitem.project;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import elemental2.dom.Element;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLTableSectionElement;

@Templated
public class ProjectServiceTasksView implements ProjectServiceTasksPresenter.View {

    @Inject
    @Named("tbody")
    @DataField("serviceTasks")
    private HTMLTableSectionElement serviceTasks;

    @Inject
    @Named("h3")
    @DataField("title")
    private HTMLHeadingElement title;

    @Override
    public void init(final ProjectServiceTasksPresenter presenter) {
    }

    @Override
    public Element getServiceTasksTable() {
        return serviceTasks;
    }

    @Override
    public String getTitle() {
        return title.textContent;
    }

}
