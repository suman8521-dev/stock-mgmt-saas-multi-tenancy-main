package com.saas.services.impl;

import com.saas.common.PageResponse;
import com.saas.entities.Category;
import com.saas.exceptions.BusinessException;
import com.saas.mappers.CategoryMapper;
import com.saas.repositories.CategoryRepository;
import com.saas.requests.CategoryRequest;
import com.saas.responses.CategoryResponse;
import com.saas.services.CategoryService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public void create(CategoryRequest request) {
        checkCategoryExistByName(request.getName());
        final Category category=categoryMapper.toEntity(request);
        this.categoryRepository.save(category);
    }


    @Override
    public void update(String id, CategoryRequest request) {

        Category category = categoryRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException("Category not found" + id));

        if (!category.getName().equalsIgnoreCase(request.getName())){
                checkCategoryExistByName(request.getName());
        }
        Category updatecategory = this.categoryMapper.toEntity(request);
        updatecategory.setId(id);
        this.categoryRepository.save(updatecategory);

    }

    @Override
    public CategoryResponse findById(String id) {
        return categoryRepository
                .findById(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(()-> new BusinessException("Category not found"+ id));
    }

    @Override
    public PageResponse<CategoryResponse> findAll(final int pageNo, final int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Category> categories = this.categoryRepository.findAll(pageRequest);
        Page<CategoryResponse> categoryResponses = categories.map(this.categoryMapper::toResponse);
        return PageResponse.of(categoryResponses);
    }

    @Override
    public void delete(String id) {
        Category category = categoryRepository
                .findById(id)
                .orElseThrow(() -> new BusinessException("Category not found" + id));
        this.categoryRepository.delete(category);
    }


    private void checkCategoryExistByName(@NotBlank(message = "Category name should not be empty")
                                          @Size(min = 3, max = 255, message = "Category name should be between 3 and 255 characters")
                                          String name) {
        Optional<Category> category=categoryRepository.findByNameIgnoreCase(name);
        if (category.isPresent()){
            log.debug("Category Already Exist");
            throw new BusinessException("Category Already Exist");
        }
    }
}
