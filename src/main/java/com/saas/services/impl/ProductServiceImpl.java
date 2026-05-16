package com.saas.services.impl;
import com.saas.common.PageResponse;
import com.saas.entities.Category;
import com.saas.entities.Product;
import com.saas.exceptions.DuplicateResourceException;
import com.saas.mappers.ProductMapper;
import com.saas.repositories.CategoryRepository;
import com.saas.repositories.ProductRepository;
import com.saas.requests.ProductRequest;
import com.saas.responses.ProductResponse;
import com.saas.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public void create(ProductRequest request) {
        checkIfProductAlreadyExistsByReference(request.getReference());

        // check if category exists
        checkIfCategoryExistById(request.getCategoryId());

        final Product entity = this.productMapper.toEntity(request);
        this.productRepository.save(entity);
    }

    @Override
    public void update(String id, ProductRequest request) {
        // check if product exists
        final Optional<Product> productExists = this.productRepository.findById(id);
        if (productExists.isEmpty()) {
            log.debug("Product does not exist");
            throw new EntityNotFoundException("Product does not exist");
        }

        // check if product already exists
        if (!productExists.get().getReference().equalsIgnoreCase(request.getReference())) {
            checkIfProductAlreadyExistsByReference(request.getReference());
        }

        // check if category exists
        checkIfCategoryExistById(request.getCategoryId());

        final Product productToUpdate = this.productMapper.toEntity(request);
        productToUpdate.setId(id);
        this.productRepository.save(productToUpdate);
    }

    @Override
    public ProductResponse findById(String id) {
        return this.productRepository.findById(id)
                .map(this.productMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exist"));
    }

    @Override
    public PageResponse<ProductResponse> findAll(int pageNo, int pageSize) {
        final PageRequest pageRequest = PageRequest.of(pageNo, pageNo);
        final Page<Product> products = this.productRepository.findAll(pageRequest);
        final Page<ProductResponse> productResponses = products.map(this.productMapper::toResponse);
        return PageResponse.of(productResponses);
    }

    @Override
    public void delete(String id) {
        final Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exist"));
        this.productRepository.delete(product);
    }

    private void checkIfProductAlreadyExistsByReference(final String reference) {
        final Optional<Product> product = this.productRepository.findByReferenceIgnoreCase(reference);
        if (product.isPresent()) {
            log.debug("Product already exists");
            throw new DuplicateResourceException("Product already exists"); // we will use custom exception later
        }
    }

    private void checkIfCategoryExistById(final String categoryId) {
        final Optional<Category> category = this.categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            log.debug("Category does not exist");
            throw new EntityNotFoundException("Category does not exist");
        }
    }
}
