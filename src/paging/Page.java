package paging;

import java.util.List;

public record Page<T>(List<T> items, int totalSize) {
    /***
     * returns the number of pages
     * @param pageSize - size of a page
     * @return total number of pages
     */
    public int totalPages(int pageSize){
        return Math.max(1,(totalSize + pageSize -1)/pageSize);
    }
}
