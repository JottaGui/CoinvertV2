package projeto_integrado.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import projeto_integrado.Entidades.OpCambio;
import projeto_integrado.Entidades.User;
import projeto_integrado.enums.Estatus;
import projeto_integrado.enums.TipoOP;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OpcambioRepo extends JpaRepository<OpCambio, Long> {

    List<OpCambio> findByUser(User user);
    List<OpCambio> findByUserAndMoeda(User user, String moeda);



    @Query("""
       SELECT SUM(o.quantidademoeda)
       FROM OpCambio o
       WHERE o.moeda = :moeda
       AND o.user.id = :userId
       AND o.estatus = 'APROVADA'
       """)
    BigDecimal somarMoedaPorUsuario(@Param("moeda") String moeda,
                                    @Param("userId") Long userId);

    @Query("""
       SELECT o
       FROM OpCambio o
       WHERE o.user.id = :userId
       AND o.estatus = 'APROVADA'
       """)
    List<OpCambio> buscarOperacoes(@Param("userId") Long userId);

    List<OpCambio> findByUserAndEstatus(User user, Estatus Estatus);

    List<OpCambio> findByEstatus(Estatus Estatus);

    @Query("SELECT o.id FROM OpCambio o WHERE o.estatus = :estatus")
    List<Long> buscarIdsPendentes(@Param("estatus") Estatus estatus);


}
