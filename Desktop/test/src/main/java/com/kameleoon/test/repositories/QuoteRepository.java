package com.kameleoon.test.repositories;

import com.kameleoon.test.model.Quote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    @Query(value = "select * from quotes order by random() limit 1", nativeQuery = true)
    Quote findRandom();

    @Query("select q from Quote q order by q.votes asc")
    List<Quote> findTop10asc(Pageable pageable);

    @Query("select q from Quote q order by q.votes desc")
    List<Quote> findTop10desc(Pageable pageable);

    @Query("select q from Quote q where q.id =?1")
    Quote findQuoteById(Long id);
}
