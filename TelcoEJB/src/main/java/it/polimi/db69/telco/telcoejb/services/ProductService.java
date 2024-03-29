package it.polimi.db69.telco.telcoejb.services;

import it.polimi.db69.telco.telcoejb.entities.Product;
import it.polimi.db69.telco.telcoejb.entities.ProductSales;
import it.polimi.db69.telco.telcoejb.exceptions.NonUniqueException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

@Stateless
public class ProductService {
    @PersistenceContext(unitName = "TELCOEJB")
    private EntityManager em;

    public ProductService(){}

    public Product findProductById(int productId){
        return em.find(Product.class, productId);
    }

    public Collection<Product> findAllProducts(){
        if (em.createNamedQuery("product.findAll", Product.class).getResultList().isEmpty())
            return null;
        return em.createNamedQuery("product.findAll", Product.class).getResultList();
    }

    public Product createProduct(String name, double fee) throws NonUniqueException{
        if (em.createNamedQuery("Product.getProductByName", Product.class)
                .setParameter("name", name).setMaxResults(1).getResultStream().findFirst().orElse(null) != null)
            throw new NonUniqueException("The product \"" + name + "\" already exists!");

        Product product = new Product(name, fee);
        em.persist(product);

        return product;
    }

    public Collection<ProductSales> getSales(){
        return em.createNamedQuery("ProductReport.getAll", ProductSales.class).getResultList();
    }

    public Collection<ProductSales> getSingleProductSales(int productId){ //very bad coding
        Product product = this.findProductById(productId);
        return em.createNamedQuery("ProductReport.getById", ProductSales.class).setParameter("product", product).getResultList();
    }
}
