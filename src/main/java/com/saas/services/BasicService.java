package com.saas.services;

import com.saas.common.PageResponse;

import java.util.List;

public interface BasicService<I,O> {

    void create(final I request);
    void update(final String id ,I request);
    O findById(final String id);
    PageResponse<O> findAll(final int pageNo,final int pageSize);
    void delete( final String id );
}
