package projeto_integrado.enums;

public enum UserRoles {
    USER("user");

    private String role;


    UserRoles(String role) {
        this.role = role;
    }

    public String getRole(){
        return role;
    }


}
