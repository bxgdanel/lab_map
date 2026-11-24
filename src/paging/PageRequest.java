package paging;

public record PageRequest(int page, int pageSize) {
    /**
     * Calculates the offset based on the page number and page size
     * @return the calculated offset
     */
    public int offset() {
        return page * pageSize;
    }
    public PageRequest{
        if(page<0 || pageSize<=0)
            throw new IllegalArgumentException("pageNumber must be >=0 and pageSize must be >0");
    }
}