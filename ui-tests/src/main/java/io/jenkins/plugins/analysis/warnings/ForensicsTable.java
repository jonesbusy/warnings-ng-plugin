package io.jenkins.plugins.analysis.warnings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.jenkinsci.test.acceptance.po.PageObject;

/**
 * Class representing an forensics-table on the {@link AnalysisResult}.
 *
 * @author Thomas Großbeck
 */
@SuppressFBWarnings("EI")
public class ForensicsTable {
    private final AnalysisResult resultDetailsPage;
    private final List<GenericTableRow> tableRows = new ArrayList<>();
    private final List<String> headers;
    private final WebElement tableElement;
    private final WebElement foreniscsTab;
    private final ForensicsTableRowType type;

    /**
     * Creates an ForensicsTable of a specific type.
     *
     * @param foreniscsTab
     *         the WebElement containing the forensics-tab
     * @param resultDetailsPage
     *         the AnalysisResult on which the forensics-table is displayed on
     * @param type
     *         the type of the forensics-table (e.g. Default or DRY)
     */
    public ForensicsTable(final WebElement foreniscsTab, final AnalysisResult resultDetailsPage,
            final ForensicsTableRowType type) {
        this.foreniscsTab = foreniscsTab;
        this.resultDetailsPage = resultDetailsPage;
        this.type = type;

        tableElement = foreniscsTab.findElement(By.id("forensics"));
        headers = tableElement.findElements(By.xpath(".//thead/tr/th"))
                .stream()
                .map(WebElement::getText)
                .collect(
                        Collectors.toList());
        updateTableRows();
    }

    /**
     * Performs a click on a link on this site and returns the corresponding PageObject of the target page.
     *
     * @param link
     *         the WebElement representing the link
     * @param targetPageClass
     *         the class of the {@link PageObject} representing the target page
     * @param <T>
     *         actual type of the page object
     *
     * @return the PageObject representing the target page
     */
    public <T extends PageObject> T clickLinkOnSite(final WebElement link, final Class<T> targetPageClass) {
        return resultDetailsPage.openLinkOnSite(link, targetPageClass);
    }

    /**
     * Returns the totals value of the table.
     *
     * @return the totals
     */
    public int getTotal() {
        String tableInfo = foreniscsTab.findElement(By.id("forensics_info")).getText();
        String total = StringUtils.substringAfter(tableInfo, "of ");
        return Integer.parseInt(StringUtils.substringBefore(total, " "));
    }

    /**
     * Updates the table rows. E.g. if they are changed by toggling a details-row.
     */
    public final void updateTableRows() {
        tableRows.clear();
        List<WebElement> tableRowsAsWebElements = tableElement.findElements(By.xpath(".//tbody/tr"));
        tableRowsAsWebElements.forEach(element -> tableRows.add(getRightTableRow(element)));
    }

    /**
     * Returns the table row as an object of the right sub class of {@link GenericTableRow}.
     *
     * @param row
     *         the WebElement representing the specific row.
     *
     * @return the table row
     */
    private GenericTableRow getRightTableRow(final WebElement row) {
        return new ForensicsTableRow(row, this);
    }

    /**
     * Returns the amount of the headers for this table.
     *
     * @return the amount of table headers
     */
    public int getHeaderSize() {
        return headers.size();
    }

    /**
     * Returns the amount of table rows.
     *
     * @return the amount of table rows.
     */
    public int getSize() {
        return tableRows.size();
    }

    /**
     * Returns the table rows as List.
     *
     * @return the rows of the table
     */
    public List<GenericTableRow> getTableRows() {
        return tableRows;
    }

    /**
     * Return the headers of the table.
     *
     * @return the headers
     */
    public List<String> getHeaders() {
        return headers;
    }

    /**
     * Returns a specific row as an instance of the expected class.
     *
     * @param row
     *         the number of the row to be returned
     * @param expectedClass
     *         the expected type of the row
     * @param <T>
     *         actual type of the row
     *
     * @return the row
     */
    public <T extends GenericTableRow> T getRowAs(final int row, final Class<T> expectedClass) {
        return getTableRows().get(row).getAs(expectedClass);
    }

    /**
     * Performs a click on a link which opens a filtered instance of the AnalysisResult.
     *
     * @param element
     *         the WebElement representing the link
     *
     * @return the filtered AnalysisResult
     */
    public AnalysisResult clickFilterLinkOnSite(final WebElement element) {
        return resultDetailsPage.openFilterLinkOnSite(element);
    }

    /**
     * Supported element types of the forensics table.
     */
    public enum ForensicsTableRowType {
        DEFAULT,
        DRY
    }
}
