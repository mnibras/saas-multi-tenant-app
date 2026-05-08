package com.nibras.saas.service;

import com.nibras.saas.common.PageResponse;

public interface BasicService<I, O> {

    void create(final I request);

    void update(final String id, final I request);

    O findById(final String id);

    PageResponse<O> findAll(final int page, final int size);

    void delete(final String id);

}
