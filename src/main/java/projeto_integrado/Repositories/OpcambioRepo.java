package projeto_integrado.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import projeto_integrado.Entidades.OpCambio;
import projeto_integrado.Entidades.User;
import projeto_integrado.enums.Estatus;
import projeto_integrado.enums.TipoOP;

import java.util.List;

@Repository
public interface OpcambioRepo extends JpaRepository<OpCambio, Long> {

    List<OpCambio> findByUser(User user);
    List<OpCambio> findByUserAndMoeda(User user, String moeda);

    @Query("SELECT SUM(o.quantidademoeda) FROM OpCambio o " +
            "WHERE o.user = :user " +
            "AND o.moeda = :moeda " +
            "AND o.operacao = :operacao")

    Double somarQuantidadePorTipo(
       @Param("user")    User user,
       @Param("moeda")   String moeda,
       @Param("operacao")TipoOP operacao
    );

    List<OpCambio> findByUserAndEstatus(User user, Estatus Estatus);
}
