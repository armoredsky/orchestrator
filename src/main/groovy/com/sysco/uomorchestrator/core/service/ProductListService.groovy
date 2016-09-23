package com.sysco.uomorchestrator.core.service

import com.google.inject.Inject
import com.sysco.uomdesktop.api.product.ProductListRepresentation
import com.sysco.uomdesktop.api.product.ProductListsRepresentation
import com.sysco.uomdesktop.core.model.ProductList
import com.sysco.uomdesktop.repository.dao.ProductListDao

class ProductListService {
    private final ProductListDao productListDao

    @Inject
    ProductListService(ProductListDao productListDao) {
        this.productListDao = productListDao
    }

    ProductListsRepresentation save(ProductListsRepresentation productListsRepresentation) {
        productListDao.deleteAll()

        List insertedProductListIds = productListsRepresentation.productListCollection.collect {
            productListDao.insert(new ProductList(it))
        }

        List<ProductListRepresentation> productLists = insertedProductListIds.collect {
            new ProductListRepresentation(productListDao.getById(it))
        }
        new ProductListsRepresentation(productListCollection: productLists)
    }

     ProductList getById(UUID id) {
        productListDao.getById(id)
     }

    List<ProductList> findAllByAccount(UUID accountId) {
        productListDao
            .findAllByAccountId(accountId)
    }
}
