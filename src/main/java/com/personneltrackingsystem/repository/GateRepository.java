package com.personneltrackingsystem.repository;

import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface GateRepository extends JpaRepository<Gate, Long> {

    @PersistenceContext
    EntityManager entityManager = null;


    // Criteria API
    default Optional<Personel> findPrsnlById(Long personelId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Personel> cq = cb.createQuery(Personel.class);
        Root<Personel> root = cq.from(Personel.class);
        cq.select(root).where(cb.equal(root.get("personelId"), personelId));

        List<Personel> result = entityManager.createQuery(cq).getResultList();
        return result.stream().findFirst();
    }


    @Modifying
    @Query("UPDATE Personel p SET p.gate = NULL WHERE p.gate = :gate")
    void updatePersonelGateReferences(@Param("gate") Gate gate);



//    @Transactional
//    @Modifying
//    default void updatePersonelGateReferencesQueryDsl(Long gateId) {
//        QPersonel personel = QPersonel.personel;
//
//        new JPAUpdateClause(entityManager, personel)
//                .set(personel.gate, (Gate) null)
//                .where(personel.gate.gateId.eq(gateId))
//                .execute();
//    }




    boolean existsByGateName(String existingGateName);

    boolean existsByGateId(Long existingGateID);

}
