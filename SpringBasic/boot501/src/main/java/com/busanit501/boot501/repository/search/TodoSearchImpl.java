package com.busanit501.boot501.repository.search;

import com.busanit501.boot501.domain.QTodo;
import com.busanit501.boot501.domain.Todo;
import com.busanit501.boot501.dto.PageRequestDTO;
import com.busanit501.boot501.dto.TodoDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

@Log4j2
public class TodoSearchImpl extends QuerydslRepositorySupport implements TodoSearch {
    public TodoSearchImpl() {
        super(Todo.class);
    }

    @Override
    public Page<TodoDTO> list(PageRequestDTO pageRequestDTO) {
        QTodo todo = QTodo.todo;
        JPQLQuery<Todo> query = from(todo);
        if (pageRequestDTO.getFrom() != null && pageRequestDTO.getTo() != null) {
            BooleanBuilder fromToBuilder = new BooleanBuilder();
            fromToBuilder.and(todo.dueDate.goe(pageRequestDTO.getFrom()));

            fromToBuilder.and(todo.dueDate.loe(pageRequestDTO.getTo()));
            query.where(fromToBuilder);
        }
        if (pageRequestDTO.getCompleted() != null) {
            query.where(todo.complete.eq(pageRequestDTO.getCompleted()));
        }
        if (pageRequestDTO.getKeyword() != null) {
            query.where(todo.title.contains(pageRequestDTO.getKeyword()));
        }
        this.getQuerydsl().applyPagination(pageRequestDTO.getPageable("tno"), query);
        JPQLQuery<TodoDTO> dtoQuery = query.select(Projections.bean(TodoDTO.class, todo.tno, todo.title, todo.dueDate, todo.complete, todo.writer));
        List<TodoDTO> list = dtoQuery.fetch();
        long count = dtoQuery.fetchCount();
        return new PageImpl<>(list, pageRequestDTO.getPageable("tno"), count);
    }
}
