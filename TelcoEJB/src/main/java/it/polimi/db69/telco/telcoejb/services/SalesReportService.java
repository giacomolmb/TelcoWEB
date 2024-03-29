package it.polimi.db69.telco.telcoejb.services;

import it.polimi.db69.telco.telcoejb.entities.PackageSales;
import it.polimi.db69.telco.telcoejb.entities.ServicePackage;
import it.polimi.db69.telco.telcoejb.entities.User;
import it.polimi.db69.telco.telcoejb.entities.ValidityPeriod;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

@Stateless
public class SalesReportService {
    @PersistenceContext(unitName = "TELCOEJB")
    private EntityManager em;

    @EJB(name = "it.polimi.db69.telco.telcoejb.services/ServicePackageService")
    private ServicePackageService packageService;

    public Collection<PackageSales> getAllSales(){
        return em.createNamedQuery("Sales.getAll", PackageSales.class).getResultList();
    }

    public Collection<PackageSales> getSalesByVp(ValidityPeriod vp){
        Collection<PackageSales> salesList = null;
        salesList = em.createNamedQuery("Sales.getByVp", PackageSales.class)
                .setParameter("vp", vp).getResultList();
        if(salesList.isEmpty()) return null;
        else return salesList;
    }

    public Collection<PackageSales> getSalesByPackage(int packageId){
        ServicePackage servicePackage = packageService.findPackageById(packageId);
        Collection<PackageSales> salesList = null;
        salesList = em.createNamedQuery("Sales.getByPackage", PackageSales.class)
                .setParameter("package", servicePackage).getResultList();
        if(salesList.isEmpty()) return null;
        else return salesList;
    }
}