package in.deepak.repository;

import in.deepak.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    //SELECT * FROM expenses WHERE profile_id = ?1 ORDER BY date DESC
   List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

   //SELECT * FROM expenses WHERE profile_id = ?1 ORDER BY date DESC LIMIT 5
   List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

   @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id = :profileId")
   BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

   //Select * from expenses where profile_id = ?1 and date between ?2 and ?3 and name like %?4%
   List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

   //We are writing the below query because we need to show only the current month dta in the dashboard
   //select * from expenses where profile_id = ?1 and date between ?2 and ?3
    List<ExpenseEntity>  findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);

    //Se
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);
}
