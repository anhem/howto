package com.github.anhem.howto.repository;

import com.github.anhem.howto.exception.NotFoundException;
import com.github.anhem.howto.model.Category;
import com.github.anhem.howto.model.id.CategoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

import static com.github.anhem.howto.repository.mapper.CategoryMapper.mapToCategory;

@Slf4j
@Repository
public class CategoryRepository extends JdbcRepository {

    private static final String SELECT_CATEGORIES = "SELECT * FROM category";
    private static final String SELECT_CATEGORY_BY_ID = "SELECT * FROM category WHERE category_id = :categoryId";
    private static final String DELETE_CATEGORY = "DELETE FROM category WHERE category_id = :categoryId";
    private static final String INSERT_CATEGORY = "INSERT INTO category(name, description, created, last_updated) values(:name, :description, :created, :lastUpdated)";
    private static final String UPDATE_CATEGORY = "UPDATE category SET name = :name, description = :description, last_updated = :lastUpdated WHERE category_id = :categoryId";

    protected CategoryRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    public List<Category> getCategories() {
        return namedParameterJdbcTemplate.query(SELECT_CATEGORIES, (rs, i) -> mapToCategory(rs));
    }

    public Category getCategory(CategoryId categoryId) {
        try {
            MapSqlParameterSource parameters = createParameters("categoryId", categoryId.value());
            return namedParameterJdbcTemplate.queryForObject(SELECT_CATEGORY_BY_ID, parameters, (rs, i) -> mapToCategory(rs));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(categoryId);
        }
    }

    public CategoryId createCategory(Category category) {
        MapSqlParameterSource parameters = createParameters("categoryId", category.getCategoryId().value())
                .addValue("name", category.getName())
                .addValue("description", category.getDescription())
                .addValue("created", Timestamp.from(category.getCreated()))
                .addValue("lastUpdated", Timestamp.from(category.getLastUpdated()));
        return insert(INSERT_CATEGORY, parameters, CategoryId.class);
    }

    public void updateCategory(Category category) {
        MapSqlParameterSource parameters = createParameters("categoryId", category.getCategoryId().value())
                .addValue("name", category.getName())
                .addValue("description", category.getDescription())
                .addValue("lastUpdated", Timestamp.from(category.getLastUpdated()));
        namedParameterJdbcTemplate.update(UPDATE_CATEGORY, parameters);
    }

    public void removeCategory(CategoryId categoryId) {
        MapSqlParameterSource parameters = createParameters("categoryId", categoryId.value());
        namedParameterJdbcTemplate.update(DELETE_CATEGORY, parameters);
        log.info("Category {} removed", categoryId);
    }
}
