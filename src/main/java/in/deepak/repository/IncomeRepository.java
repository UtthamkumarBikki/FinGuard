package in.deepak.repository;

import in.deepak.entity.ExpenseEntity;
import in.deepak.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    //SELECT * FROM incomes WHERE profile_id = ?1 ORDER BY date DESC
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    //SELECT * FROM incomes WHERE profile_id = ?1 ORDER BY date DESC LIMIT 5
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.profile.id = :profileId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long profileId);

    //Select * from incomes where profile_id = ?1 and date between ?2 and ?3 and name like %?4%
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(Long profileId, LocalDate startDate, LocalDate endDate, String keyword, Sort sort);

    //We are writing the below query because we need to show only the current month dta in the dashboard
    //select * from incomes where profile_id = ?1 and date between ?2 and ?3
    List<IncomeEntity>  findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);


}
