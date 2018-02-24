/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010-2014, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.richfaces.showcase.toolBar;

import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.richfaces.showcase.AbstractWebDriverTest;
import org.richfaces.showcase.toolBar.page.ToolbarPage;

import com.google.common.collect.Sets;

/**
 * @author <a href="mailto:jhuska@redhat.com">Juraj Huska</a>
 * @version $Revision$
 */
public class ITestToolBar extends AbstractWebDriverTest {

    private static final String CONTEXT = "/images/icons/";
    private static final String[] EXPECTED_SRCS = { CONTEXT + "create_doc.gif", CONTEXT + "create_folder.gif",
        CONTEXT + "copy.gif", CONTEXT + "save.gif", CONTEXT + "save_as.gif", CONTEXT + "save_all.gif" };
    private static final Pattern SRC_PATTERN_PORTAL = Pattern.compile("resourceID=(.*?)&");

    @Page
    private ToolbarPage page;

    @Test
    public void testExpectedElementsArePresent() {
        String src;
        Matcher matcher;
        Set<String> actualElements = Sets.newHashSet();
        for (WebElement image : page.getToolbarImages()) {
            assertTrue("Toolbar image should be present", image.isDisplayed());
            src = image.getAttribute("src");
            if (!runInPortalEnv) {
                actualElements.add(src);
            } else {
                // workaround for portal environment because src attribute is autogenerated there
                matcher = SRC_PATTERN_PORTAL.matcher(src);
                matcher.find();
                actualElements.add("/showcase" + matcher.group(1));
            }
        }
        checkExpectedSrcs(actualElements);

        assertTrue("The separator element is missing", isElementPresent(page.getSeparator()));
        assertTrue("The search input is missing", isElementPresent(page.getSearchInput()));
        assertTrue("The search input is missing", isElementPresent(page.getSearchButton()));
    }

    /**
     * Checks whether particular set contains all expected values
     *
     * @param set
     */
    private void checkExpectedSrcs(Set<String> set) {
        // must be done in this akward way, as the src attribute varies accross different JSF implementations
        // this should be pretty robust
        for (String expected : EXPECTED_SRCS) {
            boolean present = false;
            for (String actual : set) {
                if (actual.contains(expected)) {
                    present = true;
                    break;
                }
            }
            assertTrue("There is missing element " + expected, present);
        }
    }
}
