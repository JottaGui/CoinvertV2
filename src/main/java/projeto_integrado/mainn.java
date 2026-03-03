package projeto_integrado;

import projeto_integrado.Infra.CurrencyAPI;

public class mainn {
  public static void main(String[] args) {


  //User user5 = new User("joao Carlos", "meu f", "jão@email.com", 0);
	//User user2 = new User("miltom", "tenente", "mtjr@email.com", 0);
	//User user3 = new User("jott", "@1234", "jottiha@email.com", 0);

  //  UserDAO userDAO = new UserDAO();
      
     // userDAO.cadastrarUsuario(user5);
    // userDAO.loginUsuario(user5);

     CurrencyAPI api = new CurrencyAPI();
      String resultado = api.valoremadata("USD", "BRL", 2022, 12, 1);


      System.out.println("Cotação: " + resultado);



 }
}
