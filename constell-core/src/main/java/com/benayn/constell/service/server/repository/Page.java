package com.benayn.constell.service.server.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString(exclude = "resource")
public final class Page<R> {

    /**
     * Current Page No
     */
    private int index;
    /**
     * Page Size
     */
    private int size;
    /**
     * Total Size
     */
    private long rows;

    // Below is computer by index, size, rows

    /**
     * Start row, first row is zero
     */
    private int offset;

    /**
     * Total page count
     */
    private int pages;
    /**
     * First Page No
     */
    private int first = 1;
    /**
     * Last Page No
     */
    private int last;
    /**
     * Next Page No
     */
    private int next;
    /**
     * Previous Page No
     */
    private int prev;


    /**
     * Page Navigation, Start Page No
     */
    private int navStart;
    /**
     * Page Navigation, End Page No
     */
    private int navEnd;
    /**
     * Page Navigation, Show Page Count, Max is numCount+1
     */
    private int navCount = 10;
    /**
     * Page Navigation, Show Page No
     */
    private List<Integer> navPages = Lists.newArrayList();

    @Setter
    private List<R> resource = Lists.newArrayList();

    /**
     * Column name and title map
     */
    @Setter
    private Map<String, String> titles;
    /**
     * Column name
     */
    @Setter
    private List<String> columns;

    public void addColumn(String columnName) {
        if (null == columns) {
            columns = Lists.newArrayList();
        }

        columns.add(columnName);
    }

    public void addTitle(String columnName, String columnTitle) {
        if (null == titles) {
            titles = Maps.newHashMap();
        }

        titles.put(columnName, columnTitle);
    }

    public <T> Page<T> cloneButResource() {
        Page<T> np = new Page<>();
        np.index = index;
        np.size = size;
        np.rows = rows;
        np.offset = offset;
        np.pages = pages;
        np.first = first;
        np.last = last;
        np.next = next;
        np.prev = prev;
        np.navStart = navStart;
        np.navEnd = navEnd;
        np.navCount = navCount;
        np.navPages = navPages;

        return np;
    }

    public static <R> Page<R> of(int pageNo, int pageSize) {
        return of(pageNo, pageSize, 0);
    }

    public static <R> Page<R> of(int pageNo, int pageSize, long totalSize) {
        return new Page<>(pageNo, pageSize, totalSize);
    }

    private Page() { }

    private Page(int pageNo, int pageSize, long totalSize) {
        index = pageNo <= 0 ? 1 : pageNo;
        size = pageSize;
        offset = (index - 1) * size;

        if (totalSize > 0) {
            rows = totalSize;
            pages = (int) Math.ceil((double) rows / size);
            index = Math.min(index, pages);
            index = Math.max(1, index);

            last = pages;
            next = Math.min(pages, index + 1);
            prev = Math.max(1, index - 1);

            navStart = Math.max(index - navCount / 2, first);
            navEnd = Math.min(navStart + navCount, last);

            if (navEnd - navStart < navCount) {
                navStart = Math.max(navEnd - navCount, 1);
            }

            for (int idx = navStart; idx <= navEnd; idx++) {
                navPages.add(idx);
            }
        } else {
            next = index + 1;
            prev = Math.max(1, index - 1);
        }

    }

}
