package Repo;

import Domain.Entity;
import paging.Page;
import paging.PageRequest;


public interface PagingRepository<ID,E extends Entity> extends Repo<ID,E> {
    /**
     * Finds all users on a page
     * @param pageRequest
     * @return - a page containing users
     */
    Page<E> findAllOnPage(PageRequest pageRequest);

}
